package com.shadebyte.auctionhouse.events;

import com.shadebyte.auctionhouse.inventory.AGUI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

/**
 * The current file has been created by Kiran Hart
 * Date Created: 7/6/2018
 * Time Created: 4:03 PM
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise.
 */
public class AGUIListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getInventory().getHolder() instanceof AGUI) {
            AGUI gui = (AGUI) e.getInventory().getHolder();
            gui.click(e, e.getCurrentItem(), e.getRawSlot());
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (e.getInventory().getHolder() instanceof AGUI) {
            AGUI gui = (AGUI) e.getInventory().getHolder();
            gui.close(e);
        }
    }
}
