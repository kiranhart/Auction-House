package ca.tweetzy.auctionhouse.commands;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.guis.GUIAuctionHouse;
import ca.tweetzy.core.commands.AbstractCommand;
import ca.tweetzy.core.utils.TextUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
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

    public CommandAuctionHouse() {
        super(CommandType.PLAYER_ONLY, "auctionhouse");
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (AuctionAPI.tellMigrationStatus(player)) return ReturnType.FAILURE;

            if (AuctionHouse.getInstance().getAuctionBanManager().checkAndHandleBan(player)) {
                return ReturnType.FAILURE;
            }

            if (AuctionHouse.getInstance().getAuctionPlayerManager().getPlayer(player.getUniqueId()) == null) {
                AuctionHouse.getInstance().getLocale().newMessage(TextUtils.formatText("&cCould not find auction player instance for&f: &e" + player.getName() + "&c creating one now.")).sendPrefixedMessage(Bukkit.getConsoleSender());
                AuctionHouse.getInstance().getAuctionPlayerManager().addPlayer(new AuctionPlayer(player));
            }

            if (args.length == 0) {
                AuctionHouse.getInstance().getGuiManager().showGUI(player, new GUIAuctionHouse(AuctionHouse.getInstance().getAuctionPlayerManager().getPlayer(player.getUniqueId())));
                return ReturnType.SUCCESS;
            }

            if (args.length == 1 && AuctionHouse.getInstance().getCommandManager().getSubCommands("auctionhouse").stream().noneMatch(cmd -> cmd.equalsIgnoreCase(StringUtils.join(args, ' ').trim()))) {
                AuctionHouse.getInstance().getGuiManager().showGUI(player, new GUIAuctionHouse(AuctionHouse.getInstance().getAuctionPlayerManager().getPlayer(player.getUniqueId()), StringUtils.join(args, ' ').trim()));
            }
        }
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        Player player = (Player) sender;
        return AuctionHouse.getInstance().getCommandManager().getAllCommands().stream().filter(cmd -> cmd.getPermissionNode() == null || player.hasPermission(cmd.getPermissionNode())).map(AbstractCommand::getSyntax).collect(Collectors.toList());
    }

    @Override
    public String getPermissionNode() {
        return "auctionhouse.cmd";
    }

    @Override
    public String getSyntax() {
        return AuctionHouse.getInstance().getLocale().getMessage("commands.syntax.auctionhouse").getMessage();
    }

    @Override
    public String getDescription() {
        return AuctionHouse.getInstance().getLocale().getMessage("commands.description.auctionhouse").getMessage();
    }
}
