package com.shadebyte.auctionhouse.events;

import com.shadebyte.auctionhouse.Core;
import com.shadebyte.auctionhouse.api.AuctionAPI;
import com.shadebyte.auctionhouse.api.discordwebhook.DiscordEmbed;
import com.shadebyte.auctionhouse.api.discordwebhook.DiscordHook;
import com.shadebyte.auctionhouse.api.discordwebhook.DiscordMessage;
import com.shadebyte.auctionhouse.api.discordwebhook.embed.FieldEmbed;
import com.shadebyte.auctionhouse.api.event.TransactionCompleteEvent;
import com.shadebyte.auctionhouse.auction.Receipt;
import com.shadebyte.auctionhouse.auction.Transaction;
import com.shadebyte.auctionhouse.util.Debugger;
import com.shadebyte.auctionhouse.util.storage.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.UUID;

/**
 * The current file has been created by Kiran Hart
 * Date Created: 7/24/2018
 * Time Created: 12:08 PM
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise.
 */
public class TransactionListener implements Listener {

    @EventHandler
    public void onTransactionComplete(TransactionCompleteEvent e) {

        if (Core.getInstance().getConfig().getBoolean("receipt.give-on-transaction")) {
            Player buyer = Bukkit.getPlayer(UUID.fromString(e.getTransaction().getBuyer()));
            Player seller = Bukkit.getPlayer(UUID.fromString(e.getTransaction().getAuctionItem().getOwner()));

            if (buyer != null) {
                buyer.getInventory().addItem(new Receipt(e.getTransaction()).getReceipt());
            }

            if (seller != null) {
                seller.getInventory().addItem(new Receipt(e.getTransaction()).getReceipt());
            }
        }
        try {
            if (Core.getInstance().getConfig().getBoolean("database.enabled") && Core.getInstance().dbConnected) {
                new MySQL().logTransaction(e.getTransaction());
            }

            if (Core.getInstance().getConfig().getBoolean("discord.enabled")) {
                DiscordHook discordHook = new DiscordHook(Core.getInstance().getConfig().getString("discord.webhook"));
                DiscordEmbed de = DiscordEmbed.builder()
                        .title(Core.getInstance().getConfig().getString("discord.title"))
                        .description(Core.getInstance().getConfig().getString("discord.description-complete"))
                        .color(1)
                        .fields(Arrays.asList(
                                FieldEmbed.builder().name("Seller").value(Bukkit.getOfflinePlayer(UUID.fromString(e.getTransaction().getAuctionItem().getOwner())).getName()).inline(true).build(),
                                FieldEmbed.builder().name("Buyer").value(Bukkit.getOfflinePlayer(UUID.fromString(e.getTransaction().getBuyer())).getName()).build(),
                                FieldEmbed.builder().name("Transaction Type").value(e.getTransaction().getTransactionType().getTransactionType()).build(),
                                FieldEmbed.builder().name("Price").value(AuctionAPI.getInstance().friendlyNumber((e.getTransaction().getTransactionType() == Transaction.TransactionType.BOUGHT) ? e.getTransaction().getAuctionItem().getBuyNowPrice() : e.getTransaction().getAuctionItem().getCurrentPrice())).build(),
                                FieldEmbed.builder().name("Item").value(e.getTransaction().getAuctionItem().getItem().getType().name() + ":" + e.getTransaction().getAuctionItem().getItem().getDurability()).build()
                        ))
                        .build();

                DiscordMessage dm = DiscordMessage.builder().username(Core.getInstance().getConfig().getString("discord.username")).content("").avatarUrl(Core.getInstance().getConfig().getString("discord.profilepicture")).embeds(Arrays.asList(de)).build();
                discordHook.send(dm);
            }
        } catch (Exception e1) {
            Debugger.report(e1);
        }
    }

}
