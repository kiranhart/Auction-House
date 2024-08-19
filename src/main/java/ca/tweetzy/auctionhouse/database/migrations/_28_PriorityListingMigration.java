package ca.tweetzy.auctionhouse.database.migrations;

import ca.tweetzy.flight.database.DataMigration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class _28_PriorityListingMigration extends DataMigration {

	public _28_PriorityListingMigration() {
		super(28);
	}

	@Override
	public void migrate(Connection connection, String tablePrefix) throws SQLException {
		try (Statement statement = connection.createStatement()) {
			statement.execute("ALTER TABLE " + tablePrefix + "auctions ADD listing_priority BOOLEAN default 0");
			statement.execute("ALTER TABLE " + tablePrefix + "auctions ADD priority_expires_at BigInt default 0");
		}
	}
}
