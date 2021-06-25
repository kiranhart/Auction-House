package ca.tweetzy.auctionhouse.economy;

import ca.tweetzy.auctionhouse.settings.Settings;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The current file has been created by Kiran Hart
 * Date Created: June 24 2021
 * Time Created: 11:26 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class EconomyManager {

    private final JavaPlugin plugin;
    private IEconomy selectedEconomy;

    enum SupportedEconomy {
        VAULT("Vault"),
        PLAYER_POINTS("PlayerPoints");

        @Getter
        final String economyName;

        SupportedEconomy(String economyName) {
            this.economyName = economyName;
        }
    }

    public EconomyManager(JavaPlugin plugin) {
        this.plugin = plugin;
        String preferredEconomy = Settings.ECONOMY_MODE.getString();

        if (preferredEconomy.equalsIgnoreCase(SupportedEconomy.VAULT.getEconomyName())) {
            this.selectedEconomy = new VaultEconomy();
            if (!this.selectedEconomy.isEnabled()) {
                Bukkit.getPluginManager().disablePlugin(this.plugin);
                plugin.getLogger().severe("Something went wrong while trying to load the " + selectedEconomy.getHookName() + " economy!");
            }
        }

        if (preferredEconomy.equalsIgnoreCase(SupportedEconomy.PLAYER_POINTS.getEconomyName())) {
            this.selectedEconomy = new PlayerPointsEconomy();
            if (!this.selectedEconomy.isEnabled()) {
                Bukkit.getPluginManager().disablePlugin(this.plugin);
                plugin.getLogger().severe("Something went wrong while trying to load the " + selectedEconomy.getHookName() + " economy!");
            }
        }

        this.plugin.getLogger().info("Using " + selectedEconomy.getHookName() + " as the economy provider!");
    }

    public double getBalance(OfflinePlayer offlinePlayer) {
        return this.selectedEconomy.getBalance(offlinePlayer);
    }

    public boolean has(OfflinePlayer offlinePlayer, double cost) {
        return this.selectedEconomy.has(offlinePlayer, cost);
    }

    public boolean withdrawPlayer(OfflinePlayer offlinePlayer, double cost) {
        return this.selectedEconomy.withdraw(offlinePlayer, cost);
    }

    public boolean depositPlayer(OfflinePlayer offlinePlayer, double cost) {
        return this.selectedEconomy.deposit(offlinePlayer, cost);
    }
}
