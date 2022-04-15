package ca.tweetzy.auctionhouse.guis;

import ca.tweetzy.auctionhouse.api.hook.PlaceholderAPIHook;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.gui.Gui;
import ca.tweetzy.core.gui.GuiUtils;
import ca.tweetzy.core.utils.TextUtils;
import org.bukkit.entity.Player;

/**
 * Date Created: April 07 2022
 * Time Created: 9:33 p.m.
 *
 * @author Kiran Hart
 */
public abstract class AbstractPlaceholderGui extends Gui {

	private final Player player;

	public AbstractPlaceholderGui(Player player) {
		this.player = player;
		// will be overriden by other guis
		setDefaultItem(GuiUtils.createButtonItem(Settings.GUI_FILLER.getMaterial(), " "));
	}

	public AbstractPlaceholderGui(AuctionPlayer player) {
		this.player = player.getPlayer();
	}


	@Override
	public Gui setTitle(String message) {
		super.setTitle(this.player == null ? TextUtils.formatText(message) : TextUtils.formatText(PlaceholderAPIHook.PAPIReplacer.tryReplace(this.player, message)));
		return this;
	}
}
