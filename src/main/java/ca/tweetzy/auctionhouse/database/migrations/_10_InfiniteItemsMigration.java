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
public final class _10_InfiniteItemsMigration extends DataMigration {

	public _10_InfiniteItemsMigration() {
		super(10);
	}

	@Override
	public void migrate(Connection connection, String tablePrefix) throws SQLException {
		try (Statement statement = connection.createStatement()) {
			statement.execute("ALTER TABLE " + tablePrefix + "auctions ADD infinite BOOLEAN NULL");
		}
	}
}
