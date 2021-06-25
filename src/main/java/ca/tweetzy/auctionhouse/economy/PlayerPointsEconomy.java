package ca.tweetzy.auctionhouse.economy;

import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The current file has been created by Kiran Hart
 * Date Created: June 24 2021
 * Time Created: 11:39 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class PlayerPointsEconomy implements IEconomy {

    private final PlayerPoints playerPoints;

    public PlayerPointsEconomy() {
        this.playerPoints = (PlayerPoints) Bukkit.getServer().getPluginManager().getPlugin("PlayerPoints");
    }

    @Override
    public String getHookName() {
        return "PlayerPoints";
    }

    @Override
    public boolean isEnabled() {
        return playerPoints.isEnabled();
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        AtomicInteger value = new AtomicInteger(0);
        playerPoints.getAPI().lookAsync(player.getUniqueId()).thenAccept(value::set);
        return value.get();
    }

    @Override
    public boolean has(OfflinePlayer player, double cost) {
        int total = 0;
        try {
            total = playerPoints.getAPI().lookAsync(player.getUniqueId()).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return total >= convert(cost);
    }

    @Override
    public boolean withdraw(OfflinePlayer player, double cost) {
        boolean success = false;
        try {
            success = playerPoints.getAPI().takeAsync(player.getUniqueId(), convert(cost)).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return success;
    }

    @Override
    public boolean deposit(OfflinePlayer player, double cost) {
        boolean success = false;
        try {
            success = playerPoints.getAPI().giveAsync(player.getUniqueId(), convert(cost)).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return success;
    }

    private int convert(double amount) {
        return (int) Math.ceil(amount);
    }
}
