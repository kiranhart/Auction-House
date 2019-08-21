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
public class EndAllCommand extends SubCommand {

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        if (!sender.hasPermission(AuctionPermissions.ENDALL_COMMAND) || !sender.hasPermission(AuctionPermissions.ADMIN)) {
            Core.getInstance().getLocale().getMessage(AuctionLang.NO_PERMISSION).sendPrefixedMessage(sender);
            return;
        }

        if (!Core.getInstance().isLocked()) {
            Core.getInstance().getLocale().getMessage(AuctionLang.MUST_BE_LOCKED).sendPrefixedMessage(sender);
            return;
        }

        Core.getInstance().getAuctionItems().forEach(auctionItem -> {
            if (auctionItem.getTime() != 0) auctionItem.setTime(0);
        });

        Core.getInstance().getLocale().getMessage(AuctionLang.END_ALL).sendPrefixedMessage(sender);
    }

    @Override
    public String name() {
        return Core.getInstance().getCommandManager().endall;
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
