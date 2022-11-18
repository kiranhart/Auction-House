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

package ca.tweetzy.auctionhouse.auction.enums;

import ca.tweetzy.auctionhouse.AuctionHouse;

/**
 * The current file has been created by Kiran Hart
 * Date Created: June 18 2021
 * Time Created: 3:00 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public enum AuctionSortType {

	RECENT("Recent"),
	PRICE("Price");

	final String type;

	AuctionSortType(String type) {
		this.type = type;
	}

	public String getTranslatedType() {
		switch (this) {
			case PRICE:
				return AuctionHouse.getInstance().getLocale().getMessage("auction_filter.sort_order.price").getMessage();
			case RECENT:
				return AuctionHouse.getInstance().getLocale().getMessage("auction_filter.sort_order.recent").getMessage();
			default:
				return getType();
		}
	}

	public String getType() {
		return type;
	}

	public AuctionSortType next() {
		return values()[(this.ordinal() + 1) % values().length];
	}
}
