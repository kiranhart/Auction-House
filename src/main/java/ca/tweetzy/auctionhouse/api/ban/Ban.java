package ca.tweetzy.auctionhouse.api.ban;

import ca.tweetzy.auctionhouse.api.Identifiable;
import ca.tweetzy.auctionhouse.api.Trackable;
import ca.tweetzy.auctionhouse.api.sync.Storeable;
import ca.tweetzy.auctionhouse.api.sync.SynchronizeResult;
import ca.tweetzy.auctionhouse.api.sync.Unstoreable;

import java.util.UUID;

public interface Ban extends Identifiable<UUID>, Trackable, Storeable<Ban>, Unstoreable<SynchronizeResult> {

	BanType getType();

	UUID getBanner();

	void setBanner(UUID uuid);

	String getReason();

	void setReason(String reason);

	boolean isPermanent();

	void setIsPermanent(boolean permanent);

	long getExpireDate();

	void setExpireDate(long time);
}