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


import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.auctionhouse.api.AuctionHouseAPI;
import ca.tweetzy.auctionhouse.auction.AuctionedItem;
import ca.tweetzy.auctionhouse.commands.*;
import ca.tweetzy.auctionhouse.database.DataManager;
import ca.tweetzy.auctionhouse.database.migrations.*;
import ca.tweetzy.auctionhouse.database.migrations.v2.*;
import ca.tweetzy.auctionhouse.helpers.UpdateChecker;
import ca.tweetzy.auctionhouse.hooks.PlaceholderAPIHook;
import ca.tweetzy.auctionhouse.impl.AuctionAPI;
import ca.tweetzy.auctionhouse.listeners.*;
import ca.tweetzy.auctionhouse.managers.*;
import ca.tweetzy.auctionhouse.model.manager.*;
import ca.tweetzy.auctionhouse.model.TransactionLogger;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.auctionhouse.settings.Translations;
import ca.tweetzy.auctionhouse.tasks.AutoSaveTask;
import ca.tweetzy.auctionhouse.tasks.TickAuctionsTask;
import ca.tweetzy.flight.FlightPlugin;
import ca.tweetzy.flight.Metrics;
import ca.tweetzy.flight.comp.enums.ServerProject;
import ca.tweetzy.flight.command.CommandManager;
import ca.tweetzy.flight.comp.enums.ServerVersion;
import ca.tweetzy.flight.database.*;
import ca.tweetzy.flight.gui.GuiManager;
import ca.tweetzy.flight.utils.Common;
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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


/**
 * The current file has been created by Kiran Hart
 * Date Created: January 12 2021
 * Time Created: 6:30 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */

public class AuctionHouse extends FlightPlugin {

	//==========================================================================//
	// Debug toggle for development/research
	@Getter
	@Setter
	private static boolean debugMode = false;

	private DatabaseConnector databaseConnector;
	private DataManager dataManager;

	private final CurrencyManager currencyManager = new CurrencyManager();
	private final CommandManager commandManager = new CommandManager(this);
	private final ListingManager listingManager = new ListingManager();
	private final CategoryManager categoryManager = new CategoryManager();
	private final PriceLimitManager priceLimitManager = new PriceLimitManager();
	private final RequestsManager requestsManager = new RequestsManager();
	private final CartManager cartManager = new CartManager();
	private CooldownManager cooldownManager;

	private final GuiManager guiManager = new GuiManager(this);
	private final AuctionPlayerManager auctionPlayerManager = new AuctionPlayerManager();
	private final AuctionItemManager auctionItemManager = new AuctionItemManager();
	private final TransactionManager transactionManager = new TransactionManager();
	private final FilterManager filterManager = new FilterManager();
	private final BanManager banManager = new BanManager();
	private final AuctionStatisticManager auctionStatisticManager = new AuctionStatisticManager();
	private final PaymentsManager paymentsManager = new PaymentsManager();
	private final WatchlistManager watchlistManager = new WatchlistManager();
	private final NotificationManager notificationManager = new NotificationManager();

	private TransactionLogger transactionLogger;

	private AuctionHouseAPI API;

	// the default vault economy
	private Economy economy = null;


	//==========================================================================//

	private static TaskChainFactory taskChainFactory;

	@Getter
	@Setter
	private boolean migrating = false;

	@Getter
	private UpdateChecker.UpdateStatus status;

