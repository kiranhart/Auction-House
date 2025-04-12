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
import ca.tweetzy.auctionhouse.auction.AuctionPayment;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.auction.AuctionedItem;
import ca.tweetzy.auctionhouse.auction.ListingType;
import ca.tweetzy.auctionhouse.auction.enums.PaymentReason;
import ca.tweetzy.auctionhouse.guis.admin.GUIAdminExpired;
import ca.tweetzy.auctionhouse.guis.admin.GUIAdminLogs;
import ca.tweetzy.auctionhouse.guis.admin.bans.GUIBans;
import ca.tweetzy.auctionhouse.guis.core.GUIAuctionHouse;
import ca.tweetzy.auctionhouse.guis.sell.GUISellListingType;
import ca.tweetzy.auctionhouse.guis.sell.GUISellPlaceItem;
import ca.tweetzy.auctionhouse.helpers.PlayerHelper;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.core.utils.PlayerUtils;
import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.flight.command.AllowedExecutor;
import ca.tweetzy.flight.command.Command;
import ca.tweetzy.flight.command.ReturnType;
import ca.tweetzy.flight.utils.Common;
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
public class CommandAdmin extends Command {

	public CommandAdmin() {
		super(AllowedExecutor.BOTH, Settings.CMD_ALIAS_SUB_ADMIN.getStringList().toArray(new String[0]));
	}

