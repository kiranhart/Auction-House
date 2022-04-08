package ca.tweetzy.auctionhouse.guis;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.auction.AuctionStat;
import ca.tweetzy.auctionhouse.helpers.ConfigurationItemHelper;
import ca.tweetzy.auctionhouse.managers.AuctionStatManager;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.gui.Gui;
import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * The current file has been created by Kiran Hart
 * Date Created: September 01 2021
 * Time Created: 2:57 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public final class GUIStats extends AbstractPlaceholderGui {

	private final Player player;

	public GUIStats(final Player player) {
		super(player);
		this.player = player;
		setTitle(Settings.GUI_STATS_TITLE.getString());
		setDefaultItem(Settings.GUI_STATS_BG_ITEM.getMaterial().parseItem());
		setUseLockedCells(true);
		setAcceptsItems(false);
		setAllowDrops(false);
		setRows(3);
		draw();
	}

	private void draw() {

		final AuctionStat<Integer, Integer, Integer, Double, Double> playerStats = AuctionHouse.getInstance().getAuctionStatManager().getPlayerStats(this.player);

		setItem(1, 3, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_STATS_ITEMS_PERSONAL_USE_HEAD.getBoolean() ? AuctionAPI.getInstance().getPlayerHead(player.getName()) : Settings.GUI_STATS_ITEMS_PERSONAL_ITEM.getMaterial().parseItem(), Settings.GUI_STATS_ITEMS_PERSONAL_NAME.getString(), Settings.GUI_STATS_ITEMS_PERSONAL_LORE.getStringList(), new HashMap<String, Object>() {{
			put("%auctions_created%", playerStats.getCreated());
			put("%auctions_sold%", playerStats.getSold());
			put("%auctions_expired%", playerStats.getExpired());
			put("%auctions_money_spent%", AuctionAPI.getInstance().formatNumber(playerStats.getSpent()));
			put("%auctions_money_earned%", AuctionAPI.getInstance().formatNumber(playerStats.getEarned()));
		}}));

		setItem(1, 5, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_STATS_ITEMS_GLOBAL_ITEM.getString(), Settings.GUI_STATS_ITEMS_GLOBAL_NAME.getString(), Settings.GUI_STATS_ITEMS_GLOBAL_LORE.getStringList(), new HashMap<String, Object>() {{
			put("%auctions_created%", (int) AuctionHouse.getInstance().getAuctionStatManager().getGlobalStat(AuctionStatManager.GlobalAuctionStatType.CREATED));
			put("%auctions_sold%", (int) AuctionHouse.getInstance().getAuctionStatManager().getGlobalStat(AuctionStatManager.GlobalAuctionStatType.SOLD));
			put("%auctions_expired%", (int) AuctionHouse.getInstance().getAuctionStatManager().getGlobalStat(AuctionStatManager.GlobalAuctionStatType.EXPIRED));
			put("%auctions_money_spent%", AuctionAPI.getInstance().formatNumber(AuctionHouse.getInstance().getAuctionStatManager().getGlobalStat(AuctionStatManager.GlobalAuctionStatType.SPENT)));
		}}));
	}
}
