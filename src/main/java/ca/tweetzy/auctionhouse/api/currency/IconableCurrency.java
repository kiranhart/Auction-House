package ca.tweetzy.auctionhouse.api.currency;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

@Setter
@Getter
public abstract class IconableCurrency extends AbstractCurrency {

	protected ItemStack icon;

	public IconableCurrency(String owningPlugin, String currencyName, String displayName, ItemStack icon) {
		super(owningPlugin, currencyName, displayName);
		this.icon = icon;
	}
}
