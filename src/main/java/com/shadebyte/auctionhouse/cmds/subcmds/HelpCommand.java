package com.shadebyte.auctionhouse.cmds.subcmds;

import com.shadebyte.auctionhouse.Core;
import com.shadebyte.auctionhouse.api.enums.Lang;
import com.shadebyte.auctionhouse.api.enums.Permissions;
import com.shadebyte.auctionhouse.cmds.SubCommand;
import org.bukkit.command.CommandSender;

/**
 * The current file has been created by Kiran Hart
 * Date Created: 7/6/2018
 * Time Created: 11:51 AM
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise.
 */
public class HelpCommand extends SubCommand {

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        if (!sender.hasPermission(Permissions.HELP_CMD.getNode())) {
            sender.sendMessage(Core.getInstance().getSettings().getPrefix() + Core.getInstance().getLocale().getMessage(Lang.NO_PERMISSION.getNode()));
            return;
        }

        for (int i = 0; i <= 8; i++) {
            sender.sendMessage(Core.getInstance().getLocale().getMessage("cmd.help." + i));
        }
    }

    @Override
    public String name() {
        return Core.getInstance().getCommandManager().help;
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
