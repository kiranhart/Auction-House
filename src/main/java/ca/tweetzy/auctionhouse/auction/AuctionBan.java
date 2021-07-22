package ca.tweetzy.auctionhouse.auction;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

/**
 * The current file has been created by Kiran Hart
 * Date Created: July 21 2021
 * Time Created: 2:25 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */

@Getter
@Setter
public class AuctionBan implements Serializable {

    private UUID bannedPlayer;
    private String reason;
    private long time;

    public AuctionBan(UUID bannedPlayer, String reason, long time) {
        this.bannedPlayer = bannedPlayer;
        this.reason = reason;
        this.time = time;
    }
}
