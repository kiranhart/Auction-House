/*
 * Auction House
 * Copyright 2018-2022 Kiran Hart
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ca.tweetzy.auctionhouse.events;

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
