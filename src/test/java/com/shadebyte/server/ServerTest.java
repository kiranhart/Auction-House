package com.shadebyte.server;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The current file has been created by Kiran Hart
 * Date Created: 7/23/2018
 * Time Created: 9:00 PM
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise.
 */
public class ServerTest {

    public static void main(String[] args) {

        System.out.println(new SimpleDateFormat("MMMM dd yyyy").format(new Date(System.currentTimeMillis())));
    }
}
