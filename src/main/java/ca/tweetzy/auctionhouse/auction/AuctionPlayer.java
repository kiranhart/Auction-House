package ca.tweetzy.auctionhouse.auction;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.settings.Settings;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * The current file has been created by Kiran Hart
 * Date Created: February 02 2021
 * Time Created: 6:26 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */

@Getter
@Setter
public class AuctionPlayer {

	private final Player player;

	private AuctionSaleType selectedSaleType;
	private AuctionItemCategory selectedFilter;
	private AuctionSortType auctionSortType;
	private String currentSearchPhrase;
	private boolean showListingInfo = true;
	private long lastListedItem = -1;

	public AuctionPlayer(Player player) {
		this.player = player;
		resetFilter();
	}


	public boolean canListItem() {
		if (Settings.LIST_ITEM_DELAY.getInt() == -1) {
			return true;
		}

		if (this.lastListedItem == -1 || System.currentTimeMillis() >= this.lastListedItem) {
			this.lastListedItem = System.currentTimeMillis() + 1000L * Settings.LIST_ITEM_DELAY.getInt();
			return true;
		}

		AuctionHouse.getInstance().getLocale().getMessage("general.wait_to_list").processPlaceholder("time", (this.lastListedItem - System.currentTimeMillis()) / 1000).sendPrefixedMessage(this.player);

		return false;
	}

	public List<AuctionedItem> getItems(boolean getExpired) {
		List<AuctionedItem> items = new ArrayList<>();

		for (Map.Entry<UUID, AuctionedItem> entry : AuctionHouse.getInstance().getAuctionItemManager().getItems().entrySet()) {
			AuctionedItem auctionItem = entry.getValue();
			if (auctionItem.getOwner().equals(this.player.getUniqueId()) && auctionItem.isExpired() == getExpired && !AuctionHouse.getInstance().getAuctionItemManager().getGarbageBin().containsKey(auctionItem.getId())) {
				items.add(auctionItem);
			}
		}
		return items;
	}

	public void resetFilter() {
		this.selectedFilter = AuctionItemCategory.ALL;
		this.auctionSortType = AuctionSortType.RECENT;
		this.selectedSaleType = AuctionSaleType.BOTH;
		this.currentSearchPhrase = "";
	}


	public int getSellLimit() {
		if (player.hasPermission("auctionhouse.maxsell.*")) return Integer.MAX_VALUE;
		for (int i = 1001; i > 0; i--) {
			if (player.hasPermission("auctionhouse.maxsell." + i)) return i;
		}
		return 0;
	}

	public boolean isAtSellLimit() {
		return getSellLimit() - 1 < getItems(false).size();
	}

	public int getAllowedSellTime(AuctionSaleType auctionSaleType) {
		List<Integer> possibleTimes = new ArrayList<>();
		Settings.AUCTION_TIME.getStringList().forEach(line -> {
			String[] split = line.split(":");
			if (player.hasPermission("auctionhouse.time." + split[0])) {
				possibleTimes.add(Integer.parseInt(split[1]));
			}
		});

		int defaultTime = auctionSaleType == AuctionSaleType.USED_BIDDING_SYSTEM ? Settings.DEFAULT_AUCTION_LISTING_TIME.getInt() : Settings.DEFAULT_BIN_LISTING_TIME.getInt();

		return possibleTimes.size() <= 0 ? defaultTime : Math.max(defaultTime, Collections.max(possibleTimes));
	}
}
