package ca.tweetzy.auctionhouse.api.auction.category;

import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.api.sync.Identifiable;
import ca.tweetzy.flight.comp.enums.CompMaterial;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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

	default boolean doesItemMatch(final ItemStack itemStack) {
		if (itemStack == null || itemStack.getType() == CompMaterial.AIR.get() || itemStack.getAmount() == 0) return false;
		boolean matches = false;

		final String itemName = AuctionAPI.getInstance().getItemName(itemStack);
		final Material material = itemStack.getType();
		final String materialName = material.name();


		// check the material list
		if (!getMaterialList().isEmpty()) {
			for (CompMaterial compMaterial : getMaterialList()) {
				if (compMaterial.get() == itemStack.getType()) {
					matches = true;
					break;
				}
			}
		}

		// check generic field conditions
		if (!getFieldConditions().isEmpty()) {
			for (CategoryFieldCondition fieldCondition : getFieldConditions()) {
				switch (fieldCondition) {
					case IS_EDIBLE:
						if (material.isEdible() || material == Material.CAKE)
							matches = true;
						break;
					case IS_BLOCK:
						if (material.isBlock())
							matches = true;
						break;
					case IS_ARMOR:
						if (materialName.endsWith("_HELMET") || materialName.endsWith("_CHESTPLATE") || materialName.endsWith("_LEGGINGS") || materialName.endsWith("_BOOTS"))
							matches = true;
						break;
					case IS_TOOL:
						if (materialName.endsWith("_AXE") || materialName.endsWith("_PICKAXE") || materialName.endsWith("_HOE") || materialName.endsWith("SHOVEL"))
							matches = true;
						break;
					case IS_POTION:
						if (material == CompMaterial.BREWING_STAND.get()
								|| material == CompMaterial.CAULDRON.get()
								|| material == CompMaterial.BLAZE_POWDER.get()
								|| material == CompMaterial.POTION.get()
								|| material == CompMaterial.NETHER_WART.get()
								|| material == CompMaterial.REDSTONE.get()
								|| material == CompMaterial.GLOWSTONE_DUST.get()
								|| material == CompMaterial.FERMENTED_SPIDER_EYE.get()
								|| material == CompMaterial.GUNPOWDER.get()
								|| material.name().contains("DRAGONS_BREATH") || material.name().contains("DRAGON_BREATH")
								|| material == CompMaterial.GLISTERING_MELON_SLICE.get()
								|| material == CompMaterial.MAGMA_CREAM.get()
								|| material == CompMaterial.GHAST_TEAR.get()
								|| material == CompMaterial.SUGAR.get()
								|| material == CompMaterial.GOLDEN_CARROT.get()
								|| material == CompMaterial.PUFFERFISH.get()
								|| material == CompMaterial.RABBIT_FOOT.get()
								|| material == CompMaterial.PHANTOM_MEMBRANE.get()
								|| material == CompMaterial.SPIDER_EYE.get()
								|| material == CompMaterial.SPLASH_POTION.get())
							matches = true;
						break;
					case IS_WEAPON:
						if (materialName.endsWith("_SWORD") || materialName.equals("BOW") || materialName.equals("TRIDENT") || materialName.equals("CROSSBOW"))
							matches = true;
						break;
					case IS_ENCHANT:
						if (material == CompMaterial.ENCHANTED_BOOK.get())
							matches = true;
						break;
				}
			}
		}

		// check comparison conditions
		if (!getConditions().isEmpty()) {
			for (CategoryCondition condition : getConditions()) {
				final CategoryConditionType conditionType = condition.getType();
				final CategoryStringComparison stringComparison = condition.getComparisonType();
				final String value = condition.getValue();

				switch (stringComparison) {
					case STARTS_WITH:
						switch (conditionType) {
							case MATERIAL:
								if (materialName.startsWith(value)) matches = true;
								break;
							case NAME:
								if (itemName.startsWith(value)) matches = true;
								break;
							case LORE:
								// this repeats so extract probs
								if (itemStack.getItemMeta() != null && itemStack.getItemMeta().getLore() != null) {
									for (String line : itemStack.getItemMeta().getLore()) {
										if (ChatColor.stripColor(line).startsWith(value)) {
											matches = true;
											break;
										}
									}
								}
								break;
						}
						break;
					case ENDS_WITH:
						switch (conditionType) {
							case MATERIAL:
								if (materialName.endsWith(value)) matches = true;
								break;
							case NAME:
								if (itemName.endsWith(value)) matches = true;
								break;
							case LORE:
								if (itemStack.getItemMeta() != null && itemStack.getItemMeta().getLore() != null) {
									for (String line : itemStack.getItemMeta().getLore()) {
										if (ChatColor.stripColor(line).endsWith(value)) {
											matches = true;
											break;
										}
									}
								}
								break;
						}
						break;
					case CONTAINS:
						switch (conditionType) {
							case MATERIAL:
								if (AuctionAPI.getInstance().match(value, materialName)) matches = true;
								break;
							case NAME:
								if (AuctionAPI.getInstance().match(value, itemName)) matches = true;
								break;
							case LORE:
								if (AuctionAPI.getInstance().matchSearch(value, AuctionAPI.getInstance().getItemLore(itemStack))) matches = true;
								break;
							case ENCHANTMENT:
								// TODO CHECK FOR ENCHANTMENTS
								break;
						}
						break;
					case EQUALS:
						break;
				}
			}
		}

		return matches;
	}
}


//		if (item == null) return false;
//		ItemStack stack = item.getItem();
//		if (stack == null) return false;
//
//		return AuctionAPI.getInstance().matchSearch(phrase, AuctionAPI.getInstance().getItemName(stack))
//		|| AuctionAPI.getInstance().matchSearch(phrase, item.getCategory().getTranslatedType())
//		|| AuctionAPI.getInstance().matchSearch(phrase, stack.getType().name())
//		|| AuctionAPI.getInstance().matchSearch(phrase, item.getOwnerName())
//		|| AuctionAPI.getInstance().matchSearch(phrase, AuctionAPI.getInstance().getItemLore(stack))
//		|| AuctionAPI.getInstance().matchSearch(phrase, AuctionAPI.getInstance().getItemEnchantments(stack));