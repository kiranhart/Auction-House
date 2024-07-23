package ca.tweetzy.auctionhouse.guis.selector;

import ca.tweetzy.auctionhouse.guis.AuctionPagedGUI;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.gui.events.GuiClickEvent;
import ca.tweetzy.flight.utils.QuickItem;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.function.Consumer;

public final class GUIPlayerSelector extends AuctionPagedGUI<OfflinePlayer> {

	private final Consumer<OfflinePlayer> selectedPlayer;

	public GUIPlayerSelector(@NonNull final Player player, @NonNull final Consumer<OfflinePlayer> selectedPlayer) {
		super(null, player, Settings.GUI_PLAYER_SELECTOR_TITLE.getString(), 6, Arrays.asList(Bukkit.getOfflinePlayers()));
		this.selectedPlayer = selectedPlayer;
		draw();
	}

	@Override
	protected ItemStack makeDisplayItem(OfflinePlayer player) {
		final String name = player.hasPlayedBefore() && player.getName() != null ? player.getName() : "Unknown Name o.O";

		return QuickItem
				.of(player)
				.name(Settings.GUI_PLAYER_SELECTOR_ITEMS_PLAYER_NAME.getString().replace("%player_name%", name))
				.lore(this.player,Settings.GUI_PLAYER_SELECTOR_ITEMS_PLAYER_LORE.getStringList())
				.make();
	}

	@Override
	protected void onClick(OfflinePlayer player, GuiClickEvent click) {
		this.selectedPlayer.accept(player);
	}
}
