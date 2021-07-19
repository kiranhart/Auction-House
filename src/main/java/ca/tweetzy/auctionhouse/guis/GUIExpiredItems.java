package ca.tweetzy.auctionhouse.guis;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.auction.AuctionItem;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
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
    private List<AuctionItem> items;

    public GUIExpiredItems(AuctionPlayer auctionPlayer) {
        this.auctionPlayer = auctionPlayer;
        setTitle(TextUtils.formatText(Settings.GUI_EXPIRED_AUCTIONS_TITLE.getString()));
        setRows(6);
        setAcceptsItems(false);
        draw();
    }

    private void draw() {
        reset();

        setButton(5, 0, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_CLOSE_BTN_ITEM.getString(), Settings.GUI_CLOSE_BTN_NAME.getString(), Settings.GUI_CLOSE_BTN_LORE.getStringList(), null), e -> e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer)));

        AuctionHouse.newChain().asyncFirst(() -> {
            this.items = this.auctionPlayer.getItems(true);
            return this.items.stream().sorted(Comparator.comparingInt(AuctionItem::getRemainingTime).reversed()).skip((page - 1) * 45L).limit(45).collect(Collectors.toList());
        }).asyncLast((data) -> {
            pages = (int) Math.max(1, Math.ceil(this.auctionPlayer.getItems(true).size() / (double) 45));
            setPrevPage(5, 3, new TItemBuilder(Objects.requireNonNull(Settings.GUI_BACK_BTN_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_BACK_BTN_NAME.getString()).setLore(Settings.GUI_BACK_BTN_LORE.getStringList()).toItemStack());
            setButton(5, 4, new TItemBuilder(Objects.requireNonNull(Settings.GUI_REFRESH_BTN_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_REFRESH_BTN_NAME.getString()).setLore(Settings.GUI_REFRESH_BTN_LORE.getStringList()).toItemStack(), e -> draw());
            setNextPage(5, 5, new TItemBuilder(Objects.requireNonNull(Settings.GUI_NEXT_BTN_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_NEXT_BTN_NAME.getString()).setLore(Settings.GUI_NEXT_BTN_LORE.getStringList()).toItemStack());
            setOnPage(e -> {
                draw();
                SoundManager.getInstance().playSound(this.auctionPlayer.getPlayer(), Settings.SOUNDS_NAVIGATE_GUI_PAGES.getString(), 1.0F, 1.0F);
            });


            setButton(5, 1, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_EXPIRED_AUCTIONS_ITEM.getString(), Settings.GUI_EXPIRED_AUCTIONS_NAME.getString(), Settings.GUI_EXPIRED_AUCTIONS_LORE.getStringList(), null), e -> {
                for (AuctionItem auctionItem : data) {
                    PlayerUtils.giveItem(e.player, AuctionAPI.getInstance().deserializeItem(auctionItem.getRawItem()));
                    AuctionHouse.getInstance().getAuctionItemManager().sendToGarbage(auctionItem);
                }
                draw();
            });

            int slot = 0;
            for (AuctionItem auctionItem : data) {
                setButton(slot++, AuctionAPI.getInstance().deserializeItem(auctionItem.getRawItem()), e -> {
                    PlayerUtils.giveItem(e.player, AuctionAPI.getInstance().deserializeItem(auctionItem.getRawItem()));
                    AuctionHouse.getInstance().getAuctionItemManager().sendToGarbage(auctionItem);
                    e.manager.showGUI(e.player, new GUIExpiredItems(this.auctionPlayer));
                });
            }

        }).execute();
    }
}
