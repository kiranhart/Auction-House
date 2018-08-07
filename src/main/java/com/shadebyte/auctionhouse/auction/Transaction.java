package com.shadebyte.auctionhouse.auction;

import com.shadebyte.auctionhouse.Core;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The current file has been created by Kiran Hart
 * Date Created: 7/11/2018
 * Time Created: 3:06 PM
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise.
 */
public class Transaction {

    public enum TransactionType {

        AUCTION_WON("Won Auction"),
        BOUGHT("Bought Immediately");

        private String transactionType;

        TransactionType(String transactionType) {
            this.transactionType = transactionType;
        }

        public String getTransactionType() {
            return transactionType;
        }
    }

    private TransactionType transactionType;
    private AuctionItem auctionItem;
    private String buyer;
    private long timeCompleted;

    public Transaction(TransactionType transactionType, AuctionItem auctionItem, String buyer, long timeCompleted) {
        this.transactionType = transactionType;
        this.auctionItem = auctionItem;
        this.buyer = buyer;
        this.timeCompleted = timeCompleted;
    }

    public void saveTransaction() {
        Core.getInstance().getTransactions().getConfig().set("transactions." + timeCompleted + auctionItem.getKey() + ".transaction-type", transactionType.name());
        Core.getInstance().getTransactions().getConfig().set("transactions." + timeCompleted + auctionItem.getKey() + ".seller", auctionItem.getOwner());
        Core.getInstance().getTransactions().getConfig().set("transactions." + timeCompleted + auctionItem.getKey() + ".buyer", buyer);
        Core.getInstance().getTransactions().getConfig().set("transactions." + timeCompleted + auctionItem.getKey() + ".start-price", auctionItem.getStartPrice());
        Core.getInstance().getTransactions().getConfig().set("transactions." + timeCompleted + auctionItem.getKey() + ".bid-increment", auctionItem.getBidIncrement());
        Core.getInstance().getTransactions().getConfig().set("transactions." + timeCompleted + auctionItem.getKey() + ".current-price", auctionItem.getCurrentPrice());
        Core.getInstance().getTransactions().getConfig().set("transactions." + timeCompleted + auctionItem.getKey() + ".buy-now-price", auctionItem.getBuyNowPrice());
        Core.getInstance().getTransactions().getConfig().set("transactions." + timeCompleted + auctionItem.getKey() + ".time-left", auctionItem.getTime());
        Core.getInstance().getTransactions().getConfig().set("transactions." + timeCompleted + auctionItem.getKey() + ".auction-id", auctionItem.getKey());
        Core.getInstance().getTransactions().getConfig().set("transactions." + timeCompleted + auctionItem.getKey() + ".time-completed", timeCompleted);
        Core.getInstance().getTransactions().getConfig().set("transactions." + timeCompleted + auctionItem.getKey() + ".item", auctionItem.getItem());
        Core.getInstance().getTransactions().saveConfig();
    }

    public static int getTotalTransactions() {
        return (Core.getInstance().getTransactions().getConfig().getConfigurationSection("transactions") != null && Core.getInstance().getTransactions().getConfig().getConfigurationSection("transactions").getKeys(false).size() > 0) ? Core.getInstance().getTransactions().getConfig().getConfigurationSection("transactions").getKeys(false).size() : 0;
    }

    public static int getTotalTransactionsByPlayer(Player p) {
        if (getTotalTransactions() == 0) return 0;
        int total = 0;
        for (String transactions : Core.getInstance().getTransactions().getConfig().getConfigurationSection("transactions").getKeys(false)) {
            if (Core.getInstance().getTransactions().getConfig().getString("transactions." + transactions + ".seller").equalsIgnoreCase(p.getUniqueId().toString()) || Core.getInstance().getTransactions().getConfig().getString("transactions." + transactions + ".buyer").equalsIgnoreCase(p.getUniqueId().toString())) {
                total++;
            }
        }
        return total;
    }

    private static ItemStack getTransactionItem(String node) {
        String[] stack = Core.getInstance().getConfig().getString("transaction.item").split(":");
        ItemStack item = new ItemStack(Material.valueOf(stack[0].toUpperCase()), 1, Short.parseShort(stack[1]));
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Core.getInstance().getConfig().getString("transaction.name").replace("{transaction_id}", Core.getInstance().getTransactions().getConfig().getString("transactions." + node + ".auction-id"))));
        List<String> lore = new ArrayList<>();
        Core.getInstance().getConfig().getStringList("transaction.lore").forEach(e-> lore.add(ChatColor.translateAlternateColorCodes('&', e.replace("{buyer}", Bukkit.getOfflinePlayer(UUID.fromString(Core.getInstance().getTransactions().getConfig().getString("transactions." + node + ".buyer"))).getName()).replace("{seller}", Bukkit.getOfflinePlayer(UUID.fromString(Core.getInstance().getTransactions().getConfig().getString("transactions." + node + ".seller"))).getName()))));
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public static List<ItemStack> getAllRecordedTransactions() {
        List<ItemStack> items = new ArrayList<>();
        if (getTotalTransactions() == 0) return items;
        Core.getInstance().getTransactions().getConfig().getConfigurationSection("transactions").getKeys(false).forEach(node -> items.add(getTransactionItem(node)));
        return items;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public AuctionItem getAuctionItem() {
        return auctionItem;
    }

    public void setAuctionItem(AuctionItem auctionItem) {
        this.auctionItem = auctionItem;
    }

    public String getBuyer() {
        return buyer;
    }

    public void setBuyer(String buyer) {
        this.buyer = buyer;
    }

    public long getTimeCompleted() {
        return timeCompleted;
    }

    public void setTimeCompleted(long timeCompleted) {
        this.timeCompleted = timeCompleted;
    }
}
