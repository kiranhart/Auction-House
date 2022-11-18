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

package ca.tweetzy.auctionhouse.managers;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.auction.AuctionBan;
import ca.tweetzy.core.utils.TimeUtils;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The current file has been created by Kiran Hart
 * Date Created: July 21 2021
 * Time Created: 2:27 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class AuctionBanManager {

	private final ConcurrentHashMap<UUID, AuctionBan> bans = new ConcurrentHashMap<>();

	public void addBan(AuctionBan ban) {
		if (ban == null) return;
		this.bans.put(ban.getBannedPlayer(), ban);
	}

	public void removeBan(UUID player) {
		if (player == null) return;
		this.bans.remove(player);
	}

	public ConcurrentHashMap<UUID, AuctionBan> getBans() {
		return this.bans;
	}

	public boolean checkAndHandleBan(Player player) {
		if (this.bans.containsKey(player.getUniqueId())) {
			long time = this.bans.get(player.getUniqueId()).getTime();
			if (System.currentTimeMillis() >= time) {
				removeBan(player.getUniqueId());
				return false;
			}
			AuctionHouse.getInstance().getLocale().getMessage("bans.remainingtime").processPlaceholder("ban_amount", TimeUtils.makeReadable(time - System.currentTimeMillis())).sendPrefixedMessage(player);
			return true;
		}
		return false;
	}

	public void loadBans() {
		AuctionHouse.getInstance().getDataManager().getBans(all -> all.forEach(this::addBan));
	}

	public void saveBans(boolean async) {
		AuctionHouse.getInstance().getDataManager().saveBans(new ArrayList<>(getBans().values()), async);
	}
}