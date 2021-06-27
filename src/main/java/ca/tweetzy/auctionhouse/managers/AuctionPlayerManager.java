package ca.tweetzy.auctionhouse.managers;

import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.settings.Settings;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * The current file has been created by Kiran Hart
 * Date Created: February 10 2021
 * Time Created: 1:27 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */

@Getter
public class AuctionPlayerManager {

    private final ArrayList<AuctionPlayer> auctionPlayers = new ArrayList<>();
    private final HashMap<UUID, ItemStack> sellHolding = new HashMap<>();
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();

    public void addPlayer(AuctionPlayer auctionPlayer) {
        if (auctionPlayer == null) return;
        if (this.auctionPlayers.stream().anyMatch(player -> player.getPlayer().getUniqueId().equals(auctionPlayer.getPlayer().getUniqueId()))) return;
        this.auctionPlayers.add(auctionPlayer);
    }

    public void addItemToSellHolding(UUID uuid, ItemStack itemStack) {
        if (itemStack == null) return;
        this.sellHolding.put(uuid, itemStack);
    }

    public void removeItemFromSellHolding(UUID uuid) {
        this.sellHolding.remove(uuid);
    }

    public void removePlayer(UUID uuid) {
        this.auctionPlayers.removeIf(player -> player.getPlayer().getUniqueId().equals(uuid));
    }

    public AuctionPlayer getPlayer(UUID uuid) {
        return this.auctionPlayers.stream().filter(item -> item.getPlayer().getUniqueId().equals(uuid)).findFirst().orElse(null);
    }

    public List<AuctionPlayer> getAuctionPlayers() {
        return Collections.unmodifiableList(auctionPlayers);
    }

    public void addCooldown(UUID uuid) {
        this.cooldowns.put(uuid, System.currentTimeMillis() + (long) 1000 * Settings.REFRESH_COOL_DOWN.getInt());
    }

    public HashMap<UUID, Long> getCooldowns() {
        return this.cooldowns;
    }
}
