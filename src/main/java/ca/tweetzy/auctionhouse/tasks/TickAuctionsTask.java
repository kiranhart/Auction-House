package ca.tweetzy.auctionhouse.tasks;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.settings.Settings;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.stream.Collectors;

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
        try {
            // check if the auction stack even has items
            if (AuctionHouse.getInstance().getAuctionItemManager().getAuctionItems().size() == 0) {
                return;
            }

            // tick all the auction items
            AuctionHouse.getInstance().getAuctionItemManager().getAuctionItems().forEach(item -> {
                if (!item.isExpired()) {
                    item.updateRemainingTime(Settings.TICK_UPDATE_TIME.getInt());
                }
            });

            // filter items where the time is less than or equal to 0
            AuctionHouse.getInstance().getAuctionItemManager().getAuctionItems().stream().filter(item -> item.getRemainingTime() <= 0).collect(Collectors.toList()).iterator().forEachRemaining(AuctionAPI.getInstance()::endAuction);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
