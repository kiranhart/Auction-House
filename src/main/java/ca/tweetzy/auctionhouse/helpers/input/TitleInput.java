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

import ca.tweetzy.flight.utils.Common;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * The current file has been created by Kiran Hart
 * Date Created: November 08 2021
 * Time Created: 4:56 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public abstract class TitleInput extends Input {

	private final Player player;
	private final String title;
	private final String subTitle;
	private final String actionbar;

	public TitleInput(@NonNull final Player player, final String title, final String subTitle, final String actionbar) {
		super(player);
		this.player = player;
		this.title = title;
		this.subTitle = subTitle;
		this.actionbar = Common.colorize(actionbar);
	}

	public TitleInput(@NonNull final Player player, final String title, final String subTitle) {
		this(player, Common.colorize(title), Common.colorize(subTitle), Common.colorize(""));
	}

	public abstract boolean onResult(String string);

	public boolean onInput(String text) {
		if (this.onResult(text)) {
			this.close(true);
		}
		return true;
	}

	@EventHandler
	public void close(PlayerInteractEvent e) {
		if (e.getPlayer().equals(this.player) && (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK)) {
			this.close(false);
		}
	}

	@Override
	public String getTitle() {
		return this.title;
	}

	@Override
	public String getSubtitle() {
		return this.subTitle;
	}

	@Override
	public String getActionBar() {
		return this.actionbar;
	}
}
