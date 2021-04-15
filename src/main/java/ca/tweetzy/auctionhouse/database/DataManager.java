package ca.tweetzy.auctionhouse.database;

import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.auction.AuctionItem;
import ca.tweetzy.auctionhouse.transaction.Transaction;
import ca.tweetzy.core.database.DataManagerAbstract;
import ca.tweetzy.core.database.DatabaseConnector;
import org.bukkit.plugin.Plugin;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * The current file has been created by Kiran Hart
 * Date Created: April 15 2021
 * Time Created: 2:41 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class DataManager extends DataManagerAbstract {

    public DataManager(DatabaseConnector databaseConnector, Plugin plugin) {
        super(databaseConnector, plugin);
    }

    public void saveItems(List<AuctionItem> items, boolean async) {
        if (async) {
            this.async(() -> this.databaseConnector.connect(connection -> {
                String saveItems = "INSERT IGNORE INTO " + this.getTablePrefix() + "items SET data = ?";
                String truncate = "TRUNCATE TABLE " + this.getTablePrefix() + "items";
                try (PreparedStatement statement = connection.prepareStatement(truncate)) {
                    statement.execute();
                }

                PreparedStatement statement = connection.prepareStatement(saveItems);
                items.forEach(item -> {
                    try {
                        statement.setString(1, AuctionAPI.getInstance().convertToBase64(item));
                        statement.addBatch();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
                statement.executeBatch();
            }));
        } else {
            this.databaseConnector.connect(connection -> {
                String saveItems = "INSERT IGNORE INTO " + this.getTablePrefix() + "items SET data = ?";
                String truncate = "TRUNCATE TABLE " + this.getTablePrefix() + "items";
                connection.prepareStatement(truncate).executeUpdate();
                PreparedStatement statement = connection.prepareStatement(saveItems);
                items.forEach(item -> {
                    try {
                        statement.setString(1, AuctionAPI.getInstance().convertToBase64(item));
                        statement.addBatch();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
                statement.executeBatch();
            });
        }
    }

    public void saveTransactions(List<Transaction> transactions, boolean async) {
        if (async) {
            this.async(() -> this.databaseConnector.connect(connection -> {
                String saveItems = "INSERT IGNORE INTO " + this.getTablePrefix() + "transactions SET data = ?";
                String truncate = "TRUNCATE TABLE " + this.getTablePrefix() + "transactions";
                try (PreparedStatement statement = connection.prepareStatement(truncate)) {
                    statement.execute();
                }

                PreparedStatement statement = connection.prepareStatement(saveItems);
                transactions.forEach(transaction -> {
                    try {
                        statement.setString(1, AuctionAPI.getInstance().convertToBase64(transaction));
                        statement.addBatch();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
                statement.executeBatch();
            }));
        } else {
            this.databaseConnector.connect(connection -> {
                String saveItems = "INSERT IGNORE INTO " + this.getTablePrefix() + "transactions SET data = ?";
                String truncate = "TRUNCATE TABLE " + this.getTablePrefix() + "transactions";
                try (PreparedStatement statement = connection.prepareStatement(truncate)) {
                    statement.execute();
                }

                PreparedStatement statement = connection.prepareStatement(saveItems);
                transactions.forEach(transaction -> {
                    try {
                        statement.setString(1, AuctionAPI.getInstance().convertToBase64(transaction));
                        statement.addBatch();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
                statement.executeBatch();
            });
        }
    }

    public void getTransactions(Consumer<ArrayList<Transaction>> callback) {
        ArrayList<Transaction> transactions = new ArrayList<>();
        this.async(() -> this.databaseConnector.connect(connection -> {
            String select = "SELECT * FROM " + this.getTablePrefix() + "transactions";

            try (Statement statement = connection.createStatement()) {
                ResultSet result = statement.executeQuery(select);
                while (result.next()) {
                    transactions.add((Transaction) AuctionAPI.getInstance().convertBase64ToObject(result.getString("data")));
                }
            }
            this.sync(() -> callback.accept(transactions));
        }));
    }

    public void getItems(Consumer<ArrayList<AuctionItem>> callback) {
        ArrayList<AuctionItem> items = new ArrayList<>();
        this.async(() -> this.databaseConnector.connect(connection -> {
            String select = "SELECT * FROM " + this.getTablePrefix() + "items";

            try (Statement statement = connection.createStatement()) {
                ResultSet result = statement.executeQuery(select);
                while (result.next()) {
                    items.add((AuctionItem) AuctionAPI.getInstance().convertBase64ToObject(result.getString("data")));
                }
            }
            this.sync(() -> callback.accept(items));
        }));
    }
}
