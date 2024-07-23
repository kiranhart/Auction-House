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
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.auction.MinItemPrice;
import ca.tweetzy.auctionhouse.guis.AuctionPagedGUI;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.gui.events.GuiClickEvent;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.flight.utils.Replacer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

/**
 * Date Created: April 04 2022
 * Time Created: 8:21 a.m.
 *
 * @author Kiran Hart
 */
public final class GUIMinItemPrices extends AuctionPagedGUI<MinItemPrice> {

	public GUIMinItemPrices(Player player) {
		super(null, player, Settings.GUI_MIN_ITEM_PRICES_TITLE.getString(), 6, AuctionHouse.getInstance().getMinItemPriceManager().getMinPrices());
		draw();
	}

	@Override
	protected void drawFixed() {
		applyBackExit();
	}

	@Override
	protected ItemStack makeDisplayItem(MinItemPrice minItemPrice) {
		final List<String> lore = AuctionAPI.getInstance().getItemLore(minItemPrice.getItemStack().clone());
		lore.addAll(Settings.GUI_MIN_ITEM_PRICES_LORE.getStringList());

		return QuickItem
				.of(minItemPrice.getItemStack().clone())
				.name(AuctionAPI.getInstance().getItemName(minItemPrice.getItemStack()))
				.lore(this.player,Replacer.replaceVariables(lore, "price", AuctionAPI.getInstance().formatNumber(minItemPrice.getPrice())))
				.make();
	}

	@Override
	protected void onClick(MinItemPrice minItemPrice, GuiClickEvent event) {
		AuctionHouse.getInstance().getDataManager().deleteMinItemPrice(Collections.singletonList(minItemPrice.getUuid()));
		AuctionHouse.getInstance().getMinItemPriceManager().removeItem(minItemPrice);
		event.manager.showGUI(event.player, new GUIMinItemPrices(event.player));
	}
}
