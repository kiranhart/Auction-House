package ca.tweetzy.auctionhouse.api.hook;

import ca.tweetzy.auctionhouse.api.AuctionAPI;
import io.lumine.mythic.lib.api.item.NBTItem;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

/**
 * The current file has been created by Kiran Hart
 * Date Created: August 07 2021
 * Time Created: 6:36 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */

@UtilityClass
public class MMOItems {

    public boolean isEnabled() {
        return Bukkit.getPluginManager().getPlugin("MMOItems") != null;
    }

    private boolean hasType(@NonNull final NBTItem itemStack) {
        return itemStack.hasType();
    }

    public String getItemType(@NonNull final ItemStack itemStack) {
        NBTItem nbtItem = NBTItem.get(itemStack);
        if (nbtItem == null) return ChatColor.stripColor(AuctionAPI.getInstance().getItemName(itemStack));
        if (!hasType(nbtItem)) return ChatColor.stripColor(AuctionAPI.getInstance().getItemName(itemStack));
        return WordUtils.capitalize(nbtItem.getType().toLowerCase().replace("_", " "));
    }
}
