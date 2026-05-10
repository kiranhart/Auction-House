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

package ca.tweetzy.auctionhouse.transaction;



import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.auctionhouse.settings.Translations;
import ca.tweetzy.auctionhouse.AuctionHouse;

public enum TransactionViewFilter {

	SOLD("Sold"), BOUGHT("Bought"), ALL("All");

	final String type;

	TransactionViewFilter(String type) {
		this.type = type;
	}

	public String getTranslatedType() {
		switch (this) {
			case SOLD:
				return TranslationManager.string(Translations.TRANSACTION_FILTER_BUY_TYPE_SOLD);
			case BOUGHT:
				return TranslationManager.string(Translations.TRANSACTION_FILTER_BUY_TYPE_BOUGHT);
			case ALL:
				return TranslationManager.string(Translations.TRANSACTION_FILTER_BUY_TYPE_ALL);
			default:
				return getType();
		}
	}

	public String getType() {
		return type;
	}

	public TransactionViewFilter next() {
		return values()[(this.ordinal() + 1) % values().length];
	}
}
