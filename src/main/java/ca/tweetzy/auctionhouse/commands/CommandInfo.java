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
import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.flight.command.AllowedExecutor;
import ca.tweetzy.flight.command.Command;
import ca.tweetzy.flight.command.CommandContext;
import ca.tweetzy.flight.command.ReturnType;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * The current file has been created by Kiran Hart
 * Date Created: January 12 2021
 * Time Created: 6:40 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class CommandInfo extends Command {

	public CommandInfo() {
		super(AllowedExecutor.BOTH, "info");
	}

	@Override
	protected ReturnType execute(CommandSender sender, String... args) {
		return execute(new CommandContext(sender, args, getSubCommands().isEmpty() ? "" : getSubCommands().get(0)));
	}

	@Override
	protected ReturnType execute(CommandContext context) {
		final AuctionHouse instance = AuctionHouse.getInstance();
		final String version = instance.getDescription().getVersion();
		final String author = "Kiran Hart";
		
		instance.getLocale().newMessage(Common.colorize("&7Author: &e" + author)).sendPrefixedMessage(context.getSender());
		instance.getLocale().newMessage(Common.colorize("&7Version: &e" + version)).sendPrefixedMessage(context.getSender());
		
		return ReturnType.SUCCESS;
	}

	@Override
	protected List<String> tab(CommandSender sender, String... args) {
		return tab(new CommandContext(sender, args, getSubCommands().isEmpty() ? "" : getSubCommands().get(0)));
	}

	@Override
	protected List<String> tab(CommandContext context) {
		return null;
	}

	@Override
	public String getPermissionNode() {
		return null;
	}

	@Override
	public String getSyntax() {
		return "/ah info";
	}

	@Override
	public String getDescription() {
		return "Display plugin author and version information";
	}
}





