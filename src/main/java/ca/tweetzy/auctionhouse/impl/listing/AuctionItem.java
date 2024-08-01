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

package ca.tweetzy.auctionhouse.impl.listing;

import ca.tweetzy.auctionhouse.api.auction.Auction;
import ca.tweetzy.auctionhouse.api.auction.ListingType;
import ca.tweetzy.auctionhouse.api.sync.ListingDeleteResult;
import ca.tweetzy.auctionhouse.api.sync.Storeable;
import ca.tweetzy.auctionhouse.api.sync.Unstoreable;
import lombok.AllArgsConstructor;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

@AllArgsConstructor
public abstract class AuctionItem implements Auction, Storeable<AuctionItem>, Unstoreable<ListingDeleteResult> {

	protected final ItemStack item;
	protected final ListingType listingType;

	@Override
	public void store(Consumer<AuctionItem> stored) {

	}

	@Override
	public void sync(Consumer<Boolean> wasSuccess) {

	}
}
