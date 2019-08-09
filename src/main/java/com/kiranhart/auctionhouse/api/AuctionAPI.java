package com.kiranhart.auctionhouse.api;
/*
    The current file was created by Kiran Hart
    Date: August 04 2019
    Time: 1:25 PM
    
    Code within this class is not to be redistributed without proper permission.
*/

import com.kiranhart.auctionhouse.Core;
import com.kiranhart.auctionhouse.api.version.NBTEditor;
import com.kiranhart.auctionhouse.api.version.ServerVersion;
import com.kiranhart.auctionhouse.api.version.XMaterial;
import com.kiranhart.auctionhouse.auction.AuctionItem;
import com.kiranhart.auctionhouse.util.Debugger;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class AuctionAPI {

    private static AuctionAPI instance;

    private AuctionAPI() {}

    public static AuctionAPI getInstance() {
        if (instance == null) {
            instance = new AuctionAPI();
        }
        return instance;
    }

    /**
     *
     * @param value a long number to be converted into a easily readable text
     * @return a user friendly number to read
     */
    public String getFriendlyNumber(double value) {
        int power;
        String suffix = " KMBTQ";
        String formattedNumber = "";

        NumberFormat formatter = new DecimalFormat("#,###.#");
        power = (int) StrictMath.log10(value);
        value = value / (Math.pow(10, (power / 3) * 3));
        formattedNumber = formatter.format(value);
        formattedNumber = formattedNumber + suffix.charAt(power / 3);
        return formattedNumber.length() > 4 ? formattedNumber.replaceAll("\\.[0-9]+", "") : formattedNumber;
    }

    /**
     *
     * @param totalSecs take seconds and convert to proper date time
     * @return total time left in a string
     */
    public String timeLeft(int totalSecs) {
        int hours = totalSecs / 3600;
        int minutes = (totalSecs % 3600) / 60;
        int seconds = totalSecs % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    /**
     *
     * @param number is this string a number?
     * @return whether or not the provided string is numeric
     */
    public boolean isNumeric(String number) {
        try {
            Long.parseLong(number);
        } catch (NumberFormatException nfe) {
            Debugger.report(nfe);
            return false;
        }
        return true;
    }

    @SuppressWarnings("deprecation")
    public void setItemInHand(Player p, ItemStack item) {
        if (Core.getInstance().isServerVersionAtLeast(ServerVersion.V1_9)) {
            p.getInventory().setItemInMainHand(item);
        } else {
            p.setItemInHand(item);
        }
    }

    @SuppressWarnings("deprecation")
    public ItemStack getItemInHand(Player p) {
        if (Core.getInstance().isServerVersionAtLeast(ServerVersion.V1_9)) {
            return p.getInventory().getItemInMainHand();
        } else {
            return p.getItemInHand();
        }
    }

    /**
     * @return the item shown when the player clicks and auction item
     * but does not have enough money to bid or purchase it.
     */
    public ItemStack createNotEnoughMoneyIcon() {
        String[] item = Core.getInstance().getConfig().getString("guis.auctionhouse.items.not-enough-money.item").split(":");
        ItemStack stack = XMaterial.matchXMaterial(item[0].toUpperCase(), Byte.parseByte(item[1])).parseItem();
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Core.getInstance().getConfig().getString("guis.auctionhouse.items.not-enough-money.name")));
        List<String> lore = new ArrayList<>();
        Core.getInstance().getConfig().getStringList("guis.auctionhouse.items.not-enough-money.name").forEach(line -> lore.add(ChatColor.translateAlternateColorCodes('&', line)));
        meta.setLore(lore);
        stack.setItemMeta(meta);
        return stack;
    }

    /**
     * @param node            the path to the item within the config.yml
     * @param activeAuctions  update the lore on the total active auctions?
     * @param expiredAuctions update the lore on the total expired auctions?
     * @return the generated configuration item based on params
     */
    public ItemStack createConfigurationItem(String node, int activeAuctions, int expiredAuctions) {
        String[] item = Core.getInstance().getConfig().getString(node + ".item").split(":");
        ItemStack stack = XMaterial.matchXMaterial(item[0].toUpperCase(), Byte.parseByte(item[1])).parseItem();
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Core.getInstance().getConfig().getString(node + ".name")));
        List<String> lore = new ArrayList<>();
        Core.getInstance().getConfig().getStringList(node + ".lore").forEach(s -> lore.add(ChatColor.translateAlternateColorCodes('&', s.replace("{active_player_auctions}", String.valueOf(activeAuctions)).replace("{expired_player_auctions}", String.valueOf(expiredAuctions)))));
        meta.setLore(lore);
        stack.setItemMeta(meta);
        return stack;
    }

    /**
     * @param p, the player of which you wish to get all the expired items if any
     * @return a list of all of the expired items
     */
    public List<ItemStack> getAllExpiredItems(Player p) {
        List<ItemStack> items = new ArrayList<>();
        if (Core.getInstance().getData().getConfig().getConfigurationSection("expired." + p.getUniqueId().toString()) != null) {
            if (Core.getInstance().getData().getConfig().getConfigurationSection("expired." + p.getUniqueId().toString()).getKeys(false).size() >= 1) {
                Core.getInstance().getData().getConfig().getConfigurationSection("expired." + p.getUniqueId().toString()).getKeys(false).forEach(s -> items.add(Core.getInstance().getData().getConfig().getItemStack("expired." + p.getUniqueId().toString() + "." + s + ".display")));
            }
        }
        return items;
    }

    /*
     * @param stack is the auction item to be converted
     * @return the new expired item.
     */
    public ItemStack expiredAuctionItem(AuctionItem stack) {
        ItemStack item = stack.getItem();
        item = NBTEditor.set(item, stack.getKey(), "ExpiredAuctionItem");
        return item;
    }

    /**
     * @param inventory, the inventory that wil be slot checked
     * @return if there is an available slot for the item
     */
    public int availableSlots(PlayerInventory inventory) {
        int count = 0;
        for (ItemStack i : inventory) {
            if (i == null) {
                count++;
            }
        }
        return count;
    }
}
