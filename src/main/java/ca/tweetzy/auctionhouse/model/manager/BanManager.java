package ca.tweetzy.auctionhouse.model.manager;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.ban.Ban;
import ca.tweetzy.auctionhouse.api.ban.BanType;
import ca.tweetzy.auctionhouse.api.manager.KeyValueManager;
import ca.tweetzy.auctionhouse.api.sync.SynchronizeResult;
import ca.tweetzy.auctionhouse.impl.AuctionBan;
import lombok.NonNull;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.UUID;
import java.util.function.Consumer;

public final class BanManager extends KeyValueManager<UUID, Ban> {

	public BanManager() {
		super("Ban");
	}

	public boolean isBannedAlready(@NonNull final OfflinePlayer player) {
		return this.managerContent.containsKey(player.getUniqueId());
	}

	public boolean isStillBanned(@NonNull final Player player, BanType... banTypes) {
		if (!isBannedAlready(player)) return false;

		final Ban targetedBan = get(player.getUniqueId());
		boolean banned = false;

		for (BanType banType : banTypes) {
			if (targetedBan.getTypes().contains(banType)) {
				banned = true;
			}
		}

		if (banned) {
			if (targetedBan.isPermanent()) {
				// oof
				AuctionHouse.getInstance().getLocale().getMessage("ban.player permanently banned").sendPrefixedMessage(player);
			} else {
				if (System.currentTimeMillis() > targetedBan.getExpireDate()) {
					targetedBan.unStore(status -> {
						if (status == SynchronizeResult.SUCCESS)
							AuctionHouse.getInstance().getLocale().getMessage("ban.player ban expired").sendPrefixedMessage(player);
					});


					banned = false;
				} else {
					AuctionHouse.getInstance().getLocale().getMessage("ban.player still banned").processPlaceholder("ban_expiration", targetedBan.getReadableExpirationDate()).sendPrefixedMessage(player);
				}
			}
		}

		return banned;
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

	public Ban generateBan(@NonNull final Player bannedBy, @NonNull final OfflinePlayer target, @NonNull final HashSet<BanType> types, @NonNull final String reason, final boolean permanent, final long expiration) {
		return new AuctionBan(target.getUniqueId(), bannedBy.getUniqueId(), types, reason, permanent, expiration, System.currentTimeMillis());
	}

	public Ban generateEmptyBan(@NonNull final Player bannedBy, @NonNull final OfflinePlayer target) {
		return new AuctionBan(target.getUniqueId(), bannedBy.getUniqueId(), new HashSet<>(), "Enter Ban Reason Here", false, System.currentTimeMillis() + 1000L * 60 * 60, System.currentTimeMillis());
	}

	@Override
	public void load() {
		AuctionHouse.getDataManager().getBans((error, results) -> {
			if (error == null)
				results.forEach(ban -> add(ban.getId(), ban));
		});
	}
}
