package ca.tweetzy.auctionhouse.managers;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.auction.AuctionItem;
import ca.tweetzy.auctionhouse.auction.AuctionItemCategory;
import ca.tweetzy.auctionhouse.helpers.MaterialCategorizer;
import ca.tweetzy.core.utils.TextUtils;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: February 02 2021
 * Time Created: 6:27 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */


public class AuctionItemManager {

    private final ArrayList<AuctionItem> auctionItems = new ArrayList<>();

    public void addItem(AuctionItem auctionItem) {
        if (auctionItem == null) return;
        this.auctionItems.add(auctionItem);
    }

    public void removeItem(UUID uuid) {
        this.auctionItems.removeIf(item -> item.getKey().equals(uuid));
    }

    public AuctionItem getItem(UUID uuid) {
        return this.auctionItems.stream().filter(item -> item.getKey().equals(uuid)).findFirst().orElse(null);
    }

    public List<AuctionItem> getAuctionItems() {
        return Collections.unmodifiableList(auctionItems);
    }

    public List<AuctionItem> getFilteredItems(AuctionItemCategory category) {
        if (category == null) {
            return Collections.unmodifiableList(this.auctionItems);
        }
        return Collections.unmodifiableList(auctionItems.stream().filter(auctionItem -> MaterialCategorizer.getMaterialCategory(AuctionAPI.deserializeItem(auctionItem.getRawItem())) == category).collect(Collectors.toList()));
    }

    public void loadItems() {
        if (AuctionHouse.getInstance().getData().contains("auction items") && AuctionHouse.getInstance().getData().isList("auction items")) {
            List<AuctionItem> items = AuctionHouse.getInstance().getData().getStringList("auction items").stream().map(AuctionAPI.getInstance()::convertBase64ToObject).map(object -> (AuctionItem) object).collect(Collectors.toList());
            items.forEach(item -> {
                this.addItem(item);
                Bukkit.getConsoleSender().sendMessage(TextUtils.formatText(String.format("&aLoaded Item&f: &e%s &f(&a$%s&f) (&a$%.2f&f) (&a$%.2f&f) (&a$%.2f&f)", item.getKey().toString(), item.getBasePrice(), item.getBidStartPrice(), item.getBidIncPrice(), item.getCurrentPrice())));
            });
            AuctionHouse.getInstance().getData().set("auction items", null);
            AuctionHouse.getInstance().getData().save();
        }
    }

    public void saveItems() {
        this.adjustItemsInFile(this.getAuctionItems());
    }

    public void adjustItemsInFile(List<AuctionItem> items) {
        if (!AuctionHouse.getInstance().getData().contains("auction items")) {
            AuctionHouse.getInstance().getData().set("auction items", items.stream().map(AuctionAPI.getInstance()::convertToBase64).collect(Collectors.toList()));
            AuctionHouse.getInstance().getData().save();
            return;
        }

        List<AuctionItem> foundItems = AuctionHouse.getInstance().getData().getStringList("auction items").stream().map(AuctionAPI.getInstance()::convertBase64ToObject).map(object -> (AuctionItem) object).collect(Collectors.toList());
        foundItems.addAll(items);
        AuctionHouse.getInstance().getData().set("auction items", foundItems);
        AuctionHouse.getInstance().getData().save();
    }

    public void adjustItemsInFile(AuctionItem item, boolean add) {
        if (!AuctionHouse.getInstance().getData().contains("auction items") && add) {
            AuctionHouse.getInstance().getData().set("auction items", Collections.singletonList(AuctionAPI.getInstance().convertToBase64(item)));
            AuctionHouse.getInstance().getData().save();
            return;
        }

        List<AuctionItem> items = AuctionHouse.getInstance().getData().getStringList("auction items").stream().map(AuctionAPI.getInstance()::convertBase64ToObject).map(object -> (AuctionItem) object).collect(Collectors.toList());
        if (items.stream().anyMatch(i -> i.getKey().equals(item.getKey())) && !add) {
            items.removeIf(i -> i.getKey().equals(item.getKey()));
        } else {
            items.add(item);
        }

        AuctionHouse.getInstance().getData().set("auction items", items.stream().map(AuctionAPI.getInstance()::convertToBase64).collect(Collectors.toList()));
        AuctionHouse.getInstance().getData().save();
    }
}
