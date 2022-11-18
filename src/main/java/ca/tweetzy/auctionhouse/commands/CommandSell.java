/*
 * Auction House
 * Copyright 2018-2022 Kiran Hart
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ca.tweetzy.auctionhouse.commands;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.auction.AuctionedItem;
import ca.tweetzy.auctionhouse.auction.enums.AuctionSaleType;
import ca.tweetzy.auctionhouse.guis.GUIAuctionHouse;
import ca.tweetzy.auctionhouse.guis.GUIBundleCreation;
import ca.tweetzy.auctionhouse.guis.confirmation.GUIListingConfirm;
import ca.tweetzy.auctionhouse.guis.sell.GUISellListingType;
import ca.tweetzy.auctionhouse.guis.sell.GUISellPlaceItem;
import ca.tweetzy.auctionhouse.helpers.MaterialCategorizer;
import ca.tweetzy.auctionhouse.helpers.PlayerHelper;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.commands.AbstractCommand;
import ca.tweetzy.core.compatibility.XMaterial;
import ca.tweetzy.core.utils.NumberUtils;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.core.utils.nms.NBTEditor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
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

		if (CommandMiddleware.handle(player) == ReturnType.FAILURE) return ReturnType.FAILURE;

		final AuctionHouse instance = AuctionHouse.getInstance();
		if (instance.getAuctionPlayerManager().getPlayer(player.getUniqueId()) == null) {
			instance.getLocale().newMessage(TextUtils.formatText("&cCould not find auction player instance for&f: &e" + player.getName() + "&c creating one now.")).sendPrefixedMessage(Bukkit.getConsoleSender());
			instance.getAuctionPlayerManager().addPlayer(new AuctionPlayer(player));
		}

		AuctionPlayer auctionPlayer = instance.getAuctionPlayerManager().getPlayer(player.getUniqueId());

		ItemStack originalItem = PlayerHelper.getHeldItem(player).clone();
		ItemStack itemToSell = PlayerHelper.getHeldItem(player).clone();

		// check if player is at their selling limit
		if (auctionPlayer.isAtSellLimit()) {
			instance.getLocale().getMessage("general.sellinglimit").sendPrefixedMessage(player);
			return ReturnType.FAILURE;
		}

		// Open the sell menu enabled
		if (args.length == 0) {
			if (!Settings.ALLOW_USAGE_OF_SELL_GUI.getBoolean()) {
				return ReturnType.SYNTAX_ERROR;
			}

			if (itemToSell.getType() == XMaterial.AIR.parseMaterial() && Settings.SELL_MENU_REQUIRES_USER_TO_HOLD_ITEM.getBoolean()) {
				instance.getLocale().getMessage("general.air").sendPrefixedMessage(player);
				return ReturnType.FAILURE;
			} else {
				instance.getGuiManager().showGUI(player, new GUISellListingType(auctionPlayer, selected -> {
					instance.getGuiManager().showGUI(player, new GUISellPlaceItem(auctionPlayer, GUISellPlaceItem.ViewMode.SINGLE_ITEM, selected));
				}));
			}
			return ReturnType.SUCCESS;
		}

		if (itemToSell.getType() == XMaterial.AIR.parseMaterial()) {
			instance.getLocale().getMessage("general.air").sendPrefixedMessage(player);
			return ReturnType.FAILURE;
		}

		// Check for block items

		if (Settings.MAKE_BLOCKED_ITEMS_A_WHITELIST.getBoolean()) {
			if (!Settings.BLOCKED_ITEMS.getStringList().contains(itemToSell.getType().name())) {
				instance.getLocale().getMessage("general.blockeditem").processPlaceholder("item", itemToSell.getType().name()).sendPrefixedMessage(player);
				return ReturnType.FAILURE;
			}
		} else {
			if (Settings.BLOCKED_ITEMS.getStringList().contains(itemToSell.getType().name())) {
				instance.getLocale().getMessage("general.blockeditem").processPlaceholder("item", itemToSell.getType().name()).sendPrefixedMessage(player);
				return ReturnType.FAILURE;
			}
		}

		boolean blocked = false;

		String itemName = ChatColor.stripColor(AuctionAPI.getInstance().getItemName(itemToSell).toLowerCase());
		List<String> itemLore = AuctionAPI.getInstance().getItemLore(itemToSell).stream().map(line -> ChatColor.stripColor(line.toLowerCase())).collect(Collectors.toList());

		// Check for blocked names and lore
		for (String s : Settings.BLOCKED_ITEM_NAMES.getStringList()) {
			if (AuctionAPI.getInstance().match(s, itemName)) {
				instance.getLocale().getMessage("general.blockedname").sendPrefixedMessage(player);
				blocked = true;
			}
		}

		if (!itemLore.isEmpty() && !blocked) {
			for (String s : Settings.BLOCKED_ITEM_LORES.getStringList()) {
				for (String line : itemLore) {
					if (AuctionAPI.getInstance().match(s, line)) {
						instance.getLocale().getMessage("general.blockedlore").sendPrefixedMessage(player);
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
		boolean isInfinite = false;
		boolean isStackPrice = false;
		boolean partialBuy = false;

		List<String> timeSets = Arrays.asList(
				"second",
				"minute",
				"hour",
				"day",
				"week",
				"month"
		);

		for (int i = 0; i < args.length; i++) {

			if (NumberUtils.isDouble(args[i]) && !Double.isNaN(Double.parseDouble(args[i]))) {
				boolean hasTimeValue = false;

				if (i + 1 < args.length) {
					if (timeSets.contains(args[i + 1].toLowerCase()))
						hasTimeValue = true;
				}

				if (!hasTimeValue) {
					if (buyNowPrice == null)
						buyNowPrice = Double.parseDouble(args[i]);
					else if (startingBid == null)
						startingBid = Double.parseDouble(args[i]);
					else
						bidIncrement = Double.parseDouble(args[i]);
				}
			}

			if (args[i].equalsIgnoreCase("-b") || args[i].equalsIgnoreCase("-bundle"))
				isBundle = true;

			if (args[i].equalsIgnoreCase("-p") || args[i].equalsIgnoreCase("-partialbuy"))
				partialBuy = true;

			if (player.hasPermission("auctionhouse.cmdflag.stack") && args[i].equalsIgnoreCase("-s") || args[i].equalsIgnoreCase("-stack"))
				isStackPrice = true;

			if ((args[i].equalsIgnoreCase("-i") || args[i].equalsIgnoreCase("-infinite")) && (player.hasPermission("auctionhouse.admin") || player.isOp()))
				isInfinite = true;

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
			instance.getLocale().getMessage("general.please_enter_at_least_one_number").sendPrefixedMessage(player);
			return ReturnType.FAILURE;
		}

		boolean isBiddingItem = Settings.FORCE_AUCTION_USAGE.getBoolean() || buyNowPrice != null && startingBid != null && Settings.ALLOW_USAGE_OF_BID_SYSTEM.getBoolean();

		// NOT USING THE BIDDING SYSTEM
		if (!isBiddingItem /* && buyNowPrice != null */) {
			// min item price todo fix it broke
			if (!AuctionAPI.getInstance().meetsMinItemPrice(isBundle, isBiddingItem, originalItem, buyNowPrice, isBiddingItem ? startingBid : 0)) {
				instance.getLocale().getMessage("pricing.minitemprice").processPlaceholder("price", AuctionAPI.getInstance().formatNumber(instance.getMinItemPriceManager().getMinPrice(originalItem).getPrice())).sendPrefixedMessage(player);
				return ReturnType.FAILURE;
			}

			// Check the if the price meets the min/max criteria
			if (!checkBasePrice(player, buyNowPrice, false)) return ReturnType.FAILURE;
		}

		if (isBiddingItem && /* buyNowPrice != null && */ startingBid != null) {
			// min item price todo fix it broke
			if (!AuctionAPI.getInstance().meetsMinItemPrice(isBundle, isBiddingItem, originalItem, buyNowPrice, isBiddingItem ? startingBid : 0)) {
				instance.getLocale().getMessage("pricing.minitemprice").processPlaceholder("price", AuctionAPI.getInstance().formatNumber(instance.getMinItemPriceManager().getMinPrice(originalItem).getPrice())).sendPrefixedMessage(player);
				return ReturnType.FAILURE;
			}

			if (!checkBasePrice(player, buyNowPrice, true)) return ReturnType.FAILURE;

			// check the starting bid values
			if (startingBid < Settings.MIN_AUCTION_INCREMENT_PRICE.getDouble()) {
				instance.getLocale().getMessage("pricing.minstartingprice").processPlaceholder("price", Settings.MIN_AUCTION_INCREMENT_PRICE.getDouble()).sendPrefixedMessage(player);
				return ReturnType.FAILURE;
			}

			if (startingBid > Settings.MAX_AUCTION_START_PRICE.getDouble()) {
				instance.getLocale().getMessage("pricing.maxstartingprice").processPlaceholder("price", Settings.MAX_AUCTION_START_PRICE.getDouble()).sendPrefixedMessage(player);
				return ReturnType.FAILURE;
			}

			// if present check the bid increment pricing
			if (bidIncrement != null) {
				if (bidIncrement < Settings.MIN_AUCTION_INCREMENT_PRICE.getDouble()) {
					instance.getLocale().getMessage("pricing.minbidincrementprice").processPlaceholder("price", Settings.MIN_AUCTION_INCREMENT_PRICE.getDouble()).sendPrefixedMessage(player);
					return ReturnType.FAILURE;
				}

				if (bidIncrement > Settings.MAX_AUCTION_INCREMENT_PRICE.getDouble()) {
					instance.getLocale().getMessage("pricing.maxbidincrementprice").processPlaceholder("price", Settings.MAX_AUCTION_START_PRICE.getDouble()).sendPrefixedMessage(player);
					return ReturnType.FAILURE;
				}
			} else {
				bidIncrement = 1.0D;
			}

			// check if the starting bid is not higher than the buy now
			if (Settings.BASE_PRICE_MUST_BE_HIGHER_THAN_BID_START.getBoolean() && startingBid > buyNowPrice && !(buyNowPrice <= -1)) {
				instance.getLocale().getMessage("pricing.basepricetoolow").sendPrefixedMessage(player);
				return ReturnType.FAILURE;
			}
		}

		if (Settings.SMART_MIN_BUY_PRICE.getBoolean() && itemToSell.getAmount() > 1) {
			buyNowPrice = isStackPrice ? buyNowPrice : buyNowPrice * itemToSell.getAmount();
		}

		if (!Settings.ALLOW_ITEM_BUNDLES.getBoolean() && isBundle) {
			return ReturnType.FAILURE;
		} else {
			if (isBundle) {
				if (NBTEditor.contains(itemToSell, "AuctionBundleItem")) {
					instance.getLocale().getMessage("general.cannotsellbundleditem").sendPrefixedMessage(player);
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
		allowedTime = allowedTime != 0 ? allowedTime : auctionPlayer.getAllowedSellTime(isBiddingItem ? AuctionSaleType.USED_BIDDING_SYSTEM : AuctionSaleType.WITHOUT_BIDDING_SYSTEM);

		// Check list delay
		if (!auctionPlayer.canListItem()) {
			return ReturnType.FAILURE;
		}

		if (isBundle) {
			instance.getGuiManager().showGUI(player, new GUIBundleCreation(
					auctionPlayer,
					allowedTime,
					buyNowAllow,
					isBiddingItem,
					buyNowAllow ? buyNowPrice : -1,
					isBiddingItem ? startingBid : 0,
					isBiddingItem ? bidIncrement != null ? bidIncrement : Settings.MIN_AUCTION_INCREMENT_PRICE.getDouble() : 0
			));
			return ReturnType.SUCCESS;
		}

		if (Settings.ASK_FOR_LISTING_CONFIRMATION.getBoolean()) {

			// TODO clean up is monstrosity
			AuctionedItem auctionedItem = new AuctionedItem();
			auctionedItem.setId(UUID.randomUUID());
			auctionedItem.setOwner(player.getUniqueId());
			auctionedItem.setHighestBidder(player.getUniqueId());
			auctionedItem.setOwnerName(player.getName());
			auctionedItem.setHighestBidderName(player.getName());
			auctionedItem.setItem(itemToSell);
			auctionedItem.setCategory(MaterialCategorizer.getMaterialCategory(itemToSell));
			auctionedItem.setExpiresAt(System.currentTimeMillis() + 1000L * allowedTime);
			auctionedItem.setBidItem(isBiddingItem);
			auctionedItem.setExpired(false);

			auctionedItem.setBasePrice(Settings.ROUND_ALL_PRICES.getBoolean() ? Math.round(buyNowAllow ? buyNowPrice : -1) : buyNowAllow ? buyNowPrice : -1);
			auctionedItem.setBidStartingPrice(Settings.ROUND_ALL_PRICES.getBoolean() ? Math.round(isBiddingItem ? startingBid : 0) : isBiddingItem ? startingBid : 0);
			auctionedItem.setBidIncrementPrice(Settings.ROUND_ALL_PRICES.getBoolean() ? Math.round(isBiddingItem ? bidIncrement != null ? bidIncrement : Settings.MIN_AUCTION_INCREMENT_PRICE.getDouble() : 0) : isBiddingItem ? bidIncrement != null ? bidIncrement : Settings.MIN_AUCTION_INCREMENT_PRICE.getDouble() : 0);
			auctionedItem.setCurrentPrice(Settings.ROUND_ALL_PRICES.getBoolean() ? Math.round(isBiddingItem ? startingBid : buyNowPrice <= -1 ? startingBid : buyNowPrice) : isBiddingItem ? startingBid : buyNowPrice <= -1 ? startingBid : buyNowPrice);

			auctionedItem.setListedWorld(player.getWorld().getName());
			auctionedItem.setInfinite(isInfinite);
			auctionedItem.setAllowPartialBuy(partialBuy);

			ItemStack finalItemToSell = itemToSell;
			int finalAllowedTime = allowedTime;
			Double finalBuyNowPrice = buyNowPrice;
			Double finalStartingBid = startingBid;
			Double finalBidIncrement = bidIncrement;
			Double finalStartingBid1 = startingBid;
			boolean finalIsInfinite = isInfinite;
			boolean finalPartialBuy = partialBuy;
			boolean finalIsBundle = isBundle;

			instance.getGuiManager().showGUI(player, new GUIListingConfirm(player, auctionedItem, result -> {
				if (!result) {
					player.closeInventory();
					return;
				}

				AuctionAPI.getInstance().listAuction(
						player,
						originalItem,
						finalItemToSell,
						finalAllowedTime,
						/* buy now price */ buyNowAllow ? finalBuyNowPrice : -1,
						/* start bid price */ isBiddingItem ? finalStartingBid : !buyNowAllow ? finalBuyNowPrice : 0,
						/* bid inc price */ isBiddingItem ? finalBidIncrement != null ? finalBidIncrement : Settings.MIN_AUCTION_INCREMENT_PRICE.getDouble() : 0,
						/* current price */ isBiddingItem ? finalStartingBid : finalBuyNowPrice <= -1 ? finalStartingBid1 : finalBuyNowPrice,
						isBiddingItem || !buyNowAllow,
						finalIsBundle,
						true,
						finalIsInfinite,
						finalPartialBuy
				);

				player.closeInventory();
				if (Settings.OPEN_MAIN_AUCTION_HOUSE_AFTER_MENU_LIST.getBoolean()) {
					instance.getGuiManager().showGUI(player, new GUIAuctionHouse(instance.getAuctionPlayerManager().getPlayer(player.getUniqueId())));
				}
			}));
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
					true,
					isInfinite,
					partialBuy
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
