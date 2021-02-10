package ca.tweetzy.auctionhouse.managers;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.auction.AuctionItem;
import ca.tweetzy.auctionhouse.auction.AuctionItemCategory;
import ca.tweetzy.auctionhouse.helpers.MaterialCategorizer;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<AuctionItem> getFilteredItems(AuctionItemCategory category) {
        switch(category){
            case BLOCKS:
                return auctionItems.stream().filter(auctionItem -> MaterialCategorizer.getMaterialCategory(auctionItem.getOriginalItem()) == AuctionItemCategory.BLOCKS).collect(Collectors.toList());
            case ARMOR:
                return auctionItems.stream().filter(auctionItem -> MaterialCategorizer.getMaterialCategory(auctionItem.getOriginalItem()) == AuctionItemCategory.ARMOR).collect(Collectors.toList());
            case FOOD:
                return auctionItems.stream().filter(auctionItem -> MaterialCategorizer.getMaterialCategory(auctionItem.getOriginalItem()) == AuctionItemCategory.FOOD).collect(Collectors.toList());
            case TOOLS:
                return auctionItems.stream().filter(auctionItem -> MaterialCategorizer.getMaterialCategory(auctionItem.getOriginalItem()) == AuctionItemCategory.TOOLS).collect(Collectors.toList());
            case MISC:
                return auctionItems.stream().filter(auctionItem -> MaterialCategorizer.getMaterialCategory(auctionItem.getOriginalItem()) == AuctionItemCategory.MISC).collect(Collectors.toList());
        }
        return auctionItems;
    }

    public void addItem(AuctionItem item) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(AuctionHouse.getInstance(), () -> {
            auctionItems.add(item);
        });
    }
}
