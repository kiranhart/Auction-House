/*
 * Auction House
 * Copyright 2022-2023 Kiran Hart
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

import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.core.compatibility.XMaterial;
import ca.tweetzy.flight.nbtapi.NBT;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@UtilityClass
public final class BundleUtil {

	public boolean isBundledItem(@NonNull final ItemStack itemStack) {
		return itemStack.getType() != XMaterial.AIR.parseMaterial() && NBT.get(itemStack, nbt -> nbt.hasTag("AuctionBundleItem"));
	}

	public List<ItemStack> extractBundleItems(@NonNull final ItemStack itemStack) {
		final List<ItemStack> items = new ArrayList<>();

		final int totalBundledItems = NBT.get(itemStack, nbt -> nbt.getInteger("AuctionBundleItem"));

		for (int i = 0; i < totalBundledItems; i++) {
			int finalI = i;
			if (NBT.get(itemStack, nbt -> nbt.hasTag("AuctionBundleItem-" + finalI)))
				items.add(AuctionAPI.getInstance().deserializeItem(NBT.get(itemStack, nbt -> nbt.getByteArray("AuctionBundleItem-" + finalI))));
		}


		if (!items.isEmpty())
			return items;

		final ItemStack[] bundledItems = NBT.get(itemStack, nbt -> nbt.getItemStackArray("AuctionBundleItems"));
		return new ArrayList<>(Arrays.asList(bundledItems));
	}
}
