package ca.tweetzy.auctionhouse.guis;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.auction.AuctionedItem;
import ca.tweetzy.auctionhouse.auction.enums.AuctionStackType;
import ca.tweetzy.auctionhouse.guis.confirmation.GUIConfirmCancel;
import ca.tweetzy.auctionhouse.helpers.ConfigurationItemHelper;
import ca.tweetzy.auctionhouse.managers.SoundManager;
import ca.tweetzy.auctionhouse.settings.Settings;
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
public class GUIActiveAuctions extends AbstractPlaceholderGui {

	private final AuctionPlayer auctionPlayer;
	private BukkitTask task;

	private List<AuctionedItem> items;

	public GUIActiveAuctions(AuctionPlayer auctionPlayer) {
		super(auctionPlayer);
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

			// per world check
			if (Settings.PER_WORLD_ITEMS.getBoolean()) {
				this.items = this.items.stream().filter(item -> item.getListedWorld() == null || this.auctionPlayer.getPlayer().getWorld().getName().equals(item.getListedWorld())).collect(Collectors.toList());
			}

			return this.items.stream().sorted(Comparator.comparingLong(AuctionedItem::getExpiresAt).reversed()).skip((page - 1) * 45L).limit(45).collect(Collectors.toList());
		}).asyncLast((data) -> {
			pages = (int) Math.max(1, Math.ceil(this.items.size() / (double) 45L));
			drawPaginationButtons();

			int slot = 0;
			for (AuctionedItem item : data) {
				setButton(slot++, item.getDisplayStack(AuctionStackType.ACTIVE_AUCTIONS_LIST), e -> {
					switch (e.clickType) {
						case LEFT:
							if (((item.getBidStartingPrice() > 0 || item.getBidIncrementPrice() > 0) && Settings.ASK_FOR_CANCEL_CONFIRM_ON_BID_ITEMS.getBoolean()) || Settings.ASK_FOR_CANCEL_CONFIRM_ON_NON_BID_ITEMS.getBoolean()) {
								if (item.getHighestBidder().equals(e.player.getUniqueId())) {
									item.setExpired(true);
									item.setExpiresAt(System.currentTimeMillis());
									draw();
									return;
								}
								cleanup();
								e.manager.showGUI(e.player, new GUIConfirmCancel(this.auctionPlayer, item));
								return;
							}

							item.setExpired(true);
							draw();
							break;
						case RIGHT:
							if (Settings.ALLOW_PLAYERS_TO_ACCEPT_BID.getBoolean() && item.getBidStartingPrice() != 0 && !item.getHighestBidder().equals(e.player.getUniqueId())) {
								item.setExpiresAt(System.currentTimeMillis());
								draw();
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
		task = Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(AuctionHouse.getInstance(), this::drawItems, 0L, (long) 20 * Settings.TICK_UPDATE_GUI_TIME.getInt());
	}

	private void cleanup() {
		if (task != null) task.cancel();
	}
}
