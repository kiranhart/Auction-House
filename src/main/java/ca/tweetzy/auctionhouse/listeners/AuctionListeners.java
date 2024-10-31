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

package ca.tweetzy.auctionhouse.listeners;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.auction.AuctionedItem;
import ca.tweetzy.auctionhouse.auction.enums.AuctionSaleType;
import ca.tweetzy.auctionhouse.auction.enums.AuctionStatisticType;
import ca.tweetzy.auctionhouse.api.event.AuctionAdminEvent;
import ca.tweetzy.auctionhouse.events.AuctionBidEvent;
import ca.tweetzy.auctionhouse.events.AuctionEndEvent;
import ca.tweetzy.auctionhouse.events.AuctionStartEvent;
import ca.tweetzy.auctionhouse.impl.AuctionStatistic;
import ca.tweetzy.auctionhouse.model.discord.DiscordMessageCreator;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.auctionhouse.transaction.Transaction;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

/**
 * The current file has been created by Kiran Hart
 * Date Created: February 27 2021
 * Time Created: 4:49 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class AuctionListeners implements Listener {

	@EventHandler
	public void onAuctionStart(AuctionStartEvent e) {
		// new stat system
		final Player seller = e.getSeller();
		final AuctionedItem auctionedItem = e.getAuctionItem();

		// ignore if server item
		if (!auctionedItem.isServerItem())
			new AuctionStatistic(seller.getUniqueId(), auctionedItem.isBidItem() ? AuctionStatisticType.CREATED_AUCTION : AuctionStatisticType.CREATED_BIN, 1).store(null);

		if (Settings.DISCORD_ENABLED.getBoolean()) {

			AuctionHouse.newChain().async(() -> {
				Settings.DISCORD_WEBHOOKS.getStringList().forEach(hook -> {
					final boolean isBid = e.getAuctionItem().isBidItem();

					if (isBid && Settings.DISCORD_ALERT_ON_AUCTION_START.getBoolean()) {
						DiscordMessageCreator webhook = DiscordMessageCreator
								.of(hook, DiscordMessageCreator.MessageType.NEW_AUCTION_LISTING)
								.seller(seller)
								.listing(auctionedItem);

						if (Settings.DISCORD_DELAY_LISTINGS.getBoolean()) {
							AuctionHouse.getListingManager().addListingWebhook(auctionedItem.getId(), webhook);
							return;
						}

						webhook.send();
					}

					if (!isBid && Settings.DISCORD_ALERT_ON_BIN_START.getBoolean()) {
						DiscordMessageCreator webhook = DiscordMessageCreator
								.of(hook, DiscordMessageCreator.MessageType.NEW_BIN_LISTING)
								.seller(seller)
								.listing(auctionedItem);

						if (Settings.DISCORD_DELAY_LISTINGS.getBoolean()) {
							AuctionHouse.getListingManager().addListingWebhook(auctionedItem.getId(), webhook);
							return;
						}
						webhook.send();
					}
				});
			}).execute();

		}
	}

	@EventHandler
	public void onAuctionEnd(AuctionEndEvent e) {
		// new stat system
		final OfflinePlayer originalOwner = e.getOriginalOwner(), buyer = e.getBuyer();
		final UUID originalOwnerUUID = originalOwner.getUniqueId(), buyerUUID = buyer.getUniqueId();
		final AuctionedItem auctionedItem = e.getAuctionItem();

		if (!auctionedItem.isServerItem()) {
			new AuctionStatistic(originalOwnerUUID, auctionedItem.isBidItem() ? AuctionStatisticType.SOLD_AUCTION : AuctionStatisticType.SOLD_BIN, 1).store(null);
			new AuctionStatistic(originalOwnerUUID, AuctionStatisticType.MONEY_EARNED, e.getSaleType() == AuctionSaleType.USED_BIDDING_SYSTEM ? auctionedItem.getCurrentPrice() : auctionedItem.getBasePrice()).store(null);
		}

		AuctionHouse.getListingManager().cancelListingWebhook(auctionedItem.getId());
		new AuctionStatistic(buyerUUID, AuctionStatisticType.MONEY_SPENT, e.getSaleType() == AuctionSaleType.USED_BIDDING_SYSTEM ? auctionedItem.getCurrentPrice() : auctionedItem.getBasePrice()).store(null);

		AuctionHouse.newChain().async(() -> {
			if (Settings.RECORD_TRANSACTIONS.getBoolean()) {

				double price = auctionedItem.getBasePrice();
				if (e.getSaleType() == AuctionSaleType.USED_BIDDING_SYSTEM) {
					price = auctionedItem.getCurrentPrice();
				}

				AuctionHouse.getDataManager().insertTransaction(new Transaction(
						UUID.randomUUID(),
						originalOwnerUUID,
						buyerUUID,
						auctionedItem.getOwnerName(),
						buyer.getName(),
						System.currentTimeMillis(),
						auctionedItem.getItem(),
						e.getSaleType(),
						price
				), (error, transaction) -> {
					if (error == null) {
						AuctionHouse.getTransactionManager().addTransaction(transaction);
					}
				});
			}

			if (Settings.DISCORD_ENABLED.getBoolean()) {
				if (Settings.DISCORD_ALERT_ON_AUCTION_WON.getBoolean() && e.getSaleType() == AuctionSaleType.USED_BIDDING_SYSTEM)
					Settings.DISCORD_WEBHOOKS.getStringList().forEach(hook -> {
						DiscordMessageCreator
								.of(hook, DiscordMessageCreator.MessageType.AUCTION_LISTING_WON)
								.seller(originalOwner)
								.buyer(e.getBuyer())
								.listing(auctionedItem)
								.send();
					});

				if (Settings.DISCORD_ALERT_ON_BIN_BUY.getBoolean() && e.getSaleType() != AuctionSaleType.USED_BIDDING_SYSTEM)
					Settings.DISCORD_WEBHOOKS.getStringList().forEach(hook -> {
						DiscordMessageCreator
								.of(hook, DiscordMessageCreator.MessageType.BIN_LISTING_BOUGHT)
								.seller(originalOwner)
								.buyer(e.getBuyer())
								.listing(auctionedItem)
								.send();
					});
			}
		}).execute();
	}

	@EventHandler
	public void onAuctionBid(AuctionBidEvent e) {
		if (!Settings.DISCORD_ENABLED.getBoolean() && Settings.DISCORD_ALERT_ON_BID.getBoolean()) return;
		AuctionHouse.newChain().async(() -> {
			final AuctionedItem auctionedItem = e.getAuctionedItem();
			Settings.DISCORD_WEBHOOKS.getStringList().forEach(hook -> {
				DiscordMessageCreator
						.of(hook, DiscordMessageCreator.MessageType.BID_PLACED)
						.seller(Bukkit.getOfflinePlayer(auctionedItem.getOwner()))
						.bidder(e.getBidder())
						.bidAmount(e.getNewBidAmount())
						.listing(auctionedItem)
						.send();
			});
		}).execute();
	}

	@EventHandler
	public void onAdminAction(AuctionAdminEvent event) {
		if (!Settings.LOG_ADMIN_ACTIONS.getBoolean()) return;
		AuctionHouse.getListingManager().cancelListingWebhook(event.getAuctionAdminLog().getItemId());
		AuctionHouse.getDataManager().insertLog(event.getAuctionAdminLog());
	}
}
