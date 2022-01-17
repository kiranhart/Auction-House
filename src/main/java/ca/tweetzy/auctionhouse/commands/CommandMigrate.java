package ca.tweetzy.auctionhouse.commands;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.core.commands.AbstractCommand;
import ca.tweetzy.core.utils.TextUtils;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

/**
 * The current file has been created by Kiran Hart
 * Date Created: August 10 2021
 * Time Created: 12:25 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class CommandMigrate extends AbstractCommand {

	public CommandMigrate() {
		super(CommandType.CONSOLE_OK, "migrate");
	}

	@Override
	protected ReturnType runCommand(CommandSender sender, String... args) {
		AuctionHouse.getInstance().getLocale().newMessage(TextUtils.formatText("&cMigration support for v1 has been dropped since 2.53.0, use 2.52.0 or lower to migrate first.")).sendPrefixedMessage(sender);
		return ReturnType.SUCCESS;
	}

	@Override
	protected List<String> onTab(CommandSender sender, String... args) {
		if (args.length == 1) return Collections.singletonList("confirm");
		return null;
	}

	@Override
	public String getPermissionNode() {
		return "auctionhouse.cmd.migrate";
	}

	@Override
	public String getSyntax() {
		return "migrate <confirm>";
	}

	@Override
	public String getDescription() {
		return "Migrate from old data format to new format";
	}
}
