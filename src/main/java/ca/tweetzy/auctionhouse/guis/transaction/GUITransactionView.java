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
import ca.tweetzy.auctionhouse.auction.enums.AuctionSaleType;
import ca.tweetzy.auctionhouse.guis.abstraction.AuctionBaseGUI;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.auctionhouse.transaction.Transaction;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.flight.utils.Replacer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

/**
 * The current file has been created by Kiran Hart
 * Date Created: March 22 2021
 * Time Created: 7:04 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUITransactionView extends AuctionBaseGUI {

	private final Transaction transaction;

	public GUITransactionView(AuctionPlayer auctionPlayer, Transaction transaction, boolean showAll) {
		super(new GUITransactionList(auctionPlayer.getPlayer(), showAll), auctionPlayer.getPlayer(), Settings.GUI_TRANSACTION_VIEW_TITLE.getString(), 6);
		setDefaultItem(QuickItem.bg(QuickItem.of(Settings.GUI_TRANSACTION_VIEW_BACKGROUND_ITEM.getString()).make()));
		setUseLockedCells(Settings.GUI_TRANSACTION_VIEW_BACKGROUND_FILL.getBoolean());
		this.transaction = transaction;

		draw();
	}

	@Override
	protected void draw() {
		applyBackExit();

		setItem(1, 4, transaction.getItem());

		final String SERVER_LISTING_NAME = AuctionHouse.getInstance().getLocale().getMessage("general.server listing").getMessage();
		final OfflinePlayer seller = Bukkit.getOfflinePlayer(transaction.getSeller());
		final OfflinePlayer buyer = Bukkit.getOfflinePlayer(transaction.getBuyer());

		setItem(3, 2, QuickItem
				.of(AuctionAPI.getInstance().getPlayerHead(seller.getName()))
				.name(Replacer.replaceVariables(Settings.GUI_TRANSACTION_VIEW_ITEM_SELLER_NAME.getString(), "seller", seller.hasPlayedBefore() ? seller.getName() : SERVER_LISTING_NAME))
				.lore(Replacer.replaceVariables(Settings.GUI_TRANSACTION_VIEW_ITEM_SELLER_LORE.getStringList(),
						"transaction_id", transaction.getId().toString(),
						"seller", seller.hasPlayedBefore() ? seller.getName() : SERVER_LISTING_NAME,
						"buyer", buyer.getName(),
						"date", AuctionAPI.getInstance().convertMillisToDate(transaction.getTransactionTime())))
				.make());

		setItem(3, 6, QuickItem
				.of(AuctionAPI.getInstance().getPlayerHead(Bukkit.getOfflinePlayer(transaction.getBuyer()).getName()))
				.name(Replacer.replaceVariables(Settings.GUI_TRANSACTION_VIEW_ITEM_BUYER_NAME.getString(), "buyer", buyer.getName()))
				.lore(Replacer.replaceVariables(Settings.GUI_TRANSACTION_VIEW_ITEM_BUYER_LORE.getStringList(),
						"transaction_id", transaction.getId().toString(),
						"seller", seller.hasPlayedBefore() ? seller.getName() : SERVER_LISTING_NAME,
						"buyer", buyer.getName(),
						"date", AuctionAPI.getInstance().convertMillisToDate(transaction.getTransactionTime())))
				.make());


		setItem(3, 4, QuickItem
				.of(Settings.GUI_TRANSACTION_VIEW_ITEM_INFO_ITEM.getString())
				.name(Replacer.replaceVariables(Settings.GUI_TRANSACTION_VIEW_ITEM_INFO_NAME.getString(), "transaction_id", transaction.getId().toString()))
				.lore(Replacer.replaceVariables(Settings.GUI_TRANSACTION_VIEW_ITEM_INFO_LORE.getStringList(),
						"transaction_id", transaction.getId().toString(),
						"sale_type", transaction.getAuctionSaleType() == AuctionSaleType.USED_BIDDING_SYSTEM ? AuctionHouse.getInstance().getLocale().getMessage("transaction.sale_type.bid_won").getMessage() : AuctionHouse.getInstance().getLocale().getMessage("transaction.sale_type.immediate_buy").getMessage(),
						"transaction_date", AuctionAPI.getInstance().convertMillisToDate(transaction.getTransactionTime()),
						"final_price", AuctionAPI.getInstance().formatNumber(transaction.getFinalPrice()),
						"item_name", AuctionAPI.getInstance().getItemName(transaction.getItem())
				))
				.make());
	}
}
