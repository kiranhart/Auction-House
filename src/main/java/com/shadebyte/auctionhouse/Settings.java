package com.shadebyte.auctionhouse;

import com.shadebyte.auctionhouse.api.enums.Lang;

/**
 * The current file has been created by Kiran Hart
 * Date Created: 7/6/2018
 * Time Created: 6:46 PM
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise.
 */
public class Settings {

    private String prefix;

    public Settings() {
        prefix = Core.getInstance().getLocale().getMessage(Lang.PREFIX.getNode()) + " ";
    }

    public String getPrefix() {
        return prefix;
    }
}
