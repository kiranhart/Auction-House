package ca.tweetzy.auctionhouse.guis;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.auction.*;
import ca.tweetzy.auctionhouse.guis.confirmation.GUIConfirmBid;
import ca.tweetzy.auctionhouse.guis.confirmation.GUIConfirmPurchase;
import ca.tweetzy.auctionhouse.guis.filter.GUIFilterSelection;
import ca.tweetzy.auctionhouse.guis.transaction.GUITransactionList;
import ca.tweetzy.auctionhouse.helpers.ConfigurationItemHelper;
import ca.tweetzy.auctionhouse.managers.SoundManager;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.commands.AbstractCommand;
import ca.tweetzy.core.compatibility.ServerVersion;
import ca.tweetzy.core.compatibility.XMaterial;
import ca.tweetzy.core.gui.Gui;
import ca.tweetzy.core.gui.events.GuiClickEvent;
import ca.tweetzy.core.hooks.EconomyManager;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.core.utils.TimeUtils;
import ca.tweetzy.core.utils.items.TItemBuilder;
import ca.tweetzy.core.utils.nms.NBTEditor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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

public class GUIAuctionHouse extends Gui {

    final AuctionPlayer auctionPlayer;
    private List<AuctionItem> items;

    private BukkitTask task;
    private String searchPhrase = "";

    public GUIAuctionHouse(AuctionPlayer auctionPlayer) {
        this.auctionPlayer = auctionPlayer;
        setTitle(TextUtils.formatText(Settings.GUI_AUCTION_HOUSE_TITLE.getString()));
        setRows(6);
        setAcceptsItems(false);
        setAllowShiftClick(false);
        draw();

        setOnOpen(open -> {
            if (AuctionHouse.getInstance().getAuctionBanManager().checkAndHandleBan(open.player)) {
                open.gui.exit();
                return;
            }

            if (Settings.AUTO_REFRESH_AUCTION_PAGES.getBoolean()) {
                makeMess();
            }
        });

        if (Settings.AUTO_REFRESH_AUCTION_PAGES.getBoolean()) {
            setOnClose(e -> cleanup());
        }
    }

    public GUIAuctionHouse(AuctionPlayer auctionPlayer, String phrase) {
        this(auctionPlayer);
        this.searchPhrase = phrase;
    }

    public void draw() {
        reset();
        drawFixedButtons();
        drawItems();
    }

    private void drawItems() {
        AuctionHouse.newChain().asyncFirst(() -> {
            this.items = new ArrayList<>();

            for (Map.Entry<UUID, AuctionItem> entry : AuctionHouse.getInstance().getAuctionItemManager().getAuctionItems().entrySet()) {
                AuctionItem auctionItem = entry.getValue();
                if (!auctionItem.isExpired() && auctionItem.getRemainingTime() >= 1 && !AuctionHouse.getInstance().getAuctionItemManager().getGarbageBin().containsKey(auctionItem.getKey())) {
                    this.items.add(auctionItem);
                }
            }

            if (this.searchPhrase != null && this.searchPhrase.length() != 0) {
                this.items = this.items.stream().filter(item -> checkSearchCriteria(this.searchPhrase, item)).collect(Collectors.toList());
            }

            if (this.auctionPlayer.getSelectedFilter() != AuctionItemCategory.ALL && this.auctionPlayer.getSelectedFilter() != AuctionItemCategory.SEARCH && this.auctionPlayer.getSelectedFilter() != AuctionItemCategory.SELF) {
                this.items = this.items.stream().filter(item -> checkFilterCriteria(item, this.auctionPlayer.getSelectedFilter())).collect(Collectors.toList());
            } else if (this.auctionPlayer.getSelectedFilter() == AuctionItemCategory.SELF) {
                this.items = this.items.stream().filter(item -> item.getOwner().equals(this.auctionPlayer.getPlayer().getUniqueId())).collect(Collectors.toList());
            } else if (this.auctionPlayer.getSelectedFilter() == AuctionItemCategory.SEARCH && this.auctionPlayer.getCurrentSearchPhrase().length() != 0) {
                this.items = this.items.stream().filter(item -> checkSearchCriteria(this.auctionPlayer.getCurrentSearchPhrase(), item)).collect(Collectors.toList());
            }

            if (this.auctionPlayer.getSelectedSaleType() == AuctionSaleType.USED_BIDDING_SYSTEM) {
                this.items = this.items.stream().filter(item -> item.getBidStartPrice() >= 1 && item.getBidIncPrice() >= 1).collect(Collectors.toList());
            }

            if (this.auctionPlayer.getSelectedSaleType() == AuctionSaleType.WITHOUT_BIDDING_SYSTEM) {
                this.items = this.items.stream().filter(item -> item.getBidStartPrice() <= 0 && item.getBidIncPrice() <= 0).collect(Collectors.toList());
            }

            if (this.auctionPlayer.getAuctionSortType() == AuctionSortType.PRICE) {
                this.items = this.items.stream().sorted(Comparator.comparingDouble(AuctionItem::getCurrentPrice).reversed()).collect(Collectors.toList());
            }

            if (this.auctionPlayer.getAuctionSortType() == AuctionSortType.RECENT) {
                this.items = this.items.stream().sorted(Comparator.comparingDouble(AuctionItem::getRemainingTime).reversed()).collect(Collectors.toList());
            }

            return this.items.stream().skip((page - 1) * 45L).limit(45L).collect(Collectors.toList());
        }).asyncLast((data) -> {
            pages = (int) Math.max(1, Math.ceil(this.items.size() / (double) 45L));
            drawPaginationButtons();
            placeItems(data);
        }).execute();
    }

