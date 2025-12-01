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
import ca.tweetzy.auctionhouse.auction.AuctionedItem;
import ca.tweetzy.auctionhouse.helpers.AuctionCreator;
import ca.tweetzy.auctionhouse.model.MaterialCategorizer;
import ca.tweetzy.flight.command.AllowedExecutor;
import ca.tweetzy.flight.command.Command;
import ca.tweetzy.flight.command.CommandContext;
import ca.tweetzy.flight.command.ReturnType;
import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.gui.helper.InventorySafeMaterials;
import ca.tweetzy.flight.utils.MathUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * The current file has been created by Kiran Hart
 * Date Created: April 09 2021
 * Time Created: 5:40 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class CommandPop extends Command {

	public CommandPop() {
		super(AllowedExecutor.PLAYER, "pop");
	}

	@Override
	protected ReturnType execute(CommandSender sender, String... args) {
		return execute(new CommandContext(sender, args, getSubCommands().isEmpty() ? "" : getSubCommands().get(0)));
	}

	@Override
	protected ReturnType execute(CommandContext context) {
		if (AuctionAPI.tellMigrationStatus(context.getSender())) return ReturnType.FAIL;
		if (!context.hasArg(0)) return ReturnType.FAIL;

		final Player player = context.getPlayer();
		final int totalListings = MathUtil.isInt(context.getArg(0)) ? Integer.parseInt(context.getArg(0)) : 1;

		for (int i = 0; i < totalListings; i++) {

			final CompMaterial randomItem = getRandomElementFromList(InventorySafeMaterials.get());
			final double price = ThreadLocalRandom.current().nextInt(5, 1000);
			final AuctionedItem listing = new AuctionedItem(
					UUID.randomUUID(),
					player.getUniqueId(),
					player.getUniqueId(),
					player.getName(),
					player.getName(),
					MaterialCategorizer.getMaterialCategory(randomItem.get()),
					randomItem.parseItem(),
					price,
					0,
					0,
					price,
					false,
					false,
					System.currentTimeMillis() + 1000L * 60 * 60
			);

			listing.setCreatedAt(System.currentTimeMillis());

			AuctionCreator.create(AuctionHouse.getAuctionPlayerManager().getPlayer(player.getUniqueId()), listing, (created, listingResult) -> {

			});
		}

		return ReturnType.SUCCESS;

	}

	public <T> T getRandomElementFromList(List<T> list) {
		if (list.isEmpty()) {
			throw new RuntimeException("List is empty");
		}
		Random random = new Random();
		return list.get(random.nextInt(list.size()));
	}

	@Override
	protected List<String> tab(CommandSender sender, String... args) {
		return tab(new CommandContext(sender, args, getSubCommands().isEmpty() ? "" : getSubCommands().get(0)));
	}

	@Override
	protected List<String> tab(CommandContext context) {
		return null;
	}

	@Override
	public String getPermissionNode() {
		return "auctionhouse.cmds.devpopulate";
	}

	@Override
	public String getSyntax() {
		return "dev command";
	}

	@Override
	public String getDescription() {
		return "a dev command";
	}
}
