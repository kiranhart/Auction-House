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

import ca.tweetzy.auctionhouse.auction.AuctionedItem;
import ca.tweetzy.auctionhouse.auction.enums.AuctionStackType;
import ca.tweetzy.auctionhouse.guis.AbstractPlaceholderGui;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.core.utils.items.TItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.Objects;
import java.util.function.Consumer;

public final class GUIListingConfirm extends AbstractPlaceholderGui {

	private final AuctionedItem auctionedItem;
	private final Consumer<Boolean> result;

	public GUIListingConfirm(Player player, AuctionedItem auctionedItem, Consumer<Boolean> result) {
		super(player);
		this.auctionedItem = auctionedItem;
		this.result = result;
		super.setTitle(TextUtils.formatText(Settings.GUI_CONFIRM_LISTING_TITLE.getString()));
		setAcceptsItems(false);
		setAllowClose(false);
		setRows(1);
		draw();
	}

	private void draw() {
		setItems(0, 3, new TItemBuilder(Objects.requireNonNull(Settings.GUI_CONFIRM_LISTING_YES_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_CONFIRM_LISTING_YES_NAME.getString()).setLore(Settings.GUI_CONFIRM_LISTING_YES_LORE.getStringList()).toItemStack());
		setItem(0, 4, this.auctionedItem.getDisplayStack(AuctionStackType.LISTING_PREVIEW));
		setItems(5, 8, new TItemBuilder(Objects.requireNonNull(Settings.GUI_CONFIRM_LISTING_NO_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_CONFIRM_LISTING_NO_NAME.getString()).setLore(Settings.GUI_CONFIRM_LISTING_NO_LORE.getStringList()).toItemStack());

		setActionForRange(5, 8, ClickType.LEFT, e -> {
			setAllowClose(true);
			this.result.accept(false);
		});
		setActionForRange(0, 3, ClickType.LEFT, e -> {
			setAllowClose(true);
			this.result.accept(true);
		});
	}

}
