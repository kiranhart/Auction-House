/*
 * Auction House
 * Copyright 2023 Kiran Hart
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

package ca.tweetzy.auctionhouse.guis.abstraction;

import ca.tweetzy.auctionhouse.helpers.ConfigurationItemHelper;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.flight.comp.enums.CompSound;
import ca.tweetzy.flight.gui.Gui;
import ca.tweetzy.flight.gui.template.BaseGUI;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.entity.Player;

public abstract class AuctionBaseGUI extends BaseGUI {

	@Getter
	protected final Player player;

	public AuctionBaseGUI(Gui parent, @NonNull final Player player, @NonNull String title, int rows) {
		super(parent, title, rows);
		this.player = player;
		setTitle(title);
		applyDefaults();
	}

	public AuctionBaseGUI(Gui parent, @NonNull final Player player, @NonNull String title) {
		super(parent, title);
		this.player = player;
		setTitle(title);
		applyDefaults();
	}

	public AuctionBaseGUI(@NonNull final Player player, @NonNull String title) {
		super(title);
		this.player = player;
		setTitle(title);
		applyDefaults();
	}

	private void applyDefaults() {
		setDefaultItem(ConfigurationItemHelper.createConfigurationItem(this.player, Settings.GUI_FILLER.getString()));
		setNavigateSound(CompSound.matchCompSound(Settings.SOUNDS_NAVIGATE_GUI_PAGES.getString()).orElse(CompSound.ENTITY_BAT_TAKEOFF));
	}

//	@Override
//	protected ItemStack getBackButton() {
//		return QuickItem
//				.of(Settings.GUI_SHARED_ITEMS_BACK_BUTTON.getItemStack())
//				.name(TranslationManager.string(this.player, Translations.GUI_SHARED_ITEMS_BACK_BUTTON_NAME))
//				.lore(TranslationManager.list(this.player, Translations.GUI_SHARED_ITEMS_BACK_BUTTON_LORE, "left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK)))
//				.make();
//	}
//
//	@Override
//	protected ItemStack getExitButton() {
//		return QuickItem
//				.of(Settings.GUI_SHARED_ITEMS_EXIT_BUTTON.getItemStack())
//				.name(TranslationManager.string(this.player, Translations.GUI_SHARED_ITEMS_EXIT_BUTTON_NAME))
//				.lore(TranslationManager.list(this.player, Translations.GUI_SHARED_ITEMS_EXIT_BUTTON_LORE, "left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK)))
//				.make();
//	}
//
//	@Override
//	protected ItemStack getPreviousButton() {
//		return QuickItem
//				.of(Settings.GUI_BACK_BTN_ITEM.getString())
//				.name(TranslationManager.string(this.player, Translations.GUI_SHARED_ITEMS_PREVIOUS_BUTTON_NAME))
//				.lore(TranslationManager.list(this.player, Translations.GUI_SHARED_ITEMS_PREVIOUS_BUTTON_LORE, "left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK)))
//				.make();
//	}
//
//	@Override
//	protected ItemStack getNextButton() {
//		return QuickItem
//				.of(Settings.GUI_SHARED_ITEMS_NEXT_BUTTON.getItemStack())
//				.name(TranslationManager.string(this.player, Translations.GUI_SHARED_ITEMS_NEXT_BUTTON_NAME))
//				.lore(TranslationManager.list(this.player, Translations.GUI_SHARED_ITEMS_NEXT_BUTTON_LORE, "left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK)))
//				.make();
//	}

	@Override
	protected int getPreviousButtonSlot() {
		return 48;
	}

	@Override
	protected int getNextButtonSlot() {
		return 50;
	}
}