package ca.tweetzy.auctionhouse.api.auction;

import ca.tweetzy.auctionhouse.api.sync.Identifiable;
import ca.tweetzy.auctionhouse.api.sync.Storeable;
import ca.tweetzy.auctionhouse.api.sync.Synchronize;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface AuctionUser extends Identifiable<UUID>, Storeable<AuctionUser>, Synchronize {

	String getLastKnownName();

	void setLastKnownName(String lastKnownName);

	long getLastListedTime();

	void setLastListedTime(long time);

	UserSettings getSettings();

	void setSettings(UserSettings settings);

	@Nullable
	default Player getPlayer() {
		return Bukkit.getPlayer(getId());
	}
}
