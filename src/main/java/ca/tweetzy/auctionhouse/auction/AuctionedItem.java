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

package ca.tweetzy.auctionhouse.auction;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.auction.enums.AuctionItemCategory;
import ca.tweetzy.auctionhouse.auction.enums.AuctionStackType;
import ca.tweetzy.auctionhouse.helpers.BundleUtil;
import ca.tweetzy.auctionhouse.model.MaterialCategorizer;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.flight.comp.enums.ServerVersion;
import ca.tweetzy.flight.nbtapi.NBT;
import ca.tweetzy.flight.utils.QuickItem;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: July 29 2021
 * Time Created: 6:58 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */

@Getter
@Setter
public class AuctionedItem {

	private UUID id;
	private UUID owner;
	private UUID highestBidder;

	private String ownerName;
	private String highestBidderName;
	private AuctionItemCategory category;

	private ItemStack item;
	private double basePrice;
	private double bidStartingPrice;
	private double bidIncrementPrice;
	private double currentPrice;

	private boolean isBidItem;
	private boolean expired;
	private long expiresAt;

	private String listedWorld = null;
	private boolean infinite = false;
	private boolean allowPartialBuy = false;
	private boolean serverItem = false;
	private boolean isRequest = false;
	private int requestAmount = 0;

	// priority listing
	private boolean hasListingPriority = false;
	private long priorityExpiresAt = 0;

	public AuctionedItem() {
	}

	public AuctionedItem(
			@NonNull UUID id,
			@NonNull UUID owner,
			@NonNull UUID highestBidder,
			@NonNull String ownerName,
			@NonNull String highestBidderName,
			@NonNull AuctionItemCategory category,
			@NonNull ItemStack item,
			double basePrice,
			double bidStartingPrice,
			double bidIncrementPrice,
			double currentPrice,
			boolean isBidItem,
			boolean expired,
			long expiresAt
	) {
		this.id = id;
		this.owner = owner;
		this.highestBidder = highestBidder;
		this.ownerName = ownerName;
		this.highestBidderName = highestBidderName;
		this.category = category;
		this.item = item;
		this.basePrice = basePrice;
		this.bidStartingPrice = bidStartingPrice;
		this.bidIncrementPrice = bidIncrementPrice;
		this.currentPrice = currentPrice;
		this.isBidItem = isBidItem;
		this.expired = expired;
		this.expiresAt = expiresAt;
		this.serverItem = false;
		this.isRequest = false;
	}

	public static AuctionedItem createRequest(Player player, ItemStack item, int requestAmount, double price, int allowedTime) {
		final AuctionedItem requested = new AuctionedItem();

		requested.setId(UUID.randomUUID());
		requested.setOwner(player.getUniqueId());
		requested.setHighestBidder(player.getUniqueId());
		requested.setOwnerName(player.getName());
		requested.setHighestBidderName(player.getName());
		requested.setBasePrice(price);
		requested.setItem(item.clone());
		requested.setCategory(MaterialCategorizer.getMaterialCategory(item));
		requested.setExpiresAt(System.currentTimeMillis() + 1000L * allowedTime);
		requested.setBidItem(false);
		requested.setServerItem(false);
		requested.setExpired(false);
		requested.setListedWorld(player.getWorld().getName());
		requested.setInfinite(false);
		requested.setAllowPartialBuy(false);
		requested.setRequest(true);
		requested.setRequestAmount(requestAmount);

		return requested;
	}

