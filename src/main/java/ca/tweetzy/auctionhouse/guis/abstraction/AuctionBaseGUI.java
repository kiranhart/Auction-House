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

import ca.tweetzy.core.gui.Gui;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.entity.Player;

public abstract class AuctionBaseGUI extends Gui {

	@Getter
	protected final Player player;

	public AuctionBaseGUI(Gui parent, @NonNull final Player player, @NonNull String title, int rows) {
		super(rows, parent);
		this.player = player;
		setTitle(title);
	}

	public AuctionBaseGUI(Gui parent, @NonNull final Player player, @NonNull String title) {
		super(6, parent);
		this.player = player;
		setTitle(title);
	}

	public AuctionBaseGUI(@NonNull final Player player, @NonNull String title) {
		super(6, null);
		this.player = player;
		setTitle(title);
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
//	protected ItemStack getPreviousPageButton() {
//		return QuickItem
//				.of(Settings.GUI_SHARED_ITEMS_PREVIOUS_BUTTON.getItemStack())
//				.name(TranslationManager.string(this.player, Translations.GUI_SHARED_ITEMS_PREVIOUS_BUTTON_NAME))
//				.lore(TranslationManager.list(this.player, Translations.GUI_SHARED_ITEMS_PREVIOUS_BUTTON_LORE, "left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK)))
//				.make();
//	}
//
//	@Override
//	protected ItemStack getNextPageButton() {
//		return QuickItem
//				.of(Settings.GUI_SHARED_ITEMS_NEXT_BUTTON.getItemStack())
//				.name(TranslationManager.string(this.player, Translations.GUI_SHARED_ITEMS_NEXT_BUTTON_NAME))
//				.lore(TranslationManager.list(this.player, Translations.GUI_SHARED_ITEMS_NEXT_BUTTON_LORE, "left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK)))
//				.make();
//	}
}