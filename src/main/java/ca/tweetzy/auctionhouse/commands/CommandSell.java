package ca.tweetzy.auctionhouse.commands;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.auction.AuctionItem;
import ca.tweetzy.auctionhouse.helpers.PlayerHelper;
import ca.tweetzy.core.commands.AbstractCommand;
import ca.tweetzy.core.compatibility.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * The current file has been created by Kiran Hart
 * Date Created: January 12 2021
 * Time Created: 9:17 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class CommandSell extends AbstractCommand {

    final AuctionHouse instance;

    public CommandSell(AuctionHouse instance) {
        super(CommandType.PLAYER_ONLY, "sell");
        this.instance = instance;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        if (args.length == 0) return ReturnType.SYNTAX_ERROR;
        Player player = (Player) sender;

        if (PlayerHelper.getHeldItem(player).getType() == XMaterial.AIR.parseMaterial()) {
            instance.getLocale().getMessage("general.air").sendPrefixedMessage(player);
            return ReturnType.FAILURE;
        }

        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "auctionhouse.cmd.sell";
    }

    @Override
    public String getSyntax() {
        return "sell <basePrice> [bidStart] [bidIncr]";
    }

    @Override
    public String getDescription() {
        return "Used to put an item up for auction";
    }
}
