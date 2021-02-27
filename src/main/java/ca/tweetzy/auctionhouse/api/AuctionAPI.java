package ca.tweetzy.auctionhouse.api;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.concurrent.TimeUnit;

/**
 * The current file has been created by Kiran Hart
 * Date Created: January 17 2021
 * Time Created: 6:10 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class AuctionAPI {

    private static AuctionAPI instance;

    private AuctionAPI(){}

    public static AuctionAPI getInstance() {
        if (instance == null) {
            instance = new AuctionAPI();
        }
        return instance;
    }

    /**
     * @param value a long number to be converted into a easily readable text
     * @return a user friendly number to read
     */
    public String getFriendlyNumber(double value) {
        int power;
        String suffix = " KMBTQ";
        String formattedNumber = "";

        NumberFormat formatter = new DecimalFormat("#,###.#");
        power = (int) StrictMath.log10(value);
        value = value / (Math.pow(10, (power / 3) * 3));
        formattedNumber = formatter.format(value);
        formattedNumber = formattedNumber + suffix.charAt(power / 3);
        return formattedNumber.length() > 4 ? formattedNumber.replaceAll("\\.[0-9]+", "") : formattedNumber;
    }

    /**
     * Used to convert seconds to days, hours, minutes, and seconds
     *
     * @param seconds is the amount of seconds to convert
     * @return an array containing the total number of days, hours, minutes, and seconds remaining
     */
    public long[] getRemainingTimeValues(long seconds) {
        long[] vals = new long[4];
        int day = (int) TimeUnit.SECONDS.toDays(seconds);
        long hours = TimeUnit.SECONDS.toHours(seconds) - (day *24);
        long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds)* 60);
        long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) *60);
        vals[0] = day;
        vals[1] = hours;
        vals[2] = minute;
        vals[3] = second;
        return vals;
    }
}
