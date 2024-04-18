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

package ca.tweetzy.auctionhouse.guis;

import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.helpers.ConfigurationItemHelper;
import ca.tweetzy.auctionhouse.hooks.PlaceholderAPIHook;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.compatibility.XSound;
import ca.tweetzy.core.gui.Gui;
import ca.tweetzy.core.utils.TextUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Date Created: April 07 2022
 * Time Created: 9:33 p.m.
 *
 * @author Kiran Hart
 */
public abstract class AbstractPlaceholderGui extends Gui {

	protected final Player player;

	public AbstractPlaceholderGui(Player player) {
		this.player = player;
		setUseLockedCells(true);
		setDefaultItem(ConfigurationItemHelper.createConfigurationItem(this.player, Settings.GUI_FILLER.getString()));
		setNavigateSound(XSound.matchXSound(Settings.SOUNDS_NAVIGATE_GUI_PAGES.getString()).orElse(XSound.ENTITY_BAT_TAKEOFF).parseSound());
	}

	public AbstractPlaceholderGui(AuctionPlayer player) {
		this.player = player.getPlayer();
		setUseLockedCells(true);
		setDefaultItem(ConfigurationItemHelper.createConfigurationItem(this.player, Settings.GUI_FILLER.getString()));
		setNavigateSound(XSound.matchXSound(Settings.SOUNDS_NAVIGATE_GUI_PAGES.getString()).orElse(XSound.ENTITY_BAT_TAKEOFF).parseSound());
	}

	@Override
	public Gui setTitle(String message) {
		super.setTitle(this.player == null ? TextUtils.formatText(message) : TextUtils.formatText(PlaceholderAPIHook.PAPIReplacer.tryReplace(this.player, message)));
		return this;
	}

	protected ItemStack getBackButtonItem() {
		return ConfigurationItemHelper.createConfigurationItem(this.player, Settings.GUI_BACK_BTN_ITEM.getString(), Settings.GUI_BACK_BTN_NAME.getString(), Settings.GUI_BACK_BTN_LORE.getStringList(), null);
	}

	protected ItemStack getPreviousPageItem() {
		return ConfigurationItemHelper.createConfigurationItem(this.player, Settings.GUI_PREV_PAGE_BTN_ITEM.getString(), Settings.GUI_PREV_PAGE_BTN_NAME.getString(), Settings.GUI_PREV_PAGE_BTN_LORE.getStringList(), null);
	}

	protected ItemStack getNextPageItem() {
		return ConfigurationItemHelper.createConfigurationItem(this.player, Settings.GUI_NEXT_PAGE_BTN_ITEM.getString(), Settings.GUI_NEXT_PAGE_BTN_NAME.getString(), Settings.GUI_NEXT_PAGE_BTN_LORE.getStringList(), null);
	}

	protected ItemStack getRefreshButtonItem() {
		return ConfigurationItemHelper.createConfigurationItem(this.player, Settings.GUI_REFRESH_BTN_ITEM.getString(), Settings.GUI_REFRESH_BTN_NAME.getString(), Settings.GUI_REFRESH_BTN_LORE.getStringList(), null);
	}

	protected ItemStack getCloseButtonItem() {
		return ConfigurationItemHelper.createConfigurationItem(this.player, Settings.GUI_CLOSE_BTN_ITEM.getString(), Settings.GUI_CLOSE_BTN_NAME.getString(), Settings.GUI_CLOSE_BTN_LORE.getStringList(), null);
	}

	// confirms
}
