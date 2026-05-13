/*
 * Auction House
 * Copyright 2023 Kiran Hart
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

package ca.tweetzy.auctionhouse.guis;



import ca.tweetzy.flight.gui.Gui;
import ca.tweetzy.flight.gui.GuiManager;
import ca.tweetzy.flight.hooks.PlaceholderAPIHook;
import ca.tweetzy.flight.utils.input.TitleInput;
import lombok.NonNull;
import org.bukkit.entity.Player;

public abstract class AuctionBaseGUI extends AuctionThemedGUI {

	public AuctionBaseGUI(Gui parent, @NonNull final Player player, @NonNull String title, int rows) {
		super(parent, player, title, rows);
		setTitle(PlaceholderAPIHook.tryReplace(player, title));
	}

	public AuctionBaseGUI(Gui parent, @NonNull final Player player, @NonNull String title) {
		super(parent, player, title);
		setTitle(PlaceholderAPIHook.tryReplace(player, title));
	}

	public AuctionBaseGUI(@NonNull final Player player, @NonNull String title) {
		super(player, title);
		setTitle(PlaceholderAPIHook.tryReplace(player, title));
	}

	/**
	 * Safely transitions from this GUI to a new GUI.
	 * This method handles:
	 * - Setting the transition flag to prevent setOnClose from running
	 * - Properly showing the new GUI
	 *
	 * Note: For updating GUIs (AuctionUpdatingPagedGUI), use the overridden method
	 * which also cancels update tasks.
	 *
	 * @param manager The GuiManager instance
	 * @param newGui The new GUI to transition to
	 */
	protected void safeTransitionTo(@NonNull GuiManager manager, @NonNull Gui newGui) {
		this.transitionTo(manager, this.player, newGui);
	}

	/**
	 * Safely opens a TitleInput.
	 * The TitleInput constructor automatically:
	 * - Sets allowClose=true to prevent GUI from reopening
	 * - Closes the inventory
	 * - Preserves close handlers for item return
	 *
	 * Developers can simply create a TitleInput directly - no manual setup needed!
	 *
	 * @param titleInput The TitleInput instance to open (constructor handles everything)
	 */
	protected void safeOpenTitleInput(@NonNull TitleInput titleInput) {
	}
}
