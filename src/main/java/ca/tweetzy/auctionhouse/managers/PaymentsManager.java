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

package ca.tweetzy.auctionhouse.managers;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.auction.AuctionPayment;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: March 22 2021
 * Time Created: 3:34 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class PaymentsManager {

	private final ConcurrentHashMap<UUID, AuctionPayment> payments = new ConcurrentHashMap<>();

	public void addPayment(AuctionPayment payment) {
		if (payment == null) return;
		this.payments.put(payment.getId(), payment);
	}

	public void removePayment(UUID uuid) {
		this.payments.remove(uuid);
	}

	public ConcurrentHashMap<UUID, AuctionPayment> getPayments() {
		return this.payments;
	}

	public List<AuctionPayment> getPaymentsByPlayer(Player player) {
		return this.payments.values().stream().filter(payment -> payment.getTo().equals(player.getUniqueId())).collect(Collectors.toList());
	}

	public void loadPayments() {
		AuctionHouse.getInstance().getDataManager().getAuctionPayments((error, results) -> {
			if (error == null) {
				for (AuctionPayment payment : results) {
					addPayment(payment);
				}
			}
		});
	}
}
