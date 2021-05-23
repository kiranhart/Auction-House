package ca.tweetzy.auctionhouse.commands;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.auction.AuctionItem;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.commands.AbstractCommand;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

/**
 * The current file has been created by Kiran Hart
 * Date Created: May 23 2021
 * Time Created: 12:14 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class CommandAdmin extends AbstractCommand {

    public CommandAdmin() {
        super(CommandType.CONSOLE_OK, "admin");
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        if (args.length < 1) return ReturnType.FAILURE;

        switch (args[0].toLowerCase()) {
            case "endall":
                AuctionHouse.getInstance().getAuctionItemManager().getAuctionItems().forEach(item -> item.setExpired(true));
                AuctionHouse.getInstance().getLocale().getMessage("general.endedallauctions").sendPrefixedMessage(sender);
                break;
            case "relistall":
                int relistTime = args.length == 1 ? Settings.DEFAULT_AUCTION_TIME.getInt() : Integer.parseInt(args[1]);
                AuctionHouse.getInstance().getAuctionItemManager().getAuctionItems().stream().filter(AuctionItem::isExpired).forEach(item -> {
                    item.setRemainingTime(relistTime);
                    item.setExpired(false);
                });
                AuctionHouse.getInstance().getLocale().getMessage("general.relisteditems").sendPrefixedMessage(sender);
                break;
        }

        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        if (args.length == 1) return Arrays.asList("endall", "relistall");
        if (args.length == 2 && args[0].equalsIgnoreCase("relistAll")) return Arrays.asList("1", "2", "3", "4", "5");
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "auctionhouse.cmd.admin";
    }

    @Override
    public String getSyntax() {
        return "admin <endall|relistAll> [value]";
    }

    @Override
    public String getDescription() {
        return "Admin options for auction house.";
    }
}
