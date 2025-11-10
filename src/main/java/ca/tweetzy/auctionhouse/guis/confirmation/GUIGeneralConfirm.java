package ca.tweetzy.auctionhouse.guis.confirmation;

import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.guis.AuctionBaseGUI;
import ca.tweetzy.auctionhouse.model.ConfirmLock;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.flight.utils.QuickItem;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

/**
 * Finalized confirmation GUI.
 * Fully protected under the patched GuiManager + GUISessionLock.
 * Keeps logical-level protections for double-clicks and spam.
 */
public final class GUIGeneralConfirm extends AuctionBaseGUI {

	private final AuctionPlayer auctionPlayer;
	private final Consumer<Boolean> confirmed;
	private boolean alreadyHandled = false;

	public GUIGeneralConfirm(AuctionPlayer auctionPlayer, ItemStack itemStack, Consumer<Boolean> confirmed) {
		super(null, auctionPlayer.getPlayer(), Settings.GUI_CONFIRM_GENERAL_TITLE.getString(), 1);
		this.auctionPlayer = auctionPlayer;
		this.confirmed = confirmed;

		setAcceptsItems(false);
		setAllowClose(false);
		setAllowShiftClick(false);
		setAllowDrops(false);

		if (itemStack != null)
			setItem(1, 4, itemStack);

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
				.make(), ClickType.LEFT, click -> {

			if (alreadyHandled) return;
			alreadyHandled = true;

			setAllowClose(true);
			click.gui.close();
			confirmed.accept(false);
		});
	}

	private void drawYes(int slot) {
		setButton(slot, QuickItem
				.of(Settings.GUI_CONFIRM_GENERAL_YES_ITEM.getString())
				.name(Settings.GUI_CONFIRM_GENERAL_YES_NAME.getString())
				.lore(this.player, Settings.GUI_CONFIRM_GENERAL_YES_LORE.getStringList())
				.make(), ClickType.LEFT, click -> {

			if (alreadyHandled) return;
			alreadyHandled = true;

			// short-term global lock to prevent cross-GUI spam
			if (!ConfirmLock.acquire(click.player.getUniqueId(), 1000L))
				return;

			setAllowClose(true);
			click.gui.close();

			try {
				confirmed.accept(true);
			} finally {
				ConfirmLock.clear(click.player.getUniqueId());
			}
		});
	}
}
