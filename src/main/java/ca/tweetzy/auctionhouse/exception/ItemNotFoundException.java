package ca.tweetzy.auctionhouse.exception;

/**
 * The current file has been created by Kiran Hart
 * Date Created: June 03 2021
 * Time Created: 11:44 a.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class ItemNotFoundException extends NullPointerException {

	public ItemNotFoundException(String errorMessage) {
		super(errorMessage);
	}
}
