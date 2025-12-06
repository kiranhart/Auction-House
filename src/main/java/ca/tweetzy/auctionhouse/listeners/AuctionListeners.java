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
import ca.tweetzy.auctionhouse.api.auction.RequestTransaction;
import ca.tweetzy.auctionhouse.api.event.AuctionAdminEvent;
import ca.tweetzy.auctionhouse.api.event.AuctionRequestCompleteEvent;
import ca.tweetzy.auctionhouse.auction.AuctionedItem;
import ca.tweetzy.auctionhouse.auction.enums.AuctionSaleType;
import ca.tweetzy.auctionhouse.auction.enums.AuctionStatisticType;
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

		// Log auction creation
		if (AuctionHouse.getTransactionLogger() != null && !e.isCancelled()) {
			String itemName = auctionedItem.getItem().hasItemMeta() && auctionedItem.getItem().getItemMeta().hasDisplayName() 
				? auctionedItem.getItem().getItemMeta().getDisplayName() 
				: auctionedItem.getItem().getType().name();
			AuctionHouse.getTransactionLogger().logAuctionCreate(
				seller.getName(),
				itemName,
				auctionedItem.getItem().getAmount(),
				auctionedItem.getBasePrice(),
				auctionedItem.getCurrency(),
				auctionedItem.getId().toString(),
				auctionedItem.isBidItem()
			);
		}

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
	public void onAuctionRequestComplete(AuctionRequestCompleteEvent event) {
		if (!Settings.RECORD_TRANSACTIONS.getBoolean()) return;

		final RequestTransaction completedRequest = event.getRequestTransaction();

		// Log request fulfillment with balance changes
		if (AuctionHouse.getTransactionLogger() != null && !event.isCancelled()) {
			String itemName = completedRequest.getRequestedItem().hasItemMeta() && completedRequest.getRequestedItem().getItemMeta().hasDisplayName()
				? completedRequest.getRequestedItem().getItemMeta().getDisplayName()
				: completedRequest.getRequestedItem().getType().name();
			// Get currency from original listing if available
			String currency = "Vault/Vault"; // Default currency
			if (event.getOriginalListing() != null) {
				currency = event.getOriginalListing().getCurrency();
			}
			
			// Get balances for logging
			String[] currencyParts = currency.split("/");
			String currencyPlugin = currencyParts.length > 0 ? currencyParts[0] : "Vault";
			String currencyName = currencyParts.length > 1 ? currencyParts[1] : "Vault";
			
			OfflinePlayer requester = Bukkit.getOfflinePlayer(completedRequest.getRequesterUUID());
			OfflinePlayer fulfiller = Bukkit.getOfflinePlayer(completedRequest.getFulfillerUUID());
			
			double requesterOldBalance = AuctionHouse.getCurrencyManager().getBalance(requester, currencyPlugin, currencyName);
			double fulfillerOldBalance = AuctionHouse.getCurrencyManager().getBalance(fulfiller, currencyPlugin, currencyName);
			
			double paymentAmount = completedRequest.getPaymentTotal();
			double requesterNewBalance = requesterOldBalance - paymentAmount;
			double fulfillerNewBalance = fulfillerOldBalance + paymentAmount;
			
			AuctionHouse.getTransactionLogger().logRequestFulfill(
				completedRequest.getId().toString(),
				completedRequest.getRequesterName(),
				completedRequest.getFulfillerName(),
				itemName,
				completedRequest.getAmountRequested(),
				completedRequest.getPaymentTotal(),
				currency,
				requesterOldBalance,
				requesterNewBalance,
				fulfillerOldBalance,
				fulfillerNewBalance
			);
		}

		completedRequest.store(stored -> {
			if (stored != null) {
				// Create a regular Transaction for the completed offer
				// The fulfiller is the seller (they sold the item), the requester is the buyer
				// Set amount to 1 - each purchase counts as a single transaction regardless of stack size
				final org.bukkit.inventory.ItemStack transactionItem = completedRequest.getRequestedItem().clone();
				transactionItem.setAmount(1);

				AuctionHouse.newChain().async(() -> {
					final Transaction requestTransaction = new Transaction(
							UUID.randomUUID(),
							completedRequest.getFulfillerUUID(), // fulfiller is the seller
							completedRequest.getRequesterUUID(), // requester is the buyer
							completedRequest.getFulfillerName(),
							completedRequest.getRequesterName(),
							completedRequest.getTimeCreated(),
							transactionItem,
							AuctionSaleType.WITHOUT_BIDDING_SYSTEM, // offers are always non-bid
							completedRequest.getPaymentTotal()
					);
					AuctionHouse.getDataManager().insertTransaction(requestTransaction, (error, transaction) -> {
						if (error != null) {
							AuctionHouse.getInstance().getLogger().severe("Failed to insert transaction for request " + completedRequest.getId() + ": " + error.getMessage());
							error.printStackTrace();
						} else if (transaction != null) {
							AuctionHouse.getTransactionManager().addTransaction(transaction);
						} else {
							AuctionHouse.getInstance().getLogger().warning("Transaction insert succeeded but fetch returned null for request " + completedRequest.getId() + ". Transaction may not be in memory.");
							// Fallback: use the original transaction object since insert was successful
							AuctionHouse.getTransactionManager().addTransaction(requestTransaction);
						}
					});
				}).execute();
			}
		});
	}

	@EventHandler
	public void onAuctionEnd(AuctionEndEvent e) {
		// new stat system
		final OfflinePlayer originalOwner = e.getOriginalOwner(), buyer = e.getBuyer();
		final UUID originalOwnerUUID = originalOwner.getUniqueId(), buyerUUID = buyer.getUniqueId();
		final AuctionedItem auctionedItem = e.getAuctionItem();

		// Log auction end with balance changes
		if (AuctionHouse.getTransactionLogger() != null && !e.isCancelled()) {
			String itemName = auctionedItem.getItem().hasItemMeta() && auctionedItem.getItem().getItemMeta().hasDisplayName()
				? auctionedItem.getItem().getItemMeta().getDisplayName()
				: auctionedItem.getItem().getType().name();
			
			// Get balances for logging
			String[] currencyParts = auctionedItem.getCurrency().split("/");
			String currencyPlugin = currencyParts.length > 0 ? currencyParts[0] : "Vault";
			String currencyName = currencyParts.length > 1 ? currencyParts[1] : "Vault";
			
			double buyerOldBalance = AuctionHouse.getCurrencyManager().getBalance(buyer, currencyPlugin, currencyName);
			double sellerOldBalance = AuctionHouse.getCurrencyManager().getBalance(originalOwner, currencyPlugin, currencyName);
			
			double transactionAmount = e.getSaleType() == AuctionSaleType.USED_BIDDING_SYSTEM ? auctionedItem.getCurrentPrice() : auctionedItem.getBasePrice();
			double tax = e.getTax();
			double buyerPays = Settings.TAX_CHARGE_SALES_TAX_TO_BUYER.getBoolean() ? transactionAmount + tax : transactionAmount;
			double sellerReceives = Settings.TAX_CHARGE_SALES_TAX_TO_BUYER.getBoolean() ? transactionAmount : transactionAmount - tax;
			
			double buyerNewBalance = buyerOldBalance - buyerPays;
			double sellerNewBalance = sellerOldBalance + sellerReceives;
			
			if (e.getSaleType() == AuctionSaleType.USED_BIDDING_SYSTEM) {
				// Auction expired/sold via bidding
				AuctionHouse.getTransactionLogger().logAuctionExpire(
					originalOwner.getName() != null ? originalOwner.getName() : "Unknown",
					buyer.getName() != null ? buyer.getName() : "Unknown",
					itemName,
					auctionedItem.getItem().getAmount(),
					auctionedItem.getCurrentPrice(),
					auctionedItem.getCurrency(),
					auctionedItem.getId().toString(),
					buyerOldBalance,
					buyerNewBalance,
					sellerOldBalance,
					sellerNewBalance
				);
			} else {
				// Buy now purchase
				AuctionHouse.getTransactionLogger().logAuctionPurchase(
					buyer.getName() != null ? buyer.getName() : "Unknown",
					originalOwner.getName() != null ? originalOwner.getName() : "Unknown",
					itemName,
					auctionedItem.getItem().getAmount(),
					auctionedItem.getBasePrice(),
					auctionedItem.getCurrency(),
					auctionedItem.getId().toString(),
					buyerOldBalance,
					buyerNewBalance,
					sellerOldBalance,
					sellerNewBalance
				);
			}
		}

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

				// Create transaction item with amount 1 - each purchase counts as a single transaction
				org.bukkit.inventory.ItemStack transactionItem = auctionedItem.getItem().clone();
				transactionItem.setAmount(1);
				
				final Transaction auctionTransaction = new Transaction(
						UUID.randomUUID(),
						originalOwnerUUID,
						buyerUUID,
						auctionedItem.getOwnerName(),
						buyer.getName(),
						System.currentTimeMillis(),
						transactionItem,
						e.getSaleType(),
						price
				);
				AuctionHouse.getDataManager().insertTransaction(auctionTransaction, (error, transaction) -> {
					if (error != null) {
						AuctionHouse.getInstance().getLogger().severe("Failed to insert transaction for auction " + auctionedItem.getId() + ": " + error.getMessage());
						error.printStackTrace();
					} else if (transaction != null) {
						AuctionHouse.getTransactionManager().addTransaction(transaction);
					} else {
						AuctionHouse.getInstance().getLogger().warning("Transaction insert succeeded but fetch returned null for auction " + auctionedItem.getId() + ". Using original transaction object as fallback.");
						// Fallback: use the original transaction object since insert was successful
						AuctionHouse.getTransactionManager().addTransaction(auctionTransaction);
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
		// Log auction bid with balance changes
		if (AuctionHouse.getTransactionLogger() != null && !e.isCancelled()) {
			final AuctionedItem auctionedItem = e.getAuctionedItem();
			String itemName = auctionedItem.getItem().hasItemMeta() && auctionedItem.getItem().getItemMeta().hasDisplayName()
				? auctionedItem.getItem().getItemMeta().getDisplayName()
				: auctionedItem.getItem().getType().name();
			double previousBid = auctionedItem.getHighestBidder().equals(auctionedItem.getOwner()) ? 0 : auctionedItem.getCurrentPrice() - (e.getNewBidAmount() - auctionedItem.getCurrentPrice());
			
			// Get balances for logging
			String[] currencyParts = auctionedItem.getCurrency().split("/");
			String currencyPlugin = currencyParts.length > 0 ? currencyParts[0] : "Vault";
			String currencyName = currencyParts.length > 1 ? currencyParts[1] : "Vault";
			
			double bidderOldBalance = AuctionHouse.getCurrencyManager().getBalance(e.getBidder(), currencyPlugin, currencyName);
			double bidderNewBalance = bidderOldBalance - e.getNewBidAmount();
			
			Double previousBidderOldBalance = null;
			Double previousBidderNewBalance = null;
			
			// If there was a previous bidder, get their balance info
			if (!auctionedItem.getHighestBidder().equals(auctionedItem.getOwner()) && Settings.BIDDING_TAKES_MONEY.getBoolean()) {
				OfflinePlayer previousBidder = Bukkit.getOfflinePlayer(auctionedItem.getHighestBidder());
				if (previousBidder != null) {
					previousBidderOldBalance = AuctionHouse.getCurrencyManager().getBalance(previousBidder, currencyPlugin, currencyName);
					previousBidderNewBalance = previousBidderOldBalance + auctionedItem.getCurrentPrice(); // They get refunded
				}
			}
			
			AuctionHouse.getTransactionLogger().logAuctionBid(
				e.getBidder().getName() != null ? e.getBidder().getName() : "Unknown",
				auctionedItem.getOwnerName() != null ? auctionedItem.getOwnerName() : "Unknown",
				itemName,
				e.getNewBidAmount(),
				previousBid,
				auctionedItem.getCurrency(),
				auctionedItem.getId().toString(),
				bidderOldBalance,
				bidderNewBalance,
				previousBidderOldBalance,
				previousBidderNewBalance
			);
		}

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

		// Log admin action to transaction logger
		if (AuctionHouse.getTransactionLogger() != null && !event.isCancelled()) {
			String itemName = event.getAuctionAdminLog().getItem().hasItemMeta() && event.getAuctionAdminLog().getItem().getItemMeta().hasDisplayName()
				? event.getAuctionAdminLog().getItem().getItemMeta().getDisplayName()
				: event.getAuctionAdminLog().getItem().getType().name();
			AuctionHouse.getTransactionLogger().logAdminAction(
				event.getAuctionAdminLog().getAdminName(),
				event.getAuctionAdminLog().getTargetName(),
				itemName,
				event.getAuctionAdminLog().getAdminAction().name(),
				event.getAuctionAdminLog().getItemId().toString()
			);
		}
	}
}
