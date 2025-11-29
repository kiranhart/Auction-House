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
import ca.tweetzy.auctionhouse.api.auction.ListingPriceLimit;
import ca.tweetzy.auctionhouse.api.auction.RequestTransaction;
import ca.tweetzy.auctionhouse.api.ban.Ban;
import ca.tweetzy.auctionhouse.api.ban.BanType;
import ca.tweetzy.auctionhouse.api.statistic.Statistic;
import ca.tweetzy.auctionhouse.auction.*;
import ca.tweetzy.auctionhouse.auction.enums.*;
import ca.tweetzy.auctionhouse.impl.AuctionBan;
import ca.tweetzy.auctionhouse.impl.AuctionPriceLimit;
import ca.tweetzy.auctionhouse.impl.AuctionStatistic;
import ca.tweetzy.auctionhouse.impl.CompletedRequest;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.auctionhouse.transaction.Transaction;
import ca.tweetzy.auctionhouse.transaction.TransactionViewFilter;
import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.database.*;
import ca.tweetzy.flight.database.query.DeleteQuery;
import ca.tweetzy.flight.database.query.InsertQuery;
import ca.tweetzy.flight.database.query.SelectQuery;
import ca.tweetzy.flight.database.query.UpdateQuery;
import ca.tweetzy.flight.nbtapi.NbtApiException;
import ca.tweetzy.flight.utils.Common;
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
		this.runAsync(() -> {
			try {
				getQueryBuilder().insert("bans")
						.set("banned_player", ban.getId().toString())
						.set("banner", ban.getBanner().toString())
						.set("types", ban.getBanString())
						.set("reason", ban.getReason())
						.set("permanent", ban.isPermanent())
						.set("expiration", ban.getExpireDate())
						.set("created_at", ban.getTimeCreated())
						.execute((ex, affectedRows) -> {
							if (ex != null) {
								ex.printStackTrace();
								resolveCallback(callback, ex);
								return;
							}

							// Log ban creation
							if (AuctionHouse.getTransactionLogger() != null && affectedRows != null && affectedRows > 0) {
								String bannedPlayerName = Bukkit.getOfflinePlayer(ban.getId()).getName();
								if (bannedPlayerName == null) bannedPlayerName = "Unknown";
								String adminName = Bukkit.getOfflinePlayer(ban.getBanner()).getName();
								if (adminName == null) adminName = "Unknown";
								AuctionHouse.getTransactionLogger().logBanCreate(
									adminName,
									bannedPlayerName,
									ban.getBanString(),
									ban.getReason(),
									ban.isPermanent()
								);
							}

							if (callback != null) {
								getQueryBuilder().select("bans")
										.where("banned_player", ban.getId().toString())
										.fetchFirst(rs -> {
											try {
												return extractBan(rs);
											} catch (SQLException e) {
												return null;
											}
										}, callback);
							}
						});
			} catch (Exception e) {
				e.printStackTrace();
				resolveCallback(callback, e);
			}
		});
	}

	public void deleteBan(@NonNull final Ban ban, Callback<Boolean> callback) {
		this.runAsync(() -> {
			try {
				getQueryBuilder().delete("bans")
						.where("banned_player", ban.getId().toString())
						.execute((ex, affectedRows) -> {
							if (ex != null) {
								resolveCallback(callback, ex);
								return;
							}
							
							// Log ban removal
							if (AuctionHouse.getTransactionLogger() != null && affectedRows != null && affectedRows > 0) {
								String unbannedPlayerName = Bukkit.getOfflinePlayer(ban.getId()).getName();
								if (unbannedPlayerName == null) unbannedPlayerName = "Unknown";
								String adminName = Bukkit.getOfflinePlayer(ban.getBanner()).getName();
								if (adminName == null) adminName = "Unknown";
								AuctionHouse.getTransactionLogger().logBanRemove(
									adminName,
									unbannedPlayerName
								);
							}
							
							if (callback != null) {
								callback.accept(null, affectedRows != null && affectedRows > 0);
							}
						});
			} catch (Exception e) {
				resolveCallback(callback, e);
				e.printStackTrace();
			}
		});
	}

	public void getBans(@NonNull final Callback<List<Ban>> callback) {
		this.runAsync(() -> {
			getQueryBuilder().select("bans")
					.fetch(rs -> {
						try {
							return extractBan(rs);
						} catch (SQLException e) {
							return null;
						}
					}, callback);
		});
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
		this.runAsync(() -> {
			getQueryBuilder().select("filter_whitelist")
					.fetch(rs -> {
						try {
							return (AuctionFilterItem) AuctionAPI.getInstance().convertBase64ToObject(rs.getString("data"));
						} catch (Exception e) {
							return null;
						}
					}, (ex, results) -> {
						if (ex != null) {
							ex.printStackTrace();
							this.sync(() -> callback.accept(new ArrayList<>()));
							return;
						}
						ArrayList<AuctionFilterItem> filterItems = new ArrayList<>();
						if (results != null) {
							for (AuctionFilterItem item : results) {
								if (item != null) {
									filterItems.add(item);
								}
							}
						}
						this.sync(() -> callback.accept(filterItems));
					});
		});
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

			PreparedStatement statement = connection.prepareStatement("UPDATE " + this.getTablePrefix() + "auctions SET owner = ?, owner_name = ?, highest_bidder = ?, highest_bidder_name = ?, base_price = ?, bid_start_price = ?, bid_increment_price = ?, current_price = ?, expires_at = ?, expired = ?, item = ?, serialize_version = ?, itemstack = ?, listing_priority = ?, priority_expires_at = ?, created_at = ? WHERE id = ?");
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

					try {
						statement.setString(11, AuctionAPI.encodeItem(item.getItem()));
					} catch (IllegalStateException ignored) {
						statement.setString(11, "");
					}

					try {
						statement.setInt(12, 1);
						statement.setString(13, QuickItem.toString(item.getItem()));

					} catch (NbtApiException e) {
						statement.setInt(12, 0);
						statement.setString(13, null);
					}

					statement.setBoolean(14, item.getPriorityExpiresAt() >= System.currentTimeMillis() && item.isHasListingPriority());
					statement.setLong(15, item.getPriorityExpiresAt());

					if (item.getCreatedAt() != null) {
						statement.setLong(16, item.getCreatedAt());
					} else {
						statement.setNull(16, java.sql.Types.BIGINT);
					}

					statement.setString(17, item.getId().toString());


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
			final long startTime = System.currentTimeMillis();

			try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + this.getTablePrefix() + "auctions");
				 PreparedStatement versionUpdateStatement = connection.prepareStatement(
						 "UPDATE " + this.getTablePrefix() + "auctions SET serialize_version = 1, itemstack = ? WHERE id = ?");
				 PreparedStatement itemUpdateStatement = connection.prepareStatement(
						 "UPDATE " + this.getTablePrefix() + "auctions SET itemstack = ? WHERE id = ?")) {

				ResultSet resultSet = statement.executeQuery();
				final List<UUID> toRemove = new ArrayList<>();

				int batchSize = 0;
				final int BATCH_LIMIT = 500; // tweak as needed

				while (resultSet.next()) {
					AuctionedItem item = extractAuctionedItem(resultSet);

					if (item == null || item.getItem().getType() == CompMaterial.AIR.get()) {
						continue;
					}

					// Handle serialize_version upgrade
					if (resultSet.getInt("serialize_version") == 0) {
						try {
							versionUpdateStatement.setString(1, QuickItem.toString(item.getItem()));
							versionUpdateStatement.setString(2, resultSet.getString("id"));
							versionUpdateStatement.addBatch();
							batchSize++;
						} catch (NbtApiException ignored) {
						}
					}

					// Backup + itemstack handling
					final String backupItemRaw = resultSet.getString("itemstack");
					final ItemStack itemBackup = QuickItem.getItem(backupItemRaw);

					boolean backupFallback = false;
					try {
						itemUpdateStatement.setString(1, QuickItem.toString(itemBackup));
						itemUpdateStatement.setString(2, resultSet.getString("id"));
						itemUpdateStatement.addBatch();
						batchSize++;
					} catch (NbtApiException ignored) {
						backupFallback = true;
					}

					if (backupFallback) {
						itemUpdateStatement.setString(1, backupItemRaw);
						itemUpdateStatement.setString(2, resultSet.getString("id"));
						itemUpdateStatement.addBatch();
						batchSize++;
					}

					// Handle expired items
					if (resultSet.getBoolean("expired") && Settings.EXPIRATION_TIME_LIMIT_ENABLED.getBoolean() && Instant.ofEpochMilli(resultSet.getLong("expires_at")).isBefore(Instant.now().minus(Duration.ofHours(Settings.EXPIRATION_TIME_LIMIT.getInt())))) {
						toRemove.add(UUID.fromString(resultSet.getString("id")));
					} else {
						items.add(item);
					}

					// Execute batches in chunks
					if (batchSize >= BATCH_LIMIT) {
						versionUpdateStatement.executeBatch();
						itemUpdateStatement.executeBatch();
						batchSize = 0;
					}
				}

				// Flush remaining batches
				versionUpdateStatement.executeBatch();
				itemUpdateStatement.executeBatch();

				deleteItemsAsync(toRemove);
				callback.accept(null, items);

				final long endTime = System.currentTimeMillis();
				final long elapsedMs = endTime - startTime;
				Bukkit.getConsoleSender().sendMessage(Common.colorize("&8[&eAuctionHouse&8] &aLoaded & Updated Items In " + elapsedMs + " ms"));

			} catch (Exception e) {
				resolveCallback(callback, e);
			}
		}));
	}


