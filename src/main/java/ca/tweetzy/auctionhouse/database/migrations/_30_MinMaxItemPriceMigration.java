package ca.tweetzy.auctionhouse.database.migrations;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.flight.database.DataMigration;
import ca.tweetzy.flight.database.MySQLConnector;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class _30_MinMaxItemPriceMigration extends DataMigration {

	public _30_MinMaxItemPriceMigration() {
		super(30);
	}

	@Override
	public void migrate(Connection connection, String tablePrefix) throws SQLException {
		try (Statement statement = connection.createStatement()) {
			// Rename table
			if (AuctionHouse.getDatabaseConnector() instanceof MySQLConnector) {
				statement.execute("ALTER TABLE " + tablePrefix + "min_item_prices RENAME TO " + tablePrefix + "listing_prices");
				statement.execute("ALTER TABLE " + tablePrefix + "listing_prices CHANGE price min_price DOUBLE NOT NULL");
				statement.execute("ALTER TABLE " + tablePrefix + "listing_prices ADD max_price DOUBLE NOT NULL DEFAULT -1");
			} else {
				statement.execute("DROP TABLE " + tablePrefix + "min_item_prices;");
				statement.execute("CREATE TABLE IF NOT EXISTS " + tablePrefix + "listing_prices (" +
						"id VARCHAR(36) PRIMARY KEY, " +
						"item TEXT NOT NULL, " +
						"itemstack TEXT NULL, " +
						"serialize_version INTEGER DEFAULT 0, " +
						"min_price DOUBLE NOT NULL, " +
						"max_price DOUBLE NOT NULL DEFAULT -1" +
						" )");
			}
		}
	}
}
