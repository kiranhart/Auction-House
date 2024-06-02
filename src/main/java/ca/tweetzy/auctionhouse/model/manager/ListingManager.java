package ca.tweetzy.auctionhouse.model.manager;

import ca.tweetzy.auctionhouse.api.auction.Auction;
import ca.tweetzy.auctionhouse.api.manager.KeyValueManager;

import java.util.UUID;

public final class ListingManager extends KeyValueManager<UUID, Auction> {

	public ListingManager() {
		super("Listing");
	}

	@Override
	public void load() {

	}
}
