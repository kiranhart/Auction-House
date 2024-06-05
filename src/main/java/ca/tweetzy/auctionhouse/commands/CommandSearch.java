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
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.guis.core.GUIAuctionHouseV2;
import ca.tweetzy.core.commands.AbstractCommand;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.flight.utils.Common;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * The current file has been created by Kiran Hart
 * Date Created: March 23 2021
 * Time Created: 9:36 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class CommandSearch extends AbstractCommand {

	public CommandSearch() {
		super(CommandType.PLAYER_ONLY, "search");
	}

	@Override
	protected ReturnType runCommand(CommandSender sender, String... args) {
		if (args.length <= 0) return ReturnType.SYNTAX_ERROR;
		final Player player = (Player) sender;

		if (CommandMiddleware.handle(player) == ReturnType.FAILURE) return ReturnType.FAILURE;

		final StringBuilder builder = new StringBuilder();
		for (String arg : args) {
			builder.append(arg).append(" ");
		}

		final AuctionHouse instance = AuctionHouse.getInstance();
		if (instance.getAuctionPlayerManager().getPlayer(player.getUniqueId()) == null) {
			instance.getLocale().newMessage(TextUtils.formatText("&cCould not find auction player instance for&f: &e" + player.getName() + "&c creating one now.")).sendPrefixedMessage(Bukkit.getConsoleSender());
			instance.getAuctionPlayerManager().addPlayer(new AuctionPlayer(player));
		}

		instance.getGuiManager().showGUI(player, new GUIAuctionHouseV2(instance.getAuctionPlayerManager().getPlayer(player.getUniqueId()), builder.toString().trim()));
		return ReturnType.SUCCESS;
	}

	@Override
	public String getPermissionNode() {
		return "auctionhouse.cmd.search";
	}

	@Override
	public String getSyntax() {
		return AuctionHouse.getInstance().getLocale().getMessage("commands.syntax.search").getMessage();
	}

	@Override
	public String getDescription() {
		return AuctionHouse.getInstance().getLocale().getMessage("commands.description.search").getMessage();
	}

	@Override
	protected List<String> onTab(CommandSender sender, String... args) {
		return null;
	}
}
