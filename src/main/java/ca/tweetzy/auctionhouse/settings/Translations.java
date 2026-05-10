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

package ca.tweetzy.auctionhouse.settings;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.flight.settings.TranslationEntry;
import ca.tweetzy.flight.settings.TranslationManager;
import lombok.NonNull;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Locale;

public final class Translations extends TranslationManager {

	public Translations(@NonNull JavaPlugin plugin) {
		super(plugin);
		this.mainLanguage = Settings.LANG.getString().toLowerCase(Locale.ROOT).replace('-', '_');
	}

	/*
	==============================================================
					General
	==============================================================
	 */
	public static TranslationEntry GENERAL_PREFIX = create("general.prefix", "&8[&eAuctionHouse&8]");
	public static TranslationEntry GENERAL_PLUGIN_NAME = create("general.plugin name", "&eAuctionHouse");
	public static TranslationEntry GENERAL_SERVER_LISTING = create("general.server listing", "&eAuctionHouse");
	public static TranslationEntry GENERAL_NOT_A_NUMBER = create("general.notanumber", "&cThe entry &4%value% &cis not a valid number!");
	public static TranslationEntry GENERAL_LOCKED = create("general.locked", "&cThe Auction House is currently locked!");
	public static TranslationEntry GENERAL_PLAYER_NOT_FOUND = create("general.playernotfound", "&cCould not find the player &4%player%");
	public static TranslationEntry GENERAL_NOT_ENOUGH_MONEY = create("general.notenoughmoney", "&cYou do not have enough money!");
	public static TranslationEntry GENERAL_REQUESTER_HAS_NO_MONEY = create("general.requesterhasnomoney", "&cRequester does not have enough money!");
	public static TranslationEntry GENERAL_NOT_ENOUGH_ITEMS = create("general.notenoughitems", "&cYou do not have enough of that item!");
	public static TranslationEntry GENERAL_CANNOT_BE_ZERO = create("general.cannotbezero", "&cPlease provide a number greater than zero");
	public static TranslationEntry GENERAL_HIGH_REQUEST_COUNT = create("general.highrequestcount", "&cYou cannot request that many items!");
	public static TranslationEntry GENERAL_REQUEST_SHULKER_CONTAINS_ITEMS = create("general.request shulker contains items", "&cPlease make the request using an empty shulker");
	public static TranslationEntry GENERAL_AUCTION_HOUSE_CLOSED = create("general.auction house closed", "&cThe Auction House is currently closed.");
	public static TranslationEntry GENERAL_COOLDOWN_COMMAND = create("general.cooldown.command", "&cPlease wait &4%time% &cto do that again.");
	public static TranslationEntry GENERAL_COOLDOWN_FILTER = create("general.cooldown.filter", "&cPlease wait &4%time% &cto change filter");
	public static TranslationEntry GENERAL_COOLDOWN_NAVIGATE_PAGE = create("general.cooldown.navigate page", "&cPlease wait &4%time% &cto change pages");
	public static TranslationEntry GENERAL_COOLDOWN_ITEM_CLICK = create("general.cooldown.item click", "&cPlease wait &4%time% &cto click another item");
	public static TranslationEntry GENERAL_COOLDOWN_REFRESH = create("general.cooldown.refresh", "&cPlease wait &4%time% &cto refresh again");
	public static TranslationEntry GENERAL_CANT_BID_ON_OWN = create("general.cantbidonown", "&cYou cannot bid on your own item!");
	public static TranslationEntry GENERAL_ALREADY_HIGHEST_BIDDER = create("general.alreadyhighestbidder", "&cYou are already the highest bidder!");
	public static TranslationEntry GENERAL_CANT_BUY_OWN = create("general.cantbuyown", "&cYou cannot buy your own item!");
	public static TranslationEntry GENERAL_INVALID_RANGE = create("general.invalidrange", "&cInvalid range format. Example&F: &41 day");
	public static TranslationEntry GENERAL_BLOCKED_ITEM = create("general.blockeditem", "&cYou are not allowed to auction that item. (%item%)");
	public static TranslationEntry GENERAL_BLOCKED_NBT_TAG = create("general.blockednbttag", "&cThat item contains a blocked tag.");
	public static TranslationEntry GENERAL_BLOCKED_NAME = create("general.blockedname", "&cThat item contains a blocked name phrase");
	public static TranslationEntry GENERAL_BLOCKED_LORE = create("general.blockedlore", "&cThat item contains a blocked lore phrase");
	public static TranslationEntry GENERAL_AIR = create("general.air", "&cSorry, but you cannot sell air o.O");
	public static TranslationEntry GENERAL_FILTER_AIR = create("general.filter air", "&cSorry, but you cannot add air to a filter whitelist");
	public static TranslationEntry GENERAL_MIN_ITEM_PRICE_AIR = create("general.min item price air", "&cSorry, but you cannot add a price to air");
	public static TranslationEntry GENERAL_BLOCKED = create("general.blocked", "&cSorry, you are not allowed to sell &e%item%");
	public static TranslationEntry GENERAL_SELLING_LIMIT = create("general.sellinglimit", "&cYou cannot sell more items, please remove/sell current active items");
	public static TranslationEntry GENERAL_REQUEST_LIMIT = create("general.requestlimit", "&cYou cannot request more items, please remove/sell current active items");
	public static TranslationEntry GENERAL_COLLECTION_BIN_LIMIT = create("general.collectionbinlimit", "&cCollection is full, please claim your items first.");
	public static TranslationEntry GENERAL_BUNDLE_LIST_LIMIT = create("general.bundlelistlimit", "&cYou cannot list anymore bundled items!");
	public static TranslationEntry GENERAL_NO_ROOM = create("general.noroom", "&cPlease clear room in your inventory to purchase that item.");
	public static TranslationEntry GENERAL_NO_ROOM_CLAIM = create("general.noroomclaim", "&cYou do not have enough space in your inventory");
	public static TranslationEntry GENERAL_BUY_NOW_DISABLED_ON_ITEM = create("general.buynowdisabledonitem", "&cBuy now is disabled on this item.");
	public static TranslationEntry GENERAL_ENDED_ALL_AUCTIONS = create("general.endedallauctions", "&cYou force ended all active auctions");
	public static TranslationEntry GENERAL_RELISTED_ITEMS = create("general.relisteditems", "&aYou relisted all expired items!");
	public static TranslationEntry GENERAL_CANNOT_SELL_BUNDLED_ITEM = create("general.cannotsellbundleditem", "&cYou cannot sell a bundled item as a bundle.");
	public static TranslationEntry GENERAL_SHULKER_BUNDLE_LIMIT = create("general.shulker bundle limit", "&cYou have too many bundles/shulkers in the bundle.");
	public static TranslationEntry GENERAL_ENTER_SEARCH_PHRASE = create("general.entersearchphrase", "&aEnter a search phrase into chat");
	public static TranslationEntry GENERAL_FILTER_ITEM_ADDED_ALREADY = create("general.filteritemaddedalready", "&cThat item already exists within that category's filter whitelist.");
	public static TranslationEntry GENERAL_ADDED_ITEM_TO_FILTER_WHITELIST = create("general.addeditemtofilterwhitelist", "&aAdded &2%item_name%&a to the &2%filter_category%&a's whitelist");
	public static TranslationEntry GENERAL_FINISH_ENTERING_PRICE = create("general.finishenteringprice", "&cPlease finish entering the new price first.");
	public static TranslationEntry GENERAL_INVALID_TIME_STRING = create("general.invalidtimestring", "&cInvalid time, use the following format, ex: 1d (valid suffixes: s,m,h,d,y)");
	public static TranslationEntry GENERAL_SOMETHING_WENT_WRONG_WHILE_LISTING = create("general.something_went_wrong_while_listing", "&cSomething went wrong while listing item.");
	public static TranslationEntry GENERAL_TOGGLED_LISTING_ON = create("general.toggled listing.on", "&aYou turned on listing messages");
	public static TranslationEntry GENERAL_TOGGLED_LISTING_OFF = create("general.toggled listing.off", "&cYou turned off listing messages");
	public static TranslationEntry GENERAL_BOUGHT_ITEM = create("general.bought_item", "&aYou bought &fx%amount% %item%&a for &a%price%");
	public static TranslationEntry GENERAL_OFFLINE_NOTIFICATIONS_MORE = create("general.offline notifications more", "&7You have &e%count%&7 more offline notifications.");
	public static TranslationEntry GENERAL_WAIT_TO_LIST = create("general.wait_to_list", "&cPlease wait &4%time%&cs before listing another item");
	public static TranslationEntry GENERAL_PLEASE_ENTER_AT_LEAST_ONE_NUMBER = create("general.please_enter_at_least_one_number", "&cPlease enter at least 1 valid number!");
	public static TranslationEntry GENERAL_CANNOT_CANCEL_ITEM_WITH_BID = create("general.cannot cancel item with bid", "&cYou cannot cancel a listing that already has a bid!");
	public static TranslationEntry GENERAL_CANNOT_LIST_DAMAGED_ITEM = create("general.cannot list damaged item", "&cCannot list a damaged item!");
	public static TranslationEntry GENERAL_NOTHING_TO_CONFIRM = create("general.nothing to confirm", "&cYou have nothing to confirm");
	public static TranslationEntry GENERAL_CONFIRM_TIME_LIMIT_REACHED = create("general.confirm time limit reached", "&cYou didn't confirm in time, request it again.");
	public static TranslationEntry GENERAL_CONFIRMED_CANCELLATION = create("general.confirmed cancellation", "&aConfirmed, all your items will be sent to collection.");
	public static TranslationEntry GENERAL_CANCELLED = create("general.cancelled", "&cCancelled.");
	public static TranslationEntry GENERAL_ERROR = create("general.error", "&cSomething went wrong.");
	public static TranslationEntry GENERAL_RELOADED = create("general.reloaded", "&aReloaded configuration and messages.");
	public static TranslationEntry PROMPTS_ENTER_DELETION_RANGE = create("prompts.enter deletion range", "&fEnter deletion range in chat (&eExample&f: &a1 hour&7)");
	public static TranslationEntry GENERAL_CART_ITEM_ALREADY_IN_CART = create("general.cart.item already in cart", "&cThat item is already in your cart");
	public static TranslationEntry GENERAL_CART_ITEM_ADDED_TO_CART = create("general.cart.item added to cart", "&aAdded that item to your cart.");
	public static TranslationEntry GENERAL_CANNOT_LIST_REPAIRED_ITEM = create("general.cannot list repaired item", "&cCannot list a repaired item!");
	public static TranslationEntry GENERAL_MARKED_CHEST = create("general.marked chest", "&aYou marked that chest as an Auction chest");
	public static TranslationEntry GENERAL_UNMARKED_CHEST = create("general.unmarked chest", "&cYou unmarked that chest as an Auction chest");
	public static TranslationEntry GENERAL_VISIT_AUCTION_CHEST = create("general.visit auction chest", "&cVisit an Auction chest to use Auction House.");
	public static TranslationEntry GENERAL_DISABLED_IN_WORLD = create("general.disabled in world", "&cAuction House is disabled in this world.");
	public static TranslationEntry GENERAL_REQUIRES_CREATIVE = create("general.requires creative", "&cThat action requires you to be in creative mode");
	public static TranslationEntry GENERAL_DELETED_TRANSACTIONS = create("general.deleted transactions", "&cDeleted a total of &4%deleted_transactions% &ctransactions");
	public static TranslationEntry GENERAL_TRANSACTION_DELETE_BEGIN = create("general.transaction delete begin", "&cBeginning transaction deletion, this may take some time.");
	public static TranslationEntry GENERAL_QTY_BUY_DISABLED = create("general.qtybuydisabled", "&4%item_owner%&c is only accepting purchases of the entire stack.");
	public static TranslationEntry GENERAL_INVALID_BID_AMOUNT = create("general.invalid bid amount", "&cBid either too low or too high");
	public static TranslationEntry GENERAL_INVALID_DELETION_RANGE = create("general.invalid deletion range", "&cPlease enter a valid deletion range");
	public static TranslationEntry GENERAL_ADMIN_CLEARED_BIDS = create("general.admin.cleared bids", "&aSuccessfully cleared any bids for the player&F: &e%player%");
	public static TranslationEntry GENERAL_PRIORITY_BOOST_BOOSTED = create("general.priority boost.boosted", "&aSuccessfully boosted this item to the top of the Auction House");
	public static TranslationEntry GENERAL_PRIORITY_BOOST_ALREADY_BOOSTED = create("general.priority boost.already boosted", "&cThis item already has priority listing");

	/*
	==============================================================
					Watchlist
	==============================================================
	 */
	public static TranslationEntry WATCHLIST_ADDED = create("watchlist.added", "&aAdded &e%item% &ato your watchlist.");
	public static TranslationEntry WATCHLIST_REMOVED = create("watchlist.removed", "&7Removed &e%item% &7from your watchlist.");
	public static TranslationEntry WATCHLIST_LIMIT_REACHED = create("watchlist.limit reached", "&cWatchlist limit reached (&e%max%&c). Remove some listings from /ah watchlist first.");
	public static TranslationEntry WATCHLIST_CANNOT_WATCH_OWN = create("watchlist.cannot watch own", "&cYou cannot watch your own listing.");

