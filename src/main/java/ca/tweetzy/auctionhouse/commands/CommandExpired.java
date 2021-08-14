package ca.tweetzy.auctionhouse.commands;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.guis.GUIExpiredItems;
import ca.tweetzy.core.commands.AbstractCommand;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.core.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * The current file has been created by Kiran Hart
 * Date Created: March 15 2021
 * Time Created: 4:32 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class CommandExpired extends AbstractCommand {

    public CommandExpired() {
        super(CommandType.PLAYER_ONLY, "expired");
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        Player player = (Player) sender;
        if (AuctionAPI.tellMigrationStatus(player)) return ReturnType.FAILURE;

        if (AuctionHouse.getInstance().getAuctionBanManager().checkAndHandleBan(player)) {
            return ReturnType.FAILURE;
        }

        if (AuctionHouse.getInstance().getAuctionPlayerManager().getPlayer(player.getUniqueId()) == null) {
            AuctionHouse.getInstance().getLocale().newMessage(TextUtils.formatText("&cCould not find auction player instance for&f: &e" + player.getName() + "&c creating one now.")).sendPrefixedMessage(Bukkit.getConsoleSender());
            AuctionHouse.getInstance().getAuctionPlayerManager().addPlayer(new AuctionPlayer(player));
        }

        AuctionHouse.getInstance().getGuiManager().showGUI(player, new GUIExpiredItems(AuctionHouse.getInstance().getAuctionPlayerManager().getPlayer(player.getUniqueId())));
        return ReturnType.SUCCESS;
    }

    @Override
    public String getPermissionNode() {
        return "auctionhouse.cmd.expired";
    }

    @Override
    public String getSyntax() {
        return AuctionHouse.getInstance().getLocale().getMessage("commands.syntax.expired").getMessage();
    }

    @Override
    public String getDescription() {
        return AuctionHouse.getInstance().getLocale().getMessage("commands.description.expired").getMessage();
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return null;
    }
}
