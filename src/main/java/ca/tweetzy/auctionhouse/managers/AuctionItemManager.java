package ca.tweetzy.auctionhouse.managers;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.auction.AuctionItem;
import ca.tweetzy.auctionhouse.auction.AuctionItemCategory;
import ca.tweetzy.auctionhouse.helpers.MaterialCategorizer;
import ca.tweetzy.core.utils.TextUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.LinkedList;
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

    @Getter
    private final LinkedList<AuctionItem> auctionItems = new LinkedList<>();

    public void loadItems() {
        ConfigurationSection section = AuctionHouse.getInstance().getData().getConfigurationSection("auction items");
        if (section == null || section.getKeys(false).size() == 0) return;
        for (String nodes : section.getKeys(false)) {

            String key = AuctionHouse.getInstance().getData().getString("auction items." + nodes + ".key");
            double basePrice = AuctionHouse.getInstance().getData().getDouble("auction items." + nodes + ".base price");
            double bidStartPrice = AuctionHouse.getInstance().getData().getDouble("auction items." + nodes + ".bid start price");
            double bidIncrementPrice = AuctionHouse.getInstance().getData().getDouble("auction items." + nodes + ".bid increment price");
            double currentPrice = AuctionHouse.getInstance().getData().getDouble("auction items." + nodes + ".current price");

            auctionItems.add(new AuctionItem(
                    UUID.fromString(AuctionHouse.getInstance().getData().getString("auction items." + nodes + ".owner")),
                    UUID.fromString(AuctionHouse.getInstance().getData().getString("auction items." + nodes + ".highest bidder")),
                    AuctionHouse.getInstance().getData().getItemStack("auction items." + nodes + ".item"),
                    AuctionItemCategory.valueOf(AuctionHouse.getInstance().getData().getString("auction items." + nodes + ".category")),
                    UUID.fromString(key),
                    basePrice,
                    bidStartPrice,
                    bidIncrementPrice,
                    currentPrice,
                    AuctionHouse.getInstance().getData().getInt("auction items." + nodes + ".remaining time")
            ));

            Bukkit.getConsoleSender().sendMessage(TextUtils.formatText(String.format("&aLoaded Item&f: &e%s &f(&a$%s&f) (&a$%.2f&f) (&a$%.2f&f) (&a$%.2f&f)", key, basePrice, bidStartPrice, bidIncrementPrice, currentPrice)));
        }

        AuctionHouse.getInstance().getData().set("auction items", null);
        AuctionHouse.getInstance().getData().save();
    }

    public void saveItems() {
        auctionItems.forEach(auctionItem -> {
            AuctionHouse.getInstance().getData().set("auction items." + auctionItem.getKey().toString() + ".owner", auctionItem.getOwner().toString());
            AuctionHouse.getInstance().getData().set("auction items." + auctionItem.getKey().toString() + ".highest bidder", auctionItem.getHighestBidder().toString());
            AuctionHouse.getInstance().getData().set("auction items." + auctionItem.getKey().toString() + ".category", auctionItem.getCategory().name());
            AuctionHouse.getInstance().getData().set("auction items." + auctionItem.getKey().toString() + ".key", auctionItem.getKey().toString());
            AuctionHouse.getInstance().getData().set("auction items." + auctionItem.getKey().toString() + ".base price", auctionItem.getBasePrice());
            AuctionHouse.getInstance().getData().set("auction items." + auctionItem.getKey().toString() + ".bid start price", auctionItem.getBidStartPrice());
            AuctionHouse.getInstance().getData().set("auction items." + auctionItem.getKey().toString() + ".bid increment price", auctionItem.getBidIncPrice());
            AuctionHouse.getInstance().getData().set("auction items." + auctionItem.getKey().toString() + ".current price", auctionItem.getCurrentPrice());
            AuctionHouse.getInstance().getData().set("auction items." + auctionItem.getKey().toString() + ".remaining time", auctionItem.getRemainingTime());
            AuctionHouse.getInstance().getData().set("auction items." + auctionItem.getKey().toString() + ".item", auctionItem.getOriginalItem());
        });

        AuctionHouse.getInstance().getData().save();
    }

    public List<AuctionItem> getFilteredItems(AuctionItemCategory category) {
        switch (category) {
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
            auctionItems.addFirst(item);
        });
    }
}
