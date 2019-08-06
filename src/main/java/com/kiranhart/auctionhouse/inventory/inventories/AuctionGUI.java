package com.kiranhart.auctionhouse.inventory.inventories;
/*
    The current file was created by Kiran Hart
    Date: August 03 2019
    Time: 8:09 PM
    
    Code within this class is not to be redistributed without proper permission.
*/

import com.google.common.collect.Lists;
import com.kiranhart.auctionhouse.Core;
import com.kiranhart.auctionhouse.api.statics.AuctionSettings;
import com.kiranhart.auctionhouse.api.version.NBTEditor;
import com.kiranhart.auctionhouse.api.version.XMaterial;
import com.kiranhart.auctionhouse.auction.AuctionItem;
import com.kiranhart.auctionhouse.inventory.AGUI;
import com.kiranhart.auctionhouse.util.Debugger;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class AuctionGUI implements AGUI {

    private Player p;
    private List<List<AuctionItem>> chunks;

    public AuctionGUI(Player p) {
        this.p = p;
        chunks = Lists.partition(Core.getInstance().getAuctionItems(), 45);
    }

    private int page = 1;

    @Override
    public void click(InventoryClickEvent e, ItemStack clicked, int slot) {
        e.setCancelled(true);
        Player p = (Player) e.getWhoClicked();

        /*
        Page navigation system
         */

        try {
            if (page >= 1 && slot == 48) p.openInventory(setPage(this.getPage() - 1).getInventory());
            if (page >= 1 && slot == 50) p.openInventory(setPage(this.getPage() + 1).getInventory());
        } catch (Exception ex) {
            Debugger.report(ex, false);
        }

        //Different auction inventories
        //Refresh Auction GUI
        if (slot == 49) {

            return;
        }

        //Open Listings GUI
        if (slot == 45) {

            return;
        }

        //Open Expired GUI
        if (slot == 46) {

            return;
        }

        //Open Transaction Selection GUI
        if (slot == 51) {

            return;
        }

        //Clicking on active auction items.
        //Check if air
        if (clicked == null || clicked.getType() == XMaterial.AIR.parseMaterial()) {
            return;
        }

        AuctionItem possibleAuctionItem = null;

        /*
        Perform the proper steps if the user left-clicks (not using bid system)
         */
        if (e.getClick() == ClickType.LEFT) {
            //Check if the bid system is set to false
            if (!AuctionSettings.USE_BIDDING_SYSTEM) {
                return;
            }

            //Get the key of the auction item
            String auctionItemKey = NBTEditor.getString(clicked, "AuctionItemKey");
            for (AuctionItem auctionItem : Core.getInstance().getAuctionItems()) {
                if (auctionItem.getKey().equalsIgnoreCase(auctionItemKey)) possibleAuctionItem = auctionItem;
            }



            return;
        }
    }

    @Override
    public Inventory getInventory() {
        return null;
    }

    @Override
    public void close(InventoryCloseEvent e) {
    }

    public AuctionGUI setPage(int page) {
        if (page <= 0)
            this.page = 1;
        else
            this.page = page;
        return this;
    }

    public int getPage() {
        return page;
    }
}
