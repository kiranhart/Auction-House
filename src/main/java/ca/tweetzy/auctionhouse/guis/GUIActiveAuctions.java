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

package ca.tweetzy.auctionhouse.guis;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.auction.AuctionPayment;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.auction.AuctionedItem;
import ca.tweetzy.auctionhouse.auction.enums.AuctionStackType;
import ca.tweetzy.auctionhouse.auction.enums.PaymentReason;
import ca.tweetzy.auctionhouse.guis.abstraction.AuctionUpdatingPagedGUI;
import ca.tweetzy.auctionhouse.guis.confirmation.GUIConfirmCancel;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.gui.events.GuiClickEvent;
import ca.tweetzy.core.hooks.EconomyManager;
import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.flight.utils.messages.Titles;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: March 14 2021
 * Time Created: 10:33 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUIActiveAuctions extends AuctionUpdatingPagedGUI<AuctionedItem> {

	private final AuctionPlayer auctionPlayer;

	public GUIActiveAuctions(AuctionPlayer auctionPlayer) {
		super(new GUIAuctionHouse(auctionPlayer), auctionPlayer.getPlayer(), Settings.GUI_ACTIVE_AUCTIONS_TITLE.getString(), 6, 20 * Settings.TICK_UPDATE_GUI_TIME.getInt(), new ArrayList<>());
		this.auctionPlayer = auctionPlayer;

		if (Settings.AUTO_REFRESH_ACTIVE_AUCTION_PAGES.getBoolean()) {
			startTask();
		}

		applyClose();
		draw();
	}

	@Override
	protected void prePopulate() {
		this.items = this.auctionPlayer.getItems(false);

		if (Settings.PER_WORLD_ITEMS.getBoolean()) {
			this.items = this.items.stream().filter(item -> item.getListedWorld() == null || this.auctionPlayer.getPlayer().getWorld().getName().equals(item.getListedWorld())).collect(Collectors.toList());
		}

		this.items.sort(Comparator.comparingLong(AuctionedItem::getExpiresAt).reversed());
	}

	@Override
	protected ItemStack makeDisplayItem(AuctionedItem auctionedItem) {
		return auctionedItem.isRequest() ? auctionedItem.getDisplayRequestStack(AuctionStackType.ACTIVE_AUCTIONS_LIST) : auctionedItem.getDisplayStack(AuctionStackType.ACTIVE_AUCTIONS_LIST);
	}

	@Override
	protected void onClick(AuctionedItem item, GuiClickEvent click) {
		switch (click.clickType) {
			case LEFT:
				if (item.isRequest()) {
					AuctionHouse.getInstance().getAuctionItemManager().sendToGarbage(item);
					cancelTask();
					click.manager.showGUI(click.player, new GUIActiveAuctions(this.auctionPlayer));
					return;
				}

				if (Settings.SELLERS_MUST_WAIT_FOR_TIME_LIMIT_AFTER_BID.getBoolean() && item.containsValidBid()) {
					AuctionHouse.getInstance().getLocale().getMessage("general.cannot cancel item with bid").sendPrefixedMessage(click.player);
					return;
				}

				if (((item.getBidStartingPrice() > 0 || item.getBidIncrementPrice() > 0) && Settings.ASK_FOR_CANCEL_CONFIRM_ON_BID_ITEMS.getBoolean()) || Settings.ASK_FOR_CANCEL_CONFIRM_ON_NON_BID_ITEMS.getBoolean()) {
					if (item.getHighestBidder().equals(click.player.getUniqueId()) && item.isBidItem()) {
						item.setExpired(true);
						item.setExpiresAt(System.currentTimeMillis());
						if (Settings.BIDDING_TAKES_MONEY.getBoolean() && !item.getHighestBidder().equals(item.getOwner())) {
							final OfflinePlayer oldBidder = Bukkit.getOfflinePlayer(item.getHighestBidder());

							if (Settings.STORE_PAYMENTS_FOR_MANUAL_COLLECTION.getBoolean())
								AuctionHouse.getInstance().getDataManager().insertAuctionPayment(new AuctionPayment(
										oldBidder.getUniqueId(),
										item.getCurrentPrice(),
										item.getItem(),
										AuctionHouse.getInstance().getLocale().getMessage("general.prefix").getMessage(),
										PaymentReason.BID_RETURNED
								), null);
							else
								EconomyManager.deposit(oldBidder, item.getCurrentPrice());

							if (oldBidder.isOnline())
								AuctionHouse.getInstance().getLocale().getMessage("pricing.moneyadd").processPlaceholder("player_balance", AuctionAPI.getInstance().formatNumber(EconomyManager.getBalance(oldBidder))).processPlaceholder("price", AuctionAPI.getInstance().formatNumber(item.getCurrentPrice())).sendPrefixedMessage(oldBidder.getPlayer());

						}

						draw();
						return;
					}
					cancelTask();
					click.manager.showGUI(click.player, new GUIConfirmCancel(this.auctionPlayer, item));
					return;
				}

				item.setExpired(true);
				draw();
				break;
			case RIGHT:
				if (Settings.ALLOW_PLAYERS_TO_ACCEPT_BID.getBoolean() && item.getBidStartingPrice() != 0 && !item.getHighestBidder().equals(click.player.getUniqueId())) {
					item.setExpiresAt(System.currentTimeMillis());
					draw();
				}
				break;
		}
	}

	@Override
	protected void drawFixed() {
		if (this.parent == null) {
			setButton(getBackExitButtonSlot(), getExitButton(), click -> click.gui.close());
		} else {
			setButton(getBackExitButtonSlot(), getBackButton(), click -> {
				cancelTask();
				click.manager.showGUI(click.player, this.parent);
			});
		}

		setButton(5, 4, getRefreshButton(), e -> {
			cancelTask();
			e.manager.showGUI(e.player, new GUIActiveAuctions(this.auctionPlayer));
		});

		setButton(5, 1, QuickItem
				.of(Settings.GUI_ACTIVE_AUCTIONS_ITEM.getString())
				.name(Settings.GUI_ACTIVE_AUCTIONS_NAME.getString())
				.lore(Settings.GUI_ACTIVE_AUCTIONS_LORE.getStringList())
				.make(), e -> {

			if (Settings.ASK_FOR_CANCEL_CONFIRM_ON_ALL_ITEMS.getBoolean()) {
				cancelTask();
				e.gui.exit();

				Titles.sendTitle(e.player,
						20,
						20 * 5,
						20,
						Common.colorize(AuctionHouse.getInstance().getLocale().getMessage("titles.end all confirm.title").getMessage()),
						Common.colorize(AuctionHouse.getInstance().getLocale().getMessage("titles.end all confirm.subtitle").getMessage())
				);

				// reset the request time for cancel
				this.auctionPlayer.setEndAllRequestTime(System.currentTimeMillis() + (1000 * 30));
				return;
			}


			for (AuctionedItem item : this.auctionPlayer.getItems(false)) {
				if (Settings.SELLERS_MUST_WAIT_FOR_TIME_LIMIT_AFTER_BID.getBoolean() && item.containsValidBid())
					continue;

				if (item.isRequest()) {
					AuctionHouse.getInstance().getAuctionItemManager().sendToGarbage(item);
				} else {
					item.setExpired(true);
				}
			}
			draw();
		});
	}
}
