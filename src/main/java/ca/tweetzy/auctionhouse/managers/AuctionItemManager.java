package ca.tweetzy.auctionhouse.managers;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.auction.AuctionItem;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.LinkedList;

/**
 * The current file has been created by Kiran Hart
 * Date Created: February 02 2021
 * Time Created: 6:27 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
@Getter
public class AuctionItemManager {

    private final LinkedList<AuctionItem> auctionItems = new LinkedList<>();

    public void loadItems() {

    }

    public void addItem(AuctionItem item) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(AuctionHouse.getInstance(), () -> {
            auctionItems.add(item);
        });
    }
}
