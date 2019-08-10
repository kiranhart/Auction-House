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
import com.kiranhart.auctionhouse.auction.AuctionPlayer;
import com.kiranhart.auctionhouse.inventory.AGUI;
import com.kiranhart.auctionhouse.util.Debugger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ExpiredGUI implements AGUI {

    private Player p;
    private List<List<ItemStack>> chunks;
    private int page = 1;

    public ExpiredGUI(Player p) {
        this.p = p;
        chunks = Lists.partition(AuctionAPI.getInstance().getAllExpiredItems(p), 45);
    }

    @Override
    public void click(InventoryClickEvent e, ItemStack clicked, int slot) {
        e.setCancelled(true);

        try {
            if (page >= 1 && slot == 48) p.openInventory(this.setPage(this.getPage() - 1).getInventory());
            if (page >= 1 && slot == 50) p.openInventory(this.setPage(this.getPage() + 1).getInventory());
        } catch (Exception e1) {
            Debugger.report(e1, false);
        }

        if (slot >= 0 & slot <= 44) {

            if (clicked == null || clicked.getType() == Material.AIR) {
                return;
            }

            String key = (String) NBTEditor.getString(clicked, "ExpiredAuctionItem");
            ItemStack stack = Core.getInstance().getData().getConfig().getItemStack("expired." + p.getUniqueId().toString() + "." + key + ".item");
            if (AuctionAPI.getInstance().availableSlots(p.getInventory()) == 0) {
                p.getWorld().dropItemNaturally(p.getLocation(), stack);
            } else {
                p.getInventory().addItem(stack);
            }

            Core.getInstance().getData().getConfig().set("expired." + p.getUniqueId().toString() + "." + key, null);
            Core.getInstance().getData().saveConfig();

            p.closeInventory();
            p.openInventory(new ExpiredGUI(p).getInventory());
        }


        if (slot == 49) {
            if (Core.getInstance().getData().getConfig().getConfigurationSection("expired." + p.getUniqueId().toString()) != null) {
                if (Core.getInstance().getData().getConfig().getConfigurationSection("expired." + p.getUniqueId().toString()).getKeys(false).size() >= 1) {
                    Core.getInstance().getData().getConfig().getConfigurationSection("expired." + p.getUniqueId().toString()).getKeys(false).forEach(node -> {
                        ItemStack stack = Core.getInstance().getData().getConfig().getItemStack("expired." + p.getUniqueId().toString() + "." + node + ".item");
                        Core.getInstance().getData().getConfig().set("expired." + p.getUniqueId().toString() + "." + node, null);
                        Core.getInstance().getData().saveConfig();
                        if (AuctionAPI.getInstance().availableSlots(p.getInventory()) == 0) {
                            p.getWorld().dropItemNaturally(p.getLocation(), stack);
                        } else {
                            p.getInventory().addItem(stack);
                        }
                    });

                    p.closeInventory();
                    p.openInventory(new ExpiredGUI(p).getInventory());
                }
            }
        }

        if (slot == 45) {
            p.closeInventory();
            p.openInventory(new AuctionGUI(p).getInventory());
        }
    }

    @Override
    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this, 54, ChatColor.translateAlternateColorCodes('&', Core.getInstance().getConfig().getString("guis.collection.title")));

        inventory.setItem(45, AuctionAPI.getInstance().createConfigurationItem("guis.collection.items.return", 0, 0));
        inventory.setItem(48, AuctionAPI.getInstance().createConfigurationItem("guis.collection.items.previouspage", 0, 0));
        inventory.setItem(49, AuctionAPI.getInstance().createConfigurationItem("guis.collection.items.claimall", 0, 0));
        inventory.setItem(50, AuctionAPI.getInstance().createConfigurationItem("guis.collection.items.nextpage", 0, 0));
        inventory.setItem(53, AuctionAPI.getInstance().createConfigurationItem("guis.collection.items.tutorial", 0, 0));

        if (new AuctionPlayer(p).getTotalExpiredAuctions() == 0) {
            return inventory;
        }

        if (chunks.size() != 0)
            chunks.get(getPage() - 1).forEach(item -> inventory.setItem(inventory.firstEmpty(), item));

        return inventory;
    }

    private ExpiredGUI setPage(int page) {
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
