package ca.tweetzy.auctionhouse.guis.admin.bans;


import ca.tweetzy.auctionhouse.lang.AuctionLocale;
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
		super(null, player, AuctionLocale.msg(player, "gui.ban.title"), 6);
		this.ban = ban;

		setDefaultItem(QuickItem.bg(QuickItem.of(Settings.GUI_BAN_BG_ITEM.getString()).make()));
		draw();
	}

	@Override
	protected void draw() {

		setItem(1, 4, QuickItem
				.of(Bukkit.getOfflinePlayer(ban.getId()))
				.name(AuctionLocale.msg(this.player, "gui.ban.items.player.name").replace("%player_name%", this.ban.locatePlayer().getName()))
				.lore(this.player, AuctionLocale.msgList(this.player, "gui.ban.items.player.lore"))
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
				.name(AuctionLocale.msg(this.player, "gui.ban.items.create.name"))
				.lore(this.player, Replacer.replaceVariables(AuctionLocale.msgList(this.player, "gui.ban.items.create.lore")))
				.make(), click -> {

			if (this.ban.getTypes().isEmpty()) {
				AuctionLocale.tell(click.player, "ban.select ban type");
				return;
			}

			AuctionHouse.getBanManager().registerBan(this.ban, created -> {
				if (created) {
					AuctionLocale.tell(click.player, "ban.user banned", "player_name",this.ban.locatePlayer().getName());
					AuctionHouse.newChain().sync(click.gui::close).execute();
				}
			});
		});
	}

	private void drawTypesButton() {
		setButton(3, 1, QuickItem
				.of(Settings.GUI_BAN_ITEMS_TYPES_ITEM.getString())
				.name(AuctionLocale.msg(this.player, "gui.ban.items.types.name"))
				.lore(this.player, Replacer.replaceVariables(AuctionLocale.msgList(this.player, "gui.ban.items.types.lore"), "ban_type_list", this.ban.getBansAsString()))
				.make(), click -> click.manager.showGUI(click.player, new GUIBanTypeSelection(click.player, this.ban)));
	}

	private void drawPermaButton() {
		setButton(3, 3, QuickItem
				.of(Settings.GUI_BAN_ITEMS_PERMA_ITEM.getString())
				.name(AuctionLocale.msg(this.player, "gui.ban.items.permanent.name"))
				.lore(this.player, Replacer.replaceVariables(AuctionLocale.msgList(this.player, "gui.ban.items.permanent.lore"), "is_true", (this.ban.isPermanent() ? "&aTrue" : "&cFalse")))
				.make(), click -> {

			this.ban.setIsPermanent(!this.ban.isPermanent());
			drawPermaButton();
		});
	}

	private void drawReasonButton() {
		setButton(3, 5, QuickItem
				.of(Settings.GUI_BAN_ITEMS_REASON_ITEM.getString())
				.name(AuctionLocale.msg(this.player, "gui.ban.items.reason.name"))
				.lore(this.player, Replacer.replaceVariables(AuctionLocale.msgList(this.player, "gui.ban.items.reason.lore"), "ban_reason", this.ban.getReason()))
				.make(), click -> new TitleInput(AuctionHouse.getInstance(), click.player, AuctionLocale.msg(null, "titles.ban reason.title"), AuctionLocale.msg(null, "titles.ban reason.subtitle")) {

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
				.name(AuctionLocale.msg(this.player, "gui.ban.items.expiration.name"))
				.lore(this.player, Replacer.replaceVariables(AuctionLocale.msgList(this.player, "gui.ban.items.expiration.lore"), "ban_time", this.ban.getReadableExpirationDate()))
				.make(), click -> new TitleInput(AuctionHouse.getInstance(), click.player, AuctionLocale.msg(null, "titles.ban length.title"), AuctionLocale.msg(null, "titles.ban length.subtitle")) {

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
