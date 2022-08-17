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
public class _15_AuctionPlayerMigration extends DataMigration {

	public _15_AuctionPlayerMigration() {
		super(15);
	}

	@Override
	public void migrate(Connection connection, String tablePrefix) throws SQLException {
		try (Statement statement = connection.createStatement()) {
			statement.execute("CREATE TABLE " + tablePrefix + "player (" +
					"uuid VARCHAR(36) PRIMARY KEY, " +
					"filter_sale_type VARCHAR(32) NOT NULL, " +
					"filter_item_category VARCHAR(16) NOT NULL, " +
					"filter_sort_type VARCHAR(12) NOT NULL, " +
					"last_listed_item LONG NOT NULL" +
					")");

		}
	}
}
