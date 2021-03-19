package ca.tweetzy.auctionhouse.commands;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.core.commands.AbstractCommand;
import ca.tweetzy.core.configuration.editor.PluginConfigGui;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * The current file has been created by Kiran Hart
 * Date Created: March 19 2021
 * Time Created: 12:17 a.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class CommandSettings extends AbstractCommand {

    public CommandSettings() {
        super(CommandType.PLAYER_ONLY, "settings");
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        Player player = (Player) sender;
        AuctionHouse.getInstance().getGuiManager().showGUI(player, new PluginConfigGui(AuctionHouse.getInstance(), AuctionHouse.getInstance().getLocale().getMessage("general.prefix").getMessage()));
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "auctionhouse.cmd.settings";
    }

    @Override
    public String getSyntax() {
        return "settings";
    }

    @Override
    public String getDescription() {
        return "Open the in-game config editor";
    }
}
