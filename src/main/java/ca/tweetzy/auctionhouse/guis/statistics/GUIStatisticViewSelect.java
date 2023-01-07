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
import ca.tweetzy.auctionhouse.guis.AbstractPlaceholderGui;
import ca.tweetzy.auctionhouse.helpers.ConfigurationItemHelper;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.flight.comp.enums.CompMaterial;

public final class GUIStatisticViewSelect extends AbstractPlaceholderGui {

	private final AuctionPlayer auctionPlayer;

	public GUIStatisticViewSelect(AuctionPlayer player) {
		super(player);
		this.auctionPlayer = player;
		setTitle(Settings.GUI_STATS_VIEW_SELECT_TITLE.getString());
		getConfirmBuyNoItem()(ConfigurationItemHelper.createConfigurationItem(Settings.GUI_STATS_VIEW_SELECT_BG_ITEM.getString()));
		setUseLockedCells(true);
		setAcceptsItems(false);
		setAllowDrops(false);
		setRows(3);
		draw();
	}

	private void draw() {

		// self
		setButton(1, 2, ConfigurationItemHelper.createConfigurationItem(
				Settings.GUI_STATS_VIEW_SELECT_ITEMS_PERSONAL_USE_HEAD.getBoolean() ?
						AuctionAPI.getInstance().getPlayerHead(this.player.getName()) : CompMaterial.matchCompMaterial(Settings.GUI_STATS_VIEW_SELECT_ITEMS_PERSONAL_ITEM.getString()).orElse(CompMaterial.STONE).parseItem(),
				Settings.GUI_STATS_VIEW_SELECT_ITEMS_PERSONAL_NAME.getString(),
				Settings.GUI_STATS_VIEW_SELECT_ITEMS_PERSONAL_LORE.getStringList(),
				null
		), click -> click.manager.showGUI(click.player, new GUIStatisticSelf(this.auctionPlayer)));

		setButton(1, 6, ConfigurationItemHelper.createConfigurationItem(
				CompMaterial.matchCompMaterial(Settings.GUI_STATS_VIEW_SELECT_ITEMS_LEADERBOARD_ITEM.getString()).orElse(CompMaterial.STONE).parseItem(),
				Settings.GUI_STATS_VIEW_SELECT_ITEMS_LEADERBOARD_NAME.getString(),
				Settings.GUI_STATS_VIEW_SELECT_ITEMS_LEADERBOARD_LORE.getStringList(),
				null
		), click -> click.manager.showGUI(click.player, new GUIStatisticLeaderboard(this.auctionPlayer, AuctionStatisticType.MONEY_EARNED)));
	}
}
