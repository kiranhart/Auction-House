package ca.tweetzy.auctionhouse.guis.confirmation;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.api.events.AuctionBidEvent;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.auction.AuctionedItem;
import ca.tweetzy.auctionhouse.guis.AbstractPlaceholderGui;
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
import org.bukkit.scheduler.BukkitTask;

import java.util.Objects;

/**
 * The current file has been created by Kiran Hart
 * Date Created: March 17 2021
 * Time Created: 11:18 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUIConfirmBid extends AbstractPlaceholderGui {

	private final AuctionPlayer auctionPlayer;
	private final AuctionedItem auctionItem;
	private final double bidAmount;
	private BukkitTask bukkitTask;

	public GUIConfirmBid(AuctionPlayer auctionPlayer, AuctionedItem auctionItem) {
		this(auctionPlayer, auctionItem, -1);
	}

	public GUIConfirmBid(AuctionPlayer auctionPlayer, AuctionedItem auctionItem, double bidAmount) {
		super(auctionPlayer);
		this.auctionPlayer = auctionPlayer;
		this.auctionItem = auctionItem;
		this.bidAmount = bidAmount;
		setTitle(TextUtils.formatText(Settings.GUI_CONFIRM_BID_TITLE.getString()));
		setAcceptsItems(false);
		setRows(1);
		draw();

		setOnOpen(open -> {
			if (Settings.USE_LIVE_BID_NUMBER_IN_CONFIRM_GUI.getBoolean()) {
				this.bukkitTask = Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(AuctionHouse.getInstance(), this::placeAuctionItem, 0L, (long) 20 * Settings.LIVE_BID_NUMBER_IN_CONFIRM_GUI_RATE.getInt());
			}
		});

		setOnClose(close -> cleanup());
	}

	private void placeAuctionItem() {
		setItem(0, 4, this.auctionItem.getBidStack());
	}

	private void cleanup() {
		if (bukkitTask != null) {
			bukkitTask.cancel();
		}
	}

	private void draw() {
		setItems(0, 3, new TItemBuilder(Objects.requireNonNull(Settings.GUI_CONFIRM_BID_YES_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_CONFIRM_BID_YES_NAME.getString()).setLore(Settings.GUI_CONFIRM_BID_YES_LORE.getStringList()).toItemStack());
		placeAuctionItem();
		setItems(5, 8, new TItemBuilder(Objects.requireNonNull(Settings.GUI_CONFIRM_BID_NO_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_CONFIRM_BID_NO_NAME.getString()).setLore(Settings.GUI_CONFIRM_BID_NO_LORE.getStringList()).toItemStack());

		setActionForRange(5, 8, ClickType.LEFT, e -> {
			cleanup();
			e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
		});
		setActionForRange(0, 3, ClickType.LEFT, e -> {
			// Re-select the item to ensure that it's available
			AuctionedItem located = AuctionHouse.getInstance().getAuctionItemManager().getItem(this.auctionItem.getId());
			if (located == null) {
				cleanup();
				e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
				return;
			}

			double toIncrementBy = this.bidAmount == -1 ? auctionItem.getBidIncrementPrice() : this.bidAmount;

			double newBiddingAmount = 0;
			if (Settings.USE_REALISTIC_BIDDING.getBoolean()) {
				if (toIncrementBy > this.auctionItem.getCurrentPrice()) {
					newBiddingAmount = toIncrementBy;
				} else {
					if (Settings.BID_MUST_BE_HIGHER_THAN_PREVIOUS.getBoolean()) {
						e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
						AuctionHouse.getInstance().getLocale().getMessage("pricing.bidmusthigherthanprevious").processPlaceholder("current_bid", AuctionAPI.getInstance().formatNumber(auctionItem.getCurrentPrice())).sendPrefixedMessage(e.player);
						return;
					}

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

			AuctionBidEvent auctionBidEvent = new AuctionBidEvent(e.player, auctionItem, newBiddingAmount);
			Bukkit.getServer().getPluginManager().callEvent(auctionBidEvent);
			if (auctionBidEvent.isCancelled()) return;

			auctionItem.setHighestBidder(e.player.getUniqueId());
			auctionItem.setHighestBidderName(e.player.getName());
			auctionItem.setCurrentPrice(Settings.ROUND_ALL_PRICES.getBoolean() ? Math.round(newBiddingAmount) : newBiddingAmount);
			if (auctionItem.getBasePrice() != -1 && Settings.SYNC_BASE_PRICE_TO_HIGHEST_PRICE.getBoolean() && Settings.ALLOW_USAGE_OF_BUY_NOW_SYSTEM.getBoolean() && auctionItem.getCurrentPrice() > auctionItem.getBasePrice()) {
				auctionItem.setBasePrice(Settings.ROUND_ALL_PRICES.getBoolean() ? Math.round(auctionItem.getCurrentPrice()) : auctionItem.getCurrentPrice());
			}

			if (Settings.INCREASE_TIME_ON_BID.getBoolean()) {
				auctionItem.setExpiresAt(auctionItem.getExpiresAt() + 1000L * Settings.TIME_TO_INCREASE_BY_ON_BID.getInt());
			}

			if (Settings.BROADCAST_AUCTION_BID.getBoolean()) {
				Bukkit.getOnlinePlayers().forEach(player -> AuctionHouse.getInstance().getLocale().getMessage("auction.broadcast.bid")
						.processPlaceholder("player", e.player.getName())
						.processPlaceholder("player_displayname", AuctionAPI.getInstance().getDisplayName(e.player))
						.processPlaceholder("amount", AuctionAPI.getInstance().formatNumber(auctionItem.getCurrentPrice()))
						.processPlaceholder("item", AuctionAPI.getInstance().getItemName(itemStack))
						.sendPrefixedMessage(player));
			}

			if (oldBidder.isOnline()) {
				AuctionHouse.getInstance().getLocale().getMessage("auction.outbid")
						.processPlaceholder("player", e.player.getName())
						.processPlaceholder("player_displayname", AuctionAPI.getInstance().getDisplayName(e.player))
						.processPlaceholder("item", AuctionAPI.getInstance().getItemName(itemStack))
						.sendPrefixedMessage(oldBidder.getPlayer());
			}

			if (owner.isOnline()) {
				AuctionHouse.getInstance().getLocale().getMessage("auction.placedbid")
						.processPlaceholder("player", e.player.getName())
						.processPlaceholder("player_displayname", AuctionAPI.getInstance().getDisplayName(e.player))
						.processPlaceholder("amount", AuctionAPI.getInstance().formatNumber(auctionItem.getCurrentPrice()))
						.processPlaceholder("item", AuctionAPI.getInstance().getItemName(itemStack))
						.sendPrefixedMessage(owner.getPlayer());
			}

			cleanup();
			e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
		});
	}
}
