package com.kiranhart.auctionhouse.util.tasks;
/*
    The current file was created by Kiran Hart
    Date: August 07 2019
    Time: 7:35 PM
    
    Code within this class is not to be redistributed without proper permission.
*/

import com.kiranhart.auctionhouse.Core;
import com.kiranhart.auctionhouse.api.statics.AuctionSettings;
import com.kiranhart.auctionhouse.auction.AuctionItem;
import com.kiranhart.auctionhouse.util.Debugger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

public class AutoSaveTask extends BukkitRunnable {

    private static AutoSaveTask instance;
    private static Core plugin;

    private AutoSaveTask(Core core) {
        plugin = core;
    }

    public static AutoSaveTask startTask(Core core) {
        plugin = core;
        if (instance == null) {
            instance = new AutoSaveTask(plugin);
            instance.runTaskTimer(plugin, 0, 20 * AuctionSettings.AUTO_SAVE_EVERY);
        }
        return instance;
    }

    @Override
    public void run() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&b============================"));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&aGETTING READY TO PERFORM SAVE"));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&b============================"));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', " "));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', " "));
        long autoSaveStart = System.currentTimeMillis();
        clearFolder();
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&e---------------------------"));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cFinished clearing data.yml"));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&e---------------------------"));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', " "));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', " "));
        saveAuctions();
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&e-------------------------------"));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cFinished saving active auctions"));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&e-------------------------------"));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', " "));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', " "));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&eAuction Saving took: " + (System.currentTimeMillis() - autoSaveStart) + "ms"));
    }

    private void saveAuctions() {
        //Save Auctions to file.
        int node = 1;
        for (AuctionItem auctionItem : Core.getInstance().getAuctionItems()) {
            Core.getInstance().getData().getConfig().set("active." + node + ".owner", auctionItem.getOwner());
            Core.getInstance().getData().getConfig().set("active." + node + ".highestbidder", auctionItem.getHighestBidder());
            Core.getInstance().getData().getConfig().set("active." + node + ".startprice", auctionItem.getStartPrice());
            Core.getInstance().getData().getConfig().set("active." + node + ".bidincrement", auctionItem.getBidIncrement());
            Core.getInstance().getData().getConfig().set("active." + node + ".currentprice", auctionItem.getCurrentPrice());
            Core.getInstance().getData().getConfig().set("active." + node + ".buynowprice", auctionItem.getBuyNowPrice());
            Core.getInstance().getData().getConfig().set("active." + node + ".key", auctionItem.getKey());
            Core.getInstance().getData().getConfig().set("active." + node + ".time", auctionItem.getTime());
            Core.getInstance().getData().getConfig().set("active." + node + ".item", auctionItem.getItem());
            node++;
        }
        Core.getInstance().getData().saveConfig();
    }

    private void clearFolder() {
        try {
            ConfigurationSection section = Core.getInstance().getData().getConfig().getConfigurationSection("active");
            if (section.getKeys(false).size() != 0) {
                for (String node : section.getKeys(false)) {
                    int xNode = Integer.parseInt(node);
                    Core.getInstance().getData().getConfig().set("active." + xNode, null);
                }
                Core.getInstance().getData().saveConfig();
            }
        } catch (Exception e) {
            Debugger.report(e);
        }
    }
}
