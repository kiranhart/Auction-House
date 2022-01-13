package ca.tweetzy.auctionhouse.database;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.auction.*;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.auctionhouse.transaction.Transaction;
import ca.tweetzy.core.database.DataManagerAbstract;
import ca.tweetzy.core.database.DatabaseConnector;
import ca.tweetzy.core.database.MySQLConnector;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: April 15 2021
 * Time Created: 2:41 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class DataManager extends DataManagerAbstract {

	private final ExecutorService thread = Executors.newSingleThreadExecutor();

	public DataManager(DatabaseConnector databaseConnector, Plugin plugin) {
		super(databaseConnector, plugin);
	}

	public void close() {
		if (!this.thread.isShutdown()) {
			this.thread.shutdown();

			try {
				if (!this.thread.awaitTermination(60, TimeUnit.SECONDS)) {
					// Try stopping the thread forcefully (there is basically no hope left for the data)
					this.thread.shutdownNow();
				}
			} catch (InterruptedException ex) {
				AuctionAPI.getInstance().logException(super.plugin, ex);
			}

			this.databaseConnector.closeConnection();
		}
	}

	public void saveBans(List<AuctionBan> bans, boolean async) {
		;
		String saveItems = "INSERT INTO " + this.getTablePrefix() + "bans(user, reason, time) VALUES(?, ?, ?)";
		String truncate = AuctionHouse.getInstance().getDatabaseConnector() instanceof MySQLConnector ? "TRUNCATE TABLE " + this.getTablePrefix() + "bans" : "DELETE FROM " + this.getTablePrefix() + "bans";

		if (async) {
			this.async(() -> this.databaseConnector.connect(connection -> {

				try (PreparedStatement statement = connection.prepareStatement(truncate)) {
					statement.execute();
				}

				PreparedStatement statement = connection.prepareStatement(saveItems);
				bans.forEach(ban -> {
					try {
						statement.setString(1, ban.getBannedPlayer().toString());
						statement.setString(2, ban.getReason());
						statement.setLong(3, ban.getTime());
						statement.addBatch();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				});
				statement.executeBatch();
			}));
		} else {
			this.databaseConnector.connect(connection -> {
				try (PreparedStatement statement = connection.prepareStatement(truncate)) {
					statement.execute();
				}

				PreparedStatement statement = connection.prepareStatement(saveItems);
				bans.forEach(ban -> {
					try {
						statement.setString(1, ban.getBannedPlayer().toString());
						statement.setString(2, ban.getReason());
						statement.setLong(3, ban.getTime());
						statement.addBatch();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				});
				statement.executeBatch();
			});
		}
	}

	public void saveFilterWhitelist(List<AuctionFilterItem> filterItems, boolean async) {
		String saveItems = "INSERT INTO " + this.getTablePrefix() + "filter_whitelist(data) VALUES(?)";
		String truncate = AuctionHouse.getInstance().getDatabaseConnector() instanceof MySQLConnector ? "TRUNCATE TABLE " + this.getTablePrefix() + "filter_whitelist" : "DELETE FROM " + this.getTablePrefix() + "filter_whitelist";

		if (async) {
			this.async(() -> this.databaseConnector.connect(connection -> {
				try (PreparedStatement statement = connection.prepareStatement(truncate)) {
					statement.execute();
				}

				PreparedStatement statement = connection.prepareStatement(saveItems);
				filterItems.forEach(filterItem -> {
					try {
						statement.setString(1, AuctionAPI.getInstance().convertToBase64(filterItem));
						statement.addBatch();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				});
				statement.executeBatch();
			}));
		} else {
			this.databaseConnector.connect(connection -> {
				try (PreparedStatement statement = connection.prepareStatement(truncate)) {
					statement.execute();
				}

				PreparedStatement statement = connection.prepareStatement(saveItems);
				filterItems.forEach(filterItem -> {
					try {
						statement.setString(1, AuctionAPI.getInstance().convertToBase64(filterItem));
						statement.addBatch();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				});
				statement.executeBatch();
			});
		}
	}

	public void getBans(Consumer<ArrayList<AuctionBan>> callback) {
		ArrayList<AuctionBan> bans = new ArrayList<>();
		this.async(() -> this.databaseConnector.connect(connection -> {
			String select = "SELECT * FROM " + this.getTablePrefix() + "bans";

			try (Statement statement = connection.createStatement()) {
				ResultSet result = statement.executeQuery(select);
				while (result.next()) {
					bans.add(new AuctionBan(
							UUID.fromString(result.getString("user")),
							result.getString("reason"),
							result.getLong("time")
					));
				}
			}
			this.sync(() -> callback.accept(bans));
		}));
	}

	public void getFilterWhitelist(Consumer<ArrayList<AuctionFilterItem>> callback) {
		ArrayList<AuctionFilterItem> filterItems = new ArrayList<>();
		this.async(() -> this.databaseConnector.connect(connection -> {
			String select = "SELECT * FROM " + this.getTablePrefix() + "filter_whitelist";

			try (Statement statement = connection.createStatement()) {
				ResultSet result = statement.executeQuery(select);
				while (result.next()) {
					filterItems.add((AuctionFilterItem) AuctionAPI.getInstance().convertBase64ToObject(result.getString("data")));
				}
			}
			this.sync(() -> callback.accept(filterItems));
		}));
	}

	public void getItems(Callback<ArrayList<AuctionedItem>> callback) {
		ArrayList<AuctionedItem> items = new ArrayList<>();
		this.async(() -> this.databaseConnector.connect(connection -> {
			try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + this.getTablePrefix() + "auctions")) {
				ResultSet resultSet = statement.executeQuery();
				while (resultSet.next()) {
					items.add(extractAuctionedItem(resultSet));
				}

				callback.accept(null, items);
			} catch (Exception e) {
				resolveCallback(callback, e);
			}
		}));
	}

	public void getTransactions(Callback<ArrayList<Transaction>> callback) {
		ArrayList<Transaction> transactions = new ArrayList<>();
		this.async(() -> this.databaseConnector.connect(connection -> {
			try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + this.getTablePrefix() + "transactions")) {
				ResultSet resultSet = statement.executeQuery();
				while (resultSet.next()) {
					transactions.add(extractTransaction(resultSet));
				}

				callback.accept(null, transactions);
			} catch (Exception e) {
				resolveCallback(callback, e);
			}
		}));
	}

	public void insertTransaction(Transaction transaction, Callback<Transaction> callback) {
		this.databaseConnector.connect(connection -> {
			try (PreparedStatement statement = connection.prepareStatement("INSERT INTO " + getTablePrefix() + "transactions (id, seller, seller_name, buyer, buyer_name, transaction_time, item, auction_sale_type, final_price) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
				PreparedStatement fetch = connection.prepareStatement("SELECT * FROM " + this.getTablePrefix() + "transactions WHERE id = ?");

				fetch.setString(1, transaction.getId().toString());
				statement.setString(1, transaction.getId().toString());
				statement.setString(2, transaction.getSeller().toString());
				statement.setString(3, transaction.getSellerName());
				statement.setString(4, transaction.getBuyer().toString());
				statement.setString(5, transaction.getBuyerName());
				statement.setLong(6, transaction.getTransactionTime());
				statement.setString(7, AuctionAPI.encodeItem(transaction.getItem()));
				statement.setString(8, transaction.getAuctionSaleType().name());
				statement.setDouble(9, transaction.getFinalPrice());
				statement.executeUpdate();

				if (callback != null) {
					ResultSet res = fetch.executeQuery();
					res.next();
					callback.accept(null, extractTransaction(res));
				}

			} catch (Exception e) {
				e.printStackTrace();
				resolveCallback(callback, e);
			}
		});
	}

	public void insertTransactionAsync(Transaction transaction, Callback<Transaction> callback) {
		this.thread.execute(() -> insertTransaction(transaction, callback));
	}

	public void insertAuction(AuctionedItem item, Callback<AuctionedItem> callback) {
		this.databaseConnector.connect(connection -> {
			try (PreparedStatement statement = connection.prepareStatement("INSERT INTO " + this.getTablePrefix() + "auctions(id, owner, highest_bidder, owner_name, highest_bidder_name, category, base_price, bid_start_price, bid_increment_price, current_price, expired, expires_at, item_material, item_name, item_lore, item_enchants, item, listed_world, infinite) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
				PreparedStatement fetch = connection.prepareStatement("SELECT * FROM " + this.getTablePrefix() + "auctions WHERE id = ?");

				fetch.setString(1, item.getId().toString());
				statement.setString(1, item.getId().toString());
				statement.setString(2, item.getOwner().toString());
				statement.setString(3, item.getHighestBidder().toString());
				statement.setString(4, item.getOwnerName());
				statement.setString(5, item.getHighestBidderName());
				statement.setString(6, item.getCategory().name());
				statement.setDouble(7, item.getBasePrice());
				statement.setDouble(8, item.getBidStartingPrice());
				statement.setDouble(9, item.getBidIncrementPrice());
				statement.setDouble(10, item.getCurrentPrice());
				statement.setBoolean(11, item.isExpired());
				statement.setLong(12, item.getExpiresAt());
				statement.setString(13, item.getItem().getType().name());
				statement.setString(14, AuctionAPI.getInstance().getItemName(item.getItem()));
				statement.setString(15, AuctionAPI.getInstance().serializeLines(AuctionAPI.getInstance().getItemLore(item.getItem())));
				statement.setString(16, AuctionAPI.getInstance().serializeLines(AuctionAPI.getInstance().getItemEnchantments(item.getItem())));
				statement.setString(17, AuctionAPI.encodeItem(item.getItem()));
				statement.setString(18, item.getListedWorld());
				statement.setBoolean(19, item.isInfinite());
				statement.executeUpdate();

				if (callback != null) {
					ResultSet res = fetch.executeQuery();
					res.next();
					callback.accept(null, extractAuctionedItem(res));
				}

			} catch (Exception e) {
				e.printStackTrace();
				resolveCallback(callback, e);
			}
		});
	}

	public void getStats(Callback<Map<UUID, AuctionStat<Integer, Integer, Integer, Double, Double>>> callback) {
		Map<UUID, AuctionStat<Integer, Integer, Integer, Double, Double>> stats = new HashMap<>();
		this.async(() -> this.databaseConnector.connect(connection -> {
			try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + this.getTablePrefix() + "stats")) {
				ResultSet resultSet = statement.executeQuery();
				while (resultSet.next()) {
					stats.put(UUID.fromString(resultSet.getString("id")), new AuctionStat<>(
							resultSet.getInt("auctions_created"),
							resultSet.getInt("auctions_sold"),
							resultSet.getInt("auctions_expired"),
							resultSet.getDouble("money_earned"),
							resultSet.getDouble("money_spent")
					));
				}

				callback.accept(null, stats);
			} catch (Exception e) {
				resolveCallback(callback, e);
			}
		}));
	}

	public void updateStats(Map<UUID, AuctionStat<Integer, Integer, Integer, Double, Double>> stats, UpdateCallback callback) {
		this.databaseConnector.connect(connection -> {
			connection.setAutoCommit(false);
			SQLException error = null;

			PreparedStatement statement = connection.prepareStatement("REPLACE into " + this.getTablePrefix() + "stats (id, auctions_created, auctions_sold, auctions_expired, money_earned, money_spent) VALUES(?,?,?,?,?,?)");
			for (Map.Entry<UUID, AuctionStat<Integer, Integer, Integer, Double, Double>> value : stats.entrySet()) {
				try {
					statement.setString(1, value.getKey().toString());
					statement.setInt(2, value.getValue().getCreated());
					statement.setInt(3, value.getValue().getSold());
					statement.setInt(4, value.getValue().getExpired());
					statement.setDouble(5, value.getValue().getEarned());
					statement.setDouble(6, value.getValue().getSpent());
					statement.addBatch();
				} catch (SQLException e) {
					error = e;
					break;
				}
			}

			statement.executeBatch();

			if (error == null) {
				connection.commit();
				resolveUpdateCallback(callback, null);
			} else {
				connection.rollback();
				resolveUpdateCallback(callback, error);
			}

			connection.setAutoCommit(true);
		});
	}

	public void insertAuctionAsync(AuctionedItem item, Callback<AuctionedItem> callback) {
		this.thread.execute(() -> insertAuction(item, callback));
	}

	public void updateItems(Collection<AuctionedItem> items, UpdateCallback callback) {
		this.databaseConnector.connect(connection -> {
			connection.setAutoCommit(false);
			SQLException err = null;

			PreparedStatement statement = connection.prepareStatement("UPDATE " + this.getTablePrefix() + "auctions SET owner = ?, owner_name = ?, highest_bidder = ?, highest_bidder_name = ?, base_price = ?, bid_start_price = ?, bid_increment_price = ?, current_price = ?, expires_at = ?, expired = ? WHERE id = ?");
			for (AuctionedItem item : items) {
				try {
					statement.setString(1, item.getOwner().toString());
					statement.setString(2, item.getOwnerName());
					statement.setString(3, item.getHighestBidder().toString());
					statement.setString(4, item.getHighestBidderName());
					statement.setDouble(5, item.getBasePrice());
					statement.setDouble(6, item.getBidStartingPrice());
					statement.setDouble(7, item.getBidIncrementPrice());
					statement.setDouble(8, item.getCurrentPrice());
					statement.setLong(9, item.getExpiresAt());
					statement.setBoolean(10, item.isExpired());
					statement.setString(11, item.getId().toString());
					statement.addBatch();
				} catch (SQLException e) {
					err = e;
					break;
				}
			}
			statement.executeBatch();

			if (err == null) {
				connection.commit();
				resolveUpdateCallback(callback, null);
			} else {
				connection.rollback();
				resolveUpdateCallback(callback, err);
			}

			connection.setAutoCommit(true);
		});
	}

	public void deleteItems(Collection<UUID> items) {
		this.async(() -> this.databaseConnector.connect(connection -> {
			PreparedStatement statement = connection.prepareStatement("DELETE FROM " + this.getTablePrefix() + "auctions WHERE id = ?");
			for (UUID id : items) {
				statement.setString(1, id.toString());
				statement.addBatch();
			}

			statement.executeBatch();
		}));
	}

	public void migrateFromSerializationFormat(Consumer<List<AuctionedItem>> callback) {
		AuctionHouse.getInstance().setMigrating(true);
		ArrayList<AuctionItem> items = new ArrayList<>();
		this.async(() -> this.databaseConnector.connect(connection -> {
			if (Settings.DATABASE_USE.getBoolean()) {
				String select = "SELECT * FROM " + this.getTablePrefix() + "items";
				try (Statement statement = connection.createStatement()) {
					ResultSet result = statement.executeQuery(select);
					while (result.next()) {
						items.add((AuctionItem) AuctionAPI.getInstance().convertBase64ToObject(result.getString("data")));
					}
				}
			} else {
				if (AuctionHouse.getInstance().getData().contains("auction items") && AuctionHouse.getInstance().getData().isList("auction items")) {
					items.addAll(AuctionHouse.getInstance().getData().getStringList("auction items").stream().map(AuctionAPI.getInstance()::convertBase64ToObject).map(object -> (AuctionItem) object).collect(Collectors.toList()));
					AuctionHouse.getInstance().getData().set("auction items", null);
					AuctionHouse.getInstance().getData().save();
				}
			}

			List<AuctionedItem> newItems = new ArrayList<>();
			items.forEach(item -> {
				OfflinePlayer owner = Bukkit.getOfflinePlayer(item.getOwner());
				OfflinePlayer highestBidder = Bukkit.getOfflinePlayer(item.getHighestBidder());
				newItems.add(new AuctionedItem(
						item.getKey(),
						item.getOwner(),
						item.getHighestBidder(),
						owner.getName() != null ? owner.getName() : "Unknown",
						highestBidder.getName() != null ? highestBidder.getName() : "Unknown",
						item.getCategory(),
						AuctionAPI.getInstance().deserializeItem(item.getRawItem()),
						item.getBasePrice(),
						item.getBidStartPrice(),
						item.getBidIncPrice(),
						item.getCurrentPrice(),
						item.getBidStartPrice() >= 1 || item.getBidIncPrice() >= 1,
						item.isExpired(),
						System.currentTimeMillis() + 1000L * item.getRemainingTime()
				));
			});

			String saveItems = "INSERT INTO " + this.getTablePrefix() + "auctions(id, owner, highest_bidder, owner_name, highest_bidder_name, category, base_price, bid_start_price, bid_increment_price, current_price, expired, expires_at, item_material, item_name, item_lore, item_enchants, item) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement statement = connection.prepareStatement(saveItems);
			newItems.forEach(item -> {
				try {
					statement.setString(1, item.getId().toString());
					statement.setString(2, item.getOwner().toString());
					statement.setString(3, item.getHighestBidder().toString());
					statement.setString(4, item.getOwnerName());
					statement.setString(5, item.getHighestBidderName());
					statement.setString(6, item.getCategory().name());
					statement.setDouble(7, item.getBasePrice());
					statement.setDouble(8, item.getBidStartingPrice());
					statement.setDouble(9, item.getBidIncrementPrice());
					statement.setDouble(10, item.getCurrentPrice());
					statement.setBoolean(11, item.isExpired());
					statement.setLong(12, item.getExpiresAt());
					statement.setString(13, item.getItem().getType().name());
					statement.setString(14, AuctionAPI.getInstance().getItemName(item.getItem()));
					statement.setString(15, AuctionAPI.getInstance().serializeLines(AuctionAPI.getInstance().getItemLore(item.getItem())));
					statement.setString(16, AuctionAPI.getInstance().serializeLines(AuctionAPI.getInstance().getItemEnchantments(item.getItem())));
					statement.setString(17, AuctionAPI.encodeItem(item.getItem()));
					statement.addBatch();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			});
			statement.executeBatch();
			this.sync(() -> {
				AuctionHouse.getInstance().setMigrating(false);
				newItems.forEach(item -> AuctionHouse.getInstance().getAuctionItemManager().addAuctionItem(item));
				callback.accept(newItems);
			});
		}));
	}

	private AuctionedItem extractAuctionedItem(ResultSet resultSet) throws SQLException {
		AuctionedItem auctionItem = new AuctionedItem(
				UUID.fromString(resultSet.getString("id")),
				UUID.fromString(resultSet.getString("owner")),
				UUID.fromString(resultSet.getString("highest_bidder")),
				resultSet.getString("owner_name"),
				resultSet.getString("highest_bidder_name"),
				AuctionItemCategory.valueOf(resultSet.getString("category")),
				AuctionAPI.decodeItem(resultSet.getString("item")),
				resultSet.getDouble("base_price"),
				resultSet.getDouble("bid_start_price"),
				resultSet.getDouble("bid_increment_price"),
				resultSet.getDouble("current_price"),
				resultSet.getDouble("bid_start_price") >= 1 || resultSet.getDouble("bid_increment_price") >= 1,
				resultSet.getBoolean("expired"),
				resultSet.getLong("expires_at")
		);

		auctionItem.setListedWorld(resultSet.getString("listed_world"));
		auctionItem.setInfinite(hasColumn(resultSet, "infinite") && resultSet.getBoolean("infinite"));
		return auctionItem;
	}

	private Transaction extractTransaction(ResultSet resultSet) throws SQLException {
		return new Transaction(
				UUID.fromString(resultSet.getString("id")),
				UUID.fromString(resultSet.getString("seller")),
				UUID.fromString(resultSet.getString("buyer")),
				resultSet.getString("seller_name"),
				resultSet.getString("buyer_name"),
				resultSet.getLong("transaction_time"),
				AuctionAPI.decodeItem(resultSet.getString("item")),
				AuctionSaleType.valueOf(resultSet.getString("auction_sale_type")),
				resultSet.getDouble("final_price")
		);
	}

	private void resolveUpdateCallback(@Nullable UpdateCallback callback, @Nullable Exception ex) {
		if (callback != null) {
			callback.accept(ex);
		} else if (ex != null) {
			AuctionAPI.getInstance().logException(this.plugin, ex, "SQLite");
		}
	}

	private void resolveCallback(@Nullable Callback<?> callback, @NotNull Exception ex) {
		if (callback != null) {
			callback.accept(ex, null);
		} else {
			AuctionAPI.getInstance().logException(this.plugin, ex, "SQLite");
		}
	}

	private boolean hasColumn(ResultSet rs, String columnName) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		int columns = rsmd.getColumnCount();
		for (int x = 1; x <= columns; x++) {
			if (columnName.equals(rsmd.getColumnName(x))) {
				return true;
			}
		}
		return false;
	}
}
