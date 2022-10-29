package ca.tweetzy.auctionhouse.commands;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.guis.statistics.GUIStatisticTarget;
import ca.tweetzy.auctionhouse.guis.statistics.GUIStatisticViewSelect;
import ca.tweetzy.core.commands.AbstractCommand;
import ca.tweetzy.core.utils.TextUtils;
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
public class CommandStats extends AbstractCommand {

	public CommandStats() {
		super(CommandType.PLAYER_ONLY, "stats");
	}

	@Override
	protected ReturnType runCommand(CommandSender sender, String... args) {
		final Player player = (Player) sender;

		if (CommandMiddleware.handle(player) == ReturnType.FAILURE) return ReturnType.FAILURE;

		final AuctionHouse instance = AuctionHouse.getInstance();
		final AuctionPlayer user = instance.getAuctionPlayerManager().getPlayer(player.getUniqueId());

		if (user == null) {
			instance.getLocale().newMessage(TextUtils.formatText("&cCould not find auction player instance for&f: &e" + player.getName() + "&c creating one now.")).sendPrefixedMessage(Bukkit.getConsoleSender());
			instance.getAuctionPlayerManager().addPlayer(new AuctionPlayer(player));
		}

		if (args.length == 0) {
			instance.getGuiManager().showGUI(player, new GUIStatisticViewSelect(user));
			return ReturnType.SUCCESS;
		}

		final Player target = Bukkit.getPlayerExact(args[0]);

		if (target == null) {
			instance.getLocale().getMessage("general.playernotfound").processPlaceholder("player", args[0]).sendPrefixedMessage(sender);
			return ReturnType.FAILURE;
		}

		final AuctionPlayer targetAuctionPlayer = instance.getAuctionPlayerManager().getPlayer(target.getUniqueId());
		instance.getGuiManager().showGUI(player, new GUIStatisticTarget(user, targetAuctionPlayer));

		return ReturnType.SUCCESS;
	}

	@Override
	public String getPermissionNode() {
		return "auctionhouse.cmd.stats";
	}

	@Override
	public String getSyntax() {
		return AuctionHouse.getInstance().getLocale().getMessage("commands.syntax.stats").getMessage();
	}

	@Override
	public String getDescription() {
		return AuctionHouse.getInstance().getLocale().getMessage("commands.description.stats").getMessage();
	}

	@Override
	protected List<String> onTab(CommandSender sender, String... args) {
		return null;
	}
}
