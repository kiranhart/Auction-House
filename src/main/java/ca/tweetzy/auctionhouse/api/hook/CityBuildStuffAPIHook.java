package ca.tweetzy.auctionhouse.api.hook;

import at.blank.coinapi.Api.CoinAPI;
import ca.tweetzy.core.hooks.economies.Economy;
import ca.tweetzy.core.utils.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

/**
 * The current file has been created by Kiran Hart
 * Date Created: October 05 2021
 * Time Created: 9:18 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public final class CityBuildStuffAPIHook extends Economy {

	public CityBuildStuffAPIHook() {
		if (!isEnabled()) {
			Bukkit.getConsoleSender().sendMessage(TextUtils.formatText("&bCityBuildStuff &7hook cannot be created&f: &cCityBuildStuff not installed"));
			return;
		}
	}

	@Override
	public double getBalance(OfflinePlayer player) {
		return new CoinAPI(player.getUniqueId()).getCoins();
	}

	@Override
	public boolean hasBalance(OfflinePlayer player, double cost) {
		final CoinAPI api = new CoinAPI(player.getUniqueId());
		return api.getCoins() >= cost;
	}

	@Override
	public boolean withdrawBalance(OfflinePlayer player, double cost) {
		final CoinAPI api = new CoinAPI(player.getUniqueId());
		if (!api.hasAccount()) return false;

		if (!hasBalance(player, cost)) return false;
		api.setCoins(api.getCoins() - cost);

		return true;
	}

	@Override
	public boolean deposit(OfflinePlayer player, double amount) {
		final CoinAPI api = new CoinAPI(player.getUniqueId());
		if (!api.hasAccount()) return false;

		api.setCoins(api.getCoins() + amount);
		return true;
	}

	@Override
	public String getName() {
		return "CoinsAI";
	}

	@Override
	public boolean isEnabled() {
		return Bukkit.getPluginManager().getPlugin("CityBuildStuff") != null;
	}
}
