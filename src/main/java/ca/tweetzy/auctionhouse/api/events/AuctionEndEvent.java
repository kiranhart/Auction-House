package ca.tweetzy.auctionhouse.api.events;

import ca.tweetzy.auctionhouse.auction.AuctionedItem;
import ca.tweetzy.auctionhouse.auction.enums.AuctionSaleType;
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

	private double tax;

	public AuctionEndEvent(OfflinePlayer originalOwner, OfflinePlayer buyer, AuctionedItem auctionItem, AuctionSaleType saleType, double tax, boolean async) {
		super(async);
		this.originalOwner = originalOwner;
		this.buyer = buyer;
		this.auctionItem = auctionItem;
		this.saleType = saleType;
		this.tax = tax;
	}

	public AuctionEndEvent(OfflinePlayer originalOwner, OfflinePlayer buyer, AuctionedItem auctionItem, AuctionSaleType saleType, double tax) {
		this(originalOwner, buyer, auctionItem, saleType, tax, true);
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
