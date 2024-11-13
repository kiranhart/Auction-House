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
import ca.tweetzy.auctionhouse.settings.Settings;

/**
 * The current file has been created by Kiran Hart
 * Date Created: February 01 2021
 * Time Created: 6:52 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public enum AuctionItemCategory {

	ALL("All", false, Settings.ALL_FILTER_ENABLED.getBoolean(), true),
	FOOD("Food", true, Settings.FOOD_FILTER_ENABLED.getBoolean(), true),
	ARMOR("Armor", true, Settings.ARMOR_FILTER_ENABLED.getBoolean(), true),
	BLOCKS("Blocks", true, Settings.BLOCKS_FILTER_ENABLED.getBoolean(), true),
	TOOLS("Tools", true, Settings.TOOLS_FILTER_ENABLED.getBoolean(), true),
	WEAPONS("Weapons", true, Settings.WEAPONS_FILTER_ENABLED.getBoolean(), true),
	POTIONS("Potions", true, Settings.POTIONS_FILTER_ENABLED.getBoolean(), true),
	SPAWNERS("Spawners", true, Settings.SPAWNERS_FILTER_ENABLED.getBoolean(), true),
	ENCHANTS("Enchants", true, Settings.ENCHANTS_FILTER_ENABLED.getBoolean(), true),
	MISC("Misc", true, Settings.MISC_FILTER_ENABLED.getBoolean(), true),
	SEARCH("Search", false, Settings.SEARCH_FILTER_ENABLED.getBoolean(), false),
	SELF("Self", false, Settings.SELF_FILTER_ENABLED.getBoolean(), false);


	private final String type;
	private final boolean whitelistAllowed;
	private final boolean enabled;
	private final boolean transactionFilterable;

	AuctionItemCategory(String type, boolean whitelistAllowed, boolean enabled, boolean transactionFilterable) {
		this.type = type;
		this.whitelistAllowed = whitelistAllowed;
		this.enabled = enabled;
		this.transactionFilterable = transactionFilterable;
	}

	public String getType() {
		return type;
	}

	public boolean isWhitelistAllowed() {
		return whitelistAllowed;
	}

	public static boolean isAllButAllDisabled() {
		boolean isDisabled = AuctionItemCategory.ALL.enabled;
		for (AuctionItemCategory value : AuctionItemCategory.values()) {
			if (value == ALL) continue;
			if (!value.enabled) {
				isDisabled = false;
				break;
			}
		}

		return isDisabled;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public String getFilterIcon() {
		switch (this) {
			case ALL:
				return Settings.FILTERS_ALL_ICON.getString();
			case FOOD:
				return Settings.FILTERS_FOOD_ICON.getString();
			case ARMOR:
				return Settings.FILTERS_ARMOR_ICON.getString();
			case BLOCKS:
				return Settings.FILTERS_BLOCKS_ICON.getString();
			case TOOLS:
				return Settings.FILTERS_TOOLS_ICON.getString();
			case MISC:
				return Settings.FILTERS_MISC_ICON.getString();
			case ENCHANTS:
				return Settings.FILTERS_ENCHANTS_ICON.getString();
			case SPAWNERS:
				return Settings.FILTERS_SPAWNERS_ICON.getString();
			case WEAPONS:
				return Settings.FILTERS_WEAPONS_ICON.getString();
			case POTIONS:
				return Settings.FILTERS_POTIONS_ICON.getString();
			case SELF:
				return Settings.FILTERS_SELF_ICON.getString();
			case SEARCH:
				return Settings.FILTERS_SEARCH_ICON.getString();
		}
		return Settings.FILTERS_ALL_ICON.getString();
	}

	public String getTranslatedType() {
		switch (this) {
			case ALL:
				return AuctionHouse.getInstance().getLocale().getMessage("auction_filter.categories.all").getMessage();
			case FOOD:
				return AuctionHouse.getInstance().getLocale().getMessage("auction_filter.categories.food").getMessage();
			case ARMOR:
				return AuctionHouse.getInstance().getLocale().getMessage("auction_filter.categories.armor").getMessage();
			case BLOCKS:
				return AuctionHouse.getInstance().getLocale().getMessage("auction_filter.categories.blocks").getMessage();
			case TOOLS:
				return AuctionHouse.getInstance().getLocale().getMessage("auction_filter.categories.tools").getMessage();
			case MISC:
				return AuctionHouse.getInstance().getLocale().getMessage("auction_filter.categories.misc").getMessage();
			case ENCHANTS:
				return AuctionHouse.getInstance().getLocale().getMessage("auction_filter.categories.enchants").getMessage();
			case SPAWNERS:
				return AuctionHouse.getInstance().getLocale().getMessage("auction_filter.categories.spawners").getMessage();
			case WEAPONS:
				return AuctionHouse.getInstance().getLocale().getMessage("auction_filter.categories.weapons").getMessage();
			case SELF:
				return AuctionHouse.getInstance().getLocale().getMessage("auction_filter.categories.self").getMessage();
			case POTIONS:
				return AuctionHouse.getInstance().getLocale().getMessage("auction_filter.categories.potions").getMessage();
			case SEARCH:
				return AuctionHouse.getInstance().getLocale().getMessage("auction_filter.categories.search").getMessage();
		}
		return getType();
	}

//	public AuctionItemCategory next() {
//		int currentIndex = this.ordinal();
//		int nextIndex = currentIndex + 1;
//		while (!values()[nextIndex %= values().length].isEnabled()) {
//			nextIndex++;
//		}
//
//		return values()[nextIndex];
//	}

	public AuctionItemCategory next() {
		int currentIndex = this.ordinal();
		int nextIndex = currentIndex;
		int totalCategories = values().length;

		for (int i = 1; i <= totalCategories; i++) {
			nextIndex = (currentIndex + i) % totalCategories;
			if (values()[nextIndex].isEnabled()) {
				return values()[nextIndex];
			}
		}

		// If no enabled categories are found, return the current category
		return this;
	}
}
