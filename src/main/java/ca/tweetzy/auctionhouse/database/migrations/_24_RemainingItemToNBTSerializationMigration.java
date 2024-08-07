package ca.tweetzy.auctionhouse.database.migrations;

import ca.tweetzy.flight.database.DataMigration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class _24_RemainingItemToNBTSerializationMigration extends DataMigration {

	public _24_RemainingItemToNBTSerializationMigration() {
		super(24);
	}

	@Override
	public void migrate(Connection connection, String tablePrefix) throws SQLException {
		try (Statement statement = connection.createStatement()) {
			statement.execute("ALTER TABLE " + tablePrefix + "admin_logs ADD itemstack TEXT NULL");
			statement.execute("ALTER TABLE " + tablePrefix + "admin_logs ADD serialize_version INTEGER DEFAULT 0");

			statement.execute("ALTER TABLE " + tablePrefix + "min_item_prices ADD itemstack TEXT NULL");
			statement.execute("ALTER TABLE " + tablePrefix + "min_item_prices ADD serialize_version INTEGER DEFAULT 0");

			statement.execute("ALTER TABLE " + tablePrefix + "payments ADD itemstack TEXT NULL");
			statement.execute("ALTER TABLE " + tablePrefix + "payments ADD serialize_version INTEGER DEFAULT 0");
		}
	}
}
