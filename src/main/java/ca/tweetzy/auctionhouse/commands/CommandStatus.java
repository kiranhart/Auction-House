package ca.tweetzy.auctionhouse.commands;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.auction.AuctionItem;
import ca.tweetzy.core.commands.AbstractCommand;
import ca.tweetzy.core.utils.TextUtils;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * The current file has been created by Kiran Hart
 * Date Created: May 23 2021
 * Time Created: 11:59 a.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class CommandStatus extends AbstractCommand {

    public CommandStatus()  {
        super(CommandType.CONSOLE_OK, "status");
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        AuctionHouse.newChain().async(() -> {
            List<AuctionItem> items = AuctionHouse.getInstance().getAuctionItemManager().getAuctionItems();

            int totalItems = items.size();
            int activeItems = (int) items.stream().filter(item -> !item.isExpired()).count();
            int expiredItems = (int) items.stream().filter(AuctionItem::isExpired).count();
            int totalTransactions = AuctionHouse.getInstance().getTransactionManager().getTransactions().size();

            sender.sendMessage(TextUtils.formatText("&eAuction House Statistics"));
            sender.sendMessage(TextUtils.formatText(String.format("&eRunning version &a%s", AuctionHouse.getInstance().getDescription().getVersion())));
            sender.sendMessage(TextUtils.formatText(""));
            sender.sendMessage(TextUtils.formatText(String.format("&7Total Items&f: &e%d\n&7Total Transactions&f: &e%d\n&7Active Items&f: &e%d\n&7Expired Items&f: &e%d", totalItems, totalTransactions, activeItems, expiredItems)));

        }).execute();
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
        return "Return plugin statistics";
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return null;
    }
}
