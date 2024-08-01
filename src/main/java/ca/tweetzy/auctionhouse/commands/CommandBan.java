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
import ca.tweetzy.auctionhouse.guis.admin.bans.GUIBanUser;
import ca.tweetzy.auctionhouse.guis.selector.GUIPlayerSelector;
import ca.tweetzy.core.commands.AbstractCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: July 21 2021
 * Time Created: 3:05 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class CommandBan extends AbstractCommand {

	public CommandBan() {
		super(CommandType.PLAYER_ONLY, "ban");
	}

	@Override
	protected ReturnType runCommand(CommandSender sender, String... args) {
		final Player player = (Player) sender;
		final AuctionHouse instance = AuctionHouse.getInstance();

		if (args.length == 0) {
			// open the player picker then redirect to the ban user menu
			instance.getGuiManager().showGUI(player, new GUIPlayerSelector(player, selected -> {
				if (instance.getBanManager().isBannedAlready(selected)) {
					instance.getLocale().getMessage("ban.user already banned").processPlaceholder("player_name", selected.getName()).sendPrefixedMessage(player);
					return;
				}

				instance.getGuiManager().showGUI(player, new GUIBanUser(player, instance.getBanManager().generateEmptyBan(player, selected)));
			}));
			return ReturnType.SUCCESS;
		}

		final Player target = Bukkit.getPlayerExact(args[0]);

		if (target == null) {
			instance.getLocale().getMessage("general.playernotfound").processPlaceholder("player", args[0]).sendPrefixedMessage(player);
			return ReturnType.FAILURE;
		}

		if (instance.getBanManager().isBannedAlready(target)) {
			instance.getLocale().getMessage("ban.user already banned").processPlaceholder("player_name", args[0]).sendPrefixedMessage(player);
			return ReturnType.FAILURE;
		}

		instance.getGuiManager().showGUI(player, new GUIBanUser(player, instance.getBanManager().generateEmptyBan(player, target)));
		return ReturnType.SUCCESS;
	}

	@Override
	protected List<String> onTab(CommandSender sender, String... args) {
		if (args.length == 1) {
			return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
		}

		return null;
	}

	@Override
	public String getPermissionNode() {
		return "auctionhouse.cmd.ban";
	}

	@Override
	public String getSyntax() {
		return AuctionHouse.getInstance().getLocale().getMessage("commands.syntax.ban").getMessage();
	}

	@Override
	public String getDescription() {
		return AuctionHouse.getInstance().getLocale().getMessage("commands.description.ban").getMessage();
	}
}
