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
import ca.tweetzy.auctionhouse.api.auction.ListingResult;
import ca.tweetzy.auctionhouse.api.ban.BanType;
import ca.tweetzy.auctionhouse.api.currency.AbstractCurrency;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.auction.AuctionedItem;
import ca.tweetzy.auctionhouse.auction.ListingType;
import ca.tweetzy.auctionhouse.auction.enums.AuctionSaleType;
import ca.tweetzy.auctionhouse.guis.confirmation.GUIListingConfirm;
import ca.tweetzy.auctionhouse.guis.core.GUIAuctionHouse;
import ca.tweetzy.auctionhouse.guis.core.GUIBundleCreation;
import ca.tweetzy.auctionhouse.guis.sell.GUISellListingType;
import ca.tweetzy.auctionhouse.guis.sell.GUISellPlaceItem;
import ca.tweetzy.auctionhouse.helpers.AuctionCreator;
import ca.tweetzy.auctionhouse.helpers.BundleUtil;
import ca.tweetzy.auctionhouse.helpers.PlayerHelper;
import ca.tweetzy.auctionhouse.helpers.Validate;
import ca.tweetzy.auctionhouse.model.MaterialCategorizer;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.compatibility.XMaterial;
import ca.tweetzy.core.utils.NumberUtils;
import ca.tweetzy.core.utils.PlayerUtils;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.flight.command.AllowedExecutor;
import ca.tweetzy.flight.command.Command;
import ca.tweetzy.flight.command.ReturnType;
import ca.tweetzy.flight.nbtapi.NBT;
import ca.tweetzy.flight.utils.Common;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * The current file has been created by Kiran Hart
 * Date Created: August 22 2021
 * Time Created: 6:51 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public final class CommandSell extends Command {

	public CommandSell() {
		super(AllowedExecutor.PLAYER, Settings.CMD_ALIAS_SUB_SELL.getStringList().toArray(new String[0]));
	}

	@Override
	protected ReturnType execute(CommandSender sender, String... args) {
		Player player = (Player) sender;

		if (CommandMiddleware.handleAccessHours(player) == ReturnType.FAIL) return ReturnType.FAIL;
		if (CommandMiddleware.handle(player) == ReturnType.FAIL) return ReturnType.FAIL;
		if (AuctionHouse.getBanManager().isStillBanned(player, BanType.EVERYTHING, BanType.SELL)) return ReturnType.FAIL;


		if (AuctionHouse.getAuctionPlayerManager().getPlayer(player.getUniqueId()) == null) {
			AuctionHouse.getInstance().getLocale().newMessage(TextUtils.formatText("&cCould not find auction player instance for&f: &e" + player.getName() + "&c creating one now.")).sendPrefixedMessage(Bukkit.getConsoleSender());
			AuctionHouse.getAuctionPlayerManager().addPlayer(new AuctionPlayer(player));
		}

		AuctionPlayer auctionPlayer = AuctionHouse.getAuctionPlayerManager().getPlayer(player.getUniqueId());
		if (!Bukkit.getOfflinePlayer(player.getUniqueId()).isOnline())
			return ReturnType.FAIL;

		ItemStack originalItem = PlayerHelper.getHeldItem(player).clone();
		ItemStack itemToSell = PlayerHelper.getHeldItem(player).clone();

		// check if player is at their selling limit
		if (auctionPlayer.isAtItemLimit(player)) {
//			instance.getLocale().getMessage("general.sellinglimit").sendPrefixedMessage(player);
			return ReturnType.FAIL;
		}

		// Open the sell menu enabled
		if (args.length == 0) {
			if (!Settings.ALLOW_USAGE_OF_SELL_GUI.getBoolean()) {
				return ReturnType.INVALID_SYNTAX;
			}

			if (itemToSell.getType() == XMaterial.AIR.parseMaterial() && Settings.SELL_MENU_REQUIRES_USER_TO_HOLD_ITEM.getBoolean()) {
				AuctionHouse.getInstance().getLocale().getMessage("general.air").sendPrefixedMessage(player);
				return ReturnType.FAIL;
			} else {
				if (Settings.SELL_MENU_SKIPS_TYPE_SELECTION.getBoolean()) {

					if (Settings.FORCE_AUCTION_USAGE.getBoolean()) {
						AuctionHouse.getGuiManager().showGUI(player, new GUISellPlaceItem(auctionPlayer, GUISellPlaceItem.ViewMode.SINGLE_ITEM, ListingType.AUCTION));
						return ReturnType.SUCCESS;
					}

					if (!Settings.ALLOW_USAGE_OF_BID_SYSTEM.getBoolean()) {
						AuctionHouse.getGuiManager().showGUI(player, new GUISellPlaceItem(auctionPlayer, GUISellPlaceItem.ViewMode.SINGLE_ITEM, ListingType.BIN));
						return ReturnType.SUCCESS;
					}

					AuctionHouse.getGuiManager().showGUI(player, new GUISellListingType(auctionPlayer, selected -> {
						AuctionHouse.getGuiManager().showGUI(player, new GUISellPlaceItem(auctionPlayer, GUISellPlaceItem.ViewMode.SINGLE_ITEM, selected));
					}));

				} else {
					AuctionHouse.getGuiManager().showGUI(player, new GUISellListingType(auctionPlayer, selected -> {
						AuctionHouse.getGuiManager().showGUI(player, new GUISellPlaceItem(auctionPlayer, GUISellPlaceItem.ViewMode.SINGLE_ITEM, selected));
					}));
				}

			}
			return ReturnType.SUCCESS;
		}

		if (itemToSell.getType() == XMaterial.AIR.parseMaterial()) {
			AuctionHouse.getInstance().getLocale().getMessage("general.air").sendPrefixedMessage(player);
			return ReturnType.FAIL;
		}

		// check if item has dt key
		if (Validate.hasDTKey(originalItem)) {
			return ReturnType.FAIL;
		}

		// Check for block items
		if (!AuctionAPI.getInstance().meetsListingRequirements(player, itemToSell)) return ReturnType.FAIL;

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
		boolean serverAuction = false;
		String currency = AuctionHouse.getCurrencyManager().getDefaultCurrency().getStoreableName();

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

			// check if the listing should be a server auction
			if (args[i].equalsIgnoreCase("-server") && (player.hasPermission("auctionhouse.admin") || player.isOp()))
				serverAuction = true;

			if (args[i].toLowerCase().startsWith("-t") && Settings.ALLOW_PLAYERS_TO_DEFINE_AUCTION_TIME.getBoolean()) {
				if (i + 2 < args.length) {
					int customTime = (int) AuctionAPI.toTicks(args[i + 1] + " " + args[i + 2]);

					if (customTime <= Settings.MAX_CUSTOM_DEFINED_TIME.getInt())
						allowedTime = customTime;
				}
			}

			if (Settings.CURRENCY_ALLOW_PICK.getBoolean() && AuctionHouse.getCurrencyManager().locateCurrency(args[i]) != null) {
				final AbstractCurrency curr = AuctionHouse.getCurrencyManager().locateCurrency(args[i]);
				currency = curr.getStoreableName();
			}
		}
		// check buy now price null
		if (buyNowPrice == null) {
			AuctionHouse.getInstance().getLocale().getMessage("general.please_enter_at_least_one_number").sendPrefixedMessage(player);
			return ReturnType.FAIL;
		}

		final boolean isBiddingItem = Settings.FORCE_AUCTION_USAGE.getBoolean() || buyNowPrice != null && startingBid != null && Settings.ALLOW_USAGE_OF_BID_SYSTEM.getBoolean();

		// NOT USING THE BIDDING SYSTEM
		if (!isBiddingItem) {
			if (!AuctionAPI.getInstance().meetsMinItemPrice(isBundle, isBiddingItem, originalItem, buyNowPrice, isBiddingItem ? startingBid : 0)) {
				AuctionHouse.getInstance().getLocale().getMessage("pricing.minitemprice")
						.processPlaceholder("price", AuctionHouse.getAPI().getNumberAsCurrency(AuctionHouse.getPriceLimitManager().getPriceLimit(originalItem).getMinPrice()))
						.sendPrefixedMessage(player);

				return ReturnType.FAIL;
			}

			if (AuctionAPI.getInstance().isAtMaxItemPrice(isBundle, isBiddingItem, originalItem, buyNowPrice, isBiddingItem ? startingBid : 0)) {
				AuctionHouse.getInstance().getLocale().getMessage("pricing.maxitemprice")
						.processPlaceholder("price", AuctionHouse.getAPI().getNumberAsCurrency(AuctionHouse.getPriceLimitManager().getPriceLimit(originalItem).getMaxPrice()))
						.sendPrefixedMessage(player);

				return ReturnType.FAIL;
			}

			// Check the if the price meets the min/max criteria
			if (!checkBasePrice(player, buyNowPrice, false)) return ReturnType.FAIL;
		}

		if (isBiddingItem && startingBid != null) {
			if (!AuctionAPI.getInstance().meetsMinItemPrice(isBundle, true, originalItem, buyNowPrice, startingBid)) {
				AuctionHouse.getInstance().getLocale().getMessage("pricing.minitemprice")
						.processPlaceholder("price", AuctionHouse.getAPI().getNumberAsCurrency(AuctionHouse.getPriceLimitManager().getPriceLimit(originalItem).getMinPrice()))
						.sendPrefixedMessage(player);
				return ReturnType.FAIL;
			}

			if (AuctionAPI.getInstance().isAtMaxItemPrice(isBundle, isBiddingItem, originalItem, buyNowPrice, startingBid)) {
				AuctionHouse.getInstance().getLocale().getMessage("pricing.maxitemprice")
						.processPlaceholder("price", AuctionHouse.getAPI().getNumberAsCurrency(AuctionHouse.getPriceLimitManager().getPriceLimit(originalItem).getMaxPrice()))
						.sendPrefixedMessage(player);

				return ReturnType.FAIL;
			}

			if (!checkBasePrice(player, buyNowPrice, true)) return ReturnType.FAIL;

			// check the starting bid values
			if (startingBid < Settings.MIN_AUCTION_INCREMENT_PRICE.getDouble()) {
				AuctionHouse.getInstance().getLocale().getMessage("pricing.minstartingprice").processPlaceholder("price", Settings.MIN_AUCTION_INCREMENT_PRICE.getDouble()).sendPrefixedMessage(player);
				return ReturnType.FAIL;
			}

			if (startingBid > Settings.MAX_AUCTION_START_PRICE.getDouble()) {
				AuctionHouse.getInstance().getLocale().getMessage("pricing.maxstartingprice").processPlaceholder("price", Settings.MAX_AUCTION_START_PRICE.getDouble()).sendPrefixedMessage(player);
				return ReturnType.FAIL;
			}

			// if present check the bid increment pricing
			if (bidIncrement != null) {
				if (bidIncrement < Settings.MIN_AUCTION_INCREMENT_PRICE.getDouble()) {
					AuctionHouse.getInstance().getLocale().getMessage("pricing.minbidincrementprice").processPlaceholder("price", Settings.MIN_AUCTION_INCREMENT_PRICE.getDouble()).sendPrefixedMessage(player);
					return ReturnType.FAIL;
				}

				if (bidIncrement > Settings.MAX_AUCTION_INCREMENT_PRICE.getDouble()) {
					AuctionHouse.getInstance().getLocale().getMessage("pricing.maxbidincrementprice").processPlaceholder("price", Settings.MAX_AUCTION_START_PRICE.getDouble()).sendPrefixedMessage(player);
					return ReturnType.FAIL;
				}
			} else {
				bidIncrement = 1.0D;
			}

			// check if the starting bid is not higher than the buy now
			if (Settings.BASE_PRICE_MUST_BE_HIGHER_THAN_BID_START.getBoolean() && startingBid > buyNowPrice && !(buyNowPrice <= -1)) {
				AuctionHouse.getInstance().getLocale().getMessage("pricing.basepricetoolow").sendPrefixedMessage(player);
				return ReturnType.FAIL;
			}
		}

		if (Settings.SMART_MIN_BUY_PRICE.getBoolean() && itemToSell.getAmount() > 1) {
			buyNowPrice = isStackPrice ? buyNowPrice : buyNowPrice * itemToSell.getAmount();
		}

		if (!Settings.ALLOW_ITEM_BUNDLES.getBoolean() && isBundle) {
			return ReturnType.FAIL;
		} else {
			if (isBundle) {
				if (BundleUtil.isBundledItem(itemToSell)) {
					AuctionHouse.getInstance().getLocale().getMessage("general.cannotsellbundleditem").sendPrefixedMessage(player);
					return ReturnType.FAIL;
				}

				itemToSell = AuctionAPI.getInstance().createBundledItem(itemToSell, AuctionAPI.getInstance().getSimilarItemsFromInventory(player, itemToSell).toArray(new ItemStack[0]));
			}
		}

		final boolean buyNowAllow = Settings.ALLOW_USAGE_OF_BUY_NOW_SYSTEM.getBoolean();

		if (Settings.FORCE_AUCTION_USAGE.getBoolean() && startingBid == null) {
			return ReturnType.INVALID_SYNTAX;
		}

		// update the listing time to the max allowed time if it wasn't set using the command flag
		allowedTime = allowedTime != 0 ? allowedTime : auctionPlayer.getAllowedSellTime(
				isBiddingItem ? AuctionSaleType.USED_BIDDING_SYSTEM : AuctionSaleType.WITHOUT_BIDDING_SYSTEM
		);

		// Check list delay
		if (!auctionPlayer.canListItem()) {
			return ReturnType.FAIL;
		}

		if (auctionPlayer.isAtBundleLimit() && isBundle) {
			AuctionHouse.getInstance().getLocale().getMessage("general.bundlelistlimit").sendPrefixedMessage(player);
			return ReturnType.FAIL;
		}

		if (isBundle) {
			AuctionHouse.getGuiManager().showGUI(player, new GUIBundleCreation(
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

		AuctionedItem auctionedItem = new AuctionedItem();
		auctionedItem.setId(UUID.randomUUID());
		auctionedItem.setOwner(player.getUniqueId());
		auctionedItem.setHighestBidder(player.getUniqueId());
		auctionedItem.setOwnerName(player.getName());
		auctionedItem.setHighestBidderName(player.getName());

		// SCUFFED SHIT
		if (!auctionedItem.isRequest())
			NBT.modify(itemToSell, nbt -> {
				nbt.setUUID("AuctionDupeTracking", auctionedItem.getId());
			});

		auctionedItem.setItem(itemToSell);
		auctionedItem.setCategory(MaterialCategorizer.getMaterialCategory(itemToSell));
		auctionedItem.setExpiresAt(System.currentTimeMillis() + 1000L * allowedTime);
		auctionedItem.setBidItem(isBiddingItem);
		auctionedItem.setServerItem(serverAuction);
		auctionedItem.setExpired(false);

		double theStartingPrice = buyNowAllow ? buyNowPrice : -1;

		if (Settings.FORCE_AUCTION_USAGE.getBoolean()) {
			theStartingPrice = buyNowPrice;
			auctionedItem.setBasePrice(-1);
			auctionedItem.setBidStartingPrice(Settings.ROUND_ALL_PRICES.getBoolean() ? Math.round(theStartingPrice) : theStartingPrice);
			auctionedItem.setBidIncrementPrice(Settings.ROUND_ALL_PRICES.getBoolean() ? Math.round(startingBid != null ? startingBid : Settings.MIN_AUCTION_INCREMENT_PRICE.getDouble()) : startingBid != null ? startingBid : Settings.MIN_AUCTION_INCREMENT_PRICE.getDouble());
			auctionedItem.setCurrentPrice(Settings.ROUND_ALL_PRICES.getBoolean() ? Math.round(theStartingPrice) : theStartingPrice);
		} else {
			auctionedItem.setBasePrice(Settings.ROUND_ALL_PRICES.getBoolean() ? Math.round(theStartingPrice) : theStartingPrice);
			auctionedItem.setBidStartingPrice(Settings.ROUND_ALL_PRICES.getBoolean() ? Math.round(isBiddingItem ? startingBid : 0) : isBiddingItem ? startingBid : 0);
			auctionedItem.setBidIncrementPrice(Settings.ROUND_ALL_PRICES.getBoolean() ? Math.round(isBiddingItem ? bidIncrement != null ? bidIncrement : Settings.MIN_AUCTION_INCREMENT_PRICE.getDouble() : 0) : isBiddingItem ? bidIncrement != null ? bidIncrement : Settings.MIN_AUCTION_INCREMENT_PRICE.getDouble() : 0);
			auctionedItem.setCurrentPrice(Settings.ROUND_ALL_PRICES.getBoolean() ? Math.round(isBiddingItem ? startingBid : buyNowPrice <= -1 ? startingBid : buyNowPrice) : isBiddingItem ? startingBid : buyNowPrice <= -1 ? startingBid : buyNowPrice);
		}

		auctionedItem.setListedWorld(player.getWorld().getName());
		auctionedItem.setInfinite(isInfinite);
		auctionedItem.setAllowPartialBuy(partialBuy);

		if (currency != null) {
			auctionedItem.setCurrency(currency);
			auctionedItem.setCurrencyItem(null);
		}

		AuctionHouse.getAuctionPlayerManager().addToSellProcess(player);

		if (Settings.ASK_FOR_LISTING_CONFIRMATION.getBoolean()) {
			player.getInventory().setItemInHand(XMaterial.AIR.parseItem());
			auctionPlayer.setItemBeingListed(auctionedItem.getItem());

			AuctionHouse.getGuiManager().showGUI(player, new GUIListingConfirm(player, auctionedItem, result -> {
				if (!result) {
					AuctionHouse.getAuctionPlayerManager().processSell(player);

					player.closeInventory();
					PlayerUtils.giveItem(player, auctionedItem.getCleanItem());
					auctionPlayer.setItemBeingListed(null);
					return;
				}

				/*
				========================== DUPE TESTING	==========================
				 */

				Bukkit.getScheduler().runTaskLaterAsynchronously(AuctionHouse.getInstance(), () -> {
					if (auctionPlayer.getPlayer() == null || !auctionPlayer.getPlayer().isOnline()) {
						return;
					}

					AuctionCreator.create(auctionPlayer, auctionedItem, (auction, listingResult) -> {
						AuctionHouse.getAuctionPlayerManager().processSell(player);

						if (listingResult != ListingResult.SUCCESS) {
							PlayerUtils.giveItem(player, auction.getCleanItem());
							auctionPlayer.setItemBeingListed(null);
							AuctionHouse.newChain().sync(player::closeInventory).execute();
							return;
						}

						if (Settings.OPEN_MAIN_AUCTION_HOUSE_AFTER_MENU_LIST.getBoolean()) {
							player.removeMetadata("AuctionHouseConfirmListing", AuctionHouse.getInstance());
							AuctionHouse.getGuiManager().showGUI(player, new GUIAuctionHouse(auctionPlayer));
						} else
							AuctionHouse.newChain().sync(player::closeInventory).execute();
					});

				}, Settings.INTERNAL_CREATE_DELAY.getInt());


				/*
				========================== DUPE TESTING	==========================
				 */
			}));
		} else {
			if (auctionPlayer.getPlayer() == null || !auctionPlayer.getPlayer().isOnline()) {
				return ReturnType.FAIL;
			}

			player.getInventory().setItemInHand(XMaterial.AIR.parseItem());

			AuctionCreator.create(auctionPlayer, auctionedItem, (auction, listingResult) -> {
				AuctionHouse.getAuctionPlayerManager().processSell(player);

				if (listingResult != ListingResult.SUCCESS) {
					PlayerUtils.giveItem(player, auction.getItem());
					auctionPlayer.setItemBeingListed(null);
					return;
				}

				if (Settings.OPEN_MAIN_AUCTION_HOUSE_AFTER_MENU_LIST.getBoolean()) {
					player.removeMetadata("AuctionHouseConfirmListing", AuctionHouse.getInstance());
					AuctionHouse.getGuiManager().showGUI(player, new GUIAuctionHouse(auctionPlayer));
				} else
					AuctionHouse.newChain().sync(player::closeInventory).execute();
			});

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
	protected List<String> tab(CommandSender sender, String... args) {
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
