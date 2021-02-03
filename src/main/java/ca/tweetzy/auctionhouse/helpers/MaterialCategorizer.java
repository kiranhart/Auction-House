package ca.tweetzy.auctionhouse.helpers;

import ca.tweetzy.auctionhouse.auction.AuctionItemCategory;
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
        if (material.isEdible()) return AuctionItemCategory.FOOD;
        if (material.isBlock()) return AuctionItemCategory.BLOCKS;
        if (material.name().endsWith("_HELMET") || material.name().endsWith("_CHESTPLATE") || material.name().endsWith("_LEGGINGS") || material.name().endsWith("_BOOTS")) return AuctionItemCategory.ARMOR;
        if (material.name().endsWith("_AXE") || material.name().endsWith("_PICKAXE") || material.name().endsWith("_SWORD") || material.name().endsWith("_HOE") || material.name().endsWith("SHOVEL") || material.name().equals("BOW")) return AuctionItemCategory.TOOLS;
        return AuctionItemCategory.MISC;
    }
}
