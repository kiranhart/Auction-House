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



import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.auctionhouse.settings.Translations;
import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.auction.enums.AuctionSaleType;
import ca.tweetzy.auctionhouse.guis.AuctionBaseGUI;
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
		super(new GUITransactionList(auctionPlayer.getPlayer(), showAll), auctionPlayer.getPlayer(), TranslationManager.string(auctionPlayer.getPlayer(), Translations.GUI_TRANSACTION_VIEW_TITLE), 6);
		setDefaultItem(QuickItem.bg(QuickItem.of(Settings.GUI_TRANSACTION_VIEW_BACKGROUND_ITEM.getString()).make()));
		setUseLockedCells(Settings.GUI_TRANSACTION_VIEW_BACKGROUND_FILL.getBoolean());
		this.transaction = transaction;

		draw();
	}

	@Override
	protected void draw() {
		applyBackExit();

		setItem(1, 4, transaction.getItem());

		final String SERVER_LISTING_NAME = TranslationManager.string(Translations.GENERAL_SERVER_LISTING);
		final OfflinePlayer seller = Bukkit.getOfflinePlayer(transaction.getSeller());
		final OfflinePlayer buyer = Bukkit.getOfflinePlayer(transaction.getBuyer());

		setItem(3, 2, QuickItem
				.of(AuctionAPI.getInstance().getPlayerHead(seller.getName()))
				.name(Replacer.replaceVariables(TranslationManager.string(this.player, Translations.GUI_TRANSACTION_VIEW_SELLER_NAME), "seller", seller.hasPlayedBefore() ? seller.getName() : SERVER_LISTING_NAME))
				.lore(this.player, Replacer.replaceVariables(TranslationManager.list(this.player, Translations.GUI_TRANSACTION_VIEW_SELLER_LORE),
						"transaction_id", transaction.getId().toString(),
						"seller", seller.hasPlayedBefore() ? seller.getName() : SERVER_LISTING_NAME,
						"buyer", buyer.getName(),
						"date", AuctionAPI.getInstance().convertMillisToDate(transaction.getTransactionTime())))
				.make());

		setItem(3, 6, QuickItem
				.of(AuctionAPI.getInstance().getPlayerHead(Bukkit.getOfflinePlayer(transaction.getBuyer()).getName()))
				.name(Replacer.replaceVariables(TranslationManager.string(this.player, Translations.GUI_TRANSACTION_VIEW_BUYER_NAME), "buyer", buyer.getName()))
				.lore(this.player, Replacer.replaceVariables(TranslationManager.list(this.player, Translations.GUI_TRANSACTION_VIEW_BUYER_LORE),
						"transaction_id", transaction.getId().toString(),
						"seller", seller.hasPlayedBefore() ? seller.getName() : SERVER_LISTING_NAME,
						"buyer", buyer.getName(),
						"date", AuctionAPI.getInstance().convertMillisToDate(transaction.getTransactionTime())))
				.make());


		setItem(3, 4, QuickItem
				.of(Settings.GUI_TRANSACTION_VIEW_ITEM_INFO_ITEM.getString())
				.name(Replacer.replaceVariables(TranslationManager.string(this.player, Translations.GUI_TRANSACTION_VIEW_INFO_NAME), "transaction_id", transaction.getId().toString()))
				.lore(this.player, Replacer.replaceVariables(TranslationManager.list(this.player, Translations.GUI_TRANSACTION_VIEW_INFO_LORE),
						"transaction_id", transaction.getId().toString(),
						"sale_type", transaction.getAuctionSaleType() == AuctionSaleType.USED_BIDDING_SYSTEM ? TranslationManager.string(Translations.TRANSACTION_SALE_TYPE_BID_WON) : TranslationManager.string(Translations.TRANSACTION_SALE_TYPE_IMMEDIATE_BUY),
						"transaction_date", AuctionAPI.getInstance().convertMillisToDate(transaction.getTransactionTime()),
						"final_price", AuctionHouse.getAPI().getNumberAsCurrency(transaction.getFinalPrice()),
						"item_name", AuctionAPI.getInstance().getItemName(transaction.getItem())
				))
				.make());
	}
}
