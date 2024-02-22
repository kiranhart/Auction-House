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

import ca.tweetzy.flight.database.DataMigration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * The current file has been created by Kiran Hart
 * Date Created: August 12 2021
 * Time Created: 11:58 a.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class _13_MinItemPriceMigration extends DataMigration {

	public _13_MinItemPriceMigration() {
		super(13);
	}

	@Override
	public void migrate(Connection connection, String tablePrefix) throws SQLException {
		try (Statement statement = connection.createStatement()) {
			statement.execute("CREATE TABLE IF NOT EXISTS " + tablePrefix + "min_item_prices (" +
					"id VARCHAR(36) PRIMARY KEY, " +
					"item TEXT NOT NULL, " +
					"price DOUBLE NOT NULL" +
					" )");
		}
	}
}
