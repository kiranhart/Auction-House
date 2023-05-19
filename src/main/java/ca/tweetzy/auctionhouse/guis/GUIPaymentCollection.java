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

package ca.tweetzy.auctionhouse.guis;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.api.hook.PlaceholderAPIHook;
import ca.tweetzy.auctionhouse.auction.AuctionPayment;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.helpers.ConfigurationItemHelper;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.flight.utils.Replacer;
import org.bukkit.event.inventory.ClickType;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: March 15 2021
 * Time Created: 3:19 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUIPaymentCollection extends AbstractPlaceholderGui {

	final AuctionPlayer auctionPlayer;

	private List<AuctionPayment> payments;
	private Long lastClicked = null;

	public GUIPaymentCollection(AuctionPlayer auctionPlayer) {
		super(auctionPlayer);
		this.auctionPlayer = auctionPlayer;
		setTitle(TextUtils.formatText(Settings.GUI_PAYMENT_COLLECTION_TITLE.getString()));
		setRows(6);
		setAcceptsItems(false);
		draw();
	}

	public GUIPaymentCollection(AuctionPlayer auctionPlayer, Long lastClicked) {
		this(auctionPlayer);
		this.lastClicked = lastClicked;
	}

	private void draw() {
		reset();
		setButton(5, 0, getBackButtonItem(), e -> e.manager.showGUI(e.player, new GUIExpiredItems(this.auctionPlayer)));

		AuctionHouse.newChain().asyncFirst(() -> {
			this.payments = AuctionHouse.getInstance().getPaymentsManager().getPaymentsByPlayer(this.player);
			return this.payments.stream().sorted(Comparator.comparingLong(AuctionPayment::getTime)).skip((page - 1) * 45L).limit(45).collect(Collectors.toList());
		}).asyncLast((data) -> {
			pages = (int) Math.max(1, Math.ceil(AuctionHouse.getInstance().getPaymentsManager().getPaymentsByPlayer(this.player).size() / (double) 45));
			setPrevPage(5, 3, getPreviousPageItem());
//			setButton(5, 4, getRefreshButtonItem(), e -> draw());
			setNextPage(5, 5, getNextPageItem());
			setOnPage(e -> draw());


			setButton(5, 1, ConfigurationItemHelper.createConfigurationItem(
					Settings.GUI_PAYMENT_COLLECTION_ITEM.getString(),
					PlaceholderAPIHook.PAPIReplacer.tryReplace(this.player, Settings.GUI_PAYMENT_COLLECTION_NAME.getString()),
					PlaceholderAPIHook.PAPIReplacer.tryReplace(this.player, Settings.GUI_PAYMENT_COLLECTION_LORE.getStringList()), null), e -> {

				if (this.lastClicked == null) {
					this.lastClicked = System.currentTimeMillis() + Settings.CLAIM_MS_DELAY.getInt();
				} else if (this.lastClicked > System.currentTimeMillis()) {
					return;
				} else {
					this.lastClicked = System.currentTimeMillis() + Settings.CLAIM_MS_DELAY.getInt();
				}

				for (AuctionPayment auctionPayment : data) {
					auctionPayment.pay(e.player);
				}

				AuctionHouse.getInstance().getDataManager().deletePayments(data.stream().map(AuctionPayment::getId).collect(Collectors.toList()));
				data.forEach(payment -> AuctionHouse.getInstance().getPaymentsManager().removePayment(payment.getId()));

				e.manager.showGUI(e.player, new GUIPaymentCollection(this.auctionPlayer, this.lastClicked));
			});

			int slot = 0;
			for (AuctionPayment auctionPayment : data) {

				setButton(slot++, QuickItem
						.of(Settings.GUI_PAYMENT_COLLECTION_PAYMENT_ITEM.getString())
						.name(PlaceholderAPIHook.PAPIReplacer.tryReplace(this.player, Settings.GUI_PAYMENT_COLLECTION_PAYMENT_NAME.getString().replace("%payment_amount%", AuctionAPI.getInstance().formatNumber(auctionPayment.getAmount()))))
						.lore(Replacer.replaceVariables(
								PlaceholderAPIHook.PAPIReplacer.tryReplace(this.player, Settings.GUI_PAYMENT_COLLECTION_PAYMENT_LORE.getStringList()),
								"item_name", auctionPayment.getItem() == null ? "&cN/A" : AuctionAPI.getInstance().getItemName(auctionPayment.getItem()),
								"from_name", auctionPayment.getFromName(),
								"payment_reason", auctionPayment.getReason().getTranslation()
						))
						.make(), ClickType.LEFT, e -> {

					if (this.lastClicked == null) {
						this.lastClicked = System.currentTimeMillis() + Settings.CLAIM_MS_DELAY.getInt();
					} else if (this.lastClicked > System.currentTimeMillis()) {
						return;
					} else {
						this.lastClicked = System.currentTimeMillis() + Settings.CLAIM_MS_DELAY.getInt();
					}

					auctionPayment.pay(e.player);
					AuctionHouse.getInstance().getDataManager().deletePayments(Collections.singleton(auctionPayment.getId()));
					AuctionHouse.getInstance().getPaymentsManager().removePayment(auctionPayment.getId());

					e.manager.showGUI(e.player, new GUIPaymentCollection(this.auctionPlayer, this.lastClicked));
				});
			}

		}).execute();
	}
}
