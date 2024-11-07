package ca.tweetzy.auctionhouse.impl.currency;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.currency.AbstractCurrency;
import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.flight.utils.PlayerUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

public final class AllCurrency extends AbstractCurrency {

	// used for filtering only
	public AllCurrency() {
		super("AuctionHouse", "AllCurrencies", Common.colorize(AuctionHouse.getInstance().getLocale().getMessage("auction_filter.currency.all currencies").getMessage()));
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
