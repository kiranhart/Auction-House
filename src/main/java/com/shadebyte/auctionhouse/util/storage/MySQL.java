package com.shadebyte.auctionhouse.util.storage;

import com.shadebyte.auctionhouse.Core;
import com.shadebyte.auctionhouse.api.AuctionAPI;
import com.shadebyte.auctionhouse.auction.Transaction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.sql.PreparedStatement;

/**
 * The current file has been created by Kiran Hart
 * Date Created: 7/23/2018
 * Time Created: 9:49 PM
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise.
 */
public class MySQL {

    public void logTransaction(Transaction transaction) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(Core.getInstance(), () -> {
            try {
                PreparedStatement insert = Core.getInstance().getConnection().prepareStatement("INSERT INTO transactions (buyer, seller, auctiontype, startprice, buynowprice, increment, item, displayname, lore, enchantments, auctionid, timesold, finalprice) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)");
                insert.setString(1, transaction.getBuyer());
                insert.setString(2, transaction.getAuctionItem().getOwner());
                insert.setString(3, transaction.getTransactionType().getTransactionType());
                insert.setInt(4, transaction.getAuctionItem().getStartPrice());
                insert.setInt(5, transaction.getAuctionItem().getBuyNowPrice());
                insert.setInt(6, transaction.getAuctionItem().getBidIncrement());
                insert.setString(7, transaction.getAuctionItem().getItem().getTypeId() + "-" + transaction.getAuctionItem().getItem().getDurability());

                insert.setString(8, AuctionAPI.getInstance().getSQLDisplayName(transaction.getAuctionItem().getItem()));
                insert.setString(9, AuctionAPI.getInstance().getSQLLore(transaction.getAuctionItem().getItem()));
                insert.setString(10, AuctionAPI.getInstance().getSQLEnchantments(transaction.getAuctionItem().getItem()));

                insert.setString(11, transaction.getAuctionItem().getKey());
                insert.setString(12, AuctionAPI.getInstance().getDate(System.currentTimeMillis()));
                insert.setInt(13, (transaction.getTransactionType() == Transaction.TransactionType.BOUGHT) ? transaction.getAuctionItem().getBuyNowPrice() : transaction.getAuctionItem().getCurrentPrice());
                insert.executeUpdate();
                Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&aRecorded transaction id: &b" + transaction.getAuctionItem().getKey() + "&a to database."));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
