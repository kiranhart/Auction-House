package com.shadebyte.auctionhouse.inventory.inventories;

import com.google.common.collect.Lists;
import com.shadebyte.auctionhouse.Core;
import com.shadebyte.auctionhouse.api.AuctionAPI;
import com.shadebyte.auctionhouse.auction.AuctionPlayer;
import com.shadebyte.auctionhouse.inventory.AGUI;
import com.shadebyte.auctionhouse.util.NBTEditor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
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
        }

        if (slot >= 0 & slot <= 44) {

            if (clicked == null || clicked.getType() == Material.AIR) {
                return;
            }

            String key = (String) NBTEditor.getItemTag(clicked, "ExpiredAuctionItem");
            ItemStack stack = Core.getInstance().getData().getConfig().getItemStack("expired." + p.getUniqueId().toString() + "." + key + ".item");
            Core.getInstance().getData().getConfig().set("expired." + p.getUniqueId().toString() + "." + key, null);
            Core.getInstance().getData().saveConfig();
            if (AuctionAPI.getInstance().availableSlots(p.getInventory()) >= 1) {
                p.getInventory().addItem(stack);
            } else {
                p.getWorld().dropItemNaturally(p.getLocation(), stack);
            }
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
                        if (AuctionAPI.getInstance().availableSlots(p.getInventory()) >= 1) {
                            p.getInventory().addItem(stack);
                        } else {
                            p.getWorld().dropItemNaturally(p.getLocation(), stack);
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
    public void close(InventoryCloseEvent e) {

    }

    @Override
    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this, 54, ChatColor.translateAlternateColorCodes('&', Core.getInstance().getConfig().getString("gui.collection.title")));

        //Bottom Row
        inventory.setItem(45, AuctionAPI.getInstance().createConfigItem("gui.collection.items.return", 0, 0));
        inventory.setItem(48, AuctionAPI.getInstance().createConfigItem("gui.collection.items.previouspage", 0, 0));
        inventory.setItem(49, AuctionAPI.getInstance().createConfigItem("gui.collection.items.claimall", 0, 0));
        inventory.setItem(50, AuctionAPI.getInstance().createConfigItem("gui.collection.items.nextpage", 0, 0));
        inventory.setItem(53, AuctionAPI.getInstance().createConfigItem("gui.collection.items.tutorial", 0, 0));

        if (new AuctionPlayer(p).getTotalExpiredAuctions() == 0) {
            return inventory;
        }

        if (chunks.size() != 0)
            chunks.get(getPage() - 1).forEach(item -> inventory.setItem(inventory.firstEmpty(), item));

        return inventory;
    }

    public ExpiredGUI setPage(int page) {
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
