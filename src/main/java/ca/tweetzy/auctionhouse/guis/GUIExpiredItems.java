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
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.auction.AuctionedItem;
import ca.tweetzy.auctionhouse.guis.abstraction.AuctionPagedGUI;
import ca.tweetzy.auctionhouse.helpers.BundleUtil;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.gui.Gui;
import ca.tweetzy.core.gui.events.GuiClickEvent;
import ca.tweetzy.core.utils.PlayerUtils;
import ca.tweetzy.flight.nbtapi.NBT;
import ca.tweetzy.flight.utils.QuickItem;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
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

	public GUIExpiredItems(Gui parent, AuctionPlayer auctionPlayer) {
		super(parent, auctionPlayer.getPlayer(), Settings.GUI_EXPIRED_AUCTIONS_TITLE.getString(), 6, new ArrayList<>(auctionPlayer.getItems(true)));
		this.auctionPlayer = auctionPlayer;
		draw();
	}

	public GUIExpiredItems(AuctionPlayer auctionPlayer, Long lastClicked) {
		this(null, auctionPlayer);
		this.lastClicked = lastClicked;
	}

	@Override
	protected void prePopulate() {
		if (Settings.PER_WORLD_ITEMS.getBoolean()) {
			this.items = this.items.stream().filter(item -> item.getListedWorld() == null || this.auctionPlayer.getPlayer().getWorld().getName().equals(item.getListedWorld())).collect(Collectors.toList());
		}

		this.items.sort(Comparator.comparingLong(AuctionedItem::getExpiresAt).reversed());
	}

	@Override
	protected ItemStack makeDisplayItem(AuctionedItem auctionedItem) {
		return auctionedItem.getItem();
	}

	@Override
	protected void onClick(AuctionedItem auctionedItem, GuiClickEvent click) {
		if (!Settings.ALLOW_INDIVIDUAL_ITEM_CLAIM.getBoolean()) return;

		final boolean isBundle = BundleUtil.isBundledItem(auctionedItem.getItem());

		if (click.player.getInventory().firstEmpty() == -1) {
			AuctionHouse.getInstance().getLocale().getMessage("general.noroomclaim").sendPrefixedMessage(click.player);
			return;
		}

		if (this.lastClicked == null) {
			this.lastClicked = System.currentTimeMillis() + Settings.CLAIM_MS_DELAY.getInt();
		} else if (this.lastClicked > System.currentTimeMillis()) {
			return;
		} else {
			this.lastClicked = System.currentTimeMillis() + Settings.CLAIM_MS_DELAY.getInt();
		}

		if (isBundle) {
			if (Settings.BUNDLE_IS_OPENED_ON_RECLAIM.getBoolean()) {
				final List<ItemStack> bundleItems = BundleUtil.extractBundleItems(auctionedItem.getItem());
				PlayerUtils.giveItem(click.player, bundleItems);
			} else {
				PlayerUtils.giveItem(click.player, auctionedItem.getItem());
			}
		} else {
			final ItemStack item = auctionedItem.getItem();

			NBT.modify(item, nbt -> {
				nbt.removeKey("AuctionDupeTracking");
			});

			PlayerUtils.giveItem(click.player, item);
		}

		AuctionHouse.getInstance().getAuctionItemManager().sendToGarbage(auctionedItem);
		click.manager.showGUI(click.player, new GUIExpiredItems(this.auctionPlayer, this.lastClicked));
	}

	@Override
	protected void drawFixed() {
		applyBackExit();

		if (Settings.STORE_PAYMENTS_FOR_MANUAL_COLLECTION.getBoolean()) {
			setButton(5, 2, QuickItem
					.of(Settings.GUI_EXPIRED_AUCTIONS_PAYMENTS_ITEM.getString())
					.name(Settings.GUI_EXPIRED_AUCTIONS_PAYMENTS_NAME.getString()).lore(Settings.GUI_EXPIRED_AUCTIONS_PAYMENTS_LORE.getStringList())
					.make(), e -> e.manager.showGUI(e.player, new GUIPaymentCollection(this, this.auctionPlayer)));
		}

		setButton(5, 1, QuickItem
				.of(Settings.GUI_EXPIRED_AUCTIONS_ITEM.getString())
				.name(Settings.GUI_EXPIRED_AUCTIONS_NAME.getString())
				.lore(Settings.GUI_EXPIRED_AUCTIONS_LORE.getStringList())
				.make(), e -> {

			if (this.lastClicked == null) {
				this.lastClicked = System.currentTimeMillis() + Settings.CLAIM_MS_DELAY.getInt();
			} else if (this.lastClicked > System.currentTimeMillis()) {
				return;
			} else {
				this.lastClicked = System.currentTimeMillis() + Settings.CLAIM_MS_DELAY.getInt();
			}

			for (AuctionedItem auctionItem : this.items) {
				final boolean isBundle = BundleUtil.isBundledItem(auctionItem.getItem());

				if (e.player.getInventory().firstEmpty() == -1) {
					AuctionHouse.getInstance().getLocale().getMessage("general.noroomclaim").sendPrefixedMessage(e.player);
					break;
				}

				if (isBundle) {
					if (Settings.BUNDLE_IS_OPENED_ON_RECLAIM.getBoolean()) {
						final List<ItemStack> bundleItems = BundleUtil.extractBundleItems(auctionItem.getItem());
						PlayerUtils.giveItem(e.player, bundleItems);
					} else {
						PlayerUtils.giveItem(e.player, auctionItem.getItem());
					}
				} else {
					final ItemStack item = auctionItem.getItem();
					// remove the dupe tracking
					NBT.modify(item, nbt -> {
						nbt.removeKey("AuctionDupeTracking");
					});

					PlayerUtils.giveItem(e.player, item);
				}

				AuctionHouse.getInstance().getAuctionItemManager().sendToGarbage(auctionItem);
			}

			e.manager.showGUI(e.player, new GUIExpiredItems(this.auctionPlayer, this.lastClicked));
		});
	}
}
