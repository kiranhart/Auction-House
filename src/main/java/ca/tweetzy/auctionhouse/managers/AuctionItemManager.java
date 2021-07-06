package ca.tweetzy.auctionhouse.managers;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.auction.AuctionItem;
import ca.tweetzy.core.utils.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: February 02 2021
 * Time Created: 6:27 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */

public class AuctionItemManager {

    private final ConcurrentHashMap<UUID, AuctionItem> auctionItems = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, AuctionItem> garbageBin = new ConcurrentHashMap<>();

    public void addItem(AuctionItem auctionItem) {
        if (auctionItem == null) return;
        this.auctionItems.put(auctionItem.getKey(), auctionItem);
    }

    public void sendToGarbage(AuctionItem auctionItem) {
        if (auctionItem == null) return;
        this.garbageBin.put(auctionItem.getKey(), auctionItem);
    }

    public void removeUnknownOwnerItems() {
        List<UUID> knownOfflinePlayers = Arrays.stream(Bukkit.getOfflinePlayers()).map(OfflinePlayer::getUniqueId).collect(Collectors.toList());
        this.auctionItems.keySet().removeIf(id -> !knownOfflinePlayers.contains(id));
    }

    public AuctionItem getItem(UUID uuid) {
        return this.auctionItems.getOrDefault(uuid, null);
    }

    public ConcurrentHashMap<UUID, AuctionItem> getAuctionItems() {
        return this.auctionItems;
    }

    public ConcurrentHashMap<UUID, AuctionItem> getGarbageBin() {
        return this.garbageBin;
    }

    public void loadItems(boolean useDatabase) {
        if (useDatabase) {
            AuctionHouse.getInstance().getDataManager().getItems(all -> all.forEach(this::addItem));
        } else {
            if (AuctionHouse.getInstance().getData().contains("auction items") && AuctionHouse.getInstance().getData().isList("auction items")) {
                List<AuctionItem> items = AuctionHouse.getInstance().getData().getStringList("auction items").stream().map(AuctionAPI.getInstance()::convertBase64ToObject).map(object -> (AuctionItem) object).collect(Collectors.toList());
                long start = System.currentTimeMillis();
                items.forEach(this::addItem);
                AuctionHouse.getInstance().getLocale().newMessage(TextUtils.formatText(String.format("&aLoaded &2%d &aauction items(s) in &e%d&fms", items.size(), System.currentTimeMillis() - start))).sendPrefixedMessage(Bukkit.getConsoleSender());
                AuctionHouse.getInstance().getData().set("auction items", null);
                AuctionHouse.getInstance().getData().save();
            }
        }
    }

    public void saveItems(boolean useDatabase, boolean async) {
        if (useDatabase) {
            AuctionHouse.getInstance().getDataManager().saveItems(new ArrayList<>(this.getAuctionItems().values()), async);
        } else {
            this.adjustItemsInFile(new ArrayList<>(this.getAuctionItems().values()));
        }
    }

    public void adjustItemsInFile(List<AuctionItem> items) {
        AuctionHouse.getInstance().getData().set("auction items", items.stream().map(AuctionAPI.getInstance()::convertToBase64).collect(Collectors.toList()));
        AuctionHouse.getInstance().getData().save();
    }
}
