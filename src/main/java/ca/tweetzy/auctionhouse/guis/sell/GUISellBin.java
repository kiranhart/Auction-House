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
import ca.tweetzy.auctionhouse.guis.GUIAuctionHouse;
import ca.tweetzy.auctionhouse.guis.abstraction.AuctionBaseGUI;
import ca.tweetzy.auctionhouse.guis.confirmation.GUIListingConfirm;
import ca.tweetzy.auctionhouse.helpers.AuctionCreator;
import ca.tweetzy.auctionhouse.helpers.BundleUtil;
import ca.tweetzy.auctionhouse.model.MaterialCategorizer;
import ca.tweetzy.auctionhouse.helpers.input.TitleInput;
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

public final class GUISellBin extends AuctionBaseGUI {

	private final AuctionPlayer auctionPlayer;

	private final double listingPrice;
	private final long listingTime;
	private boolean allowPartialBuy;

	public GUISellBin(@NonNull final AuctionPlayer auctionPlayer, final double listingPrice, final long listingTime, boolean allowPartialBuy) {
		super(null, auctionPlayer.getPlayer(), Settings.GUI_SELL_BIN_TITLE.getString(), 6);
		this.auctionPlayer = auctionPlayer;
		this.listingPrice = listingPrice;
		this.listingTime = listingTime;
		this.allowPartialBuy = allowPartialBuy;
		setDefaultItem(QuickItem.bg(QuickItem.of(Settings.GUI_SELL_BIN_BG_ITEM.getString()).make()));

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
					.of(Settings.GUI_SELL_BIN_ITEM_ITEMS_TIME_ITEM.getString())
					.name(Settings.GUI_SELL_BIN_ITEM_ITEMS_TIME_NAME.getString())
					.lore(Replacer.replaceVariables(Settings.GUI_SELL_BIN_ITEM_ITEMS_TIME_LORE.getStringList(),
							"remaining_days", times[0],
							"remaining_hours", times[1],
							"remaining_minutes", times[2],
							"remaining_seconds", times[3]
					)).make(), click -> {

				click.gui.exit();
				new TitleInput(click.player, AuctionHouse.getInstance().getLocale().getMessage("titles.listing time.title").getMessage(), AuctionHouse.getInstance().getLocale().getMessage("titles.listing time.subtitle").getMessage(), AuctionHouse.getInstance().getLocale().getMessage("titles.listing time.actionbar").getMessage()) {


					@Override
					public void onExit(Player player) {
						click.manager.showGUI(player, GUISellBin.this);
					}

					@Override
					public boolean onResult(String string) {
						string = ChatColor.stripColor(string);

						String[] parts = ChatColor.stripColor(string).split(" ");
						if (parts.length == 2) {
							if (NumberUtils.isInt(parts[0]) && Arrays.asList("second", "minute", "hour", "day", "week", "month", "year").contains(parts[1].toLowerCase())) {
								if (AuctionAPI.toTicks(string) <= Settings.MAX_CUSTOM_DEFINED_TIME.getInt()) {
									click.manager.showGUI(click.player, new GUISellBin(GUISellBin.this.auctionPlayer, GUISellBin.this.listingPrice, AuctionAPI.toTicks(string), GUISellBin.this.allowPartialBuy));
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
				.of(Settings.GUI_SELL_BIN_ITEM_ITEMS_PRICE_ITEM.getString())
				.name(Settings.GUI_SELL_BIN_ITEM_ITEMS_PRICE_NAME.getString())
				.lore(Replacer.replaceVariables(Settings.GUI_SELL_BIN_ITEM_ITEMS_PRICE_LORE.getStringList(), "listing_bin_price", AuctionAPI.getInstance().formatNumber(listingPrice)))
				.make(), click -> {

			click.gui.exit();
			new TitleInput(click.player, AuctionHouse.getInstance().getLocale().getMessage("titles.buy now price.title").getMessage(), AuctionHouse.getInstance().getLocale().getMessage("titles.buy now price.subtitle").getMessage()) {

				@Override
				public void onExit(Player player) {
					click.manager.showGUI(player, GUISellBin.this);
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

					if (listingAmount < Settings.MIN_AUCTION_PRICE.getDouble()) listingAmount = Settings.MIN_AUCTION_PRICE.getDouble();
					if (listingAmount > Settings.MAX_AUCTION_PRICE.getDouble()) listingAmount = Settings.MAX_AUCTION_PRICE.getDouble();

					click.manager.showGUI(click.player, new GUISellBin(GUISellBin.this.auctionPlayer, listingAmount, GUISellBin.this.listingTime, GUISellBin.this.allowPartialBuy));

					return true;
				}
			};
		});

		drawQtyPurchase();

		setItem(1, 4, createListingItem().getDisplayStack(AuctionStackType.LISTING_PREVIEW));

		setButton(getRows() - 1, 4, QuickItem
				.of(Settings.GUI_SELL_BIN_ITEM_ITEMS_CONTINUE_ITEM.getString())
				.name(Settings.GUI_SELL_BIN_ITEM_ITEMS_CONTINUE_NAME.getString())
				.lore(Settings.GUI_SELL_BIN_ITEM_ITEMS_CONTINUE_LORE.getStringList())
				.make(), click -> {

			if (!AuctionAPI.getInstance().meetsListingRequirements(click.player, this.auctionPlayer.getItemBeingListed())) return;
			if (!auctionPlayer.canListItem()) return;

			click.gui.exit();

			// do listing confirmation first
			if (Settings.ASK_FOR_LISTING_CONFIRMATION.getBoolean()) {
				click.manager.showGUI(click.player, new GUIListingConfirm(click.player, createListingItem(), confirmed -> {
					if (confirmed) performAuctionListing(click);
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
			} else AuctionHouse.newChain().sync(click.player::closeInventory).execute();
		});
	}

	private void drawQtyPurchase() {
		if (Settings.ALLOW_PURCHASE_OF_SPECIFIC_QUANTITIES.getBoolean()) {
			setButton(3, 7, QuickItem
					.of(this.allowPartialBuy ? Settings.GUI_SELL_BIN_ITEM_ITEMS_PARTIAL_ENABLED_ITEM.getString() : Settings.GUI_SELL_BIN_ITEM_ITEMS_PARTIAL_DISABLED_ITEM.getString())
					.name(this.allowPartialBuy ? Settings.GUI_SELL_BIN_ITEM_ITEMS_PARTIAL_ENABLED_NAME.getString() : Settings.GUI_SELL_BIN_ITEM_ITEMS_PARTIAL_DISABLED_NAME.getString())
					.lore(this.allowPartialBuy ? Settings.GUI_SELL_BIN_ITEM_ITEMS_PARTIAL_ENABLED_LORE.getStringList() : Settings.GUI_SELL_BIN_ITEM_ITEMS_PARTIAL_DISABLED_LORE.getStringList())
					.make(), e -> {

				this.allowPartialBuy = !allowPartialBuy;
				drawQtyPurchase();
			});
		}
	}

	private AuctionedItem createListingItem() {
		final AuctionedItem item = new AuctionedItem(UUID.randomUUID(), auctionPlayer.getUuid(), auctionPlayer.getUuid(), auctionPlayer.getPlayer().getName(), auctionPlayer.getPlayer().getName(), MaterialCategorizer.getMaterialCategory(this.auctionPlayer.getItemBeingListed()), this.auctionPlayer.getItemBeingListed(), this.listingPrice, 0, 0, this.listingPrice, false, false, System.currentTimeMillis() + (this.listingTime * 1000L));

		item.setAllowPartialBuy(this.allowPartialBuy);
		return item;
	}
}
