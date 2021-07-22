package ca.tweetzy.auctionhouse.commands;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.core.commands.AbstractCommand;
import ca.tweetzy.core.utils.PlayerUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * The current file has been created by Kiran Hart
 * Date Created: July 21 2021
 * Time Created: 4:11 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class CommandUnban extends AbstractCommand {

    public CommandUnban() {
        super(CommandType.CONSOLE_OK, "unban");
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        if (args.length != 1) return ReturnType.SYNTAX_ERROR;
        Player target = PlayerUtils.findPlayer(args[0]);

        if (target == null) {
            AuctionHouse.getInstance().getLocale().getMessage("general.playernotfound").processPlaceholder("player", args[0]).sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        if (!AuctionHouse.getInstance().getAuctionBanManager().getBans().containsKey(target.getUniqueId())) {
            AuctionHouse.getInstance().getLocale().getMessage("bans.playernotbanned").processPlaceholder("player", args[0]).sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        AuctionHouse.getInstance().getAuctionBanManager().removeBan(target.getUniqueId());
        AuctionHouse.getInstance().getLocale().getMessage("bans.playerunbanned").processPlaceholder("player", args[0]).sendPrefixedMessage(sender);
        AuctionHouse.getInstance().getLocale().getMessage("bans.unbanned").sendPrefixedMessage(target);
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "auctionhouse.cmd.unban";
    }

    @Override
    public String getSyntax() {
        return AuctionHouse.getInstance().getLocale().getMessage("commands.syntax.unban").getMessage();
    }

    @Override
    public String getDescription() {
        return AuctionHouse.getInstance().getLocale().getMessage("commands.description.unban").getMessage();
    }
}
