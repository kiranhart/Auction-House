package com.kiranhart.auctionhouse.cmds.subcommands;


import com.kiranhart.auctionhouse.Core;
import com.kiranhart.auctionhouse.api.AuctionAPI;
import com.kiranhart.auctionhouse.api.events.AuctionStartEvent;
import com.kiranhart.auctionhouse.api.statics.AuctionLang;
import com.kiranhart.auctionhouse.api.statics.AuctionPermissions;
import com.kiranhart.auctionhouse.api.statics.AuctionSettings;
import com.kiranhart.auctionhouse.auction.AuctionItem;
import com.kiranhart.auctionhouse.auction.AuctionPlayer;
import com.kiranhart.auctionhouse.cmds.SubCommand;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The current file has been created by Kiran Hart
 * Date Created: 7/6/2018
 * Time Created: 11:50 AM
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise.
 */
public class SellCommand extends SubCommand {

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        if (!sender.hasPermission(AuctionPermissions.SELL_COMMAND)) {
            Core.getInstance().getLocale().getMessage(AuctionLang.NO_PERMISSION).sendPrefixedMessage(sender);
            return;
        }

        if (!(sender instanceof Player)) {
            Core.getInstance().getLocale().getMessage(AuctionLang.PLAYERS_ONLY).sendPrefixedMessage(sender);
            return;
        }

        Player p = (Player) sender;

        //TODO FINISH BLOCKED ITEMS ON SELL COMMAND
//        for (String bitems : Core.getInstance().getConfig().getStringList("blocked-items")) {
//            String[] item = bitems.split(":");
//            if (AuctionAPI.getItemInHand(p) != null || AuctionAPI.getItemInHand(p).getType() != Material.AIR) {
//                if (AuctionAPI.getItemInHand(p).getType() == Material.valueOf(item[0].toUpperCase()) && AuctionAPI.getItemInHand(p).getDurability() == Short.parseShort(item[1])) {
//                    p.sendMessage(Core.getInstance().getSettings().getPrefix() + Core.getInstance().getLocale().getMessage(Lang.BLOCKED_ITEM.getNode()));
//                    return;
//                }
//            }
//        }

        int timeLimit;
        List<Integer> times = new ArrayList<>();

        Core.getInstance().getConfig().getConfigurationSection("auctiontime").getKeys(false).forEach(perm -> {
            if (p.hasPermission("auctiontime." + String.valueOf(perm))) {
                times.add(Core.getInstance().getConfig().getInt("auctiontime." + perm));
            }
        });

        timeLimit = (times.size() <= 0) ? AuctionSettings.DEFAULT_AUCTION_TIME : Collections.max(times);

        if (args.length == 1) {
            if (AuctionSettings.USE_BIDDING_SYSTEM) {
                Core.getInstance().getLocale().getMessage(AuctionLang.COMMAND_SELL_WITH_BID).sendPrefixedMessage(sender);
            } else {
                Core.getInstance().getLocale().getMessage(AuctionLang.COMMAND_SELL_NO_BIDS).sendPrefixedMessage(sender);
            }
            return;
        }

