package com.kiranhart.auctionhouse.cmds.subcommands;

import com.kiranhart.auctionhouse.Core;
import com.kiranhart.auctionhouse.api.AuctionAPI;
import com.kiranhart.auctionhouse.api.statics.AuctionLang;
import com.kiranhart.auctionhouse.api.statics.AuctionPermissions;
import com.kiranhart.auctionhouse.auction.Transaction;
import com.kiranhart.auctionhouse.cmds.SubCommand;
import com.kiranhart.auctionhouse.util.Debugger;
import com.kiranhart.auctionhouse.util.storage.Database;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * The current file has been created by Kiran Hart
 * Date Created: 7/6/2018
 * Time Created: 11:51 AM
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise.
 */
public class UploadTransactionsCommand extends SubCommand {

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        if (!sender.hasPermission(AuctionPermissions.UPLOAD_TRANSACTIONS_COMMAND)) {
            Core.getInstance().getLocale().getMessage(AuctionLang.NO_PERMISSION).sendPrefixedMessage(sender);
            return;
        }

        try {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l(!) Attempting to gather and upload every transaction."));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l(!) Server may freeze / hang for large transaction files."));
            long start = System.currentTimeMillis();
            Database.getInstance().performTransactionUpload(AuctionAPI.getInstance().requestEveryFlatFileTransaction().toArray(new Transaction[AuctionAPI.getInstance().requestEveryFlatFileTransaction().size()]));
        } catch (Exception e) {
            Debugger.report(e);
        }
    }

    @Override
    public String name() {
        return Core.getInstance().getCommandManager().uploadtransactions;
    }

    @Override
    public String info() {
        return null;
    }

    @Override
    public String[] aliases() {
        return new String[]{"uploadtrans", "uptransactions", "upldtrans"};
    }
}
