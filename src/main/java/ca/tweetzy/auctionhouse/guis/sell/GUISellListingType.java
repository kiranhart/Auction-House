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

package ca.tweetzy.auctionhouse.guis.sell;

import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.auction.ListingType;
import ca.tweetzy.auctionhouse.guis.abstraction.AuctionBaseGUI;
import ca.tweetzy.auctionhouse.guis.core.GUIAuctionHouseV2;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.flight.utils.QuickItem;
import lombok.NonNull;

import java.util.function.Consumer;

public final class GUISellListingType extends AuctionBaseGUI {

	private final AuctionPlayer auctionPlayer;
	private final Consumer<ListingType> listingType;

	public GUISellListingType(@NonNull final AuctionPlayer auctionPlayer, final Consumer<ListingType> listingType) {
		super(null, auctionPlayer.getPlayer(), Settings.GUI_SELL_LISTING_TYPE_TITLE.getString(), 3);
		this.auctionPlayer = auctionPlayer;
		this.listingType = listingType;
		setDefaultItem(QuickItem.bg(QuickItem.of(Settings.GUI_SELL_LISTING_TYPE_BG_ITEM.getString()).make()));
		draw();
	}

	@Override
	protected void draw() {
		if (Settings.FORCE_AUCTION_USAGE.getBoolean())
			drawAuctionButton(4);

		if (!Settings.ALLOW_USAGE_OF_BID_SYSTEM.getBoolean())
			drawBinButton(4);

		if (Settings.ALLOW_USAGE_OF_BID_SYSTEM.getBoolean() && !(Settings.FORCE_AUCTION_USAGE.getBoolean())) {
			drawAuctionButton(6);
			drawBinButton(2);
		}

		setButton(getRows() - 1, 0, QuickItem
				.of(Settings.GUI_SELL_LISTING_TYPE_ITEMS_RETURN_ITEM.getString())
				.name(Settings.GUI_SELL_LISTING_TYPE_ITEMS_RETURN_NAME.getString())
				.lore(Settings.GUI_SELL_LISTING_TYPE_ITEMS_RETURN_LORE.getStringList())
				.make(), click -> click.manager.showGUI(click.player, new GUIAuctionHouseV2(this.auctionPlayer)));
	}

	private void drawAuctionButton(int col) {
		setButton(1, col, QuickItem
				.of(Settings.GUI_SELL_LISTING_TYPE_ITEMS_AUCTION_ITEM.getString())
				.name(Settings.GUI_SELL_LISTING_TYPE_ITEMS_AUCTION_NAME.getString())
				.lore(Settings.GUI_SELL_LISTING_TYPE_ITEMS_AUCTION_LORE.getStringList())
				.make(), click -> {

			if (this.listingType != null)
				this.listingType.accept(ListingType.AUCTION);
		});
	}

	private void drawBinButton(int col) {
		setButton(1, col, QuickItem
				.of(Settings.GUI_SELL_LISTING_TYPE_ITEMS_BIN_ITEM.getString())
				.name(Settings.GUI_SELL_LISTING_TYPE_ITEMS_BIN_NAME.getString())
				.lore(Settings.GUI_SELL_LISTING_TYPE_ITEMS_BIN_LORE.getStringList())
				.make(), click -> {

			if (this.listingType != null)
				this.listingType.accept(ListingType.BIN);
		});
	}
}
