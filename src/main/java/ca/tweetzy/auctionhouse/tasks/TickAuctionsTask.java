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

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.auction.AuctionedItem;
import ca.tweetzy.auctionhouse.auction.enums.AuctionSaleType;
import ca.tweetzy.auctionhouse.events.AuctionEndEvent;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.utils.PlayerUtils;
import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.flight.nbtapi.NBT;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

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
				AuctionHouse.getAuctionItemManager().getDeletedItems().put(auctionItem.getId(), auctionItem);
				auctionItemIterator.remove();
				continue;
			}

			// begin the scuffed deletion
			if (!AuctionHouse.getAuctionItemManager().getDeletedItems().keySet().isEmpty()) {
				if (Settings.GARBAGE_DELETION_TIMED_MODE.getBoolean() && clock % Settings.GARBAGE_DELETION_TIMED_DELAY.getInt() == 0) {
					AuctionHouse.getDataManager().deleteItemsAsync(AuctionHouse.getAuctionItemManager().getDeletedItems().values().stream().map(AuctionedItem::getId).collect(Collectors.toList()));
					if (!Settings.DISABLE_CLEANUP_MSG.getBoolean())
						AuctionHouse.getInstance().getLocale().newMessage(Common.colorize("&aCleaned a total of &e" + AuctionHouse.getAuctionItemManager().getDeletedItems().size() + "&a items.")).sendPrefixedMessage(Bukkit.getConsoleSender());
					AuctionHouse.getAuctionItemManager().getDeletedItems().clear();
				} else {
					if (AuctionHouse.getAuctionItemManager().getDeletedItems().size() >= Settings.GARBAGE_DELETION_MAX_ITEMS.getInt()) {
						AuctionHouse.getDataManager().deleteItemsAsync(AuctionHouse.getAuctionItemManager().getDeletedItems().values().stream().map(AuctionedItem::getId).collect(Collectors.toList()));
						if (!Settings.DISABLE_CLEANUP_MSG.getBoolean())
							AuctionHouse.getInstance().getLocale().newMessage(Common.colorize("&aCleaned a total of &e" + AuctionHouse.getAuctionItemManager().getDeletedItems().size() + "&a items.")).sendPrefixedMessage(Bukkit.getConsoleSender());
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
						Bukkit.getOnlinePlayers().forEach(player -> AuctionHouse.getInstance().getLocale().getMessage("auction.broadcast.ending")
								.processPlaceholder("item", AuctionAPI.getInstance().getItemName(itemStack))
								.processPlaceholder("seconds", timeRemaining)
								.sendPrefixedMessage(player));
					}
				}
			}

			if (timeRemaining <= 0 && !auctionItem.isExpired()) {

				// CRITICAL: Atomically mark item as processed BEFORE any operations to prevent race conditions
				// If another thread (purchase/tick) already processed it, skip
				if (!AuctionHouse.getAuctionItemManager().tryMarkAsPurchased(auctionItem)) {
					// Item was already processed by another thread, skip
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
				if (!auctionWinner.isOnline() && auctionItem.hasValidItemCurrency()) {
					auctionItem.setExpired(true);
					// Remove from garbage since we're not processing it (will be handled as expired)
					AuctionHouse.getAuctionItemManager().getGarbageBin().remove(auctionItem.getId());
					continue;
				}

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
				if (Bukkit.getOfflinePlayer(auctionItem.getOwner()).isOnline()) {
					AuctionHouse.getInstance().getLocale().getMessage("auction.itemsold")
							.processPlaceholder("item", AuctionAPI.getInstance().getItemName(itemStack))
							.processPlaceholder("amount", itemStack.clone().getAmount())
							.processPlaceholder("price", AuctionHouse.getAPI().getFinalizedCurrencyNumber(Settings.TAX_CHARGE_SALES_TAX_TO_BUYER.getBoolean() ? finalPrice : finalPrice - tax, auctionItem.getCurrency(), auctionItem.getCurrencyItem()))
							.processPlaceholder("buyer_name", Bukkit.getOfflinePlayer(auctionItem.getHighestBidder()).getName())
							.processPlaceholder("buyer_displayname", AuctionAPI.getInstance().getDisplayName(Bukkit.getOfflinePlayer(auctionItem.getHighestBidder())))
							.sendPrefixedMessage(Bukkit.getOfflinePlayer(auctionItem.getOwner()).getPlayer());
					AuctionHouse.getInstance().getLocale().getMessage("pricing.moneyadd")
							.processPlaceholder("player_balance", AuctionHouse.getCurrencyManager().getFormattedBalance(Bukkit.getOfflinePlayer(auctionItem.getOwner()), auctionItem.getCurrency(), auctionItem.getCurrencyItem()))
							.processPlaceholder("price", AuctionHouse.getAPI().getFinalizedCurrencyNumber(Settings.TAX_CHARGE_SALES_TAX_TO_BUYER.getBoolean() ? finalPrice : finalPrice - tax, auctionItem.getCurrency(), auctionItem.getCurrencyItem()))
							.sendPrefixedMessage(Bukkit.getOfflinePlayer(auctionItem.getOwner()).getPlayer());
				}

				if (auctionWinner.isOnline()) {
					assert auctionWinner.getPlayer() != null;
					AuctionHouse.getInstance().getLocale().getMessage("auction.bidwon")
							.processPlaceholder("item", AuctionAPI.getInstance().getItemName(itemStack))
							.processPlaceholder("amount", itemStack.getAmount())
							.processPlaceholder("price", AuctionHouse.getAPI().getFinalizedCurrencyNumber(Settings.TAX_CHARGE_SALES_TAX_TO_BUYER.getBoolean() ? finalPrice + tax : finalPrice, auctionItem.getCurrency(), auctionItem.getCurrencyItem()))
							.sendPrefixedMessage(auctionWinner.getPlayer());

					if (!Settings.BIDDING_TAKES_MONEY.getBoolean())
						AuctionHouse.getInstance().getLocale().getMessage("pricing.moneyremove")
								.processPlaceholder("player_balance", AuctionHouse.getCurrencyManager().getFormattedBalance(auctionWinner.getPlayer(), auctionItem.getCurrency(), auctionItem.getCurrencyItem()))
								.processPlaceholder("price", AuctionHouse.getAPI().getFinalizedCurrencyNumber(Settings.TAX_CHARGE_SALES_TAX_TO_BUYER.getBoolean() ? finalPrice + tax : finalPrice, auctionItem.getCurrency(), auctionItem.getCurrencyItem()))
								.sendPrefixedMessage(auctionWinner.getPlayer());

					// remove the dupe tracking
					NBT.modify(itemStack, nbt -> {
						nbt.removeKey("AuctionDupeTracking");
					});

					// handle full inventory
					if (auctionWinner.getPlayer().getInventory().firstEmpty() != -1) {
						Bukkit.getServer().getScheduler().runTaskLater(AuctionHouse.getInstance(), () -> PlayerUtils.giveItem(auctionWinner.getPlayer(), itemStack), 0);
						// Item already marked as purchased above (race condition protection)
					} else {
						auctionItem.setOwner(auctionWinner.getUniqueId());
						auctionItem.setHighestBidder(auctionWinner.getUniqueId());
						auctionItem.setExpired(true);
						// Remove from garbage since inventory is full (will be handled as expired)
						AuctionHouse.getAuctionItemManager().getGarbageBin().remove(auctionItem.getId());
						continue;
					}

				}

				auctionItem.setOwner(auctionWinner.getUniqueId());
				auctionItem.setHighestBidder(auctionWinner.getUniqueId());
				auctionItem.setExpired(true);
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
