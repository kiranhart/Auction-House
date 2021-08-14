package ca.tweetzy.auctionhouse.commands;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.core.commands.AbstractCommand;
import ca.tweetzy.core.utils.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * The current file has been created by Kiran Hart
 * Date Created: August 11 2021
 * Time Created: 2:27 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class CommandToggleListInfo extends AbstractCommand {

    public CommandToggleListInfo() {
        super(CommandType.PLAYER_ONLY, "togglelistinfo");
    }


    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        Player player = (Player) sender;

        if (AuctionHouse.getInstance().getAuctionPlayerManager().getPlayer(player.getUniqueId()) == null) {
            AuctionHouse.getInstance().getLocale().newMessage(TextUtils.formatText("&cCould not find auction player instance for&f: &e" + player.getName() + "&c creating one now.")).sendPrefixedMessage(Bukkit.getConsoleSender());
            AuctionHouse.getInstance().getAuctionPlayerManager().addPlayer(new AuctionPlayer(player));
        }

        AuctionPlayer auctionPlayer = AuctionHouse.getInstance().getAuctionPlayerManager().getPlayer(player.getUniqueId());
        auctionPlayer.setShowListingInfo(!auctionPlayer.isShowListingInfo());
        AuctionHouse.getInstance().getLocale().getMessage("general.toggled listing." + (auctionPlayer.isShowListingInfo() ? "on" : "off")).sendPrefixedMessage(player);

        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "auctionhouse.cmds.togglelistinfo";
    }

    @Override
    public String getSyntax() {
        return AuctionHouse.getInstance().getLocale().getMessage("commands.syntax.togglelistinfo").getMessage();
    }

    @Override
    public String getDescription() {
        return AuctionHouse.getInstance().getLocale().getMessage("commands.description.togglelistinfo").getMessage();
    }
}
