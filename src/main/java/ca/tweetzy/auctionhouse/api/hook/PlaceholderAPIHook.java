package ca.tweetzy.auctionhouse.api.hook;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

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

		if (params.equalsIgnoreCase("server_active_auctions")) {
			return String.valueOf(AuctionHouse.getInstance().getAuctionItemManager().getItems().size());
		}

		return null;
	}
}
