package ca.tweetzy.auctionhouse.commands;

import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.core.commands.AbstractCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * The current file has been created by Kiran Hart
 * Date Created: May 23 2021
 * Time Created: 11:59 a.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class CommandStatus extends AbstractCommand {

    public CommandStatus() {
        super(CommandType.CONSOLE_OK, "status");
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        if (AuctionAPI.tellMigrationStatus(sender)) return ReturnType.FAILURE;
        Player player = (Player) sender;


        return ReturnType.SUCCESS;
    }

    @Override
    public String getPermissionNode() {
        return "auctionhouse.cmd.status";
    }

    @Override
    public String getSyntax() {
        return "status";
    }

    @Override
    public String getDescription() {
        return "Return plugin statistics";
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return null;
    }
}
