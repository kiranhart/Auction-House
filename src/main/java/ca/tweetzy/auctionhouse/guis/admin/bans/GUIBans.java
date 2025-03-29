package ca.tweetzy.auctionhouse.guis.admin.bans;

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
		super(null, player, Settings.GUI_BANS_TITLE.getString(), 6, new ArrayList<>(AuctionHouse.getBanManager().getManagerContent().values()));
		setDefaultItem(QuickItem.bg(QuickItem.of(Settings.GUI_BANS_BG_ITEM.getString()).make()));
		draw();
	}

	@Override
	protected ItemStack makeDisplayItem(Ban ban) {

		return QuickItem
				.of(Bukkit.getOfflinePlayer(ban.getId()))
				.name(Settings.GUI_BANS_ITEMS_BAN_NAME.getString().replace("%player_name%", ban.locatePlayer().getName()))
				.lore(this.player, Replacer.replaceVariables(Settings.GUI_BANS_ITEMS_BAN_LORE.getStringList(),
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
