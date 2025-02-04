package ca.tweetzy.auctionhouse.guis.core;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.auction.Cart;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.auction.AuctionedItem;
import ca.tweetzy.auctionhouse.auction.enums.AuctionStackType;
import ca.tweetzy.auctionhouse.guis.AuctionUpdatingPagedGUI;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.gui.Gui;
import ca.tweetzy.core.gui.events.GuiClickEvent;
import ca.tweetzy.flight.utils.QuickItem;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class GUICart extends AuctionUpdatingPagedGUI<AuctionedItem> {

	private final AuctionPlayer auctionPlayer;

	public GUICart(Gui parent, @NonNull final AuctionPlayer auctionPlayer) {
		super(parent, auctionPlayer.getPlayer(), Settings.GUI_CART_TITLE.getString(), Settings.GUI_CART_ROWS.getInt(), 20 * Settings.TICK_UPDATE_GUI_TIME.getInt(), new ArrayList<>());
		this.auctionPlayer = auctionPlayer;

		if (!Bukkit.getOfflinePlayer(auctionPlayer.getUuid()).isOnline()) return;

		setOnOpen(open -> {
			if (Settings.AUTO_REFRESH_AUCTION_PAGES.getBoolean()) startTask();
		});

		applyClose();
		draw();
	}

	@Override
	protected void prePopulate() {
		this.items = new ArrayList<>(AuctionHouse.getCartManager().getPlayerCart(this.player).getItems());
	}

	@Override
	protected void drawFixed() {
		if (this.parent == null) {
			setButton(getBackExitButtonSlot(), getExitButton(), click -> click.gui.close());
		} else {
			setButton(getBackExitButtonSlot(), getBackButton(), click -> {
				cancelTask();
				click.manager.showGUI(click.player, new GUIAuctionHouse(this.auctionPlayer));
			});
		}

		// checkout button
		setButton(Settings.GUI_CART_ITEMS_CHECKOUT_SLOT.getInt(), QuickItem
				.of(Settings.GUI_CART_ITEMS_CHECKOUT_ITEM.getString())
				.name(Settings.GUI_CART_ITEMS_CHECKOUT_NAME.getString())
				.lore(Settings.GUI_CART_ITEMS_CHECKOUT_LORE.getStringList())
				.make(), click -> {

			AuctionHouse.getCartManager().checkout(click.player);
			draw();
		});

	}

	@Override
	protected ItemStack makeDisplayItem(AuctionedItem auctionedItem) {
		return auctionedItem.getDisplayStack(this.player, AuctionStackType.CART);
	}

	@Override
	protected void onClick(AuctionedItem auctionedItem, GuiClickEvent click) {
		AuctionHouse.getCartManager().getPlayerCart(click.player).removeItem(auctionedItem);
		draw();
	}

	@Override
	protected List<Integer> fillSlots() {
		return Settings.GUI_CART_FILL_SLOTS.getIntegerList();
	}
}
