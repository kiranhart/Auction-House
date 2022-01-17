package ca.tweetzy.auctionhouse.database.migrations;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.core.database.DataMigration;
import ca.tweetzy.core.database.MySQLConnector;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * The current file has been created by Kiran Hart
 * Date Created: August 24 2021
 * Time Created: 4:04 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public final class _11_AdminLogMigration extends DataMigration {

	public _11_AdminLogMigration() {
		super(11);
	}

	@Override
	public void migrate(Connection connection, String tablePrefix) throws SQLException {
		try (Statement statement = connection.createStatement()) {
			String autoIncrement = AuctionHouse.getInstance().getDatabaseConnector() instanceof MySQLConnector ? " AUTO_INCREMENT" : "";


			statement.execute("CREATE TABLE IF NOT EXISTS " + tablePrefix + "admin_logs (" +
					"id INTEGER PRIMARY KEY" + autoIncrement + ", " +
					"admin VARCHAR(36) NOT NULL, " +
					"admin_name VARCHAR(16) NOT NULL, " +
					"target VARCHAR(36) NOT NULL, " +
					"target_name VARCHAR(16) NOT NULL, " +
					"item TEXT NOT NULL, " +
					"item_id VARCHAR(36) NOT NULL, " +
					"action VARCHAR(36) NOT NULL, " +
					"time BigInt NOT NULL" +

					" )");
		}
	}
}
