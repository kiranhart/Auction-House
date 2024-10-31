package ca.tweetzy.auctionhouse.api.event;

import ca.tweetzy.auctionhouse.api.ban.Ban;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class AuctionPlayerBanEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();

	@Setter @Getter
	private boolean cancelled;

	@Getter
	private final Ban ban;

	public AuctionPlayerBanEvent(@NonNull final Ban ban) {
		this.ban = ban;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
