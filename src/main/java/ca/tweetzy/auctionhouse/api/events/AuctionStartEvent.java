package ca.tweetzy.auctionhouse.api.events;

import ca.tweetzy.auctionhouse.auction.AuctionItem;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * The current file has been created by Kiran Hart
 * Date Created: February 27 2021
 * Time Created: 4:59 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class AuctionStartEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    private Player seller;
    private AuctionItem auctionItem;

    public AuctionStartEvent(Player seller, AuctionItem auctionItem) {
        this.seller = seller;
        this.auctionItem = auctionItem;
    }

    public Player getSeller() {
        return seller;
    }

    public AuctionItem getAuctionItem() {
        return auctionItem;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
