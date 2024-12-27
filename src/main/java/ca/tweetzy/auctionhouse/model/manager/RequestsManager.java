package ca.tweetzy.auctionhouse.model.manager;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.auction.RequestTransaction;
import ca.tweetzy.auctionhouse.api.manager.KeyValueManager;

import java.util.UUID;

public final class RequestsManager extends KeyValueManager<UUID, RequestTransaction> {

	public RequestsManager() {
		super("Requests");
	}

	public void addRequest(RequestTransaction requestTransaction) {
		if (this.managerContent.containsKey(requestTransaction.getId())) return;
		this.managerContent.put(requestTransaction.getId(), requestTransaction);
	}

	@Override
	public void load() {
		this.clear();

		AuctionHouse.getDataManager().getCompletedRequests((error, results) -> {
			if (error == null) {
				results.forEach(this::addRequest);
			}
		});
	}
}
