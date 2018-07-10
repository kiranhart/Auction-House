package com.shadebyte.auctionhouse.inventory.inventories;

import com.google.common.collect.Lists;
import com.shadebyte.auctionhouse.Core;
import com.shadebyte.auctionhouse.api.AuctionAPI;
import com.shadebyte.auctionhouse.auction.AuctionItem;
import com.shadebyte.auctionhouse.auction.AuctionPlayer;
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

        try {
            if (page >= 1 && slot == 48) p.openInventory(this.setPage(this.getPage() - 1).getInventory());
            if (page >= 1 && slot == 50) p.openInventory(this.setPage(this.getPage() + 1).getInventory());
        } catch (Exception ex) {
            //Hide for now
        }

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

                if (Core.getEconomy().getBalance(p) < item.getBidIncrement()) {
                    e.getClickedInventory().setItem(slot, AuctionAPI.getInstance().createConfigItem("gui.auction.items.not-enough-money", 0, 0));
                    Bukkit.getServer().getScheduler().runTaskLater(Core.getInstance(), () -> {
                        p.closeInventory();
                        p.openInventory(AuctionGUI.getInstance(p).getInventory());
                    }, 1);

                    // p.sendMessage(Core.getInstance().getSettings().getPrefix() + Core.getInstance().getLocale().getMessage(Lang.NOT_ENOUGH_MONEY.getNode()));
                    return;
                }

                item.setCurrentPrice(item.getCurrentPrice() + item.getBidIncrement());
                item.setHighestBidder(p.getUniqueId().toString());
                p.closeInventory();
                p.openInventory(AuctionGUI.getInstance(p).getInventory());
            }

            if (e.getClick() == ClickType.RIGHT) {
                String key = (String) NBTEditor.getItemTag(clicked, "AuctionItemKey");
                for (AuctionItem auctionItem : Core.getInstance().auctionItems) {
                    if (auctionItem.getKey().equalsIgnoreCase(key)) item = auctionItem;
                }

                p.closeInventory();
                p.openInventory(ConfirmationGUI.getInstance(item).getInventory());
//                p.getInventory().addItem(item.getItem());
//                item.setTime(0);
//                Core.getInstance().auctionItems.remove(item);
//
//                p.closeInventory();
//                p.openInventory(AuctionGUI.getInstance(p).getInventory());
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
        inventory.setItem(45, AuctionAPI.getInstance().createConfigItem("gui.auction.items.yourauctions", new AuctionPlayer(p).getTotalActiveAuctions(), 0));
        inventory.setItem(46, AuctionAPI.getInstance().createConfigItem("gui.auction.items.collectionbin", 0, new AuctionPlayer(p).getTotalExpiredAuctions()));
        inventory.setItem(48, AuctionAPI.getInstance().createConfigItem("gui.auction.items.previouspage", 0, 0));
        inventory.setItem(49, AuctionAPI.getInstance().createConfigItem("gui.auction.items.refresh", 0, 0));
        inventory.setItem(50, AuctionAPI.getInstance().createConfigItem("gui.auction.items.nextpage", 0, 0));
        inventory.setItem(52, AuctionAPI.getInstance().createConfigItem("gui.auction.items.howtosell", 0, 0));
        inventory.setItem(53, AuctionAPI.getInstance().createConfigItem("gui.auction.items.guide", 0, 0));

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