	/*
	==============================================================
					Pricing
	==============================================================
	 */
	public static TranslationEntry PRICING_REQUEST_MIN_PRICE = create("pricing.request.min price", "&cThe minimum request price must be &a%price%");
	public static TranslationEntry PRICING_REQUEST_MAX_PRICE = create("pricing.request.max price", "&cThe maximum request price is &a%price%");
	public static TranslationEntry PRICING_LIMIT_ADDED_PRICE_LIMIT = create("pricing.limit.added price limit", "&aSuccessfully added a min/max price limit to that item.");
	public static TranslationEntry PRICING_LIMIT_UPDATED_PRICE_LIMIT = create("pricing.limit.updated price limit", "&aSuccessfully updated price limits for this item.");
	public static TranslationEntry PRICING_MIN_BASE_PRICE = create("pricing.minbaseprice", "&cThe minimum base price must be &a%price%");
	public static TranslationEntry PRICING_MIN_STARTING_PRICE = create("pricing.minstartingprice", "&cThe minimum starting bid price must be &a%price%");
	public static TranslationEntry PRICING_MIN_BID_INCREMENT_PRICE = create("pricing.minbidincrementprice", "&cThe minimum bid increment must be &a%price%");
	public static TranslationEntry PRICING_MAX_BASE_PRICE = create("pricing.maxbaseprice", "&cThe maximum base price is &a%price%");
	public static TranslationEntry PRICING_MAX_STARTING_PRICE = create("pricing.maxstartingprice", "&cThe maximum starting bid price is &a%price%");
	public static TranslationEntry PRICING_MAX_BID_INCREMENT_PRICE = create("pricing.maxbidincrementprice", "&cThe maximum bid increment is &a%price%");
	public static TranslationEntry PRICING_BASE_PRICE_TOO_LOW = create("pricing.basepricetoolow", "&cThe buy now price must be higher than the starting bid.");
	public static TranslationEntry PRICING_STARTING_PRICE_TOO_HIGH = create("pricing.startingpricetoohigh", "&cThe starting bid cannot be higher than buyout. &f(&a%price%&f)");
	public static TranslationEntry PRICING_MONEY_REMOVE = create("pricing.moneyremove", "&c&l- %price% &7(%player_balance%)");
	public static TranslationEntry PRICING_MONEY_ADD = create("pricing.moneyadd", "&a&l+ %price% &7(%player_balance%)");
	public static TranslationEntry PRICING_BID_MUST_HIGHER_THAN_PREVIOUS = create("pricing.bidmusthigherthanprevious", "&cYour bid must be higher than &4%current_bid%");
	public static TranslationEntry PRICING_MIN_ITEM_PRICE = create("pricing.minitemprice", "&cThe minimum price for this item must be &a%price%");
	public static TranslationEntry PRICING_MAX_ITEM_PRICE = create("pricing.maxitemprice", "&cThe maximum price for this item is &a%price%");

	/*
	==============================================================
					Titles (prompts)
	==============================================================
	 */
	public static TranslationEntry TITLES_END_ALL_CONFIRM_TITLE = create("titles.end all confirm.title", "&eConfirm End All");
	public static TranslationEntry TITLES_END_ALL_CONFIRM_SUBTITLE = create("titles.end all confirm.subtitle", "&bType &f/&eah confirm &bto cancel all auctions");
	public static TranslationEntry TITLES_BUY_NOW_PRICE_TITLE = create("titles.buy now price.title", "&eBuy Out Price");
	public static TranslationEntry TITLES_BUY_NOW_PRICE_SUBTITLE = create("titles.buy now price.subtitle", "&fEnter new buyout price in chat");
	public static TranslationEntry TITLES_STARTING_BID_PRICE_TITLE = create("titles.starting bid price.title", "&eStarting Bid Price");
	public static TranslationEntry TITLES_STARTING_BID_PRICE_SUBTITLE = create("titles.starting bid price.subtitle", "&fEnter new starting bid in chat");
	public static TranslationEntry TITLES_BID_INCREMENT_PRICE_TITLE = create("titles.bid increment price.title", "&eBid Increment Price");
	public static TranslationEntry TITLES_BID_INCREMENT_PRICE_SUBTITLE = create("titles.bid increment price.subtitle", "&fEnter new bid increment in chat");
	public static TranslationEntry TITLES_LISTING_TIME_TITLE = create("titles.listing time.title", "&eListing Time");
	public static TranslationEntry TITLES_LISTING_TIME_SUBTITLE = create("titles.listing time.subtitle", "&fEnter new listing time in chat");
	public static TranslationEntry TITLES_LISTING_TIME_ACTIONBAR = create("titles.listing time.actionbar", "&aExample&f: &e1 day &f(&eTime Units&f: &bsecond&f, &bminute&f, &bhour&f, &bday&f, &bweek&f, &bmonth&f, &byear&f)");
	public static TranslationEntry TITLES_ENTER_BID_TITLE = create("titles.enter bid.title", "&ePlace Bid");
	public static TranslationEntry TITLES_ENTER_BID_SUBTITLE = create("titles.enter bid.subtitle", "&fEnter new bid amount in chat");
	public static TranslationEntry TITLES_ENTER_DELETION_RANGE_TITLE = create("titles.enter deletion range.title", "&eDeletion Range");
	public static TranslationEntry TITLES_ENTER_DELETION_RANGE_SUBTITLE = create("titles.enter deletion range.subtitle", "&fEnter deletion range in chat");
	public static TranslationEntry TITLES_ENTER_REQUEST_AMOUNT_TITLE = create("titles.enter request amount.title", "&eRequest Amount");
	public static TranslationEntry TITLES_ENTER_REQUEST_AMOUNT_SUBTITLE = create("titles.enter request amount.subtitle", "&fEnter requested amount in chat");
	public static TranslationEntry TITLES_ENTER_REQUEST_PRICE_TITLE = create("titles.enter request price.title", "&eRequest Price");
	public static TranslationEntry TITLES_ENTER_REQUEST_PRICE_SUBTITLE = create("titles.enter request price.subtitle", "&fEnter how much you'll pay in chat");
	public static TranslationEntry TITLES_BAN_REASON_TITLE = create("titles.ban reason.title", "&eBan Reason");
	public static TranslationEntry TITLES_BAN_REASON_SUBTITLE = create("titles.ban reason.subtitle", "&fEnter reason for ban in chat");
	public static TranslationEntry TITLES_BAN_LENGTH_TITLE = create("titles.ban length.title", "&eBan Length");
	public static TranslationEntry TITLES_BAN_LENGTH_SUBTITLE = create("titles.ban length.subtitle", "&fEnter ban length in chat. (ex. 1 day 12 hours)");
	public static TranslationEntry TITLES_MATERIAL_SEARCH_TITLE = create("titles.material search.title", "&eSearch for material");
	public static TranslationEntry TITLES_MATERIAL_SEARCH_SUBTITLE = create("titles.material search.subtitle", "&fEnter item/material name in chat");
	public static TranslationEntry TITLES_PRICE_LIMIT_MIN_TITLE = create("titles.price limit.min.title", "&eAdjust Price Limit");
	public static TranslationEntry TITLES_PRICE_LIMIT_MIN_SUBTITLE = create("titles.price limit.min.subtitle", "&fEnter new min price for this item in chat");
	public static TranslationEntry TITLES_PRICE_LIMIT_MAX_TITLE = create("titles.price limit.max.title", "&eAdjust Price Limit");
	public static TranslationEntry TITLES_PRICE_LIMIT_MAX_SUBTITLE = create("titles.price limit.max.subtitle", "&fEnter new max price for this item in chat");

	/*
	==============================================================
					Transaction / Discord / Ban / Auction
	==============================================================
	 */
	public static TranslationEntry TRANSACTION_SALE_TYPE_BID_WON = create("transaction.sale_type.bid_won", "Won Auction");
	public static TranslationEntry TRANSACTION_SALE_TYPE_IMMEDIATE_BUY = create("transaction.sale_type.immediate_buy", "Bought Immediately");
	public static TranslationEntry DISCORD_PLAYER_LOST = create("discord.player_lost", "Player Lost o.O");
	public static TranslationEntry DISCORD_NO_BUYER = create("discord.no_buyer", "No Buyer");
	public static TranslationEntry DISCORD_NOT_SOLD = create("discord.not_sold", "Not Sold");
	public static TranslationEntry DISCORD_NOT_BOUGHT = create("discord.not_bought", "Was not bought");
	public static TranslationEntry DISCORD_SALE_BID_WIN = create("discord.sale_bid_win", "Won Bid");
	public static TranslationEntry DISCORD_SALE_IMMEDIATE_BUY = create("discord.sale_immediate_buy", "Bought Immediately");
	public static TranslationEntry DISCORD_IS_BID_TRUE = create("discord.is_bid_true", "true");
	public static TranslationEntry DISCORD_IS_BID_FALSE = create("discord.is_bid_false", "false");
	public static TranslationEntry BAN_USER_ALREADY_BANNED = create("ban.user already banned", "&4%player_name% &cis already banned!");
	public static TranslationEntry BAN_USER_NOT_BANNED = create("ban.user not banned", "&4%player_name% &cis not banned!");
	public static TranslationEntry BAN_USER_BANNED = create("ban.user banned", "&4%player_name% &cis now banned!");
	public static TranslationEntry BAN_USER_UNBANNED = create("ban.user unbanned", "&4%player_name% &cis no longer banned!");
	public static TranslationEntry BAN_SELECT_BAN_TYPE = create("ban.select ban type", "&cPlease select at least one ban type");
	public static TranslationEntry BAN_PLAYER_PERMANENTLY_BANNED = create("ban.player permanently banned", "&cYou are permanently banned!");
	public static TranslationEntry BAN_PLAYER_STILL_BANNED = create("ban.player still banned", "&cYou are banned until &e%ban_expiration%");
	public static TranslationEntry BAN_PLAYER_BAN_EXPIRED = create("ban.player ban expired", "&aYou are no longer banned!");

	/*
	==============================================================
					Auction Filter / Transaction Filter / Stats
	==============================================================
	 */
	public static TranslationEntry AUCTION_FILTER_SALE_TYPES_BIDDABLE = create("auction_filter.sale_types.biddable", "Biddable");
	public static TranslationEntry AUCTION_FILTER_SALE_TYPES_NON_BIDDABLE = create("auction_filter.sale_types.non_biddable", "Not Biddable");
	public static TranslationEntry AUCTION_FILTER_SALE_TYPES_BOTH = create("auction_filter.sale_types.both", "All");
	public static TranslationEntry AUCTION_FILTER_CATEGORIES_ALL = create("auction_filter.categories.all", "All");
	public static TranslationEntry AUCTION_FILTER_CATEGORIES_FOOD = create("auction_filter.categories.food", "Food");
	public static TranslationEntry AUCTION_FILTER_CATEGORIES_ARMOR = create("auction_filter.categories.armor", "Armor");
	public static TranslationEntry AUCTION_FILTER_CATEGORIES_BLOCKS = create("auction_filter.categories.blocks", "Blocks");
	public static TranslationEntry AUCTION_FILTER_CATEGORIES_POTIONS = create("auction_filter.categories.potions", "Potions");
	public static TranslationEntry AUCTION_FILTER_CATEGORIES_TOOLS = create("auction_filter.categories.tools", "Tools");
	public static TranslationEntry AUCTION_FILTER_CATEGORIES_MISC = create("auction_filter.categories.misc", "Misc");
	public static TranslationEntry AUCTION_FILTER_CATEGORIES_SPAWNERS = create("auction_filter.categories.spawners", "Spawners");
	public static TranslationEntry AUCTION_FILTER_CATEGORIES_ENCHANTS = create("auction_filter.categories.enchants", "Enchants");
	public static TranslationEntry AUCTION_FILTER_CATEGORIES_WEAPONS = create("auction_filter.categories.weapons", "Weapons");
	public static TranslationEntry AUCTION_FILTER_CATEGORIES_SELF = create("auction_filter.categories.self", "Self");
	public static TranslationEntry AUCTION_FILTER_CATEGORIES_SEARCH = create("auction_filter.categories.search", "Search");
	public static TranslationEntry AUCTION_FILTER_SORT_ORDER_RECENT = create("auction_filter.sort_order.recent", "Recent");
	public static TranslationEntry AUCTION_FILTER_SORT_ORDER_PRICE = create("auction_filter.sort_order.price", "Price");
	public static TranslationEntry AUCTION_FILTER_SORT_ORDER_OLDEST = create("auction_filter.sort_order.oldest", "Oldest");
	public static TranslationEntry AUCTION_FILTER_CURRENCY_ALL_CURRENCIES = create("auction_filter.currency.all currencies", "All Currencies");
	public static TranslationEntry AUCTION_FILTER_CURRENCY_ITEM_CURRENCY = create("auction_filter.currency.item currency", "Item Currency");
	public static TranslationEntry TRANSACTION_FILTER_BUY_TYPE_SOLD = create("transaction_filter.buy_type.sold", "Sold");
	public static TranslationEntry TRANSACTION_FILTER_BUY_TYPE_BOUGHT = create("transaction_filter.buy_type.bought", "Bought");
	public static TranslationEntry TRANSACTION_FILTER_BUY_TYPE_ALL = create("transaction_filter.buy_type.all", "All");
	public static TranslationEntry AUCTION_STATISTIC_CREATED_AUCTION = create("auction_statistic.created_auction", "Created Auction");
	public static TranslationEntry AUCTION_STATISTIC_CREATED_BIN = create("auction_statistic.created_bin", "Created Bin");
	public static TranslationEntry AUCTION_STATISTIC_SOLD_AUCTIONS = create("auction_statistic.sold_auctions", "Sold Auctions");
	public static TranslationEntry AUCTION_STATISTIC_SOLD_BINS = create("auction_statistic.sold_bins", "Sold Bins");
	public static TranslationEntry AUCTION_STATISTIC_MONEY_SPENT = create("auction_statistic.money_spent", "Money Spent");
	public static TranslationEntry AUCTION_STATISTIC_MONEY_EARNED = create("auction_statistic.money_earned", "Money Earned");

