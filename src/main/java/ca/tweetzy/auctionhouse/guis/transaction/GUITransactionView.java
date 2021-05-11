package ca.tweetzy.auctionhouse.guis.transaction;

import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.auction.AuctionItem;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.auction.AuctionSaleType;
import ca.tweetzy.auctionhouse.helpers.ConfigurationItemHelper;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.auctionhouse.transaction.Transaction;
import ca.tweetzy.core.gui.Gui;
import ca.tweetzy.core.gui.GuiUtils;
import ca.tweetzy.core.utils.TextUtils;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: March 22 2021
 * Time Created: 7:04 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUITransactionView extends Gui {

    public GUITransactionView(AuctionPlayer auctionPlayer, Transaction transaction) {
        setTitle(TextUtils.formatText(Settings.GUI_TRANSACTION_VIEW_TITLE.getString()));
        setRows(6);
        setAcceptsItems(false);
        setDefaultItem(Settings.GUI_TRANSACTION_VIEW_BACKGROUND_ITEM.getMaterial().parseItem());
        setUseLockedCells(Settings.GUI_TRANSACTION_VIEW_BACKGROUND_FILL.getBoolean());

        setButton(5, 0, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_CLOSE_BTN_ITEM.getString(), Settings.GUI_CLOSE_BTN_NAME.getString(), Settings.GUI_CLOSE_BTN_LORE.getStringList(), null), e -> e.manager.showGUI(e.player, new GUITransactionList(auctionPlayer)));
        setItem(1, 4, AuctionAPI.getInstance().deserializeItem(transaction.getAuctionItem().getRawItem()));

        setItem(3, 2, GuiUtils.createButtonItem(AuctionAPI.getInstance().getPlayerHead(Bukkit.getOfflinePlayer(transaction.getSeller()).getName()), TextUtils.formatText(Settings.GUI_TRANSACTION_VIEW_ITEM_SELLER_NAME.getString().replace("%seller_name%", Bukkit.getOfflinePlayer(transaction.getSeller()).getName())),
                Settings.GUI_TRANSACTION_VIEW_ITEM_SELLER_LORE.getStringList().stream().map(line -> line.replace("%seller_id%", transaction.getSeller().toString())).map(TextUtils::formatText).collect(Collectors.toList())));

        setItem(3, 6, GuiUtils.createButtonItem(AuctionAPI.getInstance().getPlayerHead(Bukkit.getOfflinePlayer(transaction.getBuyer()).getName()), TextUtils.formatText(Settings.GUI_TRANSACTION_VIEW_ITEM_BUYER_NAME.getString().replace("%buyer_name%", Bukkit.getOfflinePlayer(transaction.getBuyer()).getName())),
                Settings.GUI_TRANSACTION_VIEW_ITEM_BUYER_LORE.getStringList().stream().map(line -> line.replace("%buyer_id%", transaction.getBuyer().toString())).map(TextUtils::formatText).collect(Collectors.toList())));

        setItem(3, 4, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_TRANSACTION_VIEW_ITEM_INFO_ITEM.getString(), Settings.GUI_TRANSACTION_VIEW_ITEM_INFO_NAME.getString(), Settings.GUI_TRANSACTION_VIEW_ITEM_INFO_LORE.getStringList(), new HashMap<String, Object>() {{
            put("%transaction_id%", transaction.getId().toString());
            put("%sale_type%", transaction.getAuctionSaleType() == AuctionSaleType.USED_BIDDING_SYSTEM ? "Won Auction" : "Bought Immediately");
            put("%transaction_date%", AuctionAPI.getInstance().convertMillisToDate(transaction.getTransactionTime()));
            put("%final_price%", AuctionAPI.getInstance().formatNumber(transaction.getFinalPrice()));
        }}));
    }
}
