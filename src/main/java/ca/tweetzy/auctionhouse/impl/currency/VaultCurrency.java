package ca.tweetzy.auctionhouse.impl.currency;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.currency.AbstractCurrency;
import ca.tweetzy.auctionhouse.settings.Settings;
import org.bukkit.OfflinePlayer;

public final class VaultCurrency extends AbstractCurrency {

	public VaultCurrency() {
		super("Vault", "Vault", Settings.CURRENCY_VAULT_SYMBOL.getString());
	}

	@Override
	public boolean has(OfflinePlayer player, double amount) {
		return AuctionHouse.getEconomy().has(player, amount);
	}

	@Override
	public boolean withdraw(OfflinePlayer player, double amount) {
		return AuctionHouse.getEconomy().withdrawPlayer(player, amount).transactionSuccess();
	}

	@Override
	public boolean deposit(OfflinePlayer player, double amount) {
		return AuctionHouse.getEconomy().depositPlayer(player, amount).transactionSuccess();
	}

	@Override
	public double getBalance(OfflinePlayer player) {
		return AuctionHouse.getEconomy().getBalance(player);
	}
}
