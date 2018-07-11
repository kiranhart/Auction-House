package com.shadebyte.auctionhouse.cmds.subcmds;

import com.shadebyte.auctionhouse.Core;
import com.shadebyte.auctionhouse.api.AuctionAPI;
import com.shadebyte.auctionhouse.api.enums.Lang;
import com.shadebyte.auctionhouse.api.enums.Permissions;
import com.shadebyte.auctionhouse.auction.AuctionItem;
import com.shadebyte.auctionhouse.auction.AuctionPlayer;
import com.shadebyte.auctionhouse.cmds.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * The current file has been created by Kiran Hart
 * Date Created: 7/6/2018
 * Time Created: 11:50 AM
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise.
 */
public class SellCommand extends SubCommand {

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        if (!sender.hasPermission(Permissions.BASE.getNode())) {
            sender.sendMessage(Core.getInstance().getSettings().getPrefix() + Core.getInstance().getLocale().getMessage(Lang.NO_PERMISSION.getNode()));
            return;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Core.getInstance().getSettings().getPrefix() + Core.getInstance().getLocale().getMessage(Lang.PLAYERS_ONLY.getNode()));
            return;
        }

        Player p = (Player) sender;

        if (args.length == 1) {
            p.sendMessage(Core.getInstance().getSettings().getPrefix() + Core.getInstance().getLocale().getMessage(Lang.CMD_SELL.getNode()));
            return;
        }

        if (args.length == 2) {
            if (AuctionAPI.getInstance().isNumeric(args[1])) {
                p.sendMessage(Core.getInstance().getSettings().getPrefix() + Core.getInstance().getLocale().getMessage(Lang.CMD_SELL.getNode()));
            } else {
                p.sendMessage(Core.getInstance().getSettings().getPrefix() + Core.getInstance().getLocale().getMessage(Lang.NOT_A_NUMBER.getNode()));
            }
        }

        if (args.length == 3) {
            if (AuctionAPI.getInstance().isNumeric(args[1]) && AuctionAPI.getInstance().isNumeric(args[2])) {
                p.sendMessage(Core.getInstance().getSettings().getPrefix() + Core.getInstance().getLocale().getMessage(Lang.CMD_SELL.getNode()));
            } else {
                p.sendMessage(Core.getInstance().getSettings().getPrefix() + Core.getInstance().getLocale().getMessage(Lang.NOT_A_NUMBER.getNode()));
            }
        }

        if (args.length == 4) {
            if (AuctionAPI.getInstance().isNumeric(args[1]) && AuctionAPI.getInstance().isNumeric(args[2]) && AuctionAPI.getInstance().isNumeric(args[3])) {
                if (new AuctionPlayer(p).hasMaximumAuctionsActive()) {
                    p.sendMessage(Core.getInstance().getSettings().getPrefix() + Core.getInstance().getLocale().getMessage(Lang.AUCTION_MAX.getNode()));
                    return;
                }

                int buyNow = Integer.parseInt(args[1]);
                int startPrice = Integer.parseInt(args[2]);
                int increment = Integer.parseInt(args[3]);
                AuctionItem auctionItem = new AuctionItem(p.getUniqueId().toString(), AuctionAPI.getItemInHand(p), 3600, startPrice, increment, buyNow);
                Core.getInstance().auctionItems.add(0, auctionItem);
                p.sendMessage(Core.getInstance().getSettings().getPrefix() + Core.getInstance().getLocale().getMessage(Lang.AUCTION_LISTED.getNode()).replace("{itemname}", auctionItem.getDisplayName()).replace("{price}", AuctionAPI.getInstance().friendlyNumber(startPrice)));
            } else {
                p.sendMessage(Core.getInstance().getSettings().getPrefix() + Core.getInstance().getLocale().getMessage(Lang.NOT_A_NUMBER.getNode()));
            }
        }
    }

    @Override
    public String name() {
        return Core.getInstance().getCommandManager().sell;
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
