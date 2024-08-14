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
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.flight.command.AllowedExecutor;
import ca.tweetzy.flight.command.Command;
import ca.tweetzy.flight.command.ReturnType;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

/**
 * The current file has been created by Kiran Hart
 * Date Created: August 11 2021
 * Time Created: 2:27 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class CommandToggleListInfo extends Command {

	public CommandToggleListInfo() {
		super(AllowedExecutor.PLAYER, Settings.CMD_ALIAS_SUB_TOGGLELISTINFO.getStringList().toArray(new String[0]));
	}


	@Override
	protected ReturnType execute(CommandSender sender, String... args) {
		final Player player = (Player) sender;
		final UUID playerUUID = player.getUniqueId();

		if (AuctionHouse.getAuctionPlayerManager().getPlayer(playerUUID) == null) {
			AuctionHouse.getInstance().getLocale().newMessage(TextUtils.formatText("&cCould not find auction player instance for&f: &e" + player.getName() + "&c creating one now.")).sendPrefixedMessage(Bukkit.getConsoleSender());
			AuctionHouse.getAuctionPlayerManager().addPlayer(new AuctionPlayer(player));
		}

		final AuctionPlayer auctionPlayer = AuctionHouse.getAuctionPlayerManager().getPlayer(playerUUID);
		auctionPlayer.setShowListingInfo(!auctionPlayer.isShowListingInfo());
		AuctionHouse.getInstance().getLocale().getMessage("general.toggled listing." + (auctionPlayer.isShowListingInfo() ? "on" : "off")).sendPrefixedMessage(player);

		return ReturnType.SUCCESS;
	}

	@Override
	protected List<String> tab(CommandSender sender, String... args) {
		return null;
	}

	@Override
	public String getPermissionNode() {
		return "auctionhouse.cmds.togglelistinfo";
	}

	@Override
	public String getSyntax() {
		return AuctionHouse.getInstance().getLocale().getMessage("commands.syntax.togglelistinfo").getMessage();
	}

	@Override
	public String getDescription() {
		return AuctionHouse.getInstance().getLocale().getMessage("commands.description.togglelistinfo").getMessage();
	}
}
