package ca.tweetzy.auctionhouse.guis;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.auction.AuctionedItem;
import ca.tweetzy.auctionhouse.guis.confirmation.GUIConfirmPurchase;
import ca.tweetzy.auctionhouse.helpers.ConfigurationItemHelper;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.gui.Gui;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.core.utils.items.TItemBuilder;
import ca.tweetzy.core.utils.nms.NBTEditor;
import org.bukkit.block.ShulkerBox;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: April 14 2021
 * Time Created: 12:28 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUIContainerInspect extends Gui {

	private final int[] fillSlots = {0, 1, 2, 3, 4, 5, 6, 7, 8, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 46, 47, 48, 50, 51, 52, 53};
	private final ItemStack container;
	private List<ItemStack> items;

	private AuctionPlayer auctionPlayer;
	private AuctionedItem auctionItem;
	private boolean buyingSpecificQuantity;
	private boolean fromPurchaseGUI;

	/**
	 * Used to inspect a shulker box from it's item stack.
	 *
	 * @param container is the shulker box
	 */
	public GUIContainerInspect(ItemStack container) {
		this.container = container;
		this.fromPurchaseGUI = false;
		setTitle(TextUtils.formatText(Settings.GUI_INSPECT_TITLE.getString()));
		setDefaultItem(Settings.GUI_INSPECT_BG_ITEM.getMaterial().parseItem());
		setUseLockedCells(false);
		setAcceptsItems(false);
		setAllowDrops(false);
		setRows(6);

		if (NBTEditor.contains(this.container, "AuctionBundleItem")) {
			List<ItemStack> items = new ArrayList<>();

			for (int i = 0; i < NBTEditor.getInt(this.container, "AuctionBundleItem"); i++) {
				items.add(AuctionAPI.getInstance().deserializeItem(NBTEditor.getByteArray(this.container, "AuctionBundleItem-" + i)));
			}

			this.items = items;
		}

		if (this.container.getType().name().contains("SHULKER_BOX")) {
			BlockStateMeta meta = (BlockStateMeta) this.container.getItemMeta();
			ShulkerBox skulkerBox = (ShulkerBox) meta.getBlockState();
			this.items = Arrays.asList(skulkerBox.getInventory().getContents());
		}

		draw();
		setOnClose(close -> close.manager.showGUI(close.player, new GUIAuctionHouse(AuctionHouse.getInstance().getAuctionPlayerManager().getPlayer(close.player.getUniqueId()))));
	}

	public GUIContainerInspect(ItemStack container, AuctionPlayer auctionPlayer, AuctionedItem auctionItem, boolean buyingSpecificQuantity) {
		this(container);
		this.auctionPlayer = auctionPlayer;
		this.auctionItem = auctionItem;
		this.buyingSpecificQuantity = buyingSpecificQuantity;
		this.fromPurchaseGUI = true;

		// Overwrite the default close, since they are accessing the inspection from the purchase screen
		setOnClose(close -> {
			AuctionHouse.getInstance().getTransactionManager().addPrePurchase(close.player, auctionItem.getId());
			close.manager.showGUI(close.player, new GUIConfirmPurchase(this.auctionPlayer, this.auctionItem, this.buyingSpecificQuantity));
		});
	}

	private void draw() {
		reset();
		pages = (int) Math.max(1, Math.ceil(this.items.size() / (double) 27L));

		for (int i : fillSlots) setItem(i, getDefaultItem());

		setPrevPage(5, 3, new TItemBuilder(Objects.requireNonNull(Settings.GUI_BACK_BTN_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_BACK_BTN_NAME.getString()).setLore(Settings.GUI_BACK_BTN_LORE.getStringList()).toItemStack());
		setButton(5, 4, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_CLOSE_BTN_ITEM.getString(), Settings.GUI_CLOSE_BTN_NAME.getString(), Settings.GUI_CLOSE_BTN_LORE.getStringList(), null), e -> {
			if (fromPurchaseGUI) {
				AuctionHouse.getInstance().getTransactionManager().addPrePurchase(e.player, auctionItem.getId());
				e.manager.showGUI(e.player, new GUIConfirmPurchase(this.auctionPlayer, this.auctionItem, this.buyingSpecificQuantity));
			} else {
				e.manager.showGUI(e.player, new GUIAuctionHouse(AuctionHouse.getInstance().getAuctionPlayerManager().getPlayer(e.player.getUniqueId())));
			}
		});
		setNextPage(5, 5, new TItemBuilder(Objects.requireNonNull(Settings.GUI_NEXT_BTN_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_NEXT_BTN_NAME.getString()).setLore(Settings.GUI_NEXT_BTN_LORE.getStringList()).toItemStack());
		setOnPage(e -> draw());

		int slot = 9;
		List<ItemStack> data = this.items.stream().skip((page - 1) * 27L).limit(27L).collect(Collectors.toList());
		for (ItemStack item : data) {
			setItem(slot++, item);
		}
	}
}
