/*
 * Auction House
 * Copyright 2023 Kiran Hart
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
import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.flight.command.AllowedExecutor;
import ca.tweetzy.flight.command.Command;
import ca.tweetzy.flight.command.CommandContext;
import ca.tweetzy.flight.command.ReturnType;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * Command to toggle debug mode for development and research purposes.
 * Debug mode enables detailed logging for GUI transitions, task management, etc.
 */
public class CommandDebug extends Command {

	public CommandDebug() {
		super(AllowedExecutor.BOTH, "debug");
	}

	@Override
	protected ReturnType execute(CommandSender sender, String... args) {
		return execute(new CommandContext(sender, args, getSubCommands().isEmpty() ? "" : getSubCommands().get(0)));
	}

	@Override
	protected ReturnType execute(CommandContext context) {
		if (AuctionAPI.tellMigrationStatus(context.getSender())) return ReturnType.FAIL;
		
		// Toggle debug mode
		AuctionHouse.setDebugMode(!AuctionHouse.isDebugMode());
		
		final String status = AuctionHouse.isDebugMode() ? "&aenabled" : "&cdisabled";
		AuctionHouse.getInstance().getLocale().newMessage(Common.colorize("&7Debug mode has been " + status + "&7.")).sendPrefixedMessage(context.getSender());
		
		if (AuctionHouse.isDebugMode()) {
			AuctionHouse.getInstance().getLocale().newMessage(Common.colorize("&7You will now see detailed debug messages in the console.")).sendPrefixedMessage(context.getSender());
		}
		
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
		return "auctionhouse.cmds.debug";
	}

	@Override
	public String getSyntax() {
		return AuctionHouse.getInstance().getLocale().getMessage("commands.syntax.debug").getMessage();
	}

	@Override
	public String getDescription() {
		return AuctionHouse.getInstance().getLocale().getMessage("commands.description.debug").getMessage();
	}
}

