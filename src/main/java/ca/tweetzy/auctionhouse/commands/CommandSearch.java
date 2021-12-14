package ca.tweetzy.auctionhouse.commands;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.guis.GUIAuctionHouse;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.commands.AbstractCommand;
import ca.tweetzy.core.utils.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * The current file has been created by Kiran Hart
 * Date Created: March 23 2021
 * Time Created: 9:36 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class CommandSearch extends AbstractCommand {

	public CommandSearch() {
		super(CommandType.PLAYER_ONLY, "search");
	}

	@Override
	protected ReturnType runCommand(CommandSender sender, String... args) {
		if (args.length <= 0) return ReturnType.SYNTAX_ERROR;
		Player player = (Player) sender;

		if (CommandMiddleware.handle(player) == ReturnType.FAILURE) return ReturnType.FAILURE;

		StringBuilder builder = new StringBuilder();
		for (String arg : args) {
			builder.append(arg).append(" ");
		}

		if (AuctionHouse.getInstance().getAuctionPlayerManager().getPlayer(player.getUniqueId()) == null) {
			AuctionHouse.getInstance().getLocale().newMessage(TextUtils.formatText("&cCould not find auction player instance for&f: &e" + player.getName() + "&c creating one now.")).sendPrefixedMessage(Bukkit.getConsoleSender());
			AuctionHouse.getInstance().getAuctionPlayerManager().addPlayer(new AuctionPlayer(player));
		}

		AuctionHouse.getInstance().getGuiManager().showGUI(player, new GUIAuctionHouse(AuctionHouse.getInstance().getAuctionPlayerManager().getPlayer(player.getUniqueId()), builder.toString().trim()));
		return ReturnType.SUCCESS;
	}

	@Override
	public String getPermissionNode() {
		return "auctionhouse.cmd.search";
	}

	@Override
	public String getSyntax() {
		return AuctionHouse.getInstance().getLocale().getMessage("commands.syntax.search").getMessage();
	}

	@Override
	public String getDescription() {
		return AuctionHouse.getInstance().getLocale().getMessage("commands.description.search").getMessage();
	}

	@Override
	protected List<String> onTab(CommandSender sender, String... args) {
		return null;
	}
}