	/*
	==============================================================
					Auction messages
	==============================================================
	 */
	public static TranslationEntry AUCTION_LISTED_WITH_BID = create("auction.listed.withbid", "&eListed &fx%amount% &6%item% &e&lBuy Now&f: &a%base_price% &e&lStarting&f: &a%start_price% &e&lIncrement&f: &a%increment_price%");
	public static TranslationEntry AUCTION_LISTED_NO_BID = create("auction.listed.nobid", "&eListed &fx%amount% &6%item% &efor &a%base_price%");
	public static TranslationEntry AUCTION_LISTED_REQUEST = create("auction.listed.request", "&eRequested &fx%amount% &6%item%&f(s) &efor &a%base_price%");
	public static TranslationEntry AUCTION_BROADCAST_WITH_BID = create("auction.broadcast.withbid", "&e%player% listed &fx%amount% &6%item% &e&lBuy Now&f: &a%base_price% &e&lStarting&f: &a%start_price% &e&lIncrement&f: &a%increment_price%");
	public static TranslationEntry AUCTION_BROADCAST_NO_BID = create("auction.broadcast.nobid", "&e%player% listed &fx%amount% &6%item% &efor &a%base_price%");
	public static TranslationEntry AUCTION_BROADCAST_SOLD = create("auction.broadcast.sold", "&e&fx%amount% &6%item% &esold to %player% for &a%price%");
	public static TranslationEntry AUCTION_BROADCAST_SERVER_LISTING = create("auction.broadcast.serverlisting", "&e&fx%amount% &6%item% &ehas appeared on the auction house.");
	public static TranslationEntry AUCTION_BROADCAST_BID = create("auction.broadcast.bid", "&e%player% increased the bid to &a%amount% &eon &6%item%");
	public static TranslationEntry AUCTION_BROADCAST_ENDING = create("auction.broadcast.ending", "&eAuction for &6%item% &eis ending in &6%seconds%&es");
	public static TranslationEntry AUCTION_BID_WON = create("auction.bidwon", "&eYou won the bid for&fx%amount% &6%item% &efor &a%price%");
	public static TranslationEntry AUCTION_ITEM_SOLD = create("auction.itemsold", "&eYou sold &6%item% &eto &6%buyer_name% &efor &a%price%");
	public static TranslationEntry AUCTION_ITEM_NOT_AVAILABLE = create("auction.itemnotavailable", "&cThat item is no longer available :(");
	public static TranslationEntry AUCTION_BID_ITEM_WITH_DISABLED_BUY_NOW = create("auction.biditemwithdisabledbuynow", "&CN/A");
	public static TranslationEntry AUCTION_OUTBID = create("auction.outbid", "&6%player% &ehas out bid you for &6%item%");
	public static TranslationEntry AUCTION_PLACED_BID = create("auction.placedbid", "&6%player% &eincreased the bid to &a%amount% &eon &6%item%");
	public static TranslationEntry AUCTION_NO_BIDS = create("auction.nobids", "&cNo Bids");
	public static TranslationEntry AUCTION_TAX_CANNOT_PAY_LISTING_FEE = create("auction.tax.cannotpaylistingfee", "&cYou do not have enough money to pay the listing fee &f(&4%price%&f)");
	public static TranslationEntry AUCTION_TAX_PAID_LISTING_FEE = create("auction.tax.paidlistingfee", "&aPaid &2%price%&a listing fee");
	public static TranslationEntry ADMIN_ACTION_RETURN = create("admin action.return", "Returned");
	public static TranslationEntry ADMIN_ACTION_CLAIM = create("admin action.claim", "Claimed");
	public static TranslationEntry ADMIN_ACTION_DELETE = create("admin action.delete", "Deleted");
	public static TranslationEntry ADMIN_ACTION_COPY = create("admin action.copy", "Copied");
	public static TranslationEntry PAYMENTS_LISTING_FAILED = create("payments.listing failed", "&cItem could not listed, fee returned");
	public static TranslationEntry PAYMENTS_ITEM_SOLD = create("payments.item sold", "&aThis item was sold/won by someone");
	public static TranslationEntry PAYMENTS_ADMIN_REMOVED = create("payments.admin removed", "&cItem was removed by an admin");
	public static TranslationEntry PAYMENTS_BID_RETURNED = create("payments.bid returned", "&bOutbid, your original bid was returned");

	/*
	==============================================================
					Commands (syntax + descriptions)
	==============================================================
	 */
	public static TranslationEntry COMMANDS_INVALID_SYNTAX = create("commands.invalid_syntax", "&7The valid syntax is: &6%syntax%&7.");
	public static TranslationEntry COMMANDS_NO_PERMISSION = create("commands.no_permission", "&dYou do not have permission to do that.");
	public static TranslationEntry COMMANDS_SELL_ARGS_SUGGESTION_ONE = create("commands.sell.args.suggestion one", "100");
	public static TranslationEntry COMMANDS_SELL_ARGS_SUGGESTION_TWO = create("commands.sell.args.suggestion two", "50 -b");
	public static TranslationEntry COMMANDS_SELL_ARGS_SUGGESTION_THREE = create("commands.sell.args.suggestion three", "10 -b");
	public static TranslationEntry COMMANDS_SYNTAX_ACTIVE = create("commands.syntax.active", "active");
	public static TranslationEntry COMMANDS_SYNTAX_CART = create("commands.syntax.cart", "cart");
	public static TranslationEntry COMMANDS_SYNTAX_AUCTIONHOUSE = create("commands.syntax.auctionhouse", "/ah");
	public static TranslationEntry COMMANDS_SYNTAX_CONVERT = create("commands.syntax.convert", "convert");
	public static TranslationEntry COMMANDS_SYNTAX_CONFIRM = create("commands.syntax.confirm", "confirm");
	public static TranslationEntry COMMANDS_SYNTAX_EXPIRED = create("commands.syntax.expired", "expired");
	public static TranslationEntry COMMANDS_SYNTAX_RELOAD = create("commands.syntax.reload", "reload");
	public static TranslationEntry COMMANDS_SYNTAX_SEARCH = create("commands.syntax.search", "search <keywords>");
	public static TranslationEntry COMMANDS_SYNTAX_SELL = create("commands.syntax.sell", "sell <basePrice> [bidStart] [bidIncr]");
	public static TranslationEntry COMMANDS_SYNTAX_SETTINGS = create("commands.syntax.settings", "settings");
	public static TranslationEntry COMMANDS_SYNTAX_TRANSACTIONS = create("commands.syntax.transactions", "transactions");
	public static TranslationEntry COMMANDS_SYNTAX_UPLOAD = create("commands.syntax.upload", "upload");
	public static TranslationEntry COMMANDS_SYNTAX_FILTER = create("commands.syntax.filter", "filter [additem] [category]");
	public static TranslationEntry COMMANDS_SYNTAX_BAN = create("commands.syntax.ban", "ban [player]");
	public static TranslationEntry COMMANDS_SYNTAX_UNBAN = create("commands.syntax.unban", "unban <player>");
	public static TranslationEntry COMMANDS_SYNTAX_TOGGLELISTINFO = create("commands.syntax.togglelistinfo", "togglelistinfo");
	public static TranslationEntry COMMANDS_SYNTAX_MARKCHEST = create("commands.syntax.markchest", "markchest");
	public static TranslationEntry COMMANDS_SYNTAX_PRICE_LIMIT = create("commands.syntax.price limit", "pricelimit [set <min/max> <price>]");
	public static TranslationEntry COMMANDS_SYNTAX_STATS = create("commands.syntax.stats", "stats [player]");
	public static TranslationEntry COMMANDS_SYNTAX_REQUEST = create("commands.syntax.request", "request <price> [-single]");
	public static TranslationEntry COMMANDS_SYNTAX_WATCHLIST = create("commands.syntax.watchlist", "watchlist");
	public static TranslationEntry COMMANDS_SYNTAX_DEBUG = create("commands.syntax.debug", "debug");
	public static TranslationEntry COMMANDS_DESCRIPTION_ACTIVE = create("commands.description.active", "View all your auction listings");
	public static TranslationEntry COMMANDS_DESCRIPTION_CART = create("commands.description.cart", "Used to view your cart");
	public static TranslationEntry COMMANDS_DESCRIPTION_AUCTIONHOUSE = create("commands.description.auctionhouse", "Main command for the plugin, it opens the auction window.");
	public static TranslationEntry COMMANDS_DESCRIPTION_CONFIRM = create("commands.description.confirm", "Used to confirm an action");
	public static TranslationEntry COMMANDS_DESCRIPTION_CONVERT = create("commands.description.convert", "Used to make an attempted conversion from < 2.0.0+");
	public static TranslationEntry COMMANDS_DESCRIPTION_EXPIRED = create("commands.description.expired", "View all your expired/cancelled listings");
	public static TranslationEntry COMMANDS_DESCRIPTION_RELOAD = create("commands.description.reload", "Reload plugin files");
	public static TranslationEntry COMMANDS_DESCRIPTION_SEARCH = create("commands.description.search", "Search for specific item(s) in the auction house");
	public static TranslationEntry COMMANDS_DESCRIPTION_SELL = create("commands.description.sell", "Used to put an item up for auction");
	public static TranslationEntry COMMANDS_DESCRIPTION_SETTINGS = create("commands.description.settings", "Open the in-game config editor");
	public static TranslationEntry COMMANDS_DESCRIPTION_TRANSACTIONS = create("commands.description.transactions", "Used to open the transactions menu");
	public static TranslationEntry COMMANDS_DESCRIPTION_UPLOAD = create("commands.description.upload", "Used to upload flat file data to the database");
	public static TranslationEntry COMMANDS_DESCRIPTION_FILTER = create("commands.description.filter", "Edit the filter whitelist items");
	public static TranslationEntry COMMANDS_DESCRIPTION_BAN = create("commands.description.ban", "Ban a player from the auction house for a set amount of time.");
	public static TranslationEntry COMMANDS_DESCRIPTION_UNBAN = create("commands.description.unban", "Unban a player from the auction house");
	public static TranslationEntry COMMANDS_DESCRIPTION_TOGGLELISTINFO = create("commands.description.togglelistinfo", "Toggle whether auction house should message you when you list an item");
	public static TranslationEntry COMMANDS_DESCRIPTION_MARKCHEST = create("commands.description.markchest", "Toggles whether a chest is an auction chest");
	public static TranslationEntry COMMANDS_DESCRIPTION_PRICE_LIMIT = create("commands.description.price limit", "Adds a minimum sell price to an item");
	public static TranslationEntry COMMANDS_DESCRIPTION_STATS = create("commands.description.stats", "View yours or another players stats");
	public static TranslationEntry COMMANDS_DESCRIPTION_REQUEST = create("commands.description.request", "Makes request for item your holding");
	public static TranslationEntry COMMANDS_DESCRIPTION_WATCHLIST = create("commands.description.watchlist", "View your saved/watchlist listings");
	public static TranslationEntry COMMANDS_DESCRIPTION_DEBUG = create("commands.description.debug", "Toggle debug mode for development and research");

	/*
	==============================================================
					Command error info (multi-line)
	==============================================================
	 */
	public static TranslationEntry COMMAND_INFO_ERROR_INFORMATION = create("command info.error information",
			"&8&m-----------------------------------------------------",
			"<center>%pl_name%",
			"<center>&cSeems like you entered that command incorrectly.",
			"",
			"<center>&6<> &f- &7Required arguments",
			"<center>&8[] &f- &7Optional arguments",
			"",
			"<center>&aHere is the correct usage&F:",
			"<center>&f/&eah %syntax%",
			"",
			"&8&m-----------------------------------------------------"
	);

	/*
	==============================================================
					Errors (ban)
	==============================================================
	 */
	public static TranslationEntry BAN_PLAYER_IS_ALREADY_BANNED = create("error.bans.player already banned", "&4%player_name% &cis already banned!");


