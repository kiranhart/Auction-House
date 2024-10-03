package ca.tweetzy.auctionhouse.impl.category;

import ca.tweetzy.auctionhouse.api.auction.category.CategoryCondition;
import ca.tweetzy.auctionhouse.api.auction.category.CategoryFieldCondition;
import ca.tweetzy.auctionhouse.api.auction.category.ItemCategory;
import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.utils.ChatUtil;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@AllArgsConstructor
public final class AuctionItemCategory implements ItemCategory {

	private final String id;
	private String name;
	private ItemStack icon;
	private final HashSet<CategoryFieldCondition> fieldConditions;
	private final List<CategoryCondition> conditions;
	private final List<CompMaterial> materials;

	public AuctionItemCategory(@NonNull final String id, @NonNull final String name) {
		this(id, name, CompMaterial.PAPER.parseItem(), new HashSet<>(), new ArrayList<>(), new ArrayList<>());
	}

	public AuctionItemCategory(@NonNull final String id) {
		this(id, ChatUtil.capitalizeFully(id), CompMaterial.PAPER.parseItem(), new HashSet<>(), new ArrayList<>(), new ArrayList<>());
	}


	@Override
	public @NonNull String getId() {
		return this.id;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public ItemStack getIcon() {
		return this.icon;
	}

	@Override
	public void setIcon(ItemStack item) {
		this.icon = item;
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
