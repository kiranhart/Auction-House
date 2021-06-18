package ca.tweetzy.auctionhouse.tasks;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.auction.AuctionItem;
import ca.tweetzy.auctionhouse.settings.Settings;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
import java.util.List;

/**
 * The current file has been created by Kiran Hart
 * Date Created: February 18 2021
 * Time Created: 8:47 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class TickAuctionsTask extends BukkitRunnable {

    private static TickAuctionsTask instance;

    public static TickAuctionsTask startTask() {
        if (instance == null) {
            instance = new TickAuctionsTask();
            // maybe to async
            instance.runTaskTimerAsynchronously(AuctionHouse.getInstance(), 0, (long) 20 * Settings.TICK_UPDATE_TIME.getInt());
        }
        return instance;
    }

    @Override
    public void run() {

        // check if the auction stack even has items
        List<AuctionItem> auctionItems = AuctionHouse.getInstance().getAuctionItemManager().getAuctionItems();

        synchronized (auctionItems) {
            if (auctionItems.size() == 0) {
                return;
            }

            Iterator<AuctionItem> iterator = auctionItems.iterator();
            while (iterator.hasNext()) {
                AuctionItem item = iterator.next();
                if (!item.isExpired()) {
                    item.updateRemainingTime(Settings.TICK_UPDATE_TIME.getInt());
                }

                if (item.getRemainingTime() <= 0) {
                    AuctionAPI.getInstance().endAuction(item);
                }
            }
        }

    }
}