	/*
	==============================================================
					GUI – global items (legacy paths)
	==============================================================
	 */
	public static TranslationEntry GUI_GLOBAL_BACK_NAME = create("gui.global items.back button.name", "&e<< Previous Page");
	public static TranslationEntry GUI_GLOBAL_BACK_LORE = create("gui.global items.back button.lore", "&7Click the button to go", "&7back to the previous page.");
	public static TranslationEntry GUI_GLOBAL_PREV_PAGE_NAME = create("gui.global items.previous page button.name", "&e<< Previous Page");
	public static TranslationEntry GUI_GLOBAL_PREV_PAGE_LORE = create("gui.global items.previous page button.lore", "&7Click the button to go", "&7back to the previous page.");
	public static TranslationEntry GUI_GLOBAL_CLOSE_NAME = create("gui.global items.close button.name", "&cClose");
	public static TranslationEntry GUI_GLOBAL_CLOSE_LORE = create("gui.global items.close button.lore", "&7Click to close this menu.");
	public static TranslationEntry GUI_GLOBAL_NEXT_PAGE_NAME = create("gui.global items.next page button.name", "&eNext Page >>");
	public static TranslationEntry GUI_GLOBAL_NEXT_PAGE_LORE = create("gui.global items.next page button.lore", "&7Click the button to go", "&7to the next page.");
	public static TranslationEntry GUI_GLOBAL_REFRESH_NAME = create("gui.global items.refresh button.name", "&6&LRefresh Page");
	public static TranslationEntry GUI_GLOBAL_REFRESH_LORE = create("gui.global items.refresh button.lore", "&7Click to refresh the page");
	public static TranslationEntry GUI_GLOBAL_CURRENCY_NAME = create("gui.global items.currency.name", "&e&lCurrency");
	public static TranslationEntry GUI_GLOBAL_CURRENCY_LORE = create("gui.global items.currency.lore", "", "&7Click to change currency");

	/*
	==============================================================
					GUI – currency picker, material picker, cart
	==============================================================
	 */
	public static TranslationEntry GUI_CURRENCY_PICKER_TITLE = create("gui.currency picker.title", "&7Auction House &f- &ePick Currency");
	public static TranslationEntry GUI_CURRENCY_PICKER_CUSTOM_NAME = create("gui.currency picker.items.custom currency.name", "&e&lCustom Currency");
	public static TranslationEntry GUI_CURRENCY_PICKER_CUSTOM_LORE = create("gui.currency picker.items.custom currency.lore",
			"&7If you want to use use a specific item for",
			"&7the currency, you can set that here.",
			"",
			"&e&lLeft Click &7with the item you want to use",
			"&b&lRight Click &7to open a material picker &eor",
			"&7as the currency onto this icon."
	);
	public static TranslationEntry GUI_CURRENCY_PICKER_CURRENCY_LORE = create("gui.currency picker.items.currency.lore", "&e&lClick &7to select this currency");
	public static TranslationEntry GUI_MATERIAL_PICKER_TITLE = create("gui.material picker.title", "&7Auction House &f- &ePick an Item");
	public static TranslationEntry GUI_MATERIAL_PICKER_MATERIAL_LORE = create("gui.material picker.items.material.lore", "&7Click to select this item");
	public static TranslationEntry GUI_MATERIAL_PICKER_SEARCH_NAME = create("gui.material picker.items.search.name", "&e&lSearch");
	public static TranslationEntry GUI_MATERIAL_PICKER_SEARCH_LORE = create("gui.material picker.items.search.lore", "&7Click to search for materials");
	public static TranslationEntry GUI_MATERIAL_PICKER_RESET_NAME = create("gui.material picker.items.reset.name", "&c&lClear Search");
	public static TranslationEntry GUI_MATERIAL_PICKER_RESET_LORE = create("gui.material picker.items.reset.lore", "&7Click to clear search");
	public static TranslationEntry GUI_CART_TITLE = create("gui.cart.title", "&7Auction House &f- &EYour Cart");
	public static TranslationEntry GUI_CART_CHECKOUT_NAME = create("gui.cart.items.checkout.name", "&e&lCheckout");
	public static TranslationEntry GUI_CART_CHECKOUT_LORE = create("gui.cart.items.checkout.lore", "&7Used to checkout this cart", "&eClick to checkout");

	/*
	==============================================================
					GUI – main auction house
	==============================================================
	 */
	public static TranslationEntry GUI_AUCTION_HOUSE_TITLE = create("gui.auction house.title", "&7Auction House");
	public static TranslationEntry GUI_AUCTION_HOUSE_GUIDE_NAME = create("gui.auction house.items.guide.name", "&e&lGuide");
	public static TranslationEntry GUI_AUCTION_HOUSE_GUIDE_LORE = create("gui.auction house.items.guide.lore",
			"&7This is the auction house, here you can",
			"&7list items for sale and purchase items",
			"&7that others have listed for sale.",
			"",
			"&7The auction is also a great way to make",
			"&7money by selling farmable items other",
			"&7players may be interested in buying.",
			"",
			"&7Any unsold items are sent to your collection bin.",
			"",
			"&7Each item is listed as a auction, so",
			"&7players can out bid each other, or simply",
			"&7purchase it right away with a set price."
	);
	public static TranslationEntry GUI_AUCTION_HOUSE_TRANSACTIONS_NAME = create("gui.auction house.items.transactions.name", "&e&lTransactions");
	public static TranslationEntry GUI_AUCTION_HOUSE_TRANSACTIONS_LORE = create("gui.auction house.items.transactions.lore",
			"&7Click to view transaction history",
			"",
			"&eTotal Items Bought&f: &a%total_items_bought%",
			"&eTotal Items Sold&f: &a%total_items_sold%"
	);
	public static TranslationEntry GUI_AUCTION_HOUSE_HOW_TO_SELL_NAME = create("gui.auction house.items.how to sell.name", "&e&lHow to Sell");
	public static TranslationEntry GUI_AUCTION_HOUSE_HOW_TO_SELL_LORE = create("gui.auction house.items.how to sell.lore",
			"&7To list an item on the auction house, just hold",
			"&7the item in your hand and type the following command.",
			"&e/ah sell <buyNowPrice> [startPrice] [bidIncrement]"
	);
	public static TranslationEntry GUI_AUCTION_HOUSE_CART_NAME = create("gui.auction house.items.cart.name", "&e&lCart");
	public static TranslationEntry GUI_AUCTION_HOUSE_CART_LORE = create("gui.auction house.items.cart.lore",
			"&7Click to view your cart",
			"",
			"&eTotal Items&f: &a%cart_item_count%"
	);
	public static TranslationEntry GUI_AUCTION_HOUSE_LIST_ITEM_NAME = create("gui.auction house.items.list new item.name", "&e&lList Item");
	public static TranslationEntry GUI_AUCTION_HOUSE_LIST_ITEM_LORE = create("gui.auction house.items.list new item.lore", "&7Click to list an item on the auction house.");
	public static TranslationEntry GUI_AUCTION_HOUSE_YOUR_AUCTIONS_NAME = create("gui.auction house.items.your auctions.name", "&e&lYour Auctions");
	public static TranslationEntry GUI_AUCTION_HOUSE_YOUR_AUCTIONS_LORE = create("gui.auction house.items.your auctions.lore",
			"&7Click here to view all of the items you",
			"&7are currently selling on the auction.",
			"",
			"&e&l%active_player_auctions% Item(s)",
			"&e&lBalance &a%player_balance%"
	);
	public static TranslationEntry GUI_AUCTION_HOUSE_COLLECTION_BIN_NAME = create("gui.auction house.items.collection bin.name", "&e&lCollection Bin");
	public static TranslationEntry GUI_AUCTION_HOUSE_COLLECTION_BIN_LORE = create("gui.auction house.items.collection bin.lore",
			"&7Click here to view and collect all of the",
			"&7items you have cancelled or have expired.",
			"",
			"&e&l%expired_player_auctions% Item(s)"
	);
	public static TranslationEntry GUI_AUCTION_HOUSE_WATCHLIST_NAME = create("gui.auction house.items.watchlist.name", "&e&lWatchlist");
	public static TranslationEntry GUI_AUCTION_HOUSE_WATCHLIST_LORE = create("gui.auction house.items.watchlist.lore",
			"&7Click here to view listings you have",
			"&7saved to your watchlist.",
			"",
			"&e&l%watchlist_count% Listing(s)"
	);
	public static TranslationEntry GUI_AUCTION_HOUSE_FILTER_NAME = create("gui.auction house.items.filter.name", "&e&lFilter Options");
	public static TranslationEntry GUI_AUCTION_HOUSE_FILTER_LORE = create("gui.auction house.items.filter.lore",
			"&eItem Category&f: &7%filter_category%",
			"&eAuction Type&f: &7%filter_auction_type%",
			"&eSort Order&f: &7%filter_sort_order%",
			"&eCurrency&f: &7%filter_currency%",
			"",
			"&7Left-Click to change item category",
			"&7Right-Click to change change auction type",
			"&7Shift Right-Click to change sort order",
			"&7Shift Left-Click to change currency",
			"&7Press Drop to reset filters"
	);
	public static TranslationEntry GUI_AUCTION_HOUSE_FILTER_MENU_NAME = create("gui.auction house.items.filter menu.name", "&e&lCurrent Filter&f: &6%filter_category%");
	public static TranslationEntry GUI_AUCTION_HOUSE_FILTER_MENU_LORE = create("gui.auction house.items.filter menu.lore",
			"&eItem Category&f: &7%filter_category%",
			"&eAuction Type&f: &7%filter_auction_type%",
			"&eSort Order&f: &7%filter_sort_order%",
			"",
			"&7Left-Click to change item category",
			"&7Right-Click to change change auction type",
			"&7Shift Right-Click to change sort order",
			"&7Press Drop to reset filters"
	);

	public static TranslationEntry GUI_WATCHLIST_TITLE = create("gui.watchlist.title", "&e&lYour Watchlist");
	public static TranslationEntry GUI_WATCHLIST_EMPTY_NAME = create("gui.watchlist.empty.name", "&cNo watched listings");
	public static TranslationEntry GUI_WATCHLIST_EMPTY_LORE = create("gui.watchlist.empty.lore", "&7Use middle-click on a listing in the AH to add it here.");

	/*
	==============================================================
					GUI – confirm buy / request / listing / bid / cancel / general
	==============================================================
	 */
	public static TranslationEntry GUI_CONFIRM_BUY_TITLE = create("gui.confirm buy.title", "&7Are you sure?");
	public static TranslationEntry GUI_CONFIRM_BUY_INCREASE_NAME = create("gui.confirm buy.increase button.name", "&a&l+1");
	public static TranslationEntry GUI_CONFIRM_BUY_INCREASE_LORE = create("gui.confirm buy.increase button.lore", "&7Click to add &a+1 &7to purchase quantity");
	public static TranslationEntry GUI_CONFIRM_BUY_DECREASE_NAME = create("gui.confirm buy.decrease button.name", "&c&l-1");
	public static TranslationEntry GUI_CONFIRM_BUY_DECREASE_LORE = create("gui.confirm buy.decrease button.lore", "&7Click to remove &c-1 &7from the purchase quantity");
	public static TranslationEntry GUI_CONFIRM_BUY_QTY_INFO_NAME = create("gui.confirm buy.qty info.name", "&ePurchase Information");
	public static TranslationEntry GUI_CONFIRM_BUY_QTY_INFO_LORE = create("gui.confirm buy.qty info.lore",
			"&7Original Stack Size&f: &e%original_stack_size%",
			"&7Price for entire stack&f: &a%original_stack_price%",
			"&7Price per item&f: &a%price_per_item%",
			"",
			"&7Purchase Qty&f: &e%purchase_quantity%",
			"&7Total&f: &a%purchase_price%"
	);
	public static TranslationEntry GUI_CONFIRM_BUY_NO_NAME = create("gui.confirm buy.no.name", "&c&LCancel");
	public static TranslationEntry GUI_CONFIRM_BUY_NO_LORE = create("gui.confirm buy.no.lore", "&7Click to cancel your purchase");
	public static TranslationEntry GUI_CONFIRM_BUY_YES_NAME = create("gui.confirm buy.yes.name", "&a&lConfirm");
	public static TranslationEntry GUI_CONFIRM_BUY_YES_LORE = create("gui.confirm buy.yes.lore", "&7Click to confirm your purchase");
	public static TranslationEntry GUI_CONFIRM_REQUEST_NO_NAME = create("gui.confirm request.no.name", "&c&LCancel");
	public static TranslationEntry GUI_CONFIRM_REQUEST_NO_LORE = create("gui.confirm request.no.lore", "&7Click to cancel your purchase");
	public static TranslationEntry GUI_CONFIRM_REQUEST_YES_NAME = create("gui.confirm request.yes.name", "&a&lConfirm");
	public static TranslationEntry GUI_CONFIRM_REQUEST_YES_LORE = create("gui.confirm request.yes.lore", "&7Click to confirm your purchase");
	public static TranslationEntry GUI_CONFIRM_LISTING_TITLE = create("gui.confirm listing.title", "&7Are you sure?");
	public static TranslationEntry GUI_CONFIRM_LISTING_NO_NAME = create("gui.confirm listing.no.name", "&c&LCancel");
	public static TranslationEntry GUI_CONFIRM_LISTING_NO_LORE = create("gui.confirm listing.no.lore", "&7Click to cancel listing");
	public static TranslationEntry GUI_CONFIRM_LISTING_YES_NAME = create("gui.confirm listing.yes.name", "&a&lConfirm");
	public static TranslationEntry GUI_CONFIRM_LISTING_YES_LORE = create("gui.confirm listing.yes.lore", "&7Click to list your item");
	public static TranslationEntry GUI_CONFIRM_BID_TITLE = create("gui.confirm bid.title", "&7Are you sure?");
	public static TranslationEntry GUI_CONFIRM_BID_NO_NAME = create("gui.confirm bid.no.name", "&c&LCancel");
	public static TranslationEntry GUI_CONFIRM_BID_NO_LORE = create("gui.confirm bid.no.lore", "&7Click to cancel your bid");
	public static TranslationEntry GUI_CONFIRM_BID_YES_NAME = create("gui.confirm bid.yes.name", "&a&lConfirm");
	public static TranslationEntry GUI_CONFIRM_BID_YES_LORE = create("gui.confirm bid.yes.lore", "&7Click to confirm your bid");
	public static TranslationEntry GUI_CONFIRM_CANCEL_TITLE = create("gui.confirm cancel.title", "&7Are you sure?");
	public static TranslationEntry GUI_CONFIRM_CANCEL_NO_NAME = create("gui.confirm cancel.no.name", "&c&LCancel");
	public static TranslationEntry GUI_CONFIRM_CANCEL_NO_LORE = create("gui.confirm cancel.no.lore", "&7Click to cancel item removal");
	public static TranslationEntry GUI_CONFIRM_CANCEL_YES_NAME = create("gui.confirm cancel.yes.name", "&a&lConfirm");
	public static TranslationEntry GUI_CONFIRM_CANCEL_YES_LORE = create("gui.confirm cancel.yes.lore", "&7Click to confirm item cancellation");
	public static TranslationEntry GUI_CONFIRM_GENERAL_TITLE = create("gui.confirm general.title", "&7Are you sure?");
	public static TranslationEntry GUI_CONFIRM_GENERAL_NO_NAME = create("gui.confirm general.no.name", "&c&LCancel");
	public static TranslationEntry GUI_CONFIRM_GENERAL_NO_LORE = create("gui.confirm general.no.lore", "&7Click to cancel");
	public static TranslationEntry GUI_CONFIRM_GENERAL_YES_NAME = create("gui.confirm general.yes.name", "&a&lConfirm");
	public static TranslationEntry GUI_CONFIRM_GENERAL_YES_LORE = create("gui.confirm general.yes.lore", "&7Click to retrieve item");

