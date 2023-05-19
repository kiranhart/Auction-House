/*
 * Auction House
 * Copyright 2023 Kiran Hart
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

public enum PaymentReason {

	LISTING_FAILED,
	ITEM_SOLD,
	ADMIN_REMOVED,
	BID_RETURNED;

	public String getTranslation() {
		switch (this) {
			case LISTING_FAILED:
				return AuctionHouse.getInstance().getLocale().getMessage("payments.listing failed").getMessage();
			case ITEM_SOLD:
				return AuctionHouse.getInstance().getLocale().getMessage("payments.item sold").getMessage();
			case ADMIN_REMOVED:
				return AuctionHouse.getInstance().getLocale().getMessage("payments.admin removed").getMessage();
			case BID_RETURNED:
				return AuctionHouse.getInstance().getLocale().getMessage("payments.bid returned").getMessage();
		}

		return this.name();
	}
}