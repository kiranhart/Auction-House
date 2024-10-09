package ca.tweetzy.auctionhouse.impl;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.auction.ListingPriceLimit;
import ca.tweetzy.auctionhouse.api.sync.SynchronizeResult;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.function.Consumer;

public final class AuctionPriceLimit implements ListingPriceLimit {

	private final UUID id;
	private ItemStack item;
	private double minPrice;
	private double maxPrice;

	public AuctionPriceLimit(@NonNull final UUID id, @NonNull final ItemStack item, final double minPrice, final double maxPrice) {
		this.id = id;
		this.item = item;
		this.minPrice = minPrice;
		this.maxPrice = maxPrice;
	}

	public AuctionPriceLimit(@NonNull final ItemStack item, final double minPrice, final double maxPrice) {
		this(UUID.randomUUID(), item, minPrice, maxPrice);
	}

	@Override
	public @NonNull UUID getId() {
		return this.id;
	}

	@Override
	public ItemStack getItem() {
		return this.item;
	}

	@Override
	public void setItem(@NonNull ItemStack itemStack) {
		this.item = itemStack;
	}

	@Override
	public double getMinPrice() {
		return this.minPrice;
	}

	@Override
	public void setMinPrice(double minPrice) {
		this.minPrice = minPrice;
	}

	@Override
	public double getMaxPrice() {
		return this.maxPrice;
	}

	@Override
	public void setMaxPrice(double maxPrice) {
		this.maxPrice = maxPrice;
	}

	@Override
	public void store(Consumer<ListingPriceLimit> stored) {
		AuctionHouse.getDataManager().insertListingPriceLimit(this, (error, result) -> {
			if (error == null) {
				AuctionHouse.getPriceLimitManager().add(result);
			}

			if (stored != null)
				stored.accept(result);
		});
	}

	@Override
	public void sync(Consumer<Boolean> wasSuccess) {
		AuctionHouse.getDataManager().updateListingPriceLimit(this, (error, result) -> {
			if (wasSuccess != null)
				wasSuccess.accept(error == null);
		});
	}

	@Override
	public void unStore(Consumer<SynchronizeResult> status) {
		AuctionHouse.getDataManager().deleteListingPriceLimit(getId(), (error, success) -> {
			if (error == null && success) {
				AuctionHouse.getPriceLimitManager().remove(this);
			}

			if (status != null)
				status.accept(success && error == null ? SynchronizeResult.SUCCESS : SynchronizeResult.FAILURE);
		});
	}
}
