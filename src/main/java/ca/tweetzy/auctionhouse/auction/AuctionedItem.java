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



import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.auctionhouse.settings.Translations;
import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.api.currency.AbstractCurrency;
import ca.tweetzy.auctionhouse.auction.enums.AuctionItemCategory;
import ca.tweetzy.auctionhouse.auction.enums.AuctionStackType;
import ca.tweetzy.auctionhouse.helpers.BundleUtil;
import ca.tweetzy.auctionhouse.impl.currency.ItemCurrency;
import ca.tweetzy.auctionhouse.model.MaterialCategorizer;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.comp.enums.ServerVersion;
import ca.tweetzy.flight.nbtapi.NBT;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.flight.utils.Replacer;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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

	private String currency = AuctionHouse.getCurrencyManager().getDefaultCurrency().getStoreableName();
	private ItemStack currencyItem;

	private Long createdAt;

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
		requested.setCreatedAt(System.currentTimeMillis());

		return requested;
	}

	public ItemStack getBidStack(Player player) {
		QuickItem itemStack = QuickItem.of(this.item.clone());
		itemStack.amount(Math.max(this.item.getAmount(), 1));
		List<String> lore = new ArrayList<>();

		if (this.serverItem)
			this.ownerName = TranslationManager.string(Translations.GENERAL_SERVER_LISTING);

		lore.addAll(Common.colorize(TranslationManager.list(player, Translations.AUCTION_STACK_HEADER)));
		lore.addAll(Common.colorize(TranslationManager.list(player, Translations.AUCTION_STACK_SELLER).stream().map(s -> s.replace("%seller%", this.ownerName)).collect(Collectors.toList())));
		lore.addAll(Common.colorize(TranslationManager.list(player, Translations.AUCTION_STACK_CURRENT_PRICE).stream().map(s -> s.replace("%currentprice%", AuctionHouse.getAPI().getFinalizedCurrencyNumber(this.currentPrice, this.currency, this.currencyItem))).collect(Collectors.toList())));
		lore.addAll(Common.colorize(TranslationManager.list(player, Translations.AUCTION_STACK_HIGHEST_BIDDER).stream().map(s -> s.replace("%highestbidder%", this.highestBidder.equals(this.owner) ? TranslationManager.string(player, Translations.AUCTION_NO_BIDS) : this.highestBidderName)).collect(Collectors.toList())));

		if (this.infinite) {
			lore.addAll(Common.colorize(TranslationManager.list(player, Translations.AUCTION_STACK_INFINITE)));
		} else {
			long[] times = AuctionAPI.getInstance().getRemainingTimeValues((this.expiresAt - System.currentTimeMillis()) / 1000);

			lore.addAll(Common.colorize(TranslationManager.list(player, Translations.AUCTION_STACK_TIME_LEFT).stream().map(s -> s
					.replace("%remaining_days%", String.valueOf(times[0]))
					.replace("%remaining_hours%", String.valueOf(times[1]))
					.replace("%remaining_minutes%", String.valueOf(times[2]))
					.replace("%remaining_seconds%", String.valueOf(times[3]))
					.replace("%remaining_total_hours%", String.valueOf(((this.expiresAt - System.currentTimeMillis()) / 1000) / 3600))
			).collect(Collectors.toList())));
		}

		lore.addAll(Common.colorize(TranslationManager.list(player, Translations.AUCTION_STACK_CONTROLS_FOOTER)));

		itemStack.lore(player, lore);
		return itemStack.make();
	}

	public ItemStack getDisplayRequestStack(Player player, AuctionStackType type) {
		QuickItem itemStack = QuickItem.of(this.item.clone());
		itemStack.amount(Math.max(this.item.getAmount(), 1));

		List<String> lore = new ArrayList<>();

		lore.addAll(Common.colorize(TranslationManager.list(player, Translations.AUCTION_STACK_HEADER)));
		lore.addAll(Common.colorize(TranslationManager.list(player, Translations.AUCTION_STACK_REQUESTER).stream().map(s -> s.replace("%requester%", this.ownerName)).collect(Collectors.toList())));
		lore.addAll(Common.colorize(TranslationManager.list(player, Translations.AUCTION_STACK_REQUEST_PRICE).stream().map(s -> s.replace("%request_price%", AuctionHouse.getAPI().getFinalizedCurrencyNumber(this.basePrice, this.currency, this.currencyItem))).collect(Collectors.toList())));
		lore.addAll(Common.colorize(TranslationManager.list(player, Translations.AUCTION_STACK_REQUEST_COUNT).stream().map(s -> s.replace("%request_amount%", String.valueOf(requestAmount))).collect(Collectors.toList())));

		long[] times = AuctionAPI.getInstance().getRemainingTimeValues((this.expiresAt - System.currentTimeMillis()) / 1000);

		lore.addAll(Common.colorize(TranslationManager.list(player, Translations.AUCTION_STACK_TIME_LEFT).stream().map(s -> s
				.replace("%remaining_days%", String.valueOf(times[0]))
				.replace("%remaining_hours%", String.valueOf(times[1]))
				.replace("%remaining_minutes%", String.valueOf(times[2]))
				.replace("%remaining_seconds%", String.valueOf(times[3]))
				.replace("%remaining_total_hours%", String.valueOf(((this.expiresAt - System.currentTimeMillis()) / 1000) / 3600))
		).collect(Collectors.toList())));

		lore.addAll(Common.colorize(TranslationManager.list(player, Translations.AUCTION_STACK_CONTROLS_HEADER)));

		if (type == AuctionStackType.ACTIVE_AUCTIONS_LIST)
			lore.addAll(Common.colorize(TranslationManager.list(player, Translations.AUCTION_STACK_CONTROLS_CANCEL_REQUEST)));

		if (type == AuctionStackType.MAIN_AUCTION_HOUSE)
			lore.addAll(Common.colorize(TranslationManager.list(player, Translations.AUCTION_STACK_CONTROLS_FULFILL_REQUEST)));

		lore.addAll(Common.colorize(TranslationManager.list(player, Translations.AUCTION_STACK_CONTROLS_FOOTER)));

		itemStack.lore(player, lore);

		return itemStack.make();
	}

	public ItemStack getDisplayStack(Player player, AuctionStackType type) {
		ItemStack itemStack = this.item.clone();
		itemStack.setAmount(Math.max(this.item.getAmount(), 1));

		List<String> originalLore = this.item.getItemMeta() != null && this.item.getItemMeta().getLore() != null ? this.item.getItemMeta().getLore() : new ArrayList<>();
		// Mutable copy: MultiVarReplacer mutates the list in place; Flight/config may return an unmodifiable list.
		List<String> BASE_LORE = new ArrayList<>(Settings.AUCTION_STACK_INFO_LAYOUT.getStringList());

		if (this.serverItem)
			this.ownerName = TranslationManager.string(Translations.GENERAL_SERVER_LISTING);

		final List<String> HEADER = Common.colorize(TranslationManager.list(player, Translations.AUCTION_STACK_HEADER));
		final List<String> CONTROLS_HEADER = Common.colorize(TranslationManager.list(player, Translations.AUCTION_STACK_CONTROLS_HEADER));
		final List<String> CONTROLS_FOOTER = Common.colorize(TranslationManager.list(player, Translations.AUCTION_STACK_CONTROLS_FOOTER));

		final List<String> SELLER = Common.colorize(Replacer.replaceVariables(TranslationManager.list(player, Translations.AUCTION_STACK_SELLER), "seller", this.ownerName));
		final List<String> BUY_NOW_PRICE = Replacer.replaceVariables(TranslationManager.list(player, Translations.AUCTION_STACK_BUY_NOW), "buynowprice", getFormattedBasePrice());
		final List<String> CURRENT_PRICE = Replacer.replaceVariables(TranslationManager.list(player, Translations.AUCTION_STACK_CURRENT_PRICE), "currentprice", getFormattedCurrentPrice());
		final List<String> INCREMENT_PRICE = Replacer.replaceVariables(TranslationManager.list(player, Translations.AUCTION_STACK_BID_INCREMENT), "bidincrement", getFormattedIncrementPrice());
		final List<String> HIGHEST_BIDDER = Replacer.replaceVariables(TranslationManager.list(player, Translations.AUCTION_STACK_HIGHEST_BIDDER), "highestbidder", this.highestBidder.equals(this.owner) ? TranslationManager.string(player, Translations.AUCTION_NO_BIDS) : this.highestBidderName);

		List<String> LISTING_TIME = TranslationManager.list(player, Translations.AUCTION_STACK_INFINITE);
		if (!this.isInfinite()) {
			long[] times = AuctionAPI.getInstance().getRemainingTimeValues((this.expiresAt - System.currentTimeMillis()) / 1000);
			LISTING_TIME = Replacer.replaceVariables(TranslationManager.list(player, Translations.AUCTION_STACK_TIME_LEFT),
					"remaining_days", String.valueOf(times[0]),
					"remaining_hours", String.valueOf(times[1]),
					"remaining_minutes", String.valueOf(times[2]),
					"remaining_seconds", String.valueOf(times[3]),
					"remaining_total_hours", String.valueOf(((this.expiresAt - System.currentTimeMillis()) / 1000) / 3600)
			);
		}

		final List<String> LISTING_PRIORITY = TranslationManager.list(player, Translations.AUCTION_STACK_PRIORITY_LISTING);
		final List<String> LISTING_WATCHED = TranslationManager.list(player, Translations.AUCTION_STACK_WATCHED);
		final List<String> CONTROLS = new ArrayList<>();

		if (type == AuctionStackType.MAIN_AUCTION_HOUSE) {
			if (AuctionHouse.getAPI().isAuctionHouseOpen()) {
				if (this.isBidItem) {
					if (this.basePrice != -1) {
						CONTROLS.addAll(Common.colorize(TranslationManager.list(player, Translations.AUCTION_STACK_CONTROLS_BID_ON)));
					} else {
						CONTROLS.addAll(Common.colorize(TranslationManager.list(player, Translations.AUCTION_STACK_CONTROLS_BID_ON_NO_BUY_NOW)));
					}
				} else {
					CONTROLS.addAll(Common.colorize(TranslationManager.list(player, Translations.AUCTION_STACK_CONTROLS_BID_OFF)));

					if (Settings.CART_SYSTEM_ENABLED.getBoolean()) {
						CONTROLS.addAll(Common.colorize(TranslationManager.list(player, Translations.AUCTION_STACK_CONTROLS_ADD_TO_CART)));
					}

					if (this.isAllowPartialBuy()) {
						CONTROLS.addAll(Common.colorize(TranslationManager.list(player, Translations.AUCTION_STACK_CONTROLS_PARTIAL_BUY)));
					}
				}

				if (BundleUtil.isBundledItem(this.item.clone()) || (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_11) && this.item.clone().getType().name().contains("SHULKER_BOX"))) {
					CONTROLS.addAll(Common.colorize(TranslationManager.list(player, Translations.AUCTION_STACK_CONTROLS_INSPECTION)));
				}

				if (Settings.WATCHLIST_ENABLED.getBoolean() && !this.owner.equals(player.getUniqueId())) {
					CONTROLS.addAll(Common.colorize(TranslationManager.list(player, Translations.AUCTION_STACK_CONTROLS_WATCHLIST)));
				}
			} else {
				final String[] timesToOpen = AuctionHouse.getAPI().getTimeUntilNextRange(Settings.TIMED_USAGE_RANGE.getStringList());
				CONTROLS.addAll(Replacer.replaceVariables(TranslationManager.list(player, Translations.AUCTION_STACK_AUCTION_CLOSED), "hours", timesToOpen[0], "minutes", timesToOpen[1], "seconds", timesToOpen[2]));
			}
		} else {
			if (type == AuctionStackType.HIGHEST_BID_PREVIEW) {
				CONTROLS.addAll(Common.colorize(TranslationManager.list(player, Translations.AUCTION_STACK_CONTROLS_HIGHEST_BIDDER)));
			} else {
				if (type == AuctionStackType.LISTING_PREVIEW) {
					CONTROLS.addAll(Common.colorize(TranslationManager.list(player, Translations.AUCTION_STACK_CONTROLS_PREVIEW_ITEM)));
				} else if (type == AuctionStackType.CART) {
					CONTROLS.addAll(Common.colorize(TranslationManager.list(player, Translations.AUCTION_STACK_CONTROLS_CART)));
				} else {
					CONTROLS.addAll(Common.colorize(TranslationManager.list(player, Translations.AUCTION_STACK_CONTROLS_CANCEL_ITEM)));

					if (Settings.ALLOW_PLAYERS_TO_ACCEPT_BID.getBoolean() && this.bidStartingPrice >= 1 || this.bidIncrementPrice >= 1) {
						if (!this.owner.equals(this.highestBidder)) {
							CONTROLS.addAll(Common.colorize(TranslationManager.list(player, Translations.AUCTION_STACK_CONTROLS_ACCEPT_BID)));
						}
					}

					if (Settings.LISTING_PRIORITY_ENABLED.getBoolean()) {
						CONTROLS.addAll(Common.colorize(TranslationManager.list(player, Translations.AUCTION_STACK_CONTROLS_PRIORITY_LISTING)));
					}
				}
			}
		}


		// replace all the variables
