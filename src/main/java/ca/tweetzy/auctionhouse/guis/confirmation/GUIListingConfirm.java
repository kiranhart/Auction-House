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

package ca.tweetzy.auctionhouse.guis.confirmation;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.auction.AuctionedItem;
import ca.tweetzy.auctionhouse.auction.enums.AuctionStackType;
import ca.tweetzy.auctionhouse.guis.AbstractPlaceholderGui;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.utils.TextUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public final class GUIListingConfirm extends AbstractPlaceholderGui {

	private final AuctionedItem auctionedItem;
	private final Consumer<Boolean> result;

	private final Set<UUID> resulted = new HashSet<>();

	public GUIListingConfirm(Player player, AuctionedItem auctionedItem, Consumer<Boolean> result) {
		super(player);
		this.auctionedItem = auctionedItem;
		this.result = result;
		super.setTitle(TextUtils.formatText(Settings.GUI_CONFIRM_LISTING_TITLE.getString()));
		setAcceptsItems(false);
		setAllowClose(false);
		setOnOpen(open -> {
			if (open.player.hasMetadata("AuctionHouseConfirmListing")) {
				open.gui.close();
				return;
			}

			open.player.setMetadata("AuctionHouseConfirmListing", new FixedMetadataValue(AuctionHouse.getInstance(), "ConfirmListing"));
		});

		setOnClose(close -> {
			close.player.removeMetadata("AuctionHouseConfirmListing", AuctionHouse.getInstance());
			AuctionHouse.getInstance().getAuctionPlayerManager().processSell(close.player);
		});

		setRows(1);
		draw();
	}


	private void draw() {
		setItems(0, 3, getConfirmListingYesItem());
		setItem(0, 4, this.auctionedItem.getDisplayStack(AuctionStackType.LISTING_PREVIEW));
		setItems(5, 8, getConfirmListingNoItem());

		setActionForRange(5, 8, ClickType.LEFT, e -> {
			if (resulted.contains(e.player.getUniqueId())) return;
			resulted.add(e.player.getUniqueId());

			setAllowClose(true);
			this.result.accept(false);
		});
		setActionForRange(0, 3, ClickType.LEFT, e -> {
			if (resulted.contains(e.player.getUniqueId())) return;
			resulted.add(e.player.getUniqueId());

			setAllowClose(true);
			this.result.accept(true);
		});
	}

}
