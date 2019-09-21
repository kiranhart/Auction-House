package com.kiranhart.auctionhouse.api;
/*
    The current file was created by Kiran Hart
    Date: August 04 2019
    Time: 1:25 PM
    
    Code within this class is not to be redistributed without proper permission.
*/

import com.kiranhart.auctionhouse.Core;
import com.kiranhart.auctionhouse.api.statics.AuctionPermissions;
import com.kiranhart.auctionhouse.api.version.NBTEditor;
import com.kiranhart.auctionhouse.api.version.ServerVersion;
import com.kiranhart.auctionhouse.api.version.XMaterial;
import com.kiranhart.auctionhouse.auction.AuctionItem;
import com.kiranhart.auctionhouse.auction.AuctionSortMethod;
import com.kiranhart.auctionhouse.auction.Transaction;
import com.kiranhart.auctionhouse.util.Debugger;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.UUID;

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

    public enum AuctionHeadType {
        BUYER, SELLER
    }

    private static final TreeMap<Integer, String> romanSuffix = new TreeMap();

    static {
        romanSuffix.put(1000, "M");
        romanSuffix.put(900, "CM");
        romanSuffix.put(500, "D");
        romanSuffix.put(400, "CD");
        romanSuffix.put(100, "C");
        romanSuffix.put(90, "XC");
        romanSuffix.put(50, "L");
        romanSuffix.put(40, "XL");
        romanSuffix.put(10, "X");
        romanSuffix.put(9, "IX");
        romanSuffix.put(5, "V");
        romanSuffix.put(4, "IV");
        romanSuffix.put(1, "I");
    }

    /**
     * @param number, convert given number to roman numerals
     * @return the provided number in roman numerals
     */
    public String toRoman(int number) {
        int l = romanSuffix.floorKey(number);
        if (number == l) {
            return romanSuffix.get(number);
        }
        return romanSuffix.get(l) + toRoman(number - l);
    }

    /**
     * @param sender is the cmd sender being checked for admin permissions
     * @return whether they have it or not
     */
    public boolean senderHasHigherPermissions(CommandSender sender) {
        if (sender.isOp()) return true;
        if (sender.hasPermission(AuctionPermissions.ADMIN)) return true;
        if (sender.hasPermission(AuctionPermissions.UPLOAD_TRANSACTIONS_COMMAND)) return true;
        if (sender.hasPermission(AuctionPermissions.RELOAD_COMMAND)) return true;
        if (sender.hasPermission(AuctionPermissions.LOCK_COMMAND)) return true;
        if (sender.hasPermission(AuctionPermissions.ENDALL_COMMAND)) return true;
        return false;
    }

    /**
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
     * @param node,         the node of the item stack
     * @param buyer,        buyer uuid
     * @param seller,       seller uuid
     * @param startPrice,   what is the start price
     * @param bidincrement, what is the bid increment
     * @param buynowprice,  what is the buy now price
     * @return
     */
    public ItemStack createTransactionConfigItem(String node, String buyer, String seller, int startPrice, int bidincrement, int buynowprice) {
        String[] rawItem = Core.getInstance().getConfig().getString(node + ".item").split(":");
        ItemStack stack = XMaterial.matchXMaterial(rawItem[0].toUpperCase(), Byte.parseByte(rawItem[1])).parseItem();
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Core.getInstance().getConfig().getString(node + ".name")));
        List<String> lore = new ArrayList<>();
        Core.getInstance().getConfig().getStringList(node + ".lore").forEach(s -> lore.add(ChatColor.translateAlternateColorCodes('&', s.replace("{seller}", seller).replace("{buyer}", buyer).replace("{startprice}", String.valueOf(startPrice)).replace("{bidincrement}", String.valueOf(bidincrement)).replace("{buynowprice}", String.valueOf(buynowprice)))));
        meta.setLore(lore);
        stack.setItemMeta(meta);
        return stack;
    }

    /**
     * @param type        what type of auction head is it?
     * @param transaction the transaction required for special data
     * @return the skull item, hopefully
     */
    public ItemStack createUserHead(AuctionHeadType type, String transaction) {
        ItemStack stack = XMaterial.PLAYER_HEAD.parseItem();
        SkullMeta meta = (SkullMeta) stack.getItemMeta();
        meta.setOwner((type == AuctionHeadType.BUYER) ? Bukkit.getOfflinePlayer(UUID.fromString(Core.getInstance().getTransactions().getConfig().getString("transactions." + transaction + ".buyer"))).getName() : Bukkit.getOfflinePlayer(UUID.fromString(Core.getInstance().getTransactions().getConfig().getString("transactions." + transaction + ".seller"))).getName());
        meta.setDisplayName((type == AuctionHeadType.BUYER) ? ChatColor.translateAlternateColorCodes('&', Core.getInstance().getConfig().getString("guis.singletransaction.items.buyer.name")) : ChatColor.translateAlternateColorCodes('&', Core.getInstance().getConfig().getString("guis.singletransaction.items.seller.name")));
        List<String> lore = new ArrayList<>();
        Core.getInstance().getConfig().getStringList((type == AuctionHeadType.BUYER) ? "guis.singletransaction.items.buyer" : "guis.singletransaction.items.seller").forEach(s -> lore.add(ChatColor.translateAlternateColorCodes('&', s)));
        meta.setLore(lore);
        stack.setItemMeta(meta);
        stack = (type == AuctionHeadType.SELLER) ? NBTEditor.set(stack, Core.getInstance().getTransactions().getConfig().getString("transactions." + transaction + ".seller"), "AuctionSellerHead") : NBTEditor.set(stack, Core.getInstance().getTransactions().getConfig().getString("transactions." + transaction + ".buyer"), "AuctionBuyerHead");
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

    /**
     * @param stack is the item stack you will get the lore from
     * @return the found lore of the given item
     */
    public String getMySQLLore(ItemStack stack) {
        String lore = "";
        if (!stack.hasItemMeta()) return lore = "No Lore";
        if (!stack.getItemMeta().hasLore()) return lore = "No Lore";
        for (String s : stack.getItemMeta().getLore()) {
            lore += ChatColor.stripColor(s) + ";";
        }
        return lore;
    }

    /**
     * @param stack is the item stack you will get the enchants from
     * @return the enchantments that were found in a string
     */
    public String getMySQLEnchantments(ItemStack stack) {
        String enchants = "";
        if (!stack.hasItemMeta()) return enchants = "None";
        if (!stack.getItemMeta().hasEnchants()) return enchants = "None";
        for (Enchantment enchantment : stack.getItemMeta().getEnchants().keySet()) {
            String name = enchantment.getName().replace("_", " ").toLowerCase();
            String level = toRoman(stack.getItemMeta().getEnchantLevel(enchantment));
            String comp = StringUtils.capitalize(name) + "," + level;
            enchants += comp + ";";
        }
        return enchants;
    }

    /**
     * @return a list of every transaction that was recorded on the flat file 'transactions.yml'
     */
    public List<Transaction> requestEveryFlatFileTransaction() {
        List<Transaction> collection = new ArrayList<>();

        if (Core.getInstance().getTransactions().getConfig().getConfigurationSection("transactions") == null || Core.getInstance().getTransactions().getConfig().getConfigurationSection("transactions").getKeys(false).size() == 0) {
            return collection;
        }

        Core.getInstance().getTransactions().getConfig().getConfigurationSection("transactions").getKeys(false).forEach(transaction -> {

            Transaction.TransactionType transactionType = (Core.getInstance().getTransactions().getConfig().getString("transactions." + transaction + ".transaction-type").equalsIgnoreCase("Won Auction")) ? Transaction.TransactionType.AUCTION_WON : Transaction.TransactionType.BOUGHT;

            UUID seller = UUID.fromString(Core.getInstance().getTransactions().getConfig().getString("transactions." + transaction + ".seller"));
            UUID buyer = UUID.fromString(Core.getInstance().getTransactions().getConfig().getString("transactions." + transaction + ".buyer"));

            long timeCompleted = Core.getInstance().getTransactions().getConfig().getLong("transactions." + transaction + ".time-completed");
            long startPrice = Core.getInstance().getTransactions().getConfig().getLong("transactions." + transaction + ".start-price");
            long bidIncrement = Core.getInstance().getTransactions().getConfig().getLong("transactions." + transaction + ".bid-increment");
            long currentPrice = Core.getInstance().getTransactions().getConfig().getLong("transactions." + transaction + ".current-price");
            long buyNowPrice = Core.getInstance().getTransactions().getConfig().getLong("transactions." + transaction + ".buy-now-price");

            int timeLeft = Core.getInstance().getTransactions().getConfig().getInt("transactions." + transaction + ".time-left");
            String auctionID = Core.getInstance().getTransactions().getConfig().getString("transactions." + transaction + ".auction-id");

            ItemStack item = Core.getInstance().getTransactions().getConfig().getItemStack("transactions." + transaction + ".item");
            ItemStack receipt = Core.getInstance().getTransactions().getConfig().getItemStack("transactions." + transaction + ".receipt");

            collection.add(new Transaction(transactionType, new AuctionItem(seller, buyer, item, startPrice, bidIncrement, buyNowPrice, currentPrice, timeLeft, auctionID), buyer, timeCompleted));
        });
        return collection;
    }

    public List<AuctionItem> sortBasedOnPlayer(List<AuctionItem> list, Player p) {
        List<AuctionItem> sorted = list;

        if (!Core.getInstance().getSortMethod().containsKey(p)) {
            Core.getInstance().getSortMethod().put(p, AuctionSortMethod.DEFAULT);
            return sorted;
        }

        //Armor
        if (Core.getInstance().getSortMethod().get(p) == AuctionSortMethod.ARMOR) {
            sorted.forEach(item -> {
                if (!item.getItem().getType().name().endsWith("_HELMET") || !item.getItem().getType().name().endsWith("_CHESTPLATE") || !item.getItem().getType().name().endsWith("_LEGGINGS") || !item.getItem().getType().name().endsWith("_BOOTS")) {
                    sorted.remove(item);
                }
            });
        }

        //Blocks
        if (Core.getInstance().getSortMethod().get(p) == AuctionSortMethod.BLOCKS) {
            sorted.forEach(item -> {
                if (!item.getItem().getType().isBlock()) {
                    sorted.remove(item);
                }
            });
        }

        //Tools
        if (Core.getInstance().getSortMethod().get(p) == AuctionSortMethod.TOOLS) {
            sorted.forEach(item -> {
                if (!item.getItem().getType().name().endsWith("_SWORD") || !item.getItem().getType().name().endsWith("_AXE") || !item.getItem().getType().name().endsWith("_HOE") || !item.getItem().getType().name().endsWith("_SHOVEL") || item.getItem().getType() != XMaterial.BOW.parseMaterial()) {
                    sorted.remove(item);
                }
            });
        }

        //Food
        if (Core.getInstance().getSortMethod().get(p) == AuctionSortMethod.FOOD) {
            sorted.forEach(item -> {
                if (!item.getItem().getType().isEdible()) {
                    sorted.remove(item);
                }
            });
        }
        return sorted;
    }
}
