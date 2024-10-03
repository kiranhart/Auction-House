package ca.tweetzy.auctionhouse.database.migrations;

import ca.tweetzy.flight.database.DataMigration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class _25_BidHistoryMigration extends DataMigration {

	public _25_BidHistoryMigration() {
		super(25);
	}

	@Override
	public void migrate(Connection connection, String tablePrefix) throws SQLException {
		try (Statement statement = connection.createStatement()) {
			statement.execute("CREATE TABLE " + tablePrefix + "bids (" +
					"id VARCHAR(36) NOT NULL PRIMARY KEY, " +
					"listing_id VARCHAR(36) NOT NULL, " +
					"bidder_uuid VARCHAR(36) NOT NULL, " +
					"bidder_name VARCHAR(16) NOT NULL, " +
					"currency TEXT NOT NULL DEFAULT ('Vault/Vault')," +
					"currency_item TEXT NULL," +
					"amount TEXT NOT NULL, " +
					"world TEXT NOT NULL, " +
					"server BOOLEAN NOT NULL, " +
					"created_at BigInt NOT NULL )");
		}
	}
}
