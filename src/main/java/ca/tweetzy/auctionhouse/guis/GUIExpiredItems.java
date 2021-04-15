package ca.tweetzy.auctionhouse.guis;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.auction.AuctionItem;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.auction.AuctionStackType;
import ca.tweetzy.auctionhouse.helpers.ConfigurationItemHelper;
import ca.tweetzy.auctionhouse.managers.SoundManager;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.gui.Gui;
import ca.tweetzy.core.utils.PlayerUtils;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.core.utils.items.TItemBuilder;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: March 15 2021
 * Time Created: 3:19 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUIExpiredItems extends Gui {

    final AuctionPlayer auctionPlayer;

    public GUIExpiredItems(AuctionPlayer auctionPlayer) {
        this.auctionPlayer = auctionPlayer;
        setTitle(TextUtils.formatText(Settings.GUI_EXPIRED_AUCTIONS_TITLE.getString()));
        setRows(6);
        setAcceptsItems(false);
        draw();
    }

    private void draw() {
        reset();

        // Pagination
        pages = (int) Math.max(1, Math.ceil(this.auctionPlayer.getItems(true).size() / (double) 45));
        setPrevPage(5, 3, new TItemBuilder(Objects.requireNonNull(Settings.GUI_BACK_BTN_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_BACK_BTN_NAME.getString()).setLore(Settings.GUI_BACK_BTN_LORE.getStringList()).toItemStack());
        setButton(5, 4, new TItemBuilder(Objects.requireNonNull(Settings.GUI_REFRESH_BTN_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_REFRESH_BTN_NAME.getString()).setLore(Settings.GUI_REFRESH_BTN_LORE.getStringList()).toItemStack(), e -> draw());
        setNextPage(5, 5, new TItemBuilder(Objects.requireNonNull(Settings.GUI_NEXT_BTN_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_NEXT_BTN_NAME.getString()).setLore(Settings.GUI_NEXT_BTN_LORE.getStringList()).toItemStack());
        setOnPage(e -> {
            draw();
            SoundManager.getInstance().playSound(this.auctionPlayer.getPlayer(), Settings.SOUNDS_NAVIGATE_GUI_PAGES.getString(), 1.0F, 1.0F);
        });

        // Other Buttons
        setButton(5, 0, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_CLOSE_BTN_ITEM.getString(), Settings.GUI_CLOSE_BTN_NAME.getString(), Settings.GUI_CLOSE_BTN_LORE.getStringList(), null), e -> e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer)));
        setButton(5, 1, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_EXPIRED_AUCTIONS_ITEM.getString(), Settings.GUI_EXPIRED_AUCTIONS_NAME.getString(), Settings.GUI_EXPIRED_AUCTIONS_LORE.getStringList(), null), e -> {
            this.auctionPlayer.getItems(true).forEach(item -> {
                AuctionHouse.getInstance().getAuctionItemManager().removeItem(item.getKey());
                PlayerUtils.giveItem(e.player, AuctionAPI.getInstance().deserializeItem(item.getRawItem()));
            });
            draw();
        });

        List<AuctionItem> data = this.auctionPlayer.getItems(true).stream().sorted(Comparator.comparingInt(AuctionItem::getRemainingTime).reversed()).skip((page - 1) * 45L).limit(45).collect(Collectors.toList());
        int slot = 0;
        for (AuctionItem item : data) {
            setButton(slot++, AuctionAPI.getInstance().deserializeItem(item.getRawItem()), e -> {
                AuctionHouse.getInstance().getAuctionItemManager().removeItem(item.getKey());
                PlayerUtils.giveItem(e.player, AuctionAPI.getInstance().deserializeItem(item.getRawItem()));
                draw();
            });
        }
    }
}
