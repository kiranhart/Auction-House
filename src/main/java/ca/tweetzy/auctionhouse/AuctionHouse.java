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

import ca.tweetzy.auctionhouse.database.DataManager;
import ca.tweetzy.auctionhouse.database.migrations.*;
import ca.tweetzy.auctionhouse.helpers.UpdateChecker;
import ca.tweetzy.auctionhouse.managers.*;
import ca.tweetzy.auctionhouse.settings.v3.Settings;
import ca.tweetzy.auctionhouse.settings.v3.Translations;
import ca.tweetzy.flight.FlightPlugin;
import ca.tweetzy.flight.database.DataMigrationManager;
import ca.tweetzy.flight.database.DatabaseConnector;
import ca.tweetzy.flight.database.SQLiteConnector;
import ca.tweetzy.flight.utils.Common;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;
import lombok.Getter;
import lombok.Setter;


/**
 * The current file has been created by Kiran Hart
 * Date Created: January 12 2021
 * Time Created: 6:30 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */

public class AuctionHouse extends FlightPlugin {

	//==========================================================================
	private DatabaseConnector databaseConnector;
	private DataManager dataManager;

	private static TaskChainFactory taskChainFactory;
	private static AuctionHouse instance;
//	private PluginHook ultraEconomyHook;

	@Getter
	@Setter
	private boolean migrating = false;

//	@Getter
//	private final GuiManager guiManager = new GuiManager(this);
//
//	protected Metrics metrics;
//
//	@Getter
//	private CommandManager commandManager;


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
	private AuctionStatisticManager auctionStatisticManager;

	@Getter
	private MinItemPriceManager minItemPriceManager;

	@Getter
	private PaymentsManager paymentsManager;

	@Getter
	private UpdateChecker.UpdateStatus status;

	@Override
	protected void onFlight() {
		Settings.init();
		Translations.init();

		Common.setPrefix(Settings.PREFIX.getStringOr("&8[&EAuctionHouse&8]"));

		// Set up the database if enabled
		this.databaseConnector = new SQLiteConnector(this);
		this.dataManager = new DataManager(this.databaseConnector, this, null);

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
				new _21_RequestsDynAmtMigration()
		);

		dataMigrationManager.runMigrations();
	}

