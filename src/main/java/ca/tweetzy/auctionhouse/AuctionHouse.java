package ca.tweetzy.auctionhouse;

import ca.tweetzy.auctionhouse.commands.CommandActive;
import ca.tweetzy.auctionhouse.commands.CommandAuctionHouse;
import ca.tweetzy.auctionhouse.commands.CommandExpired;
import ca.tweetzy.auctionhouse.commands.CommandSell;
import ca.tweetzy.auctionhouse.listeners.AuctionListeners;
import ca.tweetzy.auctionhouse.listeners.PlayerListeners;
import ca.tweetzy.auctionhouse.managers.AuctionItemManager;
import ca.tweetzy.auctionhouse.managers.AuctionPlayerManager;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.auctionhouse.tasks.TickAuctionsTask;
import ca.tweetzy.core.TweetyCore;
import ca.tweetzy.core.TweetyPlugin;
import ca.tweetzy.core.commands.CommandManager;
import ca.tweetzy.core.compatibility.ServerVersion;
import ca.tweetzy.core.configuration.Config;
import ca.tweetzy.core.core.PluginID;
import ca.tweetzy.core.gui.GuiManager;
import ca.tweetzy.core.utils.Metrics;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
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

    private final GuiManager guiManager = new GuiManager(this);
    private final Config data = new Config(this, "data.yml");

    protected Metrics metrics;
    private CommandManager commandManager;
    private AuctionPlayerManager auctionPlayerManager;
    private AuctionItemManager auctionItemManager;

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

        // listeners
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerListeners(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new AuctionListeners(), this);

        this.data.load();

        // auction players
        this.auctionPlayerManager = new AuctionPlayerManager();
//        Bukkit.getOnlinePlayers().forEach(p -> this.auctionPlayerManager.addSpeedyPlayer(p));

        // load auction items
        this.auctionItemManager = new AuctionItemManager();
        this.auctionItemManager.loadItems();

        // gui manager
        this.guiManager.init();

        // commands
        this.commandManager = new CommandManager(this);
        this.commandManager.addCommand(new CommandAuctionHouse()).addSubCommands(
                new CommandSell(),
                new CommandActive(),
                new CommandExpired()
        );

        // start the auction tick task
        TickAuctionsTask.startTask();

        // metrics
        this.metrics = new Metrics(this, (int) PluginID.AUCTION_HOUSE.getbStatsID());
    }

    @Override
    public void onPluginDisable() {
        this.auctionItemManager.saveItems();
        instance = null;
        //token change test
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

    public AuctionItemManager getAuctionItemManager() {
        return auctionItemManager;
    }

    public AuctionPlayerManager getAuctionPlayerManager() {
        return auctionPlayerManager;
    }

    public GuiManager getGuiManager() {
        return guiManager;
    }
}
