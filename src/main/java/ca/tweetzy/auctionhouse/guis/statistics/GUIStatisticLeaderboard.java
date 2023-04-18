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

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.auction.enums.AuctionStatisticType;
import ca.tweetzy.auctionhouse.guis.AbstractPlaceholderGui;
import ca.tweetzy.auctionhouse.helpers.ConfigurationItemHelper;
import ca.tweetzy.auctionhouse.managers.SoundManager;
import ca.tweetzy.auctionhouse.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
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
		setTitle(Settings.GUI_STATS_LEADERBOARD_TITLE.getString());
		setDefaultItem(ConfigurationItemHelper.createConfigurationItem(Settings.GUI_STATS_LEADERBOARD_BG_ITEM.getString()));
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


			return this.statisticsMap.entrySet().stream().skip((page - 1) * 45L).limit(45L).collect(Collectors.toList());
		}).asyncLast((data) -> {
			pages = (int) Math.max(1, Math.ceil(this.statisticsMap.size() / (double) 45L));
			drawPaginationButtons();

			int slot = 0;
			for (Map.Entry<UUID, Double> playerStat : data) {
				final OfflinePlayer targetUser = Bukkit.getOfflinePlayer(playerStat.getKey());

				final ItemStack head = AuctionAPI.getInstance().getPlayerHead(targetUser.getName());

				setItem(slot++, ConfigurationItemHelper.createConfigurationItem(
						head,
						Settings.GUI_STATS_LEADERBOARD_ITEMS_PLAYER_NAME.getString(),
						Settings.GUI_STATS_LEADERBOARD_ITEMS_PLAYER_LORE.getStringList(),
						new HashMap<String, Object>() {{
							put("%player_name%", targetUser.getName() == null ? "&e&lUsername not found" : targetUser.getName());
							put("%auction_statistic_name%", statisticType.getTranslatedType());
							put("%auction_statistic_value%", AuctionAPI.getInstance().formatNumber(playerStat.getValue()));
						}}
				));
			}
		}).execute();

		setButton(5, 0, getBackButtonItem(), e -> {
			e.manager.showGUI(e.player, new GUIStatisticViewSelect(this.auctionPlayer));
		});

		drawStatisticTypeButton();
	}

	private void drawPaginationButtons() {
		setPrevPage(5, 3, getPreviousPageItem());
		setNextPage(5, 5, getNextPageItem());
		setOnPage(e -> {
			draw();
			SoundManager.getInstance().playSound(this.auctionPlayer.getPlayer(), Settings.SOUNDS_NAVIGATE_GUI_PAGES.getString());
		});
	}

	private void drawStatisticTypeButton() {
		setButton(5, 4, ConfigurationItemHelper.createConfigurationItem(
				Settings.GUI_STATS_LEADERBOARD_ITEMS_STAT_ITEM.getString(),
				Settings.GUI_STATS_LEADERBOARD_ITEMS_STAT_NAME.getString(),
				Settings.GUI_STATS_LEADERBOARD_ITEMS_STAT_LORE.getStringList(), new HashMap<String, Object>() {{
					put("%statistic_name%", statisticType.getTranslatedType());
				}}), click -> {

			click.manager.showGUI(click.player, new GUIStatisticLeaderboard(this.auctionPlayer, this.statisticType.next()));
		});
	}
}
