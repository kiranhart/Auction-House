package com.shadebyte.auctionhouse.inventory.inventories;

import com.google.common.collect.Lists;
import com.shadebyte.auctionhouse.Core;
import com.shadebyte.auctionhouse.api.AuctionAPI;
import com.shadebyte.auctionhouse.auction.AuctionItem;
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
public class AuctionGUI implements AGUI {

    private static AuctionGUI instance;
    private Player p;

    private AuctionGUI(Player p) {
        this.p = p;
    }

    public static AuctionGUI getInstance(Player p) {
        if (instance == null) {
            instance = new AuctionGUI(p);
        }
        return instance;
    }

    private int page = 1;


    @Override
    public void click(InventoryClickEvent e, ItemStack clicked, int slot) {
        e.setCancelled(true);
        Player p = (Player) e.getWhoClicked();


        if (page >= 1 && slot == 48) p.openInventory(this.setPage(this.getPage() - 1).getInventory());
        if (page >= 1 && slot == 50) p.openInventory(this.setPage(this.getPage() + 1).getInventory());


        if (slot == 49) {
            p.closeInventory();
            p.openInventory(AuctionGUI.getInstance(p).getInventory());
        }

        if (slot >= 0 & slot <= 44) {
            if (clicked == null || clicked.getType() == Material.AIR) {
                return;
            }

            AuctionItem item = null;

            if (e.getClick() == ClickType.LEFT) {
                String key = (String) NBTEditor.getItemTag(clicked, "AuctionItemKey");
                for (AuctionItem auctionItem : Core.getInstance().auctionItems) {
                    if (auctionItem.getKey().equalsIgnoreCase(key)) item = auctionItem;
                }

                item.setCurrentPrice(item.getCurrentPrice() + item.getBidIncrement());
                p.closeInventory();
                p.openInventory(AuctionGUI.getInstance(p).getInventory());
            }

            if (e.getClick() == ClickType.RIGHT) {
                String key = (String) NBTEditor.getItemTag(clicked, "AuctionItemKey");
                for (AuctionItem auctionItem : Core.getInstance().auctionItems) {
                    if (auctionItem.getKey().equalsIgnoreCase(key)) item = auctionItem;
                }

                p.getInventory().addItem(item.getItem());
                item.setTime(0);
                Core.getInstance().auctionItems.remove(item);

                p.closeInventory();
                p.openInventory(AuctionGUI.getInstance(p).getInventory());
            }
        }
    }

    @Override
    public void close(InventoryCloseEvent e) {

    }

    @Override
    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this, 54, ChatColor.translateAlternateColorCodes('&', Core.getInstance().getConfig().getString("gui.auction.title")));

        //Bottom Row
        inventory.setItem(45, AuctionAPI.getInstance().createConfigItem("gui.auction.items.yourauctions"));
        inventory.setItem(46, AuctionAPI.getInstance().createConfigItem("gui.auction.items.collectionbin"));
        inventory.setItem(48, AuctionAPI.getInstance().createConfigItem("gui.auction.items.previouspage"));
        inventory.setItem(49, AuctionAPI.getInstance().createConfigItem("gui.auction.items.refresh"));
        inventory.setItem(50, AuctionAPI.getInstance().createConfigItem("gui.auction.items.nextpage"));
        inventory.setItem(52, AuctionAPI.getInstance().createConfigItem("gui.auction.items.howtosell"));
        inventory.setItem(53, AuctionAPI.getInstance().createConfigItem("gui.auction.items.guide"));

        //Pagination
        List<List<AuctionItem>> chunks = Lists.partition(Core.getInstance().auctionItems, 45);

        if (chunks.size() != 0)
            chunks.get(getPage() - 1).forEach(item -> inventory.setItem(inventory.firstEmpty(), item.auctionStack()));

        return inventory;
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
