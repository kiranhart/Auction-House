package ca.tweetzy.auctionhouse.model.manager;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.auction.ListingPriceLimit;
import ca.tweetzy.auctionhouse.api.manager.ListManager;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.flight.comp.enums.ServerVersion;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;

public final class PriceLimitManager extends ListManager<ListingPriceLimit> {

	public PriceLimitManager() {
		super("Listing Price");
	}

	public ListingPriceLimit getPriceLimit(@NonNull ItemStack item) {
		if (Settings.MIN_ITEM_PRICE_USES_SIMPE_COMPARE.getBoolean())
			return this.managerContent.stream().filter(mins -> simpleMatching(mins, item)).findFirst().orElse(null);

		return this.managerContent.stream().filter(mins -> mins.getItem().isSimilar(item)).findFirst().orElse(null);
	}

	private boolean simpleMatching(ListingPriceLimit listingPriceLimit, ItemStack item) {
		boolean modelDataMatch = true;

		boolean typeMatch = listingPriceLimit.getItem().getType() == item.getType();

		if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_14))
			if (listingPriceLimit.getItem().getItemMeta() != null && listingPriceLimit.getItem().getItemMeta().hasCustomModelData() && item.getItemMeta() != null && item.getItemMeta().hasCustomModelData())
				modelDataMatch = listingPriceLimit.getItem().getItemMeta().getCustomModelData() == item.getItemMeta().getCustomModelData();


		return typeMatch && modelDataMatch;
	}

	@Override
	public void load() {
		clear();

		AuctionHouse.getDataManager().getListingPriceLimits((error, prices) -> {
			if (error == null)
				prices.forEach(this::add);
		});
	}
}
