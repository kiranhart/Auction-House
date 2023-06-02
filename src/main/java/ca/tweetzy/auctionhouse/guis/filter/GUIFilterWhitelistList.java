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

package ca.tweetzy.auctionhouse.guis.filter;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.auction.AuctionFilterItem;
import ca.tweetzy.auctionhouse.auction.enums.AuctionItemCategory;
import ca.tweetzy.auctionhouse.guis.AbstractPlaceholderGui;
import ca.tweetzy.auctionhouse.helpers.ConfigurationItemHelper;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.utils.TextUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: June 22 2021
 * Time Created: 4:06 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUIFilterWhitelistList extends AbstractPlaceholderGui {

	final AuctionItemCategory filerCategory;
	List<AuctionFilterItem> items;

	public GUIFilterWhitelistList(Player player, AuctionItemCategory filerCategory) {
		super(player);
		this.filerCategory = filerCategory;
		setTitle(TextUtils.formatText(Settings.GUI_FILTER_WHITELIST_LIST_TITLE.getString().replace("%filter_category%", filerCategory.getTranslatedType())));
		setRows(6);
		setAcceptsItems(false);
		setDefaultItem(ConfigurationItemHelper.createConfigurationItem(this.player, Settings.GUI_FILTER_WHITELIST_LIST_BG_ITEM.getString()));
		setUseLockedCells(true);
		draw();

		setOnClose(close -> close.manager.showGUI(close.player, new GUIFilterWhitelist(close.player)));
	}

	private void draw() {
		reset();
		setPrevPage(5, 3, getPreviousPageItem());
		setButton(5, 4, getCloseButtonItem(), e -> e.manager.showGUI(e.player, new GUIFilterWhitelist(e.player)));
		setNextPage(5, 5, getNextPageItem());
		setOnPage(e -> draw());

		AuctionHouse.newChain().asyncFirst(() -> {
			this.items = AuctionHouse.getInstance().getFilterManager().getFilterWhitelist().stream().filter(item -> item.getCategory() == filerCategory).collect(Collectors.toList());
			return this.items.stream().skip((page - 1) * 28L).limit(28L).collect(Collectors.toList());
		}).asyncLast((data) -> {
			pages = (int) Math.max(1, Math.ceil(this.items.size() / (double) 28L));
			int slot = 10;
			for (AuctionFilterItem item : data) {
				setButton(slot, item.getItemStack(), ClickType.RIGHT, e -> {
					AuctionHouse.getInstance().getFilterManager().removeFilterItem(item);
					draw();
				});

				slot = Arrays.asList(16, 25, 34).contains(slot) ? slot + 3 : slot + 1;
			}
		}).execute();

	}
}
