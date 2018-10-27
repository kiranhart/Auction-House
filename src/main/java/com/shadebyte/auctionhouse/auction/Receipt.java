package com.shadebyte.auctionhouse.auction;

import com.shadebyte.auctionhouse.Core;
import com.shadebyte.auctionhouse.util.NBTEditor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: 7/25/2018
 * Time Created: 11:32 PM
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise.
 */
public class Receipt {

    private long total;
    private String seller, buyer, date, time;
    private Transaction transaction;

    public Receipt(Transaction transaction) {
        this.transaction = transaction;
        this.total = (transaction.getTransactionType() == Transaction.TransactionType.BOUGHT) ? transaction.getAuctionItem().getBuyNowPrice() : transaction.getAuctionItem().getCurrentPrice();
        this.seller = transaction.getAuctionItem().getOwner();
        this.buyer = transaction.getBuyer();
        this.date = new SimpleDateFormat("MMMM dd yyyy").format(new Date(transaction.getTimeCompleted()));
        this.time = new SimpleDateFormat("h:mm a").format(new Date());
    }

    public ItemStack getReceipt() {
        String[] item = Core.getInstance().getConfig().getString("receipt.item").split(":");
        ItemStack stack = new ItemStack(Material.valueOf(item[0].toUpperCase()), 1, Short.parseShort(item[1]));
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Core.getInstance().getConfig().getString("receipt.name")));
        List<String> lore = Core.getInstance().getConfig().getStringList("receipt.lore").stream().map(node -> ChatColor.translateAlternateColorCodes('&', node.replace("{time}", time).replace("{date}", date).replace("{price}", NumberFormat.getInstance().format(total)).replace("{seller}", Bukkit.getOfflinePlayer(UUID.fromString(seller)).getName()))).collect(Collectors.toList());
        meta.setLore(lore);
        stack.setItemMeta(meta);
        stack = NBTEditor.setItemTag(stack, transaction.getTimeCompleted() + transaction.getAuctionItem().getKey(), "AuctionReceipt");
        return stack;
    }

    public long getTotal() {
        return total;
    }

    public String getSeller() {
        return seller;
    }

    public String getBuyer() {
        return buyer;
    }

    public String getDate() {
        return date;
    }
}
