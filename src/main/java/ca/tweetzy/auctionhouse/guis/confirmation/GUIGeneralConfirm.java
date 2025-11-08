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

package ca.tweetzy.auctionhouse.guis.confirmation;

import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.guis.AuctionBaseGUI;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.flight.utils.QuickItem;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

/**
 * The current file has been created by Kiran Hart
 * Date Created: May 20 2021
 * Time Created: 11:28 a.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUIGeneralConfirm extends AuctionBaseGUI {

	private final AuctionPlayer auctionPlayer;
	private final Consumer<Boolean> confirmed;

	public GUIGeneralConfirm(AuctionPlayer auctionPlayer, ItemStack itemStack, Consumer<Boolean> confirmed) {
		super(null, auctionPlayer.getPlayer(), Settings.GUI_CONFIRM_GENERAL_TITLE.getString(), 1);
		this.auctionPlayer = auctionPlayer;
		this.confirmed = confirmed;
		setAcceptsItems(false);
		setAllowClose(false);

		if (itemStack != null)
			setItem(1,4, itemStack);

		draw();
	}


	@Override
	protected void draw() {
		for (int i = 0; i < 4; i++)
			drawYes(i);

		for (int i = 5; i < 9; i++)
			drawNo(i);
	}

	private void drawNo(int slot) {
		setButton(slot, QuickItem
				.of(Settings.GUI_CONFIRM_GENERAL_NO_ITEM.getString())
				.name(Settings.GUI_CONFIRM_GENERAL_NO_NAME.getString())
				.lore(this.player, Settings.GUI_CONFIRM_GENERAL_NO_LORE.getStringList())
				.make(), click -> {

			setAllowClose(true);
			confirmed.accept(false);
		});
	}

	private void drawYes(int slot) {
		setButton(slot, QuickItem
				.of(Settings.GUI_CONFIRM_GENERAL_YES_ITEM.getString())
				.name(Settings.GUI_CONFIRM_GENERAL_YES_NAME.getString())
				.lore(this.player, Settings.GUI_CONFIRM_GENERAL_YES_LORE.getStringList())
				.make(), click -> {

			setAllowClose(true);
			confirmed.accept(true);
		});
	}
}
