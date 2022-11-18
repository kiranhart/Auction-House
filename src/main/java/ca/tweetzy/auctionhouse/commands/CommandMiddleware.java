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
import ca.tweetzy.auctionhouse.api.hook.FloodGateHook;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.commands.AbstractCommand;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;

/**
 * The current file has been created by Kiran Hart
 * Date Created: December 14 2021
 * Time Created: 2:19 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
@UtilityClass
public final class CommandMiddleware {

	public AbstractCommand.ReturnType handle(@NonNull final Player player) {
		if (AuctionAPI.tellMigrationStatus(player)) return AbstractCommand.ReturnType.FAILURE;

		final AuctionHouse instance = AuctionHouse.getInstance();
		if (Settings.BLOCKED_WORLDS.getStringList().contains(player.getWorld().getName())) {
			instance.getLocale().getMessage("general.disabled in world").sendPrefixedMessage(player);
			return AbstractCommand.ReturnType.FAILURE;
		}

		if (Settings.USE_AUCTION_CHEST_MODE.getBoolean()) {
			instance.getLocale().getMessage("general.visit auction chest").sendPrefixedMessage(player);
			return AbstractCommand.ReturnType.FAILURE;
		}

		if (instance.getAuctionBanManager().checkAndHandleBan(player)) {
			return AbstractCommand.ReturnType.FAILURE;
		}

		if (FloodGateHook.isFloodGateUser(player)) return AbstractCommand.ReturnType.FAILURE;

		return AbstractCommand.ReturnType.SUCCESS;
	}
}
