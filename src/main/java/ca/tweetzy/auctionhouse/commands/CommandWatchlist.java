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
import ca.tweetzy.auctionhouse.guis.core.GUIWatchedListings;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.flight.command.AllowedExecutor;
import ca.tweetzy.flight.command.Command;
import ca.tweetzy.flight.command.CommandContext;
import ca.tweetzy.flight.command.ReturnType;
import ca.tweetzy.flight.utils.Common;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandWatchlist extends Command {

	public CommandWatchlist() {
		super(AllowedExecutor.PLAYER, Settings.CMD_ALIAS_SUB_WATCHLIST.getStringList().toArray(new String[0]));
	}

	@Override
	protected ReturnType execute(CommandSender sender, String... args) {
		return execute(new CommandContext(sender, args, getSubCommands().isEmpty() ? "" : getSubCommands().get(0)));
	}

	@Override
	protected ReturnType execute(CommandContext context) {
		final Player player = context.getPlayer();

		if (!Settings.WATCHLIST_ENABLED.getBoolean()) {
			return ReturnType.FAIL;
		}

		if (CommandMiddleware.handleAccessHours(player) == ReturnType.FAIL) return ReturnType.FAIL;
		if (CommandMiddleware.handle(player) == ReturnType.FAIL) return ReturnType.FAIL;

		AuctionPlayer auctionPlayer = AuctionHouse.getAuctionPlayerManager().getPlayer(player.getUniqueId());
		if (auctionPlayer == null) {
			Common.tell(Bukkit.getConsoleSender(), Common.colorize("&cCould not find auction player instance for&f: &e" + player.getName() + "&c creating one now."));
			AuctionHouse.getAuctionPlayerManager().addPlayer(new AuctionPlayer(player));
			auctionPlayer = AuctionHouse.getAuctionPlayerManager().getPlayer(player.getUniqueId());
		}

		AuctionHouse.getGuiManager().showGUI(player, new GUIWatchedListings(auctionPlayer));
		return ReturnType.SUCCESS;
	}

	@Override
	public String getPermissionNode() {
		return "auctionhouse.cmd.watchlist";
	}

	@Override
	public String getSyntax() {
		return AuctionLocale.msg(null, "commands.syntax.watchlist");
	}

	@Override
	public String getDescription() {
		return AuctionLocale.msg(null, "commands.description.watchlist");
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
