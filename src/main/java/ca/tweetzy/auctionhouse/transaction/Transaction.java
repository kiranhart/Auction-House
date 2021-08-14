package ca.tweetzy.auctionhouse.transaction;

import ca.tweetzy.auctionhouse.auction.AuctionSaleType;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * The current file has been created by Kiran Hart
 * Date Created: March 19 2021
 * Time Created: 12:53 a.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */

@Getter
@Setter
public class Transaction {

    private final UUID id;
    private final UUID seller;
    private final UUID buyer;
    private final String sellerName;
    private final String buyerName;
    private final Long transactionTime;
    private final ItemStack item;
    private final AuctionSaleType auctionSaleType;
    private final double finalPrice;

    public Transaction(
            @NonNull UUID id,
            @NonNull UUID seller,
            @NonNull UUID buyer,
            @NonNull String sellerName,
            @NonNull String buyerName,
            long transactionTime,
            @NonNull ItemStack item,
            @NonNull AuctionSaleType auctionSaleType,
            double finalPrice
    ) {
        this.id = id;
        this.seller = seller;
        this.buyer = buyer;
        this.sellerName = sellerName;
        this.buyerName = buyerName;
        this.transactionTime = transactionTime;
        this.item = item;
        this.auctionSaleType = auctionSaleType;
        this.finalPrice = finalPrice;
    }
}
