package ca.tweetzy.auctionhouse.guis;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.api.events.AuctionBidEvent;
import ca.tweetzy.auctionhouse.api.hook.FloodGateHook;
import ca.tweetzy.auctionhouse.api.hook.PlaceholderAPIHook;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.auction.AuctionedItem;
import ca.tweetzy.auctionhouse.auction.enums.AuctionItemCategory;
import ca.tweetzy.auctionhouse.auction.enums.AuctionSaleType;
import ca.tweetzy.auctionhouse.auction.enums.AuctionSortType;
import ca.tweetzy.auctionhouse.auction.enums.AuctionStackType;
import ca.tweetzy.auctionhouse.guis.admin.GUIAdminItem;
import ca.tweetzy.auctionhouse.guis.confirmation.GUIConfirmBid;
import ca.tweetzy.auctionhouse.guis.confirmation.GUIConfirmPurchase;
import ca.tweetzy.auctionhouse.guis.filter.GUIFilterSelection;
import ca.tweetzy.auctionhouse.guis.transaction.GUITransactionType;
import ca.tweetzy.auctionhouse.helpers.ConfigurationItemHelper;
import ca.tweetzy.auctionhouse.managers.SoundManager;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.compatibility.ServerVersion;
import ca.tweetzy.core.compatibility.XMaterial;
import ca.tweetzy.core.gui.Gui;
import ca.tweetzy.core.gui.events.GuiClickEvent;
import ca.tweetzy.core.hooks.EconomyManager;
import ca.tweetzy.core.input.PlayerChatInput;
import ca.tweetzy.core.utils.NumberUtils;
import ca.tweetzy.core.utils.TextUtils;
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
	private List<AuctionedItem> items;

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

		setOnClose(close -> {
			this.items.clear();

			if (Settings.AUTO_REFRESH_AUCTION_PAGES.getBoolean()) {
				cleanup();
			}
		});
	}

	public GUIAuctionHouse(AuctionPlayer auctionPlayer, String phrase) {
		this(auctionPlayer);
		this.searchPhrase = phrase;
	}

	public void draw() {
		try {
			reset();
			drawFixedButtons();
			drawItems();
		} catch (Exception e) {
			AuctionHouse.getInstance().getLogger().warning("Something stupid is happening during the draw process (Main Menu)");
		}
	}

	private void drawItems() {
		AuctionHouse.newChain().asyncFirst(() -> {
			this.items = new ArrayList<>();

			for (Map.Entry<UUID, AuctionedItem> entry : AuctionHouse.getInstance().getAuctionItemManager().getItems().entrySet()) {
				AuctionedItem auctionItem = entry.getValue();
				if (!auctionItem.isExpired() && !AuctionHouse.getInstance().getAuctionItemManager().getGarbageBin().containsKey(auctionItem.getId())) {
					if (Settings.PER_WORLD_ITEMS.getBoolean()) {
						if (auctionItem.getListedWorld() == null || this.auctionPlayer.getPlayer().getWorld().getName().equals(auctionItem.getListedWorld())) {
							this.items.add(auctionItem);
						}
					} else {
						this.items.add(auctionItem);
					}
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
				this.items = this.items.stream().filter(AuctionedItem::isBidItem).collect(Collectors.toList());
			}

			if (this.auctionPlayer.getSelectedSaleType() == AuctionSaleType.WITHOUT_BIDDING_SYSTEM) {
				this.items = this.items.stream().filter(item -> !item.isBidItem()).collect(Collectors.toList());
			}

			if (this.auctionPlayer.getAuctionSortType() == AuctionSortType.PRICE) {
				this.items = this.items.stream().sorted(Comparator.comparingDouble(AuctionedItem::getCurrentPrice).reversed()).collect(Collectors.toList());
			}

			if (this.auctionPlayer.getAuctionSortType() == AuctionSortType.RECENT) {
				this.items = this.items.stream().sorted(Comparator.comparingLong(AuctionedItem::getExpiresAt).reversed()).collect(Collectors.toList());
			}

			this.items = this.items.stream().sorted(Comparator.comparing(AuctionedItem::isInfinite).reversed()).collect(Collectors.toList());

			return this.items.stream().skip((page - 1) * 45L).limit(45L).collect(Collectors.toList());
		}).asyncLast((data) -> {
			pages = (int) Math.max(1, Math.ceil(this.items.size() / (double) 45L));
			drawVariableButtons();
			drawPaginationButtons();
			placeItems(data);
		}).execute();
	}

	private boolean checkFilterCriteria(AuctionedItem auctionItem, AuctionItemCategory category) {
		return auctionItem.getCategory() == category ||
				AuctionHouse.getInstance().getFilterManager().getFilterWhitelist(category).stream().anyMatch(item -> item.isSimilar(auctionItem.getItem()));
	}

	private boolean checkSearchCriteria(String phrase, AuctionedItem item) {
		ItemStack stack = item.getItem();
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
	private void handleNonBidItem(AuctionedItem auctionItem, GuiClickEvent e, boolean buyingQuantity) {
		if (e.player.getUniqueId().equals(auctionItem.getOwner()) && !Settings.OWNER_CAN_PURCHASE_OWN_ITEM.getBoolean()) {
			AuctionHouse.getInstance().getLocale().getMessage("general.cantbuyown").sendPrefixedMessage(e.player);
			return;
		}

		if (!EconomyManager.hasBalance(e.player, auctionItem.getBasePrice())) {
			AuctionHouse.getInstance().getLocale().getMessage("general.notenoughmoney").sendPrefixedMessage(e.player);
			return;
		}

		if (buyingQuantity) {
			if (auctionItem.getBidStartingPrice() <= 0 || !Settings.ALLOW_USAGE_OF_BID_SYSTEM.getBoolean()) {
				if (!Settings.ALLOW_PURCHASE_OF_SPECIFIC_QUANTITIES.getBoolean()) return;
			}
		}

		cleanup();
		e.manager.showGUI(e.player, new GUIConfirmPurchase(this.auctionPlayer, auctionItem, buyingQuantity));
		AuctionHouse.getInstance().getTransactionManager().addPrePurchase(e.player, auctionItem.getId());
	}

	private void handleBidItem(AuctionedItem auctionItem, GuiClickEvent e, boolean buyNow) {
		if (buyNow) {
			if (auctionItem.isBidItem()) {
				if (!Settings.ALLOW_USAGE_OF_BUY_NOW_SYSTEM.getBoolean()) return;
				if (auctionItem.getBasePrice() <= -1) {
					AuctionHouse.getInstance().getLocale().getMessage("general.buynowdisabledonitem").sendPrefixedMessage(e.player);
					return;
				}

				cleanup();
				e.manager.showGUI(e.player, new GUIConfirmPurchase(this.auctionPlayer, auctionItem, false));
				AuctionHouse.getInstance().getTransactionManager().addPrePurchase(e.player, auctionItem.getId());
			}
			return;
		}

		if (e.player.getUniqueId().equals(auctionItem.getOwner()) && !Settings.OWNER_CAN_BID_OWN_ITEM.getBoolean()) {
			AuctionHouse.getInstance().getLocale().getMessage("general.cantbidonown").sendPrefixedMessage(e.player);
			return;
		}

		cleanup();

		if (Settings.FORCE_CUSTOM_BID_AMOUNT.getBoolean()) {
			e.gui.exit();
			PlayerChatInput.PlayerChatInputBuilder<Double> builder = new PlayerChatInput.PlayerChatInputBuilder<>(AuctionHouse.getInstance(), e.player);
			builder.isValidInput((p, str) -> NumberUtils.isDouble(str) && Double.parseDouble(str) >= auctionItem.getBidIncrementPrice());
			builder.sendValueMessage(TextUtils.formatText(AuctionHouse.getInstance().getLocale().getMessage("prompts.enter bid amount").processPlaceholder("current_bid", AuctionAPI.getInstance().formatNumber(auctionItem.getCurrentPrice())).getMessage()));
			builder.invalidInputMessage(TextUtils.formatText(AuctionHouse.getInstance().getLocale().getMessage("prompts.enter valid bid amount").getMessage()));
			builder.toCancel("cancel");
			builder.onCancel(p -> e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer)));
			builder.setValue((p, value) -> Double.parseDouble(value));
			builder.onFinish((p, value) -> {
				if (value > Settings.MAX_AUCTION_INCREMENT_PRICE.getDouble()) {
					AuctionHouse.getInstance().getLocale().getMessage("pricing.maxbidincrementprice").processPlaceholder("price", Settings.MAX_AUCTION_INCREMENT_PRICE.getDouble()).sendPrefixedMessage(e.player);
					return;
				}

				double newBiddingAmount = 0;
				if (Settings.USE_REALISTIC_BIDDING.getBoolean()) {
					if (value > auctionItem.getCurrentPrice()) {
						newBiddingAmount = value;
					} else {
						if (Settings.BID_MUST_BE_HIGHER_THAN_PREVIOUS.getBoolean()) {
							e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
							AuctionHouse.getInstance().getLocale().getMessage("pricing.bidmusthigherthanprevious").processPlaceholder("current_bid", AuctionAPI.getInstance().formatNumber(auctionItem.getCurrentPrice())).sendPrefixedMessage(e.player);
							return;
						}

						newBiddingAmount = auctionItem.getCurrentPrice() + value;
					}
				} else {
					newBiddingAmount = auctionItem.getCurrentPrice() + value;
				}

				if (Settings.PLAYER_NEEDS_TOTAL_PRICE_TO_BID.getBoolean() && !EconomyManager.hasBalance(e.player, newBiddingAmount)) {
					AuctionHouse.getInstance().getLocale().getMessage("general.notenoughmoney").sendPrefixedMessage(e.player);
					return;
				}

				if (Settings.ASK_FOR_BID_CONFIRMATION.getBoolean()) {
					e.manager.showGUI(e.player, new GUIConfirmBid(this.auctionPlayer, auctionItem, newBiddingAmount));
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
				if (auctionItem.getBasePrice() != -1 && Settings.SYNC_BASE_PRICE_TO_HIGHEST_PRICE.getBoolean() && auctionItem.getCurrentPrice() > auctionItem.getBasePrice()) {
					auctionItem.setBasePrice(Settings.ROUND_ALL_PRICES.getBoolean() ? Math.round(auctionItem.getCurrentPrice()) : auctionItem.getCurrentPrice());
				}

				if (Settings.INCREASE_TIME_ON_BID.getBoolean()) {
					auctionItem.setExpiresAt(auctionItem.getExpiresAt() + 1000L * Settings.TIME_TO_INCREASE_BY_ON_BID.getInt());
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

				if (Settings.BROADCAST_AUCTION_BID.getBoolean()) {
					Bukkit.getOnlinePlayers().forEach(player -> AuctionHouse.getInstance().getLocale().getMessage("auction.broadcast.bid")
							.processPlaceholder("player", e.player.getName())
							.processPlaceholder("player_displayname", AuctionAPI.getInstance().getDisplayName(e.player))
							.processPlaceholder("amount", AuctionAPI.getInstance().formatNumber(auctionItem.getCurrentPrice()))
							.processPlaceholder("item", AuctionAPI.getInstance().getItemName(itemStack))
							.sendPrefixedMessage(player));
				}

				e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
			});

			PlayerChatInput<Double> input = builder.build();
			input.start();
			return;
		}

		e.manager.showGUI(e.player, new GUIBid(this.auctionPlayer, auctionItem));
	}

	private void handleItemRemove(AuctionedItem auctionItem, GuiClickEvent e) {
		if (e.player.isOp() || e.player.hasPermission("auctionhouse.admin")) {
			cleanup();
			e.manager.showGUI(e.player, new GUIAdminItem(this.auctionPlayer, auctionItem));
		}
	}

	private void handleContainerInspect(GuiClickEvent e) {
		ItemStack clicked = e.clickedItem;

		if (NBTEditor.contains(clicked, "AuctionBundleItem")) {
			cleanup();
			e.manager.showGUI(e.player, new GUIContainerInspect(e.clickedItem));
			return;
		}

		if (!ServerVersion.isServerVersionAtLeast(ServerVersion.V1_11)) return;
		if (e.player.isOp() || e.player.hasPermission("auctionhouse.admin") || e.player.hasPermission("auctionhouse.inspectshulker")) {
			if (!(clicked.getItemMeta() instanceof BlockStateMeta)) return;

			BlockStateMeta meta = (BlockStateMeta) clicked.getItemMeta();
			if (!(meta.getBlockState() instanceof ShulkerBox)) return;
			cleanup();
			e.manager.showGUI(e.player, new GUIContainerInspect(e.clickedItem));
		}
	}

	private void placeItems(List<AuctionedItem> data) {
		int slot = 0;
		for (AuctionedItem auctionItem : data) {
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
				if (!auctionItem.isBidItem()) {
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
	private void drawVariableButtons() {
		if (Settings.GUI_AUCTION_HOUSE_ITEMS_YOUR_AUCTIONS_ENABLED.getBoolean()) {
			setButton(Settings.GUI_AUCTION_HOUSE_ITEMS_YOUR_AUCTIONS_SLOT.getInt(), ConfigurationItemHelper.createConfigurationItem(Settings.GUI_AUCTION_HOUSE_ITEMS_YOUR_AUCTIONS_ITEM.getString(), Settings.GUI_AUCTION_HOUSE_ITEMS_YOUR_AUCTIONS_NAME.getString(), PlaceholderAPIHook.PAPIReplacer.tryReplace(this.auctionPlayer.getPlayer(), Settings.GUI_AUCTION_HOUSE_ITEMS_YOUR_AUCTIONS_LORE.getStringList()), new HashMap<String, Object>() {{
				put("%active_player_auctions%", auctionPlayer.getItems(false).size());
				put("%player_balance%", Settings.USE_SHORT_NUMBERS_ON_PLAYER_BALANCE.getBoolean() ? AuctionAPI.getInstance().getFriendlyNumber(EconomyManager.getBalance(auctionPlayer.getPlayer())) : AuctionAPI.getInstance().formatNumber(EconomyManager.getBalance(auctionPlayer.getPlayer())));
			}}), e -> {
				cleanup();
				e.manager.showGUI(e.player, new GUIActiveAuctions(this.auctionPlayer));
			});
		}

		if (Settings.GUI_AUCTION_HOUSE_ITEMS_COLLECTION_BIN_ENABLED.getBoolean()) {
			setButton(Settings.GUI_AUCTION_HOUSE_ITEMS_COLLECTION_BIN_SLOT.getInt(), ConfigurationItemHelper.createConfigurationItem(Settings.GUI_AUCTION_HOUSE_ITEMS_COLLECTION_BIN_ITEM.getString(), Settings.GUI_AUCTION_HOUSE_ITEMS_COLLECTION_BIN_NAME.getString(), Settings.GUI_AUCTION_HOUSE_ITEMS_COLLECTION_BIN_LORE.getStringList(), new HashMap<String, Object>() {{
				put("%expired_player_auctions%", auctionPlayer.getItems(true).size());
			}}), e -> {
				cleanup();
				e.manager.showGUI(e.player, new GUIExpiredItems(this.auctionPlayer));
			});
		}

		if (Settings.GUI_AUCTION_HOUSE_ITEMS_TRANSACTIONS_ENABLED.getBoolean()) {
			setButton(Settings.GUI_AUCTION_HOUSE_ITEMS_TRANSACTIONS_SLOT.getInt(), ConfigurationItemHelper.createConfigurationItem(Settings.GUI_AUCTION_HOUSE_ITEMS_TRANSACTIONS_ITEM.getString(), Settings.GUI_AUCTION_HOUSE_ITEMS_TRANSACTIONS_NAME.getString(), Settings.GUI_AUCTION_HOUSE_ITEMS_TRANSACTIONS_LORE.getStringList(), new HashMap<String, Object>() {{
				put("%total_items_bought%", AuctionHouse.getInstance().getTransactionManager().getTotalItemsBought(auctionPlayer.getPlayer().getUniqueId()));
				put("%total_items_sold%", AuctionHouse.getInstance().getTransactionManager().getTotalItemsSold(auctionPlayer.getPlayer().getUniqueId()));
			}}), e -> e.manager.showGUI(e.player, new GUITransactionType()));
		}
	}

	private void drawPaginationButtons() {
		setItem(Settings.GUI_NEXT_BTN_SLOT.getInt(), XMaterial.AIR.parseItem());
		setItem(Settings.GUI_BACK_BTN_SLOT.getInt(), XMaterial.AIR.parseItem());
		setPrevPage(Settings.GUI_BACK_BTN_SLOT.getInt(), new TItemBuilder(Objects.requireNonNull(Settings.GUI_BACK_BTN_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_BACK_BTN_NAME.getString()).setLore(Settings.GUI_BACK_BTN_LORE.getStringList()).toItemStack());
		setNextPage(Settings.GUI_NEXT_BTN_SLOT.getInt(), new TItemBuilder(Objects.requireNonNull(Settings.GUI_NEXT_BTN_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_NEXT_BTN_NAME.getString()).setLore(Settings.GUI_NEXT_BTN_LORE.getStringList()).toItemStack());

		setOnPage(e -> {
			draw();
			SoundManager.getInstance().playSound(this.auctionPlayer.getPlayer(), Settings.SOUNDS_NAVIGATE_GUI_PAGES.getString(), 1.0F, 1.0F);
		});
	}

	private void drawFixedButtons() {
		drawFilterButton();

		if (Settings.REPLACE_HOW_TO_SELL_WITH_LIST_BUTTON.getBoolean()) {
			if (Settings.GUI_AUCTION_HOUSE_ITEMS_LIST_ITEM_ENABLED.getBoolean()) {
				setButton(Settings.GUI_AUCTION_HOUSE_ITEMS_LIST_ITEM_SLOT.getInt(), ConfigurationItemHelper.createConfigurationItem(Settings.GUI_AUCTION_HOUSE_ITEMS_LIST_ITEM_ITEM.getString(), Settings.GUI_AUCTION_HOUSE_ITEMS_LIST_ITEM_NAME.getString(), Settings.GUI_AUCTION_HOUSE_ITEMS_LIST_ITEM_LORE.getStringList(), null), e -> {
					// using this will ignore the "SELL_MENU_REQUIRES_USER_TO_HOLD_ITEM" setting
					if (FloodGateHook.isFloodGateUser(e.player)) {
						AuctionHouse.getInstance().getLocale().getMessage("commands.no_permission").sendPrefixedMessage(e.player);
						return;
					}

					AuctionHouse.getInstance().getGuiManager().showGUI(e.player, new GUISellItem(auctionPlayer, XMaterial.AIR.parseItem()));
					AuctionHouse.getInstance().getAuctionPlayerManager().addItemToSellHolding(e.player.getUniqueId(), XMaterial.AIR.parseItem());
				});
			}
		} else {
			if (Settings.GUI_AUCTION_HOUSE_ITEMS_HOW_TO_SELL_ENABLED.getBoolean()) {
				setButton(Settings.GUI_AUCTION_HOUSE_ITEMS_HOW_TO_SELL_SLOT.getInt(), ConfigurationItemHelper.createConfigurationItem(Settings.GUI_AUCTION_HOUSE_ITEMS_HOW_TO_SELL_ITEM.getString(), Settings.GUI_AUCTION_HOUSE_ITEMS_HOW_TO_SELL_NAME.getString(), Settings.GUI_AUCTION_HOUSE_ITEMS_HOW_TO_SELL_LORE.getStringList(), null), null);
			}
		}

		if (Settings.GUI_AUCTION_HOUSE_ITEMS_GUIDE_ENABLED.getBoolean()) {
			setButton(Settings.GUI_AUCTION_HOUSE_ITEMS_GUIDE_SLOT.getInt(), ConfigurationItemHelper.createConfigurationItem(Settings.GUI_AUCTION_HOUSE_ITEMS_GUIDE_ITEM.getString(), Settings.GUI_AUCTION_HOUSE_ITEMS_GUIDE_NAME.getString(), Settings.GUI_AUCTION_HOUSE_ITEMS_GUIDE_LORE.getStringList(), null), null);
		}

		if (Settings.GUI_REFRESH_BTN_ENABLED.getBoolean()) {
			setButton(Settings.GUI_REFRESH_BTN_SLOT.getInt(), new TItemBuilder(Objects.requireNonNull(Settings.GUI_REFRESH_BTN_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_REFRESH_BTN_NAME.getString()).setLore(Settings.GUI_REFRESH_BTN_LORE.getStringList()).toItemStack(), e -> {
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

			if (Settings.GUI_AUCTION_HOUSE_ITEMS_FILTER_MENU_ENABLED.getBoolean()) {
				setButton(Settings.GUI_AUCTION_HOUSE_ITEMS_FILTER_MENU_SLOT.getInt(), item, e -> {
					switch (e.clickType) {
						case LEFT:
							e.manager.showGUI(e.player, new GUIFilterSelection(this.auctionPlayer));
							break;
						case DROP:
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
			}
			return;
		}

		if (Settings.GUI_AUCTION_HOUSE_ITEMS_FILTER_ENABLED.getBoolean()) {
			setButton(Settings.GUI_AUCTION_HOUSE_ITEMS_FILTER_SLOT.getInt(), ConfigurationItemHelper.createConfigurationItem(Settings.GUI_AUCTION_HOUSE_ITEMS_FILTER_ITEM.getString(), Settings.GUI_AUCTION_HOUSE_ITEMS_FILTER_NAME.getString(), Settings.GUI_AUCTION_HOUSE_ITEMS_FILTER_LORE.getStringList(), new HashMap<String, Object>() {{
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
	}

	/*
	====================== AUTO REFRESH ======================
	 */
	private void makeMess() {
		task = Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(AuctionHouse.getInstance(), this::drawItems, 0L, (long) 20 * Settings.TICK_UPDATE_GUI_TIME.getInt());
	}

	private void cleanup() {
		if (task != null) {
			task.cancel();
		}
	}

}
