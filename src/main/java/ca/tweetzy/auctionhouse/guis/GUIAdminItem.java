package ca.tweetzy.auctionhouse.guis;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.auction.AuctionItem;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.helpers.ConfigurationItemHelper;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.gui.Gui;
import ca.tweetzy.core.utils.PlayerUtils;
import ca.tweetzy.core.utils.TextUtils;
import org.bukkit.inventory.ItemStack;

/**
 * The current file has been created by Kiran Hart
 * Date Created: July 14 2021
 * Time Created: 3:04 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUIAdminItem extends Gui {

    private final AuctionPlayer auctionPlayer;
    private final AuctionItem auctionItem;

    public GUIAdminItem(AuctionPlayer auctionPlayer, AuctionItem auctionItem) {
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
        setButton(1, 2, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_ITEM_ADMIN_ITEMS_RETURN_ITEM.getString(), Settings.GUI_ITEM_ADMIN_ITEMS_RETURN_NAME.getString(), Settings.GUI_ITEM_ADMIN_ITEMS_RETURN_LORE.getStringList(), null), e -> {
            this.auctionItem.setRemainingTime(0);
            this.auctionItem.setExpired(true);
            e.gui.close();
        });

        setButton(1, 4, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_ITEM_ADMIN_ITEMS_CLAIM_ITEM.getString(), Settings.GUI_ITEM_ADMIN_ITEMS_CLAIM_NAME.getString(), Settings.GUI_ITEM_ADMIN_ITEMS_CLAIM_LORE.getStringList(), null), e -> {
            ItemStack item = AuctionAPI.getInstance().deserializeItem(this.auctionItem.getRawItem());
            PlayerUtils.giveItem(e.player, item);
            AuctionHouse.getInstance().getAuctionItemManager().sendToGarbage(this.auctionItem);
            e.gui.close();
        });

        setButton(1, 6, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_ITEM_ADMIN_ITEMS_DELETE_ITEM.getString(), Settings.GUI_ITEM_ADMIN_ITEMS_DELETE_NAME.getString(), Settings.GUI_ITEM_ADMIN_ITEMS_DELETE_LORE.getStringList(), null), e -> {
            AuctionHouse.getInstance().getAuctionItemManager().sendToGarbage(this.auctionItem);
            e.gui.close();
        });
    }
}
