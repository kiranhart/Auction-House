package ca.tweetzy.auctionhouse.api.events;

import ca.tweetzy.auctionhouse.auction.AuctionedItem;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * The current file has been created by Kiran Hart
 * Date Created: September 27 2021
 * Time Created: 11:18 a.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
@Getter
@Setter
public final class AuctionBidEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled;

	private final OfflinePlayer bidder;
	private final AuctionedItem auctionedItem;
	private final double newBidAmount;

	public AuctionBidEvent(OfflinePlayer bidder, AuctionedItem auctionedItem, double newBidAmount, boolean async) {
		super(async);
		this.bidder = bidder;
		this.auctionedItem = auctionedItem;
		this.newBidAmount = newBidAmount;
	}

	public AuctionBidEvent(OfflinePlayer bidder, AuctionedItem auctionedItem, double newBidAmount) {
		this(bidder, auctionedItem, newBidAmount, false);
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
