package ca.tweetzy.auctionhouse.model.manager;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.currency.AbstractCurrency;
import ca.tweetzy.auctionhouse.api.manager.ListManager;
import ca.tweetzy.auctionhouse.impl.currency.AllCurrency;
import ca.tweetzy.auctionhouse.impl.currency.ItemCurrency;
import ca.tweetzy.auctionhouse.impl.currency.VaultCurrency;
import ca.tweetzy.auctionhouse.model.currency.CoinEngineEconomyLoader;
import ca.tweetzy.auctionhouse.model.currency.EcoBitsEconomyLoader;
import ca.tweetzy.auctionhouse.model.currency.FundsEconomyLoader;
import ca.tweetzy.auctionhouse.model.currency.UltraEconomyLoader;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.flight.comp.enums.CompMaterial;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

public final class CurrencyManager extends ListManager<AbstractCurrency> {

	public CurrencyManager() {
		super("Currency");
	}

	public AbstractCurrency locateCurrency(@NonNull final String owningPlugin, @NonNull final String currencyName) {
		return getManagerContent().stream().filter(currency -> currency.getOwningPlugin().equals(owningPlugin) && currency.getCurrencyName().equals(currencyName)).findFirst().orElse(null);
	}

	public AbstractCurrency locateCurrency(@NonNull final String currencyName) {
		return getManagerContent().stream().filter(currency -> currency.getCurrencyName().equalsIgnoreCase(currencyName)).findFirst().orElse(null);
	}

	public AbstractCurrency getDefaultCurrency() {
		final String[] CURRENCY_DEFAULT = Settings.CURRENCY_DEFAULT_SELECTED.getString().split("/");
		return locateCurrency(CURRENCY_DEFAULT[0], CURRENCY_DEFAULT[1]);
	}

	public AbstractCurrency getAllCurrency() {
		return locateCurrency("AuctionHouse", "AllCurrencies");
	}

	public boolean has(@NonNull final OfflinePlayer offlinePlayer, @NonNull final String owningPlugin, @NonNull final String currencyName, final double amount) {
		if (owningPlugin.equalsIgnoreCase("vault") || currencyName.equalsIgnoreCase("vault"))
			return AuctionHouse.getEconomy().has(offlinePlayer, amount);

		return locateCurrency(owningPlugin, currencyName).has(offlinePlayer, amount);
	}

	public double getBalance(@NonNull final OfflinePlayer offlinePlayer, @NonNull final String owningPlugin, @NonNull final String currencyName) {
		if (owningPlugin.equalsIgnoreCase("vault") || currencyName.equalsIgnoreCase("vault"))
			return AuctionHouse.getEconomy().getBalance(offlinePlayer);
		return locateCurrency(owningPlugin, currencyName).getBalance(offlinePlayer);
	}

	public boolean withdraw(@NonNull final OfflinePlayer offlinePlayer, @NonNull final String owningPlugin, @NonNull final String currencyName, final double amount) {
		if (owningPlugin.equalsIgnoreCase("vault") || currencyName.equalsIgnoreCase("vault")) {
			AuctionHouse.getEconomy().withdrawPlayer(offlinePlayer, amount);
			return true;
		}

		return locateCurrency(owningPlugin, currencyName).withdraw(offlinePlayer, amount);
	}

	public boolean deposit(@NonNull final OfflinePlayer offlinePlayer, @NonNull final String owningPlugin, @NonNull final String currencyName, final double amount) {
		if (owningPlugin.equalsIgnoreCase("vault") || currencyName.equalsIgnoreCase("vault")) {
			AuctionHouse.getEconomy().depositPlayer(offlinePlayer, amount);
			return true;
		}

		return locateCurrency(owningPlugin, currencyName).deposit(offlinePlayer, amount);
	}

	public boolean deposit(@NonNull final OfflinePlayer offlinePlayer, double amount, @NonNull final String currency, ItemStack currencyItem) {
		if (currencyItem != null && currencyItem.getType() != CompMaterial.AIR.parseMaterial())
			return deposit(offlinePlayer, currencyItem, (int) amount);

		final String[] currSplit = currency.split("/");
		return locateCurrency(currSplit[0], currSplit[1]).deposit(offlinePlayer, amount);
	}

