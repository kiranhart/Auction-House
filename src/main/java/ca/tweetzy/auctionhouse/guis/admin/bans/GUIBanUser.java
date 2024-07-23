package ca.tweetzy.auctionhouse.guis.admin.bans;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.ban.Ban;
import ca.tweetzy.auctionhouse.guis.AuctionBaseGUI;
import ca.tweetzy.auctionhouse.helpers.TimeConverter;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.flight.utils.Replacer;
import ca.tweetzy.flight.utils.input.TitleInput;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class GUIBanUser extends AuctionBaseGUI {

	private final Ban ban;

	public GUIBanUser(@NonNull final Player player, @NonNull final Ban ban) {
		super(null, player, Settings.GUI_BAN_TITLE.getString(), 6);
		this.ban = ban;

		setDefaultItem(QuickItem.bg(QuickItem.of(Settings.GUI_BAN_BG_ITEM.getString()).make()));
		draw();
	}

	@Override
	protected void draw() {

		setItem(1, 4, QuickItem
				.of(Bukkit.getOfflinePlayer(ban.getId()))
				.name(Settings.GUI_BAN_ITEMS_PLAYER_NAME.getString().replace("%player_name%", this.ban.locatePlayer().getName()))
				.lore(this.player,Settings.GUI_BAN_ITEMS_PLAYER_LORE.getStringList())
				.make());

		// types
		drawTypesButton();

		// permanent
		drawPermaButton();

		// reason
		drawReasonButton();

		// time
		drawTimeButton();

		setButton(getRows() - 1, 4, QuickItem
				.of(Settings.GUI_BAN_ITEMS_CREATE_ITEM.getString())
				.name(Settings.GUI_BAN_ITEMS_CREATE_NAME.getString())
				.lore(this.player,Replacer.replaceVariables(Settings.GUI_BAN_ITEMS_CREATE_LORE.getStringList()))
				.make(), click -> {

			if (this.ban.getTypes().isEmpty()) {
				AuctionHouse.getInstance().getLocale().getMessage("ban.select ban type").sendPrefixedMessage(click.player);
				return;
			}

			AuctionHouse.getInstance().getBanManager().registerBan(this.ban, created -> {
				if (created) {
					AuctionHouse.getInstance().getLocale().getMessage("ban.user banned").processPlaceholder("player_name", this.ban.locatePlayer().getName()).sendPrefixedMessage(click.player);
					AuctionHouse.newChain().sync(click.gui::close).execute();
				}
			});
		});
	}

	private void drawTypesButton() {
		setButton(3, 1, QuickItem
				.of(Settings.GUI_BAN_ITEMS_TYPES_ITEM.getString())
				.name(Settings.GUI_BAN_ITEMS_TYPES_NAME.getString())
				.lore(this.player,Replacer.replaceVariables(Settings.GUI_BAN_ITEMS_TYPES_LORE.getStringList(), "ban_type_list", this.ban.getBansAsString()))
				.make(), click -> click.manager.showGUI(click.player, new GUIBanTypeSelection(click.player, this.ban)));
	}

	private void drawPermaButton() {
		setButton(3, 3, QuickItem
				.of(Settings.GUI_BAN_ITEMS_PERMA_ITEM.getString())
				.name(Settings.GUI_BAN_ITEMS_PERMA_NAME.getString())
				.lore(this.player,Replacer.replaceVariables(Settings.GUI_BAN_ITEMS_PERMA_LORE.getStringList(), "is_true", (this.ban.isPermanent() ? "&aTrue" : "&cFalse")))
				.make(), click -> {

			this.ban.setIsPermanent(!this.ban.isPermanent());
			drawPermaButton();
		});
	}

	private void drawReasonButton() {
		setButton(3, 5, QuickItem
				.of(Settings.GUI_BAN_ITEMS_REASON_ITEM.getString())
				.name(Settings.GUI_BAN_ITEMS_REASON_NAME.getString())
				.lore(this.player,Replacer.replaceVariables(Settings.GUI_BAN_ITEMS_REASON_LORE.getStringList(), "ban_reason", this.ban.getReason()))
				.make(), click -> new TitleInput(AuctionHouse.getInstance(), click.player, AuctionHouse.getInstance().getLocale().getMessage("titles.ban reason.title").getMessage(), AuctionHouse.getInstance().getLocale().getMessage("titles.ban reason.subtitle").getMessage()) {

			@Override
			public void onExit(Player player) {
				click.manager.showGUI(player, GUIBanUser.this);
			}

			@Override
			public boolean onResult(String string) {
				GUIBanUser.this.ban.setReason(string);
				click.manager.showGUI(click.player, new GUIBanUser(click.player, GUIBanUser.this.ban));
				return true;
			}
		});
	}

	private void drawTimeButton() {
		setButton(3, 7, QuickItem
				.of(Settings.GUI_BAN_ITEMS_TIME_ITEM.getString())
				.name(Settings.GUI_BAN_ITEMS_TIME_NAME.getString())
				.lore(this.player,Replacer.replaceVariables(Settings.GUI_BAN_ITEMS_TIME_LORE.getStringList(), "ban_time", this.ban.getReadableExpirationDate()))
				.make(), click -> new TitleInput(AuctionHouse.getInstance(), click.player, AuctionHouse.getInstance().getLocale().getMessage("titles.ban length.title").getMessage(), AuctionHouse.getInstance().getLocale().getMessage("titles.ban length.subtitle").getMessage()) {

			@Override
			public void onExit(Player player) {
				click.manager.showGUI(player, GUIBanUser.this);
			}

			@Override
			public boolean onResult(String string) {
				string = ChatColor.stripColor(string);

				long time = 0;
				try {
					time = TimeConverter.convertHumanReadableTime(string);
				} catch (IllegalArgumentException e) {
				}

				GUIBanUser.this.ban.setExpireDate(System.currentTimeMillis() + time);
				click.manager.showGUI(click.player, new GUIBanUser(click.player, GUIBanUser.this.ban));
				return true;
			}
		});
	}

}
