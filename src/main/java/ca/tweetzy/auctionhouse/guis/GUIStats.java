package ca.tweetzy.auctionhouse.guis;

import ca.tweetzy.auctionhouse.settings.Settings;
import org.bukkit.entity.Player;

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
		setTitle(Settings.GUI_STATS_VIEW_SELECT_TITLE.getString());
		setDefaultItem(Settings.GUI_STATS_VIEW_SELECT_BG_ITEM.getMaterial().parseItem());
		setUseLockedCells(true);
		setAcceptsItems(false);
		setAllowDrops(false);
		setRows(3);
		draw();
	}

	private void draw() {

	}
}
