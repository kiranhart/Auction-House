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
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.auction.MinItemPrice;
import ca.tweetzy.auctionhouse.helpers.ConfigurationItemHelper;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.utils.TextUtils;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Date Created: April 04 2022
 * Time Created: 8:21 a.m.
 *
 * @author Kiran Hart
 */
public final class GUIMinItemPrices extends AbstractPlaceholderGui {

	final List<MinItemPrice> minPrices;

	public GUIMinItemPrices(Player player) {
		super(player);
		this.minPrices = AuctionHouse.getInstance().getMinItemPriceManager().getMinPrices();
		setTitle(TextUtils.formatText(Settings.GUI_MIN_ITEM_PRICES_TITLE.getString()));
		setRows(6);
		setAcceptsItems(false);
		draw();
	}

	private void draw() {
		reset();

		pages = (int) Math.max(1, Math.ceil(this.minPrices.size() / (double) 45));

		setPrevPage(5, 3, getPreviousPageItem());
		setNextPage(5, 5, getNextPageItem());
		setOnPage(e -> draw());

		int slot = 0;
		final List<MinItemPrice> data = this.minPrices.stream().skip((page - 1) * 45L).limit(45).collect(Collectors.toList());

		for (MinItemPrice minItemPrice : data) {

			final List<String> lore = AuctionAPI.getInstance().getItemLore(minItemPrice.getItemStack());
			lore.addAll(Settings.GUI_MIN_ITEM_PRICES_LORE.getStringList());

			setButton(slot++, ConfigurationItemHelper.createConfigurationItem(this.player, minItemPrice.getItemStack(), AuctionAPI.getInstance().getItemName(minItemPrice.getItemStack()), lore, new HashMap<String, Object>() {{
				put("%price%", AuctionAPI.getInstance().formatNumber(minItemPrice.getPrice()));
			}}), click -> {
				AuctionHouse.getInstance().getDataManager().deleteMinItemPrice(Collections.singletonList(minItemPrice.getUuid()));
				AuctionHouse.getInstance().getMinItemPriceManager().removeItem(minItemPrice);
				click.manager.showGUI(click.player, new GUIMinItemPrices(click.player));
			});
		}
	}
}
