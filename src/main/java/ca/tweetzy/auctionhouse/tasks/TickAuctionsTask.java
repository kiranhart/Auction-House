package ca.tweetzy.auctionhouse.tasks;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.api.events.AuctionEndEvent;
import ca.tweetzy.auctionhouse.auction.AuctionItem;
import ca.tweetzy.auctionhouse.auction.AuctionSaleType;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.utils.PlayerUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;

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

        // check if the auction stack even has items
        Iterator<AuctionItem> auctionItemIterator = AuctionHouse.getInstance().getAuctionItemManager().getAuctionItems().listIterator();

        while (auctionItemIterator.hasNext()) {
            AuctionItem auctionItem = auctionItemIterator.next();

            if (AuctionHouse.getInstance().getAuctionItemManager().getGarbageBin().contains(auctionItem)) {
                AuctionHouse.getInstance().getAuctionItemManager().getGarbageBin().remove(auctionItem);
                auctionItemIterator.remove();
                continue;
            }

            if (!auctionItem.isExpired()) {
                auctionItem.updateRemainingTime(Settings.TICK_UPDATE_TIME.getInt());
            }

            if (auctionItem.getRemainingTime() <= 0) {
                if (auctionItem.getHighestBidder().equals(auctionItem.getOwner())) {
                    auctionItem.setExpired(true);
                    continue;
                }

                OfflinePlayer auctionWinner = Bukkit.getOfflinePlayer(auctionItem.getHighestBidder());

                if (!AuctionHouse.getInstance().getEconomyManager().has(auctionWinner, auctionItem.getCurrentPrice())) {
                    auctionItem.setExpired(true);
                    continue;
                }

                AuctionEndEvent auctionEndEvent = new AuctionEndEvent(Bukkit.getOfflinePlayer(auctionItem.getOwner()), auctionWinner, auctionItem, AuctionSaleType.USED_BIDDING_SYSTEM);
                AuctionHouse.getInstance().getServer().getPluginManager().callEvent(auctionEndEvent);
                if (auctionEndEvent.isCancelled()) continue;


                AuctionHouse.getInstance().getEconomyManager().withdrawPlayer(auctionWinner, auctionItem.getCurrentPrice());
                AuctionHouse.getInstance().getEconomyManager().depositPlayer(Bukkit.getOfflinePlayer(auctionItem.getOwner()), auctionItem.getCurrentPrice());

                if (Bukkit.getOfflinePlayer(auctionItem.getOwner()).isOnline()) {
                    AuctionHouse.getInstance().getLocale().getMessage("auction.itemsold")
                            .processPlaceholder("item", WordUtils.capitalizeFully(AuctionAPI.getInstance().deserializeItem(auctionItem.getRawItem()).getType().name().replace("_", " ")))
                            .processPlaceholder("price", AuctionAPI.getInstance().formatNumber(auctionItem.getCurrentPrice()))
                            .processPlaceholder("buyer_name", Bukkit.getOfflinePlayer(auctionItem.getHighestBidder()).getName())
                            .sendPrefixedMessage(Bukkit.getOfflinePlayer(auctionItem.getOwner()).getPlayer());
                    AuctionHouse.getInstance().getLocale().getMessage("pricing.moneyadd").processPlaceholder("price", AuctionAPI.getInstance().formatNumber(auctionItem.getCurrentPrice())).sendPrefixedMessage(Bukkit.getOfflinePlayer(auctionItem.getOwner()).getPlayer());
                }

                if (auctionWinner.isOnline()) {
                    AuctionHouse.getInstance().getLocale().getMessage("auction.bidwon")
                            .processPlaceholder("item", WordUtils.capitalizeFully(AuctionAPI.getInstance().deserializeItem(auctionItem.getRawItem()).getType().name().replace("_", " ")))
                            .processPlaceholder("amount", AuctionAPI.getInstance().deserializeItem(auctionItem.getRawItem()).getAmount())
                            .processPlaceholder("price", AuctionAPI.getInstance().formatNumber(auctionItem.getCurrentPrice()))
                            .sendPrefixedMessage(auctionWinner.getPlayer());
                    AuctionHouse.getInstance().getLocale().getMessage("pricing.moneyremove").processPlaceholder("price", AuctionAPI.getInstance().formatNumber(auctionItem.getCurrentPrice())).sendPrefixedMessage(auctionWinner.getPlayer());

                    if (Settings.ALLOW_PURCHASE_IF_INVENTORY_FULL.getBoolean()) {
                        PlayerUtils.giveItem(auctionWinner.getPlayer(), AuctionAPI.getInstance().deserializeItem(auctionItem.getRawItem()));
                        auctionItemIterator.remove();
                        continue;
                    }

                    if (auctionWinner.getPlayer().getInventory().firstEmpty() != -1) {
                        PlayerUtils.giveItem(auctionWinner.getPlayer(), AuctionAPI.getInstance().deserializeItem(auctionItem.getRawItem()));
                        auctionItemIterator.remove();
                        continue;
                    }

                    auctionItem.setOwner(auctionWinner.getUniqueId());
                    auctionItem.setExpired(true);

                } else {
                    auctionItem.setOwner(auctionWinner.getUniqueId());
                    auctionItem.setExpired(true);
                }
            }
        }
    }
}
