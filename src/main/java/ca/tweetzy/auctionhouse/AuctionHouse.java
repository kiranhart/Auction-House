package ca.tweetzy.auctionhouse;

import ca.tweetzy.auctionhouse.api.UpdateChecker;
import ca.tweetzy.auctionhouse.api.hook.PlaceholderAPI;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.commands.*;
import ca.tweetzy.auctionhouse.database.DataManager;
import ca.tweetzy.auctionhouse.database.migrations.*;
import ca.tweetzy.auctionhouse.listeners.AuctionListeners;
import ca.tweetzy.auctionhouse.listeners.PlayerListeners;
import ca.tweetzy.auctionhouse.managers.*;
import ca.tweetzy.auctionhouse.settings.LocaleSettings;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.auctionhouse.tasks.AutoSaveTask;
import ca.tweetzy.auctionhouse.tasks.TickAuctionsTask;
import ca.tweetzy.core.TweetyCore;
import ca.tweetzy.core.TweetyPlugin;
import ca.tweetzy.core.commands.CommandManager;
import ca.tweetzy.core.compatibility.ServerProject;
import ca.tweetzy.core.compatibility.ServerVersion;
import ca.tweetzy.core.configuration.Config;
import ca.tweetzy.core.database.DataMigrationManager;
import ca.tweetzy.core.database.DatabaseConnector;
import ca.tweetzy.core.database.MySQLConnector;
import ca.tweetzy.core.database.SQLiteConnector;
import ca.tweetzy.core.gui.GuiManager;
import ca.tweetzy.core.hooks.EconomyManager;
import ca.tweetzy.core.utils.Metrics;
import ca.tweetzy.core.utils.TextUtils;
import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

import java.util.List;


/**
 * The current file has been created by Kiran Hart
 * Date Created: January 12 2021
 * Time Created: 6:30 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */

@SuppressWarnings("unused")
public class AuctionHouse extends TweetyPlugin {

    private static TaskChainFactory taskChainFactory;
    private static AuctionHouse instance;

    @Getter
    @Setter
    private boolean migrating = false;

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
    private FilterManager filterManager;

    @Getter
    private AuctionBanManager auctionBanManager;

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
        TweetyCore.registerPlugin(this, 1, "CHEST");

        // Check server version
        if (ServerVersion.isServerVersionAtOrBelow(ServerVersion.V1_7)) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        taskChainFactory = BukkitTaskChainFactory.create(this);

        // Load Economy
        EconomyManager.load();

        // Settings
        Settings.setup();

        // local
        setLocale(Settings.LANG.getString());
        LocaleSettings.setup();

        // Setup Economy
        EconomyManager.getManager().setPreferredHook(Settings.ECONOMY_PLUGIN.getString());
        if (!EconomyManager.getManager().isEnabled()) {
            getLogger().severe("Could not find a valid economy provider for Auction House");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // listeners
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerListeners(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new AuctionListeners(), this);

        this.data.load();

        // auction players
        this.auctionPlayerManager = new AuctionPlayerManager();
        Bukkit.getOnlinePlayers().forEach(p -> this.auctionPlayerManager.addPlayer(new AuctionPlayer(p)));

        // Setup the database if enabled
        this.databaseConnector = Settings.DATABASE_USE.getBoolean() ? new MySQLConnector(this, Settings.DATABASE_HOST.getString(), Settings.DATABASE_PORT.getInt(), Settings.DATABASE_NAME.getString(), Settings.DATABASE_USERNAME.getString(), Settings.DATABASE_PASSWORD.getString(), Settings.DATABASE_USE_SSL.getBoolean()) : new SQLiteConnector(this);
        this.dataManager = new DataManager(this.databaseConnector, this);

        DataMigrationManager dataMigrationManager = new DataMigrationManager(this.databaseConnector, this.dataManager,
                new _1_InitialMigration(),
                new _2_FilterWhitelistMigration(),
                new _3_BansMigration(),
                new _4_ItemsChangeMigration(),
                new _5_TransactionChangeMigration(),
                new _6_BigIntMigration(),
                new _7_TransactionBigIntMigration()
        );

        dataMigrationManager.runMigrations();

        // load auction items
        this.auctionItemManager = new AuctionItemManager();
        this.auctionItemManager.start();

        // load transactions
        this.transactionManager = new TransactionManager();
        this.transactionManager.loadTransactions();

        // load the filter whitelist items
        this.filterManager = new FilterManager();
        this.filterManager.loadItems();

        // load the bans
        this.auctionBanManager = new AuctionBanManager();
        this.auctionBanManager.loadBans();

        // gui manager
        this.guiManager.init();

        // commands
        this.commandManager = new CommandManager(this);
        this.commandManager.setSyntaxErrorMessage(TextUtils.formatText(getLocale().getMessage("commands.invalid_syntax").getMessage().split("\n")));
        this.commandManager.setNoPermsMessage(TextUtils.formatText(getLocale().getMessage("commands.no_permission").getMessage()));
        this.commandManager.addCommand(new CommandAuctionHouse()).addSubCommands(
                new CommandSell(),
                new CommandActive(),
                new CommandExpired(),
                new CommandTransactions(),
                new CommandSearch(),
                new CommandSettings(),
                new CommandToggleListInfo(),
                new CommandMigrate(),
                new CommandReload(),
                new CommandFilter(),
                new CommandStatus(),
                new CommandAdmin(),
                new CommandBan(),
                new CommandUnban()
        );

        // Placeholder API
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderAPI(this).register();
        }

