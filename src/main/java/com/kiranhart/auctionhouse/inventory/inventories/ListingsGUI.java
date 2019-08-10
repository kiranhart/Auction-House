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
import com.kiranhart.auctionhouse.auction.AuctionItem;
import com.kiranhart.auctionhouse.auction.AuctionPlayer;
import com.kiranhart.auctionhouse.inventory.AGUI;
import com.kiranhart.auctionhouse.util.Debugger;
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

public class ListingsGUI implements AGUI {

    private Player p;
    private List<List<AuctionItem>> chunks;
    private int page = 1;

    public ListingsGUI(Player p) {
        this.p = p;
        chunks = Lists.partition(new AuctionPlayer(p).getAuctionItems(), 45);
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

            AuctionItem item = null;
            if (e.getClick() == ClickType.LEFT) {
                String key = (String) NBTEditor.getString(clicked, "AuctionItemKey");
                for (AuctionItem auctionItem : Core.getInstance().getAuctionItems()) {
                    if (auctionItem.getKey().equalsIgnoreCase(key)) item = auctionItem;
                }

                item.setTime(0);
                Core.getInstance().getData().getConfig().set("expired." + item.getOwner().toString() + "." + item.getKey() + ".item", item.getItem());
                Core.getInstance().getData().getConfig().set("expired." + item.getOwner().toString() + "." + item.getKey() + ".display", AuctionAPI.getInstance().expiredAuctionItem(item));
                Core.getInstance().getData().saveConfig();
                Core.getInstance().getAuctionItems().remove(item);
                p.closeInventory();
                p.openInventory(new ListingsGUI(p).getInventory());
            }
        }

        if (slot == 45) {
            p.closeInventory();
            p.openInventory(new AuctionGUI(p).getInventory());
        }
    }

    @Override
    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this, 54, ChatColor.translateAlternateColorCodes('&', Core.getInstance().getConfig().getString("guis.active.title")));

        inventory.setItem(45, AuctionAPI.getInstance().createConfigurationItem("guis.active.items.return", 0, 0));
        inventory.setItem(48, AuctionAPI.getInstance().createConfigurationItem("guis.active.items.previouspage", 0, 0));
        inventory.setItem(50, AuctionAPI.getInstance().createConfigurationItem("guis.active.items.nextpage", 0, 0));
        inventory.setItem(53, AuctionAPI.getInstance().createConfigurationItem("guis.active.items.tutorial", 0, 0));

        if (chunks.size() != 0)
            chunks.get(getPage() - 1).forEach(item -> inventory.setItem(inventory.firstEmpty(), item.getAuctionStack(AuctionItem.AuctionItemType.LIST)));

        return inventory;
    }

    private ListingsGUI setPage(int page) {
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