    private boolean checkFilterCriteria(AuctionItem auctionItem, AuctionItemCategory category) {
        return auctionItem.getCategory() == category ||
                AuctionHouse.getInstance().getFilterManager().getFilterWhitelist(category).stream().anyMatch(item -> item.isSimilar(AuctionAPI.getInstance().deserializeItem(auctionItem.getRawItem())));
    }

    private boolean checkSearchCriteria(String phrase, AuctionItem item) {
        ItemStack stack = AuctionAPI.getInstance().deserializeItem(item.getRawItem());
        return AuctionAPI.getInstance().match(phrase, AuctionAPI.getInstance().getItemName(stack)) ||
                AuctionAPI.getInstance().match(phrase, item.getCategory().getTranslatedType()) ||
                AuctionAPI.getInstance().match(phrase, stack.getType().name()) ||
                AuctionAPI.getInstance().match(phrase, Bukkit.getOfflinePlayer(item.getOwner()).getName()) ||
                AuctionAPI.getInstance().match(phrase, AuctionAPI.getInstance().getItemLore(stack)) ||
                AuctionAPI.getInstance().match(phrase, AuctionAPI.getInstance().getItemEnchantments(stack));
    }

    /*
    ====================== CLICK HANDLES ======================
     */
    private void handleNonBidItem(AuctionItem auctionItem, GuiClickEvent e, boolean buyingQuantity) {
        if (e.player.getUniqueId().equals(auctionItem.getOwner()) && !Settings.OWNER_CAN_PURCHASE_OWN_ITEM.getBoolean()) {
            AuctionHouse.getInstance().getLocale().getMessage("general.cantbuyown").sendPrefixedMessage(e.player);
            return;
        }

        if (!EconomyManager.hasBalance(e.player, auctionItem.getBasePrice())) {
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
                AuctionHouse.getInstance().getTransactionManager().addPrePurchase(e.player, auctionItem.getKey());
            }
            return;
        }

        if (e.player.getUniqueId().equals(auctionItem.getOwner()) && !Settings.OWNER_CAN_BID_OWN_ITEM.getBoolean()) {
            AuctionHouse.getInstance().getLocale().getMessage("general.cantbidonown").sendPrefixedMessage(e.player);
            return;
        }

        if (Settings.PLAYER_NEEDS_TOTAL_PRICE_TO_BID.getBoolean() && !EconomyManager.hasBalance(e.player, auctionItem.getCurrentPrice() + auctionItem.getBidIncPrice())) {
            AuctionHouse.getInstance().getLocale().getMessage("general.notenoughmoney").sendPrefixedMessage(e.player);
            return;
        }

