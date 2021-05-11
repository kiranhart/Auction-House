package ca.tweetzy.auctionhouse.listeners;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.api.events.AuctionEndEvent;
import ca.tweetzy.auctionhouse.api.events.AuctionStartEvent;
import ca.tweetzy.auctionhouse.auction.AuctionSaleType;
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
        if (Settings.DISCORD_ENABLED.getBoolean() && Settings.DISCORD_ALERT_ON_AUCTION_START.getBoolean()) {
            Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(AuctionHouse.getInstance(), () -> Settings.DISCORD_WEBHOOKS.getStringList().forEach(hook -> AuctionAPI.getInstance().sendDiscordMessage(hook, e.getSeller(), e.getSeller(), e.getAuctionItem(), AuctionSaleType.USED_BIDDING_SYSTEM, true, e.getAuctionItem().getBidStartPrice() >= Settings.MIN_AUCTION_START_PRICE.getDouble())), 1L);
        }
    }

    @EventHandler
    public void onAuctionEnd(AuctionEndEvent e) {
        Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(AuctionHouse.getInstance(), () -> {
            if (Settings.RECORD_TRANSACTIONS.getBoolean()) {
                AuctionHouse.getInstance().getTransactionManager().addTransaction(new Transaction(
                        UUID.randomUUID(),
                        e.getOriginalOwner().getUniqueId(),
                        e.getBuyer().getUniqueId(),
                        System.currentTimeMillis(),
                        e.getAuctionItem(),
                        e.getSaleType()
                ));
            }
            if (Settings.DISCORD_ENABLED.getBoolean() && Settings.DISCORD_ALERT_ON_AUCTION_FINISH.getBoolean()) {
                Settings.DISCORD_WEBHOOKS.getStringList().forEach(hook -> AuctionAPI.getInstance().sendDiscordMessage(hook, e.getOriginalOwner(), e.getBuyer(), e.getAuctionItem(), e.getSaleType(), false, e.getSaleType() == AuctionSaleType.USED_BIDDING_SYSTEM));
            }
        }, 1L);
    }
}
