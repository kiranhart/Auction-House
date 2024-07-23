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
import ca.tweetzy.auctionhouse.guis.AuctionPagedGUI;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.gui.events.GuiClickEvent;
import ca.tweetzy.flight.utils.Pair;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.flight.utils.Replacer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public final class GUIStatisticLeaderboard extends AuctionPagedGUI<Pair<UUID, Double>> {

	private final AuctionPlayer auctionPlayer;
	private final AuctionStatisticType statisticType;

	public GUIStatisticLeaderboard(AuctionPlayer player, AuctionStatisticType statisticType) {
		super(new GUIStatisticViewSelect(player), player.getPlayer(), Settings.GUI_STATS_LEADERBOARD_TITLE.getString(), 6, new ArrayList<>());
		this.auctionPlayer = player;
		this.statisticType = statisticType;
		setDefaultItem(QuickItem.bg(QuickItem.of(Settings.GUI_STATS_LEADERBOARD_BG_ITEM.getString()).make()));
		draw();
	}

	@Override
	protected void prePopulate() {
		AuctionHouse.getInstance().getAuctionStatisticManager().getStatisticMap(this.statisticType)
				.entrySet()
				.stream()
				.sorted(Map.Entry.<UUID, Double>comparingByValue().reversed())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b, LinkedHashMap::new)).forEach((key, value) -> this.items.add(new Pair<>(key, value)));
	}

	@Override
	protected void drawFixed() {
		applyBackExit();

		setButton(5, 4, QuickItem.of(Settings.GUI_STATS_LEADERBOARD_ITEMS_STAT_ITEM.getString())
				.name(Settings.GUI_STATS_LEADERBOARD_ITEMS_STAT_NAME.getString())
				.lore(this.player,Replacer.replaceVariables(Settings.GUI_STATS_LEADERBOARD_ITEMS_STAT_LORE.getStringList(), "statistic_name", statisticType.getTranslatedType()))
				.make(), click -> click.manager.showGUI(click.player, new GUIStatisticLeaderboard(this.auctionPlayer, this.statisticType.next())));
	}

	@Override
	protected ItemStack makeDisplayItem(Pair<UUID, Double> entry) {
		final OfflinePlayer targetUser = Bukkit.getOfflinePlayer(entry.getFirst());
		final ItemStack head = AuctionAPI.getInstance().getPlayerHead(targetUser.getName());


		return QuickItem
				.of(head)
				.name(Settings.GUI_STATS_LEADERBOARD_ITEMS_PLAYER_NAME.getString().replace("%player_name%", targetUser.getName() == null ? "&e&lUsername not found" : targetUser.getName()))
				.lore(this.player,Replacer.replaceVariables(Settings.GUI_STATS_LEADERBOARD_ITEMS_PLAYER_LORE.getStringList(),
						"player_name", targetUser.getName() == null ? "&e&lUsername not found" : targetUser.getName(),
						"auction_statistic_name", statisticType.getTranslatedType(),
						"auction_statistic_value", AuctionAPI.getInstance().formatNumber(entry.getSecond())
				)).make();
	}

	@Override
	protected void onClick(Pair<UUID, Double> object, GuiClickEvent clickEvent) {

	}
}
