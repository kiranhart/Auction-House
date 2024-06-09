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

import ca.tweetzy.auctionhouse.api.sync.Navigable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum StatisticType implements Navigable<StatisticType> {

	CREATED_AUCTION("Created Auction"),
	CREATED_BIN("Created Bin"),

	SOLD_AUCTION("Sold Auctions"),
	SOLD_BIN("Sold Bins"),

	MONEY_SPENT("Money Spent"),
	MONEY_EARNED("Money Earned");

	@Getter
	private final String type;

	@Override
	public StatisticType next() {
		return values()[(this.ordinal() + 1) % values().length];
	}

	@Override
	public StatisticType previous() {
		return values()[(ordinal() - 1 + values().length) % values().length];
	}

	@Override
	public Class<StatisticType> enumClass() {
		return StatisticType.class;
	}
}