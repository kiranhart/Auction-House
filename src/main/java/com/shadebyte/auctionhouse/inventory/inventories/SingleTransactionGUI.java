package com.shadebyte.auctionhouse.inventory.inventories;

import com.shadebyte.auctionhouse.Core;
import com.shadebyte.auctionhouse.api.AuctionAPI;
import com.shadebyte.auctionhouse.auction.Transaction;
import com.shadebyte.auctionhouse.inventory.AGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * The current file has been created by Kiran Hart
 * Date Created: 8/7/2018
 * Time Created: 4:32 PM
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise.
 */
public class SingleTransactionGUI implements AGUI {

    private Transaction transaction;

    public SingleTransactionGUI(Transaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public void click(InventoryClickEvent e, ItemStack clicked, int slot) {
        e.setCancelled(true);
    }

    @Override
    public void close(InventoryCloseEvent e) {

    }

    @Override
    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this, 54, ChatColor.translateAlternateColorCodes('&', Core.getInstance().getConfig().getString("gui.singletransaction.title")));
        //Fill Inventory
        for (int i = 0; i < inventory.getSize(); i++) inventory.setItem(i, AuctionAPI.getInstance().createConfigItem("gui.singletransaction.items.background", 0, 0));

        inventory.setItem(13, transaction.getAuctionItem().getItem());
        inventory.setItem(48, AuctionAPI.getInstance().createConfigItem("gui.singletransaction.items.back", 0, 0));
        inventory.setItem(49, AuctionAPI.getInstance().createConfigItem("gui.singletransaction.items.you", 0, 0));
        inventory.setItem(50, AuctionAPI.getInstance().createConfigItem("gui.singletransaction.items.auctionhouse", 0, 0));

        return inventory;
    }
}
