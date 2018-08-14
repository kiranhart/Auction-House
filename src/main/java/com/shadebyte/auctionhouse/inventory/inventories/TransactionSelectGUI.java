package com.shadebyte.auctionhouse.inventory.inventories;

import com.shadebyte.auctionhouse.inventory.AGUI;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * The current file has been created by Kiran Hart
 * Date Created: 8/14/2018
 * Time Created: 11:37 AM
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise.
 */
public class TransactionSelectGUI implements AGUI {

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
