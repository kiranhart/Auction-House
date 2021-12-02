package ca.tweetzy.auctionhouse.database.migrations;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.core.database.DataMigration;
import ca.tweetzy.core.database.MySQLConnector;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * The current file has been created by Kiran Hart
 * Date Created: April 15 2021
 * Time Created: 2:41 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class _1_InitialMigration extends DataMigration {

	public _1_InitialMigration() {
		super(1);
	}

	@Override
	public void migrate(Connection connection, String tablePrefix) throws SQLException {
		String autoIncrement = AuctionHouse.getInstance().getDatabaseConnector() instanceof MySQLConnector ? " AUTO_INCREMENT" : "";

		try (Statement statement = connection.createStatement()) {
			statement.execute("CREATE TABLE " + tablePrefix + "items (" +
					"id INTEGER PRIMARY KEY" + autoIncrement + ", " +
					"data LONGTEXT NOT NULL )");

			statement.execute("CREATE TABLE " + tablePrefix + "transactions (" +
					"id INTEGER PRIMARY KEY" + autoIncrement + ", " +
					"data LONGTEXT NOT NULL )");
		}
	}
}
