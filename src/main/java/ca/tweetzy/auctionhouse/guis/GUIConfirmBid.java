package ca.tweetzy.auctionhouse.guis;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.auction.AuctionItem;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.gui.Gui;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.core.utils.items.TItemBuilder;
import org.bukkit.event.inventory.ClickType;

import java.util.Objects;

/**
 * The current file has been created by Kiran Hart
 * Date Created: March 17 2021
 * Time Created: 11:18 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUIConfirmBid extends Gui {

    final AuctionPlayer auctionPlayer;
    final AuctionItem auctionItem;

    public GUIConfirmBid(AuctionPlayer auctionPlayer, AuctionItem auctionItem) {
        this.auctionPlayer = auctionPlayer;
        this.auctionItem = auctionItem;
        setTitle(TextUtils.formatText(Settings.GUI_CONFIRM_BID_TITLE.getString()));
        setAcceptsItems(false);
        setRows(1);
        draw();
    }

    private void draw() {
        setItems(0, 3, new TItemBuilder(Objects.requireNonNull(Settings.GUI_CONFIRM_BID_YES_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_CONFIRM_BID_YES_NAME.getString()).setLore(Settings.GUI_CONFIRM_BID_YES_LORE.getStringList()).toItemStack());
        setItem(0, 4, AuctionAPI.getInstance().deserializeItem(this.auctionItem.getRawItem()));
        setItems(5, 8, new TItemBuilder(Objects.requireNonNull(Settings.GUI_CONFIRM_BID_NO_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_CONFIRM_BID_NO_NAME.getString()).setLore(Settings.GUI_CONFIRM_BID_NO_LORE.getStringList()).toItemStack());

        setActionForRange(5, 8, ClickType.LEFT, e -> e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer)));
        setActionForRange(0, 3, ClickType.LEFT, e -> {
            // Re-select the item to ensure that it's available
            AuctionItem located = AuctionHouse.getInstance().getAuctionItemManager().getItem(this.auctionItem.getKey());
            if (located == null) {
                e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
                return;
            }

            if (Settings.PLAYER_NEEDS_TOTAL_PRICE_TO_BID.getBoolean() && !AuctionHouse.getInstance().getEconomy().has(e.player, auctionItem.getCurrentPrice() + auctionItem.getBidIncPrice())) {
                AuctionHouse.getInstance().getLocale().getMessage("general.notenoughmoney").sendPrefixedMessage(e.player);
                return;
            }

            auctionItem.setHighestBidder(e.player.getUniqueId());
            auctionItem.setCurrentPrice(auctionItem.getCurrentPrice() + auctionItem.getBidIncPrice());
            if (Settings.SYNC_BASE_PRICE_TO_HIGHEST_PRICE.getBoolean() && auctionItem.getCurrentPrice() > auctionItem.getBasePrice()) {
                auctionItem.setBasePrice(auctionItem.getCurrentPrice());
            }

            if (Settings.INCREASE_TIME_ON_BID.getBoolean()) {
                auctionItem.setRemainingTime(auctionItem.getRemainingTime() + Settings.TIME_TO_INCREASE_BY_ON_BID.getInt());
            }

            e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
        });
    }
}
