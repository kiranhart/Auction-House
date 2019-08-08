package com.kiranhart.auctionhouse.listeners;
/*
    The current file was created by Kiran Hart
    Date: August 07 2019
    Time: 8:13 PM
    
    Code within this class is not to be redistributed without proper permission.
*/

import com.kiranhart.auctionhouse.inventory.AGUI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

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
