package com.kiranhart.auctionhouse.cmds.subcommands;

import com.kiranhart.auctionhouse.Core;
import com.kiranhart.auctionhouse.api.statics.AuctionLang;
import com.kiranhart.auctionhouse.api.statics.AuctionPermissions;
import com.kiranhart.auctionhouse.cmds.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * The current file has been created by Kiran Hart
 * Date Created: 7/6/2018
 * Time Created: 11:51 AM
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise.
 */
public class ExpiredCommand extends SubCommand {

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        if (!sender.hasPermission(AuctionPermissions.EXPIRED_COMMAND)) {
            Core.getInstance().getLocale().getMessage(AuctionLang.NO_PERMISSION).sendPrefixedMessage(sender);
            return;
        }

        if (!(sender instanceof Player)) {
            Core.getInstance().getLocale().getMessage(AuctionLang.PLAYERS_ONLY).sendPrefixedMessage(sender);
            return;
        }

        Player p = (Player) sender;
        //  p.openInventory(new ExpiredGUI(p).getInventory());
    }

    @Override
    public String name() {
        return Core.getInstance().getCommandManager().expired;
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
