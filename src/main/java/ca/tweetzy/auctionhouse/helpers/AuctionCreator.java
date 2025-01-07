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
import ca.tweetzy.auctionhouse.api.auction.ListingResult;
import ca.tweetzy.auctionhouse.auction.AuctionPayment;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.auction.AuctionedItem;
import ca.tweetzy.auctionhouse.auction.enums.PaymentReason;
import ca.tweetzy.auctionhouse.events.AuctionStartEvent;
import ca.tweetzy.auctionhouse.managers.SoundManager;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.utils.PlayerUtils;
import ca.tweetzy.core.utils.TextUtils;
import com.google.gson.JsonObject;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;

import static ca.tweetzy.auctionhouse.api.auction.ListingResult.*;


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
		if (!auctionItem.isServerItem() && !auctionItem.isRequest()) {
			if (seller == null) {
				result.accept(auctionItem, PLAYER_INSTANCE_NOT_FOUND);
				return;
			}

			// Hooks & Special Cases
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

		if (!auctionItem.isRequest()) {
			if (!AuctionAPI.getInstance().meetsMinItemPrice(BundleUtil.isBundledItem(auctionItem.getItem()), auctionItem.isBidItem(), auctionItem.getItem(), auctionItem.getBasePrice(), auctionItem.getBidStartingPrice())) {
				instance.getLocale().getMessage("pricing.minitemprice")
						.processPlaceholder("price", AuctionHouse.getAPI().getNumberAsCurrency(AuctionHouse.getPriceLimitManager().getPriceLimit(auctionItem.getItem()).getMinPrice(), false))
						.sendPrefixedMessage(seller);

				result.accept(auctionItem, MINIMUM_PRICE_NOT_MET);
				return;
			}

			if (AuctionAPI.getInstance().isAtMaxItemPrice(BundleUtil.isBundledItem(auctionItem.getItem()), auctionItem.isBidItem(), auctionItem.getItem(), auctionItem.getBasePrice(), auctionItem.getBidStartingPrice())) {
				instance.getLocale().getMessage("pricing.maxitemprice")
						.processPlaceholder("price", AuctionHouse.getAPI().getNumberAsCurrency(AuctionHouse.getPriceLimitManager().getPriceLimit(auctionItem.getItem()).getMaxPrice(), false))
						.sendPrefixedMessage(seller);

				result.accept(auctionItem, ABOVE_MAXIMUM_PRICE);
				return;
			}
		}

		final ItemStack finalItemToSell = auctionItem.getItem().clone();
		final double originalBasePrice = auctionItem.getBasePrice();

		final double listingFee = Settings.TAX_ENABLED.getBoolean() && Settings.TAX_CHARGE_LISTING_FEE.getBoolean() ? AuctionAPI.getInstance().calculateListingFee(originalBasePrice) : 0;

		// check tax
		if (Settings.TAX_ENABLED.getBoolean() && Settings.TAX_CHARGE_LISTING_FEE.getBoolean() && !auctionItem.isServerItem() && !auctionItem.isRequest()) {
			if (!AuctionHouse.getCurrencyManager().has(seller, listingFee)) {
				instance.getLocale().getMessage("auction.tax.cannotpaylistingfee")
						.processPlaceholder("price", AuctionHouse.getAPI().getNumberAsCurrency(listingFee, false))
						.sendPrefixedMessage(seller);
				result.accept(auctionItem, CANNOT_PAY_LISTING_FEE);
				return;
			}

			AuctionHouse.getCurrencyManager().withdraw(seller, listingFee);
			AuctionHouse.getInstance().getLocale().getMessage("auction.tax.paidlistingfee")
					.processPlaceholder("price", AuctionHouse.getAPI().getFinalizedCurrencyNumber(listingFee, auctionItem.getCurrency(), auctionItem.getCurrencyItem()))
					.sendPrefixedMessage(seller);

			AuctionHouse.getInstance().getLocale().getMessage("pricing.moneyremove")
					.processPlaceholder("player_balance", AuctionHouse.getCurrencyManager().getFormattedBalance(seller, auctionItem.getCurrency(), auctionItem.getCurrencyItem()))
					.processPlaceholder("price", AuctionHouse.getAPI().getFinalizedCurrencyNumber(listingFee, auctionItem.getCurrency(), auctionItem.getCurrencyItem()))
					.sendPrefixedMessage(seller);
		}

		// final item adjustments
		if (auctionItem.getListedWorld() == null && seller != null)
			auctionItem.setListedWorld(seller.getWorld().getName());


		// check if not request
		if (!auctionItem.isRequest()) {
			AuctionStartEvent startEvent = new AuctionStartEvent(seller, auctionItem, listingFee);

			if (Bukkit.isPrimaryThread())
				Bukkit.getServer().getPluginManager().callEvent(startEvent);
			else
				Bukkit.getScheduler().runTask(AuctionHouse.getInstance(), () -> Bukkit.getServer().getPluginManager().callEvent(startEvent));

			if (startEvent.isCancelled()) {
				result.accept(auctionItem, EVENT_CANCELED);
				return;
			}
		}

		// overwrite to be random uuid since it's a server auction

		if (auctionItem.isServerItem() && !auctionItem.isRequest()) {
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
		String msg = AuctionHouse.getInstance().getLocale().getMessage(auctionItem.isRequest() ? "auction.listed.request" : auctionItem.isBidItem() ? "auction.listed.withbid" : "auction.listed.nobid")
				.processPlaceholder("amount", finalItemToSell.getAmount())
				.processPlaceholder("item", AuctionAPI.getInstance().getItemName(finalItemToSell))
				.processPlaceholder("base_price", auctionItem.getBasePrice() <= -1 ? NAX : auctionItem.getFormattedBasePrice())
				.processPlaceholder("start_price", auctionItem.getFormattedStartingPrice())
				.processPlaceholder("increment_price", auctionItem.getFormattedIncrementPrice()).getMessage();

		if (seller != null && !auctionItem.isServerItem()) {
			if (AuctionHouse.getAuctionPlayerManager().getPlayer(seller.getUniqueId()) == null) {
				AuctionHouse.getInstance().getLocale().newMessage(TextUtils.formatText("&cCould not find auction player instance for&f: &e" + seller.getName() + "&c creating one now.")).sendPrefixedMessage(Bukkit.getConsoleSender());
				AuctionHouse.getAuctionPlayerManager().addPlayer(new AuctionPlayer(seller));
			}

			if (AuctionHouse.getAuctionPlayerManager().getPlayer(seller.getUniqueId()).isShowListingInfo()) {
				AuctionHouse.getInstance().getLocale().newMessage(msg).sendPrefixedMessage(seller);
			}
		}

		//====================================================================================

		// Actually attempt the insertion now
		AuctionHouse.getDataManager().insertAuction(auctionItem, (error, inserted) -> {
			if (auctionPlayer != null)
				auctionPlayer.setItemBeingListed(null);

			if (error != null) {
				if (Settings.SHOW_LISTING_ERROR_IN_CONSOLE.getBoolean())
					error.printStackTrace();

				if (seller != null) {
					instance.getLocale().getMessage("general.something_went_wrong_while_listing").sendPrefixedMessage(seller);

					ItemStack originalCopy = auctionItem.getCleanItem().clone();
					int totalOriginal = BundleUtil.isBundledItem(originalCopy) ? AuctionAPI.getInstance().getItemCountInPlayerInventory(seller, originalCopy) : originalCopy.getAmount();

					if (!auctionItem.isRequest()) {
						if (BundleUtil.isBundledItem(originalCopy)) {
							originalCopy.setAmount(1);
							for (int i = 0; i < totalOriginal; i++) PlayerUtils.giveItem(seller, originalCopy);
						} else {
							originalCopy.setAmount(totalOriginal);
							PlayerUtils.giveItem(seller, originalCopy);
						}
					}
				}

				// If the item could not be added for whatever reason and the tax listing fee is enabled, refund them
				if (Settings.TAX_ENABLED.getBoolean() && Settings.TAX_CHARGE_LISTING_FEE.getBoolean() && !auctionItem.isServerItem() && !auctionItem.isRequest() && seller != null) {
					if (Settings.STORE_PAYMENTS_FOR_MANUAL_COLLECTION.getBoolean())
						AuctionHouse.getDataManager().insertAuctionPayment(new AuctionPayment(
								seller.getUniqueId(),
								listingFee,
								auctionItem.getItem(),
								AuctionHouse.getInstance().getLocale().getMessage("general.prefix").getMessage(),
								PaymentReason.LISTING_FAILED,
								auctionItem.getCurrency(),
								auctionItem.getCurrencyItem()
						), null);
					else
						AuctionHouse.getCurrencyManager().deposit(seller, listingFee, auctionItem.getCurrency(), auctionItem.getCurrencyItem());

					AuctionHouse.getInstance().getLocale().getMessage("pricing.moneyadd")
							.processPlaceholder("player_balance", AuctionHouse.getCurrencyManager().getFormattedBalance(seller, auctionItem.getCurrency(), auctionItem.getCurrencyItem()))
							.processPlaceholder("price", AuctionHouse.getAPI().getNumberAsCurrency(listingFee, false))
							.sendPrefixedMessage(seller);
				}

				result.accept(auctionItem, UNKNOWN);
				return;
			}

			AuctionHouse.getAuctionItemManager().addAuctionItem(auctionItem);

			//====================================================================================
			// ANOTHER VERY SHIT BROADCAST THAT IS IN FACT BROKEN
			if (Settings.BROADCAST_AUCTION_LIST.getBoolean() && !auctionItem.isRequest()) {
				final String prefix = AuctionHouse.getInstance().getLocale().getMessage("general.prefix").getMessage();
				String msgToAll = AuctionHouse.getInstance().getLocale().getMessage(auctionItem.isServerItem() ? "auction.broadcast.serverlisting" : auctionItem.isBidItem() ? "auction.broadcast.withbid" : "auction.broadcast.nobid")
						.processPlaceholder("amount", finalItemToSell.getAmount())
						.processPlaceholder("player", auctionItem.isServerItem() ? SERVER_LISTING_NAME : seller.getName())
						.processPlaceholder("player_displayname", auctionItem.isServerItem() ? SERVER_LISTING_NAME : AuctionAPI.getInstance().getDisplayName(seller))
						.processPlaceholder("item", AuctionAPI.getInstance().getItemName(finalItemToSell))
						.processPlaceholder("base_price", auctionItem.getBasePrice() <= -1 ? NAX : auctionItem.getFormattedBasePrice())
						.processPlaceholder("start_price", auctionItem.getFormattedStartingPrice())
						.processPlaceholder("increment_price", auctionItem.getFormattedIncrementPrice()).getMessage();

				Bukkit.getOnlinePlayers().forEach(p -> {
					if (seller != null && p.getUniqueId().equals(seller.getUniqueId())) return;
					p.sendMessage(TextUtils.formatText((prefix.length() == 0 ? "" : prefix + " ") + msgToAll));
				});
			}
			//====================================================================================


			result.accept(auctionItem, SUCCESS);
		});
	}

	private String getSimplifiedItemJson(ItemStack item) {
		JsonObject itemJson = new JsonObject();
		itemJson.addProperty("id", item.getType().getKey().toString());
		itemJson.addProperty("Count", item.getAmount());

		if (item.hasItemMeta()) {
			ItemMeta meta = item.getItemMeta();
			if (meta.hasDisplayName()) {
				itemJson.addProperty("tag", "{display:{Name:'" +
						ChatColor.stripColor(meta.getDisplayName()) + "'}}");
			}
		}

		return itemJson.toString();
	}

}
