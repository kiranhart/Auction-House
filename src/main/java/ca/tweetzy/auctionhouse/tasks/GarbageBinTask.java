package ca.tweetzy.auctionhouse.tasks;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.auction.AuctionedItem;
import ca.tweetzy.auctionhouse.settings.Settings;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.stream.Collectors;

/**
 * Date Created: April 18 2022
 * Time Created: 1:35 p.m.
 *
 * @author Kiran Hart
 */
public final class GarbageBinTask extends BukkitRunnable {

	private static GarbageBinTask instance;

	public static GarbageBinTask startTask() {
		if (instance == null) {
			instance = new GarbageBinTask();
			instance.runTaskTimerAsynchronously(AuctionHouse.getInstance(), 20 * 5, (long) 20 * (Settings.GARBAGE_DELETION_TIMED_MODE.getBoolean() ? Settings.GARBAGE_DELETION_TIMED_DELAY.getInt() : 1));
		}
		return instance;
	}

	@Override
	public void run() {
		if (!AuctionHouse.getInstance().getAuctionItemManager().getGarbageBin().keySet().isEmpty()) {

			// timed mode
			if (Settings.GARBAGE_DELETION_TIMED_MODE.getBoolean()) {
				AuctionHouse.getInstance().getDataManager().deleteItems(AuctionHouse.getInstance().getAuctionItemManager().getGarbageBin().values().stream().map(AuctionedItem::getId).collect(Collectors.toList()));
				AuctionHouse.getInstance().getAuctionItemManager().getGarbageBin().clear();
				return;
			}

			// item mode
			if (AuctionHouse.getInstance().getAuctionItemManager().getGarbageBin().size() >= Settings.GARBAGE_DELETION_MAX_ITEMS.getInt()) {
				AuctionHouse.getInstance().getDataManager().deleteItems(AuctionHouse.getInstance().getAuctionItemManager().getGarbageBin().values().stream().map(AuctionedItem::getId).collect(Collectors.toList()));
				AuctionHouse.getInstance().getAuctionItemManager().getGarbageBin().clear();
			}
		}
	}
}
