import java.util.concurrent.TimeUnit;

/**
 * The current file has been created by Kiran Hart
 * Date Created: February 10 2021
 * Time Created: 4:00 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class test {

    public static double[] getRemainingTimeValues(long seconds) {
        double[] vals = new double[4];
        vals[0] = (double) seconds / 86400; // days
        vals[1] = vals[0] / 3600; // hours
        vals[2] = (double) (vals[1] % 3600) / 60; // minutes
        vals[3] = vals[2] % 60; // seconds
        return vals;
    }

    public static void calculateTime(long seconds) {

    }

    public static void main(String[] args) {
        calculateTime(60 * 53 * 25 * 2);
    }
}
