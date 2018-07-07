package com.shadebyte.auctionhouse.inventory;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

/**
 * The current file has been created by Kiran Hart
 * Date Created: 7/6/2018
 * Time Created: 11:54 AM
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise.
 */
public interface AGUI extends InventoryHolder {

    void click(InventoryClickEvent e, ItemStack clicked, int slot);

    void close(InventoryCloseEvent e);
}