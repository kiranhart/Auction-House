package ca.tweetzy.auctionhouse.impl;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.auction.RequestTransaction;
import ca.tweetzy.auctionhouse.api.auction.TransactionWrapper;
import ca.tweetzy.auctionhouse.auction.AuctionedItem;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.function.Consumer;

@AllArgsConstructor
public final class CompletedRequest extends TransactionWrapper implements RequestTransaction {

	private UUID id;

	private ItemStack requestedItem;
	private int amount;
	private double payment;

	private UUID requesterUUID;
	private String requesterName;

	private UUID fulfillerUUID;
	private String fulfillerName;

	private long timeCompleted;

	public CompletedRequest(@NonNull AuctionedItem listing, Player fulfiller, double price) {
		this(UUID.randomUUID(), listing.getItem(), listing.getRequestAmount(), price, listing.getOwner(), listing.getOwnerName(), fulfiller.getUniqueId(), fulfiller.getName(), System.currentTimeMillis());
	}

	@Override
	public @NonNull UUID getId() {
		return this.id;
	}

	@Override
	public ItemStack getRequestedItem() {
		return this.requestedItem;
	}

	@Override
	public int getAmountRequested() {
		return this.amount;
	}

	@Override
	public double getPaymentTotal() {
		return this.payment;
	}

	@Override
	public UUID getRequesterUUID() {
		return this.requesterUUID;
	}

	@Override
	public String getRequesterName() {
		return this.requesterName;
	}

	@Override
	public UUID getFulfillerUUID() {
		return this.fulfillerUUID;
	}

	@Override
	public String getFulfillerName() {
		return this.fulfillerName;
	}


	@Override
	public void store(Consumer<RequestTransaction> stored) {
		AuctionHouse.getDataManager().insertCompletedRequest(this, (error, completedRequest) -> {
			if (error != null) return;

			if (completedRequest != null) {
				AuctionHouse.getRequestsManager().addRequest(completedRequest);

				if (stored != null)
					stored.accept(completedRequest);
			}
		});
	}

	@Override
	public long getTimeCreated() {
		return this.timeCompleted;
	}

	@Override
	public long getLastUpdated() {
		return this.timeCompleted;
	}
}
