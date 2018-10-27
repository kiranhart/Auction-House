package com.shadebyte.auctionhouse.auction;

import com.shadebyte.auctionhouse.Core;
import com.shadebyte.auctionhouse.api.AuctionAPI;
import com.shadebyte.auctionhouse.util.NBTEditor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.bukkit.ChatColor.translateAlternateColorCodes;

/**
 * The current file has been created by Kiran Hart
 * Date Created: 7/6/2018
 * Time Created: 11:49 AM
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise.
 */
public class AuctionItem {

    private String owner;
    private String highestBidder;
    private ItemStack item;
    private long startPrice;
    private long bidIncrement;
    private long buyNowPrice;
    private long currentPrice;
    private int time;
    private String key;

    public AuctionItem(String owner, String highestBidder, ItemStack item, long startPrice, long bidIncrement, long buyNowPrice, long currentPrice, int time, String key) {
        this.owner = owner;
        this.item = item;
        this.startPrice = startPrice;
        this.bidIncrement = bidIncrement;
        this.buyNowPrice = buyNowPrice;
        this.time = time;
        this.currentPrice = currentPrice;
        this.key = key;
        this.highestBidder = highestBidder;
    }

    public AuctionItem(String owner, ItemStack item, int time, long startPrice, long bidIncrement, long buyNowPrice) {
        this.owner = owner;
        this.item = item.clone();
        this.startPrice = startPrice;
        this.bidIncrement = bidIncrement;
        this.buyNowPrice = buyNowPrice;
        this.time = time;
        this.currentPrice = startPrice;
        key = UUID.randomUUID().toString();
        this.highestBidder = owner;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public long getStartPrice() {
        return startPrice;
    }

    public void setStartPrice(int startPrice) {
        this.startPrice = startPrice;
    }

    public long getBidIncrement() {
        return bidIncrement;
    }

    public void setBidIncrement(int bidIncrement) {
        this.bidIncrement = bidIncrement;
    }

    public long getBuyNowPrice() {
        return buyNowPrice;
    }

    public void setBuyNowPrice(long buyNowPrice) {
        this.buyNowPrice = buyNowPrice;
    }

    public long getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(long currentPrice) {
        this.currentPrice = currentPrice;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getKey() {
        return key;
    }

    public String getHighestBidder() {
        return highestBidder;
    }

    public void setHighestBidder(String highestBidder) {
        this.highestBidder = highestBidder;
    }

    public String getDisplayName() {
        String name = key;
        if (item.hasItemMeta()) {
            if (item.getItemMeta().hasDisplayName())
                name = item.getItemMeta().getDisplayName();
            else
                name = StringUtils.capitalize(item.getType().name().toLowerCase().replace("_", " "));
        } else {
            name = StringUtils.capitalize(item.getType().name().toLowerCase().replace("_", " "));
        }
        return name;
    }

    public void updateTime(int removeAmount) {
        if (time - removeAmount <= 0) {
            time = 0;
        } else {
            time = time - removeAmount;
        }
    }

    public ItemStack auctionStack() {
        ItemStack stack = item.clone();
        stack.setAmount((stack.getAmount() > 1) ? stack.getAmount() : 1);
        ItemMeta meta = stack.getItemMeta();
        List<String> lore = (meta.hasLore() ? meta.getLore() : new ArrayList<>());
        lore.add(translateAlternateColorCodes('&', "&7-------------------------"));
        if (owner == null)
            lore.add(translateAlternateColorCodes('&', "&eSeller&f: &bSample User"));
        else
            lore.add(translateAlternateColorCodes('&', "&eSeller&f: &b" + Bukkit.getOfflinePlayer(UUID.fromString(owner)).getName()));
        lore.add(translateAlternateColorCodes('&', ""));
        lore.add(translateAlternateColorCodes('&', "&eBuy Now: &a$" + AuctionAPI.getInstance().friendlyNumber(buyNowPrice)));
        if (Core.getInstance().getConfig().getBoolean("settings.use-bid-system")) {
            lore.add(translateAlternateColorCodes('&', "&eCurrent Price: &a$" + AuctionAPI.getInstance().friendlyNumber(currentPrice)));
            lore.add(translateAlternateColorCodes('&', "&eBid Increment: &a$" + AuctionAPI.getInstance().friendlyNumber(bidIncrement)));
        }
        lore.add(translateAlternateColorCodes('&', ""));
        lore.add(translateAlternateColorCodes('&', "&eTime Left: &b" + AuctionAPI.getInstance().timeLeft(getTime())));
        lore.add(translateAlternateColorCodes('&', "&7-------------------------"));
        if (Core.getInstance().getConfig().getBoolean("settings.use-bid-system"))
            lore.add(translateAlternateColorCodes('&', "&eLeft-Click&f: &bBid"));
        lore.add(translateAlternateColorCodes('&', "&eRight-Click&f: &bBuy Now"));
        lore.add(translateAlternateColorCodes('&', "&7-------------------------"));
        meta.setLore(lore);
        stack.setItemMeta(meta);
        stack = NBTEditor.setItemTag(stack, getKey(), "AuctionItemKey");
        return stack;
    }

    public ItemStack listingStack() {
        ItemStack stack = item.clone();
        stack.setAmount((stack.getAmount() > 1) ? stack.getAmount() : 1);
        ItemMeta meta = stack.getItemMeta();
        List<String> lore = (meta.hasLore() ? meta.getLore() : new ArrayList<>());
        lore.add(translateAlternateColorCodes('&', "&7-------------------------"));
        lore.add(translateAlternateColorCodes('&', "&eBuy Now: &a$" + AuctionAPI.getInstance().friendlyNumber(buyNowPrice)));
        if (Core.getInstance().getConfig().getBoolean("settings.use-bid-system")) {
            lore.add(translateAlternateColorCodes('&', "&eCurrent Price: &a$" + AuctionAPI.getInstance().friendlyNumber(currentPrice)));
            lore.add(translateAlternateColorCodes('&', "&eBid Increment: &a$" + AuctionAPI.getInstance().friendlyNumber(bidIncrement)));
        }
        lore.add(translateAlternateColorCodes('&', ""));
        lore.add(translateAlternateColorCodes('&', "&eTime Left: &b" + AuctionAPI.getInstance().timeLeft(getTime())));
        lore.add(translateAlternateColorCodes('&', "&7-------------------------"));
        meta.setLore(lore);
        stack.setItemMeta(meta);
        stack = NBTEditor.setItemTag(stack, getKey(), "AuctionItemKey");
        return stack;
    }
}
