package com.kiranhart.auctionhouse.auction;
/*
    The current file was created by Kiran Hart
    Date: August 03 2019
    Time: 8:11 PM
    
    Code within this class is not to be redistributed without proper permission.
*/

import com.kiranhart.auctionhouse.Core;
import com.kiranhart.auctionhouse.api.version.NBTEditor;
import com.kiranhart.auctionhouse.api.version.XMaterial;
import com.kiranhart.auctionhouse.util.tasks.SaveTransactionTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    private UUID buyer;
    private long timeCompleted;

    public Transaction(TransactionType transactionType, AuctionItem auctionItem, UUID buyer, long timeCompleted) {
        this.transactionType = transactionType;
        this.auctionItem = auctionItem;
        this.buyer = buyer;
        this.timeCompleted = timeCompleted;
    }


    public void saveTransaction() {
        SaveTransactionTask.startTask(Core.getInstance(), this);
    }

    public ItemStack getReceipt() {
        return new Receipt(this).getReceipt();
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

    public static List<ItemStack> getAllRecordedTransactions() {
        List<ItemStack> items = new ArrayList<>();
        if (getTotalTransactions() == 0) return items;
        Core.getInstance().getTransactions().getConfig().getConfigurationSection("transactions").getKeys(false).forEach(node -> items.add(getTransactionItem(node)));
        return items;
    }

    public static List<ItemStack> getAllRecordedTransactionsByPlayer(Player p) {
        List<ItemStack> items = new ArrayList<>();
        if (getTotalTransactions() == 0) return items;
        if (getTotalTransactionsByPlayer(p) == 0) return items;
        for (String node : Core.getInstance().getTransactions().getConfig().getConfigurationSection("transactions").getKeys(false)) {
            if (Core.getInstance().getTransactions().getConfig().getString("transactions." + node + ".seller").equalsIgnoreCase(p.getUniqueId().toString()) || Core.getInstance().getTransactions().getConfig().getString("transactions." + node + ".buyer").equalsIgnoreCase(p.getUniqueId().toString())) {
                items.add(getTransactionItem(node));
            }
        }
        return items;
    }

    private static ItemStack getTransactionItem(String node) {
        String[] stack = Core.getInstance().getConfig().getString("transaction.item").split(":");
        ItemStack item = XMaterial.matchXMaterial(stack[0].toUpperCase(), Byte.parseByte(stack[1])).get().parseItem();
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Core.getInstance().getConfig().getString("transaction.name").replace("{transaction_id}", Core.getInstance().getTransactions().getConfig().getString("transactions." + node + ".auction-id"))));
        List<String> lore = new ArrayList<>();
        Core.getInstance().getConfig().getStringList("transaction.lore").forEach(e -> lore.add(ChatColor.translateAlternateColorCodes('&', e.replace("{buyer}", Bukkit.getOfflinePlayer(UUID.fromString(Core.getInstance().getTransactions().getConfig().getString("transactions." + node + ".buyer"))).getName()).replace("{seller}", Bukkit.getOfflinePlayer(UUID.fromString(Core.getInstance().getTransactions().getConfig().getString("transactions." + node + ".seller"))).getName()))));
        meta.setLore(lore);
        item.setItemMeta(meta);
        item = NBTEditor.set(item, node, "AuctionTransactionID");
        return item;
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

    public UUID getBuyer() {
        return buyer;
    }

    public void setBuyer(UUID buyer) {
        this.buyer = buyer;
    }

    public long getTimeCompleted() {
        return timeCompleted;
    }

    public void setTimeCompleted(long timeCompleted) {
        this.timeCompleted = timeCompleted;
    }
}