	@Override
	protected void onFlight() {
		if (ServerVersion.isServerVersionAtOrBelow(ServerVersion.V1_7)) {
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		API = new AuctionAPI();
		taskChainFactory = BukkitTaskChainFactory.create(this);

		Settings.init();
		initializeBStats();

		Translations.init();
		Common.setPrefix(Common.colorize(TranslationManager.string(Translations.GENERAL_PREFIX)));
		Common.setPluginName(Common.colorize(TranslationManager.string(Translations.GENERAL_PLUGIN_NAME)));

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
				new _24_RemainingItemToNBTSerializationMigration(),
				new _25_BidHistoryMigration(),
				new _26_MultiSerAndCurrencyMigration(),
				new _27_FixMigration25to26Migration(),
				new _28_PriorityListingMigration(),
				new _29_PaymentMultiCurrencyMigration(),
				new _30_MinMaxItemPriceMigration(),
				new _31_RequestTransactionMigration(),
				new _32_CreatedAtMigration(),
				new _33_WatchlistMigration(),
				new _34_OfflineNotificationsMigration()
		);

		dataMigrationManager.runMigrations();

		if (!setupEconomy()) {
			Bukkit.getServer().getConsoleSender().sendMessage(Common.colorize("&7[&eAuctionHouse&7] &f- &cCould not setup vault, please make sure you have an economy plugin."));
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		this.guiManager.init();
		this.banManager.load();
		this.currencyManager.load();
		this.paymentsManager.load();
		this.priceLimitManager.load();
		this.requestsManager.load();
		this.cartManager.load();
		this.cooldownManager = new CooldownManager(this);

		if (Settings.TRANSACTION_LOGGING_ENABLED.getBoolean()) {
			this.transactionLogger = new TransactionLogger(this);
			this.transactionLogger.start();
			Common.log("&aTransaction logging enabled - logs stored in plugins/AuctionHouse/logs/");
		}

		Bukkit.getServer().getPluginManager().registerEvents(new PlayerListeners(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new MeteorClientListeners(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new AuctionListeners(), this);

		if (getServer().getPluginManager().isPluginEnabled("ChestShop"))
			Bukkit.getServer().getPluginManager().registerEvents(new ChestShopListener(), this);

		if (getServer().getPluginManager().isPluginEnabled("CMI") && Settings.PREVENT_SALE_OF_REPAIRED_ITEMS.getBoolean())
			Bukkit.getServer().getPluginManager().registerEvents(new CMIListener(), this);

		this.auctionItemManager.start();
		this.transactionManager.loadTransactions();
		this.filterManager.loadItems();
		this.auctionStatisticManager.loadStatistics();
		this.auctionPlayerManager.loadPlayers();
		this.watchlistManager.load();

		this.commandManager.setSyntaxErrorMessages(TranslationManager.list(Translations.COMMAND_INFO_ERROR_INFORMATION));

		this.commandManager.registerCommandDynamically(new CommandAuctionHouse()).addSubCommands(
				new CommandSell(),
				new CommandActive(),
				new CommandExpired(),
				new CommandWatchlist(),
				new CommandCart(),
				new CommandTransactions(),
				new CommandSearch(),
				new CommandSettings(),
				new CommandToggleListInfo(),
				new CommandMigrate(),
				new CommandReload(),
				new CommandInfo(),
				new CommandFilter(),
				new CommandAdmin(),
				new CommandBan(),
				new CommandUnban(),
				new CommandMarkChest(),
				new CommandUpload(),
				new CommandPriceLimit(),
				new CommandStats(),
				new CommandPayments(),
				new CommandBids(),
				new CommandConfirm(),
				new CommandRequest(),
				new CommandPop(),
				new CommandDebug()
		);

		final Plugin papi = Bukkit.getPluginManager().getPlugin("PlaceholderAPI");
		if (papi != null && papi.isEnabled())
			new PlaceholderAPIHook(this).register();

		TickAuctionsTask.startTask();

		if (Settings.AUTO_SAVE_ENABLED.getBoolean()) {
			AutoSaveTask.startTask();
		}

		if (Settings.UPDATE_CHECKER.getBoolean() && ServerProject.getServerVersion() != ServerProject.UNKNOWN)
			getServer().getScheduler().runTaskLaterAsynchronously(this, () -> this.status = new UpdateChecker(this, 60325, Bukkit.getConsoleSender()).check().getStatus(), 1L);

		getServer().getScheduler().runTaskLater(this, () -> {
			if (!ServerProject.isServer(ServerProject.SPIGOT, ServerProject.PAPER)) {
				getLogger().warning("You're running Auction House on a non supported server jar, although small, there's a chance somethings will not work or just entirely break.");
			}

			final String uIDPartOne = "%%__US";
			final String uIDPartTwo = "ER__%%";

			if (USER.contains(uIDPartOne) && USER.contains(uIDPartTwo)) {
				getLogger().severe("Could not detect user ID, are you running a cracked / self-compiled copy of auction house?");
			} else {
				if (!Settings.HIDE_THANKYOU.getBoolean()) {
					Bukkit.getConsoleSender().sendMessage(Common.colorize("&e&m--------------------------------------------------------"));
					Bukkit.getConsoleSender().sendMessage(Common.colorize(""));
					Bukkit.getConsoleSender().sendMessage(Common.colorize("&aThank you for purchasing Auction House, it means a lot"));
					Bukkit.getConsoleSender().sendMessage(Common.colorize("&7 - Kiran Hart"));
					Bukkit.getConsoleSender().sendMessage(Common.colorize(""));
					Bukkit.getConsoleSender().sendMessage(Common.colorize("&e&m--------------------------------------------------------"));
				}
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
	protected int getBStatsId() {
		return 6806;
	}

	@Override
	protected List<Metrics.CustomChart> getCustomMetricCharts() {
		return Collections.singletonList(new Metrics.SimplePie("using_mysql", () -> String.valueOf(Settings.DATABASE_USE.getBoolean())));
	}

	@Override
	protected void onSleep() {
		if (this.transactionLogger != null) {
			this.transactionLogger.stop();
		}

		if (this.dataManager != null) {
			this.dataManager.deleteItems(this.auctionItemManager.getDeletedItems().values().stream().map(AuctionedItem::getId).collect(Collectors.toList()));

			this.auctionItemManager.end();
			this.filterManager.saveFilterWhitelist(false);

			shutdownDataManager(this.dataManager, 3, 15);
		}

		getServer().getScheduler().cancelTasks(this);
	}

	public static <T> TaskChain<T> newChain() {
		return taskChainFactory.newChain();
	}

	public static <T> TaskChain<T> newSharedChain(String name) {
		return taskChainFactory.newSharedChain(name);
	}

	public static AuctionHouse getInstance() {
		return (AuctionHouse) FlightPlugin.getInstance();
	}

	public static AuctionHouseAPI getAPI() {
		return getInstance().API;
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

	public static CooldownManager getCooldownManager() {
		return getInstance().cooldownManager;
	}

	public static CommandManager getCommandManager() {
		return getInstance().commandManager;
	}

	public static AuctionPlayerManager getAuctionPlayerManager() {
		return getInstance().auctionPlayerManager;
	}

	public static RequestsManager getRequestsManager() {
		return getInstance().requestsManager;
	}

	public static CartManager getCartManager() {
		return getInstance().cartManager;
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

	public static PriceLimitManager getPriceLimitManager() {
		return getInstance().priceLimitManager;
	}

	public static PaymentsManager getPaymentsManager() {
		return getInstance().paymentsManager;
	}

	public static WatchlistManager getWatchlistManager() {
		return getInstance().watchlistManager;
	}

	public static NotificationManager getNotificationManager() {
		return getInstance().notificationManager;
	}

	public static CurrencyManager getCurrencyManager() {
		return getInstance().currencyManager;
	}

	public static ListingManager getListingManager() {
		return getInstance().listingManager;
	}

	public static CategoryManager getCategoryManager() {
		return getInstance().categoryManager;
	}

	public static TransactionLogger getTransactionLogger() {
		return getInstance().transactionLogger;
	}

	public static Economy getEconomy() {
		return getInstance().economy;
	}

	String USER = "%%__USER__%%";

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		this.economy = rsp.getProvider();
		return this.economy != null;
	}
}
