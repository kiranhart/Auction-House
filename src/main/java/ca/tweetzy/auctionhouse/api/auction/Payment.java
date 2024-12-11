package ca.tweetzy.auctionhouse.api.auction;

import ca.tweetzy.auctionhouse.api.sync.*;
import ca.tweetzy.auctionhouse.auction.enums.PaymentReason;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public interface Payment extends Identifiable<UUID>, Storeable<Payment>, Unstoreable<SynchronizeResult>, Trackable {

	UUID getTo();

	ItemStack getItem();

	String getFromName();

	PaymentReason getPaymentReason();

	double getAmount();


}
