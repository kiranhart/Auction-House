package com.shadebyte.server;

import com.shadebyte.auctionhouse.util.Debugger;
import com.zaxxer.hikari.HikariDataSource;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * The current file has been created by Kiran Hart
 * Date Created: 7/23/2018
 * Time Created: 9:00 PM
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise.
 */
public class ServerTest {

    private HikariDataSource hikari;

    public ServerTest() {

        String x = "discord.add.description";


        System.out.println(x.substring(x.lastIndexOf(".")).replace(".", ""));
        long xf = 1000000000000000L;
        System.out.println(friendlyNumber(xf));
    }

    public String friendlyNumber(double value) {
        int power;
        String suffix = " KMBTQE";
        String formattedNumber = "";

        NumberFormat formatter = new DecimalFormat("#,###.#");
        power = (int) StrictMath.log10(value);
        value = value / (Math.pow(10, (power / 3) * 3));
        formattedNumber = formatter.format(value);
        formattedNumber = formattedNumber + suffix.charAt(power / 3);
        return formattedNumber.length() > 4 ? formattedNumber.replaceAll("\\.[0-9]+", "") : formattedNumber;
    }

    public boolean isNumeric(String number) {
        try {
            Long.parseLong(number);
        } catch (NumberFormatException nfe) {
            Debugger.report(nfe);
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        new ServerTest();
    }
}
