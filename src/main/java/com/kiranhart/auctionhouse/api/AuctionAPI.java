package com.kiranhart.auctionhouse.api;
/*
    The current file was created by Kiran Hart
    Date: August 04 2019
    Time: 1:25 PM
    
    Code within this class is not to be redistributed without proper permission.
*/

import com.kiranhart.auctionhouse.Core;
import com.kiranhart.auctionhouse.api.version.ServerVersion;
import com.kiranhart.auctionhouse.util.Debugger;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class AuctionAPI {

    private static AuctionAPI instance;

    private AuctionAPI() {}

    public static AuctionAPI getInstance() {
        if (instance == null) {
            instance = new AuctionAPI();
        }
        return instance;
    }

    /**
     *
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
     *
     * @param totalSecs take seconds and convert to proper date time
     * @return total time left in a string
     */
    public String timeLeft(int totalSecs) {
        int hours = totalSecs / 3600;
        int minutes = (totalSecs % 3600) / 60;
        int seconds = totalSecs % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    /**
     *
     * @param number is this string a number?
     * @return whether or not the provided string is numeric
     */
    public boolean isNumeric(String number) {
        try {
            Long.parseLong(number);
        } catch (NumberFormatException nfe) {
            Debugger.report(nfe);
            return false;
        }
        return true;
    }

    @SuppressWarnings("deprecation")
    public void setItemInHand(Player p, ItemStack item) {
        if (Core.getInstance().isServerVersionAtLeast(ServerVersion.V1_9)) {
            p.getInventory().setItemInMainHand(item);
        } else {
            p.setItemInHand(item);
        }
    }

    @SuppressWarnings("deprecation")
    public ItemStack getItemInHand(Player p) {
        if (Core.getInstance().isServerVersionAtLeast(ServerVersion.V1_9)) {
            return p.getInventory().getItemInMainHand();
        } else {
            return p.getItemInHand();
        }
    }
}
