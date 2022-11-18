/*
 * Auction House
 * Copyright 2022 Kiran Hart
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

package ca.tweetzy.auctionhouse.guis.sell;

import ca.tweetzy.auctionhouse.ahv3.api.ListingType;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.auction.enums.AuctionSaleType;
import ca.tweetzy.auctionhouse.guis.AbstractPlaceholderGui;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.gui.GuiUtils;
import ca.tweetzy.core.utils.PlayerUtils;
import ca.tweetzy.flight.utils.QuickItem;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Objects;

public final class GUISellPlaceItem extends AbstractPlaceholderGui {

	public enum ViewMode {
		SINGLE_ITEM,
		BUNDLE_ITEM
	}

	private final AuctionPlayer auctionPlayer;
	private final ViewMode viewMode;
	private final ListingType listingType;

	public GUISellPlaceItem(@NonNull final AuctionPlayer auctionPlayer, @NonNull final ViewMode viewMode, @NonNull final ListingType listingType) {
		super(auctionPlayer);
		this.auctionPlayer = auctionPlayer;
		this.viewMode = viewMode;
		this.listingType = listingType;
		setTitle(Settings.GUI_SELL_PLACE_ITEM_TITLE.getString());
		setDefaultItem(GuiUtils.createButtonItem(Settings.GUI_SELL_PLACE_ITEM_BG_ITEM.getMaterial(), " "));
		setRows(viewMode == ViewMode.SINGLE_ITEM ? 4 : 6);

		setAcceptsItems(true);

		if (viewMode == ViewMode.SINGLE_ITEM) {
			setUnlocked(1, 4);
			setItem(1, 4, AIR);
		} else {
			setUnlockedRange(0, 35);
			setItems(0, 35, AIR);
		}

		setOnClose(close -> gatherSellableItems().forEach(item -> PlayerUtils.giveItem(close.player, item)));

		draw();
	}

	private void draw() {

		setButton(getRows() - 1, 0, QuickItem
				.of(Objects.requireNonNull(Settings.GUI_CLOSE_BTN_ITEM.getMaterial().parseItem()))
				.name(Settings.GUI_CLOSE_BTN_NAME.getString())
				.lore(Settings.GUI_CLOSE_BTN_LORE.getStringList())
				.make(), click -> {

			if (click.cursor.getType() == Material.AIR) {
				click.gui.close();
				click.manager.showGUI(click.player, new GUISellListingType(this.auctionPlayer, selectedListing -> click.manager.showGUI(click.player, new GUISellPlaceItem(this.auctionPlayer, this.viewMode, selectedListing))));
			}
		});

		setButton(getRows() - 1, 4, QuickItem
				.of(Objects.requireNonNull(Settings.GUI_SELL_PLACE_ITEM_ITEMS_CONTINUE_ITEM.getMaterial().parseItem()))
				.name(Settings.GUI_SELL_PLACE_ITEM_ITEMS_CONTINUE_NAME.getString())
				.lore(Settings.GUI_SELL_PLACE_ITEM_ITEMS_CONTINUE_LORE.getStringList())
				.make(), click -> {

			final HashSet<ItemStack> items = gatherSellableItems();

			if (items.isEmpty())
				return;

			final ItemStack toList = items.size() > 1 ? AuctionAPI.getInstance().createBundledItem(items.stream().findFirst().orElse(null), items.toArray(new ItemStack[0])) : items.stream().findFirst().orElse(null);
			if (toList == null) return;

			this.auctionPlayer.setItemBeingListed(toList);
			click.gui.exit();

			if (this.listingType == ListingType.BIN)
				click.manager.showGUI(click.player, new GUISellBin(this.auctionPlayer, Settings.MIN_AUCTION_PRICE.getDouble(), System.currentTimeMillis() + (1000L * this.auctionPlayer.getAllowedSellTime(AuctionSaleType.WITHOUT_BIDDING_SYSTEM)), false));
			else
				click.manager.showGUI(click.player, new GUISellAuction(
						this.auctionPlayer,
						Settings.MIN_AUCTION_PRICE.getDouble(),
						Settings.MIN_AUCTION_START_PRICE.getDouble(),
						Settings.MIN_AUCTION_INCREMENT_PRICE.getDouble(),
						System.currentTimeMillis() + (1000L * this.auctionPlayer.getAllowedSellTime(AuctionSaleType.USED_BIDDING_SYSTEM)),
						Settings.ALLOW_USAGE_OF_BUY_NOW_SYSTEM.getBoolean()
				));
		});

		if (Settings.ALLOW_ITEM_BUNDLES.getBoolean()) {
			setButton(getRows() - 1, 8, QuickItem
					.of(Objects.requireNonNull(Settings.ITEM_BUNDLE_ITEM.getMaterial().parseItem()))
					.name(this.viewMode == ViewMode.SINGLE_ITEM ? Settings.GUI_SELL_PLACE_ITEM_ITEMS_SINGLE_NAME.getString() : Settings.GUI_SELL_PLACE_ITEM_ITEMS_BUNDLE_NAME.getString())
					.lore(this.viewMode == ViewMode.SINGLE_ITEM ? Settings.GUI_SELL_PLACE_ITEM_ITEMS_SINGLE_LORE.getStringList() : Settings.GUI_SELL_PLACE_ITEM_ITEMS_BUNDLE_LORE.getStringList())
					.make(), click -> {

				click.gui.close();
				click.manager.showGUI(click.player, new GUISellPlaceItem(this.auctionPlayer, this.viewMode == ViewMode.SINGLE_ITEM ? ViewMode.BUNDLE_ITEM : ViewMode.SINGLE_ITEM, this.listingType));
			});
		}
	}

	private HashSet<ItemStack> gatherSellableItems() {
		final HashSet<ItemStack> items = new HashSet<>();

		for (int i = this.viewMode == ViewMode.SINGLE_ITEM ? 13 : 0; i < (this.viewMode == ViewMode.SINGLE_ITEM ? 14 : 36); i++) {
			final ItemStack item = getItem(i);
			if (item != null)
				items.add(item);
		}

		return items;
	}
}
