package ca.tweetzy.auctionhouse;

import ca.tweetzy.auctionhouse.api.UpdateChecker;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.commands.*;
import ca.tweetzy.auctionhouse.database.DataManager;
import ca.tweetzy.auctionhouse.database.migrations._1_InitialMigration;
import ca.tweetzy.auctionhouse.listeners.AuctionListeners;
import ca.tweetzy.auctionhouse.listeners.PlayerListeners;
import ca.tweetzy.auctionhouse.managers.AuctionItemManager;
import ca.tweetzy.auctionhouse.managers.AuctionPlayerManager;
import ca.tweetzy.auctionhouse.managers.TransactionManager;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.auctionhouse.tasks.AutoSaveTask;
import ca.tweetzy.auctionhouse.tasks.TickAuctionsTask;
import ca.tweetzy.core.TweetyCore;
import ca.tweetzy.core.TweetyPlugin;
import ca.tweetzy.core.commands.CommandManager;
import ca.tweetzy.core.compatibility.ServerVersion;
import ca.tweetzy.core.configuration.Config;
import ca.tweetzy.core.core.PluginID;
import ca.tweetzy.core.database.DataMigrationManager;
import ca.tweetzy.core.database.DatabaseConnector;
import ca.tweetzy.core.database.MySQLConnector;
import ca.tweetzy.core.gui.GuiManager;
import ca.tweetzy.core.utils.Metrics;
import lombok.Getter;
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

@SuppressWarnings("unused")
public class AuctionHouse extends TweetyPlugin {

    private static AuctionHouse instance;

    @Getter
    private Economy economy;

    @Getter
    private final GuiManager guiManager = new GuiManager(this);

    @Getter
    private final Config data = new Config(this, "data.yml");

    protected Metrics metrics;

    @Getter
    private CommandManager commandManager;

    @Getter
    private AuctionPlayerManager auctionPlayerManager;

    @Getter
    private AuctionItemManager auctionItemManager;

    @Getter
    private TransactionManager transactionManager;

    @Getter
    private DatabaseConnector databaseConnector;

    @Getter
    private DataManager dataManager;

    @Getter
    private UpdateChecker.UpdateStatus status;

    @Override
    public void onPluginLoad() {
        instance = this;
    }

    @Override
    public void onPluginEnable() {
        TweetyCore.registerPlugin(this, (int) PluginID.AUCTION_HOUSE.getTweetzyID(), "CHEST");

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
        Bukkit.getOnlinePlayers().forEach(p -> this.auctionPlayerManager.addPlayer(new AuctionPlayer(p)));

        // Setup the database if enabled
        if (Settings.DATABASE_USE.getBoolean()) {
            this.databaseConnector = new MySQLConnector(this, Settings.DATABASE_HOST.getString(), Settings.DATABASE_PORT.getInt(), Settings.DATABASE_NAME.getString(), Settings.DATABASE_USERNAME.getString(), Settings.DATABASE_PASSWORD.getString(), Settings.DATABASE_USE_SSL.getBoolean());
            this.dataManager = new DataManager(this.databaseConnector, this);
            DataMigrationManager dataMigrationManager = new DataMigrationManager(this.databaseConnector, this.dataManager, new _1_InitialMigration());
            dataMigrationManager.runMigrations();
        }

        // load auction items
        this.auctionItemManager = new AuctionItemManager();
        this.auctionItemManager.loadItems(Settings.DATABASE_USE.getBoolean());

        // load transactions
        this.transactionManager = new TransactionManager();
        this.transactionManager.loadTransactions(Settings.DATABASE_USE.getBoolean());

        // gui manager
        this.guiManager.init();

        // commands
        this.commandManager = new CommandManager(this);
        this.commandManager.addCommand(new CommandAuctionHouse()).addSubCommands(
                new CommandSell(),
                new CommandActive(),
                new CommandExpired(),
                new CommandTransactions(),
                new CommandSearch(),
                new CommandSettings(),
                new CommandConvert(),
                new CommandReload(),
                new CommandUpload()
        );

        // start the auction tick task
        TickAuctionsTask.startTask();
        // auto save task
        if (Settings.AUTO_SAVE_ENABLED.getBoolean()) {
            AutoSaveTask.startTask();
        }

        // update check
        getServer().getScheduler().runTaskLaterAsynchronously(this, () -> this.status = new UpdateChecker(this, 60325, getConsole()).check().getStatus(), 1L);

        // metrics
        this.metrics = new Metrics(this, (int) PluginID.AUCTION_HOUSE.getbStatsID());
    }

    @Override
    public void onPluginDisable() {
        this.auctionItemManager.saveItems(Settings.DATABASE_USE.getBoolean(), false);
        this.transactionManager.saveTransactions(Settings.DATABASE_USE.getBoolean(), false);
        instance = null;
    }

    @Override
    public void onConfigReload() {
        Settings.setup();
        setLocale(Settings.LANG.getString(), true);
    }

    @Override
    public List<Config> getExtraConfig() {
        return null;
    }

    public static AuctionHouse getInstance() {
        return instance;
    }

    String IS_SONGODA_DOWNLOAD = "%%__SONGODA__%%";
    String SONGODA_NODE = "%%__SONGODA_NODE__%%";
    String TIMESTAMP = "%%__TIMESTAMP__%%";
    String USER = "%%__USER__%%";
    String USERNAME = "%%__USERNAME__%%";
    String RESOURCE = "%%__RESOURCE__%%";
    String NONCE = "%%__NONCE__%%";
}
