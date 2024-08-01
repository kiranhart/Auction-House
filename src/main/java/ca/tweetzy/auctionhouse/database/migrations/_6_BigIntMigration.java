/*
 * Auction House
 * Copyright 2018-2022 Kiran Hart
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ca.tweetzy.auctionhouse.database.migrations;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.flight.database.DataMigration;
import ca.tweetzy.flight.database.MySQLConnector;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * The current file has been created by Kiran Hart
 * Date Created: August 12 2021
 * Time Created: 11:58 a.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class _6_BigIntMigration extends DataMigration {

	public _6_BigIntMigration() {
		super(6);
	}

	@Override
	public void migrate(Connection connection, String tablePrefix) throws SQLException {
		try (Statement statement = connection.createStatement()) {

			if (AuctionHouse.getDatabaseConnector() instanceof MySQLConnector) {
				statement.execute("ALTER TABLE " + tablePrefix + "auctions MODIFY COLUMN expires_at BigInt(20)");

			} else {
				statement.execute("DROP TABLE " + tablePrefix + "auctions");
				statement.execute("CREATE TABLE " + tablePrefix + "auctions (" +
						"id VARCHAR(36) PRIMARY KEY, " +
						"owner VARCHAR(36) NOT NULL, " +
						"owner_name VARCHAR(16) NOT NULL, " +
						"highest_bidder VARCHAR(36) NOT NULL," +
						"highest_bidder_name VARCHAR(16) NOT NULL," +
						"category VARCHAR(48) NOT NULL, " +
						"base_price DOUBLE NOT NULL, " +
						"bid_start_price DOUBLE NOT NULL, " +
						"bid_increment_price DOUBLE NOT NULL, " +
						"current_price DOUBLE NOT NULL, " +
						"expired BOOLEAN NOT NULL, " +
						"expires_at BigInt(20) NOT NULL, " +
						"item_material VARCHAR(48) NOT NULL, " +
						"item_name TEXT NOT NULL, " +
						"item_lore TEXT NULL, " +
						"item_enchants TEXT NULL, " +
						"item TEXT NOT NULL " +
						" )");
			}

		}
	}
}
