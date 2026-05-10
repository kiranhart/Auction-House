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

package ca.tweetzy.auctionhouse.model.manager;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.lang.AuctionLocale;
import ca.tweetzy.auctionhouse.auction.QueuedNotification;
import ca.tweetzy.auctionhouse.settings.Settings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public final class NotificationManager {

	private static final Gson GSON = new Gson();
	private static final Type STRING_MAP_TYPE = new TypeToken<Map<String, String>>() {}.getType();
	private static final int MAX_DELIVER_PER_JOIN = 50;

	public void queue(@NonNull UUID playerUuid, @NonNull String messageKey, @NonNull Map<String, String> placeholders) {
		if (!Settings.OFFLINE_NOTIFICATIONS_ENABLED.getBoolean()) return;
		UUID id = UUID.randomUUID();
		String json = placeholders.isEmpty() ? "{}" : GSON.toJson(placeholders);
		AuctionHouse.getDataManager().insertNotification(id, playerUuid, messageKey, json, null);
	}

	public void deliverTo(@NonNull Player player) {
		AuctionHouse.getDataManager().getNotificationsForPlayer(player.getUniqueId(), (ex, list) -> {
			if (ex != null || list == null || list.isEmpty()) return;
			List<QueuedNotification> toDeliver = list.size() > MAX_DELIVER_PER_JOIN
					? list.subList(0, MAX_DELIVER_PER_JOIN)
					: list;
			List<UUID> toDelete = toDeliver.stream().map(QueuedNotification::getId).collect(Collectors.toList());

			Bukkit.getScheduler().runTask(AuctionHouse.getInstance(), () -> {
				for (QueuedNotification n : toDeliver) {
					Map<String, String> placeholders = parsePlaceholders(n.getPlaceholderDataJson());
					AuctionLocale.tell(player, n.getMessageKey(), placeholders);
				}
				if (list.size() > MAX_DELIVER_PER_JOIN) {
					AuctionLocale.tell(player, "general.offline notifications more", "count",String.valueOf(list.size() - MAX_DELIVER_PER_JOIN));
				}
				AuctionHouse.getDataManager().deleteNotifications(toDelete, null);
			});
		});
	}

	private Map<String, String> parsePlaceholders(String json) {
		if (json == null || json.isEmpty()) return Collections.emptyMap();
		try {
			Map<String, String> map = GSON.fromJson(json, STRING_MAP_TYPE);
			return map != null ? map : Collections.emptyMap();
		} catch (Exception e) {
			return Collections.emptyMap();
		}
	}
}
