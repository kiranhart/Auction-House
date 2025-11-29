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

package ca.tweetzy.auctionhouse.model;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.settings.Settings;
import org.bukkit.Bukkit;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

/**
 * Handles transaction logging to daily-rotated files
 */
public final class TransactionLogger {

	private final AuctionHouse plugin;
	private final File logsDirectory;
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private final SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private BufferedWriter writer;
	private String currentLogDate;
	private final ConcurrentLinkedQueue<String> messageQueue = new ConcurrentLinkedQueue<>();
	private int taskId = -1;
	private int cleanupTaskId = -1;
	
	private volatile boolean running = false;

	public TransactionLogger(AuctionHouse plugin) {
		this.plugin = plugin;
		this.logsDirectory = new File(plugin.getDataFolder(), "logs");
		
		// Create logs directory if it doesn't exist
		if (!this.logsDirectory.exists()) {
			this.logsDirectory.mkdirs();
		}
	}

	/**
	 * Starts the transaction logger
	 */
	public void start() {
		if (running) return;
		
		running = true;
		openLogFile();
		
		// Start async task to flush messages
		taskId = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
			try {
				flushMessages();
			} catch (Exception e) {
				plugin.getLogger().severe("Error flushing transaction log messages: " + e.getMessage());
				e.printStackTrace();
			}
		}, 20L, 20L).getTaskId(); // Flush every second
		
		// Start cleanup task (runs daily)
		cleanupTaskId = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
			try {
				cleanupOldLogs();
			} catch (Exception e) {
				plugin.getLogger().severe("Error cleaning up old transaction logs: " + e.getMessage());
				e.printStackTrace();
			}
		}, 20L * 60L * 60L, 20L * 60L * 60L * 24L).getTaskId(); // Run every 24 hours, starting 1 hour after startup
	}

	/**
	 * Stops the transaction logger
	 */
	public void stop() {
		running = false;
		
		// Cancel tasks
		if (taskId != -1) {
			Bukkit.getScheduler().cancelTask(taskId);
			taskId = -1;
		}
		
		if (cleanupTaskId != -1) {
			Bukkit.getScheduler().cancelTask(cleanupTaskId);
			cleanupTaskId = -1;
		}
		
		// Flush remaining messages
		flushMessages();
		
		// Close writer
		closeLogFile();
	}

	/**
	 * Opens or rotates the log file based on current date
	 */
	private synchronized void openLogFile() {
		String today = dateFormat.format(new Date());
		
		// Check if we need to rotate
		if (currentLogDate != null && currentLogDate.equals(today) && writer != null) {
			return; // Already using correct file
		}
		
		// Close existing writer if any
		if (writer != null) {
			closeLogFile();
		}
		
		try {
			File logFile = new File(logsDirectory, "transactions-" + today + ".log");
			writer = new BufferedWriter(new FileWriter(logFile, true)); // Append mode
			currentLogDate = today;
		} catch (IOException e) {
			plugin.getLogger().severe("Failed to open transaction log file: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Closes the current log file
	 */
	private synchronized void closeLogFile() {
		if (writer != null) {
			try {
				writer.flush();
				writer.close();
			} catch (IOException e) {
				plugin.getLogger().severe("Failed to close transaction log file: " + e.getMessage());
				e.printStackTrace();
			}
			writer = null;
		}
	}

	/**
	 * Flushes queued messages to the log file
	 */
	private synchronized void flushMessages() {
		if (!running || writer == null) return;
		
		// Check if we need to rotate the log file
		String today = dateFormat.format(new Date());
		if (!today.equals(currentLogDate)) {
			openLogFile();
		}
		
		String message;
		while ((message = messageQueue.poll()) != null) {
			try {
				writer.write(message);
				writer.newLine();
			} catch (IOException e) {
				plugin.getLogger().severe("Failed to write to transaction log: " + e.getMessage());
				e.printStackTrace();
				// Attempt to reopen file
				openLogFile();
				break;
			}
		}
		
		// Flush to disk
		try {
			if (writer != null) {
				writer.flush();
			}
		} catch (IOException e) {
			plugin.getLogger().severe("Failed to flush transaction log: " + e.getMessage());
		}
	}

	/**
	 * Logs a message to the transaction log
	 */
	private void log(String message) {
		if (!running) return;
		
		String timestamp = timestampFormat.format(new Date());
		messageQueue.offer("[" + timestamp + "] " + message);
	}

	// ===== Transaction Type Logging Methods =====

	/**
	 * Logs an auction creation
	 */
	public void logAuctionCreate(String sellerName, String itemName, int quantity, double price, String currency, String auctionId, boolean isBidItem) {
		log("AUCTION_CREATE | Seller: " + sellerName + " | Item: " + itemName + " x" + quantity + 
			" | Price: " + price + " (" + currency + ") | AuctionID: " + auctionId + 
			" | Type: " + (isBidItem ? "AUCTION" : "BIN") + " | Status: SUCCESS");
	}

	/**
	 * Logs an auction purchase (buy now)
	 */
	public void logAuctionPurchase(String buyerName, String sellerName, String itemName, int quantity, double price, String currency, String auctionId, 
								   Double buyerOldBalance, Double buyerNewBalance, Double sellerOldBalance, Double sellerNewBalance) {
		String balanceInfo = "";
		if (buyerOldBalance != null && buyerNewBalance != null) {
			balanceInfo += " | BuyerBalance: " + buyerOldBalance + " -> " + buyerNewBalance;
		}
		if (sellerOldBalance != null && sellerNewBalance != null) {
			balanceInfo += " | SellerBalance: " + sellerOldBalance + " -> " + sellerNewBalance;
		}
		log("AUCTION_PURCHASE | Buyer: " + buyerName + " | Seller: " + sellerName + 
			" | Item: " + itemName + " x" + quantity + " | Price: " + price + " (" + currency + ") | AuctionID: " + auctionId + balanceInfo + " | Status: SUCCESS");
	}

	/**
	 * Logs an auction bid
	 */
	public void logAuctionBid(String bidderName, String sellerName, String itemName, double bidAmount, double previousBid, String currency, String auctionId,
							  Double bidderOldBalance, Double bidderNewBalance, Double previousBidderOldBalance, Double previousBidderNewBalance) {
		String balanceInfo = "";
		if (bidderOldBalance != null && bidderNewBalance != null) {
			balanceInfo += " | BidderBalance: " + bidderOldBalance + " -> " + bidderNewBalance;
		}
		if (previousBidderOldBalance != null && previousBidderNewBalance != null) {
			balanceInfo += " | PreviousBidderBalance: " + previousBidderOldBalance + " -> " + previousBidderNewBalance;
		}
		log("AUCTION_BID | Bidder: " + bidderName + " | Seller: " + sellerName + 
			" | Item: " + itemName + " | Bid: " + bidAmount + " (" + currency + ") | PreviousBid: " + previousBid + 
			" | AuctionID: " + auctionId + balanceInfo + " | Status: SUCCESS");
	}

	/**
	 * Logs an auction expiration/sale via bidding
	 */
	public void logAuctionExpire(String sellerName, String buyerName, String itemName, int quantity, double finalPrice, String currency, String auctionId,
								 Double buyerOldBalance, Double buyerNewBalance, Double sellerOldBalance, Double sellerNewBalance) {
		String balanceInfo = "";
		if (buyerOldBalance != null && buyerNewBalance != null) {
			balanceInfo += " | BuyerBalance: " + buyerOldBalance + " -> " + buyerNewBalance;
		}
		if (sellerOldBalance != null && sellerNewBalance != null) {
			balanceInfo += " | SellerBalance: " + sellerOldBalance + " -> " + sellerNewBalance;
		}
		log("AUCTION_EXPIRE | Seller: " + sellerName + " | Buyer: " + buyerName + 
			" | Item: " + itemName + " x" + quantity + " | FinalPrice: " + finalPrice + " (" + currency + ") | AuctionID: " + auctionId + balanceInfo + " | Status: SUCCESS");
	}

	/**
	 * Logs an auction cancellation/deletion
	 */
	public void logAuctionCancel(String sellerName, String itemName, String auctionId, String reason) {
		log("AUCTION_CANCEL | Seller: " + sellerName + " | Item: " + itemName + 
			" | AuctionID: " + auctionId + " | Reason: " + reason + " | Status: SUCCESS");
	}

	/**
	 * Logs a request creation
	 */
	public void logRequestCreate(String playerName, String itemName, int quantity, double price, String currency, String requestId) {
		log("REQUEST_CREATE | Player: " + playerName + " | Item: " + itemName + " x" + quantity + 
			" | Price: " + price + " (" + currency + ") | RequestID: " + requestId + " | Status: SUCCESS");
	}

	/**
	 * Logs a request fulfillment
	 */
	public void logRequestFulfill(String requestId, String requesterName, String fulfillerName, String itemName, int quantity, double price, String currency,
								  Double requesterOldBalance, Double requesterNewBalance, Double fulfillerOldBalance, Double fulfillerNewBalance) {
		String balanceInfo = "";
		if (requesterOldBalance != null && requesterNewBalance != null) {
			balanceInfo += " | RequesterBalance: " + requesterOldBalance + " -> " + requesterNewBalance;
		}
		if (fulfillerOldBalance != null && fulfillerNewBalance != null) {
			balanceInfo += " | FulfillerBalance: " + fulfillerOldBalance + " -> " + fulfillerNewBalance;
		}
		log("REQUEST_FULFILL | RequestID: " + requestId + " | Requester: " + requesterName + 
			" | Fulfiller: " + fulfillerName + " | Item: " + itemName + " x" + quantity + 
			" | Price: " + price + " (" + currency + ")" + balanceInfo + " | Status: SUCCESS");
	}

	/**
	 * Logs a request deletion
	 */
	public void logRequestDelete(String requestId, String ownerName, String reason) {
		log("REQUEST_DELETE | RequestID: " + requestId + " | Owner: " + ownerName + 
			" | Reason: " + reason + " | Status: SUCCESS");
	}

	/**
	 * Logs a cart purchase
	 */
	public void logCartPurchase(String buyerName, int itemCount, double totalPrice, String currency, Double buyerOldBalance, Double buyerNewBalance) {
		String balanceInfo = "";
		if (buyerOldBalance != null && buyerNewBalance != null) {
			balanceInfo += " | BuyerBalance: " + buyerOldBalance + " -> " + buyerNewBalance;
		}
		log("CART_PURCHASE | Buyer: " + buyerName + " | ItemCount: " + itemCount + 
			" | TotalPrice: " + totalPrice + " (" + currency + ")" + balanceInfo + " | Status: SUCCESS");
	}

	/**
	 * Logs a ban creation
	 */
	public void logBanCreate(String adminName, String bannedPlayerName, String banTypes, String reason, boolean permanent) {
		log("BAN_CREATE | Admin: " + adminName + " | BannedPlayer: " + bannedPlayerName + 
			" | BanTypes: " + banTypes + " | Reason: " + reason + " | Permanent: " + permanent + " | Status: SUCCESS");
	}

	/**
	 * Logs a ban removal
	 */
	public void logBanRemove(String adminName, String unbannedPlayerName) {
		log("BAN_REMOVE | Admin: " + adminName + " | UnbannedPlayer: " + unbannedPlayerName + " | Status: SUCCESS");
	}

	/**
	 * Logs a payment creation
	 */
	public void logPaymentCreate(String playerName, double amount, String currency, String reason, String paymentId, Double playerOldBalance, Double playerNewBalance) {
		String balanceInfo = "";
		if (playerOldBalance != null && playerNewBalance != null) {
			balanceInfo += " | PlayerBalance: " + playerOldBalance + " -> " + playerNewBalance;
		}
		log("PAYMENT_CREATE | Player: " + playerName + " | Amount: " + amount + " (" + currency + ") | Reason: " + reason + 
			" | PaymentID: " + paymentId + balanceInfo + " | Status: SUCCESS");
	}

	/**
	 * Logs a payment collection
	 */
	public void logPaymentCollect(String playerName, double amount, String currency, String paymentId, Double playerOldBalance, Double playerNewBalance) {
		String balanceInfo = "";
		if (playerOldBalance != null && playerNewBalance != null) {
			balanceInfo += " | PlayerBalance: " + playerOldBalance + " -> " + playerNewBalance;
		}
		log("PAYMENT_COLLECT | Player: " + playerName + " | Amount: " + amount + " (" + currency + ") | PaymentID: " + paymentId + balanceInfo + " | Status: SUCCESS");
	}

	/**
	 * Logs an admin action (return, claim, delete, copy)
	 */
	public void logAdminAction(String adminName, String targetPlayerName, String itemName, String action, String auctionId) {
		log("ADMIN_ACTION | Admin: " + adminName + " | TargetPlayer: " + targetPlayerName + 
			" | Item: " + itemName + " | Action: " + action + " | AuctionID: " + auctionId + " | Status: SUCCESS");
	}

	/**
	 * Logs an admin command execution
	 */
	public void logAdminCommand(String adminName, String command, String details) {
		log("ADMIN_COMMAND | Admin: " + adminName + " | Command: " + command + 
			" | Details: " + details + " | Status: SUCCESS");
	}

	/**
	 * Logs an error
	 */
	public void logError(String transactionType, String details, String errorMessage) {
		log("ERROR | Type: " + transactionType + " | Details: " + details + 
			" | Error: " + errorMessage + " | Status: FAILED");
	}

	/**
	 * Logs a warning
	 */
	public void logWarning(String transactionType, String details, String warningMessage) {
		log("WARNING | Type: " + transactionType + " | Details: " + details + 
			" | Warning: " + warningMessage);
	}

	/**
	 * Cleans up old log files based on retention days setting
	 */
	public void cleanupOldLogs() {
		int retentionDays = Settings.TRANSACTION_LOGGING_RETENTION_DAYS.getInt();
		
		// If retention is 0, never cleanup
		if (retentionDays <= 0) {
			return;
		}
		
		File[] logFiles = logsDirectory.listFiles((dir, name) -> 
			name.startsWith("transactions-") && name.endsWith(".log")
		);
		
		if (logFiles == null || logFiles.length == 0) {
			return;
		}
		
		long cutoffTime = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(retentionDays);
		int deletedCount = 0;
		
		for (File logFile : logFiles) {
			if (logFile.lastModified() < cutoffTime) {
				if (logFile.delete()) {
					deletedCount++;
				} else {
					plugin.getLogger().warning("Failed to delete old transaction log: " + logFile.getName());
				}
			}
		}
		
		if (deletedCount > 0) {
			plugin.getLogger().info("Cleaned up " + deletedCount + " old transaction log file(s)");
		}
	}
}

