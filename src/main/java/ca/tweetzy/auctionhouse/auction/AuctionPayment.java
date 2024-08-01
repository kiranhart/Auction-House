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
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.auction.enums.PaymentReason;
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

	public AuctionPayment(UUID to, double amount, ItemStack item, String fromName, PaymentReason reason) {
		this(UUID.randomUUID(), to, item, fromName, reason, amount, System.currentTimeMillis());
	}


	public void pay(Player player) {
		AuctionHouse.getCurrencyManager().deposit(player, this.amount);
		AuctionHouse.getInstance().getLocale().getMessage("pricing.moneyadd").processPlaceholder("player_balance", AuctionAPI.getInstance().formatNumber(AuctionHouse.getCurrencyManager().getBalance(player))).processPlaceholder("price", AuctionAPI.getInstance().formatNumber(this.amount)).sendPrefixedMessage(player);
	}
}