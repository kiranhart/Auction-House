package ca.tweetzy.auctionhouse.guis;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.helpers.ConfigurationItemHelper;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.compatibility.XMaterial;
import ca.tweetzy.core.gui.Gui;
import ca.tweetzy.core.utils.PlayerUtils;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: November 22 2021
 * Time Created: 1:11 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public final class GUIBundleCreation extends Gui {

	public GUIBundleCreation(AuctionPlayer player, int allowedTime, boolean buyNowAllow, boolean isBiddingItem, Double buyNowPrice, Double startingBid, Double bidIncrement) {
		setTitle(Settings.GUI_CREATE_BUNDLE_TITLE.getString());
		setRows(6);
		setAllowDrops(false);
		setAllowShiftClick(true);
		setAcceptsItems(true);
		setUnlockedRange(0, 44);

		setOnClose(close -> {
			for (int i = 0; i < 44; i++) {
				final ItemStack item = getItem(i);
				if (item == null || item.getType() == XMaterial.AIR.parseMaterial()) continue;
				PlayerUtils.giveItem(player.getPlayer(), item);
			}
		});

		setButton(49, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_CREATE_BUNDLE_CONFIRM_ITEM.getString(), Settings.GUI_CREATE_BUNDLE_CONFIRM_NAME.getString(), Settings.GUI_CREATE_BUNDLE_CONFIRM_LORE.getStringList(), new HashMap<>()), e -> {
			ItemStack firstItem = null;
			List<ItemStack> validItems = new ArrayList<>();

			for (int i = 0; i < 44; i++) {
				final ItemStack item = getItem(i);
				if (item == null || item.getType() == XMaterial.AIR.parseMaterial()) continue;

				if (Settings.MAKE_BLOCKED_ITEMS_A_WHITELIST.getBoolean()) {
					if (!Settings.BLOCKED_ITEMS.getStringList().contains(item.getType().name())) {
						AuctionHouse.getInstance().getLocale().getMessage("general.blockeditem").processPlaceholder("item", item.getType().name()).sendPrefixedMessage(e.player);
						continue;
					}
				} else {
					if (Settings.BLOCKED_ITEMS.getStringList().contains(item.getType().name())) {
						AuctionHouse.getInstance().getLocale().getMessage("general.blockeditem").processPlaceholder("item", item.getType().name()).sendPrefixedMessage(e.player);
						continue;
					}
				}

				boolean blocked = false;

				String itemName = ChatColor.stripColor(AuctionAPI.getInstance().getItemName(item).toLowerCase());
				List<String> itemLore = AuctionAPI.getInstance().getItemLore(item).stream().map(line -> ChatColor.stripColor(line.toLowerCase())).collect(Collectors.toList());

				// Check for blocked names and lore
				for (String s : Settings.BLOCKED_ITEM_NAMES.getStringList()) {
					if (AuctionAPI.getInstance().match(s, itemName)) {
						AuctionHouse.getInstance().getLocale().getMessage("general.blockedname").sendPrefixedMessage(e.player);
						blocked = true;
					}
				}

				if (!itemLore.isEmpty() && !blocked) {
					for (String s : Settings.BLOCKED_ITEM_LORES.getStringList()) {
						for (String line : itemLore) {
							if (AuctionAPI.getInstance().match(s, line)) {
								AuctionHouse.getInstance().getLocale().getMessage("general.blockedlore").sendPrefixedMessage(e.player);
								blocked = true;
							}
						}
					}
				}

				if (blocked) continue;

				if (firstItem == null)
					firstItem = item;

				validItems.add(item);
			}

			// are they even allowed to sell more items
			if (player.isAtSellLimit()) {
				AuctionHouse.getInstance().getLocale().getMessage("general.sellinglimit").sendPrefixedMessage(e.player);
				return;
			}

			if (validItems.size() == 0) return;
			final ItemStack bundle = AuctionAPI.getInstance().createBundledItem(firstItem, validItems.toArray(new ItemStack[0]));

			AuctionAPI.getInstance().listAuction(
					player.getPlayer(),
					firstItem,
					bundle,
					allowedTime,
					/* buy now price */ buyNowAllow ? buyNowPrice : -1,
					/* start bid price */ isBiddingItem ? startingBid : !buyNowAllow ? buyNowPrice : 0,
					/* bid inc price */ isBiddingItem ? bidIncrement != null ? bidIncrement : Settings.MIN_AUCTION_INCREMENT_PRICE.getDouble() : 0,
					/* current price */ isBiddingItem ? startingBid : buyNowPrice <= -1 ? startingBid : buyNowPrice,
					isBiddingItem || !buyNowAllow,
					true,
					false
			);

			e.gui.exit();
			if (Settings.OPEN_MAIN_AUCTION_HOUSE_AFTER_MENU_LIST.getBoolean()) {
				e.manager.showGUI(e.player, new GUIAuctionHouse(player));
			}
		});
	}
}
