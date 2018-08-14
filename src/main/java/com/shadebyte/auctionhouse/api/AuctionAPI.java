package com.shadebyte.auctionhouse.api;

import com.shadebyte.auctionhouse.Core;
import com.shadebyte.auctionhouse.api.enums.Version;
import com.shadebyte.auctionhouse.auction.AuctionItem;
import com.shadebyte.auctionhouse.util.Debugger;
import com.shadebyte.auctionhouse.util.NBTEditor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * The current file has been created by Kiran Hart
 * Date Created: 7/6/2018
 * Time Created: 11:48 AM
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise.
 */
public class AuctionAPI {

    private static char[] c = new char[]{'k', 'm', 'b', 't'};
    private final static TreeMap<Integer, String> map = new TreeMap<Integer, String>();

    private static AuctionAPI instance;

    private AuctionAPI() {
    }

    public static AuctionAPI getInstance() {
        if (instance == null) {
            instance = new AuctionAPI();
        }
        return instance;
    }

    static {

        map.put(1000, "M");
        map.put(900, "CM");
        map.put(500, "D");
        map.put(400, "CD");
        map.put(100, "C");
        map.put(90, "XC");
        map.put(50, "L");
        map.put(40, "XL");
        map.put(10, "X");
        map.put(9, "IX");
        map.put(5, "V");
        map.put(4, "IV");
        map.put(1, "I");

    }

    public String toRoman(int number) {
        int l = map.floorKey(number);
        if (number == l) {
            return map.get(number);
        }
        return map.get(l) + toRoman(number - l);
    }

    public ItemStack expiredAuctionItem(AuctionItem stack) {
        ItemStack item = stack.getItem();
        item = NBTEditor.setItemTag(item, stack.getKey(), "ExpiredAuctionItem");
        return item;
    }

    public List<ItemStack> getAllExpiredItems(Player p) {
        List<ItemStack> items = new ArrayList<>();
        if (Core.getInstance().getData().getConfig().getConfigurationSection("expired." + p.getUniqueId().toString()) != null) {
            if (Core.getInstance().getData().getConfig().getConfigurationSection("expired." + p.getUniqueId().toString()).getKeys(false).size() >= 1) {
                for (String s : Core.getInstance().getData().getConfig().getConfigurationSection("expired." + p.getUniqueId().toString()).getKeys(false)) {
                    items.add(Core.getInstance().getData().getConfig().getItemStack("expired." + p.getUniqueId().toString() + "." + s + ".display"));
                }
            }
        }
        return items;
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

    public ItemStack createTransactionConfigItem(String node, String buyer, String seller, int startPrice, int bidincrement, int buynowprice) {
        String[] rawItem = Core.getInstance().getConfig().getString(node + ".item").split(":");
        ItemStack stack = new ItemStack(Material.valueOf(rawItem[0].toUpperCase()), 1, Short.parseShort(rawItem[1]));
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Core.getInstance().getConfig().getString(node + ".name")));
        List<String> lore = new ArrayList<>();
        Core.getInstance().getConfig().getStringList(node + ".lore").forEach(s -> lore.add(ChatColor.translateAlternateColorCodes('&', s.replace("{seller}", seller).replace("{buyer}", buyer).replace("{startprice}", String.valueOf(startPrice)).replace("{bidincrement}", String.valueOf(bidincrement)).replace("{buynowprice}", String.valueOf(buynowprice)))));
        meta.setLore(lore);
        stack.setItemMeta(meta);
        return stack;
    }

    public ItemStack createUserHead(String node, String seller, String buyer, int usr) {
        ItemStack stack = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta meta = (SkullMeta) stack.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Core.getInstance().getConfig().getString(node + ".name")));
        meta.setOwner((usr == 0) ? Bukkit.getOfflinePlayer(UUID.fromString(seller)).getName() : Bukkit.getOfflinePlayer(UUID.fromString(buyer)).getName());
        List<String> lore = new ArrayList<>();
        Core.getInstance().getConfig().getStringList(node + ".lore").forEach(s -> lore.add(ChatColor.translateAlternateColorCodes('&', s)));
        meta.setLore(lore);
        stack.setItemMeta(meta);
        stack = (usr == 0) ? NBTEditor.setItemTag(stack, seller, "AuctionSellerHead") : NBTEditor.setItemTag(stack, buyer, "AuctionBuyerHead");
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

    public String getDate(long milli) {
        return new SimpleDateFormat("MMMM dd yyyy").format(new Date(milli));
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

    public String getSQLDisplayName(ItemStack item) {
        String name;
        if (item.hasItemMeta()) {
            name = (item.getItemMeta().hasDisplayName()) ? item.getItemMeta().getDisplayName() : StringUtils.capitalize(item.getType().name().toLowerCase().replace("_", " "));
        } else {
            name = StringUtils.capitalize(item.getType().name().toLowerCase().replace("_", " "));
        }
        return name;
    }

    public String getSQLLore(ItemStack item) {
        String lore = "";
        if (item.hasItemMeta()) {
            if (!item.getItemMeta().hasLore()) {
                lore = "No Lore";
            } else {
                for (String s : item.getItemMeta().getLore()) {
                    lore += ChatColor.stripColor(s) + ";";
                }
            }
        } else {
            lore = "No Lore";
        }
        return lore;
    }

    public String getSQLEnchantments(ItemStack item) {
        String lore = "";
        if (item.hasItemMeta()) {
            if (!item.getItemMeta().hasEnchants()) {
                lore = "No Enchantments";
            } else {
                for (Enchantment enchantment : item.getItemMeta().getEnchants().keySet()) {
                    String name = enchantment.getName().replace("_", " ").toLowerCase();
                    String level = toRoman(item.getItemMeta().getEnchantLevel(enchantment));
                    String e = StringUtils.capitalize(name) + "," + level;
                    lore += e + ";";
                }
            }
        } else {
            lore = "No Enchantments";
        }
        return lore;
    }
}
