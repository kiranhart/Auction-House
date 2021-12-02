package ca.tweetzy.auctionhouse.guis.filter;

import ca.tweetzy.auctionhouse.auction.AuctionItemCategory;
import ca.tweetzy.auctionhouse.helpers.ConfigurationItemHelper;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.gui.Gui;
import ca.tweetzy.core.utils.TextUtils;

/**
 * The current file has been created by Kiran Hart
 * Date Created: June 22 2021
 * Time Created: 3:14 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUIFilterWhitelist extends Gui {

	public GUIFilterWhitelist() {
		setTitle(TextUtils.formatText(Settings.GUI_FILTER_WHITELIST_TITLE.getString()));
		setRows(6);
		setAcceptsItems(false);
		setDefaultItem(Settings.GUI_FILTER_WHITELIST_BG_ITEM.getMaterial().parseItem());
		setUseLockedCells(true);
		draw();
	}

	private void draw() {
		setButton(2, 1, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_FILTER_WHITELIST_ITEMS_BLOCKS_ITEM.getString(), Settings.GUI_FILTER_WHITELIST_ITEMS_BLOCKS_NAME.getString(), Settings.GUI_FILTER_WHITELIST_ITEMS_BLOCKS_LORE.getStringList(), null), e -> {
			e.manager.showGUI(e.player, new GUIFilterWhitelistList(AuctionItemCategory.BLOCKS));
		});

		setButton(2, 3, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_FILTER_WHITELIST_ITEMS_FOOD_ITEM.getString(), Settings.GUI_FILTER_WHITELIST_ITEMS_FOOD_NAME.getString(), Settings.GUI_FILTER_WHITELIST_ITEMS_FOOD_LORE.getStringList(), null), e -> {
			e.manager.showGUI(e.player, new GUIFilterWhitelistList(AuctionItemCategory.FOOD));
		});

		setButton(2, 5, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_FILTER_WHITELIST_ITEMS_ARMOR_ITEM.getString(), Settings.GUI_FILTER_WHITELIST_ITEMS_ARMOR_NAME.getString(), Settings.GUI_FILTER_WHITELIST_ITEMS_ARMOR_LORE.getStringList(), null), e -> {
			e.manager.showGUI(e.player, new GUIFilterWhitelistList(AuctionItemCategory.ARMOR));
		});

		setButton(2, 7, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_FILTER_WHITELIST_ITEMS_TOOLS_ITEM.getString(), Settings.GUI_FILTER_WHITELIST_ITEMS_TOOLS_NAME.getString(), Settings.GUI_FILTER_WHITELIST_ITEMS_TOOLS_LORE.getStringList(), null), e -> {
			e.manager.showGUI(e.player, new GUIFilterWhitelistList(AuctionItemCategory.TOOLS));
		});

		// 2ND ROW STARTS

		setButton(3, 1, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_FILTER_WHITELIST_ITEMS_SPAWNERS_ITEM.getString(), Settings.GUI_FILTER_WHITELIST_ITEMS_SPAWNERS_NAME.getString(), Settings.GUI_FILTER_WHITELIST_ITEMS_SPAWNERS_LORE.getStringList(), null), e -> {
			e.manager.showGUI(e.player, new GUIFilterWhitelistList(AuctionItemCategory.SPAWNERS));
		});

		setButton(3, 3, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_FILTER_WHITELIST_ITEMS_ENCHANTS_ITEM.getString(), Settings.GUI_FILTER_WHITELIST_ITEMS_ENCHANTS_NAME.getString(), Settings.GUI_FILTER_WHITELIST_ITEMS_ENCHANTS_LORE.getStringList(), null), e -> {
			e.manager.showGUI(e.player, new GUIFilterWhitelistList(AuctionItemCategory.ENCHANTS));
		});

		setButton(3, 5, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_FILTER_WHITELIST_ITEMS_WEAPONS_ITEM.getString(), Settings.GUI_FILTER_WHITELIST_ITEMS_WEAPONS_NAME.getString(), Settings.GUI_FILTER_WHITELIST_ITEMS_WEAPONS_LORE.getStringList(), null), e -> {
			e.manager.showGUI(e.player, new GUIFilterWhitelistList(AuctionItemCategory.WEAPONS));
		});

		setButton(3, 7, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_FILTER_WHITELIST_ITEMS_MISC_ITEM.getString(), Settings.GUI_FILTER_WHITELIST_ITEMS_MISC_NAME.getString(), Settings.GUI_FILTER_WHITELIST_ITEMS_MISC_LORE.getStringList(), null), e -> {
			e.manager.showGUI(e.player, new GUIFilterWhitelistList(AuctionItemCategory.MISC));
		});
	}
}
