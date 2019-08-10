package com.kiranhart.auctionhouse.inventory.inventories;
/*
    The current file was created by Kiran Hart
    Date: August 03 2019
    Time: 8:10 PM
    
    Code within this class is not to be redistributed without proper permission.
*/

import com.kiranhart.auctionhouse.Core;
import com.kiranhart.auctionhouse.api.AuctionAPI;
import com.kiranhart.auctionhouse.api.version.NBTEditor;
import com.kiranhart.auctionhouse.inventory.AGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class SingleTransactionGUI implements AGUI {

    private String transactionNode;

    public SingleTransactionGUI(String transactionNode) {
        this.transactionNode = transactionNode;
    }

    @Override
    public void click(InventoryClickEvent e, ItemStack clicked, int slot) {
        e.setCancelled(true);
        Player p = (Player) e.getWhoClicked();

        if (slot == 39) p.openInventory(new TransactionSelectGUI().getInventory());
        if (slot == 40) p.openInventory(new PlayerTransactionsGUI(p).getInventory());
        if (slot == 41) p.openInventory(new AuctionGUI(p).getInventory());

        if (slot == 30 && NBTEditor.getString(clicked, "AuctionSellerHead") != null) {
            String id = (String) NBTEditor.getString(e.getCurrentItem(), "AuctionSellerHead");
            p.openInventory(new PlayerTransactionsGUI(Bukkit.getOfflinePlayer(UUID.fromString(id)).getPlayer()).getInventory());
        }

        if (slot == 32 && NBTEditor.getString(clicked, "AuctionBuyerHead") != null) {
            String id = (String) NBTEditor.getString(e.getCurrentItem(), "AuctionBuyerHead");
            p.openInventory(new PlayerTransactionsGUI(Bukkit.getOfflinePlayer(UUID.fromString(id)).getPlayer()).getInventory());
        }
    }

    @Override
    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this, 54, ChatColor.translateAlternateColorCodes('&', Core.getInstance().getConfig().getString("guis.singletransaction.title")));

        //Fill Inventory
        for (int i = 0; i < inventory.getSize(); i++)
            inventory.setItem(i, AuctionAPI.getInstance().createConfigurationItem("guis.singletransaction.items.background", 0, 0));

        inventory.setItem(13, Core.getInstance().getTransactions().getConfig().getItemStack("transactions." + transactionNode + ".item"));
        inventory.setItem(39, AuctionAPI.getInstance().createConfigurationItem("guis.singletransaction.items.back", 0, 0));
        inventory.setItem(40, AuctionAPI.getInstance().createConfigurationItem("guis.singletransaction.items.you", 0, 0));
        inventory.setItem(41, AuctionAPI.getInstance().createConfigurationItem("guis.singletransaction.items.auctionhouse", 0, 0));

        inventory.setItem(30, AuctionAPI.getInstance().createUserHead(AuctionAPI.AuctionHeadType.BUYER, transactionNode));
        inventory.setItem(31, Core.getInstance().getTransactions().getConfig().getItemStack("transactions." + transactionNode + ".receipt"));
        inventory.setItem(32, AuctionAPI.getInstance().createUserHead(AuctionAPI.AuctionHeadType.SELLER, transactionNode));

        inventory.setItem(48, AuctionAPI.getInstance().createTransactionConfigItem("guis.singletransaction.items.startprice", "", "", Core.getInstance().getTransactions().getConfig().getInt("transactions." + transactionNode + ".start-price"), 0, 0));
        inventory.setItem(49, AuctionAPI.getInstance().createTransactionConfigItem("guis.singletransaction.items.increment", "", "", 0, Core.getInstance().getTransactions().getConfig().getInt("transactions." + transactionNode + ".bid-increment"), 0));
        inventory.setItem(50, AuctionAPI.getInstance().createTransactionConfigItem("guis.singletransaction.items.buynowprice", "", "", 0, 0, Core.getInstance().getTransactions().getConfig().getInt("transactions." + transactionNode + ".buy-now-price")));

        return inventory;
    }

    @Override
    public void close(InventoryCloseEvent e) {
    }
}
