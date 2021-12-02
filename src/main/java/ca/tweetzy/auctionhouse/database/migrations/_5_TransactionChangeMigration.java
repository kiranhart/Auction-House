package ca.tweetzy.auctionhouse.database.migrations;

import ca.tweetzy.core.database.DataMigration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * The current file has been created by Kiran Hart
 * Date Created: August 08 2021
 * Time Created: 1:06 a.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class _5_TransactionChangeMigration extends DataMigration {

	public _5_TransactionChangeMigration() {
		super(5);
	}

	@Override
	public void migrate(Connection connection, String tablePrefix) throws SQLException {
		try (Statement statement = connection.createStatement()) {
			statement.execute("DROP TABLE " + tablePrefix + "transactions");
			statement.execute("CREATE TABLE " + tablePrefix + "transactions (" +
					"id VARCHAR(36) PRIMARY KEY, " +
					"seller VARCHAR(36) NOT NULL, " +
					"seller_name VARCHAR(16) NOT NULL, " +
					"buyer VARCHAR(36) NOT NULL," +
					"buyer_name VARCHAR(16) NOT NULL," +
					"transaction_time BigInt NOT NULL, " +
					"item TEXT NOT NULL, " +
					"auction_sale_type VARCHAR(32) NOT NULL, " +
					"final_price DOUBLE NOT NULL " +
					" )");
		}
	}
}
