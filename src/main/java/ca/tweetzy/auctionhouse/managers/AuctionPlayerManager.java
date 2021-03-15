package ca.tweetzy.auctionhouse.managers;

import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * The current file has been created by Kiran Hart
 * Date Created: February 10 2021
 * Time Created: 1:27 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */

@Getter
public class AuctionPlayerManager {

    private final ArrayList<AuctionPlayer> auctionPlayers = new ArrayList<>();

    public void closeAuctionHouse() {
        auctionPlayers.stream().filter(AuctionPlayer::isViewingAuctionHouse).map(AuctionPlayer::getPlayer).forEach(Player::closeInventory);
    }

    public void addPlayer(AuctionPlayer auctionPlayer) {
        if (auctionPlayer == null) return;
        if (this.auctionPlayers.stream().anyMatch(player -> player.getPlayer().getUniqueId().equals(auctionPlayer.getPlayer().getUniqueId()))) return;
        this.auctionPlayers.add(auctionPlayer);
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
}
