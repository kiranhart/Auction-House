package com.kiranhart.auctionhouse.inventory.inventories;
/*
    The current file was created by Kiran Hart
    Date: August 03 2019
    Time: 8:10 PM
    
    Code within this class is not to be redistributed without proper permission.
*/

import com.kiranhart.auctionhouse.Core;
import com.kiranhart.auctionhouse.api.AuctionAPI;
import com.kiranhart.auctionhouse.api.events.TransactionCompleteEvent;
import com.kiranhart.auctionhouse.api.statics.AuctionLang;
import com.kiranhart.auctionhouse.api.statics.AuctionSettings;
import com.kiranhart.auctionhouse.auction.AuctionItem;
import com.kiranhart.auctionhouse.auction.Transaction;
import com.kiranhart.auctionhouse.inventory.AGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.stream.IntStream;

public class ConfirmationGUI implements AGUI {

    private AuctionItem auctionItem;

    public ConfirmationGUI(AuctionItem auctionItem) {
        this.auctionItem = auctionItem;
    }

    private int[] yes = {0, 1, 2, 3};
    private int[] no = {5, 6, 7, 8};

    @Override
    public void click(InventoryClickEvent e, ItemStack clicked, int slot) {
        e.setCancelled(true);
        Player p = (Player) e.getWhoClicked();

        //Check if the slots clicked are within range (no)
        if (IntStream.of(no).anyMatch(x -> x == slot)) {
            p.closeInventory();
            p.openInventory(new AuctionGUI(p).getInventory());
            return;
        }

        //Check if player clicks yes
        if (IntStream.of(yes).anyMatch(x -> x == slot)) {
            //Check if the auction item is hasn't been purchased yet
            if (Core.getInstance().getAuctionItems().contains(auctionItem)) {
                //Check if they have enough money to purchase the item
                if (Core.getInstance().getEconomy().has(p, auctionItem.getBuyNowPrice())) {
                    //Withdraw
                    Core.getInstance().getEconomy().withdrawPlayer(p, auctionItem.getBuyNowPrice());
                    Core.getInstance().getEconomy().depositPlayer(Bukkit.getOfflinePlayer(auctionItem.getOwner()), auctionItem.getBuyNowPrice());

                    //Check inventory space
                    if (AuctionAPI.getInstance().availableSlots(p.getInventory()) == 0) {
                        Core.getInstance().getData().getConfig().set("expired." + p.getUniqueId().toString() + "." + auctionItem.getKey() + ".item", auctionItem.getItem());
                        Core.getInstance().getData().getConfig().set("expired." + p.getUniqueId().toString() + "." + auctionItem.getKey() + ".display", AuctionAPI.getInstance().expiredAuctionItem(auctionItem));
                    } else {
                        p.getInventory().addItem(auctionItem.getItem());
                    }

                    Core.getInstance().getLocale().getMessage(AuctionLang.AUCTION_BUY).processPlaceholder("itemname", auctionItem.getDisplayName()).processPlaceholder("price", AuctionAPI.getInstance().getFriendlyNumber(auctionItem.getBuyNowPrice())).sendPrefixedMessage(p);
                    OfflinePlayer owner = Bukkit.getOfflinePlayer(auctionItem.getOwner());

                    if (owner.isOnline()) {
                        Core.getInstance().getLocale().getMessage(AuctionLang.AUCTION_SOLD).processPlaceholder("player", p.getName()).processPlaceholder("itemname", auctionItem.getDisplayName()).processPlaceholder("price", AuctionAPI.getInstance().getFriendlyNumber(auctionItem.getBuyNowPrice())).sendPrefixedMessage(owner.getPlayer());
                    }

                    //Perform the transaction
                    Transaction transaction = new Transaction(Transaction.TransactionType.BOUGHT, auctionItem, p.getUniqueId(), System.currentTimeMillis());

                    if (AuctionSettings.SAVE_TRANSACTIONS) {
                        transaction.saveTransaction();
                    }

                    auctionItem.setTime(0);
                    Core.getInstance().getAuctionItems().remove(auctionItem);
                    p.closeInventory();
                    TransactionCompleteEvent completeEvent = new TransactionCompleteEvent(transaction);
                    Core.getInstance().getPm().callEvent(completeEvent);
                    p.openInventory(new AuctionGUI(p).getInventory());
                } else {
                    p.closeInventory();
                    Core.getInstance().getLocale().getMessage(AuctionLang.NOT_ENOUGH_MONEY).sendPrefixedMessage(p);
                }
            } else {
                p.closeInventory();
                p.openInventory(new AuctionGUI(p).getInventory());
            }
        }
    }

    @Override
    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this, 9, ChatColor.translateAlternateColorCodes('&', Core.getInstance().getConfig().getString("guis.confirm.title")));
        for (int i = 0; i <= 3; i++) {
            inventory.setItem(i, AuctionAPI.getInstance().createConfigurationItem("guis.confirm.items.confirm", 0, 0));
        }

        for (int i = 5; i <= 8; i++) {
            inventory.setItem(i, AuctionAPI.getInstance().createConfigurationItem("guis.confirm.items.decline", 0, 0));
        }

        inventory.setItem(4, auctionItem.getItem());
        return inventory;
    }

    @Override
    public void close(InventoryCloseEvent e) {

    }

}
