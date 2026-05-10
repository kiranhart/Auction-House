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


import ca.tweetzy.auctionhouse.lang.AuctionLocale;
import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.auction.AuctionedItem;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.flight.command.AllowedExecutor;
import ca.tweetzy.flight.command.Command;
import ca.tweetzy.flight.command.CommandContext;
import ca.tweetzy.flight.command.ReturnType;
import ca.tweetzy.flight.utils.messages.Titles;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * The current file has been created by Kiran Hart
 * Date Created: March 15 2021
 * Time Created: 4:32 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class CommandConfirm extends Command {

	public CommandConfirm() {
		super(AllowedExecutor.PLAYER, Settings.CMD_ALIAS_SUB_CONFIRM.getStringList().toArray(new String[0]));
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

		if (AuctionHouse.getAuctionPlayerManager().getPlayer(player.getUniqueId()) == null) {
			Common.tell(Bukkit.getConsoleSender(), Common.colorize("&cCould not find auction player instance for&f: &e" + player.getName() + "&c creating one now."));
			AuctionHouse.getAuctionPlayerManager().addPlayer(new AuctionPlayer(player));
		}

		final AuctionPlayer auctionPlayer = AuctionHouse.getAuctionPlayerManager().getPlayer(player.getUniqueId());

		if (auctionPlayer.getEndAllRequestTime() == -1) {
			AuctionLocale.tell(player, "general.nothing to confirm");
			return ReturnType.SUCCESS;
		}

		if (System.currentTimeMillis() > auctionPlayer.getEndAllRequestTime()) {
			AuctionLocale.tell(player, "general.confirm time limit reached");
			return ReturnType.SUCCESS;
		}

		if (System.currentTimeMillis() <= auctionPlayer.getEndAllRequestTime()) {
			AuctionLocale.tell(player, "general.confirmed cancellation");
			auctionPlayer.setEndAllRequestTime(-1);
			Titles.clearTitle(player);

			for (AuctionedItem item : auctionPlayer.getItems(false)) {
				if (Settings.SELLERS_MUST_WAIT_FOR_TIME_LIMIT_AFTER_BID.getBoolean() && item.containsValidBid())
					continue;

				if (item.isRequest()) {
					AuctionHouse.getAuctionItemManager().sendToGarbage(item);
				} else {
					item.setExpired(true);
				}
			}
		}

		return ReturnType.SUCCESS;
	}

	@Override
	public String getPermissionNode() {
		return "auctionhouse.cmd.confirm";
	}

	@Override
	public String getSyntax() {
		return AuctionLocale.msg(null, "commands.syntax.confirm");
	}

	@Override
	public String getDescription() {
		return AuctionLocale.msg(null, "commands.description.confirm");
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
