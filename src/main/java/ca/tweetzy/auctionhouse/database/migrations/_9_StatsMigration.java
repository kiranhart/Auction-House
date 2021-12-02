package ca.tweetzy.auctionhouse.database.migrations;

import ca.tweetzy.core.database.DataMigration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * The current file has been created by Kiran Hart
 * Date Created: August 24 2021
 * Time Created: 4:04 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public final class _9_StatsMigration extends DataMigration {

	public _9_StatsMigration() {
		super(9);
	}

	@Override
	public void migrate(Connection connection, String tablePrefix) throws SQLException {
		try (Statement statement = connection.createStatement()) {
			statement.execute("CREATE TABLE " + tablePrefix + "stats (" +
					"id VARCHAR(36) PRIMARY KEY, " +
					"auctions_created INT NOT NULL, " +
					"auctions_sold INT NOT NULL, " +
					"auctions_expired INT NOT NULL, " +
					"money_earned DOUBLE NOT NULL, " +
					"money_spent DOUBLE NOT NULL )");
		}
	}
}
