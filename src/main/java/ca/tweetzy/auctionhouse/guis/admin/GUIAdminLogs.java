package ca.tweetzy.auctionhouse.guis.admin;

import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.auction.AuctionAdminLog;
import ca.tweetzy.auctionhouse.guis.AbstractPlaceholderGui;
import ca.tweetzy.auctionhouse.helpers.ConfigurationItemHelper;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.core.utils.items.TItemBuilder;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: January 17 2022
 * Time Created: 2:11 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public final class GUIAdminLogs extends AbstractPlaceholderGui {

	final List<AuctionAdminLog> logs;

	public GUIAdminLogs(Player player, List<AuctionAdminLog> logs) {
		super(player);
		this.logs = logs;

		setTitle(TextUtils.formatText(Settings.GUI_LOGS_TITLE.getString()));
		setRows(6);
		setAcceptsItems(false);
		draw();
	}

	private void draw() {
		reset();

		pages = (int) Math.max(1, Math.ceil(this.logs.size() / (double) 45));
		setPrevPage(5, 3, new TItemBuilder(Objects.requireNonNull(Settings.GUI_BACK_BTN_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_BACK_BTN_NAME.getString()).setLore(Settings.GUI_BACK_BTN_LORE.getStringList()).toItemStack());
		setNextPage(5, 5, new TItemBuilder(Objects.requireNonNull(Settings.GUI_NEXT_BTN_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_NEXT_BTN_NAME.getString()).setLore(Settings.GUI_NEXT_BTN_LORE.getStringList()).toItemStack());
		setOnPage(e -> draw());

		int slot = 0;
		List<AuctionAdminLog> data = this.logs.stream().sorted(Comparator.comparingLong(AuctionAdminLog::getTime).reversed()).skip((page - 1) * 45L).limit(45).collect(Collectors.toList());

		for (AuctionAdminLog log : data) {
			setItem(slot++, ConfigurationItemHelper.createConfigurationItem(log.getItem(), AuctionAPI.getInstance().getItemName(log.getItem()), Settings.GUI_LOGS_LORE.getStringList(), new HashMap<String, Object>() {{
				put("%admin%", log.getAdminName());
				put("%target%", log.getTargetName());
				put("%admin_uuid%", log.getAdmin());
				put("%target_uuid%", log.getTarget());
				put("%item_id%", log.getItemId());
				put("%admin_action%", log.getAdminAction().getTranslation());
				put("%admin_log_date%", AuctionAPI.getInstance().convertMillisToDate(log.getTime()));
			}}));
		}
	}
}
