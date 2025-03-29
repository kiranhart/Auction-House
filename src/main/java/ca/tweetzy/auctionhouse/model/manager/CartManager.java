package ca.tweetzy.auctionhouse.model.manager;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.api.auction.Cart;
import ca.tweetzy.auctionhouse.api.manager.KeyValueManager;
import ca.tweetzy.auctionhouse.auction.AuctionedItem;
import ca.tweetzy.auctionhouse.auction.enums.AuctionSaleType;
import ca.tweetzy.auctionhouse.events.AuctionEndEvent;
import ca.tweetzy.auctionhouse.exception.ItemNotFoundException;
import ca.tweetzy.auctionhouse.guis.core.GUIAuctionHouse;
import ca.tweetzy.auctionhouse.impl.AuctionCart;
import ca.tweetzy.auctionhouse.managers.SoundManager;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.flight.gui.events.GuiClickEvent;
import ca.tweetzy.core.utils.PlayerUtils;
import ca.tweetzy.flight.nbtapi.NBT;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public final class CartManager extends KeyValueManager<UUID, Cart> {

	public CartManager() {
		super("Cart");
	}

	public Cart getPlayerCart(@NonNull final Player player) {
		return getPlayerCart(player.getUniqueId());
	}

	public Cart getPlayerCart(@NonNull final UUID uuid) {
		Cart cart = this.managerContent.getOrDefault(uuid, null);

		if (cart == null) {
			cart = new AuctionCart();
			this.managerContent.put(uuid, cart);
		}

		// do cleanup here
		for (AuctionedItem item : cart.getItems()) {
			long timeRemaining = (item.getExpiresAt() - System.currentTimeMillis()) / 1000;
			if (item == null || AuctionHouse.getAuctionItemManager().getItem(item.getId()) == null || item.isExpired() || timeRemaining <= 0)
				cart.removeItem(item);
		}

		return cart;
	}

	public Cart checkout(@NonNull final Player player) {
		final Cart cart = getPlayerCart(player);
		if (cart.getItems().isEmpty()) return cart;

		for (AuctionedItem item : cart.getItems()) {
			try {

				// if the item is in the garbage then just don't continue
				if (AuctionHouse.getAuctionItemManager().getGarbageBin().containsKey(item.getId())) {
					cart.removeItem(item);
					continue;
				}

				final AuctionedItem located = AuctionHouse.getAuctionItemManager().getItem(item.getId());

				if (located == null || located.isExpired()) {
					cart.removeItem(item);
					continue;
				}

				double tax = Settings.TAX_ENABLED.getBoolean() ? (Settings.TAX_SALES_TAX_BUY_NOW_PERCENTAGE.getDouble() / 100) * located.getBasePrice() : 0D;

				if (!located.playerHasSufficientMoney(player, located.getBasePrice() + (Settings.TAX_CHARGE_SALES_TAX_TO_BUYER.getBoolean() ? tax : 0D))) {
					continue;
				}

				if (!Settings.ALLOW_PURCHASE_IF_INVENTORY_FULL.getBoolean() && player.getInventory().firstEmpty() == -1) {
					AuctionHouse.getInstance().getLocale().getMessage("general.noroom").sendPrefixedMessage(player);
					break;
				}

				AuctionEndEvent auctionEndEvent = new AuctionEndEvent(Bukkit.getOfflinePlayer(located.getOwner()), player, located, AuctionSaleType.WITHOUT_BIDDING_SYSTEM, tax, false);
				Bukkit.getServer().getPluginManager().callEvent(auctionEndEvent);
				if (auctionEndEvent.isCancelled()) continue;

				transferFunds(located, player, located.getBasePrice());
				if (!located.isInfinite())
					AuctionHouse.getAuctionItemManager().sendToGarbage(located);

				cart.removeItem(item);
				ItemStack foundItem = located.getItem().clone();

				NBT.modify(foundItem, nbt -> {
					nbt.removeKey("AuctionDupeTracking");
				});

				PlayerUtils.giveItem(player, foundItem);
				sendMessages(player, located, false, 0, foundItem.getAmount());

				if (Settings.BROADCAST_AUCTION_SALE.getBoolean()) {
					final OfflinePlayer seller = Bukkit.getOfflinePlayer(located.getOwner());

					Bukkit.getOnlinePlayers().forEach(players -> AuctionHouse.getInstance().getLocale().getMessage("auction.broadcast.sold")
							.processPlaceholder("player", player.getName())
							.processPlaceholder("player_displayname", AuctionAPI.getInstance().getDisplayName(player))
							.processPlaceholder("seller", located.getOwnerName())
							.processPlaceholder("seller_displayname", AuctionAPI.getInstance().getDisplayName(seller))
							.processPlaceholder("amount", located.getItem().getAmount())
							.processPlaceholder("item", AuctionAPI.getInstance().getItemName(located.getItem()))
							.processPlaceholder("price",
									located.getBasePrice() > located.getCurrentPrice() ? located.getFormattedBasePrice() : located.getFormattedCurrentPrice()
							)
							.sendPrefixedMessage(players));
				}

				AuctionHouse.getTransactionManager().getPrePurchasePlayers(located.getId()).forEach(players -> {
					AuctionHouse.getTransactionManager().removeAllRelatedPlayers(located.getId());
					players.closeInventory();
				});

			} catch (ItemNotFoundException itemNotFoundException) {
				AuctionHouse.getInstance().getLogger().info("Tried to checkout item that was bought, or does not exist");
			}
		}


		return cart;
	}

	private void transferFunds(AuctionedItem auctionedItem, Player from, double amount) {
		double tax = Settings.TAX_ENABLED.getBoolean() ? (Settings.TAX_SALES_TAX_BUY_NOW_PERCENTAGE.getDouble() / 100) * amount : 0D;

		AuctionAPI.getInstance().withdrawBalance(from, Settings.ROUND_ALL_PRICES.getBoolean() ? Math.round(Settings.TAX_CHARGE_SALES_TAX_TO_BUYER.getBoolean() ? amount + tax : amount) : Settings.TAX_CHARGE_SALES_TAX_TO_BUYER.getBoolean() ? amount + tax : amount, auctionedItem);
		AuctionAPI.getInstance().depositBalance(Bukkit.getOfflinePlayer(auctionedItem.getOwner()), Settings.ROUND_ALL_PRICES.getBoolean() ? Math.round(Settings.TAX_CHARGE_SALES_TAX_TO_BUYER.getBoolean() ? amount : amount - tax) : Settings.TAX_CHARGE_SALES_TAX_TO_BUYER.getBoolean() ? amount : amount - tax, auctionedItem.getItem(), from, auctionedItem);
	}

	private void sendMessages(Player player, AuctionedItem located, boolean overwritePrice, double price, int qtyOverride) {
		double totalPrice = overwritePrice ? price : located.getBasePrice();
		double tax = Settings.TAX_ENABLED.getBoolean() ? (Settings.TAX_SALES_TAX_BUY_NOW_PERCENTAGE.getDouble() / 100) * totalPrice : 0D;

		AuctionHouse.getInstance().getLocale().getMessage("pricing.moneyremove")
				.processPlaceholder("player_balance", AuctionHouse.getAPI().getFinalizedCurrencyNumber(AuctionHouse.getCurrencyManager().getBalance(player, located.getCurrency().split("/")[0], located.getCurrency().split("/")[1]), located.getCurrency(), located.getCurrencyItem()))
				.processPlaceholder("price", AuctionHouse.getAPI().getFinalizedCurrencyNumber(Settings.TAX_CHARGE_SALES_TAX_TO_BUYER.getBoolean() ? totalPrice - tax : totalPrice, located.getCurrency(), located.getCurrencyItem()))
				.sendPrefixedMessage(player);

		AuctionHouse.getInstance().getLocale().getMessage("general.bought_item")
				.processPlaceholder("amount", qtyOverride).processPlaceholder("item", AuctionAPI.getInstance().getItemName(located.getItem()))
				.processPlaceholder("price", AuctionHouse.getAPI().getFinalizedCurrencyNumber(Settings.TAX_CHARGE_SALES_TAX_TO_BUYER.getBoolean() ? totalPrice - tax : totalPrice, located.getCurrency(), located.getCurrencyItem()))
				.sendPrefixedMessage(player);

		if (Bukkit.getOfflinePlayer(located.getOwner()).isOnline()) {
			AuctionHouse.getInstance().getLocale().getMessage("auction.itemsold")
					.processPlaceholder("item", AuctionAPI.getInstance().getItemName(located.getItem()))
					.processPlaceholder("amount", qtyOverride)
					.processPlaceholder("price", AuctionHouse.getAPI().getFinalizedCurrencyNumber(Settings.TAX_CHARGE_SALES_TAX_TO_BUYER.getBoolean() ? totalPrice : totalPrice - tax, located.getCurrency(), located.getCurrencyItem()))
					.processPlaceholder("buyer_name", player.getName())
					.sendPrefixedMessage(Bukkit.getOfflinePlayer(located.getOwner()).getPlayer());

			AuctionHouse.getInstance().getLocale().getMessage("pricing.moneyadd")
					.processPlaceholder("player_balance", AuctionHouse.getAPI().getFinalizedCurrencyNumber(AuctionHouse.getCurrencyManager().getBalance(Bukkit.getOfflinePlayer(located.getOwner()), located.getCurrency().split("/")[0], located.getCurrency().split("/")[1]), located.getCurrency(), located.getCurrencyItem()))
					.processPlaceholder("price", AuctionHouse.getAPI().getFinalizedCurrencyNumber(Settings.TAX_CHARGE_SALES_TAX_TO_BUYER.getBoolean() ? totalPrice : totalPrice - tax, located.getCurrency(), located.getCurrencyItem()))
					.sendPrefixedMessage(Bukkit.getOfflinePlayer(located.getOwner()).getPlayer());
		}
	}

	public boolean isItemInCart(@NonNull final UUID uuid, AuctionedItem auctionedItem) {
		final Cart cart = getPlayerCart(uuid);
		return cart.getItems().stream().anyMatch(item -> item.getId().equals(auctionedItem.getId()));
	}

	public boolean addToCart(@NonNull final UUID uuid, AuctionedItem auctionedItem) {
		if (auctionedItem.isBidItem() || auctionedItem.isRequest()) return false;

		final Cart cart = getPlayerCart(uuid);

		cart.addItem(auctionedItem);
		this.managerContent.put(uuid, cart);
		return true;
	}

	public boolean removeFromCart(@NonNull final UUID uuid, AuctionedItem auctionedItem) {
		final Cart cart = getPlayerCart(uuid);
		if (cart.getItemCount() == 0) return false;

		cart.removeItem(auctionedItem);
		return true;
	}

	@Override
	public void load() {
	}
}
