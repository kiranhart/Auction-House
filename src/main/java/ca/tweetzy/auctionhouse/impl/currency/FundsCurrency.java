package ca.tweetzy.auctionhouse.impl.currency;

import ca.tweetzy.auctionhouse.api.currency.IconableCurrency;
import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.funds.api.FundsAPI;
import ca.tweetzy.funds.api.interfaces.Account;
import ca.tweetzy.funds.api.interfaces.Currency;
import org.bukkit.OfflinePlayer;

public final class FundsCurrency extends IconableCurrency {

	private final Currency currency;

	public FundsCurrency(String currencyName) {
		super("Funds", currencyName, "", CompMaterial.PAPER.parseItem());
		this.currency = FundsAPI.getInstance().getCurrency(currencyName);

		if (this.currency != null) {
			setDisplayName(this.currency.getName());
			setIcon(this.currency.getIcon().parseItem());
		}
	}

	@Override
	public boolean has(OfflinePlayer player, double amount) {
		final Account account = FundsAPI.getInstance().getAccount(player);

		if (account == null || this.currency == null)
			return false;

		return account.getCurrencies().getOrDefault(this.currency, 0D) >= amount;
	}

	@Override
	public boolean withdraw(OfflinePlayer player, double amount) {
		final Account account = FundsAPI.getInstance().getAccount(player);

		if (account == null || this.currency == null)
			return false;

		account.withdrawCurrency(this.currency, amount);
		account.sync(true);
		return true;
	}

	@Override
	public boolean deposit(OfflinePlayer player, double amount) {
		final Account account = FundsAPI.getInstance().getAccount(player);

		if (account == null || this.currency == null)
			return false;

		account.depositCurrency(this.currency, amount);
		account.sync(true);
		return true;
	}

	@Override
	public double getBalance(OfflinePlayer player) {
		final Account account = FundsAPI.getInstance().getAccount(player);

		if (account == null || this.currency == null)
			return 0;

		return account.getCurrencies().getOrDefault(this.currency, 0D);
	}
}