	/*
	==============================================================
					GUI – active / expired / payment collection / transactions
	==============================================================
	 */
	public static TranslationEntry GUI_ACTIVE_AUCTIONS_TITLE = create("gui.active auctions.title", "&7Active Listings");
	public static TranslationEntry GUI_ACTIVE_AUCTIONS_CANCEL_ALL_NAME = create("gui.active auctions.cancel all.name", "&e&lEnd All");
	public static TranslationEntry GUI_ACTIVE_AUCTIONS_CANCEL_ALL_LORE = create("gui.active auctions.cancel all.lore",
			"&7Click here to end all of the active listings",
			"&7that you have posted."
	);
	public static TranslationEntry GUI_ACTIVE_BIDS_TITLE = create("gui.active bids.title", "&7Your Winning Bids");
	public static TranslationEntry GUI_EXPIRED_AUCTIONS_TITLE = create("gui.expired auctions.title", "&7Expired Listings");
	public static TranslationEntry GUI_EXPIRED_AUCTIONS_CLAIM_ALL_NAME = create("gui.expired auctions.cancel all.name", "&e&lClaim All");
	public static TranslationEntry GUI_EXPIRED_AUCTIONS_CLAIM_ALL_LORE = create("gui.expired auctions.cancel all.lore", "&7Click here to claim all of your expired auctions");
	public static TranslationEntry GUI_EXPIRED_AUCTIONS_COLLECT_PAYMENTS_NAME = create("gui.expired auctions.collect payments.name", "&e&lCollect Payments");
	public static TranslationEntry GUI_EXPIRED_AUCTIONS_COLLECT_PAYMENTS_LORE = create("gui.expired auctions.collect payments.lore", "&7Click here to view your payments");
	public static TranslationEntry GUI_PAYMENT_COLLECTION_TITLE = create("gui.payment collection.title", "&7Payment Collection");
	public static TranslationEntry GUI_PAYMENT_COLLECTION_CLAIM_ALL_NAME = create("gui.payment collection.claim all.name", "&e&lClaim All");
	public static TranslationEntry GUI_PAYMENT_COLLECTION_CLAIM_ALL_LORE = create("gui.payment collection.claim all.lore", "&7Click here to claim all of your payments");
	public static TranslationEntry GUI_PAYMENT_COLLECTION_PAYMENT_NAME = create("gui.payment collection.payment.name", "&a&l%payment_amount%");
	public static TranslationEntry GUI_PAYMENT_COLLECTION_PAYMENT_LORE = create("gui.payment collection.payment.lore",
			"&7Item&f: &e%item_name%",
			"&7From&f: &e%from_name%",
			"&7Reason&f:",
			"&e%payment_reason%",
			"",
			"&7Click here to claim this payment"
	);
	public static TranslationEntry GUI_TRANSACTIONS_TYPE_TITLE = create("gui.transactions type.title", "&7&LTransactions");
	public static TranslationEntry GUI_TRANSACTIONS_TYPE_ALL_NAME = create("gui.transactions type.items.all transactions.name", "&eAll Transactions");
	public static TranslationEntry GUI_TRANSACTIONS_TYPE_ALL_LORE = create("gui.transactions type.items.all transactions.lore", "&7Click to view all transactions");
	public static TranslationEntry GUI_TRANSACTIONS_TYPE_SELF_NAME = create("gui.transactions type.items.self transactions.name", "&eYour Transactions");
	public static TranslationEntry GUI_TRANSACTIONS_TYPE_SELF_LORE = create("gui.transactions type.items.self transactions.lore", "&7Click to view all your transactions");
	public static TranslationEntry GUI_TRANSACTIONS_TYPE_REQUEST_NAME = create("gui.transactions type.items.requests transactions.name", "&eCompleted Requests");
	public static TranslationEntry GUI_TRANSACTIONS_TYPE_REQUEST_LORE = create("gui.transactions type.items.requests transactions.lore", "&7Click to view completed requests");
	public static TranslationEntry GUI_TRANSACTIONS_TYPE_DELETE_NAME = create("gui.transactions type.items.delete transactions.name", "&cDelete Transactions");
	public static TranslationEntry GUI_TRANSACTIONS_TYPE_DELETE_LORE = create("gui.transactions type.items.delete transactions.lore",
			"&7Click to delete transactions older than a specified period",
			"&7Ex. 3 day will delete every single transaction older",
			"&7than 3 days from the current time.",
			"&7Valid time ranges:",
			"&esecond",
			"&eminute",
			"&ehour",
			"&eday",
			"&eweek",
			"&emonth",
			"&eyear"
	);
	public static TranslationEntry GUI_PRICE_LIMITS_TITLE = create("gui.price limits.title", "&7Auction House &f- &ePrice Limits");
	public static TranslationEntry GUI_PRICE_LIMITS_LORE = create("gui.price limits.lore",
			"&7&m-------------------------",
			"&7Minimum Price&f: &a%min_price%",
			"&7Maximum Price&f: &a%max_price%",
			"",
			"&7(&e!&7) &f- &BWhen setting the max price",
			"&byou can use -1 to disable it.",
			"",
			"&7Left-Click to change min price",
			"&7Right-Click to change max price",
			"&7Press Drop to delete"
	);
	public static TranslationEntry GUI_LOGS_TITLE = create("gui.admin logs.title", "&7&LAdmin Logs");
	public static TranslationEntry GUI_LOGS_LORE = create("gui.admin logs.lore",
			"&7Admin&F: &e%admin%",
			"&7Target&F: &e%target%",
			"&7Item ID&F: %item_id%",
			"&7Action&F: &e%admin_action%",
			"&7Date&F: &e%admin_log_date%"
	);
	public static TranslationEntry GUI_REQUEST_TRANSACTIONS_TITLE = create("gui.request transactions.title", "&7&LYour Completed Requests");
	public static TranslationEntry GUI_REQUEST_TRANSACTIONS_TITLE_ALL = create("gui.request transactions.title all", "&7&LAll Completed Requests");
	public static TranslationEntry GUI_REQUEST_TRANSACTIONS_TRANSACTION_LORE = create("gui.request transactions.items.transaction.lore",
			"&7ID&F: &e%transaction_id%",
			"",
			"&7Payment Offered&f: &a%transaction_price%",
			"&7Amount Requested&f: &e%transaction_amount%",
			"",
			"&7Requested By&F: &e%transaction_requester%",
			"&7Completed By&F: &e%transaction_completer%",
			"",
			"&7Fulfilled On&F:",
			"&b%transaction_date%",
			""
	);
	public static TranslationEntry GUI_REQUEST_TRANSACTIONS_FILTER_NAME = create("gui.request transactions.items.filter.name", "&e&lFilter Options");
	public static TranslationEntry GUI_REQUEST_TRANSACTIONS_FILTER_LORE = create("gui.request transactions.items.filter.lore",
			"&eSort Order&f: &7%filter_sort_order%",
			"",
			"&7Shift Right-Click to change sort order",
			"&7Press Drop to reset filters"
	);
	public static TranslationEntry GUI_REQUEST_TRANSACTIONS_ALL_NAME = create("gui.request transactions.items.all.name", "&e&lToggle View All");
	public static TranslationEntry GUI_REQUEST_TRANSACTIONS_ALL_LORE = create("gui.request transactions.items.all.lore", "&7Click to toggle view all");
	public static TranslationEntry GUI_TRANSACTIONS_TITLE = create("gui.transactions.title", "&7&LYour Transactions");
	public static TranslationEntry GUI_TRANSACTIONS_TITLE_ALL = create("gui.transactions.title all", "&7&LAll Transactions");
	public static TranslationEntry GUI_TRANSACTIONS_ITEM_NAME = create("gui.transactions.items.transaction.name", "&e%transaction_id%");
	public static TranslationEntry GUI_TRANSACTIONS_ITEM_LORE = create("gui.transactions.items.transaction.lore",
			"&7Seller&F: &e%seller%",
			"&7Buyer&F: &e%buyer%",
			"&7Item name&F: %item_name%",
			"&7Quantity&F: &e%item_quantity%",
			"&7Date&F: &e%date%",
			"",
			"&7Click to view more details"
	);
	public static TranslationEntry GUI_TRANSACTIONS_FILTER_NAME = create("gui.transactions.items.filter.name", "&e&lFilter Options");
	public static TranslationEntry GUI_TRANSACTIONS_FILTER_LORE = create("gui.transactions.items.filter.lore",
			"&eItem Category&f: &7%filter_category%",
			"&eAuction Type&f: &7%filter_auction_type%",
			"&eTransaction Type&f: &7%filter_buy_type%",
			"&eSort Order&f: &7%filter_sort_order%",
			"",
			"&7Left-Click to change item category",
			"&7Right-Click to change change auction type",
			"&7Shift Right-Click to change sort order",
			"&7Shift Left-Click to change buy type",
			"&7Press Drop to reset filters"
	);
	public static TranslationEntry GUI_TRANSACTION_VIEW_TITLE = create("gui.transaction view.title", "&7&LViewing Transaction");
	public static TranslationEntry GUI_TRANSACTION_VIEW_SELLER_NAME = create("gui.transaction view.items.seller.name", "&e%seller%");
	public static TranslationEntry GUI_TRANSACTION_VIEW_SELLER_LORE = create("gui.transaction view.items.seller.lore", "&7This is the player who sold the item.");
	public static TranslationEntry GUI_TRANSACTION_VIEW_BUYER_NAME = create("gui.transaction view.items.buyer.name", "&e%buyer%");
	public static TranslationEntry GUI_TRANSACTION_VIEW_BUYER_LORE = create("gui.transaction view.items.buyer.lore", "&7This is the player who bought the item.");
	public static TranslationEntry GUI_TRANSACTION_VIEW_INFO_NAME = create("gui.transaction view.items.information.name", "&e%transaction_id%");
	public static TranslationEntry GUI_TRANSACTION_VIEW_INFO_LORE = create("gui.transaction view.items.information.lore",
			"&7ID&f: &e%transaction_id%",
			"&7Item name&F: %item_name%",
			"&7Sale Type&f: &e%sale_type%",
			"&7Date&f: &e%transaction_date%",
			"&7Final Price&f: &e%final_price%"
	);
	public static TranslationEntry GUI_INSPECT_TITLE = create("gui.inspect.title", "&7&LInspecting Container");

