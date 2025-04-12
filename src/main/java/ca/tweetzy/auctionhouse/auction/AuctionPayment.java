/*
 * Auction House
 * Copyright 2023 Kiran Hart
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

package ca.tweetzy.auctionhouse.auction;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.auction.enums.PaymentReason;
import ca.tweetzy.flight.comp.enums.CompMaterial;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@AllArgsConstructor
@Getter
public final class AuctionPayment {

	private final UUID id;
	private final UUID to;
	private final ItemStack item;
	private final String fromName;
	private final PaymentReason reason;

	private final double amount;
	private final long time;

	private final String currency;
	private final ItemStack currencyItem;

	public AuctionPayment(UUID to, double amount, ItemStack item, String fromName, PaymentReason reason, String currency, ItemStack currencyItem) {
		this(UUID.randomUUID(), to, item, fromName, reason, amount, System.currentTimeMillis(), currency, currencyItem);
	}


	public void pay(Player player) {
		final String[] currSplit = currency.split("/");

		if (currencyItem != null && currencyItem.getType() != CompMaterial.AIR.get()) {
			AuctionHouse.getCurrencyManager().deposit(player, currencyItem, (int) this.amount);
		} else {
			AuctionHouse.getCurrencyManager().deposit(player, currSplit[0], currSplit[1], this.amount);
		}

		AuctionHouse.getInstance().getLocale().getMessage("pricing.moneyadd")
				.processPlaceholder("player_balance", AuctionHouse.getAPI().getNumberAsCurrency(AuctionHouse.getCurrencyManager().getBalance(player)))
				.processPlaceholder("price", AuctionHouse.getAPI().getFinalizedCurrencyNumber(this.amount, this.currency, this.currencyItem))
				.sendPrefixedMessage(player);
	}
}