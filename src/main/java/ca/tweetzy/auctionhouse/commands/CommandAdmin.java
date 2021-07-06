package ca.tweetzy.auctionhouse.commands;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.commands.AbstractCommand;
import ca.tweetzy.core.compatibility.XMaterial;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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
                for (UUID id : AuctionHouse.getInstance().getAuctionItemManager().getAuctionItems().keySet()) {
                    AuctionHouse.getInstance().getAuctionItemManager().getAuctionItems().get(id).setExpired(true);
                }
                AuctionHouse.getInstance().getLocale().getMessage("general.endedallauctions").sendPrefixedMessage(sender);
                break;
            case "relistall":
                int relistTime = args.length == 1 ? Settings.DEFAULT_AUCTION_TIME.getInt() : Integer.parseInt(args[1]);
                for (UUID id : AuctionHouse.getInstance().getAuctionItemManager().getAuctionItems().keySet()) {
                    if (AuctionHouse.getInstance().getAuctionItemManager().getAuctionItems().get(id).isExpired()) {
                        AuctionHouse.getInstance().getAuctionItemManager().getAuctionItems().get(id).setRemainingTime(relistTime);
                        AuctionHouse.getInstance().getAuctionItemManager().getAuctionItems().get(id).setExpired(false);
                    }
                }
                AuctionHouse.getInstance().getLocale().getMessage("general.relisteditems").sendPrefixedMessage(sender);
                break;
            case "cleanunknownusers":
                // Don't tell ppl that this exists
                AuctionHouse.getInstance().getAuctionItemManager().removeUnknownOwnerItems();
                break;
            case "clearall":
                // Don't tell ppl that this exists
                AuctionHouse.getInstance().getAuctionItemManager().getAuctionItems().clear();
            case "clean":
                // Don't tell ppl that this exists
                for (UUID id : AuctionHouse.getInstance().getAuctionItemManager().getAuctionItems().keySet()) {
                    ItemStack deserialize = AuctionAPI.getInstance().deserializeItem(AuctionHouse.getInstance().getAuctionItemManager().getAuctionItems().get(id).getRawItem());
                    if (deserialize == null || XMaterial.isAir(XMaterial.matchXMaterial(deserialize))) {
                        AuctionHouse.getInstance().getAuctionItemManager().sendToGarbage(AuctionHouse.getInstance().getAuctionItemManager().getAuctionItems().get(id));
                    }
                }
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
