package ca.tweetzy.auctionhouse.model.manager;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.settings.Settings;
import lombok.NonNull;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public final class CooldownManager {

	private final NamespacedKey MAIN_CMD_COOLDOWN_KEY;

	public CooldownManager(@NonNull final JavaPlugin javaPlugin) {
		MAIN_CMD_COOLDOWN_KEY = new NamespacedKey(javaPlugin, "AuctionHouseCMDCooldownMain");
	}

	public boolean isInCooldown(@NonNull final Player player) {
		final long COOLDOWN_TIME = Settings.CMD_COOLDOWN.getLong();
		if (COOLDOWN_TIME <= 0) return false;

		long cooldownEndsAt = 0;

		if (player.getPersistentDataContainer().has(MAIN_CMD_COOLDOWN_KEY, PersistentDataType.LONG)) {
			cooldownEndsAt = player.getPersistentDataContainer().get(MAIN_CMD_COOLDOWN_KEY, PersistentDataType.LONG);
		}

		if (cooldownEndsAt > System.currentTimeMillis()) {
			AuctionHouse.getInstance().getLocale().getMessage("general.cooldown.command").processPlaceholder("time", formatTime(cooldownEndsAt - System.currentTimeMillis())).sendPrefixedMessage(player);
			return true;
		}

		player.getPersistentDataContainer().set(MAIN_CMD_COOLDOWN_KEY, PersistentDataType.LONG, System.currentTimeMillis() + (COOLDOWN_TIME * 1000L));
		return false;
	}

	// TODO MOVE INTO FLIGHT
	private String formatTime(long milliseconds) {
		long seconds = milliseconds / 1000;
		long days = seconds / 86400;
		seconds %= 86400;
		long hours = seconds / 3600;
		seconds %= 3600;
		long minutes = seconds / 60;
		seconds %= 60;

		StringBuilder result = new StringBuilder();
		if (days > 0) {
			result.append(days).append("d ");
		}
		if (hours > 0) {
			result.append(hours).append("h ");
		}
		if (minutes > 0) {
			result.append(minutes).append("m ");
		}
		if (seconds > 0 || result.length() == 0) {
			result.append(seconds).append("s");
		}

		return result.toString().trim();
	}

}
