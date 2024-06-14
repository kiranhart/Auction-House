package ca.tweetzy.auctionhouse.model.manager;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.ban.Ban;
import ca.tweetzy.auctionhouse.api.ban.BanType;
import ca.tweetzy.auctionhouse.api.manager.KeyValueManager;
import ca.tweetzy.auctionhouse.impl.AuctionBan;
import lombok.NonNull;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.UUID;
import java.util.function.Consumer;

public final class BanManager extends KeyValueManager<UUID, Ban> {

	public BanManager() {
		super("Ban");
	}

	public void registerBan(@NonNull final Ban ban, final Consumer<Boolean> created) {
		if (this.managerContent.containsKey(ban.getId())) return;

		ban.store(storedBan -> {
			if (storedBan != null) {
				add(storedBan.getId(), storedBan);
				if (created != null)
					created.accept(true);
			} else {
				if (created != null)
					created.accept(false);
			}
		});
	}

	public Ban generateBan(@NonNull final Player bannedBy, @NonNull final Player target, @NonNull final HashSet<BanType> types, @NonNull final String reason, final boolean permanent, final long expiration) {
		return new AuctionBan(target.getUniqueId(), bannedBy.getUniqueId(), types, reason, permanent, expiration, System.currentTimeMillis());
	}

	@Override
	public void load() {
		AuctionHouse.getInstance().getDataManager().getBans((error, results) -> {
			if (error == null)
				results.forEach(ban -> add(ban.getId(), ban));
		});
	}
}
