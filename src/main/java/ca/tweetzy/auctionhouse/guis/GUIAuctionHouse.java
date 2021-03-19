package ca.tweetzy.auctionhouse.guis;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.auction.AuctionItem;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.auction.AuctionStackType;
import ca.tweetzy.auctionhouse.helpers.ConfigurationItemHelper;
import ca.tweetzy.auctionhouse.managers.SoundManager;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.gui.Gui;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.core.utils.items.TItemBuilder;
import org.bukkit.Bukkit;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: March 14 2021
 * Time Created: 6:34 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUIAuctionHouse extends Gui {

    final AuctionPlayer auctionPlayer;
    final List<AuctionItem> items;

    private int taskId;

    public GUIAuctionHouse(AuctionPlayer auctionPlayer) {
        this.auctionPlayer = auctionPlayer;
        this.items = AuctionHouse.getInstance().getAuctionItemManager().getAuctionItems().stream().filter(item -> !item.isExpired()).collect(Collectors.toList());
        setTitle(TextUtils.formatText(Settings.GUI_AUCTION_HOUSE_TITLE.getString()));
        setRows(6);
        setAcceptsItems(false);
        draw();

        if (Settings.AUTO_REFRESH_AUCTION_PAGES.getBoolean()) {
            setOnOpen(e -> {
                taskId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(AuctionHouse.getInstance(), this::draw, 0L, Settings.TICK_UPDATE_TIME.getInt());
            });

            setOnClose(e -> {
                Bukkit.getServer().getScheduler().cancelTask(taskId);
            });
        }
    }

    public void draw() {
        reset();

        // Pagination
        pages = (int) Math.max(1, Math.ceil(this.items.size() / (double) 45));
        setPrevPage(5, 3, new TItemBuilder(Objects.requireNonNull(Settings.GUI_BACK_BTN_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_BACK_BTN_NAME.getString()).setLore(Settings.GUI_BACK_BTN_LORE.getStringList()).toItemStack());
        setButton(5, 4, new TItemBuilder(Objects.requireNonNull(Settings.GUI_REFRESH_BTN_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_REFRESH_BTN_NAME.getString()).setLore(Settings.GUI_REFRESH_BTN_LORE.getStringList()).toItemStack(), e -> e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer)));
        setNextPage(5, 5, new TItemBuilder(Objects.requireNonNull(Settings.GUI_NEXT_BTN_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_NEXT_BTN_NAME.getString()).setLore(Settings.GUI_NEXT_BTN_LORE.getStringList()).toItemStack());
        setOnPage(e -> {
            draw();
            SoundManager.getInstance().playSound(this.auctionPlayer.getPlayer(), Settings.SOUNDS_NAVIGATE_GUI_PAGES.getString(), 1.0F, 1.0F);
        });

        // Other Buttons
        setButton(5, 0, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_AUCTION_HOUSE_ITEMS_YOUR_AUCTIONS_ITEM.getString(), Settings.GUI_AUCTION_HOUSE_ITEMS_YOUR_AUCTIONS_NAME.getString(), Settings.GUI_AUCTION_HOUSE_ITEMS_YOUR_AUCTIONS_LORE.getStringList(), new HashMap<String, Object>(){{
            put("%active_player_auctions%", auctionPlayer.getItems(false).size());
        }}), e -> e.manager.showGUI(e.player, new GUIActiveAuctions(this.auctionPlayer)));

        setButton(5, 1, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_AUCTION_HOUSE_ITEMS_COLLECTION_BIN_ITEM.getString(), Settings.GUI_AUCTION_HOUSE_ITEMS_COLLECTION_BIN_NAME.getString(), Settings.GUI_AUCTION_HOUSE_ITEMS_COLLECTION_BIN_LORE.getStringList(), new HashMap<String, Object>(){{
            put("%expired_player_auctions%", auctionPlayer.getItems(true).size());
        }}), e -> e.manager.showGUI(e.player, new GUIExpiredItems(this.auctionPlayer)));

        setButton(5, 6, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_AUCTION_HOUSE_ITEMS_TRANSACTIONS_ITEM.getString(), Settings.GUI_AUCTION_HOUSE_ITEMS_TRANSACTIONS_NAME.getString(), Settings.GUI_AUCTION_HOUSE_ITEMS_TRANSACTIONS_LORE.getStringList(), null), null);
        setButton(5, 7, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_AUCTION_HOUSE_ITEMS_HOW_TO_SELL_ITEM.getString(), Settings.GUI_AUCTION_HOUSE_ITEMS_HOW_TO_SELL_NAME.getString(), Settings.GUI_AUCTION_HOUSE_ITEMS_HOW_TO_SELL_LORE.getStringList(), null), null);
        setButton(5, 8, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_AUCTION_HOUSE_ITEMS_GUIDE_ITEM.getString(), Settings.GUI_AUCTION_HOUSE_ITEMS_GUIDE_NAME.getString(), Settings.GUI_AUCTION_HOUSE_ITEMS_GUIDE_LORE.getStringList(), null), null);

        // Items
        int slot = 0;
        List<AuctionItem> data = this.items.stream().sorted(Comparator.comparingInt(AuctionItem::getRemainingTime).reversed()).skip((page - 1) * 45L).limit(45).collect(Collectors.toList());
        for (AuctionItem auctionItem : data) {
            setButton(slot++, auctionItem.getDisplayStack(AuctionStackType.MAIN_AUCTION_HOUSE), e -> {
                switch (e.clickType) {
                    case LEFT:
                        if (auctionItem.getBidStartPrice() <= 0) {
                            if (e.player.getUniqueId().equals(auctionItem.getOwner()) && !Settings.OWNER_CAN_PURCHASE_OWN_ITEM.getBoolean()) {
                                AuctionHouse.getInstance().getLocale().getMessage("general.cantbuyown").sendPrefixedMessage(e.player);
                                return;
                            }
                            e.manager.showGUI(e.player, new GUIConfirmPurchase(this.auctionPlayer, auctionItem));
                        } else {
                            if (e.player.getUniqueId().equals(auctionItem.getOwner()) && !Settings.OWNER_CAN_BID_OWN_ITEM.getBoolean()) {
                                AuctionHouse.getInstance().getLocale().getMessage("general.cantbidonown").sendPrefixedMessage(e.player);
                                return;
                            }

                            auctionItem.setCurrentPrice(auctionItem.getCurrentPrice() + auctionItem.getBidIncPrice());

                            if (Settings.INCREASE_TIME_ON_BID.getBoolean()) {
                                auctionItem.setRemainingTime(auctionItem.getRemainingTime() + Settings.TIME_TO_INCREASE_BY_ON_BID.getInt());
                            }

                            if (Settings.REFRESH_GUI_WHEN_BID.getBoolean()) {
                                e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
                            }
                        }
                        break;
                    case MIDDLE:
                        if (e.player.isOp() || e.player.hasPermission("auctionhouse.admin")) {
                            AuctionHouse.getInstance().getAuctionItemManager().removeItem(auctionItem.getKey());
                            e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
                        }
                        break;
                    case RIGHT:
                        if (auctionItem.getBidStartPrice() >= Settings.MIN_AUCTION_START_PRICE.getDouble()) {
                            if (e.player.getUniqueId().equals(auctionItem.getOwner()) && !Settings.OWNER_CAN_PURCHASE_OWN_ITEM.getBoolean()) {
                                AuctionHouse.getInstance().getLocale().getMessage("general.cantbuyown").sendPrefixedMessage(e.player);
                                return;
                            }
                            e.manager.showGUI(e.player, new GUIConfirmPurchase(this.auctionPlayer, auctionItem));
                        }
                        break;
                }
            });
        }
    }
}
