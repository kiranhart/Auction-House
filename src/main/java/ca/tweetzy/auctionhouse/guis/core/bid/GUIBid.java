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

package ca.tweetzy.auctionhouse.guis.core.bid;



import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.auctionhouse.settings.Translations;
import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.auction.AuctionPayment;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.auction.AuctionedItem;
import ca.tweetzy.auctionhouse.auction.enums.PaymentReason;
import ca.tweetzy.auctionhouse.events.AuctionBidEvent;
import ca.tweetzy.auctionhouse.guis.AuctionBaseGUI;
import ca.tweetzy.auctionhouse.guis.confirmation.GUIConfirmBid;
import ca.tweetzy.auctionhouse.guis.core.GUIAuctionHouse;
import ca.tweetzy.flight.utils.input.TitleInput;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.flight.utils.MathUtil;
import ca.tweetzy.flight.utils.QuickItem;

import java.util.HashMap;

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
public class GUIBid extends AuctionBaseGUI {

	private final AuctionPlayer auctionPlayer;
	private final AuctionedItem auctionItem;

	public GUIBid(AuctionPlayer auctionPlayer, AuctionedItem auctionItem) {
		super(new GUIAuctionHouse(auctionPlayer), auctionPlayer.getPlayer(), TranslationManager.string(auctionPlayer.getPlayer(), Translations.GUI_BIDDING_TITLE), 3);
		this.auctionPlayer = auctionPlayer;
		this.auctionItem = auctionItem;
		setDefaultItem(QuickItem.bg(QuickItem.of(Settings.GUI_BIDDING_BG_ITEM.getString()).make()));
		setOnClose(close -> close.manager.showGUI(close.player, new GUIAuctionHouse(this.auctionPlayer)));
		draw();
	}

