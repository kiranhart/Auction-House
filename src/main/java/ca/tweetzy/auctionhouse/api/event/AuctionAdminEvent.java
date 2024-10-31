package ca.tweetzy.auctionhouse.api.event;

import ca.tweetzy.auctionhouse.auction.AuctionAdminLog;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@Setter
public final class AuctionAdminEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled;

	private final AuctionAdminLog auctionAdminLog;

	public AuctionAdminEvent(AuctionAdminLog auctionAdminLog) {
		this.auctionAdminLog = auctionAdminLog;
	}


	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}