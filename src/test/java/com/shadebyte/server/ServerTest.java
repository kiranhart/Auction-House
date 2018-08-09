package com.shadebyte.server;

import com.shadebyte.auctionhouse.Core;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * The current file has been created by Kiran Hart
 * Date Created: 7/23/2018
 * Time Created: 9:00 PM
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise.
 */
public class ServerTest {

    private HikariDataSource hikari;

    public ServerTest() {
        hikari = new HikariDataSource();
        hikari.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        hikari.addDataSourceProperty("serverName", "localhost");
        hikari.addDataSourceProperty("port", 3306);
        hikari.addDataSourceProperty("databaseName", "auctionhouse");
        hikari.addDataSourceProperty("user", "root");
        hikari.addDataSourceProperty("password", "");
        if (!hikari.isClosed()) {
            System.out.println("Connected to the database");
        }

        try {
            Connection connection = hikari.getConnection();
            PreparedStatement statement = connection.prepareStatement("");
            statement.execute();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ServerTest();
    }
}
