package ca.tweetzy.auctionhouse.api.events;

import ca.tweetzy.auctionhouse.auction.AuctionSaleType;
import ca.tweetzy.auctionhouse.auction.AuctionedItem;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * The current file has been created by Kiran Hart
 * Date Created: February 18 2021
 * Time Created: 9:01 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */

@Setter
@Getter
public class AuctionEndEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled;

	private OfflinePlayer originalOwner;
	private OfflinePlayer buyer;
	private AuctionedItem auctionItem;
	private AuctionSaleType saleType;

	public AuctionEndEvent(OfflinePlayer originalOwner, OfflinePlayer buyer, AuctionedItem auctionItem, AuctionSaleType saleType, boolean async) {
		super(async);
		this.originalOwner = originalOwner;
		this.buyer = buyer;
		this.auctionItem = auctionItem;
		this.saleType = saleType;
	}

	public AuctionEndEvent(OfflinePlayer originalOwner, OfflinePlayer buyer, AuctionedItem auctionItem, AuctionSaleType saleType) {
		this(originalOwner, buyer, auctionItem, saleType, true);
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
