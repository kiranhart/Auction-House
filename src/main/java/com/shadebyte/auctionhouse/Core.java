package com.shadebyte.auctionhouse;

import com.massivestats.MassiveStats;
import com.shadebyte.auctionhouse.api.AuctionAPI;
import com.shadebyte.auctionhouse.api.enums.Lang;
import com.shadebyte.auctionhouse.api.event.AuctionEndEvent;
import com.shadebyte.auctionhouse.api.event.AuctionStartEvent;
import com.shadebyte.auctionhouse.api.event.TransactionCompleteEvent;
import com.shadebyte.auctionhouse.auction.AuctionItem;
import com.shadebyte.auctionhouse.auction.Transaction;
import com.shadebyte.auctionhouse.cmds.CommandManager;
import com.shadebyte.auctionhouse.events.AGUIListener;
import com.shadebyte.auctionhouse.events.TransactionListener;
import com.shadebyte.auctionhouse.util.Debugger;
import com.shadebyte.auctionhouse.util.Locale;
import com.shadebyte.auctionhouse.util.storage.ConfigWrapper;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public final class Core extends JavaPlugin {

    //Instance Variable
    private static Core instance;

    //Instance for the command management system
    private CommandManager commandManager;

    //Economy
    private static Economy economy = null;

    //Settings
    private Settings settings = null;

    //Data config instance from the config wrapper
    private ConfigWrapper data;
    private ConfigWrapper transactions;

    //Language system instance
    private Locale locale;

    //Storage
    public List<AuctionItem> auctionItems;

    //Database
    private Connection connection;
    public String host, database, username, password;
    public int port;
    public boolean dbConnected;

    //Timing
    private Long startTime;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        dbConnected = false;

        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&bStarted to load Auction Items from data file."));
        startTime = System.currentTimeMillis();

        if (!setupEconomy()) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cVault could not be loaded/was found. Disabling Auction House!"));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        initDataFiles();

        //Locales
        Locale.init(this);
        Locale.saveDefaultLocale("en_US");
        this.locale = Locale.getLocale(this.getConfig().getString("Locale", getConfig().getString("lang")));

        settings = new Settings();

        commandManager = new CommandManager();
        commandManager.initialize();

        initEvents();
        initStorage();

        if (getConfig().getBoolean("database.enabled")) mysqlSetup();

        try {
            MassiveStats stats = new MassiveStats(this);
            stats.setListenerDisabled(false);
        } catch (Exception e) {
            Debugger.report(e);
        }

        loadAuctions();
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&bAuction House finished loading, took " + (System.currentTimeMillis() - startTime) + " ms"));
        tickAuctions();
    }

    @Override
    public void onDisable() {
        saveAuctions();
    }


    private void mysqlSetup() {
        host = this.getConfig().getString("database.host");
        port = this.getConfig().getInt("database.port");
        database = this.getConfig().getString("database.database");
        username = this.getConfig().getString("database.username");
        password = this.getConfig().getString("database.password");

        try {

            synchronized (this) {
                if (getConnection() != null && !getConnection().isClosed()) {
                    return;
                }

                Class.forName("com.mysql.jdbc.Driver");
                setConnection(DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, this.username, this.password));
                Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSuccessfully Connected to MySQL"));
                dbConnected = true;
            }
        } catch (SQLException e) {
            Debugger.report(e);
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cCould not connect to MySQL"));
        } catch (ClassNotFoundException e) {
            Debugger.report(e);
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cCould not connect to MySQL"));
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    private void loadAuctions() {
        try {
            ConfigurationSection section = data.getConfig().getConfigurationSection("active");
            if (section.getKeys(false).size() != 0) {
                int size = section.getKeys(false).size();
                Bukkit.getServer().getScheduler().runTaskAsynchronously(this, () -> {
                    for (String node : section.getKeys(false)) {
                        int xNode = Integer.parseInt(node);
                        String owner = data.getConfig().getString("active." + xNode + ".owner");
                        ItemStack stack = data.getConfig().getItemStack("active." + xNode + ".item");
                        int startPrice = data.getConfig().getInt("active." + xNode + ".startprice");
                        int bidIncrement = data.getConfig().getInt("active." + xNode + ".bidincrement");
                        int buyNowPrice = data.getConfig().getInt("active." + xNode + ".buynowprice");
                        int currentPrice = data.getConfig().getInt("active." + xNode + ".currentprice");
                        int time = data.getConfig().getInt("active." + xNode + ".time");
                        String key = data.getConfig().getString("active." + xNode + ".key");
                        String highestBidder = data.getConfig().getString("active." + xNode + ".highestbidder");

                        AuctionItem item = new AuctionItem(owner, highestBidder, stack, startPrice, bidIncrement, buyNowPrice, currentPrice, time, key);
                        auctionItems.add(item);
                        data.getConfig().set("active." + xNode, null);
                        AuctionStartEvent auctionStartEvent = new AuctionStartEvent(item);
                        getServer().getPluginManager().callEvent(auctionStartEvent);
                        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&eLoaded Auction Item with key&f: &b" + item.getKey()));
                    }
                    data.saveConfig();
                });
                Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&eLoaded a total of &f: &b" + size + "&e items"));
            }
        } catch (Exception e) {
            Debugger.report(e);
        }
    }

    private void tickAuctions() {
        try {
            Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
                if (auctionItems.size() != 0) {
                    auctionItems.forEach(auctionItem -> auctionItem.updateTime(5));
                    for (AuctionItem auctionItem : auctionItems) {
                        if (auctionItem.getTime() <= 0) {
                            AuctionEndEvent auctionEndEvent = new AuctionEndEvent(auctionItem);
                            getServer().getPluginManager().callEvent(auctionEndEvent);
                            if (!auctionEndEvent.isCancelled()) {
                                if (auctionItem.getHighestBidder().equalsIgnoreCase(auctionItem.getOwner())) {
                                    data.getConfig().set("expired." + auctionItem.getOwner() + "." + auctionItem.getKey() + ".item", auctionItem.getItem());
                                    data.getConfig().set("expired." + auctionItem.getOwner() + "." + auctionItem.getKey() + ".display", AuctionAPI.getInstance().expiredAuctionItem(auctionItem));
                                    data.saveConfig();
                                    auctionItems.remove(auctionItem);
                                } else {
                                    Player highestBidder = Bukkit.getPlayer(UUID.fromString(auctionItem.getHighestBidder()));
                                    if (highestBidder != null) {
                                        if (getEconomy().getBalance(highestBidder) < auctionItem.getCurrentPrice()) {
                                            highestBidder.sendMessage(Core.getInstance().getSettings().getPrefix() + Core.getInstance().getLocale().getMessage(Lang.NOT_ENOUGH_MONEY.getNode()));
                                            data.getConfig().set("expired." + auctionItem.getOwner() + "." + auctionItem.getKey() + ".item", auctionItem.getItem());
                                            data.getConfig().set("expired." + auctionItem.getOwner() + "." + auctionItem.getKey() + ".display", AuctionAPI.getInstance().expiredAuctionItem(auctionItem));
                                        } else {
                                            highestBidder.sendMessage(Core.getInstance().getSettings().getPrefix() + Core.getInstance().getLocale().getMessage(Lang.AUCTION_BUY.getNode()).replace("{itemname}", auctionItem.getDisplayName()).replace("{price}", AuctionAPI.getInstance().friendlyNumber(auctionItem.getCurrentPrice())));
                                            if (AuctionAPI.getInstance().availableSlots(highestBidder.getInventory()) < 1)
                                                highestBidder.getWorld().dropItemNaturally(highestBidder.getLocation(), auctionItem.getItem());
                                            else
                                                highestBidder.getInventory().addItem(auctionItem.getItem());
                                            Transaction transaction = new Transaction(Transaction.TransactionType.AUCTION_WON, auctionItem, highestBidder.getUniqueId().toString(), System.currentTimeMillis());
                                            transaction.saveTransaction();
                                            getServer().getPluginManager().callEvent(new TransactionCompleteEvent(transaction));
                                        }
                                        data.saveConfig();
                                        auctionItems.remove(auctionItem);
                                    } else {
                                        if (getEconomy().getBalance(highestBidder) < auctionItem.getCurrentPrice()) {
                                            data.getConfig().set("expired." + auctionItem.getOwner() + "." + auctionItem.getKey() + ".item", auctionItem.getItem());
                                            data.getConfig().set("expired." + auctionItem.getOwner() + "." + auctionItem.getKey() + ".display", AuctionAPI.getInstance().expiredAuctionItem(auctionItem));
                                        } else {
                                            data.getConfig().set("expired." + auctionItem.getHighestBidder() + "." + auctionItem.getKey() + ".item", auctionItem.getItem());
                                            data.getConfig().set("expired." + auctionItem.getHighestBidder() + "." + auctionItem.getKey() + ".display", AuctionAPI.getInstance().expiredAuctionItem(auctionItem));
                                            Transaction transaction = new Transaction(Transaction.TransactionType.AUCTION_WON, auctionItem, highestBidder.getUniqueId().toString(), System.currentTimeMillis());
                                            transaction.saveTransaction();
                                            getServer().getPluginManager().callEvent(new TransactionCompleteEvent(transaction));
                                        }
                                        data.saveConfig();
                                        auctionItems.remove(auctionItem);
                                    }
                                }
                            }
                        }
                    }
                }
            }, 0, 20 * 5);
        } catch (Exception e) {
        }
    }

    private void saveAuctions() {
        //Save Auctions to file.
        int node = 1;
        for (AuctionItem auctionItem : auctionItems) {
            data.getConfig().set("active." + node + ".owner", auctionItem.getOwner());
            data.getConfig().set("active." + node + ".highestbidder", auctionItem.getHighestBidder());
            data.getConfig().set("active." + node + ".startprice", auctionItem.getStartPrice());
            data.getConfig().set("active." + node + ".bidincrement", auctionItem.getBidIncrement());
            data.getConfig().set("active." + node + ".currentprice", auctionItem.getCurrentPrice());
            data.getConfig().set("active." + node + ".buynowprice", auctionItem.getBuyNowPrice());
            data.getConfig().set("active." + node + ".key", auctionItem.getKey());
            data.getConfig().set("active." + node + ".time", auctionItem.getTime());
            data.getConfig().set("active." + node + ".item", auctionItem.getItem());
            node++;
        }
        data.saveConfig();
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
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

    private void initEvents() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new AGUIListener(), this);
        pm.registerEvents(new TransactionListener(), this);
    }

    private void initStorage() {
        auctionItems = new CopyOnWriteArrayList<>();
    }

    public static Core getInstance() {
        return instance;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public Locale getLocale() {
        return locale;
    }

    public ConfigWrapper getData() {
        return data;
    }

    public ConfigWrapper getTransactions() {
        return transactions;
    }

    public static Economy getEconomy() {
        return economy;
    }

    public Settings getSettings() {
        return settings;
    }
}
