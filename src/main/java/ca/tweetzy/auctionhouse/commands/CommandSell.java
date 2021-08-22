package ca.tweetzy.auctionhouse.commands;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.guis.GUISellItem;
import ca.tweetzy.auctionhouse.helpers.PlayerHelper;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.commands.AbstractCommand;
import ca.tweetzy.core.compatibility.CompatibleHand;
import ca.tweetzy.core.compatibility.XMaterial;
import ca.tweetzy.core.utils.NumberUtils;
import ca.tweetzy.core.utils.PlayerUtils;
import ca.tweetzy.core.utils.nms.NBTEditor;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
        Player player = (Player) sender;
        if (AuctionAPI.tellMigrationStatus(player)) return ReturnType.FAILURE;

        if (AuctionHouse.getInstance().getAuctionBanManager().checkAndHandleBan(player)) {
            return ReturnType.FAILURE;
        }

        AuctionPlayer auctionPlayer = AuctionHouse.getInstance().getAuctionPlayerManager().getPlayer(player.getUniqueId());

        ItemStack originalItem = PlayerHelper.getHeldItem(player).clone();
        ItemStack itemToSell = PlayerHelper.getHeldItem(player).clone();

        // check if player is at their selling limit
        if (auctionPlayer.isAtSellLimit()) {
            AuctionHouse.getInstance().getLocale().getMessage("general.sellinglimit").sendPrefixedMessage(player);
            return ReturnType.FAILURE;
        }

        // open the sell menu if its 0;
        if (args.length == 0) {
            if (!Settings.ALLOW_USAGE_OF_SELL_GUI.getBoolean()) {
                return ReturnType.SYNTAX_ERROR;
            }

            if (itemToSell.getType() == XMaterial.AIR.parseMaterial() && Settings.SELL_MENU_REQUIRES_USER_TO_HOLD_ITEM.getBoolean()) {
                AuctionHouse.getInstance().getLocale().getMessage("general.air").sendPrefixedMessage(player);
                return ReturnType.FAILURE;
            } else {
                AuctionHouse.getInstance().getGuiManager().showGUI(player, new GUISellItem(auctionPlayer, itemToSell));
                AuctionHouse.getInstance().getAuctionPlayerManager().addItemToSellHolding(player.getUniqueId(), itemToSell);
                PlayerUtils.takeActiveItem(player, CompatibleHand.MAIN_HAND, itemToSell.getAmount());
            }

            return ReturnType.SUCCESS;
        }

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

        // get the max allowed time for this player.
        int allowedTime = auctionPlayer.getAllowedSellTime();

        // Special command arguments
        List<String> commandFlags = AuctionAPI.getInstance().getCommandFlags(args);
        List<Double> listingPrices = new ArrayList<>();

        boolean isUsingBundle = false;

        for (String arg : args) {
            if (NumberUtils.isDouble(arg)) {
                listingPrices.add(Double.parseDouble(arg));
            }
        }

        boolean isBiddingItem = listingPrices.size() == 3 || !Settings.ALLOW_USAGE_OF_BUY_NOW_SYSTEM.getBoolean() && listingPrices.size() == 2;
        if (Settings.FORCE_AUCTION_USAGE.getBoolean()) isBiddingItem = true;


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

        if (!Settings.ALLOW_USAGE_OF_BUY_NOW_SYSTEM.getBoolean() && isBiddingItem) {
            if (listingPrices.get(0) < Settings.MIN_AUCTION_START_PRICE.getDouble()) {
                AuctionHouse.getInstance().getLocale().getMessage("pricing.minstartingprice").processPlaceholder("price", Settings.MIN_AUCTION_START_PRICE.getDouble()).sendPrefixedMessage(player);
                return ReturnType.FAILURE;
            }

            if (listingPrices.get(0) > Settings.MAX_AUCTION_START_PRICE.getDouble()) {
                AuctionHouse.getInstance().getLocale().getMessage("pricing.maxstartingprice").processPlaceholder("price", Settings.MAX_AUCTION_START_PRICE.getDouble()).sendPrefixedMessage(player);
                return ReturnType.FAILURE;
            }

            if (!Settings.FORCE_AUCTION_USAGE.getBoolean()) {
                if (listingPrices.get(1) < Settings.MIN_AUCTION_INCREMENT_PRICE.getDouble()) {
                    AuctionHouse.getInstance().getLocale().getMessage("pricing.minbidincrementprice").processPlaceholder("price", Settings.MIN_AUCTION_INCREMENT_PRICE.getDouble()).sendPrefixedMessage(player);
                    return ReturnType.FAILURE;
                }

                if (listingPrices.get(1) > Settings.MAX_AUCTION_INCREMENT_PRICE.getDouble()) {
                    AuctionHouse.getInstance().getLocale().getMessage("pricing.maxbidincrementprice").processPlaceholder("price", Settings.MAX_AUCTION_INCREMENT_PRICE.getDouble()).sendPrefixedMessage(player);
                    return ReturnType.FAILURE;
                }
            }
        } else {
            if (Settings.ALLOW_USAGE_OF_BID_SYSTEM.getBoolean() && isBiddingItem && listingPrices.get(0) < Settings.MIN_AUCTION_PRICE.getDouble() && !(listingPrices.get(0) <= -1)) {
                AuctionHouse.getInstance().getLocale().getMessage("pricing.minbaseprice").processPlaceholder("price", Settings.MIN_AUCTION_PRICE.getDouble()).sendPrefixedMessage(player);
                return ReturnType.FAILURE;
            }

            if (Settings.ALLOW_USAGE_OF_BID_SYSTEM.getBoolean() && isBiddingItem && listingPrices.get(0) > Settings.MAX_AUCTION_PRICE.getDouble()) {
                AuctionHouse.getInstance().getLocale().getMessage("pricing.maxbaseprice").processPlaceholder("price", Settings.MAX_AUCTION_PRICE.getDouble()).sendPrefixedMessage(player);
                return ReturnType.FAILURE;
            }

            if (!Settings.FORCE_AUCTION_USAGE.getBoolean()) {
                if (Settings.ALLOW_USAGE_OF_BID_SYSTEM.getBoolean() && isBiddingItem && listingPrices.get(1) < Settings.MIN_AUCTION_START_PRICE.getDouble()) {
                    AuctionHouse.getInstance().getLocale().getMessage("pricing.minstartingprice").processPlaceholder("price", Settings.MIN_AUCTION_START_PRICE.getDouble()).sendPrefixedMessage(player);
                    return ReturnType.FAILURE;
                }

                if (Settings.ALLOW_USAGE_OF_BID_SYSTEM.getBoolean() && isBiddingItem && listingPrices.get(2) < Settings.MIN_AUCTION_INCREMENT_PRICE.getDouble()) {
                    AuctionHouse.getInstance().getLocale().getMessage("pricing.minbidincrementprice").processPlaceholder("price", Settings.MIN_AUCTION_INCREMENT_PRICE.getDouble()).sendPrefixedMessage(player);
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
            }
        }

        if (Settings.ALLOW_ITEM_BUNDLES.getBoolean() && commandFlags.contains("-b")) {
            if (NBTEditor.contains(itemToSell, "AuctionBundleItem")) {
                AuctionHouse.getInstance().getLocale().getMessage("general.cannotsellbundleditem").sendPrefixedMessage(player);
                return ReturnType.FAILURE;
            }

            itemToSell = AuctionAPI.getInstance().createBundledItem(itemToSell, AuctionAPI.getInstance().getSimilarItemsFromInventory(player, itemToSell).toArray(new ItemStack[0]));
            isUsingBundle = true;
        }

        final boolean buyNowAllow = Settings.ALLOW_USAGE_OF_BUY_NOW_SYSTEM.getBoolean() && !Settings.FORCE_AUCTION_USAGE.getBoolean();

        AuctionAPI.getInstance().listAuction(
                player,
                originalItem,
                itemToSell,
                allowedTime,
                buyNowAllow ? isBiddingItem && listingPrices.get(0) <= -1 ? -1 : listingPrices.get(0) : isBiddingItem ? -1 : listingPrices.get(0),
                buyNowAllow ? isBiddingItem ? listingPrices.get(1) : 0 : isBiddingItem ? listingPrices.get(0) : 0,
                buyNowAllow ? isBiddingItem ? listingPrices.get(2) : 0 : isBiddingItem ? listingPrices.size() == 1 ? 1 : listingPrices.get(1) : 0,
                buyNowAllow ? isBiddingItem ? listingPrices.get(1) : listingPrices.get(0) : listingPrices.get(0),
                isBiddingItem,
                isUsingBundle,
                true
        );

        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        if (args.length <= 3) return Arrays.asList("1", "2", "3", "4", "5");
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
