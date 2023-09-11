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

import ca.tweetzy.auctionhouse.api.hook.PlaceholderAPIHook;
import ca.tweetzy.core.compatibility.XMaterial;
import ca.tweetzy.core.utils.NumberUtils;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.flight.comp.enums.ServerVersion;
import ca.tweetzy.flight.nbtapi.NBT;
import ca.tweetzy.flight.utils.QuickItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
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

	public static ItemStack createConfigurationItem(Player player, ItemStack stack, int model, String title, List<String> lore, HashMap<String, Object> replacements, String... nbtData) {
		if (stack.getType() == XMaterial.AIR.parseMaterial())
			return stack;


		final ItemMeta meta = stack.getItemMeta();
		assert meta != null;
		meta.setDisplayName(TextUtils.formatText(PlaceholderAPIHook.PAPIReplacer.tryReplace(player, title)));

		if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_14))
			meta.setCustomModelData(model);

		if (replacements != null) {
			for (String key : replacements.keySet()) {
				if (title.contains(key)) title = title.replace(key, String.valueOf(replacements.get(key)));
			}

			for (int i = 0; i < lore.size(); i++) {
				for (String key : replacements.keySet()) {
					if (lore.get(i).contains(key)) {
						lore.set(i, lore.get(i).replace(key, String.valueOf(replacements.get(key))));
					}
				}
			}
		}

		meta.setDisplayName(TextUtils.formatText(PlaceholderAPIHook.PAPIReplacer.tryReplace(player, title)));
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_POTION_EFFECTS);

		meta.setLore(PlaceholderAPIHook.PAPIReplacer.tryReplace(player, lore).stream().map(TextUtils::formatText).collect(Collectors.toList()));

		stack.setItemMeta(meta);
		if (nbtData != null) {
			for (String nbt : nbtData) {
				NBT.modify(stack, nbtD -> {
					nbtD.setString(nbt.split(";")[0], nbt.split(";")[1]);
				});
			}
		}
		return stack;
	}

	public static ItemStack createConfigurationItem(Player player, String item) {
		return createConfigurationItem(player, item, " ", new ArrayList<>(), null);
	}

	public static ItemStack createConfigurationItem(Player player, String item, String title, List<String> lore, HashMap<String, Object> replacements) {
		String[] split = item.split(":");

		if (split.length == 2 && NumberUtils.isInt(split[1])) {
			return createConfigurationItem(player, Objects.requireNonNull(XMaterial.matchXMaterial(split[0]).get().parseItem()), Integer.parseInt(split[1]), title, lore, replacements);
		} else {
			return createConfigurationItem(player, Objects.requireNonNull(QuickItem.of(item).make()), -1, title, lore, replacements);

		}
	}

	public static ItemStack createConfigurationItem(Player player, ItemStack item, String title, List<String> lore, HashMap<String, Object> replacements) {
		return createConfigurationItem(player, item, 0, title, lore, replacements);
	}
}
