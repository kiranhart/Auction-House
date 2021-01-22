package ca.tweetzy.auctionhouse;

import ca.tweetzy.auctionhouse.commands.CommandAuctionHouse;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.TweetyCore;
import ca.tweetzy.core.TweetyPlugin;
import ca.tweetzy.core.commands.CommandManager;
import ca.tweetzy.core.compatibility.ServerVersion;
import ca.tweetzy.core.configuration.Config;
import ca.tweetzy.core.core.PluginID;
import ca.tweetzy.core.utils.Metrics;
import ca.tweetzy.core.utils.nms.NBTEditor;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.List;

/**
 * The current file has been created by Kiran Hart
 * Date Created: January 12 2021
 * Time Created: 6:30 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class AuctionHouse extends TweetyPlugin {

    private static AuctionHouse instance;

    private Economy economy;

    private final Config data = new Config(this, "data.yml");

    protected Metrics metrics;
    private CommandManager commandManager;

    @Override
    public void onPluginLoad() {
        instance = this;
    }

    @Override
    public void onPluginEnable() {
        TweetyCore.registerPlugin(this, (int) PluginID.AUCTION_HOUSE.getTweetzyID(), "CHEST");
        TweetyCore.initEvents(this);

        // Check server version
        if (ServerVersion.isServerVersionAtOrBelow(ServerVersion.V1_7)) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Check for vault
        if (getServer().getPluginManager().isPluginEnabled("Vault")) {
            RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp != null) this.economy = rsp.getProvider();
        }

        // Settings
        Settings.setup();

        // local
        setLocale(Settings.LANG.getString(), false);

        // commands

        this.data.load();

        // metrics
        if (Settings.METRICS.getBoolean()) {
            this.metrics = new Metrics(this, (int) PluginID.AUCTION_HOUSE.getbStatsID());
        }
    }

    @Override
    public void onPluginDisable() {
        instance = null;
    }

    @Override
    public void onConfigReload() {
    }

    @Override
    public void reloadConfig() {
    }

    @Override
    public List<Config> getExtraConfig() {
        return null;
    }

    public static AuctionHouse getInstance() {
        return instance;
    }

    public Config getData() {
        return data;
    }

    public Economy getEconomy() {
        return economy;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }
}
