package ca.tweetzy.auctionhouse.database.migrations;

import ca.tweetzy.flight.database.DataMigration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class _31_RequestTransactionMigration extends DataMigration {


	public _31_RequestTransactionMigration() {
		super(31);
	}

	@Override
	public void migrate(Connection connection, String tablePrefix) throws SQLException {
		//id, item, amount, price, requester_uuid, requester_name, fulfiller_uuid, fulfiller_name, time_created
		try (Statement statement = connection.createStatement()) {
			statement.execute("CREATE TABLE IF NOT EXISTS " + tablePrefix + "completed_requests (" +
					"id VARCHAR(36) PRIMARY KEY, " +
					"item TEXT NOT NULL, " +
					"amount INTEGER NOT NULL, " +
					"price DOUBLE NOT NULL, " +
					"requester_uuid VARCHAR(36) NOT NULL, " +
					"requester_name VARCHAR(16) NOT NULL, " +
					"fulfiller_uuid VARCHAR(36) NOT NULL, " +
					"fulfiller_name VARCHAR(16) NOT NULL, " +
					"time_created BigInt NOT NULL " +
					" )");
		}
	}
}
