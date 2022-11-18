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

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.auction.AuctionedItem;
import ca.tweetzy.auctionhouse.auction.enums.AuctionStackType;
import ca.tweetzy.auctionhouse.guis.AbstractPlaceholderGui;
import ca.tweetzy.auctionhouse.helpers.MaterialCategorizer;
import ca.tweetzy.auctionhouse.helpers.input.TitleInput;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.gui.GuiUtils;
import ca.tweetzy.core.utils.NumberUtils;
import ca.tweetzy.core.utils.PlayerUtils;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.flight.utils.Replacer;
import lombok.NonNull;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public final class GUISellAuction extends AbstractPlaceholderGui {

	private final AuctionPlayer auctionPlayer;

	private double binPrice;
	private double startingBid;
	private double bidIncrement;
	private long listingTime;
	private boolean allowBuyNow;

	public GUISellAuction(@NonNull final AuctionPlayer auctionPlayer, final double binPrice, final double startingBid, final double bidIncrement, final long listingTime, final boolean allowBuyNow) {
		super(auctionPlayer);
		this.auctionPlayer = auctionPlayer;
		this.binPrice = binPrice;
		this.startingBid = startingBid;
		this.bidIncrement = bidIncrement;
		this.listingTime = listingTime;
		this.allowBuyNow = allowBuyNow;

		setTitle(Settings.GUI_SELL_AUCTION_TITLE.getString());
		setDefaultItem(GuiUtils.createButtonItem(Settings.GUI_SELL_AUCTION_BG_ITEM.getMaterial(), " "));
		setRows(6);

		setOnClose(close -> PlayerUtils.giveItem(close.player, this.auctionPlayer.getItemBeingListed()));

		draw();
	}

	private void draw() {
		reset();

		if (Settings.ALLOW_PLAYERS_TO_DEFINE_AUCTION_TIME.getBoolean()) {
			setButton(3, 1, QuickItem
					.of(Objects.requireNonNull(Settings.GUI_SELL_AUCTION_ITEM_ITEMS_TIME_ITEM.getMaterial().parseItem()))
					.name(Settings.GUI_SELL_AUCTION_ITEM_ITEMS_TIME_NAME.getString())
					.lore(Settings.GUI_SELL_AUCTION_ITEM_ITEMS_TIME_LORE.getStringList()).make(), click -> {

				click.gui.exit();
				new TitleInput(click.player, AuctionHouse.getInstance().getLocale().getMessage("titles.listing time.title").getMessage(), AuctionHouse.getInstance().getLocale().getMessage("titles.listing time.subtitle").getMessage()) {

					@Override
					public void onExit(Player player) {
						click.manager.showGUI(player, GUISellAuction.this);
					}

					@Override
					public boolean onResult(String string) {
						string = ChatColor.stripColor(string);

						String[] parts = ChatColor.stripColor(string).split(" ");
						if (parts.length == 2) {
							if (NumberUtils.isInt(parts[0]) && Arrays.asList("second", "minute", "hour", "day", "week", "month", "year").contains(parts[1].toLowerCase())) {
								if (AuctionAPI.toTicks(string) <= Settings.MAX_CUSTOM_DEFINED_TIME.getInt()) {
									click.manager.showGUI(click.player, new GUISellAuction(
											GUISellAuction.this.auctionPlayer,
											GUISellAuction.this.binPrice,
											GUISellAuction.this.startingBid,
											GUISellAuction.this.bidIncrement,
											System.currentTimeMillis() + (1000L * AuctionAPI.toTicks(string)),
											GUISellAuction.this.allowBuyNow
									));
									return true;
								}
							}
						}

						return false;
					}
				};
			});
		}

		setButton(3, 4, QuickItem
				.of(Objects.requireNonNull(Settings.GUI_SELL_BIN_ITEM_ITEMS_PRICE_ITEM.getMaterial().parseItem()))
				.name(Settings.GUI_SELL_BIN_ITEM_ITEMS_PRICE_NAME.getString())
				.lore(Replacer.replaceVariables(Settings.GUI_SELL_BIN_ITEM_ITEMS_PRICE_LORE.getStringList(), "listing_bin_price", AuctionAPI.getInstance().formatNumber(this.binPrice))).make(), click -> {

			click.gui.exit();
			new TitleInput(click.player, AuctionHouse.getInstance().getLocale().getMessage("titles.buy now price.title").getMessage(), AuctionHouse.getInstance().getLocale().getMessage("titles.buy now price.subtitle").getMessage()) {

				@Override
				public void onExit(Player player) {
					click.manager.showGUI(player, GUISellAuction.this);
				}

				@Override
				public boolean onResult(String string) {
					string = ChatColor.stripColor(string);

					if (!NumberUtils.isDouble(string)) {
						AuctionHouse.getInstance().getLocale().getMessage("general.notanumber").sendPrefixedMessage(player);
						return false;
					}

					double listingAmount = Double.parseDouble(string);

					if (listingAmount < Settings.MIN_AUCTION_PRICE.getDouble())
						listingAmount = Settings.MIN_AUCTION_PRICE.getDouble();

					if (listingAmount > Settings.MAX_AUCTION_PRICE.getDouble())
						listingAmount = Settings.MAX_AUCTION_PRICE.getDouble();

					click.manager.showGUI(click.player, new GUISellAuction(
							GUISellAuction.this.auctionPlayer,
							listingAmount,
							GUISellAuction.this.startingBid,
							GUISellAuction.this.bidIncrement,
							GUISellAuction.this.listingTime,
							GUISellAuction.this.allowBuyNow
					));

					return true;
				}
			};
		});

		setItem(1, 4, new AuctionedItem(
				UUID.randomUUID(),
				auctionPlayer.getUuid(),
				auctionPlayer.getUuid(),
				auctionPlayer.getPlayer().getName(),
				auctionPlayer.getPlayer().getName(),
				MaterialCategorizer.getMaterialCategory(this.auctionPlayer.getItemBeingListed()),
				this.auctionPlayer.getItemBeingListed(),
				this.binPrice,
				0,
				0,
				this.binPrice,
				false, false,
				this.listingTime
		).getDisplayStack(AuctionStackType.LISTING_PREVIEW));

		setButton(getRows() - 1, 4, QuickItem
				.of(Objects.requireNonNull(Settings.GUI_SELL_AUCTION_ITEM_ITEMS_CONTINUE_ITEM.getMaterial().parseItem()))
				.name(Settings.GUI_SELL_AUCTION_ITEM_ITEMS_CONTINUE_NAME.getString())
				.lore(Settings.GUI_SELL_AUCTION_ITEM_ITEMS_CONTINUE_LORE.getStringList())
				.make(), click -> {

		});
	}
}
