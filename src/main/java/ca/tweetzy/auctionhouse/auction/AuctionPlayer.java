package ca.tweetzy.auctionhouse.auction;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.settings.Settings;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

    public AuctionPlayer(Player player) {
        this.player = player;
        resetFilter();
    }

    public List<AuctionItem> getItems(boolean getExpired) {
        return AuctionHouse.getInstance().getAuctionItemManager().getAuctionItems().stream().filter(item -> item.getOwner().equals(this.player.getUniqueId()) && !AuctionHouse.getInstance().getAuctionItemManager().getGarbageBin().contains(item) && item.isExpired() == getExpired).collect(Collectors.toList());
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

    public int getAllowedSellTime() {
        List<Integer> possibleTimes = new ArrayList<>();
        Settings.AUCTION_TIME.getStringList().forEach(line -> {
            String[] split = line.split(":");
            if (player.hasPermission("auctionhouse.time." + split[0])) {
                possibleTimes.add(Integer.parseInt(split[1]));
            }
        });

        return possibleTimes.size() <= 0 ? Settings.DEFAULT_AUCTION_TIME.getInt() : Math.max(Settings.DEFAULT_AUCTION_TIME.getInt(), Collections.max(possibleTimes));
    }
}
