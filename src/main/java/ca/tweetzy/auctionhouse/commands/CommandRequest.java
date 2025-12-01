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
import ca.tweetzy.auctionhouse.api.ban.BanType;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.auction.AuctionedItem;
import ca.tweetzy.auctionhouse.auction.enums.AuctionSaleType;
import ca.tweetzy.auctionhouse.guis.core.GUIAuctionHouse;
import ca.tweetzy.auctionhouse.guis.sell.GUIRequestItem;
import ca.tweetzy.auctionhouse.helpers.AuctionCreator;
import ca.tweetzy.auctionhouse.helpers.PlayerHelper;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.utils.MathUtil;
import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.flight.command.AllowedExecutor;
import ca.tweetzy.flight.command.Command;
import ca.tweetzy.flight.command.CommandContext;
import ca.tweetzy.flight.command.ReturnType;
import ca.tweetzy.flight.comp.enums.ServerVersion;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.util.List;

/**
 * The current file has been created by Kiran Hart
 * Date Created: March 15 2021
 * Time Created: 4:32 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class CommandRequest extends Command {

	public CommandRequest() {
		super(AllowedExecutor.PLAYER, Settings.CMD_ALIAS_SUB_REQUEST.getStringList().toArray(new String[0]));
	}

	@Override
	protected ReturnType execute(CommandSender sender, String... args) {
		return execute(new CommandContext(sender, args, getSubCommands().isEmpty() ? "" : getSubCommands().get(0)));
	}

	@Override
	protected ReturnType execute(CommandContext context) {
		final Player player = context.getPlayer();

		if (CommandMiddleware.handleAccessHours(player) == ReturnType.FAIL) return ReturnType.FAIL;
		if (CommandMiddleware.handle(player) == ReturnType.FAIL) return ReturnType.FAIL;
		if (AuctionHouse.getBanManager().isStillBanned(player, BanType.EVERYTHING, BanType.REQUESTS)) return ReturnType.FAIL;

		if (AuctionHouse.getAuctionPlayerManager().getPlayer(player.getUniqueId()) == null) {
			AuctionHouse.getInstance().getLocale().newMessage(Common.colorize("&cCould not find auction player instance for&f: &e" + player.getName() + "&c creating one now.")).sendPrefixedMessage(Bukkit.getConsoleSender());
			AuctionHouse.getAuctionPlayerManager().addPlayer(new AuctionPlayer(player));
		}

		final AuctionPlayer auctionPlayer = AuctionHouse.getAuctionPlayerManager().getPlayer(player.getUniqueId());

		// grab held item & check valid
		final ItemStack originalItem = PlayerHelper.getHeldItem(player).clone();

		if (originalItem.getType() == CompMaterial.AIR.get()) {
			AuctionHouse.getInstance().getLocale().getMessage("general.air").sendPrefixedMessage(player);
			return ReturnType.FAIL;
		}

		// check if is shulker box and if contains items
		if (Settings.BLOCK_REQUEST_USING_FILLED_SHULKER.getBoolean() && ServerVersion.isServerVersionAtLeast(ServerVersion.V1_11)) {
			if (originalItem.getItemMeta() instanceof BlockStateMeta) {
				final BlockStateMeta meta = (BlockStateMeta) originalItem.getItemMeta();

				// check if shulker
				if (meta.getBlockState() instanceof ShulkerBox) {
					final ShulkerBox shulkerBox = (ShulkerBox) meta.getBlockState();

					boolean containsItems = false;
					for (ItemStack item : shulkerBox.getInventory().getContents()) {
						if (item != null && item.getType() != Material.AIR) {
							containsItems = true;
							break;
						}
					}

					if (containsItems) {
						AuctionHouse.getInstance().getLocale().getMessage("general.request shulker contains items").sendPrefixedMessage(player);
						return ReturnType.FAIL;
					}
				}

			}
		}

		if (context.getArgCount() == 0) {
			AuctionHouse.getGuiManager().showGUI(player, new GUIRequestItem(auctionPlayer, originalItem, originalItem.getAmount(), Settings.MIN_REQUEST_PRICE.getDouble()));
			return ReturnType.SUCCESS;
		}

		// check if price is even a number
		if (!MathUtil.isDouble(context.getArg(0))) {
			AuctionHouse.getInstance().getLocale().getMessage("general.notanumber").processPlaceholder("value", context.getArg(0)).sendPrefixedMessage(player);
			return ReturnType.FAIL;
		}

		// Check for block items
		if (!AuctionAPI.getInstance().meetsListingRequirements(player, originalItem)) return ReturnType.FAIL;

		// check if at limit
		if (auctionPlayer.isAtItemLimit(player)) {
			AuctionHouse.getInstance().getLocale().getMessage("general.requestlimit").sendPrefixedMessage(player);
			return ReturnType.FAIL;
		}

		// get the max allowed time for this player.
		final int allowedTime = auctionPlayer.getAllowedSellTime(AuctionSaleType.WITHOUT_BIDDING_SYSTEM);

		// Check list delay
		if (!auctionPlayer.canListItem()) {
			return ReturnType.FAIL;
		}

		// check min/max prices
		final double price = Double.parseDouble(context.getArg(0));

		if (price < Settings.MIN_REQUEST_PRICE.getDouble()) {
			AuctionHouse.getInstance().getLocale().getMessage("pricing.request.min price").processPlaceholder("price", Settings.MIN_REQUEST_PRICE.getDouble()).sendPrefixedMessage(player);
			return ReturnType.FAIL;
		}

		if (price > Settings.MAX_REQUEST_PRICE.getDouble()) {
			AuctionHouse.getInstance().getLocale().getMessage("pricing.request.max price").processPlaceholder("price", Settings.MAX_REQUEST_PRICE.getDouble()).sendPrefixedMessage(player);
			return ReturnType.FAIL;
		}

		AuctionedItem auctionedItem = AuctionedItem.createRequest(player, originalItem, originalItem.getAmount(), price, allowedTime);

		AuctionHouse.getAuctionPlayerManager().addToSellProcess(player);
		if (auctionPlayer.getPlayer() == null || !auctionPlayer.getPlayer().isOnline()) {
			return ReturnType.FAIL;
		}

		AuctionCreator.create(auctionPlayer, auctionedItem, (auction, listingResult) -> {
			AuctionHouse.getAuctionPlayerManager().processSell(player);

			if (Settings.OPEN_MAIN_AUCTION_HOUSE_AFTER_MENU_LIST.getBoolean()) {
				player.removeMetadata("AuctionHouseConfirmListing", AuctionHouse.getInstance());
				AuctionHouse.getGuiManager().showGUI(player, new GUIAuctionHouse(auctionPlayer));
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
	protected List<String> tab(CommandSender sender, String... args) {
		return tab(new CommandContext(sender, args, getSubCommands().isEmpty() ? "" : getSubCommands().get(0)));
	}

	@Override
	protected List<String> tab(CommandContext context) {
		return null;
	}
}
