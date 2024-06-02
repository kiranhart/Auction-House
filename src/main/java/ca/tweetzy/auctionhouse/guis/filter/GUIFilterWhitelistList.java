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
import ca.tweetzy.auctionhouse.guis.abstraction.AuctionPagedGUI;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.gui.events.GuiClickEvent;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.flight.gui.helper.InventoryBorder;
import ca.tweetzy.flight.utils.QuickItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: June 22 2021
 * Time Created: 4:06 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUIFilterWhitelistList extends AuctionPagedGUI<AuctionFilterItem> {

	final AuctionItemCategory filerCategory;

	public GUIFilterWhitelistList(Player player, AuctionItemCategory filerCategory) {
		super(new GUIFilterWhitelist(player), player, TextUtils.formatText(Settings.GUI_FILTER_WHITELIST_LIST_TITLE.getString().replace("%filter_category%", filerCategory.getTranslatedType())), 6, new ArrayList<>(AuctionHouse.getInstance().getFilterManager().getFilterWhitelist()));
		this.filerCategory = filerCategory;
		setDefaultItem(QuickItem.bg(QuickItem.of(Settings.GUI_FILTER_WHITELIST_LIST_BG_ITEM.getString()).make()));
		setUseLockedCells(true);
		draw();
	}

	@Override
	protected void prePopulate() {
		this.items = this.items.stream().filter(item -> item.getCategory() == this.filerCategory).collect(Collectors.toList());
	}

	@Override
	protected void drawFixed() {
		applyBackExit();
	}

	@Override
	protected ItemStack makeDisplayItem(AuctionFilterItem filterItem) {
		return filterItem.getItemStack();
	}

	@Override
	protected void onClick(AuctionFilterItem filterItem, GuiClickEvent click) {
		AuctionHouse.getInstance().getFilterManager().removeFilterItem(filterItem);
		click.manager.showGUI(click.player, new GUIFilterWhitelistList(click.player, this.filerCategory));
	}

	@Override
	protected List<Integer> fillSlots() {
		return InventoryBorder.getInsideBorders(6);
	}
}
