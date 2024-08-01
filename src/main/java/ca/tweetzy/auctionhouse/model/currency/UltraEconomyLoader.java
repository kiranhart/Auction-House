package ca.tweetzy.auctionhouse.model.currency;

import ca.tweetzy.auctionhouse.api.currency.AbstractCurrency;
import ca.tweetzy.auctionhouse.impl.currency.UltraEconomyCurrency;
import ca.tweetzy.auctionhouse.settings.Settings;
import me.TechsCode.UltraEconomy.UltraEconomy;
import me.TechsCode.UltraEconomy.objects.Currency;

import java.util.ArrayList;
import java.util.List;

public final class UltraEconomyLoader extends CurrencyLoader {

	public UltraEconomyLoader() {
		super("UltraEconomy");
	}


	@Override
	public List<AbstractCurrency> getCurrencies() {
		final List<AbstractCurrency> currencies = new ArrayList<>();

		for (Currency currency : UltraEconomy.getInstance().getCurrencies()) {
			boolean blackListed = false;

			for (String blacklisted : Settings.CURRENCY_BLACKLISTED.getStringList()) {
				final String[] blacklistSplit = blacklisted.split(":");

				if (blacklistSplit.length != 2) continue;
				if (!blacklistSplit[0].equalsIgnoreCase(this.owningPlugin)) continue;

				if (blacklistSplit[1].equalsIgnoreCase(currency.getName()))
					blackListed = true;

			}

			if (!blackListed)
				currencies.add(new UltraEconomyCurrency(currency.getName()));
		}

		return currencies;
	}
}
