package ca.tweetzy.auctionhouse.api.ban;

import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.api.sync.*;
import ca.tweetzy.flight.utils.ChatUtil;
import ca.tweetzy.flight.utils.Common;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.HashSet;
import java.util.UUID;

public interface Ban extends Identifiable<UUID>, Trackable, Storeable<Ban>, Unstoreable<SynchronizeResult> {

	HashSet<BanType> getTypes();

	UUID getBanner();

	void setBanner(UUID uuid);

	String getReason();

	void setReason(String reason);

	boolean isPermanent();

	void setIsPermanent(boolean permanent);

	long getExpireDate();

	void setExpireDate(long time);

	default OfflinePlayer locatePlayer() {
		OfflinePlayer player = Bukkit.getPlayer(getId());
		if (player != null)
			return player;

		player = Bukkit.getOfflinePlayer(getId());
		return player;
	}

	default String getBansAsString() {
		if (getTypes().isEmpty())
			return "&cNone Selected";

		StringBuilder contents = new StringBuilder();

		for (BanType type : getTypes()) {
			contents.append(Common.colorize("&7- &e" + ChatUtil.capitalizeFully(type.name()))).append("\n");
		}

		return contents.toString();
	}

	default String getBanString() {
		StringBuilder contents = new StringBuilder();

		for (BanType type : getTypes()) {
			contents.append(type.name()).append(",");
		}

		return contents.toString();
	}

	default String getReadableExpirationDate() {
		return AuctionAPI.getInstance().convertMillisToDate(getExpireDate());
	}
}