//	public void getItems(Callback<ArrayList<AuctionedItem>> callback) {
//		ArrayList<AuctionedItem> items = new ArrayList<>();
//		this.runAsync(() -> this.databaseConnector.connect(connection -> {
//			final long startTime = System.currentTimeMillis();
//
//			try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + this.getTablePrefix() + "auctions")) {
//				ResultSet resultSet = statement.executeQuery();
//				final List<UUID> toRemove = new ArrayList<>();
//
//				while (resultSet.next()) {
//					AuctionedItem item = extractAuctionedItem(resultSet);
//
//					if (item == null || item.getItem().getType() == CompMaterial.AIR.get()) {
//						continue;
//					}
//
//					if (resultSet.getInt("serialize_version") == 0) {
//						try (PreparedStatement updateStatement = connection.prepareStatement("UPDATE " + this.getTablePrefix() + "auctions SET serialize_version = 1, itemstack = ? WHERE id = ?")) {
//							try {
//								updateStatement.setString(1, QuickItem.toString(item.getItem()));
//								updateStatement.setString(2, resultSet.getString("id"));
//								updateStatement.executeUpdate();
//							} catch (NbtApiException ignored) {
//							}
//						}
//					}
//
//					final String backupItemRaw = resultSet.getString("itemstack");
//					final ItemStack itemBackup = QuickItem.getItem(backupItemRaw);
//
//					try (PreparedStatement updateStatement = connection.prepareStatement("UPDATE " + this.getTablePrefix() + "auctions SET itemstack = ? WHERE id = ?")) {
//
//						boolean backupFallback = false;
//						try {
//							updateStatement.setString(1, QuickItem.toString(itemBackup));
//							updateStatement.setString(2, resultSet.getString("id"));
//							updateStatement.executeUpdate();
//						} catch (NbtApiException ignored) {
//							backupFallback = true;
//						}
//
//						if (backupFallback) {
//							// Attempt to update with the backup raw string
//							updateStatement.setString(1, backupItemRaw);
//							updateStatement.setString(2, resultSet.getString("id"));
//							updateStatement.executeUpdate();
//						}
//					}
//
//					if (resultSet.getBoolean("expired") && Settings.EXPIRATION_TIME_LIMIT_ENABLED.getBoolean() && Instant.ofEpochMilli(resultSet.getLong("expires_at")).isBefore(Instant.now().minus(Duration.ofHours(Settings.EXPIRATION_TIME_LIMIT.getInt())))) {
//						toRemove.add(UUID.fromString(resultSet.getString("id")));
//					} else {
//						items.add(item);
//					}
//				}
//
//				deleteItemsAsync(toRemove);
//				callback.accept(null, items);
//
//				final long endTime = System.currentTimeMillis();
//				final long elapsedMs = endTime - startTime;
//				Bukkit.getConsoleSender().sendMessage(Common.colorize("&8[&eAuctionHouse&8] &aLoaded & Updated Items In " + elapsedMs + " ms"));
//
//
//			} catch (Exception e) {
//				resolveCallback(callback, e);
//			}
//		}));
//	}

	public void insertAuction(AuctionedItem item, Callback<AuctionedItem> callback) {
		this.runAsync(() -> {
			try {
				final AuctionAPI api = AuctionAPI.getInstance();
				String itemEncoded;
				try {
					itemEncoded = AuctionAPI.encodeItem(item.getItem());
				} catch (IllegalStateException ignored) {
					itemEncoded = "";
				}

				int serializeVersion = 1;
				String itemstack;
				try {
					itemstack = QuickItem.toString(item.getItem());
				} catch (NbtApiException e) {
					serializeVersion = 0;
					itemstack = null;
				}

				String currencyItemStr = (item.getCurrencyItem() == null || item.getCurrencyItem() == CompMaterial.AIR.parseItem()) ? null : QuickItem.toString(item.getCurrencyItem());

				getQueryBuilder().insert("auctions")
						.set("id", item.getId().toString())
						.set("owner", item.getOwner().toString())
						.set("highest_bidder", item.getHighestBidder().toString())
						.set("owner_name", item.getOwnerName())
						.set("highest_bidder_name", item.getHighestBidderName())
						.set("category", item.getCategory().name())
						.set("base_price", item.getBasePrice())
						.set("bid_start_price", item.getBidStartingPrice())
						.set("bid_increment_price", item.getBidIncrementPrice())
						.set("current_price", item.getCurrentPrice())
						.set("expired", item.isExpired())
						.set("expires_at", item.getExpiresAt())
						.set("item_material", item.getItem().getType().name()) // todo remove
						.set("item_name", api.getItemName(item.getItem())) // todo remove
						.set("item_lore", api.serializeLines(api.getItemLore(item.getItem()))) // todo remove
						.set("item_enchants", api.serializeLines(api.getItemEnchantments(item.getItem()))) // todo remove
						.set("item", itemEncoded)
						.set("listed_world", item.getListedWorld())
						.set("infinite", item.isInfinite())
						.set("allow_partial_buys", item.isAllowPartialBuy())
						.set("server_auction", item.isServerItem())
						.set("is_request", item.isRequest())
						.set("request_count", item.getRequestAmount())
						.set("serialize_version", serializeVersion)
						.set("itemstack", itemstack)
						.set("listing_priority", item.isHasListingPriority())
						.set("priority_expires_at", item.getPriorityExpiresAt())
						.set("currency", item.getCurrency())
						.set("currency_item", currencyItemStr)
						.set("created_at", item.getCreatedAt())
						.execute((ex, affectedRows) -> {
							if (ex != null) {
								ex.printStackTrace();
								resolveCallback(callback, ex);
								return;
							}

							// Log auction insertion (already logged in event, but log here for database confirmation)
							// Note: Auction creation is already logged in AuctionStartEvent, so we skip duplicate logging

							if (callback != null) {
								getQueryBuilder().select("auctions")
										.where("id", item.getId().toString())
										.fetchFirst(rs -> {
											try {
												return extractAuctionedItem(rs);
											} catch (SQLException e) {
												return null;
											}
										}, callback);
							}
						});
			} catch (Exception e) {
				e.printStackTrace();
				resolveCallback(callback, e);
			}
		});
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

		if (hasColumn(resultSet, "created_at")) {
			long createdAt = resultSet.getLong("created_at");
			auctionItem.setCreatedAt(resultSet.wasNull() ? null : createdAt);
		}

		return auctionItem;
	}


	//=================================================================================================//
	// 								         AUCTION LOGS                                             //
	//=================================================================================================//

	public void getAdminLogs(Callback<ArrayList<AuctionAdminLog>> callback) {
		this.runAsync(() -> {
			this.databaseConnector.connect(connection -> {
				try {
					getQueryBuilder().select("admin_logs")
							.fetchResultSet(resultSet -> {
								ArrayList<AuctionAdminLog> logs = new ArrayList<>();
								try {
									while (resultSet.next()) {
										final AuctionAdminLog log = extractAdminLog(resultSet);
										if (log == null || log.getItem() == null || log.getItem().getType() == CompMaterial.AIR.get()) continue;

										if (resultSet.getInt("serialize_version") == 0) {
											try {
												String possible = QuickItem.toString(log.getItem());
												getQueryBuilder().update("admin_logs")
														.set("serialize_version", 1)
														.set("itemstack", possible)
														.where("id", resultSet.getInt("id"))
														.execute(null);
											} catch (NbtApiException ignored) {
											}
										}

										logs.add(log);
									}
									callback.accept(null, logs);
								} catch (SQLException e) {
									resolveCallback(callback, e);
								}
							});
				} catch (Exception e) {
					resolveCallback(callback, e);
				}
			});
		});
	}

