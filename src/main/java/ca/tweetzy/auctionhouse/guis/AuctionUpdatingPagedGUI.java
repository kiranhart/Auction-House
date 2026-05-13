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
import ca.tweetzy.auctionhouse.managers.SoundManager;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.flight.gui.Gui;
import ca.tweetzy.flight.gui.GuiManager;
import ca.tweetzy.flight.gui.events.GuiClickEvent;
import ca.tweetzy.flight.hooks.PlaceholderAPIHook;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AuctionUpdatingPagedGUI<T> extends AuctionThemedGUI {

	@Getter
	protected final Gui parent;
	protected List<T> items;
	protected final int updateDelay;
	protected BukkitTask task;

	public AuctionUpdatingPagedGUI(final Gui parent, @NonNull final Player player, @NonNull final String title, final int rows, int updateDelay, @NonNull final List<T> items) {
		super(parent, player, PlaceholderAPIHook.tryReplace(player, title), rows);
		this.parent = parent;
		this.items = (items instanceof ArrayList) ? items : new ArrayList<>(items);
		this.updateDelay = updateDelay;
	}

	public AuctionUpdatingPagedGUI(@NonNull final Player player, @NonNull final String title, final int rows, int updateDelay, @NonNull final List<T> items) {
		this(null, player, title, rows, updateDelay, items);
	}

	@Override
	protected void draw() {
		int currentPage = this.page;
		reset();
		this.page = currentPage;
		setOnPage(e -> {
			draw();
			SoundManager.getInstance().playSound(player, Settings.SOUNDS_NAVIGATE_GUI_PAGES.getString());
		});
		populateItems();
		drawFixed();
	}

	protected void startTask() {
		if (this.task != null && !this.task.isCancelled()) {
			this.task.cancel();
			if (AuctionHouse.isDebugMode()) {
				AuctionHouse.getInstance().getLogger().warning("[AuctionUpdatingPagedGUI] Cancelled existing task before starting new one for " + this.getClass().getSimpleName() + " (player: " + this.player.getName() + ")");
			}
		}

		this.task = Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(AuctionHouse.getInstance(), this::draw, 0L, updateDelay);

		if (AuctionHouse.isDebugMode()) {
			AuctionHouse.getInstance().getLogger().info("[AuctionUpdatingPagedGUI] Started update task for " + this.getClass().getSimpleName() + " (player: " + this.player.getName() + ", task ID: " + this.task.getTaskId() + ", delay: " + this.updateDelay + " ticks)");
		}
	}

	protected void applyClose() {
		setOnClose(close -> {
			if (AuctionHouse.isDebugMode()) {
				AuctionHouse.getInstance().getLogger().info("[AuctionUpdatingPagedGUI] setOnClose triggered for " + this.getClass().getSimpleName() + " (player: " + close.player.getName() + ")");
			}
			cancelTask();
		});
	}

	protected void prePopulate() {
	}

	protected void drawFixed() {
	}

	protected void cancelTask() {
		if (this.task != null && !this.task.isCancelled()) {
			int taskId = this.task.getTaskId();
			this.task.cancel();
			this.task = null;
			if (AuctionHouse.isDebugMode()) {
				AuctionHouse.getInstance().getLogger().info("[AuctionUpdatingPagedGUI] Cancelled update task for " + this.getClass().getSimpleName() + " (player: " + this.player.getName() + ", task ID: " + taskId + ")");
			}
		} else if (this.task != null) {
			this.task = null;
			if (AuctionHouse.isDebugMode()) {
				AuctionHouse.getInstance().getLogger().warning("[AuctionUpdatingPagedGUI] Task was already cancelled but reference still exists for " + this.getClass().getSimpleName() + " (player: " + this.player.getName() + ") - cleared reference");
			}
		}
	}

	protected void safeTransitionTo(@NonNull GuiManager manager, @NonNull Gui newGui) {
		this.cancelTask();
		this.transitionTo(manager, this.player, newGui);
	}

	private void populateItems() {
		if (this.items != null) {
			AuctionHouse.newChain().asyncFirst(() -> {
				for (int i = 0; i < this.getRows() * 9; i++) {
					setItem(i, getDefaultItem());
				}

				prePopulate();
				final List<Integer> slotCoords = this.fillSlots();
				final int slotCount = slotCoords.size();
				final List<T> paginatedItems = this.items.stream()
						.skip((page - 1) * (long) slotCount)
						.limit(slotCount)
						.collect(Collectors.toCollection(ArrayList::new));

				final Map<Integer, ItemStack> slotToItemStack = new HashMap<>();
				final Map<Integer, T> slotToObject = new HashMap<>();

				for (int idx = 0; idx < slotCoords.size(); idx++) {
					if (idx >= paginatedItems.size()) {
						break;
					}
					final int slot = slotCoords.get(idx);
					final T object = paginatedItems.get(idx);
					if (object != null) {
						ItemStack displayItem = this.makeDisplayItem(object);
						if (displayItem != null) {
							slotToItemStack.put(slot, displayItem);
							slotToObject.put(slot, object);
						}
					}
				}

				return new Object[] { slotToItemStack, slotToObject };
			}).asyncLast((result) -> {
				@SuppressWarnings("unchecked")
				final Map<Integer, ItemStack> slotToItemStack = (Map<Integer, ItemStack>) ((Object[]) result)[0];
				@SuppressWarnings("unchecked")
				final Map<Integer, T> slotToObject = (Map<Integer, T>) ((Object[]) result)[1];

				final List<Integer> slotCoords = this.fillSlots();
				final int slotCount = slotCoords.size();
				pages = (int) Math.max(1, Math.ceil(this.items.size() / (double) slotCount));

				this.fillSlots().forEach(slot -> setItem(slot, getEmptyFillSlotItem()));

				bindPagingNavButtons(this::draw);

				for (Map.Entry<Integer, ItemStack> entry : slotToItemStack.entrySet()) {
					final int slot = entry.getKey();
					final ItemStack itemStack = entry.getValue();
					final T object = slotToObject.get(slot);
					setButton(slot, itemStack, click -> this.onClick(object, click));
				}
			}).execute();
		}
	}

	protected ItemStack getEmptyFillSlotItem() {
		return getDefaultItem();
	}

	protected abstract ItemStack makeDisplayItem(final T object);

	protected abstract void onClick(final T object, final GuiClickEvent clickEvent);
}
