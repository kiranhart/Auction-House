package ca.tweetzy.auctionhouse.database.migrations;

import ca.tweetzy.flight.database.DataMigration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class _27_FixMigration25to26Migration extends DataMigration {

	public _27_FixMigration25to26Migration() {
		super(27);
	}

	@Override
	public void migrate(Connection connection, String tablePrefix) throws SQLException {
		try (Statement statement = connection.createStatement()) {
			statement.execute("ALTER TABLE " + tablePrefix + "auctions DROP COLUMN currency");
			statement.execute("ALTER TABLE " + tablePrefix + "auctions ADD currency VARCHAR(70) DEFAULT ('Vault/Vault')");

			statement.execute("DROP TABLE " + tablePrefix + "bids;");
			statement.execute("CREATE TABLE " + tablePrefix + "bids (" +
					"id VARCHAR(36) NOT NULL PRIMARY KEY, " +
					"listing_id VARCHAR(36) NOT NULL, " +
					"bidder_uuid VARCHAR(36) NOT NULL, " +
					"bidder_name VARCHAR(16) NOT NULL, " +
					"currency VARCHAR(70) NOT NULL DEFAULT ('Vault/Vault')," +
					"currency_item TEXT NULL," +
					"amount DOUBLE NOT NULL, " +
					"world VARCHAR(126) NOT NULL, " +
					"server VARCHAR(80) NOT NULL, " +
					"created_at BigInt NOT NULL )");
		}
	}
}
