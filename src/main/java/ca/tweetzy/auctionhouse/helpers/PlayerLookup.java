package ca.tweetzy.auctionhouse.helpers;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

@UtilityClass
public final class PlayerLookup {

	public @Nullable Player findPlayer(@Nullable String name) {
		if (name == null || name.isBlank()) {
			return null;
		}

		Player exact = Bukkit.getPlayerExact(name);
		if (exact != null) {
			return exact;
		}

		String lower = name.toLowerCase(Locale.ROOT);

		for (Player online : Bukkit.getOnlinePlayers()) {
			String n = online.getName();
			if (n.equalsIgnoreCase(name)) {
				return online;
			}
			if (n.toLowerCase(Locale.ROOT).startsWith(lower)) {
				return online;
			}
		}
		return null;
	}
}