        if (args.length == 2) {
            if (AuctionAPI.getInstance().isNumeric(args[1])) {
                if (AuctionSettings.USE_BIDDING_SYSTEM) {
                    Core.getInstance().getLocale().getMessage(AuctionLang.COMMAND_SELL_WITH_BID).sendPrefixedMessage(sender);
                } else {

                    if (new AuctionPlayer(p).getLimit() - 1 < new AuctionPlayer(p).getTotalActiveAuctions()) {
                        Core.getInstance().getLocale().getMessage(AuctionLang.AUCTION_MAX).sendPrefixedMessage(sender);
                        return;
                    }

                    long buyNow = Long.parseLong(args[1]);

                    //Max / Min Prices
                    if (buyNow > AuctionSettings.MAX_AUCTION_PRICE) {
                        Core.getInstance().getLocale().getMessage(AuctionLang.PRICE_MAX_AUCTION).sendPrefixedMessage(sender);
                        return;
                    }

                    if (buyNow < AuctionSettings.MIN_AUCTION_PRICE) {
                        Core.getInstance().getLocale().getMessage(AuctionLang.PRICE_MIN_AUCTION).sendPrefixedMessage(sender);
                        return;
                    }

                    if (AuctionAPI.getInstance().getItemInHand(p) == null || AuctionAPI.getInstance().getItemInHand(p).getType() == Material.AIR) {
                        Core.getInstance().getLocale().getMessage(AuctionLang.AIR_IN_HAND).sendPrefixedMessage(sender);
                        return;
                    }

                    AuctionItem auctionItem = new AuctionItem(p.getUniqueId(), AuctionAPI.getInstance().getItemInHand(p), timeLimit, buyNow, 0, buyNow);
                    AuctionStartEvent auctionStartEvent = new AuctionStartEvent(auctionItem);
                    Core.getInstance().getServer().getPluginManager().callEvent(auctionStartEvent);

                    if (!auctionStartEvent.isCancelled()) {
                        Core.getInstance().getAuctionItems().add(0, auctionItem);
                        Core.getInstance().getLocale().getMessage(AuctionLang.AUCTION_LISTED).processPlaceholder("itemname", auctionItem.getDisplayName()).processPlaceholder("price", AuctionAPI.getInstance().getFriendlyNumber(buyNow)).sendPrefixedMessage(p);

                        AuctionAPI.getInstance().setItemInHand(p, null);
                        p.updateInventory();

                        //Discord Hook //TODO FINISH DISCORD WEB HOOK
                        if (Core.getInstance().getConfig().getBoolean("discord.enabled")) {

                            //Discord Hook
//                            if (Core.getInstance().getConfig().getBoolean("discord.enabled")) {
//                                DiscordHook discordHook = new DiscordHook(Core.getInstance().getConfig().getString("discord.webhook"));
//
//
//                                List<FieldEmbed> embeds = new ArrayList<>();
//                                for (String s : Core.getInstance().getConfig().getConfigurationSection("discord.add").getKeys(false)) {
//                                    embeds.add(new DiscordMessageWrapper("discord.add." + s, auctionItem).getFieldEmbed());
//                                }
//
//                                DiscordEmbed de = DiscordEmbed.builder()
//                                        .title(Core.getInstance().getConfig().getString("discord.title"))
//                                        .color(1)
//                                        .fields(embeds)
//                                        .build();
//
//                                DiscordMessage dm = DiscordMessage.builder().username(Core.getInstance().getConfig().getString("discord.username")).content("").avatarUrl(Core.getInstance().getConfig().getString("discord.profilepicture")).embeds(Arrays.asList(de)).build();
//                                discordHook.send(dm);
//                            }
                        }
                    }
                }
            } else {
                Core.getInstance().getLocale().getMessage(AuctionLang.NOT_A_NUMBER).processPlaceholder("value", args[1]).sendPrefixedMessage(sender);
            }
        }

        if (args.length == 3) {
            if (AuctionSettings.USE_BIDDING_SYSTEM) {
                if (AuctionAPI.getInstance().isNumeric(args[1]) && AuctionAPI.getInstance().isNumeric(args[2])) {
                    Core.getInstance().getLocale().getMessage(AuctionLang.COMMAND_SELL_WITH_BID).sendPrefixedMessage(sender);
                } else {
                    Core.getInstance().getLocale().getMessage(AuctionLang.NOT_A_NUMBER).processPlaceholder("value", args[1]).sendPrefixedMessage(sender);
                }
            }
        }

