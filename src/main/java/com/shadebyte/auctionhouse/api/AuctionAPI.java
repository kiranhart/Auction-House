package com.shadebyte.auctionhouse.api;

import com.shadebyte.auctionhouse.Core;
import com.shadebyte.auctionhouse.api.enums.Version;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * The current file has been created by Kiran Hart
 * Date Created: 7/6/2018
 * Time Created: 11:48 AM
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise.
 */
public class AuctionAPI {

    private static AuctionAPI instance;

    private AuctionAPI() {
    }

    public static AuctionAPI getInstance() {
        if (instance == null) {
            instance = new AuctionAPI();
        }
        return instance;
    }

    public ItemStack createConfigItem(String node) {
        String[] rawItem = Core.getInstance().getConfig().getString(node + ".item").split(":");
        ItemStack stack = new ItemStack(Material.valueOf(rawItem[0].toUpperCase()), 1, Short.parseShort(rawItem[1]));
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Core.getInstance().getConfig().getString(node + ".name")));
        List<String> lore = new ArrayList<>();
        Core.getInstance().getConfig().getStringList(node + ".lore").forEach(s -> lore.add(ChatColor.translateAlternateColorCodes('&', s)));
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

    public ItemStack fill(String name) {
        ItemStack stack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 1);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&6&l" + name));
        stack.setItemMeta(meta);
        return stack;
    }
}
