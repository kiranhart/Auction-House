package ca.tweetzy.auctionhouse.api.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

/**
 * The current file has been created by Kiran Hart
 * Date Created: July 21 2021
 * Time Created: 3:38 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */

@Getter
@Setter
public class AuctionBanPlayerEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    private Player banner;
    private UUID bannedPlayerUUID;
    private String reason;
    private long seconds;

    public AuctionBanPlayerEvent(Player banner, UUID bannedPlayerUUID, String reason, long seconds, boolean async) {
        super(async);
        this.banner = banner;
        this.bannedPlayerUUID = bannedPlayerUUID;
        this.reason = reason;
        this.seconds = seconds;
    }

    public AuctionBanPlayerEvent(Player banner, UUID bannedPlayerUUID, String reason, long seconds) {
        this(banner, bannedPlayerUUID, reason, seconds, true);
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