	public ItemStack getBidStack(Player player) {
		QuickItem itemStack = QuickItem.of(this.item.clone());
		itemStack.amount(Math.max(this.item.getAmount(), 1));
		List<String> lore = new ArrayList<>();

		if (this.serverItem)
			this.ownerName = AuctionHouse.getInstance().getLocale().getMessage("general.server listing").getMessage();

		lore.addAll(TextUtils.formatText(Settings.AUCTION_STACK_DETAILS_HEADER.getStringList()));
		lore.addAll(TextUtils.formatText(Settings.AUCTION_STACK_DETAILS_SELLER.getStringList().stream().map(s -> s.replace("%seller%", this.ownerName)).collect(Collectors.toList())));
		lore.addAll(TextUtils.formatText(Settings.AUCTION_STACK_DETAILS_CURRENT_PRICE.getStringList().stream().map(s -> s.replace("%currentprice%", Settings.USE_SHORT_NUMBERS_ON_ITEMS.getBoolean() ? AuctionAPI.getInstance().getFriendlyNumber(this.currentPrice) : AuctionAPI.getInstance().formatNumber(this.currentPrice))).collect(Collectors.toList())));
		lore.addAll(TextUtils.formatText(Settings.AUCTION_STACK_DETAILS_HIGHEST_BIDDER.getStringList().stream().map(s -> s.replace("%highestbidder%", this.highestBidder.equals(this.owner) ? AuctionHouse.getInstance().getLocale().getMessage("auction.nobids").getMessage() : this.highestBidderName)).collect(Collectors.toList())));

		if (this.infinite) {
			lore.addAll(TextUtils.formatText(Settings.AUCTION_STACK_DETAILS_INFINITE.getStringList()));
		} else {
			long[] times = AuctionAPI.getInstance().getRemainingTimeValues((this.expiresAt - System.currentTimeMillis()) / 1000);

			lore.addAll(TextUtils.formatText(Settings.AUCTION_STACK_DETAILS_TIME_LEFT.getStringList().stream().map(s -> s
					.replace("%remaining_days%", String.valueOf(times[0]))
					.replace("%remaining_hours%", String.valueOf(times[1]))
					.replace("%remaining_minutes%", String.valueOf(times[2]))
					.replace("%remaining_seconds%", String.valueOf(times[3]))
					.replace("%remaining_total_hours%", String.valueOf(((this.expiresAt - System.currentTimeMillis()) / 1000) / 3600))
			).collect(Collectors.toList())));
		}

		lore.addAll(TextUtils.formatText(Settings.AUCTION_STACK_PURCHASE_CONTROL_FOOTER.getStringList()));

		itemStack.lore(player, lore);
		return itemStack.make();
	}

	public ItemStack getDisplayRequestStack(Player player, AuctionStackType type) {
		QuickItem itemStack = QuickItem.of(this.item.clone());
		itemStack.amount(Math.max(this.item.getAmount(), 1));

		List<String> lore = new ArrayList<>();

		lore.addAll(TextUtils.formatText(Settings.AUCTION_STACK_DETAILS_HEADER.getStringList()));
		lore.addAll(TextUtils.formatText(Settings.AUCTION_STACK_DETAILS_REQUESTER.getStringList().stream().map(s -> s.replace("%requester%", this.ownerName)).collect(Collectors.toList())));
		lore.addAll(TextUtils.formatText(Settings.AUCTION_STACK_DETAILS_REQUEST_PRICE.getStringList().stream().map(s -> s.replace("%request_price%", Settings.USE_SHORT_NUMBERS_ON_ITEMS.getBoolean() ? AuctionAPI.getInstance().getFriendlyNumber(this.basePrice) : AuctionAPI.getInstance().formatNumber(this.basePrice))).collect(Collectors.toList())));
		lore.addAll(TextUtils.formatText(Settings.AUCTION_STACK_DETAILS_REQUEST_COUNT.getStringList().stream().map(s -> s.replace("%request_amount%", String.valueOf(requestAmount))).collect(Collectors.toList())));

		long[] times = AuctionAPI.getInstance().getRemainingTimeValues((this.expiresAt - System.currentTimeMillis()) / 1000);

		lore.addAll(TextUtils.formatText(Settings.AUCTION_STACK_DETAILS_TIME_LEFT.getStringList().stream().map(s -> s
				.replace("%remaining_days%", String.valueOf(times[0]))
				.replace("%remaining_hours%", String.valueOf(times[1]))
				.replace("%remaining_minutes%", String.valueOf(times[2]))
				.replace("%remaining_seconds%", String.valueOf(times[3]))
				.replace("%remaining_total_hours%", String.valueOf(((this.expiresAt - System.currentTimeMillis()) / 1000) / 3600))
		).collect(Collectors.toList())));

		lore.addAll(TextUtils.formatText(Settings.AUCTION_STACK_PURCHASE_CONTROL_HEADER.getStringList()));

		if (type == AuctionStackType.ACTIVE_AUCTIONS_LIST)
			lore.addAll(TextUtils.formatText(Settings.AUCTION_STACK_PURCHASE_CONTROLS_CANCEL_REQUEST.getStringList()));

		if (type == AuctionStackType.MAIN_AUCTION_HOUSE)
			lore.addAll(TextUtils.formatText(Settings.AUCTION_STACK_PURCHASE_CONTROLS_FULFILL_REQUEST.getStringList()));

		lore.addAll(TextUtils.formatText(Settings.AUCTION_STACK_PURCHASE_CONTROL_FOOTER.getStringList()));

		itemStack.lore(player, lore);
		return itemStack.make();
	}

