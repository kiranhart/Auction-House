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
import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.nbtapi.NBT;
import ca.tweetzy.flight.nbtapi.iface.ReadableItemNBT;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@UtilityClass
public final class BundleUtil {

	public boolean isBundledItem(@NonNull final ItemStack itemStack) {
		if (itemStack == null || itemStack.getType() == CompMaterial.AIR.get() || itemStack.getAmount() == 0) return false;
		final boolean hasBundleTag = NBT.get(itemStack, nbt -> (boolean) nbt.hasTag("AuctionBundleItem"));
		return itemStack.getType() != CompMaterial.AIR.get() && hasBundleTag;
	}


	public List<ItemStack> extractBundleItems(@NonNull final ItemStack itemStack) {
		final List<ItemStack> items = new ArrayList<>();

		final int totalBundledItems = NBT.get(itemStack, nbt -> (int) nbt.getInteger("AuctionBundleItem"));

		for (int i = 0; i < totalBundledItems; i++) {
			int finalI = i;
			if (NBT.get(itemStack, (Function<ReadableItemNBT, Boolean>) nbt -> nbt.hasTag("AuctionBundleItem-" + finalI)))
				items.add(AuctionAPI.getInstance().deserializeItem(NBT.get(itemStack, nbt -> (byte[]) nbt.getByteArray("AuctionBundleItem-" + finalI))));
		}


		if (!items.isEmpty())
			return items;

		final ItemStack[] bundledItems = NBT.get(itemStack, nbt -> (ItemStack[]) nbt.getItemStackArray("AuctionBundleItems"));
		if (bundledItems == null)
			return new ArrayList<>();
		return Arrays.stream(bundledItems)
				.filter(item -> item != null)
				.collect(Collectors.toList());
	}
}
