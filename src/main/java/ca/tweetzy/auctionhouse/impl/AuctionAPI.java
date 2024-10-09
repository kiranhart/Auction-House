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

package ca.tweetzy.auctionhouse.impl;

import ca.tweetzy.auctionhouse.api.AuctionHouseAPI;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.utils.ItemUtil;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

public final class AuctionAPI implements AuctionHouseAPI {

	private String replaceLastDecimal(String input) {
		int lastComma = input.lastIndexOf(",00");
		int lastDot = input.lastIndexOf(".00");

		int lastIndex = Math.max(lastComma, lastDot);

		if (lastIndex != -1) {
			return input.substring(0, lastIndex) + input.substring(lastIndex + 3);
		}

		return input;
	}

	@Override
	public String getNumberAsCurrency(double number, boolean hideSymbol) {

		final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale.Builder()
				.setLanguage(Settings.CURRENCY_FORMAT_LANGUAGE.getString())
				.setRegion(Settings.CURRENCY_FORMAT_COUNTRY.getString())
				.build()
		);

		if (hideSymbol)
			if (currencyFormatter instanceof DecimalFormat) {
				DecimalFormat decimalFormat = (DecimalFormat) currencyFormatter;
				DecimalFormatSymbols symbols = decimalFormat.getDecimalFormatSymbols();
				symbols.setCurrencySymbol(""); // Set the currency symbol to an empty string
				decimalFormat.setDecimalFormatSymbols(symbols);
			}

		currencyFormatter.setGroupingUsed(Settings.CURRENCY_USE_GROUPING.getBoolean());

		String formatted = currencyFormatter.format(number);

		if (Settings.CURRENCY_STRIP_ENDING_ZEROES.getBoolean()) {
			formatted = replaceLastDecimal(formatted);
		}

		if (Settings.CURRENCY_TIGHT_CURRENCY_SYMBOL.getBoolean()) {
			formatted = formatted.replaceFirst("\\u00A0", "");
		}

		return formatted;
	}

	@Override
	public String getNumberAsCurrency(double number) {
		return getNumberAsCurrency(number, true);
	}

	@Override
	public String getAbbreviatedNumber(double number, boolean hideSymbol) {
		String[] suffixes = {"", "k", "m", "b", "t", "q", "Q", "s", "S", "o", "n", "d"};
		int suffixIndex = 0;
		double tempNumber = number;

		while (tempNumber >= 1000 && suffixIndex < suffixes.length - 1) {
			tempNumber /= 1000;
			suffixIndex++;
		}

		DecimalFormat decimalFormat = new DecimalFormat("#.##");
		String abbreviatedNumber = decimalFormat.format(tempNumber) + suffixes[suffixIndex];

		if (!hideSymbol) {
			abbreviatedNumber = getNumberAsCurrency(tempNumber, false) + suffixes[suffixIndex];
		}

		return abbreviatedNumber;
	}

	@Override
	public String getAbbreviatedNumber(double number) {
		return getAbbreviatedNumber(number, true);
	}

	@Override
	public String getFinalizedCurrencyNumber(double number, String currency, ItemStack currencyItem) {
		final String baseCurrencyFormat = Settings.CURRENCY_ABBREVIATE_NUMBERS.getBoolean() ? getAbbreviatedNumber(number, Settings.CURRENCY_HIDE_VAULT_SYMBOL.getBoolean()) : getNumberAsCurrency(number, Settings.CURRENCY_HIDE_VAULT_SYMBOL.getBoolean());
		final String currencyUnformatted = Settings.CURRENCY_ABBREVIATE_NUMBERS.getBoolean() ? getAbbreviatedNumber(number) : getNumberAsCurrency(number);

		if (currency == null)
			return baseCurrencyFormat;

		// split the currency string
		final String[] currencyProperties = currency.split("/");

		// basic vault currency, use normal formatting
		if (currencyProperties[0].equalsIgnoreCase("Vault")) {
			return baseCurrencyFormat;
		}

		// using an item currency
		if (currencyProperties[0].equalsIgnoreCase("AuctionHouse") && currencyProperties[1].equalsIgnoreCase("Item")) {
			final String currencyItemName = currencyItem != null && currencyItem.getType() != CompMaterial.AIR.parseMaterial() ? ItemUtil.getItemName(currencyItem) : currencyProperties[2];
			return String.format("%s %s", currencyUnformatted, currencyItemName);
		}

		// using another currency system with custom name
		return String.format("%s %s", currencyUnformatted, currencyProperties[2]);
	}
}
