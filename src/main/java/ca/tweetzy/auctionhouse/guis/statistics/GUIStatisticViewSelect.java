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

package ca.tweetzy.auctionhouse.guis.statistics;

import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.auction.enums.AuctionStatisticType;
import ca.tweetzy.auctionhouse.guis.AuctionBaseGUI;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.flight.utils.QuickItem;

public final class GUIStatisticViewSelect extends AuctionBaseGUI {

	private final AuctionPlayer auctionPlayer;

	public GUIStatisticViewSelect(AuctionPlayer player) {
		super(null, player.getPlayer(), Settings.GUI_STATS_VIEW_SELECT_TITLE.getString(), 3);
		this.auctionPlayer = player;
		setDefaultItem(QuickItem.bg(QuickItem.of(Settings.GUI_STATS_VIEW_SELECT_BG_ITEM.getString()).make()));
		draw();
	}

	@Override
	protected void draw() {
		// self
		setButton(1, 2, QuickItem.of(Settings.GUI_STATS_VIEW_SELECT_ITEMS_PERSONAL_USE_HEAD.getBoolean() ? AuctionAPI.getInstance().getPlayerHead(this.player.getName()) : QuickItem.of(Settings.GUI_STATS_VIEW_SELECT_ITEMS_PERSONAL_ITEM.getString()).make())
				.name(Settings.GUI_STATS_VIEW_SELECT_ITEMS_PERSONAL_NAME.getString())
				.lore(this.player,Settings.GUI_STATS_VIEW_SELECT_ITEMS_PERSONAL_LORE.getStringList())
				.make(), click -> click.manager.showGUI(click.player, new GUIStatisticView(this.auctionPlayer, this.auctionPlayer)));

		setButton(1, 6, QuickItem.of(Settings.GUI_STATS_VIEW_SELECT_ITEMS_LEADERBOARD_ITEM.getString())
				.name(Settings.GUI_STATS_VIEW_SELECT_ITEMS_LEADERBOARD_NAME.getString())
				.lore(this.player,Settings.GUI_STATS_VIEW_SELECT_ITEMS_LEADERBOARD_LORE.getStringList())
				.make(), click -> click.manager.showGUI(click.player, new GUIStatisticLeaderboard(this.auctionPlayer, AuctionStatisticType.MONEY_EARNED)));
	}
}
