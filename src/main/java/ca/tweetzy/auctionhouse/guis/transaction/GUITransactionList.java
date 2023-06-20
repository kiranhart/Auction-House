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
import ca.tweetzy.auctionhouse.guis.AbstractPlaceholderGui;
import ca.tweetzy.auctionhouse.guis.GUIAuctionHouse;
import ca.tweetzy.auctionhouse.helpers.ConfigurationItemHelper;
import ca.tweetzy.auctionhouse.helpers.MaterialCategorizer;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.auctionhouse.transaction.Transaction;
import ca.tweetzy.core.compatibility.XSound;
import ca.tweetzy.core.utils.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: March 22 2021
 * Time Created: 7:03 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUITransactionList extends AbstractPlaceholderGui {

	List<Transaction> transactions;
	final AuctionPlayer auctionPlayer;
	final Player player;
	final boolean showAll;


	public GUITransactionList(Player player, boolean showAll) {
		super(player);
		this.player = player;
		final AuctionHouse instance = AuctionHouse.getInstance();
		this.auctionPlayer = instance.getAuctionPlayerManager().getPlayer(this.player.getUniqueId());
		this.showAll = showAll;
		if (showAll)
			this.transactions = new ArrayList<>(instance.getTransactionManager().getTransactions().values());
		else
			this.transactions = instance.getTransactionManager().getTransactions().values().stream().filter(transaction -> transaction.getSeller().equals(player.getUniqueId()) || transaction.getBuyer().equals(player.getUniqueId())).collect(Collectors.toList());


		setTitle(TextUtils.formatText(showAll ? Settings.GUI_TRANSACTIONS_TITLE_ALL.getString() : Settings.GUI_TRANSACTIONS_TITLE.getString()));
		setRows(6);
		setAcceptsItems(false);
		setNavigateSound(XSound.matchXSound(Settings.SOUNDS_NAVIGATE_GUI_PAGES.getString()).orElse(XSound.ENTITY_BAT_TAKEOFF));
		draw();
	}

	private void draw() {
		reset();

		AuctionHouse.newChain().asyncFirst(() -> {
			// perform filter
			if (this.auctionPlayer.getSelectedTransactionFilter() != AuctionItemCategory.ALL && this.auctionPlayer.getSelectedTransactionFilter() != AuctionItemCategory.SEARCH && this.auctionPlayer.getSelectedTransactionFilter() != AuctionItemCategory.SELF) {
				this.transactions = this.transactions.stream().filter(item -> MaterialCategorizer.getMaterialCategory(item.getItem()) == this.auctionPlayer.getSelectedTransactionFilter()).collect(Collectors.toList());
			}

			if (this.auctionPlayer.getSelectedTransactionSaleType() == AuctionSaleType.USED_BIDDING_SYSTEM) {
				this.transactions = this.transactions.stream().filter(transaction -> transaction.getAuctionSaleType() == AuctionSaleType.USED_BIDDING_SYSTEM).collect(Collectors.toList());
			}

			if (this.auctionPlayer.getSelectedTransactionSaleType() == AuctionSaleType.WITHOUT_BIDDING_SYSTEM) {
				this.transactions = this.transactions.stream().filter(transaction -> transaction.getAuctionSaleType() == AuctionSaleType.WITHOUT_BIDDING_SYSTEM).collect(Collectors.toList());
			}

			if (this.auctionPlayer.getTransactionSortType() == AuctionSortType.PRICE) {
				this.transactions = this.transactions.stream().sorted(Comparator.comparingDouble(Transaction::getFinalPrice).reversed()).collect(Collectors.toList());
			}

			if (this.auctionPlayer.getTransactionSortType() == AuctionSortType.RECENT) {
				this.transactions = this.transactions.stream().sorted(Comparator.comparingLong(Transaction::getTransactionTime).reversed()).collect(Collectors.toList());
			}

			return this.transactions.stream().skip((page - 1) * 45L).limit(45).collect(Collectors.toList());

		}).asyncLast((data) -> {
			pages = (int) Math.max(1, Math.ceil(this.transactions.size() / (double) 45L));
			setPrevPage(5, 3, getPreviousPageItem());
			setButton(5, 4, getRefreshButtonItem(), e -> e.manager.showGUI(e.player, new GUITransactionList(this.player, this.showAll)));
			setNextPage(5, 5, getNextPageItem());
			setOnPage(e -> draw());

			int slot = 0;

			for (Transaction transaction : data) {
				final ItemStack item = transaction.getItem().clone();
				setButton(slot++, ConfigurationItemHelper.createConfigurationItem(this.player, item, Settings.GUI_TRANSACTIONS_ITEM_TRANSACTION_NAME.getString(), Settings.GUI_TRANSACTIONS_ITEM_TRANSACTION_LORE.getStringList(), new HashMap<String, Object>() {{
					put("%transaction_id%", transaction.getId().toString());
					put("%seller%", Bukkit.getOfflinePlayer(transaction.getSeller()).getName());
					put("%buyer%", Bukkit.getOfflinePlayer(transaction.getBuyer()).getName());
					put("%date%", AuctionAPI.getInstance().convertMillisToDate(transaction.getTransactionTime()));
					put("%item_name%", AuctionAPI.getInstance().getItemName(item));
				}}), e -> e.manager.showGUI(e.player, new GUITransactionView(this.auctionPlayer, transaction, this.showAll)));
			}

		}).execute();

		// Other Buttons
		setButton(5, 0, getBackButtonItem(), e -> {
			if (Settings.RESTRICT_ALL_TRANSACTIONS_TO_PERM.getBoolean() && !e.player.hasPermission("auctionhouse.transactions.viewall")) {
				e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
			} else {
				e.manager.showGUI(e.player, new GUITransactionType(e.player));
			}
		});

		setButton(Settings.GUI_TRANSACTIONS_ITEMS_FILTER_SLOT.getInt(), ConfigurationItemHelper.createConfigurationItem(
				this.player,
				Settings.GUI_TRANSACTIONS_ITEMS_FILTER_ITEM.getString(),
				Settings.GUI_TRANSACTIONS_ITEMS_FILTER_NAME.getString(),
				Settings.GUI_TRANSACTIONS_ITEMS_FILTER_LORE.getStringList(),
				new HashMap<String, Object>() {{
					put("%filter_category%", auctionPlayer.getSelectedTransactionFilter().getTranslatedType());
					put("%filter_auction_type%", auctionPlayer.getSelectedTransactionSaleType().getTranslatedType());
					put("%filter_sort_order%", auctionPlayer.getTransactionSortType().getTranslatedType());
				}}
		), click -> {

			if (click.clickType == ClickType.valueOf(Settings.CLICKS_FILTER_CATEGORY.getString().toUpperCase())) {
				this.auctionPlayer.setSelectedTransactionFilter(this.auctionPlayer.getSelectedTransactionFilter().next());
				draw();
			}


			if (click.clickType == ClickType.valueOf(Settings.CLICKS_FILTER_RESET.getString().toUpperCase())) {
				this.auctionPlayer.resetTransactionFilter();
				click.manager.showGUI(click.player, new GUITransactionList(click.player, this.showAll));
				return;
			}


			if (click.clickType == ClickType.valueOf(Settings.CLICKS_FILTER_SORT_SALE_TYPE.getString().toUpperCase())) {
				if (Settings.ALLOW_USAGE_OF_BID_SYSTEM.getBoolean()) {
					this.auctionPlayer.setSelectedTransactionSaleType(this.auctionPlayer.getSelectedTransactionSaleType().next());
					draw();
				}
				return;
			}

			if (click.clickType == ClickType.valueOf(Settings.CLICKS_FILTER_SORT_PRICE_OR_RECENT.getString().toUpperCase())) {
				this.auctionPlayer.setTransactionSortType(this.auctionPlayer.getTransactionSortType().next());
				draw();
			}
		});
	}
}
