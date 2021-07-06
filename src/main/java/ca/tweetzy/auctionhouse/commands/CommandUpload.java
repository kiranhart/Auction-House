package ca.tweetzy.auctionhouse.commands;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.auction.AuctionItem;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.auctionhouse.transaction.Transaction;
import ca.tweetzy.core.commands.AbstractCommand;
import ca.tweetzy.core.utils.TextUtils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: April 15 2021
 * Time Created: 4:17 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class CommandUpload extends AbstractCommand {

    public CommandUpload() {
        super(CommandType.CONSOLE_OK, "upload");
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        if (!Settings.DATABASE_USE.getBoolean()) {
            AuctionHouse.getInstance().getLocale().newMessage(TextUtils.formatText("&cYou must be using a database to use this command")).sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        if (AuctionHouse.getInstance().getData().contains("auction items") && AuctionHouse.getInstance().getData().isList("auction items")) {
            List<AuctionItem> items = AuctionHouse.getInstance().getData().getStringList("auction items").stream().map(AuctionAPI.getInstance()::convertBase64ToObject).map(object -> (AuctionItem) object).collect(Collectors.toList());
            items.forEach(AuctionHouse.getInstance().getAuctionItemManager()::addItem);
        }

        if (AuctionHouse.getInstance().getData().contains("transactions") && AuctionHouse.getInstance().getData().isList("transactions")) {
            List<Transaction> transactions = AuctionHouse.getInstance().getData().getStringList("transactions").stream().map(AuctionAPI.getInstance()::convertBase64ToObject).map(object -> (Transaction) object).collect(Collectors.toList());
            transactions.forEach(AuctionHouse.getInstance().getTransactionManager()::addTransaction);
        }

        AuctionHouse.getInstance().getData().set("auction items", null);
        AuctionHouse.getInstance().getData().set("transactions", null);
        AuctionHouse.getInstance().getData().save();

        AuctionHouse.getInstance().getDataManager().saveItems(new ArrayList<>(AuctionHouse.getInstance().getAuctionItemManager().getAuctionItems().values()), true);
        AuctionHouse.getInstance().getDataManager().saveTransactions(AuctionHouse.getInstance().getTransactionManager().getTransactions(), true);

        AuctionHouse.getInstance().getLocale().newMessage(TextUtils.formatText("&aLoaded file items/transactions and saved them to the database.")).sendPrefixedMessage(sender);
        return ReturnType.SUCCESS;
    }

    @Override
    public String getPermissionNode() {
        return "auctionhouse.cmd.upload";
    }

    @Override
    public String getSyntax() {
        return AuctionHouse.getInstance().getLocale().getMessage("commands.syntax.upload").getMessage();
    }

    @Override
    public String getDescription() {
        return AuctionHouse.getInstance().getLocale().getMessage("commands.description.upload").getMessage();
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return null;
    }
}
