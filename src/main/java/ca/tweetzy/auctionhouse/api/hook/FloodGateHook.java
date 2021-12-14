package ca.tweetzy.auctionhouse.api.hook;

import ca.tweetzy.auctionhouse.settings.Settings;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;

/**
 * The current file has been created by Kiran Hart
 * Date Created: December 14 2021
 * Time Created: 1:57 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
@UtilityClass
public final class FloodGateHook {

	private boolean isFloodGateActive() {
		return Bukkit.getServer().getPluginManager().isPluginEnabled("floodgate");
	}

	public boolean isFloodGateUser(@NonNull final Player player) {
		if (!isFloodGateActive()) return false;
		return !Settings.ALLOW_FLOODGATE_PLAYERS.getBoolean() && FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId());
	}
}
