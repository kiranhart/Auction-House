package ca.tweetzy.auctionhouse.managers;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.auction.AuctionStat;
import lombok.Getter;
import org.bukkit.OfflinePlayer;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The current file has been created by Kiran Hart
 * Date Created: September 01 2021
 * Time Created: 2:58 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public final class AuctionStatManager {

	public static enum GlobalAuctionStatType {
		CREATED,
		EXPIRED,
		SOLD,
		SPENT
	}

	@Getter
	private final ConcurrentHashMap<UUID, AuctionStat<Integer, Integer, Integer, Double, Double>> stats = new ConcurrentHashMap<>();

	public void loadStats() {
		AuctionHouse.getInstance().getDataManager().getStats((error, stats) -> {
			if (error == null) {
				this.stats.putAll(stats);
			}
		});
	}

	public AuctionStat<Integer, Integer, Integer, Double, Double> getPlayerStats(final OfflinePlayer player) {
		return this.stats.getOrDefault(player.getUniqueId(), new AuctionStat<>(0, 0, 0, 0D, 0D));
	}

	public double getGlobalStat(final GlobalAuctionStatType globalAuctionStatType) {
		double total = 0D;
		switch (globalAuctionStatType) {
			case CREATED:
				total += stats.values().stream().mapToInt(AuctionStat::getCreated).sum();
				break;
			case EXPIRED:
				total += stats.values().stream().mapToInt(AuctionStat::getExpired).sum();
				break;
			case SOLD:
				total += stats.values().stream().mapToInt(AuctionStat::getSold).sum();
				break;
			case SPENT:
				total += stats.values().stream().mapToDouble(AuctionStat::getSpent).sum();
				break;
		}
		return total;
	}

	public void saveStats() {
		AuctionHouse.getInstance().getDataManager().updateStats(this.stats, null);
	}

	public void insertOrUpdate(final OfflinePlayer player, final AuctionStat<Integer, Integer, Integer, Double, Double> stats) {
		if (!this.stats.containsKey(player.getUniqueId())) {
			this.stats.put(player.getUniqueId(), stats);
			return;
		}

		final AuctionStat<Integer, Integer, Integer, Double, Double> foundStats = this.stats.get(player.getUniqueId());
		foundStats.setCreated(foundStats.getCreated() + stats.getCreated());
		foundStats.setSold(foundStats.getSold() + stats.getSold());
		foundStats.setExpired(foundStats.getExpired() + stats.getExpired());
		foundStats.setEarned(foundStats.getEarned() + stats.getEarned());
		foundStats.setSpent(foundStats.getSpent() + stats.getSpent());

		this.stats.put(player.getUniqueId(), foundStats);
	}
}
