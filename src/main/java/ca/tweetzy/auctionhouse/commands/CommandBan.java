package ca.tweetzy.auctionhouse.commands;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.api.events.AuctionBanPlayerEvent;
import ca.tweetzy.auctionhouse.auction.AuctionBan;
import ca.tweetzy.auctionhouse.guis.GUIBans;
import ca.tweetzy.core.commands.AbstractCommand;
import ca.tweetzy.core.utils.PlayerUtils;
import ca.tweetzy.core.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: July 21 2021
 * Time Created: 3:05 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class CommandBan extends AbstractCommand {

    public CommandBan() {
        super(CommandType.PLAYER_ONLY, "ban");
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        Player player = (Player) sender;
        if (args.length == 0) {
            // Open the bans menu
            AuctionHouse.getInstance().getGuiManager().showGUI(player, new GUIBans());
            return ReturnType.SUCCESS;
        }

        if (args.length < 3) {
            return ReturnType.SYNTAX_ERROR;
        }

        Player target = PlayerUtils.findPlayer(args[0]);
        String timeString = args[1];
        StringBuilder reason = new StringBuilder();
        for (int i = 2; i < args.length; i++) {
            reason.append(args[i]).append(" ");
        }

        if (target == null) {
            AuctionHouse.getInstance().getLocale().getMessage("general.playernotfound").processPlaceholder("player", args[0]).sendPrefixedMessage(player);
            return ReturnType.FAILURE;
        }

        if (!AuctionAPI.getInstance().isValidTimeString(timeString)) {
            AuctionHouse.getInstance().getLocale().getMessage("general.invalidtimestring").sendPrefixedMessage(player);
            return ReturnType.FAILURE;
        }

        if (reason.toString().length() == 0) {
            AuctionHouse.getInstance().getLocale().getMessage("bans.nobanreason").sendPrefixedMessage(player);
            return ReturnType.FAILURE;
        }

        if (AuctionHouse.getInstance().getAuctionBanManager().getBans().containsKey(target.getUniqueId())) {
            AuctionHouse.getInstance().getLocale().getMessage("bans.playeralreadybanned").processPlaceholder("player", args[0]).sendPrefixedMessage(player);
            return ReturnType.FAILURE;
        }

        long bannedSeconds = AuctionAPI.getInstance().getSecondsFromString(timeString);

        AuctionBanPlayerEvent auctionBanPlayerEvent = new AuctionBanPlayerEvent(player, target.getUniqueId(), reason.toString().trim(), bannedSeconds, false);
        Bukkit.getServer().getPluginManager().callEvent(auctionBanPlayerEvent);
        if (auctionBanPlayerEvent.isCancelled()) return ReturnType.FAILURE;

        AuctionBan auctionBan = new AuctionBan(target.getUniqueId(), reason.toString().trim(), System.currentTimeMillis() + bannedSeconds * 1000);
        AuctionHouse.getInstance().getAuctionBanManager().addBan(auctionBan);
        AuctionHouse.getInstance().getLocale().getMessage("bans.bannedplayer").processPlaceholder("player", args[0]).processPlaceholder("ban_amount", TimeUtils.makeReadable(bannedSeconds * 1000)).sendPrefixedMessage(player);
        AuctionHouse.getInstance().getLocale().getMessage("bans.remainingtime").processPlaceholder("ban_amount", TimeUtils.makeReadable(bannedSeconds * 1000)).sendPrefixedMessage(target);
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        if (args.length == 1) return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
        if (args.length == 2) return Arrays.asList("1m", "1h", "1d", "1y");
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "auctionhouse.cmd.ban";
    }

    @Override
    public String getSyntax() {
        return AuctionHouse.getInstance().getLocale().getMessage("commands.syntax.ban").getMessage();
    }

    @Override
    public String getDescription() {
        return AuctionHouse.getInstance().getLocale().getMessage("commands.description.ban").getMessage();
    }
}
