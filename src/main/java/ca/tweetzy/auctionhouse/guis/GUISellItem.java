package ca.tweetzy.auctionhouse.guis;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.api.events.AuctionStartEvent;
import ca.tweetzy.auctionhouse.auction.AuctionItem;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.helpers.ConfigurationItemHelper;
import ca.tweetzy.auctionhouse.helpers.MaterialCategorizer;
import ca.tweetzy.auctionhouse.managers.SoundManager;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.commands.AbstractCommand;
import ca.tweetzy.core.compatibility.XMaterial;
import ca.tweetzy.core.gui.Gui;
import ca.tweetzy.core.gui.events.GuiClickEvent;
import ca.tweetzy.core.input.ChatPrompt;
import ca.tweetzy.core.utils.NumberUtils;
import ca.tweetzy.core.utils.PlayerUtils;
import ca.tweetzy.core.utils.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

/**
 * The current file has been created by Kiran Hart
 * Date Created: June 27 2021
 * Time Created: 10:28 a.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */

public class GUISellItem extends Gui {

    private final AuctionPlayer auctionPlayer;
    private ItemStack itemToBeListed;

    private double buyNowPrice;
    private double bidStartPrice;
    private double bidIncrementPrice;
    private boolean isBiddingItem;
    private boolean isAllowingBuyNow;

    public GUISellItem(AuctionPlayer auctionPlayer, ItemStack itemToBeListed, double buyNowPrice, double bidStartPrice, double bidIncrementPrice, boolean isBiddingItem, boolean isAllowingBuyNow) {
        this.auctionPlayer = auctionPlayer;
        this.itemToBeListed = itemToBeListed;
        this.buyNowPrice = buyNowPrice;
        this.bidStartPrice = bidStartPrice;
        this.bidIncrementPrice = bidIncrementPrice;
        this.isBiddingItem = isBiddingItem;
        this.isAllowingBuyNow = isAllowingBuyNow;
        setTitle(TextUtils.formatText(Settings.GUI_SELL_TITLE.getString()));
        setDefaultItem(Settings.GUI_SELL_BG_ITEM.getMaterial().parseItem());
        setUseLockedCells(true);
        setAcceptsItems(true);
        setRows(5);
        draw();

        setOnOpen(open -> {
            // Check if they are already using a sell gui
            if (ChatPrompt.isRegistered(open.player)) {
                AuctionHouse.getInstance().getLocale().getMessage("general.finishenteringprice").sendPrefixedMessage(open.player);
                open.gui.exit();
            }
        });

        setOnClose(close -> {
            ItemStack toGiveBack = AuctionHouse.getInstance().getAuctionPlayerManager().getSellHolding().get(close.player.getUniqueId());
            if (toGiveBack != null && toGiveBack.getType() != XMaterial.AIR.parseMaterial()) {
                PlayerUtils.giveItem(close.player, toGiveBack);
            } else {
                PlayerUtils.giveItem(close.player, getItem(1, 4));
            }

            AuctionHouse.getInstance().getAuctionPlayerManager().getSellHolding().remove(close.player.getUniqueId());
        });

        setUnlocked(1, 4);
        setUnlockedRange(45, 89);
    }

    public GUISellItem(AuctionPlayer auctionPlayer, ItemStack itemToBeListed) {
        this(auctionPlayer, itemToBeListed, Settings.MIN_AUCTION_PRICE.getDouble(), Settings.MIN_AUCTION_INCREMENT_PRICE.getDouble(), Settings.MIN_AUCTION_START_PRICE.getDouble(), false, true);
    }

    private void draw() {
        reset();

        // the draw item that is being listed
        setButton(1, 4, this.itemToBeListed, e -> {
            // Is the user selling with an item in hand?
            if (AuctionHouse.getInstance().getAuctionPlayerManager().getSellHolding().containsKey(e.player.getUniqueId())) {
                if (AuctionHouse.getInstance().getAuctionPlayerManager().getSellHolding().get(e.player.getUniqueId()).getType() != XMaterial.AIR.parseMaterial()) {
                    e.event.setCancelled(true);
                }
            }
        });

        setButton(3, 1, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_SELL_ITEMS_BUY_NOW_ITEM.getString(), Settings.GUI_SELL_ITEMS_BUY_NOW_NAME.getString(), Settings.GUI_SELL_ITEMS_BUY_NOW_LORE.getStringList(), new HashMap<String, Object>() {{
            put("%buy_now_price%", AuctionAPI.getInstance().formatNumber(buyNowPrice));
        }}), ClickType.LEFT, e -> {
            setTheItemToBeListed();
            e.gui.exit();

            ChatPrompt.showPrompt(AuctionHouse.getInstance(), this.auctionPlayer.getPlayer(), TextUtils.formatText(AuctionHouse.getInstance().getLocale().getMessage("prompts.enter new buy now price").getMessage()), chat -> {
                String msg = chat.getMessage();
                if (validateChatNumber(msg, Settings.MIN_AUCTION_PRICE.getDouble())) {
                    // check if the buy now price is higher than the bid start price
                    if (this.isAllowingBuyNow && this.isBiddingItem && Settings.BASE_PRICE_MUST_BE_HIGHER_THAN_BID_START.getBoolean() && Double.parseDouble(msg) < this.bidStartPrice) {
                        reopen(e);
                        return;
                    }

                    this.buyNowPrice = Double.parseDouble(msg);
                }
                reopen(e);
            }).setOnCancel(() -> reopen(e)).setOnClose(() -> reopen(e));
        });

