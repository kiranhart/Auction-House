package com.kiranhart.auctionhouse.util.storage;
/*
    The current file was created by Kiran Hart
    Date: August 12 2019
    Time: 4:17 PM
    
    Code within this class is not to be redistributed without proper permission.
*/

import com.kiranhart.auctionhouse.Core;
import com.kiranhart.auctionhouse.api.AuctionAPI;
import com.kiranhart.auctionhouse.api.version.XMaterial;
import com.kiranhart.auctionhouse.auction.Transaction;
import com.kiranhart.auctionhouse.util.Debugger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class Database {

    private static Database instance;

    private Database() {
    }

    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    public enum Tables {

        TRANSACTIONS("transactions"),
        TRANSACTIONS_HART_DEV("pl_auctionhouse_transactions"),
        ;

        private String tableName;

        Tables(String tableName) {
            this.tableName = tableName;
        }

        public String getTableName() {
            return tableName;
        }
    }

    public boolean performTableCreation(Tables... tables) {
        try {
            //Loop through each of the passed in tables
            for (Tables table : tables) {

                //Check if the table is TRANSACTIONS
                if (table == Tables.TRANSACTIONS) {
                    //Perform the table creation;
                    String query = "CREATE TABLE IF NOT EXISTS `" + table.getTableName() + "` ( `id` INT NOT NULL AUTO_INCREMENT , `transaction_type` TEXT NOT NULL , `seller` TEXT NOT NULL , `buyer` TEXT NOT NULL , `start_price` TEXT NOT NULL , `bid_increment` TEXT NOT NULL , `buy_now_price` TEXT NOT NULL , `final_price` TEXT NOT NULL , `time_left` INT NOT NULL , `auction_id` TEXT NOT NULL , `time_completed` TEXT NOT NULL , `item_type` TEXT NOT NULL , `item_name` TEXT NOT NULL , `item_lore` TEXT NOT NULL , `item_enchants` TEXT NOT NULL , PRIMARY KEY (`id`)) ENGINE = InnoDB";
                    Connection connection = Core.getInstance().getHikari().getConnection();
                    PreparedStatement statement = connection.prepareStatement(query);
                    if (statement.execute())
                        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&eAuctionHouse&8] &aCreated `transactions` table successfully!"));
                    else
                        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&eAuctionHouse&8] &cFailed to create `transactions` table!"));
                }
            }
        } catch (Exception e) {
            Debugger.report(e);
            return false;
        }
        return true;
    }

    public void performTransactionUpload(Transaction... transactions) {
        try {

            Connection connection = Core.getInstance().getHikari().getConnection();

            for (Transaction transaction : transactions) {
                PreparedStatement insert = connection.prepareStatement("INSERT INTO " + Tables.TRANSACTIONS.getTableName() + " (transaction_type, seller, buyer, start_price, bid_increment, buy_now_price, final_price, time_left, auction_id, time_completed, item_type, item_name, item_lore, item_enchants) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                insert.setString(1, transaction.getTransactionType().getTransactionType());
                insert.setString(2, transaction.getAuctionItem().getOwner().toString());
                insert.setString(3, transaction.getBuyer().toString());
                insert.setString(4, String.valueOf(transaction.getAuctionItem().getStartPrice()));
                insert.setString(5, String.valueOf(transaction.getAuctionItem().getBidIncrement()));
                insert.setString(6, String.valueOf(transaction.getAuctionItem().getBuyNowPrice()));
                insert.setString(7, (transaction.getTransactionType() == Transaction.TransactionType.BOUGHT) ? String.valueOf(transaction.getAuctionItem().getBuyNowPrice()) : String.valueOf(transaction.getAuctionItem().getCurrentPrice()));
                insert.setInt(8, transaction.getAuctionItem().getTime());
                insert.setString(9, String.valueOf(transaction.getAuctionItem().getKey()));
                insert.setString(10, String.valueOf(transaction.getTimeCompleted()));
                insert.setString(11, (XMaterial.matchXMaterial(transaction.getAuctionItem().getItem()) == null) ? transaction.getAuctionItem().getDisplayName() : XMaterial.matchXMaterial(transaction.getAuctionItem().getItem()).toString() + "===" + transaction.getAuctionItem().getItem().getType().name() + ":" + transaction.getAuctionItem().getItem().getDurability());
                insert.setString(12, ChatColor.stripColor(transaction.getAuctionItem().getDisplayName()));
                insert.setString(13, AuctionAPI.getInstance().getMySQLLore(transaction.getAuctionItem().getItem()));
                insert.setString(14, AuctionAPI.getInstance().getMySQLEnchantments(transaction.getAuctionItem().getItem()));
                insert.executeUpdate();
                Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&eAuctionHouse&8] &aRecorded transaction id: &b" + transaction.getAuctionItem().getKey() + "&a to database."));
            }
        } catch (Exception e) {
            Debugger.report(e);
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&eAuctionHouse&8] &cCould not save the transaction to the database, saved to transactions.yml"));
        }
    }
}
