package ca.tweetzy.auctionhouse.api.event;

import ca.tweetzy.auctionhouse.api.auction.RequestTransaction;
import ca.tweetzy.auctionhouse.auction.AuctionedItem;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Setter
@Getter
public final class AuctionRequestCompleteEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled;

	private AuctionedItem originalListing;
	private RequestTransaction requestTransaction;

	public AuctionRequestCompleteEvent(AuctionedItem originalListing, RequestTransaction requestTransaction) {
		super(false);
		this.originalListing = originalListing;
		this.requestTransaction = requestTransaction;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
