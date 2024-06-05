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

package ca.tweetzy.auctionhouse.guis.admin;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.auction.AuctionAdminLog;
import ca.tweetzy.auctionhouse.auction.AuctionPayment;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.auction.AuctionedItem;
import ca.tweetzy.auctionhouse.auction.enums.AdminAction;
import ca.tweetzy.auctionhouse.auction.enums.PaymentReason;
import ca.tweetzy.auctionhouse.events.AuctionAdminEvent;
import ca.tweetzy.auctionhouse.guis.abstraction.AuctionBaseGUI;
import ca.tweetzy.auctionhouse.guis.core.GUIAuctionHouseV2;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.hooks.EconomyManager;
import ca.tweetzy.core.utils.PlayerUtils;
import ca.tweetzy.flight.utils.QuickItem;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * The current file has been created by Kiran Hart
 * Date Created: July 14 2021
 * Time Created: 3:04 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUIAdminItem extends AuctionBaseGUI {

	private final AuctionPlayer auctionPlayer;
	private final AuctionedItem auctionItem;

	public GUIAdminItem(AuctionPlayer auctionPlayer, AuctionedItem auctionItem) {
		super(new GUIAuctionHouseV2(auctionPlayer), auctionPlayer.getPlayer(), Settings.GUI_ITEM_ADMIN_TITLE.getString(), 3);
		this.auctionPlayer = auctionPlayer;
		this.auctionItem = auctionItem;
		setAcceptsItems(false);
		setUseLockedCells(true);
		setOnClose(close -> close.manager.showGUI(close.player, new GUIAuctionHouseV2(this.auctionPlayer)));
		draw();
	}

	@Override
	protected void draw() {
		applyBackExit();

		drawReturnButton();
		drawClaimButton();
		drawDeleteButton();
		drawCopyButton();
	}

	private void drawReturnButton() {

		if (Settings.ADMIN_OPTION_SHOW_RETURN_ITEM.getBoolean())
			setButton(1, 1, QuickItem.of(Settings.GUI_ITEM_ADMIN_ITEMS_RETURN_ITEM.getString()).name(Settings.GUI_ITEM_ADMIN_ITEMS_RETURN_NAME.getString()).lore(Settings.GUI_ITEM_ADMIN_ITEMS_RETURN_LORE.getStringList()).make(), click -> {

				if (!click.player.hasPermission("auctionhouse.admin.returnitem")) return;

				AuctionAdminEvent event = new AuctionAdminEvent(createLog(click.player, AdminAction.RETURN_ITEM));
				Bukkit.getServer().getPluginManager().callEvent(event);
				if (event.isCancelled()) return;

				if (!this.auctionItem.isServerItem()) {
					this.auctionItem.setExpiresAt(System.currentTimeMillis());
					this.auctionItem.setExpired(true);
				}

				if (Settings.BIDDING_TAKES_MONEY.getBoolean() && !this.auctionItem.getHighestBidder().equals(this.auctionItem.getOwner())) {
					final OfflinePlayer oldBidder = Bukkit.getOfflinePlayer(this.auctionItem.getHighestBidder());

					if (Settings.STORE_PAYMENTS_FOR_MANUAL_COLLECTION.getBoolean())
						AuctionHouse.getInstance().getDataManager().insertAuctionPayment(new AuctionPayment(oldBidder.getUniqueId(), auctionItem.getCurrentPrice(), auctionItem.getItem(), click.player.getName(), PaymentReason.ADMIN_REMOVED), null);
					else EconomyManager.deposit(oldBidder, auctionItem.getCurrentPrice());

					if (oldBidder.isOnline())
						AuctionHouse.getInstance().getLocale().getMessage("pricing.moneyadd").processPlaceholder("player_balance", AuctionAPI.getInstance().formatNumber(EconomyManager.getBalance(oldBidder))).processPlaceholder("price", AuctionAPI.getInstance().formatNumber(this.auctionItem.getCurrentPrice())).sendPrefixedMessage(oldBidder.getPlayer());

				}

				click.gui.close();
			});
	}

	private void drawClaimButton() {
		if (Settings.ADMIN_OPTION_SHOW_CLAIM_ITEM.getBoolean())
			setButton(1, 3, QuickItem.of(Settings.GUI_ITEM_ADMIN_ITEMS_CLAIM_ITEM.getString()).name(Settings.GUI_ITEM_ADMIN_ITEMS_CLAIM_NAME.getString()).lore(Settings.GUI_ITEM_ADMIN_ITEMS_CLAIM_LORE.getStringList()).make(), click -> {

				if (!click.player.hasPermission("auctionhouse.admin.claimitem")) return;

				AuctionAdminEvent event = new AuctionAdminEvent(createLog(click.player, AdminAction.CLAIM_ITEM));
				Bukkit.getServer().getPluginManager().callEvent(event);
				if (event.isCancelled()) return;

				PlayerUtils.giveItem(click.player, this.auctionItem.getItem());

				if (Settings.BIDDING_TAKES_MONEY.getBoolean() && !this.auctionItem.getHighestBidder().equals(this.auctionItem.getOwner())) {
					final OfflinePlayer oldBidder = Bukkit.getOfflinePlayer(this.auctionItem.getHighestBidder());

					if (Settings.STORE_PAYMENTS_FOR_MANUAL_COLLECTION.getBoolean())
						AuctionHouse.getInstance().getDataManager().insertAuctionPayment(new AuctionPayment(oldBidder.getUniqueId(), auctionItem.getCurrentPrice(), auctionItem.getItem(), click.player.getName(), PaymentReason.ADMIN_REMOVED), null);
					else EconomyManager.deposit(oldBidder, auctionItem.getCurrentPrice());

					if (oldBidder.isOnline())
						AuctionHouse.getInstance().getLocale().getMessage("pricing.moneyadd").processPlaceholder("player_balance", AuctionAPI.getInstance().formatNumber(EconomyManager.getBalance(oldBidder))).processPlaceholder("price", AuctionAPI.getInstance().formatNumber(this.auctionItem.getCurrentPrice())).sendPrefixedMessage(oldBidder.getPlayer());

				}

				AuctionHouse.getInstance().getAuctionItemManager().sendToGarbage(this.auctionItem);
				click.gui.close();
			});
	}

	private void drawDeleteButton() {
		if (Settings.ADMIN_OPTION_SHOW_DELETE_ITEM.getBoolean())
			setButton(1, 5, QuickItem.of(Settings.GUI_ITEM_ADMIN_ITEMS_DELETE_ITEM.getString()).name(Settings.GUI_ITEM_ADMIN_ITEMS_DELETE_NAME.getString()).lore(Settings.GUI_ITEM_ADMIN_ITEMS_DELETE_LORE.getStringList()).make(), click -> {

				if (!click.player.hasPermission("auctionhouse.admin.deleteitem")) return;
				AuctionAdminEvent event = new AuctionAdminEvent(createLog(click.player, AdminAction.DELETE_ITEM));
				Bukkit.getServer().getPluginManager().callEvent(event);
				if (event.isCancelled()) return;

				if (Settings.BIDDING_TAKES_MONEY.getBoolean() && !this.auctionItem.getHighestBidder().equals(this.auctionItem.getOwner())) {
					final OfflinePlayer oldBidder = Bukkit.getOfflinePlayer(this.auctionItem.getHighestBidder());

					if (Settings.STORE_PAYMENTS_FOR_MANUAL_COLLECTION.getBoolean())
						AuctionHouse.getInstance().getDataManager().insertAuctionPayment(new AuctionPayment(
								oldBidder.getUniqueId(),
								auctionItem.getCurrentPrice(),
								auctionItem.getItem(),
								click.player.getName(),
								PaymentReason.ADMIN_REMOVED
						), null);
					else
						EconomyManager.deposit(oldBidder, auctionItem.getCurrentPrice());

					if (oldBidder.isOnline())
						AuctionHouse.getInstance().getLocale().getMessage("pricing.moneyadd").processPlaceholder("player_balance", AuctionAPI.getInstance().formatNumber(EconomyManager.getBalance(oldBidder))).processPlaceholder("price", AuctionAPI.getInstance().formatNumber(this.auctionItem.getCurrentPrice())).sendPrefixedMessage(oldBidder.getPlayer());

				}

				AuctionHouse.getInstance().getAuctionItemManager().sendToGarbage(this.auctionItem);
				click.gui.close();
			});
	}

	private void drawCopyButton() {
		if (Settings.ADMIN_OPTION_SHOW_COPY_ITEM.getBoolean())
			setButton(1, 7, QuickItem.of(Settings.GUI_ITEM_ADMIN_ITEMS_COPY_ITEM.getString()).name(Settings.GUI_ITEM_ADMIN_ITEMS_COPY_NAME.getString()).lore(Settings.GUI_ITEM_ADMIN_ITEMS_COPY_LORE.getStringList()).make(), click -> {

				if (!click.player.hasPermission("auctionhouse.admin.copyitem")) return;
				if (Settings.ITEM_COPY_REQUIRES_GMC.getBoolean() && click.player.getGameMode() != GameMode.CREATIVE) {
					AuctionHouse.getInstance().getLocale().getMessage("general.requires creative").sendPrefixedMessage(click.player);
					return;
				}

				AuctionAdminEvent event = new AuctionAdminEvent(createLog(click.player, AdminAction.COPY_ITEM));
				Bukkit.getServer().getPluginManager().callEvent(event);
				if (event.isCancelled()) return;

				PlayerUtils.giveItem(click.player, this.auctionItem.getItem());
				click.gui.close();
			});

	}

	private AuctionAdminLog createLog(final Player player, AdminAction adminAction) {
		return new AuctionAdminLog(player.getUniqueId(), player.getName(), auctionItem.getOwner(), auctionItem.getOwnerName(), auctionItem.getItem(), auctionItem.getId(), adminAction, System.currentTimeMillis());
	}
}
