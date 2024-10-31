package ca.tweetzy.auctionhouse.impl;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.ban.Ban;
import ca.tweetzy.auctionhouse.api.ban.BanType;
import ca.tweetzy.auctionhouse.api.sync.SynchronizeResult;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.UUID;
import java.util.function.Consumer;

@AllArgsConstructor
public final class AuctionBan implements Ban {

	private final UUID bannedUser;
	private UUID banner;
	private final HashSet<BanType> types;
	private String reason;
	private boolean permanent;
	private long expiration;
	private long createdOn;

	@Override
	public @NonNull UUID getId() {
		return this.bannedUser;
	}

	@Override
	public HashSet<BanType> getTypes() {
		return this.types;
	}

	@Override
	public UUID getBanner() {
		return this.banner;
	}

	@Override
	public void setBanner(UUID uuid) {
		this.banner = uuid;
	}

	@Override
	public String getReason() {
		return this.reason;
	}

	@Override
	public void setReason(String reason) {
		this.reason = reason;
	}

	@Override
	public boolean isPermanent() {
		return this.permanent;
	}

	@Override
	public void setIsPermanent(boolean permanent) {
		this.permanent = permanent;
	}

	@Override
	public long getExpireDate() {
		return this.expiration;
	}

	@Override
	public void setExpireDate(long time) {
		this.expiration = time;
	}

	@Override
	public long getTimeCreated() {
		return this.createdOn;
	}

	@Override
	public long getLastUpdated() {
		return this.createdOn;
	}

	@Override
	public void store(Consumer<Ban> stored) {
		AuctionHouse.getDataManager().insertBan(this, (ex, result) -> {
			if (ex == null)
				stored.accept(result);
		});
	}

	@Override
	public void unStore(@Nullable Consumer<SynchronizeResult> status) {
		AuctionHouse.getDataManager().deleteBan(this, (error, updateStatus) -> {
			if (updateStatus) {
				AuctionHouse.getBanManager().remove(getId());
			}

			if (status != null)
				status.accept(error == null ? updateStatus ? SynchronizeResult.SUCCESS : SynchronizeResult.FAILURE : SynchronizeResult.FAILURE);
		});
	}
}
