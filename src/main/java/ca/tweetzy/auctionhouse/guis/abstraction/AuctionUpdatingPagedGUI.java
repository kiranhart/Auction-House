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

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.core.gui.BaseGUI;
import ca.tweetzy.core.gui.Gui;
import ca.tweetzy.core.gui.events.GuiClickEvent;
import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.flight.utils.QuickItem;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.stream.Collectors;

public abstract class AuctionUpdatingPagedGUI<T> extends BaseGUI {

	@Getter
	protected final Player player;
	protected final Gui parent;
	protected List<T> items;
	protected final int updateDelay;
	protected BukkitTask task;

	public AuctionUpdatingPagedGUI(final Gui parent, @NonNull final Player player, @NonNull final String title, final int rows, int updateDelay, @NonNull final List<T> items) {
		super(parent, title, rows);
		this.parent = parent;
		this.player = player;
		this.items = items;
		this.updateDelay = updateDelay;
	}

	public AuctionUpdatingPagedGUI(@NonNull final Player player, @NonNull final String title, final int rows, int updateDelay, @NonNull final List<T> items) {
		this(null, player, title, rows, updateDelay, items);
	}

	@Override
	protected void draw() {
		reset();
		drawFixed();
		populateItems();
	}

	protected void startTask() {
		this.task = Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(AuctionHouse.getInstance(), () -> {
//			this.fillSlots().forEach(slot -> setItem(slot, getDefaultItem()));
			populateItems();
		}, 0L, updateDelay);
	}

	protected void applyClose() {
		setOnClose(close -> cancelTask());
	}

	protected void prePopulate() {
	}

	protected void drawFixed() {
	}

	protected void cancelTask() {
		if (this.task != null) {
			this.task.cancel();
			Common.log("Cancelled updating task in menu");
		}
	}

	private void populateItems() {
		if (this.items != null) {
			AuctionHouse.newChain().asyncFirst(() -> {
				this.fillSlots().forEach(slot -> setItem(slot, getDefaultItem()));
				prePopulate();

				final List<T> itemsToFill = this.items.stream().skip((page - 1) * (long) this.fillSlots().size()).limit(this.fillSlots().size()).collect(Collectors.toList());
				return itemsToFill;
			}).asyncLast((data) -> {
				pages = (int) Math.max(1, Math.ceil(this.items.size() / (double) this.fillSlots().size()));

				setPrevPage(getPreviousButtonSlot(), getPreviousButton());
				setNextPage(getNextButtonSlot(), getNextButton());
				setOnPage(e -> draw());

				for (int i = 0; i < this.rows * 9; i++) {
					if (this.fillSlots().contains(i) && this.fillSlots().indexOf(i) < data.size()) {
						final T object = data.get(this.fillSlots().indexOf(i));
						setButton(i, this.makeDisplayItem(object), click -> this.onClick(object, click));
					}
				}
			}).execute();
		}
	}

	protected abstract ItemStack makeDisplayItem(final T object);

	protected abstract void onClick(final T object, final GuiClickEvent clickEvent);

	protected ItemStack getPreviousButton() {
		return QuickItem.of(CompMaterial.ARROW, "&ePrevious").make();
	}

	protected ItemStack getNextButton() {
		return QuickItem.of(CompMaterial.ARROW, "&eNext").make();
	}

	protected int getPreviousButtonSlot() {
		return 48;
	}

	protected int getNextButtonSlot() {
		return 50;
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