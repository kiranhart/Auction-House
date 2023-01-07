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
import ca.tweetzy.auctionhouse.guis.AbstractPlaceholderGui;
import ca.tweetzy.auctionhouse.helpers.ConfigurationItemHelper;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.auctionhouse.transaction.Transaction;
import ca.tweetzy.core.gui.GuiUtils;
import ca.tweetzy.core.utils.TextUtils;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: March 22 2021
 * Time Created: 7:04 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUITransactionView extends AbstractPlaceholderGui {

	public GUITransactionView(AuctionPlayer auctionPlayer, Transaction transaction, boolean showAll) {
		super(auctionPlayer);
		setTitle(TextUtils.formatText(Settings.GUI_TRANSACTION_VIEW_TITLE.getString()));
		setRows(6);
		setAcceptsItems(false);
		setDefaultItem(ConfigurationItemHelper.createConfigurationItem(Settings.GUI_TRANSACTION_VIEW_BACKGROUND_ITEM.getString()));
		setUseLockedCells(Settings.GUI_TRANSACTION_VIEW_BACKGROUND_FILL.getBoolean());

		setButton(5, 0, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_CLOSE_BTN_ITEM.getString(), Settings.GUI_CLOSE_BTN_NAME.getString(), Settings.GUI_CLOSE_BTN_LORE.getStringList(), null), e -> e.manager.showGUI(e.player, new GUITransactionList(auctionPlayer.getPlayer(), showAll)));
		setItem(1, 4, transaction.getItem());

		setItem(3, 2, GuiUtils.createButtonItem(AuctionAPI.getInstance().getPlayerHead(Bukkit.getOfflinePlayer(transaction.getSeller()).getName()), TextUtils.formatText(Settings.GUI_TRANSACTION_VIEW_ITEM_SELLER_NAME.getString().replace("%seller_name%", Bukkit.getOfflinePlayer(transaction.getSeller()).getName())),
				Settings.GUI_TRANSACTION_VIEW_ITEM_SELLER_LORE.getStringList().stream().map(line -> line.replace("%seller_id%", transaction.getSeller().toString())).map(TextUtils::formatText).collect(Collectors.toList())));

		setItem(3, 6, GuiUtils.createButtonItem(AuctionAPI.getInstance().getPlayerHead(Bukkit.getOfflinePlayer(transaction.getBuyer()).getName()), TextUtils.formatText(Settings.GUI_TRANSACTION_VIEW_ITEM_BUYER_NAME.getString().replace("%buyer_name%", Bukkit.getOfflinePlayer(transaction.getBuyer()).getName())),
				Settings.GUI_TRANSACTION_VIEW_ITEM_BUYER_LORE.getStringList().stream().map(line -> line.replace("%buyer_id%", transaction.getBuyer().toString())).map(TextUtils::formatText).collect(Collectors.toList())));

		setItem(3, 4, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_TRANSACTION_VIEW_ITEM_INFO_ITEM.getString(), Settings.GUI_TRANSACTION_VIEW_ITEM_INFO_NAME.getString(), Settings.GUI_TRANSACTION_VIEW_ITEM_INFO_LORE.getStringList(), new HashMap<String, Object>() {{
			put("%transaction_id%", transaction.getId().toString());
			put("%sale_type%", transaction.getAuctionSaleType() == AuctionSaleType.USED_BIDDING_SYSTEM ? AuctionHouse.getInstance().getLocale().getMessage("transaction.sale_type.bid_won").getMessage() : AuctionHouse.getInstance().getLocale().getMessage("transaction.sale_type.immediate_buy").getMessage());
			put("%transaction_date%", AuctionAPI.getInstance().convertMillisToDate(transaction.getTransactionTime()));
			put("%final_price%", AuctionAPI.getInstance().formatNumber(transaction.getFinalPrice()));
			put("%item_name%", AuctionAPI.getInstance().getItemName(transaction.getItem()));
		}}));
	}
}
