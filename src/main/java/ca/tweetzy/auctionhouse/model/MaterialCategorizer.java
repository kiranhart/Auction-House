/*
 * Auction House
 * Copyright 2018-2022 Kiran Hart
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ca.tweetzy.auctionhouse.model;

import ca.tweetzy.auctionhouse.auction.enums.AuctionItemCategory;
import ca.tweetzy.core.compatibility.XMaterial;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * The current file has been created by Kiran Hart
 * Date Created: February 02 2021
 * Time Created: 5:34 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class MaterialCategorizer {

	public static AuctionItemCategory getMaterialCategory(ItemStack stack) {
		return getMaterialCategory(stack.getType());
	}

	public static AuctionItemCategory getMaterialCategory(Material material) {
		if (material == XMaterial.SPAWNER.parseMaterial()) return AuctionItemCategory.SPAWNERS;
		if (material.isEdible() || material == Material.CAKE) return AuctionItemCategory.FOOD;
		if (material.isBlock()) return AuctionItemCategory.BLOCKS;
		if (material == XMaterial.ENCHANTED_BOOK.parseMaterial()) return AuctionItemCategory.ENCHANTS;

		final String materialName = material.name();
		// Armor filter
		if (materialName.endsWith("_HELMET") || materialName.endsWith("_CHESTPLATE") || materialName.endsWith("_LEGGINGS") || materialName.endsWith("_BOOTS"))
			return AuctionItemCategory.ARMOR;

		// Weapon Filter
		if (materialName.endsWith("_SWORD") || materialName.equals("BOW") || materialName.equals("TRIDENT") || materialName.equals("CROSSBOW"))
			return AuctionItemCategory.WEAPONS;

		// Tool Filter
		if (materialName.endsWith("_AXE") || materialName.endsWith("_PICKAXE") || materialName.endsWith("_HOE") || materialName.endsWith("SHOVEL"))
			return AuctionItemCategory.TOOLS;

		// Potions
		if (material == XMaterial.BREWING_STAND.parseMaterial()
				|| material == XMaterial.CAULDRON.parseMaterial()
				|| material == XMaterial.BLAZE_POWDER.parseMaterial()
				|| material == XMaterial.POTION.parseMaterial()
				|| material == XMaterial.NETHER_WART.parseMaterial()
				|| material == XMaterial.REDSTONE.parseMaterial()
				|| material == XMaterial.GLOWSTONE_DUST.parseMaterial()
				|| material == XMaterial.FERMENTED_SPIDER_EYE.parseMaterial()
				|| material == XMaterial.GUNPOWDER.parseMaterial()
				|| material.name().contains("DRAGONS_BREATH") || material.name().contains("DRAGON_BREATH")
				|| material == XMaterial.GLISTERING_MELON_SLICE.parseMaterial()
				|| material == XMaterial.MAGMA_CREAM.parseMaterial()
				|| material == XMaterial.GHAST_TEAR.parseMaterial()
				|| material == XMaterial.SUGAR.parseMaterial()
				|| material == XMaterial.GOLDEN_CARROT.parseMaterial()
				|| material == XMaterial.PUFFERFISH.parseMaterial()
				|| material == XMaterial.RABBIT_FOOT.parseMaterial()
				|| material == XMaterial.PHANTOM_MEMBRANE.parseMaterial()
				|| material == XMaterial.SPIDER_EYE.parseMaterial()
				|| material == XMaterial.SPLASH_POTION.parseMaterial())
			return AuctionItemCategory.POTIONS;

		return AuctionItemCategory.MISC;
	}
}
