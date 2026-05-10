package ca.tweetzy.auctionhouse.guis.admin.bans;

import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.auctionhouse.settings.Translations;
import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.api.ban.Ban;
import ca.tweetzy.auctionhouse.api.sync.SynchronizeResult;
import ca.tweetzy.auctionhouse.guis.AuctionPagedGUI;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.flight.gui.events.GuiClickEvent;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.flight.utils.Replacer;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public final class GUIBans extends AuctionPagedGUI<Ban> {

	public GUIBans(@NonNull Player player) {
		super(null, player, TranslationManager.string(player, Translations.GUI_ALL_BANS_TITLE), 6, new ArrayList<>(AuctionHouse.getBanManager().getManagerContent().values()));
		setDefaultItem(QuickItem.bg(QuickItem.of(Settings.GUI_BANS_BG_ITEM.getString()).make()));
		draw();
	}

	@Override
	protected ItemStack makeDisplayItem(Ban ban) {

		return QuickItem
				.of(Bukkit.getOfflinePlayer(ban.getId()))
				.name(TranslationManager.string(this.player, Translations.GUI_ALL_BANS_USER_NAME).replace("%player_name%", ban.locatePlayer().getName()))
				.lore(this.player, Replacer.replaceVariables(TranslationManager.list(this.player, Translations.GUI_ALL_BANS_USER_LORE),
						"ban_banner", Bukkit.getOfflinePlayer(ban.getBanner()).getName(),
						"ban_date", AuctionAPI.getInstance().convertMillisToDate(ban.getTimeCreated()),
						"ban_expiration", ban.getReadableExpirationDate(),
						"is_true", (ban.isPermanent() ? "&aTrue" : "&cFalse"),
						"ban_type_list", ban.getBansAsString()
				)).make();
	}

	@Override
	protected void onClick(Ban ban, GuiClickEvent click) {
		ban.unStore(synchronizeResult -> {
			if (synchronizeResult == SynchronizeResult.SUCCESS)
				click.manager.showGUI(click.player, new GUIBans(click.player));
		});
	}
}
