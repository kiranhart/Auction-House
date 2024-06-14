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
import ca.tweetzy.auctionhouse.api.ban.Ban;
import ca.tweetzy.auctionhouse.guis.AuctionUpdatingPagedGUI;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.gui.events.GuiClickEvent;
import ca.tweetzy.core.utils.TimeUtils;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.flight.utils.Replacer;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

/**
 * The current file has been created by Kiran Hart
 * Date Created: July 22 2021
 * Time Created: 12:16 a.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUIBans extends AuctionUpdatingPagedGUI<Ban> {

	public GUIBans(Player player) {
		super(null, player, Settings.GUI_BANS_TITLE.getString(), 6, 20, new ArrayList<>());
		setDefaultItem(QuickItem.bg(QuickItem.of(Settings.GUI_BANS_BG_ITEM.getString()).make()));

		startTask();
		applyClose();
		draw();
	}

	@Override
	protected void prePopulate() {
		this.items = new ArrayList<>(AuctionHouse.getInstance().getBanManager().getManagerContent().values());
	}

	protected void drawFixed() {
		applyBackExit();
	}

	@Override
	protected ItemStack makeDisplayItem(Ban ban) {
		final OfflinePlayer offlinePlayer = ban.locatePlayer();

		return QuickItem
				.of(AuctionAPI.getInstance().getPlayerHead(offlinePlayer.getName()))
				.name(Replacer.replaceVariables(Settings.GUI_BANS_BAN_NAME.getString(), "player_name", offlinePlayer.getName()))
				.lore(Replacer.replaceVariables(Settings.GUI_BANS_BAN_LORE.getStringList(),
						"player_name", offlinePlayer.getName(),
						"player_displayname", AuctionAPI.getInstance().getDisplayName(offlinePlayer),
						"ban_reason", ban.getReason(),
						"ban_amount", (ban.getExpireDate() - System.currentTimeMillis()) <= 0 ? AuctionHouse.getInstance().getLocale().getMessage("bans.ban expired").getMessage() : TimeUtils.makeReadable(ban.getExpireDate() - System.currentTimeMillis())
				)).make();
	}

	@Override
	protected void onClick(Ban ban, GuiClickEvent click) {
		final OfflinePlayer offlinePlayer = ban.locatePlayer();

//		AuctionHouse.getInstance().getAuctionBanManager().removeBan(ban.getBannedPlayer());


		// TODO REMOVE BAN
		AuctionHouse.getInstance().getLocale().getMessage("bans.playerunbanned").processPlaceholder("player_displayname", AuctionAPI.getInstance().getDisplayName(offlinePlayer)).processPlaceholder("player", offlinePlayer.getName()).sendPrefixedMessage(click.player);
		if (offlinePlayer.isOnline()) {
			AuctionHouse.getInstance().getLocale().getMessage("bans.unbanned").processPlaceholder("player_displayname", AuctionAPI.getInstance().getDisplayName(offlinePlayer)).processPlaceholder("player", offlinePlayer.getName()).sendPrefixedMessage(offlinePlayer.getPlayer());
		}

		draw();
	}
}
