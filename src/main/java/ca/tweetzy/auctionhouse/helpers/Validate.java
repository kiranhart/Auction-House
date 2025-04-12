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

package ca.tweetzy.auctionhouse.helpers;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.nbtapi.NBT;
import lombok.experimental.UtilityClass;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@UtilityClass
public final class Validate {


	public boolean hasDTKey(ItemStack stack) {
		if (stack == null || stack.getType() == CompMaterial.AIR.get() || stack.getAmount() == 0) return false;

		final UUID uuid = NBT.get(stack, nbt -> (UUID) nbt.getUUID("AuctionDupeTracking"));
		return uuid != null && AuctionHouse.getInstance().getAuctionItemManager().getItem(uuid) != null;
	}

}
