import ca.tweetzy.auctionhouse.api.AuctionAPI;
import org.apache.commons.lang.StringUtils;

import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The current file has been created by Kiran Hart
 * Date Created: July 21 2021
 * Time Created: 2:51 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class Test {

    public static void main(String[] args) {
//        System.out.println(TimeUtils.makeReadable(getSecondsFromString("2y")*1000));
//        List<String> enchants = new ArrayList<>();
//        enchants.add("Sharpness V");
//        enchants.add("Sharpness I");
//        enchants.add("Fire Aspect IV");

//        System.out.println(StringUtils.join(enchants, ";=;"));

//        final String uIDPartOne = "%%__US";
//        final String uIDPartTwo = "ER__%%";
//
//        final String UID = "%%__USER_%%";

//        System.out.println(UID.contains(uIDPartOne) && UID.contains(uIDPartTwo));

//        System.out.println(AuctionAPI.toTicks("1 day"));

        String arguments = "50 -b";

        Arrays.asList(arguments.split(" ")).forEach(System.out::println);
    }

    public static long getSecondsFromString(String time) {
        time = time.toLowerCase();
        char suffix = time.charAt(time.length() - 1);
        int amount = Character.getNumericValue(time.charAt(time.length() - 2));
        switch(suffix) {
            case 's':
                return amount;
            case 'm':
                return (long) amount * 60;
            case 'h':
                return (long) amount * 3600;
            case 'd':
                return (long) amount * 3600 * 24;
            case 'y':
                return (long) amount * 3600 * 24 * 365;
            default:
                return 0L;
        }
    }
}
