package ca.tweetzy.auctionhouse.auction;

import ca.tweetzy.auctionhouse.AuctionHouse;

/**
 * The current file has been created by Kiran Hart
 * Date Created: February 01 2021
 * Time Created: 6:52 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public enum AuctionItemCategory {

    ALL("All"),
    FOOD("Food"),
    ARMOR("Armor"),
    BLOCKS("Blocks"),
    TOOLS("Tools"),
    MISC("Misc");


    private final String type;

    AuctionItemCategory(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String getTranslatedType() {
        switch (this) {
            case ALL:
                return AuctionHouse.getInstance().getLocale().getMessage("auction_filter.categories.all").getMessage();
            case FOOD:
                return AuctionHouse.getInstance().getLocale().getMessage("auction_filter.categories.food").getMessage();
            case ARMOR:
                return AuctionHouse.getInstance().getLocale().getMessage("auction_filter.categories.armor").getMessage();
            case BLOCKS:
                return AuctionHouse.getInstance().getLocale().getMessage("auction_filter.categories.blocks").getMessage();
            case TOOLS:
                return AuctionHouse.getInstance().getLocale().getMessage("auction_filter.categories.tools").getMessage();
            case MISC:
                return AuctionHouse.getInstance().getLocale().getMessage("auction_filter.categories.misc").getMessage();
        }
        return getType();
    }

    public AuctionItemCategory next() {
        return values()[(this.ordinal() + 1) % values().length];
    }
}
