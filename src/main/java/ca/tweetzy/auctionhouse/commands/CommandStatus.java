package ca.tweetzy.auctionhouse.commands;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.guis.GUIStats;
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
        super(CommandType.PLAYER_ONLY, "status", "stats");
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        if (AuctionAPI.tellMigrationStatus(sender)) return ReturnType.FAILURE;
        Player player = (Player) sender;
        AuctionHouse.getInstance().getGuiManager().showGUI(player, new GUIStats(player));
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
        return "Open the auction house statistics";
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return null;
    }
}