        if (Settings.ASK_FOR_BID_CONFIRMATION.getBoolean()) {
            cleanup();
            e.manager.showGUI(e.player, new GUIConfirmBid(this.auctionPlayer, auctionItem));
        } else {
            ItemStack itemStack = AuctionAPI.getInstance().deserializeItem(auctionItem.getRawItem());

            OfflinePlayer oldBidder = Bukkit.getOfflinePlayer(auctionItem.getHighestBidder());
            OfflinePlayer owner = Bukkit.getOfflinePlayer(auctionItem.getOwner());


            auctionItem.setHighestBidder(e.player.getUniqueId());
            auctionItem.setCurrentPrice(auctionItem.getCurrentPrice() + auctionItem.getBidIncPrice());
            if (auctionItem.getBasePrice() != -1 && Settings.SYNC_BASE_PRICE_TO_HIGHEST_PRICE.getBoolean() && auctionItem.getCurrentPrice() > auctionItem.getBasePrice()) {
                auctionItem.setBasePrice(auctionItem.getCurrentPrice());
            }

            if (Settings.INCREASE_TIME_ON_BID.getBoolean()) {
                auctionItem.setRemainingTime(auctionItem.getRemainingTime() + Settings.TIME_TO_INCREASE_BY_ON_BID.getInt());
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

            if (Settings.REFRESH_GUI_WHEN_BID.getBoolean()) {
                cleanup();
                e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
            }
        }
    }

