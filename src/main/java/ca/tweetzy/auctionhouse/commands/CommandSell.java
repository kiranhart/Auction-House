package ca.tweetzy.auctionhouse.commands;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.auction.AuctionItem;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.helpers.MaterialCategorizer;
import ca.tweetzy.auctionhouse.helpers.PlayerHelper;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.commands.AbstractCommand;
import ca.tweetzy.core.compatibility.XMaterial;
import ca.tweetzy.core.utils.NumberUtils;
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

    final AuctionHouse instance;

    public CommandSell(AuctionHouse instance) {
        super(CommandType.PLAYER_ONLY, "sell");
        this.instance = instance;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        if (args.length <= 0) return ReturnType.SYNTAX_ERROR;
        Player player = (Player) sender;
        AuctionPlayer auctionPlayer = new AuctionPlayer(player);

        ItemStack itemToSell = PlayerHelper.getHeldItem(player);

        if (itemToSell.getType() == XMaterial.AIR.parseMaterial()) {
            instance.getLocale().getMessage("general.air").sendPrefixedMessage(player);
            return ReturnType.FAILURE;
        }

        // Check for block items
        if (Settings.BLOCKED_ITEMS.getStringList().contains(itemToSell.getType().name())) {
            instance.getLocale().getMessage("general.blocked").processPlaceholder("item", itemToSell.getType().name()).sendPrefixedMessage(player);
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
            instance.getLocale().getMessage("general.sellinglimit").sendPrefixedMessage(player);
            return ReturnType.FAILURE;
        }

        if (args.length <= 1) {
            if (!NumberUtils.isDouble(args[0])) {
                instance.getLocale().getMessage("general.notanumber").processPlaceholder("value", args[0]).sendPrefixedMessage(player);
                return ReturnType.SYNTAX_ERROR;
            }

            double basePrice = Double.parseDouble(args[0]);

            if (basePrice < Settings.MIN_AUCTION_PRICE.getDouble()) {
                instance.getLocale().getMessage("pricing.minbaseprice").processPlaceholder("price", args[0]).sendPrefixedMessage(player);
                return ReturnType.FAILURE;
            }

            if (basePrice > Settings.MAX_AUCTION_PRICE.getDouble()) {
                instance.getLocale().getMessage("pricing.maxbaseprice").processPlaceholder("price", args[0]).sendPrefixedMessage(player);
                return ReturnType.FAILURE;
            }

            // list the item
            instance.getAuctionItemManager().addItem(new AuctionItem(
                    player.getUniqueId(),
                    player.getUniqueId(),
                    itemToSell,
                    MaterialCategorizer.getMaterialCategory(itemToSell),
                    UUID.randomUUID(),
                    basePrice,
                    0,
                    0,
                    basePrice,
                    allowedTime
            ));

        } else {
            // they want to use the bidding system, so make it a bid item
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
