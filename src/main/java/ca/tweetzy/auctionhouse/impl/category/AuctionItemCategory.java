package ca.tweetzy.auctionhouse.impl.category;

import ca.tweetzy.auctionhouse.api.auction.category.CategoryCondition;
import ca.tweetzy.auctionhouse.api.auction.category.CategoryFieldCondition;
import ca.tweetzy.auctionhouse.api.auction.category.ItemCategory;
import ca.tweetzy.flight.comp.enums.CompMaterial;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.List;

@AllArgsConstructor
public final class AuctionItemCategory implements ItemCategory {

	private final String id;
	private final ItemStack icon;
	private final HashSet<CategoryFieldCondition> fieldConditions;
	private final List<CategoryCondition> conditions;
	private final List<CompMaterial> materials;

	@Override
	public @NonNull String getId() {
		return this.id;
	}

	@Override
	public ItemStack getIcon() {
		return this.icon;
	}

	@Override
	public HashSet<CategoryFieldCondition> getFieldConditions() {
		return this.fieldConditions;
	}

	@Override
	public List<CategoryCondition> getConditions() {
		return this.conditions;
	}

	@Override
	public List<CompMaterial> getMaterialList() {
		return this.materials;
	}
}
