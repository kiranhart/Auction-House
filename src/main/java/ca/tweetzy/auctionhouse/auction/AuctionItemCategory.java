package ca.tweetzy.auctionhouse.auction;

/**
 * The current file has been created by Kiran Hart
 * Date Created: February 01 2021
 * Time Created: 6:52 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public enum AuctionItemCategory {

    FOOD("Food"),
    ARMOR("Armor"),
    BLOCKS("Blocks"),
    TOOLS("Tools"),
    MISC("Misc");


    private String type;

    AuctionItemCategory(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
