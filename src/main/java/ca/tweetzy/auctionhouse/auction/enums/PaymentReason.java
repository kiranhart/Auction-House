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


import ca.tweetzy.auctionhouse.lang.AuctionLocale;
import ca.tweetzy.auctionhouse.AuctionHouse;

public enum PaymentReason {

	LISTING_FAILED,
	ITEM_SOLD,
	ADMIN_REMOVED,
	BID_RETURNED;

	public String getTranslation() {
		switch (this) {
			case LISTING_FAILED:
				return AuctionLocale.msg(null, "payments.listing failed");
			case ITEM_SOLD:
				return AuctionLocale.msg(null, "payments.item sold");
			case ADMIN_REMOVED:
				return AuctionLocale.msg(null, "payments.admin removed");
			case BID_RETURNED:
				return AuctionLocale.msg(null, "payments.bid returned");
		}

		return this.name();
	}
}