	@Override
	protected ReturnType execute(CommandSender sender, String... args) {
		if (args.length < 1) return ReturnType.FAIL;
		if (AuctionAPI.tellMigrationStatus(sender)) return ReturnType.FAIL;

		switch (args[0].toLowerCase()) {
			case "logs":
				if (!(sender instanceof Player)) break;
				Player player = (Player) sender;
				if (!player.hasPermission("auctionhouse.cmd.admin.logs")) return ReturnType.FAIL;

				AuctionHouse.getDataManager().getAdminLogs((error, logs) -> {
					if (error == null)
						AuctionHouse.newChain().sync(() -> AuctionHouse.getGuiManager().showGUI(player, new GUIAdminLogs(player, logs))).execute();
					else
						error.printStackTrace();
				});
				break;
			case "viewexpired":
				if (!(sender instanceof Player)) break;
				player = (Player) sender;
				if (!player.hasPermission("auctionhouse.cmd.admin.viewexpired")) return ReturnType.FAIL;


				if (args.length < 2) return ReturnType.FAIL;
				OfflinePlayer target = Bukkit.getPlayerExact(args[1]);

				if (target == null) {
					for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
						if (offlinePlayer.getName() != null && offlinePlayer.getName().equalsIgnoreCase(args[1])) {
							target = offlinePlayer;
						}
					}
				}

				if (target == null) {
					AuctionHouse.getInstance().getLocale().getMessage("general.playernotfound").processPlaceholder("player", args[1]).sendPrefixedMessage(sender);
					return ReturnType.FAIL;
				}

				AuctionHouse.getGuiManager().showGUI(player, new GUIAdminExpired(player, target));

				break;
			case "endall":
				if (!sender.hasPermission("auctionhouse.cmd.admin.endall")) return ReturnType.FAIL;
				for (UUID id : AuctionHouse.getAuctionItemManager().getItems().keySet()) {
					AuctionHouse.getAuctionItemManager().getItems().get(id).setExpired(true);
				}
				AuctionHouse.getInstance().getLocale().getMessage("general.endedallauctions").sendPrefixedMessage(sender);
				break;
			case "bans":
				if (!(sender instanceof Player)) break;
				player = (Player) sender;
				if (!player.hasPermission("auctionhouse.cmd.admin.bans")) return ReturnType.FAIL;
				AuctionHouse.getGuiManager().showGUI(player, new GUIBans(player));
				break;
			case "relistall":
				if (!sender.hasPermission("auctionhouse.cmd.admin.relistall")) return ReturnType.FAIL;
				for (UUID id : AuctionHouse.getAuctionItemManager().getItems().keySet()) {
					if (AuctionHouse.getAuctionItemManager().getItems().get(id).isExpired()) {
						int relistTime = args.length == 1 ? AuctionHouse.getAuctionItemManager().getItems().get(id).isBidItem() ? Settings.DEFAULT_AUCTION_LISTING_TIME.getInt() : Settings.DEFAULT_BIN_LISTING_TIME.getInt() : Integer.parseInt(args[1]);

						AuctionHouse.getAuctionItemManager().getItems().get(id).setExpiresAt(System.currentTimeMillis() + 1000L * relistTime);
						AuctionHouse.getAuctionItemManager().getItems().get(id).setExpired(false);
					}
				}
				AuctionHouse.getInstance().getLocale().getMessage("general.relisteditems").sendPrefixedMessage(sender);
				break;
			case "clearall":
				if (!sender.hasPermission("auctionhouse.cmd.admin.clearall")) return ReturnType.FAIL;
				// Don't tell ppl that this exists
				AuctionHouse.getAuctionItemManager().getItems().clear();
				break;
			case "clear":
				if (args.length < 4) return ReturnType.FAIL;
				if (!sender.hasPermission("auctionhouse.cmd.admin.clear")) return ReturnType.FAIL;

				player = PlayerUtils.findPlayer(args[1]);
				if (player == null) return ReturnType.FAIL;

				final boolean returnItems = Boolean.parseBoolean(args[2]);
				boolean returnMoney = Boolean.parseBoolean(args[3]);

				handleUserClear(player, returnMoney, returnItems);

				break;
			case "clearbids":
				if (args.length < 3) return ReturnType.FAIL;
				if (!sender.hasPermission("auctionhouse.cmd.admin.clearbids")) return ReturnType.FAIL;

				player = PlayerUtils.findPlayer(args[1]);
				if (player == null) return ReturnType.FAIL;

				returnMoney = Boolean.parseBoolean(args[2]);

				handleUserBidClear(player, returnMoney);
				AuctionHouse.getInstance().getLocale().getMessage("general.admin.cleared bids").processPlaceholder("player", args[1]).sendPrefixedMessage(sender);
				break;
			case "opensell":
				if (args.length < 2) return ReturnType.FAIL;
				if (!sender.hasPermission("auctionhouse.cmd.admin.opensell")) return ReturnType.FAIL;

				player = PlayerUtils.findPlayer(args[1]);
				if (player == null) return ReturnType.FAIL;
				if (AuctionHouse.getInstance().getBanManager().isStillBanned(player, BanType.EVERYTHING, BanType.SELL)) return ReturnType.FAIL;

				ItemStack itemToSell = PlayerHelper.getHeldItem(player).clone();

				if (itemToSell.getType() == CompMaterial.AIR.get() && Settings.SELL_MENU_REQUIRES_USER_TO_HOLD_ITEM.getBoolean()) {
					AuctionHouse.getInstance().getLocale().getMessage("general.air").sendPrefixedMessage(player);
					return ReturnType.FAIL;
				} else {
					final AuctionPlayer auctionPlayer = AuctionHouse.getAuctionPlayerManager().getPlayer(player.getUniqueId());

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
				break;
			case "open":
				if (args.length < 2) return ReturnType.FAIL;
				if (!sender.hasPermission("auctionhouse.cmd.admin.open")) return ReturnType.FAIL;

				player = PlayerUtils.findPlayer(args[1]);
				if (player == null) return ReturnType.FAIL;

				if (CommandMiddleware.handle(player) == ReturnType.FAIL) return ReturnType.FAIL;

				if (AuctionHouse.getAuctionPlayerManager().getPlayer(player.getUniqueId()) == null) {
					AuctionHouse.getInstance().getLocale().newMessage(Common.colorize("&cCould not find auction player instance for&f: &e" + player.getName() + "&c creating one now.")).sendPrefixedMessage(Bukkit.getConsoleSender());
					AuctionHouse.getAuctionPlayerManager().addPlayer(new AuctionPlayer(player));
				}

				AuctionHouse.getGuiManager().showGUI(player, new GUIAuctionHouse(AuctionHouse.getAuctionPlayerManager().getPlayer(player.getUniqueId())));
				break;
		}

		return ReturnType.SUCCESS;
	}

