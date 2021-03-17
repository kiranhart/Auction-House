package ca.tweetzy.auctionhouse.commands;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.events.AuctionStartEvent;
import ca.tweetzy.auctionhouse.auction.AuctionItem;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.helpers.MaterialCategorizer;
import ca.tweetzy.auctionhouse.helpers.PlayerHelper;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.commands.AbstractCommand;
import ca.tweetzy.core.compatibility.CompatibleHand;
import ca.tweetzy.core.compatibility.XMaterial;
import ca.tweetzy.core.utils.NumberUtils;
import ca.tweetzy.core.utils.PlayerUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * The current file has been created by Kiran Hart
 * Date Created: January 12 2021
 * Time Created: 9:17 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class CommandSell extends AbstractCommand {
    
    public CommandSell() {
        super(CommandType.PLAYER_ONLY, "sell");
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        if (args.length <= 0) return ReturnType.SYNTAX_ERROR;
        Player player = (Player) sender;
        AuctionPlayer auctionPlayer = AuctionHouse.getInstance().getAuctionPlayerManager().getPlayer(player.getUniqueId());

        ItemStack itemToSell = PlayerHelper.getHeldItem(player);

        if (itemToSell.getType() == XMaterial.AIR.parseMaterial()) {
            AuctionHouse.getInstance().getLocale().getMessage("general.air").sendPrefixedMessage(player);
            return ReturnType.FAILURE;
        }

        // Check for block items
        if (Settings.BLOCKED_ITEMS.getStringList().contains(itemToSell.getType().name())) {
            AuctionHouse.getInstance().getLocale().getMessage("general.blocked").processPlaceholder("item", itemToSell.getType().name()).sendPrefixedMessage(player);
            return ReturnType.FAILURE;
        }

        List<Integer> possibleTimes = new ArrayList<>();
        Settings.AUCTION_TIME.getStringList().forEach(line -> {
            String[] split = line.split(":");
            if (player.hasPermission("auctionhouse.time." + split[0])) {
                possibleTimes.add(Integer.parseInt(split[1]));
            }
        });

        // get the max allowed time for this player.
        int allowedTime = possibleTimes.size() <= 0 ? Settings.DEFAULT_AUCTION_TIME.getInt() : Collections.max(possibleTimes);

        // check if player is at their selling limit
        if (auctionPlayer.isAtSellLimit()) {
            AuctionHouse.getInstance().getLocale().getMessage("general.sellinglimit").sendPrefixedMessage(player);
            return ReturnType.FAILURE;
        }

        if (args.length <= 1) {
            if (!NumberUtils.isDouble(args[0])) {
                AuctionHouse.getInstance().getLocale().getMessage("general.notanumber").processPlaceholder("value", args[0]).sendPrefixedMessage(player);
                return ReturnType.SYNTAX_ERROR;
            }

            double basePrice = Double.parseDouble(args[0]);

            if (basePrice < Settings.MIN_AUCTION_PRICE.getDouble()) {
                AuctionHouse.getInstance().getLocale().getMessage("pricing.minbaseprice").processPlaceholder("price", args[0]).sendPrefixedMessage(player);
                return ReturnType.FAILURE;
            }

            if (basePrice > Settings.MAX_AUCTION_PRICE.getDouble()) {
                AuctionHouse.getInstance().getLocale().getMessage("pricing.maxbaseprice").processPlaceholder("price", args[0]).sendPrefixedMessage(player);
                return ReturnType.FAILURE;
            }

            AuctionItem item = new AuctionItem(
                    player.getUniqueId(),
                    player.getUniqueId(),
                    itemToSell,
                    MaterialCategorizer.getMaterialCategory(itemToSell),
                    UUID.randomUUID(),
                    basePrice,
                    0,
                    0,
                    basePrice,
                    allowedTime,
                    false
            );

            AuctionStartEvent startEvent = new AuctionStartEvent(player, item);
            Bukkit.getServer().getPluginManager().callEvent(startEvent);
            if (startEvent.isCancelled()) return ReturnType.FAILURE;

            // list the item
            AuctionHouse.getInstance().getAuctionItemManager().addItem(item);

            AuctionHouse.getInstance().getLocale().getMessage("auction.listed.nobid")
                    .processPlaceholder("amount", itemToSell.getAmount())
                    .processPlaceholder("item", WordUtils.capitalizeFully(itemToSell.getType().name().replace("_", " ")))
                    .processPlaceholder("base_price", basePrice)
                    .sendPrefixedMessage(player);

            PlayerUtils.takeActiveItem(player, CompatibleHand.MAIN_HAND, itemToSell.getAmount());
        } else {
            // they want to use the bidding system, so make it a bid item
            if (!NumberUtils.isDouble(args[0])) {
                AuctionHouse.getInstance().getLocale().getMessage("general.notanumber").processPlaceholder("value", args[0]).sendPrefixedMessage(player);
                return ReturnType.SYNTAX_ERROR;
            }

            if (!NumberUtils.isDouble(args[1])) {
                AuctionHouse.getInstance().getLocale().getMessage("general.notanumber").processPlaceholder("value", args[1]).sendPrefixedMessage(player);
                return ReturnType.SYNTAX_ERROR;
            }

            if (!NumberUtils.isDouble(args[2])) {
                AuctionHouse.getInstance().getLocale().getMessage("general.notanumber").processPlaceholder("value", args[2]).sendPrefixedMessage(player);
                return ReturnType.SYNTAX_ERROR;
            }

            double basePrice = Double.parseDouble(args[0]);
            double bidStartPrice = Double.parseDouble(args[1]);
            double bidIncPrice = Double.parseDouble(args[2]);

            // check min
            if (basePrice < Settings.MIN_AUCTION_PRICE.getDouble()) {
                AuctionHouse.getInstance().getLocale().getMessage("pricing.minbaseprice").processPlaceholder("price", args[0]).sendPrefixedMessage(player);
                return ReturnType.FAILURE;
            }

            if (bidStartPrice < Settings.MIN_AUCTION_START_PRICE.getDouble()) {
                AuctionHouse.getInstance().getLocale().getMessage("pricing.minstartingprice").processPlaceholder("price", args[1]).sendPrefixedMessage(player);
                return ReturnType.FAILURE;
            }

            if (bidIncPrice < Settings.MIN_AUCTION_INCREMENT_PRICE.getDouble()) {
                AuctionHouse.getInstance().getLocale().getMessage("pricing.minbidincrementprice").processPlaceholder("price", args[2]).sendPrefixedMessage(player);
                return ReturnType.FAILURE;
            }

            // check max
            if (basePrice > Settings.MAX_AUCTION_PRICE.getDouble()) {
                AuctionHouse.getInstance().getLocale().getMessage("pricing.maxbaseprice").processPlaceholder("price", args[0]).sendPrefixedMessage(player);
                return ReturnType.FAILURE;
            }

            if (bidStartPrice > Settings.MAX_AUCTION_START_PRICE.getDouble()) {
                AuctionHouse.getInstance().getLocale().getMessage("pricing.maxstartingprice").processPlaceholder("price", args[1]).sendPrefixedMessage(player);
                return ReturnType.FAILURE;
            }

            if (bidIncPrice > Settings.MAX_AUCTION_INCREMENT_PRICE.getDouble()) {
                AuctionHouse.getInstance().getLocale().getMessage("pricing.maxbidincrementprice").processPlaceholder("price", args[2]).sendPrefixedMessage(player);
                return ReturnType.FAILURE;
            }

            AuctionItem item = new AuctionItem(
                    player.getUniqueId(),
                    player.getUniqueId(),
                    itemToSell,
                    MaterialCategorizer.getMaterialCategory(itemToSell),
                    UUID.randomUUID(),
                    basePrice,
                    bidStartPrice,
                    bidIncPrice,
                    bidStartPrice,
                    allowedTime,
                    false
            );

            AuctionStartEvent startEvent = new AuctionStartEvent(player, item);
            Bukkit.getServer().getPluginManager().callEvent(startEvent);
            if (startEvent.isCancelled()) return ReturnType.FAILURE;

            AuctionHouse.getInstance().getAuctionItemManager().addItem(item);

            AuctionHouse.getInstance().getLocale().getMessage("auction.listed.withbid")
                    .processPlaceholder("amount", itemToSell.getAmount())
                    .processPlaceholder("item", WordUtils.capitalizeFully(itemToSell.getType().name().replace("_", " ")))
                    .processPlaceholder("base_price", basePrice)
                    .processPlaceholder("start_price", bidStartPrice)
                    .processPlaceholder("increment_price", bidIncPrice)
                    .sendPrefixedMessage(player);

            PlayerUtils.takeActiveItem(player, CompatibleHand.MAIN_HAND, itemToSell.getAmount());
        }

        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "auctionhouse.cmd.sell";
    }

    @Override
    public String getSyntax() {
        return "sell <basePrice> [bidStart] [bidIncr]";
    }

    @Override
    public String getDescription() {
        return "Used to put an item up for auction";
    }
}
