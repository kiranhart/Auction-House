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

import ca.tweetzy.flight.config.ConfigEntry;
import ca.tweetzy.flight.settings.FlightSettings;
import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.comp.enums.CompSound;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * The current file has been created by Kiran Hart
 * Date Created: January 12 2021
 * Time Created: 6:36 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public final class Settings extends FlightSettings {

	public static final ConfigEntry LANG = create("lang", "en_US", "Default language file");
	public static final ConfigEntry HIDE_THANKYOU = create("hide thank you", false, "Hides the purchase thank you message in the console.");

	public static final ConfigEntry CURRENCY_ALLOW_PICK = create("economy.currency.allow pick", true, "If true, players will be able to select which currency they want to use.");
	public static final ConfigEntry CURRENCY_ALLOW_CUSTOM = create("economy.currency.allow custom item", true, "If true, players will be able to provide a custom item as the currency");
	public static final ConfigEntry CURRENCY_LIMIT_TO_PERMISSION = create("economy.currency.limit to permission", false, "If true, currencies will be limited by permission. Example auctionhouse.currency.ultraeconomy_gems will allow usage of the gems currency from ultra economy");
	public static final ConfigEntry CURRENCY_DEFAULT_SELECTED = create("economy.currency.default selection", "Vault/Vault", "The default currency selection, PluginName/CurrencyName -> Ex. Vault/Vault or UltraEconomy/Gems etc");
	public static final ConfigEntry CURRENCY_VAULT_SYMBOL = create("economy.currency.vault symbol", "$", "When using default/vault currency, what symbol should be used.");
	public static final ConfigEntry CURRENCY_VAULT_SYMBOL_OVERRIDES = create("economy.currency.vault symbol overrides", false, "If true, the vault symbol will override the symbol provided by the country/language combination");
	// Optional: economy.currency.use provider symbol — intentionally not exposed (legacy comment in original tweety Settings)
	public static final ConfigEntry CURRENCY_BLACKLISTED = create("economy.currency.black listed", Collections.singletonList("UltraEconomy:Test"), "A list of owning plugins & the currency to be blacklisted. Ex. UltraEconomy:Test");
	public static final ConfigEntry CURRENCY_FORMAT_LANGUAGE = create("economy.currency.format.language", "en", "An ISO 639 alpha-2 or alpha-3 language code.");
	public static final ConfigEntry CURRENCY_FORMAT_COUNTRY = create("economy.currency.format.country", "US", "An ISO 3166 alpha-2 country code or a UN M.49 numeric-3 area code.");
	public static final ConfigEntry CURRENCY_ABBREVIATE_NUMBERS = create("economy.currency.abbreviate numbers", false, "Should numbers be abbreviated?. Example: 123,000 will become 123k ");
	public static final ConfigEntry CURRENCY_HIDE_VAULT_SYMBOL = create("economy.currency.hide vault symbol", false, "Should the specified vault symbol be hidden?");
	public static final ConfigEntry CURRENCY_STRIP_ENDING_ZEROES = create("economy.currency.strip ending zeroes", false, "If the number ends with 00, should it be stripped. EX 123.00 becomes 123");
	public static final ConfigEntry CURRENCY_TIGHT_CURRENCY_SYMBOL = create("economy.currency.tight currency symbol", false, "If true, the space between the currency symbol and number will be removed");
	public static final ConfigEntry CURRENCY_USE_GROUPING = create("economy.currency.use grouping", true, "If false, number grouping will be disabled. Ex. 123,456.78 becomes 123456.78");
	public static final ConfigEntry CURRENCY_REMOVE_SPACE_FROM_CUSTOM = create("economy.currency.hide space between in currency", false, "If true, if the currency has a custom display name is will go from 123 currency to 123currency/symbol");

	public static final ConfigEntry CMD_ALIAS_MAIN = create("command aliases.main", Arrays.asList("ah", "auctions", "auctionhouses", "ahgui", "auctiongui"), "Command aliases for the main command");
	public static final ConfigEntry CMD_ALIAS_SUB_ACTIVE = create("command aliases.subcommands.active", Collections.singletonList("active"), "Command aliases for the active command");
	public static final ConfigEntry CMD_ALIAS_SUB_CART = create("command aliases.subcommands.cart", Collections.singletonList("cart"), "Command aliases for the cart command");
	public static final ConfigEntry CMD_ALIAS_SUB_ADMIN = create("command aliases.subcommands.admin", Collections.singletonList("admin"), "Command aliases for the admin command");
	public static final ConfigEntry CMD_ALIAS_SUB_BAN = create("command aliases.subcommands.ban", Collections.singletonList("ban"), "Command aliases for the ban command");
	public static final ConfigEntry CMD_ALIAS_SUB_BIDS = create("command aliases.subcommands.bids", Collections.singletonList("bids"), "Command aliases for the bids command");
	public static final ConfigEntry CMD_ALIAS_SUB_CONFIRM = create("command aliases.subcommands.confirm", Collections.singletonList("confirm"), "Command aliases for the confirm command");
	public static final ConfigEntry CMD_ALIAS_SUB_EXPIRED = create("command aliases.subcommands.expired", Collections.singletonList("expired"), "Command aliases for the expired command");
	public static final ConfigEntry CMD_ALIAS_SUB_FILTER = create("command aliases.subcommands.filter", Collections.singletonList("filter"), "Command aliases for the filter command");
	public static final ConfigEntry CMD_ALIAS_SUB_MARKCHEST = create("command aliases.subcommands.markchest", Collections.singletonList("markchest"), "Command aliases for the markchest command");
	public static final ConfigEntry CMD_ALIAS_SUB_PRICE_LIMIT = create("command aliases.subcommands.price limit", Collections.singletonList("pricelimit"), "Command aliases for the price limits command, formally min prices");
	public static final ConfigEntry CMD_ALIAS_SUB_PAYMENTS = create("command aliases.subcommands.payments", Collections.singletonList("payments"), "Command aliases for the payments command");
	public static final ConfigEntry CMD_ALIAS_SUB_REQUEST = create("command aliases.subcommands.request", Collections.singletonList("request"), "Command aliases for the request command");
	public static final ConfigEntry CMD_ALIAS_SUB_SEARCH = create("command aliases.subcommands.search", Collections.singletonList("search"), "Command aliases for the search command");
	public static final ConfigEntry CMD_ALIAS_SUB_SELL = create("command aliases.subcommands.sell", Collections.singletonList("sell"), "Command aliases for the sell command");
	public static final ConfigEntry CMD_ALIAS_SUB_STATS = create("command aliases.subcommands.stats", Collections.singletonList("stats"), "Command aliases for the stats command");
	public static final ConfigEntry CMD_ALIAS_SUB_TOGGLELISTINFO = create("command aliases.subcommands.togglelistinfo", Collections.singletonList("togglelistinfo"), "Command aliases for the toggle list info command");
	public static final ConfigEntry CMD_ALIAS_SUB_TRANSACTIONS = create("command aliases.subcommands.transactions", Collections.singletonList("transactions"), "Command aliases for the transactions command");
	public static final ConfigEntry CMD_ALIAS_SUB_UNBAN = create("command aliases.subcommands.unban", Collections.singletonList("unban"), "Command aliases for the unban command");
	public static final ConfigEntry CMD_ALIAS_SUB_WATCHLIST = create("command aliases.subcommands.watchlist", Collections.singletonList("watchlist"), "Command aliases for the watchlist command");



	public static final ConfigEntry CMD_FLAG_ALIAS_SELL_BUNDLE = create("command flags.sell command.bundle", Arrays.asList("-b", "-bundle"), "Aliases for the bundle command flag in the sell command");
	public static final ConfigEntry CMD_FLAG_ALIAS_SELL_PARTIAL_BUY = create("command flags.sell command.partial buy", Arrays.asList("-p", "-partialbuy"), "Aliases for the partial buy command flag in the sell command");
	public static final ConfigEntry CMD_FLAG_ALIAS_SELL_STACK_PRICE = create("command flags.sell command.stack price", Arrays.asList("-s", "-stack"), "Aliases for the stack price flag in the sell command");
	public static final ConfigEntry CMD_FLAG_ALIAS_SELL_INFINITE = create("command flags.sell command.infinite", Arrays.asList("-i", "-infinite"), "Aliases for the infinite flag in the sell command");
	public static final ConfigEntry CMD_FLAG_ALIAS_SELL_SERVER = create("command flags.sell command.server", Arrays.asList("-server"), "Aliases for the server flag in the sell command");
	public static final ConfigEntry CMD_FLAG_ALIAS_SELL_TIME = create("command flags.sell command.time", Arrays.asList("-t"), "Aliases for the time flag in the sell command");
	public static final ConfigEntry CMD_FLAG_ALIAS_SELL_SINGLE = create("command flags.sell command.single", Arrays.asList("-one"), "Aliases for the single item flag in the sell command");

	public static final ConfigEntry TIME_ALIAS_YEAR = create("time aliases.year", Arrays.asList("y", "year", "years"), "Time aliases for year, Must be in lowercase.");
	public static final ConfigEntry TIME_ALIAS_MONTH = create("time aliases.month", Arrays.asList("mo", "month", "months"), "Time aliases for month, Must be in lowercase.");
	public static final ConfigEntry TIME_ALIAS_WEEK = create("time aliases.week", Arrays.asList("w", "week", "weeks"), "Time aliases for week, Must be in lowercase.");
	public static final ConfigEntry TIME_ALIAS_DAY = create("time aliases.day", Arrays.asList("d", "day", "days"), "Time aliases for day, Must be in lowercase.");
	public static final ConfigEntry TIME_ALIAS_HOUR = create("time aliases.hour", Arrays.asList("h", "hour", "hours"), "Time aliases for hour, Must be in lowercase.");
	public static final ConfigEntry TIME_ALIAS_MINUTE = create("time aliases.minute", Arrays.asList("min", "minute", "minutes"), "Time aliases for minute, Must be in lowercase.");
	public static final ConfigEntry TIME_ALIAS_SECOND = create("time aliases.second", Arrays.asList("s", "second", "seconds"), "Time aliases for second, Must be in lowercase.");

	public static final ConfigEntry ALLOW_USAGE_OF_IN_GAME_EDITOR = create("Allow Usage Of This Menu In Game", true, "Once you set this to true, you will no longer be able to access it unless you enable it within the actual config.yml");
	public static final ConfigEntry UPDATE_CHECKER = create("update checker", true, "If true, auction house will check for updates periodically");

	public static final ConfigEntry DATE_FORMAT = create("auction setting.date format", "MMM dd, yyyy hh:mm aa", "You can learn more about date formats by googling SimpleDateFormat patterns or visiting this link", "https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html");
	public static final ConfigEntry TIMEZONE = create("auction setting.timezone", "America/Toronto", "Ensure this is correct as features like the access hours will use this timezone. https://timezonedb.com/time-zones");
	public static final ConfigEntry PACKET_NAMESPACE_KEYS = create("auction setting.packet.namespaced keys", Arrays.asList("ecoitems", "ecoarmor"), "Namespaced keys of plugins using packet lore");

	public static final ConfigEntry CART_SYSTEM_ENABLED = create("auction setting.cart system.enabled", false, "Should auction house allow the cart system?");
	public static final ConfigEntry WATCHLIST_ENABLED = create("auction setting.watchlist.enabled", true, "Should players be able to watch/save listings and view them in a watchlist?");
	public static final ConfigEntry WATCHLIST_MAX_LISTINGS = create("auction setting.watchlist.max listings per player", 30, "Maximum number of listings a player can have on their watchlist.");
	public static final ConfigEntry CLICKS_ADD_TO_WATCHLIST = create("auction setting.clicks.add to watchlist", "SHIFT_RIGHT", "Click type to add/remove a listing from your watchlist. Valid: LEFT, RIGHT, SHIFT_LEFT, SHIFT_RIGHT");
	public static final ConfigEntry USE_NAMES_FOR_CHECKS = create("auction setting.experimental.use names for checks", false, "Do not touch this unless you have a good reason too?");


	/*  ===============================
	 *          BASIC SETTINGS
	 *  ===============================*/

	//	Listing Priority
	public static final ConfigEntry LISTING_PRIORITY_ENABLED = create("auction setting.listing priority.enabled", true, "If true, players will be able to pay to prioritize listings");
	public static final ConfigEntry LISTING_PRIORITY_TIME_PER_BOOST = create("auction setting.listing priority.time per boost", 60 * 30, "How many seconds should the priority last for each time they pay", "By default users will be able to stack boosts");
	public static final ConfigEntry LISTING_PRIORITY_TIME_ALLOW_MULTI_BOOST = create("auction setting.listing priority.allow multiple boost", false, "If true players can boost an item multiple times before it runs out. (ex. if they have a boost active they can extend by paying before it expires)");
	public static final ConfigEntry LISTING_PRIORITY_TIME_COST_PER_BOOST = create("auction setting.listing priority.cost per boost", 1000, "How much should it cost the player to boost their item each time");

	// Timed Usage
	public static final ConfigEntry TIMED_USAGE_ENABLED = create("auction setting.access hours.use access hours", false, "If true, the auction house will be only accessible");
	public static final ConfigEntry TIMED_USAGE_RANGE = create("auction setting.access hours.access hours", Collections.singletonList(
			"00:00:00-23:59:59"
	), "The hours in 24hr format which the auction house can be used.", "The times use the specified timezone ");


	public static final ConfigEntry SHOW_LISTING_ERROR_IN_CONSOLE = create("auction setting.show listing error in console", false, "If true, an exception will be thrown and shown in the console if something goes wrong during item listing");
	public static final ConfigEntry STORE_PAYMENTS_FOR_MANUAL_COLLECTION = create("auction setting.store payments for manual collection", false, "If true, auction house will store the payments to be manually collected rather than automatically given to the player");
	public static final ConfigEntry MANUAL_PAYMENTS_ONLY_FOR_OFFLINE_USERS = create("auction setting.use stored payments for offline only", false, "If true, the usage of the manual payment collection will only be done if the user is offline");
	public static final ConfigEntry ALLOW_REPEAT_BIDS = create("auction setting.allow repeated bids", true, "If true, the highest bidder on an item can keep placing bids to raise their initial bid.");
	public static final ConfigEntry COLLECTION_BIN_ITEM_LIMIT = create("auction setting.collection bin item limit", 45, "How many items can be stored in the collection bin. If this is reached the player cannot list anymore items, regardless of active listings");
	public static final ConfigEntry SELL_MENU_SKIPS_TYPE_SELECTION = create("auction setting.skip type selection for sell menu", false, "If true the sell menu process will skip asking for the listing type depending on your auction settings (ie. bin only or auction only)");
	public static final ConfigEntry EXPIRE_MENU_REQUIRES_CONFIRM = create("auction setting.collection bin needs confirm", true, "If true the player must confirm they want to remove their item from the collection bin");

	public static final ConfigEntry BUNDLE_LIST_LIMIT = create("auction setting.bundle listing limit.listing limit", 45, "How many bundled listings can a player sell at any given time");
	public static final ConfigEntry BUNDLE_LIST_LIMIT_INCLUDE_COLLECTION_BIN = create("auction setting.bundle listing limit.include collection bin", false, "If true, collection bin bundles will also count towards this limit");

	public static final ConfigEntry DEFAULT_BIN_LISTING_TIME = create("auction setting.listings times.bin item", 86400, "The default listing time for bin items (buy only items) before they expire");
	public static final ConfigEntry DEFAULT_AUCTION_LISTING_TIME = create("auction setting.listings times.auction item", 604800, "The default listing time for auction items before they expire");

	public static final ConfigEntry DEFAULT_FILTER_CATEGORY = create("auction setting.default filters.auction category", "ALL", "Valid Options: ALL, FOOD, ARMOR, BLOCKS, TOOLS, WEAPONS, POTIONS, SPAWNERS, ENCHANTS, MISC, SEARCH, SELF");
	public static final ConfigEntry DEFAULT_FILTER_SORT = create("auction setting.default filters.auction sort", "RECENT", "Valid Options: RECENT, OLDEST, PRICE");
	public static final ConfigEntry DEFAULT_FILTER_SALE_TYPE = create("auction setting.default filters.sale type", "BOTH", "Valid Options: USED_BIDDING_SYSTEM, WITHOUT_BIDDING_SYSTEM, BOTH");
	public static final ConfigEntry ENABLE_FILTER_SYSTEM = create("auction setting.use filter system", true, "If false, auction house will disable the filter button.");

	public static final ConfigEntry FILTER_CLICKS_SORT_PRICE_RECENT_ENABLED = create("auction setting.default filters.enabled clicks.sort by price or recent", true, "If false, the click action for this filter option will not work.");
	public static final ConfigEntry FILTER_CLICKS_RESET_ENABLED = create("auction setting.default filters.enabled clicks.reset", true, "If false, the click action for this filter option will not work.");
	public static final ConfigEntry FILTER_CLICKS_LISTING_CURRENCY_ENABLED = create("auction setting.default filters.enabled clicks.listing currency", true, "If false, the click action for this filter option will not work.");
	public static final ConfigEntry FILTER_CLICKS_SALE_TYPE_ENABLED = create("auction setting.default filters.enabled clicks.sort sale type", true, "If false, the click action for this filter option will not work.");
	public static final ConfigEntry FILTER_CLICKS_CHANGE_CATEGORY_ENABLED = create("auction setting.default filters.enabled clicks.change category", true, "If false, the click action for this filter option will not work.");
	public static final ConfigEntry FILTER_CLICKS_TRANSACTION_BUY_TYPE_ENABLED = create("auction setting.default filters.enabled clicks.transaction buy type", true, "If false, the click action for this filter option will not work.");
	public static final ConfigEntry FILTER_DONT_REMEMBER = create("auction setting.default filters.do not save filter setting", false, "If true if you close the auction house your filter options will reset back to default");

	public static final ConfigEntry INTERNAL_CREATE_DELAY = create("auction setting.internal create delay", 2, "How many ticks should auction house wait before actually creating the item.");
	public static final ConfigEntry MAX_AUCTION_PRICE = create("auction setting.pricing.max auction price", 1000000000, "The max price for buy only / buy now items");
	public static final ConfigEntry MAX_AUCTION_START_PRICE = create("auction setting.pricing.max auction start price", 1000000000, "The max price starting a bidding auction");
	public static final ConfigEntry MAX_AUCTION_INCREMENT_PRICE = create("auction setting.pricing.max auction increment price", 1000000000, "The max amount for incrementing a bid.");
	public static final ConfigEntry MIN_AUCTION_PRICE = create("auction setting.pricing.min auction price", 1, "The min price for buy only / buy now items");
	public static final ConfigEntry MIN_AUCTION_START_PRICE = create("auction setting.pricing.min auction start price", 1, "The min price starting a bidding auction");
	public static final ConfigEntry MIN_AUCTION_INCREMENT_PRICE = create("auction setting.pricing.min auction increment price", 1, "The min amount for incrementing a bid.");
	public static final ConfigEntry OWNER_CAN_PURCHASE_OWN_ITEM = create("auction setting.purchase.owner can purchase own item", false, "Should the owner of an auction be able to purchase it?", "This probably should be set to false...");
	public static final ConfigEntry OWNER_CAN_BID_OWN_ITEM = create("auction setting.purchase.owner can bid on own item", false, "Should the owner of an auction be able to bid on it?", "This probably should be set to false...");
	public static final ConfigEntry OWNER_CAN_FULFILL_OWN_REQUEST = create("auction setting.purchase.owner can fulfill own request", false, "Should the owner of a request be able to fulfill it", "This probably should be set to false...");
	public static final ConfigEntry MAX_REQUEST_AMOUNT = create("auction setting.max request amount", 64, "How much of an item should a player be able to ask for in a single request?");
	public static final ConfigEntry BLOCK_REQUEST_USING_FILLED_SHULKER = create("auction setting.block requests using filled shulkers", true, "If false, players can request make a request using a shulker that contains items");
	public static final ConfigEntry MIN_REQUEST_PRICE = create("auction setting.pricing.min request price", 1, "The minimum price for a request");
	public static final ConfigEntry MAX_REQUEST_PRICE = create("auction setting.pricing.max request price", 1000000000, "The maximum price for a request");


	public static final ConfigEntry AUTO_REFRESH_AUCTION_PAGES = create("auction setting.auto refresh auction pages", true, "Should auction pages auto refresh?");
	public static final ConfigEntry AUTO_REFRESH_ACTIVE_AUCTION_PAGES = create("auction setting.auto refresh active auction pages", false, "Should the /ah active pages be auto refreshed?");
	public static final ConfigEntry INCREASE_TIME_ON_BID = create("auction setting.increase time on bid", true, "Should the remaining time be increased when a bid is placed?");
	public static final ConfigEntry TIME_TO_INCREASE_BY_ON_BID = create("auction setting.time to increase by on the bid", 20, "How many seconds should be added to the remaining time?");
	public static final ConfigEntry ALLOW_SALE_OF_DAMAGED_ITEMS = create("auction setting.allow sale of damaged items", true, "If true, player's can sell items that are damaged (not max durability)");
	public static final ConfigEntry ALLOW_FLOODGATE_PLAYERS = create("auction setting.allow flood gate players", false, "If true, player's who connected using floodgate (bedrock players) won't be able to use the auction house");
	public static final ConfigEntry RESTRICT_ALL_TRANSACTIONS_TO_PERM = create("auction setting.restrict viewing all transactions", false, "If true, player's will need the perm: auctionhouse.transactions.viewall to view all transactions");
	public static final ConfigEntry BLOCKED_WORLDS = create("auction setting.blocked worlds", Collections.singletonList("creative"), "A list of worlds that Auction House will be disabled in");
	public static final ConfigEntry PREVENT_SALE_OF_REPAIRED_ITEMS = create("auction setting.prevent sale of repaired items", false, "Items repaired before this setting is turned on will still be able to be listed.");
	public static final ConfigEntry ITEM_COPY_REQUIRES_GMC = create("auction setting.admin copy requires creative", false, "If true when using the admin copy option the player must be in creative");
	public static final ConfigEntry LOG_ADMIN_ACTIONS = create("auction setting.log admin actions", true, "If true, any admin actions made will be logged");
	public static final ConfigEntry ROUND_ALL_PRICES = create("auction setting.round all prices", false, "If true, any decimal numbers will be rounded to the nearest whole number");
	public static final ConfigEntry DISABLE_AUTO_SAVE_MSG = create("auction setting.disable auto save message", false, "If true, auction house will not log the auto save task to the console");
	public static final ConfigEntry DISABLE_CLEANUP_MSG = create("auction setting.disable clean up message", false, "If true, auction house will not log the clean up process to the console");

	public static final ConfigEntry DISABLE_PROFILE_UPDATE_MSG = create("auction setting.disable profile update message", false, "If true, auction house will not log the player profile updates to the console");
//	public static final ConfigEntry DISABLE_PLAYER_REF_UPDATE_MSG = create("auction setting.disable player reference update message", false, "If true, auction house will not log the player reference updates to the console");

	public static final ConfigEntry TICK_UPDATE_TIME = create("auction setting.tick auctions every", 1, "How many seconds should pass before the plugin updates all the times on items?");

	public static final ConfigEntry GARBAGE_DELETION_TIMED_MODE = create("auction setting.garbage deletion.timed mode", true, "If true, auction house will only run the garbage deletion task, after set amount of seconds", "otherwise if false, it will wait until the total garbage bin count", "reaches/exceeds the specified value");
	public static final ConfigEntry GARBAGE_DELETION_TIMED_DELAY = create("auction setting.garbage deletion.timed delay", 60, "If timed mode is true, this value will be ran after x specified seconds, the lower this number the more frequent a new async task will be ran!");
	public static final ConfigEntry GARBAGE_DELETION_MAX_ITEMS = create("auction setting.garbage deletion.max items", 30, "If timed mode is false, whenever the garbage bin reaches this number, auction house will run the deletion task.", "You should adjust this number based on your server since some servers may have more or less items being claimed / marked for garbage clean up");

	public static final ConfigEntry CLAIM_MS_DELAY = create("auction setting.item claim delay", 100, "How many ms should a player wait before being allowed to claim an item?, Ideally you don't wanna change this. It's meant to prevent auto clicker dupe claims");

	public static final ConfigEntry TICK_UPDATE_GUI_TIME = create("auction setting.refresh gui every", 10, "How many seconds should pass before the auction gui auto refreshes?");
	public static final ConfigEntry RECORD_TRANSACTIONS = create("auction setting.record transactions", true, "Should every transaction be recorded (everything an auction is won or an item is bought)");
	public static final ConfigEntry BUNDLE_IS_OPENED_ON_RECLAIM = create("auction setting.open bundle on reclaim", true, "When the player claims an expired item, if its a bundle, should it be automatically opened. (items that cannot fit will drop to the ground)");
	public static final ConfigEntry MAX_SHULKER_IN_BUNDLE = create("auction setting.maximmum bundle and shulker in bundle", 5, "The maximum amount of shulkers/vanilla bundles that can be added to a bundle");

	public static final ConfigEntry BROADCAST_AUCTION_LIST = create("auction setting.broadcast auction list", false, "Should the entire server be alerted when a player lists an item?");
	public static final ConfigEntry BROADCAST_AUCTION_BID = create("auction setting.broadcast auction bid", false, "Should the entire server be alerted when a player bids on an item?");
	public static final ConfigEntry BROADCAST_AUCTION_SALE = create("auction setting.broadcast auction sale", false, "Should the entire server be alerted when an auction is sold");
	public static final ConfigEntry BROADCAST_AUCTION_ENDING = create("auction setting.broadcast auction ending", false, "Should the entire server be alerted when an auction is about to end?");
	public static final ConfigEntry BROADCAST_AUCTION_ENDING_AT_TIME = create("auction setting.broadcast auction ending at time", 20, "When the time on the auction item reaches this amount of seconds left, the broadcast ending will take affect ");
	public static final ConfigEntry OFFLINE_NOTIFICATIONS_ENABLED = create("auction setting.offline notifications enabled", true, "When a player is offline, queue auction notifications (item sold, outbid, bid placed, etc.) and deliver them when they next join.");

	public static final ConfigEntry USE_REALISTIC_BIDDING = create("auction setting.use realistic bidding", false, "If true auction house will use a more realistic bidding approach. Ex. the previous bid is 400, and if a player bids 500, rather than making the new bid 900, it will be set to 500.");
	public static final ConfigEntry BID_MUST_BE_HIGHER_THAN_PREVIOUS = create("auction setting.bid must be higher than previous", true, "Only applies if use realistic bidding is true, this will make it so that they must bid higher than the current bid.");
	public static final ConfigEntry USE_LIVE_BID_NUMBER_IN_CONFIRM_GUI = create("auction setting.live bid number in confirm gui.use", true, "If true, the bid confirmation menu will auto update every 1 second by default");
	public static final ConfigEntry LIVE_BID_NUMBER_IN_CONFIRM_GUI_RATE = create("auction setting.live bid number in confirm gui.rate", 1, "How often the confirm gui for bids will update");

	public static final ConfigEntry PLAYER_NEEDS_TOTAL_PRICE_TO_BID = create("auction setting.bidder must have funds in account", false, "Should the player who is placing a bid on an item have the money in their account to cover the cost?");
	public static final ConfigEntry ALLOW_USAGE_OF_BID_SYSTEM = create("auction setting.allow bid system usage", true, "Should players be allowed to use the bid option cmd params?");
	public static final ConfigEntry ALLOW_USAGE_OF_BUY_NOW_SYSTEM = create("auction setting.allow buy now system usage", true, "Should players be allowed to use the right-click buy now feature on biddable items?");
	public static final ConfigEntry BUY_NOW_DISABLED_BY_DEFAULT_IN_SELL_MENU = create("auction setting.buy now disabled in sell menu by default", false, "If true, players will just need to toggle buy now on their items to allow buy now");
	public static final ConfigEntry AUTO_SAVE_ENABLED = create("auction setting.auto save.enabled", true, "Should the auto save task be enabled?");
	public static final ConfigEntry AUTO_SAVE_EVERY = create("auction setting.auto save.time", 900, "How often should the auto save active? (in seconds. Ex. 900 = 15min)");
	public static final ConfigEntry ALLOW_PURCHASE_OF_SPECIFIC_QUANTITIES = create("auction setting.allow purchase of specific quantities", false, "When a buy now item is right-clicked should it open a", "special gui to specify the quantity of items to buy from the stack?");
	public static final ConfigEntry USE_REFRESH_COOL_DOWN = create("auction setting.use refresh cool down", true, "Should the refresh cooldown be enabled?");
	public static final ConfigEntry REFRESH_COOL_DOWN = create("auction setting.refresh cool down", 2, "How many seconds should pass before the player can refresh the auction house again?");
	public static final ConfigEntry MAIN_AH_FILTER_COOLDOWN = create("auction setting.auction house filter cooldown", 1500, "How many milliseconds should pass before they can change the filter again? use -1 to disable");
	public static final ConfigEntry MAIN_AH_NAVIGATION_COOLDOWN = create("auction setting.auction house page navigation cooldown", 500, "How many milliseconds should pass before they can navigate to another page? use -1 to disable");
	public static final ConfigEntry MAIN_AH_ITEM_CLICK_COOLDOWN = create("auction setting.auction house item click cooldown", 500, "How many milliseconds should pass before a player can click another auction item? This prevents spam clicking and server lag. use -1 to disable");
	public static final ConfigEntry MAIN_AH_REFRESH_BUTTON_COOLDOWN = create("auction setting.auction house refresh button cooldown", 1000, "How many milliseconds should pass before a player can click the refresh button again? This prevents spam clicking and server lag. use -1 to disable");
	public static final ConfigEntry TRANSACTION_FILTER_COOLDOWN = create("auction setting.transaction filter cooldown", 1500, "How many milliseconds should pass before they can change the filter again? use -1 to disable");
	public static final ConfigEntry TRANSACTION_NAVIGATION_COOLDOWN = create("auction setting.transaction page navigation cooldown", 500, "How many milliseconds should pass before they can navigate to another page? use -1 to disable");

	public static final ConfigEntry CMD_COOLDOWN = create("auction setting.command cool down", 0, "How many seconds should pass between using commands");

	public static final ConfigEntry ALLOW_PURCHASE_IF_INVENTORY_FULL = create("auction setting.allow purchase with full inventory", true, "Should auction house allow players to buy items even if their", "inventory is full, if true, items will be dropped on the floor if there is no room.");
	public static final ConfigEntry ASK_FOR_BID_CONFIRMATION = create("auction setting.ask for bid confirmation", true, "Should Auction House open the confirmation menu for the user to confirm", "whether they actually meant to place a bid or not?");
	public static final ConfigEntry ASK_FOR_PURCHASE_CONFIRMATION = create("auction setting.ask for purchase confirmation", true, "Should Auction House open the confirmation menu for the user to confirm", "whether they actually meant to purchase the item or not?");
	public static final ConfigEntry ASK_FOR_LISTING_CONFIRMATION = create("auction setting.ask for listing confirmation", false, "Should Auction House ask the user to confirm the listing?");
	public static final ConfigEntry REPLACE_HOW_TO_SELL_WITH_LIST_BUTTON = create("auction setting.replace how to sell with list button", false, "This will replace the \\\"How to Sell\\\" button with a List Item button");
	public static final ConfigEntry REPLACE_GUIDE_WITH_CART_BUTTON = create("auction setting.replace guide with cart button", false, "This will replace the \\\"Guide\\\" button with the Cart button. This will only work if the cart system is enabled");
	public static final ConfigEntry ALLOW_USAGE_OF_SELL_GUI = create("auction setting.allow usage of sell gui", true, "Should the sell menu be enabled?");
	public static final ConfigEntry FORCE_AUCTION_USAGE = create("auction setting.force auction usage", false, "If enabled, all items sold on the auction house must be an auction (biddable) items");
	public static final ConfigEntry ALLOW_INDIVIDUAL_ITEM_CLAIM = create("auction setting.allow individual item claim", true, "If enabled, you will be able to click individual items from the expiration menu to claim them back. Otherwise you will have to use the claim all button");
	public static final ConfigEntry FORCE_CUSTOM_BID_AMOUNT = create("auction setting.force custom bid amount", false, "If enabled, the bid increment line on auction items will be hidden, bid increment values will be ignored, and when you go to bid on an item, it will ask you to enter a custom amount.");
	public static final ConfigEntry SOUND_PITCH = create("auction setting.sound.pitch", 1.0, "The pitch value for sounds played by auction house");
	public static final ConfigEntry SOUND_VOLUME = create("auction setting.sound.volume", 1.0, "The volume value for sounds played by auction house");

	public static final ConfigEntry BIDDING_TAKES_MONEY = create("auction setting.bidding takes money", false, "If enabled, players will be outright charged the current bid for the item", "If they are outbid or the item is cancelled, they will get their money back. Disables ability for owners to bid on their own items!");
	public static final ConfigEntry LIST_ITEM_DELAY = create("auction setting.list item delay", -1, "If not set to -1 (disabled) how many seconds must a player wait to list another item after listing 1?");
	public static final ConfigEntry FORCE_SYNC_MONEY_ACTIONS = create("auction setting.force sync money actions", false, "If true, auction house will forcefully run a sync task to withdraw/deposit cash, this does not apply when using the commands");
	public static final ConfigEntry EXPIRATION_TIME_LIMIT_ENABLED = create("auction setting.expiration time limit.enabled", false, "If true, auction house will automatically delete un claimed expired items after 7 days (default)");
	public static final ConfigEntry EXPIRATION_TIME_LIMIT = create("auction setting.expiration time limit.time", 24 * 7, "In hours, what should the minimum age of an unclaimed item be inorder for it to be deleted?");

	public static final ConfigEntry ASK_FOR_CANCEL_CONFIRM_ON_BID_ITEMS = create("auction setting.ask for cancel confirm on bid items", true, "Should Auction House ask the user if they want to cancel the item?");
	public static final ConfigEntry ASK_FOR_CANCEL_CONFIRM_ON_NON_BID_ITEMS = create("auction setting.ask for cancel confirm on non bid items", false, "Should Auction House ask the user if they want to cancel the item?");
	public static final ConfigEntry ASK_FOR_CANCEL_CONFIRM_ON_ALL_ITEMS = create("auction setting.ask for cancel confirm on end all", true, "Should Auction House ask the user to confirm in chat when using end all in active listings?");

	public static final ConfigEntry BASE_PRICE_MUST_BE_HIGHER_THAN_BID_START = create("auction setting.base price must be higher than bid start", true, "Should the base price (buy now price) be higher than the initial bid starting price?");
	public static final ConfigEntry SYNC_BASE_PRICE_TO_HIGHEST_PRICE = create("auction setting.sync the base price to the current price", true, "Ex. If the buy now price was 100, and the current price exceeds 100 to say 200, the buy now price will become 200.");
	public static final ConfigEntry ADMIN_OPTION_SHOW_RETURN_ITEM = create("auction setting.admin option.show return to player", true);
	public static final ConfigEntry ADMIN_OPTION_SHOW_CLAIM_ITEM = create("auction setting.admin option.show claim item", true);
	public static final ConfigEntry ADMIN_OPTION_SHOW_DELETE_ITEM = create("auction setting.admin option.show delete item", true);
	public static final ConfigEntry ADMIN_OPTION_SHOW_COPY_ITEM = create("auction setting.admin option.show copy item", true);

	public static final ConfigEntry ALLOW_PLAYERS_TO_ACCEPT_BID = create("auction setting.allow players to accept bid", true, "If true, players can right click a biddable item inside their active listings menu to accept the current bid");
	public static final ConfigEntry SELLERS_MUST_WAIT_FOR_TIME_LIMIT_AFTER_BID = create("auction setting.prevent cancellation of bid on items", false, "If true, players must wait out the duration of the auction listing if there is already a bid on it (makes them commit to selling it)");
	public static final ConfigEntry PER_WORLD_ITEMS = create("auction setting.per world items", false, "If true, items can only be seen in the world they were listed in, same goes for bidding/buying/collecting");
	public static final ConfigEntry ALLOW_PLAYERS_TO_DEFINE_AUCTION_TIME = create("auction setting.allow players to set auction time", false, "If true players can use -t 1 day for example to set the listing time for their item");
	public static final ConfigEntry MAX_CUSTOM_DEFINED_TIME = create("auction setting.max custom defined time", 604800, "What should the limit on custom defined listing times be in seconds?");
	public static final ConfigEntry SMART_MIN_BUY_PRICE = create("auction setting.smart min and buy price", false, "Will calculate buy now/min prices on a per item basis. For example, if the user states $100 and the item is in a stack of", "32, the min / buy now price will be $3200. If they provide -s or -stack in the command", "this will be ignored and the entire stack will sell for $100");
	public static final ConfigEntry TITLE_INPUT_CANCEL_WORD = create("auction setting.title input cancel word", "cancel", "The word to be used to cancel chat inputs (users can also just click any block)");

	public static final ConfigEntry USE_SEPARATE_FILTER_MENU = create("auction setting.use separate filter menu", false, "If true, rather than using a single filter item inside the auction menu", "it will open an entirely new menu to select the filter");
	public static final ConfigEntry FILTER_ONLY_USES_WHITELIST = create("auction setting.filter only uses whitelist", false, "If true, auction house will ignore default filters, and only filter by the items added to the category whitelists");
	public static final ConfigEntry FILTER_WHITELIST_USES_DURABILITY = create("auction setting.filter whitelist uses durability", false, "If true, the filter will look at material names and durability values for comparisons only");
	public static final ConfigEntry SELL_MENU_REQUIRES_USER_TO_HOLD_ITEM = create("auction setting.require user to hold item when using sell menu", false, "If enabled, when running just /ah sell, the user will need to hold the item in their hand, otherwise they just add it in the gui.");
	public static final ConfigEntry OPEN_MAIN_AUCTION_HOUSE_AFTER_MENU_LIST = create("auction setting.open main auction house after listing using menu", true, "Should the main auction house be opened after the user lists an item using the sell menu?");
	public static final ConfigEntry SELL_MENU_CLOSE_SENDS_TO_LISTING = create("auction setting.sell menu close sends to listings", true, "If true, when the player clicks the close button within the sell menu, it will send them to the main auction house");
	public static final ConfigEntry PAYMENT_HANDLE_USE_CMD = create("auction setting.payment handle.use command", false, "In special cases, you will want to use this");
	public static final ConfigEntry PAYMENT_HANDLE_WITHDRAW_CMD = create("auction setting.payment handle.withdraw command", "eco take %player% %price%", "Command that will be executed to withdraw a player's balance");
	public static final ConfigEntry PAYMENT_HANDLE_DEPOSIT_CMD = create("auction setting.payment handle.deposit command", "eco give %player% %price%", "Command that will be executed to deposit a player's balance");

	public static final ConfigEntry TAX_ENABLED = create("auction setting.tax.enabled", false, "Should auction house use it's tax system?");
	public static final ConfigEntry TAX_CHARGE_LISTING_FEE = create("auction setting.tax.charge listing fee", true, "Should auction house charge players to list an item?");
	public static final ConfigEntry TAX_LISTING_FEE = create("auction setting.tax.listing fee", 5.0, "How much should it cost to list a new item?");
	public static final ConfigEntry TAX_LISTING_FEE_PERCENTAGE = create("auction setting.tax.listing fee is percentage", true, "Should the listing fee be based on a percentage instead?");
	public static final ConfigEntry TAX_CHARGE_SALES_TAX_TO_BUYER = create("auction setting.tax.charge sale tax to buyer", false, "Should auction house tax the buyer instead of the seller?");
	public static final ConfigEntry TAX_SALES_TAX_BUY_NOW_PERCENTAGE = create("auction setting.tax.buy now sales tax", 15.0, "Tax % that should be charged on items that are bought immediately");
	public static final ConfigEntry TAX_SALES_TAX_AUCTION_WON_PERCENTAGE = create("auction setting.tax.auction won sales tax", 10.0, "Tax % that should be charged on items that are won through the auction");


	public static final ConfigEntry FILTERS_ALL_ICON = create("auction setting.filter icons.all", "HOPPER");
	public static final ConfigEntry FILTERS_FOOD_ICON = create("auction setting.filter icons.food", "APPLE");
	public static final ConfigEntry FILTERS_ARMOR_ICON = create("auction setting.filter icons.armor", "DIAMOND_HELMET");
	public static final ConfigEntry FILTERS_BLOCKS_ICON = create("auction setting.filter icons.blocks", "GRASS_BLOCK");
	public static final ConfigEntry FILTERS_TOOLS_ICON = create("auction setting.filter icons.tools", "STONE_SHOVEL");
	public static final ConfigEntry FILTERS_WEAPONS_ICON = create("auction setting.filter icons.weapons", "IRON_SWORD");
	public static final ConfigEntry FILTERS_SPAWNERS_ICON = create("auction setting.filter icons.spawners", "SPAWNER");
	public static final ConfigEntry FILTERS_ENCHANTS_ICON = create("auction setting.filter icons.enchants", "ENCHANTED_BOOK");
	public static final ConfigEntry FILTERS_POTIONS_ICON = create("auction setting.filter icons.potions", "POTION");
	public static final ConfigEntry FILTERS_MISC_ICON = create("auction setting.filter icons.misc", "OAK_SIGN");
	public static final ConfigEntry FILTERS_SELF_ICON = create("auction setting.filter icons.self", "NAME_TAG");
	public static final ConfigEntry FILTERS_SEARCH_ICON = create("auction setting.filter icons.search", "COMPASS");


	public static final ConfigEntry ALL_FILTER_ENABLED = create("auction setting.enabled filters.all", true, "Should this filter be enabled?");
	public static final ConfigEntry FOOD_FILTER_ENABLED = create("auction setting.enabled filters.food", true, "Should this filter be enabled?");
	public static final ConfigEntry ARMOR_FILTER_ENABLED = create("auction setting.enabled filters.armor", true, "Should this filter be enabled?");
	public static final ConfigEntry BLOCKS_FILTER_ENABLED = create("auction setting.enabled filters.blocks", true, "Should this filter be enabled?");
	public static final ConfigEntry TOOLS_FILTER_ENABLED = create("auction setting.enabled filters.tools", true, "Should this filter be enabled?");
	public static final ConfigEntry WEAPONS_FILTER_ENABLED = create("auction setting.enabled filters.weapons", true, "Should this filter be enabled?");
	public static final ConfigEntry SPAWNERS_FILTER_ENABLED = create("auction setting.enabled filters.spawners", true, "Should this filter be enabled?");
	public static final ConfigEntry ENCHANTS_FILTER_ENABLED = create("auction setting.enabled filters.enchants", true, "Should this filter be enabled?");
	public static final ConfigEntry POTIONS_FILTER_ENABLED = create("auction setting.enabled filters.potions", true, "Should this filter be enabled?");
	public static final ConfigEntry MISC_FILTER_ENABLED = create("auction setting.enabled filters.misc", true, "Should this filter be enabled?");
	public static final ConfigEntry SEARCH_FILTER_ENABLED = create("auction setting.enabled filters.search", true, "Should this filter be enabled?");
	public static final ConfigEntry SELF_FILTER_ENABLED = create("auction setting.enabled filters.self", true, "Should this filter be enabled?");
	public static final ConfigEntry USE_AUCTION_CHEST_MODE = create("auction setting.use auction chest mode", false, "Enabling this will make it so players can only access the auction through the auction chest");
	public static final ConfigEntry AUTO_BSTATS = create("auction setting.use bstats", true, "Auto enable bStats");
	public static final ConfigEntry FORCE_MATERIAL_NAMES_FOR_DISCORD = create("auction setting.force material names for discord", false, "If true, auction house will use the actual material name rather than custom name");

	public static final ConfigEntry ALLOW_ITEM_BUNDLES = create("auction setting.bundles.enabled", true, "If true, players can use -b in the sell command to bundle all similar items into a single item.");
	public static final ConfigEntry ITEM_BUNDLE_ITEM = create("auction setting.bundles.item", CompMaterial.GOLD_BLOCK.name());
	public static final ConfigEntry MIN_ITEM_PRICE_USES_SIMPE_COMPARE = create("auction setting.use simple compare for min item price", true, "If true, AH will just compare material and model data types");

	public static final ConfigEntry CLICKS_NON_BID_ITEM_PURCHASE = create("auction setting.clicks.non bid item purchase", "LEFT", "Valid Click Types", "LEFT", "RIGHT", "SHIFT_LEFT", "SHIFT_RIGHT", "MIDDLE", "", "&cIf you overlap click types (ex. LEFT for both inspect and buy) things will go crazy.");

	public static final ConfigEntry CLICKS_NON_BID_ITEM_ADD_TO_CART = create("auction setting.clicks.non bid item add to cart", "RIGHT", "Valid Click Types", "LEFT", "RIGHT", "SHIFT_LEFT", "SHIFT_RIGHT", "MIDDLE", "", "&cIf you overlap click types (ex. LEFT for both inspect and buy) things will go crazy.");

	public static final ConfigEntry CLICKS_NON_BID_ITEM_QTY_PURCHASE = create("auction setting.clicks.non bid item qty purchase", "SHIFT_LEFT", "Valid Click Types", "LEFT", "RIGHT", "SHIFT_LEFT", "SHIFT_RIGHT", "MIDDLE", "", "&cIf you overlap click types (ex. LEFT for both inspect and buy) things will go crazy.");

	public static final ConfigEntry CLICKS_BID_ITEM_PLACE_BID = create("auction setting.clicks.bid item place bid", "LEFT", "Valid Click Types", "LEFT", "RIGHT", "SHIFT_LEFT", "SHIFT_RIGHT", "MIDDLE", "", "&cIf you overlap click types (ex. LEFT for both inspect and buy) things will go crazy.");

	public static final ConfigEntry CLICKS_BID_ITEM_BUY_NOW = create("auction setting.clicks.bid item buy now", "RIGHT", "Valid Click Types", "LEFT", "RIGHT", "SHIFT_LEFT", "SHIFT_RIGHT", "MIDDLE", "", "&cIf you overlap click types (ex. LEFT for both inspect and buy) things will go crazy.");

	public static final ConfigEntry CLICKS_INSPECT_CONTAINER = create("auction setting.clicks.inspect container", "SHIFT_RIGHT", "Valid Click Types", "LEFT", "RIGHT", "SHIFT_LEFT", "SHIFT_RIGHT", "MIDDLE", "", "&cIf you overlap click types (ex. LEFT for both inspect and buy) things will go crazy.");

	public static final ConfigEntry CLICKS_REMOVE_ITEM = create("auction setting.clicks.remove item", "DROP", "Valid Click Types", "LEFT", "RIGHT", "SHIFT_LEFT", "SHIFT_RIGHT", "MIDDLE", "", "&cIf you overlap click types (ex. LEFT for both inspect and buy) things will go crazy.");

	public static final ConfigEntry CLICKS_FILTER_SORT_PRICE_OR_RECENT = create("auction setting.clicks.filter.sort by price or recent", "SHIFT_RIGHT", "Valid Click Types", "LEFT", "RIGHT", "SHIFT_LEFT", "SHIFT_RIGHT", "MIDDLE", "DROP", "", "&cIf you overlap click types (ex. LEFT for both inspect and buy) things will go crazy.");

	public static final ConfigEntry CLICKS_FILTER_CURRENCY = create("auction setting.clicks.filter.listing currency", "SHIFT_LEFT", "Valid Click Types", "LEFT", "RIGHT", "SHIFT_LEFT", "SHIFT_RIGHT", "MIDDLE", "DROP", "", "&cIf you overlap click types (ex. LEFT for both inspect and buy) things will go crazy.");

	public static final ConfigEntry CLICKS_FILTER_SORT_SALE_TYPE = create("auction setting.clicks.filter.sort sale type", "RIGHT", "Valid Click Types", "LEFT", "RIGHT", "SHIFT_LEFT", "SHIFT_RIGHT", "MIDDLE", "DROP", "", "&cIf you overlap click types (ex. LEFT for both inspect and buy) things will go crazy.");

	public static final ConfigEntry CLICKS_FILTER_TRANSACTION_BUY_TYPE = create("auction setting.clicks.filter.transaction buy type", "SHIFT_LEFT", "Valid Click Types", "LEFT", "RIGHT", "SHIFT_LEFT", "SHIFT_RIGHT", "MIDDLE", "DROP", "", "&cIf you overlap click types (ex. LEFT for both inspect and buy) things will go crazy.");

	public static final ConfigEntry CLICKS_FILTER_RESET = create("auction setting.clicks.filter.reset", "DROP", "Valid Click Types", "LEFT", "RIGHT", "SHIFT_LEFT", "SHIFT_RIGHT", "MIDDLE", "DROP", "", "&cIf you overlap click types (ex. LEFT for both inspect and buy) things will go crazy.");

	public static final ConfigEntry CLICKS_FILTER_CATEGORY = create("auction setting.clicks.filter.change category", "LEFT", "Valid Click Types", "LEFT", "RIGHT", "SHIFT_LEFT", "SHIFT_RIGHT", "MIDDLE", "DROP", "", "&cIf you overlap click types (ex. LEFT for both inspect and buy) things will go crazy.");


	/*  ===============================
	 *         DATABASE OPTIONS
	 *  ===============================*/
	public static final ConfigEntry DATABASE_USE = create("database.use database", false, "Should the plugin use a database to store shop data?");
	public static final ConfigEntry DATABASE_TABLE_PREFIX = create("database.table prefix", "auctionhouse_", "What prefix should be used for table names");
	public static final ConfigEntry DATABASE_HOST = create("database.host", "localhost", "What is the connection url/host");
	public static final ConfigEntry DATABASE_PORT = create("database.port", 3306, "What is the port to database (default is 3306)");
	public static final ConfigEntry DATABASE_NAME = create("database.name", "plugin_dev", "What is the name of the database?");
	public static final ConfigEntry DATABASE_USERNAME = create("database.username", "root", "What is the name of the user connecting?");
	public static final ConfigEntry DATABASE_PASSWORD = create("database.password", "Password1.", "What is the password to the user connecting?");
	public static final ConfigEntry DATABASE_CUSTOM_PARAMS = create("database.custom parameters", "?useUnicode=yes&characterEncoding=UTF-8&useServerPrepStmts=false&rewriteBatchedStatements=true&useSSL=true", "Leave this alone if you don't know what you're doing. Set to 'None' to use no custom connection params");

	/*  ===============================
	 *         DISCORD WEBHOOK NEW
	 *  ===============================*/
	public static final ConfigEntry DISCORD_ENABLED = create("discord.enabled", false, "Should the discord webhook feature be enabled?");
	public static final ConfigEntry DISCORD_WEBHOOKS = create("discord.webhooks", Collections.singletonList("https://discord.com/api/webhooks/1077667480920653840/CZbJG7DBoGhPXYICgp2--Ey_itVVmYqaQgorBfpvL7nQoQZWWMxz1TQgs1xG45Mzlpsn"), "A list of webhook urls (channels) you want a message sent to");
	public static final ConfigEntry DISCORD_DELAY_LISTINGS = create("discord.delay options.delay listing", false, "If true AuctionHouse will delay sending new listing messages by the specified seconds.");
	public static final ConfigEntry DISCORD_DELAY_LISTING_TIME = create("discord.delay options.delay listing time", 10, "How many seconds should Auction House wait to send the discord message for new listings");

	// options for when the alerts should be sent
	public static final ConfigEntry DISCORD_ALERT_ON_AUCTION_START = create("discord.alerts.new auction listing", true, "Should a message be sent when a new auction listing is made");
	public static final ConfigEntry DISCORD_ALERT_ON_BIN_START = create("discord.alerts.new bin listing", true, "Should a message be sent when a new bin listing is made (non biddable)");
	public static final ConfigEntry DISCORD_ALERT_ON_BID = create("discord.alerts.new bid", true, "Should a message be sent when a bid is placed on an item");
	public static final ConfigEntry DISCORD_ALERT_ON_BIN_BUY = create("discord.alerts.bin listing bought", true, "Should a message be sent when an item is bought");
	public static final ConfigEntry DISCORD_ALERT_ON_AUCTION_WON = create("discord.alerts.auction listing won", true, "Should a message be sent when an auction is won");
	// colors for each message
	public static final ConfigEntry DISCORD_COLOR_NEW_AUCTION_LISTING = create("discord.colors.new auction listing", "137-100-100", "The color of the embed, it needs to be in hsb format.", "Separate the numbers with a -");
	public static final ConfigEntry DISCORD_COLOR_NEW_BIN_LISTING = create("discord.colors.new bin listing", "137-100-100", "The color of the embed, it needs to be in hsb format.", "Separate the numbers with a -");
	public static final ConfigEntry DISCORD_COLOR_NEW_BID = create("discord.colors.new bid", "137-100-100", "The color of the embed, it needs to be in hsb format.", "Separate the numbers with a -");
	public static final ConfigEntry DISCORD_COLOR_BIN_LISTING_BOUGHT = create("discord.colors.bin listing bought", "137-100-100", "The color of the embed, it needs to be in hsb format.", "Separate the numbers with a -");
	public static final ConfigEntry DISCORD_COLOR_AUCTION_LISTING_WON = create("discord.colors.auction listing won", "137-100-100", "The color of the embed, it needs to be in hsb format.", "Separate the numbers with a -");
	// titles for each message
	// fields
	public static final ConfigEntry DISCORD_MSG_FIELD_SELLER_INLINE = create("discord.field.seller.inline", true);

	public static final ConfigEntry DISCORD_MSG_FIELD_ITEM_INLINE = create("discord.field.item.inline", true);



	public static final ConfigEntry DISCORD_MSG_FIELD_BIN_LISTING_PRICE_INLINE = create("discord.field.bin listing price.inline", true);

	public static final ConfigEntry DISCORD_MSG_FIELD_AUCTION_BUYOUT_PRICE_INLINE = create("discord.field.auction buyout price.inline", true);

	public static final ConfigEntry DISCORD_MSG_FIELD_AUCTION_START_PRICE_INLINE = create("discord.field.auction start price.inline", false);

	public static final ConfigEntry DISCORD_MSG_FIELD_BIN_BOUGHT_INLINE = create("discord.field.bin listing bought.inline", false);

	public static final ConfigEntry DISCORD_MSG_FIELD_AUCTION_WON_INLINE = create("discord.field.auction listing won price.inline", false);

	public static final ConfigEntry DISCORD_MSG_FIELD_AUCTION_WINNER_INLINE = create("discord.field.auction winner.inline", false);

	public static final ConfigEntry DISCORD_MSG_FIELD_AUCTION_BIDDER_INLINE = create("discord.field.auction bidder.inline", false);

	public static final ConfigEntry DISCORD_MSG_FIELD_BID_AMT_INLINE = create("discord.field.bid amount.inline", true);

	public static final ConfigEntry DISCORD_MSG_FIELD_AUCTION_CURRENT_PRICE_INLINE = create("discord.field.current auction price.inline", true);

	/*  ===============================
	 *          BLACK LISTED
	 *  ===============================*/
	public static final ConfigEntry BLOCKED_ITEMS = create("blocked items", Collections.singletonList("ENDER_CHEST"), "Materials that should be blocked (not allowed to sell)");
	public static final ConfigEntry BLOCKED_NBT_TAGS = create("blocked nbt tags", Collections.singletonList("example_tag"), "A list of NBT tags that are blocked from the auction house. These are case sensitive");
	public static final ConfigEntry MAKE_BLOCKED_ITEMS_A_WHITELIST = create("blocked items is whitelist", false, "If true, blocked items will become a whitelist, meaning only items specified in blacked list will be allowed in the ah");
	public static final ConfigEntry BLOCKED_ITEM_NAMES = create("blocked item names", Arrays.asList(
			"fuck",
			"bitch",
			"nigger",
			"nigga",
			"pussy"
	), "If an item contains any words/names specified here, it won't list.");

	public static final ConfigEntry BLOCKED_ITEM_LORES = create("blocked item lores", Arrays.asList(
			"kill yourself",
			"another random phrase"
	), "If an item lore contains any of these values, it won't list");

	/*  ===============================
	 *         MAX AUCTION TIME
	 *  ===============================*/
	public static final ConfigEntry AUCTION_TIME = create("auction time", Collections.singletonList("rankone:30"), "Special time permissions for users.", "If they have the following permission in this format:", "auctionhouse.time.rankone", "rankone refers to the list item under auction time, they will get the time specified (in seconds)");

	/*  ===============================
	 *           GLOBAL ITEMS
	 *  ===============================*/

	public static final ConfigEntry GUI_FILLER = create("gui.filler item", CompMaterial.BLACK_STAINED_GLASS_PANE.name(), "An item to be used to fill empty gui slots, this will be", "removed in later versions to be done on a per gui basis");

	public static final ConfigEntry GUI_BACK_BTN_ITEM = create("gui.global items.back button.item", "OAK_DOOR", "Settings for the previous page button");


	public static final ConfigEntry GUI_PREV_PAGE_BTN_SLOT = create("gui.global items.previous page button.slot", 48, "Valid Slots: 45 - 53");
	public static final ConfigEntry GUI_PREV_PAGE_BTN_ITEM = create("gui.global items.previous page button.item", "ARROW", "Settings for the previous page button");

	public static final ConfigEntry GUI_CLOSE_BTN_ITEM = create("gui.global items.close button.item", "BARRIER", "Settings for the close button");

	public static final ConfigEntry GUI_NEXT_PAGE_BTN_SLOT = create("gui.global items.next page button.slot", 50, "Valid Slots: 45 - 53");
	public static final ConfigEntry GUI_NEXT_PAGE_BTN_ITEM = create("gui.global items.next page button.item", "ARROW", "Settings for the next button");

	public static final ConfigEntry GUI_REFRESH_BTN_ENABLED = create("gui.global items.refresh button.enabled", true);
	public static final ConfigEntry GUI_REFRESH_BTN_SLOT = create("gui.global items.refresh button.slot", 49, "Valid Slots: 45 - 53");
	public static final ConfigEntry GUI_REFRESH_BTN_ITEM = create("gui.global items.refresh button.item", "CHEST", "Settings for the refresh page");

	// currency picker


	// material picker

	/*  ===============================
	 *        CART GUI
	 *  ===============================*/
	public static final ConfigEntry GUI_CART_ROWS = create("gui.cart.rows", 6);
	public static final ConfigEntry GUI_CART_FILL_SLOTS = create("gui.cart.fill slots", IntStream.rangeClosed(0, 44).boxed().collect(Collectors.toList()));
	public static final ConfigEntry GUI_CART_ITEMS_CHECKOUT_ITEM = create("gui.cart.items.checkout.item", CompMaterial.LIME_STAINED_GLASS_PANE.name());
	public static final ConfigEntry GUI_CART_ITEMS_CHECKOUT_SLOT = create("gui.cart.items.checkout.slot", 49);

	/*  ===============================
	 *         MAIN AUCTION GUI
	 *  ===============================*/
	public static final ConfigEntry GUI_AUCTION_HOUSE_ROWS = create("gui.auction house.rows", 6);
	public static final ConfigEntry GUI_AUCTION_HOUSE_FILL_SLOTS = create("gui.auction house.fill slots", IntStream.rangeClosed(0, 44).boxed().collect(Collectors.toList()));
	public static final ConfigEntry GUI_AUCTION_HOUSE_FILL_ITEM = create("gui.auction house.fill item", GUI_FILLER.getString(), "Item used to fill empty listing slots in the main auction house GUI", "This only applies to slots defined in gui.auction house.fill slots");
	public static final ConfigEntry GUI_AUCTION_HOUSE_BG_ITEM = create("gui.auction house.bg item", GUI_FILLER.getString(), "Default/background item for the main auction house GUI", "Used for non-listing slots and any empty/locked slots in this GUI");


	public static final ConfigEntry GUI_AUCTION_HOUSE_ITEMS_GUIDE_ENABLED = create("gui.auction house.items.guide.enabled", true);
	public static final ConfigEntry GUI_AUCTION_HOUSE_ITEMS_GUIDE_SLOT = create("gui.auction house.items.guide.slot", 53, "Valid Slots: 45 - 53");
	public static final ConfigEntry GUI_AUCTION_HOUSE_ITEMS_GUIDE_ITEM = create("gui.auction house.items.guide.item", "BOOK");

	public static final ConfigEntry GUI_AUCTION_HOUSE_ITEMS_TRANSACTIONS_ENABLED = create("gui.auction house.items.transactions.enabled", true);
	public static final ConfigEntry GUI_AUCTION_HOUSE_ITEMS_TRANSACTIONS_SLOT = create("gui.auction house.items.transactions.slot", 51, "Valid Slots: 45 - 53");
	public static final ConfigEntry GUI_AUCTION_HOUSE_ITEMS_TRANSACTIONS_ITEM = create("gui.auction house.items.transactions.item", "PAPER");

	public static final ConfigEntry GUI_AUCTION_HOUSE_ITEMS_HOW_TO_SELL_ENABLED = create("gui.auction house.items.how to sell.enabled", true);
	public static final ConfigEntry GUI_AUCTION_HOUSE_ITEMS_HOW_TO_SELL_SLOT = create("gui.auction house.items.how to sell.slot", 52, "Valid Slots: 45 - 53");
	public static final ConfigEntry GUI_AUCTION_HOUSE_ITEMS_HOW_TO_SELL_ITEM = create("gui.auction house.items.how to sell.item", "GOLD_INGOT");

	public static final ConfigEntry GUI_AUCTION_HOUSE_ITEMS_CART_SLOT = create("gui.auction house.items.cart.slot", 53, "Valid Slots: 45 - 53");
	public static final ConfigEntry GUI_AUCTION_HOUSE_ITEMS_CART_ITEM = create("gui.auction house.items.cart.item", CompMaterial.CHEST_MINECART.name());

	public static final ConfigEntry GUI_AUCTION_HOUSE_ITEMS_LIST_ITEM_ENABLED = create("gui.auction house.items.list new item.enabled", true);
	public static final ConfigEntry GUI_AUCTION_HOUSE_ITEMS_LIST_ITEM_SLOT = create("gui.auction house.items.list new item.slot", 52, "Valid Slots: 45 - 53");
	public static final ConfigEntry GUI_AUCTION_HOUSE_ITEMS_LIST_ITEM_ITEM = create("gui.auction house.items.list new item.item", "CLOCK");

	public static final ConfigEntry GUI_AUCTION_HOUSE_ITEMS_YOUR_AUCTIONS_ENABLED = create("gui.auction house.items.your auctions.enabled", true);
	public static final ConfigEntry GUI_AUCTION_HOUSE_ITEMS_YOUR_AUCTIONS_SLOT = create("gui.auction house.items.your auctions.slot", 45, "Valid Slots: 45 - 53");
	public static final ConfigEntry GUI_AUCTION_HOUSE_ITEMS_YOUR_AUCTIONS_ITEM = create("gui.auction house.items.your auctions.item", "DIAMOND");

	public static final ConfigEntry GUI_AUCTION_HOUSE_ITEMS_COLLECTION_BIN_ENABLED = create("gui.auction house.items.collection bin.enabled", true);
	public static final ConfigEntry GUI_AUCTION_HOUSE_ITEMS_COLLECTION_BIN_SLOT = create("gui.auction house.items.collection bin.slot", 46, "Valid Slots: 45 - 53");
	public static final ConfigEntry GUI_AUCTION_HOUSE_ITEMS_COLLECTION_BIN_ITEM = create("gui.auction house.items.collection bin.item", "ENDER_CHEST");

	public static final ConfigEntry GUI_AUCTION_HOUSE_ITEMS_WATCHLIST_ENABLED = create("gui.auction house.items.watchlist.enabled", false);
	public static final ConfigEntry GUI_AUCTION_HOUSE_ITEMS_WATCHLIST_SLOT = create("gui.auction house.items.watchlist.slot", 53, "Valid Slots: 45 - 53");
	public static final ConfigEntry GUI_AUCTION_HOUSE_ITEMS_WATCHLIST_ITEM = create("gui.auction house.items.watchlist.item", "BOOKMARK");

	public static final ConfigEntry GUI_WATCHLIST_EMPTY_ITEM = create("gui.watchlist.empty.item", "BARRIER");

	public static final ConfigEntry GUI_AUCTION_HOUSE_ITEMS_FILTER_ENABLED = create("gui.auction house.items.filter.enabled", true);
	public static final ConfigEntry GUI_AUCTION_HOUSE_ITEMS_FILTER_SLOT = create("gui.auction house.items.filter.slot", 47, "Valid Slots: 45 - 53");
	public static final ConfigEntry GUI_AUCTION_HOUSE_ITEMS_FILTER_ITEM = create("gui.auction house.items.filter.item", "NETHER_STAR");

	public static final ConfigEntry GUI_AUCTION_HOUSE_ITEMS_FILTER_MENU_ENABLED = create("gui.auction house.items.filter menu.enabled", true);
	public static final ConfigEntry GUI_AUCTION_HOUSE_ITEMS_FILTER_MENU_SLOT = create("gui.auction house.items.filter menu.slot", 47, "Valid Slots: 45 - 53");
	public static final ConfigEntry GUI_AUCTION_HOUSE_ITEMS_FILTER_MENU_ITEM = create("gui.auction house.items.filter menu.item", "HOPPER");

	/*  ===============================
	 *         CONFIRM BUY GUI
	 *  ===============================*/
	public static final ConfigEntry GUI_CONFIRM_FILL_BG_ON_QUANTITY = create("gui.confirm buy.fill background when buying quantity", true, "Should the empty slots be filled with an item", "when the player decides to buy a specific quantity of items?");
	public static final ConfigEntry GUI_CONFIRM_BG_ITEM = create("gui.confirm buy.bg item", CompMaterial.BLACK_STAINED_GLASS_PANE.name(), "This will only show when buying specific item quantities");

	public static final ConfigEntry GUI_CONFIRM_INCREASE_QTY_ITEM = create("gui.confirm buy.increase button.item", CompMaterial.LIME_STAINED_GLASS_PANE.name());

	public static final ConfigEntry GUI_CONFIRM_DECREASE_QTY_ITEM = create("gui.confirm buy.decrease button.item", CompMaterial.RED_STAINED_GLASS_PANE.name());

	public static final ConfigEntry GUI_CONFIRM_QTY_INFO_ITEM = create("gui.confirm buy.qty info.item", CompMaterial.PAPER.name());

	public static final ConfigEntry GUI_CONFIRM_BUY_NO_ITEM = create("gui.confirm buy.no.item", "RED_STAINED_GLASS_PANE");

	public static final ConfigEntry GUI_CONFIRM_BUY_YES_ITEM = create("gui.confirm buy.yes.item", "LIME_STAINED_GLASS_PANE");

	public static final ConfigEntry GUI_CONFIRM_REQUEST_NO_ITEM = create("gui.confirm request.no.item", "RED_STAINED_GLASS_PANE");

	public static final ConfigEntry GUI_CONFIRM_REQUEST_YES_ITEM = create("gui.confirm request.yes.item", "LIME_STAINED_GLASS_PANE");

	/*  ===============================
	 *         CONFIRM LISTING GUI
	 *  ===============================*/
	public static final ConfigEntry GUI_CONFIRM_LISTING_NO_ITEM = create("gui.confirm listing.no.item", "RED_STAINED_GLASS_PANE");

	public static final ConfigEntry GUI_CONFIRM_LISTING_YES_ITEM = create("gui.confirm listing.yes.item", "LIME_STAINED_GLASS_PANE");

	/*  ===============================
	 *         CONFIRM BID GUI
	 *  ===============================*/
	public static final ConfigEntry GUI_CONFIRM_BID_NO_ITEM = create("gui.confirm bid.no.item", "RED_STAINED_GLASS_PANE");

	public static final ConfigEntry GUI_CONFIRM_BID_YES_ITEM = create("gui.confirm bid.yes.item", "LIME_STAINED_GLASS_PANE");

	/*  ===============================
	 *       CONFIRM CANCEL GUI
	 *  ===============================*/
	public static final ConfigEntry GUI_CONFIRM_CANCEL_NO_ITEM = create("gui.confirm cancel.no.item", "RED_STAINED_GLASS_PANE");

	public static final ConfigEntry GUI_CONFIRM_CANCEL_YES_ITEM = create("gui.confirm cancel.yes.item", "LIME_STAINED_GLASS_PANE");

	public static final ConfigEntry GUI_CONFIRM_GENERAL_NO_ITEM = create("gui.confirm general.no.item", "RED_STAINED_GLASS_PANE");

	public static final ConfigEntry GUI_CONFIRM_GENERAL_YES_ITEM = create("gui.confirm general.yes.item", "LIME_STAINED_GLASS_PANE");

	/*  ===============================
	 *         ACTIVE AUCTION GUI
	 *  ===============================*/


	public static final ConfigEntry GUI_ACTIVE_AUCTIONS_ITEM = create("gui.active auctions.cancel all.item", "ENDER_CHEST");

	/*  ===============================
	 *         ACTIVE BIDS GUI
	 *  ===============================*/



	/*  ===============================
	 *         EXPIRED AUCTION GUI
	 *  ===============================*/


	public static final ConfigEntry GUI_EXPIRED_AUCTIONS_ITEM = create("gui.expired auctions.cancel all.item", "ENDER_CHEST");

	public static final ConfigEntry GUI_EXPIRED_AUCTIONS_PAYMENTS_ITEM = create("gui.expired auctions.collect payments.item", "GOLD_INGOT");

	/*  ===============================
	 *       PAYMENT COLLECTION GUI
	 *  ===============================*/


	public static final ConfigEntry GUI_PAYMENT_COLLECTION_ITEM = create("gui.payment collection.claim all.item", "ENDER_CHEST");

	public static final ConfigEntry GUI_PAYMENT_COLLECTION_PAYMENT_ITEM = create("gui.payment collection.payment.item", "PAPER");

	/*  ===============================
	 *      TRANSACTIONS TYPE GUI
	 *  ===============================*/
	public static final ConfigEntry GUI_TRANSACTIONS_TYPE_BG_ITEM = create("gui.transactions type.bg item", CompMaterial.BLACK_STAINED_GLASS_PANE.name());

	public static final ConfigEntry GUI_TRANSACTIONS_TYPE_ITEMS_ALL_TRANSACTIONS_ITEM = create("gui.transactions type.items.all transactions.item", CompMaterial.PAPER.name());

	public static final ConfigEntry GUI_TRANSACTIONS_TYPE_ITEMS_SELF_TRANSACTIONS_ITEM = create("gui.transactions type.items.self transactions.item", CompMaterial.DIAMOND.name());

	public static final ConfigEntry GUI_TRANSACTIONS_TYPE_ITEMS_REQUEST_TRANSACTIONS_ITEM = create("gui.transactions type.items.requests transactions.item", CompMaterial.WRITTEN_BOOK.name());


	public static final ConfigEntry GUI_TRANSACTIONS_TYPE_ITEMS_DELETE_ITEM = create("gui.transactions type.items.delete transactions.item", CompMaterial.LAVA_BUCKET.name());

	/*  ===============================
	 *       MIN ITEM PRICES GUI
	 *  ===============================*/

	/*  ===============================
	 *    		LOGS LIST GUI
	 *  ===============================*/

	/*  ===============================
	 *      REQ TRANSACTIONS LIST GUI
	 *  ===============================*/

	public static final ConfigEntry GUI_REQUEST_TRANSACTIONS_ITEMS_FILTER_SLOT = create("gui.request transactions.items.filter.slot", 47, "Valid Slots: 45 - 53");
	public static final ConfigEntry GUI_REQUEST_TRANSACTIONS_ITEMS_FILTER_ITEM = create("gui.request transactions.items.filter.item", "NETHER_STAR");

	public static final ConfigEntry GUI_REQUEST_TRANSACTIONS_ITEMS_ALL_SLOT = create("gui.request transactions.items.all.slot", 51, "Valid Slots: 45 - 53");
	public static final ConfigEntry GUI_REQUEST_TRANSACTIONS_ITEMS_ALL_ITEM = create("gui.request transactions.items.all.item off", CompMaterial.RED_DYE.name());
	public static final ConfigEntry GUI_REQUEST_TRANSACTIONS_ITEMS_ALL_ITEM_ON = create("gui.request transactions.items.all.item on", CompMaterial.LIME_DYE.name());

	/*  ===============================
	 *      TRANSACTIONS LIST GUI
	 *  ===============================*/

	public static final ConfigEntry GUI_TRANSACTIONS_ITEMS_FILTER_SLOT = create("gui.transactions.items.filter.slot", 47, "Valid Slots: 45 - 53");
	public static final ConfigEntry GUI_TRANSACTIONS_ITEMS_FILTER_ITEM = create("gui.transactions.items.filter.item", "NETHER_STAR");

	/*  ===============================
	 *      TRANSACTIONS VIEW GUI
	 *  ===============================*/
	public static final ConfigEntry GUI_TRANSACTION_VIEW_BACKGROUND_FILL = create("gui.transaction view.background.fill", true);
	public static final ConfigEntry GUI_TRANSACTION_VIEW_BACKGROUND_ITEM = create("gui.transaction view.background.item", "BLACK_STAINED_GLASS_PANE");



	public static final ConfigEntry GUI_TRANSACTION_VIEW_ITEM_INFO_ITEM = create("gui.transaction view.items.information.item", "PAPER");

	/*  ===============================
	 *         INSPECTION GUI
	 *  ===============================*/
	public static final ConfigEntry GUI_INSPECT_BG_ITEM = create("gui.inspect.bg item", CompMaterial.BLACK_STAINED_GLASS_PANE.name());

	/*  ===============================
	 *         BANS GUI
	 *  ===============================*/
	public static final ConfigEntry GUI_BANS_BG_ITEM = create("gui.all bans.bg item", CompMaterial.BLACK_STAINED_GLASS_PANE.name());

	public static final ConfigEntry GUI_BAN_BG_ITEM = create("gui.ban.bg item", CompMaterial.BLACK_STAINED_GLASS_PANE.name());



	public static final ConfigEntry GUI_BAN_ITEMS_TYPES_ITEM = create("gui.ban.items.types.item", CompMaterial.COMPARATOR.name());

	public static final ConfigEntry GUI_BAN_ITEMS_PERMA_ITEM = create("gui.ban.items.permanent.item", CompMaterial.LAVA_BUCKET.name());

	public static final ConfigEntry GUI_BAN_ITEMS_REASON_ITEM = create("gui.ban.items.reason.item", CompMaterial.PAPER.name());

	public static final ConfigEntry GUI_BAN_ITEMS_TIME_ITEM = create("gui.ban.items.expiration.item", CompMaterial.CLOCK.name());

	public static final ConfigEntry GUI_BAN_ITEMS_CREATE_ITEM = create("gui.ban.items.create.item", CompMaterial.LIME_DYE.name());

	public static final ConfigEntry GUI_BAN_TYPES_BG_ITEM = create("gui.ban types.bg item", CompMaterial.BLACK_STAINED_GLASS_PANE.name());



	/*  ===============================
	 *         FILTER GUI
	 *  ===============================*/
	public static final ConfigEntry GUI_FILTER_BG_ITEM = create("gui.filter.bg item", CompMaterial.BLACK_STAINED_GLASS_PANE.name());

	public static final ConfigEntry GUI_FILTER_ITEMS_ALL_SLOTS = create("gui.filter.items.all.slots", 12);
	public static final ConfigEntry GUI_FILTER_ITEMS_ALL_ITEM = create("gui.filter.items.all.item", CompMaterial.HOPPER.name());

	public static final ConfigEntry GUI_FILTER_ITEMS_OWN_SLOTS = create("gui.filter.items.own.slots", 13);

	public static final ConfigEntry GUI_FILTER_ITEMS_SEARCH_SLOTS = create("gui.filter.items.search.slots", 14);
	public static final ConfigEntry GUI_FILTER_ITEMS_SEARCH_ITEM = create("gui.filter.items.search.item", CompMaterial.NAME_TAG.name());

	public static final ConfigEntry GUI_FILTER_ITEMS_MISC_SLOTS = create("gui.filter.items.misc.slots", 19);
	public static final ConfigEntry GUI_FILTER_ITEMS_MISC_ITEM = create("gui.filter.items.misc.item", CompMaterial.OAK_SIGN.name());

	public static final ConfigEntry GUI_FILTER_ITEMS_POTIONS_SLOTS = create("gui.filter.items.potions.slots", 31);
	public static final ConfigEntry GUI_FILTER_ITEMS_POTIONS_ITEM = create("gui.filter.items.potions.item", CompMaterial.SPLASH_POTION.name());


	public static final ConfigEntry GUI_FILTER_ITEMS_ENCHANTS_SLOTS = create("gui.filter.items.enchants.slots", 20);
	public static final ConfigEntry GUI_FILTER_ITEMS_ENCHANTS_ITEM = create("gui.filter.items.enchants.item", CompMaterial.ENCHANTED_BOOK.name());

	public static final ConfigEntry GUI_FILTER_ITEMS_ARMOR_SLOTS = create("gui.filter.items.armor.slots", 21);
	public static final ConfigEntry GUI_FILTER_ITEMS_ARMOR_ITEM = create("gui.filter.items.armor.item", CompMaterial.CHAINMAIL_CHESTPLATE.name());

	public static final ConfigEntry GUI_FILTER_ITEMS_WEAPONS_SLOTS = create("gui.filter.items.weapons.slots", 22);
	public static final ConfigEntry GUI_FILTER_ITEMS_WEAPONS_ITEM = create("gui.filter.items.weapons.item", CompMaterial.DIAMOND_SWORD.name());

	public static final ConfigEntry GUI_FILTER_ITEMS_TOOLS_SLOTS = create("gui.filter.items.tools.slots", 23);
	public static final ConfigEntry GUI_FILTER_ITEMS_TOOLS_ITEM = create("gui.filter.items.tools.item", CompMaterial.IRON_PICKAXE.name());

	public static final ConfigEntry GUI_FILTER_ITEMS_SPAWNERS_SLOTS = create("gui.filter.items.spawners.slots", 24);
	public static final ConfigEntry GUI_FILTER_ITEMS_SPAWNERS_ITEM = create("gui.filter.items.spawners.item", CompMaterial.CREEPER_SPAWN_EGG.name());

	public static final ConfigEntry GUI_FILTER_ITEMS_BLOCKS_SLOTS = create("gui.filter.items.blocks.slots", 25);
	public static final ConfigEntry GUI_FILTER_ITEMS_BLOCKS_ITEM = create("gui.filter.items.blocks.item", CompMaterial.GOLD_BLOCK.name());

	/*  ===============================
	 *      CUSTOM ITEM FILTER GUI
	 *  ===============================*/
	public static final ConfigEntry GUI_FILTER_WHITELIST_BG_ITEM = create("gui.filter whitelist.bg item", CompMaterial.BLACK_STAINED_GLASS_PANE.name());

	public static final ConfigEntry GUI_FILTER_WHITELIST_ITEMS_BLOCKS_ITEM = create("gui.filter whitelist.items.blocks.item", CompMaterial.GRASS_BLOCK.name());

	public static final ConfigEntry GUI_FILTER_WHITELIST_ITEMS_FOOD_ITEM = create("gui.filter whitelist.items.food.item", CompMaterial.CAKE.name());

	public static final ConfigEntry GUI_FILTER_WHITELIST_ITEMS_ARMOR_ITEM = create("gui.filter whitelist.items.armor.item", CompMaterial.DIAMOND_HELMET.name());

	public static final ConfigEntry GUI_FILTER_WHITELIST_ITEMS_TOOLS_ITEM = create("gui.filter whitelist.items.tools.item", CompMaterial.IRON_PICKAXE.name());

	public static final ConfigEntry GUI_FILTER_WHITELIST_ITEMS_SPAWNERS_ITEM = create("gui.filter whitelist.items.spawners.item", CompMaterial.SPAWNER.name());

	public static final ConfigEntry GUI_FILTER_WHITELIST_ITEMS_ENCHANTS_ITEM = create("gui.filter whitelist.items.enchants.item", CompMaterial.ENCHANTED_BOOK.name());

	public static final ConfigEntry GUI_FILTER_WHITELIST_ITEMS_WEAPONS_ITEM = create("gui.filter whitelist.items.weapons.item", CompMaterial.DIAMOND_SWORD.name());

	public static final ConfigEntry GUI_FILTER_WHITELIST_ITEMS_POTIONS_ITEM = create("gui.filter whitelist.items.potions.item", CompMaterial.SPLASH_POTION.name());


	public static final ConfigEntry GUI_FILTER_WHITELIST_ITEMS_MISC_ITEM = create("gui.filter whitelist.items.misc.item", CompMaterial.BONE_MEAL.name());

	/*  ===============================
	 *      CUSTOM ITEM FILTER GUI
	 *  ===============================*/
	public static final ConfigEntry GUI_FILTER_WHITELIST_LIST_BG_ITEM = create("gui.filter whitelist list.bg item", CompMaterial.BLACK_STAINED_GLASS_PANE.name());

	/*  ===============================
	 *    ITEM SELL LISTING TYPE GUI
	 *  ===============================*/
	public static final ConfigEntry GUI_SELL_LISTING_TYPE_BG_ITEM = create("gui.sell listing type.bg item", CompMaterial.BLACK_STAINED_GLASS_PANE.name());

	public static final ConfigEntry GUI_SELL_LISTING_TYPE_ITEMS_BIN_ITEM = create("gui.sell listing type.items.bin.item", CompMaterial.SUNFLOWER.name());

	public static final ConfigEntry GUI_SELL_LISTING_TYPE_ITEMS_AUCTION_ITEM = create("gui.sell listing type.items.auction.item", CompMaterial.DIAMOND.name());

	public static final ConfigEntry GUI_SELL_LISTING_TYPE_ITEMS_RETURN_ITEM = create("gui.sell listing type.items.return.item", CompMaterial.BARRIER.name());

	/*  ===============================
	 *    ITEM SELL PLACE ITEM GUI
	 *  ===============================*/
	public static final ConfigEntry GUI_SELL_PLACE_ITEM_BG_ITEM = create("gui.sell place item.bg item", CompMaterial.BLACK_STAINED_GLASS_PANE.name());

	public static final ConfigEntry GUI_SELL_PLACE_ITEM_ITEMS_CONTINUE_ITEM = create("gui.sell place item.items.continue.item", CompMaterial.LIME_STAINED_GLASS_PANE.name());


	public static final ConfigEntry GUI_SELL_PLACE_ITEM_ITEMS_BUNDLE_ITEM = create("gui.sell place item.items.bundle.item", CompMaterial.GOLD_BLOCK.name());

	public static final ConfigEntry GUI_SELL_PLACE_ITEM_ITEMS_SINGLE_ITEM = create("gui.sell place item.items.single.item", CompMaterial.DIAMOND.name());


	/*  ===============================
	 *    ITEM SELL BIN GUI
	 *  ===============================*/

	public static final ConfigEntry GUI_SELL_ITEM_ITEM_CURRENCY_ITEM = create("gui.global items.currency.item", CompMaterial.GOLD_INGOT.name());

	public static final ConfigEntry GUI_SELL_BIN_BG_ITEM = create("gui.sell bin item.bg item", CompMaterial.BLACK_STAINED_GLASS_PANE.name());

	public static final ConfigEntry GUI_SELL_BIN_ITEM_ITEMS_CONTINUE_ITEM = create("gui.sell bin item.items.confirm.item", CompMaterial.LIME_STAINED_GLASS_PANE.name());

	public static final ConfigEntry GUI_SELL_BIN_ITEM_ITEMS_FEE_ITEM = create("gui.sell bin item.items.fee.item", "https://textures.minecraft.net/texture/a4e1da882e434829b96ec8ef242a384a53d89018fa65fee5b37deb04eccbf10e");

	public static final ConfigEntry GUI_SELL_BIN_ITEM_ITEMS_TIME_ITEM = create("gui.sell bin item.items.time.item", CompMaterial.CLOCK.name());

	public static final ConfigEntry GUI_SELL_BIN_ITEM_ITEMS_PRICE_ITEM = create("gui.sell bin item.items.price.item", CompMaterial.DIAMOND.name());

	public static final ConfigEntry GUI_SELL_BIN_ITEM_ITEMS_PARTIAL_ENABLED_ITEM = create("gui.sell bin item.items.partial enabled.item", CompMaterial.LIME_STAINED_GLASS_PANE.name());

	public static final ConfigEntry GUI_SELL_BIN_ITEM_ITEMS_PARTIAL_DISABLED_ITEM = create("gui.sell bin item.items.partial disabled.item", CompMaterial.RED_STAINED_GLASS_PANE.name());


	/*  ===============================
	 *    REQUEST ITEM GUI
	 *  ===============================*/

	public static final ConfigEntry GUI_REQUEST_ITEMS_AMT_ITEM = create("gui.request.items.amt.item", CompMaterial.REPEATER.name());

	public static final ConfigEntry GUI_REQUEST_ITEMS_PRICE_ITEM = create("gui.request.items.price.item", CompMaterial.SUNFLOWER.name());

	public static final ConfigEntry GUI_REQUEST_ITEMS_REQUEST_ITEM = create("gui.request.items.request.item", CompMaterial.LIME_STAINED_GLASS_PANE.name());



	/*  ===============================
	 *    ITEM SELL AUCTION GUI
	 *  ===============================*/

	public static final ConfigEntry GUI_SELL_AUCTION_BG_ITEM = create("gui.sell auction item.bg item", CompMaterial.BLACK_STAINED_GLASS_PANE.name());

	public static final ConfigEntry GUI_SELL_AUCTION_ITEM_ITEMS_TIME_ITEM = create("gui.sell auction item.items.time.item", CompMaterial.CLOCK.name());

	public static final ConfigEntry GUI_SELL_AUCTION_ITEM_ITEMS_FEE_ITEM = create("gui.sell auction item.items.fee.item", "https://textures.minecraft.net/texture/a4e1da882e434829b96ec8ef242a384a53d89018fa65fee5b37deb04eccbf10e");

	public static final ConfigEntry GUI_SELL_AUCTION_ITEM_ITEMS_BUYOUT_PRICE_ITEM = create("gui.sell auction item.items.bin price.item", CompMaterial.DIAMOND.name());

	public static final ConfigEntry GUI_SELL_AUCTION_ITEM_ITEMS_STARTING_PRICE_ITEM = create("gui.sell auction item.items.starting price.item", CompMaterial.DIAMOND.name());

	public static final ConfigEntry GUI_SELL_AUCTION_ITEM_ITEMS_INCREMENT_PRICE_ITEM = create("gui.sell auction item.items.increment price.item", CompMaterial.DIAMOND.name());

	public static final ConfigEntry GUI_SELL_AUCTION_ITEM_ITEMS_BUYOUT_ENABLED_ITEM = create("gui.sell auction item.items.buyout enabled.item", CompMaterial.LIME_STAINED_GLASS_PANE.name());

	public static final ConfigEntry GUI_SELL_AUCTION_ITEM_ITEMS_BUYOUT_DISABLED_ITEM = create("gui.sell auction item.items.buyout disabled.item", CompMaterial.RED_STAINED_GLASS_PANE.name());

	public static final ConfigEntry GUI_SELL_AUCTION_ITEM_ITEMS_CONTINUE_ITEM = create("gui.sell auction item.items.confirm.item", CompMaterial.LIME_STAINED_GLASS_PANE.name());

	/*  ===============================
	 *         AH STATS GUI
	 *  ===============================*/
	public static final ConfigEntry GUI_STATS_VIEW_SELECT_BG_ITEM = create("gui.stat view select.bg item", CompMaterial.BLACK_STAINED_GLASS_PANE.name());

	public static final ConfigEntry GUI_STATS_VIEW_SELECT_ITEMS_PERSONAL_USE_HEAD = create("gui.stat view select.items.personal.use head", true);
	public static final ConfigEntry GUI_STATS_VIEW_SELECT_ITEMS_PERSONAL_ITEM = create("gui.stat view select.items.personal.item", CompMaterial.DIAMOND.name());

	public static final ConfigEntry GUI_STATS_VIEW_SELECT_ITEMS_LEADERBOARD_ITEM = create("gui.stat view select.items.leaderboard.item", CompMaterial.NETHER_STAR.name());

	public static final ConfigEntry GUI_STATS_SELF_BG_ITEM = create("gui.stat view self.bg item", CompMaterial.BLACK_STAINED_GLASS_PANE.name());

	public static final ConfigEntry GUI_STATS_SELF_ITEMS_CREATED_AUCTION_ITEM = create("gui.stat view self.items.created auction.item", CompMaterial.DIAMOND.name());

	public static final ConfigEntry GUI_STATS_SELF_ITEMS_CREATED_BIN_ITEM = create("gui.stat view self.items.created bin.item", CompMaterial.HOPPER_MINECART.name());

	public static final ConfigEntry GUI_STATS_SELF_ITEMS_SOLD_AUCTION_ITEM = create("gui.stat view self.items.sold auction.item", CompMaterial.LADDER.name());

	public static final ConfigEntry GUI_STATS_SELF_ITEMS_SOLD_BIN_ITEM = create("gui.stat view self.items.sold bin.item", CompMaterial.CHEST.name());

	public static final ConfigEntry GUI_STATS_SELF_ITEMS_MONEY_EARNED_ITEM = create("gui.stat view self.items.money earned.item", CompMaterial.LIME_STAINED_GLASS_PANE.name());

	public static final ConfigEntry GUI_STATS_SELF_ITEMS_MONEY_SPENT_ITEM = create("gui.stat view self.items.money spent.item", CompMaterial.RED_STAINED_GLASS_PANE.name());

	public static final ConfigEntry GUI_STATS_LEADERBOARD_BG_ITEM = create("gui.stat view leaderboard.bg item", CompMaterial.BLACK_STAINED_GLASS_PANE.name());


	public static final ConfigEntry GUI_STATS_LEADERBOARD_ITEMS_STAT_ITEM = create("gui.stat view leaderboard.items.stat.item", CompMaterial.NETHER_STAR.name());

	// other player
	public static final ConfigEntry GUI_STATS_SEARCH_BG_ITEM = create("gui.stat view other.bg item", CompMaterial.BLACK_STAINED_GLASS_PANE.name());

	public static final ConfigEntry GUI_STATS_SEARCH_ITEMS_CREATED_AUCTION_ITEM = create("gui.stat view other.items.created auction.item", CompMaterial.DIAMOND.name());

	public static final ConfigEntry GUI_STATS_SEARCH_ITEMS_CREATED_BIN_ITEM = create("gui.stat view other.items.created bin.item", CompMaterial.HOPPER_MINECART.name());

	public static final ConfigEntry GUI_STATS_SEARCH_ITEMS_SOLD_AUCTION_ITEM = create("gui.stat view other.items.sold auction.item", CompMaterial.LADDER.name());

	public static final ConfigEntry GUI_STATS_SEARCH_ITEMS_SOLD_BIN_ITEM = create("gui.stat view other.items.sold bin.item", CompMaterial.CHEST.name());

	public static final ConfigEntry GUI_STATS_SEARCH_ITEMS_MONEY_EARNED_ITEM = create("gui.stat view other.items.money earned.item", CompMaterial.LIME_STAINED_GLASS_PANE.name());

	public static final ConfigEntry GUI_STATS_SEARCH_ITEMS_MONEY_SPENT_ITEM = create("gui.stat view other.items.money spent.item", CompMaterial.RED_STAINED_GLASS_PANE.name());


	/*  ===============================
	 *       EXPIRED ITEMS ADMIN GUI
	 *  ===============================*/
	public static final ConfigEntry GUI_EXPIRED_ITEMS_ADMIN_BG_ITEM = create("gui.expired items admin.bg item", CompMaterial.BLACK_STAINED_GLASS_PANE.name());

	/*  ===============================
	 *         ITEM ADMIN GUI
	 *  ===============================*/
	public static final ConfigEntry GUI_ITEM_ADMIN_BG_ITEM = create("gui.item admin.bg item", CompMaterial.BLACK_STAINED_GLASS_PANE.name());

	public static final ConfigEntry GUI_ITEM_ADMIN_ITEMS_RETURN_ITEM = create("gui.item admin.items.send to player.item", CompMaterial.ENDER_CHEST.name());

	public static final ConfigEntry GUI_ITEM_ADMIN_ITEMS_CLAIM_ITEM = create("gui.item admin.items.claim item.item", CompMaterial.HOPPER.name());

	public static final ConfigEntry GUI_ITEM_ADMIN_ITEMS_DELETE_ITEM = create("gui.item admin.items.delete item.item", CompMaterial.BARRIER.name());

	public static final ConfigEntry GUI_ITEM_ADMIN_ITEMS_COPY_ITEM = create("gui.item admin.items.copy item.item", CompMaterial.REPEATER.name());

	/*  ===============================
	 *         BIDDING GUI
	 *  ===============================*/
	public static final ConfigEntry GUI_BIDDING_BG_ITEM = create("gui.bidding.bg item", CompMaterial.BLACK_STAINED_GLASS_PANE.name());

	public static final ConfigEntry GUI_BIDDING_ITEMS_DEFAULT_ITEM = create("gui.bidding.items.default amount.item", CompMaterial.SUNFLOWER.name());

	public static final ConfigEntry GUI_BIDDING_ITEMS_CUSTOM_ITEM = create("gui.bidding.items.custom amount.item", CompMaterial.OAK_SIGN.name());

	/*  ===============================
	 *         BUNDLES GUI
	 *  ===============================*/
	public static final ConfigEntry GUI_CREATE_BUNDLE_CONFIRM_ITEM = create("gui.create bundle.items.confirm.item", CompMaterial.LIME_STAINED_GLASS_PANE.name());

	/*  ===============================
	 *         AUCTION STACKS (layout tokens only; lore lines live in language files)
	 *  ===============================*/

	public static final ConfigEntry AUCTION_STACK_INFO_LAYOUT = create("auction stack.info layout", Arrays.asList(
			"%original_item_lore%",
			"%header%",
			"%seller%",
			"%highest_bidder%",
			"",
			"%buy_now_price%",
			"%current_price%",
			"%bid_increment%",
			"",
			"%listing_time%",
			"%listing_priority%",
			"%listing_watched%",
			"%controls_header%",
			"%controls%",
			"%controls_footer%"
	), "The info order for the stacks, if a listing doesnt require one of these, Auction House will just ignore it.", "This is mainly used to just change the ordering of listing stack information");


	/*  ===============================
	 *         AUCTION SOUNDS
	 *  ===============================*/
	public static final ConfigEntry SOUNDS_LISTED_ITEM_ON_AUCTION_HOUSE = create("sounds.listed item on the auction house", CompSound.ENTITY_EXPERIENCE_ORB_PICKUP.friendlyName());
	public static final ConfigEntry SOUNDS_NAVIGATE_GUI_PAGES = create("sounds.navigated between gui pages", CompSound.ENTITY_BAT_TAKEOFF.friendlyName());
	public static final ConfigEntry SOUNDS_NOT_ENOUGH_MONEY = create("sounds.not enough money", CompSound.ENTITY_ITEM_BREAK.friendlyName());
	public static final ConfigEntry SOUNDS_PURCHASE_SUCCESS = create("sounds.purchase success", CompSound.ENTITY_EXPERIENCE_ORB_PICKUP.friendlyName());
	public static final ConfigEntry SOUNDS_ITEM_SOLD = create("sounds.item sold", CompSound.BLOCK_NOTE_BLOCK_PLING.friendlyName());
	public static final ConfigEntry SOUNDS_GUI_CLICK = create("sounds.gui click", CompSound.UI_BUTTON_CLICK.friendlyName());

	/*  ===============================
	 *      TRANSACTION LOGGING
	 *  ===============================*/
	public static final ConfigEntry TRANSACTION_LOGGING_ENABLED = create("settings.transaction logging.enabled", true, "If true, all transactional actions will be logged to daily-rotated log files");
	public static final ConfigEntry TRANSACTION_LOGGING_RETENTION_DAYS = create("settings.transaction logging.retention days", 30, "How many days to keep transaction log files before automatic cleanup (0 = never cleanup)");

	public static void init() {
		ca.tweetzy.flight.FlightPlugin.getCoreConfig().init();
	}

	public static long asLong(ConfigEntry entry) {
		Object v = entry.get();
		if (v == null) {
			return 0;
		}
		if (v instanceof Number number) {
			return number.longValue();
		}
		return Double.valueOf(v.toString()).longValue();
	}
}
