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
import ca.tweetzy.auctionhouse.api.auction.ListingPriceLimit;
import ca.tweetzy.auctionhouse.api.sync.SynchronizeResult;
import ca.tweetzy.auctionhouse.guis.AuctionPagedGUI;
import ca.tweetzy.auctionhouse.helpers.input.TitleInput;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.flight.utils.MathUtil;
import ca.tweetzy.flight.gui.events.GuiClickEvent;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.flight.utils.Replacer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Date Created: April 04 2022
 * Time Created: 8:21 a.m.
 *
 * @author Kiran Hart
 */
public final class GUIPriceLimits extends AuctionPagedGUI<ListingPriceLimit> {

	public GUIPriceLimits(Player player) {
		super(null, player, Settings.GUI_PRICE_LIMITS_TITLE.getString(), 6, AuctionHouse.getPriceLimitManager().getManagerContent());
		draw();
	}

	@Override
	protected void drawFixed() {
		applyBackExit();
	}

	@Override
	protected ItemStack makeDisplayItem(ListingPriceLimit listingPriceLimit) {
		final List<String> lore = AuctionAPI.getInstance().getItemLore(listingPriceLimit.getItem().clone());
		lore.addAll(Settings.GUI_PRICE_LIMITS_LORE.getStringList());

		return QuickItem
				.of(listingPriceLimit.getItem().clone())
				.name(AuctionAPI.getInstance().getItemName(listingPriceLimit.getItem()))
				.lore(this.player, Replacer.replaceVariables(lore,
						"min_price", AuctionHouse.getAPI().getNumberAsCurrency(listingPriceLimit.getMinPrice(), false),
						"max_price", AuctionHouse.getAPI().getNumberAsCurrency(listingPriceLimit.getMaxPrice(), false)
				))
				.make();
	}

	@Override
	protected void onClick(ListingPriceLimit listingPriceLimit, GuiClickEvent event) {

		if (event.clickType == ClickType.LEFT) {
			new TitleInput(event.player, AuctionHouse.getInstance().getLocale().getMessage("titles.price limit.min.title").getMessage(), AuctionHouse.getInstance().getLocale().getMessage("titles.price limit.min.subtitle").getMessage()) {

				@Override
				public void onExit(Player player) {
					event.manager.showGUI(player, GUIPriceLimits.this);
				}

				@Override
				public boolean onResult(String string) {
					string = ChatColor.stripColor(string);

					if (!MathUtil.isDouble(string)) {
						AuctionHouse.getInstance().getLocale().getMessage("general.notanumber").processPlaceholder("value", string).sendPrefixedMessage(player);
						return false;
					}

					double newPrice = Double.parseDouble(string);

					if (Double.isNaN(newPrice)) {
						AuctionHouse.getInstance().getLocale().getMessage("general.notanumber").processPlaceholder("value", string).sendPrefixedMessage(player);
						return false;
					}

					listingPriceLimit.setMinPrice(newPrice);
					listingPriceLimit.sync(success -> {
						if (success)
							event.manager.showGUI(event.player, new GUIPriceLimits(event.player));
					});

					return true;
				}
			};
		}

		if (event.clickType == ClickType.RIGHT) {
			new TitleInput(event.player, AuctionHouse.getInstance().getLocale().getMessage("titles.price limit.max.title").getMessage(), AuctionHouse.getInstance().getLocale().getMessage("titles.price limit.max.subtitle").getMessage()) {

				@Override
				public void onExit(Player player) {
					event.manager.showGUI(player, GUIPriceLimits.this);
				}

				@Override
				public boolean onResult(String string) {
					string = ChatColor.stripColor(string);

					if (!MathUtil.isDouble(string)) {
						AuctionHouse.getInstance().getLocale().getMessage("general.notanumber").processPlaceholder("value", string).sendPrefixedMessage(player);
						return false;
					}

					double newPrice = Double.parseDouble(string);

					if (Double.isNaN(newPrice)) {
						AuctionHouse.getInstance().getLocale().getMessage("general.notanumber").processPlaceholder("value", string).sendPrefixedMessage(player);
						return false;
					}

					listingPriceLimit.setMaxPrice(newPrice);
					listingPriceLimit.sync(success -> {
						if (success)
							event.manager.showGUI(event.player, new GUIPriceLimits(event.player));
					});

					return true;
				}
			};
		}

		if (event.clickType == ClickType.DROP) {
			listingPriceLimit.unStore(result -> {
				if (result == SynchronizeResult.SUCCESS)
					event.manager.showGUI(event.player, new GUIPriceLimits(event.player));
			});
		}

	}
}
