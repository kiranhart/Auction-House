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
import ca.tweetzy.auctionhouse.api.auction.ListingResult;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.auction.AuctionedItem;
import ca.tweetzy.auctionhouse.auction.ListingType;
import ca.tweetzy.auctionhouse.auction.enums.AuctionStackType;
import ca.tweetzy.auctionhouse.guis.AuctionBaseGUI;
import ca.tweetzy.auctionhouse.guis.confirmation.GUIListingConfirm;
import ca.tweetzy.auctionhouse.guis.core.GUIAuctionHouse;
import ca.tweetzy.auctionhouse.helpers.AuctionCreator;
import ca.tweetzy.auctionhouse.helpers.BundleUtil;
import ca.tweetzy.auctionhouse.helpers.input.TitleInput;
import ca.tweetzy.auctionhouse.model.MaterialCategorizer;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.gui.events.GuiClickEvent;
import ca.tweetzy.core.utils.NumberUtils;
import ca.tweetzy.core.utils.PlayerUtils;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.flight.utils.Replacer;
import lombok.NonNull;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.UUID;

public final class GUISellAuction extends AuctionBaseGUI {

	private final AuctionPlayer auctionPlayer;

	private double binPrice;
	private double startingBid;
	private final double bidIncrement;
	private final long listingTime;
	private boolean allowBuyNow;

	public GUISellAuction(@NonNull final AuctionPlayer auctionPlayer, final double binPrice, final double startingBid, final double bidIncrement, final long listingTime, final boolean allowBuyNow) {
		super(null, auctionPlayer.getPlayer(), Settings.GUI_SELL_AUCTION_TITLE.getString(), 6);
		this.auctionPlayer = auctionPlayer;
		this.binPrice = binPrice;
		this.startingBid = startingBid;
		this.bidIncrement = bidIncrement;
		this.listingTime = listingTime;
		this.allowBuyNow = allowBuyNow;

		setDefaultItem(QuickItem.bg(QuickItem.of(Settings.GUI_SELL_AUCTION_BG_ITEM.getString()).make()));

		setOnClose(close -> {
			final ItemStack itemToGive = this.auctionPlayer.getItemBeingListed();
			if (itemToGive != null)

				if (BundleUtil.isBundledItem(itemToGive)) {
					PlayerUtils.giveItem(close.player, BundleUtil.extractBundleItems(itemToGive));
				} else {
					PlayerUtils.giveItem(close.player, itemToGive);
				}

			this.auctionPlayer.setItemBeingListed(null);
		});

		draw();
	}