	@Override
	protected void draw() {
		setItem(1, 4, this.auctionItem.getItem());

		// Watchlist toggle (only if not own listing and watchlist enabled)
		if (Settings.WATCHLIST_ENABLED.getBoolean() && !this.auctionPlayer.getUuid().equals(this.auctionItem.getOwner())) {
			boolean watching = AuctionHouse.getWatchlistManager().isWatching(this.auctionPlayer.getUuid(), this.auctionItem.getId());
			setButton(2, 4, QuickItem
					.of(watching ? "BARRIER" : "BOOKMARK")
					.name(TranslationManager.string(this.player, watching ? Translations.GUI_BIDDING_WATCHLIST_REMOVE_NAME : Translations.GUI_BIDDING_WATCHLIST_ADD_NAME))
					.lore(this.player, TranslationManager.list(this.player, watching ? Translations.GUI_BIDDING_WATCHLIST_REMOVE_LORE : Translations.GUI_BIDDING_WATCHLIST_ADD_LORE))
					.make(), e -> {
				if (watching) {
					AuctionHouse.getWatchlistManager().remove(e.player.getUniqueId(), this.auctionItem.getId(), (err, ok) -> {
						if (ok) {
							Common.tell(e.player, TranslationManager.string(e.player instanceof Player pl ? pl : null, Translations.WATCHLIST_REMOVED, "item",AuctionAPI.getInstance().getItemName(this.auctionItem.getItem())));
							draw();
						}
					});
				} else {
					if (AuctionHouse.getWatchlistManager().getWatchlistCount(e.player.getUniqueId()) >= Settings.WATCHLIST_MAX_LISTINGS.getInt()) {
						Common.tell(e.player, TranslationManager.string(e.player instanceof Player pl ? pl : null, Translations.WATCHLIST_LIMIT_REACHED, "max",String.valueOf(Settings.WATCHLIST_MAX_LISTINGS.getInt())));
						return;
					}
					AuctionHouse.getWatchlistManager().add(e.player.getUniqueId(), this.auctionItem.getId(), (err, ok) -> {
						if (ok) {
							Common.tell(e.player, TranslationManager.string(e.player instanceof Player pl ? pl : null, Translations.WATCHLIST_ADDED, "item",AuctionAPI.getInstance().getItemName(this.auctionItem.getItem())));
							draw();
						}
					});
				}
			});
		}

		setButton(1, 2, QuickItem
				.of(Settings.GUI_BIDDING_ITEMS_DEFAULT_ITEM.getString())
				.name(TranslationManager.string(this.player, Translations.GUI_BIDDING_DEFAULT_NAME))
				.lore(this.player, TranslationManager.list(this.player, Translations.GUI_BIDDING_DEFAULT_LORE)).make(), e -> {

			if (Settings.PLAYER_NEEDS_TOTAL_PRICE_TO_BID.getBoolean() && !AuctionHouse.getCurrencyManager().has(e.player, auctionItem.getCurrentPrice() + auctionItem.getBidIncrementPrice())) {
				Common.tell(e.player, TranslationManager.string(e.player instanceof Player pl ? pl : null, Translations.GENERAL_NOT_ENOUGH_MONEY));
				return;
			}

			final double minBid = Settings.USE_REALISTIC_BIDDING.getBoolean() ? this.auctionItem.getCurrentPrice() + this.auctionItem.getBidIncrementPrice() : this.auctionItem.getBidIncrementPrice();
			
			// Use safe transition method to prevent setOnClose from running
			this.safeTransitionTo(e.manager, new GUIConfirmBid(this.auctionPlayer, auctionItem, minBid));
		});

		setButton(1, 6, QuickItem
				.of(Settings.GUI_BIDDING_ITEMS_CUSTOM_ITEM.getString())
				.name(TranslationManager.string(this.player, Translations.GUI_BIDDING_CUSTOM_NAME))
				.lore(this.player, TranslationManager.list(this.player, Translations.GUI_BIDDING_CUSTOM_LORE))
				.make(), e -> {

		// TitleInput automatically handles allowClose and inventory closing
		new TitleInput(AuctionHouse.getInstance(), player, TranslationManager.string(Translations.TITLES_ENTER_BID_TITLE), TranslationManager.string(Translations.TITLES_ENTER_BID_SUBTITLE)) {

				@Override
				public void onExit(Player player) {
					AuctionHouse.getGuiManager().showGUI(player, new GUIAuctionHouse(GUIBid.this.auctionPlayer));
				}

				@Override
				public boolean onResult(String string) {
					string = ChatColor.stripColor(string);

					if (!MathUtil.isDouble(string)) {
						Common.tell(player, TranslationManager.string(player instanceof Player pl ? pl : null, Translations.GENERAL_NOT_A_NUMBER, "value",string));
						return false;
					}

					double value = Double.parseDouble(string);

					if (value <= 0) {
						Common.tell(e.player, TranslationManager.string(e.player instanceof Player pl ? pl : null, Translations.GENERAL_CANNOT_BE_ZERO));
						return false;
					}

					if (value > Settings.MAX_AUCTION_INCREMENT_PRICE.getDouble()) {
						Common.tell(e.player, TranslationManager.string(e.player instanceof Player pl ? pl : null, Translations.PRICING_MAX_BID_INCREMENT_PRICE, "price",Settings.MAX_AUCTION_INCREMENT_PRICE.getDouble()));
						return false;
					}

					double newBiddingAmount = 0;
					if (Settings.USE_REALISTIC_BIDDING.getBoolean()) {
						if (value < auctionItem.getCurrentPrice() + auctionItem.getBidIncrementPrice()) {
							Common.tell(e.player, TranslationManager.string(e.player instanceof Player pl ? pl : null, Translations.PRICING_MIN_BID_INCREMENT_PRICE, "price",AuctionHouse.getAPI().getFinalizedCurrencyNumber(auctionItem.getCurrentPrice() + auctionItem.getBidIncrementPrice(), auctionItem.getCurrency(), auctionItem.getCurrencyItem())));
							return false;
						}

						if (value > GUIBid.this.auctionItem.getCurrentPrice()) {
							newBiddingAmount = value;
						} else {
							if (Settings.BID_MUST_BE_HIGHER_THAN_PREVIOUS.getBoolean()) {
								e.manager.showGUI(e.player, new GUIAuctionHouse(GUIBid.this.auctionPlayer));
								Common.tell(e.player, TranslationManager.string(e.player instanceof Player pl ? pl : null, Translations.PRICING_BID_MUST_HIGHER_THAN_PREVIOUS, "current_bid",AuctionHouse.getAPI().getFinalizedCurrencyNumber(auctionItem.getCurrentPrice(), auctionItem.getCurrency(), auctionItem.getCurrencyItem())));
								return true;
							}

							newBiddingAmount = GUIBid.this.auctionItem.getCurrentPrice() + value;
						}
					} else {
						if (value < auctionItem.getBidIncrementPrice()) {
							Common.tell(e.player, TranslationManager.string(e.player instanceof Player pl ? pl : null, Translations.PRICING_MIN_BID_INCREMENT_PRICE, "price",AuctionHouse.getAPI().getFinalizedCurrencyNumber(auctionItem.getBidIncrementPrice(), auctionItem.getCurrency(), auctionItem.getCurrencyItem())));
							return false;
						}

						newBiddingAmount = GUIBid.this.auctionItem.getCurrentPrice() + value;
					}

					newBiddingAmount = Settings.ROUND_ALL_PRICES.getBoolean() ? Math.round(newBiddingAmount) : newBiddingAmount;

					if (Settings.PLAYER_NEEDS_TOTAL_PRICE_TO_BID.getBoolean() && !AuctionHouse.getCurrencyManager().has(e.player, newBiddingAmount)) {
						Common.tell(e.player, TranslationManager.string(e.player instanceof Player pl ? pl : null, Translations.GENERAL_NOT_ENOUGH_MONEY));
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

						if (!AuctionHouse.getCurrencyManager().has(e.player, newBiddingAmount)) {
							Common.tell(e.player, TranslationManager.string(e.player instanceof Player pl ? pl : null, Translations.GENERAL_NOT_ENOUGH_MONEY));
							return true;
						}

						if (e.player.getUniqueId().equals(owner.getUniqueId()) || oldBidder.getUniqueId().equals(e.player.getUniqueId())) {
							return true;
						}

						if (!auctionItem.getHighestBidder().equals(auctionItem.getOwner())) {
							if (Settings.STORE_PAYMENTS_FOR_MANUAL_COLLECTION.getBoolean())
								AuctionHouse.getDataManager().insertAuctionPayment(new AuctionPayment(
										oldBidder.getUniqueId(),
										oldBidAmount,
										auctionItem.getItem(),
										TranslationManager.string(Translations.GENERAL_PREFIX),
										PaymentReason.BID_RETURNED,
										auctionItem.getCurrency(),
										auctionItem.getCurrencyItem()
								), null);
							else
								AuctionHouse.getCurrencyManager().deposit(oldBidder, oldBidAmount, auctionItem.getCurrency(), auctionItem.getCurrencyItem());
							String[] currencyParts = auctionItem.getCurrency().split("/");
							String balanceStr = AuctionHouse.getAPI().getFinalizedCurrencyNumber(AuctionHouse.getCurrencyManager().getBalance(oldBidder, currencyParts.length > 0 ? currencyParts[0] : "Vault", currencyParts.length > 1 ? currencyParts[1] : "Vault"), auctionItem.getCurrency(), auctionItem.getCurrencyItem());
							String priceStr = AuctionHouse.getAPI().getFinalizedCurrencyNumber(oldBidAmount, auctionItem.getCurrency(), auctionItem.getCurrencyItem());
							if (oldBidder.isOnline() && oldBidder.getPlayer() != null) {
								Common.tell(oldBidder.getPlayer(), TranslationManager.string(oldBidder.getPlayer() instanceof Player pl ? pl : null, Translations.PRICING_MONEY_ADD, "player_balance",balanceStr,"price",priceStr));
							} else {
								HashMap<String, String> placeholders = new HashMap<>();
								placeholders.put("player_balance", balanceStr);
								placeholders.put("price", priceStr);
								AuctionHouse.getNotificationManager().queue(oldBidder.getUniqueId(), "pricing.moneyadd", placeholders);
							}
						}

						AuctionHouse.getCurrencyManager().withdraw(e.player, newBiddingAmount, auctionItem.getCurrency(), auctionItem.getCurrencyItem());
						Common.tell(e.player, TranslationManager.string(e.player instanceof Player pl ? pl : null, Translations.PRICING_MONEY_REMOVE, "player_balance",AuctionHouse.getAPI().getFinalizedCurrencyNumber(AuctionHouse.getCurrencyManager().getBalance(e.player, auctionItem.getCurrency().split("/")[0], auctionItem.getCurrency().split("/")[1]), auctionItem.getCurrency(), auctionItem.getCurrencyItem()),"price",AuctionHouse.getAPI().getFinalizedCurrencyNumber(newBiddingAmount, auctionItem.getCurrency(), auctionItem.getCurrencyItem())));

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

					if (oldBidder.isOnline() && oldBidder.getPlayer() != null) {
						Common.tell(oldBidder.getPlayer(), TranslationManager.string(oldBidder.getPlayer() instanceof Player pl ? pl : null, Translations.AUCTION_OUTBID, "player",e.player.getName(),"player_displayname",AuctionAPI.getInstance().getDisplayName(e.player),"item",AuctionAPI.getInstance().getItemName(itemStack)));
					} else {
						HashMap<String, String> outbidPlaceholders = new HashMap<>();
						outbidPlaceholders.put("player", e.player.getName());
						outbidPlaceholders.put("player_displayname", AuctionAPI.getInstance().getDisplayName(e.player));
						outbidPlaceholders.put("item", AuctionAPI.getInstance().getItemName(itemStack));
						AuctionHouse.getNotificationManager().queue(oldBidder.getUniqueId(), "auction.outbid", outbidPlaceholders);
					}

					if (owner.isOnline() && owner.getPlayer() != null) {
						Common.tell(owner.getPlayer(), TranslationManager.string(owner.getPlayer() instanceof Player pl ? pl : null, Translations.AUCTION_PLACED_BID, "player",e.player.getName(),"player_displayname",AuctionAPI.getInstance().getDisplayName(e.player),"amount",AuctionHouse.getAPI().getFinalizedCurrencyNumber(auctionItem.getCurrentPrice(), auctionItem.getCurrency(), auctionItem.getCurrencyItem()),"item",AuctionAPI.getInstance().getItemName(itemStack)));
					} else {
						HashMap<String, String> placedbidPlaceholders = new HashMap<>();
						placedbidPlaceholders.put("player", e.player.getName());
						placedbidPlaceholders.put("player_displayname", AuctionAPI.getInstance().getDisplayName(e.player));
						placedbidPlaceholders.put("amount", AuctionHouse.getAPI().getFinalizedCurrencyNumber(auctionItem.getCurrentPrice(), auctionItem.getCurrency(), auctionItem.getCurrencyItem()));
						placedbidPlaceholders.put("item", AuctionAPI.getInstance().getItemName(itemStack));
						AuctionHouse.getNotificationManager().queue(owner.getUniqueId(), "auction.placedbid", placedbidPlaceholders);
					}

					if (Settings.BROADCAST_AUCTION_BID.getBoolean()) {
						Bukkit.getOnlinePlayers().forEach(player -> Common.tell(player, TranslationManager.string(player instanceof Player pl ? pl : null, Translations.AUCTION_BROADCAST_BID, "player",e.player.getName(),"player_displayname",AuctionAPI.getInstance().getDisplayName(e.player),"amount",AuctionHouse.getAPI().getFinalizedCurrencyNumber(auctionItem.getCurrentPrice(), auctionItem.getCurrency(), auctionItem.getCurrencyItem()),"item",AuctionAPI.getInstance().getItemName(itemStack))));
					}

					e.manager.showGUI(e.player, new GUIAuctionHouse(GUIBid.this.auctionPlayer));

					return true;
				}
			};
		});
	}
}
