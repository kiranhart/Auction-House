package ca.tweetzy.auctionhouse.guis;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.auction.AuctionedItem;
import ca.tweetzy.auctionhouse.helpers.ConfigurationItemHelper;
import ca.tweetzy.auctionhouse.managers.SoundManager;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.gui.Gui;
import ca.tweetzy.core.utils.PlayerUtils;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.core.utils.items.TItemBuilder;
import org.bukkit.event.inventory.ClickType;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: March 15 2021
 * Time Created: 3:19 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUIExpiredItems extends Gui {

	final AuctionPlayer auctionPlayer;

	private List<AuctionedItem> items;
	private Long lastClicked = null;

	public GUIExpiredItems(AuctionPlayer auctionPlayer) {
		this.auctionPlayer = auctionPlayer;
		setTitle(TextUtils.formatText(Settings.GUI_EXPIRED_AUCTIONS_TITLE.getString()));
		setRows(6);
		setAcceptsItems(false);
		draw();
	}

	public GUIExpiredItems(AuctionPlayer auctionPlayer, Long lastClicked) {
		this(auctionPlayer);
		this.lastClicked = lastClicked;
	}

	private void draw() {
		reset();

		setButton(5, 0, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_CLOSE_BTN_ITEM.getString(), Settings.GUI_CLOSE_BTN_NAME.getString(), Settings.GUI_CLOSE_BTN_LORE.getStringList(), null), e -> e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer)));

		AuctionHouse.newChain().asyncFirst(() -> {
			this.items = this.auctionPlayer.getItems(true);

			if (Settings.PER_WORLD_ITEMS.getBoolean()) {
				this.items = this.items.stream().filter(item -> item.getListedWorld() == null || this.auctionPlayer.getPlayer().getWorld().getName().equals(item.getListedWorld())).collect(Collectors.toList());
			}

			return this.items.stream().sorted(Comparator.comparingLong(AuctionedItem::getExpiresAt).reversed()).skip((page - 1) * 45L).limit(45).collect(Collectors.toList());
		}).asyncLast((data) -> {
			pages = (int) Math.max(1, Math.ceil(this.auctionPlayer.getItems(true).size() / (double) 45));
			setPrevPage(5, 3, new TItemBuilder(Objects.requireNonNull(Settings.GUI_BACK_BTN_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_BACK_BTN_NAME.getString()).setLore(Settings.GUI_BACK_BTN_LORE.getStringList()).toItemStack());
			setButton(5, 4, new TItemBuilder(Objects.requireNonNull(Settings.GUI_REFRESH_BTN_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_REFRESH_BTN_NAME.getString()).setLore(Settings.GUI_REFRESH_BTN_LORE.getStringList()).toItemStack(), e -> draw());
			setNextPage(5, 5, new TItemBuilder(Objects.requireNonNull(Settings.GUI_NEXT_BTN_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_NEXT_BTN_NAME.getString()).setLore(Settings.GUI_NEXT_BTN_LORE.getStringList()).toItemStack());
			setOnPage(e -> {
				draw();
				SoundManager.getInstance().playSound(this.auctionPlayer.getPlayer(), Settings.SOUNDS_NAVIGATE_GUI_PAGES.getString(), 1.0F, 1.0F);
			});


			setButton(5, 1, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_EXPIRED_AUCTIONS_ITEM.getString(), Settings.GUI_EXPIRED_AUCTIONS_NAME.getString(), Settings.GUI_EXPIRED_AUCTIONS_LORE.getStringList(), null), e -> {

				for (AuctionedItem auctionItem : data) {
					if (e.player.getInventory().firstEmpty() == -1) {
						AuctionHouse.getInstance().getLocale().getMessage("general.noroomclaim").sendPrefixedMessage(e.player);
						break;
					}

					PlayerUtils.giveItem(e.player, auctionItem.getItem());
					AuctionHouse.getInstance().getAuctionItemManager().sendToGarbage(auctionItem);
				}

				e.manager.showGUI(e.player, new GUIExpiredItems(this.auctionPlayer, this.lastClicked));
			});

			int slot = 0;
			for (AuctionedItem auctionItem : data) {
				setButton(slot++, auctionItem.getItem(), ClickType.LEFT, e -> {
					if (!Settings.ALLOW_INDIVIDUAL_ITEM_CLAIM.getBoolean()) return;

					if (e.player.getInventory().firstEmpty() == -1) {
						AuctionHouse.getInstance().getLocale().getMessage("general.noroomclaim").sendPrefixedMessage(e.player);
						return;
					}

					if (this.lastClicked == null) {
						this.lastClicked = System.currentTimeMillis() + Settings.CLAIM_MS_DELAY.getInt();
					} else if (this.lastClicked > System.currentTimeMillis()) {
						return;
					} else {
						this.lastClicked = System.currentTimeMillis() + Settings.CLAIM_MS_DELAY.getInt();
					}

					PlayerUtils.giveItem(e.player, auctionItem.getItem());
					AuctionHouse.getInstance().getAuctionItemManager().sendToGarbage(auctionItem);
					e.manager.showGUI(e.player, new GUIExpiredItems(this.auctionPlayer, this.lastClicked));
				});
			}

		}).execute();
	}
}
