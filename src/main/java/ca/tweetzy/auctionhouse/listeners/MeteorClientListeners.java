/*
 * Auction House
 * Copyright 2023 Kiran Hart
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

package ca.tweetzy.auctionhouse.listeners;

import ca.tweetzy.auctionhouse.AuctionHouse;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class MeteorClientListeners implements Listener {

	static boolean isCancelled = false;

	@EventHandler
	public void onCraftDuringSell(final PrepareItemCraftEvent event) {
		final CraftingInventory craftingInventory = event.getInventory();

		if (AuctionHouse.getAuctionPlayerManager().isInSellProcess((Player) craftingInventory.getViewers().get(0)) && !isCancelled) {
			isCancelled = true;
			craftingInventory.setMatrix(new ItemStack[0]);
			craftingInventory.setResult(null);
			isCancelled = false;
		}
	}

	@EventHandler
	public void onPlayerPlaceItemIntoFrame(final PlayerInteractEntityEvent event) {
		final Player player = event.getPlayer();

		if (AuctionHouse.getInstance().getAuctionPlayerManager().isInSellProcess(player)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onItemDropDuringSell(final PlayerDropItemEvent event) {
		final Player player = event.getPlayer();

		if (AuctionHouse.getInstance().getAuctionPlayerManager().isInSellProcess(player)) event.setCancelled(true);
	}

	@EventHandler
	public void onHotbarSwapDuringSell(final PlayerItemHeldEvent event) {
		final Player player = event.getPlayer();

		if (AuctionHouse.getInstance().getAuctionPlayerManager().isInSellProcess(player)) event.setCancelled(true);
	}

	@EventHandler
	public void onOffhandSwap(final PlayerSwapHandItemsEvent event) {
		final Player player = event.getPlayer();

		if (AuctionHouse.getInstance().getAuctionPlayerManager().isInSellProcess(player)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onItemRemove(final InventoryClickEvent event) {
		final HumanEntity clicker = event.getWhoClicked();
		if (!(clicker instanceof Player)) return;

		final Player player = (Player) clicker;
		if (AuctionHouse.getInstance().getAuctionPlayerManager().isInSellProcess(player)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onCommandDuringSell(final PlayerCommandPreprocessEvent event) {
		final Player player = event.getPlayer();

		if (AuctionHouse.getInstance().getAuctionPlayerManager().isInSellProcess(player)) event.setCancelled(true);
	}

	@EventHandler
	public void onBuildDuringSell(final BlockPlaceEvent event) {
		final Player player = event.getPlayer();

		if (AuctionHouse.getInstance().getAuctionPlayerManager().isInSellProcess(player)) {
			event.setBuild(false);
			event.setCancelled(true);
		}
	}

	public Inventory getTopInventory(InventoryEvent event) {
		try {
			Object view = event.getView();
			Method getTopInventory = view.getClass().getMethod("getTopInventory");
			getTopInventory.setAccessible(true);
			return (Inventory) getTopInventory.invoke(view);
		} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

}
