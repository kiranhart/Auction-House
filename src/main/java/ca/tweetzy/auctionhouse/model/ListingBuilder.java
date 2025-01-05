package ca.tweetzy.auctionhouse.model;

import ca.tweetzy.auctionhouse.api.auction.Bid;
import ca.tweetzy.auctionhouse.api.auction.ListingType;
import ca.tweetzy.auctionhouse.impl.listing.AuctionListing;
import ca.tweetzy.auctionhouse.impl.listing.BinListing;
import ca.tweetzy.flight.comp.enums.CompMaterial;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ListingBuilder {

	// Common fields
	private UUID uuid;
	private UUID ownerUUID;
	private String ownerName;
	private ItemStack item;
	private String currency;
	private ItemStack currencyItem;
	private double binPrice;
	private String listedWorld;
	private String listedServer;
	private long listedAt;
	private long expiresAt;

	// Auction-specific fields
	private double startingBid;
	private double bidIncrement;
	private UUID highestBidderUUID;
	private String highestBidderName;
	private List<Bid> bids;
	private boolean isAuction;

	public static ListingBuilder auction(@NonNull final Player player, @NonNull final ItemStack item) {
		return of(player, item, ListingType.AUCTION);
	}

	public static ListingBuilder bin(@NonNull final Player player, @NonNull final ItemStack item) {
		return of(player, item, ListingType.BIN);
	}

	public static ListingBuilder of(Player player, ItemStack item, ListingType listingType) {
		ListingBuilder builder = new ListingBuilder();
		builder.setOwner(player);
		builder.setItem(item);
		builder.setListedWorld(player.getWorld().getName());
		builder.setListedServer(player.getServer().getName());

		if (listingType == ListingType.AUCTION) {
			builder.isAuction = true;
		}
		return builder;
	}

	public ListingBuilder() {
		// Initialize default values
		this.uuid = UUID.randomUUID();
		this.currency = "Vault/Vault";
		this.currencyItem = CompMaterial.AIR.parseItem();
		this.listedAt = System.currentTimeMillis();
		this.expiresAt = this.listedAt + 1000 * 60 * 60; // Default to 1 hour
		this.bids = new ArrayList<>();
	}

	public ListingBuilder setUuid(UUID uuid) {
		this.uuid = uuid;
		return this;
	}

	public ListingBuilder setOwner(UUID ownerUUID, String ownerName) {
		this.ownerUUID = ownerUUID;
		this.ownerName = ownerName;
		return this;
	}

	public ListingBuilder setOwner(Player player) {
		this.ownerUUID = player.getUniqueId();
		this.ownerName = player.getName();
		this.listedWorld = player.getWorld().getName();
		this.listedServer = player.getServer().getName();
		return this;
	}

	public ListingBuilder setItem(ItemStack item) {
		this.item = item;
		return this;
	}

	public ListingBuilder setCurrency(String currency) {
		this.currency = currency;
		return this;
	}

	public ListingBuilder setCurrencyItem(ItemStack currencyItem) {
		this.currencyItem = currencyItem;
		return this;
	}

	public ListingBuilder setBinPrice(double binPrice) {
		this.binPrice = binPrice;
		return this;
	}

	public ListingBuilder setListedWorld(String listedWorld) {
		this.listedWorld = listedWorld;
		return this;
	}

	public ListingBuilder setListedServer(String listedServer) {
		this.listedServer = listedServer;
		return this;
	}

	public ListingBuilder setListedAt(long listedAt) {
		this.listedAt = listedAt;
		return this;
	}

	public ListingBuilder setExpiresAt(long expiresAt) {
		this.expiresAt = expiresAt;
		return this;
	}

	public ListingBuilder setAuction() {
		this.isAuction = true;
		return this;
	}

	public ListingBuilder setStartingBid(double startingBid) {
		this.startingBid = startingBid;
		this.isAuction = true;
		return this;
	}

	public ListingBuilder setBiddingIncrement(double bidIncrement) {
		this.bidIncrement = bidIncrement;
		this.isAuction = true;
		return this;
	}

	public ListingBuilder setHighestBidder(UUID highestBidderUUID, String highestBidderName) {
		this.highestBidderUUID = highestBidderUUID;
		this.highestBidderName = highestBidderName;
		return this;
	}

	public ListingBuilder setBids(List<Bid> bids) {
		this.bids = bids;
		return this;
	}

	public AuctionListing buildAuctionListing() {
		if (!isAuction) {
			throw new IllegalStateException("This builder is not configured for an auction listing.");
		}
		return new AuctionListing(uuid, ownerUUID, ownerName, item, currency, currencyItem, startingBid, bidIncrement, binPrice, listedWorld, listedServer, highestBidderUUID, highestBidderName, bids, listedAt, expiresAt);
	}

	public BinListing buildBinListing() {
		if (isAuction) {
			throw new IllegalStateException("This builder is configured for an auction listing.");
		}
		return new BinListing(ListingType.BIN, uuid, ownerUUID, ownerName, item, currency, currencyItem, binPrice, listedWorld, listedServer, listedAt, expiresAt);
	}
}