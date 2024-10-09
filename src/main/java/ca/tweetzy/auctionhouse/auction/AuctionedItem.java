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
import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.comp.enums.ServerVersion;
import ca.tweetzy.flight.nbtapi.NBT;
import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.flight.utils.Replacer;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static ca.tweetzy.auctionhouse.model.MultiVarReplacer.replaceVariable;

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
	private boolean hasListingPriority;
	private long priorityExpiresAt;

	private String currency = Settings.CURRENCY_DEFAULT_SELECTED.getString();
	private ItemStack currencyItem;

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
		this.hasListingPriority = false;
		this.priorityExpiresAt = 0;
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
		lore.addAll(TextUtils.formatText(Settings.AUCTION_STACK_DETAILS_CURRENT_PRICE.getStringList().stream().map(s -> s.replace("%currentprice%", AuctionHouse.getAPI().getFinalizedCurrencyNumber(this.currentPrice, this.currency, this.currencyItem))).collect(Collectors.toList())));
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
		lore.addAll(TextUtils.formatText(Settings.AUCTION_STACK_DETAILS_REQUEST_PRICE.getStringList().stream().map(s -> s.replace("%request_price%", AuctionHouse.getAPI().getFinalizedCurrencyNumber(this.basePrice, this.currency, this.currencyItem))).collect(Collectors.toList())));
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

		List<String> originalLore = this.item.getItemMeta() != null && this.item.getItemMeta().getLore() != null ? this.item.getItemMeta().getLore() : new ArrayList<>();
		List<String> BASE_LORE = Settings.AUCTION_STACK_INFO_LAYOUT.getStringList();
		itemStack.clearLore();

		if (this.serverItem)
			this.ownerName = AuctionHouse.getInstance().getLocale().getMessage("general.server listing").getMessage();

		final List<String> HEADER = Common.colorize(Settings.AUCTION_STACK_DETAILS_HEADER.getStringList());
		final List<String> CONTROLS_HEADER = Common.colorize(Settings.AUCTION_STACK_PURCHASE_CONTROL_HEADER.getStringList());
		final List<String> CONTROLS_FOOTER = Common.colorize(Settings.AUCTION_STACK_PURCHASE_CONTROL_FOOTER.getStringList());

		final List<String> SELLER = Common.colorize(Replacer.replaceVariables(Settings.AUCTION_STACK_DETAILS_SELLER.getStringList(), "seller", this.ownerName));
		final List<String> BUY_NOW_PRICE = Replacer.replaceVariables(Settings.AUCTION_STACK_DETAILS_BUY_NOW.getStringList(), "buynowprice", getFormattedBasePrice());
		final List<String> CURRENT_PRICE = Replacer.replaceVariables(Settings.AUCTION_STACK_DETAILS_CURRENT_PRICE.getStringList(), "currentprice", getFormattedCurrentPrice());
		final List<String> INCREMENT_PRICE = Replacer.replaceVariables(Settings.AUCTION_STACK_DETAILS_BID_INCREMENT.getStringList(), "bidincrement", getFormattedIncrementPrice());
		final List<String> HIGHEST_BIDDER = Replacer.replaceVariables(Settings.AUCTION_STACK_DETAILS_HIGHEST_BIDDER.getStringList(), "highestbidder", this.highestBidder.equals(this.owner) ? AuctionHouse.getInstance().getLocale().getMessage("auction.nobids").getMessage() : this.highestBidderName);

		List<String> LISTING_TIME = Settings.AUCTION_STACK_DETAILS_INFINITE.getStringList();
		if (!this.isInfinite()) {
			long[] times = AuctionAPI.getInstance().getRemainingTimeValues((this.expiresAt - System.currentTimeMillis()) / 1000);
			LISTING_TIME = Replacer.replaceVariables(Settings.AUCTION_STACK_DETAILS_TIME_LEFT.getStringList(),
					"remaining_days", String.valueOf(times[0]),
					"remaining_hours", String.valueOf(times[1]),
					"remaining_minutes", String.valueOf(times[2]),
					"remaining_seconds", String.valueOf(times[3]),
					"remaining_total_hours", String.valueOf(((this.expiresAt - System.currentTimeMillis()) / 1000) / 3600)
			);
		}

		final List<String> LISTING_PRIORITY = Settings.AUCTION_STACK_DETAILS_PRIORITY_LISTING.getStringList();
		final List<String> CONTROLS = new ArrayList<>();

		if (type == AuctionStackType.MAIN_AUCTION_HOUSE) {
			if (this.isBidItem) {
				if (this.basePrice != -1) {
					CONTROLS.addAll(Common.colorize(Settings.AUCTION_STACK_PURCHASE_CONTROLS_BID_ON.getStringList()));
				} else {
					CONTROLS.addAll(Common.colorize(Settings.AUCTION_STACK_PURCHASE_CONTROLS_BID_ON_NO_BUY_NOW.getStringList()));
				}
			} else {
				CONTROLS.addAll(Common.colorize(Settings.AUCTION_STACK_PURCHASE_CONTROLS_BID_OFF.getStringList()));
				if (this.isAllowPartialBuy()) {
					CONTROLS.addAll(Common.colorize(Settings.AUCTION_STACK_PURCHASE_CONTROLS_PARTIAL_BUY.getStringList()));
				}
			}

			if (BundleUtil.isBundledItem(this.item.clone()) || (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_11) && this.item.clone().getType().name().contains("SHULKER_BOX"))) {
				CONTROLS.addAll(Common.colorize(Settings.AUCTION_STACK_PURCHASE_CONTROLS_INSPECTION.getStringList()));
			}
		} else {
			if (type == AuctionStackType.HIGHEST_BID_PREVIEW) {
				CONTROLS.addAll(Common.colorize(Settings.AUCTION_STACK_HIGHEST_BIDDER_ITEM.getStringList()));
			} else {
				if (type == AuctionStackType.LISTING_PREVIEW) {
					CONTROLS.addAll(Common.colorize(Settings.AUCTION_STACK_LISTING_PREVIEW_ITEM.getStringList()));

				} else {
					CONTROLS.addAll(Common.colorize(Settings.AUCTION_STACK_PURCHASE_CONTROLS_CANCEL_ITEM.getStringList()));

					if (Settings.ALLOW_PLAYERS_TO_ACCEPT_BID.getBoolean() && this.bidStartingPrice >= 1 || this.bidIncrementPrice >= 1) {
						if (!this.owner.equals(this.highestBidder)) {
							CONTROLS.addAll(Common.colorize(Settings.AUCTION_STACK_PURCHASE_CONTROLS_ACCEPT_BID.getStringList()));
						}
					}

					if (Settings.LISTING_PRIORITY_ENABLED.getBoolean()) {
						CONTROLS.addAll(Common.colorize(Settings.AUCTION_STACK_PURCHASE_CONTROLS_PRIORITY_LISTING.getStringList()));
					}
				}
			}
		}

		// replace all the variables
		replaceVariable(BASE_LORE, "%original_item_lore%", originalLore, false);
		replaceVariable(BASE_LORE, "%header%", HEADER, false);
		replaceVariable(BASE_LORE, "%seller%", SELLER, false);
		replaceVariable(BASE_LORE, "%highest_bidder%", HIGHEST_BIDDER, !this.isBidItem);
		replaceVariable(BASE_LORE, "%buy_now_price%", BUY_NOW_PRICE, this.basePrice == -1);
		replaceVariable(BASE_LORE, "%current_price%", CURRENT_PRICE, !this.isBidItem);
		replaceVariable(BASE_LORE, "%bid_increment%", INCREMENT_PRICE, !this.isBidItem || Settings.FORCE_CUSTOM_BID_AMOUNT.getBoolean());
		replaceVariable(BASE_LORE, "%listing_time%", LISTING_TIME, false);
		replaceVariable(BASE_LORE, "%listing_priority%", LISTING_PRIORITY, !this.hasListingPriority);
		replaceVariable(BASE_LORE, "%controls_header%", CONTROLS_HEADER, false);
		replaceVariable(BASE_LORE, "%controls_footer%", CONTROLS_FOOTER, false);
		replaceVariable(BASE_LORE, "%controls%", CONTROLS, false);

		itemStack.lore(player, BASE_LORE);

		return itemStack.make();
	}


	public boolean playerHasSufficientMoney(OfflinePlayer player, double amount) {
		if (this.currencyItem != null && this.currencyItem.getType() != CompMaterial.AIR.parseMaterial()) {
			return AuctionHouse.getCurrencyManager().has(player, this.currencyItem, (int) amount);
		}

		final String[] split = this.currency.split("/");
		return AuctionHouse.getCurrencyManager().has(player, split[0], split[1], amount);
	}


	public ItemStack getCleanItem() {
		ItemStack cleaned = this.item.clone();
		NBT.modify(cleaned, nbt -> {
			nbt.removeKey("AuctionDupeTracking");
		});

		return cleaned;
	}

	public boolean hasValidItemCurrency() {
		return this.currencyItem != null && this.currencyItem.getType() != CompMaterial.AIR.parseMaterial();
	}

	public String getFormattedCurrentPrice() {
		return AuctionHouse.getAPI().getFinalizedCurrencyNumber(this.currentPrice, this.currency, this.currencyItem);
	}

	public String getFormattedBasePrice() {
		return AuctionHouse.getAPI().getFinalizedCurrencyNumber(this.basePrice, this.currency, this.currencyItem);
	}

	public String getFormattedStartingPrice() {
		return AuctionHouse.getAPI().getFinalizedCurrencyNumber(this.bidStartingPrice, this.currency, this.currencyItem);
	}

	public String getFormattedIncrementPrice() {
		return AuctionHouse.getAPI().getFinalizedCurrencyNumber(this.bidIncrementPrice, this.currency, this.currencyItem);
	}

	public boolean containsValidBid() {
		return isBidItem() && !this.highestBidder.equals(this.owner);
	}

	public boolean isListingPriorityActive() {
		return this.hasListingPriority && this.priorityExpiresAt > System.currentTimeMillis();
	}
}
