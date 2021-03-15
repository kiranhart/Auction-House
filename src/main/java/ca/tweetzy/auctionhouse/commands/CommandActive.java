package ca.tweetzy.auctionhouse.commands;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.guis.GUIActiveAuctions;
import ca.tweetzy.auctionhouse.guis.GUIAuctionHouse;
import ca.tweetzy.core.commands.AbstractCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * The current file has been created by Kiran Hart
 * Date Created: March 15 2021
 * Time Created: 4:32 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class CommandActive extends AbstractCommand {

    public CommandActive() {
        super(CommandType.PLAYER_ONLY, "active");
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        Player player = (Player) sender;
        AuctionHouse.getInstance().getGuiManager().showGUI(player, new GUIActiveAuctions(AuctionHouse.getInstance().getAuctionPlayerManager().getPlayer(player.getUniqueId())));
        return ReturnType.SUCCESS;
    }

    @Override
    public String getPermissionNode() {
        return "auctionhouse.cmd.active";
    }

    @Override
    public String getSyntax() {
        return "active";
    }

    @Override
    public String getDescription() {
        return "View all your auction listings";
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return null;
    }
}
