package ca.tweetzy.auctionhouse.database.migrations;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.core.database.DataMigration;
import ca.tweetzy.core.database.MySQLConnector;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * The current file has been created by Kiran Hart
 * Date Created: September 08 2022
 * Time Created: 9:13 a.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class _16_StatisticVersionTwoMigration extends DataMigration {

	public _16_StatisticVersionTwoMigration() {
		super(16);
	}

	@Override
	public void migrate(Connection connection, String tablePrefix) throws SQLException {

		String autoIncrement = AuctionHouse.getInstance().getDatabaseConnector() instanceof MySQLConnector ? " AUTO_INCREMENT" : "";

		try (Statement statement = connection.createStatement()) {
			statement.execute("CREATE TABLE " + tablePrefix + "statistic (" +
					"id INTEGER PRIMARY KEY" + autoIncrement + ", " +
					"stat_owner VARCHAR(36) NOT NULL, " +
					"stat_type VARCHAR(20) NOT NULL, " +
					"value DOUBLE NOT NULL, " +
					"time LONG NOT NULL" +
					")");

		}
	}
}
