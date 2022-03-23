package ca.tweetzy.auctionhouse.api.hook;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import lombok.experimental.UtilityClass;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: August 07 2021
 * Time Created: 6:36 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class PlaceholderAPIHook extends PlaceholderExpansion {

	private final AuctionHouse plugin;

	public PlaceholderAPIHook(AuctionHouse plugin) {
		this.plugin = plugin;
	}


	@Override
	public @NotNull
	String getIdentifier() {
		return "auctionhouse";
	}

	@Override
	public @NotNull
	String getAuthor() {
		return "KiranHart";
	}

	@Override
	public @NotNull
	String getVersion() {
		return "1.0.0";
	}

	@Override
	public boolean persist() {
		return true;
	}

	@Override
	public String onRequest(OfflinePlayer player, @NotNull String params) {
		if (params.equalsIgnoreCase("name")) return player == null ? null : player.getName();

		if (params.equalsIgnoreCase("active_auctions")) {
			AuctionPlayer auctionPlayer = AuctionHouse.getInstance().getAuctionPlayerManager().getPlayer(player.getUniqueId());
			if (auctionPlayer == null) return null;
			return String.valueOf(auctionPlayer.getItems(false).size());
		}

		if (params.equalsIgnoreCase("expired_auctions")) {
			AuctionPlayer auctionPlayer = AuctionHouse.getInstance().getAuctionPlayerManager().getPlayer(player.getUniqueId());
			if (auctionPlayer == null) return null;
			return String.valueOf(auctionPlayer.getItems(true).size());
		}

		if (params.equalsIgnoreCase("total_money_earned")) {
			return AuctionAPI.getInstance().formatNumber(AuctionHouse.getInstance().getAuctionStatManager().getPlayerStats(player).getEarned());
		}

		if (params.equalsIgnoreCase("total_money_spent")) {
			return AuctionAPI.getInstance().formatNumber(AuctionHouse.getInstance().getAuctionStatManager().getPlayerStats(player).getSpent());
		}

		if (params.equalsIgnoreCase("total_items_listed")) {
			return AuctionAPI.getInstance().formatNumber(AuctionHouse.getInstance().getAuctionStatManager().getPlayerStats(player).getCreated());
		}

		if (params.equalsIgnoreCase("total_items_expired")) {
			return AuctionAPI.getInstance().formatNumber(AuctionHouse.getInstance().getAuctionStatManager().getPlayerStats(player).getExpired());
		}

		if (params.equalsIgnoreCase("total_items_sold")) {
			return AuctionAPI.getInstance().formatNumber(AuctionHouse.getInstance().getAuctionStatManager().getPlayerStats(player).getSold());
		}

		if (params.equalsIgnoreCase("server_active_auctions")) {
			return String.valueOf(AuctionHouse.getInstance().getAuctionItemManager().getItems().size());
		}

		return null;
	}

	@UtilityClass
	public static final class PAPIReplacer {
		public String tryReplace(Player player, String msg) {
			if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))
				msg = PlaceholderAPI.setPlaceholders(player, msg);
			return msg;
		}

		public List<String> tryReplace(Player player, List<String> msgs) {
			return msgs.stream().map(line -> tryReplace(player, line)).collect(Collectors.toList());
		}
	}
}
