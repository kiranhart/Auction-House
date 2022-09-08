package ca.tweetzy.auctionhouse.listeners;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.api.events.AuctionAdminEvent;
import ca.tweetzy.auctionhouse.api.events.AuctionBidEvent;
import ca.tweetzy.auctionhouse.api.events.AuctionEndEvent;
import ca.tweetzy.auctionhouse.api.events.AuctionStartEvent;
import ca.tweetzy.auctionhouse.auction.AuctionStatistic;
import ca.tweetzy.auctionhouse.auction.enums.AuctionSaleType;
import ca.tweetzy.auctionhouse.auction.enums.AuctionStatisticType;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.auctionhouse.transaction.Transaction;
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
		new AuctionStatistic(e.getSeller().getUniqueId(), e.getAuctionItem().isBidItem() ? AuctionStatisticType.CREATED_AUCTION : AuctionStatisticType.CREATED_BIN, 1).store(null);

		if (Settings.DISCORD_ENABLED.getBoolean() && Settings.DISCORD_ALERT_ON_AUCTION_START.getBoolean()) {
			AuctionHouse.newChain().async(() -> {
				Settings.DISCORD_WEBHOOKS.getStringList().forEach(hook -> {
					AuctionAPI.getInstance().sendDiscordMessage(
							hook,
							e.getSeller(),
							e.getSeller(),
							e.getAuctionItem(),
							AuctionSaleType.USED_BIDDING_SYSTEM,
							true,
							e.getAuctionItem().isBidItem()
					);
				});
			}).execute();
		}
	}

	@EventHandler
	public void onAuctionEnd(AuctionEndEvent e) {
		// new stat system
		new AuctionStatistic(e.getOriginalOwner().getUniqueId(), e.getAuctionItem().isBidItem() ? AuctionStatisticType.SOLD_AUCTION : AuctionStatisticType.SOLD_BIN, 1).store(null);
		new AuctionStatistic(e.getOriginalOwner().getUniqueId(), AuctionStatisticType.MONEY_EARNED, e.getSaleType() == AuctionSaleType.USED_BIDDING_SYSTEM ? e.getAuctionItem().getCurrentPrice() : e.getAuctionItem().getBasePrice()).store(null);
		new AuctionStatistic(e.getBuyer().getUniqueId(), AuctionStatisticType.MONEY_SPENT, e.getSaleType() == AuctionSaleType.USED_BIDDING_SYSTEM ? e.getAuctionItem().getCurrentPrice() : e.getAuctionItem().getBasePrice()).store(null);

		AuctionHouse.newChain().async(() -> {
			if (Settings.RECORD_TRANSACTIONS.getBoolean()) {

				AuctionHouse.getInstance().getDataManager().insertTransactionAsync(new Transaction(
						UUID.randomUUID(),
						e.getOriginalOwner().getUniqueId(),
						e.getBuyer().getUniqueId(),
						e.getAuctionItem().getOwnerName(),
						e.getBuyer().getName(),
						System.currentTimeMillis(),
						e.getAuctionItem().getItem(),
						e.getSaleType(),
						e.getAuctionItem().getCurrentPrice()
				), (error, transaction) -> {
					if (error == null) {
						AuctionHouse.getInstance().getTransactionManager().addTransaction(transaction);
					}
				});

			}

			if (Settings.DISCORD_ENABLED.getBoolean() && Settings.DISCORD_ALERT_ON_AUCTION_FINISH.getBoolean()) {
				Settings.DISCORD_WEBHOOKS.getStringList().forEach(hook -> AuctionAPI.getInstance().sendDiscordMessage(hook, e.getOriginalOwner(), e.getBuyer(), e.getAuctionItem(), e.getSaleType(), false, e.getSaleType() == AuctionSaleType.USED_BIDDING_SYSTEM));
			}
		}).execute();
	}

	@EventHandler
	public void onAuctionBid(AuctionBidEvent e) {
		if (!Settings.DISCORD_ENABLED.getBoolean() && Settings.DISCORD_ALERT_ON_AUCTION_BID.getBoolean()) return;
		AuctionHouse.newChain().async(() -> {
			Settings.DISCORD_WEBHOOKS.getStringList().forEach(hook -> AuctionAPI.getInstance().sendDiscordBidMessage(hook, e.getAuctionedItem(), e.getNewBidAmount()));
		}).execute();
	}

	@EventHandler
	public void onAdminAction(AuctionAdminEvent event) {
		if (!Settings.LOG_ADMIN_ACTIONS.getBoolean()) return;
		AuctionHouse.getInstance().getDataManager().insertLogAsync(event.getAuctionAdminLog());
	}
}
