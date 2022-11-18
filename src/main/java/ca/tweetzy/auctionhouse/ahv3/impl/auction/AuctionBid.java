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

package ca.tweetzy.auctionhouse.ahv3.impl.auction;

import ca.tweetzy.auctionhouse.ahv3.api.auction.Bid;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.function.Consumer;

@AllArgsConstructor
public final class AuctionBid implements Bid {

	private final UUID id;
	private final UUID auctionId;
	private final UUID bidderUUID;
	private final String bidderName;
	private final BigDecimal amount;
	private final long time;

	@Override
	public @NonNull UUID getId() {
		return this.id;
	}

	@Override
	public UUID getAuctionId() {
		return this.auctionId;
	}

	@Override
	public UUID getBidderUUID() {
		return this.bidderUUID;
	}

	@Override
	public String getBidderName() {
		return this.bidderName;
	}

	@Override
	public BigDecimal getAmount() {
		return this.amount;
	}

	@Override
	public long getBidTime() {
		return this.time;
	}

	@Override
	public void store(Consumer<Bid> storedItem) {
		// TODO implement auction bid store
	}
}
