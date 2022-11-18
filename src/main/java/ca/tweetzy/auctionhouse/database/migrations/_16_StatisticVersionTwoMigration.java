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

import ca.tweetzy.core.database.DataMigration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * The current file has been created by Kiran Hart
 * Date Created: September 08 2022
 * Time Created: 9:13 a.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class _16_StatisticVersionTwoMigration extends DataMigration {

	public _16_StatisticVersionTwoMigration() {
		super(16);
	}

	@Override
	public void migrate(Connection connection, String tablePrefix) throws SQLException {

		try (Statement statement = connection.createStatement()) {
			statement.execute("CREATE TABLE " + tablePrefix + "statistic (" +
					"uuid VARCHAR(36) PRIMARY KEY, " +
					"stat_owner VARCHAR(36) NOT NULL, " +
					"stat_type VARCHAR(20) NOT NULL, " +
					"value DOUBLE NOT NULL, " +
					"time LONG NOT NULL" +
					")");

		}
	}
}
