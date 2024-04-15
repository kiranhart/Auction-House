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

import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.auction.AuctionAdminLog;
import ca.tweetzy.auctionhouse.guis.abstraction.AuctionPagedGUI;
import ca.tweetzy.auctionhouse.helpers.ConfigurationItemHelper;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.gui.events.GuiClickEvent;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.flight.utils.Replacer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * The current file has been created by Kiran Hart
 * Date Created: January 17 2022
 * Time Created: 2:11 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public final class GUIAdminLogs extends AuctionPagedGUI<AuctionAdminLog> {

	public GUIAdminLogs(Player player, List<AuctionAdminLog> logs) {
		super(null, player, Settings.GUI_LOGS_TITLE.getString(), 6, logs);
		setAcceptsItems(false);
		draw();
	}

	@Override
	protected void drawFixed() {
		applyBackExit();
	}

	@Override
	protected void prePopulate() {
		this.items.sort(Comparator.comparingLong(AuctionAdminLog::getTime).reversed());
	}

	@Override
	protected ItemStack makeDisplayItem(AuctionAdminLog log) {
		return QuickItem
				.of(log.getItem())
				.name(AuctionAPI.getInstance().getItemName(log.getItem()))
				.lore(Replacer.replaceVariables(Settings.GUI_LOGS_LORE.getStringList(),
						"admin", log.getAdminName(),
						"target", log.getTargetName(),
						"admin_uuid", log.getAdmin(),
						"target_uuid", log.getTarget(),
						"item_id", log.getItemId(),
						"admin_action", log.getAdminAction().getTranslation(),
						"admin_log_date", AuctionAPI.getInstance().convertMillisToDate(log.getTime())
				)).make();
	}

	@Override
	protected void onClick(AuctionAdminLog object, GuiClickEvent clickEvent) {

	}
}
