package ca.tweetzy.auctionhouse.guis.confirmation;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.auction.AuctionStackType;
import ca.tweetzy.auctionhouse.auction.AuctionedItem;
import ca.tweetzy.auctionhouse.guis.GUIActiveAuctions;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.gui.Gui;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.core.utils.items.TItemBuilder;
import org.bukkit.event.inventory.ClickType;

import java.util.Objects;

/**
 * The current file has been created by Kiran Hart
 * Date Created: May 20 2021
 * Time Created: 11:28 a.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUIConfirmCancel extends Gui {

    final AuctionPlayer auctionPlayer;
    final AuctionedItem auctionItem;

    public GUIConfirmCancel(AuctionPlayer auctionPlayer, AuctionedItem auctionItem) {
        this.auctionPlayer = auctionPlayer;
        this.auctionItem = auctionItem;
        setTitle(TextUtils.formatText(Settings.GUI_CONFIRM_CANCEL_TITLE.getString()));
        setAcceptsItems(false);
        setRows(1);
        draw();
    }

    private void draw() {
        setItems(0, 3, new TItemBuilder(Objects.requireNonNull(Settings.GUI_CONFIRM_CANCEL_YES_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_CONFIRM_CANCEL_YES_NAME.getString()).setLore(Settings.GUI_CONFIRM_CANCEL_YES_LORE.getStringList()).toItemStack());
        setItem(0, 4, this.auctionItem.getDisplayStack(AuctionStackType.ACTIVE_AUCTIONS_LIST));
        setItems(5, 8, new TItemBuilder(Objects.requireNonNull(Settings.GUI_CONFIRM_CANCEL_NO_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_CONFIRM_CANCEL_NO_NAME.getString()).setLore(Settings.GUI_CONFIRM_CANCEL_NO_LORE.getStringList()).toItemStack());

        setActionForRange(5, 8, ClickType.LEFT, e -> e.manager.showGUI(e.player, new GUIActiveAuctions(this.auctionPlayer)));
        setActionForRange(0, 3, ClickType.LEFT, e -> {
            // Re-select the item to ensure that it's available
            AuctionedItem located = AuctionHouse.getInstance().getAuctionItemManager().getItem(this.auctionItem.getId());
            if (located == null) {
                e.manager.showGUI(e.player, new GUIActiveAuctions(this.auctionPlayer));
                return;
            }

            located.setExpired(true);
            e.manager.showGUI(e.player, new GUIActiveAuctions(this.auctionPlayer));
        });
    }
}
