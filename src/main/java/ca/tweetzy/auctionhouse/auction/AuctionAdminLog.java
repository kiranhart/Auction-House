package ca.tweetzy.auctionhouse.auction;

import ca.tweetzy.auctionhouse.auction.enums.AdminAction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * The current file has been created by Kiran Hart
 * Date Created: January 17 2022
 * Time Created: 1:35 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
@AllArgsConstructor
@Getter
public final class AuctionAdminLog {

	private final UUID admin;
	private final String adminName;
	private final UUID target;
	private final String targetName;
	private final ItemStack item;
	private final UUID itemId;
	private final AdminAction adminAction;
	private final long time;
}
