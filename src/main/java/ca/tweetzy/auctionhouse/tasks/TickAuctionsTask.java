package ca.tweetzy.auctionhouse.tasks;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.api.events.AuctionEndEvent;
import ca.tweetzy.auctionhouse.auction.AuctionSaleType;
import ca.tweetzy.auctionhouse.auction.AuctionedItem;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.hooks.EconomyManager;
import ca.tweetzy.core.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
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
            instance.runTaskTimerAsynchronously(AuctionHouse.getInstance(), 0, (long) 20 * Settings.TICK_UPDATE_TIME.getInt());
        }
        return instance;
    }

    @Override
    public void run() {

        Set<Map.Entry<UUID, AuctionedItem>> entrySet = AuctionHouse.getInstance().getAuctionItemManager().getItems().entrySet();
        Iterator<Map.Entry<UUID, AuctionedItem>> auctionItemIterator = entrySet.iterator();

        while (auctionItemIterator.hasNext()) {
            Map.Entry<UUID, AuctionedItem> entry = auctionItemIterator.next();
            AuctionedItem auctionItem = entry.getValue();
            ItemStack itemStack = auctionItem.getItem();

            if (!AuctionHouse.getInstance().getAuctionItemManager().getGarbageBin().keySet().isEmpty()) {
                AuctionHouse.getInstance().getDataManager().deleteItems(AuctionHouse.getInstance().getAuctionItemManager().getGarbageBin().values().stream().map(AuctionedItem::getId).collect(Collectors.toList()));
            }

            if (AuctionHouse.getInstance().getAuctionItemManager().getGarbageBin().containsKey(auctionItem.getId())) {
                AuctionHouse.getInstance().getAuctionItemManager().getGarbageBin().remove(auctionItem.getId());
                auctionItemIterator.remove();
                continue;
            }

            long timeRemaining = (auctionItem.getExpiresAt() - System.currentTimeMillis()) / 1000;

            if (!auctionItem.isExpired()) {
                if (Settings.BROADCAST_AUCTION_ENDING.getBoolean()) {
                    if (timeRemaining <= Settings.BROADCAST_AUCTION_ENDING_AT_TIME.getInt() && timeRemaining % 10 == 0 && timeRemaining != 0) {
                        Bukkit.getOnlinePlayers().forEach(player -> AuctionHouse.getInstance().getLocale().getMessage("auction.broadcast.ending")
                                .processPlaceholder("item", AuctionAPI.getInstance().getItemName(itemStack))
                                .processPlaceholder("seconds", timeRemaining)
                                .sendPrefixedMessage(player));
                    }
                }
            }

            if (timeRemaining <= 0) {
                if (auctionItem.getHighestBidder().equals(auctionItem.getOwner())) {
                    auctionItem.setExpired(true);
                    continue;
                }

                OfflinePlayer auctionWinner = Bukkit.getOfflinePlayer(auctionItem.getHighestBidder());

                double finalPrice = auctionItem.getCurrentPrice();
                double tax = Settings.TAX_ENABLED.getBoolean() ? (Settings.TAX_SALES_TAX_AUCTION_WON_PERCENTAGE.getDouble() / 100) * auctionItem.getCurrentPrice() : 0D;

                if (!EconomyManager.hasBalance(auctionWinner, Settings.TAX_CHARGE_SALES_TAX_TO_BUYER.getBoolean() ? finalPrice + tax : finalPrice)) {
                    auctionItem.setExpired(true);
                    continue;
                }

                AuctionEndEvent auctionEndEvent = new AuctionEndEvent(Bukkit.getOfflinePlayer(auctionItem.getOwner()), auctionWinner, auctionItem, AuctionSaleType.USED_BIDDING_SYSTEM);
                AuctionHouse.getInstance().getServer().getPluginManager().callEvent(auctionEndEvent);
                if (auctionEndEvent.isCancelled()) continue;


                EconomyManager.withdrawBalance(auctionWinner, Settings.TAX_CHARGE_SALES_TAX_TO_BUYER.getBoolean() ? finalPrice + tax : finalPrice);
                EconomyManager.deposit(Bukkit.getOfflinePlayer(auctionItem.getOwner()), Settings.TAX_CHARGE_SALES_TAX_TO_BUYER.getBoolean() ? finalPrice : finalPrice - tax);

                if (Bukkit.getOfflinePlayer(auctionItem.getOwner()).isOnline()) {
                    AuctionHouse.getInstance().getLocale().getMessage("auction.itemsold")
                            .processPlaceholder("item", AuctionAPI.getInstance().getItemName(itemStack))
                            .processPlaceholder("price", AuctionAPI.getInstance().formatNumber(Settings.TAX_CHARGE_SALES_TAX_TO_BUYER.getBoolean() ? finalPrice : finalPrice - tax))
                            .processPlaceholder("buyer_name", Bukkit.getOfflinePlayer(auctionItem.getHighestBidder()).getName())
                            .sendPrefixedMessage(Bukkit.getOfflinePlayer(auctionItem.getOwner()).getPlayer());
                    AuctionHouse.getInstance().getLocale().getMessage("pricing.moneyadd").processPlaceholder("price", AuctionAPI.getInstance().formatNumber(Settings.TAX_CHARGE_SALES_TAX_TO_BUYER.getBoolean() ? finalPrice : finalPrice - tax)).sendPrefixedMessage(Bukkit.getOfflinePlayer(auctionItem.getOwner()).getPlayer());
                }

                if (auctionWinner.isOnline()) {
                    assert auctionWinner.getPlayer() != null;
                    AuctionHouse.getInstance().getLocale().getMessage("auction.bidwon")
                            .processPlaceholder("item", AuctionAPI.getInstance().getItemName(itemStack))
                            .processPlaceholder("amount", itemStack.getAmount())
                            .processPlaceholder("price", AuctionAPI.getInstance().formatNumber(Settings.TAX_CHARGE_SALES_TAX_TO_BUYER.getBoolean() ? finalPrice + tax : finalPrice))
                            .sendPrefixedMessage(auctionWinner.getPlayer());
                    AuctionHouse.getInstance().getLocale().getMessage("pricing.moneyremove").processPlaceholder("price", AuctionAPI.getInstance().formatNumber(Settings.TAX_CHARGE_SALES_TAX_TO_BUYER.getBoolean() ? finalPrice + tax : finalPrice)).sendPrefixedMessage(auctionWinner.getPlayer());

                    if (Settings.ALLOW_PURCHASE_IF_INVENTORY_FULL.getBoolean()) {
                        PlayerUtils.giveItem(auctionWinner.getPlayer(), itemStack);
                        auctionItemIterator.remove();
                        continue;
                    }

                    if (auctionWinner.getPlayer().getInventory().firstEmpty() != -1) {
                        PlayerUtils.giveItem(auctionWinner.getPlayer(), itemStack);
                        auctionItemIterator.remove();
                        continue;
                    }
                }

                auctionItem.setOwner(auctionWinner.getUniqueId());
                auctionItem.setExpired(true);
            }
        }
    }

}
