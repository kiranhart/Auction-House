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

import ca.tweetzy.auctionhouse.api.AuctionAPI;
import io.lumine.mythic.lib.api.item.NBTItem;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

/**
 * The current file has been created by Kiran Hart
 * Date Created: August 07 2021
 * Time Created: 6:36 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */

@UtilityClass
public class MMOItemsHook {

	public boolean isEnabled() {
		return Bukkit.getPluginManager().getPlugin("MMOItemsHook") != null;
	}

	private boolean hasType(@NonNull final NBTItem itemStack) {
		return itemStack.hasType();
	}

	public String getItemType(@NonNull final ItemStack itemStack) {
		NBTItem nbtItem = NBTItem.get(itemStack);
		if (nbtItem == null) return ChatColor.stripColor(AuctionAPI.getInstance().getItemName(itemStack));
		if (!hasType(nbtItem)) return ChatColor.stripColor(AuctionAPI.getInstance().getItemName(itemStack));
		return WordUtils.capitalize(nbtItem.getType().toLowerCase().replace("_", " "));
	}
}
