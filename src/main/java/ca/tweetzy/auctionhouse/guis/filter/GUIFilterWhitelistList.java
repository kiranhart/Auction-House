package ca.tweetzy.auctionhouse.guis.filter;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.auction.AuctionFilterItem;
import ca.tweetzy.auctionhouse.auction.enums.AuctionItemCategory;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.gui.Gui;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.core.utils.items.TItemBuilder;
import org.bukkit.event.inventory.ClickType;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: June 22 2021
 * Time Created: 4:06 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUIFilterWhitelistList extends Gui {

	final AuctionItemCategory filerCategory;
	List<AuctionFilterItem> items;

	public GUIFilterWhitelistList(AuctionItemCategory filerCategory) {
		this.filerCategory = filerCategory;
		setTitle(TextUtils.formatText(Settings.GUI_FILTER_WHITELIST_LIST_TITLE.getString().replace("%filter_category%", filerCategory.getTranslatedType())));
		setRows(6);
		setAcceptsItems(false);
		setDefaultItem(Settings.GUI_FILTER_WHITELIST_LIST_BG_ITEM.getMaterial().parseItem());
		setUseLockedCells(true);
		draw();

		setOnClose(close -> close.manager.showGUI(close.player, new GUIFilterWhitelist()));
	}

	private void draw() {
		reset();
		setPrevPage(5, 3, new TItemBuilder(Objects.requireNonNull(Settings.GUI_BACK_BTN_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_BACK_BTN_NAME.getString()).setLore(Settings.GUI_BACK_BTN_LORE.getStringList()).toItemStack());
		setButton(5, 4, new TItemBuilder(Objects.requireNonNull(Settings.GUI_CLOSE_BTN_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_CLOSE_BTN_NAME.getString()).setLore(Settings.GUI_CLOSE_BTN_LORE.getStringList()).toItemStack(), e -> e.manager.showGUI(e.player, new GUIFilterWhitelist()));
		setNextPage(5, 5, new TItemBuilder(Objects.requireNonNull(Settings.GUI_NEXT_BTN_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_NEXT_BTN_NAME.getString()).setLore(Settings.GUI_NEXT_BTN_LORE.getStringList()).toItemStack());
		setOnPage(e -> draw());

		AuctionHouse.newChain().asyncFirst(() -> {
			this.items = AuctionHouse.getInstance().getFilterManager().getFilterWhitelist().stream().filter(item -> item.getCategory() == filerCategory).collect(Collectors.toList());
			return this.items.stream().skip((page - 1) * 28L).limit(28L).collect(Collectors.toList());
		}).asyncLast((data) -> {
			pages = (int) Math.max(1, Math.ceil(this.items.size() / (double) 28L));
			int slot = 10;
			for (AuctionFilterItem item : data) {
				setButton(slot, item.getItemStack(), ClickType.RIGHT, e -> {
					AuctionHouse.getInstance().getFilterManager().removeFilterItem(item);
					draw();
				});

				slot = Arrays.asList(16, 25, 34).contains(slot) ? slot + 3 : slot + 1;
			}
		}).execute();

	}
}
