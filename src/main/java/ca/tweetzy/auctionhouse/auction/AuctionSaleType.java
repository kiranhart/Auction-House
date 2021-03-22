package ca.tweetzy.auctionhouse.auction;

/**
 * The current file has been created by Kiran Hart
 * Date Created: January 17 2021
 * Time Created: 5:41 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public enum AuctionSaleType {

    USED_BIDDING_SYSTEM("Biddable"),
    WITHOUT_BIDDING_SYSTEM("Not Biddable"),
    // Didn't feel like making an entirely new enumeration, so BOTH is really only used in the auction filtering
    BOTH("All");

    private final String type;

    AuctionSaleType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
    public AuctionSaleType next() {
        return values()[(this.ordinal() + 1) % values().length];
    }
}
