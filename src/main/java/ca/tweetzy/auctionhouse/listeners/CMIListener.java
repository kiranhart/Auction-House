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

package ca.tweetzy.auctionhouse.listeners;

import ca.tweetzy.core.utils.nms.NBTEditor;
import com.Zrips.CMI.events.CMIAnvilItemRepairEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

/**
 * The current file has been created by Kiran Hart
 * Date Created: January 11 2022
 * Time Created: 11:39 a.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public final class CMIListener implements Listener {

	@EventHandler
	public void onCMIRepair(CMIAnvilItemRepairEvent event) {
		ItemStack stack = event.getItemTo();
		if (stack == null) return;

		stack = NBTEditor.set(stack, "AUCTION_REPAIRED", "AuctionHouseRepaired");
		event.setItemTo(stack);
	}
}
