package ca.tweetzy.auctionhouse.guis.settings;

import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.auctionhouse.settings.Translations;
import ca.tweetzy.auctionhouse.guis.AuctionPagedGUI;
import ca.tweetzy.flight.FlightPlugin;
import ca.tweetzy.flight.config.tweetzy.TweetzyYamlConfig;
import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.gui.events.GuiClickEvent;
import ca.tweetzy.flight.utils.Pair;
import ca.tweetzy.flight.utils.QuickItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class PluginConfigGUI extends AuctionPagedGUI<Pair<String, TweetzyYamlConfig>> {

	public PluginConfigGUI(Player player) {
		super(null, player, TranslationManager.string(Translations.GENERAL_PREFIX), 3, new ArrayList<>());
		draw();
	}

	@Override
	protected void prePopulate() {
		this.items.add(new Pair<>(FlightPlugin.getCoreConfig().file.getName(), FlightPlugin.getCoreConfig()));
	}

	@Override
	protected ItemStack makeDisplayItem(Pair<String, TweetzyYamlConfig> config) {
		return QuickItem.of(CompMaterial.PAPER).name("&e" + config.getFirst()).lore("&cThe in-game editor is currently disabled", "&cit will be fixed in the next update please", "&cuse the config file in the mean time", "&4Sorry for the inconvenience").make();
	}

	@Override
	protected void onClick(Pair<String, TweetzyYamlConfig> object, GuiClickEvent clickEvent) {

	}

	@Override
	protected List<Integer> fillSlots() {
		return Collections.singletonList(13);
	}
}
