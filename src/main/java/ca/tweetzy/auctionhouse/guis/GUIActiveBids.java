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
import ca.tweetzy.auctionhouse.guis.abstraction.AuctionPagedGUI;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.gui.events.GuiClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: March 9th 2023
 * Time Created: 11:55 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUIActiveBids extends AuctionPagedGUI<AuctionedItem> {

	private final AuctionPlayer auctionPlayer;

	public GUIActiveBids(AuctionPlayer auctionPlayer) {
		super(new GUIAuctionHouse(auctionPlayer), auctionPlayer.getPlayer(), Settings.GUI_ACTIVE_BIDS_TITLE.getString(), 6, new ArrayList<>(AuctionHouse.getInstance().getAuctionItemManager().getHighestBidItems(auctionPlayer.getPlayer())));
		this.auctionPlayer = auctionPlayer;
		draw();
	}

	@Override
	protected void drawFixed() {
		applyBackExit();
	}

	@Override
	protected void prePopulate() {
		if (Settings.PER_WORLD_ITEMS.getBoolean())
			this.items = this.items.stream().filter(item -> item.getListedWorld() == null || this.auctionPlayer.getPlayer().getWorld().getName().equals(item.getListedWorld())).collect(Collectors.toList());

		this.items.sort(Comparator.comparingLong(AuctionedItem::getExpiresAt).reversed());
	}

	@Override
	protected ItemStack makeDisplayItem(AuctionedItem auctionedItem) {
		return auctionedItem.getDisplayStack(AuctionStackType.HIGHEST_BID_PREVIEW);
	}

	@Override
	protected void onClick(AuctionedItem object, GuiClickEvent clickEvent) {
	}
}