	public ItemStack getDisplayStack(Player player, AuctionStackType type) {
		QuickItem itemStack = QuickItem.of(this.item.clone());
		itemStack.amount(Math.max(this.item.getAmount(), 1));

		List<String> lore = new ArrayList<>();

		if (this.serverItem)
			this.ownerName = AuctionHouse.getInstance().getLocale().getMessage("general.server listing").getMessage();

		lore.addAll(TextUtils.formatText(Settings.AUCTION_STACK_DETAILS_HEADER.getStringList()));
		lore.addAll(TextUtils.formatText(Settings.AUCTION_STACK_DETAILS_SELLER.getStringList().stream().map(s -> s.replace("%seller%", this.ownerName)).collect(Collectors.toList())));

		if (this.basePrice != -1) {
			lore.addAll(TextUtils.formatText(Settings.AUCTION_STACK_DETAILS_BUY_NOW.getStringList().stream().filter(s -> this.isBidItem ? s.length() != 0 : s.length() >= 0).map(s -> s.replace("%buynowprice%", Settings.USE_SHORT_NUMBERS_ON_ITEMS.getBoolean() ? AuctionAPI.getInstance().getFriendlyNumber(this.basePrice) : AuctionAPI.getInstance().formatNumber(this.basePrice))).collect(Collectors.toList())));
		}

		if (this.isBidItem) {
			lore.addAll(TextUtils.formatText(Settings.AUCTION_STACK_DETAILS_CURRENT_PRICE.getStringList().stream().map(s -> s.replace("%currentprice%", Settings.USE_SHORT_NUMBERS_ON_ITEMS.getBoolean() ? AuctionAPI.getInstance().getFriendlyNumber(this.currentPrice) : AuctionAPI.getInstance().formatNumber(this.currentPrice))).collect(Collectors.toList())));
			if (!Settings.FORCE_CUSTOM_BID_AMOUNT.getBoolean()) {
				lore.addAll(TextUtils.formatText(Settings.AUCTION_STACK_DETAILS_BID_INCREMENT.getStringList().stream().map(s -> s.replace("%bidincrement%", Settings.USE_SHORT_NUMBERS_ON_ITEMS.getBoolean() ? AuctionAPI.getInstance().getFriendlyNumber(this.bidIncrementPrice) : AuctionAPI.getInstance().formatNumber(this.bidIncrementPrice))).collect(Collectors.toList())));
			}

			if (Settings.FORCE_CUSTOM_BID_AMOUNT.getBoolean() && Settings.USE_REALISTIC_BIDDING.getBoolean()) {
				lore.addAll(TextUtils.formatText(Settings.AUCTION_STACK_DETAILS_BID_INCREMENT.getStringList().stream().map(s -> s.replace("%bidincrement%", Settings.USE_SHORT_NUMBERS_ON_ITEMS.getBoolean() ? AuctionAPI.getInstance().getFriendlyNumber(this.bidIncrementPrice) : AuctionAPI.getInstance().formatNumber(this.bidIncrementPrice))).collect(Collectors.toList())));
			}

			lore.addAll(TextUtils.formatText(Settings.AUCTION_STACK_DETAILS_HIGHEST_BIDDER.getStringList().stream().map(s -> s.replace("%highestbidder%", this.highestBidder.equals(this.owner) ? AuctionHouse.getInstance().getLocale().getMessage("auction.nobids").getMessage() : this.highestBidderName)).collect(Collectors.toList())));
		}

		if (this.infinite) {
			lore.addAll(TextUtils.formatText(Settings.AUCTION_STACK_DETAILS_INFINITE.getStringList()));
		} else {
			long[] times = AuctionAPI.getInstance().getRemainingTimeValues((this.expiresAt - System.currentTimeMillis()) / 1000);
			lore.addAll(TextUtils.formatText(Settings.AUCTION_STACK_DETAILS_TIME_LEFT.getStringList().stream().map(s -> s
					.replace("%remaining_days%", String.valueOf(times[0]))
					.replace("%remaining_hours%", String.valueOf(times[1]))
					.replace("%remaining_minutes%", String.valueOf(times[2]))
					.replace("%remaining_seconds%", String.valueOf(times[3]))
					.replace("%remaining_total_hours%", String.valueOf(((this.expiresAt - System.currentTimeMillis()) / 1000) / 3600))
			).collect(Collectors.toList())));
		}

		lore.addAll(TextUtils.formatText(Settings.AUCTION_STACK_PURCHASE_CONTROL_HEADER.getStringList()));

		if (type == AuctionStackType.MAIN_AUCTION_HOUSE) {
			if (this.isBidItem) {
				if (this.basePrice != -1) {
					lore.addAll(TextUtils.formatText(Settings.AUCTION_STACK_PURCHASE_CONTROLS_BID_ON.getStringList()));
				} else {
					lore.addAll(TextUtils.formatText(Settings.AUCTION_STACK_PURCHASE_CONTROLS_BID_ON_NO_BUY_NOW.getStringList()));
				}
			} else {
				lore.addAll(TextUtils.formatText(Settings.AUCTION_STACK_PURCHASE_CONTROLS_BID_OFF.getStringList()));
				if (this.isAllowPartialBuy()) {
					lore.addAll(TextUtils.formatText(Settings.AUCTION_STACK_PURCHASE_CONTROLS_PARTIAL_BUY.getStringList()));
				}
			}

			if (BundleUtil.isBundledItem(this.item.clone()) || (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_11) && this.item.clone().getType().name().contains("SHULKER_BOX"))) {
				lore.addAll(TextUtils.formatText(Settings.AUCTION_STACK_PURCHASE_CONTROLS_INSPECTION.getStringList()));
			}
		} else {
			if (type == AuctionStackType.HIGHEST_BID_PREVIEW) {
				lore.addAll(TextUtils.formatText(Settings.AUCTION_STACK_HIGHEST_BIDDER_ITEM.getStringList()));
			} else {
				if (type == AuctionStackType.LISTING_PREVIEW) {
					lore.addAll(TextUtils.formatText(Settings.AUCTION_STACK_LISTING_PREVIEW_ITEM.getStringList()));

				} else {
					lore.addAll(TextUtils.formatText(Settings.AUCTION_STACK_PURCHASE_CONTROLS_CANCEL_ITEM.getStringList()));

					if (Settings.ALLOW_PLAYERS_TO_ACCEPT_BID.getBoolean() && this.bidStartingPrice >= 1 || this.bidIncrementPrice >= 1) {
						if (!this.owner.equals(this.highestBidder)) {
							lore.addAll(TextUtils.formatText(Settings.AUCTION_STACK_PURCHASE_CONTROLS_ACCEPT_BID.getStringList()));
						}
					}
				}
			}
		}

		lore.addAll(TextUtils.formatText(Settings.AUCTION_STACK_PURCHASE_CONTROL_FOOTER.getStringList()));

		itemStack.lore(player, lore);
		return itemStack.make();
	}

	public ItemStack getCleanItem() {
		ItemStack cleaned = this.item.clone();
		NBT.modify(cleaned, nbt -> {
			nbt.removeKey("AuctionDupeTracking");
		});

		return cleaned;
	}

	public boolean containsValidBid() {
		return isBidItem() && !this.highestBidder.equals(this.owner);
	}

	public boolean isListingPriorityActive() {
		return this.hasListingPriority && this.priorityExpiresAt > System.currentTimeMillis();
	}
}
