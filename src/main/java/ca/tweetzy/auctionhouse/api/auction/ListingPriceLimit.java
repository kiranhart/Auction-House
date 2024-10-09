package ca.tweetzy.auctionhouse.api.auction;

import ca.tweetzy.auctionhouse.api.sync.*;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public interface ListingPriceLimit extends Identifiable<UUID>, Storeable<ListingPriceLimit>, Unstoreable<SynchronizeResult>, Synchronize {

	ItemStack getItem();

	void setItem(@NonNull ItemStack itemStack);

	double getMinPrice();

	void setMinPrice(final double minPrice);

	double getMaxPrice();

	void setMaxPrice(final double maxPrice);

}
