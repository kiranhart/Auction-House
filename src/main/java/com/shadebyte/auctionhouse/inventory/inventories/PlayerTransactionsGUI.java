package com.shadebyte.auctionhouse.inventory.inventories;

import com.google.common.collect.Lists;
import com.shadebyte.auctionhouse.Core;
import com.shadebyte.auctionhouse.api.AuctionAPI;
import com.shadebyte.auctionhouse.auction.Transaction;
import com.shadebyte.auctionhouse.inventory.AGUI;
import com.shadebyte.auctionhouse.util.NBTEditor;
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

/**
 * The current file has been created by Kiran Hart
 * Date Created: 7/6/2018
 * Time Created: 11:56 AM
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise.
 */
public class PlayerTransactionsGUI implements AGUI {

    private Player p;
    private List<List<ItemStack>> chunks;
    private int page = 1;

    public PlayerTransactionsGUI(Player p) {
        this.p = p;
        chunks = Lists.partition(Transaction.getAllRecordedTransactionsByPlayer(p), 45);
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
                String key = (String) NBTEditor.getItemTag(clicked, "AuctionTransactionID");
                p.closeInventory();
                p.openInventory(new SingleTransactionGUI(key).getInventory());
            }
        }
    }

    @Override
    public void close(InventoryCloseEvent e) {

    }

    @Override
    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this, 54, ChatColor.translateAlternateColorCodes('&', Core.getInstance().getConfig().getString("gui.playertransactions.title").replace("{player}", p.getName())));

        //Bottom Row
        inventory.setItem(48, AuctionAPI.getInstance().createConfigItem("gui.playertransactions.items.previouspage", 0, 0));
        inventory.setItem(49, AuctionAPI.getInstance().createConfigItem("gui.playertransactions.items.close", 0, 0));
        inventory.setItem(50, AuctionAPI.getInstance().createConfigItem("gui.playertransactions.items.nextpage", 0, 0));

        if (Transaction.getTotalTransactions() == 0) {
            return inventory;
        }

        if (chunks.size() != 0)
            chunks.get(getPage() - 1).forEach(item -> inventory.setItem(inventory.firstEmpty(), item));

        return inventory;
    }

    public PlayerTransactionsGUI setPage(int page) {
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
