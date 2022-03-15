package ca.tweetzy.auctionhouse.guis.transaction;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.guis.GUIAuctionHouse;
import ca.tweetzy.auctionhouse.helpers.ConfigurationItemHelper;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.auctionhouse.transaction.Transaction;
import ca.tweetzy.core.gui.Gui;
import ca.tweetzy.core.input.PlayerChatInput;
import ca.tweetzy.core.utils.NumberUtils;
import ca.tweetzy.core.utils.TextUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * The current file has been created by Kiran Hart
 * Date Created: September 21 2021
 * Time Created: 5:32 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public final class GUITransactionType extends Gui {

	private final Player player;

	public GUITransactionType(Player player) {
		this.player = player;
		setTitle(TextUtils.formatText(Settings.GUI_TRANSACTIONS_TYPE_TITLE.getString()));
		setRows(4);
		setAcceptsItems(false);
		setUseLockedCells(true);
		setDefaultItem(Settings.GUI_TRANSACTIONS_TYPE_BG_ITEM.getMaterial().parseItem());
		draw();
	}

	private void draw() {
//		(player.hasPermission("auctionhouse.admin") || player.isOp())
		setButton(11, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_TRANSACTIONS_TYPE_ITEMS_ALL_TRANSACTIONS_ITEM.getString(), Settings.GUI_TRANSACTIONS_TYPE_ITEMS_ALL_TRANSACTIONS_NAME.getString(), Settings.GUI_TRANSACTIONS_TYPE_ITEMS_ALL_TRANSACTIONS_LORE.getStringList(), null), e -> {
			if (Settings.RESTRICT_ALL_TRANSACTIONS_TO_PERM.getBoolean() && !e.player.hasPermission("auctionhouse.transactions.viewall")) {
				AuctionHouse.getInstance().getLocale().getMessage("commands.no_permission").sendPrefixedMessage(e.player);
				return;
			}

			e.manager.showGUI(e.player, new GUITransactionList(e.player, true));
		});

		setButton(15, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_TRANSACTIONS_TYPE_ITEMS_SELF_TRANSACTIONS_ITEM.getString(), Settings.GUI_TRANSACTIONS_TYPE_ITEMS_SELF_TRANSACTIONS_NAME.getString(), Settings.GUI_TRANSACTIONS_TYPE_ITEMS_SELF_TRANSACTIONS_LORE.getStringList(), null), e -> {
			e.manager.showGUI(e.player, new GUITransactionList(e.player, false));
		});

		if (player.isOp() || player.hasPermission("auctionhouse.admin")) {
			setButton(3, 8, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_TRANSACTIONS_TYPE_ITEMS_DELETE_ITEM.getString(), Settings.GUI_TRANSACTIONS_TYPE_ITEMS_DELETE_NAME.getString(), Settings.GUI_TRANSACTIONS_TYPE_ITEMS_DELETE_LORE.getStringList(), null), e -> {
				e.gui.close();
				PlayerChatInput.PlayerChatInputBuilder<Long> builder = new PlayerChatInput.PlayerChatInputBuilder<>(AuctionHouse.getInstance(), e.player);
				builder.isValidInput((p, str) -> {
					String[] parts = ChatColor.stripColor(str).split(" ");
					if (parts.length == 2) {
						return NumberUtils.isInt(parts[0]) && Arrays.asList("second", "minute", "hour", "day", "week", "month", "year").contains(parts[1].toLowerCase());
					}
					return false;
				});
				builder.sendValueMessage(TextUtils.formatText(AuctionHouse.getInstance().getLocale().getMessage("prompts.enter deletion range").getMessage()));
				builder.invalidInputMessage(TextUtils.formatText(AuctionHouse.getInstance().getLocale().getMessage("prompts.enter valid deletion range").getMessage()));
				builder.toCancel("cancel");
				builder.onCancel(p -> e.manager.showGUI(e.player, new GUITransactionType(e.player)));
				builder.setValue((p, value) -> AuctionAPI.toTicks(ChatColor.stripColor(value)));
				builder.onFinish((p, value) -> {
					int seconds = value.intValue();

					AuctionHouse.newChain().async(() -> {
						AuctionHouse.getInstance().getLocale().getMessage("general.transaction delete begin").sendPrefixedMessage(e.player);
						List<UUID> toRemove = new ArrayList<>();

						Set<Map.Entry<UUID, Transaction>> entrySet = AuctionHouse.getInstance().getTransactionManager().getTransactions().entrySet();
						Iterator<Map.Entry<UUID, Transaction>> entryIterator = entrySet.iterator();

						while (entryIterator.hasNext()) {
							Map.Entry<UUID, Transaction> entry = entryIterator.next();
							Transaction transaction = entry.getValue();

							if (Instant.ofEpochMilli(transaction.getTransactionTime()).isBefore(Instant.now().minus(Duration.ofSeconds(seconds)))) {
								toRemove.add(transaction.getId());
								entryIterator.remove();
							}
						}

						AuctionHouse.getInstance().getDataManager().deleteTransactions(toRemove);
						AuctionHouse.getInstance().getLocale().getMessage("general.deleted transactions").processPlaceholder("deleted_transactions", toRemove.size()).sendPrefixedMessage(e.player);
					}).execute();
				});

				PlayerChatInput<Long> input = builder.build();
				input.start();

			});
		}

		setButton(3, 0, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_BACK_BTN_ITEM.getString(), Settings.GUI_BACK_BTN_NAME.getString(), Settings.GUI_BACK_BTN_LORE.getStringList(), null), e -> e.manager.showGUI(e.player, new GUIAuctionHouse(AuctionHouse.getInstance().getAuctionPlayerManager().getPlayer(e.player.getUniqueId()))));
	}
}