	/*
	==============================================================
					GUI – bans, player selector, filter, filter whitelist
	==============================================================
	 */
	public static TranslationEntry GUI_ALL_BANS_TITLE = create("gui.all bans.title", "&7&LAuction House &f- &eAll Bans");
	public static TranslationEntry GUI_ALL_BANS_USER_NAME = create("gui.all bans.items.user.name", "&e%player_name%");
	public static TranslationEntry GUI_ALL_BANS_USER_LORE = create("gui.all bans.items.user.lore",
			"&7Banned By&f: &e%ban_banner%",
			"",
			"&7Banned On&f: &e%ban_date%",
			"&7Expires on&f: &e%ban_expiration%",
			"&7Permanent&F: %is_true%",
			"",
			"&7Ban Types",
			"&e%ban_type_list%",
			"",
			"&EClick &7to unban this user"
	);
	public static TranslationEntry GUI_BAN_TITLE = create("gui.ban.title", "&7&LAuction House &f- &eBan User");
	public static TranslationEntry GUI_BAN_PLAYER_NAME = create("gui.ban.items.player.name", "&e%player_name%");
	public static TranslationEntry GUI_BAN_PLAYER_LORE = create("gui.ban.items.player.lore", "&7This is the selected user to be banned.");
	public static TranslationEntry GUI_BAN_TYPES_NAME = create("gui.ban.items.types.name", "&eBan Types");
	public static TranslationEntry GUI_BAN_TYPES_LORE = create("gui.ban.items.types.lore",
			"&7The types of ban this player will receive",
			"",
			"&7Currently Selected Bans",
			"&e%ban_type_list%",
			"",
			"&EClick &7to adjust ban types"
	);
	public static TranslationEntry GUI_BAN_PERMA_NAME = create("gui.ban.items.permanent.name", "&ePermanent");
	public static TranslationEntry GUI_BAN_PERMA_LORE = create("gui.ban.items.permanent.lore",
			"&7Should this ban be permanent",
			"",
			"&7Permanent&f: &e%is_true%",
			"",
			"&EClick &7to toggle setting"
	);
	public static TranslationEntry GUI_BAN_REASON_NAME = create("gui.ban.items.reason.name", "&eBan Reason");
	public static TranslationEntry GUI_BAN_REASON_LORE = create("gui.ban.items.reason.lore",
			"&7The reason for the ban",
			"",
			"&7Ban Reason&F:",
			"&e%ban_reason%",
			"",
			"&EClick &7to change reason"
	);
	public static TranslationEntry GUI_BAN_EXPIRATION_NAME = create("gui.ban.items.expiration.name", "&eExpiration");
	public static TranslationEntry GUI_BAN_EXPIRATION_LORE = create("gui.ban.items.expiration.lore",
			"&7When should this ban be lifted?",
			"&7Will only matter if it's no permanent",
			"",
			"&7Expiration Date&F: &e%ban_time%",
			"",
			"&EClick &7to change ban length"
	);
	public static TranslationEntry GUI_BAN_CREATE_NAME = create("gui.ban.items.create.name", "&eBan User");
	public static TranslationEntry GUI_BAN_CREATE_LORE = create("gui.ban.items.create.lore", "&EClick &7to confirm ban.");
	public static TranslationEntry GUI_BAN_TYPES_TITLE = create("gui.ban types.title", "&7&LAuction House &f- &eBan Types");
	public static TranslationEntry GUI_BAN_TYPES_TYPE_NAME = create("gui.ban types.items.type.name", "&e%ban_type%");
	public static TranslationEntry GUI_BAN_TYPES_TYPE_LORE = create("gui.ban types.items.type.lore", "&EClick &7to toggle ban type");
	public static TranslationEntry GUI_PLAYER_SELECTOR_TITLE = create("gui.player selector.title", "&7&LAuction House &f- &eSelect Player");
	public static TranslationEntry GUI_PLAYER_SELECTOR_PLAYER_NAME = create("gui.player selector.items.player.name", "&e%player_name%");
	public static TranslationEntry GUI_PLAYER_SELECTOR_PLAYER_LORE = create("gui.player selector.items.player.lore", "&eClick &7To select player");
	public static TranslationEntry GUI_FILTER_TITLE = create("gui.filter.title", "&7Auction House - &eFilter Selection");
	public static TranslationEntry GUI_FILTER_ALL_NAME = create("gui.filter.items.all.name", "&e&lAll");
	public static TranslationEntry GUI_FILTER_ALL_LORE = create("gui.filter.items.all.lore", "&7Click to set the filter to&f: &eAll");
	public static TranslationEntry GUI_FILTER_OWN_NAME = create("gui.filter.items.own.name", "&e&lYour Listings");
	public static TranslationEntry GUI_FILTER_OWN_LORE = create("gui.filter.items.own.lore", "&7Click to set the filter to&f: &eYour Listings");
	public static TranslationEntry GUI_FILTER_SEARCH_NAME = create("gui.filter.items.search.name", "&e&lSearch");
	public static TranslationEntry GUI_FILTER_SEARCH_LORE = create("gui.filter.items.search.lore",
			"&7Click to set the filter to&f: &eSearch",
			"&7Current search phrase&f: &e%filter_search_phrase%"
	);
	public static TranslationEntry GUI_FILTER_MISC_NAME = create("gui.filter.items.misc.name", "&e&lMiscellaneous");
	public static TranslationEntry GUI_FILTER_MISC_LORE = create("gui.filter.items.misc.lore", "&7Click to set the filter to&f: &eMiscellaneous");
	public static TranslationEntry GUI_FILTER_POTIONS_NAME = create("gui.filter.items.potions.name", "&e&LPotions");
	public static TranslationEntry GUI_FILTER_POTIONS_LORE = create("gui.filter.items.potions.lore", "&7Click to set the filter to&f: &ePotions");
	public static TranslationEntry GUI_FILTER_ENCHANTS_NAME = create("gui.filter.items.enchants.name", "&e&lEnchantments");
	public static TranslationEntry GUI_FILTER_ENCHANTS_LORE = create("gui.filter.items.enchants.lore", "&7Click to set the filter to&f: &eEnchantments");
	public static TranslationEntry GUI_FILTER_ARMOR_NAME = create("gui.filter.items.armor.name", "&e&lArmor");
	public static TranslationEntry GUI_FILTER_ARMOR_LORE = create("gui.filter.items.armor.lore", "&7Click to set the filter to&f: &eArmor");
	public static TranslationEntry GUI_FILTER_WEAPONS_NAME = create("gui.filter.items.weapons.name", "&e&lWeapons");
	public static TranslationEntry GUI_FILTER_WEAPONS_LORE = create("gui.filter.items.weapons.lore", "&7Click to set the filter to&f: &eWeapons");
	public static TranslationEntry GUI_FILTER_TOOLS_NAME = create("gui.filter.items.tools.name", "&e&lTools");
	public static TranslationEntry GUI_FILTER_TOOLS_LORE = create("gui.filter.items.tools.lore", "&7Click to set the filter to&f: &eTools");
	public static TranslationEntry GUI_FILTER_SPAWNERS_NAME = create("gui.filter.items.spawners.name", "&e&LSpawners");
	public static TranslationEntry GUI_FILTER_SPAWNERS_LORE = create("gui.filter.items.spawners.lore", "&7Click to set the filter to&f: &eSpawners");
	public static TranslationEntry GUI_FILTER_BLOCKS_NAME = create("gui.filter.items.blocks.name", "&e&lBlocks");
	public static TranslationEntry GUI_FILTER_BLOCKS_LORE = create("gui.filter.items.blocks.lore", "&7Click to set the filter to&f: &eBlocks");
	public static TranslationEntry GUI_FILTER_WHITELIST_TITLE = create("gui.filter whitelist.title", "&7Auction Filter - &eWhitelist");
	public static TranslationEntry GUI_FILTER_WHITELIST_BLOCKS_NAME = create("gui.filter whitelist.items.blocks.name", "&e&lBlock Filters");
	public static TranslationEntry GUI_FILTER_WHITELIST_BLOCKS_LORE = create("gui.filter whitelist.items.blocks.lore", "&7Click to adjust the item whitelist for this filter");
	public static TranslationEntry GUI_FILTER_WHITELIST_FOOD_NAME = create("gui.filter whitelist.items.food.name", "&e&lFood Filters");
	public static TranslationEntry GUI_FILTER_WHITELIST_FOOD_LORE = create("gui.filter whitelist.items.food.lore", "&7Click to adjust the item whitelist for this filter");
	public static TranslationEntry GUI_FILTER_WHITELIST_ARMOR_NAME = create("gui.filter whitelist.items.armor.name", "&e&LArmor Filters");
	public static TranslationEntry GUI_FILTER_WHITELIST_ARMOR_LORE = create("gui.filter whitelist.items.armor.lore", "&7Click to adjust the item whitelist for this filter");
	public static TranslationEntry GUI_FILTER_WHITELIST_TOOLS_NAME = create("gui.filter whitelist.items.tools.name", "&e&lTool Filters");
	public static TranslationEntry GUI_FILTER_WHITELIST_TOOLS_LORE = create("gui.filter whitelist.items.tools.lore", "&7Click to adjust the item whitelist for this filter");
	public static TranslationEntry GUI_FILTER_WHITELIST_SPAWNERS_NAME = create("gui.filter whitelist.items.spawners.name", "&e&lSpawner Filters");
	public static TranslationEntry GUI_FILTER_WHITELIST_SPAWNERS_LORE = create("gui.filter whitelist.items.spawners.lore", "&7Click to adjust the item whitelist for this filter");
	public static TranslationEntry GUI_FILTER_WHITELIST_ENCHANTS_NAME = create("gui.filter whitelist.items.enchants.name", "&e&lEnchantment Filters");
	public static TranslationEntry GUI_FILTER_WHITELIST_ENCHANTS_LORE = create("gui.filter whitelist.items.enchants.lore", "&7Click to adjust the item whitelist for this filter");
	public static TranslationEntry GUI_FILTER_WHITELIST_WEAPONS_NAME = create("gui.filter whitelist.items.weapons.name", "&e&lWeapon Filters");
	public static TranslationEntry GUI_FILTER_WHITELIST_WEAPONS_LORE = create("gui.filter whitelist.items.weapons.lore", "&7Click to adjust the item whitelist for this filter");
	public static TranslationEntry GUI_FILTER_WHITELIST_POTIONS_NAME = create("gui.filter whitelist.items.potions.name", "&e&LPotions Filters");
	public static TranslationEntry GUI_FILTER_WHITELIST_POTIONS_LORE = create("gui.filter whitelist.items.potions.lore", "&7Click to adjust the item whitelist for this filter");
	public static TranslationEntry GUI_FILTER_WHITELIST_MISC_NAME = create("gui.filter whitelist.items.misc.name", "&e&lMiscellaneous Filters");
	public static TranslationEntry GUI_FILTER_WHITELIST_MISC_LORE = create("gui.filter whitelist.items.misc.lore", "&7Click to adjust the item whitelist for this filter");
	public static TranslationEntry GUI_FILTER_WHITELIST_LIST_TITLE = create("gui.filter whitelist list.title", "&7Filter Whitelist - &e%filter_category%");

