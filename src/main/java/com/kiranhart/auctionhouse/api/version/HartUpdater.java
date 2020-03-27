package com.kiranhart.auctionhouse.api.version;

import com.kiranhart.auctionhouse.api.statics.AuctionPermissions;
import com.kiranhart.auctionhouse.util.Debugger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
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

    public HartUpdater(JavaPlugin plugin, String v) {

        try {
            String url = "https://api.spigotmc.org/legacy/update.php?resource=75600";

            HttpURLConnection httpClient = (HttpURLConnection) new URL(url).openConnection();


            // optional default is GET
            httpClient.setRequestMethod("GET");

            //add request header
            httpClient.setRequestProperty("User-Agent", "Mozilla/5.0");

            int responseCode = httpClient.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            try (BufferedReader in = new BufferedReader(new InputStreamReader(httpClient.getInputStream()))) {

                StringBuilder response = new StringBuilder();
                String line;

                while ((line = in.readLine()) != null) {
                    response.append(line);
                }

                if (!v.equalsIgnoreCase(response.toString())) {
                    Bukkit.getConsoleSender().sendMessage(translateAlternateColorCodes('&', "&b======================="));
                    Bukkit.getConsoleSender().sendMessage(translateAlternateColorCodes('&', "&e" + plugin.getDescription().getName() + " Updater"));
                    Bukkit.getConsoleSender().sendMessage(translateAlternateColorCodes('&', "&b-----------------------"));
                    Bukkit.getConsoleSender().sendMessage(translateAlternateColorCodes('&', "&fLatest Version: &6" + response.toString()));
                    Bukkit.getConsoleSender().sendMessage(translateAlternateColorCodes('&', "&fCurrent Version: &6" + v));
                    Bukkit.getConsoleSender().sendMessage(translateAlternateColorCodes('&', "&cPlease update the plugin."));
                    Bukkit.getConsoleSender().sendMessage(translateAlternateColorCodes('&', "&b-----------------------"));

                    Bukkit.getOnlinePlayers().forEach(p -> {
                        if (p.isOp() || p.hasPermission(AuctionPermissions.ADMIN)) {
                            p.sendMessage(translateAlternateColorCodes('&', "&b======================="));
                            Bukkit.getConsoleSender().sendMessage(translateAlternateColorCodes('&', "&e" + plugin.getDescription().getName() + " Updater"));
                            p.sendMessage(translateAlternateColorCodes('&', "&b-----------------------"));
                            p.sendMessage(translateAlternateColorCodes('&', "&fLatest Version: &6" + response.toString()));
                            p.sendMessage(translateAlternateColorCodes('&', "&fCurrent Version: &6" + v));
                            p.sendMessage(translateAlternateColorCodes('&', "&cPlease update the plugin."));
                            p.sendMessage(translateAlternateColorCodes('&', "&b-----------------------"));
                        }
                    });
                } else {
                    Bukkit.getConsoleSender().sendMessage(translateAlternateColorCodes('&', "&b======================="));
                    Bukkit.getConsoleSender().sendMessage(translateAlternateColorCodes('&', "&e" + plugin.getDescription().getName() + " is update to date"));
                    Bukkit.getConsoleSender().sendMessage(translateAlternateColorCodes('&', "&b-----------------------"));
                }
            }
        } catch (Exception e) {
            Debugger.report(e, false);
        }
    }
}

class Plugin {
    String version;
}
