package ca.tweetzy.auctionhouse.economy;

import org.bukkit.OfflinePlayer;

/**
 * The current file has been created by Kiran Hart
 * Date Created: June 24 2021
 * Time Created: 11:21 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public interface IEconomy {

    String getHookName();

    boolean isEnabled();

    double getBalance(OfflinePlayer player);

    boolean has(OfflinePlayer player, double cost);

    boolean withdraw(OfflinePlayer player, double cost);

    boolean deposit(OfflinePlayer player, double cost);
}
