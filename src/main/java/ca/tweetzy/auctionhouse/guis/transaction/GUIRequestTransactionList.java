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
import ca.tweetzy.auctionhouse.api.auction.RequestTransaction;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.auction.enums.AuctionSortType;
import ca.tweetzy.auctionhouse.guis.AuctionPagedGUI;
import ca.tweetzy.auctionhouse.guis.core.GUIAuctionHouse;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.flight.gui.events.GuiClickEvent;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.flight.utils.Replacer;
import ca.tweetzy.flight.utils.TimeUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: March 22 2021
 * Time Created: 7:03 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUIRequestTransactionList extends AuctionPagedGUI<RequestTransaction> {

	final AuctionPlayer auctionPlayer;
	final boolean showAll;

	public GUIRequestTransactionList(Player player) {
		this(player, true);
	}

	public GUIRequestTransactionList(Player player, boolean showAll) {
		super(null, player, showAll ? Settings.GUI_REQUEST_TRANSACTIONS_TITLE_ALL.getString() : Settings.GUI_REQUEST_TRANSACTIONS_TITLE.getString(), 6, new ArrayList<>());
		this.auctionPlayer = AuctionHouse.getAuctionPlayerManager().getPlayer(player.getUniqueId());
		this.showAll = showAll;
		setAcceptsItems(false);
		draw();
	}

	@Override
	protected void prePopulate() {
		this.items = this.showAll ? new ArrayList<>(AuctionHouse.getRequestsManager().getManagerContent().values()) : AuctionHouse.getRequestsManager().getManagerContent().values().stream().filter(transaction -> transaction.getFulfillerUUID().equals(player.getUniqueId()) || transaction.getRequesterUUID().equals(player.getUniqueId())).collect(Collectors.toList());

		// perform filter
		if (this.auctionPlayer.getTransactionSortType() == AuctionSortType.PRICE) {
			this.items = this.items.stream().sorted(Comparator.comparingDouble(RequestTransaction::getPaymentTotal).reversed()).collect(Collectors.toList());
		}

		if (this.auctionPlayer.getTransactionSortType() == AuctionSortType.RECENT) {
			this.items = this.items.stream().sorted(Comparator.comparingLong(RequestTransaction::getTimeCreated).reversed()).collect(Collectors.toList());
		}
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

		setButton(Settings.GUI_REQUEST_TRANSACTIONS_ITEMS_FILTER_SLOT.getInt(), QuickItem
				.of(Settings.GUI_REQUEST_TRANSACTIONS_ITEMS_FILTER_ITEM.getString())
				.name(Settings.GUI_REQUEST_TRANSACTIONS_ITEMS_FILTER_NAME.getString())
				.lore(this.player, Replacer.replaceVariables(Settings.GUI_REQUEST_TRANSACTIONS_ITEMS_FILTER_LORE.getStringList(),
						"filter_sort_order", auctionPlayer.getTransactionSortType().getTranslatedType(),
						"filter_buy_type", auctionPlayer.getTransactionViewFilter().getTranslatedType()
				)).make(), click -> {

			if (click.clickType == ClickType.valueOf(Settings.CLICKS_FILTER_RESET.getString().toUpperCase())) {
				this.auctionPlayer.resetTransactionFilter();
				click.manager.showGUI(click.player, new GUIRequestTransactionList(click.player, this.showAll));
				return;
			}

			if (click.clickType == ClickType.valueOf(Settings.CLICKS_FILTER_SORT_PRICE_OR_RECENT.getString().toUpperCase())) {
				this.auctionPlayer.setTransactionSortType(this.auctionPlayer.getTransactionSortType().next());
				click.manager.showGUI(click.player, new GUIRequestTransactionList(click.player, this.showAll));
			}
		});

		if (this.player.hasPermission("auctionhouse.transactions.viewall")) {
			setButton(Settings.GUI_REQUEST_TRANSACTIONS_ITEMS_ALL_SLOT.getInt(), QuickItem
					.of(this.showAll ? Settings.GUI_REQUEST_TRANSACTIONS_ITEMS_ALL_ITEM_ON.getString() : Settings.GUI_REQUEST_TRANSACTIONS_ITEMS_ALL_ITEM.getString())
					.name(Settings.GUI_REQUEST_TRANSACTIONS_ITEMS_ALL_NAME.getString())
					.lore(Settings.GUI_REQUEST_TRANSACTIONS_ITEMS_ALL_LORE.getStringList()).make(), click -> {

				click.manager.showGUI(click.player, new GUIRequestTransactionList(this.player, !this.showAll));
			});
		}
	}

	@Override
	protected ItemStack makeDisplayItem(RequestTransaction transaction) {
		final ItemStack item = transaction.getRequestedItem().clone();

		return QuickItem
				.of(item)
				.lore(this.player, Replacer.replaceVariables(Settings.GUI_REQUEST_TRANSACTIONS_ITEM_TRANSACTION_LORE.getStringList(),
						"transaction_id", transaction.getId().toString(),
						"transaction_price", AuctionHouse.getAPI().getNumberAsCurrency(transaction.getPaymentTotal()),
						"transaction_amount", transaction.getAmountRequested(),
						"transaction_requester", transaction.getRequesterName(),
						"transaction_completer", transaction.getFulfillerName(),
						"transaction_date", TimeUtil.convertToReadableDate(transaction.getTimeCreated(), Settings.DATE_FORMAT.getString())
				)).make();
	}

	@Override
	protected void onClick(RequestTransaction transaction, GuiClickEvent click) {
	}
}
