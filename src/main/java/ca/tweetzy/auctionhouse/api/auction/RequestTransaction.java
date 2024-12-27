package ca.tweetzy.auctionhouse.api.auction;

import ca.tweetzy.auctionhouse.api.sync.Identifiable;
import ca.tweetzy.auctionhouse.api.sync.Storeable;
import ca.tweetzy.auctionhouse.api.sync.Trackable;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public interface RequestTransaction extends Identifiable<UUID>, Storeable<RequestTransaction>, Trackable {

	ItemStack getRequestedItem();

	int getAmountRequested();

	double getPaymentTotal();

	UUID getRequesterUUID();

	String getRequesterName();

	UUID getFulfillerUUID();

	String getFulfillerName();
}
