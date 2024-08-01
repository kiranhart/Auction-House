package ca.tweetzy.auctionhouse.model.currency;

import ca.tweetzy.auctionhouse.api.currency.AbstractCurrency;
import lombok.NonNull;

import java.util.List;

public abstract class CurrencyLoader {

	protected String owningPlugin;

	public CurrencyLoader(@NonNull final String owningPlugin) {
		this.owningPlugin = owningPlugin;
	}

	public abstract List<AbstractCurrency> getCurrencies();
}
