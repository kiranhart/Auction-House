package ca.tweetzy.auctionhouse.tasks;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.api.events.AuctionEndEvent;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.utils.PlayerUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: February 18 2021
 * Time Created: 8:47 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class TickAuctionsTask extends BukkitRunnable {

    private static TickAuctionsTask instance;

    public static TickAuctionsTask startTask() {
        if (instance == null) {
            instance = new TickAuctionsTask();
            // maybe to async
            instance.runTaskTimerAsynchronously(AuctionHouse.getInstance(), 0, (long) 20 * Settings.TICK_UPDATE_TIME.getInt());
        }
        return instance;
    }

    @Override
    public void run() {
        try {
            // check if the auction stack even has items
            if (AuctionHouse.getInstance().getAuctionItemManager().getAuctionItems().size() == 0) {
                return;
            }

            // tick all the auction items
            AuctionHouse.getInstance().getAuctionItemManager().getAuctionItems().forEach(item -> {
                if (!item.isExpired()) {
                    item.updateRemainingTime(Settings.TICK_UPDATE_TIME.getInt());
                }
            });
            // filter items where the time is less than or equal to 0

            AuctionHouse.getInstance().getAuctionItemManager().getAuctionItems().stream().filter(item -> item.getRemainingTime() <= 0).collect(Collectors.toList()).iterator().forEachRemaining(item -> {
                // call the auction end event
                AuctionEndEvent auctionEndEvent = new AuctionEndEvent(item);
                AuctionHouse.getInstance().getServer().getPluginManager().callEvent(auctionEndEvent);

                // if the event is cancelled then stop
                if (!auctionEndEvent.isCancelled()) {
                    // check if the auction item owner is the same as the highest bidder
                    if (item.getOwner().equals(item.getHighestBidder())) {
                        // was not sold
                        AuctionHouse.getInstance().getAuctionItemManager().adjustItemsInFile(item, true);
                    } else {
                        // the item was sold ?? then do the checks
                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(item.getHighestBidder());
                        if (offlinePlayer.isOnline()) {
                            if (AuctionHouse.getInstance().getEconomy().has(offlinePlayer, item.getCurrentPrice())) {
                                // since they're online, try to add the item to their inventory
                                PlayerUtils.giveItem(offlinePlayer.getPlayer(), AuctionAPI.deserializeItem(item.getRawItem()));
                                // withdraw money and give to the owner
                                AuctionHouse.getInstance().getEconomy().withdrawPlayer(offlinePlayer, item.getCurrentPrice());
                                AuctionHouse.getInstance().getEconomy().depositPlayer(Bukkit.getOfflinePlayer(item.getOwner()), item.getCurrentPrice());
                                // send a message to each of them
                                AuctionHouse.getInstance().getLocale().getMessage("auction.bidwon")
                                        .processPlaceholder("item", WordUtils.capitalizeFully(AuctionAPI.deserializeItem(item.getRawItem()).getType().name().replace("_", " ")))
                                        .processPlaceholder("amount", AuctionAPI.deserializeItem(item.getRawItem()).getAmount())
                                        .processPlaceholder("price", String.format("%,.2f", item.getCurrentPrice()))
                                        .sendPrefixedMessage(offlinePlayer.getPlayer());
                                AuctionHouse.getInstance().getLocale().getMessage("pricing.moneyremove").processPlaceholder("price", String.format("%,.2f", item.getCurrentPrice())).sendPrefixedMessage(offlinePlayer.getPlayer());
                                // if the original owner is online, let them know they sold an item
                                if (Bukkit.getOfflinePlayer(item.getOwner()).isOnline()) {
                                    AuctionHouse.getInstance().getLocale().getMessage("auction.itemsold")
                                            .processPlaceholder("item", WordUtils.capitalizeFully(AuctionAPI.deserializeItem(item.getRawItem()).getType().name().replace("_", " ")))
                                            .processPlaceholder("price", String.format("%,.2f", item.getCurrentPrice()))
                                            .sendPrefixedMessage(Bukkit.getOfflinePlayer(item.getOwner()).getPlayer());
                                    AuctionHouse.getInstance().getLocale().getMessage("pricing.moneyadd").processPlaceholder("price", String.format("%,.2f", item.getCurrentPrice())).sendPrefixedMessage(Bukkit.getOfflinePlayer(item.getOwner()).getPlayer());
                                }

                                AuctionHouse.getInstance().getAuctionItemManager().adjustItemsInFile(item, false);
                            } else {
                                // they don't have enough money to buy it, so send it back to the original owner
                                AuctionHouse.getInstance().getAuctionItemManager().adjustItemsInFile(item, true);
                            }
                        } else {
                            // offline, so save their purchase in the collection inventory
                            if (AuctionHouse.getInstance().getEconomy().has(offlinePlayer, item.getCurrentPrice())) {
                                // withdraw money and give to the owner
                                AuctionHouse.getInstance().getEconomy().withdrawPlayer(offlinePlayer, item.getCurrentPrice());
                                AuctionHouse.getInstance().getEconomy().depositPlayer(Bukkit.getOfflinePlayer(item.getOwner()), item.getCurrentPrice());
                                item.setOwner(offlinePlayer.getUniqueId());
                                AuctionHouse.getInstance().getAuctionItemManager().adjustItemsInFile(item, true);
                            } else {
                                // they don't have enough money to buy it, so send it back to the original owner
                                AuctionHouse.getInstance().getAuctionItemManager().adjustItemsInFile(item, true);
                            }
                        }
                    }
                }
            });


            // auction page updates
//            if (Settings.AUTO_REFRESH_AUCTION_PAGES.getBoolean()) {
//                Bukkit.getOnlinePlayers().forEach(player -> {
//                    if (player.getOpenInventory().getTitle().equals(Settings.GUI_AUCTION_HOUSE_TITLE.getString())) {
//                        player.openInventory(new AuctionHouseGUI(AuctionHouse.getInstance().getAuctionPlayerManager().locateAndSelectPlayer(player)).getInventory());
//                    }
//                });
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
