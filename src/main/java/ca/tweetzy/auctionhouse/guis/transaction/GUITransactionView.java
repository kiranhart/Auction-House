package ca.tweetzy.auctionhouse.guis.transaction;

import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.transaction.Transaction;
import ca.tweetzy.core.gui.Gui;

/**
 * The current file has been created by Kiran Hart
 * Date Created: March 22 2021
 * Time Created: 7:04 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUITransactionView extends Gui {

    final AuctionPlayer auctionPlayer;
    final Transaction transaction;

    public GUITransactionView(AuctionPlayer auctionPlayer, Transaction transaction) {
        this.auctionPlayer = auctionPlayer;
        this.transaction = transaction;
    }
}
