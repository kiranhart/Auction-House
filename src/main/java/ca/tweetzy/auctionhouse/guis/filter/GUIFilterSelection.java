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
import ca.tweetzy.auctionhouse.guis.AuctionBaseGUI;
import ca.tweetzy.auctionhouse.guis.core.GUIAuctionHouse;
import ca.tweetzy.auctionhouse.helpers.SlotHelper;
import ca.tweetzy.auctionhouse.lang.AuctionLocale;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.flight.utils.Replacer;
import ca.tweetzy.flight.utils.input.TitleInput;
import org.bukkit.entity.Player;

/**
 * The current file has been created by Kiran Hart
 * Date Created: June 18 2021
 * Time Created: 2:10 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */

public class GUIFilterSelection extends AuctionBaseGUI {

	final AuctionPlayer auctionPlayer;

	public GUIFilterSelection(AuctionPlayer auctionPlayer) {
		super(new GUIAuctionHouse(auctionPlayer), auctionPlayer.getPlayer(), AuctionLocale.msg(auctionPlayer.getPlayer(), "gui.filter.title"), 5);
		this.auctionPlayer = auctionPlayer;

		setDefaultItem(QuickItem.bg(QuickItem.of(Settings.GUI_FILTER_BG_ITEM.getString()).make()));
		setOnClose(closed -> closed.manager.showGUI(closed.player, new GUIAuctionHouse(this.auctionPlayer)));
		draw();

	}

