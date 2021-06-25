package ca.tweetzy.auctionhouse.exception;

/**
 * The current file has been created by Kiran Hart
 * Date Created: June 24 2021
 * Time Created: 11:17 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class EconomyProviderNotFoundException extends NullPointerException {

    public EconomyProviderNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
