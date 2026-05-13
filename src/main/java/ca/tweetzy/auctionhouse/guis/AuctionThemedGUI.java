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

package ca.tweetzy.auctionhouse.guis;

import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.auctionhouse.settings.Translations;
import ca.tweetzy.flight.comp.enums.CompSound;
import ca.tweetzy.flight.gui.Gui;
import ca.tweetzy.flight.gui.template.BaseGUI;
import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.flight.utils.QuickItem;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Shared Auction House GUI styling: filler item, sounds, and standard nav buttons.
 */
public abstract class AuctionThemedGUI extends BaseGUI {

	@Getter
	protected final Player player;

	protected AuctionThemedGUI(Gui parent, @NonNull final Player player, @NonNull String title, int rows) {
		super(parent, title, rows);
		this.player = player;
		applyAuctionThemeDefaults();
	}

	protected AuctionThemedGUI(Gui parent, @NonNull final Player player, @NonNull String title) {
		super(parent, title, 1);
		this.player = player;
		applyAuctionThemeDefaults();
	}

	protected AuctionThemedGUI(@NonNull final Player player, @NonNull String title) {
		super(title);
		this.player = player;
		applyAuctionThemeDefaults();
	}

	protected final void applyAuctionThemeDefaults() {
		setDefaultItem(QuickItem.bg(QuickItem.of(Settings.GUI_FILLER.getString()).make()));
		setNavigateSound(CompSound.matchCompSound(Settings.SOUNDS_NAVIGATE_GUI_PAGES.getString()).orElse(CompSound.ENTITY_BAT_TAKEOFF));
		setDefaultSound(CompSound.matchCompSound(Settings.SOUNDS_GUI_CLICK.getString()).orElse(CompSound.UI_BUTTON_CLICK));
	}

	/**
	 * Binds previous/next page buttons, or locks slots when not applicable.
	 *
	 * @param redraw runs after a page change (typically {@code this::draw})
	 */
	protected void bindPagingNavButtons(@NonNull Runnable redraw) {
		if (this.page > 1) {
			setButton(getPreviousButtonSlot(), getPreviousButton(), click -> {
				prevPage();
				redraw.run();
			});
		} else {
			setUnlocked(getPreviousButtonSlot(), false);
			setConditional(getPreviousButtonSlot(), null, null);
			setItem(getPreviousButtonSlot(), getDefaultItem());
		}

		if (this.page < pages) {
			setButton(getNextButtonSlot(), getNextButton(), click -> {
				nextPage();
				redraw.run();
			});
		} else {
			setUnlocked(getNextButtonSlot(), false);
			setConditional(getNextButtonSlot(), null, null);
			setItem(getNextButtonSlot(), getDefaultItem());
		}
	}

	@Override
	protected ItemStack getBackButton() {
		return QuickItem
				.of(Settings.GUI_BACK_BTN_ITEM.getString())
				.name(TranslationManager.string(this.player, Translations.GUI_GLOBAL_BACK_NAME))
				.lore(this.player, TranslationManager.list(this.player, Translations.GUI_GLOBAL_BACK_LORE))
				.make();
	}

	@Override
	protected ItemStack getExitButton() {
		return QuickItem
				.of(Settings.GUI_CLOSE_BTN_ITEM.getString())
				.name(TranslationManager.string(this.player, Translations.GUI_GLOBAL_CLOSE_NAME))
				.lore(this.player, TranslationManager.list(this.player, Translations.GUI_GLOBAL_CLOSE_LORE))
				.make();
	}

	@Override
	protected ItemStack getPreviousButton() {
		return QuickItem
				.of(Settings.GUI_PREV_PAGE_BTN_ITEM.getString())
				.name(TranslationManager.string(this.player, Translations.GUI_GLOBAL_PREV_PAGE_NAME))
				.lore(this.player, TranslationManager.list(this.player, Translations.GUI_GLOBAL_PREV_PAGE_LORE))
				.make();
	}

	@Override
	protected ItemStack getNextButton() {
		return QuickItem
				.of(Settings.GUI_NEXT_PAGE_BTN_ITEM.getString())
				.name(TranslationManager.string(this.player, Translations.GUI_GLOBAL_NEXT_PAGE_NAME))
				.lore(this.player, TranslationManager.list(this.player, Translations.GUI_GLOBAL_NEXT_PAGE_LORE))
				.make();
	}

	protected ItemStack getRefreshButton() {
		return QuickItem
				.of(Settings.GUI_REFRESH_BTN_ITEM.getString())
				.name(TranslationManager.string(this.player, Translations.GUI_GLOBAL_REFRESH_NAME))
				.lore(TranslationManager.list(this.player, Translations.GUI_GLOBAL_REFRESH_LORE))
				.make();
	}

	@Override
	protected int getPreviousButtonSlot() {
		return 48;
	}

	@Override
	protected int getNextButtonSlot() {
		return 50;
	}
}
