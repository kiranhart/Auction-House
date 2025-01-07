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
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
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

		if (currencyFormatter instanceof DecimalFormat) {
			DecimalFormat decimalFormat = (DecimalFormat) currencyFormatter;
			DecimalFormatSymbols symbols = decimalFormat.getDecimalFormatSymbols();

			if (hideSymbol) {
				symbols.setCurrencySymbol(""); // Set the currency symbol to an empty string
			} else {
				if (Settings.CURRENCY_VAULT_SYMBOL_OVERRIDES.getBoolean())
					symbols.setCurrencySymbol(Settings.CURRENCY_VAULT_SYMBOL.getString());
			}

			decimalFormat.setDecimalFormatSymbols(symbols);

		}

		currencyFormatter.setGroupingUsed(Settings.CURRENCY_USE_GROUPING.getBoolean());

		String formatted = currencyFormatter.format(number);

		if (Settings.CURRENCY_STRIP_ENDING_ZEROES.getBoolean()) {
			formatted = replaceLastDecimal(formatted);
		}

		if (Settings.CURRENCY_TIGHT_CURRENCY_SYMBOL.getBoolean()) {
//			formatted = formatted.replaceFirst("\\u00A0", "");

			formatted = formatted.replaceAll("[\\s\\u00A0]", "");
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
		return String.format(Settings.CURRENCY_REMOVE_SPACE_FROM_CUSTOM.getBoolean() ? "%s%s" : "%s %s", currencyUnformatted, currencyProperties[2]);
	}

	@Override
	public String getCurrentMilitaryTime() {
		final Instant now = Instant.now();
		final ZoneId zoneId = ZoneId.of(Settings.TIMEZONE.getString());
		final ZonedDateTime zonedDateTime = now.atZone(zoneId);

		final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
		return zonedDateTime.format(formatter);
	}

	@Override
	public boolean isCurrentTimeInRange(List<String> timeRanges) {
		final String currentTime = getCurrentMilitaryTime();
		final LocalTime now = LocalTime.parse(currentTime);

		for (String range : timeRanges) {
			final String[] times = range.split("-");
			final LocalTime start = LocalTime.parse(times[0]);
			final LocalTime end = LocalTime.parse(times[1]);

			if (isTimeInRange(now, start, end)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isAuctionHouseOpen() {
		if (!Settings.TIMED_USAGE_ENABLED.getBoolean()) return true;

		return isCurrentTimeInRange(Settings.TIMED_USAGE_RANGE.getStringList());
	}

	private boolean isTimeInRange(LocalTime time, LocalTime start, LocalTime end) {
		if (start.isBefore(end)) {
			return !time.isBefore(start) && !time.isAfter(end);
		} else {
			return !time.isBefore(start) || !time.isAfter(end);
		}
	}

	@Override
	public String[] getTimeUntilNextRange(List<String> timeRanges) {
		LocalTime currentTime = LocalTime.parse(getCurrentMilitaryTime());

		if (isCurrentTimeInRange(timeRanges)) {
			return new String[]{"Open"};
		}

		LocalTime nextStart = null;
		for (String range : timeRanges) {
			LocalTime start = LocalTime.parse(range.split("-")[0]);
			if (start.isAfter(currentTime) && (nextStart == null || start.isBefore(nextStart))) {
				nextStart = start;
			}
		}

		if (nextStart == null) {
			nextStart = LocalTime.parse(timeRanges.get(0).split("-")[0]);
		}

		long secondsUntilNext = currentTime.until(nextStart, ChronoUnit.SECONDS);
		if (secondsUntilNext < 0) {
			secondsUntilNext += 24 * 60 * 60; // Add 24 hours if next start is tomorrow
		}

		long hours = secondsUntilNext / 3600;
		long minutes = (secondsUntilNext % 3600) / 60;
		long seconds = secondsUntilNext % 60;

		String[] times = new String[3];
		times[0] = String.valueOf(hours);
		times[1] = String.valueOf(minutes);
		times[2] = String.valueOf(seconds);

		return times;
	}
}
