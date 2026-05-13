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
import ca.tweetzy.flight.gui.events.GuiClickEvent;
import ca.tweetzy.flight.hooks.PlaceholderAPIHook;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AuctionPagedGUI<T> extends AuctionThemedGUI {

	@Getter
	protected final Gui parent;
	protected List<T> items;

	@Setter
	protected boolean async = false;

	public AuctionPagedGUI(Gui parent, @NonNull final Player player, @NonNull String title, int rows, @NonNull List<T> items) {
		super(parent, player, PlaceholderAPIHook.tryReplace(player, title), rows);
		this.parent = parent;
		this.items = (items instanceof ArrayList) ? items : new ArrayList<>(items);
	}

	public AuctionPagedGUI(@NonNull final Player player, @NonNull String title, int rows, @NonNull List<T> items) {
		this(null, player, title, rows, items);
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

	protected void prePopulate() {
	}

	protected void drawFixed() {
	}

	private void populateItems() {
		if (this.items != null) {
			if (!this.async) {
				renderItems();
			} else {
				AuctionHouse.newChain().asyncFirst(() -> {
					prePopulate();
					final List<Integer> slotCoords = this.fillSlots();
					final int slotCount = slotCoords.size();
					final List<T> paginatedItems = this.items.stream()
							.skip((page - 1) * (long) slotCount)
							.limit(slotCount)
							.collect(Collectors.toList());

					final Map<Integer, ItemStack> slotToItemStack = new HashMap<>();
					final Map<Integer, T> slotToObject = new HashMap<>();

					for (int idx = 0; idx < slotCoords.size(); idx++) {
						if (idx >= paginatedItems.size()) {
							break;
						}
						final int slot = slotCoords.get(idx);
						final T object = paginatedItems.get(idx);
						slotToItemStack.put(slot, this.makeDisplayItem(object));
						slotToObject.put(slot, object);
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

					this.fillSlots().forEach(slot -> setItem(slot, getDefaultItem()));

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
	}

	private void renderItems() {
		this.fillSlots().forEach(slot -> setItem(slot, getDefaultItem()));
		prePopulate();

		final List<Integer> slotCoords = this.fillSlots();
		final int slotCount = slotCoords.size();

		final List<T> itemsToFill = this.items.stream()
				.skip((page - 1) * (long) slotCount)
				.limit(slotCount)
				.collect(Collectors.toList());
		pages = (int) Math.max(1, Math.ceil(this.items.size() / (double) slotCount));

		bindPagingNavButtons(this::draw);

		for (int idx = 0; idx < slotCoords.size() && idx < itemsToFill.size(); idx++) {
			final int slot = slotCoords.get(idx);
			final T object = itemsToFill.get(idx);
			setButton(slot, this.makeDisplayItem(object), click -> this.onClick(object, click));
		}
	}

	protected abstract ItemStack makeDisplayItem(final T object);

	protected abstract void onClick(final T object, final GuiClickEvent clickEvent);
}
