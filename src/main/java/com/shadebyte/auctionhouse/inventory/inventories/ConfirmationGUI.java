package com.shadebyte.auctionhouse.inventory.inventories;

import com.shadebyte.auctionhouse.Core;
import com.shadebyte.auctionhouse.api.AuctionAPI;
import com.shadebyte.auctionhouse.auction.AuctionItem;
import com.shadebyte.auctionhouse.inventory.AGUI;
import org.apache.commons.lang.math.IntRange;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.stream.IntStream;

/**
 * The current file has been created by Kiran Hart
 * Date Created: 7/6/2018
 * Time Created: 11:56 AM
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise.
 */
public class ConfirmationGUI implements AGUI {

    private static ConfirmationGUI instance;
    private AuctionItem auctionItem;

    private ConfirmationGUI(AuctionItem auctionItem) {
        this.auctionItem = auctionItem;
    }

    public static ConfirmationGUI getInstance(AuctionItem auctionItem) {
        if (instance == null) {
            instance = new ConfirmationGUI(auctionItem);
        }
        return instance;
    }

    @Override
    public void click(InventoryClickEvent e, ItemStack clicked, int slot) {
        e.setCancelled(true);
    }

    @Override
    public void close(InventoryCloseEvent e) {
        Bukkit.getServer().getScheduler().runTaskLater(Core.getInstance(), () -> e.getPlayer().openInventory(e.getInventory()),1);
    }

    @Override
    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this, 9, Core.getInstance().getConfig().getString("gui.confirm.title"));
        for (int i = 0; i <= 3; i++) {
            inventory.setItem(i, AuctionAPI.getInstance().fill("&a&lYes", 5));
        }

        for (int i = 5; i <= 8; i++) {
            inventory.setItem(i, AuctionAPI.getInstance().fill("&c&lNo", 14));
        }

        inventory.setItem(4, auctionItem.getItem());
        return inventory;
    }
}