	@Override
	protected void draw() {

		if (AuctionItemCategory.ALL.isEnabled())
			SlotHelper.getButtonSlots(Settings.GUI_FILTER_ITEMS_ALL_SLOTS.getString()).forEach(slot -> setButton(slot, QuickItem.of(Settings.GUI_FILTER_ITEMS_ALL_ITEM.getString()).name(AuctionLocale.msg(this.player, "gui.filter.items.all.name")).lore(this.player, AuctionLocale.msgList(this.player, "gui.filter.items.all.lore")).make(), e -> {
				this.auctionPlayer.setSelectedFilter(AuctionItemCategory.ALL);
				updatePlayerFilter(this.auctionPlayer);
				e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
			}));


		if (AuctionItemCategory.SELF.isEnabled())
			SlotHelper.getButtonSlots(Settings.GUI_FILTER_ITEMS_OWN_SLOTS.getString()).forEach(slot -> setButton(slot, QuickItem.of(this.player).name(AuctionLocale.msg(this.player, "gui.filter.items.own.name")).lore(this.player, AuctionLocale.msgList(this.player, "gui.filter.items.own.lore")).make(), e -> {
				this.auctionPlayer.setSelectedFilter(AuctionItemCategory.SELF);
				updatePlayerFilter(this.auctionPlayer);
				e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
			}));

		if (AuctionItemCategory.SEARCH.isEnabled())
			SlotHelper.getButtonSlots(Settings.GUI_FILTER_ITEMS_SEARCH_SLOTS.getString()).forEach(slot -> setButton(slot, QuickItem.of(Settings.GUI_FILTER_ITEMS_SEARCH_ITEM.getString()).name(AuctionLocale.msg(this.player, "gui.filter.items.search.name")).lore(this.player, Replacer.replaceVariables(AuctionLocale.msgList(this.player, "gui.filter.items.search.lore"), "filter_search_phrase", this.auctionPlayer.getCurrentSearchPhrase())).make(), e -> {
				if (!e.player.hasPermission("auctionhouse.cmd.search")) {
					AuctionLocale.tell(e.player, "commands.no_permission");
					return;
				}

				e.gui.exit();
				final AuctionPlayer ap = this.auctionPlayer;
				new TitleInput(
						AuctionHouse.getInstance(),
						ap.getPlayer(),
						Common.colorize(AuctionLocale.msg(e.player, "titles.material search.title")),
						Common.colorize(AuctionLocale.msg(e.player, "titles.material search.subtitle"))
				) {
					@Override
					public boolean onResult(String string) {
						if (string == null || string.isBlank()) {
							return false;
						}
						ap.setCurrentSearchPhrase(string.trim());
						ap.setSelectedFilter(AuctionItemCategory.SEARCH);
						e.manager.showGUI(e.player, new GUIAuctionHouse(ap));
						return true;
					}

					@Override
					public void onExit(Player player) {
						e.manager.showGUI(player, new GUIFilterSelection(ap));
					}
				};
			}));

		if (AuctionItemCategory.MISC.isEnabled())
			SlotHelper.getButtonSlots(Settings.GUI_FILTER_ITEMS_MISC_SLOTS.getString()).forEach(slot -> setButton(slot, QuickItem.of(Settings.GUI_FILTER_ITEMS_MISC_ITEM.getString()).name(AuctionLocale.msg(this.player, "gui.filter.items.misc.name")).lore(this.player, AuctionLocale.msgList(this.player, "gui.filter.items.misc.lore")).make(), e -> {
				this.auctionPlayer.setSelectedFilter(AuctionItemCategory.MISC);
				updatePlayerFilter(this.auctionPlayer);
				e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
			}));


		if (AuctionItemCategory.ENCHANTS.isEnabled())
			SlotHelper.getButtonSlots(Settings.GUI_FILTER_ITEMS_ENCHANTS_SLOTS.getString()).forEach(slot -> setButton(slot, QuickItem.of(Settings.GUI_FILTER_ITEMS_ENCHANTS_ITEM.getString()).name(AuctionLocale.msg(this.player, "gui.filter.items.enchants.name")).lore(this.player, AuctionLocale.msgList(this.player, "gui.filter.items.enchants.lore")).make(), e -> {
				this.auctionPlayer.setSelectedFilter(AuctionItemCategory.ENCHANTS);
				updatePlayerFilter(this.auctionPlayer);
				e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
			}));


		if (AuctionItemCategory.ARMOR.isEnabled())
			SlotHelper.getButtonSlots(Settings.GUI_FILTER_ITEMS_ARMOR_SLOTS.getString()).forEach(slot -> setButton(slot, QuickItem.of(Settings.GUI_FILTER_ITEMS_ARMOR_ITEM.getString()).name(AuctionLocale.msg(this.player, "gui.filter.items.armor.name")).lore(this.player, AuctionLocale.msgList(this.player, "gui.filter.items.armor.lore")).make(), e -> {
				this.auctionPlayer.setSelectedFilter(AuctionItemCategory.ARMOR);
				updatePlayerFilter(this.auctionPlayer);
				e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
			}));


		if (AuctionItemCategory.WEAPONS.isEnabled())
			SlotHelper.getButtonSlots(Settings.GUI_FILTER_ITEMS_WEAPONS_SLOTS.getString()).forEach(slot -> setButton(slot, QuickItem.of(Settings.GUI_FILTER_ITEMS_WEAPONS_ITEM.getString()).name(AuctionLocale.msg(this.player, "gui.filter.items.weapons.name")).lore(this.player, AuctionLocale.msgList(this.player, "gui.filter.items.weapons.lore")).make(), e -> {
				this.auctionPlayer.setSelectedFilter(AuctionItemCategory.WEAPONS);
				updatePlayerFilter(this.auctionPlayer);
				e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
			}));


		if (AuctionItemCategory.TOOLS.isEnabled())
			SlotHelper.getButtonSlots(Settings.GUI_FILTER_ITEMS_TOOLS_SLOTS.getString()).forEach(slot -> setButton(slot, QuickItem.of(Settings.GUI_FILTER_ITEMS_TOOLS_ITEM.getString()).name(AuctionLocale.msg(this.player, "gui.filter.items.tools.name")).lore(this.player, AuctionLocale.msgList(this.player, "gui.filter.items.tools.lore")).make(), e -> {
				this.auctionPlayer.setSelectedFilter(AuctionItemCategory.TOOLS);
				updatePlayerFilter(this.auctionPlayer);
				e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
			}));


		if (AuctionItemCategory.SPAWNERS.isEnabled())
			SlotHelper.getButtonSlots(Settings.GUI_FILTER_ITEMS_SPAWNERS_SLOTS.getString()).forEach(slot -> setButton(slot, QuickItem.of(Settings.GUI_FILTER_ITEMS_SPAWNERS_ITEM.getString()).name(AuctionLocale.msg(this.player, "gui.filter.items.spawners.name")).lore(this.player, AuctionLocale.msgList(this.player, "gui.filter.items.spawners.lore")).make(), e -> {
				this.auctionPlayer.setSelectedFilter(AuctionItemCategory.SPAWNERS);
				updatePlayerFilter(this.auctionPlayer);
				e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
			}));


		if (AuctionItemCategory.BLOCKS.isEnabled())
			SlotHelper.getButtonSlots(Settings.GUI_FILTER_ITEMS_BLOCKS_SLOTS.getString()).forEach(slot -> setButton(slot, QuickItem.of(Settings.GUI_FILTER_ITEMS_BLOCKS_ITEM.getString()).name(AuctionLocale.msg(this.player, "gui.filter.items.blocks.name")).lore(this.player, AuctionLocale.msgList(this.player, "gui.filter.items.blocks.lore")).make(), e -> {
				this.auctionPlayer.setSelectedFilter(AuctionItemCategory.BLOCKS);
				updatePlayerFilter(this.auctionPlayer);
				e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
			}));

		if (AuctionItemCategory.POTIONS.isEnabled())
			SlotHelper.getButtonSlots(Settings.GUI_FILTER_ITEMS_POTIONS_SLOTS.getString()).forEach(slot -> setButton(slot, QuickItem.of(Settings.GUI_FILTER_ITEMS_POTIONS_ITEM.getString()).name(AuctionLocale.msg(this.player, "gui.filter.items.potions.name")).lore(this.player, AuctionLocale.msgList(this.player, "gui.filter.items.potions.lore")).make(), e -> {
				this.auctionPlayer.setSelectedFilter(AuctionItemCategory.POTIONS);
				updatePlayerFilter(this.auctionPlayer);
				e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
			}));

	}

	private void updatePlayerFilter(AuctionPlayer player) {
		AuctionHouse.getInstance().getDataManager().updateAuctionPlayer(player, (error, success) -> {
			if (error == null && success)
				if (!Settings.DISABLE_PROFILE_UPDATE_MSG.getBoolean())
					AuctionHouse.getInstance().getLogger().info("Updating profile for player: " + player.getPlayer().getName());

		});
	}
}


