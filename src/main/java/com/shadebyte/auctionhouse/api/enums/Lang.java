package com.shadebyte.auctionhouse.api.enums;

/**
 * The current file has been created by Kiran Hart
 * Date Created: 7/6/2018
 * Time Created: 11:52 AM
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise.
 */
public enum Lang {

    PREFIX("prefix"),
    NO_PERMISSION("nopermission"),
    PLAYERS_ONLY("playersonly"),
    NOT_A_NUMBER("notanumber"),
    NOT_ENOUGH_MONEY("notenoughmoney"),
    CANNOT_BUY_OWN("cantbuyown"),
    CANNOT_BID_OWN("cantbidonown"),
    AIR("air"),
    INVALID_TRANSACTION("invalidtransaction"),
    BLOCKED_ITEM("blockeditem"),

    AUCTION_LISTED("auction.listed"),
    AUCTION_BUY("auction.buy"),
    AUCTION_MAX("auction.max"),
    AUCTION_SOLD("auction.sold"),
    GOT_OUT_BID("outbid"),

    MAX_START_PRICE("price.max.start"),
    MAX_AUCTION_PRICE("price.max.auction"),
    MAX_INCREMENT_PRICE("price.max.increment"),

    MIN_START_PRICE("price.min.start"),
    MIN_AUCTION_PRICE("price.min.auction"),
    MIN_INCREMENT_PRICE("price.min.increment"),

    INVALID_SUBCOMMAND("cmd.invalid"),
    CMD_SELL("cmd.sell"),

    ;

    String node;

    Lang(String node) {
        this.node = node;
    }

    public String getNode() {
        return node;
    }
}
