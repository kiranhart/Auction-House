package com.shadebyte.auctionhouse.util;

import com.shadebyte.auctionhouse.Core;
import org.bukkit.Bukkit;

import static org.bukkit.ChatColor.translateAlternateColorCodes;

/**
 * The current file has been created by Kiran Hart
 * Date Created: 7/6/2018
 * Time Created: 11:59 AM
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise.
 */
public class Debugger {

    public static void report(Exception e) {
        if (debugEnabled()) {
            Bukkit.getConsoleSender().sendMessage(translateAlternateColorCodes('&', "&b================================================================"));
            Bukkit.getConsoleSender().sendMessage(translateAlternateColorCodes('&', "&eAuction House has ran into an error, report this to the author."));
            Bukkit.getConsoleSender().sendMessage(translateAlternateColorCodes('&', "&b----------------------------------------------------------------"));
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(translateAlternateColorCodes('&', "&b================================================================"));
        }
    }

    private static boolean debugEnabled() {
        return Core.getInstance().getConfig().getBoolean("debugger");
    }
}
