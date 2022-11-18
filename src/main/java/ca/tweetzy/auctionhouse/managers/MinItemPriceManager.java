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

package ca.tweetzy.auctionhouse.managers;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.auction.MinItemPrice;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Date Created: April 04 2022
 * Time Created: 8:22 a.m.
 *
 * @author Kiran Hart
 */
public final class MinItemPriceManager {

	private final List<MinItemPrice> minPrices = new ArrayList<>();

	public void addItem(MinItemPrice minItemPrice) {
		if (this.minPrices.contains(minItemPrice)) return;
		this.minPrices.add(minItemPrice);
	}

	public void removeItem(MinItemPrice minItemPrice) {
		if (!this.minPrices.contains(minItemPrice)) return;
		this.minPrices.remove(minItemPrice);
	}

	public MinItemPrice getMinPrice(ItemStack item) {
		return this.minPrices.stream().filter(mins -> mins.getItemStack().isSimilar(item)).findFirst().orElse(null);
	}

	public List<MinItemPrice> getMinPrices() {
		return this.minPrices;
	}

	public void loadMinPrices() {
		this.minPrices.clear();

		AuctionHouse.getInstance().getDataManager().getMinItemPrices((error, items) -> {
			if (error == null)
				items.forEach(this::addItem);
		});
	}
}