//	public void getTransactions(Callback<ArrayList<Transaction>> callback) {
//		ArrayList<Transaction> transactions = new ArrayList<>();
//		this.runAsync(() -> this.databaseConnector.connect(connection -> {
//			try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + this.getTablePrefix() + "transactions")) {
//				ResultSet resultSet = statement.executeQuery();
//				while (resultSet.next()) {
//					final Transaction transaction = extractTransaction(resultSet);
//
//					if (transaction == null || transaction.getItem().getType() == CompMaterial.AIR.get()) {
//						continue;
//					}
//
//					if (resultSet.getInt("serialize_version") == 0) {
//						try (PreparedStatement updateStatement = connection.prepareStatement("UPDATE " + this.getTablePrefix() + "transactions SET serialize_version = 1, itemstack = ? WHERE id = ?")) {
//							try {
//								String possible = QuickItem.toString(transaction.getItem());
//								updateStatement.setString(1, possible);
//								updateStatement.setString(2, resultSet.getString("id"));
//								updateStatement.executeUpdate();
//							} catch (NbtApiException e) {
//								//todo idk do something
//							}
//						}
//					}
//
//
//					final String backupItemRaw = resultSet.getString("itemstack");
//					final ItemStack itemBackup = QuickItem.getItem(backupItemRaw);
//
//					try (PreparedStatement updateStatement = connection.prepareStatement("UPDATE " + this.getTablePrefix() + "transactions SET itemstack = ? WHERE id = ?")) {
//
//						boolean backupFallback = false;
//						try {
//							updateStatement.setString(1, QuickItem.toString(itemBackup));
//							updateStatement.setString(2, resultSet.getString("id"));
//							updateStatement.executeUpdate();
//						} catch (NbtApiException e) {
//							Bukkit.broadcastMessage("t fail");
//							backupFallback = true;
//						}
//
//						if (backupFallback) {
//							// Attempt to update with the backup raw string
//							updateStatement.setString(1, backupItemRaw);
//							updateStatement.setString(2, resultSet.getString("id"));
//							updateStatement.executeUpdate();
//						}
//					}
//
//					transactions.add(transaction);
//				}
//
//				callback.accept(null, transactions);
//			} catch (Exception e) {
//				resolveCallback(callback, e);
//			}
//		}));
//	}

	public void getTransactions(Callback<ArrayList<Transaction>> callback) {
		ArrayList<Transaction> transactions = new ArrayList<>();
		this.runAsync(() -> this.databaseConnector.connect(connection -> {
			try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + this.getTablePrefix() + "transactions");
				 PreparedStatement versionUpdateStatement = connection.prepareStatement(
						 "UPDATE " + this.getTablePrefix() + "transactions SET serialize_version = 1, itemstack = ? WHERE id = ?");
				 PreparedStatement itemUpdateStatement = connection.prepareStatement(
						 "UPDATE " + this.getTablePrefix() + "transactions SET itemstack = ? WHERE id = ?")) {

				ResultSet resultSet = statement.executeQuery();

				int batchSize = 0;
				final int BATCH_LIMIT = 500; // tune as needed

				while (resultSet.next()) {
					final Transaction transaction = extractTransaction(resultSet);

					if (transaction == null || transaction.getItem().getType() == CompMaterial.AIR.get()) {
						continue;
					}

					// Handle serialize_version upgrade
					if (resultSet.getInt("serialize_version") == 0) {
						try {
							String possible = QuickItem.toString(transaction.getItem());
							versionUpdateStatement.setString(1, possible);
							versionUpdateStatement.setString(2, resultSet.getString("id"));
							versionUpdateStatement.addBatch();
							batchSize++;
						} catch (NbtApiException e) {
							// TODO: decide what you want here (log, skip, etc.)
						}
					}

					// Backup + itemstack handling
					final String backupItemRaw = resultSet.getString("itemstack");
					final ItemStack itemBackup = QuickItem.getItem(backupItemRaw);

					boolean backupFallback = false;
					try {
						itemUpdateStatement.setString(1, QuickItem.toString(itemBackup));
						itemUpdateStatement.setString(2, resultSet.getString("id"));
						itemUpdateStatement.addBatch();
						batchSize++;
					} catch (NbtApiException e) {
						Bukkit.broadcastMessage("t fail");
						backupFallback = true;
					}

					if (backupFallback) {
						itemUpdateStatement.setString(1, backupItemRaw);
						itemUpdateStatement.setString(2, resultSet.getString("id"));
						itemUpdateStatement.addBatch();
						batchSize++;
					}

					transactions.add(transaction);

					// Execute batches in chunks
					if (batchSize >= BATCH_LIMIT) {
						versionUpdateStatement.executeBatch();
						itemUpdateStatement.executeBatch();
						batchSize = 0;
					}
				}

				// Flush remaining batches
				versionUpdateStatement.executeBatch();
				itemUpdateStatement.executeBatch();

				callback.accept(null, transactions);

			} catch (Exception e) {
				resolveCallback(callback, e);
			}
		}));
	}


	public void insertTransaction(Transaction transaction, Callback<Transaction> callback) {
		this.runAsync(() -> {
			try {
				String itemEncoded;
				try {
					itemEncoded = AuctionAPI.encodeItem(transaction.getItem());
				} catch (IllegalStateException ignored) {
					itemEncoded = "";
				}

				int serializeVersion = 1;
				String itemstack;
				try {
					itemstack = QuickItem.toString(transaction.getItem());
				} catch (NbtApiException e) {
					serializeVersion = 0;
					itemstack = null;
				}

				getQueryBuilder().insert("transactions")
						.set("id", transaction.getId().toString())
						.set("seller", transaction.getSeller().toString())
						.set("seller_name", transaction.getSellerName())
						.set("buyer", transaction.getBuyer().toString())
						.set("buyer_name", transaction.getBuyerName())
						.set("transaction_time", transaction.getTransactionTime())
						.set("item", itemEncoded)
						.set("auction_sale_type", transaction.getAuctionSaleType().name())
						.set("final_price", transaction.getFinalPrice())
						.set("serialize_version", serializeVersion)
						.set("itemstack", itemstack)
						.execute((ex, affectedRows) -> {
							if (ex != null) {
								ex.printStackTrace();
								resolveCallback(callback, ex);
								return;
							}

							if (callback != null) {
								getQueryBuilder().select("transactions")
										.where("id", transaction.getId().toString())
										.fetchFirst(rs -> {
											try {
												return extractTransaction(rs);
											} catch (SQLException e) {
												return null;
											}
										}, (ex2, result) -> {
											if (ex2 != null) {
												resolveCallback(callback, ex2);
											} else if (result != null) {
												callback.accept(null, result);
											} else {
												callback.accept(null, null);
											}
										});
							}
						});
			} catch (Exception e) {
				e.printStackTrace();
				resolveCallback(callback, e);
			}
		});
	}

	public void insertCompletedRequest(RequestTransaction requestTransaction, Callback<RequestTransaction> callback) {
		this.runAsync(() -> {
			try {
				getQueryBuilder().insert("completed_requests")
						.set("id", requestTransaction.getId().toString())
						.set("item", QuickItem.toString(requestTransaction.getRequestedItem()))
						.set("amount", requestTransaction.getAmountRequested())
						.set("price", requestTransaction.getPaymentTotal())
						.set("requester_uuid", requestTransaction.getRequesterUUID().toString())
						.set("requester_name", requestTransaction.getRequesterName())
						.set("fulfiller_uuid", requestTransaction.getFulfillerUUID().toString())
						.set("fulfiller_name", requestTransaction.getFulfillerName())
						.set("time_created", requestTransaction.getTimeCreated())
						.execute((ex, affectedRows) -> {
							if (ex != null) {
								ex.printStackTrace();
								resolveCallback(callback, ex);
								return;
							}

							if (callback != null) {
								getQueryBuilder().select("completed_requests")
										.where("id", requestTransaction.getId().toString())
										.fetchFirst(rs -> {
											try {
												return extractCompletedRequest(rs);
											} catch (SQLException e) {
												return null;
											}
										}, (ex2, result) -> {
											if (ex2 != null) {
												resolveCallback(callback, ex2);
											} else if (result != null) {
												callback.accept(null, result);
											} else {
												callback.accept(null, null);
											}
										});
							}
						});
			} catch (Exception e) {
				e.printStackTrace();
				resolveCallback(callback, e);
			}
		});
	}

	public void getCompletedRequests(Callback<ArrayList<RequestTransaction>> callback) {
		this.runAsync(() -> {
			getQueryBuilder().select("completed_requests")
					.fetch(rs -> {
						try {
							RequestTransaction transaction = extractCompletedRequest(rs);
							return transaction == null ? null : transaction;
						} catch (SQLException e) {
							return null;
						}
					}, (ex, results) -> {
						if (ex != null) {
							resolveCallback(callback, ex);
							return;
						}
						ArrayList<RequestTransaction> transactions = new ArrayList<>();
						if (results != null) {
							for (RequestTransaction transaction : results) {
								if (transaction != null) {
									transactions.add(transaction);
								}
							}
						}
						callback.accept(null, transactions);
					});
		});
	}


	public void insertLog(AuctionAdminLog adminLog) {
		this.runAsync(() -> {
			try {
				String itemEncoded;
				try {
					itemEncoded = AuctionAPI.encodeItem(adminLog.getItem());
				} catch (IllegalStateException ignored) {
					itemEncoded = "";
				}

				int serializeVersion = 1;
				String itemstack;
				try {
					itemstack = QuickItem.toString(adminLog.getItem());
				} catch (NbtApiException e) {
					serializeVersion = 0;
					itemstack = null;
				}

				getQueryBuilder().insert("admin_logs")
						.set("admin", adminLog.getAdmin().toString())
						.set("admin_name", adminLog.getAdminName())
						.set("target", adminLog.getTarget().toString())
						.set("target_name", adminLog.getTargetName())
						.set("item", itemEncoded)
						.set("item_id", adminLog.getItemId().toString())
						.set("action", adminLog.getAdminAction().name())
						.set("time", adminLog.getTime())
						.set("serialize_version", serializeVersion)
						.set("itemstack", itemstack)
						.execute(null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	//=================================================================================================//
	// 								     LISTING PRICE LIMITS                                          //
	//=================================================================================================//
	public void insertListingPriceLimit(ListingPriceLimit priceLimit, Callback<ListingPriceLimit> callback) {
		this.runAsync(() -> {
			try {
				String itemEncoded;
				try {
					itemEncoded = AuctionAPI.encodeItem(priceLimit.getItem());
				} catch (IllegalStateException ignored) {
					itemEncoded = "";
				}

				int serializeVersion = 1;
				String itemstack;
				try {
					itemstack = QuickItem.toString(priceLimit.getItem());
				} catch (NbtApiException e) {
					serializeVersion = 0;
					itemstack = null;
				}

				getQueryBuilder().insert("listing_prices")
						.set("id", priceLimit.getId().toString())
						.set("item", itemEncoded)
						.set("min_price", priceLimit.getMinPrice())
						.set("max_price", priceLimit.getMaxPrice())
						.set("serialize_version", serializeVersion)
						.set("itemstack", itemstack)
						.execute((ex, affectedRows) -> {
							if (ex != null) {
								ex.printStackTrace();
								resolveCallback(callback, ex);
								return;
							}

							if (callback != null) {
								getQueryBuilder().select("listing_prices")
										.where("id", priceLimit.getId().toString())
										.fetchFirst(rs -> {
											try {
												return extractListingPriceLimit(rs);
											} catch (SQLException e) {
												return null;
											}
										}, callback);
							}
						});
			} catch (Exception e) {
				e.printStackTrace();
				resolveCallback(callback, e);
			}
		});
	}

	public void getListingPriceLimits(Callback<ArrayList<ListingPriceLimit>> callback) {
		this.runAsync(() -> {
			this.databaseConnector.connect(connection -> {
				try {
					getQueryBuilder().select("listing_prices")
							.fetchResultSet(resultSet -> {
								ArrayList<ListingPriceLimit> listingPriceLimits = new ArrayList<>();
								try {
									while (resultSet.next()) {
										final ListingPriceLimit listingPrice = extractListingPriceLimit(resultSet);
										if (listingPrice == null || listingPrice.getItem() == null || listingPrice.getItem().getType() == CompMaterial.AIR.get()) continue;

										if (resultSet.getInt("serialize_version") == 0) {
											try {
												String possible = QuickItem.toString(listingPrice.getItem());
												getQueryBuilder().update("listing_prices")
														.set("serialize_version", 1)
														.set("itemstack", possible)
														.where("id", resultSet.getString("id"))
														.execute(null);
											} catch (NbtApiException ignored) {
											}
										}

										listingPriceLimits.add(listingPrice);
									}
									callback.accept(null, listingPriceLimits);
								} catch (SQLException e) {
									resolveCallback(callback, e);
								}
							});
				} catch (Exception e) {
					resolveCallback(callback, e);
				}
			});
		});
	}

	public void deleteListingPriceLimit(@NonNull final UUID uuid, Callback<Boolean> callback) {
		this.runAsync(() -> {
			try {
				getQueryBuilder().delete("listing_prices")
						.where("id", uuid.toString())
						.execute((ex, affectedRows) -> {
							if (ex != null) {
								resolveCallback(callback, ex);
								return;
							}
							if (callback != null) {
								callback.accept(null, affectedRows != null && affectedRows > 0);
							}
						});
			} catch (Exception e) {
				resolveCallback(callback, e);
			}
		});
	}

	public void updateListingPriceLimit(@NonNull final ListingPriceLimit listingPriceLimit, Callback<Boolean> callback) {
		this.runAsync(() -> {
			try {
				getQueryBuilder().update("listing_prices")
						.set("min_price", listingPriceLimit.getMinPrice())
						.set("max_price", listingPriceLimit.getMaxPrice())
						.where("id", listingPriceLimit.getId().toString())
						.execute((ex, affectedRows) -> {
							if (ex != null) {
								resolveCallback(callback, ex);
								return;
							}
							if (callback != null) {
								callback.accept(null, affectedRows != null && affectedRows > 0);
							}
						});
			} catch (Exception e) {
				resolveCallback(callback, e);
			}
		});
	}


	public void insertStatistic(Statistic statistic, Callback<Statistic> callback) {
		this.runAsync(() -> {
			try {
				getQueryBuilder().insert("statistic")
						.set("uuid", statistic.getId().toString())
						.set("stat_owner", statistic.getOwner().toString())
						.set("stat_type", statistic.getType().name())
						.set("value", statistic.getValue())
						.set("time", statistic.getTimeCreated())
						.execute((ex, affectedRows) -> {
							if (ex != null) {
								ex.printStackTrace();
								resolveCallback(callback, ex);
								return;
							}

							if (callback != null) {
								getQueryBuilder().select("statistic")
										.where("uuid", statistic.getId().toString())
										.fetchFirst(rs -> {
											try {
												return extractAuctionStatistic(rs);
											} catch (SQLException e) {
												return null;
											}
										}, callback);
							}
						});
			} catch (Exception e) {
				e.printStackTrace();
				resolveCallback(callback, e);
			}
		});
	}

	public void getStatistics(Callback<List<Statistic>> callback) {
		this.runAsync(() -> {
			getQueryBuilder().select("statistic")
					.fetch(rs -> {
						try {
							return extractAuctionStatistic(rs);
						} catch (SQLException e) {
							return null;
						}
					}, callback);
		});
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
		this.runAsync(() -> {
			getQueryBuilder().select("player")
					.fetch(rs -> {
						try {
							return extractAuctionPlayer(rs);
						} catch (SQLException e) {
							return null;
						}
					}, (ex, results) -> {
						if (ex != null) {
							resolveCallback(callback, ex);
							return;
						}
						ArrayList<AuctionPlayer> auctionPlayers = new ArrayList<>();
						if (results != null) {
							for (AuctionPlayer player : results) {
								if (player != null) {
									auctionPlayers.add(player);
								}
							}
						}
						callback.accept(null, auctionPlayers);
					});
		});
	}

	public void updateAuctionPlayer(@NonNull final AuctionPlayer auctionPlayer, Callback<Boolean> callback) {
		this.runAsync(() -> {
			try {
				getQueryBuilder().update("player")
						.set("filter_sale_type", auctionPlayer.getSelectedSaleType().name())
						.set("filter_item_category", auctionPlayer.getSelectedFilter().name())
						.set("filter_sort_type", auctionPlayer.getAuctionSortType().name())
						.set("last_listed_item", auctionPlayer.getLastListedItem())
						.where("uuid", auctionPlayer.getUuid().toString())
						.execute((ex, affectedRows) -> {
							if (ex != null) {
								resolveCallback(callback, ex);
								return;
							}
							if (callback != null) {
								callback.accept(null, affectedRows != null && affectedRows > 0);
							}
						});
			} catch (Exception e) {
				resolveCallback(callback, e);
			}
		});
	}

	public void getAuctionPayments(Callback<ArrayList<AuctionPayment>> callback) {
		this.runAsync(() -> {
			this.databaseConnector.connect(connection -> {
				try {
					getQueryBuilder().select("payments")
							.fetchResultSet(resultSet -> {
								ArrayList<AuctionPayment> payments = new ArrayList<>();
								try {
									while (resultSet.next()) {
										final AuctionPayment payment = extractAuctionPayment(resultSet);
										if (payment == null || payment.getItem() == null || payment.getItem().getType() == CompMaterial.AIR.get()) continue;

										if (resultSet.getInt("serialize_version") == 0) {
											try {
												String possible = QuickItem.toString(payment.getItem());
												getQueryBuilder().update("payments")
														.set("serialize_version", 1)
														.set("itemstack", possible)
														.where("uuid", resultSet.getString("uuid"))
														.where("time", resultSet.getLong("time"))
														.execute(null);
											} catch (NbtApiException ignored) {
											}
										}

										payments.add(payment);
									}
									callback.accept(null, payments);
								} catch (SQLException e) {
									resolveCallback(callback, e);
								}
							});
				} catch (Exception e) {
					resolveCallback(callback, e);
				}
			});
		});
	}

	public void insertAuctionPayment(AuctionPayment auctionPayment, Callback<AuctionPayment> callback) {
		this.runAsync(() -> {
			try {
				String currencyItemStr = (auctionPayment.getCurrencyItem() == null || auctionPayment.getCurrencyItem() == CompMaterial.AIR.parseItem()) ? null : QuickItem.toString(auctionPayment.getCurrencyItem());

				getQueryBuilder().insert("payments")
						.set("uuid", auctionPayment.getId().toString())
						.set("payment_for", auctionPayment.getTo().toString())
						.set("amount", auctionPayment.getAmount())
						.set("time", auctionPayment.getTime())
						.set("item", AuctionAPI.encodeItem(auctionPayment.getItem()))
						.set("from_name", auctionPayment.getFromName())
						.set("reason", auctionPayment.getReason().name())
						.set("currency", auctionPayment.getCurrency())
						.set("currency_item", currencyItemStr)
						.execute((ex, affectedRows) -> {
							if (ex != null) {
								ex.printStackTrace();
								resolveCallback(callback, ex);
								return;
							}

							// Log payment creation (balance doesn't change until collected)
							if (AuctionHouse.getTransactionLogger() != null && affectedRows != null && affectedRows > 0) {
								String playerName = auctionPayment.getFromName() != null ? auctionPayment.getFromName() : "Unknown";
								AuctionHouse.getTransactionLogger().logPaymentCreate(
									playerName,
									auctionPayment.getAmount(),
									auctionPayment.getCurrency(),
									auctionPayment.getReason().name(),
									auctionPayment.getId().toString(),
									null, // Balance doesn't change until payment is collected
									null
								);
							}

							// insert into storage
							AuctionHouse.getPaymentsManager().add(auctionPayment);

							if (callback != null) {
								getQueryBuilder().select("payments")
										.where("uuid", auctionPayment.getId().toString())
										.fetchFirst(rs -> {
											try {
												return extractAuctionPayment(rs);
											} catch (SQLException e) {
												return null;
											}
										}, (ex2, result) -> {
											if (ex2 != null) {
												resolveCallback(callback, ex2);
											} else if (result != null) {
												callback.accept(null, result);
											} else {
												callback.accept(null, null);
											}
										});
							}
						});
			} catch (Exception e) {
				e.printStackTrace();
				resolveCallback(callback, e);
			}
		});
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
				-1,
				AuctionHouse.getCurrencyManager().getAllCurrency()
		);
	}

	private ListingPriceLimit extractListingPriceLimit(ResultSet resultSet) throws SQLException {

		String possibleItem = resultSet.getString("item");
		if (possibleItem.contains("Head Database"))
			possibleItem = possibleItem.replace("Head Database", "HeadDatabase");

		ItemStack item = resultSet.getInt("serialize_version") == 1 ? QuickItem.getItem(resultSet.getString("itemstack")) : AuctionAPI.decodeItem(possibleItem);

		return new AuctionPriceLimit(
				UUID.fromString(resultSet.getString("id")),
				item,
				resultSet.getDouble("min_price"),
				resultSet.getDouble("max_price")
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

	public RequestTransaction extractCompletedRequest(ResultSet resultSet) throws SQLException {
		//id, item, amount, price, requester_uuid, requester_name, fulfiller_uuid, fulfiller_name, time_created
		return new CompletedRequest(
				UUID.fromString(resultSet.getString("id")),
				QuickItem.getItem(resultSet.getString("item")),
				resultSet.getInt("amount"),
				resultSet.getDouble("price"),
				UUID.fromString(resultSet.getString("requester_uuid")),
				resultSet.getString("requester_name"),
				UUID.fromString(resultSet.getString("fulfiller_uuid")),
				resultSet.getString("fulfiller_name"),
				resultSet.getLong("time_created")
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
