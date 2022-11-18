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

package ca.tweetzy.auctionhouse.api.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

/**
 * The current file has been created by Kiran Hart
 * Date Created: July 21 2021
 * Time Created: 3:38 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */

@Getter
@Setter
public class AuctionBanPlayerEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled;

	private Player banner;
	private UUID bannedPlayerUUID;
	private String reason;
	private long seconds;

	public AuctionBanPlayerEvent(Player banner, UUID bannedPlayerUUID, String reason, long seconds, boolean async) {
		super(async);
		this.banner = banner;
		this.bannedPlayerUUID = bannedPlayerUUID;
		this.reason = reason;
		this.seconds = seconds;
	}

	public AuctionBanPlayerEvent(Player banner, UUID bannedPlayerUUID, String reason, long seconds) {
		this(banner, bannedPlayerUUID, reason, seconds, true);
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
