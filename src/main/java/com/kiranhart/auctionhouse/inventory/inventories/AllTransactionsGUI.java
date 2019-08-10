package com.kiranhart.auctionhouse.inventory.inventories;
/*
    The current file was created by Kiran Hart
    Date: August 03 2019
    Time: 8:10 PM
    
    Code within this class is not to be redistributed without proper permission.
*/

import com.google.common.collect.Lists;
import com.kiranhart.auctionhouse.Core;
import com.kiranhart.auctionhouse.api.AuctionAPI;
import com.kiranhart.auctionhouse.api.version.NBTEditor;
import com.kiranhart.auctionhouse.auction.Transaction;
import com.kiranhart.auctionhouse.inventory.AGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class AllTransactionsGUI implements AGUI {

    private Player p;
    private List<List<ItemStack>> chunks;
    private int page = 1;

    public AllTransactionsGUI(Player p) {
        this.p = p;
        chunks = Lists.partition(Transaction.getAllRecordedTransactions(), 45);
    }

    @Override
    public void click(InventoryClickEvent e, ItemStack clicked, int slot) {
        e.setCancelled(true);

        try {
            if (page >= 1 && slot == 48) p.openInventory(this.setPage(this.getPage() - 1).getInventory());
            if (page >= 1 && slot == 50) p.openInventory(this.setPage(this.getPage() + 1).getInventory());
        } catch (Exception e1) {
        }

        if (slot == 49) p.openInventory(new TransactionSelectGUI().getInventory());

        if (slot >= 0 & slot <= 44) {
            if (clicked == null || clicked.getType() == Material.AIR) {
                return;
            }

            if (e.getClick() == ClickType.LEFT) {
                String key = (String) NBTEditor.getString(clicked, "AuctionTransactionID");
                p.closeInventory();
                p.openInventory(new SingleTransactionGUI(key).getInventory());
            }
        }
    }

    @Override
    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this, 54, ChatColor.translateAlternateColorCodes('&', Core.getInstance().getConfig().getString("guis.alltransactions.title")));

        //Bottom Row
        inventory.setItem(48, AuctionAPI.getInstance().createConfigurationItem("guis.alltransactions.items.previouspage", 0, 0));
        inventory.setItem(49, AuctionAPI.getInstance().createConfigurationItem("guis.alltransactions.items.close", 0, 0));
        inventory.setItem(50, AuctionAPI.getInstance().createConfigurationItem("guis.alltransactions.items.nextpage", 0, 0));

        if (Transaction.getTotalTransactions() == 0) {
            return inventory;
        }

        if (chunks.size() != 0)
            chunks.get(getPage() - 1).forEach(item -> inventory.setItem(inventory.firstEmpty(), item));

        return inventory;
    }

    private AllTransactionsGUI setPage(int page) {
        if (page <= 0)
            this.page = 1;
        else
            this.page = page;
        return this;
    }

    private int getPage() {
        return page;
    }

    @Override
    public void close(InventoryCloseEvent e) {
    }
}
