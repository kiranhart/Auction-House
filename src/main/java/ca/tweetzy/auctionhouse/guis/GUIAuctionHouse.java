package ca.tweetzy.auctionhouse.guis;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.auction.*;
import ca.tweetzy.auctionhouse.guis.transaction.GUITransactionList;
import ca.tweetzy.auctionhouse.helpers.ConfigurationItemHelper;
import ca.tweetzy.auctionhouse.managers.SoundManager;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.compatibility.ServerVersion;
import ca.tweetzy.core.gui.Gui;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.core.utils.items.TItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.ShulkerBox;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.scheduler.BukkitTask;

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

// TODO CLEAN UP THE ENTIRE CLICK SYSTEM, IT'S KINDA MESSY
public class GUIAuctionHouse extends Gui {

    final AuctionPlayer auctionPlayer;
    List<AuctionItem> items;

    private int taskId;
    private BukkitTask task;
    private AuctionItemCategory filterCategory = AuctionItemCategory.ALL;
    private AuctionSaleType filterAuctionType = AuctionSaleType.BOTH;

    public GUIAuctionHouse(AuctionPlayer auctionPlayer) {
        this.auctionPlayer = auctionPlayer;
        this.items = AuctionHouse.getInstance().getAuctionItemManager().getAuctionItems().stream().filter(item -> !item.isExpired()).collect(Collectors.toList());
        setTitle(TextUtils.formatText(Settings.GUI_AUCTION_HOUSE_TITLE.getString()));
        setRows(6);
        setAcceptsItems(false);
        draw();

        if (Settings.AUTO_REFRESH_AUCTION_PAGES.getBoolean()) {
            setOnOpen(e -> makeMess());
            setOnClose(e -> cleanup());
        }
    }

    public GUIAuctionHouse(AuctionPlayer auctionPlayer, String phrase) {
        this(auctionPlayer);
        // re-fetch the auction items since we wanna filter out any items that match the phrase
        this.items = this.items.stream().filter(auctionItem -> AuctionAPI.getInstance().match(phrase, ChatColor.stripColor(auctionItem.getItemName())) || AuctionAPI.getInstance().match(phrase, auctionItem.getCategory().getType()) || AuctionAPI.getInstance().match(phrase, auctionItem.getCategory().getTranslatedType()) || AuctionAPI.getInstance().match(phrase, Bukkit.getOfflinePlayer(auctionItem.getOwner()).getName())).collect(Collectors.toList());
    }

    public GUIAuctionHouse(AuctionPlayer auctionPlayer, AuctionItemCategory filterCategory, AuctionSaleType filterAuctionType) {
        this(auctionPlayer);
        this.filterCategory = filterCategory;
        this.filterAuctionType = filterAuctionType;

        // Apply any filtering, there is probably a cleaner way of doing this, but I'm blanking
        if (this.filterCategory != AuctionItemCategory.ALL)
            items = items.stream().filter(item -> item.getCategory() == this.filterCategory).collect(Collectors.toList());
        if (this.filterAuctionType != AuctionSaleType.BOTH)
            items = items.stream().filter(item -> this.filterAuctionType == AuctionSaleType.USED_BIDDING_SYSTEM ? item.getBidStartPrice() >= Settings.MIN_AUCTION_START_PRICE.getDouble() : item.getBidStartPrice() <= 0).collect(Collectors.toList());
    }

