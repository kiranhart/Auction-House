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

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.auction.AuctionedItem;
import ca.tweetzy.auctionhouse.auction.enums.AuctionStackType;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.compatibility.XSound;
import ca.tweetzy.core.utils.TextUtils;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: March 9th 2023
 * Time Created: 11:55 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUIActiveBids extends AbstractPlaceholderGui {

	private final AuctionPlayer auctionPlayer;
	private List<AuctionedItem> items;

	public GUIActiveBids(AuctionPlayer auctionPlayer) {
		super(auctionPlayer);
		this.auctionPlayer = auctionPlayer;
		setTitle(TextUtils.formatText(Settings.GUI_ACTIVE_BIDS_TITLE.getString()));
		setRows(6);
		setAcceptsItems(false);
		setNavigateSound(XSound.matchXSound(Settings.SOUNDS_NAVIGATE_GUI_PAGES.getString()).orElse(XSound.ENTITY_BAT_TAKEOFF));
		draw();
	}

	private void draw() {
		reset();
		drawFixedButtons();
		drawItems();
	}

	private void drawItems() {
		AuctionHouse.newChain().asyncFirst(() -> {
			this.items = AuctionHouse.getInstance().getAuctionItemManager().getHighestBidItems(this.player);

			// per world check
			if (Settings.PER_WORLD_ITEMS.getBoolean()) {
				this.items = this.items.stream().filter(item -> item.getListedWorld() == null || this.auctionPlayer.getPlayer().getWorld().getName().equals(item.getListedWorld())).collect(Collectors.toList());
			}

			return this.items.stream().sorted(Comparator.comparingLong(AuctionedItem::getExpiresAt).reversed()).skip((page - 1) * 45L).limit(45).collect(Collectors.toList());
		}).asyncLast((data) -> {
			pages = (int) Math.max(1, Math.ceil(this.items.size() / (double) 45L));
			drawPaginationButtons();

			int slot = 0;
			for (AuctionedItem item : data) {
				setItem(slot++, item.getDisplayStack(AuctionStackType.HIGHEST_BID_PREVIEW));
			}
		}).execute();
	}

	private void drawFixedButtons() {
		setButton(5, 0, getBackButtonItem(), e -> {
			e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
		});

	}

	private void drawPaginationButtons() {
		setPrevPage(5, 3, getPreviousPageItem());
		setNextPage(5, 5, getNextPageItem());
		setOnPage(e -> draw());
	}
}
