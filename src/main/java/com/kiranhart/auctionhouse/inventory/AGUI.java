package com.kiranhart.auctionhouse.inventory;
/*
    The current file was created by Kiran Hart
    Date: August 03 2019
    Time: 8:08 PM
    
    Code within this class is not to be redistributed without proper permission.
*/

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public interface AGUI extends InventoryHolder {

    void click(InventoryClickEvent e, ItemStack clicked, int slot);

    void close(InventoryCloseEvent e);
}
