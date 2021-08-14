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
        if (args.length == 0) {
            AuctionHouse.getInstance().getLocale().newMessage(TextUtils.formatText("&cUse '/ah migrate confirm' to confirm the migration, please sure that you have made a backup of your auction house data.yml / auction house database tables. Although everything should work fine, there is always a chance that something wil go wrong.")).sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        if (args.length == 1 && !args[0].equalsIgnoreCase("confirm")) return ReturnType.SYNTAX_ERROR;

        // Begin migration
        AuctionHouse.getInstance().getLocale().newMessage(TextUtils.formatText("&cMIGRATION STARTED")).sendPrefixedMessage(sender);
        AuctionHouse.getInstance().getLocale().newMessage(TextUtils.formatText("&eAuction House usage will be disabled until it is finished, this may take some time depending on how many items you have stored.")).sendPrefixedMessage(sender);
        AuctionHouse.getInstance().getDataManager().migrateFromSerializationFormat(items -> {
            AuctionHouse.getInstance().getLocale().newMessage(TextUtils.formatText("&aMIGRATION FINISHED")).sendPrefixedMessage(sender);
            AuctionHouse.getInstance().getLocale().newMessage(TextUtils.formatText("&eConverted a total of " + items.size() + " to new format")).sendPrefixedMessage(sender);
            AuctionHouse.getInstance().getLocale().newMessage(TextUtils.formatText("&eYou can now use the auction house safely")).sendPrefixedMessage(sender);
        });

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
