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

package ca.tweetzy.auctionhouse.commands;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.auction.AuctionFilterItem;
import ca.tweetzy.auctionhouse.auction.enums.AuctionItemCategory;
import ca.tweetzy.auctionhouse.guis.filter.GUIFilterWhitelist;
import ca.tweetzy.auctionhouse.helpers.PlayerHelper;
import ca.tweetzy.core.commands.AbstractCommand;
import ca.tweetzy.flight.comp.enums.CompMaterial;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: June 22 2021
 * Time Created: 3:18 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class CommandFilter extends AbstractCommand {

	public CommandFilter() {
		super(CommandType.PLAYER_ONLY, "filter");
	}

	@Override
	protected ReturnType runCommand(CommandSender sender, String... args) {
		final Player player = (Player) sender;
		if (CommandMiddleware.handle(player) == ReturnType.FAILURE) return ReturnType.FAILURE;

		final AuctionHouse instance = AuctionHouse.getInstance();
		if (args.length == 0) {
			instance.getGuiManager().showGUI(player, new GUIFilterWhitelist(player));
			return ReturnType.SUCCESS;
		}

		if (args.length == 2 && args[0].equalsIgnoreCase("additem")) {
			boolean isValid = false;
			for (AuctionItemCategory value : AuctionItemCategory.values()) {
				if (args[1].toUpperCase().equals(value.name())) {
					isValid = true;
					break;
				}
			}

			if (isValid && AuctionItemCategory.valueOf(args[1].toUpperCase()).isWhitelistAllowed()) {

				ItemStack held = PlayerHelper.getHeldItem(player);
				if (held.getType() == CompMaterial.AIR.parseMaterial()) {
					instance.getLocale().getMessage("general.filter air").sendPrefixedMessage(player);
					return ReturnType.FAILURE;
				}


				if (instance.getFilterManager().getFilteredItem(held) != null && instance.getFilterManager().getFilteredItem(held).getCategory() == AuctionItemCategory.valueOf(args[1].toUpperCase())) {
					instance.getLocale().getMessage("general.filteritemaddedalready").sendPrefixedMessage(player);
					return ReturnType.FAILURE;
				}

				AuctionFilterItem filterItem = new AuctionFilterItem(held, AuctionItemCategory.valueOf(args[1].toUpperCase()));
				instance.getFilterManager().addFilterItem(filterItem);
				instance.getLocale().getMessage("general.addeditemtofilterwhitelist").processPlaceholder("item_name", AuctionAPI.getInstance().getItemName(held)).processPlaceholder("filter_category", args[1]).sendPrefixedMessage(player);
			}
		}

		return ReturnType.SUCCESS;
	}

	@Override
	public String getPermissionNode() {
		return "auctionhouse.cmd.filter";
	}

	@Override
	public String getSyntax() {
		return AuctionHouse.getInstance().getLocale().getMessage("commands.syntax.filter").getMessage();
	}

	@Override
	public String getDescription() {
		return AuctionHouse.getInstance().getLocale().getMessage("commands.description.filter").getMessage();
	}

	@Override
	protected List<String> onTab(CommandSender sender, String... args) {
		if (args.length == 1) return Collections.singletonList("additem");
		if (args.length == 2)
			return Arrays.stream(AuctionItemCategory.values()).filter(AuctionItemCategory::isWhitelistAllowed).map(AuctionItemCategory::getTranslatedType).collect(Collectors.toList());
		return null;
	}
}
