package ca.tweetzy.auctionhouse.managers;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.core.utils.nms.NBTEditor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * The current file has been created by Kiran Hart
 * Date Created: February 10 2021
 * Time Created: 1:27 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */

@Getter
public class AuctionPlayerManager {

    private final LinkedList<AuctionPlayer> auctionPlayers = new LinkedList<>();

    public void closeAuctionHouse() {
        auctionPlayers.stream().filter(AuctionPlayer::isViewingAuctionHouse).map(AuctionPlayer::getPlayer).forEach(Player::closeInventory);
    }

    public void addPlayer(AuctionPlayer player) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(AuctionHouse.getInstance(), () -> {
            if (auctionPlayers.stream().noneMatch(players -> players.getId().equals(player.getId()))) {
                auctionPlayers.add(player);
            }
        });
    }

    public void addSpeedyPlayer(Player player) {
        if (auctionPlayers.stream().noneMatch(p -> p.getId().equals(player.getUniqueId()))) {
            auctionPlayers.add(new AuctionPlayer(player));
        }
    }

    public void removePlayer(AuctionPlayer player) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(AuctionHouse.getInstance(), () -> {
            auctionPlayers.remove(player);
        });
    }

    public AuctionPlayer locateAndSelectPlayer(Player player) {
        if (auctionPlayers.stream().anyMatch(players -> players.getId().equals(player.getUniqueId()))) {
            return auctionPlayers.stream().filter(players -> players.getId().equals(player.getUniqueId())).findFirst().get();
        }
        return null;
    }
}
