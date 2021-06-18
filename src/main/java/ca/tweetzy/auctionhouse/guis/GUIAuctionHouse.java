package ca.tweetzy.auctionhouse.guis;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.auction.*;
import ca.tweetzy.auctionhouse.guis.transaction.GUITransactionList;
import ca.tweetzy.auctionhouse.helpers.ConfigurationItemHelper;
import ca.tweetzy.auctionhouse.managers.SoundManager;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.compatibility.ServerVersion;
import ca.tweetzy.core.compatibility.XMaterial;
import ca.tweetzy.core.gui.Gui;
import ca.tweetzy.core.gui.events.GuiClickEvent;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.core.utils.items.TItemBuilder;
import ca.tweetzy.core.utils.nms.NBTEditor;
import org.bukkit.Bukkit;
import org.bukkit.block.ShulkerBox;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
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
    private List<AuctionItem> items;

    private int taskId;
    private BukkitTask task;
    private AuctionItemCategory filterCategory = AuctionItemCategory.ALL;
    private AuctionSaleType filterAuctionType = AuctionSaleType.BOTH;
    private String searchPhrase = "";

    public GUIAuctionHouse(AuctionPlayer auctionPlayer) {
        this.auctionPlayer = auctionPlayer;
        setTitle(TextUtils.formatText(Settings.GUI_AUCTION_HOUSE_TITLE.getString()));
        setRows(6);
        setAcceptsItems(false);
        setAllowShiftClick(false);
        draw();

        if (Settings.AUTO_REFRESH_AUCTION_PAGES.getBoolean()) {
            setOnOpen(e -> makeMess());
            setOnClose(e -> cleanup());
        }
    }

    public GUIAuctionHouse(AuctionPlayer auctionPlayer, String phrase) {
        this(auctionPlayer);
        this.searchPhrase = phrase;
    }

    public GUIAuctionHouse(AuctionPlayer auctionPlayer, AuctionItemCategory filterCategory, AuctionSaleType filterAuctionType) {
        this(auctionPlayer);
        this.filterCategory = filterCategory;
        this.filterAuctionType = filterAuctionType;
    }

    public void draw() {
        reset();
        drawFixedButtons();
        drawItems();
    }

    private void drawItems() {
        AuctionHouse.newChain().asyncFirst(() -> {
            List<AuctionItem> filteredItems = new ArrayList<>();
           this.items = AuctionHouse.getInstance().getAuctionItemManager().getAuctionItems().stream().filter(auctionItem -> !auctionItem.isExpired() && auctionItem.getRemainingTime() >= 1).collect(Collectors.toList());

            synchronized (this.items) {
                Iterator<AuctionItem> iterator = this.items.iterator();
                while (iterator.hasNext()) {
                    AuctionItem item = iterator.next();

                    if (!item.isExpired() && item.getRemainingTime() >= 1) {
                        filteredItems.add(item);
                    }

                    // do the filter here now
                    if (this.searchPhrase != null && this.searchPhrase.length() != 0) {
                       filteredItems = filteredItems.stream().filter(auctionItem ->
                               AuctionAPI.getInstance().match(this.searchPhrase, auctionItem.getItemName()) ||
                                       AuctionAPI.getInstance().match(this.searchPhrase, auctionItem.getCategory().getTranslatedType()) ||
                                       AuctionAPI.getInstance().match(this.searchPhrase, Bukkit.getOfflinePlayer(auctionItem.getOwner()).getName())) // TODO add enchantment searching
                               .collect(Collectors.toList());
                    }

                    if (this.filterCategory != AuctionItemCategory.ALL) {
                        filteredItems = filteredItems.stream().filter(auctionItem -> auctionItem.getCategory() == this.filterCategory).collect(Collectors.toList());
                    }

                    if (this.filterAuctionType != AuctionSaleType.BOTH) {
                        filteredItems = filteredItems.stream().filter(auctionItem -> this.filterAuctionType == AuctionSaleType.USED_BIDDING_SYSTEM ? auctionItem.getBidStartPrice() >= Settings.MIN_AUCTION_START_PRICE.getDouble() : auctionItem.getBidStartPrice() <= 0).collect(Collectors.toList());
                    }
                }
            }

            filteredItems = filteredItems.stream().skip((page - 1) * 45L).limit(45).sorted(Comparator.comparingInt(AuctionItem::getRemainingTime).reversed()).collect(Collectors.toList());
            return filteredItems;
        }).asyncLast((data) -> {
            pages = (int) Math.max(1, Math.ceil(this.items.size() / (double) 45L));
            drawPaginationButtons();
            placeItems(data);
        }).execute();
    }

    /*
    ====================== CLICK HANDLES ======================
     */
    private void handleNonBidItem(AuctionItem auctionItem, GuiClickEvent e, boolean buyingQuantity) {
        if (e.player.getUniqueId().equals(auctionItem.getOwner()) && !Settings.OWNER_CAN_PURCHASE_OWN_ITEM.getBoolean()) {
            AuctionHouse.getInstance().getLocale().getMessage("general.cantbuyown").sendPrefixedMessage(e.player);
            return;
        }

        if (!AuctionHouse.getInstance().getEconomy().has(e.player, auctionItem.getBasePrice())) {
            AuctionHouse.getInstance().getLocale().getMessage("general.notenoughmoney").sendPrefixedMessage(e.player);
            return;
        }

        if (buyingQuantity) {
            if (auctionItem.getBidStartPrice() <= 0 || !Settings.ALLOW_USAGE_OF_BID_SYSTEM.getBoolean()) {
                if (!Settings.ALLOW_PURCHASE_OF_SPECIFIC_QUANTITIES.getBoolean()) return;
            }
        }

        cleanup();
        e.manager.showGUI(e.player, new GUIConfirmPurchase(this.auctionPlayer, auctionItem, buyingQuantity));
        AuctionHouse.getInstance().getTransactionManager().addPrePurchase(e.player, auctionItem.getKey());
    }

    private void handleBidItem(AuctionItem auctionItem, GuiClickEvent e, boolean buyNow) {
        if (buyNow) {
            if (auctionItem.getBidStartPrice() >= Settings.MIN_AUCTION_START_PRICE.getDouble()) {
                if (!Settings.ALLOW_USAGE_OF_BUY_NOW_SYSTEM.getBoolean()) return;
                if (auctionItem.getBasePrice() <= -1) {
                    AuctionHouse.getInstance().getLocale().getMessage("general.buynowdisabledonitem").sendPrefixedMessage(e.player);
                    return;
                }

                cleanup();
                e.manager.showGUI(e.player, new GUIConfirmPurchase(this.auctionPlayer, auctionItem, false));
            }
            return;
        }

        if (e.player.getUniqueId().equals(auctionItem.getOwner()) && !Settings.OWNER_CAN_BID_OWN_ITEM.getBoolean()) {
            AuctionHouse.getInstance().getLocale().getMessage("general.cantbidonown").sendPrefixedMessage(e.player);
            return;
        }

        if (Settings.PLAYER_NEEDS_TOTAL_PRICE_TO_BID.getBoolean() && !AuctionHouse.getInstance().getEconomy().has(e.player, auctionItem.getCurrentPrice() + auctionItem.getBidIncPrice())) {
            AuctionHouse.getInstance().getLocale().getMessage("general.notenoughmoney").sendPrefixedMessage(e.player);
            return;
        }

        if (Settings.ASK_FOR_BID_CONFIRMATION.getBoolean()) {
            cleanup();
            e.manager.showGUI(e.player, new GUIConfirmBid(this.auctionPlayer, auctionItem));
        } else {
            auctionItem.setHighestBidder(e.player.getUniqueId());
            auctionItem.setCurrentPrice(auctionItem.getCurrentPrice() + auctionItem.getBidIncPrice());
            if (Settings.SYNC_BASE_PRICE_TO_HIGHEST_PRICE.getBoolean() && auctionItem.getCurrentPrice() > auctionItem.getBasePrice()) {
                auctionItem.setBasePrice(auctionItem.getCurrentPrice());
            }

            if (Settings.INCREASE_TIME_ON_BID.getBoolean()) {
                auctionItem.setRemainingTime(auctionItem.getRemainingTime() + Settings.TIME_TO_INCREASE_BY_ON_BID.getInt());
            }

            if (Settings.REFRESH_GUI_WHEN_BID.getBoolean()) {
                cleanup();
                e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
            }
        }
    }

    private void handleItemRemove(AuctionItem auctionItem, GuiClickEvent e) {
        if (e.player.isOp() || e.player.hasPermission("auctionhouse.admin")) {
            if (Settings.SEND_REMOVED_ITEM_BACK_TO_PLAYER.getBoolean()) {
                AuctionHouse.getInstance().getAuctionItemManager().getItem(auctionItem.getKey()).setExpired(true);
            } else {
                AuctionHouse.getInstance().getAuctionItemManager().removeItem(auctionItem.getKey());
            }

            cleanup();
            e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
        }
    }

    private void handleContainerInspect(GuiClickEvent e) {
        if (!ServerVersion.isServerVersionAtLeast(ServerVersion.V1_11)) return;
        ItemStack clicked = e.clickedItem;

        if (NBTEditor.contains(clicked, "AuctionBundleItem")) {
            cleanup();
            e.manager.showGUI(e.player, new GUIContainerInspect(e.clickedItem));
            return;
        }

        if (e.player.isOp() || e.player.hasPermission("auctionhouse.admin") || e.player.hasPermission("auctionhouse.inspectshulker")) {
            if (!(clicked.getItemMeta() instanceof BlockStateMeta)) return;

            BlockStateMeta meta = (BlockStateMeta) clicked.getItemMeta();
            if (!(meta.getBlockState() instanceof ShulkerBox)) return;
            cleanup();
            e.manager.showGUI(e.player, new GUIContainerInspect(e.clickedItem));
        }
    }

    private void placeItems(List<AuctionItem> data) {
        int slot = 0;
        for (AuctionItem auctionItem : data) {
            setButton(slot++, auctionItem.getDisplayStack(AuctionStackType.MAIN_AUCTION_HOUSE), e -> {
                // Non Type specific actions
                if (e.clickType == ClickType.valueOf(Settings.CLICKS_INSPECT_CONTAINER.getString().toUpperCase())) {
                    handleContainerInspect(e);
                    return;
                }

                if (e.clickType == ClickType.valueOf(Settings.CLICKS_REMOVE_ITEM.getString().toUpperCase())) {
                    handleItemRemove(auctionItem, e);
                    return;
                }

                // Non Biddable Items
                if (auctionItem.getBidStartPrice() <= 0) {
                    if (e.clickType == ClickType.valueOf(Settings.CLICKS_NON_BID_ITEM_PURCHASE.getString().toUpperCase())) {
                        handleNonBidItem(auctionItem, e, false);
                        return;
                    }

                    if (e.clickType == ClickType.valueOf(Settings.CLICKS_NON_BID_ITEM_QTY_PURCHASE.getString().toUpperCase())) {
                        handleNonBidItem(auctionItem, e, true);
                        return;
                    }
                    return;
                }

                // Biddable Items
                if (e.clickType == ClickType.valueOf(Settings.CLICKS_BID_ITEM_PLACE_BID.getString().toUpperCase())) {
                    handleBidItem(auctionItem, e, false);
                    return;
                }

                if (e.clickType == ClickType.valueOf(Settings.CLICKS_BID_ITEM_BUY_NOW.getString().toUpperCase())) {
                    handleBidItem(auctionItem, e, true);
                }
            });
        }
    }

    /*
    ====================== FIXED BUTTONS ======================
     */

    private void drawPaginationButtons() {
        setPrevPage(5, 3, new TItemBuilder(Objects.requireNonNull(Settings.GUI_BACK_BTN_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_BACK_BTN_NAME.getString()).setLore(Settings.GUI_BACK_BTN_LORE.getStringList()).toItemStack());
        setNextPage(5, 5, new TItemBuilder(Objects.requireNonNull(Settings.GUI_NEXT_BTN_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_NEXT_BTN_NAME.getString()).setLore(Settings.GUI_NEXT_BTN_LORE.getStringList()).toItemStack());
        setOnPage(e -> {
            draw();
            SoundManager.getInstance().playSound(this.auctionPlayer.getPlayer(), Settings.SOUNDS_NAVIGATE_GUI_PAGES.getString(), 1.0F, 1.0F);
        });
    }

    private void drawFixedButtons() {
        setButton(5, 0, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_AUCTION_HOUSE_ITEMS_YOUR_AUCTIONS_ITEM.getString(), Settings.GUI_AUCTION_HOUSE_ITEMS_YOUR_AUCTIONS_NAME.getString(), Settings.GUI_AUCTION_HOUSE_ITEMS_YOUR_AUCTIONS_LORE.getStringList(), new HashMap<String, Object>() {{
            put("%active_player_auctions%", auctionPlayer.getItems(false).size());
            put("%player_balance%", AuctionAPI.getInstance().formatNumber(AuctionHouse.getInstance().getEconomy().getBalance(auctionPlayer.getPlayer())));
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

        drawFilterButton();

        setButton(5, 6, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_AUCTION_HOUSE_ITEMS_TRANSACTIONS_ITEM.getString(), Settings.GUI_AUCTION_HOUSE_ITEMS_TRANSACTIONS_NAME.getString(), Settings.GUI_AUCTION_HOUSE_ITEMS_TRANSACTIONS_LORE.getStringList(), new HashMap<String, Object>() {{
            put("%total_items_bought%", AuctionHouse.getInstance().getTransactionManager().getTransactions().stream().filter(transaction -> transaction.getBuyer().equals(auctionPlayer.getPlayer().getUniqueId())).count());
            put("%total_items_sold%", AuctionHouse.getInstance().getTransactionManager().getTransactions().stream().filter(transaction -> transaction.getSeller().equals(auctionPlayer.getPlayer().getUniqueId())).count());
        }}), e -> e.manager.showGUI(e.player, new GUITransactionList(this.auctionPlayer)));

        setButton(5, 7, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_AUCTION_HOUSE_ITEMS_HOW_TO_SELL_ITEM.getString(), Settings.GUI_AUCTION_HOUSE_ITEMS_HOW_TO_SELL_NAME.getString(), Settings.GUI_AUCTION_HOUSE_ITEMS_HOW_TO_SELL_LORE.getStringList(), null), null);
        setButton(5, 8, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_AUCTION_HOUSE_ITEMS_GUIDE_ITEM.getString(), Settings.GUI_AUCTION_HOUSE_ITEMS_GUIDE_NAME.getString(), Settings.GUI_AUCTION_HOUSE_ITEMS_GUIDE_LORE.getStringList(), null), null);

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
    }

    private void drawFilterButton() {
        setButton(5, 2, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_AUCTION_HOUSE_ITEMS_FILTER_ITEM.getString(), Settings.GUI_AUCTION_HOUSE_ITEMS_FILTER_NAME.getString(), Settings.GUI_AUCTION_HOUSE_ITEMS_FILTER_LORE.getStringList(), new HashMap<String, Object>() {{
            put("%filter_category%", filterCategory.getTranslatedType());
            put("%filter_auction_type%", filterAuctionType.getTranslatedType());
        }}), e -> {
            switch (e.clickType) {
                case LEFT:
                    this.filterCategory = this.filterCategory.next();
                    draw();
                    break;
                case RIGHT:
                    this.filterAuctionType = this.filterAuctionType.next();
                    draw();
                    break;
            }
        });
    }

    private void updateFilter() {
        setItems(0, 44, XMaterial.AIR.parseItem());
        drawItems();
        drawFilterButton();
    }

    /*
    ====================== AUTO REFRESH ======================
     */
    private void makeMess() {
        if (Settings.USE_ASYNC_GUI_REFRESH.getBoolean()) {
            task = Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(AuctionHouse.getInstance(), this::drawItems, 0L, (long) 20 * Settings.TICK_UPDATE_GUI_TIME.getInt());
        } else {
            taskId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(AuctionHouse.getInstance(), this::drawItems, 0L, (long) 20 * Settings.TICK_UPDATE_GUI_TIME.getInt());
        }
    }

    private void cleanup() {
        if (Settings.USE_ASYNC_GUI_REFRESH.getBoolean()) {
            task.cancel();
        } else {
            Bukkit.getServer().getScheduler().cancelTask(taskId);
        }
    }

}
