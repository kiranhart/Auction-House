package com.shadebyte.auctionhouse.events;

import com.shadebyte.auctionhouse.Core;
import com.shadebyte.auctionhouse.api.AuctionAPI;
import com.shadebyte.auctionhouse.api.enums.Lang;
import com.shadebyte.auctionhouse.api.enums.Permissions;
import com.shadebyte.auctionhouse.inventory.inventories.SingleTransactionGUI;
import com.shadebyte.auctionhouse.util.NBTEditor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * The current file has been created by Kiran Hart
 * Date Created: 8/9/2018
 * Time Created: 11:26 AM
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise.
 */
public class PlayerListener implements Listener {

    @EventHandler
    public void onReceiptRightClick(PlayerInteractEvent e) {
        Player p = e.getPlayer();

        if (e.getAction() != Action.RIGHT_CLICK_AIR) {
            return;
        }

        if (AuctionAPI.getItemInHand(p).getType() == Material.AIR || AuctionAPI.getItemInHand(p) == null) {
            return;
        }

        if (NBTEditor.getItemTag(AuctionAPI.getItemInHand(p), "AuctionReceipt") == null) {
            return;
        }

        if (!p.hasPermission(Permissions.USE_RECEIPT.getNode())) {
            p.sendMessage(Core.getInstance().getSettings().getPrefix() + Core.getInstance().getLocale().getMessage(Lang.NO_PERMISSION.getNode()));
            return;
        }

        String node = (String) NBTEditor.getItemTag(AuctionAPI.getItemInHand(p), "AuctionReceipt");

        if (!Core.getInstance().getTransactions().getConfig().contains("transactions." + node)) {
            p.sendMessage(Core.getInstance().getSettings().getPrefix() + Core.getInstance().getLocale().getMessage(Lang.INVALID_TRANSACTION.getNode()));
            return;
        }

        p.openInventory(new SingleTransactionGUI(node).getInventory());
    }
}
