package ca.tweetzy.auctionhouse.database.migrations;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.core.database.DataMigration;
import ca.tweetzy.core.database.MySQLConnector;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * The current file has been created by Kiran Hart
 * Date Created: August 12 2021
 * Time Created: 11:58 a.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class _7_TransactionBigIntMigration extends DataMigration {

    public _7_TransactionBigIntMigration() {
        super(7);
    }

    @Override
    public void migrate(Connection connection, String tablePrefix) throws SQLException {
        try (Statement statement = connection.createStatement()) {

            if (AuctionHouse.getInstance().getDatabaseConnector() instanceof MySQLConnector) {
                statement.execute("ALTER TABLE " + tablePrefix + "transactions MODIFY COLUMN transaction_time BigInt(20)");

            } else {
                statement.execute("DROP TABLE " + tablePrefix + "transactions");
                statement.execute("CREATE TABLE " + tablePrefix + "transactions (" +
                        "id VARCHAR(36) PRIMARY KEY, " +
                        "seller VARCHAR(36) NOT NULL, " +
                        "seller_name VARCHAR(16) NOT NULL, " +
                        "buyer VARCHAR(36) NOT NULL," +
                        "buyer_name VARCHAR(16) NOT NULL," +
                        "transaction_time BigInt(20) NOT NULL, " +
                        "item TEXT NOT NULL, " +
                        "auction_sale_type VARCHAR(32) NOT NULL, " +
                        "final_price DOUBLE NOT NULL " +
                        " )");
            }

        }
    }
}
