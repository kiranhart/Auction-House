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
import ca.tweetzy.auctionhouse.guis.confirmation.GUIConfirmCancel;
import ca.tweetzy.auctionhouse.helpers.ConfigurationItemHelper;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.compatibility.XSound;
import ca.tweetzy.core.hooks.EconomyManager;
import ca.tweetzy.core.utils.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitTask;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: March 14 2021
 * Time Created: 10:33 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUIActiveAuctions extends AbstractPlaceholderGui {

	private final AuctionPlayer auctionPlayer;
	private BukkitTask task;

	private List<AuctionedItem> items;

	public GUIActiveAuctions(AuctionPlayer auctionPlayer) {
		super(auctionPlayer);
		this.auctionPlayer = auctionPlayer;
		setTitle(TextUtils.formatText(Settings.GUI_ACTIVE_AUCTIONS_TITLE.getString()));
		setRows(6);
		setAcceptsItems(false);
		setNavigateSound(XSound.matchXSound(Settings.SOUNDS_NAVIGATE_GUI_PAGES.getString()).orElse(XSound.ENTITY_BAT_TAKEOFF));
		draw();

		if (Settings.AUTO_REFRESH_AUCTION_PAGES.getBoolean()) {
			setOnOpen(e -> makeMess());
			setOnClose(e -> cleanup());
		}
	}

	private void draw() {
		reset();
		drawFixedButtons();
		drawItems();
	}

	private void drawItems() {
		AuctionHouse.newChain().asyncFirst(() -> {
			this.items = this.auctionPlayer.getItems(false);

			// per world check
			if (Settings.PER_WORLD_ITEMS.getBoolean()) {
				this.items = this.items.stream().filter(item -> item.getListedWorld() == null || this.auctionPlayer.getPlayer().getWorld().getName().equals(item.getListedWorld())).collect(Collectors.toList());
			}

			return this.items.stream().sorted(Comparator.comparingLong(AuctionedItem::getExpiresAt).reversed()).skip((page - 1) * 45L).limit(45).collect(Collectors.toList());
		}).asyncLast((data) -> {
			pages = (int) Math.max(1, Math.ceil(this.items.size() / (double) 45L));
			drawPaginationButtons();

			int slot = 0;
			for (AuctionedItem item : data) {
				setButton(slot++, item.getDisplayStack(AuctionStackType.ACTIVE_AUCTIONS_LIST), e -> {
					switch (e.clickType) {
						case LEFT:
							if (Settings.SELLERS_MUST_WAIT_FOR_TIME_LIMIT_AFTER_BID.getBoolean()) {
								AuctionHouse.getInstance().getLocale().getMessage("general.cannot cancel item with bid").sendPrefixedMessage(e.player);
								return;
							}

							if (((item.getBidStartingPrice() > 0 || item.getBidIncrementPrice() > 0) && Settings.ASK_FOR_CANCEL_CONFIRM_ON_BID_ITEMS.getBoolean()) || Settings.ASK_FOR_CANCEL_CONFIRM_ON_NON_BID_ITEMS.getBoolean()) {
								if (item.getHighestBidder().equals(e.player.getUniqueId())) {
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
								cleanup();
								e.manager.showGUI(e.player, new GUIConfirmCancel(this.auctionPlayer, item));
								return;
							}

							item.setExpired(true);
							draw();
							break;
						case RIGHT:
							if (Settings.ALLOW_PLAYERS_TO_ACCEPT_BID.getBoolean() && item.getBidStartingPrice() != 0 && !item.getHighestBidder().equals(e.player.getUniqueId())) {
								item.setExpiresAt(System.currentTimeMillis());
								draw();
							}
							break;
					}
				});
			}
		}).execute();
	}

	private void drawFixedButtons() {
		setButton(5, 0, getBackButtonItem(), e -> {
			cleanup();
			e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
		});

		setButton(5, 4, getRefreshButtonItem(), e -> e.manager.showGUI(e.player, new GUIActiveAuctions(this.auctionPlayer)));

		setButton(5, 1, ConfigurationItemHelper.createConfigurationItem(this.player, Settings.GUI_ACTIVE_AUCTIONS_ITEM.getString(), Settings.GUI_ACTIVE_AUCTIONS_NAME.getString(), Settings.GUI_ACTIVE_AUCTIONS_LORE.getStringList(), null), e -> {
			this.auctionPlayer.getItems(false).forEach(item -> item.setExpired(true));
			draw();
		});
	}

	private void drawPaginationButtons() {
		setPrevPage(5, 3, getPreviousPageItem());
		setNextPage(5, 5, getNextPageItem());
		setOnPage(e -> draw());
	}

	/*
	====================== AUTO REFRESH ======================
	 */
	private void makeMess() {
		task = Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(AuctionHouse.getInstance(), this::drawItems, 0L, (long) 20 * Settings.TICK_UPDATE_GUI_TIME.getInt());
	}

	private void cleanup() {
		if (task != null) task.cancel();
	}
}
