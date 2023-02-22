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

package ca.tweetzy.auctionhouse.helpers;

import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.api.DiscordWebhook;
import ca.tweetzy.auctionhouse.auction.AuctionedItem;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.flight.utils.ItemUtil;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

import java.awt.*;

public final class DiscordMessageCreator {

	public static enum MessageType {
		NEW_AUCTION_LISTING,
		NEW_BIN_LISTING,

		AUCTION_LISTING_WON,
		BIN_LISTING_BOUGHT,

		BID_PLACED,
	}

	private final String webhook;
	private final MessageType messageType;
	private OfflinePlayer seller;
	private OfflinePlayer buyer;
	private OfflinePlayer bidder;
	private AuctionedItem listing;
	private double bidAmount;

	private DiscordMessageCreator(@NonNull final String webhook, @NonNull final MessageType messageType) {
		this.webhook = webhook;
		this.messageType = messageType;
	}

	public static DiscordMessageCreator of(@NonNull final String webhook, @NonNull final MessageType messageType) {
		return new DiscordMessageCreator(webhook, messageType);
	}

	public DiscordMessageCreator seller(@NonNull final OfflinePlayer seller) {
		this.seller = seller;
		return this;
	}

	public DiscordMessageCreator buyer(@NonNull final OfflinePlayer buyer) {
		this.buyer = buyer;
		return this;
	}

	public DiscordMessageCreator bidder(@NonNull final OfflinePlayer bidder) {
		this.bidder = bidder;
		return this;
	}

	public DiscordMessageCreator bidAmount(final double bidAmount) {
		this.bidAmount = bidAmount;
		return this;
	}

	public DiscordMessageCreator listing(@NonNull final AuctionedItem listing) {
		this.listing = listing;
		return this;
	}

	public DiscordWebhook generate() {
		final DiscordWebhook hook = generateBaseHook();
		DiscordWebhook.EmbedObject embed = generateBaseEmbed();

		embed.addField(Settings.DISCORD_MSG_FIELD_SELLER_NAME.getString(), Settings.DISCORD_MSG_FIELD_SELLER_VALUE.getString().replace("%seller%", this.seller.getName()), Settings.DISCORD_MSG_FIELD_SELLER_INLINE.getBoolean());
		embed.addField(Settings.DISCORD_MSG_FIELD_ITEM_NAME.getString(), Settings.DISCORD_MSG_FIELD_ITEM_VALUE.getString().replace("%item_name%", "x" + this.listing.getItem().getAmount() + " " + ChatColor.stripColor(ItemUtil.getStackName(this.listing.getItem()))), Settings.DISCORD_MSG_FIELD_SELLER_INLINE.getBoolean());

		switch (this.messageType) {
			case NEW_AUCTION_LISTING:
				embed = applyAuctionInfo(embed);
				break;
			case NEW_BIN_LISTING:
				embed = applyBinInfo(embed);
				break;
			case AUCTION_LISTING_WON:
				embed = applyAuctionWonInfo(embed);
				break;
			case BIN_LISTING_BOUGHT:
				embed = applyBinPurchaseInfo(embed);
				break;
			case BID_PLACED:
				embed = applyBidInfo(embed);
				break;
		}

		hook.addEmbed(embed);
		return hook;
	}

	public DiscordWebhook.EmbedObject applyBinPurchaseInfo(DiscordWebhook.EmbedObject embed) {
		embed = applyBinInfo(embed);
		embed.addField(Settings.DISCORD_MSG_FIELD_BIN_BOUGHT_NAME.getString(), Settings.DISCORD_MSG_FIELD_BIN_BOUGHT_VALUE.getString().replace("%buyer%", this.buyer.getName()), Settings.DISCORD_MSG_FIELD_BIN_BOUGHT_INLINE.getBoolean());
		return embed;
	}

	public DiscordWebhook.EmbedObject applyBidInfo(DiscordWebhook.EmbedObject embed) {
		embed.addField(Settings.DISCORD_MSG_FIELD_AUCTION_START_PRICE_NAME.getString(), Settings.DISCORD_MSG_FIELD_AUCTION_START_PRICE_VALUE.getString().replace("%starting_price%", AuctionAPI.getInstance().formatNumber(this.listing.getBidStartingPrice())), Settings.DISCORD_MSG_FIELD_AUCTION_START_PRICE_INLINE.getBoolean());
		embed.addField(Settings.DISCORD_MSG_FIELD_AUCTION_BIDDER_NAME.getString(), Settings.DISCORD_MSG_FIELD_AUCTION_BIDDER_VALUE.getString().replace("%bidder%", this.bidder.getName()), Settings.DISCORD_MSG_FIELD_AUCTION_BIDDER_INLINE.getBoolean());

		embed.addField(Settings.DISCORD_MSG_FIELD_BID_AMT_NAME.getString(), Settings.DISCORD_MSG_FIELD_BID_AMT_VALUE.getString().replace("%bid_amount%", AuctionAPI.getInstance().formatNumber(this.bidAmount)), Settings.DISCORD_MSG_FIELD_BID_AMT_INLINE.getBoolean());
		embed.addField(Settings.DISCORD_MSG_FIELD_AUCTION_CURRENT_PRICE_NAME.getString(), Settings.DISCORD_MSG_FIELD_AUCTION_CURRENT_PRICE_VALUE.getString().replace("%current_price%", AuctionAPI.getInstance().formatNumber(this.listing.getCurrentPrice())), Settings.DISCORD_MSG_FIELD_AUCTION_CURRENT_PRICE_INLINE.getBoolean());

		return embed;
	}


