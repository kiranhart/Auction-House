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

package ca.tweetzy.auctionhouse.guis.core;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.api.auction.ListingResult;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.auction.AuctionedItem;
import ca.tweetzy.auctionhouse.guis.abstraction.AuctionBaseGUI;
import ca.tweetzy.auctionhouse.guis.confirmation.GUIListingConfirm;
import ca.tweetzy.auctionhouse.helpers.AuctionCreator;
import ca.tweetzy.auctionhouse.helpers.BundleUtil;
import ca.tweetzy.auctionhouse.model.MaterialCategorizer;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.compatibility.XMaterial;
import ca.tweetzy.core.utils.PlayerUtils;
import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.utils.QuickItem;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * The current file has been created by Kiran Hart
 * Date Created: November 22 2021
 * Time Created: 1:11 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public final class GUIBundleCreation extends AuctionBaseGUI {

	final AuctionHouse instance = AuctionHouse.getInstance();
	private final AuctionPlayer auctionPlayer;
	private final int allowedTime;
	private final boolean buyNowAllow;
	private final boolean isBiddingItem;
	private final Double buyNowPrice;
	private final Double startingBid;
	private final Double bidIncrement;

	public GUIBundleCreation(AuctionPlayer auctionPlayer, int allowedTime, boolean buyNowAllow, boolean isBiddingItem, Double buyNowPrice, Double startingBid, Double bidIncrement) {
		super(null, auctionPlayer.getPlayer(), Settings.GUI_CREATE_BUNDLE_TITLE.getString(), 6);
		this.auctionPlayer = auctionPlayer;
		this.allowedTime = allowedTime;
		this.buyNowAllow = buyNowAllow;
		this.isBiddingItem = isBiddingItem;
		this.buyNowPrice = buyNowPrice;
		this.startingBid = startingBid;
		this.bidIncrement = bidIncrement;

		setAllowDrops(false);
		setAcceptsItems(true);
		setAllowShiftClick(true);
		setUnlockedRange(0, 44);

		for (int i = 0; i < 45; i++)
			setItem(i, CompMaterial.AIR.parseItem());

		Arrays.asList(45, 46, 47, 48, 49, 50, 51, 52, 53).forEach(i -> setAction(i, e -> e.event.setCancelled(true)));

		setOnClose(close -> {
			for (int i = 0; i < 45; i++) {
				final ItemStack item = getItem(i);
				if (item == null || item.getType() == XMaterial.AIR.parseMaterial()) continue;
				PlayerUtils.giveItem(auctionPlayer.getPlayer(), item);
			}
		});

		draw();
	}

	@Override
	protected void draw() {

		setButton(getRows() - 1, 4, QuickItem
				.of(Settings.GUI_CREATE_BUNDLE_CONFIRM_ITEM.getString())
				.name(Settings.GUI_CREATE_BUNDLE_CONFIRM_NAME.getString())
				.lore(Settings.GUI_CREATE_BUNDLE_CONFIRM_LORE.getStringList())
				.make(), ClickType.LEFT, e -> {


			ItemStack firstItem = null;
			List<ItemStack> validItems = new ArrayList<>();

			boolean containsBundle = false;

			for (int i = 0; i < 44; i++) {
				final ItemStack item = getItem(i);
				if (item == null || item.getType() == XMaterial.AIR.parseMaterial()) continue;

				boolean meetsListingRequirements = AuctionAPI.getInstance().meetsListingRequirements(player, item);

				if (BundleUtil.isBundledItem(item)) {
					meetsListingRequirements = false;
					containsBundle = true;
				}

				if (!meetsListingRequirements) continue;

				if (firstItem == null)
					firstItem = item;

				validItems.add(item);
			}

			// are they even allowed to sell more items
			if (auctionPlayer.isAtItemLimit(e.player)) {
				return;
			}

			if (containsBundle) {
				AuctionHouse.getInstance().getLocale().getMessage("general.cannotsellbundleditem").sendPrefixedMessage(e.player);
				return;
			}

			if (validItems.size() == 0) return;
			final ItemStack bundle = AuctionAPI.getInstance().createBundledItem(firstItem, validItems.toArray(new ItemStack[0]));

			AuctionedItem auctionedItem = new AuctionedItem();
			auctionedItem.setId(UUID.randomUUID());
			auctionedItem.setOwner(e.player.getUniqueId());
			auctionedItem.setHighestBidder(e.player.getUniqueId());
			auctionedItem.setOwnerName(e.player.getName());
			auctionedItem.setHighestBidderName(e.player.getName());
			auctionedItem.setItem(validItems.size() > 1 ? bundle : validItems.get(0));
			auctionedItem.setCategory(MaterialCategorizer.getMaterialCategory(validItems.size() > 1 ? bundle : validItems.get(0)));
			auctionedItem.setExpiresAt(System.currentTimeMillis() + 1000L * allowedTime);
			auctionedItem.setBidItem(isBiddingItem);
			auctionedItem.setExpired(false);

			auctionedItem.setBasePrice(Settings.ROUND_ALL_PRICES.getBoolean() ? Math.round(buyNowAllow ? buyNowPrice : -1) : buyNowAllow ? buyNowPrice : -1);
			auctionedItem.setBidStartingPrice(Settings.ROUND_ALL_PRICES.getBoolean() ? Math.round(isBiddingItem ? startingBid : !buyNowAllow ? buyNowPrice : 0) : isBiddingItem ? startingBid : !buyNowAllow ? buyNowPrice : 0);
			auctionedItem.setBidIncrementPrice(Settings.ROUND_ALL_PRICES.getBoolean() ? Math.round(isBiddingItem ? bidIncrement != null ? bidIncrement : Settings.MIN_AUCTION_INCREMENT_PRICE.getDouble() : 0) : isBiddingItem ? bidIncrement != null ? bidIncrement : Settings.MIN_AUCTION_INCREMENT_PRICE.getDouble() : 0);
			auctionedItem.setCurrentPrice(Settings.ROUND_ALL_PRICES.getBoolean() ? Math.round(isBiddingItem ? startingBid : buyNowPrice <= -1 ? startingBid : buyNowPrice) : isBiddingItem ? startingBid : buyNowPrice <= -1 ? startingBid : buyNowPrice);

			auctionedItem.setListedWorld(e.player.getWorld().getName());
			auctionedItem.setInfinite(false);
			auctionedItem.setAllowPartialBuy(false);

			auctionPlayer.setItemBeingListed(bundle);

			if (Settings.ASK_FOR_LISTING_CONFIRMATION.getBoolean()) {
				instance.getGuiManager().showGUI(auctionPlayer.getPlayer(), new GUIListingConfirm(auctionPlayer.getPlayer(), auctionedItem, result -> {
					if (!result) {
						auctionPlayer.getPlayer().closeInventory();

						if (BundleUtil.isBundledItem(auctionedItem.getItem())) PlayerUtils.giveItem(auctionPlayer.getPlayer(), BundleUtil.extractBundleItems(auctionedItem.getItem()));
						else PlayerUtils.giveItem(auctionPlayer.getPlayer(), auctionedItem.getItem());


						auctionPlayer.setItemBeingListed(null);
						return;
					}

					AuctionCreator.create(auctionPlayer, auctionedItem, (auction, listingResult) -> {
						if (listingResult != ListingResult.SUCCESS) {
							PlayerUtils.giveItem(auctionPlayer.getPlayer(), auction.getItem());
							auctionPlayer.setItemBeingListed(null);
							return;
						}

						if (Settings.OPEN_MAIN_AUCTION_HOUSE_AFTER_MENU_LIST.getBoolean()) {
							player.removeMetadata("AuctionHouseConfirmListing", AuctionHouse.getInstance());
							instance.getGuiManager().showGUI(auctionPlayer.getPlayer(), new GUIAuctionHouseV2(auctionPlayer));
						} else
							AuctionHouse.newChain().sync(player::closeInventory).execute();
					});
				}));
			} else {
				e.gui.exit();
				AuctionCreator.create(auctionPlayer, auctionedItem, (auction, listingResult) -> {
					if (listingResult != ListingResult.SUCCESS) {
						PlayerUtils.giveItem(auctionPlayer.getPlayer(), auction.getItem());
						auctionPlayer.setItemBeingListed(null);
						return;
					}

					if (Settings.OPEN_MAIN_AUCTION_HOUSE_AFTER_MENU_LIST.getBoolean()) {
						player.removeMetadata("AuctionHouseConfirmListing", AuctionHouse.getInstance());
						instance.getGuiManager().showGUI(auctionPlayer.getPlayer(), new GUIAuctionHouseV2(auctionPlayer));
					} else
						AuctionHouse.newChain().sync(player::closeInventory).execute();
				});
			}
		});
	}
}
