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

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.flight.comp.enums.CompSound;
import ca.tweetzy.flight.gui.Gui;
import ca.tweetzy.flight.gui.events.GuiClickEvent;
import ca.tweetzy.flight.gui.template.BaseGUI;
import ca.tweetzy.flight.hooks.PlaceholderAPIHook;
import ca.tweetzy.flight.utils.QuickItem;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
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
		super(parent, PlaceholderAPIHook.tryReplace(player, title), rows);
		this.parent = parent;
		this.player = player;
		this.items = items;
		this.updateDelay = updateDelay;
		applyDefaults();
	}

	public AuctionUpdatingPagedGUI(@NonNull final Player player, @NonNull final String title, final int rows, int updateDelay, @NonNull final List<T> items) {
		this(null, player, title, rows, updateDelay, items);
		applyDefaults();
	}

	@Override
	protected void draw() {
		reset();
		populateItems();
		drawFixed();
	}

	protected void startTask() {
		this.task = Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(AuctionHouse.getInstance(), () -> {
//			this.fillSlots().forEach(slot -> setItem(slot, getDefaultItem()));
			populateItems();
		}, 0L, updateDelay);
	}

	protected void applyClose() {
		setOnClose(close -> {
			cancelTask();
		});
	}

	protected void prePopulate() {
	}


	protected void drawFixed() {
	}

	protected void cancelTask() {
		if (this.task != null) {
			this.task.cancel();
		}
	}

	private void populateItems() {
		if (this.items != null) {
			AuctionHouse.newChain().asyncFirst(() -> {
				this.fillSlots().forEach(slot -> setItem(slot, getDefaultItem()));
				prePopulate();
				return this.items.stream().skip((page - 1) * (long) this.fillSlots().size()).limit(this.fillSlots().size()).collect(Collectors.toCollection(ArrayList::new));
			}).asyncLast((data) -> {
				pages = (int) Math.max(1, Math.ceil(this.items.size() / (double) this.fillSlots().size()));

				setPrevPage(getPreviousButtonSlot(), getPreviousButton());
				setNextPage(getNextButtonSlot(), getNextButton());
				setOnPage(e -> {
					draw();
				});

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

	@Override
	protected ItemStack getBackButton() {
		return QuickItem
				.of(Settings.GUI_BACK_BTN_ITEM.getString())
				.name(Settings.GUI_BACK_BTN_NAME.getString())
				.lore(this.player, Settings.GUI_BACK_BTN_LORE.getStringList())
				.make();
	}

	@Override
	protected ItemStack getExitButton() {
		return QuickItem
				.of(Settings.GUI_CLOSE_BTN_ITEM.getString())
				.name(Settings.GUI_CLOSE_BTN_NAME.getString())
				.lore(this.player, Settings.GUI_CLOSE_BTN_LORE.getStringList())
				.make();
	}

	@Override
	protected ItemStack getPreviousButton() {
		return QuickItem
				.of(Settings.GUI_PREV_PAGE_BTN_ITEM.getString())
				.name(Settings.GUI_PREV_PAGE_BTN_NAME.getString())
				.lore(this.player, Settings.GUI_PREV_PAGE_BTN_LORE.getStringList())
				.make();
	}

	@Override
	protected ItemStack getNextButton() {
		return QuickItem
				.of(Settings.GUI_NEXT_PAGE_BTN_ITEM.getString())
				.name(Settings.GUI_NEXT_PAGE_BTN_NAME.getString())
				.lore(this.player, Settings.GUI_NEXT_PAGE_BTN_LORE.getStringList())
				.make();
	}

	protected ItemStack getRefreshButton() {
		return QuickItem
				.of(Settings.GUI_REFRESH_BTN_ITEM.getString())
				.name(Settings.GUI_REFRESH_BTN_NAME.getString())
				.lore(this.player, Settings.GUI_REFRESH_BTN_LORE.getStringList())
				.make();
	}

	private void applyDefaults() {
		setDefaultItem(QuickItem.bg(QuickItem.of(Settings.GUI_FILLER.getString()).make()));
		setNavigateSound(CompSound.matchCompSound(Settings.SOUNDS_NAVIGATE_GUI_PAGES.getString()).orElse(CompSound.ENTITY_BAT_TAKEOFF));
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