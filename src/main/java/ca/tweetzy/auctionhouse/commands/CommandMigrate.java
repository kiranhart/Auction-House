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
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.flight.command.AllowedExecutor;
import ca.tweetzy.flight.command.Command;
import ca.tweetzy.flight.command.ReturnType;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

/**
 * The current file has been created by Kiran Hart
 * Date Created: August 10 2021
 * Time Created: 12:25 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class CommandMigrate extends Command {

	public CommandMigrate() {
		super(AllowedExecutor.BOTH, "migrate");
	}

	@Override
	protected ReturnType execute(CommandSender sender, String... args) {
		AuctionHouse.getInstance().getLocale().newMessage(TextUtils.formatText("&cMigration support for v1 has been dropped since 2.53.0, use 2.52.0 or lower to migrate first.")).sendPrefixedMessage(sender);
		return ReturnType.SUCCESS;
	}

	@Override
	protected List<String> tab(CommandSender sender, String... args) {
		if (args.length == 1) return Collections.singletonList("confirm");
		return null;
	}

	@Override
	public String getPermissionNode() {
		return "auctionhouse.cmd.migrate";
	}

	@Override
	public String getSyntax() {
		return "migrate <confirm>";
	}

	@Override
	public String getDescription() {
		return "Migrate from old data format to new format";
	}
}
