/*
 * Auction House
 * Copyright 2022 Kiran Hart
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

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.auction.enums.AuctionStatisticType;
import ca.tweetzy.auctionhouse.guis.AbstractPlaceholderGui;
import ca.tweetzy.auctionhouse.helpers.ConfigurationItemHelper;
import ca.tweetzy.auctionhouse.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public final class GUIStatisticLeaderboard extends AbstractPlaceholderGui {

	private final AuctionPlayer auctionPlayer;
	private final AuctionStatisticType statisticType;

	private Map<UUID, Double> statisticsMap;

	public GUIStatisticLeaderboard(AuctionPlayer player, AuctionStatisticType statisticType) {
		super(player);
		this.auctionPlayer = player;
		this.statisticType = statisticType;
		setTitle(Settings.GUI_STATS_VIEW_SELECT_TITLE.getString());
		setDefaultItem(Settings.GUI_STATS_VIEW_SELECT_BG_ITEM.getMaterial().parseItem());
		setUseLockedCells(true);
		setAcceptsItems(false);
		setAllowDrops(false);
		setRows(6);
		draw();
	}

	private void draw() {

		AuctionHouse.newChain().asyncFirst(() -> {
			this.statisticsMap = AuctionHouse.getInstance().getAuctionStatisticManager().getStatisticMap(this.statisticType)
					.entrySet()
					.stream()
					.sorted(Map.Entry.<UUID, Double>comparingByValue().reversed())
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b, LinkedHashMap::new));



			return this.statisticsMap;
		}).asyncLast((data) -> {
			pages = (int) Math.max(1, Math.ceil(this.statisticsMap.size() / (double) 45L));

			int slot = 0;
			for (Map.Entry<UUID, Double> playerStat : data.entrySet()) {
				final ItemStack head = AuctionAPI.getInstance().getPlayerHead(Bukkit.getOfflinePlayer(playerStat.getKey()).getName());

				setItem(slot++, ConfigurationItemHelper.createConfigurationItem(head, "STAT", Arrays.asList(
						playerStat.getKey() + " == uuid",
						playerStat.getValue() + " == val"
				), null));
			}
		}).execute();

		setButton(5, 0, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_CLOSE_BTN_ITEM.getString(), Settings.GUI_CLOSE_BTN_NAME.getString(), Settings.GUI_CLOSE_BTN_LORE.getStringList(), null), e -> {
			e.manager.showGUI(e.player, new GUIStatisticViewSelect(this.auctionPlayer));
		});
	}
}
