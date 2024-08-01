package ca.tweetzy.auctionhouse.impl.currency;

import ca.tweetzy.auctionhouse.api.currency.IconableCurrency;
import ca.tweetzy.flight.comp.enums.CompMaterial;
import me.TechsCode.UltraEconomy.UltraEconomy;
import me.TechsCode.UltraEconomy.objects.Account;
import me.TechsCode.UltraEconomy.objects.Currency;
import org.bukkit.OfflinePlayer;

public final class UltraEconomyCurrency extends IconableCurrency {

	private final Currency currency;

	public UltraEconomyCurrency(String currencyName) {
		super("UltraEconomy", currencyName, "", CompMaterial.PAPER.parseItem());

		this.currency = UltraEconomy.getInstance().getCurrencies().name(currencyName).orElse(null);

		if (this.currency != null) {
			setDisplayName(this.currency.getName());
		}
	}

	@Override
	public boolean has(OfflinePlayer player, double amount) {
		final Account account = UltraEconomy.getInstance().getAccounts().uuid(player.getUniqueId()).orElse(null);
		if (account == null) return false;

		return account.getBalance(this.currency).getSum() >= amount;
	}

	@Override
	public boolean withdraw(OfflinePlayer player, double amount) {
		final Account account = UltraEconomy.getInstance().getAccounts().uuid(player.getUniqueId()).orElse(null);
		if (account == null) return false;

		account.removeBalance(this.currency, amount);
		return true;
	}

	@Override
	public boolean deposit(OfflinePlayer player, double amount) {
		final Account account = UltraEconomy.getInstance().getAccounts().uuid(player.getUniqueId()).orElse(null);
		if (account == null) return false;

		account.addBalance(this.currency, amount);
		return true;
	}

	@Override
	public double getBalance(OfflinePlayer player) {
		final Account account = UltraEconomy.getInstance().getAccounts().uuid(player.getUniqueId()).orElse(null);
		if (account == null) return 0;
		return account.getBalance(this.currency).getSum();
	}
}

