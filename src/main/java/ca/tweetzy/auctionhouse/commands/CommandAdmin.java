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
import ca.tweetzy.auctionhouse.ahv3.api.ListingType;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.auction.AuctionPayment;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.auction.AuctionedItem;
import ca.tweetzy.auctionhouse.auction.enums.PaymentReason;
import ca.tweetzy.auctionhouse.guis.GUIAuctionHouse;
import ca.tweetzy.auctionhouse.guis.admin.GUIAdminExpired;
import ca.tweetzy.auctionhouse.guis.admin.GUIAdminLogs;
import ca.tweetzy.auctionhouse.guis.sell.GUISellListingType;
import ca.tweetzy.auctionhouse.guis.sell.GUISellPlaceItem;
import ca.tweetzy.auctionhouse.helpers.PlayerHelper;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.commands.AbstractCommand;
import ca.tweetzy.core.hooks.EconomyManager;
import ca.tweetzy.core.utils.PlayerUtils;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.flight.comp.enums.CompMaterial;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: May 23 2021
 * Time Created: 12:14 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class CommandAdmin extends AbstractCommand {

	public CommandAdmin() {
		super(CommandType.CONSOLE_OK, "admin");
	}

	@Override
	protected ReturnType runCommand(CommandSender sender, String... args) {
		if (args.length < 1) return ReturnType.FAILURE;
		if (AuctionAPI.tellMigrationStatus(sender)) return ReturnType.FAILURE;

		final AuctionHouse instance = AuctionHouse.getInstance();
		switch (args[0].toLowerCase()) {
			case "logs":
				if (!(sender instanceof Player)) break;
				Player player = (Player) sender;
				if (!player.hasPermission("auctionhouse.cmd.admin.logs")) return ReturnType.FAILURE;

				instance.getDataManager().getAdminLogs((error, logs) -> {
					if (error == null)
						AuctionHouse.newChain().sync(() -> instance.getGuiManager().showGUI(player, new GUIAdminLogs(player, logs))).execute();
					else
						error.printStackTrace();
				});
				break;
			case "viewexpired":
				if (!(sender instanceof Player)) break;
				player = (Player) sender;
				if (!player.hasPermission("auctionhouse.cmd.admin.viewexpired")) return ReturnType.FAILURE;


				if (args.length < 2) return ReturnType.FAILURE;
				OfflinePlayer target = Bukkit.getPlayerExact(args[1]);

				if (target == null) {
					for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
						if (offlinePlayer.getName() != null && offlinePlayer.getName().equalsIgnoreCase(args[1])) {
							target = offlinePlayer;
						}
					}
				}

				if (target == null) {
					instance.getLocale().getMessage("general.playernotfound").processPlaceholder("player", args[1]).sendPrefixedMessage(sender);
					return ReturnType.FAILURE;
				}

				instance.getGuiManager().showGUI(player, new GUIAdminExpired(player, target));

				break;
			case "endall":
				if (!sender.hasPermission("auctionhouse.cmd.admin.endall")) return ReturnType.FAILURE;
				for (UUID id : instance.getAuctionItemManager().getItems().keySet()) {
					instance.getAuctionItemManager().getItems().get(id).setExpired(true);
				}
				instance.getLocale().getMessage("general.endedallauctions").sendPrefixedMessage(sender);
				break;
			case "relistall":
				if (!sender.hasPermission("auctionhouse.cmd.admin.relistall")) return ReturnType.FAILURE;
				for (UUID id : instance.getAuctionItemManager().getItems().keySet()) {
					if (instance.getAuctionItemManager().getItems().get(id).isExpired()) {
						int relistTime = args.length == 1 ? instance.getAuctionItemManager().getItems().get(id).isBidItem() ? Settings.DEFAULT_AUCTION_LISTING_TIME.getInt() : Settings.DEFAULT_BIN_LISTING_TIME.getInt() : Integer.parseInt(args[1]);

						instance.getAuctionItemManager().getItems().get(id).setExpiresAt(System.currentTimeMillis() + 1000L * relistTime);
						instance.getAuctionItemManager().getItems().get(id).setExpired(false);
					}
				}
				instance.getLocale().getMessage("general.relisteditems").sendPrefixedMessage(sender);
				break;
			case "clearall":
				if (!sender.hasPermission("auctionhouse.cmd.admin.clearall")) return ReturnType.FAILURE;
				// Don't tell ppl that this exists
				instance.getAuctionItemManager().getItems().clear();
				break;
			case "clear":
				if (args.length < 4) return ReturnType.FAILURE;
				if (!sender.hasPermission("auctionhouse.cmd.admin.clear")) return ReturnType.FAILURE;

				player = PlayerUtils.findPlayer(args[1]);
				if (player == null) return ReturnType.FAILURE;

				final boolean returnItems  = Boolean.parseBoolean(args[2]);
				final boolean returnMoney  = Boolean.parseBoolean(args[3]);

				handleUserClear(player, returnMoney, returnItems);

				break;
			case "opensell":
				if (args.length < 2) return ReturnType.FAILURE;
				if (!sender.hasPermission("auctionhouse.cmd.admin.opensell")) return ReturnType.FAILURE;

				player = PlayerUtils.findPlayer(args[1]);
				if (player == null) return ReturnType.FAILURE;

				ItemStack itemToSell = PlayerHelper.getHeldItem(player).clone();

				if (itemToSell.getType() == CompMaterial.AIR.parseMaterial() && Settings.SELL_MENU_REQUIRES_USER_TO_HOLD_ITEM.getBoolean()) {
					instance.getLocale().getMessage("general.air").sendPrefixedMessage(player);
					return ReturnType.FAILURE;
				} else {
					final AuctionPlayer auctionPlayer = instance.getAuctionPlayerManager().getPlayer(player.getUniqueId());

					if (Settings.SELL_MENU_SKIPS_TYPE_SELECTION.getBoolean()) {
						if (Settings.FORCE_AUCTION_USAGE.getBoolean()) {
							AuctionHouse.getInstance().getGuiManager().showGUI(player, new GUISellPlaceItem(auctionPlayer, GUISellPlaceItem.ViewMode.SINGLE_ITEM, ListingType.AUCTION));
							return ReturnType.SUCCESS;
						}

						if (!Settings.ALLOW_USAGE_OF_BID_SYSTEM.getBoolean()) {
							AuctionHouse.getInstance().getGuiManager().showGUI(player, new GUISellPlaceItem(auctionPlayer, GUISellPlaceItem.ViewMode.SINGLE_ITEM, ListingType.BIN));
							return ReturnType.SUCCESS;
						}

						AuctionHouse.getInstance().getGuiManager().showGUI(player, new GUISellListingType(auctionPlayer, selected -> {
							AuctionHouse.getInstance().getGuiManager().showGUI(player, new GUISellPlaceItem(auctionPlayer, GUISellPlaceItem.ViewMode.SINGLE_ITEM, selected));
						}));

					} else {
						instance.getGuiManager().showGUI(player, new GUISellListingType(auctionPlayer, selected -> {
							instance.getGuiManager().showGUI(player, new GUISellPlaceItem(auctionPlayer, GUISellPlaceItem.ViewMode.SINGLE_ITEM, selected));
						}));
					}
				}
				break;
			case "open":
				if (args.length < 2) return ReturnType.FAILURE;
				if (!sender.hasPermission("auctionhouse.cmd.admin.open")) return ReturnType.FAILURE;

				player = PlayerUtils.findPlayer(args[1]);
				if (player == null) return ReturnType.FAILURE;

				if (CommandMiddleware.handle(player) == ReturnType.FAILURE) return ReturnType.FAILURE;

				if (instance.getAuctionPlayerManager().getPlayer(player.getUniqueId()) == null) {
					instance.getLocale().newMessage(TextUtils.formatText("&cCould not find auction player instance for&f: &e" + player.getName() + "&c creating one now.")).sendPrefixedMessage(Bukkit.getConsoleSender());
					instance.getAuctionPlayerManager().addPlayer(new AuctionPlayer(player));
				}

				instance.getGuiManager().showGUI(player, new GUIAuctionHouse(instance.getAuctionPlayerManager().getPlayer(player.getUniqueId())));
				break;
		}

		return ReturnType.SUCCESS;
	}

	@Override
	protected List<String> onTab(CommandSender sender, String... args) {
		if (args.length == 1) return Arrays.asList("endall", "relistall", "logs", "viewexpired", "open", "clear");
		if (args.length == 2 && args[0].equalsIgnoreCase("relistAll")) return Arrays.asList("1", "2", "3", "4", "5");
		if (args.length == 2 && (args[0].equalsIgnoreCase("viewexpired") || args[0].equalsIgnoreCase("open")))
			return Bukkit.getOnlinePlayers().stream().map(OfflinePlayer::getName).collect(Collectors.toList());
		return null;
	}

	@Override
	public String getPermissionNode() {
		return "auctionhouse.cmd.admin";
	}

	@Override
	public String getSyntax() {
		return "admin <endall|relistAll> [value]";
	}

	@Override
	public String getDescription() {
		return "Admin options for auction house.";
	}

	private void handleUserClear(final Player player, final boolean returnBids, final boolean giveItemsBack) {
		final AuctionPlayer auctionPlayer = AuctionHouse.getInstance().getAuctionPlayerManager().getPlayer(player.getUniqueId());
		final List<AuctionedItem> items = auctionPlayer.getItems(false);

		for (AuctionedItem auctionItem : items) {
			if (returnBids) {
				if (Settings.BIDDING_TAKES_MONEY.getBoolean() && !auctionItem.getHighestBidder().equals(auctionItem.getOwner())) {
					final OfflinePlayer oldBidder = Bukkit.getOfflinePlayer(auctionItem.getHighestBidder());

					if (Settings.STORE_PAYMENTS_FOR_MANUAL_COLLECTION.getBoolean())
						AuctionHouse.getInstance().getDataManager().insertAuctionPayment(new AuctionPayment(
								oldBidder.getUniqueId(),
								auctionItem.getCurrentPrice(),
								auctionItem.getItem(),
								player.getName(),
								PaymentReason.ADMIN_REMOVED
						), null);
					else
						EconomyManager.deposit(oldBidder, auctionItem.getCurrentPrice());

					if (oldBidder.isOnline())
						AuctionHouse.getInstance().getLocale().getMessage("pricing.moneyadd").processPlaceholder("player_balance", AuctionAPI.getInstance().formatNumber(EconomyManager.getBalance(oldBidder))).processPlaceholder("price", AuctionAPI.getInstance().formatNumber(auctionItem.getCurrentPrice())).sendPrefixedMessage(oldBidder.getPlayer());

				}
			}

			if (giveItemsBack) {
				auctionItem.setExpiresAt(System.currentTimeMillis());
				auctionItem.setExpired(true);
			} else {
				AuctionHouse.getInstance().getAuctionItemManager().sendToGarbage(auctionItem);
			}
		}
	}
}
