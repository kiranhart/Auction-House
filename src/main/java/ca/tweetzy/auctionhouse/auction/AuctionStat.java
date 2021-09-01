package ca.tweetzy.auctionhouse.auction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * The current file has been created by Kiran Hart
 * Date Created: September 01 2021
 * Time Created: 3:23 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
@AllArgsConstructor
@Getter
@Setter
public final class AuctionStat<Created, Sold, Expired, Earned, Spent> {

	private Created created;
	private Sold sold;
	private Expired expired;
	private Earned earned;
	private Spent spent;
}
