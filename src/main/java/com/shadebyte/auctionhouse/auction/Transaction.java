package com.shadebyte.auctionhouse.auction;

import com.shadebyte.auctionhouse.Core;

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
