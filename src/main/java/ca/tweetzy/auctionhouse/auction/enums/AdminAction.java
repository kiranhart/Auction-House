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
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * The current file has been created by Kiran Hart
 * Date Created: January 17 2022
 * Time Created: 1:29 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
@AllArgsConstructor
public enum AdminAction {

	RETURN_ITEM(AuctionHouse.getInstance().getLocale().getMessage("admin action.return").getMessage()),
	CLAIM_ITEM(AuctionHouse.getInstance().getLocale().getMessage("admin action.claim").getMessage()),
	DELETE_ITEM(AuctionHouse.getInstance().getLocale().getMessage("admin action.delete").getMessage()),
	COPY_ITEM(AuctionHouse.getInstance().getLocale().getMessage("admin action.copy").getMessage());

	@Getter
	private final String translation;
}