	public boolean withdraw(@NonNull final OfflinePlayer offlinePlayer, double amount, @NonNull final String currency, ItemStack currencyItem) {
		if (currencyItem != null && currencyItem.getType() != CompMaterial.AIR.parseMaterial())
			return withdraw(offlinePlayer, currencyItem, (int) amount);

		final String[] currSplit = currency.split("/");
		return locateCurrency(currSplit[0], currSplit[1]).withdraw(offlinePlayer, amount);
	}

	public boolean has(@NonNull final OfflinePlayer offlinePlayer, @NonNull final ItemStack itemStack, final int amount) {
		return ((ItemCurrency) locateCurrency("AuctionHouse", "Item")).has(offlinePlayer, amount, itemStack);

	}

	public boolean withdraw(@NonNull final OfflinePlayer offlinePlayer, @NonNull final ItemStack itemStack, final int amount) {
		return ((ItemCurrency) locateCurrency("AuctionHouse", "Item")).withdraw(offlinePlayer, amount, itemStack);
	}

	public boolean deposit(@NonNull final OfflinePlayer offlinePlayer, @NonNull final ItemStack itemStack, final int amount) {
		return ((ItemCurrency) locateCurrency("AuctionHouse", "Item")).deposit(offlinePlayer, amount, itemStack);
	}

	public boolean has(@NonNull final OfflinePlayer offlinePlayer, final double amount) {
		final String[] CURRENCY_DEFAULT = Settings.CURRENCY_DEFAULT_SELECTED.getString().split("/");
		return has(offlinePlayer, CURRENCY_DEFAULT[0], CURRENCY_DEFAULT[1], amount);

	}

	public boolean withdraw(@NonNull final OfflinePlayer offlinePlayer, final double amount) {
		final String[] CURRENCY_DEFAULT = Settings.CURRENCY_DEFAULT_SELECTED.getString().split("/");
		return withdraw(offlinePlayer, CURRENCY_DEFAULT[0], CURRENCY_DEFAULT[1], amount);
	}

	public boolean deposit(@NonNull final OfflinePlayer offlinePlayer, final double amount) {
		final String[] CURRENCY_DEFAULT = Settings.CURRENCY_DEFAULT_SELECTED.getString().split("/");
		return deposit(offlinePlayer, CURRENCY_DEFAULT[0], CURRENCY_DEFAULT[1], amount);
	}

	public double getBalance(@NonNull final OfflinePlayer offlinePlayer) {
		final String[] CURRENCY_DEFAULT = Settings.CURRENCY_DEFAULT_SELECTED.getString().split("/");
		return getBalance(offlinePlayer, CURRENCY_DEFAULT[0], CURRENCY_DEFAULT[1]);
	}

	public String getFormattedBalance(@NonNull final OfflinePlayer offlinePlayer, String currency, ItemStack currencyItem) {
		return AuctionHouse.getAPI().getFinalizedCurrencyNumber(getBalance(offlinePlayer, currency.split("/")[0], currency.split("/")[1]), currency, currencyItem);
	}

	public AbstractCurrency getNext(@NonNull final AbstractCurrency current) {
		int currentIndex = getManagerContent().indexOf(current);
		if (currentIndex == -1) {
			return locateCurrency("Vault");
		}

		int next = (currentIndex + 1) % getManagerContent().size();
		return managerContent.get(next);
	}

	@Override
	public void load() {
		clear();

		// used to filter for all items, not actually a valid currency to be used
		add(new AllCurrency());

		// add vault by default
		add(new VaultCurrency());
		add(new ItemCurrency());

		// load currencies from providers that allow multiple currencies
		if (Bukkit.getServer().getPluginManager().isPluginEnabled("UltraEconomy"))
			new UltraEconomyLoader().getCurrencies().forEach(this::add);

		if (Bukkit.getServer().getPluginManager().isPluginEnabled("Funds"))
			new FundsEconomyLoader().getCurrencies().forEach(this::add);

		if (Bukkit.getServer().getPluginManager().isPluginEnabled("EcoBits"))
			new EcoBitsEconomyLoader().getCurrencies().forEach(this::add);

		if (Bukkit.getServer().getPluginManager().isPluginEnabled("CoinsEngine"))
			new CoinEngineEconomyLoader().getCurrencies().forEach(this::add);
	}
}
