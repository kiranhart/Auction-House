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

package ca.tweetzy.auctionhouse.settings;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.core.configuration.Config;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * The current file has been created by Kiran Hart
 * Date Created: June 16 2021
 * Time Created: 2:45 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class LocaleSettings {

	static final HashMap<String, String> languageNodes = new HashMap<>();

	static {
		languageNodes.put("general.prefix", "&8[&eAuctionHouse&8]");
		languageNodes.put("general.notanumber", "&cThe entry &4%value% &cis not a valid number!");
		languageNodes.put("general.locked", "&cThe Auction House is currently locked!");
		languageNodes.put("general.playernotfound", "&cCould not find the player &4%player%");
		languageNodes.put("general.notenoughmoney", "&cYou do not have enough money!");
		languageNodes.put("general.cantbidonown", "&cYou cannot bid on your own item!");
		languageNodes.put("general.cantbuyown", "&cYou cannot buy your own item!");
		languageNodes.put("general.blockeditem", "&cYou are not allowed to auction that item. (%item%)");
		languageNodes.put("general.blockedname", "&cThat item contains a blocked name phrase");
		languageNodes.put("general.blockedlore", "&cThat item contains a blocked lore phrase");
		languageNodes.put("general.air", "&cSorry, but you cannot sell air o.O");
		languageNodes.put("general.filter air", "&cSorry, but you cannot add air to a filter whitelist");
		languageNodes.put("general.min item price air", "&cSorry, but you cannot add a price to air");
		languageNodes.put("general.blocked", "&cSorry, you are not allowed to sell &e%item%");
		languageNodes.put("general.sellinglimit", "&cYou cannot sell more items, please remove/sell current active items");
		languageNodes.put("general.noroom", "&cPlease clear room in your inventory to purchase that item.");
		languageNodes.put("general.noroomclaim", "&cYou do not have enough space in your inventory");
		languageNodes.put("general.buynowdisabledonitem", "&cBuy now is disabled on this item.");
		languageNodes.put("general.endedallauctions", "&cYou force ended all active auctions");
		languageNodes.put("general.relisteditems", "&aYou relisted all expired items!");
		languageNodes.put("general.cannotsellbundleditem", "&cYou cannot sell a bundled item as a bundle.");
		languageNodes.put("general.entersearchphrase", "&aEnter a search phrase into chat");
		languageNodes.put("general.filteritemaddedalready", "&cThat item already exists within that category's filter whitelist.");
		languageNodes.put("general.addeditemtofilterwhitelist", "&aAdded &2%item_name%&a to the &2%filter_category%&a's whitelist");
		languageNodes.put("general.finishenteringprice", "&cPlease finish entering the new price first.");
		languageNodes.put("general.invalidtimestring", "&cInvalid time, use the following format, ex: 1d (valid suffixes: s,m,h,d,y)");
		languageNodes.put("general.something_went_wrong_while_listing", "&cSomething went wrong while listing item.");
		languageNodes.put("general.toggled listing.on", "&aYou turned on listing messages");
		languageNodes.put("general.toggled listing.off", "&cYou turned off listing messages");
		languageNodes.put("general.bought_item", "&aYou bought &fx%amount% %item%&a for &a$%price%");
		languageNodes.put("general.wait_to_list", "&cPlease wait &4%time%&cs before listing another item");
		languageNodes.put("general.please_enter_at_least_one_number", "&cPlease enter at least 1 valid number!");
		languageNodes.put("general.mcmmo_ability_active", "&cCannot list item when mcMMO ability is active!");
		languageNodes.put("general.cannot list damaged item", "&cCannot list a damaged item!");
		languageNodes.put("general.cannot list repaired item", "&cCannot list a repaired item!");
		languageNodes.put("general.marked chest", "&aYou marked that chest as an Auction chest");
		languageNodes.put("general.unmarked chest", "&cYou unmarked that chest as an Auction chest");
		languageNodes.put("general.visit auction chest", "&cVisit an Auction chest to use Auction House.");
		languageNodes.put("general.disabled in world", "&cAuction House is disabled in this world.");
		languageNodes.put("general.requires creative", "&cThat action requires you to be in creative mode");
		languageNodes.put("general.deleted transactions", "&cDeleted a total of &4%deleted_transactions% &ctransactions");
		languageNodes.put("general.transaction delete begin", "&cBeginning transaction deletion, this may take some time.");
		languageNodes.put("general.min price already added", "&cThere is already a minimum price set, please delete the existing one first.");
		languageNodes.put("general.added min price", "&aSuccessfully set minimum price for %item% &ato &2$%price%");
		languageNodes.put("general.qtybuydisabled", "&4%item_owner%&c is only accepting purchases of the entire stack.");


		languageNodes.put("pricing.minbaseprice", "&cThe minimum base price must be &a$%price%");
		languageNodes.put("pricing.minstartingprice", "&cThe minimum starting bid price must be &a$%price%");
		languageNodes.put("pricing.minbidincrementprice", "&cThe minimum bid increment must be &a$%price%");
		languageNodes.put("pricing.maxbaseprice", "&cThe maximum base price is &a$%price%");
		languageNodes.put("pricing.maxstartingprice", "&cThe maximum starting bid price is &a$%price%");
		languageNodes.put("pricing.maxbidincrementprice", "&cThe maximum bid increment is &a$%price%");
		languageNodes.put("pricing.basepricetoolow", "&cThe buy now price must be higher than the starting bid.");
		languageNodes.put("pricing.moneyremove", "&c&l- $%price% &7(%player_balance%)");
		languageNodes.put("pricing.moneyadd", "&a&l+ $%price% &7(%player_balance%)");
		languageNodes.put("pricing.bidmusthigherthanprevious", "&cYour bid must be higher than &4%current_bid%");
		languageNodes.put("pricing.minitemprice", "&cThe minimum price for this item must be &a$%price%");


		languageNodes.put("titles.buy now price.title", "&eBuy Out Price");
		languageNodes.put("titles.buy now price.subtitle", "&fEnter new buyout price in chat");

		languageNodes.put("titles.starting bid price.title", "&eStarting Bid Price");
		languageNodes.put("titles.starting bid price.subtitle", "&fEnter new starting bid in chat");

		languageNodes.put("titles.bid increment price.title", "&eBid Increment Price");
		languageNodes.put("titles.bid increment price.subtitle", "&fEnter new bid increment in chat");

		languageNodes.put("titles.listing time.title", "&eListing Time");
		languageNodes.put("titles.listing time.subtitle", "&fEnter new listing time in chat");

		languageNodes.put("prompts.enter new buy now price", "&aPlease enter the new buy now price in chat:");
		languageNodes.put("prompts.enter new starting bid", "&aPlease enter the new starting bid in chat:");
		languageNodes.put("prompts.enter new bid increment", "&aPlease enter the new bid increment in chat:");

		languageNodes.put("prompts.enter bid amount", "&7Current Bid &e%current_bid% &7Type '&ccancel&7' to cancel placing bid");
		languageNodes.put("prompts.enter valid bid amount", "&cBid either too low or too high");

		languageNodes.put("prompts.enter listing time", "&aPlease enter listing time (ex. 1 day):");
		languageNodes.put("prompts.enter valid listing time", "&cPlease enter a valid listing time.");

		languageNodes.put("prompts.enter deletion range", "&aPlease enter deletion range (ex. 1 day):");
		languageNodes.put("prompts.enter valid deletion range", "&cPlease enter a valid deletion range");

		languageNodes.put("transaction.sale_type.bid_won", "Won Auction");
		languageNodes.put("transaction.sale_type.immediate_buy", "Bought Immediately");

		languageNodes.put("discord.player_lost", "Player Lost o.O");
		languageNodes.put("discord.no_buyer", "No Buyer");
		languageNodes.put("discord.not_sold", "Not Sold");
		languageNodes.put("discord.not_bought", "Was not bought");
		languageNodes.put("discord.sale_bid_win", "Won Bid");
		languageNodes.put("discord.sale_immediate_buy", "Bought Immediately");
		languageNodes.put("discord.is_bid_true", "true");
		languageNodes.put("discord.is_bid_false", "false");

		languageNodes.put("bans.nobanreason", "&cPlease enter a ban reason");
		languageNodes.put("bans.bannedplayer", "&aBanned &2%player% &afrom the auction house for &2%ban_amount%");
		languageNodes.put("bans.playeralreadybanned", "&4%player% &cis already banned from the auction house");
		languageNodes.put("bans.playernotbanned", "&4%player% &cis not banned from the auction house");
		languageNodes.put("bans.playerunbanned", "&cUnbanned &4%player% &cfrom the auction house");
		languageNodes.put("bans.remainingtime", "&cYou are banned from the auction house for &4%ban_amount%");
		languageNodes.put("bans.unbanned", "&aYou are now unbanned from the auction house");
		languageNodes.put("bans.ban expired", "&aBan Expired");

		languageNodes.put("auction_filter.sale_types.biddable", "Biddable");
		languageNodes.put("auction_filter.sale_types.non_biddable", "Not Biddable");
		languageNodes.put("auction_filter.sale_types.both", "All");
		languageNodes.put("auction_filter.categories.all", "All");
		languageNodes.put("auction_filter.categories.food", "Food");
		languageNodes.put("auction_filter.categories.armor", "Armor");
		languageNodes.put("auction_filter.categories.blocks", "Blocks");
		languageNodes.put("auction_filter.categories.tools", "Tools");
		languageNodes.put("auction_filter.categories.misc", "Misc");
		languageNodes.put("auction_filter.categories.spawners", "Spawners");
		languageNodes.put("auction_filter.categories.enchants", "Enchants");
		languageNodes.put("auction_filter.categories.weapons", "Weapons");
		languageNodes.put("auction_filter.categories.self", "Self");
		languageNodes.put("auction_filter.categories.search", "Search");
		languageNodes.put("auction_filter.sort_order.recent", "Recent");
		languageNodes.put("auction_filter.sort_order.price", "Price");

		languageNodes.put("auction_statistic.created_auction", "Created Auction");
		languageNodes.put("auction_statistic.created_bin", "Created Bin");
		languageNodes.put("auction_statistic.sold_auctions", "Sold Auctions");
		languageNodes.put("auction_statistic.sold_bins", "Sold Bins");
		languageNodes.put("auction_statistic.money_spent", "Money Spent");
		languageNodes.put("auction_statistic.money_earned", "Money Earned");

		languageNodes.put("auction.listed.withbid", "&eListed &fx%amount% &6%item% &e&lBuy Now&f: &a%base_price% &e&lStarting&f: &a%start_price% &e&lIncrement&f: &a%increment_price%");
		languageNodes.put("auction.listed.nobid", "&eListed &fx%amount% &6%item% &efor &a%base_price%");
		languageNodes.put("auction.broadcast.withbid", "&e%player% listed &fx%amount% &6%item% &e&lBuy Now&f: &a%base_price% &e&lStarting&f: &a%start_price% &e&lIncrement&f: &a%increment_price%");
		languageNodes.put("auction.broadcast.nobid", "&e%player% listed &fx%amount% &6%item% &efor &a%base_price%");
		languageNodes.put("auction.broadcast.sold", "&e&fx%amount% &6%item% &esold to %player% for &a%price%");

		languageNodes.put("auction.broadcast.bid", "&e%player% increased the bid to &a$%amount% &eon &6%item%");
		languageNodes.put("auction.broadcast.ending", "&eAuction for &6%item% &eis ending in &6%seconds%&es");

		languageNodes.put("auction.bidwon", "&eYou won the bid for&fx%amount% &6%item% &efor &a%price%");
		languageNodes.put("auction.itemsold", "&eYou sold &6%item% &eto &6%buyer_name% &efor &a%price%");
		languageNodes.put("auction.itemnotavailable", "&cThat item is no longer available :(");
		languageNodes.put("auction.biditemwithdisabledbuynow", "&CN/A");
		languageNodes.put("auction.outbid", "&6%player% &ehas out bid you for &6%item%");
		languageNodes.put("auction.placedbid", "&6%player% &eincreased the bid to &a$%amount% &eon &6%item%");
		languageNodes.put("auction.nobids", "&cNo Bids");

		languageNodes.put("auction.tax.cannotpaylistingfee", "&cYou do not have enough money to pay the listing fee &f(&4$%price%&f)");
		languageNodes.put("auction.tax.paidlistingfee", "&aPaid &2$%price%&a listing fee");

		languageNodes.put("admin action.return", "Returned");
		languageNodes.put("admin action.claim", "Claimed");
		languageNodes.put("admin action.delete", "Deleted");
		languageNodes.put("admin action.copy", "Copied");

		languageNodes.put("commands.invalid_syntax", "&7The valid syntax is: &6%syntax%&7.");
		languageNodes.put("commands.no_permission", "&dYou do not have permission to do that.");
		languageNodes.put("commands.sell.args.suggestion one", "100");
		languageNodes.put("commands.sell.args.suggestion two", "50 -b");
		languageNodes.put("commands.sell.args.suggestion three", "10 -b");

		languageNodes.put("commands.syntax.active", "active");
		languageNodes.put("commands.syntax.auctionhouse", "/ah");
		languageNodes.put("commands.syntax.convert", "convert");
		languageNodes.put("commands.syntax.expired", "expired");
		languageNodes.put("commands.syntax.reload", "reload");
		languageNodes.put("commands.syntax.search", "search <keywords>");
		languageNodes.put("commands.syntax.sell", "sell <basePrice> [bidStart] [bidIncr]");
		languageNodes.put("commands.syntax.settings", "settings");
		languageNodes.put("commands.syntax.transactions", "transactions");
		languageNodes.put("commands.syntax.upload", "upload");
		languageNodes.put("commands.syntax.filter", "filter [additem] [category]");
		languageNodes.put("commands.syntax.ban", "ban [player] [time] [reason]");
		languageNodes.put("commands.syntax.unban", "unban <player>");
		languageNodes.put("commands.syntax.togglelistinfo", "togglelistinfo");
		languageNodes.put("commands.syntax.markchest", "markchest");
		languageNodes.put("commands.syntax.min price", "minprices [add] [price]");
		languageNodes.put("commands.syntax.stats", "stats [player]");

		languageNodes.put("commands.description.active", "View all your auction listings");
		languageNodes.put("commands.description.auctionhouse", "Main command for the plugin, it opens the auction window.");
		languageNodes.put("commands.description.convert", "Used to make an attempted conversion from < 2.0.0+");
		languageNodes.put("commands.description.expired", "View all your expired/cancelled listings");
		languageNodes.put("commands.description.reload", "Reload plugin files");
		languageNodes.put("commands.description.search", "Search for specific item(s) in the auction house");
		languageNodes.put("commands.description.sell", "Used to put an item up for auction");
		languageNodes.put("commands.description.settings", "Open the in-game config editor");
		languageNodes.put("commands.description.transactions", "Used to open the transactions menu");
		languageNodes.put("commands.description.upload", "Used to upload flat file data to the database");
		languageNodes.put("commands.description.filter", "Edit the filter whitelist items");
		languageNodes.put("commands.description.ban", "Ban a player from the auction house for a set amount of time.");
		languageNodes.put("commands.description.unban", "Unban a player from the auction house");
		languageNodes.put("commands.description.togglelistinfo", "Toggle whether auction house should message you when you list an item");
		languageNodes.put("commands.description.markchest", "Toggles whether a chest is an auction chest");
		languageNodes.put("commands.description.min price", "Adds a minimum sell price to an item");
		languageNodes.put("commands.description.stats", "View yours or another players stats");
	}

	public static void send(CommandSender sender, String msg) {
		if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null && sender instanceof Player) {
			msg = PlaceholderAPI.setPlaceholders((Player) sender, msg);
		}

		AuctionHouse.getInstance().getLocale().getMessage(msg).sendPrefixedMessage(sender);
	}

	public static void setup() {
		Config config = AuctionHouse.getInstance().getLocale().getConfig();

		languageNodes.keySet().forEach(key -> {
			config.setDefault(key, languageNodes.get(key));
		});

		config.setAutoremove(false).setAutosave(true);
		config.saveChanges();
	}
}
