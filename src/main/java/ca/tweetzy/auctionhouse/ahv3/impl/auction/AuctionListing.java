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

package ca.tweetzy.auctionhouse.ahv3.impl.auction;

import ca.tweetzy.auctionhouse.ahv3.api.ListingType;
import ca.tweetzy.auctionhouse.ahv3.api.auction.Bid;
import ca.tweetzy.auctionhouse.ahv3.api.auction.Biddable;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public final class AuctionListing extends BinListing implements Biddable {

	private final BigDecimal startingBid;
	private final List<Bid> bids;

	public AuctionListing(
			@NonNull UUID uuid,
			@NonNull UUID ownerUUID,
			@NonNull String ownerName,
			@NonNull ItemStack item,
			@NonNull BigDecimal startingBid,
			@NonNull BigDecimal binPrice,
			@NonNull String listedWorld,
			@NonNull String listedServer,
			@NonNull List<Bid> bids,
			long listedAt,
			long expiresAt
	) {
		super(ListingType.AUCTION, uuid, ownerUUID, ownerName, item, binPrice, listedWorld, listedServer, listedAt, expiresAt);
		this.startingBid = startingBid;
		this.bids = bids;
	}

	public AuctionListing(
			@NonNull Player player,
			@NonNull ItemStack item,
			@NonNull BigDecimal startingBid,
			@NonNull final BigDecimal binPrice,
			@NonNull List<Bid> bids
	) {
		super(ListingType.AUCTION, player, item, binPrice);
		this.startingBid = startingBid;
		this.bids = bids;
	}

	@Override
	public BigDecimal getStartingPrice() {
		return this.startingBid;
	}

	@Override
	public List<Bid> getBids() {
		return this.bids;
	}
}
