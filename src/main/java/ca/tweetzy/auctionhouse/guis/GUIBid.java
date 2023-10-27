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

package ca.tweetzy.auctionhouse.guis;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.events.AuctionBidEvent;
import ca.tweetzy.auctionhouse.auction.AuctionPayment;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.auction.AuctionedItem;
import ca.tweetzy.auctionhouse.auction.enums.PaymentReason;
import ca.tweetzy.auctionhouse.guis.confirmation.GUIConfirmBid;
import ca.tweetzy.auctionhouse.helpers.ConfigurationItemHelper;
import ca.tweetzy.auctionhouse.helpers.input.TitleInput;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.hooks.EconomyManager;
import ca.tweetzy.core.utils.NumberUtils;
import ca.tweetzy.core.utils.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * The current file has been created by Kiran Hart
 * Date Created: August 08 2021
 * Time Created: 5:16 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUIBid extends AbstractPlaceholderGui {

	private final AuctionPlayer auctionPlayer;
	private final AuctionedItem auctionItem;

	public GUIBid(AuctionPlayer auctionPlayer, AuctionedItem auctionItem) {
		super(auctionPlayer);
		this.auctionPlayer = auctionPlayer;
		this.auctionItem = auctionItem;
		setTitle(TextUtils.formatText(Settings.GUI_BIDDING_TITLE.getString()));
		setDefaultItem(ConfigurationItemHelper.createConfigurationItem(this.player, Settings.GUI_BIDDING_BG_ITEM.getString()));
		setUseLockedCells(true);
		setAcceptsItems(false);
		setAllowDrops(false);
		setRows(3);
		draw();

		setOnClose(close -> close.manager.showGUI(close.player, new GUIAuctionHouse(this.auctionPlayer)));
	}

	private void draw() {
		setItem(1, 4, this.auctionItem.getItem());
		setButton(1, 2, ConfigurationItemHelper.createConfigurationItem(this.player, Settings.GUI_BIDDING_ITEMS_DEFAULT_ITEM.getString(), Settings.GUI_BIDDING_ITEMS_DEFAULT_NAME.getString(), Settings.GUI_BIDDING_ITEMS_DEFAULT_LORE.getStringList(), null), e -> {
			if (Settings.PLAYER_NEEDS_TOTAL_PRICE_TO_BID.getBoolean() && !EconomyManager.hasBalance(e.player, auctionItem.getCurrentPrice() + auctionItem.getBidIncrementPrice())) {
				AuctionHouse.getInstance().getLocale().getMessage("general.notenoughmoney").sendPrefixedMessage(e.player);
				return;
			}

			e.gui.exit();
			// THE MINIMUM
			final double minBid = Settings.USE_REALISTIC_BIDDING.getBoolean() ? this.auctionItem.getCurrentPrice() + this.auctionItem.getBidIncrementPrice() : this.auctionItem.getBidIncrementPrice();

			e.manager.showGUI(e.player, new GUIConfirmBid(this.auctionPlayer, auctionItem, minBid));
		});

		// TODO UPDATE BID
		setButton(1, 6, ConfigurationItemHelper.createConfigurationItem(this.player, Settings.GUI_BIDDING_ITEMS_CUSTOM_ITEM.getString(), Settings.GUI_BIDDING_ITEMS_CUSTOM_NAME.getString(), Settings.GUI_BIDDING_ITEMS_CUSTOM_LORE.getStringList(), null), e -> {
			e.gui.exit();

			new TitleInput(player, AuctionHouse.getInstance().getLocale().getMessage("titles.enter bid.title").getMessage(), AuctionHouse.getInstance().getLocale().getMessage("titles.enter bid.subtitle").getMessage()) {

				@Override
				public void onExit(Player player) {
					AuctionHouse.getInstance().getGuiManager().showGUI(player, new GUIAuctionHouse(GUIBid.this.auctionPlayer));
				}

				@Override
				public boolean onResult(String string) {
					string = ChatColor.stripColor(string);

					if (!NumberUtils.isDouble(string)) {
						AuctionHouse.getInstance().getLocale().getMessage("general.notanumber").processPlaceholder("value", string).sendPrefixedMessage(player);
						return false;
					}

					double value = Double.parseDouble(string);

					if (value <= 0) {
						AuctionHouse.getInstance().getLocale().getMessage("general.cannotbezero").sendPrefixedMessage(e.player);
						return false;
					}

					if (value > Settings.MAX_AUCTION_INCREMENT_PRICE.getDouble()) {
						AuctionHouse.getInstance().getLocale().getMessage("pricing.maxbidincrementprice").processPlaceholder("price", Settings.MAX_AUCTION_INCREMENT_PRICE.getDouble()).sendPrefixedMessage(e.player);
						return false;
					}

					double newBiddingAmount = 0;
					if (Settings.USE_REALISTIC_BIDDING.getBoolean()) {
						if (value < auctionItem.getCurrentPrice() + auctionItem.getBidIncrementPrice()) {
							AuctionHouse.getInstance().getLocale().getMessage("pricing.minbidincrementprice").processPlaceholder("price", AuctionAPI.getInstance().formatNumber(auctionItem.getCurrentPrice() + auctionItem.getBidIncrementPrice())).sendPrefixedMessage(e.player);
							return false;
						}

						if (value > GUIBid.this.auctionItem.getCurrentPrice()) {
							newBiddingAmount = value;
						} else {
							if (Settings.BID_MUST_BE_HIGHER_THAN_PREVIOUS.getBoolean()) {
								e.manager.showGUI(e.player, new GUIAuctionHouse(GUIBid.this.auctionPlayer));
								AuctionHouse.getInstance().getLocale().getMessage("pricing.bidmusthigherthanprevious").processPlaceholder("current_bid", AuctionAPI.getInstance().formatNumber(auctionItem.getCurrentPrice())).sendPrefixedMessage(e.player);
								return true;
							}

							newBiddingAmount = GUIBid.this.auctionItem.getCurrentPrice() + value;
						}
					} else {
						if (value < auctionItem.getBidIncrementPrice()) {
							AuctionHouse.getInstance().getLocale().getMessage("pricing.minbidincrementprice").processPlaceholder("price", AuctionAPI.getInstance().formatNumber(auctionItem.getBidIncrementPrice())).sendPrefixedMessage(e.player);
							return false;
						}

						newBiddingAmount = GUIBid.this.auctionItem.getCurrentPrice() + value;
					}

					newBiddingAmount = Settings.ROUND_ALL_PRICES.getBoolean() ? Math.round(newBiddingAmount) : newBiddingAmount;

					if (Settings.PLAYER_NEEDS_TOTAL_PRICE_TO_BID.getBoolean() && !EconomyManager.hasBalance(e.player, newBiddingAmount)) {
						AuctionHouse.getInstance().getLocale().getMessage("general.notenoughmoney").sendPrefixedMessage(e.player);
						return true;
					}

					if (Settings.ASK_FOR_BID_CONFIRMATION.getBoolean()) {
						e.manager.showGUI(e.player, new GUIConfirmBid(GUIBid.this.auctionPlayer, auctionItem, value));
						return true;
					}

					ItemStack itemStack = auctionItem.getItem();

					OfflinePlayer oldBidder = Bukkit.getOfflinePlayer(auctionItem.getHighestBidder());
					OfflinePlayer owner = Bukkit.getOfflinePlayer(auctionItem.getOwner());

					AuctionBidEvent auctionBidEvent = new AuctionBidEvent(e.player, auctionItem, newBiddingAmount);
					Bukkit.getServer().getScheduler().runTask(AuctionHouse.getInstance(), () -> Bukkit.getServer().getPluginManager().callEvent(auctionBidEvent));
					if (auctionBidEvent.isCancelled()) return true;

					if (Settings.BIDDING_TAKES_MONEY.getBoolean()) {
						final double oldBidAmount = auctionItem.getCurrentPrice();

						if (!EconomyManager.hasBalance(e.player, newBiddingAmount)) {
							AuctionHouse.getInstance().getLocale().getMessage("general.notenoughmoney").sendPrefixedMessage(e.player);
							return true;
						}

						if (e.player.getUniqueId().equals(owner.getUniqueId()) || oldBidder.getUniqueId().equals(e.player.getUniqueId())) {
							return true;
						}

						if (!auctionItem.getHighestBidder().equals(auctionItem.getOwner())) {
							if (Settings.STORE_PAYMENTS_FOR_MANUAL_COLLECTION.getBoolean())
								AuctionHouse.getInstance().getDataManager().insertAuctionPayment(new AuctionPayment(
										oldBidder.getUniqueId(),
										oldBidAmount,
										auctionItem.getItem(),
										AuctionHouse.getInstance().getLocale().getMessage("general.prefix").getMessage(),
										PaymentReason.BID_RETURNED
								), null);
							else
								EconomyManager.deposit(oldBidder, oldBidAmount);
							if (oldBidder.isOnline())
								AuctionHouse.getInstance().getLocale().getMessage("pricing.moneyadd").processPlaceholder("player_balance", AuctionAPI.getInstance().formatNumber(EconomyManager.getBalance(oldBidder))).processPlaceholder("price", AuctionAPI.getInstance().formatNumber(oldBidAmount)).sendPrefixedMessage(oldBidder.getPlayer());
						}

						EconomyManager.withdrawBalance(e.player, newBiddingAmount);
						AuctionHouse.getInstance().getLocale().getMessage("pricing.moneyremove").processPlaceholder("player_balance", AuctionAPI.getInstance().formatNumber(EconomyManager.getBalance(e.player))).processPlaceholder("price", AuctionAPI.getInstance().formatNumber(newBiddingAmount)).sendPrefixedMessage(e.player);

					}

					auctionItem.setHighestBidder(e.player.getUniqueId());
					auctionItem.setHighestBidderName(e.player.getName());
					auctionItem.setCurrentPrice(newBiddingAmount);

					if (auctionItem.getBasePrice() != -1 && Settings.SYNC_BASE_PRICE_TO_HIGHEST_PRICE.getBoolean() && auctionItem.getCurrentPrice() > auctionItem.getBasePrice()) {
						auctionItem.setBasePrice(Settings.ROUND_ALL_PRICES.getBoolean() ? Math.round(auctionItem.getCurrentPrice()) : auctionItem.getCurrentPrice());
					}

					if (Settings.INCREASE_TIME_ON_BID.getBoolean()) {
						auctionItem.setExpiresAt(auctionItem.getExpiresAt() + 1000L * Settings.TIME_TO_INCREASE_BY_ON_BID.getInt());
					}

					if (oldBidder.isOnline()) {
						AuctionHouse.getInstance().getLocale().getMessage("auction.outbid")
								.processPlaceholder("player", e.player.getName())
								.processPlaceholder("player_displayname", AuctionAPI.getInstance().getDisplayName(e.player))
								.processPlaceholder("item", AuctionAPI.getInstance().getItemName(itemStack))
								.sendPrefixedMessage(oldBidder.getPlayer());
					}

					if (owner.isOnline()) {
						AuctionHouse.getInstance().getLocale().getMessage("auction.placedbid")
								.processPlaceholder("player", e.player.getName())
								.processPlaceholder("player_displayname", AuctionAPI.getInstance().getDisplayName(e.player))
								.processPlaceholder("amount", AuctionAPI.getInstance().formatNumber(auctionItem.getCurrentPrice()))
								.processPlaceholder("item", AuctionAPI.getInstance().getItemName(itemStack))
								.sendPrefixedMessage(owner.getPlayer());
					}

					if (Settings.BROADCAST_AUCTION_BID.getBoolean()) {
						Bukkit.getOnlinePlayers().forEach(player -> AuctionHouse.getInstance().getLocale().getMessage("auction.broadcast.bid")
								.processPlaceholder("player", e.player.getName())
								.processPlaceholder("player_displayname", AuctionAPI.getInstance().getDisplayName(e.player))
								.processPlaceholder("amount", AuctionAPI.getInstance().formatNumber(auctionItem.getCurrentPrice()))
								.processPlaceholder("item", AuctionAPI.getInstance().getItemName(itemStack))
								.sendPrefixedMessage(player));
					}

					e.manager.showGUI(e.player, new GUIAuctionHouse(GUIBid.this.auctionPlayer));

					return true;
				}
			};
		});
	}
}
