package ca.tweetzy.auctionhouse.impl;

import ca.tweetzy.auctionhouse.api.auction.Cart;
import ca.tweetzy.auctionhouse.auction.AuctionedItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class AuctionCart implements Cart {

	private final List<AuctionedItem> items;

	public AuctionCart() {
		this.items = Collections.synchronizedList(new ArrayList<>());
	}

	@Override
	public List<AuctionedItem> getItems() {
		synchronized (items) {
			return Collections.unmodifiableList(new ArrayList<>(items));
		}
	}

	@Override
	public void addItem(AuctionedItem item) {
		synchronized (items) {
			items.add(item);
		}
	}

	@Override
	public void removeItem(AuctionedItem item) {
		synchronized (items) {
			items.remove(item);
		}
	}

	@Override
	public void clearItems() {
		synchronized (items) {
			items.clear();
		}
	}

	@Override
	public int getItemCount() {
		synchronized (items) {
			return items.size();
		}
	}
}
