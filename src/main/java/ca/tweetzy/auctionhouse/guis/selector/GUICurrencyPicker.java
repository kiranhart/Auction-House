package ca.tweetzy.auctionhouse.guis.selector;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.currency.AbstractCurrency;
import ca.tweetzy.auctionhouse.api.currency.IconableCurrency;
import ca.tweetzy.auctionhouse.guis.AuctionPagedGUI;
import ca.tweetzy.auctionhouse.impl.currency.FundsCurrency;
import ca.tweetzy.auctionhouse.impl.currency.ItemCurrency;
import ca.tweetzy.auctionhouse.impl.currency.UltraEconomyCurrency;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.gui.Gui;
import ca.tweetzy.core.gui.events.GuiClickEvent;
import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.gui.helper.InventoryBorder;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.flight.utils.Replacer;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public final class GUICurrencyPicker extends AuctionPagedGUI<AbstractCurrency> {

	private final BiConsumer<AbstractCurrency, ItemStack> selectedCurrency;

	public GUICurrencyPicker(final Gui parent, @NonNull final Player player, @NonNull final BiConsumer<AbstractCurrency, ItemStack> selectedCurrency) {
		super(parent, player, Settings.GUI_CURRENCY_PICKER_TITLE.getString(), 6, AuctionHouse.getCurrencyManager().getManagerContent().stream().filter(currency -> !currency.getOwningPlugin().equalsIgnoreCase("auctionhouse")).collect(Collectors.toList()));
		this.selectedCurrency = selectedCurrency;
		setAcceptsItems(true);
		setAllowClose(false);
		draw();
	}

	@Override
	protected void drawFixed() {
		applyBackExit();

		// custom item
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
				if (cursor != null && cursor.getType() != CompMaterial.AIR.parseMaterial()) {

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

		if (currency instanceof FundsCurrency) {
			FundsCurrency fundsCurrency = (FundsCurrency) currency;
			quickItem.name(fundsCurrency.getDisplayName());
		} else if (currency instanceof UltraEconomyCurrency) {
			UltraEconomyCurrency ultraEconomyCurrency = (UltraEconomyCurrency) currency;
			quickItem.name(ultraEconomyCurrency.getDisplayName());
		} else {
			quickItem.name(currency.getCurrencyName().equalsIgnoreCase("vault") ? "&a" + Settings.CURRENCY_VAULT_SYMBOL.getString() : "&e" + currency.getCurrencyName());
		}

		quickItem.lore(Replacer.replaceVariables(Arrays.asList(
				"&7Owning Plugin&f: &e%currency_owning_plugin%",
				"",
				"&a&lLeft Click &7to select this currency"
		), "currency_owning_plugin", currency.getOwningPlugin()));

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
