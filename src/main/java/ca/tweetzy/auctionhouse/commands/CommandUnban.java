package ca.tweetzy.auctionhouse.commands;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.auction.AuctionBan;
import ca.tweetzy.core.commands.AbstractCommand;
import ca.tweetzy.core.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
        if (AuctionAPI.tellMigrationStatus(sender)) return ReturnType.FAILURE;

        Player target = PlayerUtils.findPlayer(args[0]);
        OfflinePlayer offlinePlayer = null;

        if (target == null) {
            offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
            if (offlinePlayer == null || !offlinePlayer.hasPlayedBefore()) {
                AuctionHouse.getInstance().getLocale().getMessage("general.playernotfound").processPlaceholder("player", args[0]).sendPrefixedMessage(sender);
                return ReturnType.FAILURE;
            }
        }

        UUID toUnBan = target == null ? offlinePlayer.getUniqueId() : target.getUniqueId();

        if (!AuctionHouse.getInstance().getAuctionBanManager().getBans().containsKey(toUnBan)) {
            AuctionHouse.getInstance().getLocale().getMessage("bans.playernotbanned").processPlaceholder("player", args[0]).sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        AuctionHouse.getInstance().getAuctionBanManager().removeBan(toUnBan);
        AuctionHouse.getInstance().getLocale().getMessage("bans.playerunbanned").processPlaceholder("player", args[0]).sendPrefixedMessage(sender);
        if (target != null) {
            AuctionHouse.getInstance().getLocale().getMessage("bans.unbanned").sendPrefixedMessage(target);
        }

        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        if (args.length == 1) return AuctionHouse.getInstance().getAuctionBanManager().getBans().values().stream().map(ban -> Bukkit.getOfflinePlayer(ban.getBannedPlayer()).getName()).collect(Collectors.toList());
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
