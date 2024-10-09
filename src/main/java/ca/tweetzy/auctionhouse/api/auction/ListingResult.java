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

package ca.tweetzy.auctionhouse.api.auction;

public enum ListingResult {

	SUCCESS,
	CANCELED_CONFIRMATION,

	CANNOT_PAY_LISTING_FEE,
	LISTING_LIMIT_REACHED,
	PLAYER_INSTANCE_NOT_FOUND,

	CANNOT_SELL_REPAIRED_ITEM,
	CANNOT_SELL_DAMAGED_ITEM,
	CANNOT_SELL_BUNDLE_ITEM,

	MINIMUM_PRICE_NOT_MET,
	ABOVE_MAXIMUM_PRICE,

	UNKNOWN,
	EVENT_CANCELED

}