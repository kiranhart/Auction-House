package ca.tweetzy.auctionhouse.guis.confirmation;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.auction.AuctionedItem;
import ca.tweetzy.auctionhouse.auction.enums.AuctionStackType;
import ca.tweetzy.auctionhouse.guis.AbstractPlaceholderGui;
import ca.tweetzy.auctionhouse.guis.GUIAuctionHouse;
import ca.tweetzy.auctionhouse.helpers.MaterialCategorizer;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.core.utils.items.TItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.UUID;

/**
 * The current file has been created by Kiran Hart
 * Date Created: March 17 2021
 * Time Created: 11:18 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUIConfirmListing extends AbstractPlaceholderGui {

	private final Player player;
	private final ItemStack originalItem;
	private final ItemStack itemToSell;
	private final int allowedTime;
	private final Double basePrice;
	private final Double startingBid;
	private final Double bidIncrement;
	private final boolean isBiddingItem;
	private final boolean isBundle;
	private final boolean requiresHandRemove;
	private final boolean isInfinite;

	private final AuctionedItem auctionedItem;

	public GUIConfirmListing(Player player, ItemStack originalItem, ItemStack itemToSell, int allowedTime, double basePrice, double startingBid, double bidIncrement, boolean isBiddingItem, boolean bundle, boolean requiresHandRemove, boolean isInfinite) {
		super(player);
		this.player = player;
		this.originalItem = originalItem;
		this.itemToSell = itemToSell;
		this.allowedTime = allowedTime;
		this.basePrice = basePrice;
		this.startingBid = startingBid;
		this.bidIncrement = bidIncrement;
		this.isBiddingItem = isBiddingItem;
		this.isBundle = bundle;
		this.requiresHandRemove = requiresHandRemove;
		this.isInfinite = isInfinite;

		this.auctionedItem = new AuctionedItem(
				UUID.randomUUID(),
				player.getUniqueId(),
				player.getUniqueId(),
				player.getName(),
				player.getName(),
				MaterialCategorizer.getMaterialCategory(this.originalItem),
				this.itemToSell.clone(),
				/* buy now price */ Settings.ALLOW_USAGE_OF_BUY_NOW_SYSTEM.getBoolean() ? this.basePrice : -1,
				/* start bid price */ this.isBiddingItem ? this.startingBid : 0,
				/* bid inc price */ this.isBiddingItem ? this.bidIncrement != null ? this.bidIncrement : 1 : 0,
				/* current price */ this.isBiddingItem ? this.startingBid : this.basePrice <= -1 ? this.startingBid : this.basePrice,
				this.isBiddingItem,
				false,
				System.currentTimeMillis() + (this.allowedTime * 1000L)
		);

		// todo add back if dupe is brought back to life
//		setOnOpen(open -> PlayerUtils.takeActiveItem(open.player, CompatibleHand.MAIN_HAND, originalItem.getAmount()));
//		setOnClose(close -> close.player.getInventory().addItem(originalItem));

		setTitle(TextUtils.formatText(Settings.GUI_CONFIRM_LISTING_TITLE.getString()));
		setAcceptsItems(false);
		setRows(1);
		draw();
	}

	private void placeAuctionItem() {
		setItem(0, 4, this.auctionedItem.getDisplayStack(AuctionStackType.LISTING_PREVIEW));
	}

	private void draw() {
		setItems(0, 3, new TItemBuilder(Objects.requireNonNull(Settings.GUI_CONFIRM_LISTING_YES_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_CONFIRM_LISTING_YES_NAME.getString()).setLore(Settings.GUI_CONFIRM_LISTING_YES_LORE.getStringList()).toItemStack());
		placeAuctionItem();
		setItems(5, 8, new TItemBuilder(Objects.requireNonNull(Settings.GUI_CONFIRM_LISTING_NO_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_CONFIRM_LISTING_NO_NAME.getString()).setLore(Settings.GUI_CONFIRM_LISTING_NO_LORE.getStringList()).toItemStack());

		setActionForRange(5, 8, ClickType.LEFT, e -> {
			e.gui.exit();
			e.player.getInventory().addItem(originalItem);
		});

		setActionForRange(0, 3, ClickType.LEFT, e -> {
			AuctionHouse.getInstance().getAuctionPlayerManager().removeItemFromSellHolding(e.player.getUniqueId());
			AuctionHouse.getInstance().getAuctionPlayerManager().removeFromUsingSellGUI(e.player.getUniqueId());

			AuctionAPI.getInstance().listAuction(
					this.player,
					this.originalItem,
					this.itemToSell,
					this.allowedTime,
					/* buy now price */ Settings.ALLOW_USAGE_OF_BUY_NOW_SYSTEM.getBoolean() ? this.basePrice : -1,
					/* start bid price */ this.isBiddingItem ? this.startingBid : 0,
					/* bid inc price */ this.isBiddingItem ? this.bidIncrement != null ? this.bidIncrement : 1 : 0,
					/* current price */ this.isBiddingItem ? this.startingBid : this.basePrice <= -1 ? this.startingBid : this.basePrice,
					this.isBiddingItem,
					this.isBundle,
					this.requiresHandRemove,
					this.isInfinite,
					false
			);


			if (Settings.OPEN_MAIN_AUCTION_HOUSE_AFTER_MENU_LIST.getBoolean()) {
				e.manager.showGUI(e.player, new GUIAuctionHouse(AuctionHouse.getInstance().getAuctionPlayerManager().getPlayer(e.player.getUniqueId())));
			} else {
				e.gui.exit();
			}
		});
	}
}
