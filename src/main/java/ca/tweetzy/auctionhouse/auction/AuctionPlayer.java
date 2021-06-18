package ca.tweetzy.auctionhouse.auction;

import ca.tweetzy.auctionhouse.AuctionHouse;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
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

    public AuctionPlayer(Player player) {
        this.player = player;
    }

    public List<AuctionItem> getItems(boolean getExpired) {
        List<AuctionItem> auctionItems = AuctionHouse.getInstance().getAuctionItemManager().getAuctionItems();
        List<AuctionItem> items = new ArrayList<>();

        synchronized (auctionItems) {
            Iterator<AuctionItem> iterator = auctionItems.iterator();
            while(iterator.hasNext()) {
                AuctionItem item = iterator.next();
                if (item.getOwner().equals(this.player.getUniqueId()) && item.isExpired() == getExpired) {
                    items.add(item);
                }
            }
        }

//        return Collections.unmodifiableList(AuctionHouse.getInstance().getAuctionItemManager().getAuctionItems().stream().filter(item -> item.getOwner().equals(this.player.getUniqueId()) && item.isExpired() == getExpired).collect(Collectors.toList()));
        return items;
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
}
