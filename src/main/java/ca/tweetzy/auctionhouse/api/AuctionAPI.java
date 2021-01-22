package ca.tweetzy.auctionhouse.api;

import java.text.DecimalFormat;
import java.text.NumberFormat;

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

    public double[] getRemainingTimeValues(long seconds) {
        double[] vals = new double[4];
        vals[0] = (double) seconds / 86400; // days
        vals[1] = (double) seconds / 3600; // hours
        vals[2] = (double) (seconds % 3600) / 60; // minutes
        vals[3] = seconds % 60; // seconds
        return vals;
    }
}
