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
import ca.tweetzy.auctionhouse.api.auction.ListingPriceLimit;
import ca.tweetzy.auctionhouse.guis.admin.GUIPriceLimits;
import ca.tweetzy.auctionhouse.helpers.PlayerHelper;
import ca.tweetzy.auctionhouse.impl.AuctionPriceLimit;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.utils.NumberUtils;
import ca.tweetzy.flight.utils.MathUtil;
import ca.tweetzy.flight.command.AllowedExecutor;
import ca.tweetzy.flight.command.Command;
import ca.tweetzy.flight.command.ReturnType;
import ca.tweetzy.flight.comp.enums.CompMaterial;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * The current file has been created by Kiran Hart
 * Date Created: June 22 2021
 * Time Created: 3:18 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class CommandPriceLimit extends Command {

	public CommandPriceLimit() {
		super(AllowedExecutor.PLAYER, Settings.CMD_ALIAS_SUB_PRICE_LIMIT.getStringList().toArray(new String[0]));
	}

	private boolean isNumeric(String s) {
		if (s == null || s.equals(""))
			return false;
		return s.matches("[-+]?\\d*\\.?\\d+");
	}

	@Override
	protected ReturnType execute(CommandSender sender, String... args) {
		final Player player = (Player) sender;
		if (args.length == 0) {
			AuctionHouse.getGuiManager().showGUI(player, new GUIPriceLimits(player));
			return ReturnType.SUCCESS;
		}

		if (args.length == 3 && args[0].equalsIgnoreCase("set")) {

//			if (!args[1].equalsIgnoreCase("min") || !args[1].equalsIgnoreCase("max")) return ReturnType.INVALID_SYNTAX;

			ItemStack held = PlayerHelper.getHeldItem(player);

			if (held.getType() == CompMaterial.AIR.get()) {
				AuctionHouse.getInstance().getLocale().getMessage("general.min item price air").sendPrefixedMessage(player);
				return ReturnType.FAIL;
			}

			ListingPriceLimit listingPriceLimit = AuctionHouse.getPriceLimitManager().getPriceLimit(held.clone());

			if (!isNumeric(args[2])) {
				AuctionHouse.getInstance().getLocale().getMessage("general.notanumber").processPlaceholder("value", args[2]).sendPrefixedMessage(player);
				return ReturnType.FAIL;
			}

			final double price = Double.parseDouble(args[2]);
			boolean requiresCreate = false;

			if (listingPriceLimit != null) {
				switch (args[1]) {
					case "min":
						listingPriceLimit.setMinPrice(price);
						break;
					case "max":
						listingPriceLimit.setMaxPrice(price);
						break;
				}
			} else {
				requiresCreate = true;
				switch (args[1]) {
					case "min":
						listingPriceLimit = new AuctionPriceLimit(
								UUID.randomUUID(),
								held,
								price,
								-1
						);
						break;
					case "max":
						listingPriceLimit = new AuctionPriceLimit(
								UUID.randomUUID(),
								held,
								Settings.MIN_AUCTION_PRICE.getDouble(),
								price
						);
						break;
				}
			}

			if (requiresCreate) {
				// run store
				listingPriceLimit.store(stored -> {
					if (stored != null) {
						AuctionHouse.getInstance().getLocale().getMessage("pricing.limit.added price limit").sendPrefixedMessage(player);
					}
				});
			} else {
				// run update
				listingPriceLimit.sync(success -> {
					if (success) {
						AuctionHouse.getInstance().getLocale().getMessage("pricing.limit.updated price limit").sendPrefixedMessage(player);
					}
				});
			}
		}

		return ReturnType.SUCCESS;
	}

	@Override
	public String getPermissionNode() {
		return "auctionhouse.cmd.pricelimit";
	}

	@Override
	public String getSyntax() {
		return AuctionHouse.getInstance().getLocale().getMessage("commands.syntax.price limit").getMessage();
	}

	@Override
	public String getDescription() {
		return AuctionHouse.getInstance().getLocale().getMessage("commands.description.price limit").getMessage();
	}

	@Override
	protected List<String> tab(CommandSender sender, String... args) {
		if (args.length == 1) return Collections.singletonList("set");
		if (args.length == 2) return Arrays.asList("min", "max");
		return null;
	}
}
