package com.kiranhart.auctionhouse;

import com.kiranhart.auctionhouse.api.statics.AuctionSettings;
import com.kiranhart.auctionhouse.api.version.HartUpdater;
import com.kiranhart.auctionhouse.api.version.ServerVersion;
import com.kiranhart.auctionhouse.auction.AuctionItem;
import com.kiranhart.auctionhouse.cmds.CommandManager;
import com.kiranhart.auctionhouse.inventory.AGUI;
import com.kiranhart.auctionhouse.listeners.AGUIListener;
import com.kiranhart.auctionhouse.util.Metrics;
import com.kiranhart.auctionhouse.util.economy.Economy;
import com.kiranhart.auctionhouse.util.economy.VaultEconomy;
import com.kiranhart.auctionhouse.util.locale.Locale;
import com.kiranhart.auctionhouse.util.storage.ConfigWrapper;
import com.kiranhart.auctionhouse.util.tasks.AutoSaveTask;
import com.kiranhart.auctionhouse.util.tasks.LoadAuctionsTask;
import com.kiranhart.auctionhouse.util.tasks.TickAuctionsTask;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class Core extends JavaPlugin {

    private static Core instance;

    private ConsoleCommandSender console = Bukkit.getConsoleSender();
    private ServerVersion serverVersion;
    private PluginManager pm;

    private AuctionSettings auctionSettings;
    private CommandManager commandManager;

    private Economy economy;
    private Locale locale;

    private ArrayList<AuctionItem> auctionItems;
    private Map<Player, Integer> currentAuctionPage;

    private ConfigWrapper transactions;
    private ConfigWrapper data;

    private HikariDataSource hikari;
    private boolean dbConnected;

    private boolean locked = false;
    private HartUpdater updater;
    private Metrics metrics;

    @Override
    public void onEnable() {

        long start = System.currentTimeMillis();
        pm = Bukkit.getPluginManager();

        console.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6========================================="));
        console.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bLoading Auction House 1.11 - Multiversion"));
        console.sendMessage(ChatColor.translateAlternateColorCodes('&', " "));
        console.sendMessage(ChatColor.translateAlternateColorCodes('&', "&fThis plugin was designed by Kiran Hart any"));
        console.sendMessage(ChatColor.translateAlternateColorCodes('&', "&fproblems should be reported directly to him."));
        console.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6========================================="));

        console.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&6AuctionHouse&8]&a Initializing instance"));
        instance = this;
        dbConnected = false;
        console.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&6AuctionHouse&8]&a Checking server version"));
        serverVersion = ServerVersion.fromPackageName(Bukkit.getServer().getClass().getPackage().getName());

        if (isServerVersion(ServerVersion.NOT_SUPPORTED, ServerVersion.V1_7)) {
            console.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6==========================================="));
            console.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bDisabling Auction House 1.11 - Multiversion"));
            console.sendMessage(ChatColor.translateAlternateColorCodes('&', " "));
            console.sendMessage(ChatColor.translateAlternateColorCodes('&', "&fServer Version is not supported!"));
            console.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6========================================="));
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        console.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&6AuctionHouse&8]&a Initializing language system"));
        new Locale(this, "en_US");
        this.locale = Locale.getLocale(getConfig().getString("lang"));

        //Economy
        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            economy = new VaultEconomy();
        } else {
            console.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&6AuctionHouse&8]&c COULD NOT FIND VAULT, DISABLING PLUGIN"));
            getServer().getPluginManager().disablePlugin(this);
        }

        console.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&6AuctionHouse&8]&a Loading data files"));
        initDataFiles();

        console.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&6AuctionHouse&8]&a Loading Settings"));
        auctionSettings = new AuctionSettings();
        auctionItems = new ArrayList<>();

        console.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&6AuctionHouse&8]&a Setting up command system"));
        commandManager = new CommandManager();
        commandManager.initialize();

        //Database
        if (AuctionSettings.DB_ENABLED) {
            hikari = new HikariDataSource();
            hikari.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
            hikari.addDataSourceProperty("serverName", AuctionSettings.DB_HOST);
            hikari.addDataSourceProperty("port", AuctionSettings.DB_PORT);
            hikari.addDataSourceProperty("databaseName", AuctionSettings.DB_NAME);
            hikari.addDataSourceProperty("user", AuctionSettings.DB_USERNAME);
            hikari.addDataSourceProperty("password", AuctionSettings.DB_PASSWORD);

            if (!hikari.isClosed()) {
                dbConnected = true;
                console.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&6AuctionHouse&8]&a Connected to database!"));
            } else {
                console.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&6AuctionHouse&8]&c Failed to connect to database!"));
            }
        }

        //Listeners
        pm.registerEvents(new AGUIListener(), this);

        //Load the auctions
        LoadAuctionsTask.startTask(this);
        currentAuctionPage = new HashMap<>();

        console.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6========================================="));
        console.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bLoaded Auction House " + getDescription().getVersion() + " - Multiversion"));
        console.sendMessage(ChatColor.translateAlternateColorCodes('&', " "));
        console.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aLoaded successfully in " + (System.currentTimeMillis() - start) + "ms"));
        console.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6========================================="));

        //Begin Auction Tick
        TickAuctionsTask.startTask(this);

        //Begin Auction Auto Save
        AutoSaveTask.startTask(this);

        //Begin the update checker
        if (getConfig().getBoolean("update-checker")) {
            getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
                updater = new HartUpdater(this, getDescription().getVersion());
            }, 0, 20 * getConfig().getInt("update-delay"));
        }

        //Start Metrics
        if (getConfig().getBoolean("metrics")) {
            metrics = new Metrics(this, 6806);
        }
    }

    @Override
    public void onDisable() {
        //Close auction inventories (AGUI) for every online player
        Bukkit.getOnlinePlayers().forEach(p -> {
            if (p.getOpenInventory().getTopInventory().getHolder() instanceof AGUI) p.closeInventory();
        });

        //Save all of the auctions
        int index = 1;
        for (AuctionItem auctionItem : getAuctionItems()) {
            getData().getConfig().set("active." + index + ".owner", auctionItem.getOwner().toString());
            getData().getConfig().set("active." + index + ".highestbidder", auctionItem.getHighestBidder().toString());
            getData().getConfig().set("active." + index + ".startprice", auctionItem.getStartPrice());
            getData().getConfig().set("active." + index + ".bidincrement", auctionItem.getBidIncrement());
            getData().getConfig().set("active." + index + ".currentprice", auctionItem.getCurrentPrice());
            getData().getConfig().set("active." + index + ".buynowprice", auctionItem.getBuyNowPrice());
            getData().getConfig().set("active." + index + ".key", auctionItem.getKey());
            getData().getConfig().set("active." + index + ".time", auctionItem.getTime());
            getData().getConfig().set("active." + index + ".item", auctionItem.getItem());
            index++;
        }
        getData().saveConfig();

        if (hikari != null)
            hikari.close();
    }

    private void initDataFiles() {
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        data = new ConfigWrapper(this, "", "data.yml");
        transactions = new ConfigWrapper(this, "", "transactions.yml");
        if (!new File(this.getDataFolder(), "data.yml").exists()) {
            data.getConfig().createSection("active");
        }
        if (!new File(this.getDataFolder(), "transactions.yml").exists()) {
            transactions.getConfig().createSection("transactions");
        }
        data.saveConfig();
        transactions.saveConfig();
    }

    public static Core getInstance() {
        return instance;
    }

    public PluginManager getPm() {
        return pm;
    }

    public Locale getLocale() {
        return locale;
    }

    public AuctionSettings getAuctionSettings() {
        return auctionSettings;
    }

    public ArrayList<AuctionItem> getAuctionItems() {
        return auctionItems;
    }

    public ConfigWrapper getData() {
        return data;
    }

    public ConfigWrapper getTransactions() {
        return transactions;
    }

    public HikariDataSource getHikari() {
        return hikari;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public Economy getEconomy() {
        return economy;
    }

    public ServerVersion getServerVersion() {
        return serverVersion;
    }

    public boolean isServerVersion(ServerVersion version) {
        return serverVersion == version;
    }

    public boolean isServerVersion(ServerVersion... versions) {
        return ArrayUtils.contains(versions, serverVersion);
    }

    public boolean isServerVersionAtLeast(ServerVersion version) {
        return serverVersion.ordinal() >= version.ordinal();
    }

    public boolean isDbConnected() {
        return dbConnected;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public Map<Player, Integer> getCurrentAuctionPage() {
        return currentAuctionPage;
    }
}

