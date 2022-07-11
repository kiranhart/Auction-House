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
public class _12_SerializeFormatDropMigration extends DataMigration {

	public _12_SerializeFormatDropMigration() {
		super(12);
	}

	@Override
	public void migrate(Connection connection, String tablePrefix) throws SQLException {
		try (Statement statement = connection.createStatement()) {
			statement.execute("DROP TABLE IF EXISTS " + tablePrefix + "items");
		}
	}
}
