package ca.tweetzy.auctionhouse.guis.selector;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.guis.AuctionPagedGUI;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.gui.Gui;
import ca.tweetzy.flight.gui.events.GuiClickEvent;
import ca.tweetzy.flight.gui.helper.InventoryBorder;
import ca.tweetzy.flight.gui.helper.InventorySafeMaterials;
import ca.tweetzy.flight.utils.ChatUtil;
import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.flight.utils.Filterer;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.flight.utils.input.TitleInput;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class GUIMaterialPicker extends AuctionPagedGUI<ItemStack> {

	private final Gui parent;
	private final String searchQuery;
	private final Consumer<ItemStack> selected;

	public GUIMaterialPicker(final Gui parent, Player player, final String searchQuery, @NonNull final Consumer<ItemStack> selected) {
		super(parent, player, Settings.GUI_MATERIAL_PICKER_TITLE.getString(), 6, new ArrayList<>());
		setAcceptsItems(true);
		setAllowClose(false);
		this.searchQuery = searchQuery;
		this.selected = selected;
		this.parent = parent;
		draw();
	}

	@Override
	protected void prePopulate() {
		// Combine mapping and filtering into a single stream operation
		this.items = InventorySafeMaterials.get().stream()
				.map(CompMaterial::parseItem)
				.filter(mat -> {
					if (this.searchQuery == null) {
						return true;
					}
					return Filterer.searchByItemInfo(this.searchQuery, mat);
				})
				.collect(Collectors.toList());
	}

	@Override
	protected void drawFixed() {

		setButton(5, 4, buildSearchButton(), click -> {
			// TitleInput automatically handles allowClose and inventory closing
			new TitleInput(
					AuctionHouse.getInstance(),
					click.player,
					Common.colorize(AuctionHouse.getInstance().getLocale().getMessage("titles.material search.title").getMessage()),
					Common.colorize(AuctionHouse.getInstance().getLocale().getMessage("titles.material search.subtitle").getMessage())
			) {
				@Override
				public boolean onResult(String string) {
					if (string.isEmpty()) return false;
					click.manager.showGUI(click.player, new GUIMaterialPicker(GUIMaterialPicker.this.parent, click.player, string, GUIMaterialPicker.this.selected));
					return true;
				}

				@Override
				public void onExit(Player player) {
					click.manager.showGUI(click.player, GUIMaterialPicker.this);
				}
			};
		});

		if (this.searchQuery != null)
			setButton(5, 7, buildResetButton(), click -> click.manager.showGUI(click.player, new GUIMaterialPicker(this.parent, click.player, this.searchQuery, this.selected)));

		applyBackExit(this.parent);
	}

	@Override
	protected ItemStack makeDisplayItem(ItemStack item) {
		if (item == null) {
			return new ItemStack(CompMaterial.AIR.get());
		}
		return QuickItem.of(item)
				.name("&e&l" + ChatUtil.capitalizeFully(item.getType()))
				.lore(this.player, Settings.GUI_MATERIAL_PICKER_ITEMS_MATERIAL_LORE.getStringList())
				.make();
	}

	protected ItemStack buildSearchButton() {
		return QuickItem.of(CompMaterial.OAK_SIGN)
				.name(Settings.GUI_MATERIAL_PICKER_ITEMS_SEARCH_NAME.getString())
				.lore(this.player, Settings.GUI_MATERIAL_PICKER_ITEMS_SEARCH_LORE.getStringList())
				.make();
	}

	protected ItemStack buildResetButton() {
		return QuickItem
				.of(CompMaterial.LAVA_BUCKET)
				.name(Settings.GUI_MATERIAL_PICKER_ITEMS_RESET_NAME.getString())
				.lore(this.player, Settings.GUI_MATERIAL_PICKER_ITEMS_RESET_LORE.getStringList())
				.make();
	}

	@Override
	protected void onClick(ItemStack object, GuiClickEvent clickEvent) {
		this.selected.accept(object);
	}

	@Override
	protected List<Integer> fillSlots() {
		return InventoryBorder.getInsideBorders(5);
	}

}