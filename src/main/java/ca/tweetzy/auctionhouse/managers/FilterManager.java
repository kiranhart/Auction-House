package ca.tweetzy.auctionhouse.managers;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.auction.AuctionFilterItem;
import ca.tweetzy.auctionhouse.auction.AuctionItemCategory;
import ca.tweetzy.core.utils.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: June 22 2021
 * Time Created: 3:41 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class FilterManager {

    private final List<AuctionFilterItem> filterWhitelist = new ArrayList<>();

    public void addFilterItem(AuctionFilterItem auctionFilterItem) {
        if (auctionFilterItem == null) return;
        this.filterWhitelist.add(auctionFilterItem);
    }

    public void removeFilterItem(AuctionFilterItem auctionFilterItem) {
        if (auctionFilterItem == null) return;
        this.filterWhitelist.remove(auctionFilterItem);
    }

    public AuctionFilterItem getFilteredItem(ItemStack itemStack) {
        return this.filterWhitelist.stream().filter(items -> items.getItemStack().isSimilar(itemStack)).findFirst().orElse(null);
    }

    public List<AuctionFilterItem> getFilterWhitelist() {
        return this.filterWhitelist;
    }

    public List<ItemStack> getFilterWhitelist(AuctionItemCategory category) {
        return this.filterWhitelist.stream().filter(item -> item.getCategory() == category).map(item -> AuctionAPI.getInstance().deserializeItem(item.getRawItem())).collect(Collectors.toList());
    }

    public void loadItems() {
        AuctionHouse.getInstance().getDataManager().getFilterWhitelist(all -> all.forEach(this::addFilterItem));
    }

    public void saveFilterWhitelist(boolean async) {
        AuctionHouse.getInstance().getDataManager().saveFilterWhitelist(getFilterWhitelist(), async);
    }
}
