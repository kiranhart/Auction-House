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

package ca.tweetzy.auctionhouse.guis.core;


import ca.tweetzy.auctionhouse.lang.AuctionLocale;
import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.auction.AuctionedItem;
import ca.tweetzy.auctionhouse.auction.enums.AuctionStackType;
import ca.tweetzy.auctionhouse.guis.AuctionPagedGUI;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.flight.gui.events.GuiClickEvent;
import ca.tweetzy.flight.utils.QuickItem;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GUIWatchedListings extends AuctionPagedGUI<AuctionedItem> {

	private final AuctionPlayer auctionPlayer;

	public GUIWatchedListings(AuctionPlayer auctionPlayer) {
		super(new GUIAuctionHouse(auctionPlayer), auctionPlayer.getPlayer(), AuctionLocale.msg(auctionPlayer.getPlayer(), "gui.watchlist.title"), 6, new ArrayList<>(AuctionHouse.getWatchlistManager().getWatchedListings(auctionPlayer.getUuid())));
		this.auctionPlayer = auctionPlayer;
		setOnClose(close -> close.manager.showGUI(close.player, new GUIAuctionHouse(auctionPlayer)));
		draw();
	}

	@Override
	protected void prePopulate() {
		this.items = AuctionHouse.getWatchlistManager().getWatchedListings(auctionPlayer.getUuid()).stream()
				.sorted(Comparator.comparingLong(AuctionedItem::getExpiresAt))
				.collect(Collectors.toList());
	}

	@Override
	protected ItemStack makeDisplayItem(AuctionedItem auctionedItem) {
		return auctionedItem.isRequest()
				? auctionedItem.getDisplayRequestStack(this.player, AuctionStackType.MAIN_AUCTION_HOUSE)
				: auctionedItem.getDisplayStack(this.player, AuctionStackType.MAIN_AUCTION_HOUSE);
	}

	@Override
	protected void onClick(AuctionedItem item, GuiClickEvent click) {
		// Re-fetch in case it was removed/sold
		AuctionedItem current = AuctionHouse.getAuctionItemManager().getItem(item.getId());
		if (current == null || current.isExpired() || AuctionHouse.getAuctionItemManager().getGarbageBin().containsKey(item.getId())) {
			AuctionHouse.getWatchlistManager().remove(click.player.getUniqueId(), item.getId(), (err, ok) -> draw());
			return;
		}

		// Shift+Click = remove from watchlist
		if (click.clickType == ClickType.SHIFT_LEFT || click.clickType == ClickType.SHIFT_RIGHT) {
			AuctionHouse.getWatchlistManager().remove(click.player.getUniqueId(), current.getId(), (err, ok) -> {
				if (ok) {
					AuctionLocale.tell(click.player, "watchlist.removed", "item",AuctionAPI.getInstance().getItemName(current.getItem()));
				}
				draw();
			});
			return;
		}

		// Left/Right = open main auction house so they can view and buy/bid from there
		click.manager.showGUI(click.player, new GUIAuctionHouse(this.auctionPlayer));
	}

	@Override
	protected void drawFixed() {
		setButton(getBackExitButtonSlot(), getBackButton(), click ->
				click.manager.showGUI(click.player, new GUIAuctionHouse(this.auctionPlayer)));

		if (this.items.isEmpty()) {
			int emptySlot = 22;
			setItem(emptySlot, QuickItem
					.of(Settings.GUI_WATCHLIST_EMPTY_ITEM.getString())
					.name(AuctionLocale.msg(this.player, "gui.watchlist.empty.name"))
					.lore(this.player, AuctionLocale.msgList(this.player, "gui.watchlist.empty.lore"))
					.make());
		}
	}

	@Override
	protected List<Integer> fillSlots() {
		return Settings.GUI_AUCTION_HOUSE_FILL_SLOTS.getIntList();
	}
}
