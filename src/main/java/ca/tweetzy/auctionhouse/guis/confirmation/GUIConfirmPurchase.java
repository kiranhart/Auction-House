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
import ca.tweetzy.auctionhouse.auction.AuctionPayment;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.auction.AuctionedItem;
import ca.tweetzy.auctionhouse.auction.enums.AuctionSaleType;
import ca.tweetzy.auctionhouse.auction.enums.AuctionStackType;
import ca.tweetzy.auctionhouse.auction.enums.PaymentReason;
import ca.tweetzy.auctionhouse.events.AuctionEndEvent;
import ca.tweetzy.auctionhouse.exception.ItemNotFoundException;
import ca.tweetzy.auctionhouse.guis.AuctionBaseGUI;
import ca.tweetzy.auctionhouse.guis.core.GUIAuctionHouse;
import ca.tweetzy.auctionhouse.guis.core.GUIContainerInspect;
import ca.tweetzy.auctionhouse.managers.SoundManager;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.gui.events.GuiClickEvent;
import ca.tweetzy.core.hooks.EconomyManager;
import ca.tweetzy.core.utils.PlayerUtils;
import ca.tweetzy.flight.nbtapi.NBT;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.flight.utils.Replacer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.util.UUID;

/**
 * The current file has been created by Kiran Hart
 * Date Created: March 17 2021
 * Time Created: 11:18 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUIConfirmPurchase extends AuctionBaseGUI {

	final AuctionPlayer auctionPlayer;
	final AuctionedItem auctionItem;

	boolean buyingSpecificQuantity;
	int purchaseQuantity = 0;
	int maxStackSize = 0;
	double pricePerItem = 0D;

	public GUIConfirmPurchase(AuctionPlayer auctionPlayer, AuctionedItem auctionItem, boolean buyingSpecificQuantity) {
		super(null, auctionPlayer.getPlayer(), Settings.GUI_CONFIRM_BUY_TITLE.getString(), !buyingSpecificQuantity ? 1 : 5);
		this.auctionPlayer = auctionPlayer;
		this.auctionItem = auctionItem;
		this.buyingSpecificQuantity = buyingSpecificQuantity;
		setAcceptsItems(false);

		int preAmount = auctionItem.getItem().getAmount();
		if (preAmount == 1) {
			this.buyingSpecificQuantity = false;
		}

		if (this.buyingSpecificQuantity) {
			setUseLockedCells(Settings.GUI_CONFIRM_FILL_BG_ON_QUANTITY.getBoolean());
			setDefaultItem(QuickItem.bg(QuickItem.of(Settings.GUI_CONFIRM_BG_ITEM.getString()).make()));
			this.purchaseQuantity = preAmount;
			this.maxStackSize = preAmount;
			this.pricePerItem = this.auctionItem.getBasePrice() / this.maxStackSize;
		}

		setOnOpen(open -> AuctionHouse.getInstance().getLogger().info("Added " + open.player.getName() + " to confirmation pre purchase"));

		setOnClose(close -> {
			AuctionHouse.getInstance().getTransactionManager().getPrePurchaseHolding().remove(close.player);
			close.manager.showGUI(close.player, new GUIAuctionHouse(this.auctionPlayer));
			AuctionHouse.getInstance().getLogger().info("Removed " + close.player.getName() + " from confirmation pre purchase");
		});

		draw();
	}

	@Override
	protected void draw() {
		ItemStack deserializeItem = this.auctionItem.getItem().clone();
		final boolean isRequest = this.auctionItem.isRequest();

		setItems(this.buyingSpecificQuantity ? 9 : 0, this.buyingSpecificQuantity ? 12 : 3, isRequest ? getConfirmRequestYesItem() : getConfirmBuyYesItem());
		setItem(this.buyingSpecificQuantity ? 1 : 0, 4, isRequest ? this.auctionItem.getDisplayRequestStack(AuctionStackType.MAIN_AUCTION_HOUSE) : this.auctionItem.getDisplayStack(AuctionStackType.LISTING_PREVIEW));
		setItems(this.buyingSpecificQuantity ? 14 : 5, this.buyingSpecificQuantity ? 17 : 8, isRequest ? getConfirmRequestNoItem() : getConfirmBuyNoItem());

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

				/*
				============================================================================
										SPECIAL SHIT FOR REQUESTS
				============================================================================
				 */
				if (isRequest) {
					// check if the fulfiller even has the item
					final int itemCount = AuctionAPI.getInstance().getItemCountInPlayerInventory(this.player, this.auctionItem.getItem());
					final int amountNeeded = this.auctionItem.getRequestAmount() == 0 ? this.auctionItem.getItem().getAmount() : this.auctionItem.getRequestAmount();


					if (itemCount < amountNeeded) {
						// yell at fulfiller for being dumb
						AuctionHouse.getInstance().getLocale().getMessage("general.notenoughitems").sendPrefixedMessage(e.player);
						return;
					}

					final OfflinePlayer requester = Bukkit.getOfflinePlayer(this.auctionItem.getOwner());

					// check if the requester even has money
					if (!EconomyManager.hasBalance(requester, buyNowPrice)) {
						AuctionHouse.getInstance().getLocale().getMessage("general.requesterhasnomoney").sendPrefixedMessage(e.player);
						return;
					}

					// transfer funds
					EconomyManager.withdrawBalance(requester, buyNowPrice);
					EconomyManager.deposit(e.player, Settings.TAX_CHARGE_SALES_TAX_TO_BUYER.getBoolean() ? buyNowPrice : buyNowPrice - tax);

					// transfer items
					AuctionAPI.getInstance().removeSpecificItemQuantityFromPlayer(this.player, this.auctionItem.getItem(), amountNeeded);
					final AuctionedItem toGive = new AuctionedItem(
							UUID.randomUUID(),
							requester.getUniqueId(),
							requester.getUniqueId(),
							this.auctionItem.getOwnerName(),
							this.auctionItem.getOwnerName(),
							this.auctionItem.getCategory(),
							this.auctionItem.getItem(),
							0,
							0,
							0,
							0,
							false, true, System.currentTimeMillis()
					);

					toGive.setRequestAmount(amountNeeded);

					AuctionHouse.getInstance().getDataManager().insertAuction(toGive, (error, inserted) -> AuctionHouse.getInstance().getAuctionItemManager().addAuctionItem(toGive));
					AuctionHouse.getInstance().getAuctionItemManager().sendToGarbage(this.auctionItem);

					AuctionHouse.getInstance().getTransactionManager().getPrePurchasePlayers(auctionItem.getId()).forEach(player -> {
						AuctionHouse.getInstance().getTransactionManager().removeAllRelatedPlayers(auctionItem.getId());
						player.closeInventory();
					});

					AuctionHouse.getInstance().getLocale().getMessage("pricing.moneyadd").processPlaceholder("player_balance", AuctionAPI.getInstance().formatNumber(EconomyManager.getBalance(e.player))).processPlaceholder("price", AuctionAPI.getInstance().formatNumber(this.auctionItem.getBasePrice())).sendPrefixedMessage(e.player);
					if (requester.isOnline())
						AuctionHouse.getInstance().getLocale().getMessage("pricing.moneyremove").processPlaceholder("player_balance", AuctionAPI.getInstance().formatNumber(EconomyManager.getBalance(requester.getPlayer()))).processPlaceholder("price", AuctionAPI.getInstance().formatNumber(this.auctionItem.getBasePrice())).sendPrefixedMessage(requester.getPlayer());

					e.gui.close();
					return;
				}


				//		languageNodes.put("pricing.moneyremove", "&c&l- $%price% &7(%player_balance%)");
				//		languageNodes.put("pricing.moneyadd", "&a&l+ $%price% &7(%player_balance%)");

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

					NBT.modify(toGive, nbt -> {
						nbt.removeKey("AuctionDupeTracking");
					});

					PlayerUtils.giveItem(e.player, toGive);
					sendMessages(e, located, true, buyNowPrice, this.purchaseQuantity);

				} else {
					transferFunds(e.player, buyNowPrice);
					if (!located.isInfinite())
						AuctionHouse.getInstance().getAuctionItemManager().sendToGarbage(located);

					if (Settings.BIDDING_TAKES_MONEY.getBoolean() && !located.getHighestBidder().equals(located.getOwner())) {
						final OfflinePlayer oldBidder = Bukkit.getOfflinePlayer(located.getHighestBidder());

						if (Settings.STORE_PAYMENTS_FOR_MANUAL_COLLECTION.getBoolean())
							AuctionHouse.getInstance().getDataManager().insertAuctionPayment(new AuctionPayment(
									oldBidder.getUniqueId(),
									auctionItem.getCurrentPrice(),
									auctionItem.getItem(),
									AuctionHouse.getInstance().getLocale().getMessage("general.prefix").getMessage(),
									PaymentReason.BID_RETURNED
							), null);
						else
							EconomyManager.deposit(oldBidder, auctionItem.getCurrentPrice());

						if (oldBidder.isOnline())
							AuctionHouse.getInstance().getLocale().getMessage("pricing.moneyadd").processPlaceholder("player_balance", AuctionAPI.getInstance().formatNumber(EconomyManager.getBalance(oldBidder))).processPlaceholder("price", AuctionAPI.getInstance().formatNumber(located.getCurrentPrice())).sendPrefixedMessage(oldBidder.getPlayer());

					}

//					PlayerUtils.giveItem(e.player, located.getItem());
					ItemStack foundItem = located.getItem().clone();

					NBT.modify(foundItem, nbt -> {
						nbt.removeKey("AuctionDupeTracking");
					});

					PlayerUtils.giveItem(e.player, foundItem);

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
							.processPlaceholder("price", AuctionAPI.getInstance().formatNumber(Math.max(auctionItem.getBasePrice(), auctionItem.getCurrentPrice())))
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
		AuctionAPI.getInstance().depositBalance(Bukkit.getOfflinePlayer(this.auctionItem.getOwner()), Settings.ROUND_ALL_PRICES.getBoolean() ? Math.round(Settings.TAX_CHARGE_SALES_TAX_TO_BUYER.getBoolean() ? amount : amount - tax) : Settings.TAX_CHARGE_SALES_TAX_TO_BUYER.getBoolean() ? amount : amount - tax, auctionItem.getItem(), from);
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
		return QuickItem
				.of(Settings.GUI_CONFIRM_QTY_INFO_ITEM.getString())
				.amount(qty)
				.name(Settings.GUI_CONFIRM_QTY_INFO_NAME.getString())
				.lore(Replacer.replaceVariables(Settings.GUI_CONFIRM_QTY_INFO_LORE.getStringList(),
						"original_stack_size", maxStackSize,
						"original_stack_price", AuctionAPI.getInstance().formatNumber(auctionItem.getBasePrice()),
						"price_per_item", AuctionAPI.getInstance().formatNumber(pricePerItem),
						"purchase_quantity", purchaseQuantity,
						"purchase_price", AuctionAPI.getInstance().formatNumber(pricePerItem * purchaseQuantity)
				))
				.make();
	}

	private ItemStack getIncreaseQtyButtonItem() {
		return QuickItem.of(Settings.GUI_CONFIRM_INCREASE_QTY_ITEM.getString()).name(Settings.GUI_CONFIRM_INCREASE_QTY_NAME.getString()).lore(Settings.GUI_CONFIRM_INCREASE_QTY_LORE.getStringList()).make();
	}

	private ItemStack getDecreaseQtyButtonItem() {
		return QuickItem.of(Settings.GUI_CONFIRM_DECREASE_QTY_ITEM.getString()).name(Settings.GUI_CONFIRM_DECREASE_QTY_NAME.getString()).lore(Settings.GUI_CONFIRM_DECREASE_QTY_LORE.getStringList()).make();
	}

	protected ItemStack getConfirmBuyYesItem() {
		return QuickItem.of(Settings.GUI_CONFIRM_BUY_YES_ITEM.getString()).name(Settings.GUI_CONFIRM_BUY_YES_NAME.getString()).lore(Settings.GUI_CONFIRM_BUY_YES_LORE.getStringList()).make();
	}

	protected ItemStack getConfirmBuyNoItem() {
		return QuickItem.of(Settings.GUI_CONFIRM_BUY_NO_ITEM.getString()).name(Settings.GUI_CONFIRM_BUY_NO_NAME.getString()).lore(Settings.GUI_CONFIRM_BUY_NO_LORE.getStringList()).make();
	}

	protected ItemStack getConfirmRequestYesItem() {
		return QuickItem.of(Settings.GUI_CONFIRM_REQUEST_YES_ITEM.getString()).name(Settings.GUI_CONFIRM_REQUEST_YES_NAME.getString()).lore(Settings.GUI_CONFIRM_REQUEST_YES_LORE.getStringList()).make();
	}

	protected ItemStack getConfirmRequestNoItem() {
		return QuickItem.of(Settings.GUI_CONFIRM_REQUEST_NO_ITEM.getString()).name(Settings.GUI_CONFIRM_REQUEST_NO_NAME.getString()).lore(Settings.GUI_CONFIRM_REQUEST_NO_LORE.getStringList()).make();
	}
}
