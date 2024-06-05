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
import ca.tweetzy.auctionhouse.guis.core.GUIAuctionHouseV2;
import ca.tweetzy.auctionhouse.guis.sell.GUIRequestItem;
import ca.tweetzy.auctionhouse.helpers.AuctionCreator;
import ca.tweetzy.auctionhouse.helpers.PlayerHelper;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.commands.AbstractCommand;
import ca.tweetzy.core.compatibility.XMaterial;
import ca.tweetzy.core.utils.NumberUtils;
import ca.tweetzy.core.utils.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * The current file has been created by Kiran Hart
 * Date Created: March 15 2021
 * Time Created: 4:32 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class CommandRequest extends AbstractCommand {

	public CommandRequest() {
		super(CommandType.PLAYER_ONLY, "request");
	}

	@Override
	protected ReturnType runCommand(CommandSender sender, String... args) {
		final Player player = (Player) sender;
		final AuctionHouse instance = AuctionHouse.getInstance();

		if (CommandMiddleware.handle(player) == ReturnType.FAILURE) return ReturnType.FAILURE;

		if (instance.getAuctionPlayerManager().getPlayer(player.getUniqueId()) == null) {
			instance.getLocale().newMessage(TextUtils.formatText("&cCould not find auction player instance for&f: &e" + player.getName() + "&c creating one now.")).sendPrefixedMessage(Bukkit.getConsoleSender());
			instance.getAuctionPlayerManager().addPlayer(new AuctionPlayer(player));
		}

		final AuctionPlayer auctionPlayer = instance.getAuctionPlayerManager().getPlayer(player.getUniqueId());

		// grab held item & check valid
		final ItemStack originalItem = PlayerHelper.getHeldItem(player).clone();

		if (originalItem.getType() == XMaterial.AIR.parseMaterial()) {
			instance.getLocale().getMessage("general.air").sendPrefixedMessage(player);
			return ReturnType.FAILURE;
		}

		if (args.length < 1) {
			instance.getGuiManager().showGUI(player, new GUIRequestItem(auctionPlayer, originalItem, originalItem.getAmount(), 1));
			return ReturnType.SUCCESS;
		}

		// check if price is even a number
		if (!NumberUtils.isDouble(args[0])) {
			instance.getLocale().getMessage("general.notanumber").processPlaceholder("value", args[0]).sendPrefixedMessage(player);
			return ReturnType.FAILURE;
		}

		// Check for block items
		if (!AuctionAPI.getInstance().meetsListingRequirements(player, originalItem)) return ReturnType.FAILURE;

		// check if at limit
		if (auctionPlayer.isAtItemLimit(player)) {
			instance.getLocale().getMessage("general.requestlimit").sendPrefixedMessage(player);
			return ReturnType.FAILURE;
		}

		// get the max allowed time for this player.
		final int allowedTime = auctionPlayer.getAllowedSellTime(AuctionSaleType.WITHOUT_BIDDING_SYSTEM);

		// Check list delay
		if (!auctionPlayer.canListItem()) {
			return ReturnType.FAILURE;
		}

		// check min/max prices
		final double price = Double.parseDouble(args[0]);

		if (price < Settings.MIN_AUCTION_PRICE.getDouble()) {
			instance.getLocale().getMessage("pricing.minbaseprice").processPlaceholder("price", Settings.MIN_AUCTION_PRICE.getDouble()).sendPrefixedMessage(player);
			return ReturnType.FAILURE;
		}

		if (price > Settings.MAX_AUCTION_PRICE.getDouble()) {
			instance.getLocale().getMessage("pricing.maxbaseprice").processPlaceholder("price", Settings.MIN_AUCTION_PRICE.getDouble()).sendPrefixedMessage(player);
			return ReturnType.FAILURE;
		}

		AuctionedItem auctionedItem = AuctionedItem.createRequest(player, originalItem, originalItem.getAmount(), price, allowedTime);

		// TODO REMOVE THIS
//		auctionedItem.setId(UUID.randomUUID());
//		auctionedItem.setOwner(player.getUniqueId());
//		auctionedItem.setHighestBidder(player.getUniqueId());
//		auctionedItem.setOwnerName(player.getName());
//		auctionedItem.setHighestBidderName(player.getName());
//		auctionedItem.setBasePrice(price);
//		auctionedItem.setItem(originalItem.clone());
//		auctionedItem.setCategory(MaterialCategorizer.getMaterialCategory(originalItem));
//		auctionedItem.setExpiresAt(System.currentTimeMillis() + 1000L * allowedTime);
//		auctionedItem.setBidItem(false);
//		auctionedItem.setServerItem(false);
//		auctionedItem.setExpired(false);
//		auctionedItem.setListedWorld(player.getWorld().getName());
//		auctionedItem.setInfinite(false);
//		auctionedItem.setAllowPartialBuy(false);
//		auctionedItem.setRequest(true);
//		auctionedItem.setRequestAmount(originalItem.getAmount());

		AuctionHouse.getInstance().getAuctionPlayerManager().addToSellProcess(player);
		if (auctionPlayer.getPlayer() == null || !auctionPlayer.getPlayer().isOnline()) {
			return ReturnType.FAILURE;
		}

		AuctionCreator.create(auctionPlayer, auctionedItem, (auction, listingResult) -> {
			AuctionHouse.getInstance().getAuctionPlayerManager().processSell(player);

			if (Settings.OPEN_MAIN_AUCTION_HOUSE_AFTER_MENU_LIST.getBoolean()) {
				player.removeMetadata("AuctionHouseConfirmListing", AuctionHouse.getInstance());
				instance.getGuiManager().showGUI(player, new GUIAuctionHouseV2(auctionPlayer));
			} else
				AuctionHouse.newChain().sync(player::closeInventory).execute();
		});

		return ReturnType.SUCCESS;
	}

	@Override
	public String getPermissionNode() {
		return "auctionhouse.cmd.request";
	}

	@Override
	public String getSyntax() {
		return AuctionHouse.getInstance().getLocale().getMessage("commands.syntax.request").getMessage();
	}

	@Override
	public String getDescription() {
		return AuctionHouse.getInstance().getLocale().getMessage("commands.description.request").getMessage();
	}

	@Override
	protected List<String> onTab(CommandSender sender, String... args) {
		return null;
	}
}
