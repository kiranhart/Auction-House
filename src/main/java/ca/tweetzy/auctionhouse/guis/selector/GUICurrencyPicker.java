package ca.tweetzy.auctionhouse.guis.selector;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.currency.AbstractCurrency;
import ca.tweetzy.auctionhouse.api.currency.IconableCurrency;
import ca.tweetzy.auctionhouse.guis.AuctionPagedGUI;
import ca.tweetzy.auctionhouse.impl.currency.ItemCurrency;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.gui.Gui;
import ca.tweetzy.flight.gui.events.GuiClickEvent;
import ca.tweetzy.flight.gui.helper.InventoryBorder;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.flight.utils.Replacer;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public final class GUICurrencyPicker extends AuctionPagedGUI<AbstractCurrency> {

	private final BiConsumer<AbstractCurrency, ItemStack> selectedCurrency;

	public GUICurrencyPicker(final Gui parent, @NonNull final Player player, @NonNull final BiConsumer<AbstractCurrency, ItemStack> selectedCurrency) {
		super(parent, player, Settings.GUI_CURRENCY_PICKER_TITLE.getString(), 6, AuctionHouse.getCurrencyManager().getPermissionAllowed(player));
		this.selectedCurrency = selectedCurrency;
		setAcceptsItems(true);
		setAllowClose(false);
		draw();
	}

	@Override
	protected void drawFixed() {
		applyBackExit();

		// custom item
		if (Settings.CURRENCY_ALLOW_CUSTOM.getBoolean())
			setButton(getRows() - 1, 4, QuickItem
					.of(CompMaterial.HOPPER)
					.name(Settings.GUI_CURRENCY_PICKER_ITEMS_CUSTOM_NAME.getString())
					.lore(Settings.GUI_CURRENCY_PICKER_ITEMS_CUSTOM_LORE.getStringList()).make(), click -> {

				if (click.clickType == ClickType.RIGHT) {
					click.manager.showGUI(click.player, new GUIMaterialPicker(null, click.player, null, item -> {
						if (item != null) {
							this.selectedCurrency.accept(new ItemCurrency(), item);
						}
					}));
				}

				if (click.clickType == ClickType.LEFT) {
					final ItemStack cursor = click.cursor;
					if (cursor != null && cursor.getType() != CompMaterial.AIR.get()) {

						final ItemStack currency = cursor.clone();
						currency.setAmount(1);

						setAllowClose(true);
						this.selectedCurrency.accept(new ItemCurrency(), currency);
					}
				}
			});
	}

	@Override
	protected ItemStack makeDisplayItem(AbstractCurrency currency) {
		QuickItem quickItem = QuickItem.of(CompMaterial.PAPER);

		if (currency instanceof IconableCurrency) {
			IconableCurrency iconableCurrency = (IconableCurrency) currency;
			quickItem = QuickItem.of(iconableCurrency.getIcon());
		}

		quickItem.name(currency.getCurrencyName().equalsIgnoreCase("vault") ? "&a" + Settings.CURRENCY_VAULT_SYMBOL.getString() : "&e" + currency.getDisplayName());

		quickItem.lore(Replacer.replaceVariables(Settings.GUI_CURRENCY_PICKER_ITEMS_CURRENCY_LORE.getStringList(), "currency_owning_plugin", currency.getOwningPlugin()));

		return quickItem.make();
	}

	@Override
	protected void onClick(AbstractCurrency currency, GuiClickEvent event) {
		setAllowClose(true);
		this.selectedCurrency.accept(currency, null);
	}

	@Override
	protected List<Integer> fillSlots() {
		return InventoryBorder.getInsideBorders(6);
	}
}
