/*
 * Auction House
 * Copyright 2022 Kiran Hart
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
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum AuctionStatisticType {

	CREATED_AUCTION("Created Auction"),
	CREATED_BIN("Created Bin"),

	SOLD_AUCTION("Sold Auctions"),
	SOLD_BIN("Sold Bins"),

	MONEY_SPENT("Money Spent"),
	MONEY_EARNED("Money Earned");

	@Getter
	private final String type;

	public String getTranslatedType() {
		switch (this) {
			case CREATED_AUCTION:
				return AuctionHouse.getInstance().getLocale().getMessage("auction_statistic.created_auction").getMessage();
			case CREATED_BIN:
				return AuctionHouse.getInstance().getLocale().getMessage("auction_statistic.created_bin").getMessage();
			case SOLD_AUCTION:
				return AuctionHouse.getInstance().getLocale().getMessage("auction_statistic.sold_auctions").getMessage();
			case SOLD_BIN:
				return AuctionHouse.getInstance().getLocale().getMessage("auction_statistic.sold_bins").getMessage();
			case MONEY_SPENT:
				return AuctionHouse.getInstance().getLocale().getMessage("auction_statistic.money_spent").getMessage();
			case MONEY_EARNED:
				return AuctionHouse.getInstance().getLocale().getMessage("auction_statistic.money_earned").getMessage();
		}
		return getType();
	}

	public AuctionStatisticType next() {
		return values()[(this.ordinal() + 1) % values().length];
	}
}
