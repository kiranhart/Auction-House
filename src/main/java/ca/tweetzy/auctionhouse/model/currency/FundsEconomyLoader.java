package ca.tweetzy.auctionhouse.model.currency;

import ca.tweetzy.auctionhouse.api.currency.AbstractCurrency;
import ca.tweetzy.auctionhouse.impl.currency.FundsCurrency;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.funds.api.FundsAPI;
import ca.tweetzy.funds.api.interfaces.Currency;

import java.util.ArrayList;
import java.util.List;

public final class FundsEconomyLoader extends CurrencyLoader {

	public FundsEconomyLoader() {
		super("Funds");
	}

	@Override
	public List<AbstractCurrency> getCurrencies() {
		final List<AbstractCurrency> currencies = new ArrayList<>();

		for (Currency currency : FundsAPI.getInstance().getCurrencies()) {
			boolean blackListed = false;

			for (String blacklisted : Settings.CURRENCY_BLACKLISTED.getStringList()) {
				final String[] blacklistSplit = blacklisted.split(":");

				if (blacklistSplit.length != 2) continue;
				if (!blacklistSplit[0].equalsIgnoreCase(this.owningPlugin)) continue;

				if (blacklistSplit[1].equalsIgnoreCase(currency.getName()))
					blackListed = true;

			}

			if (!blackListed)
				currencies.add(new FundsCurrency(currency.getId()));
		}

		return currencies;
	}
}