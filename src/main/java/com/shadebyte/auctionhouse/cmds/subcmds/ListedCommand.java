package com.shadebyte.auctionhouse.cmds.subcmds;

import com.shadebyte.auctionhouse.Core;
import com.shadebyte.auctionhouse.api.enums.Lang;
import com.shadebyte.auctionhouse.api.enums.Permissions;
import com.shadebyte.auctionhouse.cmds.SubCommand;
import com.shadebyte.auctionhouse.inventory.inventories.ExpiredGUI;
import com.shadebyte.auctionhouse.inventory.inventories.ListingsGUI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * The current file has been created by Kiran Hart
 * Date Created: 7/6/2018
 * Time Created: 11:51 AM
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise.
 */
public class ListedCommand extends SubCommand {

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        if (!sender.hasPermission(Permissions.LISTINGS_CMD.getNode())) {
            sender.sendMessage(Core.getInstance().getSettings().getPrefix() + Core.getInstance().getLocale().getMessage(Lang.NO_PERMISSION.getNode()));
            return;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Core.getInstance().getSettings().getPrefix() + Core.getInstance().getLocale().getMessage(Lang.PLAYERS_ONLY.getNode()));
            return;
        }

        Player p = (Player) sender;
        p.openInventory(new ListingsGUI(p).getInventory());
    }

    @Override
    public String name() {
        return Core.getInstance().getCommandManager().listed;
    }

    @Override
    public String info() {
        return null;
    }

    @Override
    public String[] aliases() {
        return new String[0];
    }
}
