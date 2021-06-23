package ca.tweetzy.auctionhouse.managers;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.auction.AuctionFilterItem;
import ca.tweetzy.auctionhouse.auction.AuctionItem;
import ca.tweetzy.core.utils.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: February 02 2021
 * Time Created: 6:27 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */

public class AuctionItemManager {

    private final ArrayList<AuctionItem> auctionItems = new ArrayList<>();
    private final Set<AuctionItem> garbageBin = new HashSet<>();

    public void addItem(AuctionItem auctionItem) {
        if (auctionItem == null) return;
        this.auctionItems.add(auctionItem);
    }

    public void sendToGarbage(AuctionItem auctionItem) {
        if (auctionItem == null) return;
        this.garbageBin.add(auctionItem);
    }

    public void removeUnknownOwnerItems() {
        List<UUID> knownOfflinePlayers = Arrays.stream(Bukkit.getOfflinePlayers()).map(OfflinePlayer::getUniqueId).collect(Collectors.toList());
        this.auctionItems.removeIf(item -> !knownOfflinePlayers.contains(item.getOwner()));
    }

    public AuctionItem getItem(UUID uuid) {
        return this.auctionItems.stream().filter(item -> item.getKey().equals(uuid)).findFirst().orElse(null);
    }

    public List<AuctionItem> getAuctionItems() {
        return this.auctionItems;
    }


    public Set<AuctionItem> getGarbageBin() {
        return garbageBin;
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
            AuctionHouse.getInstance().getDataManager().saveItems(getAuctionItems(), async);
        } else {
            this.adjustItemsInFile(this.getAuctionItems());
        }
    }

    public void adjustItemsInFile(List<AuctionItem> items) {
        AuctionHouse.getInstance().getData().set("auction items", items.stream().map(AuctionAPI.getInstance()::convertToBase64).collect(Collectors.toList()));
        AuctionHouse.getInstance().getData().save();
    }
}
