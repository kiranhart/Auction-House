package com.kiranhart.auctionhouse.inventory.inventories;
/*
    The current file was created by Kiran Hart
    Date: August 03 2019
    Time: 8:10 PM
    
    Code within this class is not to be redistributed without proper permission.
*/

import com.kiranhart.auctionhouse.Core;
import com.kiranhart.auctionhouse.api.AuctionAPI;
import com.kiranhart.auctionhouse.inventory.AGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class TransactionSelectGUI implements AGUI {

    @Override
    public void click(InventoryClickEvent e, ItemStack clicked, int slot) {
        e.setCancelled(true);
        Player p = (Player) e.getWhoClicked();
        if (slot == 12) p.openInventory(new AllTransactionsGUI(p).getInventory());
        if (slot == 14) p.openInventory(new PlayerTransactionsGUI(p).getInventory());
    }

    @Override
    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this, 27, ChatColor.translateAlternateColorCodes('&', Core.getInstance().getConfig().getString("guis.transactionselect.title")));

        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, AuctionAPI.getInstance().createConfigurationItem("guis.transactionselect.items.background", 0, 0));
        }

        inventory.setItem(12, AuctionAPI.getInstance().createConfigurationItem("guis.transactionselect.items.all", 0, 0));
        inventory.setItem(14, AuctionAPI.getInstance().createConfigurationItem("guis.transactionselect.items.you", 0, 0));
        return inventory;
    }

    @Override
    public void close(InventoryCloseEvent e) {
    }
}
