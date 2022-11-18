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

package ca.tweetzy.auctionhouse.auction;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.interfaces.Storeable;
import ca.tweetzy.auctionhouse.auction.enums.AuctionStatisticType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;
import java.util.function.Consumer;

@AllArgsConstructor
@Getter
public final class AuctionStatistic implements Storeable<AuctionStatistic> {

	private final UUID id;
	private final UUID statOwner;
	private final AuctionStatisticType statisticType;
	private final double value;
	private final long time;

	public AuctionStatistic(UUID statOwner, AuctionStatisticType type, double value) {
		this(UUID.randomUUID(), statOwner, type, value, System.currentTimeMillis());
	}

	@Override
	public void store(Consumer<AuctionStatistic> stored) {
		final AuctionHouse instance = AuctionHouse.getInstance();
		instance.getDataManager().insertStatistic(this, (error, statistic) -> {
			if (error != null) return;

			if (statistic != null) {
				instance.getAuctionStatisticManager().addStatistic(statistic);

				if (stored != null)
					stored.accept(statistic);
			}
		});
	}
}
