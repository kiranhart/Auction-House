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

package ca.tweetzy.auctionhouse.guis.admin;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.auction.AuctionedItem;
import ca.tweetzy.auctionhouse.guis.AuctionPagedGUI;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.flight.gui.events.GuiClickEvent;
import ca.tweetzy.flight.utils.QuickItem;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Comparator;
import java.util.stream.Collectors;

public final class GUIAdminExpired extends AuctionPagedGUI<AuctionedItem> {

	final OfflinePlayer targetUser;

	public GUIAdminExpired(Player viewer, OfflinePlayer targetUser) {
		super(null, viewer, Settings.GUI_EXPIRED_ITEMS_ADMIN_TITLE.getString(), 6, AuctionHouse.getInstance().getAuctionItemManager().getItems().values().stream().filter(item -> item.isExpired() && item.getOwner().equals(targetUser.getUniqueId()) && !AuctionHouse.getInstance().getAuctionItemManager().getGarbageBin().containsKey(item.getId())).collect(Collectors.toList()));
		this.targetUser = targetUser;
		setAcceptsItems(false);
		draw();
	}

	@Override
	protected void drawFixed() {
		applyBackExit();
	}

	@Override
	protected void prePopulate() {
		this.items.sort(Comparator.comparing(AuctionedItem::getExpiresAt).reversed());
	}

	@Override
	protected ItemStack makeDisplayItem(AuctionedItem auctionedItem) {
		final ItemStack item = auctionedItem.getItem().clone();
		return QuickItem.of(item).lore(this.player, Settings.GUI_EXPIRED_ITEMS_ADMIN_ITEMS_LORE.getStringList()).make();
	}

	@Override
	protected void onClick(AuctionedItem item, GuiClickEvent event) {
		AuctionHouse.getInstance().getAuctionItemManager().sendToGarbage(item);
		event.manager.showGUI(event.player, new GUIAdminExpired(event.player, this.targetUser));
	}
}
