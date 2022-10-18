package ca.tweetzy.auctionhouse.managers;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.auction.enums.AuctionItemCategory;
import ca.tweetzy.auctionhouse.auction.enums.AuctionSaleType;
import ca.tweetzy.auctionhouse.settings.Settings;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The current file has been created by Kiran Hart
 * Date Created: February 10 2021
 * Time Created: 1:27 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */

@Getter
public class AuctionPlayerManager {

	private final ConcurrentHashMap<UUID, AuctionPlayer> auctionPlayers = new ConcurrentHashMap<>();
	private final HashMap<UUID, ItemStack> sellHolding = new HashMap<>();
	private final HashSet<UUID> usingSellGUI = new HashSet<>();
	private final HashMap<UUID, Long> cooldowns = new HashMap<>();

	public void addPlayer(AuctionPlayer auctionPlayer) {
		if (auctionPlayer == null) return;
		this.auctionPlayers.put(auctionPlayer.getUuid(), auctionPlayer);
	}

	public void addPlayer(Player player) {
		if (player == null) return;

		AuctionPlayer found = this.auctionPlayers.get(player.getUniqueId());

		if (found == null) {
			found = new AuctionPlayer(player);
			AuctionHouse.getInstance().getDataManager().insertAuctionPlayer(found, (error, created) -> {
				if (error == null && created != null) {
					AuctionHouse.getInstance().getLogger().info("Creating profile for player: " + player.getName());
					addPlayer(created);
				}

			});

			return;
		}

		found.setPlayer(player);

		if (!Settings.ALLOW_USAGE_OF_BID_SYSTEM.getBoolean())
			found.setSelectedSaleType(AuctionSaleType.BOTH);

		// if the player's current filter is disabled, set it to all
		if (!found.getSelectedFilter().isEnabled())
			found.setSelectedFilter(AuctionItemCategory.ALL);

		if (found.getSelectedFilter() != AuctionItemCategory.ALL && AuctionItemCategory.isAllButAllDisabled())
			found.setSelectedFilter(AuctionItemCategory.ALL);

		if (!Settings.DISABLE_PROFILE_UPDATE_MSG.getBoolean())
			AuctionHouse.getInstance().getLogger().info("Updating profile player reference for: " + player.getName());

	}

	public void addToUsingSellGUI(UUID uuid) {
		if (uuid == null) return;
		this.usingSellGUI.add(uuid);
	}

	public void removeFromUsingSellGUI(UUID uuid) {
		this.usingSellGUI.remove(uuid);
	}

	public void addItemToSellHolding(UUID uuid, ItemStack itemStack) {
		if (itemStack == null) return;
		this.sellHolding.put(uuid, itemStack);
	}

	public void removeItemFromSellHolding(UUID uuid) {
		this.sellHolding.remove(uuid);
	}

	public void removePlayer(UUID uuid) {
		this.auctionPlayers.remove(uuid);
	}

	public AuctionPlayer getPlayer(UUID uuid) {
		return this.auctionPlayers.getOrDefault(uuid, null);
	}

	public ConcurrentHashMap<UUID, AuctionPlayer> getAuctionPlayers() {
		return this.auctionPlayers;
	}

	public void addCooldown(UUID uuid) {
		this.cooldowns.put(uuid, System.currentTimeMillis() + (long) 1000 * Settings.REFRESH_COOL_DOWN.getInt());
	}

	public HashMap<UUID, Long> getCooldowns() {
		return this.cooldowns;
	}

	public void loadPlayers() {
		this.auctionPlayers.clear();

		AuctionHouse.getInstance().getDataManager().getAuctionPlayers((error, all) -> {
			if (error == null) {
				all.forEach(this::addPlayer);

				// add all online players
				Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(AuctionHouse.getInstance(), () -> Bukkit.getOnlinePlayers().forEach(this::addPlayer), 20 * 3);
			}
		});
	}


}
