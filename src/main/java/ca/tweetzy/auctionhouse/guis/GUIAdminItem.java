package ca.tweetzy.auctionhouse.guis;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.auction.AuctionedItem;
import ca.tweetzy.auctionhouse.helpers.ConfigurationItemHelper;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.gui.Gui;
import ca.tweetzy.core.utils.PlayerUtils;
import ca.tweetzy.core.utils.TextUtils;

/**
 * The current file has been created by Kiran Hart
 * Date Created: July 14 2021
 * Time Created: 3:04 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUIAdminItem extends Gui {

	private final AuctionPlayer auctionPlayer;
	private final AuctionedItem auctionItem;

	public GUIAdminItem(AuctionPlayer auctionPlayer, AuctionedItem auctionItem) {
		this.auctionPlayer = auctionPlayer;
		this.auctionItem = auctionItem;
		setTitle(TextUtils.formatText(Settings.GUI_ITEM_ADMIN_TITLE.getString()));
		setDefaultItem(Settings.GUI_ITEM_ADMIN_BG_ITEM.getMaterial().parseItem());
		setRows(3);
		setAcceptsItems(false);
		setUseLockedCells(true);

		setOnClose(close -> close.manager.showGUI(close.player, new GUIAuctionHouse(this.auctionPlayer)));
		draw();
	}

	private void draw() {
		setButton(1, 1, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_ITEM_ADMIN_ITEMS_RETURN_ITEM.getString(), Settings.GUI_ITEM_ADMIN_ITEMS_RETURN_NAME.getString(), Settings.GUI_ITEM_ADMIN_ITEMS_RETURN_LORE.getStringList(), null), e -> {
			this.auctionItem.setExpiresAt(System.currentTimeMillis());
			this.auctionItem.setExpired(true);
			e.gui.close();
		});

		setButton(1, 3, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_ITEM_ADMIN_ITEMS_CLAIM_ITEM.getString(), Settings.GUI_ITEM_ADMIN_ITEMS_CLAIM_NAME.getString(), Settings.GUI_ITEM_ADMIN_ITEMS_CLAIM_LORE.getStringList(), null), e -> {
			PlayerUtils.giveItem(e.player, this.auctionItem.getItem());
			AuctionHouse.getInstance().getAuctionItemManager().sendToGarbage(this.auctionItem);
			e.gui.close();
		});

		setButton(1, 5, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_ITEM_ADMIN_ITEMS_DELETE_ITEM.getString(), Settings.GUI_ITEM_ADMIN_ITEMS_DELETE_NAME.getString(), Settings.GUI_ITEM_ADMIN_ITEMS_DELETE_LORE.getStringList(), null), e -> {
			AuctionHouse.getInstance().getAuctionItemManager().sendToGarbage(this.auctionItem);
			e.gui.close();
		});

		setButton(1, 7, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_ITEM_ADMIN_ITEMS_COPY_ITEM.getString(), Settings.GUI_ITEM_ADMIN_ITEMS_COPY_NAME.getString(), Settings.GUI_ITEM_ADMIN_ITEMS_COPY_LORE.getStringList(), null), e -> {
			PlayerUtils.giveItem(e.player, this.auctionItem.getItem());
			e.gui.close();
		});
	}
}
