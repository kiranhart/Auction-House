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

package ca.tweetzy.auctionhouse.managers;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.auction.AuctionStatistic;
import ca.tweetzy.auctionhouse.auction.enums.AuctionStatisticType;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public final class AuctionStatisticManager {

	private final List<AuctionStatistic> statistics = Collections.synchronizedList(new ArrayList<>());

	public void addStatistic(AuctionStatistic statistic) {
		synchronized (this.statistics) {
			if (this.statistics.contains(statistic)) return;
			this.statistics.add(statistic);
		}
	}

	public void addStatistics(List<AuctionStatistic> statisticsList) {
		synchronized (this.statistics) {
			statisticsList.forEach(this::addStatistic);
		}
	}

	public List<AuctionStatistic> getStatistics() {
		synchronized (this.statistics) {
			return this.statistics;
		}
	}

	public double getStatistic(AuctionStatisticType statisticType) {
		synchronized (this.statistics) {
			return this.statistics.stream().filter(stat -> stat.getStatisticType() == statisticType).mapToDouble(AuctionStatistic::getValue).sum();
		}
	}

	public double getStatistic(AuctionStatisticType statisticType, ChronoUnit timeUnit, int timeAmount) {
		synchronized (this.statistics) {
			final List<AuctionStatistic> globalStats = this.statistics.stream().filter(stat -> stat.getStatisticType() == statisticType).collect(Collectors.toList());
			final long time = Instant.now().minus(timeAmount, timeUnit).toEpochMilli();

			return globalStats.stream().filter(stat -> stat.getTime() >= time).mapToDouble(AuctionStatistic::getValue).sum();
		}
	}

	public double getStatisticByPlayer(UUID playerUuid, AuctionStatisticType statisticType) {
		synchronized (this.statistics) {
			return this.statistics.stream().filter(stat -> stat.getStatOwner().equals(playerUuid) && stat.getStatisticType() == statisticType).mapToDouble(AuctionStatistic::getValue).sum();
		}
	}

	public double getStatisticByPlayer(UUID playerUuid, AuctionStatisticType statisticType, ChronoUnit timeUnit, int timeAmount) {
		synchronized (this.statistics) {
			final List<AuctionStatistic> playerStats = this.statistics.stream().filter(stat -> stat.getStatOwner().equals(playerUuid) && stat.getStatisticType() == statisticType).collect(Collectors.toList());
			final long time = Instant.now().minus(timeAmount, timeUnit).toEpochMilli();

			return playerStats.stream().filter(stat -> stat.getTime() >= time).mapToDouble(AuctionStatistic::getValue).sum();
		}
	}

	public void loadStatistics() {
		AuctionHouse.getInstance().getDataManager().getStatistics((error, all) -> {
			if (error == null)
				all.forEach(this::addStatistic);
		});
	}
}
