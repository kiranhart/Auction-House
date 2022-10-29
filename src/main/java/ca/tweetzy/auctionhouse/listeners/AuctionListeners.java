package ca.tweetzy.auctionhouse.listeners;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.api.events.AuctionAdminEvent;
import ca.tweetzy.auctionhouse.api.events.AuctionBidEvent;
import ca.tweetzy.auctionhouse.api.events.AuctionEndEvent;
import ca.tweetzy.auctionhouse.api.events.AuctionStartEvent;
import ca.tweetzy.auctionhouse.auction.AuctionStatistic;
import ca.tweetzy.auctionhouse.auction.AuctionedItem;
import ca.tweetzy.auctionhouse.auction.enums.AuctionSaleType;
import ca.tweetzy.auctionhouse.auction.enums.AuctionStatisticType;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.auctionhouse.transaction.Transaction;
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
		new AuctionStatistic(seller.getUniqueId(), auctionedItem.isBidItem() ? AuctionStatisticType.CREATED_AUCTION : AuctionStatisticType.CREATED_BIN, 1).store(null);

		if (Settings.DISCORD_ENABLED.getBoolean() && Settings.DISCORD_ALERT_ON_AUCTION_START.getBoolean()) {
			AuctionHouse.newChain().async(() -> {
				final AuctionAPI instance = AuctionAPI.getInstance();
				Settings.DISCORD_WEBHOOKS.getStringList().forEach(hook -> {
					instance.sendDiscordMessage(
							hook,
							seller,
							seller,
							auctionedItem,
							AuctionSaleType.USED_BIDDING_SYSTEM,
							true,
							auctionedItem.isBidItem()
					);
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
		new AuctionStatistic(originalOwnerUUID, auctionedItem.isBidItem() ? AuctionStatisticType.SOLD_AUCTION : AuctionStatisticType.SOLD_BIN, 1).store(null);
		new AuctionStatistic(originalOwnerUUID, AuctionStatisticType.MONEY_EARNED, e.getSaleType() == AuctionSaleType.USED_BIDDING_SYSTEM ? auctionedItem.getCurrentPrice() : auctionedItem.getBasePrice()).store(null);
		new AuctionStatistic(buyerUUID, AuctionStatisticType.MONEY_SPENT, e.getSaleType() == AuctionSaleType.USED_BIDDING_SYSTEM ? auctionedItem.getCurrentPrice() : auctionedItem.getBasePrice()).store(null);

		AuctionHouse.newChain().async(() -> {
			if (Settings.RECORD_TRANSACTIONS.getBoolean()) {
				final AuctionHouse instance = AuctionHouse.getInstance();
				instance.getDataManager().insertTransactionAsync(new Transaction(
						UUID.randomUUID(),
						originalOwnerUUID,
						buyerUUID,
						auctionedItem.getOwnerName(),
						buyer.getName(),
						System.currentTimeMillis(),
						auctionedItem.getItem(),
						e.getSaleType(),
						auctionedItem.getCurrentPrice()
				), (error, transaction) -> {
					if (error == null) {
						instance.getTransactionManager().addTransaction(transaction);
					}
				});
			}

			if (Settings.DISCORD_ENABLED.getBoolean() && Settings.DISCORD_ALERT_ON_AUCTION_FINISH.getBoolean()) {
				final AuctionAPI instance = AuctionAPI.getInstance();
				Settings.DISCORD_WEBHOOKS.getStringList().forEach(hook -> instance.sendDiscordMessage(hook, originalOwner, buyer, auctionedItem, e.getSaleType(), false, e.getSaleType() == AuctionSaleType.USED_BIDDING_SYSTEM));
			}
		}).execute();
	}

	@EventHandler
	public void onAuctionBid(AuctionBidEvent e) {
		if (!Settings.DISCORD_ENABLED.getBoolean() && Settings.DISCORD_ALERT_ON_AUCTION_BID.getBoolean()) return;
		AuctionHouse.newChain().async(() -> {
			final AuctionAPI instance = AuctionAPI.getInstance();
			final AuctionedItem auctionedItem = e.getAuctionedItem();
			Settings.DISCORD_WEBHOOKS.getStringList().forEach(hook -> instance.sendDiscordBidMessage(hook, auctionedItem, e.getNewBidAmount()));
		}).execute();
	}

	@EventHandler
	public void onAdminAction(AuctionAdminEvent event) {
		if (!Settings.LOG_ADMIN_ACTIONS.getBoolean()) return;
		AuctionHouse.getInstance().getDataManager().insertLogAsync(event.getAuctionAdminLog());
	}
}
