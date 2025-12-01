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
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.command.AllowedExecutor;
import ca.tweetzy.flight.command.Command;
import ca.tweetzy.flight.command.CommandContext;
import ca.tweetzy.flight.command.ReturnType;
import ca.tweetzy.flight.comp.enums.ServerVersion;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

/**
 * The current file has been created by Kiran Hart
 * Date Created: December 04 2021
 * Time Created: 11:30 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public final class CommandMarkChest extends Command {

	public CommandMarkChest() {
		super(AllowedExecutor.PLAYER, Settings.CMD_ALIAS_SUB_MARKCHEST.getStringList().toArray(new String[0]));
	}

	@Override
	protected ReturnType execute(CommandSender sender, String... args) {
		return execute(new CommandContext(sender, args, getSubCommands().isEmpty() ? "" : getSubCommands().get(0)));
	}

	@Override
	protected ReturnType execute(CommandContext context) {
		if (!ServerVersion.isServerVersionAtLeast(ServerVersion.V1_14)) return ReturnType.FAIL;
		final Player player = context.getPlayer();

//		if (CommandMiddleware.handle(player) == ReturnType.FAIL) return ReturnType.FAIL;

		final Block targetBlock = player.getTargetBlock(null, 10);
		if (targetBlock.getType() != CompMaterial.CHEST.get()) return ReturnType.FAIL;

		final Chest chest = (Chest) targetBlock.getState();
		final AuctionHouse instance = AuctionHouse.getInstance();
		final NamespacedKey key = new NamespacedKey(instance, "AuctionHouseMarkedChest");

		if (chest.getPersistentDataContainer().has(key, PersistentDataType.BYTE)) {
			chest.getPersistentDataContainer().remove(key);
			chest.update(true);
			instance.getLocale().getMessage("general.unmarked chest").sendPrefixedMessage(player);
		} else {
			chest.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);
			chest.update(true);
			instance.getLocale().getMessage("general.marked chest").sendPrefixedMessage(player);
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
		return "auctionhouse.cmd.markchest";
	}

	@Override
	public String getSyntax() {
		return AuctionHouse.getInstance().getLocale().getMessage("commands.syntax.markchest").getMessage();
	}

	@Override
	public String getDescription() {
		return AuctionHouse.getInstance().getLocale().getMessage("commands.description.markchest").getMessage();
	}
}
