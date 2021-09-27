package ca.tweetzy.auctionhouse.guis;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.api.events.AuctionBidEvent;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.auction.AuctionedItem;
import ca.tweetzy.auctionhouse.guis.confirmation.GUIConfirmBid;
import ca.tweetzy.auctionhouse.helpers.ConfigurationItemHelper;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.commands.AbstractCommand;
import ca.tweetzy.core.gui.Gui;
import ca.tweetzy.core.hooks.EconomyManager;
import ca.tweetzy.core.input.PlayerChatInput;
import ca.tweetzy.core.utils.NumberUtils;
import ca.tweetzy.core.utils.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

/**
 * The current file has been created by Kiran Hart
 * Date Created: August 08 2021
 * Time Created: 5:16 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUIBid extends Gui {

    private final AuctionPlayer auctionPlayer;
    private final AuctionedItem auctionItem;

    public GUIBid(AuctionPlayer auctionPlayer, AuctionedItem auctionItem) {
        this.auctionPlayer = auctionPlayer;
        this.auctionItem = auctionItem;
        setTitle(TextUtils.formatText(Settings.GUI_BIDDING_TITLE.getString()));
        setDefaultItem(Settings.GUI_BIDDING_BG_ITEM.getMaterial().parseItem());
        setUseLockedCells(true);
        setAcceptsItems(false);
        setAllowDrops(false);
        setRows(3);
        draw();

        setOnClose(close -> close.manager.showGUI(close.player, new GUIAuctionHouse(this.auctionPlayer)));
    }

    private void draw() {
        setItem(1, 4, this.auctionItem.getItem());
        setButton(1, 2, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_BIDDING_ITEMS_DEFAULT_ITEM.getString(), Settings.GUI_BIDDING_ITEMS_DEFAULT_NAME.getString(), Settings.GUI_BIDDING_ITEMS_DEFAULT_LORE.getStringList(), null), e -> {
            if (Settings.PLAYER_NEEDS_TOTAL_PRICE_TO_BID.getBoolean() && !EconomyManager.hasBalance(e.player, auctionItem.getCurrentPrice() + auctionItem.getBidIncrementPrice())) {
                AuctionHouse.getInstance().getLocale().getMessage("general.notenoughmoney").sendPrefixedMessage(e.player);
                return;
            }

            e.gui.exit();
            e.manager.showGUI(e.player, new GUIConfirmBid(this.auctionPlayer, auctionItem));
        });

        setButton(1, 6, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_BIDDING_ITEMS_CUSTOM_ITEM.getString(), Settings.GUI_BIDDING_ITEMS_CUSTOM_NAME.getString(), Settings.GUI_BIDDING_ITEMS_CUSTOM_LORE.getStringList(), null), e -> {
            e.gui.exit();
            PlayerChatInput.PlayerChatInputBuilder<Double> builder = new PlayerChatInput.PlayerChatInputBuilder<>(AuctionHouse.getInstance(), e.player);
            builder.isValidInput((p, str) -> NumberUtils.isDouble(str) && Double.parseDouble(str) >= this.auctionItem.getBidIncrementPrice());
            builder.sendValueMessage(TextUtils.formatText(AuctionHouse.getInstance().getLocale().getMessage("prompts.enter bid amount").processPlaceholder("current_bid", AuctionAPI.getInstance().formatNumber(auctionItem.getCurrentPrice())).getMessage()));
            builder.toCancel("cancel");
            builder.onCancel(p -> e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer)));
            builder.setValue((p, value) -> Double.parseDouble(value));
            builder.onFinish((p, value) -> {
                if (value > Settings.MAX_AUCTION_INCREMENT_PRICE.getDouble()) {
                    AuctionHouse.getInstance().getLocale().getMessage("pricing.maxbidincrementprice").processPlaceholder("price", Settings.MAX_AUCTION_INCREMENT_PRICE.getDouble()).sendPrefixedMessage(e.player);
                    return;
                }

                double newBiddingAmount = 0;
                if (Settings.USE_REALISTIC_BIDDING.getBoolean()) {
                    if (value > this.auctionItem.getCurrentPrice()) {
                        newBiddingAmount = value;
                    } else {
                        if (Settings.BID_MUST_BE_HIGHER_THAN_PREVIOUS.getBoolean()) {
                            e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
                            AuctionHouse.getInstance().getLocale().getMessage("pricing.bidmusthigherthanprevious").processPlaceholder("current_bid", AuctionAPI.getInstance().formatNumber(auctionItem.getCurrentPrice())).sendPrefixedMessage(e.player);
                            return;
                        }

                        newBiddingAmount = this.auctionItem.getCurrentPrice() + value;
                    }
                } else {
                    newBiddingAmount = this.auctionItem.getCurrentPrice() + value;
                }


                if (Settings.PLAYER_NEEDS_TOTAL_PRICE_TO_BID.getBoolean() && !EconomyManager.hasBalance(e.player, newBiddingAmount)) {
                    AuctionHouse.getInstance().getLocale().getMessage("general.notenoughmoney").sendPrefixedMessage(e.player);
                    return;
                }

                if (Settings.ASK_FOR_BID_CONFIRMATION.getBoolean()) {
                    e.manager.showGUI(e.player, new GUIConfirmBid(this.auctionPlayer, auctionItem, value));
                    return;
                }

                ItemStack itemStack = auctionItem.getItem();

                OfflinePlayer oldBidder = Bukkit.getOfflinePlayer(auctionItem.getHighestBidder());
                OfflinePlayer owner = Bukkit.getOfflinePlayer(auctionItem.getOwner());

                AuctionBidEvent auctionBidEvent = new AuctionBidEvent(e.player, auctionItem, newBiddingAmount);
                Bukkit.getServer().getPluginManager().callEvent(auctionBidEvent);
                if (auctionBidEvent.isCancelled()) return;

                auctionItem.setHighestBidder(e.player.getUniqueId());
                auctionItem.setHighestBidderName(e.player.getName());
                auctionItem.setCurrentPrice(newBiddingAmount);
                if (auctionItem.getBasePrice() != -1 && Settings.SYNC_BASE_PRICE_TO_HIGHEST_PRICE.getBoolean() && auctionItem.getCurrentPrice() > auctionItem.getBasePrice()) {
                    auctionItem.setBasePrice(auctionItem.getCurrentPrice());
                }

                if (Settings.INCREASE_TIME_ON_BID.getBoolean()) {
                    auctionItem.setExpiresAt(auctionItem.getExpiresAt() + 1000L * Settings.TIME_TO_INCREASE_BY_ON_BID.getInt());
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

                if (Settings.BROADCAST_AUCTION_BID.getBoolean()) {
                    Bukkit.getOnlinePlayers().forEach(player -> AuctionHouse.getInstance().getLocale().getMessage("auction.broadcast.bid")
                            .processPlaceholder("player", e.player.getName())
                            .processPlaceholder("amount", AuctionAPI.getInstance().formatNumber(auctionItem.getCurrentPrice()))
                            .processPlaceholder("item", AuctionAPI.getInstance().getItemName(itemStack))
                            .sendPrefixedMessage(player));
                }

                e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
            });

            PlayerChatInput<Double> input = builder.build();
            input.start();
        });
    }
}
