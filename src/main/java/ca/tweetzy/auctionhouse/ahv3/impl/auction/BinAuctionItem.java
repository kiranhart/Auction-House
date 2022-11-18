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

package ca.tweetzy.auctionhouse.ahv3.impl.auction;

import ca.tweetzy.auctionhouse.ahv3.api.ListingType;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.function.Consumer;

public final class BinAuctionItem extends AuctionItem {

	private final UUID uuid;
	private final UUID ownerUUID;
	private final String ownerName;

	private BigDecimal binPrice;

	private final String listedWorld;
	private final String listedServer;

	private final long listedAt;
	private long expiresAt;

	public BinAuctionItem(
			@NonNull final UUID uuid,
			@NonNull final UUID ownerUUID,
			@NonNull final String ownerName,
			@NonNull final ItemStack item,
			@NonNull final BigDecimal price,
			@NonNull final String listedWorld,
			@NonNull final String listedServer,
			final long listedAt,
			final long expiresAt
	) {
		super(item, ListingType.BIN);
		this.uuid = uuid;
		this.ownerUUID = ownerUUID;
		this.ownerName = ownerName;
		this.binPrice = price;
		this.listedWorld = listedWorld;
		this.listedServer = listedServer;
		this.listedAt = listedAt;
		this.expiresAt = expiresAt;
	}

	public BinAuctionItem(@NonNull final Player player, @NonNull final ItemStack item, @NonNull final BigDecimal price) {
		this(UUID.randomUUID(), player.getUniqueId(), player.getName(), item, price, player.getWorld().getName(), player.getServer().getName(), System.currentTimeMillis(), System.currentTimeMillis() + 1000 * 60 * 60);
	}

	@NonNull
	@Override
	public UUID getId() {
		return this.uuid;
	}

	@Override
	public UUID getOwner() {
		return this.ownerUUID;
	}

	@Override
	public String getOwnerName() {
		return this.ownerName;
	}

	@Override
	public ItemStack getItem() {
		return this.item;
	}

	@Override
	public ListingType getType() {
		return this.listingType;
	}

	@Override
	public BigDecimal getBinPrice() {
		return this.binPrice;
	}

	@Override
	public String getListedWorld() {
		return this.listedWorld;
	}

	@Override
	public String getListedServer() {
		return this.listedServer;
	}

	@Override
	public long getListedAt() {
		return this.listedAt;
	}

	@Override
	public long getExpirationTime() {
		return this.expiresAt;
	}

	@Override
	public void setExpirationTime(long expirationTime) {
		this.expiresAt = expirationTime;
	}

	@Override
	public void setBinPrice(BigDecimal binPrice) {
		this.binPrice = binPrice;
	}

	@Override
	public long getTimeCreated() {
		return this.listedAt;
	}

	@Override
	public long getLastUpdated() {
		return 0;
	}

	@Override
	public void sync(Consumer<Boolean> wasSuccess) {

	}
}
