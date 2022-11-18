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
import ca.tweetzy.auctionhouse.auction.MinItemPrice;
import ca.tweetzy.auctionhouse.guis.GUIMinItemPrices;
import ca.tweetzy.auctionhouse.helpers.PlayerHelper;
import ca.tweetzy.core.commands.AbstractCommand;
import ca.tweetzy.core.compatibility.XMaterial;
import ca.tweetzy.core.utils.NumberUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

/**
 * The current file has been created by Kiran Hart
 * Date Created: June 22 2021
 * Time Created: 3:18 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class CommandMinPrice extends AbstractCommand {

	public CommandMinPrice() {
		super(CommandType.PLAYER_ONLY, "minprices");
	}

	@Override
	protected ReturnType runCommand(CommandSender sender, String... args) {
		final Player player = (Player) sender;
		if (CommandMiddleware.handle(player) == ReturnType.FAILURE) return ReturnType.FAILURE;

		final AuctionHouse instance = AuctionHouse.getInstance();
		if (args.length == 0) {
			instance.getGuiManager().showGUI(player, new GUIMinItemPrices(player));
			return ReturnType.SUCCESS;
		}

		if (args.length == 2 && args[0].equalsIgnoreCase("add")) {

			ItemStack held = PlayerHelper.getHeldItem(player);

			if (held.getType() == XMaterial.AIR.parseMaterial()) {
				instance.getLocale().getMessage("general.min item price air").sendPrefixedMessage(player);
				return ReturnType.FAILURE;
			}

			if (instance.getMinItemPriceManager().getMinPrice(held.clone()) != null) {
				instance.getLocale().getMessage("general.min price already added").sendPrefixedMessage(player);
				return ReturnType.FAILURE;
			}

			if (!NumberUtils.isNumeric(args[1])) {
				instance.getLocale().getMessage("general.notanumber").sendPrefixedMessage(player);
				return ReturnType.FAILURE;
			}

			final double price = Double.parseDouble(args[1]);

			instance.getDataManager().insertMinPriceAsync(new MinItemPrice(held.clone(), price), (error, inserted) -> {
				if (error == null) {
					instance.getMinItemPriceManager().addItem(inserted);
					instance.getLocale().getMessage("general.added min price")
							.processPlaceholder("item", AuctionAPI.getInstance().getItemName(inserted.getItemStack()))
							.processPlaceholder("price", AuctionAPI.getInstance().formatNumber(inserted.getPrice()))
							.sendPrefixedMessage(player);
				}
			});
		}

		return ReturnType.SUCCESS;
	}

	@Override
	public String getPermissionNode() {
		return "auctionhouse.cmd.minprice";
	}

	@Override
	public String getSyntax() {
		return AuctionHouse.getInstance().getLocale().getMessage("commands.syntax.min price").getMessage();
	}

	@Override
	public String getDescription() {
		return AuctionHouse.getInstance().getLocale().getMessage("commands.description.min price").getMessage();
	}

	@Override
	protected List<String> onTab(CommandSender sender, String... args) {
		if (args.length == 1) return Collections.singletonList("add");
		return null;
	}
}
