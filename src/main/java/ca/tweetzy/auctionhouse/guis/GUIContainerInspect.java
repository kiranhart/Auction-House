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
import ca.tweetzy.auctionhouse.guis.confirmation.GUIConfirmPurchase;
import ca.tweetzy.auctionhouse.helpers.BundleUtil;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.gui.events.GuiClickEvent;
import ca.tweetzy.flight.utils.QuickItem;
import org.bukkit.block.ShulkerBox;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The current file has been created by Kiran Hart
 * Date Created: April 14 2021
 * Time Created: 12:28 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUIContainerInspect extends AuctionPagedGUI<ItemStack> {

	private final ItemStack container;
	private AuctionPlayer auctionPlayer;
	private AuctionedItem auctionItem;
	private boolean buyingSpecificQuantity;
	private boolean fromPurchaseGUI;

	/**
	 * Used to inspect a shulker box from it's item stack.
	 *
	 * @param container is the shulker box
	 */
	public GUIContainerInspect(AuctionPlayer auctionPlayer, ItemStack container) {
		super(null, auctionPlayer.getPlayer(), Settings.GUI_INSPECT_TITLE.getString(), 6, new ArrayList<>());
		this.container = container;
		this.fromPurchaseGUI = false;
		setDefaultItem(QuickItem.bg(QuickItem.of(Settings.GUI_INSPECT_BG_ITEM.getString()).make()));
		setUseLockedCells(false);
		setAcceptsItems(false);
		setAllowDrops(false);

		if (BundleUtil.isBundledItem(this.container)) {
			this.items = BundleUtil.extractBundleItems(this.container);
		}

		if (this.container.getType().name().contains("SHULKER_BOX")) {
			BlockStateMeta meta = (BlockStateMeta) this.container.getItemMeta();
			ShulkerBox skulkerBox = (ShulkerBox) meta.getBlockState();
			this.items = Arrays.asList(skulkerBox.getInventory().getContents());
		}

		setOnClose(close -> close.manager.showGUI(close.player, new GUIAuctionHouse(AuctionHouse.getInstance().getAuctionPlayerManager().getPlayer(close.player.getUniqueId()))));
		draw();
	}

	public GUIContainerInspect(ItemStack container, AuctionPlayer auctionPlayer, AuctionedItem auctionItem, boolean buyingSpecificQuantity) {
		this(auctionPlayer, container);
		this.auctionPlayer = auctionPlayer;
		this.auctionItem = auctionItem;
		this.buyingSpecificQuantity = buyingSpecificQuantity;
		this.fromPurchaseGUI = true;

		// Overwrite the default close, since they are accessing the inspection from the purchase screen
		setOnClose(close -> {
			AuctionHouse.getInstance().getTransactionManager().addPrePurchase(close.player, auctionItem.getId());
			close.manager.showGUI(close.player, new GUIConfirmPurchase(this.auctionPlayer, this.auctionItem, this.buyingSpecificQuantity));
		});
	}

	@Override
	protected void drawFixed() {
		setButton(5, 0, getBackButton(), e -> {
			if (fromPurchaseGUI) {
				AuctionHouse.getInstance().getTransactionManager().addPrePurchase(e.player, auctionItem.getId());
				e.manager.showGUI(e.player, new GUIConfirmPurchase(this.auctionPlayer, this.auctionItem, this.buyingSpecificQuantity));
			} else {
				e.manager.showGUI(e.player, new GUIAuctionHouse(AuctionHouse.getInstance().getAuctionPlayerManager().getPlayer(e.player.getUniqueId())));
			}
		});
	}

	@Override
	protected ItemStack makeDisplayItem(ItemStack item) {
		return item;
	}

	@Override
	protected void onClick(ItemStack item, GuiClickEvent click) {
	}

	@Override
	protected List<Integer> fillSlots() {
		return Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 46, 47, 48, 50, 51, 52, 53);
	}
}
