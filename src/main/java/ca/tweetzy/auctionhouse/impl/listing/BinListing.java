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

package ca.tweetzy.auctionhouse.impl.listing;

import ca.tweetzy.auctionhouse.api.auction.ListingDisplayMode;
import ca.tweetzy.auctionhouse.api.auction.ListingType;
import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.utils.ItemUtil;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BinListing extends AuctionItem {

	private final UUID uuid;
	private final UUID ownerUUID;
	private final String ownerName;

	private String currency;
	private ItemStack currencyItem;
	private double binPrice;

	private final String listedWorld;
	private final String listedServer;

	private final long listedAt;
	private long expiresAt;

	@Getter
	@Setter
	private boolean allowPartialBuy = false;

	private boolean isPriorityListing = false;
	private long priorityExpiration = -1L;

	private boolean isInfinite = false;
	private boolean isBeingBought = false;
	private boolean isArchived = false;

	public BinListing(
			@NonNull final ListingType listingType,
			@NonNull final UUID uuid,
			@NonNull final UUID ownerUUID,
			@NonNull final String ownerName,
			@NonNull final ItemStack item,
			@NonNull final String currency,
			@NonNull final ItemStack currencyItem,
			final double price,
			@NonNull final String listedWorld,
			@NonNull final String listedServer,
			final long listedAt,
			final long expiresAt
	) {
		super(item, listingType);
		this.uuid = uuid;
		this.ownerUUID = ownerUUID;
		this.ownerName = ownerName;
		this.currency = currency;
		this.currencyItem = currencyItem;
		this.binPrice = price;
		this.listedWorld = listedWorld;
		this.listedServer = listedServer;
		this.listedAt = listedAt;
		this.expiresAt = expiresAt;
	}

	public BinListing(
			@NonNull final ListingType listingType,
			@NonNull final Player player,
			@NonNull final ItemStack item,
			final double price
	) {
		this(listingType, UUID.randomUUID(), player.getUniqueId(), player.getName(), item, "Vault/Vault", CompMaterial.AIR.parseItem(), price, player.getWorld().getName(), player.getServer().getName(), System.currentTimeMillis(), System.currentTimeMillis() + 1000 * 60 * 60);
	}

	public BinListing(
			@NonNull final Player player,
			@NonNull final ItemStack item,
			final double price
	) {
		this(ListingType.BIN, UUID.randomUUID(), player.getUniqueId(), player.getName(), item, "Vault/Vault", CompMaterial.AIR.parseItem(), price, player.getWorld().getName(), player.getServer().getName(), System.currentTimeMillis(), System.currentTimeMillis() + 1000 * 60 * 60);
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
	public String getCurrency() {
		return this.currency;
	}

	@Override
	public ItemStack getCurrencyItem() {
		return this.currencyItem;
	}

	@Override
	public double getBinPrice() {
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
	public void setCurrency(String currency) {
		this.currency = currency;
	}

	@Override
	public void setBinPrice(double binPrice) {
		this.binPrice = binPrice;
	}

	@Override
	public boolean isBeingBought() {
		return this.isBeingBought;
	}

	@Override
	public void setIsBeingBought(boolean isBeingBought) {
		this.isBeingBought = isBeingBought;
	}

	@Override
	public boolean isInfinite() {
		return this.isInfinite;
	}

	@Override
	public void setIsInfinite(boolean infinite) {
		this.isInfinite = infinite;
	}

	@Override
	public boolean isPriorityListing() {
		return this.isPriorityListing;
	}

	@Override
	public void setPriorityListing(boolean isPriority) {
		this.isPriorityListing = isPriority;
	}

	@Override
	public long getPriorityExpiration() {
		return this.priorityExpiration;
	}

	@Override
	public void setPriorityExpiration(long expiresAt) {
		this.priorityExpiration = expiresAt;
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
	public boolean isArchived() {
		return this.isArchived;
	}

	@Override
	public void setArchived(boolean archived) {
		this.isArchived = archived;
	}

	@Override
	public List<String> getDisplayLore(@NonNull ListingDisplayMode displayMode) {
		final List<String> displayLore = new ArrayList<>(ItemUtil.getItemLore(this.item));

		// header
		displayLore.add("&7&m-------------------------");
		displayLore.add("#FE8295Seller&f: &e%listing_seller%");
		displayLore.add("#FE8295Price&f: &a$%listing_price%");
		displayLore.add(" ");

		// remaining time
		displayLore.add("#FE8295Time&F: &f%days_left%&bd &f%hours_left%&bh &f%minutes_left%&bm &f%seconds_left%&bs");


		// footer
		displayLore.add("&7&m-------------------------");
		displayLore.add("&e&lLeft Click &8» &7To Purchase");
		displayLore.add("&7&m-------------------------");
		displayLore.add("&c&lPress 1 &cTo Moderate Item");
		displayLore.add("&7&m-------------------------");
		return displayLore;
	}
}