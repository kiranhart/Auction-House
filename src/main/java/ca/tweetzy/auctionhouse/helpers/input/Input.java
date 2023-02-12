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

package ca.tweetzy.auctionhouse.helpers.input;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.flight.comp.ActionBar;
import ca.tweetzy.flight.comp.Titles;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

public abstract class Input implements Listener, Runnable {

	private final Player player;
	private String title;
	private String subtitle;

	private final BukkitTask task;

	public Input(@NonNull final Player player) {
		this.player = player;
		Bukkit.getServer().getScheduler().runTaskLater(AuctionHouse.getInstance(), player::closeInventory, 1L);
		this.task = Bukkit.getServer().getScheduler().runTaskTimer(AuctionHouse.getInstance(), this, 1, 1);
		Bukkit.getServer().getPluginManager().registerEvents(this, AuctionHouse.getInstance());
	}

	public void onExit(final Player player) {
	}

	public void onDeath(final Player player) {
	}

	public abstract boolean onInput(final String input);

	public abstract String getTitle();

	public abstract String getSubtitle();

	public abstract String getActionBar();

	@Override
	public void run() {
		final String title = this.getTitle();
		final String subTitle = this.getSubtitle();
		final String actionBar = this.getActionBar();

		if (this.title == null || this.subtitle == null || !this.title.equals(title) || !this.subtitle.equals(subTitle)) {
			Titles.sendTitle(this.player, 10, 6000, 0, title, subTitle);
			this.title = title;
			this.subtitle = subTitle;
		}

		if (actionBar != null) ActionBar.sendActionBar(this.player, actionBar);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onChat(AsyncPlayerChatEvent e) {
		if (e.getPlayer().equals(this.player)) {
			if (ChatColor.stripColor(e.getMessage()).equals(Settings.TITLE_INPUT_CANCEL_WORD.getString())) {
				e.setCancelled(true);
				this.close(false, false);
			}

			this.onInput(e.getMessage());
			e.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onCommandExecute(PlayerCommandPreprocessEvent event) {
		if (event.getPlayer().equals(this.player)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void close(PlayerQuitEvent e) {
		if (e.getPlayer().equals(this.player)) {
			this.close(false, false);
		}
	}

	@EventHandler
	public void close(InventoryOpenEvent e) {
		if (e.getPlayer().equals(this.player)) {
			this.close(false, false);
		}
	}

	@EventHandler
	public void death(PlayerDeathEvent e) {
		if (e.getEntity().equals(this.player)) {
			this.close(true, false);
		}
	}

	public void close(boolean byDeath, boolean completed) {
		HandlerList.unregisterAll(this);
		this.task.cancel();

		if (byDeath) this.onDeath(this.player);
		else {
			if (!completed) {
				this.onExit(this.player);
			}
		}

		Titles.clearTitle(this.player);
		ActionBar.clearActionBar(this.player);
	}
}
