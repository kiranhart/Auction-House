package ca.tweetzy.auctionhouse.auction.enums;

import ca.tweetzy.auctionhouse.AuctionHouse;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * The current file has been created by Kiran Hart
 * Date Created: January 17 2022
 * Time Created: 1:29 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
@AllArgsConstructor
public enum AdminAction {

	RETURN_ITEM(AuctionHouse.getInstance().getLocale().getMessage("admin action.return").getMessage()),
	CLAIM_ITEM(AuctionHouse.getInstance().getLocale().getMessage("admin action.claim").getMessage()),
	DELETE_ITEM(AuctionHouse.getInstance().getLocale().getMessage("admin action.delete").getMessage()),
	COPY_ITEM(AuctionHouse.getInstance().getLocale().getMessage("admin action.copy").getMessage());

	@Getter
	private final String translation;
}
