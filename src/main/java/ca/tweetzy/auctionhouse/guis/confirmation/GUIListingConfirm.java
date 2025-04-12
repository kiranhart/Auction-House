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
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.auction.AuctionedItem;
import ca.tweetzy.auctionhouse.auction.enums.AuctionStackType;
import ca.tweetzy.auctionhouse.guis.AuctionBaseGUI;
import ca.tweetzy.auctionhouse.helpers.BundleUtil;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.core.utils.PlayerUtils;
import ca.tweetzy.flight.utils.QuickItem;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public final class GUIListingConfirm extends AuctionBaseGUI {

	private final AuctionedItem auctionedItem;
	private final Consumer<Boolean> result;

	private final Set<UUID> resulted = new HashSet<>();

	public GUIListingConfirm(Player player, AuctionedItem auctionedItem, Consumer<Boolean> result) {
		super(null, player, Settings.GUI_CONFIRM_LISTING_TITLE.getString(), 1);
		this.auctionedItem = auctionedItem;
		this.result = result;
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
			final AuctionPlayer auctionPlayer = AuctionHouse.getAuctionPlayerManager().getPlayer(close.player.getUniqueId());

			if (!this.resulted.contains(close.player.getUniqueId())) {
				if (auctionPlayer.getItemBeingListed() != null) {
					if (BundleUtil.isBundledItem(auctionedItem.getItem())) PlayerUtils.giveItem(close.player, BundleUtil.extractBundleItems(auctionedItem.getCleanItem()));
					else {
						PlayerUtils.giveItem(close.player, auctionedItem.getCleanItem());
					}

					auctionPlayer.setItemBeingListed(null);
				}
			}

			close.player.removeMetadata("AuctionHouseConfirmListing", AuctionHouse.getInstance());
			AuctionHouse.getAuctionPlayerManager().processSell(close.player);
		});
		draw();
	}

	@Override
	protected void draw() {
		for (int i = 0; i < 4; i++)
			drawYes(i);

		setItem(0, 4, this.auctionedItem.getDisplayStack(this.player, AuctionStackType.LISTING_PREVIEW));

		for (int i = 5; i < 9; i++)
			drawNo(i);
	}


	private void drawNo(int slot) {
		setButton(slot, QuickItem
				.of(Settings.GUI_CONFIRM_LISTING_NO_ITEM.getString())
				.name(Settings.GUI_CONFIRM_LISTING_NO_NAME.getString())
				.lore(this.player, Settings.GUI_CONFIRM_LISTING_NO_LORE.getStringList())
				.make(), click -> {

			if (resulted.contains(click.player.getUniqueId())) return;
			resulted.add(click.player.getUniqueId());

			setAllowClose(true);
			final AuctionPlayer auctionPlayer = AuctionHouse.getInstance().getAuctionPlayerManager().getPlayer(click.player.getUniqueId());
			this.result.accept(false);
			auctionPlayer.setItemBeingListed(CompMaterial.AIR.parseItem());
		});
	}

	private void drawYes(int slot) {
		setButton(slot, QuickItem
				.of(Settings.GUI_CONFIRM_LISTING_YES_ITEM.getString())
				.name(Settings.GUI_CONFIRM_LISTING_YES_NAME.getString())
				.lore(this.player, Settings.GUI_CONFIRM_LISTING_YES_LORE.getStringList())
				.make(), click -> {

			if (resulted.contains(click.player.getUniqueId())) return;
			resulted.add(click.player.getUniqueId());

			setAllowClose(true);
			this.result.accept(true);
		});
	}
}
