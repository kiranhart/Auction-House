package com.kiranhart.auctionhouse.api.statics;
/*
    The current file was created by Kiran Hart
    Date: August 04 2019
    Time: 1:43 PM
    
    Code within this class is not to be redistributed without proper permission.
*/

public class AuctionPermissions {

    //Base

    public static String BASE = "AuctionHouse";
    public static String ADMIN = BASE + ".admin";

    //Commands

    public static String RELOAD_COMMAND = BASE + ".cmd.reload";
    public static String HELP_COMMNAD = BASE + ".cmd.help";
    public static String SELL_COMMAND = BASE + ".cmd.sell";
    public static String EXPIRED_COMMAND = BASE + ".cmd.expired";
    public static String TRANSACTIONS_COMMAND = BASE + ".cmd.transactions";
    public static String UPLOAD_TRANSACTIONS_COMMAND = BASE + ".cmd.uploadtransactions";
    public static String LISTINGS_COMMAND = BASE + ".cmd.listings";
    public static String LOCK_COMMAND = BASE + ".cmd.lock";
    public static String ENDALL_COMMAND = BASE + ".cmd.endall";

    //Misc

    public static String MAX_AUCTIONS = BASE + ".maxauctions";
    public static String USE_RECEIPT = BASE + ".usereceipt";
}
