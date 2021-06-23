package ca.tweetzy.auctionhouse.auction;

import ca.tweetzy.auctionhouse.AuctionHouse;

/**
 * The current file has been created by Kiran Hart
 * Date Created: February 01 2021
 * Time Created: 6:52 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public enum AuctionItemCategory {

    ALL("All", false),
    FOOD("Food", true),
    ARMOR("Armor", true),
    BLOCKS("Blocks", true),
    TOOLS("Tools", true),
    WEAPONS("Weapons", true),
    SPAWNERS("Spawners", true),
    ENCHANTS("Enchants", true),
    MISC("Misc", true),
    SEARCH("Search", false),
    SELF("Self", false);


    private final String type;
    private final boolean whitelistAllowed;

    AuctionItemCategory(String type, boolean whitelistAllowed) {
        this.type = type;
        this.whitelistAllowed = whitelistAllowed;
    }

    public String getType() {
        return type;
    }

    public boolean isWhitelistAllowed() {
        return whitelistAllowed;
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
            case ENCHANTS:
                return AuctionHouse.getInstance().getLocale().getMessage("auction_filter.categories.enchants").getMessage();
            case SPAWNERS:
                return AuctionHouse.getInstance().getLocale().getMessage("auction_filter.categories.spawners").getMessage();
            case WEAPONS:
                return AuctionHouse.getInstance().getLocale().getMessage("auction_filter.categories.weapons").getMessage();
            case SELF:
                return AuctionHouse.getInstance().getLocale().getMessage("auction_filter.categories.self").getMessage();
            case SEARCH:
                return AuctionHouse.getInstance().getLocale().getMessage("auction_filter.categories.search").getMessage();
        }
        return getType();
    }

    public AuctionItemCategory next() {
        return values()[(this.ordinal() + 1) % values().length];
    }
}
