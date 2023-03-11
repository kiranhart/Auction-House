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
import ca.tweetzy.auctionhouse.api.events.AuctionEndEvent;
import ca.tweetzy.auctionhouse.auction.AuctionedItem;
import ca.tweetzy.auctionhouse.auction.enums.AuctionSaleType;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.hooks.EconomyManager;
import ca.tweetzy.core.utils.PlayerUtils;
import ca.tweetzy.core.utils.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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

		final AuctionHouse instance = AuctionHouse.getInstance();
		Set<Map.Entry<UUID, AuctionedItem>> entrySet = instance.getAuctionItemManager().getItems().entrySet();
		Iterator<Map.Entry<UUID, AuctionedItem>> auctionItemIterator = entrySet.iterator();


		while (auctionItemIterator.hasNext()) {
			Map.Entry<UUID, AuctionedItem> entry = auctionItemIterator.next();
			AuctionedItem auctionItem = entry.getValue();
			ItemStack itemStack = auctionItem.getItem();

			if (instance.getAuctionItemManager().getGarbageBin().containsKey(auctionItem.getId())) {
				instance.getAuctionItemManager().getGarbageBin().remove(auctionItem.getId());
				instance.getAuctionItemManager().getDeletedItems().put(auctionItem.getId(), auctionItem);
				auctionItemIterator.remove();
				continue;
			}

			// begin the scuffed deletion
			if (!instance.getAuctionItemManager().getDeletedItems().keySet().isEmpty()) {
				if (Settings.GARBAGE_DELETION_TIMED_MODE.getBoolean() && clock % Settings.GARBAGE_DELETION_TIMED_DELAY.getInt() == 0) {
					instance.getDataManager().deleteItemsAsync(instance.getAuctionItemManager().getDeletedItems().values().stream().map(AuctionedItem::getId).collect(Collectors.toList()));
					if (!Settings.DISABLE_CLEANUP_MSG.getBoolean())
						instance.getLocale().newMessage(TextUtils.formatText("&aCleaned a total of &e" + instance.getAuctionItemManager().getDeletedItems().size() + "&a items.")).sendPrefixedMessage(Bukkit.getConsoleSender());
					instance.getAuctionItemManager().getDeletedItems().clear();
				} else {
					if (instance.getAuctionItemManager().getDeletedItems().size() >= Settings.GARBAGE_DELETION_MAX_ITEMS.getInt()) {
						instance.getDataManager().deleteItemsAsync(instance.getAuctionItemManager().getDeletedItems().values().stream().map(AuctionedItem::getId).collect(Collectors.toList()));
						if (!Settings.DISABLE_CLEANUP_MSG.getBoolean())
							instance.getLocale().newMessage(TextUtils.formatText("&aCleaned a total of &e" + instance.getAuctionItemManager().getDeletedItems().size() + "&a items.")).sendPrefixedMessage(Bukkit.getConsoleSender());
						instance.getAuctionItemManager().getDeletedItems().clear();
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
						Bukkit.getOnlinePlayers().forEach(player -> instance.getLocale().getMessage("auction.broadcast.ending")
								.processPlaceholder("item", AuctionAPI.getInstance().getItemName(itemStack))
								.processPlaceholder("seconds", timeRemaining)
								.sendPrefixedMessage(player));
					}
				}
			}

			if (timeRemaining <= 0 && !auctionItem.isExpired()) {

				// the owner is the highest bidder, so just expire
				if (auctionItem.getHighestBidder().equals(auctionItem.getOwner())) {
					auctionItem.setExpired(true);
					continue;
				}

				OfflinePlayer auctionWinner = Bukkit.getOfflinePlayer(auctionItem.getHighestBidder());

				double finalPrice = auctionItem.getCurrentPrice();
				double tax = Settings.TAX_ENABLED.getBoolean() ? (Settings.TAX_SALES_TAX_AUCTION_WON_PERCENTAGE.getDouble() / 100) * auctionItem.getCurrentPrice() : 0D;

				if (!Settings.BIDDING_TAKES_MONEY.getBoolean())
					if (!EconomyManager.hasBalance(auctionWinner, Settings.TAX_CHARGE_SALES_TAX_TO_BUYER.getBoolean() ? finalPrice + tax : finalPrice)) {
						auctionItem.setExpired(true);
						continue;
					}


				AuctionEndEvent auctionEndEvent = new AuctionEndEvent(Bukkit.getOfflinePlayer(auctionItem.getOwner()), auctionWinner, auctionItem, AuctionSaleType.USED_BIDDING_SYSTEM, tax);
				instance.getServer().getPluginManager().callEvent(auctionEndEvent);
				if (auctionEndEvent.isCancelled()) continue;


				if (!Settings.BIDDING_TAKES_MONEY.getBoolean())
					AuctionAPI.getInstance().withdrawBalance(auctionWinner, Settings.TAX_CHARGE_SALES_TAX_TO_BUYER.getBoolean() ? finalPrice + tax : finalPrice);

				AuctionAPI.getInstance().depositBalance(Bukkit.getOfflinePlayer(auctionItem.getOwner()), Settings.TAX_CHARGE_SALES_TAX_TO_BUYER.getBoolean() ? finalPrice : finalPrice - tax, auctionItem.getItem(), auctionWinner);

				// alert seller and buyer
				if (Bukkit.getOfflinePlayer(auctionItem.getOwner()).isOnline()) {
					instance.getLocale().getMessage("auction.itemsold")
							.processPlaceholder("item", AuctionAPI.getInstance().getItemName(itemStack))
							.processPlaceholder("amount", itemStack.clone().getAmount())
							.processPlaceholder("price", AuctionAPI.getInstance().formatNumber(Settings.TAX_CHARGE_SALES_TAX_TO_BUYER.getBoolean() ? finalPrice : finalPrice - tax))
							.processPlaceholder("buyer_name", Bukkit.getOfflinePlayer(auctionItem.getHighestBidder()).getName())
							.processPlaceholder("buyer_displayname", AuctionAPI.getInstance().getDisplayName(Bukkit.getOfflinePlayer(auctionItem.getHighestBidder())))
							.sendPrefixedMessage(Bukkit.getOfflinePlayer(auctionItem.getOwner()).getPlayer());
					instance.getLocale().getMessage("pricing.moneyadd").processPlaceholder("player_balance", AuctionAPI.getInstance().formatNumber(EconomyManager.getBalance(Bukkit.getOfflinePlayer(auctionItem.getOwner())))).processPlaceholder("price", AuctionAPI.getInstance().formatNumber(Settings.TAX_CHARGE_SALES_TAX_TO_BUYER.getBoolean() ? finalPrice : finalPrice - tax)).sendPrefixedMessage(Bukkit.getOfflinePlayer(auctionItem.getOwner()).getPlayer());
				}

				if (auctionWinner.isOnline()) {
					assert auctionWinner.getPlayer() != null;
					instance.getLocale().getMessage("auction.bidwon")
							.processPlaceholder("item", AuctionAPI.getInstance().getItemName(itemStack))
							.processPlaceholder("amount", itemStack.getAmount())
							.processPlaceholder("price", AuctionAPI.getInstance().formatNumber(Settings.TAX_CHARGE_SALES_TAX_TO_BUYER.getBoolean() ? finalPrice + tax : finalPrice))
							.sendPrefixedMessage(auctionWinner.getPlayer());

					if (!Settings.BIDDING_TAKES_MONEY.getBoolean())
						instance.getLocale().getMessage("pricing.moneyremove").processPlaceholder("player_balance", AuctionAPI.getInstance().formatNumber(EconomyManager.getBalance(auctionWinner.getPlayer()))).processPlaceholder("price", AuctionAPI.getInstance().formatNumber(Settings.TAX_CHARGE_SALES_TAX_TO_BUYER.getBoolean() ? finalPrice + tax : finalPrice)).sendPrefixedMessage(auctionWinner.getPlayer());

					// handle full inventory
					if (auctionWinner.getPlayer().getInventory().firstEmpty() == -1) {
						if (Settings.ALLOW_PURCHASE_IF_INVENTORY_FULL.getBoolean()) {
							if (Settings.SYNCHRONIZE_ITEM_ADD.getBoolean())
								AuctionHouse.newChain().sync(() -> PlayerUtils.giveItem(auctionWinner.getPlayer(), itemStack)).execute();
							else
								PlayerUtils.giveItem(auctionWinner.getPlayer(), itemStack);

							instance.getAuctionItemManager().sendToGarbage(auctionItem);
							continue;
						}
					} else {
						if (Settings.SYNCHRONIZE_ITEM_ADD.getBoolean())
							AuctionHouse.newChain().sync(() -> PlayerUtils.giveItem(auctionWinner.getPlayer(), itemStack)).execute();
						else
							PlayerUtils.giveItem(auctionWinner.getPlayer(), itemStack);

						instance.getAuctionItemManager().sendToGarbage(auctionItem);
						continue;
					}
				}

				auctionItem.setOwner(auctionWinner.getUniqueId());
				auctionItem.setHighestBidder(auctionWinner.getUniqueId());
				auctionItem.setExpired(true);
			}
		}
	}

}
