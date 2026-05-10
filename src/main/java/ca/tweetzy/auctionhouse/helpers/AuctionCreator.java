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
import ca.tweetzy.auctionhouse.lang.AuctionLocale;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.flight.utils.PlayerUtil;
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
	public static final String SERVER_LISTING_NAME = AuctionLocale.msg(null, "general.server listing");


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
				AuctionLocale.tell(seller, "general.cannot list damaged item");
				result.accept(auctionItem, CANNOT_SELL_DAMAGED_ITEM);
				return;
			}

			if (Settings.PREVENT_SALE_OF_REPAIRED_ITEMS.getBoolean() && AuctionAPI.getInstance().isRepaired(auctionItem.getItem())) {
				AuctionLocale.tell(seller, "general.cannot list repaired item");
				result.accept(auctionItem, CANNOT_SELL_REPAIRED_ITEM);
				return;
			}
		}

		if (!auctionItem.isRequest()) {
			if (!AuctionAPI.getInstance().meetsMinItemPrice(BundleUtil.isBundledItem(auctionItem.getItem()), auctionItem.isBidItem(), auctionItem.getItem(), auctionItem.getBasePrice(), auctionItem.getBidStartingPrice())) {
				AuctionLocale.tell(seller, "pricing.minitemprice", "price",AuctionHouse.getAPI().getNumberAsCurrency(AuctionHouse.getPriceLimitManager().getPriceLimit(auctionItem.getItem()).getMinPrice(), false));

				result.accept(auctionItem, MINIMUM_PRICE_NOT_MET);
				return;
			}

			if (AuctionAPI.getInstance().isAtMaxItemPrice(BundleUtil.isBundledItem(auctionItem.getItem()), auctionItem.isBidItem(), auctionItem.getItem(), auctionItem.getBasePrice(), auctionItem.getBidStartingPrice())) {
				AuctionLocale.tell(seller, "pricing.maxitemprice", "price",AuctionHouse.getAPI().getNumberAsCurrency(AuctionHouse.getPriceLimitManager().getPriceLimit(auctionItem.getItem()).getMaxPrice(), false));

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
				AuctionLocale.tell(seller, "auction.tax.cannotpaylistingfee", "price",AuctionHouse.getAPI().getNumberAsCurrency(listingFee, false));
				result.accept(auctionItem, CANNOT_PAY_LISTING_FEE);
				return;
			}

			AuctionHouse.getCurrencyManager().withdraw(seller, listingFee);
			AuctionLocale.tell(seller, "auction.tax.paidlistingfee", "price",AuctionHouse.getAPI().getFinalizedCurrencyNumber(listingFee, auctionItem.getCurrency(), auctionItem.getCurrencyItem()));

			AuctionLocale.tell(seller, "pricing.moneyremove", "player_balance",AuctionHouse.getCurrencyManager().getFormattedBalance(seller, auctionItem.getCurrency(), auctionItem.getCurrencyItem()),"price",AuctionHouse.getAPI().getFinalizedCurrencyNumber(listingFee, auctionItem.getCurrency(), auctionItem.getCurrencyItem()));
		}

		// final item adjustments
		if (auctionItem.getListedWorld() == null && seller != null)
			auctionItem.setListedWorld(seller.getWorld().getName());

		AuctionStartEvent startEvent = new AuctionStartEvent(seller, auctionItem, listingFee);

		// check if not request
		if (!auctionItem.isRequest()) {
			if (Bukkit.isPrimaryThread()) {
				Bukkit.getServer().getPluginManager().callEvent(startEvent);
			} else {
				Bukkit.getScheduler().runTask(AuctionHouse.getInstance(), () -> Bukkit.getServer().getPluginManager().callEvent(startEvent));
			}
		}

		if (startEvent.isCancelled()) {
			result.accept(auctionItem, EVENT_CANCELED);
			return;
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


		String NAX = AuctionLocale.msg(null, "auction.biditemwithdisabledbuynow");
		String listedKey = auctionItem.isRequest() ? "auction.listed.request" : auctionItem.isBidItem() ? "auction.listed.withbid" : "auction.listed.nobid";
		String msg = AuctionLocale.msg(null, listedKey,
				"amount", finalItemToSell.getAmount(),
				"item", AuctionAPI.getInstance().getItemName(finalItemToSell),
				"base_price", auctionItem.getBasePrice() <= -1 ? NAX : auctionItem.getFormattedBasePrice(),
				"start_price", auctionItem.getFormattedStartingPrice(),
				"increment_price", auctionItem.getFormattedIncrementPrice());

		if (seller != null && !auctionItem.isServerItem()) {
			if (AuctionHouse.getAuctionPlayerManager().getPlayer(seller.getUniqueId()) == null) {
				Common.tell(Bukkit.getConsoleSender(), Common.colorize("&cCould not find auction player instance for&f: &e" + seller.getName() + "&c creating one now."));
				AuctionHouse.getAuctionPlayerManager().addPlayer(new AuctionPlayer(seller));
			}

			if (AuctionHouse.getAuctionPlayerManager().getPlayer(seller.getUniqueId()).isShowListingInfo()) {
				Common.tell(seller, msg);
			}
		}

		//====================================================================================

		// Actually attempt the insertion now
		AuctionHouse.getDataManager().insertAuction(auctionItem, (error, inserted) -> {
			if (auctionPlayer != null)
				auctionPlayer.setItemBeingListed(null);

			// Log request creation if it's a request
			if (AuctionHouse.getTransactionLogger() != null && error == null && inserted != null && inserted.isRequest() && seller != null) {
				String itemName = inserted.getItem().hasItemMeta() && inserted.getItem().getItemMeta().hasDisplayName()
					? inserted.getItem().getItemMeta().getDisplayName()
					: inserted.getItem().getType().name();
				AuctionHouse.getTransactionLogger().logRequestCreate(
					seller.getName(),
					itemName,
					inserted.getItem().getAmount(),
					inserted.getBasePrice(),
					inserted.getCurrency(),
					inserted.getId().toString()
				);
			}

			if (error != null) {
				if (Settings.SHOW_LISTING_ERROR_IN_CONSOLE.getBoolean())
					error.printStackTrace();

				if (seller != null) {
					AuctionLocale.tell(seller, "general.something_went_wrong_while_listing");

					ItemStack originalCopy = auctionItem.getCleanItem().clone();
					int totalOriginal = BundleUtil.isBundledItem(originalCopy) ? AuctionAPI.getInstance().getItemCountInPlayerInventory(seller, originalCopy) : originalCopy.getAmount();

					if (!auctionItem.isRequest()) {
						if (BundleUtil.isBundledItem(originalCopy)) {
							originalCopy.setAmount(1);
							for (int i = 0; i < totalOriginal; i++) PlayerUtil.giveItem(seller, originalCopy);
						} else {
							originalCopy.setAmount(totalOriginal);
							PlayerUtil.giveItem(seller, originalCopy);
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
								AuctionLocale.msg(null, "general.prefix"),
								PaymentReason.LISTING_FAILED,
								auctionItem.getCurrency(),
								auctionItem.getCurrencyItem()
						), null);
					else
						AuctionHouse.getCurrencyManager().deposit(seller, listingFee, auctionItem.getCurrency(), auctionItem.getCurrencyItem());

					AuctionLocale.tell(seller, "pricing.moneyadd", "player_balance",AuctionHouse.getCurrencyManager().getFormattedBalance(seller, auctionItem.getCurrency(), auctionItem.getCurrencyItem()),"price",AuctionHouse.getAPI().getNumberAsCurrency(listingFee, false));
				}

				result.accept(auctionItem, UNKNOWN);
				return;
			}

			AuctionHouse.getAuctionItemManager().addAuctionItem(auctionItem);

			//====================================================================================
			// ANOTHER VERY SHIT BROADCAST THAT IS IN FACT BROKEN
			if (Settings.BROADCAST_AUCTION_LIST.getBoolean() && !auctionItem.isRequest()) {
				final String prefix = AuctionLocale.msg(null, "general.prefix");
				String broadcastKey = auctionItem.isServerItem() ? "auction.broadcast.serverlisting" : auctionItem.isBidItem() ? "auction.broadcast.withbid" : "auction.broadcast.nobid";
				String msgToAll = AuctionLocale.msg(null, broadcastKey,
						"amount", finalItemToSell.getAmount(),
						"player", auctionItem.isServerItem() ? SERVER_LISTING_NAME : seller.getName(),
						"player_displayname", auctionItem.isServerItem() ? SERVER_LISTING_NAME : AuctionAPI.getInstance().getDisplayName(seller),
						"item", AuctionAPI.getInstance().getItemName(finalItemToSell),
						"base_price", auctionItem.getBasePrice() <= -1 ? NAX : auctionItem.getFormattedBasePrice(),
						"start_price", auctionItem.getFormattedStartingPrice(),
						"increment_price", auctionItem.getFormattedIncrementPrice());

				Bukkit.getOnlinePlayers().forEach(p -> {
					if (seller != null && p.getUniqueId().equals(seller.getUniqueId())) return;
					p.sendMessage(Common.colorize((prefix.length() == 0 ? "" : prefix + " ") + msgToAll));
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