	/*
	==============================================================
					GUI – sell listing type, place item, bin, request, auction, stats, admin, bidding, bundle
	==============================================================
	 */
	public static TranslationEntry GUI_SELL_LISTING_TYPE_TITLE = create("gui.sell listing type.title", "&7Auction House - &eSelect Listing Type");
	public static TranslationEntry GUI_SELL_LISTING_TYPE_BIN_NAME = create("gui.sell listing type.items.bin.name", "&e&lBin Item");
	public static TranslationEntry GUI_SELL_LISTING_TYPE_BIN_LORE = create("gui.sell listing type.items.bin.lore",
			"&7A Bin item is an item that does not accept any",
			"&7bids, it must be bought for the listed price.",
			"",
			"&7Click to list as &aBin Item"
	);
	public static TranslationEntry GUI_SELL_LISTING_TYPE_AUCTION_NAME = create("gui.sell listing type.items.auction.name", "&e&lAuction Item");
	public static TranslationEntry GUI_SELL_LISTING_TYPE_AUCTION_LORE = create("gui.sell listing type.items.auction.lore",
			"&7An Auction item is an item that can be bid",
			"&7on by multiple people, the highest bid wins.",
			"",
			"&7Click to list as an &aAuction"
	);
	public static TranslationEntry GUI_SELL_LISTING_TYPE_RETURN_NAME = create("gui.sell listing type.items.return.name", "&e&lAuction House");
	public static TranslationEntry GUI_SELL_LISTING_TYPE_RETURN_LORE = create("gui.sell listing type.items.return.lore", "", "&7Click to go to the &aAuction House");
	public static TranslationEntry GUI_SELL_PLACE_ITEM_TITLE = create("gui.sell place item.title", "&7Auction House - &ePlace Item");
	public static TranslationEntry GUI_SELL_PLACE_ITEM_BUNDLE_TITLE = create("gui.sell place item.bundle title", "&7Auction House - &ePlace Item(s)");
	public static TranslationEntry GUI_SELL_PLACE_ITEM_CONTINUE_NAME = create("gui.sell place item.items.continue.name", "&e&lContinue");
	public static TranslationEntry GUI_SELL_PLACE_ITEM_CONTINUE_LORE = create("gui.sell place item.items.continue.lore", "", "&7Click to continue to pricing");
	public static TranslationEntry GUI_SELL_PLACE_ITEM_BUNDLE_NAME = create("gui.sell place item.items.bundle.name", "&e&lListing Bundle");
	public static TranslationEntry GUI_SELL_PLACE_ITEM_BUNDLE_LORE = create("gui.sell place item.items.bundle.lore",
			"&7You are currently in the bundle",
			"&7listing view.",
			"",
			"&7Click to list a single item instead"
	);
	public static TranslationEntry GUI_SELL_PLACE_ITEM_SINGLE_NAME = create("gui.sell place item.items.single.name", "&e&lSingle Listing");
	public static TranslationEntry GUI_SELL_PLACE_ITEM_SINGLE_LORE = create("gui.sell place item.items.single.lore",
			"&7You are currently in the single",
			"&7item listing view.",
			"",
			"&7Click to list a bundle instead"
	);
	public static TranslationEntry GUI_SELL_BIN_TITLE = create("gui.sell bin item.title", "&7Auction House - &eBin Listing");
	public static TranslationEntry GUI_SELL_BIN_CONFIRM_NAME = create("gui.sell bin item.items.confirm.name", "&e&lList Item");
	public static TranslationEntry GUI_SELL_BIN_CONFIRM_LORE = create("gui.sell bin item.items.confirm.lore", "", "&7Click to list this item");
	public static TranslationEntry GUI_SELL_BIN_FEE_NAME = create("gui.sell bin item.items.fee.name", "&e&lListing Fee");
	public static TranslationEntry GUI_SELL_BIN_FEE_LORE = create("gui.sell bin item.items.fee.lore",
			"&cThere is a listing fee to list this item.",
			"",
			"&7Rate&f: &a%listing_fee%",
			"",
			"&eCost to list&f: %listing_fee_total%"
	);
	public static TranslationEntry GUI_SELL_BIN_TIME_NAME = create("gui.sell bin item.items.time.name", "&e&lListing Time");
	public static TranslationEntry GUI_SELL_BIN_TIME_LORE = create("gui.sell bin item.items.time.lore",
			"",
			"&eCurrent Time: &b%remaining_days%&fd &b%remaining_hours%&fh &b%remaining_minutes%&fm &b%remaining_seconds%&Fs",
			"",
			"&7Click to edit the listing time"
	);
	public static TranslationEntry GUI_SELL_BIN_PRICE_NAME = create("gui.sell bin item.items.price.name", "&e&lPrice");
	public static TranslationEntry GUI_SELL_BIN_PRICE_LORE = create("gui.sell bin item.items.price.lore",
			"",
			"&7The current price if &F: &a%listing_bin_price%",
			"",
			"&7Click to edit the listing price"
	);
	public static TranslationEntry GUI_SELL_BIN_PARTIAL_ENABLED_NAME = create("gui.sell bin item.items.partial enabled.name", "&e&lQuantity Purchase");
	public static TranslationEntry GUI_SELL_BIN_PARTIAL_ENABLED_LORE = create("gui.sell bin item.items.partial enabled.lore",
			"",
			"&7You have partial purchases &aenabled",
			"",
			"&7Click to &cdisable &7partial purchases"
	);
	public static TranslationEntry GUI_SELL_BIN_PARTIAL_DISABLED_NAME = create("gui.sell bin item.items.partial disabled.name", "&e&lQuantity Purchase");
	public static TranslationEntry GUI_SELL_BIN_PARTIAL_DISABLED_LORE = create("gui.sell bin item.items.partial disabled.lore",
			"",
			"&7You have partial purchases &cdisabled",
			"",
			"&7Click to &aenable &7partial purchases"
	);
	public static TranslationEntry GUI_REQUEST_TITLE = create("gui.request.title", "&7Auction House - &eRequest Item");
	public static TranslationEntry GUI_REQUEST_AMT_NAME = create("gui.request.items.amt.name", "&e&LRequest Amount");
	public static TranslationEntry GUI_REQUEST_AMT_LORE = create("gui.request.items.amt.lore",
			"&7How much of this item do you want?",
			"",
			"&eCurrent Amt: &e%request_amount%",
			"",
			"&7Click to edit request amount"
	);
	public static TranslationEntry GUI_REQUEST_PRICE_NAME = create("gui.request.items.price.name", "&e&LPrice");
	public static TranslationEntry GUI_REQUEST_PRICE_LORE = create("gui.request.items.price.lore",
			"&7How much are you going to pay for this?",
			"",
			"&eCurrent: &e%request_price%",
			"",
			"&7Click to edit request price"
	);
	public static TranslationEntry GUI_REQUEST_REQUEST_NAME = create("gui.request.items.request.name", "&e&lRequest Item(s)");
	public static TranslationEntry GUI_REQUEST_REQUEST_LORE = create("gui.request.items.request.lore", "", "&7Click to make this request");
	public static TranslationEntry GUI_SELL_AUCTION_TITLE = create("gui.sell auction item.title", "&7Auction House - &eAuction Listing");
	public static TranslationEntry GUI_SELL_AUCTION_TIME_NAME = create("gui.sell auction item.items.time.name", "&e&lListing Time");
	public static TranslationEntry GUI_SELL_AUCTION_TIME_LORE = create("gui.sell auction item.items.time.lore",
			"",
			"&eCurrent Time: &b%remaining_days%&fd &b%remaining_hours%&fh &b%remaining_minutes%&fm &b%remaining_seconds%&Fs",
			"",
			"&7Click to edit the listing time"
	);
	public static TranslationEntry GUI_SELL_AUCTION_FEE_NAME = create("gui.sell auction item.items.fee.name", "&e&lListing Fee");
	public static TranslationEntry GUI_SELL_AUCTION_FEE_LORE = create("gui.sell auction item.items.fee.lore",
			"&cThere is a listing fee to list this item.",
			"",
			"&7Rate&f: &a%listing_fee%",
			"",
			"&eCost to list&f: %listing_fee_total%"
	);
	public static TranslationEntry GUI_SELL_AUCTION_BIN_PRICE_NAME = create("gui.sell auction item.items.bin price.name", "&e&lBuyout Price");
	public static TranslationEntry GUI_SELL_AUCTION_BIN_PRICE_LORE = create("gui.sell auction item.items.bin price.lore",
			"",
			"&7The current buyout price is&F: &a%listing_bin_price%",
			"",
			"&7Click to edit the buyout price"
	);
	public static TranslationEntry GUI_SELL_AUCTION_STARTING_PRICE_NAME = create("gui.sell auction item.items.starting price.name", "&e&lStarting Price");
	public static TranslationEntry GUI_SELL_AUCTION_STARTING_PRICE_LORE = create("gui.sell auction item.items.starting price.lore",
			"",
			"&7The current starting price is&F: &a%listing_start_price%",
			"",
			"&7Click to edit the starting price"
	);
	public static TranslationEntry GUI_SELL_AUCTION_INCREMENT_PRICE_NAME = create("gui.sell auction item.items.increment price.name", "&e&lIncrement Price");
	public static TranslationEntry GUI_SELL_AUCTION_INCREMENT_PRICE_LORE = create("gui.sell auction item.items.increment price.lore",
			"",
			"&7The current increment price is&F: &a%listing_increment_price%",
			"",
			"&7Click to edit the increment price"
	);
	public static TranslationEntry GUI_SELL_AUCTION_BUYOUT_ENABLED_NAME = create("gui.sell auction item.items.buyout enabled.name", "&e&lAuction Buyout");
	public static TranslationEntry GUI_SELL_AUCTION_BUYOUT_ENABLED_LORE = create("gui.sell auction item.items.buyout enabled.lore",
			"",
			"&7You have buyout &aenabled",
			"",
			"&7Click to &cdisable &7auction buyout"
	);
	public static TranslationEntry GUI_SELL_AUCTION_BUYOUT_DISABLED_NAME = create("gui.sell auction item.items.buyout disabled.name", "&e&lAuction Buyout");
	public static TranslationEntry GUI_SELL_AUCTION_BUYOUT_DISABLED_LORE = create("gui.sell auction item.items.buyout disabled.lore",
			"",
			"&7You have buyout &cdisabled",
			"",
			"&7Click to &aenable &7auction buyout"
	);
	public static TranslationEntry GUI_SELL_AUCTION_CONFIRM_NAME = create("gui.sell auction item.items.confirm.name", "&e&lList Item");
	public static TranslationEntry GUI_SELL_AUCTION_CONFIRM_LORE = create("gui.sell auction item.items.confirm.lore", "", "&7Click to list this item");
	public static TranslationEntry GUI_STATS_VIEW_SELECT_TITLE = create("gui.stat view select.title", "&7Auction House - &eStatistics");
	public static TranslationEntry GUI_STATS_PERSONAL_NAME = create("gui.stat view select.items.personal.name", "&e&lPersonal Statistics");
	public static TranslationEntry GUI_STATS_PERSONAL_LORE = create("gui.stat view select.items.personal.lore", "&7Click to view your own stats");
	public static TranslationEntry GUI_STATS_LEADERBOARD_NAME = create("gui.stat view select.items.leaderboard.name", "&e&lLeaderboard");
	public static TranslationEntry GUI_STATS_LEADERBOARD_LORE = create("gui.stat view select.items.leaderboard.lore", "&7Click to view server leaderboard");
	public static TranslationEntry GUI_STATS_SELF_TITLE = create("gui.stat view self.items.title", "&7Auction House - &eYour Stats");
	public static TranslationEntry GUI_STATS_SELF_CREATED_AUCTION_NAME = create("gui.stat view self.items.created auction.name", "&e&lCreated Auctions");
	public static TranslationEntry GUI_STATS_SELF_CREATED_AUCTION_LORE = create("gui.stat view self.items.created auction.lore", "&7You created &e%created_auctions% &7auctions");
	public static TranslationEntry GUI_STATS_SELF_CREATED_BIN_NAME = create("gui.stat view self.items.created bin.name", "&e&lCreated Bins");
	public static TranslationEntry GUI_STATS_SELF_CREATED_BIN_LORE = create("gui.stat view self.items.created bin.lore", "&7You created &e%created_bins% &7bins");
	public static TranslationEntry GUI_STATS_SELF_SOLD_AUCTION_NAME = create("gui.stat view self.items.sold auction.name", "&e&LSold Auctions");
	public static TranslationEntry GUI_STATS_SELF_SOLD_AUCTION_LORE = create("gui.stat view self.items.sold auction.lore", "&7You sold &e%sold_auctions% &7auction(s)");
	public static TranslationEntry GUI_STATS_SELF_SOLD_BIN_NAME = create("gui.stat view self.items.sold bin.name", "&e&LSold Bins");
	public static TranslationEntry GUI_STATS_SELF_SOLD_BIN_LORE = create("gui.stat view self.items.sold bin.lore", "&7You sold &e%sold_bins% &7bin(s)");
	public static TranslationEntry GUI_STATS_SELF_MONEY_EARNED_NAME = create("gui.stat view self.items.money earned.name", "&e&LMoney Earned");
	public static TranslationEntry GUI_STATS_SELF_MONEY_EARNED_LORE = create("gui.stat view self.items.money earned.lore", "&7You earned &a%money_earned%");
	public static TranslationEntry GUI_STATS_SELF_MONEY_SPENT_NAME = create("gui.stat view self.items.money spent.name", "&e&LMoney Spent");
	public static TranslationEntry GUI_STATS_SELF_MONEY_SPENT_LORE = create("gui.stat view self.items.money spent.lore", "&7You spent &c%money_spent%");
	public static TranslationEntry GUI_STATS_LEADERBOARD_TITLE = create("gui.stat view leaderboard.items.title", "&7Auction House - &eStat Leaderboard");
	public static TranslationEntry GUI_STATS_LEADERBOARD_PLAYER_NAME = create("gui.stat view leaderboard.items.player.name", "&e&l%player_name%");
	public static TranslationEntry GUI_STATS_LEADERBOARD_PLAYER_LORE = create("gui.stat view leaderboard.items.player.lore",
			"",
			"&7Statistic&f: &e%auction_statistic_name%",
			"&7Value&f: &e%auction_statistic_value%",
			""
	);
	public static TranslationEntry GUI_STATS_LEADERBOARD_STAT_NAME = create("gui.stat view leaderboard.items.stat.name", "&e&lStatistic Type");
	public static TranslationEntry GUI_STATS_LEADERBOARD_STAT_LORE = create("gui.stat view leaderboard.items.stat.lore",
			"",
			"&7Selected&f: &e%statistic_name%",
			"&7Click to &achange &7viewed statistic",
			""
	);
	public static TranslationEntry GUI_STATS_SEARCH_TITLE = create("gui.stat view other.items.title", "&7Auction House - &e%player_name% Stats");
	public static TranslationEntry GUI_STATS_SEARCH_CREATED_AUCTION_NAME = create("gui.stat view other.items.created auction.name", "&e&lCreated Auctions");
	public static TranslationEntry GUI_STATS_SEARCH_CREATED_AUCTION_LORE = create("gui.stat view other.items.created auction.lore", "&7They created &e%created_auctions% &7auctions");
	public static TranslationEntry GUI_STATS_SEARCH_CREATED_BIN_NAME = create("gui.stat view other.items.created bin.name", "&e&lCreated Bins");
	public static TranslationEntry GUI_STATS_SEARCH_CREATED_BIN_LORE = create("gui.stat view other.items.created bin.lore", "&7They created &e%created_bins% &7bins");
	public static TranslationEntry GUI_STATS_SEARCH_SOLD_AUCTION_NAME = create("gui.stat view other.items.sold auction.name", "&e&LSold Auctions");
	public static TranslationEntry GUI_STATS_SEARCH_SOLD_AUCTION_LORE = create("gui.stat view other.items.sold auction.lore", "&7They sold &e%sold_auctions% &7auction(s)");
	public static TranslationEntry GUI_STATS_SEARCH_SOLD_BIN_NAME = create("gui.stat view other.items.sold bin.name", "&e&LSold Bins");
	public static TranslationEntry GUI_STATS_SEARCH_SOLD_BIN_LORE = create("gui.stat view other.items.sold bin.lore", "&7They &e%sold_bins% &7bin(s)");
	public static TranslationEntry GUI_STATS_SEARCH_MONEY_EARNED_NAME = create("gui.stat view other.items.money earned.name", "&e&LMoney Earned");
	public static TranslationEntry GUI_STATS_SEARCH_MONEY_EARNED_LORE = create("gui.stat view other.items.money earned.lore", "&7They earned &a%money_earned%");
	public static TranslationEntry GUI_STATS_SEARCH_MONEY_SPENT_NAME = create("gui.stat view other.items.money spent.name", "&e&LMoney Spent");
	public static TranslationEntry GUI_STATS_SEARCH_MONEY_SPENT_LORE = create("gui.stat view other.items.money spent.lore", "&7They spent &c%money_spent%");
	public static TranslationEntry GUI_EXPIRED_ITEMS_ADMIN_TITLE = create("gui.expired items admin.title", "&7Auction House - &eAdmin Expired");
	public static TranslationEntry GUI_EXPIRED_ITEMS_ADMIN_ITEM_LORE = create("gui.expired items admin.item lore", "&7Click to delete this item");
	public static TranslationEntry GUI_ITEM_ADMIN_TITLE = create("gui.item admin.title", "&7Auction House - &eAdmin Item");
	public static TranslationEntry GUI_ITEM_ADMIN_RETURN_NAME = create("gui.item admin.items.send to player.name", "&a&lReturn to player");
	public static TranslationEntry GUI_ITEM_ADMIN_RETURN_LORE = create("gui.item admin.items.send to player.lore", "&7Click to return this item to the seller");
	public static TranslationEntry GUI_ITEM_ADMIN_CLAIM_NAME = create("gui.item admin.items.claim item.name", "&a&lClaim Item");
	public static TranslationEntry GUI_ITEM_ADMIN_CLAIM_LORE = create("gui.item admin.items.claim item.lore", "&7Click to claim this item as yours");
	public static TranslationEntry GUI_ITEM_ADMIN_DELETE_NAME = create("gui.item admin.items.delete item.name", "&a&lDelete Item");
	public static TranslationEntry GUI_ITEM_ADMIN_DELETE_LORE = create("gui.item admin.items.delete item.lore", "&7Click to delete this item");
	public static TranslationEntry GUI_ITEM_ADMIN_COPY_NAME = create("gui.item admin.items.copy item.name", "&a&LCopy Item");
	public static TranslationEntry GUI_ITEM_ADMIN_COPY_LORE = create("gui.item admin.items.copy item.lore", "&7Click to copy this item");
	public static TranslationEntry GUI_BIDDING_TITLE = create("gui.bidding.title", "&7Auction House - &eBidding");
	public static TranslationEntry GUI_BIDDING_DEFAULT_NAME = create("gui.bidding.items.default amount.name", "&a&LDefault Amount");
	public static TranslationEntry GUI_BIDDING_DEFAULT_LORE = create("gui.bidding.items.default amount.lore", "&7Click to bid default amount");
	public static TranslationEntry GUI_BIDDING_CUSTOM_NAME = create("gui.bidding.items.custom amount.name", "&a&lCustom Amount");
	public static TranslationEntry GUI_BIDDING_CUSTOM_LORE = create("gui.bidding.items.custom amount.lore", "&7Click to bid a custom amount");
	public static TranslationEntry GUI_CREATE_BUNDLE_TITLE = create("gui.create bundle.title", "&7Auction House - &eBundle Items");
	public static TranslationEntry GUI_CREATE_BUNDLE_CONFIRM_NAME = create("gui.create bundle.items.confirm.name", "&a&LConfirm");
	public static TranslationEntry GUI_CREATE_BUNDLE_CONFIRM_LORE = create("gui.create bundle.items.confirm.lore", "&7Click to confirm listing");

