/*
 * Auction House
 * Copyright 2022 Kiran Hart
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

package ca.tweetzy.auctionhouse.ahv3.commands;

import ca.tweetzy.flight.command.AllowedExecutor;
import ca.tweetzy.flight.command.Command;
import ca.tweetzy.flight.command.ReturnType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public final class AuctionHouseCommand extends Command {

	public AuctionHouseCommand() {
		super(AllowedExecutor.BOTH, "ahdevv3");
	}

	@Override
	protected ReturnType execute(CommandSender sender, String... args) {
		if (!(sender instanceof Player)) return ReturnType.FAIL;

		final Player player = (Player) sender;

		// just to prevent ppl from using it during development
		if (!player.getUniqueId().equals(UUID.fromString("c5351027-b0a0-48db-b817-f3b9a3469bfc"))) return ReturnType.FAIL;



		return ReturnType.SUCCESS;
	}

	@Override
	protected List<String> tab(CommandSender sender, String... args) {
		return null;
	}

	@Override
	public String getPermissionNode() {
		return "auctionhouse.v3.command";
	}

	@Override
	public String getSyntax() {
		return null;
	}

	@Override
	public String getDescription() {
		return null;
	}
}
