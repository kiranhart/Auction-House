package com.shadebyte.auctionhouse.inventory.inventories;

import com.shadebyte.auctionhouse.Core;
import com.shadebyte.auctionhouse.api.AuctionAPI;
import com.shadebyte.auctionhouse.inventory.AGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
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
        e.setCancelled(true);
        Player p = (Player) e.getWhoClicked();
        if (slot == 12) p.openInventory(new AllTransactionsGUI(p).getInventory());
        if (slot == 14) p.openInventory(new PlayerTransactionsGUI(p).getInventory());
    }

    @Override
    public void close(InventoryCloseEvent e) {

    }

    @Override
    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this, 27, ChatColor.translateAlternateColorCodes('&', Core.getInstance().getConfig().getString("gui.transactionselect.title")));
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, AuctionAPI.getInstance().createConfigItem("gui.transactionselect.items.background", 0, 0));
        }

        inventory.setItem(12, AuctionAPI.getInstance().createConfigItem("gui.transactionselect.items.all", 0, 0));
        inventory.setItem(14, AuctionAPI.getInstance().createConfigItem("gui.transactionselect.items.you", 0, 0));
        return inventory;
    }
}
