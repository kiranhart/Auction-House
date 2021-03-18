package ca.tweetzy.auctionhouse.guis;

import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.auction.AuctionItem;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.gui.Gui;
import ca.tweetzy.core.utils.TextUtils;

/**
 * The current file has been created by Kiran Hart
 * Date Created: March 17 2021
 * Time Created: 11:18 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUIConfirmPurchase extends Gui {

    final AuctionPlayer auctionPlayer;
    final AuctionItem auctionItem;

    public GUIConfirmPurchase(AuctionPlayer auctionPlayer, AuctionItem auctionItem) {
        this.auctionPlayer = auctionPlayer;
        this.auctionItem = auctionItem;
        setTitle(TextUtils.formatText(Settings.GUI_CONFIRM_BUY_TITLE.getString()));
        setRows(1);
        setAcceptsItems(false);
        draw();
    }

    private void draw() {
        setItem(0, 4, AuctionAPI.getInstance().deserializeItem(this.auctionItem.getRawItem()));
    }
}
