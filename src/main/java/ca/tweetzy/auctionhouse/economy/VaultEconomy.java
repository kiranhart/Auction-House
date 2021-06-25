package ca.tweetzy.auctionhouse.economy;

import ca.tweetzy.auctionhouse.exception.EconomyProviderNotFoundException;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * The current file has been created by Kiran Hart
 * Date Created: June 24 2021
 * Time Created: 11:23 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class VaultEconomy implements IEconomy {

    private final net.milkbowl.vault.economy.Economy vault;

    public VaultEconomy() {
        RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> v = Bukkit.getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (v != null) {
            this.vault = v.getProvider();
        } else {
            this.vault = null;
            throw new EconomyProviderNotFoundException("Could not find any plugin to hook into: " + getHookName());
        }
    }

    @Override
    public String getHookName() {
        return "Vault";
    }

    @Override
    public boolean isEnabled() {
        return vault != null;
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        if (vault == null) return 0D;
        return vault.getBalance(player);
    }

    @Override
    public boolean has(OfflinePlayer player, double cost) {
        return vault != null && vault.has(player, cost);
    }

    @Override
    public boolean withdraw(OfflinePlayer player, double cost) {
        return vault != null && vault.withdrawPlayer(player, cost).transactionSuccess();
    }

    @Override
    public boolean deposit(OfflinePlayer player, double cost) {
        return vault != null && vault.depositPlayer(player, cost).transactionSuccess();
    }
}
