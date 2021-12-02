package ca.tweetzy.auctionhouse.api.hook;

import com.gmail.nossr50.api.AbilityAPI;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * The current file has been created by Kiran Hart
 * Date Created: October 09 2021
 * Time Created: 1:35 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
@UtilityClass
public final class McMMOHook {

	private boolean isEnabled() {
		return Bukkit.getPluginManager().getPlugin("mcMMO") != null;
	}

	public boolean isUsingAbility(@NonNull final Player player) {
		if (!isEnabled()) return false;
		return AbilityAPI.isAnyAbilityEnabled(player);
	}
}
