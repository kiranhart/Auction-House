package com.kiranhart.auctionhouse.cmds.subcommands;

import com.kiranhart.auctionhouse.Core;
import com.kiranhart.auctionhouse.api.statics.AuctionLang;
import com.kiranhart.auctionhouse.api.statics.AuctionPermissions;
import com.kiranhart.auctionhouse.cmds.SubCommand;
import org.bukkit.command.CommandSender;

/**
 * The current file has been created by Kiran Hart
 * Date Created: 7/6/2018
 * Time Created: 11:51 AM
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise.
 */
public class LockCommand extends SubCommand {

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        if (!sender.hasPermission(AuctionPermissions.LOCK_COMMAND) || !sender.hasPermission(AuctionPermissions.ADMIN)) {
            Core.getInstance().getLocale().getMessage(AuctionLang.NO_PERMISSION).sendPrefixedMessage(sender);
            return;
        }

        if (Core.getInstance().isLocked()) {
            Core.getInstance().setLocked(false);
            Core.getInstance().getLocale().getMessage(AuctionLang.UNLOCKED).sendPrefixedMessage(sender);
        } else {
            Core.getInstance().setLocked(true);
            Core.getInstance().getLocale().getMessage(AuctionLang.LOCKED).sendPrefixedMessage(sender);
        }
    }

    @Override
    public String name() {
        return Core.getInstance().getCommandManager().lock;
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
