package ca.tweetzy.auctionhouse.api;

import ca.tweetzy.auctionhouse.api.sync.Navigable;

public enum SortType implements Navigable<SortType> {

	NEWEST,
	OLDEST,
	HIGHEST_PRICE,
	LOWEST_PRICE;

	@Override
	public Class<SortType> enumClass() {
		return SortType.class;
	}
}
