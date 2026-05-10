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

package ca.tweetzy.auctionhouse.tasks;


import ca.tweetzy.auctionhouse.lang.AuctionLocale;
import ca.tweetzy.auctionhouse.helpers.PlayerLookup;
import ca.tweetzy.flight.utils.PlayerUtil;
import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.auction.AuctionedItem;
import ca.tweetzy.auctionhouse.auction.enums.AuctionSaleType;
import ca.tweetzy.auctionhouse.events.AuctionEndEvent;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.flight.nbtapi.NBT;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: February 18 2021
 * Time Created: 8:47 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class TickAuctionsTask extends BukkitRunnable {

	private static TickAuctionsTask instance;
	private static long clock;


	public static TickAuctionsTask startTask() {
		if (instance == null) {
			clock = 0L;
			instance = new TickAuctionsTask();
			instance.runTaskTimerAsynchronously(AuctionHouse.getInstance(), 0, (long) 20 * Settings.TICK_UPDATE_TIME.getInt());
		}
		return instance;
	}

	@Override
	public void run() {
		clock += Settings.TICK_UPDATE_TIME.getInt();

		Set<Map.Entry<UUID, AuctionedItem>> entrySet = AuctionHouse.getAuctionItemManager().getItems().entrySet();
		Iterator<Map.Entry<UUID, AuctionedItem>> auctionItemIterator = entrySet.iterator();


		while (auctionItemIterator.hasNext()) {
			Map.Entry<UUID, AuctionedItem> entry = auctionItemIterator.next();
			AuctionedItem auctionItem = entry.getValue();
			ItemStack itemStack = auctionItem.getItem();

			if (AuctionHouse.getAuctionItemManager().getGarbageBin().containsKey(auctionItem.getId())) {
				AuctionHouse.getAuctionItemManager().getGarbageBin().remove(auctionItem.getId());
				AuctionHouse.getWatchlistManager().removeListing(auctionItem.getId());
				AuctionHouse.getAuctionItemManager().getDeletedItems().put(auctionItem.getId(), auctionItem);
				auctionItemIterator.remove();
				continue;
			}

			// begin the scuffed deletion
			if (!AuctionHouse.getAuctionItemManager().getDeletedItems().keySet().isEmpty()) {
				if (Settings.GARBAGE_DELETION_TIMED_MODE.getBoolean() && clock % Settings.GARBAGE_DELETION_TIMED_DELAY.getInt() == 0) {
					AuctionHouse.getDataManager().deleteItemsAsync(AuctionHouse.getAuctionItemManager().getDeletedItems().values().stream().map(AuctionedItem::getId).collect(Collectors.toList()));
					if (!Settings.DISABLE_CLEANUP_MSG.getBoolean())
						Common.tell(Bukkit.getConsoleSender(), Common.colorize("&aCleaned a total of &e" + AuctionHouse.getAuctionItemManager().getDeletedItems().size() + "&a items."));
					AuctionHouse.getAuctionItemManager().getDeletedItems().clear();
				} else {
					if (AuctionHouse.getAuctionItemManager().getDeletedItems().size() >= Settings.GARBAGE_DELETION_MAX_ITEMS.getInt()) {
						AuctionHouse.getDataManager().deleteItemsAsync(AuctionHouse.getAuctionItemManager().getDeletedItems().values().stream().map(AuctionedItem::getId).collect(Collectors.toList()));
						if (!Settings.DISABLE_CLEANUP_MSG.getBoolean())
							Common.tell(Bukkit.getConsoleSender(), Common.colorize("&aCleaned a total of &e" + AuctionHouse.getAuctionItemManager().getDeletedItems().size() + "&a items."));
						AuctionHouse.getAuctionItemManager().getDeletedItems().clear();
					}
				}
			}

			// end the scuffed deletion
			if (auctionItem.isInfinite()) continue;

			long timeRemaining = (auctionItem.getExpiresAt() - System.currentTimeMillis()) / 1000;

			// broadcast ending
			if (!auctionItem.isExpired()) {
				if (Settings.BROADCAST_AUCTION_ENDING.getBoolean()) {
					if (timeRemaining <= Settings.BROADCAST_AUCTION_ENDING_AT_TIME.getInt() && timeRemaining % 10 == 0 && timeRemaining != 0) {
						Bukkit.getOnlinePlayers().forEach(player -> AuctionLocale.tell(player, "auction.broadcast.ending", "item",AuctionAPI.getInstance().getItemName(itemStack),"seconds",timeRemaining));
					}
				}
			}

			if (timeRemaining <= 0 && !auctionItem.isExpired()) {
				if (!AuctionHouse.getAuctionItemManager().tryMarkAsPurchased(auctionItem)) {
					continue;
				}

			// the owner is the highest bidder, so just expire
			if (auctionItem.getHighestBidder().equals(auctionItem.getOwner())) {
				if (auctionItem.isServerItem() || auctionItem.isRequest()) {
					// Already marked as purchased above
				} else {
					// Remove from garbage bin so item can be retrieved from collection bin
					AuctionHouse.getAuctionItemManager().getGarbageBin().remove(auctionItem.getId());
					auctionItem.setExpired(true);
				}
				continue;
			}

				OfflinePlayer auctionWinner = Bukkit.getOfflinePlayer(auctionItem.getHighestBidder());
				double finalPrice = auctionItem.getCurrentPrice();
				double tax = Settings.TAX_ENABLED.getBoolean() ? (Settings.TAX_SALES_TAX_AUCTION_WON_PERCENTAGE.getDouble() / 100) * auctionItem.getCurrentPrice() : 0D;

				if (!Settings.BIDDING_TAKES_MONEY.getBoolean()) {
					if (!auctionItem.playerHasSufficientMoney(auctionWinner, Settings.TAX_CHARGE_SALES_TAX_TO_BUYER.getBoolean() ? finalPrice + tax : finalPrice)) {
						if (auctionItem.isServerItem()) {
							// Already marked as purchased above
						} else {
							auctionItem.setExpired(true);
							// Remove from garbage since we're not processing it (will be handled as expired)
							AuctionHouse.getAuctionItemManager().getGarbageBin().remove(auctionItem.getId());
						}
						continue;
					}
				}


				AuctionEndEvent auctionEndEvent = new AuctionEndEvent(Bukkit.getOfflinePlayer(auctionItem.getOwner()), auctionWinner, auctionItem, AuctionSaleType.USED_BIDDING_SYSTEM, tax);
				AuctionHouse.getInstance().getServer().getPluginManager().callEvent(auctionEndEvent);
				if (auctionEndEvent.isCancelled()) {
					// Event cancelled, remove from garbage and mark as expired
					AuctionHouse.getAuctionItemManager().getGarbageBin().remove(auctionItem.getId());
					auctionItem.setExpired(true);
					continue;
				}


				if (!Settings.BIDDING_TAKES_MONEY.getBoolean())
					AuctionAPI.getInstance().withdrawBalance(auctionWinner, Settings.TAX_CHARGE_SALES_TAX_TO_BUYER.getBoolean() ? finalPrice + tax : finalPrice, auctionItem);

				AuctionAPI.getInstance().depositBalance(Bukkit.getOfflinePlayer(auctionItem.getOwner()), Settings.TAX_CHARGE_SALES_TAX_TO_BUYER.getBoolean() ? finalPrice : finalPrice - tax, auctionItem.getItem(), auctionWinner, auctionItem);

				// alert seller and buyer
				OfflinePlayer auctionOwner = Bukkit.getOfflinePlayer(auctionItem.getOwner());
				String itemName = AuctionAPI.getInstance().getItemName(itemStack);
				String sellerPriceStr = AuctionHouse.getAPI().getFinalizedCurrencyNumber(Settings.TAX_CHARGE_SALES_TAX_TO_BUYER.getBoolean() ? finalPrice : finalPrice - tax, auctionItem.getCurrency(), auctionItem.getCurrencyItem());
				String buyerName = Bukkit.getOfflinePlayer(auctionItem.getHighestBidder()).getName() != null ? Bukkit.getOfflinePlayer(auctionItem.getHighestBidder()).getName() : "Unknown";

				if (auctionOwner.isOnline() && auctionOwner.getPlayer() != null) {
					AuctionLocale.tell(auctionOwner.getPlayer(), "auction.itemsold", "item",itemName,"amount",String.valueOf(itemStack.clone().getAmount()),"price",sellerPriceStr,"buyer_name",buyerName,"buyer_displayname",AuctionAPI.getInstance().getDisplayName(Bukkit.getOfflinePlayer(auctionItem.getHighestBidder())));
					AuctionLocale.tell(auctionOwner.getPlayer(), "pricing.moneyadd", "player_balance",AuctionHouse.getCurrencyManager().getFormattedBalance(auctionOwner, auctionItem.getCurrency(), auctionItem.getCurrencyItem()),"price",sellerPriceStr);
				} else {
					java.util.Map<String, String> itemsoldPlaceholders = new java.util.HashMap<>();
					itemsoldPlaceholders.put("item", itemName);
					itemsoldPlaceholders.put("amount", String.valueOf(itemStack.clone().getAmount()));
					itemsoldPlaceholders.put("price", sellerPriceStr);
					itemsoldPlaceholders.put("buyer_name", buyerName);
					AuctionHouse.getNotificationManager().queue(auctionOwner.getUniqueId(), "auction.itemsold", itemsoldPlaceholders);
					java.util.Map<String, String> moneyaddPlaceholders = new java.util.HashMap<>();
					moneyaddPlaceholders.put("player_balance", AuctionHouse.getCurrencyManager().getFormattedBalance(auctionOwner, auctionItem.getCurrency(), auctionItem.getCurrencyItem()));
					moneyaddPlaceholders.put("price", sellerPriceStr);
					AuctionHouse.getNotificationManager().queue(auctionOwner.getUniqueId(), "pricing.moneyadd", moneyaddPlaceholders);
				}

				if (auctionWinner.isOnline()) {
					assert auctionWinner.getPlayer() != null;
					AuctionLocale.tell(auctionWinner.getPlayer(), "auction.bidwon", "item",AuctionAPI.getInstance().getItemName(itemStack),"amount",itemStack.getAmount(),"price",AuctionHouse.getAPI().getFinalizedCurrencyNumber(Settings.TAX_CHARGE_SALES_TAX_TO_BUYER.getBoolean() ? finalPrice + tax : finalPrice, auctionItem.getCurrency(), auctionItem.getCurrencyItem()));

					if (!Settings.BIDDING_TAKES_MONEY.getBoolean())
						AuctionLocale.tell(auctionWinner.getPlayer(), "pricing.moneyremove", "player_balance",AuctionHouse.getCurrencyManager().getFormattedBalance(auctionWinner.getPlayer(), auctionItem.getCurrency(), auctionItem.getCurrencyItem()),"price",AuctionHouse.getAPI().getFinalizedCurrencyNumber(Settings.TAX_CHARGE_SALES_TAX_TO_BUYER.getBoolean() ? finalPrice + tax : finalPrice, auctionItem.getCurrency(), auctionItem.getCurrencyItem()));

					// remove the dupe tracking
					NBT.modify(itemStack, nbt -> {
						nbt.removeKey("AuctionDupeTracking");
					});

					// handle full inventory
					if (auctionWinner.getPlayer().getInventory().firstEmpty() != -1) {
						Bukkit.getServer().getScheduler().runTaskLater(AuctionHouse.getInstance(), () -> PlayerUtil.giveItem(auctionWinner.getPlayer(), itemStack), 0);
						// Item already marked as purchased above (race condition protection)
						// Skip expiration logic to prevent item from appearing in collection bin
						continue;
					} else {
						auctionItem.setOwner(auctionWinner.getUniqueId());
						auctionItem.setHighestBidder(auctionWinner.getUniqueId());
						auctionItem.setExpired(true);
						// Remove from garbage since inventory is full (will be handled as expired)
						AuctionHouse.getAuctionItemManager().getGarbageBin().remove(auctionItem.getId());
						continue;
					}

				}

				// Winner offline: queue bid won and money remove notifications
				if (!auctionWinner.isOnline()) {
					String winnerPriceStr = AuctionHouse.getAPI().getFinalizedCurrencyNumber(Settings.TAX_CHARGE_SALES_TAX_TO_BUYER.getBoolean() ? finalPrice + tax : finalPrice, auctionItem.getCurrency(), auctionItem.getCurrencyItem());
					HashMap<String, String> bidwonPlaceholders = new HashMap<>();
					bidwonPlaceholders.put("item", itemName);
					bidwonPlaceholders.put("amount", String.valueOf(itemStack.getAmount()));
					bidwonPlaceholders.put("price", winnerPriceStr);
					AuctionHouse.getNotificationManager().queue(auctionWinner.getUniqueId(), "auction.bidwon", bidwonPlaceholders);
					if (!Settings.BIDDING_TAKES_MONEY.getBoolean()) {
						HashMap<String, String> moneyremovePlaceholders = new HashMap<>();
						moneyremovePlaceholders.put("player_balance", AuctionHouse.getCurrencyManager().getFormattedBalance(auctionWinner, auctionItem.getCurrency(), auctionItem.getCurrencyItem()));
						moneyremovePlaceholders.put("price", winnerPriceStr);
						AuctionHouse.getNotificationManager().queue(auctionWinner.getUniqueId(), "pricing.moneyremove", moneyremovePlaceholders);
					}
				}

				auctionItem.setOwner(auctionWinner.getUniqueId());
				auctionItem.setHighestBidder(auctionWinner.getUniqueId());
				auctionItem.setExpired(true);
				// Remove from garbage bin so item can be retrieved from collection bin
				AuctionHouse.getAuctionItemManager().getGarbageBin().remove(auctionItem.getId());
				// Item already marked as purchased above (race condition protection)
			}
		}
	}

	public boolean hasEmptyInventorySlot(Player player) {
		for (ItemStack item : player.getInventory().getContents()) {
			if (item == null || item.getType() == Material.AIR) {
				return true;
			}
		}
		return false;
	}
}
