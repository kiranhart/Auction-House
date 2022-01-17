package ca.tweetzy.auctionhouse.listeners;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.api.events.AuctionBidEvent;
import ca.tweetzy.auctionhouse.api.events.AuctionEndEvent;
import ca.tweetzy.auctionhouse.api.events.AuctionStartEvent;
import ca.tweetzy.auctionhouse.auction.enums.AuctionSaleType;
import ca.tweetzy.auctionhouse.auction.AuctionStat;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.auctionhouse.transaction.Transaction;
import org.bukkit.Bukkit;
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
		AuctionHouse.getInstance().getAuctionStatManager().insertOrUpdate(e.getSeller(), new AuctionStat<>(
				1, 0, 0, 0D, 0D
		));

		if (Settings.DISCORD_ENABLED.getBoolean() && Settings.DISCORD_ALERT_ON_AUCTION_START.getBoolean()) {
			Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(AuctionHouse.getInstance(), () -> {
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
			}, 1L);
		}
	}

	@EventHandler
	public void onAuctionEnd(AuctionEndEvent e) {
		AuctionHouse.getInstance().getAuctionStatManager().insertOrUpdate(e.getOriginalOwner(), new AuctionStat<>(
				0,
				1,
				0,
				e.getSaleType() == AuctionSaleType.USED_BIDDING_SYSTEM ? e.getAuctionItem().getCurrentPrice() : e.getAuctionItem().getBasePrice(),
				0D
		));

		AuctionHouse.getInstance().getAuctionStatManager().insertOrUpdate(e.getBuyer(), new AuctionStat<>(
				0,
				0,
				0,
				0D,
				e.getSaleType() == AuctionSaleType.USED_BIDDING_SYSTEM ? e.getAuctionItem().getCurrentPrice() : e.getAuctionItem().getBasePrice()
		));

		Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(AuctionHouse.getInstance(), () -> {
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
		}, 1L);
	}

	@EventHandler
	public void onAuctionBid(AuctionBidEvent e) {
		if (!Settings.DISCORD_ENABLED.getBoolean() && Settings.DISCORD_ALERT_ON_AUCTION_BID.getBoolean()) return;
		Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(AuctionHouse.getInstance(), () -> Settings.DISCORD_WEBHOOKS.getStringList().forEach(hook -> {
			AuctionAPI.getInstance().sendDiscordBidMessage(hook, e.getAuctionedItem(), e.getNewBidAmount());
		}), 1L);
	}
}
