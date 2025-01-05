package ca.tweetzy.auctionhouse.model.manager;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.auction.Auction;
import ca.tweetzy.auctionhouse.api.manager.KeyValueManager;
import ca.tweetzy.auctionhouse.auction.AuctionedItem;
import ca.tweetzy.auctionhouse.impl.listing.AuctionItem;
import ca.tweetzy.auctionhouse.model.discord.DiscordMessageCreator;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.flight.collection.expiringmap.ExpiringMap;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class ListingManager extends KeyValueManager<UUID, AuctionItem> {

	private final ExpiringMap<UUID, DiscordMessageCreator> PENDING_DISCORD_WEBHOOKS = ExpiringMap.builder().variableExpiration().build();

	public ListingManager() {
		super("Listing");

		this.PENDING_DISCORD_WEBHOOKS.addAsyncExpirationListener((id, hookCreator) -> {
			final AuctionedItem item = AuctionHouse.getAuctionItemManager().getItem(id);
			if (item == null || item.isExpired()) return;

			hookCreator.send();
		});
	}

	public void addListingWebhook(UUID uuid, DiscordMessageCreator hook) {
		this.PENDING_DISCORD_WEBHOOKS.put(uuid, hook, Settings.DISCORD_DELAY_LISTING_TIME.getInt(), TimeUnit.SECONDS);
	}

	public void cancelListingWebhook(UUID uuid) {
		this.PENDING_DISCORD_WEBHOOKS.remove(uuid);
	}

	public void sendPendingDiscordWebhooks() {
		this.PENDING_DISCORD_WEBHOOKS.keySet().forEach(id -> this.PENDING_DISCORD_WEBHOOKS.setExpiration(id, 1L, TimeUnit.NANOSECONDS));
	}

	@Override
	public void load() {
	}
}
