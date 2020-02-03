package com.kiranhart.auctionhouse.api.version;

import com.google.gson.Gson;
import com.kiranhart.auctionhouse.api.statics.AuctionPermissions;
import com.kiranhart.auctionhouse.util.Debugger;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.bukkit.ChatColor.translateAlternateColorCodes;

/**
 * The current file has been created by Kiran Hart
 * Date Created: 1/17/2020
 * Time Created: 2:31 PM
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise.
 */
public class HartUpdater {

    private String VERSION = "1.12";

    public HartUpdater() {
        try {
            URL url = new URL("https://api.spigotmc.org/legacy/update.php?resource=60325");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            BufferedReader json = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            Plugin plugin = new Gson().fromJson(json, Plugin.class);

            if (!VERSION.equalsIgnoreCase(plugin.version)) {
                Bukkit.getConsoleSender().sendMessage(translateAlternateColorCodes('&', "&b======================="));
                Bukkit.getConsoleSender().sendMessage(translateAlternateColorCodes('&', "&eAuction House Updater"));
                Bukkit.getConsoleSender().sendMessage(translateAlternateColorCodes('&', "&b-----------------------"));
                Bukkit.getConsoleSender().sendMessage(translateAlternateColorCodes('&', "&fLatest Version: &6" + plugin.version));
                Bukkit.getConsoleSender().sendMessage(translateAlternateColorCodes('&', "&fCurrent Version: &6" + this.VERSION));
                Bukkit.getConsoleSender().sendMessage(translateAlternateColorCodes('&', "&cPlease update the plugin."));
                Bukkit.getConsoleSender().sendMessage(translateAlternateColorCodes('&', "&b-----------------------"));
            
                Bukkit.getOnlinePlayers().forEach(p -> {
                    if (p.isOp() || p.hasPermission(AuctionPermissions.ADMIN)) {
                        p.sendMessage(translateAlternateColorCodes('&', "&b======================="));
                        p.sendMessage(translateAlternateColorCodes('&', "&eAuction House Updater"));
                        p.sendMessage(translateAlternateColorCodes('&', "&b-----------------------"));
                        p.sendMessage(translateAlternateColorCodes('&', "&fLatest Version: &6" + plugin.version));
                        p.sendMessage(translateAlternateColorCodes('&', "&fCurrent Version: &6" + this.VERSION));
                        p.sendMessage(translateAlternateColorCodes('&', "&cPlease update the plugin."));
                        p.sendMessage(translateAlternateColorCodes('&', "&b-----------------------"));
                    }
                });
            }

            connection.disconnect();

        } catch (IOException e) {
            Debugger.report(e, false);
        }
    }
}

class Plugin {
    String version;
}
