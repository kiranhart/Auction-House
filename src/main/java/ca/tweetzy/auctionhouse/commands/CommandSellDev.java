package ca.tweetzy.auctionhouse.commands;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.guis.sell.GUISellListingType;
import ca.tweetzy.auctionhouse.guis.sell.GUISellPlaceItem;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.commands.AbstractCommand;
import ca.tweetzy.core.utils.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/**
 * The current file has been created by Kiran Hart
 * Date Created: August 22 2021
 * Time Created: 6:51 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public final class CommandSellDev extends AbstractCommand {

	public CommandSellDev() {
		super(CommandType.PLAYER_ONLY, "selldev");
	}

	@Override
	protected ReturnType runCommand(CommandSender sender, String... args) {
		Player player = (Player) sender;

		if (CommandMiddleware.handle(player) == ReturnType.FAILURE) return ReturnType.FAILURE;

		if (AuctionHouse.getInstance().getAuctionPlayerManager().getPlayer(player.getUniqueId()) == null) {
			AuctionHouse.getInstance().getLocale().newMessage(TextUtils.formatText("&cCould not find auction player instance for&f: &e" + player.getName() + "&c creating one now.")).sendPrefixedMessage(Bukkit.getConsoleSender());
			AuctionHouse.getInstance().getAuctionPlayerManager().addPlayer(new AuctionPlayer(player));
		}


		AuctionPlayer auctionPlayer = AuctionHouse.getInstance().getAuctionPlayerManager().getPlayer(player.getUniqueId());

		if (args.length == 0)
			AuctionHouse.getInstance().getGuiManager().showGUI(player, new GUISellListingType(auctionPlayer, selected -> {
				AuctionHouse.getInstance().getGuiManager().showGUI(player, new GUISellPlaceItem(auctionPlayer, GUISellPlaceItem.ViewMode.SINGLE_ITEM, selected));
			}));

		if (args.length == 1) {

		}

		return ReturnType.SUCCESS;
	}

	private boolean checkBasePrice(final Player player, final double val, boolean allowMinusOne) {
		if (val < Settings.MIN_AUCTION_PRICE.getDouble()) {
			if (allowMinusOne && val <= -1) return true;
			AuctionHouse.getInstance().getLocale().getMessage("pricing.minbaseprice").processPlaceholder("price", Settings.MIN_AUCTION_PRICE.getDouble()).sendPrefixedMessage(player);
			return false;
		}

		if (val > Settings.MAX_AUCTION_PRICE.getDouble()) {
			AuctionHouse.getInstance().getLocale().getMessage("pricing.maxbaseprice").processPlaceholder("price", Settings.MAX_AUCTION_PRICE.getDouble()).sendPrefixedMessage(player);
			return false;
		}
		return true;
	}

	@Override
	protected List<String> onTab(CommandSender sender, String... args) {
		if (args.length == 1)
			return Arrays.asList(AuctionHouse.getInstance().getLocale().getMessage("commands.sell.args.suggestion one").getMessage().split(" "));
		if (args.length == 2)
			return Arrays.asList(AuctionHouse.getInstance().getLocale().getMessage("commands.sell.args.suggestion two").getMessage().split(" "));
		if (args.length == 3)
			return Arrays.asList(AuctionHouse.getInstance().getLocale().getMessage("commands.sell.args.suggestion three").getMessage().split(" "));
		return null;
	}

	@Override
	public String getPermissionNode() {
		return "auctionhouse.cmd.sell";
	}

	@Override
	public String getSyntax() {
		return AuctionHouse.getInstance().getLocale().getMessage("commands.syntax.sell").getMessage();
	}

	@Override
	public String getDescription() {
		return AuctionHouse.getInstance().getLocale().getMessage("commands.description.sell").getMessage();
	}
}
