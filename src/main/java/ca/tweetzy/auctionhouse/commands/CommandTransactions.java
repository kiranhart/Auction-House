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
import ca.tweetzy.auctionhouse.guis.transaction.GUITransactionList;
import ca.tweetzy.auctionhouse.guis.transaction.GUITransactionType;
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
 * Date Created: March 23 2021
 * Time Created: 9:29 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class CommandTransactions extends Command {

	public CommandTransactions() {
		super(AllowedExecutor.PLAYER, Settings.CMD_ALIAS_SUB_TRANSACTIONS.getStringList().toArray(new String[0]));
	}

	@Override
	protected ReturnType execute(CommandSender sender, String... args) {
		final Player player = (Player) sender;

		if (CommandMiddleware.handle(player) == ReturnType.FAIL) return ReturnType.FAIL;

		if (args.length == 0) {

			if (Settings.RESTRICT_ALL_TRANSACTIONS_TO_PERM.getBoolean() && !player.hasPermission("auctionhouse.transactions.viewall")) {
				AuctionHouse.getGuiManager().showGUI(player, new GUITransactionList(player, false));
			} else {
				AuctionHouse.getGuiManager().showGUI(player, new GUITransactionType(player));
			}

			return ReturnType.SUCCESS;
		}

		if (args.length == 2 && args[0].equalsIgnoreCase("search")) {
			final Player target = PlayerUtils.findPlayer(args[1]);

			AuctionHouse.newChain().async(() -> {
				OfflinePlayer offlinePlayer = null;

				if (target == null) {
					// try and look for an offline player
					offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
					if (offlinePlayer == null || !offlinePlayer.hasPlayedBefore()) {
						AuctionHouse.getInstance().getLocale().getMessage("general.playernotfound").processPlaceholder("player", args[1]).sendPrefixedMessage(player);
						return;
					}
				}

				UUID toLookup = target == null ? offlinePlayer.getUniqueId() : target.getUniqueId();
				AuctionHouse.getGuiManager().showGUI(player, new GUITransactionList(player, toLookup));

			}).execute();

		}

		return ReturnType.SUCCESS;
	}

	@Override
	public String getPermissionNode() {
		return "auctionhouse.cmd.transactions";
	}

	@Override
	public String getSyntax() {
		return AuctionHouse.getInstance().getLocale().getMessage("commands.syntax.transactions").getMessage();
	}

	@Override
	public String getDescription() {
		return AuctionHouse.getInstance().getLocale().getMessage("commands.description.transactions").getMessage();
	}

	@Override
	protected List<String> tab(CommandSender sender, String... args) {
		return null;
	}
}
