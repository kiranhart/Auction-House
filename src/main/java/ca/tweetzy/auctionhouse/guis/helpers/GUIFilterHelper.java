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

package ca.tweetzy.auctionhouse.guis.helpers;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.auction.AuctionedItem;
import ca.tweetzy.auctionhouse.auction.enums.AuctionItemCategory;
import ca.tweetzy.auctionhouse.auction.enums.AuctionSaleType;
import ca.tweetzy.auctionhouse.auction.enums.AuctionSortType;
import ca.tweetzy.auctionhouse.api.currency.AbstractCurrency;
import ca.tweetzy.auctionhouse.settings.Settings;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Utility class for common GUI filtering and sorting operations
 */
public final class GUIFilterHelper {

	private GUIFilterHelper() {
	}

	/**
	 * Creates a predicate that filters items by world if PER_WORLD_ITEMS is enabled
	 *
	 * @param player The player whose world to check
	 * @return A predicate that filters items by world
	 */
	public static Predicate<AuctionedItem> perWorldFilter(Player player) {
		if (!Settings.PER_WORLD_ITEMS.getBoolean()) {
			return item -> true;
		}
		final String worldName = player.getWorld().getName();
		return item -> item.getListedWorld() == null || worldName.equals(item.getListedWorld());
	}

	/**
	 * Creates a predicate that filters items by category
	 *
	 * @param category The category to filter by
	 * @param checkFilterCriteria Function to check filter criteria
	 * @return A predicate that filters items by category
	 */
	public static Predicate<AuctionedItem> categoryFilter(AuctionItemCategory category, Function<AuctionedItem, Boolean> checkFilterCriteria) {
		if (category == AuctionItemCategory.ALL) {
			return item -> true;
		}
		if (category == AuctionItemCategory.SELF) {
			return item -> false; // Will be handled separately with owner filter
		}
		if (category == AuctionItemCategory.SEARCH) {
			return item -> false; // Will be handled separately with search filter
		}
		return item -> checkFilterCriteria.apply(item);
	}

	/**
	 * Creates a predicate that filters items by owner UUID
	 *
	 * @param ownerUuid The owner UUID to filter by
	 * @return A predicate that filters items by owner
	 */
	public static Predicate<AuctionedItem> ownerFilter(UUID ownerUuid) {
		return item -> item.getOwner().equals(ownerUuid);
	}

	/**
	 * Creates a predicate that filters items by sale type
	 *
	 * @param saleType The sale type to filter by
	 * @return A predicate that filters items by sale type
	 */
	public static Predicate<AuctionedItem> saleTypeFilter(AuctionSaleType saleType) {
		if (saleType == AuctionSaleType.BOTH) {
			return item -> true;
		}
		if (saleType == AuctionSaleType.USED_BIDDING_SYSTEM) {
			return AuctionedItem::isBidItem;
		}
		return item -> !item.isBidItem();
	}

	/**
	 * Creates a predicate that filters items by currency
	 *
	 * @param currencyFilter The currency filter
	 * @return A predicate that filters items by currency
	 */
	public static Predicate<AuctionedItem> currencyFilter(AbstractCurrency currencyFilter) {
		if (currencyFilter == null || currencyFilter.equals(AuctionHouse.getCurrencyManager().getAllCurrency())) {
			return item -> true;
		}
		return item -> item.currencyMatches(currencyFilter);
	}

	/**
	 * Creates a comparator for sorting auction items
	 *
	 * @param sortType The sort type
	 * @return A comparator for sorting items
	 */
	public static Comparator<AuctionedItem> createSortComparator(AuctionSortType sortType) {
		Comparator<AuctionedItem> comparator;
		
		switch (sortType) {
			case PRICE:
				comparator = Comparator.comparingDouble(AuctionedItem::getCurrentPrice).reversed();
				break;
			case OLDEST:
				comparator = Comparator.comparingLong(AuctionedItem::getExpiresAt);
				break;
			case RECENT:
			default:
				comparator = Comparator.comparingLong(AuctionedItem::getExpiresAt).reversed();
				break;
		}
		
		// Always prioritize infinite items and listing priority
		return comparator
				.thenComparing(Comparator.comparing(AuctionedItem::isInfinite).reversed())
				.thenComparing(Comparator.comparing(AuctionedItem::isListingPriorityActive).reversed());
	}

	/**
	 * Creates a default comparator that prioritizes infinite items and listing priority
	 *
	 * @return A comparator for default sorting
	 */
	public static Comparator<AuctionedItem> createDefaultComparator() {
		return Comparator.comparing(AuctionedItem::isInfinite).reversed()
				.thenComparing(Comparator.comparing(AuctionedItem::isListingPriorityActive).reversed());
	}
}

