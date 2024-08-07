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
import ca.tweetzy.auctionhouse.api.statistic.Statistic;
import ca.tweetzy.auctionhouse.auction.enums.AuctionStatisticType;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public final class AuctionStatisticManager {

	private final List<Statistic> statistics = Collections.synchronizedList(new ArrayList<>());

	// 'cheat' way of doing this leaderboard thing, I will fix this eventually...
	private final Map<UUID, Double> createdAuctionCount = new ConcurrentHashMap<>();
	private final Map<UUID, Double> createdBinCount = new ConcurrentHashMap<>();
	private final Map<UUID, Double> soldAuctionCount = new ConcurrentHashMap<>();
	private final Map<UUID, Double> soldBinCount = new ConcurrentHashMap<>();
	private final Map<UUID, Double> moneySpentCount = new ConcurrentHashMap<>();
	private final Map<UUID, Double> moneyEarnedCount = new ConcurrentHashMap<>();


	public void addStatistic(Statistic statistic) {
		synchronized (this.statistics) {
			if (this.statistics.contains(statistic)) return;
			this.statistics.add(statistic);

			final UUID owner = statistic.getOwner();
			final double value = statistic.getValue();

			switch (statistic.getType()) {
				case CREATED_AUCTION:
					this.createdAuctionCount.put(owner, this.createdAuctionCount.getOrDefault(owner, 0D) + value);
					break;
				case CREATED_BIN:
					this.createdBinCount.put(owner, this.createdBinCount.getOrDefault(owner, 0D) + value);
					break;
				case SOLD_AUCTION:
					this.soldAuctionCount.put(owner, this.soldAuctionCount.getOrDefault(owner, 0D) + value);
					break;
				case SOLD_BIN:
					this.soldBinCount.put(owner, this.soldBinCount.getOrDefault(owner, 0D) + value);
					break;
				case MONEY_SPENT:
					this.moneySpentCount.put(owner, this.moneySpentCount.getOrDefault(owner, 0D) + value);
					break;
				case MONEY_EARNED:
					this.moneyEarnedCount.put(owner, this.moneyEarnedCount.getOrDefault(owner, 0D) + value);
					break;
			}
		}
	}

	public void addStatistics(List<Statistic> statisticsList) {
		synchronized (this.statistics) {
			statisticsList.forEach(this::addStatistic);
		}
	}

	public List<Statistic> getStatistics() {
		synchronized (this.statistics) {
			return this.statistics;
		}
	}

	public Map<UUID, Double> getStatisticMap(AuctionStatisticType statisticType) {
		switch (statisticType) {
			case CREATED_AUCTION:
				return this.createdAuctionCount;
			case CREATED_BIN:
				return this.createdBinCount;
			case SOLD_AUCTION:
				return this.soldAuctionCount;
			case SOLD_BIN:
				return this.soldBinCount;
			case MONEY_SPENT:
				return this.moneySpentCount;
			case MONEY_EARNED:
				return this.moneyEarnedCount;
		}

		return new HashMap<>();
	}

	public List<Statistic> getStatistics(AuctionStatisticType statisticType) {
		synchronized (this.statistics) {
			return this.statistics.stream().filter(stat -> stat.getType() == statisticType).collect(Collectors.toList());
		}
	}

	public double getStatistic(AuctionStatisticType statisticType) {
		synchronized (this.statistics) {
			return this.statistics.stream().filter(stat -> stat.getType() == statisticType).mapToDouble(Statistic::getValue).sum();
		}
	}

	public double getStatistic(AuctionStatisticType statisticType, ChronoUnit timeUnit, int timeAmount) {
		synchronized (this.statistics) {
			final List<Statistic> globalStats = this.statistics.stream().filter(stat -> stat.getType() == statisticType).collect(Collectors.toList());
			final long time = Instant.now().minus(timeAmount, timeUnit).toEpochMilli();

			return globalStats.stream().filter(stat -> stat.getTimeCreated() >= time).mapToDouble(Statistic::getValue).sum();
		}
	}

	public double getStatisticByPlayer(UUID playerUuid, AuctionStatisticType statisticType) {
		synchronized (this.statistics) {
			return this.statistics.stream().filter(stat -> stat.getOwner().equals(playerUuid) && stat.getType() == statisticType).mapToDouble(Statistic::getValue).sum();
		}
	}

	public double getStatisticByPlayer(UUID playerUuid, AuctionStatisticType statisticType, ChronoUnit timeUnit, int timeAmount) {
		synchronized (this.statistics) {
			final List<Statistic> playerStats = this.statistics.stream().filter(stat -> stat.getOwner().equals(playerUuid) && stat.getType() == statisticType).collect(Collectors.toList());
			final long time = Instant.now().minus(timeAmount, timeUnit).toEpochMilli();

			return playerStats.stream().filter(stat -> stat.getTimeCreated() >= time).mapToDouble(Statistic::getValue).sum();
		}
	}

	public void loadStatistics() {
		AuctionHouse.getInstance().getDataManager().getStatistics((error, all) -> {
			if (error == null)
				all.forEach(this::addStatistic);
		});
	}
}
