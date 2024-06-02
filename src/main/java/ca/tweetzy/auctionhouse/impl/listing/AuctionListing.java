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

import ca.tweetzy.auctionhouse.api.auction.Bid;
import ca.tweetzy.auctionhouse.api.auction.Biddable;
import ca.tweetzy.auctionhouse.api.auction.ListingType;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public final class AuctionListing extends BinListing implements Biddable {

	private final double startingBid;

	private UUID highestBidderUUID;
	private String highestBidderName;

	private final List<Bid> bids;

	public AuctionListing(
			@NonNull UUID uuid,
			@NonNull UUID ownerUUID,
			@NonNull String ownerName,
			@NonNull ItemStack item,
			@NonNull String currency,
			@NonNull ItemStack currencyItem,
			double startingBid,
			double binPrice,
			@NonNull String listedWorld,
			@NonNull String listedServer,
			@NonNull UUID highestBidderUUID,
			@NonNull String highestBidderName,
			@NonNull List<Bid> bids,
			long listedAt,
			long expiresAt
	) {
		super(ListingType.AUCTION, uuid, ownerUUID, ownerName, item, currency, currencyItem, binPrice, listedWorld, listedServer, listedAt, expiresAt);
		this.startingBid = startingBid;
		this.highestBidderName = highestBidderName;
		this.highestBidderUUID = highestBidderUUID;
		this.bids = bids;
	}

	public AuctionListing(
			@NonNull Player player,
			@NonNull ItemStack item,
			double startingBid,
			final double binPrice,
			@NonNull List<Bid> bids
	) {
		super(ListingType.AUCTION, player, item, binPrice);
		this.startingBid = startingBid;
		this.bids = bids;
	}

	@Override
	public double getStartingPrice() {
		return this.startingBid;
	}

	@Override
	public List<Bid> getBids() {
		return this.bids;
	}

	public UUID getHighestBidderUUID() {
		return highestBidderUUID;
	}

	public void setHighestBidderUUID(@NonNull final UUID highestBidderUUID) {
		this.highestBidderUUID = highestBidderUUID;
	}

	public String getHighestBidderName() {
		return highestBidderName;
	}

	public void setHighestBidderName(@NonNull String highestBidderName) {
		this.highestBidderName = highestBidderName;
	}
}