package com.kiranhart.auctionhouse.api.events;

import com.kiranhart.auctionhouse.auction.Transaction;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * The current file has been created by Kiran Hart
 * Date Created: 7/13/2018
 * Time Created: 11:44 AM
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise.
 */
public class TransactionCompleteEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Transaction transaction;

    public TransactionCompleteEvent(Transaction transaction) {
        this.transaction = transaction;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setAuctionItem(Transaction transaction) {
        this.transaction = transaction;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
