package ca.tweetzy.auctionhouse.api.hook;

import ca.tweetzy.auctionhouse.exception.UltraEconomyCurrencyException;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.hooks.economies.Economy;
import ca.tweetzy.core.utils.TextUtils;
import me.TechsCode.UltraEconomy.UltraEconomy;
import me.TechsCode.UltraEconomy.objects.Account;
import me.TechsCode.UltraEconomy.objects.Currency;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

/**
 * The current file has been created by Kiran Hart
 * Date Created: October 05 2021
 * Time Created: 9:18 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public final class UltraEconomyHook extends Economy {

	private Currency currency;

	public UltraEconomyHook() {
		if (!isEnabled()) {
			Bukkit.getConsoleSender().sendMessage(TextUtils.formatText("&bUltraEconomy &7hook cannot be created&f: &cUltraEconomy not installed"));
			return;
		}

		final String[] ultraEconomyCurrencyName = Settings.ECONOMY_PLUGIN.getString().split(":");

		if (ultraEconomyCurrencyName.length < 2 && ultraEconomyCurrencyName[0].equalsIgnoreCase("UltraEconomy")) {
			Bukkit.getConsoleSender().sendMessage(TextUtils.formatText("&cInvalid UltraEconomy format, use -> &bUltraEconomy&f:&bTheCurrencyName &cinstead"));
			return;
		}

		if (!ultraEconomyCurrencyName[0].equalsIgnoreCase("UltraEconomy")) return;

		this.currency = UltraEconomy.getAPI().getCurrencies().name(ultraEconomyCurrencyName[1]).orElse(null);

		if (this.currency == null) {
			throw new UltraEconomyCurrencyException("AuctionHouse could not find the currency: " + ultraEconomyCurrencyName[0]);
		}
	}

	@Override
	public double getBalance(OfflinePlayer player) {
		final Account account = UltraEconomy.getAPI().getAccounts().uuid(player.getUniqueId()).orElse(null);
		return account == null ? 0 : account.getBalance(this.currency).getOnBank();
	}

	@Override
	public boolean hasBalance(OfflinePlayer player, double cost) {
		final Account account = UltraEconomy.getAPI().getAccounts().uuid(player.getUniqueId()).orElse(null);
		return account != null && account.getBalance(this.currency).getOnBank() >= cost;
	}

	@Override
	public boolean withdrawBalance(OfflinePlayer player, double cost) {
		final Account account = UltraEconomy.getAPI().getAccounts().uuid(player.getUniqueId()).orElse(null);
		if (account == null) return false;

		final float oldAmount = account.getBalance(this.currency).getOnBank();
		account.getBalance(this.currency).setBank(oldAmount - (float) cost);
		return true;
	}

	@Override
	public boolean deposit(OfflinePlayer player, double amount) {
		final Account account = UltraEconomy.getAPI().getAccounts().uuid(player.getUniqueId()).orElse(null);
		if (account == null) return false;

		final float oldAmount = account.getBalance(this.currency).getOnBank();
		account.getBalance(this.currency).setBank(oldAmount + (float) amount);
		return true;
	}

	@Override
	public String getName() {
		return "UltraEconomy";
	}

	@Override
	public boolean isEnabled() {
		return Bukkit.getPluginManager().getPlugin("UltraEconomy") != null;
	}
}
