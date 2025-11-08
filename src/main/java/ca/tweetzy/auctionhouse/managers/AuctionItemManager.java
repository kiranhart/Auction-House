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

package ca.tweetzy.auctionhouse.managers;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.auction.AuctionedItem;
import ca.tweetzy.auctionhouse.settings.Settings;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: February 02 2021
 * Time Created: 6:27 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */

public class AuctionItemManager {

	@Getter
	private final ConcurrentHashMap<UUID, AuctionedItem> items = new ConcurrentHashMap<>();

	/**
	 * Items that are in the garbage bin are essentially marked for disposal
	 */
	@Getter
	private final ConcurrentHashMap<UUID, AuctionedItem> garbageBin = new ConcurrentHashMap<>();

	@Getter
	private final ConcurrentHashMap<UUID, AuctionedItem> deletedItems = new ConcurrentHashMap<>();

	public List<AuctionedItem> getHighestBidItems(Player player) {
		return getItems().values().stream().filter(item -> item.isBidItem() && !item.isExpired() && !item.getOwner().equals(player.getUniqueId()) && item.getHighestBidder().equals(player.getUniqueId())).collect(Collectors.toList());
	}


	public void start() {
		AuctionHouse.getDataManager().getItems((error, results) -> {
			if (error == null) {
				for (AuctionedItem item : results) {
					addAuctionItem(item);
				}

			} else {
				error.printStackTrace();
			}
		});
	}

	public void end() {
		AuctionHouse.getDataManager().updateItems(this.items.values(), null);
	}

	public void addAuctionItem(@NonNull AuctionedItem auctionedItem) {
		this.items.put(auctionedItem.getId(), auctionedItem);
	}

	public void sendToGarbage(@NonNull AuctionedItem auctionedItem) {
		this.garbageBin.put(auctionedItem.getId(), auctionedItem);
	}

	public AuctionedItem getItem(@NonNull UUID id) {
		return this.items.getOrDefault(id, null);
	}

	public List<AuctionedItem> getValidItems(Player player) {
		final List<AuctionedItem> itemList = new ArrayList<>();

		for (Map.Entry<UUID, AuctionedItem> entry : AuctionHouse.getAuctionItemManager().getItems().entrySet()) {
			AuctionedItem auctionItem = entry.getValue();
			if (!auctionItem.isExpired() && !AuctionHouse.getAuctionItemManager().getGarbageBin().containsKey(auctionItem.getId())) {
				if (Settings.PER_WORLD_ITEMS.getBoolean()) {
					if (auctionItem.getListedWorld() == null || player.getWorld().getName().equals(auctionItem.getListedWorld())) {
						itemList.add(auctionItem);
					}
				} else {
					itemList.add(auctionItem);
				}
			}
		}

		return itemList;
	}
}
