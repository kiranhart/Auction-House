package com.kiranhart.auctionhouse;

import com.kiranhart.auctionhouse.api.statics.AuctionSettings;
import com.kiranhart.auctionhouse.api.version.ServerVersion;
import com.kiranhart.auctionhouse.auction.AuctionItem;
import com.kiranhart.auctionhouse.cmds.CommandManager;
import com.kiranhart.auctionhouse.util.economy.Economy;
import com.kiranhart.auctionhouse.util.economy.VaultEconomy;
import com.kiranhart.auctionhouse.util.locale.Locale;
import com.kiranhart.auctionhouse.util.storage.ConfigWrapper;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.concurrent.CopyOnWriteArrayList;

public final class Core extends JavaPlugin {

    private static Core instance;

    private ConsoleCommandSender console = Bukkit.getConsoleSender();
    private ServerVersion serverVersion;

    private CommandManager commandManager;
    private Locale locale;
    private Economy economy;
    private AuctionSettings auctionSettings;

    private CopyOnWriteArrayList<AuctionItem> auctionItems;

    private ConfigWrapper data;
    private ConfigWrapper transactions;

    @Override
    public void onEnable() {

        long start = System.currentTimeMillis();
        PluginManager pm = Bukkit.getPluginManager();

        console.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6========================================="));
        console.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bLoading Auction House 1.10 - Multiversion"));
        console.sendMessage(ChatColor.translateAlternateColorCodes('&', " "));
        console.sendMessage(ChatColor.translateAlternateColorCodes('&', "&fThis plugin was designed by Kiran Hart any"));
        console.sendMessage(ChatColor.translateAlternateColorCodes('&', "&fproblems should be reported directly to him."));
        console.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6========================================="));

        console.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&6AuctionHouse&8]&a Initializing instance"));
        instance = this;
        console.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&6AuctionHouse&8]&a Checking server version"));
        serverVersion = ServerVersion.fromPackageName(Bukkit.getServer().getClass().getPackage().getName());

        if (isServerVersion(ServerVersion.NOT_SUPPORTED, ServerVersion.V1_7)) {
            console.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6==========================================="));
            console.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bDisabling Auction House 1.10 - Multiversion"));
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
        if (pm.isPluginEnabled("Vault")) {
            this.economy = new VaultEconomy();
        }

        console.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&6AuctionHouse&8]&a Loading data files"));
        initDataFiles();

        console.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&6AuctionHouse&8]&a Loading Settings"));
        auctionSettings = new AuctionSettings();
        auctionItems = new CopyOnWriteArrayList<>();

        console.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&6AuctionHouse&8]&a Setting up command system"));
        commandManager = new CommandManager();
        commandManager.initialize();

        console.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6========================================="));
        console.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bLoaded Auction House 1.10 - Multiversion"));
        console.sendMessage(ChatColor.translateAlternateColorCodes('&', " "));
        console.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aLoaded successfully in " + (System.currentTimeMillis() - start) + "ms"));
        console.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6========================================="));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
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

    public Locale getLocale() {
        return locale;
    }

    public AuctionSettings getAuctionSettings() {
        return auctionSettings;
    }

    public CopyOnWriteArrayList<AuctionItem> getAuctionItems() {
        return auctionItems;
    }

    public ConfigWrapper getData() {
        return data;
    }

    public ConfigWrapper getTransactions() {
        return transactions;
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

}

