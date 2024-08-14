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
import ca.tweetzy.auctionhouse.api.ban.Ban;
import ca.tweetzy.auctionhouse.api.sync.SynchronizeResult;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.utils.PlayerUtils;
import ca.tweetzy.flight.command.AllowedExecutor;
import ca.tweetzy.flight.command.Command;
import ca.tweetzy.flight.command.ReturnType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

/**
 * The current file has been created by Kiran Hart
 * Date Created: July 21 2021
 * Time Created: 4:11 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class CommandUnban extends Command {

	public CommandUnban() {
		super(AllowedExecutor.BOTH, Settings.CMD_ALIAS_SUB_UNBAN.getStringList().toArray(new String[0]));
	}

	@Override
	protected ReturnType execute(CommandSender sender, String... args) {
		if (args.length != 1) return ReturnType.INVALID_SYNTAX;
		if (AuctionAPI.tellMigrationStatus(sender)) return ReturnType.FAIL;

		final Player target = PlayerUtils.findPlayer(args[0]);
		OfflinePlayer offlinePlayer = null;

		if (target == null) {
			offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
			if (offlinePlayer == null || !offlinePlayer.hasPlayedBefore()) {
				AuctionHouse.getInstance().getLocale().getMessage("general.playernotfound").processPlaceholder("player", args[0]).sendPrefixedMessage(sender);
				return ReturnType.FAIL;
			}
		}

		UUID toUnBan = target == null ? offlinePlayer.getUniqueId() : target.getUniqueId();

		if (!AuctionHouse.getBanManager().getManagerContent().containsKey(toUnBan)) {
			AuctionHouse.getInstance().getLocale().getMessage("ban.user not banned").processPlaceholder("player_name", args[0]).sendPrefixedMessage(sender);
			return ReturnType.FAIL;
		}

		final Ban ban = AuctionHouse.getBanManager().get(toUnBan);
		ban.unStore(result -> {
			if (result == SynchronizeResult.SUCCESS) {
				AuctionHouse.getInstance().getLocale().getMessage("ban.user unbanned").processPlaceholder("player_name", args[0]).sendPrefixedMessage(sender);
			}
		});

		return ReturnType.SUCCESS;
	}

	@Override
	protected List<String> tab(CommandSender sender, String... args) {
//		if (args.length == 1)
//			return AuctionHouse.getInstance().getAuctionBanManager().getBans().values().stream().map(ban -> Bukkit.getOfflinePlayer(ban.getBannedPlayer()).getName()).collect(Collectors.toList());
		return null;
	}

	@Override
	public String getPermissionNode() {
		return "auctionhouse.cmd.unban";
	}

	@Override
	public String getSyntax() {
		return AuctionHouse.getInstance().getLocale().getMessage("commands.syntax.unban").getMessage();
	}

	@Override
	public String getDescription() {
		return AuctionHouse.getInstance().getLocale().getMessage("commands.description.unban").getMessage();
	}
}
