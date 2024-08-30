/*
 * Auction House
 * Copyright 2018-2022 Kiran Hart
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ca.tweetzy.auctionhouse.guis.core;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.auction.AuctionPayment;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.guis.AuctionPagedGUI;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.gui.Gui;
import ca.tweetzy.core.gui.events.GuiClickEvent;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.flight.utils.Replacer;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: March 15 2021
 * Time Created: 3:19 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUIPaymentCollection extends AuctionPagedGUI<AuctionPayment> {

	final AuctionPlayer auctionPlayer;
	private Long lastClicked = null;

	public GUIPaymentCollection(Gui parent, AuctionPlayer auctionPlayer) {
		super(parent, auctionPlayer.getPlayer(), Settings.GUI_PAYMENT_COLLECTION_TITLE.getString(), 6, new ArrayList<>(AuctionHouse.getInstance().getPaymentsManager().getPaymentsByPlayer(auctionPlayer.getPlayer())));
		this.auctionPlayer = auctionPlayer;
		draw();
	}

	public GUIPaymentCollection(AuctionPlayer auctionPlayer, Long lastClicked) {
		this(null, auctionPlayer);
		this.lastClicked = lastClicked;
	}

	@Override
	protected void prePopulate() {
		this.items.sort(Comparator.comparingLong(AuctionPayment::getTime));
	}

	@Override
	protected ItemStack makeDisplayItem(AuctionPayment payment) {
		return QuickItem
				.of(Settings.GUI_PAYMENT_COLLECTION_PAYMENT_ITEM.getString())
				.name(Replacer.replaceVariables(Settings.GUI_PAYMENT_COLLECTION_PAYMENT_NAME.getString(), "payment_amount", AuctionHouse.getAPI().getNumberAsCurrency(payment.getAmount(), false)))
				.lore(this.player, Replacer.replaceVariables(Settings.GUI_PAYMENT_COLLECTION_PAYMENT_LORE.getStringList(),
						"item_name", AuctionAPI.getInstance().getItemName(payment.getItem()),
						"from_name", payment.getFromName(),
						"payment_reason", payment.getReason().getTranslation()
				)).make();
	}

	@Override
	protected void onClick(AuctionPayment auctionPayment, GuiClickEvent click) {

		if (this.lastClicked == null) {
			this.lastClicked = System.currentTimeMillis() + Settings.CLAIM_MS_DELAY.getInt();
		} else if (this.lastClicked > System.currentTimeMillis()) {
			return;
		} else {
			this.lastClicked = System.currentTimeMillis() + Settings.CLAIM_MS_DELAY.getInt();
		}

		auctionPayment.pay(click.player);
		AuctionHouse.getDataManager().deletePayments(Collections.singleton(auctionPayment.getId()));
		AuctionHouse.getPaymentsManager().remove(auctionPayment.getId());

		click.manager.showGUI(click.player, new GUIPaymentCollection(this.auctionPlayer, this.lastClicked));
	}

	@Override
	protected void drawFixed() {
		applyBackExit();

		setButton(5, 1, QuickItem.of(Settings.GUI_PAYMENT_COLLECTION_ITEM.getString())
				.name(Settings.GUI_PAYMENT_COLLECTION_NAME.getString())
				.lore(this.player, Settings.GUI_PAYMENT_COLLECTION_LORE.getStringList())
				.make(), e -> {

			if (this.lastClicked == null) {
				this.lastClicked = System.currentTimeMillis() + Settings.CLAIM_MS_DELAY.getInt();
			} else if (this.lastClicked > System.currentTimeMillis()) {
				return;
			} else {
				this.lastClicked = System.currentTimeMillis() + Settings.CLAIM_MS_DELAY.getInt();
			}

			for (AuctionPayment auctionPayment : this.items) {
				auctionPayment.pay(e.player);
			}

			AuctionHouse.getDataManager().deletePayments(this.items.stream().map(AuctionPayment::getId).collect(Collectors.toList()));
			this.items.forEach(payment -> AuctionHouse.getPaymentsManager().remove(payment.getId()));
			e.manager.showGUI(e.player, new GUIPaymentCollection(this.auctionPlayer, this.lastClicked));
		});
	}
}
