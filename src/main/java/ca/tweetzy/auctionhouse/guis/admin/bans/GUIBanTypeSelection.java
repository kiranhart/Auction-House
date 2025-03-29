package ca.tweetzy.auctionhouse.guis.admin.bans;

import ca.tweetzy.auctionhouse.api.ban.Ban;
import ca.tweetzy.auctionhouse.api.ban.BanType;
import ca.tweetzy.auctionhouse.guis.AuctionPagedGUI;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.flight.gui.events.GuiClickEvent;
import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.utils.ChatUtil;
import ca.tweetzy.flight.utils.QuickItem;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public final class GUIBanTypeSelection extends AuctionPagedGUI<BanType> {

	private final Ban ban;

	public GUIBanTypeSelection(@NonNull Player player, @NonNull final Ban ban) {
		super(null, player, Settings.GUI_BAN_TYPES_TITLE.getString(), 3, Arrays.asList(BanType.values()));
		this.ban = ban;

		setDefaultItem(QuickItem.bg(QuickItem.of(Settings.GUI_BAN_TYPES_BG_ITEM.getString()).make()));
		setOnClose(close -> close.manager.showGUI(close.player, new GUIBanUser(close.player, this.ban)));
		draw();
	}

	@Override
	protected void drawFixed() {
		setButton(getRows() - 1, 0, getBackButton(), click -> click.manager.showGUI(click.player, new GUIBanUser(click.player, this.ban)));
	}

	@Override
	protected ItemStack makeDisplayItem(BanType banType) {
		return QuickItem
				.of(this.ban.getTypes().contains(banType) ? CompMaterial.LIME_STAINED_GLASS_PANE : CompMaterial.RED_STAINED_GLASS_PANE)
				.name(Settings.GUI_BAN_TYPES_ITEMS_TYPE_NAME.getString().replace("%ban_type%", ChatUtil.capitalizeFully(banType)))
				.lore(this.player, Settings.GUI_BAN_TYPES_ITEMS_TYPE_LORE.getStringList())
				.make();
	}

	@Override
	protected void onClick(BanType banType, GuiClickEvent click) {
		if (this.ban.getTypes().contains(banType))
			this.ban.getTypes().remove(banType);
		else
			this.ban.getTypes().add(banType);

		draw();
	}

	@Override
	protected List<Integer> fillSlots() {
		return Arrays.asList(10, 11, 12, 13, 14, 15, 16);
	}
}
