package com.kiranhart.auctionhouse.api.statics;
/*
    The current file was created by Kiran Hart
    Date: August 03 2019
    Time: 8:20 PM
    
    Code within this class is not to be redistributed without proper permission.
*/

import com.kiranhart.auctionhouse.Core;

public class AuctionSettings {

    public static int DEFAULT_AUCTION_TIME = 900;

    public static long MAX_AUCTION_PRICE = 1000000L;
    public static long MAX_AUCTION_START = 1000000L;
    public static long MAX_AUCTION_INCREMENT = 1000000L;
    public static long MIN_AUCTION_PRICE = 1L;
    public static long MIN_AUCTION_START = 1L;
    public static long MIN_AUCTION_INCREMENT = 1L;

    public static boolean OWNER_CAN_PURCHASE_OWN = false;
    public static boolean OWNER_CAN_BID_ON_OWN = false;
    public static boolean USE_BIDDING_SYSTEM = false;
    public static boolean AUTO_REFRESH_AUCTION_PAGES = true;
    public static boolean INCREASE_AUCTION_TIME_ON_BID = true;
    public static boolean USE_SHORT_NUMBERS_ON_ITEMS = false;

    public static int TIME_TO_INCREASE_BY_BID = 10;
    public static int DECREASE_SECONDS_BY_TICK = 5;
    public static int UPDATE_EVERY_TICK = 5;
    public static int AUTO_SAVE_EVERY = 1800;

    public static boolean DB_ENABLED = false;
    // public static boolean ATTEMPT_TO_CREATE_DB_TABLES_ON_START = true;
    public static String DB_HOST = "localhost";
    public static int DB_PORT = 3306;
    public static String DB_NAME = "auctionhouse";
    public static String DB_USERNAME = "root";
    public static String DB_PASSWORD = "";

    public AuctionSettings () {
        DEFAULT_AUCTION_TIME = Core.getInstance().getConfig().getInt("settings.default-auction-time");
        MAX_AUCTION_PRICE = Core.getInstance().getConfig().getLong("settings.max-auction-price");
        MAX_AUCTION_START = Core.getInstance().getConfig().getLong("settings.max-auction-start");
        MAX_AUCTION_INCREMENT = Core.getInstance().getConfig().getLong("settings.max-auction-increment");
        MIN_AUCTION_PRICE = Core.getInstance().getConfig().getLong("settings.min-auction-price");
        MIN_AUCTION_START = Core.getInstance().getConfig().getLong("settings.min-auction-start");
        MIN_AUCTION_INCREMENT = Core.getInstance().getConfig().getLong("settings.min-auction-increment");
        
        OWNER_CAN_PURCHASE_OWN = Core.getInstance().getConfig().getBoolean("settings.owner-can-purchase-own");
        OWNER_CAN_BID_ON_OWN = Core.getInstance().getConfig().getBoolean("settings.owner-can-bid-on-own");
        USE_BIDDING_SYSTEM = Core.getInstance().getConfig().getBoolean("settings.use-bidding-system");
        AUTO_REFRESH_AUCTION_PAGES = Core.getInstance().getConfig().getBoolean("settings.auto-refresh-auction-pages");
        INCREASE_AUCTION_TIME_ON_BID = Core.getInstance().getConfig().getBoolean("settings.increase-time-on-bid");
        USE_SHORT_NUMBERS_ON_ITEMS = Core.getInstance().getConfig().getBoolean("settings.use-short-numbers-on-items");

        TIME_TO_INCREASE_BY_BID = Core.getInstance().getConfig().getInt("settings.time-to-increase-by-bid");
        DECREASE_SECONDS_BY_TICK = Core.getInstance().getConfig().getInt("settings.decrease-seconds-by-tick");
        UPDATE_EVERY_TICK = Core.getInstance().getConfig().getInt("settings.update-every-tick");
        AUTO_SAVE_EVERY = Core.getInstance().getConfig().getInt("settings.auto-save-every");

        DB_ENABLED = Core.getInstance().getConfig().getBoolean("database.enabled");
        //  ATTEMPT_TO_CREATE_DB_TABLES_ON_START = Core.getInstance().getConfig().getBoolean("database.attempt-table-creation-on-start");
        DB_HOST = Core.getInstance().getConfig().getString("database.host");
        DB_PORT = Core.getInstance().getConfig().getInt("database.port");
        DB_NAME = Core.getInstance().getConfig().getString("database.name");
        DB_USERNAME = Core.getInstance().getConfig().getString("database.username");
        DB_PASSWORD = Core.getInstance().getConfig().getString("database.password");
    }
}
