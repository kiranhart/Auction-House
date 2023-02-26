/*
 * Auction House
 * Copyright 2018-2022 Kiran Hart
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ca.tweetzy.auctionhouse.guis.confirmation;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.api.events.AuctionEndEvent;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.auction.AuctionedItem;
import ca.tweetzy.auctionhouse.auction.enums.AuctionSaleType;
import ca.tweetzy.auctionhouse.auction.enums.AuctionStackType;
import ca.tweetzy.auctionhouse.exception.ItemNotFoundException;
import ca.tweetzy.auctionhouse.guis.AbstractPlaceholderGui;
import ca.tweetzy.auctionhouse.guis.GUIAuctionHouse;
import ca.tweetzy.auctionhouse.guis.GUIContainerInspect;
import ca.tweetzy.auctionhouse.helpers.ConfigurationItemHelper;
import ca.tweetzy.auctionhouse.managers.SoundManager;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.gui.events.GuiClickEvent;
import ca.tweetzy.core.hooks.EconomyManager;
import ca.tweetzy.core.utils.PlayerUtils;
import ca.tweetzy.core.utils.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.util.HashMap;

/**
 * The current file has been created by Kiran Hart
 * Date Created: March 17 2021
 * Time Created: 11:18 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUIConfirmPurchase extends AbstractPlaceholderGui {

	final AuctionPlayer auctionPlayer;
	final AuctionedItem auctionItem;

	boolean buyingSpecificQuantity;
	int purchaseQuantity = 0;
	int maxStackSize = 0;
	double pricePerItem = 0D;

	public GUIConfirmPurchase(AuctionPlayer auctionPlayer, AuctionedItem auctionItem, boolean buyingSpecificQuantity) {
		super(auctionPlayer);
		this.auctionPlayer = auctionPlayer;
		this.auctionItem = auctionItem;
		this.buyingSpecificQuantity = buyingSpecificQuantity;
		setTitle(TextUtils.formatText(Settings.GUI_CONFIRM_BUY_TITLE.getString()));
		setAcceptsItems(false);

		int preAmount = auctionItem.getItem().getAmount();
		if (preAmount == 1) {
			this.buyingSpecificQuantity = false;
		}

		setRows(!this.buyingSpecificQuantity ? 1 : 5);

		if (this.buyingSpecificQuantity) {
			setUseLockedCells(Settings.GUI_CONFIRM_FILL_BG_ON_QUANTITY.getBoolean());
			setDefaultItem(ConfigurationItemHelper.createConfigurationItem(Settings.GUI_CONFIRM_BG_ITEM.getString()));
			this.purchaseQuantity = preAmount;
			this.maxStackSize = preAmount;
			this.pricePerItem = this.auctionItem.getBasePrice() / this.maxStackSize;
		}

		setOnOpen(open -> {
			AuctionHouse.getInstance().getLogger().info("Added " + open.player.getName() + " to confirmation pre purchase");
		});

		setOnClose(close -> {
			AuctionHouse.getInstance().getTransactionManager().getPrePurchaseHolding().remove(close.player);
			close.manager.showGUI(close.player, new GUIAuctionHouse(this.auctionPlayer));
			AuctionHouse.getInstance().getLogger().info("Removed " + close.player.getName() + " from confirmation pre purchase");
		});

		draw();
	}

	private void draw() {
		ItemStack deserializeItem = this.auctionItem.getItem().clone();

		setItems(this.buyingSpecificQuantity ? 9 : 0, this.buyingSpecificQuantity ? 12 : 3, getConfirmBuyYesItem());
		setItem(this.buyingSpecificQuantity ? 1 : 0, 4, this.auctionItem.getDisplayStack(AuctionStackType.LISTING_PREVIEW));
		setItems(this.buyingSpecificQuantity ? 14 : 5, this.buyingSpecificQuantity ? 17 : 8, getConfirmBuyNoItem());

		setAction(this.buyingSpecificQuantity ? 1 : 0, 4, ClickType.LEFT, e -> {
			if (deserializeItem.getItemMeta() instanceof BlockStateMeta) {
				if (((BlockStateMeta) deserializeItem.getItemMeta()).getBlockState() instanceof ShulkerBox) {
					AuctionHouse.getInstance().getTransactionManager().getPrePurchaseHolding().remove(e.player);
					e.manager.showGUI(e.player, new GUIContainerInspect(e.clickedItem, this.auctionPlayer, this.auctionItem, this.buyingSpecificQuantity));
				}
			}
		});

		setActionForRange(this.buyingSpecificQuantity ? 14 : 5, this.buyingSpecificQuantity ? 17 : 8, ClickType.LEFT, e -> {
			e.gui.close();
		});

		setActionForRange(this.buyingSpecificQuantity ? 9 : 0, this.buyingSpecificQuantity ? 12 : 3, ClickType.LEFT, e -> {
			// Re-select the item to ensure that it's available
			try {
				// if the item is in the garbage then just don't continue
				if (AuctionHouse.getInstance().getAuctionItemManager().getGarbageBin().containsKey(this.auctionItem.getId()))
					return;
				AuctionedItem located = AuctionHouse.getInstance().getAuctionItemManager().getItem(this.auctionItem.getId());

				if (located == null || located.isExpired()) {
					AuctionHouse.getInstance().getLocale().getMessage("auction.itemnotavailable").sendPrefixedMessage(e.player);
					e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
					return;
				}

				double buyNowPrice = this.buyingSpecificQuantity ? this.purchaseQuantity * this.pricePerItem : located.getBasePrice();
				double tax = Settings.TAX_ENABLED.getBoolean() ? (Settings.TAX_SALES_TAX_BUY_NOW_PERCENTAGE.getDouble() / 100) * buyNowPrice : 0D;

				// Check economy
				if (!EconomyManager.hasBalance(e.player, buyNowPrice + (Settings.TAX_CHARGE_SALES_TAX_TO_BUYER.getBoolean() ? tax : 0D))) {
					AuctionHouse.getInstance().getLocale().getMessage("general.notenoughmoney").sendPrefixedMessage(e.player);
					SoundManager.getInstance().playSound(e.player, Settings.SOUNDS_NOT_ENOUGH_MONEY.getString());
					e.gui.close();
					return;
				}

				AuctionEndEvent auctionEndEvent = new AuctionEndEvent(Bukkit.getOfflinePlayer(this.auctionItem.getOwner()), e.player, this.auctionItem, AuctionSaleType.WITHOUT_BIDDING_SYSTEM, tax, false);
				Bukkit.getServer().getPluginManager().callEvent(auctionEndEvent);
				if (auctionEndEvent.isCancelled()) return;

				if (!Settings.ALLOW_PURCHASE_IF_INVENTORY_FULL.getBoolean() && e.player.getInventory().firstEmpty() == -1) {
					AuctionHouse.getInstance().getLocale().getMessage("general.noroom").sendPrefixedMessage(e.player);
					return;
				}

				if (this.buyingSpecificQuantity) {
					// the original item stack
					ItemStack item = auctionItem.getItem().clone();
					ItemStack toGive = auctionItem.getItem().clone();

					if (item.getAmount() - this.purchaseQuantity >= 1 && !located.isInfinite()) {
						item.setAmount(item.getAmount() - this.purchaseQuantity);
						toGive.setAmount(this.purchaseQuantity);

						located.setItem(item);
						located.setBasePrice(Settings.ROUND_ALL_PRICES.getBoolean() ? Math.round(located.getBasePrice() - buyNowPrice) : located.getBasePrice() - buyNowPrice);

						transferFunds(e.player, buyNowPrice);
					} else {
						transferFunds(e.player, buyNowPrice);
						if (!located.isInfinite())
							AuctionHouse.getInstance().getAuctionItemManager().sendToGarbage(located);
					}

					PlayerUtils.giveItem(e.player, toGive);
					sendMessages(e, located, true, buyNowPrice, this.purchaseQuantity);

				} else {
					transferFunds(e.player, buyNowPrice);
					if (!located.isInfinite())
						AuctionHouse.getInstance().getAuctionItemManager().sendToGarbage(located);

					if (Settings.BIDDING_TAKES_MONEY.getBoolean() && !located.getHighestBidder().equals(located.getOwner())) {
						final OfflinePlayer oldBidder = Bukkit.getOfflinePlayer(located.getHighestBidder());

						EconomyManager.deposit(oldBidder, auctionItem.getCurrentPrice());

						if (oldBidder.isOnline())
							AuctionHouse.getInstance().getLocale().getMessage("pricing.moneyadd").processPlaceholder("player_balance", AuctionAPI.getInstance().formatNumber(EconomyManager.getBalance(oldBidder))).processPlaceholder("price", AuctionAPI.getInstance().formatNumber(located.getCurrentPrice())).sendPrefixedMessage(oldBidder.getPlayer());

					}

					PlayerUtils.giveItem(e.player, located.getItem());
					sendMessages(e, located, false, 0, deserializeItem.getAmount());
				}

				if (Settings.BROADCAST_AUCTION_SALE.getBoolean()) {
					final OfflinePlayer seller = Bukkit.getOfflinePlayer(auctionItem.getOwner());

					Bukkit.getOnlinePlayers().forEach(player -> AuctionHouse.getInstance().getLocale().getMessage("auction.broadcast.sold")
							.processPlaceholder("player", e.player.getName())
							.processPlaceholder("player_displayname", AuctionAPI.getInstance().getDisplayName(e.player))
							.processPlaceholder("seller", auctionItem.getOwnerName())
							.processPlaceholder("seller_displayname", AuctionAPI.getInstance().getDisplayName(seller))
							.processPlaceholder("amount", auctionItem.getItem().getAmount())
							.processPlaceholder("item", AuctionAPI.getInstance().getItemName(auctionItem.getItem()))
							.processPlaceholder("price", AuctionAPI.getInstance().formatNumber(auctionItem.getCurrentPrice()))
							.sendPrefixedMessage(player));
				}

				AuctionHouse.getInstance().getTransactionManager().getPrePurchasePlayers(auctionItem.getId()).forEach(player -> {
					AuctionHouse.getInstance().getTransactionManager().removeAllRelatedPlayers(auctionItem.getId());
					player.closeInventory();
				});

				e.gui.close();

			} catch (ItemNotFoundException exception) {
				AuctionHouse.getInstance().getLogger().info("Tried to purchase item that was bought, or does not exist");
			}
		});

		if (this.buyingSpecificQuantity) {
			drawPurchaseInfo(this.maxStackSize);

			// Decrease Button
			setButton(3, 3, getDecreaseQtyButtonItem(), e -> {
				if ((this.purchaseQuantity - 1) <= 0) return;
				this.purchaseQuantity -= 1;
				drawPurchaseInfo(this.purchaseQuantity);
			});

			// Increase Button
			setButton(3, 5, getIncreaseQtyButtonItem(), e -> {
				if ((this.purchaseQuantity + 1) > this.maxStackSize) return;
				this.purchaseQuantity += 1;
				drawPurchaseInfo(this.purchaseQuantity);
			});
		}
	}

	private void transferFunds(Player from, double amount) {
		double tax = Settings.TAX_ENABLED.getBoolean() ? (Settings.TAX_SALES_TAX_BUY_NOW_PERCENTAGE.getDouble() / 100) * amount : 0D;

		AuctionAPI.getInstance().withdrawBalance(from, Settings.ROUND_ALL_PRICES.getBoolean() ? Math.round(Settings.TAX_CHARGE_SALES_TAX_TO_BUYER.getBoolean() ? amount + tax : amount) : Settings.TAX_CHARGE_SALES_TAX_TO_BUYER.getBoolean() ? amount + tax : amount);
		AuctionAPI.getInstance().depositBalance(Bukkit.getOfflinePlayer(this.auctionItem.getOwner()), Settings.ROUND_ALL_PRICES.getBoolean() ? Math.round(Settings.TAX_CHARGE_SALES_TAX_TO_BUYER.getBoolean() ? amount : amount - tax) : Settings.TAX_CHARGE_SALES_TAX_TO_BUYER.getBoolean() ? amount : amount - tax);
	}

	private void sendMessages(GuiClickEvent e, AuctionedItem located, boolean overwritePrice, double price, int qtyOverride) {
		double totalPrice = overwritePrice ? price : located.getBasePrice();
		double tax = Settings.TAX_ENABLED.getBoolean() ? (Settings.TAX_SALES_TAX_BUY_NOW_PERCENTAGE.getDouble() / 100) * totalPrice : 0D;

		AuctionHouse.getInstance().getLocale().getMessage("pricing.moneyremove").processPlaceholder("player_balance", AuctionAPI.getInstance().formatNumber(EconomyManager.getBalance(e.player))).processPlaceholder("price", AuctionAPI.getInstance().formatNumber(Settings.TAX_CHARGE_SALES_TAX_TO_BUYER.getBoolean() ? totalPrice - tax : totalPrice)).sendPrefixedMessage(e.player);
		AuctionHouse.getInstance().getLocale().getMessage("general.bought_item").processPlaceholder("amount", qtyOverride).processPlaceholder("item", AuctionAPI.getInstance().getItemName(located.getItem())).processPlaceholder("price", AuctionAPI.getInstance().formatNumber(Settings.TAX_CHARGE_SALES_TAX_TO_BUYER.getBoolean() ? totalPrice - tax : totalPrice)).sendPrefixedMessage(e.player);

		if (Bukkit.getOfflinePlayer(located.getOwner()).isOnline()) {
			AuctionHouse.getInstance().getLocale().getMessage("auction.itemsold")
					.processPlaceholder("item", AuctionAPI.getInstance().getItemName(located.getItem()))
					.processPlaceholder("amount", qtyOverride)
					.processPlaceholder("price", AuctionAPI.getInstance().formatNumber(Settings.TAX_CHARGE_SALES_TAX_TO_BUYER.getBoolean() ? totalPrice : totalPrice - tax))
					.processPlaceholder("buyer_name", e.player.getName())
					.sendPrefixedMessage(Bukkit.getOfflinePlayer(located.getOwner()).getPlayer());
			AuctionHouse.getInstance().getLocale().getMessage("pricing.moneyadd").processPlaceholder("player_balance", AuctionAPI.getInstance().formatNumber(EconomyManager.getBalance(Bukkit.getOfflinePlayer(located.getOwner())))).processPlaceholder("price", AuctionAPI.getInstance().formatNumber(Settings.TAX_CHARGE_SALES_TAX_TO_BUYER.getBoolean() ? totalPrice : totalPrice - tax)).sendPrefixedMessage(Bukkit.getOfflinePlayer(located.getOwner()).getPlayer());
		}
	}

	private void drawPurchaseInfo(int amt) {
		setItem(3, 4, getPurchaseInfoItem(amt));
	}

	private ItemStack getPurchaseInfoItem(int qty) {
		ItemStack stack = ConfigurationItemHelper.createConfigurationItem(Settings.GUI_CONFIRM_QTY_INFO_ITEM.getString(), Settings.GUI_CONFIRM_QTY_INFO_NAME.getString(), Settings.GUI_CONFIRM_QTY_INFO_LORE.getStringList(), new HashMap<String, Object>() {{
			put("%original_stack_size%", maxStackSize);
			put("%original_stack_price%", AuctionAPI.getInstance().formatNumber(auctionItem.getBasePrice()));
			put("%price_per_item%", AuctionAPI.getInstance().formatNumber(pricePerItem));
			put("%purchase_quantity%", purchaseQuantity);
			put("%purchase_price%", AuctionAPI.getInstance().formatNumber(pricePerItem * purchaseQuantity));
		}});
		stack.setAmount(qty);
		return stack;
	}
}