	public DiscordWebhook.EmbedObject applyAuctionWonInfo(DiscordWebhook.EmbedObject embed) {
		embed.addField(Settings.DISCORD_MSG_FIELD_AUCTION_START_PRICE_NAME.getString(), Settings.DISCORD_MSG_FIELD_AUCTION_START_PRICE_VALUE.getString().replace("%starting_price%", AuctionAPI.getInstance().formatNumber(this.listing.getBidStartingPrice())), Settings.DISCORD_MSG_FIELD_AUCTION_START_PRICE_INLINE.getBoolean());
		embed.addField(Settings.DISCORD_MSG_FIELD_AUCTION_WON_NAME.getString(), Settings.DISCORD_MSG_FIELD_AUCTION_WON_VALUE.getString().replace("%final_price%", AuctionAPI.getInstance().formatNumber(this.listing.getCurrentPrice())), Settings.DISCORD_MSG_FIELD_AUCTION_WON_INLINE.getBoolean());
		embed.addField(Settings.DISCORD_MSG_FIELD_AUCTION_WINNER_NAME.getString(), Settings.DISCORD_MSG_FIELD_AUCTION_WINNER_VALUE.getString().replace("%winner%", this.buyer.getName()), Settings.DISCORD_MSG_FIELD_AUCTION_WINNER_INLINE.getBoolean());
		return embed;
	}

	public DiscordWebhook.EmbedObject applyBinInfo(DiscordWebhook.EmbedObject embed) {
		embed.addField(Settings.DISCORD_MSG_FIELD_BIN_LISTING_PRICE_NAME.getString(), Settings.DISCORD_MSG_FIELD_BIN_LISTING_PRICE_VALUE.getString().replace("%item_price%", AuctionAPI.getInstance().formatNumber(this.listing.getBasePrice())), Settings.DISCORD_MSG_FIELD_BIN_LISTING_PRICE_INLINE.getBoolean());
		return embed;
	}

	public DiscordWebhook.EmbedObject applyAuctionInfo(DiscordWebhook.EmbedObject embed) {
		embed.addField(Settings.DISCORD_MSG_FIELD_AUCTION_START_PRICE_NAME.getString(), Settings.DISCORD_MSG_FIELD_AUCTION_START_PRICE_VALUE.getString().replace("%starting_price%", AuctionAPI.getInstance().formatNumber(this.listing.getBidStartingPrice())), Settings.DISCORD_MSG_FIELD_AUCTION_START_PRICE_INLINE.getBoolean());
		if (this.listing.getBasePrice() != -1)
			embed.addField(Settings.DISCORD_MSG_FIELD_AUCTION_BUYOUT_PRICE_NAME.getString(), Settings.DISCORD_MSG_FIELD_AUCTION_BUYOUT_PRICE_VALUE.getString().replace("%buy_now_price%", AuctionAPI.getInstance().formatNumber(this.listing.getBasePrice())), Settings.DISCORD_MSG_FIELD_AUCTION_BUYOUT_PRICE_INLINE.getBoolean());
		return embed;
	}

	private DiscordWebhook generateBaseHook() {
		final DiscordWebhook hook = new DiscordWebhook(this.webhook);

		// basic settings
		hook.setUsername(Settings.DISCORD_MSG_USERNAME.getString());
		hook.setAvatarUrl(Settings.DISCORD_MSG_PFP.getString());

		return hook;
	}

	private DiscordWebhook.EmbedObject generateBaseEmbed() {
		final DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject();

		// assign title
		switch (this.messageType) {
			case NEW_AUCTION_LISTING:
				embed.setTitle(Settings.DISCORD_TITLE_NEW_AUCTION_LISTING.getString());
				embed.setColor(extractColor(Settings.DISCORD_COLOR_NEW_AUCTION_LISTING.getString()));
				break;
			case NEW_BIN_LISTING:
				embed.setTitle(Settings.DISCORD_TITLE_NEW_BIN_LISTING.getString());
				embed.setColor(extractColor(Settings.DISCORD_COLOR_NEW_BIN_LISTING.getString()));
				break;
			case AUCTION_LISTING_WON:
				embed.setTitle(Settings.DISCORD_TITLE_AUCTION_LISTING_WON.getString());
				embed.setColor(extractColor(Settings.DISCORD_COLOR_AUCTION_LISTING_WON.getString()));
				break;
			case BIN_LISTING_BOUGHT:
				embed.setTitle(Settings.DISCORD_TITLE_BIN_LISTING_BOUGHT.getString());
				embed.setColor(extractColor(Settings.DISCORD_COLOR_BIN_LISTING_BOUGHT.getString()));
				break;
			case BID_PLACED:
				embed.setTitle(Settings.DISCORD_TITLE_NEW_BID.getString());
				embed.setColor(extractColor(Settings.DISCORD_COLOR_NEW_BID.getString()));
				break;
		}

		return embed;
	}

	private Color extractColor(@NonNull final String hsbString) {
		final String[] possibleColours = hsbString.split("-");
		return Color.getHSBColor(Float.parseFloat(possibleColours[0]) / 360, Float.parseFloat(possibleColours[1]) / 100, Float.parseFloat(possibleColours[2]) / 100);
	}

	@SneakyThrows
	public void send() {
		generate().execute();
	}
}
