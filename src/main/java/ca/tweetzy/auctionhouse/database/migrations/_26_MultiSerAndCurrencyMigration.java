package ca.tweetzy.auctionhouse.database.migrations;

import ca.tweetzy.flight.database.DataMigration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class _26_MultiSerAndCurrencyMigration extends DataMigration {

	public _26_MultiSerAndCurrencyMigration() {
		super(26);
	}

	@Override
	public void migrate(Connection connection, String tablePrefix) throws SQLException {
		try (Statement statement = connection.createStatement()) {
			statement.execute("ALTER TABLE " + tablePrefix + "auctions ADD currency TEXT NOT NULL DEFAULT ('Vault/Vault') ");
			statement.execute("ALTER TABLE " + tablePrefix + "auctions ADD currency_item TEXT NULL");
			statement.execute("ALTER TABLE " + tablePrefix + "auctions ADD listed_server TEXT NULL");
		}
	}
}
