package ca.tweetzy.auctionhouse.guis;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.auction.AuctionItem;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.gui.Gui;
import ca.tweetzy.core.utils.PlayerUtils;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.core.utils.items.TItemBuilder;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.ClickType;

import java.util.Objects;

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
        setItems(0, 3, new TItemBuilder(Objects.requireNonNull(Settings.GUI_CONFIRM_BUY_YES_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_CONFIRM_BUY_YES_NAME.getString()).setLore(Settings.GUI_CONFIRM_BUY_YES_LORE.getStringList()).toItemStack());
        setItem(0, 4, AuctionAPI.getInstance().deserializeItem(this.auctionItem.getRawItem()));
        setItems(5, 8, new TItemBuilder(Objects.requireNonNull(Settings.GUI_CONFIRM_BUY_NO_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_CONFIRM_BUY_NO_NAME.getString()).setLore(Settings.GUI_CONFIRM_BUY_NO_LORE.getStringList()).toItemStack());

        setActionForRange(5, 8, ClickType.LEFT, e -> e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer)));
        setActionForRange(0, 3, ClickType.LEFT, e -> {
            AuctionItem located = AuctionHouse.getInstance().getAuctionItemManager().getItem(this.auctionItem.getKey());

            if (located == null || located.isExpired()) {
                AuctionHouse.getInstance().getLocale().getMessage("auction.itemnotavailable").sendPrefixedMessage(e.player);
                e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
                return;
            }

            if (!AuctionHouse.getInstance().getEconomy().has(e.player, located.getBasePrice())) {
                AuctionHouse.getInstance().getLocale().getMessage("general.notenoughmoney").sendPrefixedMessage(e.player);
                e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
                return;
            }

            AuctionHouse.getInstance().getEconomy().withdrawPlayer(e.player, located.getBasePrice());
            AuctionHouse.getInstance().getAuctionItemManager().removeItem(located.getKey());
            PlayerUtils.giveItem(e.player, AuctionAPI.getInstance().deserializeItem(located.getRawItem()));

            AuctionHouse.getInstance().getLocale().getMessage("pricing.moneyremove").processPlaceholder("price", String.format("%,.2f", located.getCurrentPrice())).sendPrefixedMessage(e.player);

            if (Bukkit.getOfflinePlayer(located.getOwner()).isOnline()) {
                AuctionHouse.getInstance().getLocale().getMessage("auction.itemsold")
                        .processPlaceholder("item", WordUtils.capitalizeFully(AuctionAPI.getInstance().deserializeItem(located.getRawItem()).getType().name().replace("_", " ")))
                        .processPlaceholder("price", String.format("%,.2f", located.getCurrentPrice()))
                        .sendPrefixedMessage(Bukkit.getOfflinePlayer(located.getOwner()).getPlayer());
                AuctionHouse.getInstance().getLocale().getMessage("pricing.moneyadd").processPlaceholder("price", String.format("%,.2f", located.getCurrentPrice())).sendPrefixedMessage(Bukkit.getOfflinePlayer(located.getOwner()).getPlayer());
            }
        });
    }
}
