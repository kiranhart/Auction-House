package ca.tweetzy.auctionhouse.api.auction;

import ca.tweetzy.auctionhouse.auction.AuctionedItem;

import java.util.List;

public interface Cart {

	List<AuctionedItem> getItems();
}
