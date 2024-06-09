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

import ca.tweetzy.auctionhouse.auction.enums.AuctionItemCategory;
import ca.tweetzy.auctionhouse.guis.AuctionBaseGUI;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.flight.utils.QuickItem;
import org.bukkit.entity.Player;

/**
 * The current file has been created by Kiran Hart
 * Date Created: June 22 2021
 * Time Created: 3:14 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUIFilterWhitelist extends AuctionBaseGUI {

	public GUIFilterWhitelist(Player player) {
		super(null, player, Settings.GUI_FILTER_WHITELIST_TITLE.getString(), 6);
		setDefaultItem(QuickItem.bg(QuickItem.of(Settings.GUI_FILTER_WHITELIST_BG_ITEM.getString()).make()));
		draw();
	}

	@Override
	protected void draw() {
		applyBackExit();

		setButton(1, 1, QuickItem.of(Settings.GUI_FILTER_WHITELIST_ITEMS_BLOCKS_ITEM.getString()).name(Settings.GUI_FILTER_WHITELIST_ITEMS_BLOCKS_NAME.getString()).lore(Settings.GUI_FILTER_WHITELIST_ITEMS_BLOCKS_LORE.getStringList()).make(), e -> {
			e.manager.showGUI(e.player, new GUIFilterWhitelistList(e.player, AuctionItemCategory.BLOCKS));
		});

		setButton(1, 3, QuickItem.of(Settings.GUI_FILTER_WHITELIST_ITEMS_FOOD_ITEM.getString()).name(Settings.GUI_FILTER_WHITELIST_ITEMS_FOOD_NAME.getString()).lore(Settings.GUI_FILTER_WHITELIST_ITEMS_FOOD_LORE.getStringList()).make(), e -> {
			e.manager.showGUI(e.player, new GUIFilterWhitelistList(e.player, AuctionItemCategory.FOOD));
		});

		setButton(1, 5, QuickItem.of(Settings.GUI_FILTER_WHITELIST_ITEMS_ARMOR_ITEM.getString()).name(Settings.GUI_FILTER_WHITELIST_ITEMS_ARMOR_NAME.getString()).lore(Settings.GUI_FILTER_WHITELIST_ITEMS_ARMOR_LORE.getStringList()).make(), e -> {
			e.manager.showGUI(e.player, new GUIFilterWhitelistList(e.player, AuctionItemCategory.ARMOR));
		});

		setButton(1, 7, QuickItem.of(Settings.GUI_FILTER_WHITELIST_ITEMS_TOOLS_ITEM.getString()).name(Settings.GUI_FILTER_WHITELIST_ITEMS_TOOLS_NAME.getString()).lore(Settings.GUI_FILTER_WHITELIST_ITEMS_TOOLS_LORE.getStringList()).make(), e -> {
			e.manager.showGUI(e.player, new GUIFilterWhitelistList(e.player, AuctionItemCategory.TOOLS));
		});

		// 2ND ROW STARTS

		setButton(2, 1, QuickItem.of(Settings.GUI_FILTER_WHITELIST_ITEMS_SPAWNERS_ITEM.getString()).name(Settings.GUI_FILTER_WHITELIST_ITEMS_SPAWNERS_NAME.getString()).lore(Settings.GUI_FILTER_WHITELIST_ITEMS_SPAWNERS_LORE.getStringList()).make(), e -> {
			e.manager.showGUI(e.player, new GUIFilterWhitelistList(e.player, AuctionItemCategory.SPAWNERS));
		});

		setButton(2, 3, QuickItem.of(Settings.GUI_FILTER_WHITELIST_ITEMS_ENCHANTS_ITEM.getString()).name(Settings.GUI_FILTER_WHITELIST_ITEMS_ENCHANTS_NAME.getString()).lore(Settings.GUI_FILTER_WHITELIST_ITEMS_ENCHANTS_LORE.getStringList()).make(), e -> {
			e.manager.showGUI(e.player, new GUIFilterWhitelistList(e.player, AuctionItemCategory.ENCHANTS));
		});

		setButton(2, 5, QuickItem.of(Settings.GUI_FILTER_WHITELIST_ITEMS_WEAPONS_ITEM.getString()).name(Settings.GUI_FILTER_WHITELIST_ITEMS_WEAPONS_NAME.getString()).lore(Settings.GUI_FILTER_WHITELIST_ITEMS_WEAPONS_LORE.getStringList()).make(), e -> {
			e.manager.showGUI(e.player, new GUIFilterWhitelistList(e.player, AuctionItemCategory.WEAPONS));
		});

		setButton(2, 7, QuickItem.of(Settings.GUI_FILTER_WHITELIST_ITEMS_MISC_ITEM.getString()).name(Settings.GUI_FILTER_WHITELIST_ITEMS_MISC_NAME.getString()).lore(Settings.GUI_FILTER_WHITELIST_ITEMS_MISC_LORE.getStringList()).make(), e -> {
			e.manager.showGUI(e.player, new GUIFilterWhitelistList(e.player, AuctionItemCategory.MISC));
		});

		setButton(3, 1, QuickItem.of(Settings.GUI_FILTER_WHITELIST_ITEMS_POTIONS_ITEM.getString()).name(Settings.GUI_FILTER_WHITELIST_ITEMS_POTIONS_NAME.getString()).lore(Settings.GUI_FILTER_WHITELIST_ITEMS_POTIONS_LORE.getStringList()).make(), e -> {
			e.manager.showGUI(e.player, new GUIFilterWhitelistList(e.player, AuctionItemCategory.POTIONS));
		});

	}
}