	@Override
	protected List<String> tab(CommandSender sender, String... args) {
		if (args.length == 1) return Arrays.asList("endall", "relistall", "logs", "viewexpired", "open", "clear", "clearbids");
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

	private void handleUserBidClear(final Player player, final boolean returnMoney) {
		final List<AuctionedItem> items = AuctionHouse.getAuctionItemManager().getHighestBidItems(player);

		for (AuctionedItem auctionedItem : items) {
			auctionedItem.setHighestBidder(auctionedItem.getOwner());
			auctionedItem.setHighestBidderName(auctionedItem.getOwnerName());

			if (returnMoney && Settings.BIDDING_TAKES_MONEY.getBoolean())
				if (Settings.STORE_PAYMENTS_FOR_MANUAL_COLLECTION.getBoolean())
					AuctionHouse.getDataManager().insertAuctionPayment(new AuctionPayment(
							player.getUniqueId(),
							auctionedItem.getCurrentPrice(),
							auctionedItem.getItem(),
							player.getName(),
							PaymentReason.BID_RETURNED,
							auctionedItem.getCurrency(),
							auctionedItem.getCurrencyItem()
					), null);
				else
					AuctionHouse.getCurrencyManager().deposit(player, auctionedItem.getCurrentPrice(), auctionedItem.getCurrency(), auctionedItem.getCurrencyItem());
		}
	}

	private void handleUserClear(final Player player, final boolean returnBids, final boolean giveItemsBack) {
		final AuctionPlayer auctionPlayer = AuctionHouse.getAuctionPlayerManager().getPlayer(player.getUniqueId());
		final List<AuctionedItem> items = auctionPlayer.getAllItems();

		for (AuctionedItem auctionItem : items) {
			if (auctionItem.isExpired()) {
				if (!giveItemsBack)
					AuctionHouse.getAuctionItemManager().sendToGarbage(auctionItem);
				continue;
			}

			if (returnBids) {
				if (Settings.BIDDING_TAKES_MONEY.getBoolean() && !auctionItem.getHighestBidder().equals(auctionItem.getOwner())) {
					final OfflinePlayer oldBidder = Bukkit.getOfflinePlayer(auctionItem.getHighestBidder());

					if (Settings.STORE_PAYMENTS_FOR_MANUAL_COLLECTION.getBoolean())
						AuctionHouse.getDataManager().insertAuctionPayment(new AuctionPayment(
								oldBidder.getUniqueId(),
								auctionItem.getCurrentPrice(),
								auctionItem.getItem(),
								player.getName(),
								PaymentReason.ADMIN_REMOVED,
								auctionItem.getCurrency(),
								auctionItem.getCurrencyItem()
						), null);
					else
						AuctionHouse.getCurrencyManager().deposit(oldBidder, auctionItem.getCurrentPrice(), auctionItem.getCurrency(), auctionItem.getCurrencyItem());

					if (oldBidder.isOnline())
						AuctionHouse.getInstance().getLocale().getMessage("pricing.moneyadd")
								.processPlaceholder("player_balance", AuctionHouse.getAPI().getFinalizedCurrencyNumber(AuctionHouse.getCurrencyManager().getBalance(oldBidder, auctionItem.getCurrency().split("/")[0], auctionItem.getCurrency().split("/")[1]), auctionItem.getCurrency(), auctionItem.getCurrencyItem()))
								.processPlaceholder("price", auctionItem.getFormattedCurrentPrice())
								.sendPrefixedMessage(oldBidder.getPlayer());

				}
			}

			if (giveItemsBack) {
				auctionItem.setExpiresAt(System.currentTimeMillis());
				auctionItem.setExpired(true);
			} else {
				AuctionHouse.getAuctionItemManager().sendToGarbage(auctionItem);
			}
		}
	}
}
