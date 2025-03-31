package ca.tweetzy.auctionhouse.guis.selector;

import ca.tweetzy.auctionhouse.guis.AuctionPagedGUI;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.gui.events.GuiClickEvent;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.flight.utils.profiles.builder.XSkull;
import ca.tweetzy.flight.utils.profiles.objects.ProfileInputType;
import ca.tweetzy.flight.utils.profiles.objects.Profileable;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public final class GUIPlayerSelector extends AuctionPagedGUI<OfflinePlayer> {

	private final Consumer<OfflinePlayer> selectedPlayer;

	public GUIPlayerSelector(@NonNull final Player player, @NonNull final Consumer<OfflinePlayer> selectedPlayer) {
		super(null, player, Settings.GUI_PLAYER_SELECTOR_TITLE.getString(), 6, new ArrayList<>());
		setAsync(true);
		this.selectedPlayer = selectedPlayer;
		draw();
	}

	@Override
	protected void prePopulate() {
		this.items.addAll(getOnlineOfflinePlayers());
	}

	@Override
	protected ItemStack makeDisplayItem(OfflinePlayer player) {
		if (player == null || !player.hasPlayedBefore()) {
			return QuickItem.of("http://textures.minecraft.net/texture/ee7700096b5a2a87386d6205b4ddcc14fd33cf269362fa6893499431ce77bf9").name("&eUnknown Player").make();
		}

		final String name = player.hasPlayedBefore() && player.getName() != null ? player.getName() : "Unknown Name o.O";

		QuickItem item = QuickItem
				.of(CompMaterial.PLAYER_HEAD)
				.name(Settings.GUI_PLAYER_SELECTOR_ITEMS_PLAYER_NAME.getString().replace("%player_name%", name))
				.lore(this.player, Settings.GUI_PLAYER_SELECTOR_ITEMS_PLAYER_LORE.getStringList());


		return XSkull
				.of(item.make())
				.profile(Profileable.of(player))
				.fallback(Profileable.of(
						ProfileInputType.TEXTURE_URL,
						"http://textures.minecraft.net/texture/ee7700096b5a2a87386d6205b4ddcc14fd33cf269362fa6893499431ce77bf9"
				))
				.lenient()
				.apply();
	}

	@Override
	protected void onClick(OfflinePlayer player, GuiClickEvent click) {
		this.selectedPlayer.accept(player);
	}

	private List<OfflinePlayer> getOnlineOfflinePlayers() {
		final List<OfflinePlayer> players = new ArrayList<>(Arrays.asList(Bukkit.getOfflinePlayers()));
		Bukkit.getOnlinePlayers().forEach(player -> {
			if (players.stream().anyMatch(target -> target.getUniqueId().equals(player.getUniqueId()))) return;
			players.add(player);
		});

		return players;
	}
}
