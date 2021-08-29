package ca.tweetzy.auctionhouse.guis.confirmation;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.auction.AuctionedItem;
import ca.tweetzy.auctionhouse.guis.GUIAuctionHouse;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.gui.Gui;
import ca.tweetzy.core.hooks.EconomyManager;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.core.utils.items.TItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

/**
 * The current file has been created by Kiran Hart
 * Date Created: March 17 2021
 * Time Created: 11:18 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUIConfirmBid extends Gui {

    final AuctionPlayer auctionPlayer;
    final AuctionedItem auctionItem;
    final double bidAmount;

    public GUIConfirmBid(AuctionPlayer auctionPlayer, AuctionedItem auctionItem) {
        this(auctionPlayer, auctionItem, -1);
    }

    public GUIConfirmBid(AuctionPlayer auctionPlayer, AuctionedItem auctionItem, double bidAmount) {
        this.auctionPlayer = auctionPlayer;
        this.auctionItem = auctionItem;
        this.bidAmount = bidAmount;
        setTitle(TextUtils.formatText(Settings.GUI_CONFIRM_BID_TITLE.getString()));
        setAcceptsItems(false);
        setRows(1);
        draw();
    }

    private void draw() {
        setItems(0, 3, new TItemBuilder(Objects.requireNonNull(Settings.GUI_CONFIRM_BID_YES_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_CONFIRM_BID_YES_NAME.getString()).setLore(Settings.GUI_CONFIRM_BID_YES_LORE.getStringList()).toItemStack());
        setItem(0, 4, this.auctionItem.getItem());
        setItems(5, 8, new TItemBuilder(Objects.requireNonNull(Settings.GUI_CONFIRM_BID_NO_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_CONFIRM_BID_NO_NAME.getString()).setLore(Settings.GUI_CONFIRM_BID_NO_LORE.getStringList()).toItemStack());

        setActionForRange(5, 8, ClickType.LEFT, e -> e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer)));
        setActionForRange(0, 3, ClickType.LEFT, e -> {
            // Re-select the item to ensure that it's available
            AuctionedItem located = AuctionHouse.getInstance().getAuctionItemManager().getItem(this.auctionItem.getId());
            if (located == null) {
                e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
                return;
            }

            double toIncrementBy = this.bidAmount == -1 ? auctionItem.getBidIncrementPrice() : this.bidAmount;


            double newBiddingAmount = 0;
            if (Settings.USE_REALISTIC_BIDDING.getBoolean()) {
                if (toIncrementBy > this.auctionItem.getCurrentPrice()) {
                    newBiddingAmount = toIncrementBy;
                } else {
                    newBiddingAmount = this.auctionItem.getCurrentPrice() + toIncrementBy;
                }
            } else {
                newBiddingAmount = this.auctionItem.getCurrentPrice() + toIncrementBy;
            }

            if (Settings.PLAYER_NEEDS_TOTAL_PRICE_TO_BID.getBoolean() && !EconomyManager.hasBalance(e.player, newBiddingAmount)) {
                AuctionHouse.getInstance().getLocale().getMessage("general.notenoughmoney").sendPrefixedMessage(e.player);
                return;
            }

            ItemStack itemStack = auctionItem.getItem();

            OfflinePlayer oldBidder = Bukkit.getOfflinePlayer(auctionItem.getHighestBidder());
            OfflinePlayer owner = Bukkit.getOfflinePlayer(auctionItem.getOwner());

            auctionItem.setHighestBidder(e.player.getUniqueId());
            auctionItem.setHighestBidderName(e.player.getName());
            auctionItem.setCurrentPrice(newBiddingAmount);
            if (auctionItem.getBasePrice() != -1 && Settings.SYNC_BASE_PRICE_TO_HIGHEST_PRICE.getBoolean() && Settings.ALLOW_USAGE_OF_BUY_NOW_SYSTEM.getBoolean() && auctionItem.getCurrentPrice() > auctionItem.getBasePrice()) {
                auctionItem.setBasePrice(auctionItem.getCurrentPrice());
            }

            if (Settings.INCREASE_TIME_ON_BID.getBoolean()) {
                auctionItem.setExpiresAt(auctionItem.getExpiresAt() + 1000L * Settings.TIME_TO_INCREASE_BY_ON_BID.getInt());
            }

            if (Settings.BROADCAST_AUCTION_BID.getBoolean()) {
                Bukkit.getOnlinePlayers().forEach(player -> AuctionHouse.getInstance().getLocale().getMessage("auction.broadcast.bid")
                        .processPlaceholder("player", e.player.getName())
                        .processPlaceholder("amount", AuctionAPI.getInstance().formatNumber(auctionItem.getCurrentPrice()))
                        .processPlaceholder("item", AuctionAPI.getInstance().getItemName(itemStack))
                        .sendPrefixedMessage(player));
            }

            if (oldBidder.isOnline()) {
                AuctionHouse.getInstance().getLocale().getMessage("auction.outbid")
                        .processPlaceholder("player", e.player.getName())
                        .processPlaceholder("item", AuctionAPI.getInstance().getItemName(itemStack))
                        .sendPrefixedMessage(oldBidder.getPlayer());
            }

            if (owner.isOnline()) {
                AuctionHouse.getInstance().getLocale().getMessage("auction.placedbid")
                        .processPlaceholder("player", e.player.getName())
                        .processPlaceholder("amount", AuctionAPI.getInstance().formatNumber(auctionItem.getCurrentPrice()))
                        .processPlaceholder("item", AuctionAPI.getInstance().getItemName(itemStack))
                        .sendPrefixedMessage(owner.getPlayer());
            }

            e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
        });
    }
}
