/*
 * Auction House
 * Copyright 2024 Kiran Hart
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



import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.auctionhouse.settings.Translations;
import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.auction.AuctionedItem;
import ca.tweetzy.auctionhouse.auction.enums.AuctionSaleType;
import ca.tweetzy.auctionhouse.guis.AuctionBaseGUI;
import ca.tweetzy.auctionhouse.guis.core.GUIAuctionHouse;
import ca.tweetzy.auctionhouse.helpers.AuctionCreator;
import ca.tweetzy.flight.utils.input.TitleInput;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.flight.utils.MathUtil;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.flight.utils.Replacer;
import lombok.NonNull;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class GUIRequestItem extends AuctionBaseGUI {

	private final AuctionPlayer auctionPlayer;
	private final ItemStack itemRequested;
	private final int amount;
	private final double price;

	public GUIRequestItem(@NonNull final AuctionPlayer auctionPlayer, ItemStack itemRequested, final int amount, final double price) {
		super(null, auctionPlayer.getPlayer(), TranslationManager.string(auctionPlayer.getPlayer(), Translations.GUI_REQUEST_TITLE), 6);
		this.auctionPlayer = auctionPlayer;
		this.itemRequested = itemRequested;
		this.amount = amount;
		this.price = price;
		draw();
	}

	@Override
	protected void draw() {
		applyBackExit();
		setItem(1, 4, this.itemRequested);

		setButton(3, 2, QuickItem
				.of(Settings.GUI_REQUEST_ITEMS_AMT_ITEM.getString())
				.name(TranslationManager.string(this.player, Translations.GUI_REQUEST_AMT_NAME))
				.lore(this.player, Replacer.replaceVariables(TranslationManager.list(this.player, Translations.GUI_REQUEST_AMT_LORE), "request_amount", amount))
				.make(), click -> {

		// TitleInput automatically handles allowClose and inventory closing
		new TitleInput(AuctionHouse.getInstance(), click.player, TranslationManager.string(Translations.TITLES_ENTER_REQUEST_AMOUNT_TITLE), TranslationManager.string(Translations.TITLES_ENTER_REQUEST_AMOUNT_SUBTITLE)) {

				@Override
				public void onExit(Player player) {
					click.manager.showGUI(player, GUIRequestItem.this);
				}

				@Override
				public boolean onResult(String string) {
					string = ChatColor.stripColor(string);

					if (!MathUtil.isInt(string)) {
						Common.tell(player, TranslationManager.string(player instanceof Player pl ? pl : null, Translations.GENERAL_NOT_A_NUMBER, "value",string));
						return false;
					}

					int requestAmount = Integer.parseInt(string);
					if (requestAmount <= 0)
						requestAmount = GUIRequestItem.this.itemRequested.getAmount();

					if (requestAmount > Settings.MAX_REQUEST_AMOUNT.getInt()) {
						Common.tell(player, TranslationManager.string(player instanceof Player pl ? pl : null, Translations.GENERAL_HIGH_REQUEST_COUNT));
						return false;
					}

					GUIRequestItem.this.itemRequested.setAmount(requestAmount);

					click.manager.showGUI(click.player, new GUIRequestItem(
							GUIRequestItem.this.auctionPlayer,
							GUIRequestItem.this.itemRequested,
							requestAmount,
							GUIRequestItem.this.price
					));

					return true;
				}
			};
		});

		setButton(3, 6, QuickItem
				.of(Settings.GUI_REQUEST_ITEMS_PRICE_ITEM.getString())
				.name(TranslationManager.string(this.player, Translations.GUI_REQUEST_PRICE_NAME))
				.lore(this.player, Replacer.replaceVariables(TranslationManager.list(this.player, Translations.GUI_REQUEST_PRICE_LORE), "request_price", AuctionHouse.getAPI().getNumberAsCurrency(price, false)))
				.make(), click -> {

		// TitleInput automatically handles allowClose and inventory closing
		new TitleInput(AuctionHouse.getInstance(), click.player, TranslationManager.string(Translations.TITLES_ENTER_REQUEST_PRICE_TITLE), TranslationManager.string(Translations.TITLES_ENTER_REQUEST_PRICE_SUBTITLE)) {

				@Override
				public void onExit(Player player) {
					click.manager.showGUI(player, GUIRequestItem.this);
				}

				@Override
				public boolean onResult(String string) {
					string = ChatColor.stripColor(string);

					if (!MathUtil.isDouble(string)) {
						Common.tell(player, TranslationManager.string(player instanceof Player pl ? pl : null, Translations.GENERAL_NOT_A_NUMBER, "value",string));
						return false;
					}

					double newPrice = Double.parseDouble(string);

					if (Double.isNaN(newPrice)) {
						Common.tell(player, TranslationManager.string(player instanceof Player pl ? pl : null, Translations.GENERAL_NOT_A_NUMBER, "value",string));
						return false;
					}

					if (newPrice <= 0) {
						Common.tell(player, TranslationManager.string(player instanceof Player pl ? pl : null, Translations.GENERAL_CANNOT_BE_ZERO, "value",string));
						return false;
					}

					if (newPrice > Settings.MAX_REQUEST_PRICE.getDouble()) {
						Common.tell(player, TranslationManager.string(player instanceof Player pl ? pl : null, Translations.PRICING_REQUEST_MAX_PRICE, "price",Settings.MAX_REQUEST_PRICE.getDouble()));
						return false;
					}

					if (newPrice < Settings.MIN_REQUEST_PRICE.getDouble()) {
						Common.tell(player, TranslationManager.string(player instanceof Player pl ? pl : null, Translations.PRICING_REQUEST_MIN_PRICE, "price",Settings.MIN_REQUEST_PRICE.getDouble()));
						return false;
					}

					click.manager.showGUI(click.player, new GUIRequestItem(
							GUIRequestItem.this.auctionPlayer,
							GUIRequestItem.this.itemRequested,
							GUIRequestItem.this.amount,
							newPrice
					));

					return true;
				}
			};
		});


		setButton(getRows() - 1, 4, QuickItem
				.of(Settings.GUI_REQUEST_ITEMS_REQUEST_ITEM.getString())
				.name(TranslationManager.string(this.player, Translations.GUI_REQUEST_REQUEST_NAME))
				.lore(this.player, TranslationManager.list(this.player, Translations.GUI_REQUEST_REQUEST_LORE))
				.make(), click -> {

			// Check for block items
			if (!AuctionAPI.getInstance().meetsListingRequirements(player, this.itemRequested)) return;

			// check if at limit
			if (auctionPlayer.isAtItemLimit(player)) {
				Common.tell(player, TranslationManager.string(player instanceof Player pl ? pl : null, Translations.GENERAL_REQUEST_LIMIT));
				return;
			}

			// get the max allowed time for this player.
			final int allowedTime = auctionPlayer.getAllowedSellTime(AuctionSaleType.WITHOUT_BIDDING_SYSTEM);

			// Check list delay
			if (!auctionPlayer.canListItem()) {
				return;
			}

			// check min/max prices
			if (price < Settings.MIN_AUCTION_PRICE.getDouble()) {
				Common.tell(player, TranslationManager.string(player instanceof Player pl ? pl : null, Translations.PRICING_MIN_BASE_PRICE, "price",Settings.MIN_AUCTION_PRICE.getDouble()));
				return;
			}

			if (price > Settings.MAX_AUCTION_PRICE.getDouble()) {
				Common.tell(player, TranslationManager.string(player instanceof Player pl ? pl : null, Translations.PRICING_MAX_BASE_PRICE, "price",Settings.MIN_AUCTION_PRICE.getDouble()));
				return;
			}

			AuctionedItem auctionedItem = AuctionedItem.createRequest(player, this.itemRequested, this.amount, this.price, allowedTime);

			AuctionHouse.getInstance().getAuctionPlayerManager().addToSellProcess(player);
			if (auctionPlayer.getPlayer() == null || !auctionPlayer.getPlayer().isOnline()) {
				return;
			}

			AuctionCreator.create(auctionPlayer, auctionedItem, (auction, listingResult) -> {
				AuctionHouse.getInstance().getAuctionPlayerManager().processSell(player);

				if (Settings.OPEN_MAIN_AUCTION_HOUSE_AFTER_MENU_LIST.getBoolean()) {
					player.removeMetadata("AuctionHouseConfirmListing", AuctionHouse.getInstance());
					AuctionHouse.getInstance().getGuiManager().showGUI(player, new GUIAuctionHouse(auctionPlayer));
				} else
					AuctionHouse.newChain().sync(player::closeInventory).execute();
			});
		});
	}
}
