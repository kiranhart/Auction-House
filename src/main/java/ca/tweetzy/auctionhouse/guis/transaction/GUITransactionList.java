package ca.tweetzy.auctionhouse.guis.transaction;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.guis.GUIAuctionHouse;
import ca.tweetzy.auctionhouse.helpers.ConfigurationItemHelper;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.auctionhouse.transaction.Transaction;
import ca.tweetzy.core.gui.Gui;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.core.utils.items.TItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: March 22 2021
 * Time Created: 7:03 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUITransactionList extends Gui {

	final List<Transaction> transactions;
	final AuctionPlayer auctionPlayer;
	final Player player;
	final boolean showAll;


	public GUITransactionList(Player player, boolean showAll) {
		this.player = player;
		this.auctionPlayer = AuctionHouse.getInstance().getAuctionPlayerManager().getPlayer(this.player.getUniqueId());
		this.showAll = showAll;
		if (showAll)
			this.transactions = new ArrayList<>(AuctionHouse.getInstance().getTransactionManager().getTransactions().values());
		else
			this.transactions = AuctionHouse.getInstance().getTransactionManager().getTransactions().values().stream().filter(transaction -> transaction.getSeller().equals(player.getUniqueId()) || transaction.getBuyer().equals(player.getUniqueId())).collect(Collectors.toList());

		setTitle(TextUtils.formatText(showAll ? Settings.GUI_TRANSACTIONS_TITLE_ALL.getString() : Settings.GUI_TRANSACTIONS_TITLE.getString()));
		setRows(6);
		setAcceptsItems(false);
		draw();
	}

	private void draw() {
		reset();

		pages = (int) Math.max(1, Math.ceil(this.transactions.size() / (double) 45));
		setPrevPage(5, 3, new TItemBuilder(Objects.requireNonNull(Settings.GUI_BACK_BTN_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_BACK_BTN_NAME.getString()).setLore(Settings.GUI_BACK_BTN_LORE.getStringList()).toItemStack());
		setButton(5, 4, new TItemBuilder(Objects.requireNonNull(Settings.GUI_REFRESH_BTN_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_REFRESH_BTN_NAME.getString()).setLore(Settings.GUI_REFRESH_BTN_LORE.getStringList()).toItemStack(), e -> e.manager.showGUI(e.player, new GUITransactionList(this.player, this.showAll)));
		setNextPage(5, 5, new TItemBuilder(Objects.requireNonNull(Settings.GUI_NEXT_BTN_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_NEXT_BTN_NAME.getString()).setLore(Settings.GUI_NEXT_BTN_LORE.getStringList()).toItemStack());
		setOnPage(e -> draw());

		// Other Buttons
		setButton(5, 0, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_BACK_BTN_ITEM.getString(), Settings.GUI_BACK_BTN_NAME.getString(), Settings.GUI_BACK_BTN_LORE.getStringList(), null), e -> {
			if (Settings.RESTRICT_ALL_TRANSACTIONS_TO_PERM.getBoolean() && !e.player.hasPermission("auctionhouse.transactions.viewall")) {
				e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
			} else {
				e.manager.showGUI(e.player, new GUITransactionType(e.player));
			}
		});

		int slot = 0;
		List<Transaction> data = this.transactions.stream().sorted(Comparator.comparingLong(Transaction::getTransactionTime).reversed()).skip((page - 1) * 45L).limit(45).collect(Collectors.toList());

		for (Transaction transaction : data) {
			setButton(slot++, ConfigurationItemHelper.createConfigurationItem(transaction.getItem(), Settings.GUI_TRANSACTIONS_ITEM_TRANSACTION_NAME.getString(), Settings.GUI_TRANSACTIONS_ITEM_TRANSACTION_LORE.getStringList(), new HashMap<String, Object>() {{
				put("%transaction_id%", transaction.getId().toString());
				put("%seller%", Bukkit.getOfflinePlayer(transaction.getSeller()).getName());
				put("%buyer%", Bukkit.getOfflinePlayer(transaction.getBuyer()).getName());
				put("%date%", AuctionAPI.getInstance().convertMillisToDate(transaction.getTransactionTime()));
				put("%item_name%", AuctionAPI.getInstance().getItemName(transaction.getItem()));
			}}), e -> e.manager.showGUI(e.player, new GUITransactionView(this.auctionPlayer, transaction, this.showAll)));
		}
	}
}
