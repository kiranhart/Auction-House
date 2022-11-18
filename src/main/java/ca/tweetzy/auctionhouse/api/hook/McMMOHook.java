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

package ca.tweetzy.auctionhouse.api.hook;

import com.gmail.nossr50.api.AbilityAPI;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * The current file has been created by Kiran Hart
 * Date Created: October 09 2021
 * Time Created: 1:35 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
@UtilityClass
public final class McMMOHook {

	private boolean isEnabled() {
		return Bukkit.getPluginManager().getPlugin("mcMMO") != null;
	}

	public boolean isUsingAbility(@NonNull final Player player) {
		if (!isEnabled()) return false;
		return AbilityAPI.isAnyAbilityEnabled(player);
	}
}