//	@Override
//	public void onPluginEnable() {
//		TweetyCore.registerPlugin(this, 1, "CHEST");
//
//		// Check server version
//		if (ServerVersion.isServerVersionAtOrBelow(ServerVersion.V1_7)) {
//			getServer().getPluginManager().disablePlugin(this);
//			return;
//		}
//
//		taskChainFactory = BukkitTaskChainFactory.create(this);
//
//		// Settings
//		Settings.setup();
//
//		if (Settings.AUTO_BSTATS.getBoolean()) {
//			final File file = new File("plugins" + File.separator + "bStats" + File.separator + "config.yml");
//			if (file.exists()) {
//				final YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
//				configuration.set("enabled", true);
//				try {
//					configuration.save(file);
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//
//		this.ultraEconomyHook = PluginHook.addHook(Economy.class, "UltraEconomy", UltraEconomyHook.class);
//
//		// v3 translations & Settings
////		Translations.init();
//
//		// Load Economy
//		EconomyManager.load();
//
//		// local
////		setLocale(Settings.LANG.getString());
////		LocaleSettings.setup();
//
//		// Setup Economy
//		final String ECO_PLUGIN = Settings.ECONOMY_PLUGIN.getString();
//
//
//		if (ECO_PLUGIN.startsWith("UltraEconomy")) {
//			EconomyManager.getManager().setPreferredHook(this.ultraEconomyHook);
//		} else {
//			EconomyManager.getManager().setPreferredHook(ECO_PLUGIN);
//		}
//
//		if (!EconomyManager.getManager().isEnabled()) {
//			getLogger().severe("Could not find a valid economy provider for Auction House");
//			getServer().getPluginManager().disablePlugin(this);
//			return;
//		}
//
//		// listeners
//		Bukkit.getServer().getPluginManager().registerEvents(new PlayerListeners(), this);
//		Bukkit.getServer().getPluginManager().registerEvents(new MeteorClientListeners(), this);
//		Bukkit.getServer().getPluginManager().registerEvents(new AuctionListeners(), this);
//
//		if (getServer().getPluginManager().isPluginEnabled("ChestShop"))
//			Bukkit.getServer().getPluginManager().registerEvents(new ChestShopListener(), this);
//
//		if (getServer().getPluginManager().isPluginEnabled("CMI"))
//			Bukkit.getServer().getPluginManager().registerEvents(new CMIListener(), this);
//
//		// Setup the database if enabled
//		this.databaseConnector = Settings.DATABASE_USE.getBoolean() ? new MySQLConnector(
//				this,
//				Settings.DATABASE_HOST.getString(),
//				Settings.DATABASE_PORT.getInt(),
//				Settings.DATABASE_NAME.getString(),
//				Settings.DATABASE_USERNAME.getString(),
//				Settings.DATABASE_PASSWORD.getString(),
//				Settings.DATABASE_CUSTOM_PARAMS.getString().equalsIgnoreCase("None") ? "" : Settings.DATABASE_CUSTOM_PARAMS.getString()
//		) : new SQLiteConnector(this);
//
//		// Use a custom table prefix if using a remote database. The default prefix setting acts exactly like if the prefix is null
//		final String tablePrefix = Settings.DATABASE_USE.getBoolean() ? Settings.DATABASE_TABLE_PREFIX.getString() : null;
//		this.dataManager = new DataManager(this.databaseConnector, this, tablePrefix);
//
//		DataMigrationManager dataMigrationManager = new DataMigrationManager(this.databaseConnector, this.dataManager,
//				new _1_InitialMigration(),
//				new _2_FilterWhitelistMigration(),
//				new _3_BansMigration(),
//				new _4_ItemsChangeMigration(),
//				new _5_TransactionChangeMigration(),
//				new _6_BigIntMigration(),
//				new _7_TransactionBigIntMigration(),
//				new _8_ItemPerWorldMigration(),
//				new _9_StatsMigration(),
//				new _10_InfiniteItemsMigration(),
//				new _11_AdminLogMigration(),
//				new _12_SerializeFormatDropMigration(),
//				new _13_MinItemPriceMigration(),
//				new _14_PartialQtyBuyMigration(),
//				new _15_AuctionPlayerMigration(),
//				new _16_StatisticVersionTwoMigration(),
//				new _17_PaymentsMigration(),
//				new _18_PaymentsItemMigration(),
//				new _19_ServerAuctionMigration(),
//				new _20_AuctionRequestsMigration(),
//				new _21_RequestsDynAmtMigration()
//		);
//
//		dataMigrationManager.runMigrations();
//
//		// load auction items
//		this.auctionItemManager = new AuctionItemManager();
//		this.auctionItemManager.start();
//
//		// load transactions
//		this.transactionManager = new TransactionManager();
//		this.transactionManager.loadTransactions();
//
//		// load the filter whitelist items
//		this.filterManager = new FilterManager();
//		this.filterManager.loadItems();
//
//		// load the bans
//		this.auctionBanManager = new AuctionBanManager();
//		this.auctionBanManager.loadBans();
//
//		this.minItemPriceManager = new MinItemPriceManager();
//		this.minItemPriceManager.loadMinPrices();
//
//		this.auctionStatisticManager = new AuctionStatisticManager();
//		this.auctionStatisticManager.loadStatistics();
//
//		// auction players
//		this.auctionPlayerManager = new AuctionPlayerManager();
//		this.auctionPlayerManager.loadPlayers();
//
//		// payments
//		this.paymentsManager = new PaymentsManager();
//		this.paymentsManager.loadPayments();
//
//		// gui manager
//		this.guiManager.init();
//
//		// commands
//		this.commandManager = new CommandManager(this);
////		this.commandManager.setSyntaxErrorMessage(TextUtils.formatText(getLocale().getMessage("commands.invalid_syntax").getMessage().split("\n")));
////		this.commandManager.setNoPermsMessage(TextUtils.formatText(getLocale().getMessage("commands.no_permission").getMessage()));
//		this.commandManager.addCommand(new CommandAuctionHouse()).addSubCommands(
//				new CommandSell(),
//				new CommandActive(),
//				new CommandExpired(),
//				new CommandTransactions(),
//				new CommandSearch(),
//				new CommandSettings(),
//				new CommandToggleListInfo(),
//				new CommandMigrate(),
//				new CommandReload(),
//				new CommandFilter(),
//				new CommandAdmin(),
//				new CommandBan(),
//				new CommandUnban(),
//				new CommandMarkChest(),
//				new CommandUpload(),
//				new CommandMinPrice(),
//				new CommandStats(),
//				new CommandPayments(),
//				new CommandBids(),
//				new CommandConfirm(),
//				new CommandRequest()
//		);
//
//		// Placeholder API
//		if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
//			new PlaceholderAPIHook(this).register();
//		}
//
//		// start the auction tick task
//		TickAuctionsTask.startTask();
//
//		// auto save task
//		if (Settings.AUTO_SAVE_ENABLED.getBoolean()) {
//			AutoSaveTask.startTask();
//		}
//
//		// update check
////		if (Settings.UPDATE_CHECKER.getBoolean() && ServerProject.getServerVersion() != ServerProject.UNKNOWN)
////			getServer().getScheduler().runTaskLaterAsynchronously(this, () -> this.status = new UpdateChecker(this, 60325, getConsole()).check().getStatus(), 1L);
//
//		// metrics
//		this.metrics = new Metrics(this, 6806);
//		this.metrics.addCustomChart(new Metrics.SimplePie("using_mysql", () -> String.valueOf(Settings.DATABASE_USE.getBoolean())));
//
//		getServer().getScheduler().runTaskLater(this, () -> {
//			if (!ServerProject.isServer(ServerProject.SPIGOT, ServerProject.PAPER)) {
//				getLogger().severe("You're running Auction House on a non-supported Jar");
//				getLogger().severe("You will not receive any support while using a non-supported jar, support jars: Spigot or Paper");
//			}
//
//			if (ServerVersion.isServerVersionBelow(ServerVersion.V1_16)) {
//				getLogger().severe("You are receiving this message because you're running Auction House on a Minecraft version older than 1.16. As a heads up, Auction House 3.0 is going to be for 1.16+ only");
//			}
//
//			if (!ServerProject.isServer(ServerProject.PAPER, ServerProject.SPIGOT)) {
//				getLogger().warning("You're running Auction House on a non supported server jar, although small, there's a chance somethings will not work or just entirely break.");
//			}
//
//			final String uIDPartOne = "%%__US";
//			final String uIDPartTwo = "ER__%%";
//
//			if (USER.contains(uIDPartOne) && USER.contains(uIDPartTwo)) {
//				getLogger().severe("Could not detect user ID, are you running a cracked / self-compiled copy of auction house?");
//			} else {
////				getConsole().sendMessage(TextUtils.formatText("&e&m--------------------------------------------------------"));
////				getConsole().sendMessage(TextUtils.formatText(""));
////				getConsole().sendMessage(TextUtils.formatText("&aThank you for purchasing Auction House, it means a lot"));
////				getConsole().sendMessage(TextUtils.formatText("&7 - Kiran Hart"));
////				getConsole().sendMessage(TextUtils.formatText(""));
////				getConsole().sendMessage(TextUtils.formatText("&e&m--------------------------------------------------------"));
//			}
//		}, 1L);
//	}

