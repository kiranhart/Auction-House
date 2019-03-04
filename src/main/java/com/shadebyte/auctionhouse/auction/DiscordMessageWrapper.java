package com.shadebyte.auctionhouse.auction;

import com.shadebyte.auctionhouse.Core;
import com.shadebyte.auctionhouse.api.AuctionAPI;
import com.shadebyte.auctionhouse.api.discordwebhook.embed.FieldEmbed;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;

import java.util.UUID;

/**
 * The current file has been created by Kiran Hart
 * Date Created: 9/17/2018
 * Time Created: 2:28 PM
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise.
 */
public class DiscordMessageWrapper {

    private String configLocation;
    private AuctionItem auctionItem;

    public DiscordMessageWrapper(String configLocation, AuctionItem auctionItem) {
        this.configLocation = configLocation;
        this.auctionItem = auctionItem;
    }

    public FieldEmbed getFieldEmbed() {
        String name = configLocation;
        return FieldEmbed.builder()
                .name(name.substring(name.lastIndexOf(".")).replace("_", " ").replace(".", ""))
                .value(Core.getInstance().getConfig().getString(configLocation + ".data").replace("{seller}", Bukkit.getOfflinePlayer(UUID.fromString(auctionItem.getOwner())).getName())
                        .replace("{bid_start}", AuctionAPI.getInstance().friendlyNumber(auctionItem.getStartPrice()))
                        .replace("{bid_increment}", (auctionItem.getBidIncrement() == 0) ? "0" : AuctionAPI.getInstance().friendlyNumber(auctionItem.getBidIncrement()))
                        .replace("{buy_now}", (auctionItem.getBuyNowPrice() == 0) ? "0" : AuctionAPI.getInstance().friendlyNumber(auctionItem.getBuyNowPrice()))
                        .replace("{item_type}", auctionItem.getItem().getType().name())
                        .replace("{item_name}", getDisplayName())
                        .replace("{item_enchants}", getEnchantments())
                        .replace("{item_lore}", getLore())
                ).inline(Core.getInstance().getConfig().getBoolean(configLocation + ".inline")).build();
    }


    public String getDisplayName() {
        String name;
        if (auctionItem.getItem().hasItemMeta()) {
            if (auctionItem.getItem().getItemMeta().hasDisplayName())
                name = ChatColor.stripColor(auctionItem.getItem().getItemMeta().getDisplayName());
            else
                name = StringUtils.capitalize(ChatColor.stripColor(auctionItem.getItem().getType().name().toLowerCase().replace("_", " ")));
        } else {
            name = StringUtils.capitalize(ChatColor.stripColor(auctionItem.getItem().getType().name().toLowerCase().replace("_", " ")));
        }
        return name;
    }

    public String getEnchantments() {
        String lore = "";
        if (auctionItem.getItem().hasItemMeta()) {
            if (!auctionItem.getItem().getItemMeta().hasEnchants()) {
                lore = "No Enchantments";
            } else {
                for (Enchantment enchantment : auctionItem.getItem().getItemMeta().getEnchants().keySet()) {
                    String name = enchantment.getName().replace("_", " ").toLowerCase();
                    String level = AuctionAPI.getInstance().toRoman(auctionItem.getItem().getItemMeta().getEnchantLevel(enchantment));
                    String e = StringUtils.capitalize(name) + " " + level;
                    lore += e + ", ";
                }
            }
        } else {
            lore = "No Enchantments";
        }
        return lore;
    }

    public String getLore() {
        String lore = "";
        if (auctionItem.getItem().hasItemMeta()) {
            if (!auctionItem.getItem().getItemMeta().hasLore()) {
                lore = "No Lore";
            } else {
                for (String s : auctionItem.getItem().getItemMeta().getLore()) {
                    lore += ChatColor.stripColor(s) + ", ";
                }
            }
        } else {
            lore = "No Lore";
        }
        return lore;
    }

    public String getConfigLocation() {
        return configLocation;
    }
}
