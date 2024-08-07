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

package ca.tweetzy.auctionhouse.impl;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.statistic.Statistic;
import ca.tweetzy.auctionhouse.auction.enums.AuctionStatisticType;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.util.UUID;
import java.util.function.Consumer;

@AllArgsConstructor
public final class AuctionStatistic implements Statistic {

	private final UUID id;
	private final AuctionStatisticType type;
	private final UUID owner;
	private final double value;
	private final long createdAt;

	public AuctionStatistic(UUID statOwner, AuctionStatisticType type, double value) {
		this(UUID.randomUUID(), type, statOwner, value, System.currentTimeMillis());
	}

	@Override
	public @NonNull UUID getId() {
		return this.id;
	}

	@Override
	public @NonNull AuctionStatisticType getType() {
		return this.type;
	}

	@Override
	public @NonNull UUID getOwner() {
		return this.owner;
	}

	@Override
	public double getValue() {
		return this.value;
	}

	@Override
	public long getTimeCreated() {
		return this.createdAt;
	}

	@Override
	public long getLastUpdated() {
		return this.getTimeCreated();
	}

	@Override
	public void store(Consumer<Statistic> stored) {
		AuctionHouse.getDataManager().insertStatistic(this, (error, statistic) -> {
			if (error != null) return;

			if (statistic != null) {
				AuctionHouse.getAuctionStatisticManager().addStatistic(statistic);

				if (stored != null)
					stored.accept(statistic);
			}
		});
	}
}
