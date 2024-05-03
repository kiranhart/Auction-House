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
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.auction.enums.AuctionItemCategory;
import ca.tweetzy.auctionhouse.guis.GUIAuctionHouse;
import ca.tweetzy.auctionhouse.guis.abstraction.AuctionBaseGUI;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.input.ChatPrompt;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.flight.utils.Replacer;

/**
 * The current file has been created by Kiran Hart
 * Date Created: June 18 2021
 * Time Created: 2:10 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */

public class GUIFilterSelection extends AuctionBaseGUI {

	final AuctionPlayer auctionPlayer;

	public GUIFilterSelection(AuctionPlayer auctionPlayer) {
		super(new GUIAuctionHouse(auctionPlayer), auctionPlayer.getPlayer(), Settings.GUI_FILTER_TITLE.getString(), 5);
		this.auctionPlayer = auctionPlayer;

		setDefaultItem(QuickItem.bg(QuickItem.of(Settings.GUI_FILTER_BG_ITEM.getString()).make()));
		setOnClose(closed -> closed.manager.showGUI(closed.player, new GUIAuctionHouse(this.auctionPlayer)));
		draw();

	}

	@Override
	protected void draw() {

		if (AuctionItemCategory.ALL.isEnabled())
			setButton(1, 3, QuickItem.of(Settings.GUI_FILTER_ITEMS_ALL_ITEM.getString()).name(Settings.GUI_FILTER_ITEMS_ALL_NAME.getString()).lore(Settings.GUI_FILTER_ITEMS_ALL_LORE.getStringList()).make(), e -> {
				this.auctionPlayer.setSelectedFilter(AuctionItemCategory.ALL);
				updatePlayerFilter(this.auctionPlayer);
				e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
			});

		if (AuctionItemCategory.SELF.isEnabled())
			setButton(1, 4, QuickItem.of(this.player).name(Settings.GUI_FILTER_ITEMS_OWN_NAME.getString()).lore(Settings.GUI_FILTER_ITEMS_OWN_LORE.getStringList()).make(), e -> {
				this.auctionPlayer.setSelectedFilter(AuctionItemCategory.SELF);
				updatePlayerFilter(this.auctionPlayer);
				e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
			});

		if (AuctionItemCategory.SEARCH.isEnabled())
			setButton(1, 5, QuickItem.of(Settings.GUI_FILTER_ITEMS_SEARCH_ITEM.getString()).name(Settings.GUI_FILTER_ITEMS_SEARCH_NAME.getString()).lore(Replacer.replaceVariables(Settings.GUI_FILTER_ITEMS_SEARCH_LORE.getStringList(), "filter_search_phrase", this.auctionPlayer.getCurrentSearchPhrase())).make(), e -> {
				if (!e.player.hasPermission("auctionhouse.cmd.search")) {
					AuctionHouse.getInstance().getLocale().getMessage("commands.no_permission").sendPrefixedMessage(e.player);
					return;
				}

				e.gui.exit();
				ChatPrompt.showPrompt(AuctionHouse.getInstance(), this.auctionPlayer.getPlayer(), TextUtils.formatText(AuctionHouse.getInstance().getLocale().getMessage("general.entersearchphrase").getMessage()), chat -> {
					if (chat.getMessage() != null && chat.getMessage().length() != 0) {
						// the keyword is valid
						this.auctionPlayer.setCurrentSearchPhrase(chat.getMessage().trim());
						this.auctionPlayer.setSelectedFilter(AuctionItemCategory.SEARCH);
						e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
					}
				});
			});

		if (AuctionItemCategory.MISC.isEnabled())
			setButton(2, 1, QuickItem.of(Settings.GUI_FILTER_ITEMS_MISC_ITEM.getString()).name(Settings.GUI_FILTER_ITEMS_MISC_NAME.getString()).lore(Settings.GUI_FILTER_ITEMS_MISC_LORE.getStringList()).make(), e -> {
				this.auctionPlayer.setSelectedFilter(AuctionItemCategory.MISC);
				updatePlayerFilter(this.auctionPlayer);
				e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
			});

		if (AuctionItemCategory.ENCHANTS.isEnabled())
			setButton(2, 2, QuickItem.of(Settings.GUI_FILTER_ITEMS_ENCHANTS_ITEM.getString()).name(Settings.GUI_FILTER_ITEMS_ENCHANTS_NAME.getString()).lore(Settings.GUI_FILTER_ITEMS_ENCHANTS_LORE.getStringList()).make(), e -> {
				this.auctionPlayer.setSelectedFilter(AuctionItemCategory.ENCHANTS);
				updatePlayerFilter(this.auctionPlayer);
				e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
			});

		if (AuctionItemCategory.ARMOR.isEnabled())
			setButton(2, 3, QuickItem.of(Settings.GUI_FILTER_ITEMS_ARMOR_ITEM.getString()).name(Settings.GUI_FILTER_ITEMS_ARMOR_NAME.getString()).lore(Settings.GUI_FILTER_ITEMS_ARMOR_LORE.getStringList()).make(), e -> {
				this.auctionPlayer.setSelectedFilter(AuctionItemCategory.ARMOR);
				updatePlayerFilter(this.auctionPlayer);
				e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
			});

		if (AuctionItemCategory.WEAPONS.isEnabled())
			setButton(2, 4, QuickItem.of(Settings.GUI_FILTER_ITEMS_WEAPONS_ITEM.getString()).name(Settings.GUI_FILTER_ITEMS_WEAPONS_NAME.getString()).lore(Settings.GUI_FILTER_ITEMS_WEAPONS_LORE.getStringList()).make(), e -> {
				this.auctionPlayer.setSelectedFilter(AuctionItemCategory.WEAPONS);
				updatePlayerFilter(this.auctionPlayer);
				e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
			});

		if (AuctionItemCategory.TOOLS.isEnabled())
			setButton(2, 5, QuickItem.of(Settings.GUI_FILTER_ITEMS_TOOLS_ITEM.getString()).name(Settings.GUI_FILTER_ITEMS_TOOLS_NAME.getString()).lore(Settings.GUI_FILTER_ITEMS_TOOLS_LORE.getStringList()).make(), e -> {
				this.auctionPlayer.setSelectedFilter(AuctionItemCategory.TOOLS);
				updatePlayerFilter(this.auctionPlayer);
				e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
			});

		if (AuctionItemCategory.SPAWNERS.isEnabled())
			setButton(2, 6, QuickItem.of(Settings.GUI_FILTER_ITEMS_SPAWNERS_ITEM.getString()).name(Settings.GUI_FILTER_ITEMS_SPAWNERS_NAME.getString()).lore(Settings.GUI_FILTER_ITEMS_SPAWNERS_LORE.getStringList()).make(), e -> {
				this.auctionPlayer.setSelectedFilter(AuctionItemCategory.SPAWNERS);
				updatePlayerFilter(this.auctionPlayer);
				e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
			});

		if (AuctionItemCategory.BLOCKS.isEnabled())
			setButton(2, 7, QuickItem.of(Settings.GUI_FILTER_ITEMS_BLOCKS_ITEM.getString()).name(Settings.GUI_FILTER_ITEMS_BLOCKS_NAME.getString()).lore(Settings.GUI_FILTER_ITEMS_BLOCKS_LORE.getStringList()).make(), e -> {
				this.auctionPlayer.setSelectedFilter(AuctionItemCategory.BLOCKS);
				updatePlayerFilter(this.auctionPlayer);
				e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
			});

		if (AuctionItemCategory.POTIONS.isEnabled())
			setButton(3, 4, QuickItem.of(Settings.GUI_FILTER_ITEMS_POTIONS_ITEM.getString()).name(Settings.GUI_FILTER_ITEMS_POTIONS_NAME.getString()).lore(Settings.GUI_FILTER_ITEMS_POTIONS_LORE.getStringList()).make(), e -> {
				this.auctionPlayer.setSelectedFilter(AuctionItemCategory.POTIONS);
				updatePlayerFilter(this.auctionPlayer);
				e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
			});
	}

	private void updatePlayerFilter(AuctionPlayer player) {
		AuctionHouse.getInstance().getDataManager().updateAuctionPlayer(player, (error, success) -> {
			if (error == null && success)
				if (!Settings.DISABLE_PROFILE_UPDATE_MSG.getBoolean())
					AuctionHouse.getInstance().getLogger().info("Updating profile for player: " + player.getPlayer().getName());

		});
	}
}


