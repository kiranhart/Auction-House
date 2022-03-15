package ca.tweetzy.auctionhouse.managers;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.auction.AuctionedItem;
import lombok.Getter;
import lombok.NonNull;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The current file has been created by Kiran Hart
 * Date Created: February 02 2021
 * Time Created: 6:27 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */

public class AuctionItemManager {

	/*
	 * If not using usingDynamicLoad, items will be loaded into this map during initialization or when a new item is added
	 */
	@Getter
	private final ConcurrentHashMap<UUID, AuctionedItem> items = new ConcurrentHashMap<>();

	@Getter
	private final ConcurrentHashMap<AuctionedItem, Long> expiredItems = new ConcurrentHashMap<>();

	/**
	 * Items that are in the garbage bin are essentially marked for disposal
	 */
	@Getter
	private final ConcurrentHashMap<UUID, AuctionedItem> garbageBin = new ConcurrentHashMap<>();

	public void start() {
		AuctionHouse.getInstance().getDataManager().getItems((error, results) -> {
			if (error == null) {
				for (AuctionedItem item : results) {
					addAuctionItem(item);
				}
			}
		});
	}

	public void end() {
		AuctionHouse.getInstance().getDataManager().updateItems(this.items.values(), null);
	}

	public void addAuctionItem(@NonNull AuctionedItem auctionedItem) {
		this.items.put(auctionedItem.getId(), auctionedItem);
	}

	public void sendToGarbage(@NonNull AuctionedItem auctionedItem) {
		this.garbageBin.put(auctionedItem.getId(), auctionedItem);
	}

	public void sendToGarbage(@NonNull UUID uuid) {
		this.garbageBin.put(uuid, new AuctionedItem());
	}

	public AuctionedItem getItem(@NonNull UUID id) {
		return this.items.getOrDefault(id, null);
	}
}
