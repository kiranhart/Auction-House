package ca.tweetzy.auctionhouse.guis;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
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
import org.bukkit.scheduler.BukkitTask;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: March 14 2021
 * Time Created: 10:33 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUIActiveAuctions extends Gui {

    final AuctionPlayer auctionPlayer;

    private BukkitTask task;
    private int taskId;

    List<AuctionItem> items;

    public GUIActiveAuctions(AuctionPlayer auctionPlayer) {
        this.auctionPlayer = auctionPlayer;
        setTitle(TextUtils.formatText(Settings.GUI_ACTIVE_AUCTIONS_TITLE.getString()));
        setRows(6);
        setAcceptsItems(false);
        draw();

        if (Settings.AUTO_REFRESH_AUCTION_PAGES.getBoolean()) {
            setOnOpen(e -> makeMess());
            setOnClose(e -> cleanup());
        }
    }

    private void draw() {
        reset();
        drawFixedButtons();
        drawItems();
    }

    private void drawItems() {
        AuctionHouse.newChain().asyncFirst(() -> {
            this.items = this.auctionPlayer.getItems(false);
            return this.items.stream().sorted(Comparator.comparingInt(AuctionItem::getRemainingTime).reversed()).skip((page - 1) * 45L).limit(45).collect(Collectors.toList());
        }).asyncLast((data) -> {
            pages = (int) Math.max(1, Math.ceil(this.items.size() / (double) 45L));
            drawPaginationButtons();

            int slot = 0;
            for (AuctionItem item : data) {
                setButton(slot++, item.getDisplayStack(AuctionStackType.ACTIVE_AUCTIONS_LIST), e -> {
                    switch (e.clickType) {
                        case LEFT:
                            if (((item.getBidStartPrice() > 0 || item.getBidIncPrice() > 0) && Settings.ASK_FOR_CANCEL_CONFIRM_ON_BID_ITEMS.getBoolean()) || Settings.ASK_FOR_CANCEL_CONFIRM_ON_NON_BID_ITEMS.getBoolean()) {
                                cleanup();
                                e.manager.showGUI(e.player, new GUIConfirmCancel(this.auctionPlayer, item));
                                return;
                            }

                            item.setExpired(true);
                            draw();
                            break;
                        case RIGHT:
                            if (Settings.ALLOW_PLAYERS_TO_ACCEPT_BID.getBoolean() && item.getBidStartPrice() != 0) {
                                AuctionHouse.newChain().async(() -> AuctionAPI.getInstance().endAuction(item)).sync(this::draw).execute();
                            }
                            break;
                    }
                });
            }
        }).execute();
    }

    private void drawFixedButtons() {
        setButton(5, 0, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_CLOSE_BTN_ITEM.getString(), Settings.GUI_CLOSE_BTN_NAME.getString(), Settings.GUI_CLOSE_BTN_LORE.getStringList(), null), e -> {
            cleanup();
            e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
        });

        setButton(5, 4, new TItemBuilder(Objects.requireNonNull(Settings.GUI_REFRESH_BTN_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_REFRESH_BTN_NAME.getString()).setLore(Settings.GUI_REFRESH_BTN_LORE.getStringList()).toItemStack(), e -> e.manager.showGUI(e.player, new GUIActiveAuctions(this.auctionPlayer)));

        setButton(5, 1, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_ACTIVE_AUCTIONS_ITEM.getString(), Settings.GUI_ACTIVE_AUCTIONS_NAME.getString(), Settings.GUI_ACTIVE_AUCTIONS_LORE.getStringList(), null), e -> {
            this.auctionPlayer.getItems(false).forEach(item -> item.setExpired(true));
            draw();
        });
    }

    private void drawPaginationButtons() {
        setPrevPage(5, 3, new TItemBuilder(Objects.requireNonNull(Settings.GUI_BACK_BTN_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_BACK_BTN_NAME.getString()).setLore(Settings.GUI_BACK_BTN_LORE.getStringList()).toItemStack());
        setNextPage(5, 5, new TItemBuilder(Objects.requireNonNull(Settings.GUI_NEXT_BTN_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_NEXT_BTN_NAME.getString()).setLore(Settings.GUI_NEXT_BTN_LORE.getStringList()).toItemStack());
        setOnPage(e -> {
            draw();
            SoundManager.getInstance().playSound(this.auctionPlayer.getPlayer(), Settings.SOUNDS_NAVIGATE_GUI_PAGES.getString(), 1.0F, 1.0F);
        });
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
