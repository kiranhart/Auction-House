package ca.tweetzy.auctionhouse.auction.enums;

import ca.tweetzy.auctionhouse.AuctionHouse;

/**
 * The current file has been created by Kiran Hart
 * Date Created: June 18 2021
 * Time Created: 3:00 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public enum AuctionSortType {

	RECENT("Recent"),
	PRICE("Price");

	final String type;

	AuctionSortType(String type) {
		this.type = type;
	}

	public String getTranslatedType() {
		switch (this) {
			case PRICE:
				return AuctionHouse.getInstance().getLocale().getMessage("auction_filter.sort_order.price").getMessage();
			case RECENT:
				return AuctionHouse.getInstance().getLocale().getMessage("auction_filter.sort_order.recent").getMessage();
			default:
				return getType();
		}
	}

	public String getType() {
		return type;
	}

	public AuctionSortType next() {
		return values()[(this.ordinal() + 1) % values().length];
	}
}
