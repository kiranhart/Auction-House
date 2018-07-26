package com.shadebyte.auctionhouse.cmds.subcmds;

import com.shadebyte.auctionhouse.Core;
import com.shadebyte.auctionhouse.api.enums.Lang;
import com.shadebyte.auctionhouse.api.enums.Permissions;
import com.shadebyte.auctionhouse.cmds.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * The current file has been created by Kiran Hart
 * Date Created: 7/6/2018
 * Time Created: 11:51 AM
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise.
 */
public class ReloadCommand extends SubCommand {

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        if (!sender.hasPermission(Permissions.RELOAD_CMD.getNode())) {
            sender.sendMessage(Core.getInstance().getSettings().getPrefix() + Core.getInstance().getLocale().getMessage(Lang.NO_PERMISSION.getNode()));
            return;
        }

        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cStarting reload process"));
        long start = System.currentTimeMillis();

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bReloading config.yml"));
        Core.getInstance().saveConfig();
        Core.getInstance().reloadConfig();
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bReloading transactions.yml"));
        Core.getInstance().getTransactions().saveConfig();
        Core.getInstance().getTransactions().reloadConfig();
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bReloading data.yml"));
        Core.getInstance().getData().saveConfig();
        Core.getInstance().getData().reloadConfig();
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bReloading language file."));
        Core.getInstance().getLocale().reloadMessages();
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eAuction House reloaded, took &a" + (System.currentTimeMillis() - start) + "ms"));
    }

    @Override
    public String name() {
        return Core.getInstance().getCommandManager().reload;
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
