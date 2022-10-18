package ca.tweetzy.auctionhouse.commands;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.database.DataManager;
import ca.tweetzy.core.commands.AbstractCommand;
import ca.tweetzy.core.database.DatabaseConnector;
import ca.tweetzy.core.database.SQLiteConnector;
import ca.tweetzy.core.utils.TextUtils;
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

		DatabaseConnector databaseConnector = new SQLiteConnector(AuctionHouse.getInstance());
		DataManager manager = new DataManager(databaseConnector, AuctionHouse.getInstance(), null);

		manager.getItems((error, items) -> {
			if (error == null)
				items.forEach(item -> AuctionHouse.getInstance().getDataManager().insertAuctionAsync(item, null));
		});

		manager.getAdminLogs((error, logs) -> {
			if (error == null)
				logs.forEach(log -> AuctionHouse.getInstance().getDataManager().insertLogAsync(log));
		});

		manager.getTransactions((error, transactions) -> {
			if (error == null)
				transactions.forEach(transaction -> AuctionHouse.getInstance().getDataManager().insertTransactionAsync(transaction, null));
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
