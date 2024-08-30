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
import ca.tweetzy.auctionhouse.auction.enums.PaymentReason;
import ca.tweetzy.auctionhouse.events.AuctionBidEvent;
import ca.tweetzy.auctionhouse.guis.AuctionUpdatingPagedGUI;
import ca.tweetzy.auctionhouse.guis.core.GUIAuctionHouse;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.gui.events.GuiClickEvent;
import ca.tweetzy.flight.utils.QuickItem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

/**
 * The current file has been created by Kiran Hart
 * Date Created: March 17 2021
 * Time Created: 11:18 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUIConfirmBid extends AuctionUpdatingPagedGUI<AuctionedItem> {

	private final AuctionPlayer auctionPlayer;
	private final AuctionedItem auctionItem;
	private final double bidAmount;

	public GUIConfirmBid(AuctionPlayer auctionPlayer, AuctionedItem auctionItem, double bidAmount) {
		super(null, auctionPlayer.getPlayer(), Settings.GUI_CONFIRM_BID_TITLE.getString(), 1, 20 * Settings.LIVE_BID_NUMBER_IN_CONFIRM_GUI_RATE.getInt(), Collections.singletonList(auctionItem));
		this.auctionPlayer = auctionPlayer;
		this.auctionItem = auctionItem;
		this.bidAmount = bidAmount;
		setAcceptsItems(false);

		draw();

		setOnOpen(open -> {
			if (Settings.USE_LIVE_BID_NUMBER_IN_CONFIRM_GUI.getBoolean())
				startTask();
		});

		applyClose();
	}


	@Override
	protected void drawFixed() {
		for (int i = 0; i < 4; i++)
			drawYes(i);

		for (int i = 5; i < 9; i++)
			drawNo(i);
	}

	private void drawYes(int slot) {
		setButton(slot, QuickItem
				.of(Settings.GUI_CONFIRM_BID_YES_ITEM.getString())
				.name(Settings.GUI_CONFIRM_BID_YES_NAME.getString())
				.lore(this.player, Settings.GUI_CONFIRM_BID_YES_LORE.getStringList())
				.make(), click -> {

			// Re-select the item to ensure that it's available
			AuctionedItem located = AuctionHouse.getInstance().getAuctionItemManager().getItem(this.auctionItem.getId());
			if (located == null) {
				endAndReturn(click);
				return;
			}

			double toIncrementBy = this.bidAmount == -1 ? auctionItem.getBidIncrementPrice() : this.bidAmount;

			double newBiddingAmount = 0;
			if (Settings.USE_REALISTIC_BIDDING.getBoolean()) {
				if (toIncrementBy > this.auctionItem.getCurrentPrice()) {
					newBiddingAmount = toIncrementBy;
				} else {
					if (Settings.BID_MUST_BE_HIGHER_THAN_PREVIOUS.getBoolean()) {
						endAndReturn(click);
						AuctionHouse.getInstance().getLocale().getMessage("pricing.bidmusthigherthanprevious").processPlaceholder("current_bid", auctionItem.getFormattedCurrentPrice()).sendPrefixedMessage(click.player);
						return;
					}

					newBiddingAmount = this.auctionItem.getCurrentPrice() + toIncrementBy;
				}
			} else {
				newBiddingAmount = this.auctionItem.getCurrentPrice() + toIncrementBy;
			}

			newBiddingAmount = Settings.ROUND_ALL_PRICES.getBoolean() ? Math.round(newBiddingAmount) : newBiddingAmount;

			if (Settings.PLAYER_NEEDS_TOTAL_PRICE_TO_BID.getBoolean() && !AuctionHouse.getCurrencyManager().has(click.player, newBiddingAmount)) {
				AuctionHouse.getInstance().getLocale().getMessage("general.notenoughmoney").sendPrefixedMessage(click.player);
				return;
			}

			ItemStack itemStack = auctionItem.getItem();

			OfflinePlayer oldBidder = Bukkit.getOfflinePlayer(auctionItem.getHighestBidder());
			OfflinePlayer owner = Bukkit.getOfflinePlayer(auctionItem.getOwner());

			AuctionBidEvent auctionBidEvent = new AuctionBidEvent(click.player, auctionItem, newBiddingAmount);
			Bukkit.getServer().getPluginManager().callEvent(auctionBidEvent);
			if (auctionBidEvent.isCancelled()) return;

			if (Settings.BIDDING_TAKES_MONEY.getBoolean()) {
				final double oldBidAmount = auctionItem.getCurrentPrice();

				if (!AuctionHouse.getCurrencyManager().has(click.player, newBiddingAmount)) {
					AuctionHouse.getInstance().getLocale().getMessage("general.notenoughmoney").sendPrefixedMessage(click.player);
					return;
				}

				if (click.player.getUniqueId().equals(owner.getUniqueId()) || oldBidder.getUniqueId().equals(click.player.getUniqueId())) {
					return;
				}

				if (!auctionItem.getHighestBidder().equals(auctionItem.getOwner())) {
					if (Settings.STORE_PAYMENTS_FOR_MANUAL_COLLECTION.getBoolean())
						AuctionHouse.getDataManager().insertAuctionPayment(new AuctionPayment(
								oldBidder.getUniqueId(),
								oldBidAmount,
								auctionItem.getItem(),
								AuctionHouse.getInstance().getLocale().getMessage("general.prefix").getMessage(),
								PaymentReason.BID_RETURNED,
								auctionItem.getCurrency(),
								auctionItem.getCurrencyItem()
						), null);
					else
						AuctionHouse.getCurrencyManager().deposit(oldBidder, oldBidAmount, auctionItem.getCurrency(), auctionItem.getCurrencyItem());

					if (oldBidder.isOnline())
						AuctionHouse.getInstance().getLocale().getMessage("pricing.moneyadd")
								.processPlaceholder("player_balance", AuctionHouse.getAPI().getFinalizedCurrencyNumber(AuctionHouse.getCurrencyManager().getBalance(oldBidder, auctionItem.getCurrency().split("/")[0], auctionItem.getCurrency().split("/")[1]), auctionItem.getCurrency(), auctionItem.getCurrencyItem()))
								.processPlaceholder("price", AuctionHouse.getAPI().getFinalizedCurrencyNumber(oldBidAmount, auctionItem.getCurrency(), auctionItem.getCurrencyItem()))
								.sendPrefixedMessage(oldBidder.getPlayer());
				}


				AuctionHouse.getCurrencyManager().withdraw(click.player, newBiddingAmount, auctionItem.getCurrency(), auctionItem.getCurrencyItem());
				AuctionHouse.getInstance().getLocale().getMessage("pricing.moneyremove")
						.processPlaceholder("player_balance", AuctionHouse.getAPI().getFinalizedCurrencyNumber(AuctionHouse.getCurrencyManager().getBalance(click.player, auctionItem.getCurrency().split("/")[0], auctionItem.getCurrency().split("/")[1]), auctionItem.getCurrency(), auctionItem.getCurrencyItem()))
						.processPlaceholder("price", AuctionHouse.getAPI().getFinalizedCurrencyNumber(newBiddingAmount, auctionItem.getCurrency(), auctionItem.getCurrencyItem()))
						.sendPrefixedMessage(click.player);

			}

			auctionItem.setHighestBidder(click.player.getUniqueId());
			auctionItem.setHighestBidderName(click.player.getName());
			auctionItem.setCurrentPrice(newBiddingAmount);
			if (auctionItem.getBasePrice() != -1 && Settings.SYNC_BASE_PRICE_TO_HIGHEST_PRICE.getBoolean() && Settings.ALLOW_USAGE_OF_BUY_NOW_SYSTEM.getBoolean() && auctionItem.getCurrentPrice() > auctionItem.getBasePrice()) {
				auctionItem.setBasePrice(Settings.ROUND_ALL_PRICES.getBoolean() ? Math.round(auctionItem.getCurrentPrice()) : auctionItem.getCurrentPrice());
			}

			if (Settings.INCREASE_TIME_ON_BID.getBoolean()) {
				auctionItem.setExpiresAt(auctionItem.getExpiresAt() + 1000L * Settings.TIME_TO_INCREASE_BY_ON_BID.getInt());
			}

			if (Settings.BROADCAST_AUCTION_BID.getBoolean()) {
				Bukkit.getOnlinePlayers().forEach(player -> AuctionHouse.getInstance().getLocale().getMessage("auction.broadcast.bid")
						.processPlaceholder("player", click.player.getName())
						.processPlaceholder("player_displayname", AuctionAPI.getInstance().getDisplayName(click.player))
						.processPlaceholder("amount", auctionItem.getFormattedCurrentPrice())
						.processPlaceholder("item", AuctionAPI.getInstance().getItemName(itemStack))
						.sendPrefixedMessage(player));
			}

			if (oldBidder.isOnline()) {
				AuctionHouse.getInstance().getLocale().getMessage("auction.outbid")
						.processPlaceholder("player", click.player.getName())
						.processPlaceholder("player_displayname", AuctionAPI.getInstance().getDisplayName(click.player))
						.processPlaceholder("item", AuctionAPI.getInstance().getItemName(itemStack))
						.sendPrefixedMessage(oldBidder.getPlayer());
			}

			if (owner.isOnline()) {
				AuctionHouse.getInstance().getLocale().getMessage("auction.placedbid")
						.processPlaceholder("player", click.player.getName())
						.processPlaceholder("player_displayname", AuctionAPI.getInstance().getDisplayName(click.player))
						.processPlaceholder("amount", auctionItem.getFormattedCurrentPrice())
						.processPlaceholder("item", AuctionAPI.getInstance().getItemName(itemStack))
						.sendPrefixedMessage(owner.getPlayer());
			}

			endAndReturn(click);
		});
	}

	private void drawNo(int slot) {
		setButton(slot, QuickItem
				.of(Settings.GUI_CONFIRM_BID_NO_ITEM.getString())
				.name(Settings.GUI_CONFIRM_BID_NO_NAME.getString())
				.lore(this.player, Settings.GUI_CONFIRM_BID_NO_LORE.getStringList())
				.make(), click -> {

			cancelTask();
			click.manager.showGUI(click.player, new GUIAuctionHouse(this.auctionPlayer));
		});
	}

	private void endAndReturn(GuiClickEvent event) {
		cancelTask();
		event.manager.showGUI(event.player, new GUIAuctionHouse(this.auctionPlayer));
	}

	@Override
	protected ItemStack makeDisplayItem(AuctionedItem auctionItem) {
		return auctionItem.getBidStack(this.player);
	}

	@Override
	protected void onClick(AuctionedItem object, GuiClickEvent clickEvent) {
	}

	@Override
	protected List<Integer> fillSlots() {
		return Collections.singletonList(4);
	}
}
