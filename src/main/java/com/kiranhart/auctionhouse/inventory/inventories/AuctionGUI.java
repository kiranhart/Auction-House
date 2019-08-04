package com.kiranhart.auctionhouse.inventory.inventories;
/*
    The current file was created by Kiran Hart
    Date: August 03 2019
    Time: 8:09 PM
    
    Code within this class is not to be redistributed without proper permission.
*/

import com.kiranhart.auctionhouse.inventory.AGUI;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class AuctionGUI implements AGUI {

    @Override
    public void click(InventoryClickEvent e, ItemStack clicked, int slot) {

    }

    @Override
    public void close(InventoryCloseEvent e) {
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}
