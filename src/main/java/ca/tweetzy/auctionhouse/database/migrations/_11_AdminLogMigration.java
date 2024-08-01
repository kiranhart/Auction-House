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
 * Date Created: August 24 2021
 * Time Created: 4:04 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public final class _11_AdminLogMigration extends DataMigration {

	public _11_AdminLogMigration() {
		super(11);
	}

	@Override
	public void migrate(Connection connection, String tablePrefix) throws SQLException {
		try (Statement statement = connection.createStatement()) {
			String autoIncrement = AuctionHouse.getDatabaseConnector() instanceof MySQLConnector ? " AUTO_INCREMENT" : "";


			statement.execute("CREATE TABLE IF NOT EXISTS " + tablePrefix + "admin_logs (" +
					"id INTEGER PRIMARY KEY" + autoIncrement + ", " +
					"admin VARCHAR(36) NOT NULL, " +
					"admin_name VARCHAR(16) NOT NULL, " +
					"target VARCHAR(36) NOT NULL, " +
					"target_name VARCHAR(16) NOT NULL, " +
					"item TEXT NOT NULL, " +
					"item_id VARCHAR(36) NOT NULL, " +
					"action VARCHAR(36) NOT NULL, " +
					"time BigInt NOT NULL" +

					" )");
		}
	}
}