        // start the auction tick task
        TickAuctionsTask.startTask();
        // auto save task
        if (Settings.AUTO_SAVE_ENABLED.getBoolean()) {
            AutoSaveTask.startTask();
        }

        // update check
        getServer().getScheduler().runTaskLaterAsynchronously(this, () -> this.status = new UpdateChecker(this, 60325, getConsole()).check().getStatus(), 1L);

        // metrics
        this.metrics = new Metrics(this, 6806);

        getServer().getScheduler().runTaskLater(this, () -> {
            if (!ServerProject.isServer(ServerProject.SPIGOT, ServerProject.PAPER)) {
                getLogger().severe("You're running Auction House on a non-supported Jar");
                getLogger().severe("You will not receive any support while using a non-supported jar, support jars: Spigot or Paper");
            }

            if (!System.getProperty("java.version").startsWith("16")) {
                getLogger().severe("You are not running Java 16, Auction House will be updated to use Java 16 in the coming months. If you do not update, you will not be able to use Auction House.");
            }

            if (USER.equals("%%__USER__%%")) {
                getLogger().severe("Could not detect user ID, are you running a cracked / self-compiled copy of auction house?");
            } else {
                getConsole().sendMessage(TextUtils.formatText("&aThank you for purchasing Auction House, it means a lot"));
                getConsole().sendMessage(TextUtils.formatText("&7 - Kiran Hart"));
            }
        }, 1L);
    }

    @Override
    public void onPluginDisable() {
        if (this.dataManager != null) {
            this.auctionItemManager.end();
            this.filterManager.saveFilterWhitelist(false);
            this.auctionBanManager.saveBans(false);
            this.dataManager.close();
        }

        getServer().getScheduler().cancelTasks(this);
    }

    @Override
    public void onConfigReload() {
        EconomyManager.load();
        Settings.setup();
        EconomyManager.getManager().setPreferredHook(Settings.ECONOMY_PLUGIN.getString());
        setLocale(Settings.LANG.getString());
        LocaleSettings.setup();
        this.commandManager.setSyntaxErrorMessage(TextUtils.formatText(getLocale().getMessage("commands.invalid_syntax").getMessage().split("\n")));
        this.commandManager.setNoPermsMessage(TextUtils.formatText(getLocale().getMessage("commands.no_permission").getMessage()));
    }

    @Override
    public List<Config> getExtraConfig() {
        return null;
    }

    public static AuctionHouse getInstance() {
        return instance;
    }

    public static <T> TaskChain<T> newChain() {
        return taskChainFactory.newChain();
    }

    public static <T> TaskChain<T> newSharedChain(String name) {
        return taskChainFactory.newSharedChain(name);
    }


    String IS_SONGODA_DOWNLOAD = "%%__SONGODA__%%";
    String SONGODA_NODE = "%%__SONGODA_NODE__%%";
    String TIMESTAMP = "%%__TIMESTAMP__%%";
    String USER = "%%__USER__%%";
    String USERNAME = "%%__USERNAME__%%";
    String RESOURCE = "%%__RESOURCE__%%";
    String NONCE = "%%__NONCE__%%";

}
