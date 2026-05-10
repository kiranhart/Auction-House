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

package ca.tweetzy.auctionhouse.guis.core;


import ca.tweetzy.auctionhouse.lang.AuctionLocale;
import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.ban.BanType;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.auction.AuctionedItem;
import ca.tweetzy.auctionhouse.guis.AuctionPagedGUI;
import ca.tweetzy.auctionhouse.guis.confirmation.GUIGeneralConfirm;
import ca.tweetzy.auctionhouse.guis.helpers.GUIFilterHelper;
import ca.tweetzy.auctionhouse.helpers.BundleUtil;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.flight.gui.Gui;
import ca.tweetzy.flight.gui.events.GuiClickEvent;
import ca.tweetzy.flight.nbtapi.NBT;
import ca.tweetzy.flight.utils.QuickItem;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: March 15 2021
 * Time Created: 3:19 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUIExpiredItems extends AuctionPagedGUI<AuctionedItem> {

	private final AuctionPlayer auctionPlayer;

	private Long lastClicked = null;
	private Gui parent;


	public GUIExpiredItems(Gui parent, AuctionPlayer auctionPlayer, Long lastClicked) {
		super(parent, auctionPlayer.getPlayer(), AuctionLocale.msg(auctionPlayer.getPlayer(), "gui.expired auctions.title"), 6, new ArrayList<>(auctionPlayer.getItems(true)));
		this.parent = parent;
		this.auctionPlayer = auctionPlayer;
		this.lastClicked = lastClicked;
		draw();
	}

	public GUIExpiredItems(Gui parent, AuctionPlayer auctionPlayer) {
		this(parent, auctionPlayer, null);
	}

	public GUIExpiredItems(AuctionPlayer auctionPlayer, Long lastClicked) {
		this(null, auctionPlayer, lastClicked);
		this.lastClicked = lastClicked;
	}

	@Override
	protected void prePopulate() {
		this.items = this.items.stream()
				.filter(GUIFilterHelper.perWorldFilter(this.auctionPlayer.getPlayer()))
				.sorted(Comparator.comparingLong(AuctionedItem::getExpiresAt).reversed())
				.collect(Collectors.toList());
	}

	@Override
	protected ItemStack makeDisplayItem(AuctionedItem auctionedItem) {
		return auctionedItem.getItem();
	}


	@Override
	protected void onClick(AuctionedItem auctionedItem, GuiClickEvent click) {
		if (AuctionHouse.getBanManager().isStillBanned(click.player, BanType.EVERYTHING, BanType.ITEM_COLLECTION)) return;
		if (click.event.getClickedInventory().getType() == InventoryType.PLAYER) return;

		if (!Settings.ALLOW_INDIVIDUAL_ITEM_CLAIM.getBoolean()) return;

		if (this.lastClicked == null) {
			this.lastClicked = System.currentTimeMillis() + Settings.CLAIM_MS_DELAY.getInt();
		} else if (this.lastClicked > System.currentTimeMillis()) {
			return;
		} else {
			this.lastClicked = System.currentTimeMillis() + Settings.CLAIM_MS_DELAY.getInt();
		}

		if (!AuctionHouse.getAuctionItemManager().isGarbageItem(auctionedItem)) {
			if (Settings.EXPIRE_MENU_REQUIRES_CONFIRM.getBoolean()) {
				click.manager.showGUI(click.player, new GUIGeneralConfirm(this.auctionPlayer, auctionedItem.getItem(), confirmed -> {
					if (confirmed) {
						give(auctionedItem, click);
						// Jsinco - Add else check so this isn't called twice.
					} else {
						click.manager.showGUI(click.player, new GUIExpiredItems(this.parent, this.auctionPlayer, this.lastClicked));
					}
					// End Jsinco
				}));

			} else {
				give(auctionedItem, click);
			}
		}
	}

	// Jsinco - Prevent double reclaiming
	private void give(AuctionedItem auctionedItem, GuiClickEvent click) {
		if (AuctionHouse.getAuctionItemManager().isGarbageItem(auctionedItem)) {
			return;
		}
		List<ItemStack> toGive = buildStacksToGive(auctionedItem);
		giveOrDrop(click.player, toGive);
		AuctionHouse.getAuctionItemManager().sendToGarbage(auctionedItem);
		click.manager.showGUI(click.player, new GUIExpiredItems(this.parent, this.auctionPlayer, this.lastClicked));
	}
	// End Jsinco

	/**
	 * Adds items to the player's inventory; any that don't fit are dropped at the player's feet.
	 */
	private void giveOrDrop(Player player, List<ItemStack> stacks) {
		if (stacks == null || stacks.isEmpty()) return;
		for (ItemStack stack : stacks) {
			if (stack == null || stack.getType().isAir()) continue;
			for (ItemStack leftover : player.getInventory().addItem(stack).values()) {
				if (player.getWorld() != null) {
					player.getWorld().dropItemNaturally(player.getLocation(), leftover);
				}
			}
		}
	}

	/**
	 * Builds the list of item stacks to give/drop for an expired auction item (bundle handling, NBT cleanup, request amount).
	 */
	private List<ItemStack> buildStacksToGive(AuctionedItem auctionItem) {
		final boolean isBundle = BundleUtil.isBundledItem(auctionItem.getItem());
		if (isBundle) {
			if (Settings.BUNDLE_IS_OPENED_ON_RECLAIM.getBoolean()) {
				return BundleUtil.extractBundleItems(auctionItem.getItem());
			}
			return Collections.singletonList(auctionItem.getItem());
		}
		final ItemStack item = auctionItem.getItem();
		NBT.modify(item, nbt -> {
			nbt.removeKey("AuctionDupeTracking");
		});
		if (auctionItem.isRequest()) {
			item.setAmount(1);
			List<ItemStack> list = new ArrayList<>(auctionItem.getRequestAmount());
			for (int i = 0; i < auctionItem.getRequestAmount(); i++) {
				list.add(item.clone());
			}
			return list;
		}
		return Collections.singletonList(item);
	}

	@Override
	protected void drawFixed() {
		setButton(getBackExitButtonSlot(), this.parent == null ? getExitButton() : getBackButton(), click -> {
			if (this.parent == null)
				click.gui.close();
			else
				click.manager.showGUI(click.player, new GUIAuctionHouse(this.auctionPlayer));
		});

		if (Settings.STORE_PAYMENTS_FOR_MANUAL_COLLECTION.getBoolean()) {
			setButton(5, 2, QuickItem
					.of(Settings.GUI_EXPIRED_AUCTIONS_PAYMENTS_ITEM.getString())
					.name(AuctionLocale.msg(this.player, "gui.expired auctions.collect payments.name")).lore(this.player, AuctionLocale.msgList(this.player, "gui.expired auctions.collect payments.lore"))
					.make(), e -> e.manager.showGUI(e.player, new GUIPaymentCollection(this, this.auctionPlayer)));
		}

		setButton(5, 1, QuickItem
				.of(Settings.GUI_EXPIRED_AUCTIONS_ITEM.getString())
				.name(AuctionLocale.msg(this.player, "gui.expired auctions.cancel all.name"))
				.lore(this.player, AuctionLocale.msgList(this.player, "gui.expired auctions.cancel all.lore"))
				.make(), e -> {

			if (AuctionHouse.getBanManager().isStillBanned(e.player, BanType.EVERYTHING, BanType.ITEM_COLLECTION)) return;


			if (this.lastClicked == null) {
				this.lastClicked = System.currentTimeMillis() + Settings.CLAIM_MS_DELAY.getInt();
			} else if (this.lastClicked > System.currentTimeMillis()) {
				return;
			} else {
				this.lastClicked = System.currentTimeMillis() + Settings.CLAIM_MS_DELAY.getInt();
			}

			// Jsinco - Prevent double reclaiming
			for (AuctionedItem auctionItem : this.items) {
				if (AuctionHouse.getAuctionItemManager().isGarbageItem(auctionItem)) {
					// Jsinco - Maybe add some logging to track how this happened
					continue;
				}
				List<ItemStack> toGive = buildStacksToGive(auctionItem);
				giveOrDrop(e.player, toGive);
				AuctionHouse.getAuctionItemManager().sendToGarbage(auctionItem);
			}
			// End Jsinco

			e.manager.showGUI(e.player, new GUIExpiredItems(this.auctionPlayer, this.lastClicked));
		});
	}
}
