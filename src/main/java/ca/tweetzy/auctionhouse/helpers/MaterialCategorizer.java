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

package ca.tweetzy.auctionhouse.helpers;

import ca.tweetzy.auctionhouse.auction.enums.AuctionItemCategory;
import ca.tweetzy.flight.comp.enums.CompMaterial;
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
		if (material == CompMaterial.SPAWNER.parseMaterial()) return AuctionItemCategory.SPAWNERS;
		if (material.isEdible()) return AuctionItemCategory.FOOD;
		if (material.isBlock()) return AuctionItemCategory.BLOCKS;
		if (material == CompMaterial.ENCHANTED_BOOK.parseMaterial()) return AuctionItemCategory.ENCHANTS;

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
		return AuctionItemCategory.MISC;
	}
}
