package com.shadebyte.auctionhouse.inventory.inventories;

import com.shadebyte.auctionhouse.Core;
import com.shadebyte.auctionhouse.api.AuctionAPI;
import com.shadebyte.auctionhouse.auction.Transaction;
import com.shadebyte.auctionhouse.inventory.AGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * The current file has been created by Kiran Hart
 * Date Created: 8/7/2018
 * Time Created: 4:32 PM
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise.
 */
public class SingleTransactionGUI implements AGUI {

    private String transactionNode;

    public SingleTransactionGUI(String transactionNode) {
        this.transactionNode = transactionNode;
    }

    @Override
    public void click(InventoryClickEvent e, ItemStack clicked, int slot) {
        e.setCancelled(true);
    }

    @Override
    public void close(InventoryCloseEvent e) {

    }

    @Override
    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this, 54, ChatColor.translateAlternateColorCodes('&', Core.getInstance().getConfig().getString("gui.singletransaction.title")));
        //Fill Inventory
        for (int i = 0; i < inventory.getSize(); i++) inventory.setItem(i, AuctionAPI.getInstance().createConfigItem("gui.singletransaction.items.background", 0, 0));

        inventory.setItem(13, Core.getInstance().getTransactions().getConfig().getItemStack("transactions." + transactionNode + ".item"));
        inventory.setItem(39, AuctionAPI.getInstance().createConfigItem("gui.singletransaction.items.back", 0, 0));
        inventory.setItem(40, AuctionAPI.getInstance().createConfigItem("gui.singletransaction.items.you", 0, 0));
        inventory.setItem(41, AuctionAPI.getInstance().createConfigItem("gui.singletransaction.items.auctionhouse", 0, 0));

        inventory.setItem(30, AuctionAPI.getInstance().createUserHead("gui.singletransaction.items.seller", Core.getInstance().getTransactions().getConfig().getString("transactions." + transactionNode + ".seller"), "", 0));
        inventory.setItem(31, Core.getInstance().getTransactions().getConfig().getItemStack("transactions." + transactionNode + ".receipt"));
        inventory.setItem(32, AuctionAPI.getInstance().createUserHead("gui.singletransaction.items.buyer", "", Core.getInstance().getTransactions().getConfig().getString("transactions." + transactionNode + ".buyer"), 1));

        inventory.setItem(48, AuctionAPI.getInstance().createTransactionConfigItem("gui.singletransaction.items.startprice", "", "", Core.getInstance().getTransactions().getConfig().getInt("transactions." + transactionNode + ".start-price"),0,0));
        inventory.setItem(49, AuctionAPI.getInstance().createTransactionConfigItem("gui.singletransaction.items.increment", "", "", 0,Core.getInstance().getTransactions().getConfig().getInt("transactions." + transactionNode + ".bid-increment"),0));
        inventory.setItem(50, AuctionAPI.getInstance().createTransactionConfigItem("gui.singletransaction.items.buynowprice", "", "", 0,0,Core.getInstance().getTransactions().getConfig().getInt("transactions." + transactionNode + ".buy-now-price")));

        return inventory;
    }
}
