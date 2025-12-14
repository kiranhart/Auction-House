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

package ca.tweetzy.auctionhouse.guis.transaction;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.auction.enums.AuctionItemCategory;
import ca.tweetzy.auctionhouse.auction.enums.AuctionSaleType;
import ca.tweetzy.auctionhouse.auction.enums.AuctionSortType;
import ca.tweetzy.auctionhouse.guis.AuctionPagedGUI;
import ca.tweetzy.auctionhouse.guis.core.GUIAuctionHouse;
import ca.tweetzy.auctionhouse.helpers.SlotHelper;
import ca.tweetzy.auctionhouse.model.MaterialCategorizer;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.auctionhouse.transaction.Transaction;
import ca.tweetzy.auctionhouse.transaction.TransactionViewFilter;
import ca.tweetzy.flight.gui.events.GuiClickEvent;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.flight.utils.Replacer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: March 22 2021
 * Time Created: 7:03 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUITransactionList extends AuctionPagedGUI<Transaction> {

	final AuctionPlayer auctionPlayer;
	final boolean showAll;
	private UUID searchUUID;

	public GUITransactionList(Player player, UUID searchUUID) {
		this(player, true);
		this.searchUUID = searchUUID;
	}

	public GUITransactionList(Player player, boolean showAll) {
		super(null, player, showAll ? Settings.GUI_TRANSACTIONS_TITLE_ALL.getString() : Settings.GUI_TRANSACTIONS_TITLE.getString(), 6, new ArrayList<>());
		this.auctionPlayer = AuctionHouse.getAuctionPlayerManager().getPlayer(player.getUniqueId());
		this.showAll = showAll;
		applyDelay();
		draw();
	}

	private void applyDelay() {
		setSlotClickDelay(getPreviousButtonSlot(), Settings.TRANSACTION_NAVIGATION_COOLDOWN.getLong());
		setSlotClickDelay(getNextButtonSlot(), Settings.TRANSACTION_NAVIGATION_COOLDOWN.getLong());

		SlotHelper.getButtonSlots(Settings.GUI_TRANSACTIONS_ITEMS_FILTER_SLOT.getString()).forEach(slot -> {
			setSlotClickDelay(slot, Settings.TRANSACTION_FILTER_COOLDOWN.getLong());
		});

		setClickDelayAction((lastClicked, delay, click) -> {
			if (click.slot == getPreviousButtonSlot() || click.slot == getNextButtonSlot()) {
				AuctionHouse.getInstance().getLocale()
						.getMessage("general.cooldown.navigate page")
						.processPlaceholder("time", AuctionHouse.getCooldownManager().formatTime(System.currentTimeMillis() - lastClicked))
						.sendPrefixedMessage(player);
				return;
			}

			AuctionHouse.getInstance().getLocale()
					.getMessage("general.cooldown.filter")
					.processPlaceholder("time", AuctionHouse.getCooldownManager().formatTime(System.currentTimeMillis() - lastClicked))
					.sendPrefixedMessage(player);

		});
	}

	@Override
	protected void prePopulate() {
		final UUID playerUuid = this.player.getUniqueId();
		
		// Start with a stream from all transactions
		this.items = AuctionHouse.getTransactionManager().getTransactions().values().stream()
				// Filter by showAll or player-specific
				.filter(transaction -> {
					if (this.showAll) {
						return true;
					}
					return transaction.getSeller().equals(playerUuid) || transaction.getBuyer().equals(playerUuid);
				})
				// Filter by search UUID if provided
				.filter(transaction -> {
					if (this.searchUUID != null) {
						return transaction.getSeller().equals(this.searchUUID) || transaction.getBuyer().equals(this.searchUUID);
					}
					return true;
				})
				// Filter by category
				.filter(transaction -> {
					final AuctionItemCategory selectedFilter = this.auctionPlayer.getSelectedTransactionFilter();
					if (selectedFilter == AuctionItemCategory.ALL || selectedFilter == AuctionItemCategory.SEARCH || selectedFilter == AuctionItemCategory.SELF) {
						return true;
					}
					return MaterialCategorizer.getMaterialCategory(transaction.getItem()) == selectedFilter;
				})
				// Filter by sale type
				.filter(transaction -> {
					final AuctionSaleType saleType = this.auctionPlayer.getSelectedTransactionSaleType();
					if (saleType == AuctionSaleType.USED_BIDDING_SYSTEM) {
						return transaction.getAuctionSaleType() == AuctionSaleType.USED_BIDDING_SYSTEM;
					}
					if (saleType == AuctionSaleType.WITHOUT_BIDDING_SYSTEM) {
						return transaction.getAuctionSaleType() == AuctionSaleType.WITHOUT_BIDDING_SYSTEM;
					}
					return true;
				})
				// Filter by view filter
				.filter(transaction -> {
					final TransactionViewFilter viewFilter = this.auctionPlayer.getTransactionViewFilter();
					if (viewFilter == TransactionViewFilter.ALL) {
						return true;
					}
					if (viewFilter == TransactionViewFilter.BOUGHT) {
						return transaction.getBuyer().equals(playerUuid);
					}
					if (viewFilter == TransactionViewFilter.SOLD) {
						return transaction.getSeller().equals(playerUuid);
					}
					return true;
				})
				// Collect to list once
				.collect(Collectors.toList());

		// Apply sorting with compound comparator
		final Comparator<Transaction> sortComparator = createSortComparator(this.auctionPlayer.getTransactionSortType());
		if (sortComparator != null) {
			this.items.sort(sortComparator);
		}
	}

	/**
	 * Creates a comparator for sorting transactions based on sort type
	 */
	private Comparator<Transaction> createSortComparator(AuctionSortType sortType) {
		if (sortType == AuctionSortType.PRICE) {
			return Comparator.comparingDouble(Transaction::getFinalPrice).reversed();
		}
		if (sortType == AuctionSortType.RECENT) {
			return Comparator.comparingLong(Transaction::getTransactionTime).reversed();
		}
		return null;
	}

	@Override
	protected void drawFixed() {

		// Other Buttons
		setButton(5, 0, getBackButton(), e -> {
			if (Settings.RESTRICT_ALL_TRANSACTIONS_TO_PERM.getBoolean() && !e.player.hasPermission("auctionhouse.transactions.viewall")) {
				e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
			} else {
				e.manager.showGUI(e.player, new GUITransactionType(e.player));
			}
		});

		setButton(Settings.GUI_TRANSACTIONS_ITEMS_FILTER_SLOT.getInt(), QuickItem
				.of(Settings.GUI_TRANSACTIONS_ITEMS_FILTER_ITEM.getString())
				.name(Settings.GUI_TRANSACTIONS_ITEMS_FILTER_NAME.getString())
				.lore(this.player, Replacer.replaceVariables(Settings.GUI_TRANSACTIONS_ITEMS_FILTER_LORE.getStringList(),
						"filter_category", auctionPlayer.getSelectedTransactionFilter().getTranslatedType(),
						"filter_auction_type", auctionPlayer.getSelectedTransactionSaleType().getTranslatedType(),
						"filter_sort_order", auctionPlayer.getTransactionSortType().getTranslatedType(),
						"filter_buy_type", auctionPlayer.getTransactionViewFilter().getTranslatedType()
				)).make(), click -> {

			if (click.clickType == ClickType.valueOf(Settings.CLICKS_FILTER_CATEGORY.getString().toUpperCase()) && Settings.FILTER_CLICKS_CHANGE_CATEGORY_ENABLED.getBoolean()) {
				this.auctionPlayer.setSelectedTransactionFilter(this.auctionPlayer.getSelectedTransactionFilter().next());
				draw();
			}


			if (click.clickType == ClickType.valueOf(Settings.CLICKS_FILTER_RESET.getString().toUpperCase()) && Settings.FILTER_CLICKS_RESET_ENABLED.getBoolean()) {
				this.auctionPlayer.resetTransactionFilter();
				draw();
				return;
			}


			if (click.clickType == ClickType.valueOf(Settings.CLICKS_FILTER_SORT_SALE_TYPE.getString().toUpperCase()) && Settings.FILTER_CLICKS_SALE_TYPE_ENABLED.getBoolean()) {
				if (Settings.ALLOW_USAGE_OF_BID_SYSTEM.getBoolean()) {
					this.auctionPlayer.setSelectedTransactionSaleType(this.auctionPlayer.getSelectedTransactionSaleType().next());
					draw();
				}
				return;
			}

			if (click.clickType == ClickType.valueOf(Settings.CLICKS_FILTER_TRANSACTION_BUY_TYPE.getString().toUpperCase()) && Settings.FILTER_CLICKS_TRANSACTION_BUY_TYPE_ENABLED.getBoolean()) {
				this.auctionPlayer.setTransactionViewFilter(this.auctionPlayer.getTransactionViewFilter().next());
				draw();
				return;
			}

			if (click.clickType == ClickType.valueOf(Settings.CLICKS_FILTER_SORT_PRICE_OR_RECENT.getString().toUpperCase()) && Settings.FILTER_CLICKS_SORT_PRICE_RECENT_ENABLED.getBoolean()) {
				this.auctionPlayer.setTransactionSortType(this.auctionPlayer.getTransactionSortType().next());
				draw();
			}
		});
	}

	@Override
	protected ItemStack makeDisplayItem(Transaction transaction) {
		final ItemStack item = transaction.getItem().clone();
		final OfflinePlayer seller = Bukkit.getOfflinePlayer(transaction.getSeller());
		final String SERVER_LISTING_NAME = AuctionHouse.getInstance().getLocale().getMessage("general.server listing").getMessage();

		return QuickItem
				.of(item)
				.amount(item.getAmount())
				.name(Replacer.replaceVariables(Settings.GUI_TRANSACTIONS_ITEM_TRANSACTION_NAME.getString(), "item_name", AuctionAPI.getInstance().getItemName(item), "transaction_id", transaction.getId().toString()))
				.lore(this.player, Replacer.replaceVariables(Settings.GUI_TRANSACTIONS_ITEM_TRANSACTION_LORE.getStringList(),
						"transaction_id", transaction.getId().toString(),
						"seller", seller.hasPlayedBefore() ? seller.getName() : SERVER_LISTING_NAME,
						"buyer", Bukkit.getOfflinePlayer(transaction.getBuyer()).getName(),
						"date", AuctionAPI.getInstance().convertMillisToDate(transaction.getTransactionTime()),
						"item_name", AuctionAPI.getInstance().getItemName(item),
						"item_quantity", String.valueOf(item.getAmount())
				)).make();
	}

	@Override
	protected void onClick(Transaction transaction, GuiClickEvent click) {
		if (click.clickType == ClickType.DROP && (player.isOp() || player.hasPermission("auctionhouse.admin"))) {
			AuctionHouse.getDataManager().deleteTransactions(Collections.singleton(transaction.getId()));
			AuctionHouse.getTransactionManager().removeTransaction(transaction.getId());
			click.manager.showGUI(click.player, new GUITransactionList(this.player, this.showAll));
		}

		if (click.clickType == ClickType.LEFT)
			click.manager.showGUI(click.player, new GUITransactionView(this.auctionPlayer, transaction, this.showAll));
	}
}
