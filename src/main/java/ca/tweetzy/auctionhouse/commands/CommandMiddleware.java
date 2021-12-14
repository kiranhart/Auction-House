package ca.tweetzy.auctionhouse.commands;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.api.hook.FloodGateHook;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.commands.AbstractCommand;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;

/**
 * The current file has been created by Kiran Hart
 * Date Created: December 14 2021
 * Time Created: 2:19 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
@UtilityClass
public final class CommandMiddleware {

	public AbstractCommand.ReturnType handle(@NonNull final Player player) {
		if (AuctionAPI.tellMigrationStatus(player)) return AbstractCommand.ReturnType.FAILURE;

		if (Settings.BLOCKED_WORLDS.getStringList().contains(player.getWorld().getName())) {
			AuctionHouse.getInstance().getLocale().getMessage("general.disabled in world").sendPrefixedMessage(player);
			return AbstractCommand.ReturnType.FAILURE;
		}

		if (Settings.USE_AUCTION_CHEST_MODE.getBoolean()) {
			AuctionHouse.getInstance().getLocale().getMessage("general.visit auction chest").sendPrefixedMessage(player);
			return AbstractCommand.ReturnType.FAILURE;
		}

		if (AuctionHouse.getInstance().getAuctionBanManager().checkAndHandleBan(player)) {
			return AbstractCommand.ReturnType.FAILURE;
		}

		if (FloodGateHook.isFloodGateUser(player)) return AbstractCommand.ReturnType.FAILURE;

		return AbstractCommand.ReturnType.SUCCESS;
	}
}
