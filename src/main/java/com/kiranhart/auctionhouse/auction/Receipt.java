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

public class Receipt {

    private UUID seller;
    private UUID buyer;
    private String date;
    private String time;

    private long total;
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
        ItemStack stack = XMaterial.matchXMaterial(item[0].toUpperCase(), Byte.parseByte(item[1])).get().parseItem();
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Core.getInstance().getConfig().getString("receipt.name")));
        List<String> lore = Core.getInstance().getConfig().getStringList("receipt.lore").stream().map(node -> ChatColor.translateAlternateColorCodes('&', node.replace("{time}", time).replace("{date}", date).replace("{price}", NumberFormat.getInstance().format(total)).replace("{seller}", Bukkit.getOfflinePlayer(seller).getName()))).collect(Collectors.toList());
        meta.setLore(lore);
        stack.setItemMeta(meta);
        stack = NBTEditor.set(stack, transaction.getTimeCompleted() + transaction.getAuctionItem().getKey(), "AuctionReceipt");
        return stack;
    }

    public long getTotal() {
        return total;
    }

    public UUID getSeller() {
        return seller;
    }

    public UUID getBuyer() {
        return buyer;
    }

    public String getDate() {
        return date;
    }
}
