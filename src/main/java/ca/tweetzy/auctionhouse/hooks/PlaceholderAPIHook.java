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

package ca.tweetzy.auctionhouse.hooks;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.auction.enums.AuctionStatisticType;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.temporal.ChronoUnit;
import java.util.OptionalInt;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

/**
 * The current file has been created by Kiran Hart
 * Date Created: August 07 2021
 * Time Created: 6:36 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class PlaceholderAPIHook extends PlaceholderExpansion {

	private final AuctionHouse plugin;

	public PlaceholderAPIHook(AuctionHouse plugin) {
		this.plugin = plugin;
	}


	@Override
	public @NotNull
	String getIdentifier() {
		return "auctionhouse";
	}

	@Override
	public @NotNull
	String getAuthor() {
		return "KiranHart";
	}

	@Override
	public @NotNull
	String getVersion() {
		return "1.0.0";
	}

	@Override
	public boolean persist() {
		return true;
	}

	@Override
	public String onRequest(OfflinePlayer player, @NotNull String params) {
		if (params.equalsIgnoreCase("name")) return player == null ? null : player.getName();

		// legacy placeholders
		if (params.equalsIgnoreCase("total_money_earned"))
			return String.valueOf(AuctionHouse.getAuctionStatisticManager().getStatisticByPlayer(player.getUniqueId(), AuctionStatisticType.MONEY_EARNED));

		if (params.equalsIgnoreCase("total_money_spent"))
			return String.valueOf(AuctionHouse.getAuctionStatisticManager().getStatisticByPlayer(player.getUniqueId(), AuctionStatisticType.MONEY_SPENT));

		if (params.equalsIgnoreCase("total_items_bought"))
			return String.valueOf(AuctionHouse.getTransactionManager().getTotalItemsBought(player.getUniqueId()));

		if (params.equalsIgnoreCase("total_items_sold"))
			return String.valueOf(AuctionHouse.getTransactionManager().getTotalItemsSold(player.getUniqueId()));


		if (params.equalsIgnoreCase("active_auctions")) {
			AuctionPlayer auctionPlayer = AuctionHouse.getAuctionPlayerManager().getPlayer(player.getUniqueId());
			if (auctionPlayer == null) return null;
			return String.valueOf(auctionPlayer.getItems(false).size());
		}

		if (params.equalsIgnoreCase("expired_auctions")) {
			AuctionPlayer auctionPlayer = AuctionHouse.getAuctionPlayerManager().getPlayer(player.getUniqueId());
			if (auctionPlayer == null) return null;
			return String.valueOf(auctionPlayer.getItems(true).size());
		}

		if (params.equalsIgnoreCase("server_active_auctions")) {
			return String.valueOf(AuctionHouse.getAuctionItemManager().getItems().size());
		}

		// max sell stuff
		if (params.equalsIgnoreCase("max_sellable_items")) {
			AuctionPlayer auctionPlayer = AuctionHouse.getAuctionPlayerManager().getPlayer(player.getUniqueId());
			return String.valueOf(auctionPlayer.getSellLimit());
		}

		// cool statistic stuff
		final String[] paramSplit = params.split("_");

		// global statistics
		if (paramSplit[0].equalsIgnoreCase("gstat")) {
			final AuctionStatisticType auctionStatisticType = paramSplit.length == 1 ? AuctionStatisticType.CREATED_AUCTION : getStatTypeFromParam(paramSplit[1]);

			if (paramSplit.length == 3) {
				final int duration = Integer.parseInt(splitString(paramSplit[2])[0]);
				final ChronoUnit unit = getChronoUnitFromParam(splitString(paramSplit[2])[1]);

				return String.valueOf(AuctionHouse.getAuctionStatisticManager().getStatistic(auctionStatisticType, unit, duration));
			}

			return String.valueOf(AuctionHouse.getAuctionStatisticManager().getStatistic(auctionStatisticType));
		}

		if (paramSplit[0].equalsIgnoreCase("pstat")) {
			if (paramSplit.length < 2) return null;

			final AuctionStatisticType auctionStatisticType = getStatTypeFromParam(paramSplit[1]);

			// default values for placeholders without player name or time
			OfflinePlayer targetUser = player;
			String[] chronoSplit = null;

			if (paramSplit.length > 2) {
				String timePattern = "";

				// check if param has time at the end
				if (paramSplit[paramSplit.length - 1].matches("[1-9]+[smhdw]")) {
					chronoSplit = splitString(paramSplit[paramSplit.length - 1]);
					timePattern = "_" + paramSplit[paramSplit.length - 1];
				}

				// Match player name
				Matcher playerName = Pattern.compile("pstat_[a-z]+_(.+)" + timePattern).matcher(params);
				if (playerName.matches()) {
					targetUser = getPlayerMaybe(playerName.group(1));
				}
			}

			if (chronoSplit != null) {
				final int duration = Integer.parseInt(chronoSplit[0]);
				final ChronoUnit unit = getChronoUnitFromParam(chronoSplit[1]);

				return String.valueOf(AuctionHouse.getAuctionStatisticManager().getStatisticByPlayer(targetUser.getUniqueId(), auctionStatisticType, unit, duration));
			}

			return String.valueOf(AuctionHouse.getAuctionStatisticManager().getStatisticByPlayer(targetUser.getUniqueId(), auctionStatisticType));
		}

		return null;
	}

	private OfflinePlayer getPlayerMaybe(String name) {
		return CompletableFuture.supplyAsync(() -> {
			final Player onlinePlayer = Bukkit.getPlayer(name);
			if (onlinePlayer != null)
				return onlinePlayer;

			return Bukkit.getOfflinePlayer(name);
		}).join();
	}

	private AuctionStatisticType getStatTypeFromParam(@NotNull final String param) {
		switch (param.toLowerCase()) {
			case "createdauctions":
				return AuctionStatisticType.CREATED_AUCTION;
			case "createdbin":
				return AuctionStatisticType.CREATED_BIN;
			case "soldauctions":
				return AuctionStatisticType.SOLD_AUCTION;
			case "soldbin":
				return AuctionStatisticType.SOLD_BIN;
			case "moneyspent":
				return AuctionStatisticType.MONEY_SPENT;
			case "moneyearned":
				return AuctionStatisticType.MONEY_EARNED;
		}

		return AuctionStatisticType.CREATED_AUCTION;
	}

	private ChronoUnit getChronoUnitFromParam(@NotNull final String param) {
		switch (param.toLowerCase()) {
			case "s":
				return ChronoUnit.SECONDS;
			case "m":
				return ChronoUnit.MINUTES;
			case "h":
				return ChronoUnit.HOURS;
			case "d":
				return ChronoUnit.DAYS;
			case "w":
				return ChronoUnit.WEEKS;
			default:
				return ChronoUnit.MONTHS;
		}
	}

	// https://stackoverflow.com/questions/49107010/how-to-split-string-for-a-digit-and-letters-in-java
	private String[] splitString(String s) {
		OptionalInt firstLetterIndex = IntStream.range(0, s.length()).filter(i -> Character.isLetter(s.charAt(i))).findFirst();

		// Default if there is no letter, only numbers
		String numbers = s;
		String letters = "";
		// if there are letters, split the string at the first letter
		if (firstLetterIndex.isPresent()) {
			numbers = s.substring(0, firstLetterIndex.getAsInt());
			letters = s.substring(firstLetterIndex.getAsInt());
		}

		return new String[]{numbers, letters};
	}
}
