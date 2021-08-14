package ca.tweetzy.auctionhouse.database;

import org.jetbrains.annotations.Nullable;

/**
 * The current file has been created by Kiran Hart
 * Date Created: August 01 2021
 * Time Created: 1:50 a.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public interface Callback<T> {

    void accept(@Nullable Exception ex, T result);
}
