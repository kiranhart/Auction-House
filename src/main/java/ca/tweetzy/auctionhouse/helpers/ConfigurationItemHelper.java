package ca.tweetzy.auctionhouse.helpers;

import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.compatibility.XMaterial;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.core.utils.items.ItemUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: February 10 2021
 * Time Created: 1:03 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class ConfigurationItemHelper {

    public static ItemStack createConfigurationItem(String item, String title, List<String> lore, HashMap<String, Object> replacements) {
        ItemStack stack = XMaterial.matchXMaterial(item.toUpperCase()).orElse(XMaterial.RED_STAINED_GLASS_PANE).parseItem();
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(TextUtils.formatText(title));

        if (replacements !=  null) {
            for (int i = 0; i < lore.size(); i++) {
                for (String key : replacements.keySet()) {
                    if (lore.get(i).contains(key)) {
                        lore.set(i, lore.get(i).replace(key, String.valueOf(replacements.get(key))));
                    }
                }
            }
        }

        meta.setLore(lore.stream().map(TextUtils::formatText).collect(Collectors.toList()));
        stack.setItemMeta(meta);
        return stack;
    }
}
