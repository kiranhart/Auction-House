package ca.tweetzy.auctionhouse.helpers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeConverter {

	private static final Pattern TIME_PATTERN = Pattern.compile("(\\d+)\\s*(years?|months?|weeks?|days?|hours?|minutes?|seconds?)");

	public static long convertHumanReadableTime(String time) {
		Matcher matcher = TIME_PATTERN.matcher(time);
		long totalMilliseconds = 0;

		while (matcher.find()) {
			String group = matcher.group();
			String[] parts = group.split(" ");
			int amount = Integer.parseInt(parts[0]);
			String unit = parts[1].toLowerCase();

			switch (unit) {
				case "y":
				case "year":
				case "years":
					totalMilliseconds += amount * 365 * 24 * 60 * 60 * 1000;
					break;
				case "m":
				case "month":
				case "months":
					totalMilliseconds += amount * 30 * 24 * 60 * 60 * 1000;
					break;
				case "w":
				case "week":
				case "weeks":
					totalMilliseconds += amount * 7 * 24 * 60 * 60 * 1000;
					break;
				case "d":
				case "day":
				case "days":
					totalMilliseconds += amount * 24 * 60 * 60 * 1000;
					break;
				case "h":
				case "hour":
				case "hours":
					totalMilliseconds += amount * 60 * 60 * 1000;
					break;
				case "min":
				case "minute":
				case "minutes":
					totalMilliseconds += amount * 60 * 1000;
					break;
				case "s":
				case "second":
				case "seconds":
					totalMilliseconds += amount * 1000;
					break;
				default:
					throw new IllegalArgumentException("Invalid unit: " + unit);
			}
		}

		return totalMilliseconds;
	}
}