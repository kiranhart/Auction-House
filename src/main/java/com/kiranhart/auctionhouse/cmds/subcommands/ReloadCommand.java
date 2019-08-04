package com.kiranhart.auctionhouse.cmds.subcommands;

import com.kiranhart.auctionhouse.Core;
import com.kiranhart.auctionhouse.api.statics.AuctionLang;
import com.kiranhart.auctionhouse.api.statics.AuctionPermissions;
import com.kiranhart.auctionhouse.cmds.SubCommand;
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

        if (!sender.hasPermission(AuctionPermissions.RELOAD_COMMAND)) {
            Core.getInstance().getLocale().getMessage(AuctionLang.NO_PERMISSION).sendPrefixedMessage(sender);
            return;
        }

        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cStarting reload process"));
        long start = System.currentTimeMillis();

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bReloading config.yml"));
        Core.getInstance().reloadConfig();
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bReloading transactions.yml"));
        Core.getInstance().getTransactions().reloadConfig();
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bReloading data.yml"));
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
        return new String[] {"rl"};
    }
}
