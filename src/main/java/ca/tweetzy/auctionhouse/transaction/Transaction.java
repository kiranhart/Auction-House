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

package ca.tweetzy.auctionhouse.transaction;

import ca.tweetzy.auctionhouse.auction.enums.AuctionSaleType;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * The current file has been created by Kiran Hart
 * Date Created: March 19 2021
 * Time Created: 12:53 a.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */

@Getter
@Setter
public class Transaction {

	private final UUID id;
	private final UUID seller;
	private final UUID buyer;
	private final String sellerName;
	private final String buyerName;
	private final Long transactionTime;
	private final ItemStack item;
	private final AuctionSaleType auctionSaleType;
	private final double finalPrice;

	public Transaction(
			@NonNull UUID id,
			@NonNull UUID seller,
			@NonNull UUID buyer,
			@NonNull String sellerName,
			@NonNull String buyerName,
			long transactionTime,
			@NonNull ItemStack item,
			@NonNull AuctionSaleType auctionSaleType,
			double finalPrice
	) {
		this.id = id;
		this.seller = seller;
		this.buyer = buyer;
		this.sellerName = sellerName;
		this.buyerName = buyerName;
		this.transactionTime = transactionTime;
		this.item = item;
		this.auctionSaleType = auctionSaleType;
		this.finalPrice = finalPrice;
	}
}
