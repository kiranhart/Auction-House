package ca.tweetzy.auctionhouse.impl.currency;

import ca.tweetzy.auctionhouse.api.currency.IconableCurrency;
import ca.tweetzy.flight.comp.enums.CompMaterial;
import org.bukkit.OfflinePlayer;
import su.nightexpress.coinsengine.api.CoinsEngineAPI;
import su.nightexpress.coinsengine.api.currency.Currency;

public final class CoinEngineCurrency extends IconableCurrency {

	private final Currency currency;

	public CoinEngineCurrency(String currencyName) {
		super("CoinsEngine", currencyName, "", CompMaterial.PAPER.parseItem());
		this.currency = CoinsEngineAPI.getCurrency(currencyName);

		if (this.currency != null) {
			setDisplayName(this.currency.getName());
			setIcon(this.currency.getIcon());
		}
	}

	@Override
	public boolean has(OfflinePlayer player, double amount) {
		if (this.currency == null)
			return false;

		return CoinsEngineAPI.getBalance(player.getUniqueId(), this.currency) >= amount;
	}

	@Override
	public boolean withdraw(OfflinePlayer player, double amount) {
		if (this.currency == null)
			return false;

		return CoinsEngineAPI.removeBalance(player.getUniqueId(), this.currency, amount);
	}

	@Override
	public boolean deposit(OfflinePlayer player, double amount) {
		if (this.currency == null)
			return false;

		return CoinsEngineAPI.addBalance(player.getUniqueId(), this.currency, amount);
	}

	@Override
	public double getBalance(OfflinePlayer player) {
		return CoinsEngineAPI.getBalance(player.getUniqueId(), this.currency);
	}
}

