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

package ca.tweetzy.auctionhouse.auction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * Date Created: April 04 2022
 * Time Created: 8:30 a.m.
 *
 * @author Kiran Hart
 */
@AllArgsConstructor
@Getter
public final class MinItemPrice {

	private UUID uuid;
	private ItemStack itemStack;
	private double price;

	public MinItemPrice(ItemStack itemStack, double price) {
		this(UUID.randomUUID(), itemStack, price);
	}
}