    private void handleItemRemove(AuctionItem auctionItem, GuiClickEvent e) {
        if (e.player.isOp() || e.player.hasPermission("auctionhouse.admin")) {
            cleanup();
            e.manager.showGUI(e.player, new GUIAdminItem(this.auctionPlayer, auctionItem));
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
            put("%player_balance%", AuctionAPI.getInstance().formatNumber(EconomyManager.getBalance(auctionPlayer.getPlayer())));
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
            put("%total_items_bought%", AuctionHouse.getInstance().getTransactionManager().getTotalItemsBought(auctionPlayer.getPlayer().getUniqueId()));
            put("%total_items_sold%", AuctionHouse.getInstance().getTransactionManager().getTotalItemsSold(auctionPlayer.getPlayer().getUniqueId()));
        }}), e -> e.manager.showGUI(e.player, new GUITransactionList(this.auctionPlayer)));

        if (Settings.REPLACE_HOW_TO_SELL_WITH_LIST_BUTTON.getBoolean()) {
            setButton(5, 7, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_AUCTION_HOUSE_ITEMS_LIST_ITEM_ITEM.getString(), Settings.GUI_AUCTION_HOUSE_ITEMS_LIST_ITEM_NAME.getString(), Settings.GUI_AUCTION_HOUSE_ITEMS_LIST_ITEM_LORE.getStringList(), null), e -> {
                // using this will ignore the "SELL_MENU_REQUIRES_USER_TO_HOLD_ITEM" setting
                AuctionHouse.getInstance().getGuiManager().showGUI(e.player, new GUISellItem(auctionPlayer, XMaterial.AIR.parseItem()));
                AuctionHouse.getInstance().getAuctionPlayerManager().addItemToSellHolding(e.player.getUniqueId(), XMaterial.AIR.parseItem());
            });
        } else {
            setButton(5, 7, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_AUCTION_HOUSE_ITEMS_HOW_TO_SELL_ITEM.getString(), Settings.GUI_AUCTION_HOUSE_ITEMS_HOW_TO_SELL_NAME.getString(), Settings.GUI_AUCTION_HOUSE_ITEMS_HOW_TO_SELL_LORE.getStringList(), null), null);
        }

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

        if (Settings.USE_SEPARATE_FILTER_MENU.getBoolean()) {
            String materialToBeUsed = Settings.GUI_AUCTION_HOUSE_ITEMS_FILTER_MENU_ITEM.getString();
            switch (auctionPlayer.getSelectedFilter()) {
                case ALL:
                    materialToBeUsed = Settings.GUI_FILTER_ITEMS_ALL_ITEM.getString();
                    break;
                case ARMOR:
                    materialToBeUsed = Settings.GUI_FILTER_ITEMS_ARMOR_ITEM.getString();
                    break;
                case BLOCKS:
                    materialToBeUsed = Settings.GUI_FILTER_ITEMS_BLOCKS_ITEM.getString();
                    break;
                case TOOLS:
                    materialToBeUsed = Settings.GUI_FILTER_ITEMS_TOOLS_ITEM.getString();
                    break;
                case WEAPONS:
                    materialToBeUsed = Settings.GUI_FILTER_ITEMS_WEAPONS_ITEM.getString();
                    break;
                case SPAWNERS:
                    materialToBeUsed = Settings.GUI_FILTER_ITEMS_SPAWNERS_ITEM.getString();
                    break;
                case ENCHANTS:
                    materialToBeUsed = Settings.GUI_FILTER_ITEMS_ENCHANTS_ITEM.getString();
                    break;
                case MISC:
                    materialToBeUsed = Settings.GUI_FILTER_ITEMS_MISC_ITEM.getString();
                    break;
                case SEARCH:
                    materialToBeUsed = Settings.GUI_FILTER_ITEMS_SEARCH_ITEM.getString();
                    break;
                case SELF:
                    materialToBeUsed = "PLAYER_HEAD";
                    break;
            }

            HashMap<String, Object> replacements = new HashMap<String, Object>() {{
                put("%filter_category%", auctionPlayer.getSelectedFilter().getTranslatedType());
                put("%filter_auction_type%", auctionPlayer.getSelectedSaleType().getTranslatedType());
                put("%filter_sort_order%", auctionPlayer.getAuctionSortType().getTranslatedType());
            }};

            ItemStack item = materialToBeUsed.equalsIgnoreCase("PLAYER_HEAD") ? ConfigurationItemHelper.createConfigurationItem(AuctionAPI.getInstance().getPlayerHead(this.auctionPlayer.getPlayer().getName()), Settings.GUI_AUCTION_HOUSE_ITEMS_FILTER_MENU_NAME.getString(), Settings.GUI_AUCTION_HOUSE_ITEMS_FILTER_MENU_LORE.getStringList(), replacements) : ConfigurationItemHelper.createConfigurationItem(materialToBeUsed, Settings.GUI_AUCTION_HOUSE_ITEMS_FILTER_MENU_NAME.getString(), Settings.GUI_AUCTION_HOUSE_ITEMS_FILTER_MENU_LORE.getStringList(), replacements);

            setButton(5, 2, item, e -> {
                switch (e.clickType) {
                    case LEFT:
                        e.manager.showGUI(e.player, new GUIFilterSelection(this.auctionPlayer));
                        break;
                    case MIDDLE:
                        this.auctionPlayer.resetFilter();
                        draw();
                        break;
                    case RIGHT:
                        this.auctionPlayer.setSelectedSaleType(this.auctionPlayer.getSelectedSaleType().next());
                        draw();
                        break;
                    case SHIFT_RIGHT:
                        this.auctionPlayer.setAuctionSortType(this.auctionPlayer.getAuctionSortType().next());
                        draw();
                        break;
                }

            });
            return;
        }

        setButton(5, 2, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_AUCTION_HOUSE_ITEMS_FILTER_ITEM.getString(), Settings.GUI_AUCTION_HOUSE_ITEMS_FILTER_NAME.getString(), Settings.GUI_AUCTION_HOUSE_ITEMS_FILTER_LORE.getStringList(), new HashMap<String, Object>() {{
            put("%filter_category%", auctionPlayer.getSelectedFilter().getTranslatedType());
            put("%filter_auction_type%", auctionPlayer.getSelectedSaleType().getTranslatedType());
            put("%filter_sort_order%", auctionPlayer.getAuctionSortType().getTranslatedType());
        }}), e -> {
            switch (e.clickType) {
                case MIDDLE:
                    this.auctionPlayer.resetFilter();
                    draw();
                    break;
                case LEFT:
                    this.auctionPlayer.setSelectedFilter(this.auctionPlayer.getSelectedFilter().next());
                    draw();
                    break;
                case RIGHT:
                    this.auctionPlayer.setSelectedSaleType(this.auctionPlayer.getSelectedSaleType().next());
                    draw();
                    break;
                case SHIFT_RIGHT:
                    this.auctionPlayer.setAuctionSortType(this.auctionPlayer.getAuctionSortType().next());
                    draw();
                    break;
            }
        });
    }

    /*
    ====================== AUTO REFRESH ======================
     */
    private void makeMess() {
        task = Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(AuctionHouse.getInstance(), this::drawItems, 0L, (long) 20 * Settings.TICK_UPDATE_GUI_TIME.getInt());
    }

    private void cleanup() {
        task.cancel();
    }

}
