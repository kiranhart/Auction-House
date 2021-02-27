package ca.tweetzy.auctionhouse.commands;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.guis.AuctionHouseGUI;
import ca.tweetzy.core.commands.AbstractCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: January 12 2021
 * Time Created: 6:40 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class CommandAuctionHouse extends AbstractCommand {

    final AuctionHouse instance;

    public CommandAuctionHouse(AuctionHouse instance) {
        super(CommandType.PLAYER_ONLY, "auctionhouse");
        this.instance = instance;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            player.openInventory(new AuctionHouseGUI(instance.getAuctionPlayerManager().locateAndSelectPlayer(player)).getInventory());
        }
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        Player player = (Player) sender;
        return instance.getCommandManager().getAllCommands().stream().filter(cmd -> cmd.getPermissionNode() == null ||  player.hasPermission(cmd.getPermissionNode())).map(AbstractCommand::getSyntax).collect(Collectors.toList());
    }

    @Override
    public String getPermissionNode() {
        return "auctionhouse.cmd";
    }

    @Override
    public String getSyntax() {
        return "/Ah";
    }

    @Override
    public String getDescription() {
        return "Main command for the plugin, it opens the auction window.";
    }
}
