package ca.tweetzy.auctionhouse.commands;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.auction.AuctionSaleType;
import ca.tweetzy.auctionhouse.guis.GUISellItem;
import ca.tweetzy.auctionhouse.guis.confirmation.GUIConfirmListing;
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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: August 22 2021
 * Time Created: 6:51 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public final class CommandSell extends AbstractCommand {

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

		// Check list delay
		if (!auctionPlayer.canListItem()) {
			return ReturnType.FAILURE;
		}

		// Open the sell menu enabled
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

		if (Settings.MAKE_BLOCKED_ITEMS_A_WHITELIST.getBoolean()) {
			if (!Settings.BLOCKED_ITEMS.getStringList().contains(itemToSell.getType().name())) {
				AuctionHouse.getInstance().getLocale().getMessage("general.blockeditem").processPlaceholder("item", itemToSell.getType().name()).sendPrefixedMessage(player);
				return ReturnType.FAILURE;
			}
		} else {
			if (Settings.BLOCKED_ITEMS.getStringList().contains(itemToSell.getType().name())) {
				AuctionHouse.getInstance().getLocale().getMessage("general.blockeditem").processPlaceholder("item", itemToSell.getType().name()).sendPrefixedMessage(player);
				return ReturnType.FAILURE;
			}
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
		int allowedTime = 0;

		/*
		================== BEGIN GATHERING NUMBERS / ARGUMENTS ==================
		 */

		// Temporary number holdings
		Double buyNowPrice = null;
		Double startingBid = null;
		Double bidIncrement = null;
		boolean isBundle = false;


		for (int i = 0; i < args.length; i++) {
			if (NumberUtils.isDouble(args[i])) {
				if (buyNowPrice == null)
					buyNowPrice = Double.parseDouble(args[i]);
				else if (startingBid == null)
					startingBid = Double.parseDouble(args[i]);
				else
					bidIncrement = Double.parseDouble(args[i]);
			}

			if (args[i].equalsIgnoreCase("-b") || args[i].equalsIgnoreCase("-bundle"))
				isBundle = true;

			if (args[i].toLowerCase().startsWith("-t") && Settings.ALLOW_PLAYERS_TO_DEFINE_AUCTION_TIME.getBoolean()) {
				if (i + 2 < args.length) {
					int customTime = (int) AuctionAPI.toTicks(args[i + 1] + " " + args[i + 2]);
					if (customTime <= Settings.MAX_CUSTOM_DEFINED_TIME.getInt())
						allowedTime = customTime;
				}
			}
		}
		// check buy now price null
		if (buyNowPrice == null) {
			AuctionHouse.getInstance().getLocale().getMessage("general.please_enter_at_least_one_number").sendPrefixedMessage(player);
			return ReturnType.FAILURE;
		}

		boolean isBiddingItem = Settings.FORCE_AUCTION_USAGE.getBoolean() || buyNowPrice != null && startingBid != null && Settings.ALLOW_USAGE_OF_BID_SYSTEM.getBoolean();

		// NOT USING THE BIDDING SYSTEM
		if (!isBiddingItem /* && buyNowPrice != null */) {
			// Check the if the price meets the min/max criteria
			if (!checkBasePrice(player, buyNowPrice, false)) return ReturnType.FAILURE;
		}

		if (isBiddingItem && /* buyNowPrice != null && */ startingBid != null) {
			if (!checkBasePrice(player, buyNowPrice, true)) return ReturnType.FAILURE;
			// check the starting bid values
			if (startingBid < Settings.MIN_AUCTION_INCREMENT_PRICE.getDouble()) {
				AuctionHouse.getInstance().getLocale().getMessage("pricing.minstartingprice").processPlaceholder("price", Settings.MIN_AUCTION_INCREMENT_PRICE.getDouble()).sendPrefixedMessage(player);
				return ReturnType.FAILURE;
			}

			if (startingBid > Settings.MAX_AUCTION_START_PRICE.getDouble()) {
				AuctionHouse.getInstance().getLocale().getMessage("pricing.maxstartingprice").processPlaceholder("price", Settings.MAX_AUCTION_START_PRICE.getDouble()).sendPrefixedMessage(player);
				return ReturnType.FAILURE;
			}

			// if present check the bid increment pricing
			if (bidIncrement != null) {
				if (bidIncrement < Settings.MIN_AUCTION_INCREMENT_PRICE.getDouble()) {
					AuctionHouse.getInstance().getLocale().getMessage("pricing.minbidincrementprice").processPlaceholder("price", Settings.MIN_AUCTION_INCREMENT_PRICE.getDouble()).sendPrefixedMessage(player);
					return ReturnType.FAILURE;
				}

				if (bidIncrement > Settings.MAX_AUCTION_INCREMENT_PRICE.getDouble()) {
					AuctionHouse.getInstance().getLocale().getMessage("pricing.maxbidincrementprice").processPlaceholder("price", Settings.MAX_AUCTION_START_PRICE.getDouble()).sendPrefixedMessage(player);
					return ReturnType.FAILURE;
				}
			} else {
				bidIncrement = 1.0D;
			}

			// check if the starting bid is not higher than the buy now
			if (Settings.BASE_PRICE_MUST_BE_HIGHER_THAN_BID_START.getBoolean() && startingBid > buyNowPrice && !(buyNowPrice <= -1)) {
				AuctionHouse.getInstance().getLocale().getMessage("pricing.basepricetoolow").sendPrefixedMessage(player);
				return ReturnType.FAILURE;
			}
		}

		if (!Settings.ALLOW_ITEM_BUNDLES.getBoolean() && isBundle) {
			return ReturnType.FAILURE;
		} else {
			if (isBundle) {
				if (NBTEditor.contains(itemToSell, "AuctionBundleItem")) {
					AuctionHouse.getInstance().getLocale().getMessage("general.cannotsellbundleditem").sendPrefixedMessage(player);
					return ReturnType.FAILURE;
				}

				itemToSell = AuctionAPI.getInstance().createBundledItem(itemToSell, AuctionAPI.getInstance().getSimilarItemsFromInventory(player, itemToSell).toArray(new ItemStack[0]));
			}
		}

		final boolean buyNowAllow = Settings.ALLOW_USAGE_OF_BUY_NOW_SYSTEM.getBoolean();

		if (Settings.FORCE_AUCTION_USAGE.getBoolean() && startingBid == null) {
			return ReturnType.SYNTAX_ERROR;
		}

		// update the listing time to the max allowed time if it wasn't set using the command flag
		allowedTime = auctionPlayer.getAllowedSellTime(isBiddingItem ? AuctionSaleType.USED_BIDDING_SYSTEM : AuctionSaleType.WITHOUT_BIDDING_SYSTEM);


		if (Settings.ASK_FOR_LISTING_CONFIRMATION.getBoolean()) {
			AuctionHouse.getInstance().getGuiManager().showGUI(player, new GUIConfirmListing(
					player,
					originalItem,
					itemToSell,
					allowedTime,
					/* buy now price */ buyNowAllow ? buyNowPrice : -1,
					/* start bid price */ isBiddingItem ? startingBid : 0,
					/* bid inc price */ isBiddingItem ? bidIncrement != null ? bidIncrement : Settings.MIN_AUCTION_INCREMENT_PRICE.getDouble() : 0,
					isBiddingItem,
					isBundle,
					true
			));
		} else {
			AuctionAPI.getInstance().listAuction(
					player,
					originalItem,
					itemToSell,
					allowedTime,
					/* buy now price */ buyNowAllow ? buyNowPrice : -1,
					/* start bid price */ isBiddingItem ? startingBid : !buyNowAllow ? buyNowPrice : 0,
					/* bid inc price */ isBiddingItem ? bidIncrement != null ? bidIncrement : Settings.MIN_AUCTION_INCREMENT_PRICE.getDouble() : 0,
					/* current price */ isBiddingItem ? startingBid : buyNowPrice <= -1 ? startingBid : buyNowPrice,
					isBiddingItem || !buyNowAllow,
					isBundle,
					true
			);
		}

		return ReturnType.SUCCESS;
	}

	private boolean checkBasePrice(final Player player, final double val, boolean allowMinusOne) {
		if (val < Settings.MIN_AUCTION_PRICE.getDouble()) {
			if (allowMinusOne && val <= -1) return true;
			AuctionHouse.getInstance().getLocale().getMessage("pricing.minbaseprice").processPlaceholder("price", Settings.MIN_AUCTION_PRICE.getDouble()).sendPrefixedMessage(player);
			return false;
		}

		if (val > Settings.MAX_AUCTION_PRICE.getDouble()) {
			AuctionHouse.getInstance().getLocale().getMessage("pricing.maxbaseprice").processPlaceholder("price", Settings.MAX_AUCTION_PRICE.getDouble()).sendPrefixedMessage(player);
			return false;
		}
		return true;
	}

	@Override
	protected List<String> onTab(CommandSender sender, String... args) {
		if (args.length == 1)
			return Arrays.asList(AuctionHouse.getInstance().getLocale().getMessage("commands.sell.args.suggestion one").getMessage().split(" "));
		if (args.length == 2)
			return Arrays.asList(AuctionHouse.getInstance().getLocale().getMessage("commands.sell.args.suggestion two").getMessage().split(" "));
		if (args.length == 3)
			return Arrays.asList(AuctionHouse.getInstance().getLocale().getMessage("commands.sell.args.suggestion three").getMessage().split(" "));
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
