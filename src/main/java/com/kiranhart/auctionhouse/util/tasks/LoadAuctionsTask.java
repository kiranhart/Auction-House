package com.kiranhart.auctionhouse.util.tasks;
/*
    The current file was created by Kiran Hart
    Date: August 07 2019
    Time: 7:35 PM
    
    Code within this class is not to be redistributed without proper permission.
*/

import com.kiranhart.auctionhouse.Core;
import com.kiranhart.auctionhouse.api.events.AuctionStartEvent;
import com.kiranhart.auctionhouse.auction.AuctionItem;
import com.kiranhart.auctionhouse.util.Debugger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class LoadAuctionsTask extends BukkitRunnable {

    private static LoadAuctionsTask instance;
    private static Core plugin;

    private LoadAuctionsTask(Core core) {
        plugin = core;
    }

    public static LoadAuctionsTask startTask(Core core) {
        plugin = core;
        if (instance == null) {
            instance = new LoadAuctionsTask(plugin);
            instance.runTask(plugin);
        }
        return instance;
    }

    @Override
    public void run() {
        try {
            //get the configuration section (active)
            ConfigurationSection section = Core.getInstance().getData().getConfig().getConfigurationSection("active");
            //Is there data under the active node?
            if (section.getKeys(false).size() != 0) {
                //Get total size
                int size = section.getKeys(false).size();
                //begin looping through nodes
                for (String node : section.getKeys(false)) {
                    int xNode = Integer.parseInt(node);
                    //gather all of the found data
                    UUID owner = UUID.fromString(Core.getInstance().getData().getConfig().getString("active." + xNode + ".owner"));
                    ItemStack stack = Core.getInstance().getData().getConfig().getItemStack("active." + xNode + ".item");
                    int startPrice = Core.getInstance().getData().getConfig().getInt("active." + xNode + ".startprice");
                    int bidIncrement = Core.getInstance().getData().getConfig().getInt("active." + xNode + ".bidincrement");
                    int buyNowPrice = Core.getInstance().getData().getConfig().getInt("active." + xNode + ".buynowprice");
                    int currentPrice = Core.getInstance().getData().getConfig().getInt("active." + xNode + ".currentprice");
                    int time = Core.getInstance().getData().getConfig().getInt("active." + xNode + ".time");
                    String key = Core.getInstance().getData().getConfig().getString("active." + xNode + ".key");
                    UUID highestBidder = UUID.fromString(Core.getInstance().getData().getConfig().getString("active." + xNode + ".highestbidder"));

                    AuctionItem auctionItem = new AuctionItem(owner, highestBidder, stack, startPrice, bidIncrement, buyNowPrice, currentPrice, time, key);
                    Core.getInstance().getAuctionItems().add(auctionItem);
                    Core.getInstance().getData().getConfig().set("active." + xNode, null);
                    AuctionStartEvent auctionStartEvent = new AuctionStartEvent(auctionItem);
                    Bukkit.getPluginManager().callEvent(auctionStartEvent);
                    Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&eLoaded Auction Item with key&f: &b" + auctionItem.getKey()));
                }
                Core.getInstance().getData().saveConfig();
                Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&eLoaded a total of &f: &b" + size + "&e items"));
            }
        } catch (Exception e) {
            Debugger.report(e);
        }
    }
}
