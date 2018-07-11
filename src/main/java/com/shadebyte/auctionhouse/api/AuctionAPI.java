package com.shadebyte.auctionhouse.api;

import com.shadebyte.auctionhouse.Core;
import com.shadebyte.auctionhouse.api.enums.Version;
import com.shadebyte.auctionhouse.util.Debugger;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * The current file has been created by Kiran Hart
 * Date Created: 7/6/2018
 * Time Created: 11:48 AM
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise.
 */
public class AuctionAPI {

    private static char[] c = new char[]{'k', 'm', 'b', 't'};

    private static AuctionAPI instance;

    private AuctionAPI() {
    }

    public static AuctionAPI getInstance() {
        if (instance == null) {
            instance = new AuctionAPI();
        }
        return instance;
    }

    public ItemStack createConfigItem(String node, int activeAuctions, int expiredAuctions) {
        String[] rawItem = Core.getInstance().getConfig().getString(node + ".item").split(":");
        ItemStack stack = new ItemStack(Material.valueOf(rawItem[0].toUpperCase()), 1, Short.parseShort(rawItem[1]));
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Core.getInstance().getConfig().getString(node + ".name")));
        List<String> lore = new ArrayList<>();
        Core.getInstance().getConfig().getStringList(node + ".lore").forEach(s -> lore.add(ChatColor.translateAlternateColorCodes('&', s
        .replace("{active_player_auctions}", String.valueOf(activeAuctions))
        .replace("{expired_player_auctions}", String.valueOf(expiredAuctions)))));
        meta.setLore(lore);
        stack.setItemMeta(meta);
        return stack;
    }

    public int availableSlots(PlayerInventory inventory) {
        int count = 0;
        for (ItemStack i : inventory) {
            if (i == null) {
                count++;
            }
        }
        return count;
    }

    public boolean isNumeric(String number) {
        try {
            Integer.parseInt(number);
        } catch (NumberFormatException nfe) {
            Debugger.report(nfe);
            return false;
        }
        return true;
    }

    @SuppressWarnings("deprecation")
    public static ItemStack getItemInHand(Player player) {
        if (Version.getCurrentVersion().getCurrentVersionInteger() >= Version.v1_9_R1.getCurrentVersionInteger()) {
            return player.getInventory().getItemInMainHand();
        } else {
            return player.getItemInHand();
        }
    }

    @SuppressWarnings("deprecation")
    public static void setItemInHand(Player player, ItemStack item) {
        if (Version.getCurrentVersion().getCurrentVersionInteger() >= Version.v1_9_R1.getCurrentVersionInteger()) {
            player.getInventory().setItemInMainHand(item);
        } else {
            player.setItemInHand(item);
        }
    }

    public String timeLeft(int totalSecs) {
        int hours = totalSecs / 3600;
        int minutes = (totalSecs % 3600) / 60;
        int seconds = totalSecs % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public String friendlyNumber(double value) {
        int power;
        String suffix = " KMBT";
        String formattedNumber = "";

        NumberFormat formatter = new DecimalFormat("#,###.#");
        power = (int) StrictMath.log10(value);
        value = value / (Math.pow(10, (power / 3) * 3));
        formattedNumber = formatter.format(value);
        formattedNumber = formattedNumber + suffix.charAt(power / 3);
        return formattedNumber.length() > 4 ? formattedNumber.replaceAll("\\.[0-9]+", "") : formattedNumber;
    }

    public ItemStack fill(String name) {
        ItemStack stack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 1);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&6&l" + name));
        stack.setItemMeta(meta);
        return stack;
    }

    public ItemStack fill(String name, int color) {
        ItemStack stack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) color);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&6&l" + name));
        stack.setItemMeta(meta);
        return stack;
    }
}
