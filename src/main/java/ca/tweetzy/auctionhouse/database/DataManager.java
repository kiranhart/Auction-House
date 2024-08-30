/*
 * Auction House
 * Copyright 2018-2022 Kiran Hart
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ca.tweetzy.auctionhouse.database;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.api.ban.Ban;
import ca.tweetzy.auctionhouse.api.ban.BanType;
import ca.tweetzy.auctionhouse.api.statistic.Statistic;
import ca.tweetzy.auctionhouse.auction.*;
import ca.tweetzy.auctionhouse.auction.enums.*;
import ca.tweetzy.auctionhouse.impl.AuctionBan;
import ca.tweetzy.auctionhouse.impl.AuctionStatistic;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.auctionhouse.transaction.Transaction;
import ca.tweetzy.auctionhouse.transaction.TransactionViewFilter;
import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.database.*;
import ca.tweetzy.flight.nbtapi.NbtApiException;
import ca.tweetzy.flight.utils.QuickItem;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Level;

/**
 * The current file has been created by Kiran Hart
 * Date Created: April 15 2021
 * Time Created: 2:41 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class DataManager extends DataManagerAbstract {

	private final @Nullable String customTablePrefix;

	public DataManager(DatabaseConnector databaseConnector, Plugin plugin, @Nullable String customTablePrefix) {
		super(databaseConnector, plugin);
		this.customTablePrefix = customTablePrefix;
	}

	@Override
	public String getTablePrefix() {
		return customTablePrefix == null ? super.getTablePrefix() : customTablePrefix;
	}

	//=================================================================================================//
	// 										 AUCTION BANS                                              //
	//=================================================================================================//
	public void insertBan(@NonNull final Ban ban, final Callback<Ban> callback) {
		this.runAsync(() -> this.databaseConnector.connect(connection -> {
			final String query = "INSERT INTO " + this.getTablePrefix() + "bans (banned_player, banner, types, reason, permanent, expiration, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
			final String fetchQuery = "SELECT * FROM " + this.getTablePrefix() + "bans WHERE banned_player = ?";

			try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
				final PreparedStatement fetch = connection.prepareStatement(fetchQuery);

				fetch.setString(1, ban.getId().toString());

				preparedStatement.setString(1, ban.getId().toString());
				preparedStatement.setString(2, ban.getBanner().toString());
				preparedStatement.setString(3, ban.getBanString());
				preparedStatement.setString(4, ban.getReason());
				preparedStatement.setBoolean(5, ban.isPermanent());
				preparedStatement.setLong(6, ban.getExpireDate());
				preparedStatement.setLong(7, ban.getTimeCreated());

				preparedStatement.executeUpdate();

				if (callback != null) {
					final ResultSet res = fetch.executeQuery();
					res.next();
					callback.accept(null, extractBan(res));
				}

			} catch (Exception e) {
				e.printStackTrace();
				resolveCallback(callback, e);
			}
		}));
	}

	public void deleteBan(@NonNull final Ban ban, Callback<Boolean> callback) {
		this.runAsync(() -> this.databaseConnector.connect(connection -> {
			try (PreparedStatement statement = connection.prepareStatement("DELETE FROM " + this.getTablePrefix() + "bans WHERE banned_player = ?")) {
				statement.setString(1, ban.getId().toString());

				int result = statement.executeUpdate();
				callback.accept(null, result > 0);

			} catch (Exception e) {
				resolveCallback(callback, e);
				e.printStackTrace();
			}
		}));
	}

	public void getBans(@NonNull final Callback<List<Ban>> callback) {
		final List<Ban> bans = new ArrayList<>();
		this.runAsync(() -> this.databaseConnector.connect(connection -> {
			try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + this.getTablePrefix() + "bans")) {
				final ResultSet resultSet = statement.executeQuery();
				while (resultSet.next()) {
					final Ban ban = extractBan(resultSet);
					bans.add(ban);
				}

				callback.accept(null, bans);
			} catch (Exception e) {
				resolveCallback(callback, e);
			}
		}));
	}

	public Ban extractBan(final ResultSet resultSet) throws SQLException {
		final String[] rawBanTypes = resultSet.getString("types").split(",");
		final HashSet<BanType> banTypes = new HashSet<>();

		for (String possibleBanType : rawBanTypes) {
			if (possibleBanType == null || possibleBanType.isEmpty()) {
				continue;
			}

			try {
				BanType foundType = Enum.valueOf(BanType.class, possibleBanType);
				banTypes.add(foundType);
			} catch (Exception ignored) {
			}
		}

		return new AuctionBan(
				UUID.fromString(resultSet.getString("banned_player")),
				UUID.fromString(resultSet.getString("banner")),
				banTypes,
				resultSet.getString("reason"),
				resultSet.getBoolean("permanent"),
				resultSet.getLong("expiration"),
				resultSet.getLong("created_at")
		);
	}

	//=================================================================================================//
	// 										 WHITE LIST                                                //
	//=================================================================================================//

	public void saveFilterWhitelist(List<AuctionFilterItem> filterItems, boolean async) {
		String saveItems = "INSERT INTO " + this.getTablePrefix() + "filter_whitelist(data) VALUES(?)";
		String truncate = AuctionHouse.getDatabaseConnector() instanceof MySQLConnector ? "TRUNCATE TABLE " + this.getTablePrefix() + "filter_whitelist" : "DELETE FROM " + this.getTablePrefix() + "filter_whitelist";

		if (async) {
			this.runAsync(() -> this.databaseConnector.connect(connection -> {
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

	public void getFilterWhitelist(Consumer<ArrayList<AuctionFilterItem>> callback) {
		ArrayList<AuctionFilterItem> filterItems = new ArrayList<>();
		this.runAsync(() -> this.databaseConnector.connect(connection -> {
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

	//=================================================================================================//
	// 								      AUCTION LISTINGS                                             //
	//=================================================================================================//
	public void updateItemsAsync(Collection<AuctionedItem> items, UpdateCallback callback) {
		this.runAsync(() -> updateItems(items, callback));
	}

	public void updateItems(Collection<AuctionedItem> items, UpdateCallback callback) {

		this.databaseConnector.connect(connection -> {
			connection.setAutoCommit(false);
			SQLException err = null;

			PreparedStatement statement = connection.prepareStatement("UPDATE " + this.getTablePrefix() + "auctions SET owner = ?, owner_name = ?, highest_bidder = ?, highest_bidder_name = ?, base_price = ?, bid_start_price = ?, bid_increment_price = ?, current_price = ?, expires_at = ?, expired = ?, item = ?, serialize_version = ?, itemstack = ?, listing_priority = ?, priority_expires_at = ? WHERE id = ?");
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
					statement.setString(11, AuctionAPI.encodeItem(item.getItem()));

					try {
						statement.setInt(12, 1);
						statement.setString(13, QuickItem.toString(item.getItem()));

					} catch (NbtApiException e) {
						statement.setInt(12, 0);
						statement.setString(13, null);
					}

					statement.setBoolean(14, item.getPriorityExpiresAt() >= System.currentTimeMillis() && item.isHasListingPriority());
					statement.setLong(15, item.getPriorityExpiresAt());

					statement.setString(16, item.getId().toString());


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
		this.databaseConnector.connect(connection -> {
			connection.setAutoCommit(false);
			PreparedStatement statement = connection.prepareStatement("DELETE FROM " + this.getTablePrefix() + "auctions WHERE id = ?");
			for (UUID id : items) {
				statement.setString(1, id.toString());
				statement.addBatch();
			}

			statement.executeBatch();
			connection.setAutoCommit(true);
		});
	}

	public void deleteItemsAsync(Collection<UUID> items) {
		this.runAsync(() -> deleteItems(items));
	}

	public void getItems(Callback<ArrayList<AuctionedItem>> callback) {
		ArrayList<AuctionedItem> items = new ArrayList<>();
		this.runAsync(() -> this.databaseConnector.connect(connection -> {
			try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + this.getTablePrefix() + "auctions")) {
				ResultSet resultSet = statement.executeQuery();
				final List<UUID> toRemove = new ArrayList<>();

				while (resultSet.next()) {
					AuctionedItem item = extractAuctionedItem(resultSet);

					if (item == null || item.getItem().getType() == CompMaterial.AIR.parseMaterial()) {
						continue;
					}

					if (resultSet.getInt("serialize_version") == 0) {
						try (PreparedStatement updateStatement = connection.prepareStatement("UPDATE " + this.getTablePrefix() + "auctions SET serialize_version = 1, itemstack = ? WHERE id = ?")) {
							try {
								updateStatement.setString(1, QuickItem.toString(item.getItem()));
								updateStatement.setString(2, resultSet.getString("id"));
								updateStatement.executeUpdate();
							} catch (NbtApiException ignored) {
							}
						}
					}

					if (resultSet.getBoolean("expired") && Settings.EXPIRATION_TIME_LIMIT_ENABLED.getBoolean() && Instant.ofEpochMilli(resultSet.getLong("expires_at")).isBefore(Instant.now().minus(Duration.ofHours(Settings.EXPIRATION_TIME_LIMIT.getInt())))) {
						toRemove.add(UUID.fromString(resultSet.getString("id")));
					} else {
						items.add(item);
					}
				}

				deleteItemsAsync(toRemove);
				callback.accept(null, items);
			} catch (Exception e) {
				resolveCallback(callback, e);
			}
		}));
	}

	public void insertAuction(AuctionedItem item, Callback<AuctionedItem> callback) {
		this.runAsync(() -> this.databaseConnector.connect(connection -> {
			try (PreparedStatement statement = connection.prepareStatement("INSERT INTO " + this.getTablePrefix() + "auctions(id, owner, highest_bidder, owner_name, highest_bidder_name, category, base_price, bid_start_price, bid_increment_price, current_price, expired, expires_at, item_material, item_name, item_lore, item_enchants, item, listed_world, infinite, allow_partial_buys, server_auction, is_request, request_count, serialize_version, itemstack, listing_priority, priority_expires_at, currency, currency_item) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
				final AuctionAPI api = AuctionAPI.getInstance();
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
				statement.setString(13, item.getItem().getType().name()); // todo remove
				statement.setString(14, api.getItemName(item.getItem())); // todo remove
				statement.setString(15, api.serializeLines(api.getItemLore(item.getItem()))); // todo remove
				statement.setString(16, api.serializeLines(api.getItemEnchantments(item.getItem()))); // todo remove
				statement.setString(17, AuctionAPI.encodeItem(item.getItem()));
				statement.setString(18, item.getListedWorld());
				statement.setBoolean(19, item.isInfinite());
				statement.setBoolean(20, item.isAllowPartialBuy());
				statement.setBoolean(21, item.isServerItem());
				statement.setBoolean(22, item.isRequest());
				statement.setInt(23, item.getRequestAmount());

				try {
					statement.setInt(24, 1);
					statement.setString(25, QuickItem.toString(item.getItem()));
				} catch (NbtApiException e) {
					statement.setInt(24, 0);
					statement.setString(25, null);
				}

				statement.setBoolean(26, item.isHasListingPriority());
				statement.setLong(27, item.getPriorityExpiresAt());

//				currency, currency_item, listed_server
				statement.setString(28, item.getCurrency());
				statement.setString(29, (item.getCurrencyItem() == null || item.getCurrencyItem() == CompMaterial.AIR.parseItem()) ? null : QuickItem.toString(item.getCurrencyItem()));

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
		}));
	}

	private AuctionedItem extractAuctionedItem(ResultSet resultSet) throws SQLException {
		final ItemStack item = resultSet.getInt("serialize_version") == 1 ? QuickItem.getItem(resultSet.getString("itemstack")) : AuctionAPI.decodeItem(resultSet.getString("item"));

		if (item == null) {
			AuctionHouse.getInstance().getLogger().log(Level.WARNING, "Auction Item with id " + resultSet.getString("id") + " is using an unknown material, it is being skipped!");
			return null;
		}

		AuctionedItem auctionItem = new AuctionedItem(
				UUID.fromString(resultSet.getString("id")),
				UUID.fromString(resultSet.getString("owner")),
				UUID.fromString(resultSet.getString("highest_bidder")),
				resultSet.getString("owner_name"),
				resultSet.getString("highest_bidder_name"),
				AuctionItemCategory.valueOf(resultSet.getString("category")),
				item,
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
		auctionItem.setAllowPartialBuy(hasColumn(resultSet, "allow_partial_buys") && resultSet.getBoolean("allow_partial_buys"));
		auctionItem.setServerItem(resultSet.getBoolean("server_auction"));
		auctionItem.setRequest(resultSet.getBoolean("is_request"));
		auctionItem.setRequestAmount(resultSet.getInt("request_count"));

		final long priorityExpiresAt = resultSet.getLong("priority_expires_at");

		auctionItem.setHasListingPriority(priorityExpiresAt >= System.currentTimeMillis() && resultSet.getBoolean("listing_priority"));
		auctionItem.setPriorityExpiresAt(priorityExpiresAt);

		auctionItem.setCurrency(resultSet.getString("currency"));
		auctionItem.setCurrencyItem(resultSet.getString("currency_item") == null ? null : QuickItem.getItem(resultSet.getString("currency_item")));

		return auctionItem;
	}


	//=================================================================================================//
	// 								         AUCTION LOGS                                             //
	//=================================================================================================//

	public void getAdminLogs(Callback<ArrayList<AuctionAdminLog>> callback) {
		ArrayList<AuctionAdminLog> logs = new ArrayList<>();
		this.runAsync(() -> this.databaseConnector.connect(connection -> {
			try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + this.getTablePrefix() + "admin_logs")) {
				ResultSet resultSet = statement.executeQuery();
				while (resultSet.next()) {

					final AuctionAdminLog log = extractAdminLog(resultSet);
					if (log == null || log.getItem() == null || log.getItem().getType() == CompMaterial.AIR.parseMaterial()) continue;

					if (resultSet.getInt("serialize_version") == 0) {
						try (PreparedStatement updateStatement = connection.prepareStatement("UPDATE " + this.getTablePrefix() + "admin_logs SET serialize_version = 1, itemstack = ? WHERE id = ?")) {
							try {
								String possible = QuickItem.toString(log.getItem());
								updateStatement.setString(1, possible);
								updateStatement.setInt(2, resultSet.getInt("id"));
								updateStatement.executeUpdate();
							} catch (NbtApiException ignored) {
							}
						}
					}

					logs.add(log);
				}

				callback.accept(null, logs);
			} catch (Exception e) {
				resolveCallback(callback, e);
			}
		}));
	}

	public void getTransactions(Callback<ArrayList<Transaction>> callback) {
		ArrayList<Transaction> transactions = new ArrayList<>();
		this.runAsync(() -> this.databaseConnector.connect(connection -> {
			try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + this.getTablePrefix() + "transactions")) {
				ResultSet resultSet = statement.executeQuery();
				while (resultSet.next()) {
					final Transaction transaction = extractTransaction(resultSet);

					if (transaction == null || transaction.getItem().getType() == CompMaterial.AIR.parseMaterial()) {
						continue;
					}

					if (resultSet.getInt("serialize_version") == 0) {
						try (PreparedStatement updateStatement = connection.prepareStatement("UPDATE " + this.getTablePrefix() + "transactions SET serialize_version = 1, itemstack = ? WHERE id = ?")) {
							try {
								String possible = QuickItem.toString(transaction.getItem());
								updateStatement.setString(1, possible);
								updateStatement.setString(2, resultSet.getString("id"));
								updateStatement.executeUpdate();
							} catch (NbtApiException e) {
								//todo idk do something
							}
						}
					}

					transactions.add(transaction);
				}

				callback.accept(null, transactions);
			} catch (Exception e) {
				resolveCallback(callback, e);
			}
		}));
	}

	public void insertTransaction(Transaction transaction, Callback<Transaction> callback) {
		this.runAsync(() -> this.databaseConnector.connect(connection -> {
			try (PreparedStatement statement = connection.prepareStatement("INSERT INTO " + getTablePrefix() + "transactions (id, seller, seller_name, buyer, buyer_name, transaction_time, item, auction_sale_type, final_price, serialize_version, itemstack) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
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

				try {
					statement.setInt(10, 1);
					statement.setString(11, QuickItem.toString(transaction.getItem()));
				} catch (NbtApiException e) {
					statement.setInt(10, 0);
					statement.setString(11, null);
				}

				statement.executeUpdate();

				if (callback != null) {
					ResultSet res = fetch.executeQuery();
					res.next();

					final Transaction inserted = extractTransaction(res);
					if (inserted != null)
						callback.accept(null, inserted);
				}

			} catch (Exception e) {
				e.printStackTrace();
				resolveCallback(callback, e);
			}
		}));
	}

	public void insertLog(AuctionAdminLog adminLog) {
		this.runAsync(() -> this.databaseConnector.connect(connection -> {
			try (PreparedStatement statement = connection.prepareStatement("INSERT INTO " + this.getTablePrefix() + "admin_logs(admin, admin_name, target, target_name, item, item_id, action, time, serialize_version, itemstack) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {

				statement.setString(1, adminLog.getAdmin().toString());
				statement.setString(2, adminLog.getAdminName());
				statement.setString(3, adminLog.getTarget().toString());
				statement.setString(4, adminLog.getTargetName());
				statement.setString(5, AuctionAPI.encodeItem(adminLog.getItem()));
				statement.setString(6, adminLog.getItemId().toString());
				statement.setString(7, adminLog.getAdminAction().name());
				statement.setLong(8, adminLog.getTime());

				try {
					statement.setInt(9, 1);
					statement.setString(10, QuickItem.toString(adminLog.getItem()));
				} catch (NbtApiException e) {
					statement.setInt(9, 0);
					statement.setString(10, null);
				}

				statement.executeUpdate();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}));
	}

	public void insertMinPrice(MinItemPrice item, Callback<MinItemPrice> callback) {
		this.runAsync(() -> this.databaseConnector.connect(connection -> {
			try (PreparedStatement statement = connection.prepareStatement("INSERT INTO " + this.getTablePrefix() + "min_item_prices (id, item, price, serialize_version, itemstack) VALUES(?, ?, ?, ?, ?)")) {
				PreparedStatement fetch = connection.prepareStatement("SELECT * FROM " + this.getTablePrefix() + "min_item_prices WHERE id = ?");

				fetch.setString(1, item.getUuid().toString());
				statement.setString(1, item.getUuid().toString());
				statement.setString(2, AuctionAPI.encodeItem(item.getItemStack()));
				statement.setDouble(3, item.getPrice());

				try {
					statement.setInt(4, 1);
					statement.setString(5, QuickItem.toString(item.getItemStack()));
				} catch (NbtApiException e) {
					statement.setInt(4, 0);
					statement.setString(5, null);
				}


				statement.executeUpdate();

				if (callback != null) {
					ResultSet res = fetch.executeQuery();
					res.next();
					callback.accept(null, extractMinItemPrice(res));
				}

			} catch (Exception e) {
				e.printStackTrace();
				resolveCallback(callback, e);
			}
		}));
	}

	public void getMinItemPrices(Callback<ArrayList<MinItemPrice>> callback) {
		ArrayList<MinItemPrice> minItemPrices = new ArrayList<>();
		this.runAsync(() -> this.databaseConnector.connect(connection -> {
			try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + this.getTablePrefix() + "min_item_prices")) {
				ResultSet resultSet = statement.executeQuery();
				while (resultSet.next()) {

					final MinItemPrice minItemPrice = extractMinItemPrice(resultSet);
					if (minItemPrice == null || minItemPrice.getItemStack() == null || minItemPrice.getItemStack().getType() == CompMaterial.AIR.parseMaterial()) continue;

					if (resultSet.getInt("serialize_version") == 0) {
						try (PreparedStatement updateStatement = connection.prepareStatement("UPDATE " + this.getTablePrefix() + "min_item_prices SET serialize_version = 1, itemstack = ? WHERE id = ?")) {
							try {
								String possible = QuickItem.toString(minItemPrice.getItemStack());
								updateStatement.setString(1, possible);
								updateStatement.setString(2, resultSet.getString("id"));
								updateStatement.executeUpdate();
							} catch (NbtApiException ignored) {
							}
						}
					}

					minItemPrices.add(minItemPrice);
				}

				callback.accept(null, minItemPrices);
			} catch (Exception e) {
				resolveCallback(callback, e);
			}
		}));
	}

	public void deleteMinItemPrice(Collection<UUID> minPrices) {
		this.runAsync(() -> this.databaseConnector.connect(connection -> {
			PreparedStatement statement = connection.prepareStatement("DELETE FROM " + this.getTablePrefix() + "min_item_prices WHERE id = ?");
			for (UUID id : minPrices) {
				statement.setString(1, id.toString());
				statement.addBatch();
			}

			statement.executeBatch();

		}));
	}

	public void insertStatistic(Statistic statistic, Callback<Statistic> callback) {
		this.runAsync(() -> this.databaseConnector.connect(connection -> {
			try (PreparedStatement statement = connection.prepareStatement("INSERT INTO " + this.getTablePrefix() + "statistic (uuid, stat_owner, stat_type, value, time) VALUES (?, ?, ?, ?, ?)")) {

				PreparedStatement fetch = connection.prepareStatement("SELECT * FROM " + this.getTablePrefix() + "statistic WHERE uuid = ?");

				fetch.setString(1, statistic.getId().toString());
				statement.setString(1, statistic.getId().toString());
				statement.setString(2, statistic.getOwner().toString());
				statement.setString(3, statistic.getType().name());
				statement.setDouble(4, statistic.getValue());
				statement.setLong(5, statistic.getTimeCreated());
				statement.executeUpdate();

				if (callback != null) {
					ResultSet res = fetch.executeQuery();
					res.next();
					callback.accept(null, extractAuctionStatistic(res));
				}

			} catch (Exception e) {
				e.printStackTrace();
				resolveCallback(callback, e);
			}
		}));
	}

	public void getStatistics(Callback<List<Statistic>> callback) {
		List<Statistic> stats = new ArrayList<>();
		this.runAsync(() -> this.databaseConnector.connect(connection -> {
			try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + this.getTablePrefix() + "statistic")) {
				ResultSet resultSet = statement.executeQuery();
				while (resultSet.next()) {
					stats.add(extractAuctionStatistic(resultSet));
				}

				callback.accept(null, stats);
			} catch (Exception e) {
				resolveCallback(callback, e);
			}
		}));
	}

	public void deleteTransactions(Collection<UUID> transactions) {
		this.runAsync(() -> this.databaseConnector.connect(connection -> {
			PreparedStatement statement = connection.prepareStatement("DELETE FROM " + this.getTablePrefix() + "transactions WHERE id = ?");
			for (UUID id : transactions) {
				statement.setString(1, id.toString());
				statement.addBatch();
			}

			statement.executeBatch();

		}));
	}

	public void insertAuctionPlayer(AuctionPlayer auctionPlayer, Callback<AuctionPlayer> callback) {
		this.runAsync(() -> this.databaseConnector.connect(connection -> {
			try {

				String insertQuery = "INSERT OR IGNORE INTO " + getTablePrefix() + "player " +
						"(uuid, filter_sale_type, filter_item_category, filter_sort_type, last_listed_item) " +
						"VALUES (?, ?, ?, ?, ?)";


				if (AuctionHouse.getDatabaseConnector() instanceof MySQLConnector) {
					insertQuery = "INSERT INTO " + getTablePrefix() + "player " +
							"(uuid, filter_sale_type, filter_item_category, filter_sort_type, last_listed_item) " +
							"VALUES (?, ?, ?, ?, ?) " +
							"ON DUPLICATE KEY UPDATE " +
							"filter_sale_type = VALUES(filter_sale_type), " +
							"filter_item_category = VALUES(filter_item_category), " +
							"filter_sort_type = VALUES(filter_sort_type), " +
							"last_listed_item = VALUES(last_listed_item)";
				}

				// Attempt to insert the new auction player
				try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
					statement.setString(1, auctionPlayer.getPlayer().getUniqueId().toString());
					statement.setString(2, auctionPlayer.getSelectedSaleType().name());
					statement.setString(3, auctionPlayer.getSelectedFilter().name());
					statement.setString(4, auctionPlayer.getAuctionSortType().name());
					statement.setLong(5, auctionPlayer.getLastListedItem());
					statement.executeUpdate();
				}

				// Fetch the auction player (whether newly inserted or already existing)
				try (PreparedStatement fetch = connection.prepareStatement(
						"SELECT * FROM " + this.getTablePrefix() + "player WHERE uuid = ?")) {
					fetch.setString(1, auctionPlayer.getUuid().toString());
					ResultSet res = fetch.executeQuery();
					if (callback != null) {
						if (res.next()) {
							callback.accept(null, extractAuctionPlayer(res));
						} else {
							callback.accept(null, auctionPlayer); // Should not reach here
						}
					}
				}

			} catch (Exception e) {
				resolveCallback(callback, e);
			}
		}));
	}

	public void getAuctionPlayers(Callback<ArrayList<AuctionPlayer>> callback) {
		ArrayList<AuctionPlayer> auctionPlayers = new ArrayList<>();
		this.runAsync(() -> this.databaseConnector.connect(connection -> {
			try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + this.getTablePrefix() + "player")) {
				ResultSet resultSet = statement.executeQuery();
				while (resultSet.next()) {
					auctionPlayers.add(extractAuctionPlayer(resultSet));
				}

				callback.accept(null, auctionPlayers);
			} catch (Exception e) {
				resolveCallback(callback, e);
			}
		}));
	}

	public void updateAuctionPlayer(@NonNull final AuctionPlayer auctionPlayer, Callback<Boolean> callback) {
		this.runAsync(() -> this.databaseConnector.connect(connection -> {
			try (PreparedStatement statement = connection.prepareStatement("UPDATE " + this.getTablePrefix() + "player SET filter_sale_type = ?, filter_item_category = ?, filter_sort_type = ?, last_listed_item = ? WHERE uuid = ?")) {
				statement.setString(1, auctionPlayer.getSelectedSaleType().name());
				statement.setString(2, auctionPlayer.getSelectedFilter().name());
				statement.setString(3, auctionPlayer.getAuctionSortType().name());
				statement.setLong(4, auctionPlayer.getLastListedItem());
				statement.setString(5, auctionPlayer.getUuid().toString());

				int result = statement.executeUpdate();

				if (callback != null)
					callback.accept(null, result > 0);

			} catch (Exception e) {
				resolveCallback(callback, e);
			}
		}));
	}

	public void getAuctionPayments(Callback<ArrayList<AuctionPayment>> callback) {
		ArrayList<AuctionPayment> payments = new ArrayList<>();
		this.runAsync(() -> this.databaseConnector.connect(connection -> {
			try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + this.getTablePrefix() + "payments")) {
				ResultSet resultSet = statement.executeQuery();
				while (resultSet.next()) {

					final AuctionPayment payment = extractAuctionPayment(resultSet);
					if (payment == null || payment.getItem() == null || payment.getItem().getType() == CompMaterial.AIR.parseMaterial()) continue;

					if (resultSet.getInt("serialize_version") == 0) {
						try (PreparedStatement updateStatement = connection.prepareStatement("UPDATE " + this.getTablePrefix() + "payments SET serialize_version = 1, itemstack = ? WHERE uuid = ? AND time = ?")) {
							try {
								String possible = QuickItem.toString(payment.getItem());
								updateStatement.setString(1, possible);
								updateStatement.setString(2, resultSet.getString("uuid"));
								updateStatement.setLong(3, resultSet.getLong("time"));
								updateStatement.executeUpdate();
							} catch (NbtApiException ignored) {
							}
						}
					}

					payments.add(payment);
				}

				callback.accept(null, payments);
			} catch (Exception e) {
				resolveCallback(callback, e);
			}
		}));
	}

	public void insertAuctionPayment(AuctionPayment auctionPayment, Callback<AuctionPayment> callback) {
		this.runAsync(() -> this.databaseConnector.connect(connection -> {
			try (PreparedStatement statement = connection.prepareStatement("INSERT INTO " + getTablePrefix() + "payments (uuid, payment_for, amount, time, item, from_name, reason, currency, currency_item) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
				PreparedStatement fetch = connection.prepareStatement("SELECT * FROM " + this.getTablePrefix() + "payments WHERE uuid = ?");

				fetch.setString(1, auctionPayment.getId().toString());
				statement.setString(1, auctionPayment.getId().toString());
				statement.setString(2, auctionPayment.getTo().toString());
				statement.setDouble(3, auctionPayment.getAmount());
				statement.setLong(4, auctionPayment.getTime());
				statement.setString(5, AuctionAPI.encodeItem(auctionPayment.getItem()));
				statement.setString(6, auctionPayment.getFromName());
				statement.setString(7, auctionPayment.getReason().name());

				statement.setString(8, auctionPayment.getCurrency());
				statement.setString(9, (auctionPayment.getCurrencyItem() == null || auctionPayment.getCurrencyItem() == CompMaterial.AIR.parseItem()) ? null : QuickItem.toString(auctionPayment.getCurrencyItem()));


				statement.executeUpdate();

				// insert into storage
				AuctionHouse.getPaymentsManager().add(auctionPayment);

				if (callback != null) {
					ResultSet res = fetch.executeQuery();
					res.next();
					callback.accept(null, extractAuctionPayment(res));
				}

			} catch (Exception e) {
				e.printStackTrace();
				resolveCallback(callback, e);
			}
		}));
	}

	public void deletePayments(Collection<UUID> payments) {
		this.runAsync(() -> this.databaseConnector.connect(connection -> {
			PreparedStatement statement = connection.prepareStatement("DELETE FROM " + this.getTablePrefix() + "payments WHERE uuid = ?");
			for (UUID id : payments) {
				statement.setString(1, id.toString());
				statement.addBatch();
			}

			statement.executeBatch();

		}));
	}

	private AuctionPayment extractAuctionPayment(ResultSet resultSet) throws SQLException {

		String possibleItem = resultSet.getString("item");
		if (possibleItem.contains("Head Database"))
			possibleItem = possibleItem.replace("Head Database", "HeadDatabase");

		ItemStack item = resultSet.getInt("serialize_version") == 1 ? QuickItem.getItem(resultSet.getString("itemstack")) : AuctionAPI.decodeItem(possibleItem);

		return new AuctionPayment(
				UUID.fromString(resultSet.getString("uuid")),
				UUID.fromString(resultSet.getString("payment_for")),
				item,
				(resultSet.getString("from_name") == null || resultSet.getString("from_name").trim().isEmpty()) ? null : resultSet.getString("from_name"),
				(resultSet.getString("reason") == null || resultSet.getString("reason").trim().isEmpty()) ? PaymentReason.ITEM_SOLD : PaymentReason.valueOf(resultSet.getString("reason")),
				resultSet.getDouble("amount"),
				resultSet.getLong("time"),
				resultSet.getString("currency"),
				resultSet.getString("currency_item") == null ? null : QuickItem.getItem(resultSet.getString("currency_item"))
		);
	}

	private Statistic extractAuctionStatistic(ResultSet resultSet) throws SQLException {
		return new AuctionStatistic(
				UUID.fromString(resultSet.getString("uuid")),
				AuctionStatisticType.valueOf(resultSet.getString("stat_type")),
				UUID.fromString(resultSet.getString("stat_owner")),
				resultSet.getDouble("value"),
				resultSet.getLong("time")
		);
	}

	private AuctionPlayer extractAuctionPlayer(ResultSet resultSet) throws SQLException {
		return new AuctionPlayer(
				UUID.fromString(resultSet.getString("uuid")),
				Bukkit.getPlayer(UUID.fromString(resultSet.getString("uuid"))),
				AuctionSaleType.valueOf(resultSet.getString("filter_sale_type")),
				AuctionItemCategory.valueOf(resultSet.getString("filter_item_category")),
				AuctionSortType.valueOf(resultSet.getString("filter_sort_type")),
				"",
				AuctionSaleType.BOTH,
				AuctionItemCategory.ALL,
				AuctionSortType.RECENT,
				TransactionViewFilter.ALL,
				true,
				resultSet.getLong("last_listed_item"),
				null,
				-1,
				-1
		);
	}

	private MinItemPrice extractMinItemPrice(ResultSet resultSet) throws SQLException {

		String possibleItem = resultSet.getString("item");
		if (possibleItem.contains("Head Database"))
			possibleItem = possibleItem.replace("Head Database", "HeadDatabase");

		ItemStack item = resultSet.getInt("serialize_version") == 1 ? QuickItem.getItem(resultSet.getString("itemstack")) : AuctionAPI.decodeItem(possibleItem);

		return new MinItemPrice(
				UUID.fromString(resultSet.getString("id")),
				item,
				resultSet.getDouble("price")
		);
	}

	private Transaction extractTransaction(ResultSet resultSet) throws SQLException {
		String possibleItem = resultSet.getString("item");
		if (possibleItem.contains("Head Database"))
			possibleItem = possibleItem.replace("Head Database", "HeadDatabase");


		ItemStack item = null;

		if (resultSet.getInt("serialize_version") == 1)
			item = QuickItem.getItem(resultSet.getString("itemstack"));

//		AuctionAPI.decodeItemTransaction(possibleItem);
		if (item == null) return null;

		return new Transaction(
				UUID.fromString(resultSet.getString("id")),
				UUID.fromString(resultSet.getString("seller")),
				UUID.fromString(resultSet.getString("buyer")),
				resultSet.getString("seller_name"),
				resultSet.getString("buyer_name"),
				resultSet.getLong("transaction_time"),
				item,
				AuctionSaleType.valueOf(resultSet.getString("auction_sale_type")),
				resultSet.getDouble("final_price")
		);
	}

	private AuctionAdminLog extractAdminLog(ResultSet resultSet) throws SQLException {

		String possibleItem = resultSet.getString("item");
		if (possibleItem.contains("Head Database"))
			possibleItem = possibleItem.replace("Head Database", "HeadDatabase");

		ItemStack item = resultSet.getInt("serialize_version") == 1 ? QuickItem.getItem(resultSet.getString("itemstack")) : AuctionAPI.decodeItem(possibleItem);


		return new AuctionAdminLog(
				UUID.fromString(resultSet.getString("admin")),
				resultSet.getString("admin_name"),
				UUID.fromString(resultSet.getString("target")),
				resultSet.getString("target_name"),
				item,
				UUID.fromString(resultSet.getString("item_id")),
				AdminAction.valueOf(resultSet.getString("action").toUpperCase()),
				resultSet.getLong("time")
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
