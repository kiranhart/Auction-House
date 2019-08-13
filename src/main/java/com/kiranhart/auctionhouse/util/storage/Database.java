package com.kiranhart.auctionhouse.util.storage;
/*
    The current file was created by Kiran Hart
    Date: August 12 2019
    Time: 4:17 PM
    
    Code within this class is not to be redistributed without proper permission.
*/

import com.kiranhart.auctionhouse.Core;
import com.kiranhart.auctionhouse.util.Debugger;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class Database {

    private static Database instance;

    private Database() {
    }

    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    public enum Tables {

        TRANSACTIONS("transactions"),
        ;

        private String tableName;

        Tables(String tableName) {
            this.tableName = tableName;
        }

        public String getTableName() {
            return tableName;
        }
    }

    public boolean performTableCreation(Tables... tables) {
        try {
            //Loop through each of the passed in tables
            for (Tables table : tables) {

                //Check if the table is TRANSACTIONS
                if (table == Tables.TRANSACTIONS) {
                    //Perform the table creation;
                    String query = "CREATE TABLE IF NOT EXISTS `transactions` ( `id` INT NOT NULL AUTO_INCREMENT , `transaction_type` TEXT NOT NULL , `seller` TEXT NOT NULL , `buyer` TEXT NOT NULL , `start_price` TEXT NOT NULL , `bid_increment` TEXT NOT NULL , `buy_now_price` TEXT NOT NULL , `final_price` TEXT NOT NULL , `time_left` INT NOT NULL , `auction_id` INT NOT NULL , `time_completed` TEXT NOT NULL , `item_type` TEXT NOT NULL , `item_name` TEXT NOT NULL , `item_lore` TEXT NOT NULL , `item_enchants` TEXT NOT NULL , PRIMARY KEY (`id`)) ENGINE = InnoDB";
                    Connection connection = Core.getInstance().getHikari().getConnection();
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.executeQuery();
                }
            }
        } catch (Exception e) {
            Debugger.report(e);
            return false;
        }
        return true;
    }
}