	@Override
	protected void draw() {
		reset();

		setButton(getRows() - 1, 0, getBackButton(), click -> {

			click.gui.close();
			click.manager.showGUI(click.player, new GUISellPlaceItem(this.auctionPlayer, BundleUtil.isBundledItem(this.auctionPlayer.getItemBeingListed()) ? GUISellPlaceItem.ViewMode.BUNDLE_ITEM : GUISellPlaceItem.ViewMode.SINGLE_ITEM, ListingType.BIN));
		});

		if (Settings.ALLOW_PLAYERS_TO_DEFINE_AUCTION_TIME.getBoolean()) {

			final long[] times = AuctionAPI.getInstance().getRemainingTimeValues(this.listingTime);

			setButton(3, 1, QuickItem
					.of(Settings.GUI_SELL_AUCTION_ITEM_ITEMS_TIME_ITEM.getString())
					.name(Settings.GUI_SELL_AUCTION_ITEM_ITEMS_TIME_NAME.getString())
					.lore(Replacer.replaceVariables(Settings.GUI_SELL_AUCTION_ITEM_ITEMS_TIME_LORE.getStringList(),
							"remaining_days", times[0],
							"remaining_hours", times[1],
							"remaining_minutes", times[2],
							"remaining_seconds", times[3]
					)).make(), click -> {

				click.gui.exit();
				new TitleInput(
						click.player,
						AuctionHouse.getInstance().getLocale().getMessage("titles.listing time.title").getMessage(),
						AuctionHouse.getInstance().getLocale().getMessage("titles.listing time.subtitle").getMessage(),
						AuctionHouse.getInstance().getLocale().getMessage("titles.listing time.actionbar").getMessage()
				) {

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
											AuctionAPI.toTicks(string),
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

		if (this.allowBuyNow)
			if (Settings.ALLOW_USAGE_OF_BUY_NOW_SYSTEM.getBoolean())
				setButton(3, 4, QuickItem
						.of(Settings.GUI_SELL_AUCTION_ITEM_ITEMS_BUYOUT_PRICE_ITEM.getString())
						.name(Settings.GUI_SELL_AUCTION_ITEM_ITEMS_BUYOUT_PRICE_NAME.getString())
						.lore(Replacer.replaceVariables(Settings.GUI_SELL_AUCTION_ITEM_ITEMS_BUYOUT_PRICE_LORE.getStringList(), "listing_bin_price", AuctionAPI.getInstance().formatNumber(binPrice)))
						.make(), click -> {

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
								AuctionHouse.getInstance().getLocale().getMessage("general.notanumber").processPlaceholder("value", string).sendPrefixedMessage(player);
								return false;
							}

							double listingAmount = Double.parseDouble(string);
							if (Double.isNaN(listingAmount)) {
								AuctionHouse.getInstance().getLocale().getMessage("general.notanumber").processPlaceholder("value", string).sendPrefixedMessage(player);
								return false;
							}

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

		setButton(3, 3, QuickItem
				.of(Settings.GUI_SELL_AUCTION_ITEM_ITEMS_STARTING_PRICE_ITEM.getString())
				.name(Settings.GUI_SELL_AUCTION_ITEM_ITEMS_STARTING_PRICE_NAME.getString())
				.lore(Replacer.replaceVariables(Settings.GUI_SELL_AUCTION_ITEM_ITEMS_STARTING_PRICE_LORE.getStringList(), "listing_start_price", AuctionAPI.getInstance().formatNumber(startingBid)))
				.make(), click -> {

			click.gui.exit();
			new TitleInput(click.player, AuctionHouse.getInstance().getLocale().getMessage("titles.starting bid price.title").getMessage(), AuctionHouse.getInstance().getLocale().getMessage("titles.starting bid price.subtitle").getMessage()) {

				@Override
				public void onExit(Player player) {
					click.manager.showGUI(player, GUISellAuction.this);
				}

				@Override
				public boolean onResult(String string) {
					string = ChatColor.stripColor(string);

					if (!NumberUtils.isDouble(string)) {
						AuctionHouse.getInstance().getLocale().getMessage("general.notanumber").processPlaceholder("value", string).sendPrefixedMessage(player);
						return false;
					}

					double listingAmount = Double.parseDouble(string);
					if (Double.isNaN(listingAmount)) {
						AuctionHouse.getInstance().getLocale().getMessage("general.notanumber").processPlaceholder("value", string).sendPrefixedMessage(player);
						return false;
					}

					if (Settings.ALLOW_USAGE_OF_BUY_NOW_SYSTEM.getBoolean() && GUISellAuction.this.allowBuyNow)
						if (Settings.BASE_PRICE_MUST_BE_HIGHER_THAN_BID_START.getBoolean() && listingAmount >= GUISellAuction.this.binPrice) {
//							listingAmount = GUISellAuction.this.binPrice / 2 <= 0 ? Settings.MIN_AUCTION_START_PRICE.getDouble() : GUISellAuction.this.binPrice / 2;
							AuctionHouse.getInstance().getLocale().getMessage("pricing.startingpricetoohigh").processPlaceholder("price", AuctionAPI.getInstance().formatNumber(GUISellAuction.this.binPrice)).sendPrefixedMessage(player);

							return false;
						}

					if (listingAmount < Settings.MIN_AUCTION_START_PRICE.getDouble())
						listingAmount = Settings.MIN_AUCTION_START_PRICE.getDouble();

					if (listingAmount > Settings.MAX_AUCTION_START_PRICE.getDouble())
						listingAmount = Settings.MAX_AUCTION_START_PRICE.getDouble();


					click.manager.showGUI(click.player, new GUISellAuction(
							GUISellAuction.this.auctionPlayer,
							GUISellAuction.this.binPrice,
							listingAmount,
							GUISellAuction.this.bidIncrement,
							GUISellAuction.this.listingTime,
							GUISellAuction.this.allowBuyNow
					));

					return true;
				}
			};
		});

		setButton(3, 5, QuickItem
				.of(Settings.GUI_SELL_AUCTION_ITEM_ITEMS_INCREMENT_PRICE_ITEM.getString())
				.name(Settings.GUI_SELL_AUCTION_ITEM_ITEMS_INCREMENT_PRICE_NAME.getString())
				.lore(Replacer.replaceVariables(Settings.GUI_SELL_AUCTION_ITEM_ITEMS_INCREMENT_PRICE_LORE.getStringList(), "listing_increment_price", AuctionAPI.getInstance().formatNumber(bidIncrement)))
				.make(), click -> {

			click.gui.exit();
			new TitleInput(click.player, AuctionHouse.getInstance().getLocale().getMessage("titles.bid increment price.title").getMessage(), AuctionHouse.getInstance().getLocale().getMessage("titles.bid increment price.subtitle").getMessage()) {

				@Override
				public void onExit(Player player) {
					click.manager.showGUI(player, GUISellAuction.this);
				}

				@Override
				public boolean onResult(String string) {
					string = ChatColor.stripColor(string);

					if (!NumberUtils.isDouble(string)) {
						AuctionHouse.getInstance().getLocale().getMessage("general.notanumber").processPlaceholder("value", string).sendPrefixedMessage(player);
						return false;
					}

					double listingAmount = Double.parseDouble(string);
					if (Double.isNaN(listingAmount)) {
						AuctionHouse.getInstance().getLocale().getMessage("general.notanumber").processPlaceholder("value", string).sendPrefixedMessage(player);
						return false;
					}

					if (listingAmount < Settings.MIN_AUCTION_INCREMENT_PRICE.getDouble())
						listingAmount = Settings.MIN_AUCTION_INCREMENT_PRICE.getDouble();

					if (listingAmount > Settings.MAX_AUCTION_INCREMENT_PRICE.getDouble())
						listingAmount = Settings.MAX_AUCTION_INCREMENT_PRICE.getDouble();

					click.manager.showGUI(click.player, new GUISellAuction(
							GUISellAuction.this.auctionPlayer,
							GUISellAuction.this.binPrice,
							GUISellAuction.this.startingBid,
							listingAmount,
							GUISellAuction.this.listingTime,
							GUISellAuction.this.allowBuyNow
					));

					return true;
				}
			};
		});

		drawAuctionItem();
		drawBuyoutToggle();

		setButton(getRows() - 1, 4, QuickItem
				.of(Settings.GUI_SELL_AUCTION_ITEM_ITEMS_CONTINUE_ITEM.getString())
				.name(Settings.GUI_SELL_AUCTION_ITEM_ITEMS_CONTINUE_NAME.getString())
				.lore(Settings.GUI_SELL_AUCTION_ITEM_ITEMS_CONTINUE_LORE.getStringList())
				.make(), click -> {

			if (!AuctionAPI.getInstance().meetsListingRequirements(click.player, this.auctionPlayer.getItemBeingListed())) return;
			if (!auctionPlayer.canListItem()) return;

			click.gui.exit();

			// do listing confirmation first
			if (Settings.ASK_FOR_LISTING_CONFIRMATION.getBoolean()) {
				click.manager.showGUI(click.player, new GUIListingConfirm(click.player, createListingItem(), confirmed -> {
					if (confirmed)
						performAuctionListing(click);
					else {
						click.player.closeInventory();
						PlayerUtils.giveItem(click.player, this.auctionPlayer.getItemBeingListed());
					}
				}));
				return;
			}

			performAuctionListing(click);
		});
	}

	private void drawBuyoutToggle() {
		if (Settings.ALLOW_USAGE_OF_BUY_NOW_SYSTEM.getBoolean()) {
			setButton(3, 7, QuickItem
					.of(this.allowBuyNow ? Settings.GUI_SELL_AUCTION_ITEM_ITEMS_BUYOUT_ENABLED_ITEM.getString() : Settings.GUI_SELL_AUCTION_ITEM_ITEMS_BUYOUT_DISABLED_ITEM.getString())
					.name(this.allowBuyNow ? Settings.GUI_SELL_AUCTION_ITEM_ITEMS_BUYOUT_ENABLED_NAME.getString() : Settings.GUI_SELL_AUCTION_ITEM_ITEMS_BUYOUT_DISABLED_NAME.getString())
					.lore(this.allowBuyNow ? Settings.GUI_SELL_AUCTION_ITEM_ITEMS_BUYOUT_ENABLED_LORE.getStringList() : Settings.GUI_SELL_AUCTION_ITEM_ITEMS_BUYOUT_DISABLED_LORE.getStringList())
					.make(), e -> {

				this.allowBuyNow = !allowBuyNow;

				if (this.allowBuyNow && this.binPrice <= this.startingBid) {
					this.binPrice = Settings.MIN_AUCTION_PRICE.getDouble();
					this.startingBid = Settings.MIN_AUCTION_START_PRICE.getDouble();
				}

				draw();
			});
		}
	}

	private void performAuctionListing(GuiClickEvent click) {
		AuctionCreator.create(this.auctionPlayer, createListingItem(), (originalListing, listingResult) -> {
			if (listingResult != ListingResult.SUCCESS) {
				click.player.closeInventory();
				PlayerUtils.giveItem(click.player, originalListing.getItem());
				this.auctionPlayer.setItemBeingListed(null);
				return;
			}

			if (Settings.OPEN_MAIN_AUCTION_HOUSE_AFTER_MENU_LIST.getBoolean()) {
				player.removeMetadata("AuctionHouseConfirmListing", AuctionHouse.getInstance());
				click.manager.showGUI(click.player, new GUIAuctionHouse(this.auctionPlayer));
			} else
				AuctionHouse.newChain().sync(click.player::closeInventory).execute();
		});
	}

	private void drawAuctionItem() {
		setItem(1, 4, createListingItem().getDisplayStack(AuctionStackType.LISTING_PREVIEW));
	}

	private AuctionedItem createListingItem() {
		return new AuctionedItem(
				UUID.randomUUID(),
				auctionPlayer.getUuid(),
				auctionPlayer.getUuid(),
				auctionPlayer.getPlayer().getName(),
				auctionPlayer.getPlayer().getName(),
				MaterialCategorizer.getMaterialCategory(this.auctionPlayer.getItemBeingListed()),
				this.auctionPlayer.getItemBeingListed(),
				this.allowBuyNow ? this.binPrice : -1,
				this.startingBid,
				this.bidIncrement,
				this.startingBid,
				true, false,
				System.currentTimeMillis() + (this.listingTime * 1000L)
		);
	}
}
