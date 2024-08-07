/*
 * Auction House
 * Copyright 2018-2022 Kiran Hart
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ca.tweetzy.auctionhouse;

import ca.tweetzy.auctionhouse.auction.AuctionedItem;
import ca.tweetzy.auctionhouse.commands.*;
import ca.tweetzy.auctionhouse.database.DataManager;
import ca.tweetzy.auctionhouse.database.migrations.*;
import ca.tweetzy.auctionhouse.database.migrations.v2.*;
import ca.tweetzy.auctionhouse.helpers.UpdateChecker;
import ca.tweetzy.auctionhouse.hooks.PlaceholderAPIHook;
import ca.tweetzy.auctionhouse.listeners.*;
import ca.tweetzy.auctionhouse.managers.*;
import ca.tweetzy.auctionhouse.model.manager.BanManager;
import ca.tweetzy.auctionhouse.model.manager.CurrencyManager;
import ca.tweetzy.auctionhouse.model.manager.ListingManager;
import ca.tweetzy.auctionhouse.model.manager.PaymentsManager;
import ca.tweetzy.auctionhouse.settings.LocaleSettings;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.auctionhouse.settings.v3.Translations;
import ca.tweetzy.auctionhouse.tasks.AutoSaveTask;
import ca.tweetzy.auctionhouse.tasks.TickAuctionsTask;
import ca.tweetzy.core.TweetyCore;
import ca.tweetzy.core.TweetyPlugin;
import ca.tweetzy.core.commands.CommandManager;
import ca.tweetzy.core.compatibility.ServerProject;
import ca.tweetzy.core.configuration.Config;
import ca.tweetzy.core.gui.GuiManager;
import ca.tweetzy.core.utils.Metrics;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.flight.comp.enums.ServerVersion;
import ca.tweetzy.flight.config.tweetzy.TweetzyYamlConfig;
import ca.tweetzy.flight.database.*;
import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;
import lombok.Getter;
import lombok.Setter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.stream.Collectors;


/**
 * The current file has been created by Kiran Hart
 * Date Created: January 12 2021
 * Time Created: 6:30 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */

public class AuctionHouse extends TweetyPlugin {

	//==========================================================================//
	// "v3" stuff for organization
	@Getter
	private static TweetzyYamlConfig migrationCoreConfig;

	private DatabaseConnector databaseConnector;
	private DataManager dataManager;

	private final CurrencyManager currencyManager = new CurrencyManager();
	private final CommandManager commandManager = new CommandManager(this);
	private final GuiManager guiManager = new GuiManager(this);
	private final ListingManager listingManager = new ListingManager();

	private final AuctionPlayerManager auctionPlayerManager = new AuctionPlayerManager();
	private final AuctionItemManager auctionItemManager = new AuctionItemManager();
	private final TransactionManager transactionManager = new TransactionManager();
	private final FilterManager filterManager = new FilterManager();
	private final BanManager banManager = new BanManager();
	private final AuctionStatisticManager auctionStatisticManager = new AuctionStatisticManager();
	private final MinItemPriceManager minItemPriceManager = new MinItemPriceManager();
	private final PaymentsManager paymentsManager = new PaymentsManager();


	// the default vault economy
	private Economy economy = null;


	//==========================================================================//

	private static TaskChainFactory taskChainFactory;

	@Getter
	@Setter
	private boolean migrating = false;

	protected Metrics metrics;

	@Getter
	private UpdateChecker.UpdateStatus status;

	@Override
	public void onPluginLoad() {

	}

