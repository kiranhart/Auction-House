package ca.tweetzy.auctionhouse.managers;

import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.settings.Settings;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
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
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();

    public void addPlayer(AuctionPlayer auctionPlayer) {
        if (auctionPlayer == null) return;
        this.auctionPlayers.put(auctionPlayer.getPlayer().getUniqueId(), auctionPlayer);
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
}
