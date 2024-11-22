package ca.tweetzy.auctionhouse.helpers;

import ca.tweetzy.auctionhouse.settings.Settings;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeConverter {

	private static Pattern TIME_PATTERN;

	static {
		updateTimePattern();
	}

	public static void updateTimePattern() {
		List<String> allTimeUnits = new ArrayList<>();
		allTimeUnits.addAll(Settings.TIME_ALIAS_YEAR.getStringList());
		allTimeUnits.addAll(Settings.TIME_ALIAS_MONTH.getStringList());
		allTimeUnits.addAll(Settings.TIME_ALIAS_WEEK.getStringList());
		allTimeUnits.addAll(Settings.TIME_ALIAS_DAY.getStringList());
		allTimeUnits.addAll(Settings.TIME_ALIAS_HOUR.getStringList());
		allTimeUnits.addAll(Settings.TIME_ALIAS_MINUTE.getStringList());
		allTimeUnits.addAll(Settings.TIME_ALIAS_SECOND.getStringList());

		String timeUnitsRegex = String.join("|", allTimeUnits);
		TIME_PATTERN = Pattern.compile("(\\d+)\\s*(" + timeUnitsRegex + "s?)(?:\\s|$)");
	}

	public static long convertHumanReadableTime(String time) {
		Matcher matcher = TIME_PATTERN.matcher(time.toLowerCase());
		long totalMilliseconds = 0;

		while (matcher.find()) {
			int amount = Integer.parseInt(matcher.group(1));
			String unit = matcher.group(2).toLowerCase();

			// Remove trailing 's' if present
			if (unit.endsWith("s") && unit.length() > 1) {
				unit = unit.substring(0, unit.length() - 1);
			}

			long multiplier = getMultiplierForUnit(unit);
			totalMilliseconds += amount * multiplier;
		}
		return totalMilliseconds;
	}

	private static long getMultiplierForUnit(String unit) {
		// Check for more specific aliases first
		if (Settings.TIME_ALIAS_MINUTE.getStringList().contains(unit)) {
			return 60 * 1000L;
		} else if (Settings.TIME_ALIAS_MONTH.getStringList().contains(unit)) {
			return 30 * 24 * 60 * 60 * 1000L;
		} else if (Settings.TIME_ALIAS_YEAR.getStringList().contains(unit)) {
			return 365 * 24 * 60 * 60 * 1000L;
		} else if (Settings.TIME_ALIAS_WEEK.getStringList().contains(unit)) {
			return 7 * 24 * 60 * 60 * 1000L;
		} else if (Settings.TIME_ALIAS_DAY.getStringList().contains(unit)) {
			return 24 * 60 * 60 * 1000L;
		} else if (Settings.TIME_ALIAS_HOUR.getStringList().contains(unit)) {
			return 60 * 60 * 1000L;
		} else if (Settings.TIME_ALIAS_SECOND.getStringList().contains(unit)) {
			return 1000L;
		} else {
			return 0L;
		}
	}
}