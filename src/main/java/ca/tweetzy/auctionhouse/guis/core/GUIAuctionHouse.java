package ca.tweetzy.auctionhouse.guis.core;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.api.ban.BanType;
import ca.tweetzy.auctionhouse.auction.AuctionPayment;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.auction.AuctionedItem;
import ca.tweetzy.auctionhouse.auction.ListingType;
import ca.tweetzy.auctionhouse.auction.enums.*;
import ca.tweetzy.auctionhouse.api.currency.AbstractCurrency;
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
import ca.tweetzy.auctionhouse.lang.AuctionLocale;
import ca.tweetzy.auctionhouse.helpers.BundleUtil;
import ca.tweetzy.auctionhouse.helpers.SlotHelper;
import ca.tweetzy.auctionhouse.managers.SoundManager;
import ca.tweetzy.flight.utils.input.TitleInput;
import ca.tweetzy.auctionhouse.hooks.FloodGateHook;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.flight.FlightPlugin;
import ca.tweetzy.flight.comp.enums.ServerVersion;
import ca.tweetzy.flight.config.tweetzy.TweetzyYamlConfig;
import ca.tweetzy.flight.gui.events.GuiClickEvent;
import ca.tweetzy.flight.utils.MathUtil;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class GUIAuctionHouse extends AuctionUpdatingPagedGUI<AuctionedItem> {

	private final AuctionPlayer auctionPlayer;
	private String searchKeywords;
	private Long lastItemClick = null;
	private Long lastRefreshClick = null;

	public GUIAuctionHouse(@NonNull final AuctionPlayer auctionPlayer, String searchKeywords) {
		super(null, Bukkit.getPlayer(auctionPlayer.getUuid()), AuctionLocale.msg(Bukkit.getPlayer(auctionPlayer.getUuid()), "gui.auction house.title"), Settings.GUI_AUCTION_HOUSE_ROWS.getInt(), 20 * Settings.TICK_UPDATE_GUI_TIME.getInt(), new ArrayList<>());
		this.auctionPlayer = auctionPlayer;
		this.searchKeywords = searchKeywords;
		setDefaultItem(QuickItem.bg(QuickItem.of(Settings.GUI_AUCTION_HOUSE_BG_ITEM.getString()).make()));
		setAllowShiftClick(true); // Enable shift clicking for filter button
		setSlotClickDelay(getPreviousButtonSlot(), Settings.asLong(Settings.MAIN_AH_NAVIGATION_COOLDOWN));
		setSlotClickDelay(getNextButtonSlot(), Settings.asLong(Settings.MAIN_AH_NAVIGATION_COOLDOWN));

		if (Settings.USE_SEPARATE_FILTER_MENU.getBoolean() && Settings.GUI_AUCTION_HOUSE_ITEMS_FILTER_MENU_ENABLED.getBoolean()) {
			SlotHelper.getButtonSlots(Settings.GUI_AUCTION_HOUSE_ITEMS_FILTER_MENU_SLOT.getString()).forEach(slot -> setSlotClickDelay(slot, Settings.asLong(Settings.MAIN_AH_FILTER_COOLDOWN)));
		}

		if (!Settings.USE_SEPARATE_FILTER_MENU.getBoolean() && Settings.GUI_AUCTION_HOUSE_ITEMS_FILTER_ENABLED.getBoolean()) {
			SlotHelper.getButtonSlots(Settings.GUI_AUCTION_HOUSE_ITEMS_FILTER_SLOT.getString()).forEach(slot -> setSlotClickDelay(slot, Settings.asLong(Settings.MAIN_AH_FILTER_COOLDOWN)));
		}

		setClickDelayAction((lastClicked, delay, click) -> {
			if (click.slot == getPreviousButtonSlot() || click.slot == getNextButtonSlot()) {
				AuctionLocale.tell(player, "general.cooldown.navigate page", "time", AuctionHouse.getCooldownManager().formatTime(System.currentTimeMillis() - lastClicked));
				return;
			}

			AuctionLocale.tell(player, "general.cooldown.filter", "time", AuctionHouse.getCooldownManager().formatTime(System.currentTimeMillis() - lastClicked));

		});

		if (Settings.FILTER_DONT_REMEMBER.getBoolean())
			this.auctionPlayer.resetFilter();

		if (!Bukkit.getOfflinePlayer(auctionPlayer.getUuid()).isOnline()) return;

		setOnOpen(open -> {
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
		// Start with a stream from the valid items collection
		this.items = AuctionHouse.getAuctionItemManager().getValidItems(this.player).stream()
				// Apply search filter if search keywords are provided
				.filter(item -> {
					if (this.searchKeywords != null && this.searchKeywords.length() != 0) {
						return checkSearchCriteria(this.searchKeywords, item);
					}
					return true;
				})
				// Apply filter system if enabled
				.filter(item -> {
					if (this.auctionPlayer != null && Settings.ENABLE_FILTER_SYSTEM.getBoolean()) {
						// Category filter
						final AuctionItemCategory selectedFilter = this.auctionPlayer.getSelectedFilter();
						if (selectedFilter != AuctionItemCategory.ALL && selectedFilter != AuctionItemCategory.SEARCH && selectedFilter != AuctionItemCategory.SELF) {
							if (!checkFilterCriteria(item, selectedFilter)) {
								return false;
							}
						} else if (selectedFilter == AuctionItemCategory.SELF) {
							if (!item.getOwner().equals(this.auctionPlayer.getPlayer().getUniqueId())) {
								return false;
							}
						} else if (selectedFilter == AuctionItemCategory.SEARCH && this.auctionPlayer.getCurrentSearchPhrase().length() != 0) {
							if (!checkSearchCriteria(this.auctionPlayer.getCurrentSearchPhrase(), item)) {
								return false;
							}
						}

						// Sale type filter
						final AuctionSaleType saleType = this.auctionPlayer.getSelectedSaleType();
						if (saleType == AuctionSaleType.USED_BIDDING_SYSTEM && !item.isBidItem()) {
							return false;
						}
						if (saleType == AuctionSaleType.WITHOUT_BIDDING_SYSTEM && item.isBidItem()) {
							return false;
						}

						// Currency filter
						final AbstractCurrency currencyFilter = this.auctionPlayer.getSelectedCurrencyFilter();
						if (currencyFilter != null && !currencyFilter.equals(AuctionHouse.getCurrencyManager().getAllCurrency())) {
							if (!item.currencyMatches(currencyFilter)) {
								return false;
							}
						}
					}
					return true;
				})
				// Collect to list once
				.collect(Collectors.toList());

		// Apply sorting - use compound comparator to avoid multiple sorts
		final Comparator<AuctionedItem> baseComparator;
		if (this.auctionPlayer != null && Settings.ENABLE_FILTER_SYSTEM.getBoolean()) {
			baseComparator = createSortComparator(this.auctionPlayer.getAuctionSortType());
		} else {
			baseComparator = Comparator.comparing(AuctionedItem::isListingPriorityActive).reversed()
					.thenComparing(Comparator.comparing(AuctionedItem::isInfinite).reversed());
		}
		this.items.sort(baseComparator);
	}

	/**
	 * Creates a comparator for sorting based on the auction sort type
	 */
	private Comparator<AuctionedItem> createSortComparator(AuctionSortType sortType) {
		Comparator<AuctionedItem> comparator;
		switch (sortType) {
			case PRICE:
				comparator = Comparator.comparingDouble(AuctionedItem::getCurrentPrice).reversed();
				break;
			case OLDEST:
				comparator = Comparator.comparingLong(AuctionedItem::getExpiresAt);
				break;
			case RECENT:
			default:
				comparator = Comparator.comparing(AuctionedItem::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder()));
				break;
		}
		// Priority listings first, then infinite, then sort by selected type within each group
		return Comparator.comparing(AuctionedItem::isListingPriorityActive).reversed()
				.thenComparing(Comparator.comparing(AuctionedItem::isInfinite).reversed())
				.thenComparing(comparator);
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
		drawCustomButtons();
	}

	/**
	 * Draws custom buttons from configuration
	 */
	private void drawCustomButtons() {
		final TweetzyYamlConfig cfg = FlightPlugin.getCoreConfig();
		final String sectionBase = "gui.auction house.custom items";
		if (!cfg.has(sectionBase)) {
			return;
		}

		for (final String customItemEntry : cfg.getKeys(sectionBase)) {
			final String prefix = sectionBase + "." + customItemEntry;
			final Object slotValue = cfg.get(prefix + ".slot");
			if (slotValue == null) {
				continue;
			}

			SlotHelper.getButtonSlots(slotValue.toString()).forEach(slot -> {
				Object itemMat = cfg.get(prefix + ".item");
				Object nameObj = cfg.get(prefix + ".name");
				List<String> loreLines = stringList(cfg, prefix + ".lore");
				List<String> commandLines = stringList(cfg, prefix + ".commands");

				setButton(slot, QuickItem
						.of(Objects.toString(itemMat, "AIR"))
						.name(Objects.toString(nameObj, ""))
						.lore(this.player, loreLines)
						.make(), click -> executeCustomButtonCommands(commandLines, click));
			});
		}
	}

	private static List<String> stringList(TweetzyYamlConfig cfg, String path) {
		Object loreObj = cfg.get(path);
		if (!(loreObj instanceof List<?> raw)) {
			return Collections.emptyList();
		}
		List<String> out = new ArrayList<>(raw.size());
		for (Object line : raw) {
			if (line != null) {
				out.add(line.toString());
			}
		}
		return out;
	}

	/**
	 * Executes commands for custom buttons
	 */
	private void executeCustomButtonCommands(List<String> commands, GuiClickEvent click) {
		commands.forEach(cmd -> {
			boolean isConsoleCommand = cmd.startsWith("[console]");
			final String finalCommand = cmd
					.replace("[player]", "").replace("[console] ", "")
					.replace("[player] ", "").replace("[console]", "");

			if (isConsoleCommand) {
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), finalCommand.replace("%player%", click.player.getName()));
			} else {
				click.player.performCommand(finalCommand.replace("%player%", click.player.getName()));
			}
		});
	}

	@Override
	protected void onClick(AuctionedItem auctionedItem, GuiClickEvent click) {

		// Watchlist: add/remove listing (middle-click or configured click)
		if (Settings.WATCHLIST_ENABLED.getBoolean()) {
			try {
				if (click.clickType == ClickType.valueOf(Settings.CLICKS_ADD_TO_WATCHLIST.getString().toUpperCase())) {
					if (click.player.getUniqueId().equals(auctionedItem.getOwner())) {
						AuctionLocale.tell(click.player, "watchlist.cannot watch own");
						return;
					}
					if (AuctionHouse.getWatchlistManager().isWatching(click.player.getUniqueId(), auctionedItem.getId())) {
						AuctionHouse.getWatchlistManager().remove(click.player.getUniqueId(), auctionedItem.getId(), (err, ok) -> {
							if (ok) {
								AuctionLocale.tell(click.player, "watchlist.removed", "item",AuctionAPI.getInstance().getItemName(auctionedItem.getItem()));
							}
							draw();
						});
					} else {
						if (AuctionHouse.getWatchlistManager().getWatchlistCount(click.player.getUniqueId()) >= Settings.WATCHLIST_MAX_LISTINGS.getInt()) {
							AuctionLocale.tell(click.player, "watchlist.limit reached", "max",String.valueOf(Settings.WATCHLIST_MAX_LISTINGS.getInt()));
							return;
						}
						AuctionHouse.getWatchlistManager().add(click.player.getUniqueId(), auctionedItem.getId(), (err, ok) -> {
							if (ok) {
								AuctionLocale.tell(click.player, "watchlist.added", "item",AuctionAPI.getInstance().getItemName(auctionedItem.getItem()));
							}
							draw();
						});
					}
					return;
				}
			} catch (IllegalArgumentException ignored) {
				// Invalid click type in config
			}
		}

		// Item administration - allow DROP for admin remove item action
		if (click.clickType == ClickType.valueOf(Settings.CLICKS_REMOVE_ITEM.getString().toUpperCase())) {
			if (click.player.isOp() || click.player.hasPermission("auctionhouse.admin")) {
				cancelTask();
				click.manager.showGUI(click.player, new GUIAdminItem(this.auctionPlayer, auctionedItem));
			}
			return;
		}

		// Block DROP clicks (F key) on items to prevent spam/lag
		// Only allow if it's the configured remove item click type (handled above)
		if (click.clickType == ClickType.DROP) {
			return; // Silently ignore DROP clicks to prevent spam
		}

		// Rate limiting for item clicks to prevent spam and server lag
		if (Settings.asLong(Settings.MAIN_AH_ITEM_CLICK_COOLDOWN) > 0) {
			long currentTime = System.currentTimeMillis();
			if (this.lastItemClick != null && (currentTime - this.lastItemClick) < Settings.asLong(Settings.MAIN_AH_ITEM_CLICK_COOLDOWN)) {
				// Player is on cooldown, show message and return
				long remainingTime = Settings.asLong(Settings.MAIN_AH_ITEM_CLICK_COOLDOWN) - (currentTime - this.lastItemClick);
				AuctionLocale.tell(click.player, "general.cooldown.item click", "time", AuctionHouse.getCooldownManager().formatTime(remainingTime));
				return;
			}
			// Update last click time
			this.lastItemClick = currentTime;
		}

		if (!AuctionHouse.getAPI().isAuctionHouseOpen()) {
			AuctionLocale.tell(player, "general.auction house closed");
			return;
		}

		// bundle and shulker inspection
		if (click.clickType == ClickType.valueOf(Settings.CLICKS_INSPECT_CONTAINER.getString().toUpperCase())) {
			handleContainerInspect(click);
			return;
		}

		// Bin Listings
		if (!auctionedItem.isBidItem()) {

			if (click.clickType == ClickType.valueOf(Settings.CLICKS_NON_BID_ITEM_QTY_PURCHASE.getString().toUpperCase())) {
				if (!auctionedItem.isAllowPartialBuy()) {
					AuctionLocale.tell(click.player, "general.qtybuydisabled", "item_owner",auctionedItem.getOwnerName());
					return;
				}

				if (AuctionHouse.getBanManager().isStillBanned(click.player, BanType.EVERYTHING, BanType.BUYING)) return;
				handleNonBidItem(auctionedItem, click, true);
				return;
			}

			if (click.clickType == ClickType.valueOf(Settings.CLICKS_NON_BID_ITEM_ADD_TO_CART.getString().toUpperCase()) && Settings.CART_SYSTEM_ENABLED.getBoolean()) {
				// special case for request
				if (auctionedItem.isRequest()) {
					return;
				}

				if (AuctionHouse.getBanManager().isStillBanned(click.player, BanType.EVERYTHING, BanType.BUYING)) return;
				// add to cart
				if (AuctionHouse.getCartManager().isItemInCart(click.player.getUniqueId(), auctionedItem)) {
					AuctionLocale.tell(click.player, "general.cart.item already in cart");
					return;
				}

				AuctionHouse.getCartManager().addToCart(click.player.getUniqueId(), auctionedItem);
				AuctionLocale.tell(click.player, "general.cart.item added to cart");
				return;
			}

			if (click.clickType == ClickType.valueOf(Settings.CLICKS_NON_BID_ITEM_PURCHASE.getString().toUpperCase())) {
				// special case for request
				if (auctionedItem.isRequest()) {
					if (AuctionHouse.getBanManager().isStillBanned(click.player, BanType.EVERYTHING, BanType.REQUESTS)) return;

					if ((!Settings.USE_NAMES_FOR_CHECKS.getBoolean() && click.player.getUniqueId().equals(auctionedItem.getOwner())) || (Settings.USE_NAMES_FOR_CHECKS.getBoolean() && click.player.getName().equalsIgnoreCase(auctionedItem.getOwnerName())) && !Settings.OWNER_CAN_FULFILL_OWN_REQUEST.getBoolean()) {
						AuctionLocale.tell(click.player, "general.cantbuyown");
						return;
					}

//					if (Settings.USE_NAMES_FOR_CHECKS.getBoolean()) {
//						if (click.player.getName().equalsIgnoreCase(auctionedItem.getOwnerName()) && !Settings.OWNER_CAN_FULFILL_OWN_REQUEST.getBoolean()) {
//							AuctionLocale.tell(click.player, "general.cantbuyown");
//							return;
//						}
//					} else {
//						if (click.player.getUniqueId().equals(auctionedItem.getOwner()) && !Settings.OWNER_CAN_FULFILL_OWN_REQUEST.getBoolean()) {
//							AuctionLocale.tell(click.player, "general.cantbuyown");
//							return;
//						}
//					}

					cancelTask();
					if (Settings.ASK_FOR_PURCHASE_CONFIRMATION.getBoolean()) {
						click.manager.showGUI(click.player, new GUIConfirmPurchase(this.auctionPlayer, auctionedItem, false));
						AuctionHouse.getTransactionManager().addPrePurchase(click.player, auctionedItem.getId());
					} else {
						// Process purchase directly without confirmation
						int preAmount = auctionedItem.getItem().getAmount();
						boolean buyingSpecificQuantity = preAmount > 1;
						int purchaseQuantity = buyingSpecificQuantity ? preAmount : 0;
						double pricePerItem = buyingSpecificQuantity ? auctionedItem.getBasePrice() / preAmount : 0D;
						boolean success = GUIConfirmPurchase.processPurchase(click.player, this.auctionPlayer, auctionedItem, buyingSpecificQuantity, purchaseQuantity, pricePerItem);
						if (success) {
							SoundManager.getInstance().playSound(click.player, Settings.SOUNDS_PURCHASE_SUCCESS.getString());
						}
						click.manager.showGUI(click.player, new GUIAuctionHouse(this.auctionPlayer));
					}
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
			AuctionLocale.tell(click.player, "general.cantbuyown");
			return;
		}

		if (!buyingQuantity) {
			if (!auctionItem.playerHasSufficientMoney(click.player, auctionItem.getBasePrice())) {
				AuctionLocale.tell(click.player, "general.notenoughmoney");
				SoundManager.getInstance().playSound(click.player, Settings.SOUNDS_NOT_ENOUGH_MONEY.getString());
				return;
			}
		}


		if (buyingQuantity) {
			if (auctionItem.getBidStartingPrice() <= 0 || !Settings.ALLOW_USAGE_OF_BID_SYSTEM.getBoolean()) {
				if (!Settings.ALLOW_PURCHASE_OF_SPECIFIC_QUANTITIES.getBoolean()) return;
			}
		}

		cancelTask();
		if (Settings.ASK_FOR_PURCHASE_CONFIRMATION.getBoolean()) {
			click.manager.showGUI(click.player, new GUIConfirmPurchase(this.auctionPlayer, auctionItem, buyingQuantity));
			AuctionHouse.getTransactionManager().addPrePurchase(click.player, auctionItem.getId());
		} else {
			// Process purchase directly without confirmation
			int preAmount = auctionItem.getItem().getAmount();
			boolean buyingSpecificQuantity = buyingQuantity && preAmount > 1;
			if (preAmount == 1) {
				buyingSpecificQuantity = false;
			}
			int purchaseQuantity = buyingSpecificQuantity ? preAmount : 0;
			double pricePerItem = buyingSpecificQuantity ? auctionItem.getBasePrice() / preAmount : 0D;
			boolean success = GUIConfirmPurchase.processPurchase(click.player, this.auctionPlayer, auctionItem, buyingSpecificQuantity, purchaseQuantity, pricePerItem);
			if (success) {
				SoundManager.getInstance().playSound(click.player, Settings.SOUNDS_PURCHASE_SUCCESS.getString());
			}
			click.manager.showGUI(click.player, new GUIAuctionHouse(this.auctionPlayer));
		}
	}

	//======================================================================================================//
	private void handleBidItem(AuctionedItem auctionItem, GuiClickEvent click, boolean buyNow) {
		if (buyNow) {
			if (auctionItem.isBidItem()) {
				if (!Settings.ALLOW_USAGE_OF_BUY_NOW_SYSTEM.getBoolean()) return;
				if (auctionItem.getBasePrice() <= -1) {
					AuctionLocale.tell(click.player, "general.buynowdisabledonitem");
					return;
				}

				if (click.player.getUniqueId().equals(auctionItem.getOwner()) && !Settings.OWNER_CAN_PURCHASE_OWN_ITEM.getBoolean()) {
					AuctionLocale.tell(click.player, "general.cantbuyown");
					return;
				}

				cancelTask();
				if (Settings.ASK_FOR_PURCHASE_CONFIRMATION.getBoolean()) {
					click.manager.showGUI(click.player, new GUIConfirmPurchase(this.auctionPlayer, auctionItem, false));
					AuctionHouse.getTransactionManager().addPrePurchase(click.player, auctionItem.getId());
				} else {
					// Process purchase directly without confirmation
					int preAmount = auctionItem.getItem().getAmount();
					boolean buyingSpecificQuantity = preAmount > 1;
					if (preAmount == 1) {
						buyingSpecificQuantity = false;
					}
					int purchaseQuantity = buyingSpecificQuantity ? preAmount : 0;
					double pricePerItem = buyingSpecificQuantity ? auctionItem.getBasePrice() / preAmount : 0D;
					boolean success = GUIConfirmPurchase.processPurchase(click.player, this.auctionPlayer, auctionItem, buyingSpecificQuantity, purchaseQuantity, pricePerItem);
					if (success) {
						SoundManager.getInstance().playSound(click.player, Settings.SOUNDS_PURCHASE_SUCCESS.getString());
					}
					click.manager.showGUI(click.player, new GUIAuctionHouse(this.auctionPlayer));
				}
			}
			return;
		}

		if (click.player.getUniqueId().equals(auctionItem.getOwner()) && !Settings.OWNER_CAN_BID_OWN_ITEM.getBoolean() || Settings.BIDDING_TAKES_MONEY.getBoolean() && click.player.getUniqueId().equals(auctionItem.getOwner())) {
			AuctionLocale.tell(click.player, "general.cantbidonown");
			return;
		}

		if (click.player.getUniqueId().equals(auctionItem.getHighestBidder()) && !Settings.ALLOW_REPEAT_BIDS.getBoolean()) {
			AuctionLocale.tell(click.player, "general.alreadyhighestbidder");
			return;
		}

		cancelTask();

		if (Settings.FORCE_CUSTOM_BID_AMOUNT.getBoolean()) {
		// TitleInput automatically handles allowClose and inventory closing
		new TitleInput(AuctionHouse.getInstance(), player, AuctionLocale.msg(null, "titles.enter bid.title"), AuctionLocale.msg(null, "titles.enter bid.subtitle")) {

				@Override
				public void onExit(Player player) {
					AuctionHouse.getGuiManager().showGUI(player, new GUIAuctionHouse(GUIAuctionHouse.this.auctionPlayer));
				}

				@Override
				public boolean onResult(String string) {
					string = ChatColor.stripColor(string);

					if (!MathUtil.isDouble(string)) {
						AuctionLocale.tell(player, "general.notanumber", "value",string);
						return false;
					}

					double value = Double.parseDouble(string);

					if (value <= 0) {
						AuctionLocale.tell(click.player, "general.cannotbezero");
						return false;
					}

					if (value > Settings.MAX_AUCTION_INCREMENT_PRICE.getDouble()) {
						AuctionLocale.tell(click.player, "pricing.maxbidincrementprice", "price",Settings.MAX_AUCTION_INCREMENT_PRICE.getDouble());
						return false;
					}

					double newBiddingAmount = 0;
					if (Settings.USE_REALISTIC_BIDDING.getBoolean()) {
						if (value >= auctionItem.getCurrentPrice() + auctionItem.getBidIncrementPrice()) {
							newBiddingAmount = value;
						} else {
							if (Settings.BID_MUST_BE_HIGHER_THAN_PREVIOUS.getBoolean()) {
								click.manager.showGUI(click.player, new GUIAuctionHouse(GUIAuctionHouse.this.auctionPlayer));
								AuctionLocale.tell(click.player, "pricing.bidmusthigherthanprevious", "current_bid",AuctionHouse.getAPI().getFinalizedCurrencyNumber(auctionItem.getCurrentPrice(), auctionItem.getCurrency(), auctionItem.getCurrencyItem()));
								return true;
							}

							newBiddingAmount = auctionItem.getCurrentPrice() + value;
						}
					} else {
						newBiddingAmount = auctionItem.getCurrentPrice() + value;
					}

					newBiddingAmount = Settings.ROUND_ALL_PRICES.getBoolean() ? Math.round(newBiddingAmount) : newBiddingAmount;

					if (Settings.PLAYER_NEEDS_TOTAL_PRICE_TO_BID.getBoolean() && !auctionItem.playerHasSufficientMoney(click.player, newBiddingAmount)) {
						AuctionLocale.tell(click.player, "general.notenoughmoney");
						SoundManager.getInstance().playSound(click.player, Settings.SOUNDS_NOT_ENOUGH_MONEY.getString());
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
							AuctionLocale.tell(click.player, "general.notenoughmoney");
							SoundManager.getInstance().playSound(click.player, Settings.SOUNDS_NOT_ENOUGH_MONEY.getString());
							return true;
						}

						if (click.player.getUniqueId().equals(owner.getUniqueId()) || oldBidder.getUniqueId().equals(click.player.getUniqueId())) {
							return true;
						}

						if (!auctionItem.getHighestBidder().equals(auctionItem.getOwner())) {
							if (Settings.STORE_PAYMENTS_FOR_MANUAL_COLLECTION.getBoolean())
								AuctionHouse.getDataManager().insertAuctionPayment(new AuctionPayment(oldBidder.getUniqueId(), oldBidAmount, auctionItem.getItem(), AuctionLocale.msg(null, "general.prefix"), PaymentReason.BID_RETURNED, auctionItem.getCurrency(), auctionItem.getCurrencyItem()), null);
							else
								AuctionHouse.getCurrencyManager().deposit(oldBidder, oldBidAmount, auctionItem.getCurrency(), auctionItem.getCurrencyItem());

							String[] currencyParts = auctionItem.getCurrency().split("/");
							String balanceStr = AuctionHouse.getAPI().getFinalizedCurrencyNumber(AuctionHouse.getCurrencyManager().getBalance(oldBidder, currencyParts.length > 0 ? currencyParts[0] : "Vault", currencyParts.length > 1 ? currencyParts[1] : "Vault"), auctionItem.getCurrency(), auctionItem.getCurrencyItem());
							String priceStr = AuctionHouse.getAPI().getFinalizedCurrencyNumber(oldBidAmount, auctionItem.getCurrency(), auctionItem.getCurrencyItem());
							if (oldBidder.isOnline() && oldBidder.getPlayer() != null) {
								AuctionLocale.tell(oldBidder.getPlayer(), "pricing.moneyadd", "player_balance",balanceStr,"price",priceStr);
							} else {
								HashMap<String, String> placeholders = new HashMap<>();
								placeholders.put("player_balance", balanceStr);
								placeholders.put("price", priceStr);
								AuctionHouse.getNotificationManager().queue(oldBidder.getUniqueId(), "pricing.moneyadd", placeholders);
							}
						}

						AuctionHouse.getCurrencyManager().withdraw(click.player, newBiddingAmount, auctionItem.getCurrency(), auctionItem.getCurrencyItem());
						AuctionLocale.tell(click.player, "pricing.moneyremove", "player_balance",AuctionHouse.getAPI().getFinalizedCurrencyNumber(AuctionHouse.getCurrencyManager().getBalance(click.player, auctionItem.getCurrency().split("/")[0], auctionItem.getCurrency().split("/")[1]), auctionItem.getCurrency(), auctionItem.getCurrencyItem()),"price",AuctionHouse.getAPI().getFinalizedCurrencyNumber(newBiddingAmount, auctionItem.getCurrency(), auctionItem.getCurrencyItem()));

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

					if (oldBidder.isOnline() && oldBidder.getPlayer() != null) {
						AuctionLocale.tell(oldBidder.getPlayer(), "auction.outbid", "player",click.player.getName(),"player_displayname",AuctionAPI.getInstance().getDisplayName(click.player),"item",AuctionAPI.getInstance().getItemName(itemStack));
					} else {
						HashMap<String, String> outbidPlaceholders = new HashMap<>();
						outbidPlaceholders.put("player", click.player.getName());
						outbidPlaceholders.put("player_displayname", AuctionAPI.getInstance().getDisplayName(click.player));
						outbidPlaceholders.put("item", AuctionAPI.getInstance().getItemName(itemStack));
						AuctionHouse.getNotificationManager().queue(oldBidder.getUniqueId(), "auction.outbid", outbidPlaceholders);
					}

					if (owner.isOnline() && owner.getPlayer() != null) {
						AuctionLocale.tell(owner.getPlayer(), "auction.placedbid", "player",click.player.getName(),"player_displayname",AuctionAPI.getInstance().getDisplayName(click.player),"amount",AuctionHouse.getAPI().getFinalizedCurrencyNumber(auctionItem.getCurrentPrice(), auctionItem.getCurrency(), auctionItem.getCurrencyItem()),"item",AuctionAPI.getInstance().getItemName(itemStack));
					} else {
						HashMap<String, String> placedbidPlaceholders = new HashMap<>();
						placedbidPlaceholders.put("player", click.player.getName());
						placedbidPlaceholders.put("player_displayname", AuctionAPI.getInstance().getDisplayName(click.player));
						placedbidPlaceholders.put("amount", AuctionHouse.getAPI().getFinalizedCurrencyNumber(auctionItem.getCurrentPrice(), auctionItem.getCurrency(), auctionItem.getCurrencyItem()));
						placedbidPlaceholders.put("item", AuctionAPI.getInstance().getItemName(itemStack));
						AuctionHouse.getNotificationManager().queue(owner.getUniqueId(), "auction.placedbid", placedbidPlaceholders);
					}

					if (Settings.BROADCAST_AUCTION_BID.getBoolean()) {
						Bukkit.getOnlinePlayers().forEach(player -> AuctionLocale.tell(player, "auction.broadcast.bid", "player",click.player.getName(),"player_displayname",AuctionAPI.getInstance().getDisplayName(click.player),"amount",AuctionHouse.getAPI().getFinalizedCurrencyNumber(auctionItem.getCurrentPrice(), auctionItem.getCurrency(), auctionItem.getCurrencyItem()),"item",AuctionAPI.getInstance().getItemName(itemStack)));
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
				return AuctionHouse.getFilterManager().getFilterWhitelist(category).stream().anyMatch(item -> item != null && item.isSimilar(auctionItem.getItem()));
			else
				return AuctionHouse.getFilterManager().getFilterWhitelist(category).stream().anyMatch(item -> item != null && item.getType() == auctionItem.getItem().getType() && item.getDurability() == auctionItem.getItem().getDurability());
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
			SlotHelper.getButtonSlots(Settings.GUI_AUCTION_HOUSE_ITEMS_YOUR_AUCTIONS_SLOT.getString()).forEach(slot -> setButton(slot, QuickItem
					.of(Settings.GUI_AUCTION_HOUSE_ITEMS_YOUR_AUCTIONS_ITEM.getString())
					.name(AuctionLocale.msg(this.player, "gui.auction house.items.your auctions.name"))
					.lore(this.player, Replacer.replaceVariables(AuctionLocale.msgList(this.player, "gui.auction house.items.your auctions.lore"), "active_player_auctions", auctionPlayer.getItems(false).size(), "player_balance", AuctionHouse.getAPI().getNumberAsCurrency(AuctionHouse.getCurrencyManager().getBalance(auctionPlayer.getPlayer())))).make(), e -> {

				if (!AuctionHouse.getAPI().isAuctionHouseOpen()) {
					AuctionLocale.tell(player, "general.auction house closed");
					return;
				}

				cancelTask();
				e.manager.showGUI(e.player, new GUIActiveAuctions(this.auctionPlayer));
			}));
		}

		if (Settings.GUI_AUCTION_HOUSE_ITEMS_COLLECTION_BIN_ENABLED.getBoolean()) {
			SlotHelper.getButtonSlots(Settings.GUI_AUCTION_HOUSE_ITEMS_COLLECTION_BIN_SLOT.getString()).forEach(slot -> setButton(slot, QuickItem
					.of(Settings.GUI_AUCTION_HOUSE_ITEMS_COLLECTION_BIN_ITEM.getString())
					.name(AuctionLocale.msg(this.player, "gui.auction house.items.collection bin.name"))
					.lore(this.player, Replacer.replaceVariables(AuctionLocale.msgList(this.player, "gui.auction house.items.collection bin.lore"), "expired_player_auctions", auctionPlayer.getItems(true).size())).make(), e -> {

				cancelTask();
				e.manager.showGUI(e.player, new GUIExpiredItems(this, this.auctionPlayer));
			}));

		}

		if (Settings.WATCHLIST_ENABLED.getBoolean() && Settings.GUI_AUCTION_HOUSE_ITEMS_WATCHLIST_ENABLED.getBoolean()) {
			SlotHelper.getButtonSlots(Settings.GUI_AUCTION_HOUSE_ITEMS_WATCHLIST_SLOT.getString()).forEach(slot -> setButton(slot, QuickItem
					.of(Settings.GUI_AUCTION_HOUSE_ITEMS_WATCHLIST_ITEM.getString())
					.name(AuctionLocale.msg(this.player, "gui.auction house.items.watchlist.name"))
					.lore(this.player, Replacer.replaceVariables(AuctionLocale.msgList(this.player, "gui.auction house.items.watchlist.lore"), "watchlist_count", AuctionHouse.getWatchlistManager().getWatchlistCount(auctionPlayer.getUuid()))).make(), e -> {
				cancelTask();
				e.manager.showGUI(e.player, new GUIWatchedListings(this.auctionPlayer));
			}));
		}

		if (Settings.GUI_AUCTION_HOUSE_ITEMS_TRANSACTIONS_ENABLED.getBoolean()) {
			SlotHelper.getButtonSlots(Settings.GUI_AUCTION_HOUSE_ITEMS_TRANSACTIONS_SLOT.getString()).forEach(slot -> setButton(slot, QuickItem.of(Settings.GUI_AUCTION_HOUSE_ITEMS_TRANSACTIONS_ITEM.getString())
					.name(AuctionLocale.msg(this.player, "gui.auction house.items.transactions.name"))
					.lore(this.player, Replacer.replaceVariables(AuctionLocale.msgList(this.player, "gui.auction house.items.transactions.lore"),
							"total_items_bought", AuctionHouse.getTransactionManager().getTotalItemsBought(auctionPlayer.getPlayer().getUniqueId()),
							"total_items_sold", AuctionHouse.getTransactionManager().getTotalItemsSold(auctionPlayer.getPlayer().getUniqueId()))
					).make(), e -> {

				cancelTask();
				if (Settings.RESTRICT_ALL_TRANSACTIONS_TO_PERM.getBoolean() && !e.player.hasPermission("auctionhouse.transactions.viewall")) {
					e.manager.showGUI(e.player, new GUITransactionList(e.player, false));
				} else {
					e.manager.showGUI(e.player, new GUITransactionType(e.player));
				}
			}));
		}

		if (Settings.REPLACE_GUIDE_WITH_CART_BUTTON.getBoolean()) {
			SlotHelper.getButtonSlots(Settings.GUI_AUCTION_HOUSE_ITEMS_CART_SLOT.getString()).forEach(slot -> setButton(slot, QuickItem
					.of(Settings.GUI_AUCTION_HOUSE_ITEMS_CART_ITEM.getString())
					.name(AuctionLocale.msg(this.player, "gui.auction house.items.cart.name"))
					.lore(this.player, Replacer.replaceVariables(AuctionLocale.msgList(this.player, "gui.auction house.items.cart.lore"), "cart_item_count", AuctionHouse.getCartManager().getPlayerCart(this.player).getItemCount()))
					.make(), e -> {

				cancelTask();
				AuctionHouse.getGuiManager().showGUI(player, new GUICart(this, this.auctionPlayer));
			}));

		} else {
			if (Settings.GUI_AUCTION_HOUSE_ITEMS_GUIDE_ENABLED.getBoolean()) {
				SlotHelper.getButtonSlots(Settings.GUI_AUCTION_HOUSE_ITEMS_GUIDE_SLOT.getString()).forEach(slot -> setItem(slot, QuickItem
						.of(Settings.GUI_AUCTION_HOUSE_ITEMS_GUIDE_ITEM.getString())
						.name(AuctionLocale.msg(this.player, "gui.auction house.items.guide.name")).lore(this.player, AuctionLocale.msgList(this.player, "gui.auction house.items.guide.lore"))
						.make()));
			}
		}
	}

	//======================================================================================================//
	private void drawFixedButtons() {
		if (Settings.ENABLE_FILTER_SYSTEM.getBoolean())
			drawFilterButton();

		if (Settings.REPLACE_HOW_TO_SELL_WITH_LIST_BUTTON.getBoolean()) {
			if (Settings.GUI_AUCTION_HOUSE_ITEMS_LIST_ITEM_ENABLED.getBoolean()) {
				SlotHelper.getButtonSlots(Settings.GUI_AUCTION_HOUSE_ITEMS_LIST_ITEM_SLOT.getString()).forEach(slot -> setButton(slot, QuickItem
						.of(Settings.GUI_AUCTION_HOUSE_ITEMS_LIST_ITEM_ITEM.getString())
						.name(AuctionLocale.msg(this.player, "gui.auction house.items.list new item.name"))
						.lore(this.player, AuctionLocale.msgList(this.player, "gui.auction house.items.list new item.lore"))
						.make(), e -> {

					if (!AuctionHouse.getAPI().isAuctionHouseOpen()) {
						AuctionLocale.tell(player, "general.auction house closed");
						return;
					}

					if (AuctionHouse.getBanManager().isStillBanned(e.player, BanType.EVERYTHING, BanType.SELL)) return;

					// using this will ignore the "SELL_MENU_REQUIRES_USER_TO_HOLD_ITEM" setting
					if (FloodGateHook.isFloodGateUser(e.player)) {
						AuctionLocale.tell(e.player, "commands.no_permission");
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

				}));

			}
		} else {
			if (Settings.GUI_AUCTION_HOUSE_ITEMS_HOW_TO_SELL_ENABLED.getBoolean()) {
				SlotHelper.getButtonSlots(Settings.GUI_AUCTION_HOUSE_ITEMS_HOW_TO_SELL_SLOT.getString()).forEach(slot -> setItem(slot, QuickItem
						.of(Settings.GUI_AUCTION_HOUSE_ITEMS_HOW_TO_SELL_ITEM.getString())
						.name(AuctionLocale.msg(this.player, "gui.auction house.items.how to sell.name"))
						.lore(AuctionLocale.msgList(this.player, "gui.auction house.items.how to sell.lore"))
						.make()));
			}
		}

		if (Settings.GUI_REFRESH_BTN_ENABLED.getBoolean()) {
			SlotHelper.getButtonSlots(Settings.GUI_REFRESH_BTN_SLOT.getString()).forEach(slot -> setButton(slot, getRefreshButton(), ClickType.LEFT, e -> {
				// Early return cooldown check to prevent spam clicking and server lag
				if (Settings.asLong(Settings.MAIN_AH_REFRESH_BUTTON_COOLDOWN) > 0) {
					long currentTime = System.currentTimeMillis();
					if (this.lastRefreshClick != null && (currentTime - this.lastRefreshClick) < Settings.asLong(Settings.MAIN_AH_REFRESH_BUTTON_COOLDOWN)) {
						long remainingTime = Settings.asLong(Settings.MAIN_AH_REFRESH_BUTTON_COOLDOWN) - (currentTime - this.lastRefreshClick);
						AuctionLocale.tell(e.player, "general.cooldown.refresh", "time", AuctionHouse.getCooldownManager().formatTime(remainingTime));
						return; // Early return - don't process refresh
					}
					this.lastRefreshClick = currentTime;
				}

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
			}));
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
							.name(Replacer.replaceVariables(AuctionLocale.msg(this.player, "gui.auction house.items.filter menu.name"), "filter_category", auctionPlayer.getSelectedFilter().getTranslatedType(), "filter_auction_type", auctionPlayer.getSelectedSaleType().getTranslatedType(), "filter_sort_order", auctionPlayer.getAuctionSortType().getTranslatedType()))
							.lore(this.player, Replacer.replaceVariables(AuctionLocale.msgList(this.player, "gui.auction house.items.filter menu.lore"),
									"filter_category", auctionPlayer.getSelectedFilter().getTranslatedType(),
									"filter_auction_type", auctionPlayer.getSelectedSaleType().getTranslatedType(),
									"filter_sort_order", auctionPlayer.getAuctionSortType().getTranslatedType(),
									"filter_currency", auctionPlayer.getSelectedCurrencyFilter().getDisplayName()
							))
							.hideTags(true)
							.make() : QuickItem
					.of(materialToBeUsed)
					.name(Replacer.replaceVariables(AuctionLocale.msg(this.player, "gui.auction house.items.filter menu.name"),
							"filter_category", auctionPlayer.getSelectedFilter().getTranslatedType(),
							"filter_auction_type", auctionPlayer.getSelectedSaleType().getTranslatedType(),
							"filter_sort_order", auctionPlayer.getAuctionSortType().getTranslatedType()))
					.lore(this.player, Replacer.replaceVariables(AuctionLocale.msgList(this.player, "gui.auction house.items.filter menu.lore"),
							"filter_category", auctionPlayer.getSelectedFilter().getTranslatedType(),
							"filter_auction_type", auctionPlayer.getSelectedSaleType().getTranslatedType(),
							"filter_sort_order", auctionPlayer.getAuctionSortType().getTranslatedType(),
							"filter_currency", auctionPlayer.getSelectedCurrencyFilter().getDisplayName())).hideTags(true).make();

			if (Settings.GUI_AUCTION_HOUSE_ITEMS_FILTER_MENU_ENABLED.getBoolean()) {
				SlotHelper.getButtonSlots(Settings.GUI_AUCTION_HOUSE_ITEMS_FILTER_MENU_SLOT.getString()).forEach(slot -> setButton(slot, item, e -> {
					if (e.clickType == ClickType.valueOf(Settings.CLICKS_FILTER_CATEGORY.getString().toUpperCase()) && Settings.FILTER_CLICKS_CHANGE_CATEGORY_ENABLED.getBoolean()) {
						cancelTask();
						e.manager.showGUI(e.player, new GUIFilterSelection(this.auctionPlayer));
						return;
					}

					if (e.clickType == ClickType.valueOf(Settings.CLICKS_FILTER_RESET.getString().toUpperCase()) && Settings.FILTER_CLICKS_RESET_ENABLED.getBoolean()) {
						this.auctionPlayer.resetFilter();
						updatePlayerFilter(this.auctionPlayer);
						draw();
						return;
					}

					if (e.clickType == ClickType.valueOf(Settings.CLICKS_FILTER_SORT_SALE_TYPE.getString().toUpperCase()) && Settings.FILTER_CLICKS_SALE_TYPE_ENABLED.getBoolean()) {
						if (Settings.ALLOW_USAGE_OF_BID_SYSTEM.getBoolean()) {
							this.auctionPlayer.setSelectedSaleType(this.auctionPlayer.getSelectedSaleType().next());
							updatePlayerFilter(this.auctionPlayer);
							draw();
						}
						return;
					}

					if (e.clickType == ClickType.valueOf(Settings.CLICKS_FILTER_SORT_PRICE_OR_RECENT.getString().toUpperCase()) && Settings.FILTER_CLICKS_SORT_PRICE_RECENT_ENABLED.getBoolean()) {
						this.auctionPlayer.setAuctionSortType(this.auctionPlayer.getAuctionSortType().next());
						updatePlayerFilter(this.auctionPlayer);
						draw();
					}
				}));

			}
			return;
		}

		if (Settings.GUI_AUCTION_HOUSE_ITEMS_FILTER_ENABLED.getBoolean()) {
			SlotHelper.getButtonSlots(Settings.GUI_AUCTION_HOUSE_ITEMS_FILTER_SLOT.getString()).forEach(slot -> setButton(slot, QuickItem
					.of(this.auctionPlayer.getSelectedFilter().getFilterIcon())
					.name(AuctionLocale.msg(this.player, "gui.auction house.items.filter.name"))
					.lore(this.player, Replacer.replaceVariables(AuctionLocale.msgList(this.player, "gui.auction house.items.filter.lore"),
							"filter_category", auctionPlayer.getSelectedFilter().getTranslatedType(),
							"filter_auction_type", auctionPlayer.getSelectedSaleType().getTranslatedType(),
							"filter_currency", auctionPlayer.getSelectedCurrencyFilter().getDisplayName(),
							"filter_sort_order", auctionPlayer.getAuctionSortType().getTranslatedType()))
					.hideTags(true)
					.make(), e -> {

				if (e.clickType == ClickType.valueOf(Settings.CLICKS_FILTER_CATEGORY.getString().toUpperCase()) && Settings.FILTER_CLICKS_CHANGE_CATEGORY_ENABLED.getBoolean()) {
					this.auctionPlayer.setSelectedFilter(this.auctionPlayer.getSelectedFilter().next());
					updatePlayerFilter(this.auctionPlayer);
					draw();
					return;
				}

				if (e.clickType == ClickType.valueOf(Settings.CLICKS_FILTER_RESET.getString().toUpperCase()) && Settings.FILTER_CLICKS_RESET_ENABLED.getBoolean()) {
					this.auctionPlayer.resetFilter();
					updatePlayerFilter(this.auctionPlayer);
					draw();
					return;
				}

				if (e.clickType == ClickType.valueOf(Settings.CLICKS_FILTER_SORT_SALE_TYPE.getString().toUpperCase()) && Settings.FILTER_CLICKS_SALE_TYPE_ENABLED.getBoolean()) {
					if (Settings.ALLOW_USAGE_OF_BID_SYSTEM.getBoolean()) {
						this.auctionPlayer.setSelectedSaleType(this.auctionPlayer.getSelectedSaleType().next());
						updatePlayerFilter(this.auctionPlayer);
						draw();
					}
					return;
				}

				if (e.clickType == ClickType.valueOf(Settings.CLICKS_FILTER_SORT_PRICE_OR_RECENT.getString().toUpperCase()) && Settings.FILTER_CLICKS_SORT_PRICE_RECENT_ENABLED.getBoolean()) {
					this.auctionPlayer.setAuctionSortType(this.auctionPlayer.getAuctionSortType().next());
					updatePlayerFilter(this.auctionPlayer);
					draw();
					return;
				}

				if (e.clickType == ClickType.valueOf(Settings.CLICKS_FILTER_CURRENCY.getString().toUpperCase()) && Settings.FILTER_CLICKS_LISTING_CURRENCY_ENABLED.getBoolean()) {
					this.auctionPlayer.setSelectedCurrencyFilter(AuctionHouse.getCurrencyManager().getNext(this.auctionPlayer.getSelectedCurrencyFilter()));
					draw();
					return;
				}
			}));

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
		return Settings.GUI_AUCTION_HOUSE_FILL_SLOTS.getIntList();
	}

	@Override
	protected ItemStack getEmptyFillSlotItem() {
		return QuickItem.of(Settings.GUI_AUCTION_HOUSE_FILL_ITEM.getString()).make();
	}
}
