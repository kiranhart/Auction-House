package ca.tweetzy.auctionhouse.guis.core;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.api.ban.BanType;
import ca.tweetzy.auctionhouse.auction.AuctionPayment;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.auction.AuctionedItem;
import ca.tweetzy.auctionhouse.auction.ListingType;
import ca.tweetzy.auctionhouse.auction.enums.*;
import ca.tweetzy.auctionhouse.events.AuctionBidEvent;
import ca.tweetzy.auctionhouse.guis.AuctionUpdatingPagedGUI;
import ca.tweetzy.auctionhouse.guis.admin.GUIAdminItem;
import ca.tweetzy.auctionhouse.guis.confirmation.GUIConfirmBid;
import ca.tweetzy.auctionhouse.guis.confirmation.GUIConfirmPurchase;
import ca.tweetzy.auctionhouse.guis.core.bid.GUIBid;
import ca.tweetzy.auctionhouse.guis.filter.GUIFilterSelection;
import ca.tweetzy.auctionhouse.guis.sell.GUISellListingType;
import ca.tweetzy.auctionhouse.guis.sell.GUISellPlaceItem;
import ca.tweetzy.auctionhouse.guis.transaction.GUITransactionList;
import ca.tweetzy.auctionhouse.guis.transaction.GUITransactionType;
import ca.tweetzy.auctionhouse.helpers.BundleUtil;
import ca.tweetzy.auctionhouse.helpers.input.TitleInput;
import ca.tweetzy.auctionhouse.hooks.FloodGateHook;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.gui.events.GuiClickEvent;
import ca.tweetzy.core.utils.NumberUtils;
import ca.tweetzy.flight.comp.enums.ServerVersion;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.flight.utils.Replacer;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public final class GUIAuctionHouse extends AuctionUpdatingPagedGUI<AuctionedItem> {

	private final AuctionPlayer auctionPlayer;
	private String searchKeywords;

	public GUIAuctionHouse(@NonNull final AuctionPlayer auctionPlayer, String searchKeywords) {
		super(null, Bukkit.getPlayer(auctionPlayer.getUuid()), Settings.GUI_AUCTION_HOUSE_TITLE.getString(), Settings.GUI_AUCTION_HOUSE_ROWS.getInt(), 20 * Settings.TICK_UPDATE_GUI_TIME.getInt(), new ArrayList<>());
		this.auctionPlayer = auctionPlayer;
		this.searchKeywords = searchKeywords;

		if (!Bukkit.getOfflinePlayer(auctionPlayer.getUuid()).isOnline()) return;

		setOnOpen(open -> {
			// Player is banned from the auction house, close it
//			if (AuctionHouse.getInstance().getAuctionBanManager().checkAndHandleBan(open.player)) {
//				open.gui.exit();
//				return;TODO CHECK BAN
//			}

			// start auto refresh if enabled
			if (Settings.AUTO_REFRESH_AUCTION_PAGES.getBoolean()) startTask();

		});

		applyClose();
		draw();
	}

	public GUIAuctionHouse(@NonNull final AuctionPlayer auctionPlayer) {
		this(auctionPlayer, null);
	}

	@Override
	protected void prePopulate() {
		this.items = new ArrayList<>(AuctionHouse.getAuctionItemManager().getValidItems(this.player));

		if (this.searchKeywords != null && this.searchKeywords.length() != 0) {
			this.items = this.items.stream().filter(item -> checkSearchCriteria(this.searchKeywords, item)).collect(Collectors.toList());
		}

		if (this.auctionPlayer != null && Settings.ENABLE_FILTER_SYSTEM.getBoolean()) {
			if (this.auctionPlayer.getSelectedFilter() != AuctionItemCategory.ALL && this.auctionPlayer.getSelectedFilter() != AuctionItemCategory.SEARCH && this.auctionPlayer.getSelectedFilter() != AuctionItemCategory.SELF) {
				this.items = this.items.stream().filter(item -> checkFilterCriteria(item, this.auctionPlayer.getSelectedFilter())).collect(Collectors.toList());
			} else if (this.auctionPlayer.getSelectedFilter() == AuctionItemCategory.SELF) {
				this.items = this.items.stream().filter(item -> item.getOwner().equals(this.auctionPlayer.getPlayer().getUniqueId())).collect(Collectors.toList());
			} else if (this.auctionPlayer.getSelectedFilter() == AuctionItemCategory.SEARCH && this.auctionPlayer.getCurrentSearchPhrase().length() != 0) {
				this.items = this.items.stream().filter(item -> checkSearchCriteria(this.auctionPlayer.getCurrentSearchPhrase(), item)).collect(Collectors.toList());
			}

			if (this.auctionPlayer.getSelectedSaleType() == AuctionSaleType.USED_BIDDING_SYSTEM) {
				this.items = this.items.stream().filter(AuctionedItem::isBidItem).collect(Collectors.toList());
			}

			if (this.auctionPlayer.getSelectedSaleType() == AuctionSaleType.WITHOUT_BIDDING_SYSTEM) {
				this.items = this.items.stream().filter(item -> !item.isBidItem()).collect(Collectors.toList());
			}

			if (this.auctionPlayer.getAuctionSortType() == AuctionSortType.PRICE) {
				this.items = this.items.stream().sorted(Comparator.comparingDouble(AuctionedItem::getCurrentPrice).reversed()).collect(Collectors.toList());
			} else if (this.auctionPlayer.getAuctionSortType() == AuctionSortType.RECENT) {
				this.items = this.items.stream().sorted(Comparator.comparingLong(AuctionedItem::getExpiresAt).reversed()).collect(Collectors.toList());
			} else if (this.auctionPlayer.getAuctionSortType() == AuctionSortType.OLDEST) {
				this.items = this.items.stream().sorted(Comparator.comparingLong(AuctionedItem::getExpiresAt)).collect(Collectors.toList());
			}

			// currency
			if (this.auctionPlayer.getSelectedCurrencyFilter() != AuctionHouse.getCurrencyManager().getAllCurrency()) {
				this.items = this.items.stream().filter(item -> item.currencyMatches(this.auctionPlayer.getSelectedCurrencyFilter())).collect(Collectors.toList());
			}
		}

		this.items.sort(Comparator.comparing(AuctionedItem::isInfinite).reversed());
		this.items.sort(Comparator.comparing(AuctionedItem::isListingPriorityActive).reversed());
	}

	@Override
	protected ItemStack makeDisplayItem(AuctionedItem auctionedItem) {
		return auctionedItem.isRequest() ? auctionedItem.getDisplayRequestStack(this.player, AuctionStackType.MAIN_AUCTION_HOUSE) : auctionedItem.getDisplayStack(this.player, AuctionStackType.MAIN_AUCTION_HOUSE);
	}

	@Override
	protected void drawFixed() {
		if (Settings.ENABLE_FILTER_SYSTEM.getBoolean())
			drawFilterButton();

		drawFixedButtons();
		drawVariableButtons();
	}

	@Override
	protected void onClick(AuctionedItem auctionedItem, GuiClickEvent click) {

		// bundle and shulker inspection
		if (click.clickType == ClickType.valueOf(Settings.CLICKS_INSPECT_CONTAINER.getString().toUpperCase())) {
			handleContainerInspect(click);
			return;
		}

		// Item administration
		if (click.clickType == ClickType.valueOf(Settings.CLICKS_REMOVE_ITEM.getString().toUpperCase())) {
			if (click.player.isOp() || click.player.hasPermission("auctionhouse.admin")) {
				cancelTask();
				click.manager.showGUI(click.player, new GUIAdminItem(this.auctionPlayer, auctionedItem));
			}
			return;
		}

		// Bin Listings
		if (!auctionedItem.isBidItem()) {

			if (click.clickType == ClickType.valueOf(Settings.CLICKS_NON_BID_ITEM_QTY_PURCHASE.getString().toUpperCase())) {
				if (!auctionedItem.isAllowPartialBuy()) {
					AuctionHouse.getInstance().getLocale().getMessage("general.qtybuydisabled").processPlaceholder("item_owner", auctionedItem.getOwnerName()).sendPrefixedMessage(click.player);
					return;
				}

				if (AuctionHouse.getBanManager().isStillBanned(click.player, BanType.EVERYTHING, BanType.BUYING)) return;
				handleNonBidItem(auctionedItem, click, true);
				return;
			}

			if (click.clickType == ClickType.valueOf(Settings.CLICKS_NON_BID_ITEM_PURCHASE.getString().toUpperCase())) {
				// special case for request
				if (auctionedItem.isRequest()) {
					if (AuctionHouse.getBanManager().isStillBanned(click.player, BanType.EVERYTHING, BanType.REQUESTS)) return;

					if (click.player.getUniqueId().equals(auctionedItem.getOwner()) && !Settings.OWNER_CAN_FULFILL_OWN_REQUEST.getBoolean()) {
						AuctionHouse.getInstance().getLocale().getMessage("general.cantbuyown").sendPrefixedMessage(click.player);
						return;
					}

					cancelTask();
					click.manager.showGUI(click.player, new GUIConfirmPurchase(this.auctionPlayer, auctionedItem, false));
					AuctionHouse.getTransactionManager().addPrePurchase(click.player, auctionedItem.getId());
					return;
				}

				if (AuctionHouse.getBanManager().isStillBanned(click.player, BanType.EVERYTHING, BanType.BUYING)) return;
				handleNonBidItem(auctionedItem, click, false);
				return;
			}


			return;
		}

		// Auction Items
		if (click.clickType == ClickType.valueOf(Settings.CLICKS_BID_ITEM_PLACE_BID.getString().toUpperCase())) {
			if (AuctionHouse.getBanManager().isStillBanned(click.player, BanType.EVERYTHING, BanType.BIDDING)) return;
			handleBidItem(auctionedItem, click, false);
			return;
		}

		if (click.clickType == ClickType.valueOf(Settings.CLICKS_BID_ITEM_BUY_NOW.getString().toUpperCase())) {
			if (AuctionHouse.getBanManager().isStillBanned(click.player, BanType.EVERYTHING, BanType.BUYING)) return;
			handleBidItem(auctionedItem, click, true);
		}
	}

	//======================================================================================================//
	private void handleNonBidItem(AuctionedItem auctionItem, GuiClickEvent click, boolean buyingQuantity) {
		if (click.player.getUniqueId().equals(auctionItem.getOwner()) && !Settings.OWNER_CAN_PURCHASE_OWN_ITEM.getBoolean()) {
			AuctionHouse.getInstance().getLocale().getMessage("general.cantbuyown").sendPrefixedMessage(click.player);
			return;
		}

		if (!buyingQuantity) {
			if (!auctionItem.playerHasSufficientMoney(click.player, auctionItem.getBasePrice())) {
				AuctionHouse.getInstance().getLocale().getMessage("general.notenoughmoney").sendPrefixedMessage(click.player);
				return;
			}
		}


		if (buyingQuantity) {
			if (auctionItem.getBidStartingPrice() <= 0 || !Settings.ALLOW_USAGE_OF_BID_SYSTEM.getBoolean()) {
				if (!Settings.ALLOW_PURCHASE_OF_SPECIFIC_QUANTITIES.getBoolean()) return;
			}
		}

		cancelTask();
		click.manager.showGUI(click.player, new GUIConfirmPurchase(this.auctionPlayer, auctionItem, buyingQuantity));
		AuctionHouse.getTransactionManager().addPrePurchase(click.player, auctionItem.getId());
	}

	//======================================================================================================//
	private void handleBidItem(AuctionedItem auctionItem, GuiClickEvent click, boolean buyNow) {
		if (buyNow) {
			if (auctionItem.isBidItem()) {
				if (!Settings.ALLOW_USAGE_OF_BUY_NOW_SYSTEM.getBoolean()) return;
				if (auctionItem.getBasePrice() <= -1) {
					AuctionHouse.getInstance().getLocale().getMessage("general.buynowdisabledonitem").sendPrefixedMessage(click.player);
					return;
				}

				if (click.player.getUniqueId().equals(auctionItem.getOwner()) && !Settings.OWNER_CAN_PURCHASE_OWN_ITEM.getBoolean()) {
					AuctionHouse.getInstance().getLocale().getMessage("general.cantbuyown").sendPrefixedMessage(click.player);
					return;
				}

				cancelTask();
				click.manager.showGUI(click.player, new GUIConfirmPurchase(this.auctionPlayer, auctionItem, false));
				AuctionHouse.getTransactionManager().addPrePurchase(click.player, auctionItem.getId());
			}
			return;
		}

		if (click.player.getUniqueId().equals(auctionItem.getOwner()) && !Settings.OWNER_CAN_BID_OWN_ITEM.getBoolean() || Settings.BIDDING_TAKES_MONEY.getBoolean() && click.player.getUniqueId().equals(auctionItem.getOwner())) {
			AuctionHouse.getInstance().getLocale().getMessage("general.cantbidonown").sendPrefixedMessage(click.player);
			return;
		}

		if (click.player.getUniqueId().equals(auctionItem.getHighestBidder()) && !Settings.ALLOW_REPEAT_BIDS.getBoolean()) {
			AuctionHouse.getInstance().getLocale().getMessage("general.alreadyhighestbidder").sendPrefixedMessage(click.player);
			return;
		}

		cancelTask();

		if (Settings.FORCE_CUSTOM_BID_AMOUNT.getBoolean()) {
			click.gui.exit();

			new TitleInput(player, AuctionHouse.getInstance().getLocale().getMessage("titles.enter bid.title").getMessage(), AuctionHouse.getInstance().getLocale().getMessage("titles.enter bid.subtitle").getMessage()) {

				@Override
				public void onExit(Player player) {
					AuctionHouse.getGuiManager().showGUI(player, new GUIAuctionHouse(GUIAuctionHouse.this.auctionPlayer));
				}

				@Override
				public boolean onResult(String string) {
					string = ChatColor.stripColor(string);

					if (!NumberUtils.isDouble(string)) {
						AuctionHouse.getInstance().getLocale().getMessage("general.notanumber").processPlaceholder("value", string).sendPrefixedMessage(player);
						return false;
					}

					double value = Double.parseDouble(string);

					if (value <= 0) {
						AuctionHouse.getInstance().getLocale().getMessage("general.cannotbezero").sendPrefixedMessage(click.player);
						return false;
					}

					if (value > Settings.MAX_AUCTION_INCREMENT_PRICE.getDouble()) {
						AuctionHouse.getInstance().getLocale().getMessage("pricing.maxbidincrementprice").processPlaceholder("price", Settings.MAX_AUCTION_INCREMENT_PRICE.getDouble()).sendPrefixedMessage(click.player);
						return false;
					}

					double newBiddingAmount = 0;
					if (Settings.USE_REALISTIC_BIDDING.getBoolean()) {
						if (value >= auctionItem.getCurrentPrice() + auctionItem.getBidIncrementPrice()) {
							newBiddingAmount = value;
						} else {
							if (Settings.BID_MUST_BE_HIGHER_THAN_PREVIOUS.getBoolean()) {
								click.manager.showGUI(click.player, new GUIAuctionHouse(GUIAuctionHouse.this.auctionPlayer));
								AuctionHouse.getInstance().getLocale().getMessage("pricing.bidmusthigherthanprevious").processPlaceholder("current_bid", AuctionHouse.getAPI().getFinalizedCurrencyNumber(auctionItem.getCurrentPrice(), auctionItem.getCurrency(), auctionItem.getCurrencyItem())).sendPrefixedMessage(click.player);
								return true;
							}

							newBiddingAmount = auctionItem.getCurrentPrice() + value;
						}
					} else {
						newBiddingAmount = auctionItem.getCurrentPrice() + value;
					}

					newBiddingAmount = Settings.ROUND_ALL_PRICES.getBoolean() ? Math.round(newBiddingAmount) : newBiddingAmount;

					if (Settings.PLAYER_NEEDS_TOTAL_PRICE_TO_BID.getBoolean() && !auctionItem.playerHasSufficientMoney(click.player, newBiddingAmount)) {
						AuctionHouse.getInstance().getLocale().getMessage("general.notenoughmoney").sendPrefixedMessage(click.player);
						AuctionHouse.getGuiManager().showGUI(player, new GUIAuctionHouse(GUIAuctionHouse.this.auctionPlayer));
						return true;
					}

					if (Settings.ASK_FOR_BID_CONFIRMATION.getBoolean()) {
						click.manager.showGUI(click.player, new GUIConfirmBid(GUIAuctionHouse.this.auctionPlayer, auctionItem, newBiddingAmount));
						return true;
					}

					ItemStack itemStack = auctionItem.getItem();

					OfflinePlayer oldBidder = Bukkit.getOfflinePlayer(auctionItem.getHighestBidder());
					OfflinePlayer owner = Bukkit.getOfflinePlayer(auctionItem.getOwner());

					AuctionBidEvent auctionBidEvent = new AuctionBidEvent(click.player, auctionItem, newBiddingAmount, true);
					Bukkit.getServer().getPluginManager().callEvent(auctionBidEvent);
					if (auctionBidEvent.isCancelled()) return true;

					if (Settings.BIDDING_TAKES_MONEY.getBoolean()) {
						final double oldBidAmount = auctionItem.getCurrentPrice();

						if (!auctionItem.playerHasSufficientMoney(click.player, newBiddingAmount)) {
							AuctionHouse.getInstance().getLocale().getMessage("general.notenoughmoney").sendPrefixedMessage(click.player);
							return true;
						}

						if (click.player.getUniqueId().equals(owner.getUniqueId()) || oldBidder.getUniqueId().equals(click.player.getUniqueId())) {
							return true;
						}

						if (!auctionItem.getHighestBidder().equals(auctionItem.getOwner())) {
							if (Settings.STORE_PAYMENTS_FOR_MANUAL_COLLECTION.getBoolean())
								AuctionHouse.getDataManager().insertAuctionPayment(new AuctionPayment(oldBidder.getUniqueId(), oldBidAmount, auctionItem.getItem(), AuctionHouse.getInstance().getLocale().getMessage("general.prefix").getMessage(), PaymentReason.BID_RETURNED, auctionItem.getCurrency(), auctionItem.getCurrencyItem()), null);
							else
								AuctionHouse.getCurrencyManager().deposit(oldBidder, oldBidAmount, auctionItem.getCurrency(), auctionItem.getCurrencyItem());

							if (oldBidder.isOnline())
								AuctionHouse.getInstance().getLocale().getMessage("pricing.moneyadd")
										.processPlaceholder("player_balance", AuctionHouse.getAPI().getFinalizedCurrencyNumber(AuctionHouse.getCurrencyManager().getBalance(oldBidder, auctionItem.getCurrency().split("/")[0], auctionItem.getCurrency().split("/")[1]), auctionItem.getCurrency(), auctionItem.getCurrencyItem()))
										.processPlaceholder("price", AuctionHouse.getAPI().getFinalizedCurrencyNumber(oldBidAmount, auctionItem.getCurrency(), auctionItem.getCurrencyItem()))
										.sendPrefixedMessage(oldBidder.getPlayer());
						}

						AuctionHouse.getCurrencyManager().withdraw(click.player, newBiddingAmount, auctionItem.getCurrency(), auctionItem.getCurrencyItem());
						AuctionHouse.getInstance().getLocale().getMessage("pricing.moneyremove")
								.processPlaceholder("player_balance", AuctionHouse.getAPI().getFinalizedCurrencyNumber(AuctionHouse.getCurrencyManager().getBalance(click.player, auctionItem.getCurrency().split("/")[0], auctionItem.getCurrency().split("/")[1]), auctionItem.getCurrency(), auctionItem.getCurrencyItem()))
								.processPlaceholder("price", AuctionHouse.getAPI().getFinalizedCurrencyNumber(newBiddingAmount, auctionItem.getCurrency(), auctionItem.getCurrencyItem()))
								.sendPrefixedMessage(click.player);

					}

					auctionItem.setHighestBidder(click.player.getUniqueId());
					auctionItem.setHighestBidderName(click.player.getName());
					auctionItem.setCurrentPrice(newBiddingAmount);

					if (auctionItem.getBasePrice() != -1 && Settings.SYNC_BASE_PRICE_TO_HIGHEST_PRICE.getBoolean() && auctionItem.getCurrentPrice() > auctionItem.getBasePrice()) {
						auctionItem.setBasePrice(Settings.ROUND_ALL_PRICES.getBoolean() ? Math.round(auctionItem.getCurrentPrice()) : auctionItem.getCurrentPrice());
					}

					if (Settings.INCREASE_TIME_ON_BID.getBoolean()) {
						auctionItem.setExpiresAt(auctionItem.getExpiresAt() + 1000L * Settings.TIME_TO_INCREASE_BY_ON_BID.getInt());
					}

					if (oldBidder.isOnline()) {
						AuctionHouse.getInstance().getLocale().getMessage("auction.outbid").processPlaceholder("player", click.player.getName()).processPlaceholder("player_displayname", AuctionAPI.getInstance().getDisplayName(click.player)).processPlaceholder("item", AuctionAPI.getInstance().getItemName(itemStack)).sendPrefixedMessage(oldBidder.getPlayer());
					}

					if (owner.isOnline()) {
						AuctionHouse.getInstance().getLocale().getMessage("auction.placedbid").processPlaceholder("player", click.player.getName()).processPlaceholder("player_displayname", AuctionAPI.getInstance().getDisplayName(click.player)).processPlaceholder("amount", AuctionHouse.getAPI().getFinalizedCurrencyNumber(auctionItem.getCurrentPrice(), auctionItem.getCurrency(), auctionItem.getCurrencyItem())).processPlaceholder("item", AuctionAPI.getInstance().getItemName(itemStack)).sendPrefixedMessage(owner.getPlayer());
					}

					if (Settings.BROADCAST_AUCTION_BID.getBoolean()) {
						Bukkit.getOnlinePlayers().forEach(player -> AuctionHouse.getInstance().getLocale().getMessage("auction.broadcast.bid").processPlaceholder("player", click.player.getName()).processPlaceholder("player_displayname", AuctionAPI.getInstance().getDisplayName(click.player)).processPlaceholder("amount", AuctionHouse.getAPI().getFinalizedCurrencyNumber(auctionItem.getCurrentPrice(), auctionItem.getCurrency(), auctionItem.getCurrencyItem())).processPlaceholder("item", AuctionAPI.getInstance().getItemName(itemStack)).sendPrefixedMessage(player));
					}

					click.manager.showGUI(click.player, new GUIAuctionHouse(GUIAuctionHouse.this.auctionPlayer));

					return true;
				}
			};

			return;
		}

		click.manager.showGUI(click.player, new GUIBid(this.auctionPlayer, auctionItem));
	}

	//======================================================================================================//
	private void handleContainerInspect(GuiClickEvent click) {
		ItemStack clicked = click.clickedItem;

		if (BundleUtil.isBundledItem(clicked)) {
			cancelTask();
			click.manager.showGUI(click.player, new GUIContainerInspect(this.auctionPlayer, click.clickedItem));
			return;
		}

		if (!ServerVersion.isServerVersionAtLeast(ServerVersion.V1_11)) return;
		if (click.player.isOp() || click.player.hasPermission("auctionhouse.admin") || click.player.hasPermission("auctionhouse.inspectshulker")) {
			if (!(clicked.getItemMeta() instanceof BlockStateMeta)) return;

			BlockStateMeta meta = (BlockStateMeta) clicked.getItemMeta();
			if (!(meta.getBlockState() instanceof ShulkerBox)) return;
			cancelTask();
			click.manager.showGUI(click.player, new GUIContainerInspect(this.auctionPlayer, click.clickedItem));
		}
	}

	//======================================================================================================//
	private boolean checkFilterCriteria(AuctionedItem auctionItem, AuctionItemCategory category) {
		// option for only whitelisted shit
		if (Settings.FILTER_ONLY_USES_WHITELIST.getBoolean()) {
			if (!Settings.FILTER_WHITELIST_USES_DURABILITY.getBoolean())
				return AuctionHouse.getInstance().getFilterManager().getFilterWhitelist(category).stream().anyMatch(item -> item != null && item.isSimilar(auctionItem.getItem()));
			else
				return AuctionHouse.getInstance().getFilterManager().getFilterWhitelist(category).stream().anyMatch(item -> item != null && item.getType() == auctionItem.getItem().getType() && item.getDurability() == auctionItem.getItem().getDurability());
		}

		return auctionItem.getCategory() == category || AuctionHouse.getInstance().getFilterManager().getFilterWhitelist(category).stream().anyMatch(item -> item != null && item.isSimilar(auctionItem.getItem()));
	}

	private boolean checkSearchCriteria(String phrase, AuctionedItem item) {
		if (item == null) return false;
		ItemStack stack = item.getItem();
		if (stack == null) return false;

		return AuctionAPI.getInstance().matchSearch(phrase, AuctionAPI.getInstance().getItemName(stack))
				|| AuctionAPI.getInstance().matchSearch(phrase, item.getCategory().getTranslatedType())
				|| AuctionAPI.getInstance().matchSearch(phrase, stack.getType().name())
				|| AuctionAPI.getInstance().matchSearch(phrase, item.getOwnerName())
				|| AuctionAPI.getInstance().matchSearch(phrase, AuctionAPI.getInstance().getItemLore(stack))
				|| AuctionAPI.getInstance().matchSearch(phrase, AuctionAPI.getInstance().getItemEnchantments(stack));
	}

	//======================================================================================================//
	private void drawVariableButtons() {
		if (Settings.GUI_AUCTION_HOUSE_ITEMS_YOUR_AUCTIONS_ENABLED.getBoolean()) {
			setButton(Settings.GUI_AUCTION_HOUSE_ITEMS_YOUR_AUCTIONS_SLOT.getInt(), QuickItem
					.of(Settings.GUI_AUCTION_HOUSE_ITEMS_YOUR_AUCTIONS_ITEM.getString())
					.name(Settings.GUI_AUCTION_HOUSE_ITEMS_YOUR_AUCTIONS_NAME.getString())
					.lore(this.player, Replacer.replaceVariables(Settings.GUI_AUCTION_HOUSE_ITEMS_YOUR_AUCTIONS_LORE.getStringList(), "active_player_auctions", auctionPlayer.getItems(false).size(), "player_balance", AuctionHouse.getAPI().getNumberAsCurrency(AuctionHouse.getCurrencyManager().getBalance(auctionPlayer.getPlayer())))).make(), e -> {

				cancelTask();
				e.manager.showGUI(e.player, new GUIActiveAuctions(this.auctionPlayer));
			});
		}

		if (Settings.GUI_AUCTION_HOUSE_ITEMS_COLLECTION_BIN_ENABLED.getBoolean()) {
			setButton(Settings.GUI_AUCTION_HOUSE_ITEMS_COLLECTION_BIN_SLOT.getInt(), QuickItem.of(Settings.GUI_AUCTION_HOUSE_ITEMS_COLLECTION_BIN_ITEM.getString()).name(Settings.GUI_AUCTION_HOUSE_ITEMS_COLLECTION_BIN_NAME.getString()).lore(this.player, Replacer.replaceVariables(Settings.GUI_AUCTION_HOUSE_ITEMS_COLLECTION_BIN_LORE.getStringList(), "expired_player_auctions", auctionPlayer.getItems(true).size())).make(), e -> {

				cancelTask();
				e.manager.showGUI(e.player, new GUIExpiredItems(this, this.auctionPlayer));
			});
		}

		if (Settings.GUI_AUCTION_HOUSE_ITEMS_TRANSACTIONS_ENABLED.getBoolean()) {
			setButton(Settings.GUI_AUCTION_HOUSE_ITEMS_TRANSACTIONS_SLOT.getInt(), QuickItem.of(Settings.GUI_AUCTION_HOUSE_ITEMS_TRANSACTIONS_ITEM.getString())
					.name(Settings.GUI_AUCTION_HOUSE_ITEMS_TRANSACTIONS_NAME.getString())
					.lore(this.player, Replacer.replaceVariables(Settings.GUI_AUCTION_HOUSE_ITEMS_TRANSACTIONS_LORE.getStringList(),
							"total_items_bought", AuctionHouse.getTransactionManager().getTotalItemsBought(auctionPlayer.getPlayer().getUniqueId()),
							"total_items_sold", AuctionHouse.getTransactionManager().getTotalItemsSold(auctionPlayer.getPlayer().getUniqueId()))
					).make(), e -> {

				cancelTask();
				if (Settings.RESTRICT_ALL_TRANSACTIONS_TO_PERM.getBoolean() && !e.player.hasPermission("auctionhouse.transactions.viewall")) {
					e.manager.showGUI(e.player, new GUITransactionList(e.player, false));
				} else {
					e.manager.showGUI(e.player, new GUITransactionType(e.player));
				}
			});
		}
	}

	//======================================================================================================//
	private void drawFixedButtons() {
		if (Settings.ENABLE_FILTER_SYSTEM.getBoolean())
			drawFilterButton();

		if (Settings.REPLACE_HOW_TO_SELL_WITH_LIST_BUTTON.getBoolean()) {
			if (Settings.GUI_AUCTION_HOUSE_ITEMS_LIST_ITEM_ENABLED.getBoolean()) {
				setButton(Settings.GUI_AUCTION_HOUSE_ITEMS_LIST_ITEM_SLOT.getInt(), QuickItem
						.of(Settings.GUI_AUCTION_HOUSE_ITEMS_LIST_ITEM_ITEM.getString())
						.name(Settings.GUI_AUCTION_HOUSE_ITEMS_LIST_ITEM_NAME.getString())
						.lore(this.player, Settings.GUI_AUCTION_HOUSE_ITEMS_LIST_ITEM_LORE.getStringList())
						.make(), e -> {

					if (AuctionHouse.getBanManager().isStillBanned(e.player, BanType.EVERYTHING, BanType.SELL)) return;

					// using this will ignore the "SELL_MENU_REQUIRES_USER_TO_HOLD_ITEM" setting
					if (FloodGateHook.isFloodGateUser(e.player)) {
						AuctionHouse.getInstance().getLocale().getMessage("commands.no_permission").sendPrefixedMessage(e.player);
						return;
					}

					if (this.auctionPlayer.isAtItemLimit(this.player)) {
						return;
					}

					if (Settings.SELL_MENU_SKIPS_TYPE_SELECTION.getBoolean()) {
						if (Settings.FORCE_AUCTION_USAGE.getBoolean()) {
							cancelTask();
							AuctionHouse.getGuiManager().showGUI(player, new GUISellPlaceItem(auctionPlayer, GUISellPlaceItem.ViewMode.SINGLE_ITEM, ListingType.AUCTION));
							return;
						}

						if (!Settings.ALLOW_USAGE_OF_BID_SYSTEM.getBoolean()) {
							cancelTask();
							AuctionHouse.getGuiManager().showGUI(player, new GUISellPlaceItem(auctionPlayer, GUISellPlaceItem.ViewMode.SINGLE_ITEM, ListingType.BIN));
							return;
						}

						cancelTask();
						AuctionHouse.getGuiManager().showGUI(player, new GUISellListingType(this.auctionPlayer, selected -> {
							AuctionHouse.getGuiManager().showGUI(player, new GUISellPlaceItem(this.auctionPlayer, GUISellPlaceItem.ViewMode.SINGLE_ITEM, selected));
						}));

					} else {
						cancelTask();
						AuctionHouse.getGuiManager().showGUI(player, new GUISellListingType(this.auctionPlayer, selected -> {
							AuctionHouse.getGuiManager().showGUI(player, new GUISellPlaceItem(this.auctionPlayer, GUISellPlaceItem.ViewMode.SINGLE_ITEM, selected));
						}));
					}

				});
			}
		} else {
			if (Settings.GUI_AUCTION_HOUSE_ITEMS_HOW_TO_SELL_ENABLED.getBoolean()) {
				setItem(Settings.GUI_AUCTION_HOUSE_ITEMS_HOW_TO_SELL_SLOT.getInt(), QuickItem
						.of(Settings.GUI_AUCTION_HOUSE_ITEMS_HOW_TO_SELL_ITEM.getString())
						.name(Settings.GUI_AUCTION_HOUSE_ITEMS_HOW_TO_SELL_NAME.getString())
						.lore(this.player, Settings.GUI_AUCTION_HOUSE_ITEMS_HOW_TO_SELL_LORE.getStringList())
						.make());
			}
		}

		if (Settings.GUI_AUCTION_HOUSE_ITEMS_GUIDE_ENABLED.getBoolean()) {
			setItem(Settings.GUI_AUCTION_HOUSE_ITEMS_GUIDE_SLOT.getInt(), QuickItem
					.of(Settings.GUI_AUCTION_HOUSE_ITEMS_GUIDE_ITEM.getString())
					.name(Settings.GUI_AUCTION_HOUSE_ITEMS_GUIDE_NAME.getString()).lore(this.player, Settings.GUI_AUCTION_HOUSE_ITEMS_GUIDE_LORE.getStringList())
					.make());
		}

		if (Settings.GUI_REFRESH_BTN_ENABLED.getBoolean()) {
			setButton(Settings.GUI_REFRESH_BTN_SLOT.getInt(), getRefreshButton(), ClickType.LEFT, e -> {
				if (Settings.USE_REFRESH_COOL_DOWN.getBoolean()) {
					if (AuctionHouse.getAuctionPlayerManager().getCooldowns().containsKey(this.auctionPlayer.getPlayer().getUniqueId())) {
						if (AuctionHouse.getAuctionPlayerManager().getCooldowns().get(this.auctionPlayer.getPlayer().getUniqueId()) > System.currentTimeMillis()) {
							return;
						}
					}
					AuctionHouse.getAuctionPlayerManager().addCooldown(this.auctionPlayer.getPlayer().getUniqueId());
				}
				cancelTask();
				e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
			});
		}
	}

	//======================================================================================================//
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

			ItemStack item = materialToBeUsed.equalsIgnoreCase("PLAYER_HEAD") ?
					QuickItem
							.of(AuctionAPI.getInstance().getPlayerHead(this.auctionPlayer.getPlayer().getName()))
							.name(Replacer.replaceVariables(Settings.GUI_AUCTION_HOUSE_ITEMS_FILTER_MENU_NAME.getString(), "filter_category", auctionPlayer.getSelectedFilter().getTranslatedType(), "filter_auction_type", auctionPlayer.getSelectedSaleType().getTranslatedType(), "filter_sort_order", auctionPlayer.getAuctionSortType().getTranslatedType()))
							.lore(this.player, Replacer.replaceVariables(Settings.GUI_AUCTION_HOUSE_ITEMS_FILTER_MENU_LORE.getStringList(),
									"filter_category", auctionPlayer.getSelectedFilter().getTranslatedType(),
									"filter_auction_type", auctionPlayer.getSelectedSaleType().getTranslatedType(),
									"filter_sort_order", auctionPlayer.getAuctionSortType().getTranslatedType(),
									"filter_currency", auctionPlayer.getSelectedCurrencyFilter().getDisplayName()
							))
							.make() : QuickItem
					.of(materialToBeUsed)
					.name(Replacer.replaceVariables(Settings.GUI_AUCTION_HOUSE_ITEMS_FILTER_MENU_NAME.getString(),
							"filter_category", auctionPlayer.getSelectedFilter().getTranslatedType(),
							"filter_auction_type", auctionPlayer.getSelectedSaleType().getTranslatedType(),
							"filter_sort_order", auctionPlayer.getAuctionSortType().getTranslatedType()))
					.lore(this.player, Replacer.replaceVariables(Settings.GUI_AUCTION_HOUSE_ITEMS_FILTER_MENU_LORE.getStringList(),
							"filter_category", auctionPlayer.getSelectedFilter().getTranslatedType(),
							"filter_auction_type", auctionPlayer.getSelectedSaleType().getTranslatedType(),
							"filter_sort_order", auctionPlayer.getAuctionSortType().getTranslatedType(),
							"filter_currency", auctionPlayer.getSelectedCurrencyFilter().getDisplayName())).make();

			if (Settings.GUI_AUCTION_HOUSE_ITEMS_FILTER_MENU_ENABLED.getBoolean()) {
				setButton(Settings.GUI_AUCTION_HOUSE_ITEMS_FILTER_MENU_SLOT.getInt(), item, e -> {
					if (e.clickType == ClickType.valueOf(Settings.CLICKS_FILTER_CATEGORY.getString().toUpperCase())) {
						cancelTask();
						e.manager.showGUI(e.player, new GUIFilterSelection(this.auctionPlayer));
						return;
					}

					if (e.clickType == ClickType.valueOf(Settings.CLICKS_FILTER_RESET.getString().toUpperCase())) {
						this.auctionPlayer.resetFilter();
						updatePlayerFilter(this.auctionPlayer);
						draw();
						return;
					}

					if (e.clickType == ClickType.valueOf(Settings.CLICKS_FILTER_SORT_SALE_TYPE.getString().toUpperCase())) {
						if (Settings.ALLOW_USAGE_OF_BID_SYSTEM.getBoolean()) {
							this.auctionPlayer.setSelectedSaleType(this.auctionPlayer.getSelectedSaleType().next());
							updatePlayerFilter(this.auctionPlayer);
							draw();
						}
						return;
					}

					if (e.clickType == ClickType.valueOf(Settings.CLICKS_FILTER_SORT_PRICE_OR_RECENT.getString().toUpperCase())) {
						this.auctionPlayer.setAuctionSortType(this.auctionPlayer.getAuctionSortType().next());
						updatePlayerFilter(this.auctionPlayer);
						draw();
					}
				});
			}
			return;
		}

		if (Settings.GUI_AUCTION_HOUSE_ITEMS_FILTER_ENABLED.getBoolean()) {
			setButton(Settings.GUI_AUCTION_HOUSE_ITEMS_FILTER_SLOT.getInt(), QuickItem
					.of(this.auctionPlayer.getSelectedFilter().getFilterIcon())
					.name(Settings.GUI_AUCTION_HOUSE_ITEMS_FILTER_NAME.getString())
					.lore(this.player, Replacer.replaceVariables(Settings.GUI_AUCTION_HOUSE_ITEMS_FILTER_LORE.getStringList(),
							"filter_category", auctionPlayer.getSelectedFilter().getTranslatedType(),
							"filter_auction_type", auctionPlayer.getSelectedSaleType().getTranslatedType(),
							"filter_currency", auctionPlayer.getSelectedCurrencyFilter().getDisplayName(),
							"filter_sort_order", auctionPlayer.getAuctionSortType().getTranslatedType()))
					.make(), e -> {

				if (e.clickType == ClickType.valueOf(Settings.CLICKS_FILTER_CATEGORY.getString().toUpperCase())) {
					this.auctionPlayer.setSelectedFilter(this.auctionPlayer.getSelectedFilter().next());
					updatePlayerFilter(this.auctionPlayer);
					draw();
					return;
				}

				if (e.clickType == ClickType.valueOf(Settings.CLICKS_FILTER_RESET.getString().toUpperCase())) {
					this.auctionPlayer.resetFilter();
					updatePlayerFilter(this.auctionPlayer);
					draw();
					return;
				}

				if (e.clickType == ClickType.valueOf(Settings.CLICKS_FILTER_SORT_SALE_TYPE.getString().toUpperCase())) {
					if (Settings.ALLOW_USAGE_OF_BID_SYSTEM.getBoolean()) {
						this.auctionPlayer.setSelectedSaleType(this.auctionPlayer.getSelectedSaleType().next());
						updatePlayerFilter(this.auctionPlayer);
						draw();
					}
					return;
				}

				if (e.clickType == ClickType.valueOf(Settings.CLICKS_FILTER_SORT_PRICE_OR_RECENT.getString().toUpperCase())) {
					this.auctionPlayer.setAuctionSortType(this.auctionPlayer.getAuctionSortType().next());
					updatePlayerFilter(this.auctionPlayer);
					draw();
					return;
				}

				if (e.clickType == ClickType.valueOf(Settings.CLICKS_FILTER_CURRENCY.getString().toUpperCase())) {
					this.auctionPlayer.setSelectedCurrencyFilter(AuctionHouse.getCurrencyManager().getNext(this.auctionPlayer.getSelectedCurrencyFilter()));
					draw();
					return;
				}
			});
		}
	}

	//======================================================================================================//
	private void updatePlayerFilter(AuctionPlayer player) {
		AuctionHouse.getDataManager().updateAuctionPlayer(player, (error, success) -> {
			if (error == null && success)
				if (!Settings.DISABLE_PROFILE_UPDATE_MSG.getBoolean()) AuctionHouse.getInstance().getLogger().info("Updating profile for player: " + player.getPlayer().getName());

		});
	}

	@Override
	protected List<Integer> fillSlots() {
		return Settings.GUI_AUCTION_HOUSE_FILL_SLOTS.getIntegerList();
	}
}
