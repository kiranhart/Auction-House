package ca.tweetzy.auctionhouse.commands;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.auction.AuctionFilterItem;
import ca.tweetzy.auctionhouse.auction.AuctionItemCategory;
import ca.tweetzy.auctionhouse.guis.filter.GUIFilterWhitelist;
import ca.tweetzy.auctionhouse.helpers.PlayerHelper;
import ca.tweetzy.core.commands.AbstractCommand;
import ca.tweetzy.core.compatibility.XMaterial;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: June 22 2021
 * Time Created: 3:18 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class CommandFilter extends AbstractCommand {

    public CommandFilter() {
        super(CommandType.PLAYER_ONLY, "filter");
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        Player player = (Player) sender;
        if (AuctionAPI.tellMigrationStatus(player)) return ReturnType.FAILURE;

        if (args.length == 0) {
            AuctionHouse.getInstance().getGuiManager().showGUI(player, new GUIFilterWhitelist());
            return ReturnType.SUCCESS;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("additem")) {
            boolean isValid = false;
            for (AuctionItemCategory value : AuctionItemCategory.values()) {
                if (args[1].toUpperCase().equals(value.name())) {
                    isValid = true;
                    break;
                }
            }

            if (isValid && AuctionItemCategory.valueOf(args[1].toUpperCase()).isWhitelistAllowed()) {

                ItemStack held = PlayerHelper.getHeldItem(player);
                if (held.getType() == XMaterial.AIR.parseMaterial()) {
                    AuctionHouse.getInstance().getLocale().getMessage("general.filter air").sendPrefixedMessage(player);
                    return ReturnType.FAILURE;
                }


                if (AuctionHouse.getInstance().getFilterManager().getFilteredItem(held) != null && AuctionHouse.getInstance().getFilterManager().getFilteredItem(held).getCategory() == AuctionItemCategory.valueOf(args[1].toUpperCase())) {
                    AuctionHouse.getInstance().getLocale().getMessage("general.filteritemaddedalready").sendPrefixedMessage(player);
                    return ReturnType.FAILURE;
                }

                AuctionFilterItem filterItem = new AuctionFilterItem(held, AuctionItemCategory.valueOf(args[1].toUpperCase()));
                AuctionHouse.getInstance().getFilterManager().addFilterItem(filterItem);
                AuctionHouse.getInstance().getLocale().getMessage("general.addeditemtofilterwhitelist").processPlaceholder("item_name", AuctionAPI.getInstance().getItemName(held)).processPlaceholder("filter_category", args[1]).sendPrefixedMessage(player);
            }
        }

        return ReturnType.SUCCESS;
    }

    @Override
    public String getPermissionNode() {
        return "auctionhouse.cmd.filter";
    }

    @Override
    public String getSyntax() {
        return AuctionHouse.getInstance().getLocale().getMessage("commands.syntax.filter").getMessage();
    }

    @Override
    public String getDescription() {
        return AuctionHouse.getInstance().getLocale().getMessage("commands.description.filter").getMessage();
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        if (args.length == 1) return Collections.singletonList("additem");
        if (args.length == 2)
            return Arrays.stream(AuctionItemCategory.values()).filter(AuctionItemCategory::isWhitelistAllowed).map(AuctionItemCategory::getTranslatedType).collect(Collectors.toList());
        return null;
    }
}
