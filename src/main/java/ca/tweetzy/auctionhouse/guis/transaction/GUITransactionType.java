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

package ca.tweetzy.auctionhouse.guis.transaction;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.guis.AuctionBaseGUI;
import ca.tweetzy.auctionhouse.guis.core.GUIAuctionHouse;
import ca.tweetzy.auctionhouse.helpers.input.TitleInput;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.auctionhouse.transaction.Transaction;
import ca.tweetzy.flight.utils.MathUtil;
import ca.tweetzy.flight.utils.QuickItem;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * The current file has been created by Kiran Hart
 * Date Created: September 21 2021
 * Time Created: 5:32 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public final class GUITransactionType extends AuctionBaseGUI {


	public GUITransactionType(Player player) {
		super(new GUIAuctionHouse(AuctionHouse.getInstance().getAuctionPlayerManager().getPlayer(player.getUniqueId())), player, Settings.GUI_TRANSACTIONS_TYPE_TITLE.getString(), 4);
		setDefaultItem(QuickItem.bg(QuickItem.of(Settings.GUI_TRANSACTIONS_TYPE_BG_ITEM.getString()).make()));
		draw();
	}

	@Override
	protected void draw() {
		applyBackExit();

		final AuctionHouse instance = AuctionHouse.getInstance();

		setButton(11, QuickItem
				.of(Settings.GUI_TRANSACTIONS_TYPE_ITEMS_ALL_TRANSACTIONS_ITEM.getString())
				.name(Settings.GUI_TRANSACTIONS_TYPE_ITEMS_ALL_TRANSACTIONS_NAME.getString())
				.lore(this.player, Settings.GUI_TRANSACTIONS_TYPE_ITEMS_ALL_TRANSACTIONS_LORE.getStringList())
				.make(), e -> {

			if (Settings.RESTRICT_ALL_TRANSACTIONS_TO_PERM.getBoolean() && !e.player.hasPermission("auctionhouse.transactions.viewall")) {
				instance.getLocale().getMessage("commands.no_permission").sendPrefixedMessage(e.player);
				return;
			}

			e.manager.showGUI(e.player, new GUITransactionList(e.player, true));
		});

		setButton(13, QuickItem
				.of(Settings.GUI_TRANSACTIONS_TYPE_ITEMS_SELF_TRANSACTIONS_ITEM.getString())
				.name(Settings.GUI_TRANSACTIONS_TYPE_ITEMS_SELF_TRANSACTIONS_NAME.getString())
				.lore(this.player, Settings.GUI_TRANSACTIONS_TYPE_ITEMS_SELF_TRANSACTIONS_LORE.getStringList())
				.make(), e -> e.manager.showGUI(e.player, new GUITransactionList(e.player, false)));

		setButton(15, QuickItem
				.of(Settings.GUI_TRANSACTIONS_TYPE_ITEMS_REQUEST_TRANSACTIONS_ITEM.getString())
				.name(Settings.GUI_TRANSACTIONS_TYPE_ITEMS_REQUEST_TRANSACTIONS_NAME.getString())
				.lore(this.player, Settings.GUI_TRANSACTIONS_TYPE_ITEMS_REQUEST_TRANSACTIONS_LORE.getStringList())
				.make(), e -> e.manager.showGUI(e.player, new GUIRequestTransactionList(e.player, false)));

		if (player.isOp() || player.hasPermission("auctionhouse.admin")) {

			setButton(3, 8, QuickItem
					.of(Settings.GUI_TRANSACTIONS_TYPE_ITEMS_DELETE_ITEM.getString())
					.name(Settings.GUI_TRANSACTIONS_TYPE_ITEMS_DELETE_NAME.getString())
					.lore(this.player, Settings.GUI_TRANSACTIONS_TYPE_ITEMS_DELETE_LORE.getStringList())
					.make(), e -> {

				e.gui.close();

				new TitleInput(player, AuctionHouse.getInstance().getLocale().getMessage("titles.enter deletion range.title").getMessage(), AuctionHouse.getInstance().getLocale().getMessage("titles.enter deletion range.subtitle").getMessage()) {

					@Override
					public void onExit(Player player) {
						e.manager.showGUI(e.player, GUITransactionType.this);
					}

					@Override
					public boolean onResult(String string) {
						string = ChatColor.stripColor(string);

						final String[] parts = ChatColor.stripColor(string).split(" ");
						if (parts.length < 2) {
							instance.getLocale().getMessage("general.invalidrange").sendPrefixedMessage(player);
							return false;
						}

						if (!MathUtil.isInt(parts[0]) && Arrays.asList("second", "minute", "hour", "day", "week", "month", "year").contains(parts[1].toLowerCase())) {
							instance.getLocale().getMessage("prompts.enter deletion range").sendPrefixedMessage(player);
							return false;
						}

						final long ticks = AuctionAPI.toTicks(string);

						AuctionHouse.newChain().async(() -> {
							instance.getLocale().getMessage("general.transaction delete begin").sendPrefixedMessage(e.player);
							List<UUID> toRemove = new ArrayList<>();

							Set<Map.Entry<UUID, Transaction>> entrySet = instance.getTransactionManager().getTransactions().entrySet();
							Iterator<Map.Entry<UUID, Transaction>> entryIterator = entrySet.iterator();

							while (entryIterator.hasNext()) {
								Map.Entry<UUID, Transaction> entry = entryIterator.next();
								Transaction transaction = entry.getValue();

								if (Instant.ofEpochMilli(transaction.getTransactionTime()).isBefore(Instant.now().minus(Duration.ofSeconds(ticks)))) {
									toRemove.add(transaction.getId());
									entryIterator.remove();
								}
							}

							instance.getDataManager().deleteTransactions(toRemove);
							instance.getLocale().getMessage("general.deleted transactions").processPlaceholder("deleted_transactions", toRemove.size()).sendPrefixedMessage(e.player);
						}).execute();
						return true;
					}
				};
			});
		}
	}
}
