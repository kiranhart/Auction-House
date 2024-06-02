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

import ca.tweetzy.auctionhouse.api.Identifiable;
import ca.tweetzy.auctionhouse.api.Trackable;
import ca.tweetzy.auctionhouse.api.sync.Synchronize;
import ca.tweetzy.flight.utils.Filterer;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public interface Auction extends Identifiable<UUID>, Trackable, Synchronize {

	UUID getOwner();

	String getOwnerName();

	ItemStack getItem();

	ListingType getType();

	String getCurrency();

	ItemStack getCurrencyItem();

	double getBinPrice();

	String getListedWorld();

	String getListedServer();

	long getListedAt();

	long getExpirationTime();

	void setExpirationTime(long expirationTime);

	void setCurrency(String currency);

	void setBinPrice(double binPrice);

	boolean isBeingBought();

	void setIsBeingBought(boolean isBeingBought);

	default boolean isExpired() {
		return System.currentTimeMillis() >= getExpirationTime();
	}

	default boolean matches(String keywords) {
		return Filterer.searchByItemInfo(keywords, this.getItem()) || this.getOwnerName().equalsIgnoreCase(keywords);
	}

	default List<String> getDisplayLore(@NonNull final ListingDisplayMode displayMode) {
		return new ArrayList<>();
	}
}