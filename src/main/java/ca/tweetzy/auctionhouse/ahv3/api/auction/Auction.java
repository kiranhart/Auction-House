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

package ca.tweetzy.auctionhouse.ahv3.api.auction;

import ca.tweetzy.auctionhouse.ahv3.api.Trackable;
import ca.tweetzy.auctionhouse.ahv3.api.Identifiable;
import ca.tweetzy.auctionhouse.ahv3.api.ListingType;
import ca.tweetzy.auctionhouse.ahv3.api.Synchronize;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.UUID;

public interface Auction extends Identifiable, Trackable, Synchronize {

	UUID getOwner();

	String getOwnerName();

	ItemStack getItem();

	ListingType getType();

	BigDecimal getBinPrice();

	String getListedWorld();

	String getListedServer();

	long getListedAt();

	long getExpirationTime();

	void setExpirationTime(long expirationTime);

	void setBinPrice(BigDecimal binPrice);
}
