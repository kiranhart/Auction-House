package ca.tweetzy.auctionhouse.guis.filter;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.auction.enums.AuctionItemCategory;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.guis.GUIAuctionHouse;
import ca.tweetzy.auctionhouse.helpers.ConfigurationItemHelper;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.gui.Gui;
import ca.tweetzy.core.gui.GuiUtils;
import ca.tweetzy.core.input.ChatPrompt;
import ca.tweetzy.core.utils.TextUtils;

import java.util.HashMap;

/**
 * The current file has been created by Kiran Hart
 * Date Created: June 18 2021
 * Time Created: 2:10 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */

public class GUIFilterSelection extends Gui {

	final AuctionPlayer auctionPlayer;

	public GUIFilterSelection(AuctionPlayer auctionPlayer) {
		this.auctionPlayer = auctionPlayer;
		setTitle(TextUtils.formatText(Settings.GUI_FILTER_TITLE.getString()));
		setRows(4);
		setAcceptsItems(false);
		setDefaultItem(Settings.GUI_FILTER_BG_ITEM.getMaterial().parseItem());
		setUseLockedCells(true);
		draw();

		setOnClose(closed -> closed.manager.showGUI(closed.player, new GUIAuctionHouse(this.auctionPlayer)));
	}

	private void draw() {

		setButton(1, 3, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_FILTER_ITEMS_ALL_ITEM.getString(), Settings.GUI_FILTER_ITEMS_ALL_NAME.getString(), Settings.GUI_FILTER_ITEMS_ALL_LORE.getStringList(), null), e -> {
			this.auctionPlayer.setSelectedFilter(AuctionItemCategory.ALL);
			e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
		});

		setButton(1, 4, GuiUtils.createButtonItem(AuctionAPI.getInstance().getPlayerHead(this.auctionPlayer.getPlayer().getName()), TextUtils.formatText(Settings.GUI_FILTER_ITEMS_OWN_NAME.getString()), TextUtils.formatText(Settings.GUI_FILTER_ITEMS_OWN_LORE.getStringList())), e -> {
			this.auctionPlayer.setSelectedFilter(AuctionItemCategory.SELF);
			e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
		});

		setButton(1, 5, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_FILTER_ITEMS_SEARCH_ITEM.getString(), Settings.GUI_FILTER_ITEMS_SEARCH_NAME.getString(), Settings.GUI_FILTER_ITEMS_SEARCH_LORE.getStringList(), new HashMap<String, Object>() {{
			put("%filter_search_phrase%", auctionPlayer.getCurrentSearchPhrase());
		}}), e -> {
			e.gui.exit();
			ChatPrompt.showPrompt(AuctionHouse.getInstance(), this.auctionPlayer.getPlayer(), TextUtils.formatText(AuctionHouse.getInstance().getLocale().getMessage("general.entersearchphrase").getMessage()), chat -> {
				if (chat.getMessage() != null && chat.getMessage().length() != 0) {
					// the keyword is valid
					this.auctionPlayer.setCurrentSearchPhrase(chat.getMessage().trim());
					this.auctionPlayer.setSelectedFilter(AuctionItemCategory.SEARCH);
					e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
				}
			});
		});

		setButton(2, 1, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_FILTER_ITEMS_MISC_ITEM.getString(), Settings.GUI_FILTER_ITEMS_MISC_NAME.getString(), Settings.GUI_FILTER_ITEMS_MISC_LORE.getStringList(), null), e -> {
			this.auctionPlayer.setSelectedFilter(AuctionItemCategory.MISC);
			e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
		});

		setButton(2, 2, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_FILTER_ITEMS_ENCHANTS_ITEM.getString(), Settings.GUI_FILTER_ITEMS_ENCHANTS_NAME.getString(), Settings.GUI_FILTER_ITEMS_ENCHANTS_LORE.getStringList(), null), e -> {
			this.auctionPlayer.setSelectedFilter(AuctionItemCategory.ENCHANTS);
			e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
		});

		setButton(2, 3, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_FILTER_ITEMS_ARMOR_ITEM.getString(), Settings.GUI_FILTER_ITEMS_ARMOR_NAME.getString(), Settings.GUI_FILTER_ITEMS_ARMOR_LORE.getStringList(), null), e -> {
			this.auctionPlayer.setSelectedFilter(AuctionItemCategory.ARMOR);
			e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
		});

		setButton(2, 4, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_FILTER_ITEMS_WEAPONS_ITEM.getString(), Settings.GUI_FILTER_ITEMS_WEAPONS_NAME.getString(), Settings.GUI_FILTER_ITEMS_WEAPONS_LORE.getStringList(), null), e -> {
			this.auctionPlayer.setSelectedFilter(AuctionItemCategory.WEAPONS);
			e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
		});

		setButton(2, 5, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_FILTER_ITEMS_TOOLS_ITEM.getString(), Settings.GUI_FILTER_ITEMS_TOOLS_NAME.getString(), Settings.GUI_FILTER_ITEMS_TOOLS_LORE.getStringList(), null), e -> {
			this.auctionPlayer.setSelectedFilter(AuctionItemCategory.TOOLS);
			e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
		});

		setButton(2, 6, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_FILTER_ITEMS_SPAWNERS_ITEM.getString(), Settings.GUI_FILTER_ITEMS_SPAWNERS_NAME.getString(), Settings.GUI_FILTER_ITEMS_SPAWNERS_LORE.getStringList(), null), e -> {
			this.auctionPlayer.setSelectedFilter(AuctionItemCategory.SPAWNERS);
			e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
		});

		setButton(2, 7, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_FILTER_ITEMS_BLOCKS_ITEM.getString(), Settings.GUI_FILTER_ITEMS_BLOCKS_NAME.getString(), Settings.GUI_FILTER_ITEMS_BLOCKS_LORE.getStringList(), null), e -> {
			this.auctionPlayer.setSelectedFilter(AuctionItemCategory.BLOCKS);
			e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
		});
	}
}


