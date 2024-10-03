package ca.tweetzy.auctionhouse.api.auction.category;

import ca.tweetzy.auctionhouse.api.sync.Identifiable;
import ca.tweetzy.flight.comp.enums.CompMaterial;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.List;

public interface ItemCategory extends Identifiable<String> {

	ItemStack getIcon();

	void setIcon(ItemStack item);

	String getName();

	void setName(String name);

	HashSet<CategoryFieldCondition> getFieldConditions();

	List<CategoryCondition> getConditions();

	List<CompMaterial> getMaterialList();
}