	/*
	==============================================================
					Auction stack (listing lore) + Item bundle + Discord
	==============================================================
	 */
	public static TranslationEntry AUCTION_STACK_HEADER = create("auction stack.header", "&7&m-------------------------");
	public static TranslationEntry AUCTION_STACK_SELLER = create("auction stack.seller lines", "&eSeller&f: &b%seller%");
	public static TranslationEntry AUCTION_STACK_REQUESTER = create("auction stack.requester lines", "&eRequester&f: &b%requester%", "");
	public static TranslationEntry AUCTION_STACK_REQUEST_PRICE = create("auction stack.request price lines", "&eOffering: &a%request_price%");
	public static TranslationEntry AUCTION_STACK_REQUEST_COUNT = create("auction stack.request count lines", "&eRequest Amt: &a%request_amount%", "");
	public static TranslationEntry AUCTION_STACK_BUY_NOW = create("auction stack.buy now lines", "&eBuy Now: &a%buynowprice%");
	public static TranslationEntry AUCTION_STACK_CURRENT_PRICE = create("auction stack.current price lines", "&eCurrent Price: &a%currentprice%");
	public static TranslationEntry AUCTION_STACK_BID_INCREMENT = create("auction stack.bid increment lines", "&eBid Increment: &a%bidincrement%");
	public static TranslationEntry AUCTION_STACK_HIGHEST_BIDDER = create("auction stack.highest bidder lines", "&eHighest Bidder: &a%highestbidder%");
	public static TranslationEntry AUCTION_STACK_PRIORITY_LISTING = create("auction stack.priority listing lines", "<GRADIENT:F55C7A>&LPriority Listing</GRADIENT:F6BC66>");
	public static TranslationEntry AUCTION_STACK_WATCHED = create("auction stack.watched lines", "<GRADIENT:3A7BD5>&LWatched</GRADIENT:00D2FF>");
	public static TranslationEntry AUCTION_STACK_TIME_LEFT = create("auction stack.time left lines", "&eTime Left: &b%remaining_days%&fd &b%remaining_hours%&fh &b%remaining_minutes%&fm &b%remaining_seconds%s");
	public static TranslationEntry AUCTION_STACK_INFINITE = create("auction stack.infinite lines", "&eTime Left: &bNo Expiration");
	public static TranslationEntry AUCTION_STACK_CONTROLS_HEADER = create("auction stack.controls.header", "&7&m-------------------------");
	public static TranslationEntry AUCTION_STACK_CONTROLS_FOOTER = create("auction stack.controls.footer", "&7&m-------------------------");
	public static TranslationEntry AUCTION_STACK_CONTROLS_INSPECTION = create("auction stack.controls.inspection", "&eShift Right-Click to inspect");
	public static TranslationEntry AUCTION_STACK_CONTROLS_ACCEPT_BID = create("auction stack.controls.accept bid", "&eRight-Click to accept the current bid");
	public static TranslationEntry AUCTION_STACK_CONTROLS_PRIORITY_LISTING = create("auction stack.controls.priority listing", "&eShift + Left-Click to prioritize listing");
	public static TranslationEntry AUCTION_STACK_CONTROLS_CANCEL_ITEM = create("auction stack.controls.cancel item", "&eLeft-Click to cancel this listing");
	public static TranslationEntry AUCTION_STACK_CONTROLS_CANCEL_REQUEST = create("auction stack.controls.cancel request", "&eLeft-Click to cancel this request");
	public static TranslationEntry AUCTION_STACK_CONTROLS_PREVIEW_ITEM = create("auction stack.controls.preview item", "&ePreviewing Listing");
	public static TranslationEntry AUCTION_STACK_CONTROLS_CART = create("auction stack.controls.cart", "&eClick to remove from cart");
	public static TranslationEntry AUCTION_STACK_CONTROLS_HIGHEST_BIDDER = create("auction stack.controls.highest bidder", "&eCurrently Winning!");
	public static TranslationEntry AUCTION_STACK_CONTROLS_BID_ON = create("auction stack.controls.using bid", "&eLeft-Click&f: &bBid", "&eRight-Click&f: &bBuy Now");
	public static TranslationEntry AUCTION_STACK_CONTROLS_BID_ON_NO_BUY_NOW = create("auction stack.controls.using bid without buy now", "&eLeft-Click&f: &bBid");
	public static TranslationEntry AUCTION_STACK_CONTROLS_FULFILL_REQUEST = create("auction stack.controls.fulfill request", "&eLeft-Click&f: &bFulfill Request");
	public static TranslationEntry AUCTION_STACK_CONTROLS_BID_OFF = create("auction stack.controls.not using bid", "&eLeft-Click&f: &bBuy Now");
	public static TranslationEntry AUCTION_STACK_CONTROLS_ADD_TO_CART = create("auction stack.controls.add to cart", "&eRight-Click&f: &bAdd to cart");
	public static TranslationEntry AUCTION_STACK_CONTROLS_PARTIAL_BUY = create("auction stack.controls.partial buy", "&eShift Left-Click&f: &bBuy Quantity");
	public static TranslationEntry AUCTION_STACK_CONTROLS_WATCHLIST = create("auction stack.controls.watchlist", "&eShift Right-Click&f: &bTo Watch/Unwatch");
	public static TranslationEntry AUCTION_STACK_AUCTION_CLOSED = create("auction stack.controls.auction house closed",
			"&cAuction House is Closed",
			"&eReopens in: &b%hours%&fh &b%minutes%&fm &b%seconds%&fs"
	);

	public static TranslationEntry ITEM_BUNDLE_NAME = create("auction setting.bundles.name", "%item_name% &7Bundle");
	public static TranslationEntry ITEM_BUNDLE_LORE = create("auction setting.bundles.lore",
			"&7This is a bundle item, it contains",
			"&7multiple items that can be unboxed."
	);

	public static TranslationEntry DISCORD_TITLE_NEW_AUCTION_LISTING = create("discord.titles.new auction listing", "New Auction Listing");
	public static TranslationEntry DISCORD_TITLE_NEW_BIN_LISTING = create("discord.titles.new bin listing", "New Bin Listing");
	public static TranslationEntry DISCORD_TITLE_NEW_BID = create("discord.titles.new bid", "New Bid");
	public static TranslationEntry DISCORD_TITLE_BIN_LISTING_BOUGHT = create("discord.titles.bin listing bought", "Listing Bought");
	public static TranslationEntry DISCORD_TITLE_AUCTION_LISTING_WON = create("discord.titles.auction listing won", "Auction Won");
	public static TranslationEntry DISCORD_FIELD_SELLER_NAME = create("discord.field.seller.name", "Seller");
	public static TranslationEntry DISCORD_FIELD_SELLER_VALUE = create("discord.field.seller.value", "%seller%");
	public static TranslationEntry DISCORD_FIELD_ITEM_NAME = create("discord.field.item.name", "Item");
	public static TranslationEntry DISCORD_FIELD_ITEM_VALUE = create("discord.field.item.value", "%item_name%");
	public static TranslationEntry DISCORD_FIELD_ITEM_LORE_NAME = create("discord.field.item lore.name", "Lore");
	public static TranslationEntry DISCORD_FIELD_BIN_LISTING_PRICE_NAME = create("discord.field.bin listing price.name", "Price");
	public static TranslationEntry DISCORD_FIELD_BIN_LISTING_PRICE_VALUE = create("discord.field.bin listing price.value", "%item_price%");
	public static TranslationEntry DISCORD_FIELD_AUCTION_BUYOUT_PRICE_NAME = create("discord.field.auction buyout price.name", "Buy Now Price");
	public static TranslationEntry DISCORD_FIELD_AUCTION_BUYOUT_PRICE_VALUE = create("discord.field.auction buyout price.value", "%buy_now_price%");
	public static TranslationEntry DISCORD_FIELD_AUCTION_START_PRICE_NAME = create("discord.field.auction start price.name", "Starting Price");
	public static TranslationEntry DISCORD_FIELD_AUCTION_START_PRICE_VALUE = create("discord.field.auction start price.value", "%starting_price%");
	public static TranslationEntry DISCORD_FIELD_BIN_BOUGHT_NAME = create("discord.field.bin listing bought.name", "Buyer");
	public static TranslationEntry DISCORD_FIELD_BIN_BOUGHT_VALUE = create("discord.field.bin listing bought.value", "%buyer%");
	public static TranslationEntry DISCORD_FIELD_AUCTION_WON_NAME = create("discord.field.auction listing won price.name", "Final Price");
	public static TranslationEntry DISCORD_FIELD_AUCTION_WON_VALUE = create("discord.field.auction listing won price.value", "%final_price%");
	public static TranslationEntry DISCORD_FIELD_AUCTION_WINNER_NAME = create("discord.field.auction winner.name", "Winner");
	public static TranslationEntry DISCORD_FIELD_AUCTION_WINNER_VALUE = create("discord.field.auction winner.value", "%winner%");
	public static TranslationEntry DISCORD_FIELD_AUCTION_BIDDER_NAME = create("discord.field.auction bidder.name", "Bidder");
	public static TranslationEntry DISCORD_FIELD_AUCTION_BIDDER_VALUE = create("discord.field.auction bidder.value", "%bidder%");
	public static TranslationEntry DISCORD_FIELD_BID_AMT_NAME = create("discord.field.bid amount.name", "Bid Amount");
	public static TranslationEntry DISCORD_FIELD_BID_AMT_VALUE = create("discord.field.bid amount.value", "%bid_amount%");
	public static TranslationEntry DISCORD_FIELD_CURRENT_PRICE_NAME = create("discord.field.current auction price.name", "Current Price");
	public static TranslationEntry DISCORD_FIELD_CURRENT_PRICE_VALUE = create("discord.field.current auction price.value", "%current_price%");

	public static void init() {
		new Translations(AuctionHouse.getInstance()).setup(AuctionHouse.getInstance());
	}
}
