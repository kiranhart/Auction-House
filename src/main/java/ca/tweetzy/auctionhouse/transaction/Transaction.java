package ca.tweetzy.auctionhouse.transaction;

import ca.tweetzy.auctionhouse.auction.AuctionItem;
import ca.tweetzy.auctionhouse.auction.AuctionSaleType;

import java.io.Serializable;
import java.util.UUID;

/**
 * The current file has been created by Kiran Hart
 * Date Created: March 19 2021
 * Time Created: 12:53 a.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class Transaction implements Serializable {

    private final UUID id;
    private final UUID seller;
    private final UUID buyer;
    private final long transactionTime;
    private final AuctionItem auctionItem;
    private final AuctionSaleType auctionSaleType;

    public Transaction(UUID id, UUID seller, UUID buyer, long transactionTime, AuctionItem auctionItem, AuctionSaleType auctionSaleType) {
        this.id = id;
        this.seller = seller;
        this.buyer = buyer;
        this.transactionTime = transactionTime;
        this.auctionItem = auctionItem;
        this.auctionSaleType = auctionSaleType;
    }
}
