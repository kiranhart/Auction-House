package com.shadebyte.server;

import com.zaxxer.hikari.HikariDataSource;

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
    }

    public static void main(String[] args) {
        new ServerTest();
    }
}
