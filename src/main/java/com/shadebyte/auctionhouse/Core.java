package com.shadebyte.auctionhouse;

import com.massivestats.MassiveStats;
import com.shadebyte.auctionhouse.api.AuctionAPI;
import com.shadebyte.auctionhouse.api.enums.Lang;
import com.shadebyte.auctionhouse.api.event.AuctionEndEvent;
import com.shadebyte.auctionhouse.auction.AuctionItem;
import com.shadebyte.auctionhouse.cmds.CommandManager;
import com.shadebyte.auctionhouse.events.AGUIListener;
import com.shadebyte.auctionhouse.util.ConfigWrapper;
import com.shadebyte.auctionhouse.util.Debugger;
import com.shadebyte.auctionhouse.util.Locale;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.HorseInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
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

    //Language system instance
    private Locale locale;

    //Storage
    public List<AuctionItem> auctionItems;

    //Timing
    private Long startTime;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&bStarted to load Auction Items from data file."));
        startTime = System.currentTimeMillis();

        setupEconomy();
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

        try {
            MassiveStats stats = new MassiveStats(this);
            stats.setListenerDisabled(false);
        } catch (Exception e) {
            Debugger.report(e);
        }

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
                        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&eLoaded Auction Item with key&f: &b" + item.getKey()));
                    }
                    data.saveConfig();
                });
                Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&eLoaded a total of &f: &b" + size + "&e items"));
            }
        } catch (Exception e) {
            Debugger.report(e);
        }

        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&bAuction House finished loading, took " + (System.currentTimeMillis() - startTime) + " ms"));

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
                                    data.getConfig().set("expired." + auctionItem.getOwner() + "." + System.currentTimeMillis() + System.nanoTime(), auctionItem.getItem());
                                    data.saveConfig();
                                    auctionItems.remove(auctionItem);
                                } else {
                                    Player highestBidder = Bukkit.getPlayer(UUID.fromString(auctionItem.getHighestBidder()));
                                    if (highestBidder != null) {
                                        if (getEconomy().getBalance(highestBidder) < auctionItem.getCurrentPrice()) {
                                            highestBidder.sendMessage(Core.getInstance().getSettings().getPrefix() + Core.getInstance().getLocale().getMessage(Lang.NOT_ENOUGH_MONEY.getNode()));
                                            data.getConfig().set("expired." + auctionItem.getOwner() + "." + System.currentTimeMillis() + System.nanoTime(), auctionItem.getItem());
                                        } else {
                                            highestBidder.sendMessage(Core.getInstance().getSettings().getPrefix() + Core.getInstance().getLocale().getMessage(Lang.AUCTION_BUY.getNode()).replace("{itemname}", auctionItem.getDisplayName()).replace("{price}", AuctionAPI.getInstance().friendlyNumber(auctionItem.getCurrentPrice())));
                                            if (AuctionAPI.getInstance().availableSlots(highestBidder.getInventory()) < 1)
                                                highestBidder.getWorld().dropItemNaturally(highestBidder.getLocation(), auctionItem.getItem());
                                            else
                                                highestBidder.getInventory().addItem(auctionItem.getItem());
                                        }
                                        data.saveConfig();
                                        auctionItems.remove(auctionItem);
                                    } else {
                                        if (getEconomy().getBalance(highestBidder) < auctionItem.getCurrentPrice()) {
                                            data.getConfig().set("expired." + auctionItem.getOwner() + "." + System.currentTimeMillis() + System.nanoTime(), auctionItem.getItem());
                                        } else {
                                            data.getConfig().set("expired." + auctionItem.getHighestBidder() + "." + System.currentTimeMillis() + System.nanoTime(), auctionItem.getItem());
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
            //Debugger.report(e);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

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
        if (!new File(this.getDataFolder(), "data.yml").exists()) {
            data.getConfig().createSection("active");
        }
        data.saveConfig();
    }

    private void initEvents() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new AGUIListener(), this);
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

    public static Economy getEconomy() {
        return economy;
    }

    public Settings getSettings() {
        return settings;
    }
}
