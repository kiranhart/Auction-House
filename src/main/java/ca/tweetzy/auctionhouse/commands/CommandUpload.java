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

package ca.tweetzy.auctionhouse.commands;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.database.DataManager;
import ca.tweetzy.core.commands.AbstractCommand;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.flight.database.DatabaseConnector;
import ca.tweetzy.flight.database.SQLiteConnector;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

/**
 * Date Created: February 24 2022
 * Time Created: 1:07 p.m.
 *
 * @author Kiran Hart
 */
public final class CommandUpload extends AbstractCommand {

	public CommandUpload() {
		super(CommandType.CONSOLE_OK, "upload");
	}

	@Override
	protected ReturnType runCommand(CommandSender sender, String... args) {
		if (args.length < 1) {
			List<String> warning = Arrays.asList(
					"&cPlease add &4-confirm &cto confirm you understand the following risks.",
					"",
					"&f1. &4This is an experimental feature",
					"&f2. &4Proper data conversion is not guaranteed, things can go wrong",
					"&f3. &4The conversion may not even go succeed / execute",
					"&f4. &4You made a backup of your &cauctionhouse.db &4file."
			);

			TextUtils.formatText(warning).forEach(sender::sendMessage);
			return ReturnType.FAILURE;
		}

		if (!args[0].equalsIgnoreCase("-confirm")) return ReturnType.FAILURE;

		final DatabaseConnector databaseConnector = new SQLiteConnector(AuctionHouse.getInstance());
		final DataManager manager = new DataManager(databaseConnector, AuctionHouse.getInstance(), null);

		manager.getItems((error, items) -> {
			if (error == null)
				items.forEach(item -> AuctionHouse.getDataManager().insertAuction(item, null));
		});

		manager.getAdminLogs((error, logs) -> {
			if (error == null)
				logs.forEach(log -> AuctionHouse.getDataManager().insertLog(log));
		});

		manager.getTransactions((error, transactions) -> {
			if (error == null)
				transactions.forEach(transaction -> AuctionHouse.getDataManager().insertTransaction(transaction, null));
		});

		return ReturnType.SUCCESS;
	}

	@Override
	protected List<String> onTab(CommandSender sender, String... args) {
		return null;
	}

	@Override
	public String getPermissionNode() {
		return "auctionhouse.cmd.upload";
	}

	@Override
	public String getSyntax() {
		return null;
	}

	@Override
	public String getDescription() {
		return null;
	}
}
