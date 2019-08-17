package com.kiranhart.auctionhouse.util.tasks;
/*
    The current file was created by Kiran Hart
    Date: August 07 2019
    Time: 7:23 PM
    
    Code within this class is not to be redistributed without proper permission.
*/

import com.kiranhart.auctionhouse.Core;
import com.kiranhart.auctionhouse.api.statics.AuctionSettings;
import com.kiranhart.auctionhouse.auction.Transaction;
import com.kiranhart.auctionhouse.util.storage.Database;
import org.bukkit.scheduler.BukkitRunnable;

public class SaveTransactionTask extends BukkitRunnable {

    private static SaveTransactionTask instance;
    private static Core plugin;

    private Transaction transaction;

    private SaveTransactionTask(Core core, Transaction transaction) {
        plugin = core;
        this.transaction = transaction;
    }

    public static SaveTransactionTask startTask(Core core, Transaction transaction) {
        plugin = core;
        instance = new SaveTransactionTask(core, transaction);
        instance.runTask(core);
        return instance;
    }

    @Override
    public void run() {
        Core.getInstance().getTransactions().getConfig().set("transactions." + transaction.getTimeCompleted() + transaction.getAuctionItem().getKey() + ".transaction-type", transaction.getTransactionType().getTransactionType());
        Core.getInstance().getTransactions().getConfig().set("transactions." + transaction.getTimeCompleted() + transaction.getAuctionItem().getKey() + ".seller", transaction.getAuctionItem().getOwner().toString());
        Core.getInstance().getTransactions().getConfig().set("transactions." + transaction.getTimeCompleted() + transaction.getAuctionItem().getKey() + ".buyer", transaction.getBuyer().toString());
        Core.getInstance().getTransactions().getConfig().set("transactions." + transaction.getTimeCompleted() + transaction.getAuctionItem().getKey() + ".start-price", transaction.getAuctionItem().getStartPrice());
        Core.getInstance().getTransactions().getConfig().set("transactions." + transaction.getTimeCompleted() + transaction.getAuctionItem().getKey() + ".bid-increment", transaction.getAuctionItem().getBidIncrement());
        Core.getInstance().getTransactions().getConfig().set("transactions." + transaction.getTimeCompleted() + transaction.getAuctionItem().getKey() + ".current-price", transaction.getAuctionItem().getCurrentPrice());
        Core.getInstance().getTransactions().getConfig().set("transactions." + transaction.getTimeCompleted() + transaction.getAuctionItem().getKey() + ".buy-now-price", transaction.getAuctionItem().getBuyNowPrice());
        Core.getInstance().getTransactions().getConfig().set("transactions." + transaction.getTimeCompleted() + transaction.getAuctionItem().getKey() + ".time-left", transaction.getAuctionItem().getTime());
        Core.getInstance().getTransactions().getConfig().set("transactions." + transaction.getTimeCompleted() + transaction.getAuctionItem().getKey() + ".auction-id", transaction.getAuctionItem().getKey());
        Core.getInstance().getTransactions().getConfig().set("transactions." + transaction.getTimeCompleted() + transaction.getAuctionItem().getKey() + ".time-completed", transaction.getTimeCompleted());
        Core.getInstance().getTransactions().getConfig().set("transactions." + transaction.getTimeCompleted() + transaction.getAuctionItem().getKey() + ".item", transaction.getAuctionItem().getItem());
        Core.getInstance().getTransactions().getConfig().set("transactions." + transaction.getTimeCompleted() + transaction.getAuctionItem().getKey() + ".receipt", transaction.getReceipt());
        Core.getInstance().getTransactions().saveConfig();

        if (Core.getInstance().isDbConnected() && AuctionSettings.DB_ENABLED) {
            Database.getInstance().performTransactionUpload(transaction);
        }
    }
}