	@Override
	public void onPluginEnable() {
		TweetyCore.registerPlugin(this, 1, "CHEST");

		if (ServerVersion.isServerVersionAtOrBelow(ServerVersion.V1_7)) {
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		taskChainFactory = BukkitTaskChainFactory.create(this);
		migrationCoreConfig = new TweetzyYamlConfig(this, "migration-config-dont-touch.yml");

		// Settings & Locale
		Settings.setup();
		setLocale(Settings.LANG.getString());
		LocaleSettings.setup();

		initializeBStats();

		// settings / locales v3
		Translations.init();
		ca.tweetzy.auctionhouse.settings.v3.Settings.init();

		// Setup the database if enabled
		this.databaseConnector = Settings.DATABASE_USE.getBoolean() ? new MySQLConnector(
				this,
				Settings.DATABASE_HOST.getString(),
				Settings.DATABASE_PORT.getInt(),
				Settings.DATABASE_NAME.getString(),
				Settings.DATABASE_USERNAME.getString(),
				Settings.DATABASE_PASSWORD.getString(),
				Settings.DATABASE_CUSTOM_PARAMS.getString().equalsIgnoreCase("None") ? "" : Settings.DATABASE_CUSTOM_PARAMS.getString()
		) : new SQLiteConnector(this);

		// Use a custom table prefix if using a remote database. The default prefix setting acts exactly like if the prefix is null
		final String tablePrefix = Settings.DATABASE_USE.getBoolean() ? Settings.DATABASE_TABLE_PREFIX.getString() : null;
		this.dataManager = new DataManager(this.databaseConnector, this, tablePrefix);

		DataMigrationManager dataMigrationManager = new DataMigrationManager(this.databaseConnector, this.dataManager,
				new _1_InitialMigration(),
				new _2_FilterWhitelistMigration(),
				new _3_BansMigration(),
				new _4_ItemsChangeMigration(),
				new _5_TransactionChangeMigration(),
				new _6_BigIntMigration(),
				new _7_TransactionBigIntMigration(),
				new _8_ItemPerWorldMigration(),
				new _9_StatsMigration(),
				new _10_InfiniteItemsMigration(),
				new _11_AdminLogMigration(),
				new _12_SerializeFormatDropMigration(),
				new _13_MinItemPriceMigration(),
				new _14_PartialQtyBuyMigration(),
				new _15_AuctionPlayerMigration(),
				new _16_StatisticVersionTwoMigration(),
				new _17_PaymentsMigration(),
				new _18_PaymentsItemMigration(),
				new _19_ServerAuctionMigration(),
				new _20_AuctionRequestsMigration(),
				new _21_RequestsDynAmtMigration(),
				new _22_BansV2Migration(),
				new _23_ItemToNBTSerializationMigration(),
				new _24_RemainingItemToNBTSerializationMigration()
		);

		dataMigrationManager.runMigrations();

		// setup Vault Economy
		setupEconomy();

		// gui manager
		this.guiManager.init();
		this.banManager.load();
		this.currencyManager.load();
		this.paymentsManager.load();

		// warn users if there is a mismatch between new economy and old
		if (Settings.ECONOMY_PLUGIN.getString().toLowerCase().contains("ultraeconomy") && !Settings.CURRENCY_DEFAULT_SELECTED.getString().toLowerCase().contains("ultraeconomy")) {
			getLogger().warning("You are seeing this message because there is a miss match between your old economy provider settings and the new one. While the plugin will default to Vault, if you are seeing this message it's likely because you were using UltraEconomy and you haven't set it up back under 'default selection' in the config.yml!");
		}

		// listeners
		Bukkit.getServer().getPluginManager().registerEvents(new PlayerListeners(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new MeteorClientListeners(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new AuctionListeners(), this);

		if (getServer().getPluginManager().isPluginEnabled("ChestShop"))
			Bukkit.getServer().getPluginManager().registerEvents(new ChestShopListener(), this);

		if (getServer().getPluginManager().isPluginEnabled("CMI"))
			Bukkit.getServer().getPluginManager().registerEvents(new CMIListener(), this);


		this.auctionItemManager.start();
		this.transactionManager.loadTransactions();
		this.filterManager.loadItems();
		this.minItemPriceManager.loadMinPrices();
		this.auctionStatisticManager.loadStatistics();
		this.auctionPlayerManager.loadPlayers();

		// commands
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
				new CommandAdmin(),
				new CommandBan(),
				new CommandUnban(),
				new CommandMarkChest(),
				new CommandUpload(),
				new CommandMinPrice(),
				new CommandStats(),
				new CommandPayments(),
				new CommandBids(),
				new CommandConfirm(),
				new CommandRequest()
		);

		// Placeholder API
		final Plugin papi = Bukkit.getPluginManager().getPlugin("PlaceholderAPI");
		if (papi != null && papi.isEnabled())
			new PlaceholderAPIHook(this).register();


		// start the auction tick task
		TickAuctionsTask.startTask();

		// auto save task
		if (Settings.AUTO_SAVE_ENABLED.getBoolean()) {
			AutoSaveTask.startTask();
		}

		// update check
		if (Settings.UPDATE_CHECKER.getBoolean() && ServerProject.getServerVersion() != ServerProject.UNKNOWN)
			getServer().getScheduler().runTaskLaterAsynchronously(this, () -> this.status = new UpdateChecker(this, 60325, getConsole()).check().getStatus(), 1L);

		// metrics
		this.metrics = new Metrics(this, 6806);
		this.metrics.addCustomChart(new Metrics.SimplePie("using_mysql", () -> String.valueOf(Settings.DATABASE_USE.getBoolean())));

		getServer().getScheduler().runTaskLater(this, () -> {
			if (!ServerProject.isServer(ServerProject.SPIGOT, ServerProject.PAPER)) {
				getLogger().severe("You're running Auction House on a non-supported Jar");
				getLogger().severe("You will not receive any support while using a non-supported jar, support jars: Spigot or Paper");
			}

			if (!ServerProject.isServer(ServerProject.PAPER, ServerProject.SPIGOT)) {
				getLogger().warning("You're running Auction House on a non supported server jar, although small, there's a chance somethings will not work or just entirely break.");
			}

			final String uIDPartOne = "%%__US";
			final String uIDPartTwo = "ER__%%";

			if (USER.contains(uIDPartOne) && USER.contains(uIDPartTwo)) {
				getLogger().severe("Could not detect user ID, are you running a cracked / self-compiled copy of auction house?");
			} else {
				getConsole().sendMessage(TextUtils.formatText("&e&m--------------------------------------------------------"));
				getConsole().sendMessage(TextUtils.formatText(""));
				getConsole().sendMessage(TextUtils.formatText("&aThank you for purchasing Auction House, it means a lot"));
				getConsole().sendMessage(TextUtils.formatText("&7 - Kiran Hart"));
				getConsole().sendMessage(TextUtils.formatText(""));
				getConsole().sendMessage(TextUtils.formatText("&e&m--------------------------------------------------------"));
			}
		}, 1L);
	}

	private void initializeBStats() {
		if (Settings.AUTO_BSTATS.getBoolean()) {
			final File file = new File("plugins" + File.separator + "bStats" + File.separator + "config.yml");
			if (file.exists()) {
				final YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
				configuration.set("enabled", true);
				try {
					configuration.save(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void onPluginDisable() {
		if (this.dataManager != null) {
			// clean up the garbage items
			this.dataManager.deleteItems(this.auctionItemManager.getDeletedItems().values().stream().map(AuctionedItem::getId).collect(Collectors.toList()));

			this.auctionItemManager.end();
			this.filterManager.saveFilterWhitelist(false);
		}

		shutdownDataManager(this.dataManager);
		getServer().getScheduler().cancelTasks(this);
		// send out remaining webhooks
//		this.listingManager.sendPendingDiscordWebhooks();
	}

	@Override
	public void onConfigReload() {
		Settings.setup();
		setLocale(Settings.LANG.getString());
		LocaleSettings.setup();
		this.commandManager.setSyntaxErrorMessage(TextUtils.formatText(getLocale().getMessage("commands.invalid_syntax").getMessage().split("\n")));
		this.commandManager.setNoPermsMessage(TextUtils.formatText(getLocale().getMessage("commands.no_permission").getMessage()));
	}

	//========================================== Getters ==========================================
	public static <T> TaskChain<T> newChain() {
		return taskChainFactory.newChain();
	}

	public static <T> TaskChain<T> newSharedChain(String name) {
		return taskChainFactory.newSharedChain(name);
	}

	public static AuctionHouse getInstance() {
		return (AuctionHouse) TweetyPlugin.getInstance();
	}

	public static DataManager getDataManager() {
		return getInstance().dataManager;
	}

	public static DatabaseConnector getDatabaseConnector() {
		return getInstance().databaseConnector;
	}

	public static GuiManager getGuiManager() {
		return getInstance().guiManager;
	}

	public static CommandManager getCommandManager() {
		return getInstance().commandManager;
	}

	public static AuctionPlayerManager getAuctionPlayerManager() {
		return getInstance().auctionPlayerManager;
	}

	public static AuctionItemManager getAuctionItemManager() {
		return getInstance().auctionItemManager;
	}

	public static TransactionManager getTransactionManager() {
		return getInstance().transactionManager;
	}

	public static BanManager getBanManager() {
		return getInstance().banManager;
	}

	public static FilterManager getFilterManager() {
		return getInstance().filterManager;
	}

	public static AuctionStatisticManager getAuctionStatisticManager() {
		return getInstance().auctionStatisticManager;
	}

	public static MinItemPriceManager getMinItemPriceManager() {
		return getInstance().minItemPriceManager;
	}

	public static PaymentsManager getPaymentsManager() {
		return getInstance().paymentsManager;
	}

	public static CurrencyManager getCurrencyManager() {
		return getInstance().currencyManager;
	}

	public static ListingManager getListingManager() {
		return getInstance().listingManager;
	}

	public static Economy getEconomy() {
		return getInstance().economy;
	}

	//========================================== LEGACY ==========================================
	@Override
	public List<Config> getExtraConfig() {
		return null;
	}

	String IS_SONGODA_DOWNLOAD = "%%__SONGODA__%%";
	String SONGODA_NODE = "%%__SONGODA_NODE__%%";
	String TIMESTAMP = "%%__TIMESTAMP__%%";
	String USER = "%%__USER__%%";
	String USERNAME = "%%__USERNAME__%%";
	String RESOURCE = "%%__RESOURCE__%%";
	String NONCE = "%%__NONCE__%%";

	protected void shutdownDataManager(DataManagerAbstract dataManager) {
		// 3 minutes is overkill, but we just want to make sure
		shutdownDataManager(dataManager, 15, TimeUnit.MINUTES.toSeconds(3));
	}

	protected void shutdownDataManager(DataManagerAbstract dataManager, int reportInterval, long secondsUntilForceShutdown) {
		dataManager.shutdownTaskQueue();

		while (!dataManager.isTaskQueueTerminated() && secondsUntilForceShutdown > 0) {
			long secondsToWait = Math.min(reportInterval, secondsUntilForceShutdown);

			try {
				if (dataManager.waitForShutdown(secondsToWait, TimeUnit.SECONDS)) {
					break;
				}

				getLogger().info(String.format("A DataManager is currently working on %d tasks... " +
								"We are giving him another %d seconds until we forcefully shut him down " +
								"(continuing to report in %d second intervals)",
						dataManager.getTaskQueueSize(), secondsUntilForceShutdown, reportInterval));
			} catch (InterruptedException ignore) {
			} finally {
				secondsUntilForceShutdown -= secondsToWait;
			}
		}

		if (!dataManager.isTaskQueueTerminated()) {
			int unfinishedTasks = dataManager.forceShutdownTaskQueue().size();

			if (unfinishedTasks > 0) {
				getLogger().log(Level.WARNING,
						String.format("A DataManager has been forcefully terminated with %d unfinished tasks - " +
								"This can be a serious problem, please report it to us (Tweetzy)!", unfinishedTasks));
			}
		}
	}

	// helpers
	private void setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return;
		}

		final RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);

		if (rsp == null) {
			return;
		}

		this.economy = rsp.getProvider();
	}
}
