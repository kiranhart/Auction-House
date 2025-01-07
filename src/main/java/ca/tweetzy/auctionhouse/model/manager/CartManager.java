package ca.tweetzy.auctionhouse.model.manager;

import ca.tweetzy.auctionhouse.api.auction.Cart;
import ca.tweetzy.auctionhouse.api.manager.KeyValueManager;
import ca.tweetzy.auctionhouse.auction.AuctionedItem;
import ca.tweetzy.auctionhouse.impl.AuctionCart;
import lombok.NonNull;
import org.bukkit.entity.Player;

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

		return cart;
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
