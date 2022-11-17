/*
 * Auction House
 * Copyright 2022 Kiran Hart
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

package ca.tweetzy.auctionhouse.ahv3;

import ca.tweetzy.auctionhouse.ahv3.commands.AuctionHouseCommand;
import ca.tweetzy.flight.FlightPlugin;
import ca.tweetzy.flight.command.CommandManager;
import ca.tweetzy.flight.gui.GuiManager;
import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;

public final class AuctionHouse extends FlightPlugin {

	private static TaskChainFactory taskChainFactory;

	private final CommandManager commandManager = new CommandManager(this);
	private final GuiManager guiManager = new GuiManager(this);

	@Override
	protected void onFlight() {

		taskChainFactory = BukkitTaskChainFactory.create(this);

		this.commandManager.registerCommandDynamically(new AuctionHouseCommand());
	}

	@Override
	protected void onSleep() {

	}

	public static AuctionHouse getInstance() {
		return (AuctionHouse) FlightPlugin.getInstance();
	}

	public static GuiManager getGuiManager() {
		return getInstance().guiManager;
	}


	/**
	 * ======================================================
	 * AIKAR'S TASK CHAIN STUFF
	 * ======================================================
	 */

	public static <T> TaskChain<T> newChain() {
		return taskChainFactory.newChain();
	}

	public static <T> TaskChain<T> newSharedChain(String name) {
		return taskChainFactory.newSharedChain(name);
	}
}
