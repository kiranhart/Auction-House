/*
 * Auction House
 * Copyright 2022 Kiran Hart
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

package ca.tweetzy.auctionhouse.ahv3.model;

import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.core.compatibility.XMaterial;
import ca.tweetzy.flight.comp.NBTEditor;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public final class BundleUtil {

	public boolean isBundledItem(@NonNull final ItemStack itemStack) {
		return itemStack.getType() != XMaterial.AIR.parseMaterial() && NBTEditor.contains(itemStack, "AuctionBundleItem");
	}

	public List<ItemStack> extractBundleItems(@NonNull final ItemStack itemStack) {
		final List<ItemStack> items = new ArrayList<>();

		for (int i = 0; i < NBTEditor.getInt(itemStack, "AuctionBundleItem"); i++) {
			items.add(AuctionAPI.getInstance().deserializeItem(NBTEditor.getByteArray(itemStack, "AuctionBundleItem-" + i)));
		}

		return items;
	}
}
