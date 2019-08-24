package com.kiranhart.auctionhouse.util.tasks;
/*
    The current file was created by Kiran Hart
    Date: August 08 2019
    Time: 6:19 PM
    
    Code within this class is not to be redistributed without proper permission.
*/

import com.google.common.collect.Lists;
import com.kiranhart.auctionhouse.Core;
import com.kiranhart.auctionhouse.api.AuctionAPI;
import com.kiranhart.auctionhouse.api.events.AuctionEndEvent;
import com.kiranhart.auctionhouse.api.events.TransactionCompleteEvent;
import com.kiranhart.auctionhouse.api.statics.AuctionLang;
import com.kiranhart.auctionhouse.api.statics.AuctionSettings;
import com.kiranhart.auctionhouse.auction.AuctionItem;
import com.kiranhart.auctionhouse.auction.AuctionPlayer;
import com.kiranhart.auctionhouse.auction.Transaction;
import com.kiranhart.auctionhouse.util.Debugger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class TickAuctionsTask extends BukkitRunnable {

    private static TickAuctionsTask instance;
    private static Core plugin;

    private TickAuctionsTask(Core core) {
        plugin = core;
    }

    public static TickAuctionsTask startTask(Core core) {
        plugin = core;
        if (instance == null) {
            instance = new TickAuctionsTask(plugin);
            instance.runTaskTimer(plugin, 0, 20 * AuctionSettings.UPDATE_EVERY_TICK);
        }
        return instance;
    }

    @Override
    public void run() {

        try {

            //Check if auction items is not empty
            if (Core.getInstance().getAuctionItems().size() != 0) {
                //Tick the auction item
                Core.getInstance().getAuctionItems().forEach(auctionItem -> auctionItem.updateTime(AuctionSettings.DECREASE_SECONDS_BY_TICK));
                //Loop through each of the auction items
                Core.getInstance().getAuctionItems().forEach(auctionItem -> {
                    //Check if the auction time is equal or 0.
                    if (auctionItem.getTime() <= 0) {
                        //Call the AuctionEndEvent
                        AuctionEndEvent auctionEndEvent = new AuctionEndEvent(auctionItem);
                        Core.getInstance().getPm().callEvent(auctionEndEvent);

                        //Check if the auction is not cancelled
                        if (!auctionEndEvent.isCancelled()) {
                            //Check if the highest bidder is equal to the owner, if so send to expiration box
                            if (auctionItem.getHighestBidder().equals(auctionItem.getOwner())) {
                                //Save to expiration bin
                                Core.getInstance().getData().getConfig().set("expired." + auctionItem.getOwner().toString() + "." + auctionItem.getKey() + ".item", auctionItem.getItem());
                                Core.getInstance().getData().getConfig().set("expired." + auctionItem.getOwner().toString() + "." + auctionItem.getKey() + ".display", AuctionAPI.getInstance().expiredAuctionItem(auctionItem));
                                Core.getInstance().getData().saveConfig();
                                Core.getInstance().getAuctionItems().remove(auctionItem);
                            } else {
                                //Highest bidder is not the owner, perform checks
                                OfflinePlayer highestBidder = Bukkit.getOfflinePlayer(auctionItem.getHighestBidder());
                                if (highestBidder.isOnline()) {
                                    //Highest bidder is currently online
                                    //Check the balance (has enough money)
                                    if (Core.getInstance().getEconomy().hasBalance(highestBidder, auctionItem.getCurrentPrice())) {
                                        //Withdraw the money
                                        Core.getInstance().getEconomy().withdrawBalance(highestBidder, auctionItem.getCurrentPrice());
                                        //Give money the owner
                                        Core.getInstance().getEconomy().deposit(Bukkit.getOfflinePlayer(auctionItem.getOwner()), auctionItem.getCurrentPrice());
                                        //TODO Send money removal / addition messages

                                        //Check if player has available slots
                                        if (AuctionAPI.getInstance().availableSlots(highestBidder.getPlayer().getInventory()) == 0) {
                                            Core.getInstance().getData().getConfig().set("expired." + auctionItem.getHighestBidder().toString() + "." + auctionItem.getKey() + ".item", auctionItem.getItem());
                                            Core.getInstance().getData().getConfig().set("expired." + auctionItem.getHighestBidder().toString() + "." + auctionItem.getKey() + ".display", AuctionAPI.getInstance().expiredAuctionItem(auctionItem));
                                        } else {
                                            highestBidder.getPlayer().getInventory().addItem(auctionItem.getItem());
                                        }

                                        //Perform the transaction
                                        Transaction transaction = new Transaction(Transaction.TransactionType.AUCTION_WON, auctionItem, highestBidder.getPlayer().getUniqueId(), System.currentTimeMillis());
                                        transaction.saveTransaction();
                                        Core.getInstance().getPm().callEvent(new TransactionCompleteEvent(transaction));
                                    } else {
                                        //Doesn't have enough money
                                        Core.getInstance().getLocale().getMessage(AuctionLang.NOT_ENOUGH_MONEY).sendPrefixedMessage(highestBidder.getPlayer());
                                        Core.getInstance().getData().getConfig().set("expired." + auctionItem.getOwner().toString() + "." + auctionItem.getKey() + ".item", auctionItem.getItem());
                                        Core.getInstance().getData().getConfig().set("expired." + auctionItem.getOwner().toString() + "." + auctionItem.getKey() + ".display", AuctionAPI.getInstance().expiredAuctionItem(auctionItem));
                                    }

                                    //Remove the auction item from list, and save data file
                                    Core.getInstance().getData().saveConfig();
                                    Core.getInstance().getAuctionItems().remove(auctionItem);
                                } else {
                                    //Highest bidder is currently offline
                                    if (Core.getInstance().getEconomy().hasBalance(highestBidder, auctionItem.getCurrentPrice())) {
                                        //Has enough money
                                        Core.getInstance().getData().getConfig().set("expired." + auctionItem.getHighestBidder().toString() + "." + auctionItem.getKey() + ".item", auctionItem.getItem());
                                        Core.getInstance().getData().getConfig().set("expired." + auctionItem.getHighestBidder().toString() + "." + auctionItem.getKey() + ".display", AuctionAPI.getInstance().expiredAuctionItem(auctionItem));
                                        Core.getInstance().getEconomy().withdrawBalance(highestBidder, auctionItem.getCurrentPrice());
                                        Core.getInstance().getEconomy().deposit(Bukkit.getOfflinePlayer(auctionItem.getOwner()), auctionItem.getCurrentPrice());

                                        //Perform the transaction
                                        Transaction transaction = new Transaction(Transaction.TransactionType.AUCTION_WON, auctionItem, auctionItem.getHighestBidder(), System.currentTimeMillis());
                                        transaction.saveTransaction();
                                        Core.getInstance().getPm().callEvent(new TransactionCompleteEvent(transaction));
                                    } else {
                                        //Doesn't have enough money
                                        Core.getInstance().getLocale().getMessage(AuctionLang.NOT_ENOUGH_MONEY).sendPrefixedMessage(highestBidder.getPlayer());
                                        Core.getInstance().getData().getConfig().set("expired." + auctionItem.getOwner().toString() + "." + auctionItem.getKey() + ".item", auctionItem.getItem());
                                        Core.getInstance().getData().getConfig().set("expired." + auctionItem.getOwner().toString() + "." + auctionItem.getKey() + ".display", AuctionAPI.getInstance().expiredAuctionItem(auctionItem));
                                    }

                                    //Remove the auction item from list, and save data file
                                    Core.getInstance().getData().saveConfig();
                                    Core.getInstance().getAuctionItems().remove(auctionItem);
                                }
                            }
                        }
                    }
                });

                //Refresh auction page is enabled
                if (AuctionSettings.AUTO_REFRESH_AUCTION_PAGES) {
                    Bukkit.getOnlinePlayers().forEach(p -> {
                        if (p.getOpenInventory().getTitle().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', Core.getInstance().getConfig().getString("guis.auctionhouse.title")))) {
                            p.getOpenInventory().getTopInventory().clear();
                            p.getOpenInventory().getTopInventory().setItem(45, AuctionAPI.getInstance().createConfigurationItem("guis.auctionhouse.items.yourauctions", new AuctionPlayer(p).getTotalActiveAuctions(), 0));
                            p.getOpenInventory().getTopInventory().setItem(46, AuctionAPI.getInstance().createConfigurationItem("guis.auctionhouse.items.collectionbin", 0, new AuctionPlayer(p).getTotalExpiredAuctions()));
                            p.getOpenInventory().getTopInventory().setItem(48, AuctionAPI.getInstance().createConfigurationItem("guis.auctionhouse.items.previouspage", 0, 0));
                            p.getOpenInventory().getTopInventory().setItem(49, AuctionAPI.getInstance().createConfigurationItem("guis.auctionhouse.items.refresh", 0, 0));
                            p.getOpenInventory().getTopInventory().setItem(50, AuctionAPI.getInstance().createConfigurationItem("guis.auctionhouse.items.nextpage", 0, 0));
                            p.getOpenInventory().getTopInventory().setItem(51, AuctionAPI.getInstance().createConfigurationItem("guis.auctionhouse.items.transactions", 0, 0));
                            p.getOpenInventory().getTopInventory().setItem(52, AuctionAPI.getInstance().createConfigurationItem("guis.auctionhouse.items.howtosell", 0, 0));
                            p.getOpenInventory().getTopInventory().setItem(53, AuctionAPI.getInstance().createConfigurationItem("guis.auctionhouse.items.guide", 0, 0));

                            List<List<AuctionItem>> chunks = Lists.partition(Core.getInstance().getAuctionItems(), 45);
                            //chunks.get(0).forEach(item -> p.getOpenInventory().getTopInventory().setItem(p.getOpenInventory().getTopInventory().firstEmpty(), item.getAuctionStack(AuctionItem.AuctionItemType.MAIN)));

                            //Pagination
                            if (chunks.size() != 0) {
                                chunks.get(Core.getInstance().getCurrentAuctionPage().get(p) - 1).forEach(item -> p.getOpenInventory().getTopInventory().setItem(p.getOpenInventory().getTopInventory().firstEmpty(), item.getAuctionStack(AuctionItem.AuctionItemType.MAIN)));
                            }
                        }
                    });
                }
            }

        } catch (Exception e) {
            Debugger.report(e);
        }

        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&6AuctionHouse&8]&a Ran Auction Tick at rate of " + AuctionSettings.UPDATE_EVERY_TICK + "s"));
    }
}
