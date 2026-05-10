package ca.tweetzy.auctionhouse.impl.currency;


import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.auctionhouse.settings.Translations;
import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.currency.AbstractCurrency;
import ca.tweetzy.flight.utils.Common;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

public final class AllCurrency extends AbstractCurrency {

	// used for filtering only
	public AllCurrency() {
		super("AuctionHouse", "AllCurrencies", Common.colorize(TranslationManager.string(Translations.AUCTION_FILTER_CURRENCY_ALL_CURRENCIES)));
	}

	public boolean has(OfflinePlayer player, double amount, ItemStack item) {
		return true;
	}

	public boolean withdraw(OfflinePlayer player, double amount, ItemStack item) {
		return true;
	}

	public boolean deposit(OfflinePlayer player, double amount, ItemStack item) {
		return true;
	}

	@Override
	public double getBalance(OfflinePlayer player) {
		return 0;
	}

	@Override
	public boolean has(OfflinePlayer player, double amount) {
		return false;
	}

	@Override
	public boolean withdraw(OfflinePlayer player, double amount) {
		return false;
	}

	@Override
	public boolean deposit(OfflinePlayer player, double amount) {
		return false;
	}
}
