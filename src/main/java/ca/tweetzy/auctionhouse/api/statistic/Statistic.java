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

package ca.tweetzy.auctionhouse.api.statistic;

import ca.tweetzy.auctionhouse.api.sync.Identifiable;
import ca.tweetzy.auctionhouse.api.sync.Trackable;
import ca.tweetzy.auctionhouse.api.sync.Storeable;
import ca.tweetzy.auctionhouse.auction.enums.AuctionStatisticType;
import lombok.NonNull;

import java.util.UUID;

public interface Statistic extends Identifiable<UUID>, Trackable, Storeable<Statistic> {

	/**
	 * The owning player of this statistic
	 *
	 * @return the {@link UUID} of the statistic owner
	 */
	@NonNull UUID getOwner();

	/**
	 * Get the statistic type that is being tracked
	 *
	 * @return the tracked {@link AuctionStatisticType}
	 */
	@NonNull AuctionStatisticType getType();

	/**
	 * The total value of this statistic
	 *
	 * @return statistic value
	 */
	double getValue();
}