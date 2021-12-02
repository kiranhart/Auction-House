package ca.tweetzy.auctionhouse.database.migrations;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.core.database.DataMigration;
import ca.tweetzy.core.database.MySQLConnector;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * The current file has been created by Kiran Hart
 * Date Created: July 21 2021
 * Time Created: 2:21 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class _3_BansMigration extends DataMigration {

	public _3_BansMigration() {
		super(3);
	}

	@Override
	public void migrate(Connection connection, String tablePrefix) throws SQLException {
		String autoIncrement = AuctionHouse.getInstance().getDatabaseConnector() instanceof MySQLConnector ? " AUTO_INCREMENT" : "";

		try (Statement statement = connection.createStatement()) {
			statement.execute("CREATE TABLE " + tablePrefix + "bans (" +
					"id INTEGER PRIMARY KEY" + autoIncrement + ", " +
					"user VARCHAR(36) NOT NULL, " +
					"reason TEXT NOT NULL, " +
					"time BigInt NOT NULL )");
		}
	}
}
