package ca.tweetzy.auctionhouse.guis.transaction;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.guis.GUIAuctionHouse;
import ca.tweetzy.auctionhouse.helpers.ConfigurationItemHelper;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.gui.Gui;
import ca.tweetzy.core.utils.TextUtils;

/**
 * The current file has been created by Kiran Hart
 * Date Created: September 21 2021
 * Time Created: 5:32 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public final class GUITransactionType extends Gui {

	public GUITransactionType() {
		setTitle(TextUtils.formatText(Settings.GUI_TRANSACTIONS_TYPE_TITLE.getString()));
		setRows(4);
		setAcceptsItems(false);
		setUseLockedCells(true);
		setDefaultItem(Settings.GUI_TRANSACTIONS_TYPE_BG_ITEM.getMaterial().parseItem());
		draw();
	}

	private void draw() {

		setButton(11, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_TRANSACTIONS_TYPE_ITEMS_ALL_TRANSACTIONS_ITEM.getString(), Settings.GUI_TRANSACTIONS_TYPE_ITEMS_ALL_TRANSACTIONS_NAME.getString(), Settings.GUI_TRANSACTIONS_TYPE_ITEMS_ALL_TRANSACTIONS_LORE.getStringList(), null), e -> {
			e.manager.showGUI(e.player, new GUITransactionList(e.player, true));
		});

		setButton(15, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_TRANSACTIONS_TYPE_ITEMS_SELF_TRANSACTIONS_ITEM.getString(), Settings.GUI_TRANSACTIONS_TYPE_ITEMS_SELF_TRANSACTIONS_NAME.getString(), Settings.GUI_TRANSACTIONS_TYPE_ITEMS_SELF_TRANSACTIONS_LORE.getStringList(), null), e -> {
			e.manager.showGUI(e.player, new GUITransactionList(e.player, false));
		});

		setButton(3, 0, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_BACK_BTN_ITEM.getString(), Settings.GUI_BACK_BTN_NAME.getString(), Settings.GUI_BACK_BTN_LORE.getStringList(), null), e -> e.manager.showGUI(e.player, new GUIAuctionHouse(AuctionHouse.getInstance().getAuctionPlayerManager().getPlayer(e.player.getUniqueId()))));
	}
}