    public void draw() {
        reset();

        // Pagination
        pages = (int) Math.max(1, Math.ceil(this.items.size() / (double) 45));
        setPrevPage(5, 3, new TItemBuilder(Objects.requireNonNull(Settings.GUI_BACK_BTN_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_BACK_BTN_NAME.getString()).setLore(Settings.GUI_BACK_BTN_LORE.getStringList()).toItemStack());
        setButton(5, 4, new TItemBuilder(Objects.requireNonNull(Settings.GUI_REFRESH_BTN_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_REFRESH_BTN_NAME.getString()).setLore(Settings.GUI_REFRESH_BTN_LORE.getStringList()).toItemStack(), e -> {
            if (Settings.USE_REFRESH_COOL_DOWN.getBoolean()) {
                if (AuctionHouse.getInstance().getAuctionPlayerManager().getCooldowns().containsKey(this.auctionPlayer.getPlayer().getUniqueId())) {
                    if (AuctionHouse.getInstance().getAuctionPlayerManager().getCooldowns().get(this.auctionPlayer.getPlayer().getUniqueId()) > System.currentTimeMillis()) {
                        return;
                    }
                }
                AuctionHouse.getInstance().getAuctionPlayerManager().addCooldown(this.auctionPlayer.getPlayer().getUniqueId());
            }
            cleanup();
            e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
        });
        setNextPage(5, 5, new TItemBuilder(Objects.requireNonNull(Settings.GUI_NEXT_BTN_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_NEXT_BTN_NAME.getString()).setLore(Settings.GUI_NEXT_BTN_LORE.getStringList()).toItemStack());
        setOnPage(e -> {
            draw();
            SoundManager.getInstance().playSound(this.auctionPlayer.getPlayer(), Settings.SOUNDS_NAVIGATE_GUI_PAGES.getString(), 1.0F, 1.0F);
        });

        // Other Buttons
        setButton(5, 0, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_AUCTION_HOUSE_ITEMS_YOUR_AUCTIONS_ITEM.getString(), Settings.GUI_AUCTION_HOUSE_ITEMS_YOUR_AUCTIONS_NAME.getString(), Settings.GUI_AUCTION_HOUSE_ITEMS_YOUR_AUCTIONS_LORE.getStringList(), new HashMap<String, Object>() {{
            put("%active_player_auctions%", auctionPlayer.getItems(false).size());
        }}), e -> {
            cleanup();
            e.manager.showGUI(e.player, new GUIActiveAuctions(this.auctionPlayer));
        });

        setButton(5, 1, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_AUCTION_HOUSE_ITEMS_COLLECTION_BIN_ITEM.getString(), Settings.GUI_AUCTION_HOUSE_ITEMS_COLLECTION_BIN_NAME.getString(), Settings.GUI_AUCTION_HOUSE_ITEMS_COLLECTION_BIN_LORE.getStringList(), new HashMap<String, Object>() {{
            put("%expired_player_auctions%", auctionPlayer.getItems(true).size());
        }}), e -> {
            cleanup();
            e.manager.showGUI(e.player, new GUIExpiredItems(this.auctionPlayer));
        });

        setButton(5, 2, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_AUCTION_HOUSE_ITEMS_FILTER_ITEM.getString(), Settings.GUI_AUCTION_HOUSE_ITEMS_FILTER_NAME.getString(), Settings.GUI_AUCTION_HOUSE_ITEMS_FILTER_LORE.getStringList(), new HashMap<String, Object>() {{
            put("%filter_category%", filterCategory.getTranslatedType());
            put("%filter_auction_type%", filterAuctionType.getTranslatedType());
        }}), e -> {
            switch (e.clickType) {
                case LEFT:
                    this.filterCategory = this.filterCategory.next();
                    if (Settings.REFRESH_GUI_ON_FILTER_CHANGE.getBoolean()) {
                        cleanup();
                        e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer, this.filterCategory, this.filterAuctionType));
                    } else {
                        draw();
                    }
                    break;
                case RIGHT:
                    this.filterAuctionType = this.filterAuctionType.next();
                    if (Settings.REFRESH_GUI_ON_FILTER_CHANGE.getBoolean()) {
                        cleanup();
                        e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer, this.filterCategory, this.filterAuctionType));
                    } else {
                        draw();
                    }
                    break;
            }
        });

        setButton(5, 6, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_AUCTION_HOUSE_ITEMS_TRANSACTIONS_ITEM.getString(), Settings.GUI_AUCTION_HOUSE_ITEMS_TRANSACTIONS_NAME.getString(), Settings.GUI_AUCTION_HOUSE_ITEMS_TRANSACTIONS_LORE.getStringList(), null), e -> e.manager.showGUI(e.player, new GUITransactionList(this.auctionPlayer)));
        setButton(5, 7, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_AUCTION_HOUSE_ITEMS_HOW_TO_SELL_ITEM.getString(), Settings.GUI_AUCTION_HOUSE_ITEMS_HOW_TO_SELL_NAME.getString(), Settings.GUI_AUCTION_HOUSE_ITEMS_HOW_TO_SELL_LORE.getStringList(), null), null);
        setButton(5, 8, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_AUCTION_HOUSE_ITEMS_GUIDE_ITEM.getString(), Settings.GUI_AUCTION_HOUSE_ITEMS_GUIDE_NAME.getString(), Settings.GUI_AUCTION_HOUSE_ITEMS_GUIDE_LORE.getStringList(), null), null);

        // Items
        List<AuctionItem> data = this.items.stream().sorted(Comparator.comparingInt(AuctionItem::getRemainingTime).reversed()).skip((page - 1) * 45L).limit(45).collect(Collectors.toList());
        if (Settings.FILL_ITEMS_USING_ASYNC.getBoolean()) {
            Bukkit.getServer().getScheduler().runTaskAsynchronously(AuctionHouse.getInstance(), () -> placeItems(data));
            return;
        }

        placeItems(data);
    }

    private void placeItems(List<AuctionItem> data) {
        int slot = 0;
        for (AuctionItem auctionItem : data) {
            setButton(slot++, auctionItem.getDisplayStack(AuctionStackType.MAIN_AUCTION_HOUSE), e -> {
                switch (e.clickType) {
                    case LEFT:
                        if (auctionItem.getBidStartPrice() <= 0) {
                            if (e.player.getUniqueId().equals(auctionItem.getOwner()) && !Settings.OWNER_CAN_PURCHASE_OWN_ITEM.getBoolean()) {
                                AuctionHouse.getInstance().getLocale().getMessage("general.cantbuyown").sendPrefixedMessage(e.player);
                                return;
                            }

                            if (!AuctionHouse.getInstance().getEconomy().has(e.player, auctionItem.getBasePrice())) {
                                AuctionHouse.getInstance().getLocale().getMessage("general.notenoughmoney").sendPrefixedMessage(e.player);
                                return;
                            }

                            cleanup();
                            e.manager.showGUI(e.player, new GUIConfirmPurchase(this.auctionPlayer, auctionItem, false));
                        } else {
                            if (e.player.getUniqueId().equals(auctionItem.getOwner()) && !Settings.OWNER_CAN_BID_OWN_ITEM.getBoolean()) {
                                AuctionHouse.getInstance().getLocale().getMessage("general.cantbidonown").sendPrefixedMessage(e.player);
                                return;
                            }

                            if (Settings.PLAYER_NEEDS_TOTAL_PRICE_TO_BID.getBoolean() && !AuctionHouse.getInstance().getEconomy().has(e.player, auctionItem.getCurrentPrice() + auctionItem.getBidIncPrice())) {
                                AuctionHouse.getInstance().getLocale().getMessage("general.notenoughmoney").sendPrefixedMessage(e.player);
                                return;
                            }

                            auctionItem.setHighestBidder(e.player.getUniqueId());
                            auctionItem.setCurrentPrice(auctionItem.getCurrentPrice() + auctionItem.getBidIncPrice());

                            if (Settings.INCREASE_TIME_ON_BID.getBoolean()) {
                                auctionItem.setRemainingTime(auctionItem.getRemainingTime() + Settings.TIME_TO_INCREASE_BY_ON_BID.getInt());
                            }

                            if (Settings.REFRESH_GUI_WHEN_BID.getBoolean()) {
                                cleanup();
                                e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
                            }
                        }
                        break;
                    case MIDDLE:
                        if (e.player.isOp() || e.player.hasPermission("auctionhouse.admin")) {
                            if (Settings.SEND_REMOVED_ITEM_BACK_TO_PLAYER.getBoolean()) {
                                AuctionHouse.getInstance().getAuctionItemManager().getItem(auctionItem.getKey()).setExpired(true);
                            } else {
                                AuctionHouse.getInstance().getAuctionItemManager().removeItem(auctionItem.getKey());
                            }

                            cleanup();
                            e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
                        }
                        break;
                    case SHIFT_RIGHT:
                        if (!ServerVersion.isServerVersionAtLeast(ServerVersion.V1_11)) return;
                        if (e.player.isOp() || e.player.hasPermission("auctionhouse.admin") || e.player.hasPermission("auctionhouse.inspectshulker")) {
                            ItemStack clicked = e.clickedItem;
                            if (!(clicked.getItemMeta() instanceof BlockStateMeta)) return;

                            BlockStateMeta meta = (BlockStateMeta) clicked.getItemMeta();
                            if (!(meta.getBlockState() instanceof ShulkerBox)) return;
                            cleanup();
                            e.manager.showGUI(e.player, new GUIShulkerInspect(e.clickedItem));
                        }
                        break;
                    case RIGHT:
                        if (e.player.getUniqueId().equals(auctionItem.getOwner()) && !Settings.OWNER_CAN_PURCHASE_OWN_ITEM.getBoolean()) {
                            AuctionHouse.getInstance().getLocale().getMessage("general.cantbuyown").sendPrefixedMessage(e.player);
                            return;
                        }

                        if (auctionItem.getBidStartPrice() <= 0 || !Settings.ALLOW_USAGE_OF_BID_SYSTEM.getBoolean()) {
                            if (!Settings.ALLOW_PURCHASE_OF_SPECIFIC_QUANTITIES.getBoolean()) return;
                            cleanup();
                            e.manager.showGUI(e.player, new GUIConfirmPurchase(this.auctionPlayer, auctionItem, true));
                            return;
                        }

                        if (auctionItem.getBidStartPrice() >= Settings.MIN_AUCTION_START_PRICE.getDouble()) {
                            if (!Settings.ALLOW_USAGE_OF_BUY_NOW_SYSTEM.getBoolean()) return;
                            cleanup();
                            e.manager.showGUI(e.player, new GUIConfirmPurchase(this.auctionPlayer, auctionItem, false));
                        }
                        break;
                }
            });
        }
    }

    private void startTask() {
        taskId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(AuctionHouse.getInstance(), this::draw, 0L, (long) 20 * Settings.TICK_UPDATE_GUI_TIME.getInt());
    }

    private void startTaskAsync() {
        task = Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(AuctionHouse.getInstance(), this::draw, 0L, (long) 20 * Settings.TICK_UPDATE_GUI_TIME.getInt());
    }

    private void killTask() {
        Bukkit.getServer().getScheduler().cancelTask(taskId);
    }

    private void killAsyncTask() {
        task.cancel();
    }

    private void makeMess() {
        if (Settings.USE_ASYNC_GUI_REFRESH.getBoolean()) {
            startTaskAsync();
        } else {
            startTask();
        }
    }

    private void cleanup() {
        if (Settings.USE_ASYNC_GUI_REFRESH.getBoolean()) {
            killAsyncTask();
        } else {
            killTask();
        }
    }
}
