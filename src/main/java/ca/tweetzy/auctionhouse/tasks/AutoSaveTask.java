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

package ca.tweetzy.auctionhouse.tasks;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.utils.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * The current file has been created by Kiran Hart
 * Date Created: April 15 2021
 * Time Created: 2:28 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class AutoSaveTask extends BukkitRunnable {

	private static AutoSaveTask instance;

	public static AutoSaveTask startTask() {
		if (instance == null) {
			instance = new AutoSaveTask();
			instance.runTaskTimerAsynchronously(AuctionHouse.getInstance(), 20 * 5, (long) 20 * Settings.AUTO_SAVE_EVERY.getInt());
		}
		return instance;
	}

	@Override
	public void run() {
		final AuctionHouse instance = AuctionHouse.getInstance();
		instance.getDataManager().updateItemsAsync(instance.getAuctionItemManager().getItems().values(), null);
		instance.getFilterManager().saveFilterWhitelist(true);

		if (!Settings.DISABLE_AUTO_SAVE_MSG.getBoolean())
			instance.getLocale().newMessage(TextUtils.formatText("&aAuto saved auction items & transactions")).sendPrefixedMessage(Bukkit.getConsoleSender());
	}
}
