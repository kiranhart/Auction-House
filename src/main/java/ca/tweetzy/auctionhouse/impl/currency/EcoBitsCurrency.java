package ca.tweetzy.auctionhouse.impl.currency;

import ca.tweetzy.auctionhouse.api.currency.IconableCurrency;
import ca.tweetzy.flight.comp.enums.CompMaterial;
import com.willfp.ecobits.currencies.Currencies;
import com.willfp.ecobits.currencies.Currency;
import com.willfp.ecobits.currencies.CurrencyUtils;
import org.bukkit.OfflinePlayer;

import java.math.BigDecimal;

public final class EcoBitsCurrency extends IconableCurrency {

	private final Currency currency;

	public EcoBitsCurrency(String currencyName) {
		super("EcoBits", currencyName, "", CompMaterial.PAPER.parseItem());
		this.currency = Currencies.getByID(currencyName.toLowerCase());

		if (this.currency != null) {
			setDisplayName(this.currency.getName());
		}
	}

	@Override
	public boolean has(OfflinePlayer player, double amount) {
		if (this.currency == null)
			return false;

		return CurrencyUtils.getBalance(player, currency).doubleValue() >= amount;
	}

	@Override
	public boolean withdraw(OfflinePlayer player, double amount) {
		if (this.currency == null)
			return false;

		CurrencyUtils.setBalance(player, currency, BigDecimal.valueOf(CurrencyUtils.getBalance(player, currency).doubleValue() - amount));
		return true;
	}

	@Override
	public boolean deposit(OfflinePlayer player, double amount) {
		if (this.currency == null)
			return false;

		CurrencyUtils.setBalance(player, currency, BigDecimal.valueOf(CurrencyUtils.getBalance(player, currency).doubleValue() + amount));
		return true;
	}

	@Override
	public double getBalance(OfflinePlayer player) {
		if (this.currency == null)
			return 0;

		return CurrencyUtils.getBalance(player, currency).doubleValue();
	}
}

