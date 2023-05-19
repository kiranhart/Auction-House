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
import ca.tweetzy.auctionhouse.guis.AbstractPlaceholderGui;
import ca.tweetzy.auctionhouse.helpers.ConfigurationItemHelper;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.utils.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public final class GUIAdminExpired extends AbstractPlaceholderGui {

	final OfflinePlayer targetUser;
	List<AuctionedItem> items;

	public GUIAdminExpired(Player viewer, OfflinePlayer targetUser) {
		super(viewer);
		this.targetUser = targetUser;
		this.items = AuctionHouse.getInstance().getAuctionItemManager().getItems().values().stream().filter(item -> item.isExpired() && item.getOwner().equals(targetUser.getUniqueId())).collect(Collectors.toList());
		setTitle(Settings.GUI_EXPIRED_ITEMS_ADMIN_TITLE.getString());
		setRows(6);
		setAcceptsItems(false);
		draw();
	}

	private void draw() {
		reset();

		setButton(5, 4, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_CLOSE_BTN_ITEM.getString(), Settings.GUI_CLOSE_BTN_NAME.getString(), Settings.GUI_CLOSE_BTN_LORE.getStringList(), null), e -> e.gui.close());

		AuctionHouse.newChain().asyncFirst(() -> this.items.stream().sorted(Comparator.comparingLong(AuctionedItem::getExpiresAt).reversed()).skip((page - 1) * 45L).limit(45).collect(Collectors.toList())).asyncLast(data -> {
			pages = (int) Math.max(1, Math.ceil(this.items.size() / (double) 45));
			setPrevPage(5, 3, getPreviousPageItem());
			setNextPage(5, 5, getNextPageItem());

			setOnPage(e -> {
				draw();
			});

			int slot = 0;
			for (AuctionedItem auctionItem : data) {
				ItemStack item = auctionItem.getItem().clone();

				ItemMeta meta = item.hasItemMeta() ? item.getItemMeta() : Bukkit.getItemFactory().getItemMeta(item.getType());
				List<String> lore = (meta.hasLore()) ? meta.getLore() : new ArrayList<>();

				lore.addAll(TextUtils.formatText(Settings.GUI_EXPIRED_ITEMS_ADMIN_ITEMS_LORE.getStringList()));
				meta.setLore(lore);

				item.setItemMeta(meta);

				setButton(slot++, item, ClickType.LEFT, e -> {
					AuctionHouse.getInstance().getAuctionItemManager().sendToGarbage(auctionItem);
					e.manager.showGUI(e.player, new GUIAdminExpired(e.player, this.targetUser));
				});
			}

		}).execute();
	}
}
