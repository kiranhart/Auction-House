/*
 * Auction House
 * Copyright 2022 Kiran Hart
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

package ca.tweetzy.auctionhouse.helpers;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.api.ListingResult;
import ca.tweetzy.auctionhouse.api.events.AuctionStartEvent;
import ca.tweetzy.auctionhouse.api.hook.McMMOHook;
import ca.tweetzy.auctionhouse.auction.AuctionPayment;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.auction.AuctionedItem;
import ca.tweetzy.auctionhouse.auction.enums.PaymentReason;
import ca.tweetzy.auctionhouse.managers.SoundManager;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.hooks.EconomyManager;
import ca.tweetzy.core.utils.PlayerUtils;
import ca.tweetzy.core.utils.TextUtils;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;

import static ca.tweetzy.auctionhouse.api.ListingResult.*;


@UtilityClass
public final class AuctionCreator {

	public static final UUID SERVER_AUCTION_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
	public static final String SERVER_LISTING_NAME = AuctionHouse.getInstance().getLocale().getMessage("general.server listing").getMessage();


	public void create(final AuctionPlayer auctionPlayer, @NonNull final AuctionedItem auctionItem, @NonNull final BiConsumer<AuctionedItem, ListingResult> result) {
		final AtomicReference<ListingResult> status = new AtomicReference<>(SUCCESS);
		if (!auctionItem.isServerItem() && auctionPlayer == null) {
			throw new RuntimeException("Cannot create listing if AuctionPlayer is null, did you mean to create a server listing?");
		}

		final AuctionHouse instance = AuctionHouse.getInstance();
		final Player seller = auctionPlayer == null ? null : auctionPlayer.getPlayer();

		// Check if player is even valid?!?

		// only check if not a server item
		if (!auctionItem.isServerItem()) {
			if (seller == null) {
				result.accept(auctionItem, PLAYER_INSTANCE_NOT_FOUND);
				return;
			}

			// Hooks & Special Cases
			if (McMMOHook.isUsingAbility(seller)) {
				instance.getLocale().getMessage("general.mcmmo_ability_active").sendPrefixedMessage(seller);
				result.accept(auctionItem, CANNOT_LIST_WITH_MCMMO_ABILITY_ACTIVE);
				return;
			}

			if (!Settings.ALLOW_SALE_OF_DAMAGED_ITEMS.getBoolean() && AuctionAPI.getInstance().isDamaged(auctionItem.getItem())) {
				instance.getLocale().getMessage("general.cannot list damaged item").sendPrefixedMessage(seller);
				result.accept(auctionItem, CANNOT_SELL_DAMAGED_ITEM);
				return;
			}

			if (Settings.PREVENT_SALE_OF_REPAIRED_ITEMS.getBoolean() && AuctionAPI.getInstance().isRepaired(auctionItem.getItem())) {
				instance.getLocale().getMessage("general.cannot list repaired item").sendPrefixedMessage(seller);
				result.accept(auctionItem, CANNOT_SELL_REPAIRED_ITEM);
				return;
			}
		}

		if (!AuctionAPI.getInstance().meetsMinItemPrice(BundleUtil.isBundledItem(auctionItem.getItem()), auctionItem.isBidItem(), auctionItem.getItem(), auctionItem.getBasePrice(), auctionItem.getBidStartingPrice())) {
			instance.getLocale().getMessage("pricing.minitemprice").processPlaceholder("price", AuctionAPI.getInstance().formatNumber(AuctionHouse.getInstance().getMinItemPriceManager().getMinPrice(auctionItem.getItem()).getPrice())).sendPrefixedMessage(seller);
			return;
		}

		final ItemStack finalItemToSell = auctionItem.getItem().clone();
		final double originalBasePrice = auctionItem.getBasePrice();
		final double originalStartPrice = auctionItem.getBidStartingPrice();
		final double originalIncrementPrice = auctionItem.getBidIncrementPrice();

		/*

		auctionedItem.setBasePrice(Settings.ROUND_ALL_PRICES.getBoolean() ? Math.round(this.isAllowingBuyNow ? buyNowPrice : -1) : this.isAllowingBuyNow ? buyNowPrice : -1);
		auctionedItem.setBidStartingPrice(Settings.ROUND_ALL_PRICES.getBoolean() ? Math.round(this.isBiddingItem ? this.bidStartPrice : 0) : this.isBiddingItem ? this.bidStartPrice : 0);
		auctionedItem.setBidIncrementPrice(Settings.ROUND_ALL_PRICES.getBoolean() ? Math.round(this.isBiddingItem ? this.bidIncrementPrice != null ? this.bidIncrementPrice : Settings.MIN_AUCTION_INCREMENT_PRICE.getDouble() : 0) : this.isBiddingItem ? this.bidIncrementPrice != null ? this.bidIncrementPrice : Settings.MIN_AUCTION_INCREMENT_PRICE.getDouble() : 0);
		auctionedItem.setCurrentPrice(Settings.ROUND_ALL_PRICES.getBoolean() ? Math.round(this.isBiddingItem ? this.bidStartPrice : this.buyNowPrice <= -1 ? this.bidStartPrice : this.buyNowPrice) : this.isBiddingItem ? this.bidStartPrice : this.buyNowPrice <= -1 ? this.bidStartPrice : this.buyNowPrice);
		 */

		final double listingFee = Settings.TAX_ENABLED.getBoolean() && Settings.TAX_CHARGE_LISTING_FEE.getBoolean() ? AuctionAPI.getInstance().calculateListingFee(originalBasePrice) : 0;

		// check tax
		if (Settings.TAX_ENABLED.getBoolean() && Settings.TAX_CHARGE_LISTING_FEE.getBoolean() && !auctionItem.isServerItem()) {
			if (!EconomyManager.hasBalance(seller, listingFee)) {
				instance.getLocale().getMessage("auction.tax.cannotpaylistingfee").processPlaceholder("price", AuctionAPI.getInstance().formatNumber(listingFee)).sendPrefixedMessage(seller);
				result.accept(auctionItem, CANNOT_PAY_LISTING_FEE);
				return;
			}

			EconomyManager.withdrawBalance(seller, listingFee);
			AuctionHouse.getInstance().getLocale().getMessage("auction.tax.paidlistingfee").processPlaceholder("price", AuctionAPI.getInstance().formatNumber(listingFee)).sendPrefixedMessage(seller);
			AuctionHouse.getInstance().getLocale().getMessage("pricing.moneyremove").processPlaceholder("player_balance", AuctionAPI.getInstance().formatNumber(EconomyManager.getBalance(seller))).processPlaceholder("price", AuctionAPI.getInstance().formatNumber(listingFee)).sendPrefixedMessage(seller);
		}

		// final item adjustments
		if (auctionItem.getListedWorld() == null && seller != null)
			auctionItem.setListedWorld(seller.getWorld().getName());


		AuctionStartEvent startEvent = new AuctionStartEvent(seller, auctionItem, listingFee);

		if (Bukkit.isPrimaryThread())
			Bukkit.getServer().getPluginManager().callEvent(startEvent);
		else
			Bukkit.getScheduler().runTask(AuctionHouse.getInstance(), () -> Bukkit.getServer().getPluginManager().callEvent(startEvent));

		if (startEvent.isCancelled()) {
			result.accept(auctionItem, EVENT_CANCELED);
			return;
		}

		// overwrite to be random uuid since it's a server auction

		if (auctionItem.isServerItem()) {
			auctionItem.setOwner(SERVER_AUCTION_UUID);
			auctionItem.setOwnerName(SERVER_LISTING_NAME);

			auctionItem.setHighestBidder(SERVER_AUCTION_UUID);
			auctionItem.setHighestBidderName(SERVER_LISTING_NAME);
		}

		//====================================================================================

		// A VERY UGLY LISTING MESSAGING THING, IDEK, I GOTTA DEAL WITH THIS EVENTUALLY 💀

		if (seller != null)
			SoundManager.getInstance().playSound(seller, Settings.SOUNDS_LISTED_ITEM_ON_AUCTION_HOUSE.getString());


		String NAX = AuctionHouse.getInstance().getLocale().getMessage("auction.biditemwithdisabledbuynow").getMessage();
		String msg = AuctionHouse.getInstance().getLocale().getMessage(auctionItem.isBidItem() ? "auction.listed.withbid" : "auction.listed.nobid")
				.processPlaceholder("amount", finalItemToSell.getAmount())
				.processPlaceholder("item", AuctionAPI.getInstance().getItemName(finalItemToSell))
				.processPlaceholder("base_price", auctionItem.getBasePrice() <= -1 ? NAX : AuctionAPI.getInstance().formatNumber(auctionItem.getBasePrice()))
				.processPlaceholder("start_price", AuctionAPI.getInstance().formatNumber(auctionItem.getBidStartingPrice()))
				.processPlaceholder("increment_price", AuctionAPI.getInstance().formatNumber(auctionItem.getBidIncrementPrice())).getMessage();

		if (seller != null && !auctionItem.isServerItem()) {
			if (AuctionHouse.getInstance().getAuctionPlayerManager().getPlayer(seller.getUniqueId()) == null) {
				AuctionHouse.getInstance().getLocale().newMessage(TextUtils.formatText("&cCould not find auction player instance for&f: &e" + seller.getName() + "&c creating one now.")).sendPrefixedMessage(Bukkit.getConsoleSender());
				AuctionHouse.getInstance().getAuctionPlayerManager().addPlayer(new AuctionPlayer(seller));
			}

			if (AuctionHouse.getInstance().getAuctionPlayerManager().getPlayer(seller.getUniqueId()).isShowListingInfo()) {
				AuctionHouse.getInstance().getLocale().newMessage(msg).sendPrefixedMessage(seller);
			}
		}

		//====================================================================================

		// Actually attempt the insertion now
		AuctionHouse.getInstance().getDataManager().insertAuctionAsync(auctionItem, (error, inserted) -> {
			if (auctionPlayer != null)
				auctionPlayer.setItemBeingListed(null);

			if (error != null) {
				if (Settings.SHOW_LISTING_ERROR_IN_CONSOLE.getBoolean())
					error.printStackTrace();

				if (seller != null) {
					instance.getLocale().getMessage("general.something_went_wrong_while_listing").sendPrefixedMessage(seller);

					ItemStack originalCopy = auctionItem.getItem().clone();
					int totalOriginal = BundleUtil.isBundledItem(originalCopy) ? AuctionAPI.getInstance().getItemCountInPlayerInventory(seller, originalCopy) : originalCopy.getAmount();

					if (BundleUtil.isBundledItem(originalCopy)) {
						originalCopy.setAmount(1);
						for (int i = 0; i < totalOriginal; i++) PlayerUtils.giveItem(seller, originalCopy);
					} else {
						originalCopy.setAmount(totalOriginal);
						PlayerUtils.giveItem(seller, originalCopy);
					}
				}

				// If the item could not be added for whatever reason and the tax listing fee is enabled, refund them
				if (Settings.TAX_ENABLED.getBoolean() && Settings.TAX_CHARGE_LISTING_FEE.getBoolean() && !auctionItem.isServerItem() && seller != null) {
					if (Settings.STORE_PAYMENTS_FOR_MANUAL_COLLECTION.getBoolean())
						AuctionHouse.getInstance().getDataManager().insertAuctionPayment(new AuctionPayment(
								seller.getUniqueId(),
								listingFee,
								auctionItem.getItem(),
								AuctionHouse.getInstance().getLocale().getMessage("general.prefix").getMessage(),
								PaymentReason.LISTING_FAILED
						), null);
					else
						EconomyManager.deposit(seller, listingFee);
					AuctionHouse.getInstance().getLocale().getMessage("pricing.moneyadd").processPlaceholder("player_balance", AuctionAPI.getInstance().formatNumber(EconomyManager.getBalance(seller))).processPlaceholder("price", AuctionAPI.getInstance().formatNumber(listingFee)).sendPrefixedMessage(seller);
				}

				result.accept(auctionItem, UNKNOWN);
				return;
			}

			AuctionHouse.getInstance().getAuctionItemManager().addAuctionItem(auctionItem);

			//====================================================================================
			// ANOTHER VERY SHIT BROADCAST THAT IS IN FACT BROKEN
			if (Settings.BROADCAST_AUCTION_LIST.getBoolean()) {

				final String prefix = AuctionHouse.getInstance().getLocale().getMessage("general.prefix").getMessage();

				String msgToAll = AuctionHouse.getInstance().getLocale().getMessage(auctionItem.isServerItem() ? "auction.broadcast.serverlisting" : auctionItem.isBidItem() ? "auction.broadcast.withbid" : "auction.broadcast.nobid")
						.processPlaceholder("amount", finalItemToSell.getAmount())
						.processPlaceholder("player", auctionItem.isServerItem() ? SERVER_LISTING_NAME : seller.getName())
						.processPlaceholder("player_displayname", auctionItem.isServerItem() ? SERVER_LISTING_NAME : AuctionAPI.getInstance().getDisplayName(seller))
						.processPlaceholder("item", AuctionAPI.getInstance().getItemName(finalItemToSell))
						.processPlaceholder("base_price", auctionItem.getBasePrice() <= -1 ? NAX : AuctionAPI.getInstance().formatNumber(auctionItem.getBasePrice()))
						.processPlaceholder("start_price", AuctionAPI.getInstance().formatNumber(auctionItem.getBidStartingPrice()))
						.processPlaceholder("increment_price", AuctionAPI.getInstance().formatNumber(auctionItem.getBidIncrementPrice())).getMessage();

				Bukkit.getOnlinePlayers().forEach(p -> {
					if (seller != null && p.getUniqueId().equals(seller.getUniqueId())) return;
					p.sendMessage(TextUtils.formatText((prefix.length() == 0 ? "" : prefix + " ") + msgToAll));
				});
			}
			//====================================================================================


			result.accept(auctionItem, SUCCESS);
		});
	}
}
