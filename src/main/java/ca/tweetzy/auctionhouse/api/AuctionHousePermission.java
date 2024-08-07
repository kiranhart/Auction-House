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

package ca.tweetzy.auctionhouse.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@AllArgsConstructor
@Getter
public enum AuctionHousePermission {

	COMMAND_SELL(cmd("sell"), "Allows the user to use /ah sell"),
	COMMAND_SEARCH(cmd("search"), "Allows the user to use /ah search <keywords>"),
	COMMAND_ACTIVE(cmd("active"), "Allows the user to use /ah active"),
	COMMAND_EXPIRED(cmd("expired"), "Allows the user to use /ah expired"),
	COMMAND_PROFILE(cmd("profile"), "Allows the user to use /ah profile"),
	COMMAND_ADMIN(cmd("admin"), "Allows the user to use /ah admin"),
	COMMAND_RELOAD(cmd("reload"), "Allows the user to use /ah reload"),

	UNLIMITED_LISTINGS(wild("auctionhouse.maxallowedlistings"), "Allows the user to have unlimited listings"),

	;

	private final String permission;

	private final String description;

	private static String cmd(@NonNull final String value) {
		return String.format("auctionhouse.command.%s", value);
	}

	private static String wild(@NonNull final String value) {
		return String.format("%s.*", value);
	}
}