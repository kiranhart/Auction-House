package ca.tweetzy.auctionhouse.database.migrations;

import ca.tweetzy.core.database.DataMigration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * The current file has been created by Kiran Hart
 * Date Created: August 12 2021
 * Time Created: 11:58 a.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class _13_MinItemPriceMigration extends DataMigration {

	public _13_MinItemPriceMigration() {
		super(13);
	}

	@Override
	public void migrate(Connection connection, String tablePrefix) throws SQLException {
		try (Statement statement = connection.createStatement()) {
			statement.execute("CREATE TABLE IF NOT EXISTS " + tablePrefix + "min_item_prices (" +
					"id VARCHAR(36) PRIMARY KEY, " +
					"item TEXT NOT NULL, " +
					"price DOUBLE NOT NULL" +
					" )");
		}
	}
}