        if (args.length == 4) {
            if (AuctionSettings.USE_BIDDING_SYSTEM) {
                if (AuctionAPI.getInstance().isNumeric(args[1]) && AuctionAPI.getInstance().isNumeric(args[2]) && AuctionAPI.getInstance().isNumeric(args[3])) {
                    if (new AuctionPlayer(p).getLimit() - 1 < new AuctionPlayer(p).getTotalActiveAuctions()) {
                        Core.getInstance().getLocale().getMessage(AuctionLang.AUCTION_MAX).sendPrefixedMessage(sender);
                        return;
                    }

                    long buyNow = Long.parseLong(args[1]);
                    long startPrice = Long.parseLong(args[2]);
                    long increment = Long.parseLong(args[3]);

                    if (AuctionAPI.getInstance().getItemInHand(p) == null || AuctionAPI.getInstance().getItemInHand(p).getType() == Material.AIR) {
                        Core.getInstance().getLocale().getMessage(AuctionLang.AIR_IN_HAND).sendPrefixedMessage(sender);
                        return;
                    }

                    //Max Prices
                    if (buyNow > AuctionSettings.MAX_AUCTION_PRICE) {
                        Core.getInstance().getLocale().getMessage(AuctionLang.PRICE_MAX_AUCTION).sendPrefixedMessage(sender);
                        return;
                    }

                    if (startPrice > AuctionSettings.MAX_AUCTION_START) {
                        Core.getInstance().getLocale().getMessage(AuctionLang.PRICE_MAX_START).sendPrefixedMessage(sender);
                        return;
                    }

                    if (increment > AuctionSettings.MAX_AUCTION_INCREMENT) {
                        Core.getInstance().getLocale().getMessage(AuctionLang.PRICE_MAX_INCREMENT).sendPrefixedMessage(sender);
                        return;
                    }

                    //Min Prices
                    if (buyNow < AuctionSettings.MIN_AUCTION_PRICE) {
                        Core.getInstance().getLocale().getMessage(AuctionLang.PRICE_MIN_AUCTION).sendPrefixedMessage(sender);
                        return;
                    }

                    if (startPrice < AuctionSettings.MIN_AUCTION_START) {
                        Core.getInstance().getLocale().getMessage(AuctionLang.PRICE_MIN_START).sendPrefixedMessage(sender);
                        return;
                    }

                    if (increment < AuctionSettings.MIN_AUCTION_INCREMENT) {
                        Core.getInstance().getLocale().getMessage(AuctionLang.PRICE_MIN_INCREMENT).sendPrefixedMessage(sender);
                        return;
                    }

                    AuctionItem auctionItem = new AuctionItem(p.getUniqueId(), AuctionAPI.getInstance().getItemInHand(p), timeLimit, startPrice, increment, buyNow);
                    AuctionStartEvent auctionStartEvent = new AuctionStartEvent(auctionItem);
                    Core.getInstance().getServer().getPluginManager().callEvent(auctionStartEvent);

                    if (!auctionStartEvent.isCancelled()) {
                        Core.getInstance().getAuctionItems().add(0, auctionItem);
                        Core.getInstance().getLocale().getMessage(AuctionLang.AUCTION_LISTED_WITH_BID).processPlaceholder("itemname", auctionItem.getDisplayName()).processPlaceholder("price", AuctionAPI.getInstance().getFriendlyNumber(buyNow)).sendPrefixedMessage(p);

                        //Discord Hook //TODO FINISH BID DISCORD HOOK
//                        if (Core.getInstance().getConfig().getBoolean("discord.enabled")) {
//
//                            //Discord Hook
//                            if (Core.getInstance().getConfig().getBoolean("discord.enabled")) {
//                                DiscordHook discordHook = new DiscordHook(Core.getInstance().getConfig().getString("discord.webhook"));
//
//
//                                List<FieldEmbed> embeds = new ArrayList<>();
//                                for (String s : Core.getInstance().getConfig().getConfigurationSection("discord.add").getKeys(false)) {
//                                    embeds.add(new DiscordMessageWrapper("discord.add." + s, auctionItem).getFieldEmbed());
//                                }
//
//                                DiscordEmbed de = DiscordEmbed.builder()
//                                        .title(Core.getInstance().getConfig().getString("discord.title"))
//                                        .color(1)
//                                        .fields(embeds)
//                                        .build();
//
//                                DiscordMessage dm = DiscordMessage.builder().username(Core.getInstance().getConfig().getString("discord.username")).content("").avatarUrl(Core.getInstance().getConfig().getString("discord.profilepicture")).embeds(Arrays.asList(de)).build();
//                                discordHook.send(dm);
//                            }
//                        }

                        AuctionAPI.getInstance().setItemInHand(p, null);
                    }
                } else {
                    Core.getInstance().getLocale().getMessage(AuctionLang.NOT_A_NUMBER).processPlaceholder("value", args[1]).sendPrefixedMessage(sender);
                }
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
