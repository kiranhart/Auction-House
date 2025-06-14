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

package ca.tweetzy.auctionhouse.api;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface AuctionHouseAPI {

	/*
	----------------------------------------------------------------
			     CURRENCY / NUMBER / DATE RELATED STUFF
	----------------------------------------------------------------
	 */
	String getNumberAsCurrency(final double number, boolean hideSymbol);

	String getNumberAsCurrency(final double number);

	String getAbbreviatedNumber(final double number, boolean hideSymbol);

	String getAbbreviatedNumber(final double number);

	String getFinalizedCurrencyNumber(final double number, final String currency, final ItemStack currencyItem);

	String getCurrentMilitaryTime();

	boolean isCurrentTimeInRange(List<String> timeRanges);

	String[] getTimeUntilNextRange(List<String> timeRanges);

	boolean isAuctionHouseOpen();

}
