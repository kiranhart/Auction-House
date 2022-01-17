package ca.tweetzy.auctionhouse.api.events;

import ca.tweetzy.auctionhouse.auction.AuctionAdminLog;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * The current file has been created by Kiran Hart
 * Date Created: January 17 2022
 * Time Created: 1:38 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
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
