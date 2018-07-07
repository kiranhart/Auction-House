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
