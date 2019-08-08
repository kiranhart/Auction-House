package com.kiranhart.auctionhouse.auction;
/*
    The current file was created by Kiran Hart
    Date: August 03 2019
    Time: 8:11 PM
    
    Code within this class is not to be redistributed without proper permission.
*/

import com.kiranhart.auctionhouse.Core;
import com.kiranhart.auctionhouse.api.AuctionAPI;
import com.kiranhart.auctionhouse.api.statics.AuctionSettings;
import com.kiranhart.auctionhouse.api.version.NBTEditor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AuctionItem {

    private UUID owner;
    private UUID highestBidder;

    private ItemStack item;
    private String key;

    private long startPrice;
    private long bidIncrement;
    private long buyNowPrice;
    private long currentPrice;
    private int time;

    public AuctionItem (UUID owner, UUID highestBidder, ItemStack item, long startPrice, long bidIncrement, long buyNowPrice, long currentPrice, int time, String key) {
        this.owner = owner;
        this.highestBidder = highestBidder;
        this.item = item;
        this.startPrice = startPrice;
        this.bidIncrement = bidIncrement;
        this.buyNowPrice = buyNowPrice;
        this.currentPrice = currentPrice;
        this.time = time;
        this.key = key;
    }

    public AuctionItem(UUID owner, ItemStack item, int time, long startPrice, long bidIncrement, long buyNowPrice) {
        this.owner = owner;
        this.item = item.clone();
        this.startPrice = startPrice;
        this.bidIncrement = bidIncrement;
        this.buyNowPrice = buyNowPrice;
        this.time = time;
        this.currentPrice = startPrice;
        this.highestBidder = owner;
        key = UUID.randomUUID().toString();
    }

    public enum AuctionItemType {
        MAIN("auction-items.auction-stack"),
        LIST("auction-items.listing-stack");

        private String node;

        AuctionItemType(String node) {
            this.node = node;
        }

        public String getNode() {
            return node;
        }
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public UUID getHighestBidder() {
        return highestBidder;
    }

    public void setHighestBidder(UUID highestBidder) {
        this.highestBidder = highestBidder;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getStartPrice() {
        return startPrice;
    }

    public void setStartPrice(long startPrice) {
        this.startPrice = startPrice;
    }

    public long getBidIncrement() {
        return bidIncrement;
    }

    public void setBidIncrement(long bidIncrement) {
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

    /**
     *
     * @param type is this the stack in the main auction or current listing?
     * @return the itemstack that will be shown to the user
     */
    public ItemStack getAuctionStack(AuctionItemType type) {
        ItemStack stack = item.clone();
        stack.setAmount((stack.getAmount() > 1) ? stack.getAmount() : 1);
        ItemMeta meta = stack.getItemMeta();
        List<String> lore = (meta.hasLore() ? meta.getLore() : new ArrayList<>());

        String theOwner = (owner == null) ? "&eSeller&f: &bSample User" : Bukkit.getOfflinePlayer(owner).getName();
        String buyNowNumber = (AuctionSettings.USE_SHORT_NUMBERS_ON_ITEMS) ? AuctionAPI.getInstance().getFriendlyNumber(buyNowPrice) : String.valueOf(NumberFormat.getInstance().format(buyNowPrice));
        String currentPriceNumber = (AuctionSettings.USE_BIDDING_SYSTEM) ? (AuctionSettings.USE_SHORT_NUMBERS_ON_ITEMS) ? AuctionAPI.getInstance().getFriendlyNumber(currentPrice) : String.valueOf(NumberFormat.getInstance().format(currentPrice)) : "&cDisabled";
        String bidIncrementNumber = (AuctionSettings.USE_BIDDING_SYSTEM) ? (AuctionSettings.USE_SHORT_NUMBERS_ON_ITEMS) ? AuctionAPI.getInstance().getFriendlyNumber(bidIncrement) : String.valueOf(NumberFormat.getInstance().format(bidIncrement)) : "&cDisabled";
        String timeLeft = AuctionAPI.getInstance().timeLeft(getTime());

        for (String lores : Core.getInstance().getConfig().getStringList(type.getNode())) {
            lore.add(ChatColor.translateAlternateColorCodes('&', lores.replace("{buynowprice}", buyNowNumber)
                    .replace("{seller}", theOwner)
                    .replace("{currentprice}", currentPriceNumber)
                    .replace("{bidincrement}", bidIncrementNumber)
                    .replace("{highestbidder}", Bukkit.getOfflinePlayer(highestBidder).getPlayer().getName())
                    .replace("{timeleft}", timeLeft)));
        }

        meta.setLore(lore);
        stack.setItemMeta(meta);
        stack = NBTEditor.set(stack, getKey(), "AuctionItemKey");
        return stack;
    }
}
