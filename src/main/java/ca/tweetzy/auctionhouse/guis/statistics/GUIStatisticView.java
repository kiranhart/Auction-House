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


import ca.tweetzy.auctionhouse.lang.AuctionLocale;
import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.auction.enums.AuctionStatisticType;
import ca.tweetzy.auctionhouse.guis.AuctionBaseGUI;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.flight.utils.Replacer;

public final class GUIStatisticView extends AuctionBaseGUI {

	private final AuctionPlayer targetPlayer;
	private final boolean isSelf;

	public GUIStatisticView(AuctionPlayer player, AuctionPlayer targetPlayer) {
		super(new GUIStatisticViewSelect(player), player.getPlayer(), player.getUuid().equals(targetPlayer.getUuid()) ? AuctionLocale.msg(player.getPlayer(), "gui.stat view self.items.title") : AuctionLocale.msg(player.getPlayer(), "gui.stat view other.items.title").replace("%player_name%", targetPlayer.getPlayer().getName()), 6);
		this.targetPlayer = targetPlayer;
		this.isSelf = player.getUuid().equals(targetPlayer.getUuid());
		setDefaultItem(QuickItem.bg(QuickItem.of(isSelf ? Settings.GUI_STATS_SELF_BG_ITEM.getString() : Settings.GUI_STATS_SEARCH_BG_ITEM.getString()).make()));
		draw();
	}

	@Override
	protected void draw() {
		applyBackExit();

		// created auction
		setItem(1, 1, QuickItem
				.of(isSelf ? Settings.GUI_STATS_SELF_ITEMS_CREATED_AUCTION_ITEM.getString() : Settings.GUI_STATS_SEARCH_ITEMS_CREATED_AUCTION_ITEM.getString())
				.name(isSelf ? AuctionLocale.msg(this.player, "gui.stat view self.items.created auction.name") : AuctionLocale.msg(this.player, "gui.stat view other.items.created auction.name"))
				.lore(this.player, Replacer.replaceVariables(isSelf ? AuctionLocale.msgList(this.player, "gui.stat view self.items.created auction.lore") : AuctionLocale.msgList(this.player, "gui.stat view other.items.created auction.lore"), "created_auctions", (int) AuctionHouse.getInstance().getAuctionStatisticManager().getStatisticByPlayer(targetPlayer.getUuid(), AuctionStatisticType.CREATED_AUCTION)))
				.make());

		// sold auction
		setItem(3, 1, QuickItem
				.of(isSelf ? Settings.GUI_STATS_SELF_ITEMS_SOLD_AUCTION_ITEM.getString() : Settings.GUI_STATS_SEARCH_ITEMS_SOLD_AUCTION_ITEM.getString())
				.name(isSelf ? AuctionLocale.msg(this.player, "gui.stat view self.items.sold auction.name") : AuctionLocale.msg(this.player, "gui.stat view other.items.sold auction.name"))
				.lore(this.player, Replacer.replaceVariables(isSelf ? AuctionLocale.msgList(this.player, "gui.stat view self.items.sold auction.lore") : AuctionLocale.msgList(this.player, "gui.stat view other.items.sold auction.lore"), "sold_auctions", (int) AuctionHouse.getInstance().getAuctionStatisticManager().getStatisticByPlayer(targetPlayer.getUuid(), AuctionStatisticType.SOLD_AUCTION)))
				.make());

		// created bin
		setItem(1, 4, QuickItem
				.of(isSelf ? Settings.GUI_STATS_SELF_ITEMS_CREATED_BIN_ITEM.getString() : Settings.GUI_STATS_SEARCH_ITEMS_CREATED_BIN_ITEM.getString())
				.name(isSelf ? AuctionLocale.msg(this.player, "gui.stat view self.items.created bin.name") : AuctionLocale.msg(this.player, "gui.stat view other.items.created bin.name"))
				.lore(this.player, Replacer.replaceVariables(isSelf ? AuctionLocale.msgList(this.player, "gui.stat view self.items.created bin.lore") : AuctionLocale.msgList(this.player, "gui.stat view other.items.created bin.lore"), "created_bins", (int) AuctionHouse.getInstance().getAuctionStatisticManager().getStatisticByPlayer(targetPlayer.getUuid(), AuctionStatisticType.CREATED_BIN)))
				.make());

		// sold bin
		setItem(3, 4, QuickItem
				.of(isSelf ? Settings.GUI_STATS_SELF_ITEMS_SOLD_BIN_ITEM.getString() : Settings.GUI_STATS_SEARCH_ITEMS_SOLD_BIN_ITEM.getString())
				.name(isSelf ? AuctionLocale.msg(this.player, "gui.stat view self.items.sold bin.name") : AuctionLocale.msg(this.player, "gui.stat view other.items.sold bin.name"))
				.lore(this.player, Replacer.replaceVariables(isSelf ? AuctionLocale.msgList(this.player, "gui.stat view self.items.sold bin.lore") : AuctionLocale.msgList(this.player, "gui.stat view other.items.sold bin.lore"), "sold_bins", (int) AuctionHouse.getInstance().getAuctionStatisticManager().getStatisticByPlayer(targetPlayer.getUuid(), AuctionStatisticType.SOLD_BIN)))
				.make());

		// money earned
		setItem(1, 7, QuickItem
				.of(isSelf ? Settings.GUI_STATS_SELF_ITEMS_MONEY_EARNED_ITEM.getString() : Settings.GUI_STATS_SEARCH_ITEMS_MONEY_EARNED_ITEM.getString())
				.name(isSelf ? AuctionLocale.msg(this.player, "gui.stat view self.items.money earned.name") : AuctionLocale.msg(this.player, "gui.stat view other.items.money earned.name"))
				.lore(this.player, Replacer.replaceVariables(isSelf ? AuctionLocale.msgList(this.player, "gui.stat view self.items.money earned.lore") : AuctionLocale.msgList(this.player, "gui.stat view other.items.money earned.lore"), "money_earned", (int) AuctionHouse.getInstance().getAuctionStatisticManager().getStatisticByPlayer(targetPlayer.getUuid(), AuctionStatisticType.MONEY_EARNED)))
				.make());

		// money spent
		setItem(3, 7, QuickItem
				.of(isSelf ? Settings.GUI_STATS_SELF_ITEMS_MONEY_SPENT_ITEM.getString() : Settings.GUI_STATS_SEARCH_ITEMS_MONEY_SPENT_ITEM.getString())
				.name(isSelf ? AuctionLocale.msg(this.player, "gui.stat view self.items.money spent.name") : AuctionLocale.msg(this.player, "gui.stat view other.items.money spent.name"))
				.lore(this.player, Replacer.replaceVariables(isSelf ? AuctionLocale.msgList(this.player, "gui.stat view self.items.money spent.lore") : AuctionLocale.msgList(this.player, "gui.stat view other.items.money spent.lore"), "money_spent", (int) AuctionHouse.getInstance().getAuctionStatisticManager().getStatisticByPlayer(targetPlayer.getUuid(), AuctionStatisticType.MONEY_SPENT)))
				.make());
	}
}
