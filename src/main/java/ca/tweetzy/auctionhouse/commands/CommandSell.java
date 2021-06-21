package ca.tweetzy.auctionhouse.commands;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.api.events.AuctionStartEvent;
import ca.tweetzy.auctionhouse.auction.AuctionItem;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.helpers.MaterialCategorizer;
import ca.tweetzy.auctionhouse.helpers.PlayerHelper;
import ca.tweetzy.auctionhouse.managers.SoundManager;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.commands.AbstractCommand;
import ca.tweetzy.core.compatibility.CompatibleHand;
import ca.tweetzy.core.compatibility.XMaterial;
import ca.tweetzy.core.utils.NumberUtils;
import ca.tweetzy.core.utils.PlayerUtils;
import ca.tweetzy.core.utils.nms.NBTEditor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

        ItemStack originalItem = PlayerHelper.getHeldItem(player).clone();
        ItemStack itemToSell = PlayerHelper.getHeldItem(player).clone();

        if (itemToSell.getType() == XMaterial.AIR.parseMaterial()) {
            AuctionHouse.getInstance().getLocale().getMessage("general.air").sendPrefixedMessage(player);
            return ReturnType.FAILURE;
        }

        // Check for block items
        if (Settings.BLOCKED_ITEMS.getStringList().contains(itemToSell.getType().name())) {
            AuctionHouse.getInstance().getLocale().getMessage("general.blockeditem").processPlaceholder("item", itemToSell.getType().name()).sendPrefixedMessage(player);
            return ReturnType.FAILURE;
        }

        boolean blocked = false;

        String itemName = ChatColor.stripColor(AuctionAPI.getInstance().getItemName(itemToSell).toLowerCase());
        List<String> itemLore = AuctionAPI.getInstance().getItemLore(itemToSell).stream().map(line -> ChatColor.stripColor(line.toLowerCase())).collect(Collectors.toList());

        // Check for blocked names and lore
        for (String s : Settings.BLOCKED_ITEM_NAMES.getStringList()) {
            if (AuctionAPI.getInstance().match(s, itemName)) {
                AuctionHouse.getInstance().getLocale().getMessage("general.blockedname").sendPrefixedMessage(player);
                blocked = true;
            }
        }

        if (!itemLore.isEmpty() && !blocked) {
            for (String s : Settings.BLOCKED_ITEM_LORES.getStringList()) {
                for (String line : itemLore) {
                    if (AuctionAPI.getInstance().match(s, line)) {
                        AuctionHouse.getInstance().getLocale().getMessage("general.blockedlore").sendPrefixedMessage(player);
                        blocked = true;
                    }
                }
            }
        }

        if (blocked) return ReturnType.FAILURE;

        List<Integer> possibleTimes = new ArrayList<>();
        Settings.AUCTION_TIME.getStringList().forEach(line -> {
            String[] split = line.split(":");
            if (player.hasPermission("auctionhouse.time." + split[0])) {
                possibleTimes.add(Integer.parseInt(split[1]));
            }
        });

        // get the max allowed time for this player.
        int allowedTime = possibleTimes.size() <= 0 ? Settings.DEFAULT_AUCTION_TIME.getInt() : Math.max(Settings.DEFAULT_AUCTION_TIME.getInt(), Collections.max(possibleTimes));

        // check if player is at their selling limit
        if (auctionPlayer.isAtSellLimit()) {
            AuctionHouse.getInstance().getLocale().getMessage("general.sellinglimit").sendPrefixedMessage(player);
            return ReturnType.FAILURE;
        }

        // Special command arguments
        List<String> commandFlags = AuctionAPI.getInstance().getCommandFlags(args);
        List<Double> listingPrices = new ArrayList<>();

        boolean isUsingBundle = false;

        for (String arg : args) {
            if (NumberUtils.isDouble(arg)) {
                listingPrices.add(Double.parseDouble(arg));
            }
        }

        boolean isBiddingItem = listingPrices.size() == 3;

        /*======================================================================
        ================ THE PLAYER IS NOT USING THE BID OPTION ================
        ======================================================================*/

        if (!isBiddingItem && !NumberUtils.isDouble(args[0])) {
            AuctionHouse.getInstance().getLocale().getMessage("general.notanumber").processPlaceholder("value", args[0]).sendPrefixedMessage(player);
            return ReturnType.FAILURE;
        }

        if (!isBiddingItem && listingPrices.get(0) < Settings.MIN_AUCTION_PRICE.getDouble()) {
            AuctionHouse.getInstance().getLocale().getMessage("pricing.minbaseprice").processPlaceholder("price", Settings.MIN_AUCTION_PRICE.getDouble()).sendPrefixedMessage(player);
            return ReturnType.FAILURE;
        }

        if (!isBiddingItem && listingPrices.get(0) > Settings.MAX_AUCTION_PRICE.getDouble()) {
            AuctionHouse.getInstance().getLocale().getMessage("pricing.maxbaseprice").processPlaceholder("price", Settings.MAX_AUCTION_PRICE.getDouble()).sendPrefixedMessage(player);
            return ReturnType.FAILURE;
        }

        /*======================================================================
        ================ THE PLAYER IS USING THE BIDDING SYSTEM ================
        ======================================================================*/

        if (Settings.ALLOW_USAGE_OF_BID_SYSTEM.getBoolean() && isBiddingItem && listingPrices.get(0) < Settings.MIN_AUCTION_PRICE.getDouble() && !(listingPrices.get(0) <= -1)) {
            AuctionHouse.getInstance().getLocale().getMessage("pricing.minbaseprice").processPlaceholder("price", Settings.MIN_AUCTION_PRICE.getDouble()).sendPrefixedMessage(player);
            return ReturnType.FAILURE;
        }

        if (Settings.ALLOW_USAGE_OF_BID_SYSTEM.getBoolean() && isBiddingItem && listingPrices.get(1) < Settings.MIN_AUCTION_START_PRICE.getDouble()) {
            AuctionHouse.getInstance().getLocale().getMessage("pricing.minstartingprice").processPlaceholder("price", Settings.MIN_AUCTION_START_PRICE.getDouble()).sendPrefixedMessage(player);
            return ReturnType.FAILURE;
        }

        if (Settings.ALLOW_USAGE_OF_BID_SYSTEM.getBoolean() && isBiddingItem && listingPrices.get(2) < Settings.MIN_AUCTION_INCREMENT_PRICE.getDouble()) {
            AuctionHouse.getInstance().getLocale().getMessage("pricing.minbidincrementprice").processPlaceholder("price", Settings.MIN_AUCTION_INCREMENT_PRICE.getDouble()).sendPrefixedMessage(player);
            return ReturnType.FAILURE;
        }

        // check max
        if (Settings.ALLOW_USAGE_OF_BID_SYSTEM.getBoolean() && isBiddingItem && listingPrices.get(0) > Settings.MAX_AUCTION_PRICE.getDouble()) {
            AuctionHouse.getInstance().getLocale().getMessage("pricing.maxbaseprice").processPlaceholder("price", Settings.MAX_AUCTION_PRICE.getDouble()).sendPrefixedMessage(player);
            return ReturnType.FAILURE;
        }

        if (Settings.ALLOW_USAGE_OF_BID_SYSTEM.getBoolean() && isBiddingItem && listingPrices.get(1) > Settings.MAX_AUCTION_START_PRICE.getDouble()) {
            AuctionHouse.getInstance().getLocale().getMessage("pricing.maxstartingprice").processPlaceholder("price", Settings.MAX_AUCTION_START_PRICE.getDouble()).sendPrefixedMessage(player);
            return ReturnType.FAILURE;
        }

        if (Settings.ALLOW_USAGE_OF_BID_SYSTEM.getBoolean() && isBiddingItem && listingPrices.get(2) > Settings.MAX_AUCTION_INCREMENT_PRICE.getDouble()) {
            AuctionHouse.getInstance().getLocale().getMessage("pricing.maxbidincrementprice").processPlaceholder("price", Settings.MAX_AUCTION_INCREMENT_PRICE.getDouble()).sendPrefixedMessage(player);
            return ReturnType.FAILURE;
        }

        if (Settings.ALLOW_USAGE_OF_BID_SYSTEM.getBoolean() && Settings.BASE_PRICE_MUST_BE_HIGHER_THAN_BID_START.getBoolean() && isBiddingItem && listingPrices.get(1) > listingPrices.get(0) && !(listingPrices.get(0) <= -1)) {
            AuctionHouse.getInstance().getLocale().getMessage("pricing.basepricetoolow").sendPrefixedMessage(player);
            return ReturnType.FAILURE;
        }

        if (Settings.ALLOW_ITEM_BUNDLES.getBoolean() && commandFlags.contains("-b")) {
            if (NBTEditor.contains(itemToSell, "AuctionBundleItem")) {
                AuctionHouse.getInstance().getLocale().getMessage("general.cannotsellbundleditem").sendPrefixedMessage(player);
                return ReturnType.FAILURE;
            }

            itemToSell = AuctionAPI.getInstance().createBundledItem(itemToSell, AuctionAPI.getInstance().getSimilarItemsFromInventory(player, itemToSell).toArray(new ItemStack[0]));
            isUsingBundle = true;
        }

        AuctionItem auctionItem = new AuctionItem(
                player.getUniqueId(),
                player.getUniqueId(),
                itemToSell,
                MaterialCategorizer.getMaterialCategory(itemToSell),
                UUID.randomUUID(),
                isBiddingItem && listingPrices.get(0) <= -1 ? -1 : listingPrices.get(0),
                isBiddingItem ? listingPrices.get(1) : 0,
                isBiddingItem ? listingPrices.get(2) : 0,
                isBiddingItem ? listingPrices.get(1) : listingPrices.get(0),
                allowedTime,
                false
        );

        AuctionStartEvent startEvent = new AuctionStartEvent(player, auctionItem);
        Bukkit.getServer().getPluginManager().callEvent(startEvent);
        if (startEvent.isCancelled()) return ReturnType.FAILURE;

        AuctionHouse.getInstance().getAuctionItemManager().addItem(auctionItem);

        if (isUsingBundle) {
            AuctionAPI.getInstance().removeSpecificItemQuantityFromPlayer(player, originalItem, AuctionAPI.getInstance().getItemCountInPlayerInventory(player, originalItem));
        } else {
            PlayerUtils.takeActiveItem(player, CompatibleHand.MAIN_HAND, itemToSell.getAmount());
        }

        SoundManager.getInstance().playSound(player, Settings.SOUNDS_LISTED_ITEM_ON_AUCTION_HOUSE.getString(), 1.0F, 1.0F);

        AuctionHouse.getInstance().getLocale().getMessage(isBiddingItem ? "auction.listed.withbid" : "auction.listed.nobid")
                .processPlaceholder("amount", itemToSell.getAmount())
                .processPlaceholder("item", AuctionAPI.getInstance().getItemName(itemToSell))
                .processPlaceholder("base_price", listingPrices.get(0) <= -1 ? AuctionHouse.getInstance().getLocale().getMessage("auction.biditemwithdisabledbuynow").getMessage() : AuctionAPI.getInstance().formatNumber(listingPrices.get(0)))
                .processPlaceholder("start_price", isBiddingItem ? AuctionAPI.getInstance().formatNumber(listingPrices.get(1)) : 0)
                .processPlaceholder("increment_price", isBiddingItem ? AuctionAPI.getInstance().formatNumber(listingPrices.get(2)) : 0)
                .sendPrefixedMessage(player);

        if (Settings.BROADCAST_AUCTION_LIST.getBoolean()) {
            Bukkit.getOnlinePlayers().forEach(AuctionHouse.getInstance().getLocale().getMessage(isBiddingItem ? "auction.listed.withbid" : "auction.broadcast.nobid")
                    .processPlaceholder("player", player.getName())
                    .processPlaceholder("amount", itemToSell.getAmount())
                    .processPlaceholder("item", AuctionAPI.getInstance().getItemName(itemToSell))
                    .processPlaceholder("base_price", listingPrices.get(0) <= -1 ? AuctionHouse.getInstance().getLocale().getMessage("auction.biditemwithdisabledbuynow").getMessage() : AuctionAPI.getInstance().formatNumber(listingPrices.get(0)))
                    .processPlaceholder("start_price", isBiddingItem ? AuctionAPI.getInstance().formatNumber(listingPrices.get(1)) : 0)
                    .processPlaceholder("increment_price", isBiddingItem ? AuctionAPI.getInstance().formatNumber(listingPrices.get(2)) : 0)::sendPrefixedMessage);
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
        return AuctionHouse.getInstance().getLocale().getMessage("commands.syntax.sell").getMessage();
    }

    @Override
    public String getDescription() {
        return AuctionHouse.getInstance().getLocale().getMessage("commands.description.sell").getMessage();
    }
}