//	@Override
//	public void onPluginDisable() {
//		if (this.dataManager != null) {
//			// clean up the garbage items
//			AuctionHouse.getInstance().getDataManager().deleteItems(AuctionHouse.getInstance().getAuctionItemManager().getDeletedItems().values().stream().map(AuctionedItem::getId).collect(Collectors.toList()));
//
//			this.auctionItemManager.end();
//			this.filterManager.saveFilterWhitelist(false);
//			this.auctionBanManager.saveBans(false);
//			this.dataManager.close();
//		}
//
//		getServer().getScheduler().cancelTasks(this);
//	}

//	@Override
//	public void onConfigReload() {
//		EconomyManager.load();
//		Settings.setup();
//		EconomyManager.getManager().setPreferredHook(Settings.ECONOMY_PLUGIN.getString());
//		setLocale(Settings.LANG.getString());
//		LocaleSettings.setup();
//		this.commandManager.setSyntaxErrorMessage(TextUtils.formatText(getLocale().getMessage("commands.invalid_syntax").getMessage().split("\n")));
//		this.commandManager.setNoPermsMessage(TextUtils.formatText(getLocale().getMessage("commands.no_permission").getMessage()));
//	}

//	@Override
//	public List<Config> getExtraConfig() {
//		return null;
//	}

	public static AuctionHouse getInstance() {
		return (AuctionHouse) FlightPlugin.getInstance();
	}

	public static DatabaseConnector getDatabaseConnector() {
		return getInstance().databaseConnector;
	}

	public static DataManager getDataManager() {
		return getInstance().dataManager;
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