        if (this.isBiddingItem) {
            setButton(3, 2, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_SELL_ITEMS_STARTING_BID_ITEM.getString(), Settings.GUI_SELL_ITEMS_STARTING_BID_NAME.getString(), Settings.GUI_SELL_ITEMS_STARTING_BID_LORE.getStringList(), new HashMap<String, Object>() {{
                put("%starting_bid_price%", AuctionAPI.getInstance().formatNumber(bidStartPrice));
            }}), ClickType.LEFT, e -> {
                setTheItemToBeListed();
                e.gui.exit();
                ChatPrompt.showPrompt(AuctionHouse.getInstance(), this.auctionPlayer.getPlayer(), TextUtils.formatText(AuctionHouse.getInstance().getLocale().getMessage("prompts.enter new starting bid").getMessage()), chat -> {
                    String msg = chat.getMessage();
                    if (validateChatNumber(msg, Settings.MIN_AUCTION_START_PRICE.getDouble())) {
                        this.bidStartPrice = Double.parseDouble(msg);
                    }
                    reopen(e);
                }).setOnCancel(() -> reopen(e)).setOnClose(() -> reopen(e));
            });

            setButton(3, 3, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_SELL_ITEMS_BID_INC_ITEM.getString(), Settings.GUI_SELL_ITEMS_BID_INC_NAME.getString(), Settings.GUI_SELL_ITEMS_BID_INC_LORE.getStringList(), new HashMap<String, Object>() {{
                put("%bid_increment_price%", AuctionAPI.getInstance().formatNumber(bidIncrementPrice));
            }}), ClickType.LEFT, e -> {
                setTheItemToBeListed();
                e.gui.exit();
                ChatPrompt.showPrompt(AuctionHouse.getInstance(), this.auctionPlayer.getPlayer(), TextUtils.formatText(AuctionHouse.getInstance().getLocale().getMessage("prompts.enter new bid increment").getMessage()), chat -> {
                    String msg = chat.getMessage();
                    if (validateChatNumber(msg, Settings.MIN_AUCTION_INCREMENT_PRICE.getDouble())) {
                        this.bidIncrementPrice = Double.parseDouble(msg);
                    }
                    reopen(e);
                }).setOnCancel(() -> reopen(e)).setOnClose(() -> reopen(e));
            });

            setButton(3, 6, ConfigurationItemHelper.createConfigurationItem(this.isAllowingBuyNow ? Settings.GUI_SELL_ITEMS_BUY_NOW_ENABLED_ITEM.getString() : Settings.GUI_SELL_ITEMS_BUY_NOW_DISABLED_ITEM.getString(), this.isAllowingBuyNow ? Settings.GUI_SELL_ITEMS_BUY_NOW_ENABLED_NAME.getString() : Settings.GUI_SELL_ITEMS_BUY_NOW_DISABLED_NAME.getString(), this.isAllowingBuyNow ? Settings.GUI_SELL_ITEMS_BUY_NOW_ENABLED_LORE.getStringList() : Settings.GUI_SELL_ITEMS_BUY_NOW_DISABLED_LORE.getStringList(), null), ClickType.LEFT, e -> {
                this.isAllowingBuyNow = !this.isAllowingBuyNow;
                setTheItemToBeListed();
                draw();
            });

        }

        setButton(3, 5, ConfigurationItemHelper.createConfigurationItem(this.isBiddingItem ? Settings.GUI_SELL_ITEMS_BIDDING_ENABLED_ITEM.getString() : Settings.GUI_SELL_ITEMS_BIDDING_DISABLED_ITEM.getString(), this.isBiddingItem ? Settings.GUI_SELL_ITEMS_BIDDING_ENABLED_NAME.getString() : Settings.GUI_SELL_ITEMS_BIDDING_DISABLED_NAME.getString(), this.isBiddingItem ? Settings.GUI_SELL_ITEMS_BIDDING_ENABLED_LORE.getStringList() : Settings.GUI_SELL_ITEMS_BIDDING_DISABLED_LORE.getStringList(), null), e -> {
            if (!Settings.ALLOW_USAGE_OF_BID_SYSTEM.getBoolean()) {
                return;
            }

            this.isBiddingItem = !this.isBiddingItem;
            setTheItemToBeListed();
            draw();
        });

        setButton(3, 7, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_SELL_ITEMS_CONFIRM_LISTING_ITEM.getString(), Settings.GUI_SELL_ITEMS_CONFIRM_LISTING_NAME.getString(), Settings.GUI_SELL_ITEMS_CONFIRM_LISTING_LORE.getStringList(), null), e -> {
            // if the item in the sell slot is null then stop the listing
            if (getItem(1, 4) == null || getItem(1, 4).getType() == XMaterial.AIR.parseMaterial()) return;
            setTheItemToBeListed();

            AuctionItem auctionItem = new AuctionItem(
                    e.player.getUniqueId(),
                    e.player.getUniqueId(),
                    this.itemToBeListed,
                    MaterialCategorizer.getMaterialCategory(itemToBeListed),
                    UUID.randomUUID(),
                    this.isBiddingItem && !isAllowingBuyNow ? -1 : buyNowPrice,
                    this.isBiddingItem ? bidStartPrice : 0,
                    this.isBiddingItem ? bidIncrementPrice : 0,
                    this.isBiddingItem ? bidStartPrice : buyNowPrice,
                    this.auctionPlayer.getAllowedSellTime(),
                    false
            );

            AuctionStartEvent auctionStartEvent = new AuctionStartEvent(e.player, auctionItem);
            Bukkit.getServer().getPluginManager().callEvent(auctionStartEvent);
            if (auctionStartEvent.isCancelled()) return;

            AuctionHouse.getInstance().getAuctionItemManager().addItem(auctionItem);
            SoundManager.getInstance().playSound(e.player, Settings.SOUNDS_LISTED_ITEM_ON_AUCTION_HOUSE.getString(), 1.0F, 1.0F);
            AuctionHouse.getInstance().getAuctionPlayerManager().removeItemFromSellHolding(e.player.getUniqueId());

            if (Settings.OPEN_MAIN_AUCTION_HOUSE_AFTER_MENU_LIST.getBoolean()) {
                e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
            } else {
                e.gui.exit();
            }

            // TODO FIGURE OUT WHY THE HELL THIS IS NOT WORKING
            String NAX = AuctionHouse.getInstance().getLocale().getMessage("auction.biditemwithdisabledbuynow").getMessage();

            AuctionHouse.getInstance().getLocale().getMessage(isBiddingItem ? "auction.listed.withbid" : "auction.listed.nobid")
                    .processPlaceholder("player", e.player.getName())
                    .processPlaceholder("amount", itemToBeListed.getAmount())
                    .processPlaceholder("item", AuctionAPI.getInstance().getItemName(itemToBeListed))
                    .processPlaceholder("base_price", !isAllowingBuyNow ? NAX : AuctionAPI.getInstance().formatNumber(buyNowPrice))
                    .processPlaceholder("start_price", isBiddingItem ? AuctionAPI.getInstance().formatNumber(bidStartPrice) : 0)
                    .processPlaceholder("increment_price", isBiddingItem ? AuctionAPI.getInstance().formatNumber(bidIncrementPrice) : 0).sendPrefixedMessage(e.player);

            if (Settings.BROADCAST_AUCTION_LIST.getBoolean()) {
                Bukkit.getOnlinePlayers().forEach(AuctionHouse.getInstance().getLocale().getMessage(isBiddingItem ? "auction.listed.withbid" : "auction.broadcast.nobid")
                        .processPlaceholder("player", e.player.getName())
                        .processPlaceholder("amount", itemToBeListed.getAmount())
                        .processPlaceholder("item", AuctionAPI.getInstance().getItemName(itemToBeListed))
                        .processPlaceholder("base_price", !isAllowingBuyNow ? NAX : AuctionAPI.getInstance().formatNumber(buyNowPrice))
                        .processPlaceholder("start_price", isBiddingItem ? AuctionAPI.getInstance().formatNumber(bidStartPrice) : 0)
                        .processPlaceholder("increment_price", isBiddingItem ? AuctionAPI.getInstance().formatNumber(bidIncrementPrice) : 0)::sendPrefixedMessage);
            }
        });
    }

    private boolean validateChatNumber(String input, double requirement) {
        return input != null && input.length() != 0 && NumberUtils.isDouble(input) && Double.parseDouble(input) >= requirement;
    }

    private void reopen(GuiClickEvent e) {
        e.manager.showGUI(e.player, new GUISellItem(this.auctionPlayer, this.itemToBeListed, this.buyNowPrice, this.bidStartPrice, this.bidIncrementPrice, this.isBiddingItem, this.isAllowingBuyNow));
    }

    private void setTheItemToBeListed() {
        this.itemToBeListed = getItem(1, 4);
    }
}
