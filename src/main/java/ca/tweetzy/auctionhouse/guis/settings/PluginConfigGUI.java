package ca.tweetzy.auctionhouse.guis.settings;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.guis.AuctionPagedGUI;
import ca.tweetzy.core.configuration.Config;
import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.gui.events.GuiClickEvent;
import ca.tweetzy.flight.utils.Pair;
import ca.tweetzy.flight.utils.QuickItem;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class PluginConfigGUI extends AuctionPagedGUI<Pair<String, MemoryConfiguration>> {

	public PluginConfigGUI(Player player) {
		super(null, player, AuctionHouse.getInstance().getLocale().getMessage("general.prefix").getMessage(), 3, new ArrayList<>());
		draw();
	}

	@Override
	protected void prePopulate() {
		this.items.add(new Pair<>(AuctionHouse.getInstance().getCoreConfig().getFile().getName(), AuctionHouse.getInstance().getCoreConfig()));

		List<Config> more = AuctionHouse.getInstance().getExtraConfig();
		if (more != null && !more.isEmpty()) {
			for (Config cfg : more) {
				this.items.add(new Pair<>(cfg.getFile().getName(), cfg));
			}
		}
	}

	@Override
	protected ItemStack makeDisplayItem(Pair<String, MemoryConfiguration> config) {
		return QuickItem.of(CompMaterial.PAPER).name("&e" + config.getFirst()).lore("&cThe in-game editor is currently disabled", "&cit will be fixed in the next update please", "&cuse the config file in the mean time", "&4Sorry for the inconvenience").make();
	}

	@Override
	protected void onClick(Pair<String, MemoryConfiguration> object, GuiClickEvent clickEvent) {

	}

	@Override
	protected List<Integer> fillSlots() {
		return Arrays.asList(13);
	}
}
