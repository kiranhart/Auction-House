package ca.tweetzy.auctionhouse.guis;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.auction.AuctionBan;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.helpers.ConfigurationItemHelper;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.gui.Gui;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.core.utils.TimeUtils;
import ca.tweetzy.core.utils.items.TItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: July 22 2021
 * Time Created: 12:16 a.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUIBans extends AbstractPlaceholderGui {

	private List<AuctionBan> bans;
	private BukkitTask task;


	public GUIBans(Player player) {
		super(player);
		setTitle(TextUtils.formatText(Settings.GUI_BANS_TITLE.getString()));
		setDefaultItem(Settings.GUI_BANS_BG_ITEM.getMaterial().parseItem());
		setUseLockedCells(true);
		setAcceptsItems(false);
		setAllowDrops(false);
		setRows(6);
		draw();
		setOnOpen(open -> this.task = Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(AuctionHouse.getInstance(), this::drawItems, 0L, 20L));
		setOnClose(close -> this.task.cancel());
	}

	private void draw() {
		reset();
		setButton(5, 4, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_CLOSE_BTN_ITEM.getString(), Settings.GUI_CLOSE_BTN_NAME.getString(), Settings.GUI_CLOSE_BTN_LORE.getStringList(), null), e -> e.gui.close());
		drawItems();
	}

	private void drawItems() {
		AuctionHouse.newChain().asyncFirst(() -> {
			this.bans = new ArrayList<>(AuctionHouse.getInstance().getAuctionBanManager().getBans().values());
			return this.bans.stream().skip((page - 1) * 45L).limit(45L).collect(Collectors.toList());
		}).asyncLast((data) -> {
			pages = (int) Math.max(1, Math.ceil(this.bans.size() / (double) 45L));
			setPrevPage(5, 3, new TItemBuilder(Objects.requireNonNull(Settings.GUI_BACK_BTN_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_BACK_BTN_NAME.getString()).setLore(Settings.GUI_BACK_BTN_LORE.getStringList()).toItemStack());
			setNextPage(5, 5, new TItemBuilder(Objects.requireNonNull(Settings.GUI_NEXT_BTN_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_NEXT_BTN_NAME.getString()).setLore(Settings.GUI_NEXT_BTN_LORE.getStringList()).toItemStack());
			setOnPage(e -> draw());

			int slot = 0;
			for (AuctionBan ban : data) {
				OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(ban.getBannedPlayer());
				setButton(slot++, ConfigurationItemHelper.createConfigurationItem(AuctionAPI.getInstance().getPlayerHead(offlinePlayer.getName()), Settings.GUI_BANS_BAN_NAME.getString(), Settings.GUI_BANS_BAN_LORE.getStringList(), new HashMap<String, Object>() {{
					put("%player_name%", offlinePlayer.getName());
					put("%player_displayname%", AuctionAPI.getInstance().getDisplayName(offlinePlayer));
					put("%ban_reason%", ban.getReason());
					put("%ban_amount%", (ban.getTime() - System.currentTimeMillis()) <= 0 ? AuctionHouse.getInstance().getLocale().getMessage("bans.ban expired").getMessage() : TimeUtils.makeReadable(ban.getTime() - System.currentTimeMillis()));
				}}), ClickType.RIGHT, e -> {
					AuctionHouse.getInstance().getAuctionBanManager().removeBan(ban.getBannedPlayer());
					AuctionHouse.getInstance().getLocale().getMessage("bans.playerunbanned").processPlaceholder("player_displayname", AuctionAPI.getInstance().getDisplayName(offlinePlayer)).processPlaceholder("player", offlinePlayer.getName()).sendPrefixedMessage(e.player);
					if (offlinePlayer.isOnline()) {
						AuctionHouse.getInstance().getLocale().getMessage("bans.unbanned").processPlaceholder("player_displayname", AuctionAPI.getInstance().getDisplayName(offlinePlayer)).processPlaceholder("player", offlinePlayer.getName()).sendPrefixedMessage(offlinePlayer.getPlayer());
					}
					draw();
				});
			}
		}).execute();
	}
}
