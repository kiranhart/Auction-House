package ca.tweetzy.auctionhouse.model.currency;

import ca.tweetzy.auctionhouse.api.currency.AbstractCurrency;
import ca.tweetzy.auctionhouse.impl.currency.CoinEngineCurrency;
import ca.tweetzy.auctionhouse.settings.Settings;
import su.nightexpress.coinsengine.api.CoinsEngineAPI;
import su.nightexpress.coinsengine.api.currency.Currency;

import java.util.ArrayList;
import java.util.List;

public final class CoinEngineEconomyLoader extends CurrencyLoader {

	public CoinEngineEconomyLoader() {
		super("CoinsEngine");
	}

	@Override
	public List<AbstractCurrency> getCurrencies() {
		final List<AbstractCurrency> currencies = new ArrayList<>();

		for (Currency currency : CoinsEngineAPI.getCurrencyManager().getCurrencies()) {
			boolean blackListed = false;

			for (String blacklisted : Settings.CURRENCY_BLACKLISTED.getStringList()) {
				final String[] blacklistSplit = blacklisted.split(":");

				if (blacklistSplit.length != 2) continue;
				if (!blacklistSplit[0].equalsIgnoreCase(this.owningPlugin)) continue;

				if (blacklistSplit[1].equalsIgnoreCase(currency.getId()))
					blackListed = true;

			}

			if (!blackListed)
				currencies.add(new CoinEngineCurrency(currency.getId()));
		}

		return currencies;
	}
}