//		replaceVariable(BASE_LORE, "%original_item_lore%", hasPacketLore ? new ArrayList<>() : originalLore, false);
		replaceVariable(BASE_LORE, "%original_item_lore%", new ArrayList<>(), true);
		replaceVariable(BASE_LORE, "%header%", HEADER, false);
		replaceVariable(BASE_LORE, "%seller%", SELLER, false);
		replaceVariable(BASE_LORE, "%highest_bidder%", HIGHEST_BIDDER, !this.isBidItem);
		replaceVariable(BASE_LORE, "%buy_now_price%", BUY_NOW_PRICE, this.basePrice == -1);
		replaceVariable(BASE_LORE, "%current_price%", CURRENT_PRICE, !this.isBidItem);
		replaceVariable(BASE_LORE, "%bid_increment%", INCREMENT_PRICE, !this.isBidItem || Settings.FORCE_CUSTOM_BID_AMOUNT.getBoolean());
		replaceVariable(BASE_LORE, "%listing_time%", LISTING_TIME, false);
		replaceVariable(BASE_LORE, "%listing_priority%", LISTING_PRIORITY, !this.hasListingPriority);
		boolean isWatched = type == AuctionStackType.MAIN_AUCTION_HOUSE && Settings.WATCHLIST_ENABLED.getBoolean() && AuctionHouse.getWatchlistManager().isWatching(player.getUniqueId(), this.getId());
		replaceVariable(BASE_LORE, "%listing_watched%", LISTING_WATCHED, !isWatched);
		replaceVariable(BASE_LORE, "%controls_header%", CONTROLS_HEADER, false);
		replaceVariable(BASE_LORE, "%controls_footer%", CONTROLS_FOOTER, false);
		replaceVariable(BASE_LORE, "%controls%", CONTROLS, false);

//		itemStack.lore(player, BASE_LORE);
		ItemMeta meta = this.item.hasItemMeta() ? itemStack.getItemMeta() : Bukkit.getItemFactory().getItemMeta(this.item.getType());
		List<String> lore = new ArrayList<>();

		if (meta != null && meta.getLore() != null)
			lore = meta.getLore();

		lore.addAll(Common.colorize(BASE_LORE));
		meta.setLore(lore);

		itemStack.setItemMeta(meta);

		return itemStack;
	}

	public boolean playerHasSufficientMoney(OfflinePlayer player, double amount) {
		if (this.currencyItem != null && this.currencyItem.getType() != CompMaterial.AIR.get()) {
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
		return this.currencyItem != null && this.currencyItem.getType() != CompMaterial.AIR.get();
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

	public boolean currencyMatches(AbstractCurrency currencyToCheck) {
		final String[] split = this.currency.split("/");
		if (split[0].equalsIgnoreCase("AuctionHouse") && split[1].equalsIgnoreCase("Item") && currencyToCheck instanceof ItemCurrency)
			return true;

		return split[0].equalsIgnoreCase(currencyToCheck.getOwningPlugin()) && split[1].equalsIgnoreCase(currencyToCheck.getCurrencyName());
	}
}
