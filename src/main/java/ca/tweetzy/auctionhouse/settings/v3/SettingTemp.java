package ca.tweetzy.auctionhouse.settings.v3;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.flight.config.ConfigEntry;
import lombok.NonNull;

public abstract class SettingTemp {

	protected static ConfigEntry create(@NonNull final String key, @NonNull final Object value, final String... comments) {
		final ConfigEntry entry = AuctionHouse.getMigrationCoreConfig().createEntry(key, value);
		if (comments != null)
			entry.withComment(String.join(", ", comments));

		return entry;
	}
}
