package com.kiranhart.auctionhouse.util;
/*
    The current file was created by Kiran Hart
    Date: August 02 2019
    Time: 8:19 PM
    
    Code within this class is not to be redistributed without proper permission.
*/

import com.kiranhart.auctionhouse.Core;
import org.bukkit.Bukkit;

import static org.bukkit.ChatColor.translateAlternateColorCodes;

public class Debugger {

    private static boolean isDebuggerEnabled() {
        return Core.getInstance().getConfig().getBoolean("debugger");
    }

    public static void report(Exception e) {
        if (isDebuggerEnabled()) {
            Bukkit.getConsoleSender().sendMessage(translateAlternateColorCodes('&', "&b================================================================"));
            Bukkit.getConsoleSender().sendMessage(translateAlternateColorCodes('&', "&eAuction House has ran into an error, report this to the author."));
            Bukkit.getConsoleSender().sendMessage(translateAlternateColorCodes('&', "&b----------------------------------------------------------------"));
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(translateAlternateColorCodes('&', "&b================================================================"));
        }
    }

    public static void report(Exception e, boolean show) {
        if (show) report(e);
    }
}
