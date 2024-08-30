package ca.tweetzy.auctionhouse.database.migrations;

import ca.tweetzy.flight.database.DataMigration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class _29_PaymentMultiCurrencyMigration extends DataMigration {

	public _29_PaymentMultiCurrencyMigration() {
		super(29);
	}

	@Override
	public void migrate(Connection connection, String tablePrefix) throws SQLException {
		try (Statement statement = connection.createStatement()) {
			statement.execute("ALTER TABLE " + tablePrefix + "payments ADD currency TEXT NOT NULL DEFAULT 'Vault/Vault' ");
			statement.execute("ALTER TABLE " + tablePrefix + "payments ADD currency_item TEXT NULL");
		}
	}
}
