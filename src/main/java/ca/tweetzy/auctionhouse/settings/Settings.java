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
import ca.tweetzy.core.configuration.ConfigSetting;
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
public class Settings {

	static final Config config = AuctionHouse.getInstance().getCoreConfig();

	public static final ConfigSetting LANG = new ConfigSetting(config, "lang", "en_US", "Default language file");
	public static final ConfigSetting HIDE_THANKYOU = new ConfigSetting(config, "hide thank you", false, "Hides the purchase thank you message in the console.");

	public static final ConfigSetting CURRENCY_ALLOW_PICK = new ConfigSetting(config, "economy.currency.allow pick", true, "If true, players will be able to select which currency they want to use.");
	public static final ConfigSetting CURRENCY_ALLOW_CUSTOM = new ConfigSetting(config, "economy.currency.allow custom item", true, "If true, players will be able to provide a custom item as the currency");
	public static final ConfigSetting CURRENCY_LIMIT_TO_PERMISSION = new ConfigSetting(config, "economy.currency.limit to permission", false, "If true, currencies will be limited by permission. Example auctionhouse.currency.ultraeconomy_gems will allow usage of the gems currency from ultra economy");
	public static final ConfigSetting CURRENCY_DEFAULT_SELECTED = new ConfigSetting(config, "economy.currency.default selection", "Vault/Vault", "The default currency selection, PluginName/CurrencyName -> Ex. Vault/Vault or UltraEconomy/Gems etc");
	public static final ConfigSetting CURRENCY_VAULT_SYMBOL = new ConfigSetting(config, "economy.currency.vault symbol", "$", "When using default/vault currency, what symbol should be used.");
	public static final ConfigSetting CURRENCY_VAULT_SYMBOL_OVERRIDES = new ConfigSetting(config, "economy.currency.vault symbol overrides", false, "If true, the vault symbol will override the symbol provided by the country/language combination");
	public static final ConfigSetting CURRENCY_BLACKLISTED = new ConfigSetting(config, "economy.currency.black listed", Collections.singletonList("UltraEconomy:Test"), "A list of owning plugins & the currency to be blacklisted. Ex. UltraEconomy:Test");
	public static final ConfigSetting CURRENCY_FORMAT_LANGUAGE = new ConfigSetting(config, "economy.currency.format.language", "en", "An ISO 639 alpha-2 or alpha-3 language code.");
	public static final ConfigSetting CURRENCY_FORMAT_COUNTRY = new ConfigSetting(config, "economy.currency.format.country", "US", "An ISO 3166 alpha-2 country code or a UN M.49 numeric-3 area code.");
	public static final ConfigSetting CURRENCY_ABBREVIATE_NUMBERS = new ConfigSetting(config, "economy.currency.abbreviate numbers", false, "Should numbers be abbreviated?. Example: 123,000 will become 123k ");
	public static final ConfigSetting CURRENCY_HIDE_VAULT_SYMBOL = new ConfigSetting(config, "economy.currency.hide vault symbol", false, "Should the specified vault symbol be hidden?");
	public static final ConfigSetting CURRENCY_STRIP_ENDING_ZEROES = new ConfigSetting(config, "economy.currency.strip ending zeroes", false, "If the number ends with 00, should it be stripped. EX 123.00 becomes 123");
	public static final ConfigSetting CURRENCY_TIGHT_CURRENCY_SYMBOL = new ConfigSetting(config, "economy.currency.tight currency symbol", false, "If true, the space between the currency symbol and number will be removed");
	public static final ConfigSetting CURRENCY_USE_GROUPING = new ConfigSetting(config, "economy.currency.use grouping", true, "If false, number grouping will be disabled. Ex. 123,456.78 becomes 123456.78");
	public static final ConfigSetting CURRENCY_REMOVE_SPACE_FROM_CUSTOM = new ConfigSetting(config, "economy.currency.hide space between in currency", false, "If true, if the currency has a custom display name is will go from 123 currency to 123currency/symbol");

	public static final ConfigSetting CMD_ALIAS_MAIN = new ConfigSetting(config, "command aliases.main", Arrays.asList("ah", "auctions", "auctionhouses", "ahgui", "auctiongui"), "Command aliases for the main command");
	public static final ConfigSetting CMD_ALIAS_SUB_ACTIVE = new ConfigSetting(config, "command aliases.subcommands.active", Collections.singletonList("active"), "Command aliases for the active command");
	public static final ConfigSetting CMD_ALIAS_SUB_CART = new ConfigSetting(config, "command aliases.subcommands.cart", Collections.singletonList("cart"), "Command aliases for the cart command");
	public static final ConfigSetting CMD_ALIAS_SUB_ADMIN = new ConfigSetting(config, "command aliases.subcommands.admin", Collections.singletonList("admin"), "Command aliases for the admin command");
	public static final ConfigSetting CMD_ALIAS_SUB_BAN = new ConfigSetting(config, "command aliases.subcommands.ban", Collections.singletonList("ban"), "Command aliases for the ban command");
	public static final ConfigSetting CMD_ALIAS_SUB_BIDS = new ConfigSetting(config, "command aliases.subcommands.bids", Collections.singletonList("bids"), "Command aliases for the bids command");
	public static final ConfigSetting CMD_ALIAS_SUB_CONFIRM = new ConfigSetting(config, "command aliases.subcommands.confirm", Collections.singletonList("confirm"), "Command aliases for the confirm command");
	public static final ConfigSetting CMD_ALIAS_SUB_EXPIRED = new ConfigSetting(config, "command aliases.subcommands.expired", Collections.singletonList("expired"), "Command aliases for the expired command");
	public static final ConfigSetting CMD_ALIAS_SUB_FILTER = new ConfigSetting(config, "command aliases.subcommands.filter", Collections.singletonList("filter"), "Command aliases for the filter command");
	public static final ConfigSetting CMD_ALIAS_SUB_MARKCHEST = new ConfigSetting(config, "command aliases.subcommands.markchest", Collections.singletonList("markchest"), "Command aliases for the markchest command");
	public static final ConfigSetting CMD_ALIAS_SUB_PRICE_LIMIT = new ConfigSetting(config, "command aliases.subcommands.price limit", Collections.singletonList("pricelimit"), "Command aliases for the price limits command, formally min prices");
	public static final ConfigSetting CMD_ALIAS_SUB_PAYMENTS = new ConfigSetting(config, "command aliases.subcommands.payments", Collections.singletonList("payments"), "Command aliases for the payments command");
	public static final ConfigSetting CMD_ALIAS_SUB_REQUEST = new ConfigSetting(config, "command aliases.subcommands.request", Collections.singletonList("request"), "Command aliases for the request command");
	public static final ConfigSetting CMD_ALIAS_SUB_SEARCH = new ConfigSetting(config, "command aliases.subcommands.search", Collections.singletonList("search"), "Command aliases for the search command");
	public static final ConfigSetting CMD_ALIAS_SUB_SELL = new ConfigSetting(config, "command aliases.subcommands.sell", Collections.singletonList("sell"), "Command aliases for the sell command");
	public static final ConfigSetting CMD_ALIAS_SUB_STATS = new ConfigSetting(config, "command aliases.subcommands.stats", Collections.singletonList("stats"), "Command aliases for the stats command");
	public static final ConfigSetting CMD_ALIAS_SUB_TOGGLELISTINFO = new ConfigSetting(config, "command aliases.subcommands.togglelistinfo", Collections.singletonList("togglelistinfo"), "Command aliases for the toggle list info command");
	public static final ConfigSetting CMD_ALIAS_SUB_TRANSACTIONS = new ConfigSetting(config, "command aliases.subcommands.transactions", Collections.singletonList("transactions"), "Command aliases for the transactions command");
	public static final ConfigSetting CMD_ALIAS_SUB_UNBAN = new ConfigSetting(config, "command aliases.subcommands.unban", Collections.singletonList("unban"), "Command aliases for the unban command");

	public static final ConfigSetting CMD_ERROR_DESC = new ConfigSetting(config, "command info.error information", Arrays.asList(
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
	), "The msg that is shown when the command syntax is wrong");


	public static final ConfigSetting CMD_FLAG_ALIAS_SELL_BUNDLE = new ConfigSetting(config, "command flags.sell command.bundle", Arrays.asList("-b", "-bundle"), "Aliases for the bundle command flag in the sell command");
	public static final ConfigSetting CMD_FLAG_ALIAS_SELL_PARTIAL_BUY = new ConfigSetting(config, "command flags.sell command.partial buy", Arrays.asList("-p", "-partialbuy"), "Aliases for the partial buy command flag in the sell command");
	public static final ConfigSetting CMD_FLAG_ALIAS_SELL_STACK_PRICE = new ConfigSetting(config, "command flags.sell command.stack price", Arrays.asList("-s", "-stack"), "Aliases for the stack price flag in the sell command");
	public static final ConfigSetting CMD_FLAG_ALIAS_SELL_INFINITE = new ConfigSetting(config, "command flags.sell command.infinite", Arrays.asList("-i", "-infinite"), "Aliases for the infinite flag in the sell command");
	public static final ConfigSetting CMD_FLAG_ALIAS_SELL_SERVER = new ConfigSetting(config, "command flags.sell command.server", Arrays.asList("-server"), "Aliases for the server flag in the sell command");
	public static final ConfigSetting CMD_FLAG_ALIAS_SELL_TIME = new ConfigSetting(config, "command flags.sell command.time", Arrays.asList("-t"), "Aliases for the time flag in the sell command");
	public static final ConfigSetting CMD_FLAG_ALIAS_SELL_SINGLE = new ConfigSetting(config, "command flags.sell command.single", Arrays.asList("-one"), "Aliases for the single item flag in the sell command");

	public static final ConfigSetting TIME_ALIAS_YEAR = new ConfigSetting(config, "time aliases.year", Arrays.asList("y", "year", "years"), "Time aliases for year, Must be in lowercase.");
	public static final ConfigSetting TIME_ALIAS_MONTH = new ConfigSetting(config, "time aliases.month", Arrays.asList("mo", "month", "months"), "Time aliases for month, Must be in lowercase.");
	public static final ConfigSetting TIME_ALIAS_WEEK = new ConfigSetting(config, "time aliases.week", Arrays.asList("w", "week", "weeks"), "Time aliases for week, Must be in lowercase.");
	public static final ConfigSetting TIME_ALIAS_DAY = new ConfigSetting(config, "time aliases.day", Arrays.asList("d", "day", "days"), "Time aliases for day, Must be in lowercase.");
	public static final ConfigSetting TIME_ALIAS_HOUR = new ConfigSetting(config, "time aliases.hour", Arrays.asList("h", "hour", "hours"), "Time aliases for hour, Must be in lowercase.");
	public static final ConfigSetting TIME_ALIAS_MINUTE = new ConfigSetting(config, "time aliases.minute", Arrays.asList("min", "minute", "minutes"), "Time aliases for minute, Must be in lowercase.");
	public static final ConfigSetting TIME_ALIAS_SECOND = new ConfigSetting(config, "time aliases.second", Arrays.asList("s", "second", "seconds"), "Time aliases for second, Must be in lowercase.");

	public static final ConfigSetting ALLOW_USAGE_OF_IN_GAME_EDITOR = new ConfigSetting(config, "Allow Usage Of This Menu In Game", true, "Once you set this to true, you will no longer be able to access it unless you enable it within the actual config.yml");
	public static final ConfigSetting UPDATE_CHECKER = new ConfigSetting(config, "update checker", true, "If true, auction house will check for updates periodically");

	public static final ConfigSetting DATE_FORMAT = new ConfigSetting(config, "auction setting.date format", "MMM dd, yyyy hh:mm aa", "You can learn more about date formats by googling SimpleDateFormat patterns or visiting this link", "https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html");
	public static final ConfigSetting TIMEZONE = new ConfigSetting(config, "auction setting.timezone", "America/Toronto", "Ensure this is correct as features like the access hours will use this timezone. https://timezonedb.com/time-zones");
	public static final ConfigSetting PACKET_NAMESPACE_KEYS = new ConfigSetting(config, "auction setting.packet.namespaced keys", Arrays.asList("ecoitems", "ecoarmor"), "Namespaced keys of plugins using packet lore");

	public static final ConfigSetting CART_SYSTEM_ENABLED = new ConfigSetting(config, "auction setting.cart system.enabled", false, "Should auction house allow the cart system?");
	public static final ConfigSetting USE_NAMES_FOR_CHECKS = new ConfigSetting(config, "auction setting.experimental.use names for checks", false, "Do not touch this unless you have a good reason too?");


	/*  ===============================
	 *          BASIC SETTINGS
	 *  ===============================*/

	//	Listing Priority
	public static final ConfigSetting LISTING_PRIORITY_ENABLED = new ConfigSetting(config, "auction setting.listing priority.enabled", true, "If true, players will be able to pay to prioritize listings");
	public static final ConfigSetting LISTING_PRIORITY_TIME_PER_BOOST = new ConfigSetting(config, "auction setting.listing priority.time per boost", 60 * 30, "How many seconds should the priority last for each time they pay", "By default users will be able to stack boosts");
	public static final ConfigSetting LISTING_PRIORITY_TIME_ALLOW_MULTI_BOOST = new ConfigSetting(config, "auction setting.listing priority.allow multiple boost", false, "If true players can boost an item multiple times before it runs out. (ex. if they have a boost active they can extend by paying before it expires)");
	public static final ConfigSetting LISTING_PRIORITY_TIME_COST_PER_BOOST = new ConfigSetting(config, "auction setting.listing priority.cost per boost", 1000, "How much should it cost the player to boost their item each time");

	// Timed Usage
	public static final ConfigSetting TIMED_USAGE_ENABLED = new ConfigSetting(config, "auction setting.access hours.use access hours", false, "If true, the auction house will be only accessible");
	public static final ConfigSetting TIMED_USAGE_RANGE = new ConfigSetting(config, "auction setting.access hours.access hours", Collections.singletonList(
			"00:00:00-23:59:59"
	), "The hours in 24hr format which the auction house can be used.", "The times use the specified timezone ");


	public static final ConfigSetting SHOW_LISTING_ERROR_IN_CONSOLE = new ConfigSetting(config, "auction setting.show listing error in console", false, "If true, an exception will be thrown and shown in the console if something goes wrong during item listing");
	public static final ConfigSetting STORE_PAYMENTS_FOR_MANUAL_COLLECTION = new ConfigSetting(config, "auction setting.store payments for manual collection", false, "If true, auction house will store the payments to be manually collected rather than automatically given to the player");
	public static final ConfigSetting MANUAL_PAYMENTS_ONLY_FOR_OFFLINE_USERS = new ConfigSetting(config, "auction setting.use stored payments for offline only", false, "If true, the usage of the manual payment collection will only be done if the user is offline");
	public static final ConfigSetting ALLOW_REPEAT_BIDS = new ConfigSetting(config, "auction setting.allow repeated bids", true, "If true, the highest bidder on an item can keep placing bids to raise their initial bid.");
	public static final ConfigSetting COLLECTION_BIN_ITEM_LIMIT = new ConfigSetting(config, "auction setting.collection bin item limit", 45, "How many items can be stored in the collection bin. If this is reached the player cannot list anymore items, regardless of active listings");
	public static final ConfigSetting SELL_MENU_SKIPS_TYPE_SELECTION = new ConfigSetting(config, "auction setting.skip type selection for sell menu", false, "If true the sell menu process will skip asking for the listing type depending on your auction settings (ie. bin only or auction only)");

	public static final ConfigSetting BUNDLE_LIST_LIMIT = new ConfigSetting(config, "auction setting.bundle listing limit.listing limit", 45, "How many bundled listings can a player sell at any given time");
	public static final ConfigSetting BUNDLE_LIST_LIMIT_INCLUDE_COLLECTION_BIN = new ConfigSetting(config, "auction setting.bundle listing limit.include collection bin", false, "If true, collection bin bundles will also count towards this limit");

	public static final ConfigSetting DEFAULT_BIN_LISTING_TIME = new ConfigSetting(config, "auction setting.listings times.bin item", 86400, "The default listing time for bin items (buy only items) before they expire");
	public static final ConfigSetting DEFAULT_AUCTION_LISTING_TIME = new ConfigSetting(config, "auction setting.listings times.auction item", 604800, "The default listing time for auction items before they expire");

	public static final ConfigSetting DEFAULT_FILTER_CATEGORY = new ConfigSetting(config, "auction setting.default filters.auction category", "ALL", "Valid Options: ALL, FOOD, ARMOR, BLOCKS, TOOLS, WEAPONS, POTIONS, SPAWNERS, ENCHANTS, MISC, SEARCH, SELF");
	public static final ConfigSetting DEFAULT_FILTER_SORT = new ConfigSetting(config, "auction setting.default filters.auction sort", "RECENT", "Valid Options: RECENT, OLDEST, PRICE");
	public static final ConfigSetting DEFAULT_FILTER_SALE_TYPE = new ConfigSetting(config, "auction setting.default filters.sale type", "BOTH", "Valid Options: USED_BIDDING_SYSTEM, WITHOUT_BIDDING_SYSTEM, BOTH");
	public static final ConfigSetting ENABLE_FILTER_SYSTEM = new ConfigSetting(config, "auction setting.use filter system", true, "If false, auction house will disable the filter button.");

	public static final ConfigSetting FILTER_CLICKS_SORT_PRICE_RECENT_ENABLED = new ConfigSetting(config, "auction setting.default filters.enabled clicks.sort by price or recent", true, "If false, the click action for this filter option will not work.");
	public static final ConfigSetting FILTER_CLICKS_RESET_ENABLED = new ConfigSetting(config, "auction setting.default filters.enabled clicks.reset", true, "If false, the click action for this filter option will not work.");
	public static final ConfigSetting FILTER_CLICKS_LISTING_CURRENCY_ENABLED = new ConfigSetting(config, "auction setting.default filters.enabled clicks.listing currency", true, "If false, the click action for this filter option will not work.");
	public static final ConfigSetting FILTER_CLICKS_SALE_TYPE_ENABLED = new ConfigSetting(config, "auction setting.default filters.enabled clicks.sort sale type", true, "If false, the click action for this filter option will not work.");
	public static final ConfigSetting FILTER_CLICKS_CHANGE_CATEGORY_ENABLED = new ConfigSetting(config, "auction setting.default filters.enabled clicks.change category", true, "If false, the click action for this filter option will not work.");
	public static final ConfigSetting FILTER_CLICKS_TRANSACTION_BUY_TYPE_ENABLED = new ConfigSetting(config, "auction setting.default filters.enabled clicks.transaction buy type", true, "If false, the click action for this filter option will not work.");
	public static final ConfigSetting FILTER_DONT_REMEMBER = new ConfigSetting(config, "auction setting.default filters.do not save filter setting", false, "If true if you close the auction house your filter options will reset back to default");

	public static final ConfigSetting INTERNAL_CREATE_DELAY = new ConfigSetting(config, "auction setting.internal create delay", 2, "How many ticks should auction house wait before actually creating the item.");
	public static final ConfigSetting MAX_AUCTION_PRICE = new ConfigSetting(config, "auction setting.pricing.max auction price", 1000000000, "The max price for buy only / buy now items");
	public static final ConfigSetting MAX_AUCTION_START_PRICE = new ConfigSetting(config, "auction setting.pricing.max auction start price", 1000000000, "The max price starting a bidding auction");
	public static final ConfigSetting MAX_AUCTION_INCREMENT_PRICE = new ConfigSetting(config, "auction setting.pricing.max auction increment price", 1000000000, "The max amount for incrementing a bid.");
	public static final ConfigSetting MIN_AUCTION_PRICE = new ConfigSetting(config, "auction setting.pricing.min auction price", 1, "The min price for buy only / buy now items");
	public static final ConfigSetting MIN_AUCTION_START_PRICE = new ConfigSetting(config, "auction setting.pricing.min auction start price", 1, "The min price starting a bidding auction");
	public static final ConfigSetting MIN_AUCTION_INCREMENT_PRICE = new ConfigSetting(config, "auction setting.pricing.min auction increment price", 1, "The min amount for incrementing a bid.");
	public static final ConfigSetting OWNER_CAN_PURCHASE_OWN_ITEM = new ConfigSetting(config, "auction setting.purchase.owner can purchase own item", false, "Should the owner of an auction be able to purchase it?", "This probably should be set to false...");
	public static final ConfigSetting OWNER_CAN_BID_OWN_ITEM = new ConfigSetting(config, "auction setting.purchase.owner can bid on own item", false, "Should the owner of an auction be able to bid on it?", "This probably should be set to false...");
	public static final ConfigSetting OWNER_CAN_FULFILL_OWN_REQUEST = new ConfigSetting(config, "auction setting.purchase.owner can fulfill own request", false, "Should the owner of a request be able to fulfill it", "This probably should be set to false...");
	public static final ConfigSetting MAX_REQUEST_AMOUNT = new ConfigSetting(config, "auction setting.max request amount", 64, "How much of an item should a player be able to ask for in a single request?");
	public static final ConfigSetting BLOCK_REQUEST_USING_FILLED_SHULKER = new ConfigSetting(config, "auction setting.block requests using filled shulkers", true, "If false, players can request make a request using a shulker that contains items");
	public static final ConfigSetting MIN_REQUEST_PRICE = new ConfigSetting(config, "auction setting.pricing.min request price", 1, "The minimum price for a request");
	public static final ConfigSetting MAX_REQUEST_PRICE = new ConfigSetting(config, "auction setting.pricing.max request price", 1000000000, "The maximum price for a request");


	public static final ConfigSetting AUTO_REFRESH_AUCTION_PAGES = new ConfigSetting(config, "auction setting.auto refresh auction pages", true, "Should auction pages auto refresh?");
	public static final ConfigSetting AUTO_REFRESH_ACTIVE_AUCTION_PAGES = new ConfigSetting(config, "auction setting.auto refresh active auction pages", false, "Should the /ah active pages be auto refreshed?");
	public static final ConfigSetting INCREASE_TIME_ON_BID = new ConfigSetting(config, "auction setting.increase time on bid", true, "Should the remaining time be increased when a bid is placed?");
	public static final ConfigSetting TIME_TO_INCREASE_BY_ON_BID = new ConfigSetting(config, "auction setting.time to increase by on the bid", 20, "How many seconds should be added to the remaining time?");
	public static final ConfigSetting ALLOW_SALE_OF_DAMAGED_ITEMS = new ConfigSetting(config, "auction setting.allow sale of damaged items", true, "If true, player's can sell items that are damaged (not max durability)");
	public static final ConfigSetting ALLOW_FLOODGATE_PLAYERS = new ConfigSetting(config, "auction setting.allow flood gate players", false, "If true, player's who connected using floodgate (bedrock players) won't be able to use the auction house");
	public static final ConfigSetting RESTRICT_ALL_TRANSACTIONS_TO_PERM = new ConfigSetting(config, "auction setting.restrict viewing all transactions", false, "If true, player's will need the perm: auctionhouse.transactions.viewall to view all transactions");
	public static final ConfigSetting BLOCKED_WORLDS = new ConfigSetting(config, "auction setting.blocked worlds", Collections.singletonList("creative"), "A list of worlds that Auction House will be disabled in");
	public static final ConfigSetting PREVENT_SALE_OF_REPAIRED_ITEMS = new ConfigSetting(config, "auction setting.prevent sale of repaired items", false, "Items repaired before this setting is turned on will still be able to be listed.");
	public static final ConfigSetting ITEM_COPY_REQUIRES_GMC = new ConfigSetting(config, "auction setting.admin copy requires creative", false, "If true when using the admin copy option the player must be in creative");
	public static final ConfigSetting LOG_ADMIN_ACTIONS = new ConfigSetting(config, "auction setting.log admin actions", true, "If true, any admin actions made will be logged");
	public static final ConfigSetting ROUND_ALL_PRICES = new ConfigSetting(config, "auction setting.round all prices", false, "If true, any decimal numbers will be rounded to the nearest whole number");
	public static final ConfigSetting DISABLE_AUTO_SAVE_MSG = new ConfigSetting(config, "auction setting.disable auto save message", false, "If true, auction house will not log the auto save task to the console");
	public static final ConfigSetting DISABLE_CLEANUP_MSG = new ConfigSetting(config, "auction setting.disable clean up message", false, "If true, auction house will not log the clean up process to the console");

	public static final ConfigSetting DISABLE_PROFILE_UPDATE_MSG = new ConfigSetting(config, "auction setting.disable profile update message", false, "If true, auction house will not log the player profile updates to the console");
//	public static final ConfigSetting DISABLE_PLAYER_REF_UPDATE_MSG = new ConfigSetting(config, "auction setting.disable player reference update message", false, "If true, auction house will not log the player reference updates to the console");

	public static final ConfigSetting TICK_UPDATE_TIME = new ConfigSetting(config, "auction setting.tick auctions every", 1, "How many seconds should pass before the plugin updates all the times on items?");

	public static final ConfigSetting GARBAGE_DELETION_TIMED_MODE = new ConfigSetting(config, "auction setting.garbage deletion.timed mode", true, "If true, auction house will only run the garbage deletion task, after set amount of seconds", "otherwise if false, it will wait until the total garbage bin count", "reaches/exceeds the specified value");
	public static final ConfigSetting GARBAGE_DELETION_TIMED_DELAY = new ConfigSetting(config, "auction setting.garbage deletion.timed delay", 60, "If timed mode is true, this value will be ran after x specified seconds, the lower this number the more frequent a new async task will be ran!");
	public static final ConfigSetting GARBAGE_DELETION_MAX_ITEMS = new ConfigSetting(config, "auction setting.garbage deletion.max items", 30, "If timed mode is false, whenever the garbage bin reaches this number, auction house will run the deletion task.", "You should adjust this number based on your server since some servers may have more or less items being claimed / marked for garbage clean up");

	public static final ConfigSetting CLAIM_MS_DELAY = new ConfigSetting(config, "auction setting.item claim delay", 100, "How many ms should a player wait before being allowed to claim an item?, Ideally you don't wanna change this. It's meant to prevent auto clicker dupe claims");

	public static final ConfigSetting TICK_UPDATE_GUI_TIME = new ConfigSetting(config, "auction setting.refresh gui every", 10, "How many seconds should pass before the auction gui auto refreshes?");
	public static final ConfigSetting RECORD_TRANSACTIONS = new ConfigSetting(config, "auction setting.record transactions", true, "Should every transaction be recorded (everything an auction is won or an item is bought)");
	public static final ConfigSetting BUNDLE_IS_OPENED_ON_RECLAIM = new ConfigSetting(config, "auction setting.open bundle on reclaim", true, "When the player claims an expired item, if its a bundle, should it be automatically opened. (items that cannot fit will drop to the ground)");
	public static final ConfigSetting MAX_SHULKER_IN_BUNDLE = new ConfigSetting(config, "auction setting.maximmum bundle and shulker in bundle", 5, "The maximum amount of shulkers/vanilla bundles that can be added to a bundle");

	public static final ConfigSetting BROADCAST_AUCTION_LIST = new ConfigSetting(config, "auction setting.broadcast auction list", false, "Should the entire server be alerted when a player lists an item?");
	public static final ConfigSetting BROADCAST_AUCTION_BID = new ConfigSetting(config, "auction setting.broadcast auction bid", false, "Should the entire server be alerted when a player bids on an item?");
	public static final ConfigSetting BROADCAST_AUCTION_SALE = new ConfigSetting(config, "auction setting.broadcast auction sale", false, "Should the entire server be alerted when an auction is sold");
	public static final ConfigSetting BROADCAST_AUCTION_ENDING = new ConfigSetting(config, "auction setting.broadcast auction ending", false, "Should the entire server be alerted when an auction is about to end?");
	public static final ConfigSetting BROADCAST_AUCTION_ENDING_AT_TIME = new ConfigSetting(config, "auction setting.broadcast auction ending at time", 20, "When the time on the auction item reaches this amount of seconds left, the broadcast ending will take affect ");

	public static final ConfigSetting USE_REALISTIC_BIDDING = new ConfigSetting(config, "auction setting.use realistic bidding", false, "If true auction house will use a more realistic bidding approach. Ex. the previous bid is 400, and if a player bids 500, rather than making the new bid 900, it will be set to 500.");
	public static final ConfigSetting BID_MUST_BE_HIGHER_THAN_PREVIOUS = new ConfigSetting(config, "auction setting.bid must be higher than previous", true, "Only applies if use realistic bidding is true, this will make it so that they must bid higher than the current bid.");
	public static final ConfigSetting USE_LIVE_BID_NUMBER_IN_CONFIRM_GUI = new ConfigSetting(config, "auction setting.live bid number in confirm gui.use", true, "If true, the bid confirmation menu will auto update every 1 second by default");
	public static final ConfigSetting LIVE_BID_NUMBER_IN_CONFIRM_GUI_RATE = new ConfigSetting(config, "auction setting.live bid number in confirm gui.rate", 1, "How often the confirm gui for bids will update");

	public static final ConfigSetting PLAYER_NEEDS_TOTAL_PRICE_TO_BID = new ConfigSetting(config, "auction setting.bidder must have funds in account", false, "Should the player who is placing a bid on an item have the money in their account to cover the cost?");
	public static final ConfigSetting ALLOW_USAGE_OF_BID_SYSTEM = new ConfigSetting(config, "auction setting.allow bid system usage", true, "Should players be allowed to use the bid option cmd params?");
	public static final ConfigSetting ALLOW_USAGE_OF_BUY_NOW_SYSTEM = new ConfigSetting(config, "auction setting.allow buy now system usage", true, "Should players be allowed to use the right-click buy now feature on biddable items?");
	public static final ConfigSetting BUY_NOW_DISABLED_BY_DEFAULT_IN_SELL_MENU = new ConfigSetting(config, "auction setting.buy now disabled in sell menu by default", false, "If true, players will just need to toggle buy now on their items to allow buy now");
	public static final ConfigSetting AUTO_SAVE_ENABLED = new ConfigSetting(config, "auction setting.auto save.enabled", true, "Should the auto save task be enabled?");
	public static final ConfigSetting AUTO_SAVE_EVERY = new ConfigSetting(config, "auction setting.auto save.time", 900, "How often should the auto save active? (in seconds. Ex. 900 = 15min)");
	public static final ConfigSetting ALLOW_PURCHASE_OF_SPECIFIC_QUANTITIES = new ConfigSetting(config, "auction setting.allow purchase of specific quantities", false, "When a buy now item is right-clicked should it open a", "special gui to specify the quantity of items to buy from the stack?");
	public static final ConfigSetting USE_REFRESH_COOL_DOWN = new ConfigSetting(config, "auction setting.use refresh cool down", true, "Should the refresh cooldown be enabled?");
	public static final ConfigSetting REFRESH_COOL_DOWN = new ConfigSetting(config, "auction setting.refresh cool down", 2, "How many seconds should pass before the player can refresh the auction house again?");
	public static final ConfigSetting MAIN_AH_FILTER_COOLDOWN = new ConfigSetting(config, "auction setting.auction house filter cooldown", 1500, "How many milliseconds should pass before they can change the filter again? use -1 to disable");
	public static final ConfigSetting MAIN_AH_NAVIGATION_COOLDOWN = new ConfigSetting(config, "auction setting.auction house page navigation cooldown", 500, "How many milliseconds should pass before they can navigate to another page? use -1 to disable");
	public static final ConfigSetting TRANSACTION_FILTER_COOLDOWN = new ConfigSetting(config, "auction setting.transaction filter cooldown", 1500, "How many milliseconds should pass before they can change the filter again? use -1 to disable");
	public static final ConfigSetting TRANSACTION_NAVIGATION_COOLDOWN = new ConfigSetting(config, "auction setting.transaction page navigation cooldown", 500, "How many milliseconds should pass before they can navigate to another page? use -1 to disable");

	public static final ConfigSetting CMD_COOLDOWN = new ConfigSetting(config, "auction setting.command cool down", 0, "How many seconds should pass between using commands");

	public static final ConfigSetting ALLOW_PURCHASE_IF_INVENTORY_FULL = new ConfigSetting(config, "auction setting.allow purchase with full inventory", true, "Should auction house allow players to buy items even if their", "inventory is full, if true, items will be dropped on the floor if there is no room.");
	public static final ConfigSetting ASK_FOR_BID_CONFIRMATION = new ConfigSetting(config, "auction setting.ask for bid confirmation", true, "Should Auction House open the confirmation menu for the user to confirm", "whether they actually meant to place a bid or not?");
	public static final ConfigSetting ASK_FOR_LISTING_CONFIRMATION = new ConfigSetting(config, "auction setting.ask for listing confirmation", false, "Should Auction House ask the user to confirm the listing?");
	public static final ConfigSetting REPLACE_HOW_TO_SELL_WITH_LIST_BUTTON = new ConfigSetting(config, "auction setting.replace how to sell with list button", false, "This will replace the \"How to Sell\" button with a List Item button");
	public static final ConfigSetting REPLACE_GUIDE_WITH_CART_BUTTON = new ConfigSetting(config, "auction setting.replace guide with cart button", false, "This will replace the \"Guide\" button with the Cart button. This will only work if the cart system is enabled");
	public static final ConfigSetting ALLOW_USAGE_OF_SELL_GUI = new ConfigSetting(config, "auction setting.allow usage of sell gui", true, "Should the sell menu be enabled?");
	public static final ConfigSetting FORCE_AUCTION_USAGE = new ConfigSetting(config, "auction setting.force auction usage", false, "If enabled, all items sold on the auction house must be an auction (biddable) items");
	public static final ConfigSetting ALLOW_INDIVIDUAL_ITEM_CLAIM = new ConfigSetting(config, "auction setting.allow individual item claim", true, "If enabled, you will be able to click individual items from the expiration menu to claim them back. Otherwise you will have to use the claim all button");
	public static final ConfigSetting FORCE_CUSTOM_BID_AMOUNT = new ConfigSetting(config, "auction setting.force custom bid amount", false, "If enabled, the bid increment line on auction items will be hidden, bid increment values will be ignored, and when you go to bid on an item, it will ask you to enter a custom amount.");
	public static final ConfigSetting SOUND_PITCH = new ConfigSetting(config, "auction setting.sound.pitch", 1.0, "The pitch value for sounds played by auction house");
	public static final ConfigSetting SOUND_VOLUME = new ConfigSetting(config, "auction setting.sound.volume", 1.0, "The volume value for sounds played by auction house");

	public static final ConfigSetting BIDDING_TAKES_MONEY = new ConfigSetting(config, "auction setting.bidding takes money", false, "If enabled, players will be outright charged the current bid for the item", "If they are outbid or the item is cancelled, they will get their money back. Disables ability for owners to bid on their own items!");
	public static final ConfigSetting LIST_ITEM_DELAY = new ConfigSetting(config, "auction setting.list item delay", -1, "If not set to -1 (disabled) how many seconds must a player wait to list another item after listing 1?");
	public static final ConfigSetting FORCE_SYNC_MONEY_ACTIONS = new ConfigSetting(config, "auction setting.force sync money actions", false, "If true, auction house will forcefully run a sync task to withdraw/deposit cash, this does not apply when using the commands");
	public static final ConfigSetting EXPIRATION_TIME_LIMIT_ENABLED = new ConfigSetting(config, "auction setting.expiration time limit.enabled", false, "If true, auction house will automatically delete un claimed expired items after 7 days (default)");
	public static final ConfigSetting EXPIRATION_TIME_LIMIT = new ConfigSetting(config, "auction setting.expiration time limit.time", 24 * 7, "In hours, what should the minimum age of an unclaimed item be inorder for it to be deleted?");

	public static final ConfigSetting ASK_FOR_CANCEL_CONFIRM_ON_BID_ITEMS = new ConfigSetting(config, "auction setting.ask for cancel confirm on bid items", true, "Should Auction House ask the user if they want to cancel the item?");
	public static final ConfigSetting ASK_FOR_CANCEL_CONFIRM_ON_NON_BID_ITEMS = new ConfigSetting(config, "auction setting.ask for cancel confirm on non bid items", false, "Should Auction House ask the user if they want to cancel the item?");
	public static final ConfigSetting ASK_FOR_CANCEL_CONFIRM_ON_ALL_ITEMS = new ConfigSetting(config, "auction setting.ask for cancel confirm on end all", true, "Should Auction House ask the user to confirm in chat when using end all in active listings?");

	public static final ConfigSetting BASE_PRICE_MUST_BE_HIGHER_THAN_BID_START = new ConfigSetting(config, "auction setting.base price must be higher than bid start", true, "Should the base price (buy now price) be higher than the initial bid starting price?");
	public static final ConfigSetting SYNC_BASE_PRICE_TO_HIGHEST_PRICE = new ConfigSetting(config, "auction setting.sync the base price to the current price", true, "Ex. If the buy now price was 100, and the current price exceeds 100 to say 200, the buy now price will become 200.");
	public static final ConfigSetting ADMIN_OPTION_SHOW_RETURN_ITEM = new ConfigSetting(config, "auction setting.admin option.show return to player", true);
	public static final ConfigSetting ADMIN_OPTION_SHOW_CLAIM_ITEM = new ConfigSetting(config, "auction setting.admin option.show claim item", true);
	public static final ConfigSetting ADMIN_OPTION_SHOW_DELETE_ITEM = new ConfigSetting(config, "auction setting.admin option.show delete item", true);
	public static final ConfigSetting ADMIN_OPTION_SHOW_COPY_ITEM = new ConfigSetting(config, "auction setting.admin option.show copy item", true);

	public static final ConfigSetting ALLOW_PLAYERS_TO_ACCEPT_BID = new ConfigSetting(config, "auction setting.allow players to accept bid", true, "If true, players can right click a biddable item inside their active listings menu to accept the current bid");
	public static final ConfigSetting SELLERS_MUST_WAIT_FOR_TIME_LIMIT_AFTER_BID = new ConfigSetting(config, "auction setting.prevent cancellation of bid on items", false, "If true, players must wait out the duration of the auction listing if there is already a bid on it (makes them commit to selling it)");
	public static final ConfigSetting PER_WORLD_ITEMS = new ConfigSetting(config, "auction setting.per world items", false, "If true, items can only be seen in the world they were listed in, same goes for bidding/buying/collecting");
	public static final ConfigSetting ALLOW_PLAYERS_TO_DEFINE_AUCTION_TIME = new ConfigSetting(config, "auction setting.allow players to set auction time", false, "If true players can use -t 1 day for example to set the listing time for their item");
	public static final ConfigSetting MAX_CUSTOM_DEFINED_TIME = new ConfigSetting(config, "auction setting.max custom defined time", 604800, "What should the limit on custom defined listing times be in seconds?");
	public static final ConfigSetting SMART_MIN_BUY_PRICE = new ConfigSetting(config, "auction setting.smart min and buy price", false, "Will calculate buy now/min prices on a per item basis. For example, if the user states $100 and the item is in a stack of", "32, the min / buy now price will be $3200. If they provide -s or -stack in the command", "this will be ignored and the entire stack will sell for $100");
	public static final ConfigSetting TITLE_INPUT_CANCEL_WORD = new ConfigSetting(config, "auction setting.title input cancel word", "cancel", "The word to be used to cancel chat inputs (users can also just click any block)");

	public static final ConfigSetting USE_SEPARATE_FILTER_MENU = new ConfigSetting(config, "auction setting.use separate filter menu", false, "If true, rather than using a single filter item inside the auction menu", "it will open an entirely new menu to select the filter");
	public static final ConfigSetting FILTER_ONLY_USES_WHITELIST = new ConfigSetting(config, "auction setting.filter only uses whitelist", false, "If true, auction house will ignore default filters, and only filter by the items added to the category whitelists");
	public static final ConfigSetting FILTER_WHITELIST_USES_DURABILITY = new ConfigSetting(config, "auction setting.filter whitelist uses durability", false, "If true, the filter will look at material names and durability values for comparisons only");
	public static final ConfigSetting SELL_MENU_REQUIRES_USER_TO_HOLD_ITEM = new ConfigSetting(config, "auction setting.require user to hold item when using sell menu", false, "If enabled, when running just /ah sell, the user will need to hold the item in their hand, otherwise they just add it in the gui.");
	public static final ConfigSetting OPEN_MAIN_AUCTION_HOUSE_AFTER_MENU_LIST = new ConfigSetting(config, "auction setting.open main auction house after listing using menu", true, "Should the main auction house be opened after the user lists an item using the sell menu?");
	public static final ConfigSetting SELL_MENU_CLOSE_SENDS_TO_LISTING = new ConfigSetting(config, "auction setting.sell menu close sends to listings", true, "If true, when the player clicks the close button within the sell menu, it will send them to the main auction house");
	public static final ConfigSetting PAYMENT_HANDLE_USE_CMD = new ConfigSetting(config, "auction setting.payment handle.use command", false, "In special cases, you will want to use this");
	public static final ConfigSetting PAYMENT_HANDLE_WITHDRAW_CMD = new ConfigSetting(config, "auction setting.payment handle.withdraw command", "eco take %player% %price%", "Command that will be executed to withdraw a player's balance");
	public static final ConfigSetting PAYMENT_HANDLE_DEPOSIT_CMD = new ConfigSetting(config, "auction setting.payment handle.deposit command", "eco give %player% %price%", "Command that will be executed to deposit a player's balance");

	public static final ConfigSetting TAX_ENABLED = new ConfigSetting(config, "auction setting.tax.enabled", false, "Should auction house use it's tax system?");
	public static final ConfigSetting TAX_CHARGE_LISTING_FEE = new ConfigSetting(config, "auction setting.tax.charge listing fee", true, "Should auction house charge players to list an item?");
	public static final ConfigSetting TAX_LISTING_FEE = new ConfigSetting(config, "auction setting.tax.listing fee", 5.0, "How much should it cost to list a new item?");
	public static final ConfigSetting TAX_LISTING_FEE_PERCENTAGE = new ConfigSetting(config, "auction setting.tax.listing fee is percentage", true, "Should the listing fee be based on a percentage instead?");
	public static final ConfigSetting TAX_CHARGE_SALES_TAX_TO_BUYER = new ConfigSetting(config, "auction setting.tax.charge sale tax to buyer", false, "Should auction house tax the buyer instead of the seller?");
	public static final ConfigSetting TAX_SALES_TAX_BUY_NOW_PERCENTAGE = new ConfigSetting(config, "auction setting.tax.buy now sales tax", 15.0, "Tax % that should be charged on items that are bought immediately");
	public static final ConfigSetting TAX_SALES_TAX_AUCTION_WON_PERCENTAGE = new ConfigSetting(config, "auction setting.tax.auction won sales tax", 10.0, "Tax % that should be charged on items that are won through the auction");


	public static final ConfigSetting FILTERS_ALL_ICON = new ConfigSetting(config, "auction setting.filter icons.all", "HOPPER");
	public static final ConfigSetting FILTERS_FOOD_ICON = new ConfigSetting(config, "auction setting.filter icons.food", "APPLE");
	public static final ConfigSetting FILTERS_ARMOR_ICON = new ConfigSetting(config, "auction setting.filter icons.armor", "DIAMOND_HELMET");
	public static final ConfigSetting FILTERS_BLOCKS_ICON = new ConfigSetting(config, "auction setting.filter icons.blocks", "GRASS_BLOCK");
	public static final ConfigSetting FILTERS_TOOLS_ICON = new ConfigSetting(config, "auction setting.filter icons.tools", "STONE_SHOVEL");
	public static final ConfigSetting FILTERS_WEAPONS_ICON = new ConfigSetting(config, "auction setting.filter icons.weapons", "IRON_SWORD");
	public static final ConfigSetting FILTERS_SPAWNERS_ICON = new ConfigSetting(config, "auction setting.filter icons.spawners", "SPAWNER");
	public static final ConfigSetting FILTERS_ENCHANTS_ICON = new ConfigSetting(config, "auction setting.filter icons.enchants", "ENCHANTED_BOOK");
	public static final ConfigSetting FILTERS_POTIONS_ICON = new ConfigSetting(config, "auction setting.filter icons.potions", "POTION");
	public static final ConfigSetting FILTERS_MISC_ICON = new ConfigSetting(config, "auction setting.filter icons.misc", "OAK_SIGN");
	public static final ConfigSetting FILTERS_SELF_ICON = new ConfigSetting(config, "auction setting.filter icons.self", "NAME_TAG");
	public static final ConfigSetting FILTERS_SEARCH_ICON = new ConfigSetting(config, "auction setting.filter icons.search", "COMPASS");


	public static final ConfigSetting ALL_FILTER_ENABLED = new ConfigSetting(config, "auction setting.enabled filters.all", true, "Should this filter be enabled?");
	public static final ConfigSetting FOOD_FILTER_ENABLED = new ConfigSetting(config, "auction setting.enabled filters.food", true, "Should this filter be enabled?");
	public static final ConfigSetting ARMOR_FILTER_ENABLED = new ConfigSetting(config, "auction setting.enabled filters.armor", true, "Should this filter be enabled?");
	public static final ConfigSetting BLOCKS_FILTER_ENABLED = new ConfigSetting(config, "auction setting.enabled filters.blocks", true, "Should this filter be enabled?");
	public static final ConfigSetting TOOLS_FILTER_ENABLED = new ConfigSetting(config, "auction setting.enabled filters.tools", true, "Should this filter be enabled?");
	public static final ConfigSetting WEAPONS_FILTER_ENABLED = new ConfigSetting(config, "auction setting.enabled filters.weapons", true, "Should this filter be enabled?");
	public static final ConfigSetting SPAWNERS_FILTER_ENABLED = new ConfigSetting(config, "auction setting.enabled filters.spawners", true, "Should this filter be enabled?");
	public static final ConfigSetting ENCHANTS_FILTER_ENABLED = new ConfigSetting(config, "auction setting.enabled filters.enchants", true, "Should this filter be enabled?");
	public static final ConfigSetting POTIONS_FILTER_ENABLED = new ConfigSetting(config, "auction setting.enabled filters.potions", true, "Should this filter be enabled?");
	public static final ConfigSetting MISC_FILTER_ENABLED = new ConfigSetting(config, "auction setting.enabled filters.misc", true, "Should this filter be enabled?");
	public static final ConfigSetting SEARCH_FILTER_ENABLED = new ConfigSetting(config, "auction setting.enabled filters.search", true, "Should this filter be enabled?");
	public static final ConfigSetting SELF_FILTER_ENABLED = new ConfigSetting(config, "auction setting.enabled filters.self", true, "Should this filter be enabled?");
	public static final ConfigSetting USE_AUCTION_CHEST_MODE = new ConfigSetting(config, "auction setting.use auction chest mode", false, "Enabling this will make it so players can only access the auction through the auction chest");
	public static final ConfigSetting AUTO_BSTATS = new ConfigSetting(config, "auction setting.use bstats", true, "Auto enable bStats");
	public static final ConfigSetting FORCE_MATERIAL_NAMES_FOR_DISCORD = new ConfigSetting(config, "auction setting.force material names for discord", false, "If true, auction house will use the actual material name rather than custom name");

	public static final ConfigSetting ALLOW_ITEM_BUNDLES = new ConfigSetting(config, "auction setting.bundles.enabled", true, "If true, players can use -b in the sell command to bundle all similar items into a single item.");
	public static final ConfigSetting ITEM_BUNDLE_ITEM = new ConfigSetting(config, "auction setting.bundles.item", CompMaterial.GOLD_BLOCK.name());
	public static final ConfigSetting MIN_ITEM_PRICE_USES_SIMPE_COMPARE = new ConfigSetting(config, "auction setting.use simple compare for min item price", true, "If true, AH will just compare material and model data types");
	public static final ConfigSetting ITEM_BUNDLE_NAME = new ConfigSetting(config, "auction setting.bundles.name", "%item_name% &7Bundle");
	public static final ConfigSetting ITEM_BUNDLE_LORE = new ConfigSetting(config, "auction setting.bundles.lore", Arrays.asList(
			"&7This is a bundle item, it contains",
			"&7multiple items that can be unboxed."
	));

	public static final ConfigSetting CLICKS_NON_BID_ITEM_PURCHASE = new ConfigSetting(config, "auction setting.clicks.non bid item purchase", "LEFT",
			"Valid Click Types",
			"LEFT",
			"RIGHT",
			"SHIFT_LEFT",
			"SHIFT_RIGHT",
			"MIDDLE",
			"",
			"&cIf you overlap click types (ex. LEFT for both inspect and buy) things will go crazy."
	);

	public static final ConfigSetting CLICKS_NON_BID_ITEM_ADD_TO_CART = new ConfigSetting(config, "auction setting.clicks.non bid item add to cart", "RIGHT",
			"Valid Click Types",
			"LEFT",
			"RIGHT",
			"SHIFT_LEFT",
			"SHIFT_RIGHT",
			"MIDDLE",
			"",
			"&cIf you overlap click types (ex. LEFT for both inspect and buy) things will go crazy."
	);

	public static final ConfigSetting CLICKS_NON_BID_ITEM_QTY_PURCHASE = new ConfigSetting(config, "auction setting.clicks.non bid item qty purchase", "SHIFT_LEFT",
			"Valid Click Types",
			"LEFT",
			"RIGHT",
			"SHIFT_LEFT",
			"SHIFT_RIGHT",
			"MIDDLE",
			"",
			"&cIf you overlap click types (ex. LEFT for both inspect and buy) things will go crazy."
	);

	public static final ConfigSetting CLICKS_BID_ITEM_PLACE_BID = new ConfigSetting(config, "auction setting.clicks.bid item place bid", "LEFT",
			"Valid Click Types",
			"LEFT",
			"RIGHT",
			"SHIFT_LEFT",
			"SHIFT_RIGHT",
			"MIDDLE",
			"",
			"&cIf you overlap click types (ex. LEFT for both inspect and buy) things will go crazy."
	);

	public static final ConfigSetting CLICKS_BID_ITEM_BUY_NOW = new ConfigSetting(config, "auction setting.clicks.bid item buy now", "RIGHT",
			"Valid Click Types",
			"LEFT",
			"RIGHT",
			"SHIFT_LEFT",
			"SHIFT_RIGHT",
			"MIDDLE",
			"",
			"&cIf you overlap click types (ex. LEFT for both inspect and buy) things will go crazy."
	);

	public static final ConfigSetting CLICKS_INSPECT_CONTAINER = new ConfigSetting(config, "auction setting.clicks.inspect container", "SHIFT_RIGHT",
			"Valid Click Types",
			"LEFT",
			"RIGHT",
			"SHIFT_LEFT",
			"SHIFT_RIGHT",
			"MIDDLE",
			"",
			"&cIf you overlap click types (ex. LEFT for both inspect and buy) things will go crazy."
	);

	public static final ConfigSetting CLICKS_REMOVE_ITEM = new ConfigSetting(config, "auction setting.clicks.remove item", "DROP",
			"Valid Click Types",
			"LEFT",
			"RIGHT",
			"SHIFT_LEFT",
			"SHIFT_RIGHT",
			"MIDDLE",
			"",
			"&cIf you overlap click types (ex. LEFT for both inspect and buy) things will go crazy."
	);

	public static final ConfigSetting CLICKS_FILTER_SORT_PRICE_OR_RECENT = new ConfigSetting(config, "auction setting.clicks.filter.sort by price or recent", "SHIFT_RIGHT",
			"Valid Click Types",
			"LEFT",
			"RIGHT",
			"SHIFT_LEFT",
			"SHIFT_RIGHT",
			"MIDDLE",
			"DROP",
			"",
			"&cIf you overlap click types (ex. LEFT for both inspect and buy) things will go crazy."
	);

	public static final ConfigSetting CLICKS_FILTER_CURRENCY = new ConfigSetting(config, "auction setting.clicks.filter.listing currency", "SHIFT_LEFT",
			"Valid Click Types",
			"LEFT",
			"RIGHT",
			"SHIFT_LEFT",
			"SHIFT_RIGHT",
			"MIDDLE",
			"DROP",
			"",
			"&cIf you overlap click types (ex. LEFT for both inspect and buy) things will go crazy."
	);

	public static final ConfigSetting CLICKS_FILTER_SORT_SALE_TYPE = new ConfigSetting(config, "auction setting.clicks.filter.sort sale type", "RIGHT",
			"Valid Click Types",
			"LEFT",
			"RIGHT",
			"SHIFT_LEFT",
			"SHIFT_RIGHT",
			"MIDDLE",
			"DROP",
			"",
			"&cIf you overlap click types (ex. LEFT for both inspect and buy) things will go crazy."
	);

	public static final ConfigSetting CLICKS_FILTER_TRANSACTION_BUY_TYPE = new ConfigSetting(config, "auction setting.clicks.filter.transaction buy type", "SHIFT_LEFT",
			"Valid Click Types",
			"LEFT",
			"RIGHT",
			"SHIFT_LEFT",
			"SHIFT_RIGHT",
			"MIDDLE",
			"DROP",
			"",
			"&cIf you overlap click types (ex. LEFT for both inspect and buy) things will go crazy."
	);

	public static final ConfigSetting CLICKS_FILTER_RESET = new ConfigSetting(config, "auction setting.clicks.filter.reset", "DROP",
			"Valid Click Types",
			"LEFT",
			"RIGHT",
			"SHIFT_LEFT",
			"SHIFT_RIGHT",
			"MIDDLE",
			"DROP",
			"",
			"&cIf you overlap click types (ex. LEFT for both inspect and buy) things will go crazy."
	);

	public static final ConfigSetting CLICKS_FILTER_CATEGORY = new ConfigSetting(config, "auction setting.clicks.filter.change category", "LEFT",
			"Valid Click Types",
			"LEFT",
			"RIGHT",
			"SHIFT_LEFT",
			"SHIFT_RIGHT",
			"MIDDLE",
			"DROP",
			"",
			"&cIf you overlap click types (ex. LEFT for both inspect and buy) things will go crazy."
	);


	/*  ===============================
	 *         DATABASE OPTIONS
	 *  ===============================*/
	public static final ConfigSetting DATABASE_USE = new ConfigSetting(config, "database.use database", false, "Should the plugin use a database to store shop data?");
	public static final ConfigSetting DATABASE_TABLE_PREFIX = new ConfigSetting(config, "database.table prefix", "auctionhouse_", "What prefix should be used for table names");
	public static final ConfigSetting DATABASE_HOST = new ConfigSetting(config, "database.host", "localhost", "What is the connection url/host");
	public static final ConfigSetting DATABASE_PORT = new ConfigSetting(config, "database.port", 3306, "What is the port to database (default is 3306)");
	public static final ConfigSetting DATABASE_NAME = new ConfigSetting(config, "database.name", "plugin_dev", "What is the name of the database?");
	public static final ConfigSetting DATABASE_USERNAME = new ConfigSetting(config, "database.username", "root", "What is the name of the user connecting?");
	public static final ConfigSetting DATABASE_PASSWORD = new ConfigSetting(config, "database.password", "Password1.", "What is the password to the user connecting?");
	public static final ConfigSetting DATABASE_CUSTOM_PARAMS = new ConfigSetting(config, "database.custom parameters", "?useUnicode=yes&characterEncoding=UTF-8&useServerPrepStmts=false&rewriteBatchedStatements=true&useSSL=true", "Leave this alone if you don't know what you're doing. Set to 'None' to use no custom connection params");

	/*  ===============================
	 *         DISCORD WEBHOOK NEW
	 *  ===============================*/
	public static final ConfigSetting DISCORD_ENABLED = new ConfigSetting(config, "discord.enabled", false, "Should the discord webhook feature be enabled?");
	public static final ConfigSetting DISCORD_MSG_USERNAME = new ConfigSetting(config, "discord.user.username", "Auction House", "The name of the user who will send the message");
	public static final ConfigSetting DISCORD_MSG_PFP = new ConfigSetting(config, "discord.user.avatar picture", "https://cdn.kiranhart.com/spigot/auctionhouse/icon.png", "The avatar image of the discord user");
	public static final ConfigSetting DISCORD_WEBHOOKS = new ConfigSetting(config, "discord.webhooks", Collections.singletonList("https://discord.com/api/webhooks/1077667480920653840/CZbJG7DBoGhPXYICgp2--Ey_itVVmYqaQgorBfpvL7nQoQZWWMxz1TQgs1xG45Mzlpsn"), "A list of webhook urls (channels) you want a message sent to");
	public static final ConfigSetting DISCORD_DELAY_LISTINGS = new ConfigSetting(config, "discord.delay options.delay listing", false, "If true AuctionHouse will delay sending new listing messages by the specified seconds.");
	public static final ConfigSetting DISCORD_DELAY_LISTING_TIME = new ConfigSetting(config, "discord.delay options.delay listing time", 10, "How many seconds should Auction House wait to send the discord message for new listings");

	// options for when the alerts should be sent
	public static final ConfigSetting DISCORD_ALERT_ON_AUCTION_START = new ConfigSetting(config, "discord.alerts.new auction listing", true, "Should a message be sent when a new auction listing is made");
	public static final ConfigSetting DISCORD_ALERT_ON_BIN_START = new ConfigSetting(config, "discord.alerts.new bin listing", true, "Should a message be sent when a new bin listing is made (non biddable)");
	public static final ConfigSetting DISCORD_ALERT_ON_BID = new ConfigSetting(config, "discord.alerts.new bid", true, "Should a message be sent when a bid is placed on an item");
	public static final ConfigSetting DISCORD_ALERT_ON_BIN_BUY = new ConfigSetting(config, "discord.alerts.bin listing bought", true, "Should a message be sent when an item is bought");
	public static final ConfigSetting DISCORD_ALERT_ON_AUCTION_WON = new ConfigSetting(config, "discord.alerts.auction listing won", true, "Should a message be sent when an auction is won");
	// colors for each message
	public static final ConfigSetting DISCORD_COLOR_NEW_AUCTION_LISTING = new ConfigSetting(config, "discord.colors.new auction listing", "137-100-100", "The color of the embed, it needs to be in hsb format.", "Separate the numbers with a -");
	public static final ConfigSetting DISCORD_COLOR_NEW_BIN_LISTING = new ConfigSetting(config, "discord.colors.new bin listing", "137-100-100", "The color of the embed, it needs to be in hsb format.", "Separate the numbers with a -");
	public static final ConfigSetting DISCORD_COLOR_NEW_BID = new ConfigSetting(config, "discord.colors.new bid", "137-100-100", "The color of the embed, it needs to be in hsb format.", "Separate the numbers with a -");
	public static final ConfigSetting DISCORD_COLOR_BIN_LISTING_BOUGHT = new ConfigSetting(config, "discord.colors.bin listing bought", "137-100-100", "The color of the embed, it needs to be in hsb format.", "Separate the numbers with a -");
	public static final ConfigSetting DISCORD_COLOR_AUCTION_LISTING_WON = new ConfigSetting(config, "discord.colors.auction listing won", "137-100-100", "The color of the embed, it needs to be in hsb format.", "Separate the numbers with a -");
	// titles for each message
	public static final ConfigSetting DISCORD_TITLE_NEW_AUCTION_LISTING = new ConfigSetting(config, "discord.titles.new auction listing", "New Auction Listing");
	public static final ConfigSetting DISCORD_TITLE_NEW_BIN_LISTING = new ConfigSetting(config, "discord.titles.new bin listing", "New Bin Listing");
	public static final ConfigSetting DISCORD_TITLE_NEW_BID = new ConfigSetting(config, "discord.titles.new bid", "New Bid");
	public static final ConfigSetting DISCORD_TITLE_BIN_LISTING_BOUGHT = new ConfigSetting(config, "discord.titles.bin listing bought", "Listing Bought");
	public static final ConfigSetting DISCORD_TITLE_AUCTION_LISTING_WON = new ConfigSetting(config, "discord.titles.auction listing won", "Auction Won");
	// fields
	public static final ConfigSetting DISCORD_MSG_FIELD_SELLER_NAME = new ConfigSetting(config, "discord.field.seller.name", "Seller");
	public static final ConfigSetting DISCORD_MSG_FIELD_SELLER_VALUE = new ConfigSetting(config, "discord.field.seller.value", "%seller%");
	public static final ConfigSetting DISCORD_MSG_FIELD_SELLER_INLINE = new ConfigSetting(config, "discord.field.seller.inline", true);

	public static final ConfigSetting DISCORD_MSG_FIELD_ITEM_NAME = new ConfigSetting(config, "discord.field.item.name", "Item");
	public static final ConfigSetting DISCORD_MSG_FIELD_ITEM_VALUE = new ConfigSetting(config, "discord.field.item.value", "%item_name%");
	public static final ConfigSetting DISCORD_MSG_FIELD_ITEM_INLINE = new ConfigSetting(config, "discord.field.item.inline", true);

	public static final ConfigSetting DISCORD_MSG_FIELD_ITEM_LORE_NAME = new ConfigSetting(config, "discord.field.item lore.name", "Lore");


	public static final ConfigSetting DISCORD_MSG_FIELD_BIN_LISTING_PRICE_NAME = new ConfigSetting(config, "discord.field.bin listing price.name", "Price");
	public static final ConfigSetting DISCORD_MSG_FIELD_BIN_LISTING_PRICE_VALUE = new ConfigSetting(config, "discord.field.bin listing price.value", "%item_price%");
	public static final ConfigSetting DISCORD_MSG_FIELD_BIN_LISTING_PRICE_INLINE = new ConfigSetting(config, "discord.field.bin listing price.inline", true);

	public static final ConfigSetting DISCORD_MSG_FIELD_AUCTION_BUYOUT_PRICE_NAME = new ConfigSetting(config, "discord.field.auction buyout price.name", "Buy Now Price");
	public static final ConfigSetting DISCORD_MSG_FIELD_AUCTION_BUYOUT_PRICE_VALUE = new ConfigSetting(config, "discord.field.auction buyout price.value", "%buy_now_price%");
	public static final ConfigSetting DISCORD_MSG_FIELD_AUCTION_BUYOUT_PRICE_INLINE = new ConfigSetting(config, "discord.field.auction buyout price.inline", true);

	public static final ConfigSetting DISCORD_MSG_FIELD_AUCTION_START_PRICE_NAME = new ConfigSetting(config, "discord.field.auction start price.name", "Starting Price");
	public static final ConfigSetting DISCORD_MSG_FIELD_AUCTION_START_PRICE_VALUE = new ConfigSetting(config, "discord.field.auction start price.value", "%starting_price%");
	public static final ConfigSetting DISCORD_MSG_FIELD_AUCTION_START_PRICE_INLINE = new ConfigSetting(config, "discord.field.auction start price.inline", false);

	public static final ConfigSetting DISCORD_MSG_FIELD_BIN_BOUGHT_NAME = new ConfigSetting(config, "discord.field.bin listing bought.name", "Buyer");
	public static final ConfigSetting DISCORD_MSG_FIELD_BIN_BOUGHT_VALUE = new ConfigSetting(config, "discord.field.bin listing bought.value", "%buyer%");
	public static final ConfigSetting DISCORD_MSG_FIELD_BIN_BOUGHT_INLINE = new ConfigSetting(config, "discord.field.bin listing bought.inline", false);

	public static final ConfigSetting DISCORD_MSG_FIELD_AUCTION_WON_NAME = new ConfigSetting(config, "discord.field.auction listing won price.name", "Final Price");
	public static final ConfigSetting DISCORD_MSG_FIELD_AUCTION_WON_VALUE = new ConfigSetting(config, "discord.field.auction listing won price.value", "%final_price%");
	public static final ConfigSetting DISCORD_MSG_FIELD_AUCTION_WON_INLINE = new ConfigSetting(config, "discord.field.auction listing won price.inline", false);

	public static final ConfigSetting DISCORD_MSG_FIELD_AUCTION_WINNER_NAME = new ConfigSetting(config, "discord.field.auction winner.name", "Winner");
	public static final ConfigSetting DISCORD_MSG_FIELD_AUCTION_WINNER_VALUE = new ConfigSetting(config, "discord.field.auction winner.value", "%winner%");
	public static final ConfigSetting DISCORD_MSG_FIELD_AUCTION_WINNER_INLINE = new ConfigSetting(config, "discord.field.auction winner.inline", false);

	public static final ConfigSetting DISCORD_MSG_FIELD_AUCTION_BIDDER_NAME = new ConfigSetting(config, "discord.field.auction bidder.name", "Bidder");
	public static final ConfigSetting DISCORD_MSG_FIELD_AUCTION_BIDDER_VALUE = new ConfigSetting(config, "discord.field.auction bidder.value", "%bidder%");
	public static final ConfigSetting DISCORD_MSG_FIELD_AUCTION_BIDDER_INLINE = new ConfigSetting(config, "discord.field.auction bidder.inline", false);

	public static final ConfigSetting DISCORD_MSG_FIELD_BID_AMT_NAME = new ConfigSetting(config, "discord.field.bid amount.name", "Bid Amount");
	public static final ConfigSetting DISCORD_MSG_FIELD_BID_AMT_VALUE = new ConfigSetting(config, "discord.field.bid amount.value", "%bid_amount%");
	public static final ConfigSetting DISCORD_MSG_FIELD_BID_AMT_INLINE = new ConfigSetting(config, "discord.field.bid amount.inline", true);

	public static final ConfigSetting DISCORD_MSG_FIELD_AUCTION_CURRENT_PRICE_NAME = new ConfigSetting(config, "discord.field.current auction price.name", "Current Price");
	public static final ConfigSetting DISCORD_MSG_FIELD_AUCTION_CURRENT_PRICE_VALUE = new ConfigSetting(config, "discord.field.current auction price.value", "%current_price%");
	public static final ConfigSetting DISCORD_MSG_FIELD_AUCTION_CURRENT_PRICE_INLINE = new ConfigSetting(config, "discord.field.current auction price.inline", true);

	/*  ===============================
	 *          BLACK LISTED
	 *  ===============================*/
	public static final ConfigSetting BLOCKED_ITEMS = new ConfigSetting(config, "blocked items", Collections.singletonList("ENDER_CHEST"), "Materials that should be blocked (not allowed to sell)");
	public static final ConfigSetting BLOCKED_NBT_TAGS = new ConfigSetting(config, "blocked nbt tags", Collections.singletonList("example_tag"), "A list of NBT tags that are blocked from the auction house. These are case sensitive");
	public static final ConfigSetting MAKE_BLOCKED_ITEMS_A_WHITELIST = new ConfigSetting(config, "blocked items is whitelist", false, "If true, blocked items will become a whitelist, meaning only items specified in blacked list will be allowed in the ah");
	public static final ConfigSetting BLOCKED_ITEM_NAMES = new ConfigSetting(config, "blocked item names", Arrays.asList(
			"fuck",
			"bitch",
			"nigger",
			"nigga",
			"pussy"
	), "If an item contains any words/names specified here, it won't list.");

	public static final ConfigSetting BLOCKED_ITEM_LORES = new ConfigSetting(config, "blocked item lores", Arrays.asList(
			"kill yourself",
			"another random phrase"
	), "If an item lore contains any of these values, it won't list");

	/*  ===============================
	 *         MAX AUCTION TIME
	 *  ===============================*/
	public static final ConfigSetting AUCTION_TIME = new ConfigSetting(config, "auction time", Collections.singletonList("rankone:30"), "Special time permissions for users.", "If they have the following permission in this format:", "auctionhouse.time.rankone", "rankone refers to the list item under auction time, they will get the time specified (in seconds)");

	/*  ===============================
	 *           GLOBAL ITEMS
	 *  ===============================*/

	public static final ConfigSetting GUI_FILLER = new ConfigSetting(config, "gui.filler item", CompMaterial.BLACK_STAINED_GLASS_PANE.name(), "An item to be used to fill empty gui slots, this will be", "removed in later versions to be done on a per gui basis");

	public static final ConfigSetting GUI_BACK_BTN_ITEM = new ConfigSetting(config, "gui.global items.back button.item", "OAK_DOOR", "Settings for the previous page button");
	public static final ConfigSetting GUI_BACK_BTN_NAME = new ConfigSetting(config, "gui.global items.back button.name", "&e<< Previous Page");
	public static final ConfigSetting GUI_BACK_BTN_LORE = new ConfigSetting(config, "gui.global items.back button.lore", Arrays.asList("&7Click the button to go", "&7back to the previous page."));


	public static final ConfigSetting GUI_PREV_PAGE_BTN_SLOT = new ConfigSetting(config, "gui.global items.previous page button.slot", 48, "Valid Slots: 45 - 53");
	public static final ConfigSetting GUI_PREV_PAGE_BTN_ITEM = new ConfigSetting(config, "gui.global items.previous page button.item", "ARROW", "Settings for the previous page button");
	public static final ConfigSetting GUI_PREV_PAGE_BTN_NAME = new ConfigSetting(config, "gui.global items.previous page button.name", "&e<< Previous Page");
	public static final ConfigSetting GUI_PREV_PAGE_BTN_LORE = new ConfigSetting(config, "gui.global items.previous page button.lore", Arrays.asList("&7Click the button to go", "&7back to the previous page."));

	public static final ConfigSetting GUI_CLOSE_BTN_ITEM = new ConfigSetting(config, "gui.global items.close button.item", "BARRIER", "Settings for the close button");
	public static final ConfigSetting GUI_CLOSE_BTN_NAME = new ConfigSetting(config, "gui.global items.close button.name", "&cClose");
	public static final ConfigSetting GUI_CLOSE_BTN_LORE = new ConfigSetting(config, "gui.global items.close button.lore", Collections.singletonList("&7Click to close this menu."));

	public static final ConfigSetting GUI_NEXT_PAGE_BTN_SLOT = new ConfigSetting(config, "gui.global items.next page button.slot", 50, "Valid Slots: 45 - 53");
	public static final ConfigSetting GUI_NEXT_PAGE_BTN_ITEM = new ConfigSetting(config, "gui.global items.next page button.item", "ARROW", "Settings for the next button");
	public static final ConfigSetting GUI_NEXT_PAGE_BTN_NAME = new ConfigSetting(config, "gui.global items.next page button.name", "&eNext Page >>");
	public static final ConfigSetting GUI_NEXT_PAGE_BTN_LORE = new ConfigSetting(config, "gui.global items.next page button.lore", Arrays.asList("&7Click the button to go", "&7to the next page."));

	public static final ConfigSetting GUI_REFRESH_BTN_ENABLED = new ConfigSetting(config, "gui.global items.refresh button.enabled", true);
	public static final ConfigSetting GUI_REFRESH_BTN_SLOT = new ConfigSetting(config, "gui.global items.refresh button.slot", 49, "Valid Slots: 45 - 53");
	public static final ConfigSetting GUI_REFRESH_BTN_ITEM = new ConfigSetting(config, "gui.global items.refresh button.item", "CHEST", "Settings for the refresh page");
	public static final ConfigSetting GUI_REFRESH_BTN_NAME = new ConfigSetting(config, "gui.global items.refresh button.name", "&6&LRefresh Page");
	public static final ConfigSetting GUI_REFRESH_BTN_LORE = new ConfigSetting(config, "gui.global items.refresh button.lore", Collections.singletonList("&7Click to refresh the page"));

	// currency picker
	public static final ConfigSetting GUI_CURRENCY_PICKER_TITLE = new ConfigSetting(config, "gui.currency picker.title", "&7Auction House &f- &ePick Currency");
	public static final ConfigSetting GUI_CURRENCY_PICKER_ITEMS_CUSTOM_NAME = new ConfigSetting(config, "gui.currency picker.items.custom currency.name", "&e&lCustom Currency");
	public static final ConfigSetting GUI_CURRENCY_PICKER_ITEMS_CUSTOM_LORE = new ConfigSetting(config, "gui.currency picker.items.custom currency.lore",
			Arrays.asList(
					"&7If you want to use use a specific item for",
					"&7the currency, you can set that here.",
					"",
					"&e&lLeft Click &7with the item you want to use",
					"&b&lRight Click &7to open a material picker &eor",
					"&7as the currency onto this icon."
			)
	);

	public static final ConfigSetting GUI_CURRENCY_PICKER_ITEMS_CURRENCY_LORE = new ConfigSetting(config, "gui.currency picker.items.currency.lore",
			Collections.singletonList(
					"&e&lClick &7to select this currency"
			),
			"You can use %owning_plugin% to get the plugin name"
	);

	// material picker
	public static final ConfigSetting GUI_MATERIAL_PICKER_TITLE = new ConfigSetting(config, "gui.material picker.title", "&7Auction House &f- &ePick an Item");
	public static final ConfigSetting GUI_MATERIAL_PICKER_ITEMS_MATERIAL_LORE = new ConfigSetting(config, "gui.material picker.items.material.lore", "&7Click to select this item");
	public static final ConfigSetting GUI_MATERIAL_PICKER_ITEMS_SEARCH_NAME = new ConfigSetting(config, "gui.material picker.items.search.name", "&e&lSearch");
	public static final ConfigSetting GUI_MATERIAL_PICKER_ITEMS_SEARCH_LORE = new ConfigSetting(config, "gui.material picker.items.search.lore",
			Collections.singletonList(
					"&7Click to search for materials"
			)
	);
	public static final ConfigSetting GUI_MATERIAL_PICKER_ITEMS_RESET_NAME = new ConfigSetting(config, "gui.material picker.items.reset.name", "&c&lClear Search");
	public static final ConfigSetting GUI_MATERIAL_PICKER_ITEMS_RESET_LORE = new ConfigSetting(config, "gui.material picker.items.reset.lore",
			Collections.singletonList(
					"&7Click to clear search"
			)
	);

	/*  ===============================
	 *        CART GUI
	 *  ===============================*/
	public static final ConfigSetting GUI_CART_TITLE = new ConfigSetting(config, "gui.cart.title", "&7Auction House &f- &EYour Cart");
	public static final ConfigSetting GUI_CART_ROWS = new ConfigSetting(config, "gui.cart.rows", 6);
	public static final ConfigSetting GUI_CART_FILL_SLOTS = new ConfigSetting(config, "gui.cart.fill slots", IntStream.rangeClosed(0, 44).boxed().collect(Collectors.toList()));
	public static final ConfigSetting GUI_CART_ITEMS_CHECKOUT_ITEM = new ConfigSetting(config, "gui.cart.items.checkout.item", CompMaterial.LIME_STAINED_GLASS_PANE.name());
	public static final ConfigSetting GUI_CART_ITEMS_CHECKOUT_SLOT = new ConfigSetting(config, "gui.cart.items.checkout.slot", 49);
	public static final ConfigSetting GUI_CART_ITEMS_CHECKOUT_NAME = new ConfigSetting(config, "gui.cart.items.checkout.name", "&e&lCheckout");
	public static final ConfigSetting GUI_CART_ITEMS_CHECKOUT_LORE = new ConfigSetting(config, "gui.cart.items.checkout.lore", Arrays.asList(
			"&7Used to checkout this cart",
			"&eClick to checkout"
	));

	/*  ===============================
	 *         MAIN AUCTION GUI
	 *  ===============================*/
	public static final ConfigSetting GUI_AUCTION_HOUSE_TITLE = new ConfigSetting(config, "gui.auction house.title", "&7Auction House");
	public static final ConfigSetting GUI_AUCTION_HOUSE_ROWS = new ConfigSetting(config, "gui.auction house.rows", 6);
	public static final ConfigSetting GUI_AUCTION_HOUSE_FILL_SLOTS = new ConfigSetting(config, "gui.auction house.fill slots", IntStream.rangeClosed(0, 44).boxed().collect(Collectors.toList()));


	public static final ConfigSetting GUI_AUCTION_HOUSE_ITEMS_GUIDE_ENABLED = new ConfigSetting(config, "gui.auction house.items.guide.enabled", true);
	public static final ConfigSetting GUI_AUCTION_HOUSE_ITEMS_GUIDE_SLOT = new ConfigSetting(config, "gui.auction house.items.guide.slot", 53, "Valid Slots: 45 - 53");
	public static final ConfigSetting GUI_AUCTION_HOUSE_ITEMS_GUIDE_ITEM = new ConfigSetting(config, "gui.auction house.items.guide.item", "BOOK");
	public static final ConfigSetting GUI_AUCTION_HOUSE_ITEMS_GUIDE_NAME = new ConfigSetting(config, "gui.auction house.items.guide.name", "&e&lGuide");
	public static final ConfigSetting GUI_AUCTION_HOUSE_ITEMS_GUIDE_LORE = new ConfigSetting(config, "gui.auction house.items.guide.lore", Arrays.asList(
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
	));

	public static final ConfigSetting GUI_AUCTION_HOUSE_ITEMS_TRANSACTIONS_ENABLED = new ConfigSetting(config, "gui.auction house.items.transactions.enabled", true);
	public static final ConfigSetting GUI_AUCTION_HOUSE_ITEMS_TRANSACTIONS_SLOT = new ConfigSetting(config, "gui.auction house.items.transactions.slot", 51, "Valid Slots: 45 - 53");
	public static final ConfigSetting GUI_AUCTION_HOUSE_ITEMS_TRANSACTIONS_ITEM = new ConfigSetting(config, "gui.auction house.items.transactions.item", "PAPER");
	public static final ConfigSetting GUI_AUCTION_HOUSE_ITEMS_TRANSACTIONS_NAME = new ConfigSetting(config, "gui.auction house.items.transactions.name", "&e&lTransactions");
	public static final ConfigSetting GUI_AUCTION_HOUSE_ITEMS_TRANSACTIONS_LORE = new ConfigSetting(config, "gui.auction house.items.transactions.lore", Arrays.asList(
			"&7Click to view transaction history",
			"",
			"&eTotal Items Bought&f: &a%total_items_bought%",
			"&eTotal Items Sold&f: &a%total_items_sold%"
	));

	public static final ConfigSetting GUI_AUCTION_HOUSE_ITEMS_HOW_TO_SELL_ENABLED = new ConfigSetting(config, "gui.auction house.items.how to sell.enabled", true);
	public static final ConfigSetting GUI_AUCTION_HOUSE_ITEMS_HOW_TO_SELL_SLOT = new ConfigSetting(config, "gui.auction house.items.how to sell.slot", 52, "Valid Slots: 45 - 53");
	public static final ConfigSetting GUI_AUCTION_HOUSE_ITEMS_HOW_TO_SELL_ITEM = new ConfigSetting(config, "gui.auction house.items.how to sell.item", "GOLD_INGOT");
	public static final ConfigSetting GUI_AUCTION_HOUSE_ITEMS_HOW_TO_SELL_NAME = new ConfigSetting(config, "gui.auction house.items.how to sell.name", "&e&lHow to Sell");
	public static final ConfigSetting GUI_AUCTION_HOUSE_ITEMS_HOW_TO_SELL_LORE = new ConfigSetting(config, "gui.auction house.items.how to sell.lore", Arrays.asList(
			"&7To list an item on the auction house, just hold",
			"&7the item in your hand and type the following command.",
			"&e/ah sell <buyNowPrice> [startPrice] [bidIncrement]"
	));

	public static final ConfigSetting GUI_AUCTION_HOUSE_ITEMS_CART_SLOT = new ConfigSetting(config, "gui.auction house.items.cart.slot", 53, "Valid Slots: 45 - 53");
	public static final ConfigSetting GUI_AUCTION_HOUSE_ITEMS_CART_ITEM = new ConfigSetting(config, "gui.auction house.items.cart.item", CompMaterial.CHEST_MINECART.name());
	public static final ConfigSetting GUI_AUCTION_HOUSE_ITEMS_CART_NAME = new ConfigSetting(config, "gui.auction house.items.cart.name", "&e&lCart");
	public static final ConfigSetting GUI_AUCTION_HOUSE_ITEMS_CART_LORE = new ConfigSetting(config, "gui.auction house.items.cart.lore", Arrays.asList(
			"&7Click to view your cart",
			"",
			"&eTotal Items&f: &a%cart_item_count%"
	));

	public static final ConfigSetting GUI_AUCTION_HOUSE_ITEMS_LIST_ITEM_ENABLED = new ConfigSetting(config, "gui.auction house.items.list new item.enabled", true);
	public static final ConfigSetting GUI_AUCTION_HOUSE_ITEMS_LIST_ITEM_SLOT = new ConfigSetting(config, "gui.auction house.items.list new item.slot", 52, "Valid Slots: 45 - 53");
	public static final ConfigSetting GUI_AUCTION_HOUSE_ITEMS_LIST_ITEM_ITEM = new ConfigSetting(config, "gui.auction house.items.list new item.item", "CLOCK");
	public static final ConfigSetting GUI_AUCTION_HOUSE_ITEMS_LIST_ITEM_NAME = new ConfigSetting(config, "gui.auction house.items.list new item.name", "&e&lList Item");
	public static final ConfigSetting GUI_AUCTION_HOUSE_ITEMS_LIST_ITEM_LORE = new ConfigSetting(config, "gui.auction house.items.list new item.lore", Collections.singletonList("&7Click to list an item on the auction house."));

	public static final ConfigSetting GUI_AUCTION_HOUSE_ITEMS_YOUR_AUCTIONS_ENABLED = new ConfigSetting(config, "gui.auction house.items.your auctions.enabled", true);
	public static final ConfigSetting GUI_AUCTION_HOUSE_ITEMS_YOUR_AUCTIONS_SLOT = new ConfigSetting(config, "gui.auction house.items.your auctions.slot", 45, "Valid Slots: 45 - 53");
	public static final ConfigSetting GUI_AUCTION_HOUSE_ITEMS_YOUR_AUCTIONS_ITEM = new ConfigSetting(config, "gui.auction house.items.your auctions.item", "DIAMOND");
	public static final ConfigSetting GUI_AUCTION_HOUSE_ITEMS_YOUR_AUCTIONS_NAME = new ConfigSetting(config, "gui.auction house.items.your auctions.name", "&e&lYour Auctions");
	public static final ConfigSetting GUI_AUCTION_HOUSE_ITEMS_YOUR_AUCTIONS_LORE = new ConfigSetting(config, "gui.auction house.items.your auctions.lore", Arrays.asList(
			"&7Click here to view all of the items you",
			"&7are currently selling on the auction.",
			"",
			"&e&l%active_player_auctions% Item(s)",
			"&e&lBalance &a%player_balance%"
	));

	public static final ConfigSetting GUI_AUCTION_HOUSE_ITEMS_COLLECTION_BIN_ENABLED = new ConfigSetting(config, "gui.auction house.items.collection bin.enabled", true);
	public static final ConfigSetting GUI_AUCTION_HOUSE_ITEMS_COLLECTION_BIN_SLOT = new ConfigSetting(config, "gui.auction house.items.collection bin.slot", 46, "Valid Slots: 45 - 53");
	public static final ConfigSetting GUI_AUCTION_HOUSE_ITEMS_COLLECTION_BIN_ITEM = new ConfigSetting(config, "gui.auction house.items.collection bin.item", "ENDER_CHEST");
	public static final ConfigSetting GUI_AUCTION_HOUSE_ITEMS_COLLECTION_BIN_NAME = new ConfigSetting(config, "gui.auction house.items.collection bin.name", "&e&lCollection Bin");
	public static final ConfigSetting GUI_AUCTION_HOUSE_ITEMS_COLLECTION_BIN_LORE = new ConfigSetting(config, "gui.auction house.items.collection bin.lore", Arrays.asList(
			"&7Click here to view and collect all of the",
			"&7items you have cancelled or have expired.",
			"",
			"&e&l%expired_player_auctions% Item(s)"
	));

	public static final ConfigSetting GUI_AUCTION_HOUSE_ITEMS_FILTER_ENABLED = new ConfigSetting(config, "gui.auction house.items.filter.enabled", true);
	public static final ConfigSetting GUI_AUCTION_HOUSE_ITEMS_FILTER_SLOT = new ConfigSetting(config, "gui.auction house.items.filter.slot", 47, "Valid Slots: 45 - 53");
	public static final ConfigSetting GUI_AUCTION_HOUSE_ITEMS_FILTER_ITEM = new ConfigSetting(config, "gui.auction house.items.filter.item", "NETHER_STAR");
	public static final ConfigSetting GUI_AUCTION_HOUSE_ITEMS_FILTER_NAME = new ConfigSetting(config, "gui.auction house.items.filter.name", "&e&lFilter Options");
	public static final ConfigSetting GUI_AUCTION_HOUSE_ITEMS_FILTER_LORE = new ConfigSetting(config, "gui.auction house.items.filter.lore", Arrays.asList(
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
	));

	public static final ConfigSetting GUI_AUCTION_HOUSE_ITEMS_FILTER_MENU_ENABLED = new ConfigSetting(config, "gui.auction house.items.filter menu.enabled", true);
	public static final ConfigSetting GUI_AUCTION_HOUSE_ITEMS_FILTER_MENU_SLOT = new ConfigSetting(config, "gui.auction house.items.filter menu.slot", 47, "Valid Slots: 45 - 53");
	public static final ConfigSetting GUI_AUCTION_HOUSE_ITEMS_FILTER_MENU_ITEM = new ConfigSetting(config, "gui.auction house.items.filter menu.item", "HOPPER");
	public static final ConfigSetting GUI_AUCTION_HOUSE_ITEMS_FILTER_MENU_NAME = new ConfigSetting(config, "gui.auction house.items.filter menu.name", "&e&lCurrent Filter&f: &6%filter_category%");
	public static final ConfigSetting GUI_AUCTION_HOUSE_ITEMS_FILTER_MENU_LORE = new ConfigSetting(config, "gui.auction house.items.filter menu.lore", Arrays.asList(
			"&eItem Category&f: &7%filter_category%",
			"&eAuction Type&f: &7%filter_auction_type%",
			"&eSort Order&f: &7%filter_sort_order%",
			"",
			"&7Left-Click to change item category",
			"&7Right-Click to change change auction type",
			"&7Shift Right-Click to change sort order",
			"&7Press Drop to reset filters"
	));

	/*  ===============================
	 *         CONFIRM BUY GUI
	 *  ===============================*/
	public static final ConfigSetting GUI_CONFIRM_BUY_TITLE = new ConfigSetting(config, "gui.confirm buy.title", "&7Are you sure?");
	public static final ConfigSetting GUI_CONFIRM_FILL_BG_ON_QUANTITY = new ConfigSetting(config, "gui.confirm buy.fill background when buying quantity", true, "Should the empty slots be filled with an item", "when the player decides to buy a specific quantity of items?");
	public static final ConfigSetting GUI_CONFIRM_BG_ITEM = new ConfigSetting(config, "gui.confirm buy.bg item", CompMaterial.BLACK_STAINED_GLASS_PANE.name(), "This will only show when buying specific item quantities");

	public static final ConfigSetting GUI_CONFIRM_INCREASE_QTY_ITEM = new ConfigSetting(config, "gui.confirm buy.increase button.item", CompMaterial.LIME_STAINED_GLASS_PANE.name());
	public static final ConfigSetting GUI_CONFIRM_INCREASE_QTY_NAME = new ConfigSetting(config, "gui.confirm buy.increase button.name", "&a&l+1");
	public static final ConfigSetting GUI_CONFIRM_INCREASE_QTY_LORE = new ConfigSetting(config, "gui.confirm buy.increase button.lore", Collections.singletonList("&7Click to add &a+1 &7to purchase quantity"));

	public static final ConfigSetting GUI_CONFIRM_DECREASE_QTY_ITEM = new ConfigSetting(config, "gui.confirm buy.decrease button.item", CompMaterial.RED_STAINED_GLASS_PANE.name());
	public static final ConfigSetting GUI_CONFIRM_DECREASE_QTY_NAME = new ConfigSetting(config, "gui.confirm buy.decrease button.name", "&c&l-1");
	public static final ConfigSetting GUI_CONFIRM_DECREASE_QTY_LORE = new ConfigSetting(config, "gui.confirm buy.decrease button.lore", Collections.singletonList("&7Click to remove &c-1 &7from the purchase quantity"));

	public static final ConfigSetting GUI_CONFIRM_QTY_INFO_ITEM = new ConfigSetting(config, "gui.confirm buy.qty info.item", CompMaterial.PAPER.name());
	public static final ConfigSetting GUI_CONFIRM_QTY_INFO_NAME = new ConfigSetting(config, "gui.confirm buy.qty info.name", "&ePurchase Information");
	public static final ConfigSetting GUI_CONFIRM_QTY_INFO_LORE = new ConfigSetting(config, "gui.confirm buy.qty info.lore", Arrays.asList(
			"&7Original Stack Size&f: &e%original_stack_size%",
			"&7Price for entire stack&f: &a%original_stack_price%",
			"&7Price per item&f: &a%price_per_item%",
			"",
			"&7Purchase Qty&f: &e%purchase_quantity%",
			"&7Total&f: &a%purchase_price%"
	), "Valid Placeholders", "%original_stack_size%", "%original_stack_price%", "%price_per_item%", "%purchase_quantity%", "%purchase_price%");

	public static final ConfigSetting GUI_CONFIRM_BUY_NO_ITEM = new ConfigSetting(config, "gui.confirm buy.no.item", "RED_STAINED_GLASS_PANE");
	public static final ConfigSetting GUI_CONFIRM_BUY_NO_NAME = new ConfigSetting(config, "gui.confirm buy.no.name", "&c&LCancel");
	public static final ConfigSetting GUI_CONFIRM_BUY_NO_LORE = new ConfigSetting(config, "gui.confirm buy.no.lore", Collections.singletonList(
			"&7Click to cancel your purchase"
	));

	public static final ConfigSetting GUI_CONFIRM_BUY_YES_ITEM = new ConfigSetting(config, "gui.confirm buy.yes.item", "LIME_STAINED_GLASS_PANE");
	public static final ConfigSetting GUI_CONFIRM_BUY_YES_NAME = new ConfigSetting(config, "gui.confirm buy.yes.name", "&a&lConfirm");
	public static final ConfigSetting GUI_CONFIRM_BUY_YES_LORE = new ConfigSetting(config, "gui.confirm buy.yes.lore", Collections.singletonList(
			"&7Click to confirm your purchase"
	));

	public static final ConfigSetting GUI_CONFIRM_REQUEST_NO_ITEM = new ConfigSetting(config, "gui.confirm request.no.item", "RED_STAINED_GLASS_PANE");
	public static final ConfigSetting GUI_CONFIRM_REQUEST_NO_NAME = new ConfigSetting(config, "gui.confirm request.no.name", "&c&LCancel");
	public static final ConfigSetting GUI_CONFIRM_REQUEST_NO_LORE = new ConfigSetting(config, "gui.confirm request.no.lore", Collections.singletonList(
			"&7Click to cancel your purchase"
	));

	public static final ConfigSetting GUI_CONFIRM_REQUEST_YES_ITEM = new ConfigSetting(config, "gui.confirm request.yes.item", "LIME_STAINED_GLASS_PANE");
	public static final ConfigSetting GUI_CONFIRM_REQUEST_YES_NAME = new ConfigSetting(config, "gui.confirm request.yes.name", "&a&lConfirm");
	public static final ConfigSetting GUI_CONFIRM_REQUEST_YES_LORE = new ConfigSetting(config, "gui.confirm request.yes.lore", Collections.singletonList(
			"&7Click to confirm your purchase"
	));

	/*  ===============================
	 *         CONFIRM LISTING GUI
	 *  ===============================*/
	public static final ConfigSetting GUI_CONFIRM_LISTING_TITLE = new ConfigSetting(config, "gui.confirm listing.title", "&7Are you sure?");
	public static final ConfigSetting GUI_CONFIRM_LISTING_NO_ITEM = new ConfigSetting(config, "gui.confirm listing.no.item", "RED_STAINED_GLASS_PANE");
	public static final ConfigSetting GUI_CONFIRM_LISTING_NO_NAME = new ConfigSetting(config, "gui.confirm listing.no.name", "&c&LCancel");
	public static final ConfigSetting GUI_CONFIRM_LISTING_NO_LORE = new ConfigSetting(config, "gui.confirm listing.no.lore", Collections.singletonList(
			"&7Click to cancel listing"
	));

	public static final ConfigSetting GUI_CONFIRM_LISTING_YES_ITEM = new ConfigSetting(config, "gui.confirm listing.yes.item", "LIME_STAINED_GLASS_PANE");
	public static final ConfigSetting GUI_CONFIRM_LISTING_YES_NAME = new ConfigSetting(config, "gui.confirm listing.yes.name", "&a&lConfirm");
	public static final ConfigSetting GUI_CONFIRM_LISTING_YES_LORE = new ConfigSetting(config, "gui.confirm listing.yes.lore", Collections.singletonList(
			"&7Click to list your item"
	));

	/*  ===============================
	 *         CONFIRM BID GUI
	 *  ===============================*/
	public static final ConfigSetting GUI_CONFIRM_BID_TITLE = new ConfigSetting(config, "gui.confirm bid.title", "&7Are you sure?");
	public static final ConfigSetting GUI_CONFIRM_BID_NO_ITEM = new ConfigSetting(config, "gui.confirm bid.no.item", "RED_STAINED_GLASS_PANE");
	public static final ConfigSetting GUI_CONFIRM_BID_NO_NAME = new ConfigSetting(config, "gui.confirm bid.no.name", "&c&LCancel");
	public static final ConfigSetting GUI_CONFIRM_BID_NO_LORE = new ConfigSetting(config, "gui.confirm bid.no.lore", Collections.singletonList(
			"&7Click to cancel your bid"
	));

	public static final ConfigSetting GUI_CONFIRM_BID_YES_ITEM = new ConfigSetting(config, "gui.confirm bid.yes.item", "LIME_STAINED_GLASS_PANE");
	public static final ConfigSetting GUI_CONFIRM_BID_YES_NAME = new ConfigSetting(config, "gui.confirm bid.yes.name", "&a&lConfirm");
	public static final ConfigSetting GUI_CONFIRM_BID_YES_LORE = new ConfigSetting(config, "gui.confirm bid.yes.lore", Collections.singletonList(
			"&7Click to confirm your bid"
	));

	/*  ===============================
	 *       CONFIRM CANCEL GUI
	 *  ===============================*/
	public static final ConfigSetting GUI_CONFIRM_CANCEL_TITLE = new ConfigSetting(config, "gui.confirm cancel.title", "&7Are you sure?");
	public static final ConfigSetting GUI_CONFIRM_CANCEL_NO_ITEM = new ConfigSetting(config, "gui.confirm cancel.no.item", "RED_STAINED_GLASS_PANE");
	public static final ConfigSetting GUI_CONFIRM_CANCEL_NO_NAME = new ConfigSetting(config, "gui.confirm cancel.no.name", "&c&LCancel");
	public static final ConfigSetting GUI_CONFIRM_CANCEL_NO_LORE = new ConfigSetting(config, "gui.confirm cancel.no.lore", Collections.singletonList(
			"&7Click to cancel item removal"
	));

	public static final ConfigSetting GUI_CONFIRM_CANCEL_YES_ITEM = new ConfigSetting(config, "gui.confirm cancel.yes.item", "LIME_STAINED_GLASS_PANE");
	public static final ConfigSetting GUI_CONFIRM_CANCEL_YES_NAME = new ConfigSetting(config, "gui.confirm cancel.yes.name", "&a&lConfirm");
	public static final ConfigSetting GUI_CONFIRM_CANCEL_YES_LORE = new ConfigSetting(config, "gui.confirm cancel.yes.lore", Collections.singletonList(
			"&7Click to confirm item cancellation"
	));

	/*  ===============================
	 *         ACTIVE AUCTION GUI
	 *  ===============================*/

	public static final ConfigSetting GUI_ACTIVE_AUCTIONS_TITLE = new ConfigSetting(config, "gui.active auctions.title", "&7Active Listings");

	public static final ConfigSetting GUI_ACTIVE_AUCTIONS_ITEM = new ConfigSetting(config, "gui.active auctions.cancel all.item", "ENDER_CHEST");
	public static final ConfigSetting GUI_ACTIVE_AUCTIONS_NAME = new ConfigSetting(config, "gui.active auctions.cancel all.name", "&e&lEnd All");
	public static final ConfigSetting GUI_ACTIVE_AUCTIONS_LORE = new ConfigSetting(config, "gui.active auctions.cancel all.lore", Arrays.asList(
			"&7Click here to end all of the active listings",
			"&7that you have posted."
	));

	/*  ===============================
	 *         ACTIVE BIDS GUI
	 *  ===============================*/

	public static final ConfigSetting GUI_ACTIVE_BIDS_TITLE = new ConfigSetting(config, "gui.active bids.title", "&7Your Winning Bids");


	/*  ===============================
	 *         EXPIRED AUCTION GUI
	 *  ===============================*/

	public static final ConfigSetting GUI_EXPIRED_AUCTIONS_TITLE = new ConfigSetting(config, "gui.expired auctions.title", "&7Expired Listings");

	public static final ConfigSetting GUI_EXPIRED_AUCTIONS_ITEM = new ConfigSetting(config, "gui.expired auctions.cancel all.item", "ENDER_CHEST");
	public static final ConfigSetting GUI_EXPIRED_AUCTIONS_NAME = new ConfigSetting(config, "gui.expired auctions.cancel all.name", "&e&lClaim All");
	public static final ConfigSetting GUI_EXPIRED_AUCTIONS_LORE = new ConfigSetting(config, "gui.expired auctions.cancel all.lore", Collections.singletonList(
			"&7Click here to claim all of your expired auctions"
	));

	public static final ConfigSetting GUI_EXPIRED_AUCTIONS_PAYMENTS_ITEM = new ConfigSetting(config, "gui.expired auctions.collect payments.item", "GOLD_INGOT");
	public static final ConfigSetting GUI_EXPIRED_AUCTIONS_PAYMENTS_NAME = new ConfigSetting(config, "gui.expired auctions.collect payments.name", "&e&lCollect Payments");
	public static final ConfigSetting GUI_EXPIRED_AUCTIONS_PAYMENTS_LORE = new ConfigSetting(config, "gui.expired auctions.collect payments.lore", Collections.singletonList(
			"&7Click here to view your payments"
	));

	/*  ===============================
	 *       PAYMENT COLLECTION GUI
	 *  ===============================*/

	public static final ConfigSetting GUI_PAYMENT_COLLECTION_TITLE = new ConfigSetting(config, "gui.payment collection.title", "&7Payment Collection");

	public static final ConfigSetting GUI_PAYMENT_COLLECTION_ITEM = new ConfigSetting(config, "gui.payment collection.claim all.item", "ENDER_CHEST");
	public static final ConfigSetting GUI_PAYMENT_COLLECTION_NAME = new ConfigSetting(config, "gui.payment collection.claim all.name", "&e&lClaim All");
	public static final ConfigSetting GUI_PAYMENT_COLLECTION_LORE = new ConfigSetting(config, "gui.payment collection.claim all.lore", Collections.singletonList(
			"&7Click here to claim all of your payments"
	));

	public static final ConfigSetting GUI_PAYMENT_COLLECTION_PAYMENT_ITEM = new ConfigSetting(config, "gui.payment collection.payment.item", "PAPER");
	public static final ConfigSetting GUI_PAYMENT_COLLECTION_PAYMENT_NAME = new ConfigSetting(config, "gui.payment collection.payment.name", "&a&l%payment_amount%");
	public static final ConfigSetting GUI_PAYMENT_COLLECTION_PAYMENT_LORE = new ConfigSetting(config, "gui.payment collection.payment.lore", Arrays.asList(
			"&7Item&f: &e%item_name%",
			"&7From&f: &e%from_name%",
			"&7Reason&f:",
			"&e%payment_reason%",
			"",
			"&7Click here to claim this payment"
	));

	/*  ===============================
	 *      TRANSACTIONS TYPE GUI
	 *  ===============================*/
	public static final ConfigSetting GUI_TRANSACTIONS_TYPE_TITLE = new ConfigSetting(config, "gui.transactions type.title", "&7&LTransactions");
	public static final ConfigSetting GUI_TRANSACTIONS_TYPE_BG_ITEM = new ConfigSetting(config, "gui.transactions type.bg item", CompMaterial.BLACK_STAINED_GLASS_PANE.name());

	public static final ConfigSetting GUI_TRANSACTIONS_TYPE_ITEMS_ALL_TRANSACTIONS_ITEM = new ConfigSetting(config, "gui.transactions type.items.all transactions.item", CompMaterial.PAPER.name());
	public static final ConfigSetting GUI_TRANSACTIONS_TYPE_ITEMS_ALL_TRANSACTIONS_NAME = new ConfigSetting(config, "gui.transactions type.items.all transactions.name", "&eAll Transactions");
	public static final ConfigSetting GUI_TRANSACTIONS_TYPE_ITEMS_ALL_TRANSACTIONS_LORE = new ConfigSetting(config, "gui.transactions type.items.all transactions.lore", Collections.singletonList("&7Click to view all transactions"));

	public static final ConfigSetting GUI_TRANSACTIONS_TYPE_ITEMS_SELF_TRANSACTIONS_ITEM = new ConfigSetting(config, "gui.transactions type.items.self transactions.item", CompMaterial.DIAMOND.name());
	public static final ConfigSetting GUI_TRANSACTIONS_TYPE_ITEMS_SELF_TRANSACTIONS_NAME = new ConfigSetting(config, "gui.transactions type.items.self transactions.name", "&eYour Transactions");
	public static final ConfigSetting GUI_TRANSACTIONS_TYPE_ITEMS_SELF_TRANSACTIONS_LORE = new ConfigSetting(config, "gui.transactions type.items.self transactions.lore", Collections.singletonList("&7Click to view all your transactions"));

	public static final ConfigSetting GUI_TRANSACTIONS_TYPE_ITEMS_REQUEST_TRANSACTIONS_ITEM = new ConfigSetting(config, "gui.transactions type.items.requests transactions.item", CompMaterial.WRITTEN_BOOK.name());
	public static final ConfigSetting GUI_TRANSACTIONS_TYPE_ITEMS_REQUEST_TRANSACTIONS_NAME = new ConfigSetting(config, "gui.transactions type.items.requests transactions.name", "&eCompleted Requests");
	public static final ConfigSetting GUI_TRANSACTIONS_TYPE_ITEMS_REQUEST_TRANSACTIONS_LORE = new ConfigSetting(config, "gui.transactions type.items.requests transactions.lore", Collections.singletonList("&7Click to view completed requests"));


	public static final ConfigSetting GUI_TRANSACTIONS_TYPE_ITEMS_DELETE_ITEM = new ConfigSetting(config, "gui.transactions type.items.delete transactions.item", CompMaterial.LAVA_BUCKET.name());
	public static final ConfigSetting GUI_TRANSACTIONS_TYPE_ITEMS_DELETE_NAME = new ConfigSetting(config, "gui.transactions type.items.delete transactions.name", "&cDelete Transactions");
	public static final ConfigSetting GUI_TRANSACTIONS_TYPE_ITEMS_DELETE_LORE = new ConfigSetting(config, "gui.transactions type.items.delete transactions.lore", Arrays.asList(
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
	));

	/*  ===============================
	 *       MIN ITEM PRICES GUI
	 *  ===============================*/
	public static final ConfigSetting GUI_PRICE_LIMITS_TITLE = new ConfigSetting(config, "gui.price limits.title", "&7Auction House &f- &ePrice Limits");
	public static final ConfigSetting GUI_PRICE_LIMITS_LORE = new ConfigSetting(config, "gui.price limits.lore", Arrays.asList(
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
	));

	/*  ===============================
	 *    		LOGS LIST GUI
	 *  ===============================*/
	public static final ConfigSetting GUI_LOGS_TITLE = new ConfigSetting(config, "gui.admin logs.title", "&7&LAdmin Logs");
	public static final ConfigSetting GUI_LOGS_LORE = new ConfigSetting(config, "gui.admin logs.lore", Arrays.asList(
			"&7Admin&F: &e%admin%",
			"&7Target&F: &e%target%",
			"&7Item ID&F: %item_id%",
			"&7Action&F: &e%admin_action%",
			"&7Date&F: &e%admin_log_date%"
	));

	/*  ===============================
	 *      REQ TRANSACTIONS LIST GUI
	 *  ===============================*/
	public static final ConfigSetting GUI_REQUEST_TRANSACTIONS_TITLE = new ConfigSetting(config, "gui.request transactions.title", "&7&LYour Completed Requests");
	public static final ConfigSetting GUI_REQUEST_TRANSACTIONS_TITLE_ALL = new ConfigSetting(config, "gui.request transactions.title all", "&7&LAll Completed Requests");

	public static final ConfigSetting GUI_REQUEST_TRANSACTIONS_ITEM_TRANSACTION_LORE = new ConfigSetting(config, "gui.request transactions.items.transaction.lore", Arrays.asList(
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
	));

	public static final ConfigSetting GUI_REQUEST_TRANSACTIONS_ITEMS_FILTER_SLOT = new ConfigSetting(config, "gui.request transactions.items.filter.slot", 47, "Valid Slots: 45 - 53");
	public static final ConfigSetting GUI_REQUEST_TRANSACTIONS_ITEMS_FILTER_ITEM = new ConfigSetting(config, "gui.request transactions.items.filter.item", "NETHER_STAR");
	public static final ConfigSetting GUI_REQUEST_TRANSACTIONS_ITEMS_FILTER_NAME = new ConfigSetting(config, "gui.request transactions.items.filter.name", "&e&lFilter Options");
	public static final ConfigSetting GUI_REQUEST_TRANSACTIONS_ITEMS_FILTER_LORE = new ConfigSetting(config, "gui.request transactions.items.filter.lore", Arrays.asList(
			"&eSort Order&f: &7%filter_sort_order%",
			"",
			"&7Shift Right-Click to change sort order",
			"&7Press Drop to reset filters"
	));

	public static final ConfigSetting GUI_REQUEST_TRANSACTIONS_ITEMS_ALL_SLOT = new ConfigSetting(config, "gui.request transactions.items.all.slot", 51, "Valid Slots: 45 - 53");
	public static final ConfigSetting GUI_REQUEST_TRANSACTIONS_ITEMS_ALL_ITEM = new ConfigSetting(config, "gui.request transactions.items.all.item off", CompMaterial.RED_DYE.name());
	public static final ConfigSetting GUI_REQUEST_TRANSACTIONS_ITEMS_ALL_ITEM_ON = new ConfigSetting(config, "gui.request transactions.items.all.item on", CompMaterial.LIME_DYE.name());
	public static final ConfigSetting GUI_REQUEST_TRANSACTIONS_ITEMS_ALL_NAME = new ConfigSetting(config, "gui.request transactions.items.all.name", "&e&lToggle View All");
	public static final ConfigSetting GUI_REQUEST_TRANSACTIONS_ITEMS_ALL_LORE = new ConfigSetting(config, "gui.request transactions.items.all.lore", Arrays.asList(
			"&7Click to toggle view all"
	));

	/*  ===============================
	 *      TRANSACTIONS LIST GUI
	 *  ===============================*/
	public static final ConfigSetting GUI_TRANSACTIONS_TITLE = new ConfigSetting(config, "gui.transactions.title", "&7&LYour Transactions");
	public static final ConfigSetting GUI_TRANSACTIONS_TITLE_ALL = new ConfigSetting(config, "gui.transactions.title all", "&7&LAll Transactions");

	public static final ConfigSetting GUI_TRANSACTIONS_ITEM_TRANSACTION_NAME = new ConfigSetting(config, "gui.transactions.items.transaction.name", "&e%transaction_id%");
	public static final ConfigSetting GUI_TRANSACTIONS_ITEM_TRANSACTION_LORE = new ConfigSetting(config, "gui.transactions.items.transaction.lore", Arrays.asList(
			"&7Seller&F: &e%seller%",
			"&7Buyer&F: &e%buyer%",
			"&7Item name&F: %item_name%",
			"&7Date&F: &e%date%",
			"",
			"&7Click to view more details"
	));

	public static final ConfigSetting GUI_TRANSACTIONS_ITEMS_FILTER_SLOT = new ConfigSetting(config, "gui.transactions.items.filter.slot", 47, "Valid Slots: 45 - 53");
	public static final ConfigSetting GUI_TRANSACTIONS_ITEMS_FILTER_ITEM = new ConfigSetting(config, "gui.transactions.items.filter.item", "NETHER_STAR");
	public static final ConfigSetting GUI_TRANSACTIONS_ITEMS_FILTER_NAME = new ConfigSetting(config, "gui.transactions.items.filter.name", "&e&lFilter Options");
	public static final ConfigSetting GUI_TRANSACTIONS_ITEMS_FILTER_LORE = new ConfigSetting(config, "gui.transactions.items.filter.lore", Arrays.asList(
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
	));

	/*  ===============================
	 *      TRANSACTIONS VIEW GUI
	 *  ===============================*/
	public static final ConfigSetting GUI_TRANSACTION_VIEW_TITLE = new ConfigSetting(config, "gui.transaction view.title", "&7&LViewing Transaction");
	public static final ConfigSetting GUI_TRANSACTION_VIEW_BACKGROUND_FILL = new ConfigSetting(config, "gui.transaction view.background.fill", true);
	public static final ConfigSetting GUI_TRANSACTION_VIEW_BACKGROUND_ITEM = new ConfigSetting(config, "gui.transaction view.background.item", "BLACK_STAINED_GLASS_PANE");

	public static final ConfigSetting GUI_TRANSACTION_VIEW_ITEM_SELLER_NAME = new ConfigSetting(config, "gui.transaction view.items.seller.name", "&e%seller%");
	public static final ConfigSetting GUI_TRANSACTION_VIEW_ITEM_SELLER_LORE = new ConfigSetting(config, "gui.transaction view.items.seller.lore", Arrays.asList(
			"&7This is the player who sold the item."
	));

	public static final ConfigSetting GUI_TRANSACTION_VIEW_ITEM_BUYER_NAME = new ConfigSetting(config, "gui.transaction view.items.buyer.name", "&e%buyer%");
	public static final ConfigSetting GUI_TRANSACTION_VIEW_ITEM_BUYER_LORE = new ConfigSetting(config, "gui.transaction view.items.buyer.lore", Arrays.asList(
			"&7This is the player who bought the item."
	));

	public static final ConfigSetting GUI_TRANSACTION_VIEW_ITEM_INFO_ITEM = new ConfigSetting(config, "gui.transaction view.items.information.item", "PAPER");
	public static final ConfigSetting GUI_TRANSACTION_VIEW_ITEM_INFO_NAME = new ConfigSetting(config, "gui.transaction view.items.information.name", "&e%transaction_id%");
	public static final ConfigSetting GUI_TRANSACTION_VIEW_ITEM_INFO_LORE = new ConfigSetting(config, "gui.transaction view.items.information.lore", Arrays.asList(
			"&7ID&f: &e%transaction_id%",
			"&7Item name&F: %item_name%",
			"&7Sale Type&f: &e%sale_type%",
			"&7Date&f: &e%transaction_date%",
			"&7Final Price&f: &e%final_price%"
	));

	/*  ===============================
	 *         INSPECTION GUI
	 *  ===============================*/
	public static final ConfigSetting GUI_INSPECT_TITLE = new ConfigSetting(config, "gui.inspect.title", "&7&LInspecting Container");
	public static final ConfigSetting GUI_INSPECT_BG_ITEM = new ConfigSetting(config, "gui.inspect.bg item", CompMaterial.BLACK_STAINED_GLASS_PANE.name());

	/*  ===============================
	 *         BANS GUI
	 *  ===============================*/
	public static final ConfigSetting GUI_BANS_TITLE = new ConfigSetting(config, "gui.all bans.title", "&7&LAuction House &f- &eAll Bans");
	public static final ConfigSetting GUI_BANS_BG_ITEM = new ConfigSetting(config, "gui.all bans.bg item", CompMaterial.BLACK_STAINED_GLASS_PANE.name());
	public static final ConfigSetting GUI_BANS_ITEMS_BAN_NAME = new ConfigSetting(config, "gui.all bans.items.user.name", "&e%player_name%");
	public static final ConfigSetting GUI_BANS_ITEMS_BAN_LORE = new ConfigSetting(config, "gui.all bans.items.user.lore", Arrays.asList(
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
	));

	public static final ConfigSetting GUI_BAN_TITLE = new ConfigSetting(config, "gui.ban.title", "&7&LAuction House &f- &eBan User");
	public static final ConfigSetting GUI_BAN_BG_ITEM = new ConfigSetting(config, "gui.ban.bg item", CompMaterial.BLACK_STAINED_GLASS_PANE.name());

	public static final ConfigSetting GUI_BAN_ITEMS_PLAYER_NAME = new ConfigSetting(config, "gui.ban.items.player.name", "&e%player_name%");
	public static final ConfigSetting GUI_BAN_ITEMS_PLAYER_LORE = new ConfigSetting(config, "gui.ban.items.player.lore", Collections.singletonList("&7This is the selected user to be banned."));


	public static final ConfigSetting GUI_BAN_ITEMS_TYPES_ITEM = new ConfigSetting(config, "gui.ban.items.types.item", CompMaterial.COMPARATOR.name());
	public static final ConfigSetting GUI_BAN_ITEMS_TYPES_NAME = new ConfigSetting(config, "gui.ban.items.types.name", "&eBan Types");
	public static final ConfigSetting GUI_BAN_ITEMS_TYPES_LORE = new ConfigSetting(config, "gui.ban.items.types.lore", Arrays.asList(
			"&7The types of ban this player will receive",
			"",
			"&7Currently Selected Bans",
			"&e%ban_type_list%",
			"",
			"&EClick &7to adjust ban types"
	));

	public static final ConfigSetting GUI_BAN_ITEMS_PERMA_ITEM = new ConfigSetting(config, "gui.ban.items.permanent.item", CompMaterial.LAVA_BUCKET.name());
	public static final ConfigSetting GUI_BAN_ITEMS_PERMA_NAME = new ConfigSetting(config, "gui.ban.items.permanent.name", "&ePermanent");
	public static final ConfigSetting GUI_BAN_ITEMS_PERMA_LORE = new ConfigSetting(config, "gui.ban.items.permanent.lore", Arrays.asList(
			"&7Should this ban be permanent",
			"",
			"&7Permanent&f: &e%is_true%",
			"",
			"&EClick &7to toggle setting"
	));

	public static final ConfigSetting GUI_BAN_ITEMS_REASON_ITEM = new ConfigSetting(config, "gui.ban.items.reason.item", CompMaterial.PAPER.name());
	public static final ConfigSetting GUI_BAN_ITEMS_REASON_NAME = new ConfigSetting(config, "gui.ban.items.reason.name", "&eBan Reason");
	public static final ConfigSetting GUI_BAN_ITEMS_REASON_LORE = new ConfigSetting(config, "gui.ban.items.reason.lore", Arrays.asList(
			"&7The reason for the ban",
			"",
			"&7Ban Reason&F:",
			"&e%ban_reason%",
			"",
			"&EClick &7to change reason"
	));

	public static final ConfigSetting GUI_BAN_ITEMS_TIME_ITEM = new ConfigSetting(config, "gui.ban.items.expiration.item", CompMaterial.CLOCK.name());
	public static final ConfigSetting GUI_BAN_ITEMS_TIME_NAME = new ConfigSetting(config, "gui.ban.items.expiration.name", "&eExpiration");
	public static final ConfigSetting GUI_BAN_ITEMS_TIME_LORE = new ConfigSetting(config, "gui.ban.items.expiration.lore", Arrays.asList(
			"&7When should this ban be lifted?",
			"&7Will only matter if it's no permanent",
			"",
			"&7Expiration Date&F: &e%ban_time%",
			"",
			"&EClick &7to change ban length"
	));

	public static final ConfigSetting GUI_BAN_ITEMS_CREATE_ITEM = new ConfigSetting(config, "gui.ban.items.create.item", CompMaterial.LIME_DYE.name());
	public static final ConfigSetting GUI_BAN_ITEMS_CREATE_NAME = new ConfigSetting(config, "gui.ban.items.create.name", "&eBan User");
	public static final ConfigSetting GUI_BAN_ITEMS_CREATE_LORE = new ConfigSetting(config, "gui.ban.items.create.lore", Collections.singletonList(
			"&EClick &7to confirm ban."
	));

	public static final ConfigSetting GUI_BAN_TYPES_TITLE = new ConfigSetting(config, "gui.ban types.title", "&7&LAuction House &f- &eBan Types");
	public static final ConfigSetting GUI_BAN_TYPES_BG_ITEM = new ConfigSetting(config, "gui.ban types.bg item", CompMaterial.BLACK_STAINED_GLASS_PANE.name());
	public static final ConfigSetting GUI_BAN_TYPES_ITEMS_TYPE_NAME = new ConfigSetting(config, "gui.ban types.items.type.name", "&e%ban_type%");
	public static final ConfigSetting GUI_BAN_TYPES_ITEMS_TYPE_LORE = new ConfigSetting(config, "gui.ban types.items.type.lore", Collections.singletonList(
			"&EClick &7to toggle ban type"
	));

	public static final ConfigSetting GUI_PLAYER_SELECTOR_TITLE = new ConfigSetting(config, "gui.player selector.title", "&7&LAuction House &f- &eSelect Player");
	public static final ConfigSetting GUI_PLAYER_SELECTOR_ITEMS_PLAYER_NAME = new ConfigSetting(config, "gui.player selector.items.player.name", "&e%player_name%");
	public static final ConfigSetting GUI_PLAYER_SELECTOR_ITEMS_PLAYER_LORE = new ConfigSetting(config, "gui.player selector.items.player.lore", Collections.singletonList("&eClick &7To select player"));


	/*  ===============================
	 *         FILTER GUI
	 *  ===============================*/
	public static final ConfigSetting GUI_FILTER_TITLE = new ConfigSetting(config, "gui.filter.title", "&7Auction House - &eFilter Selection");
	public static final ConfigSetting GUI_FILTER_BG_ITEM = new ConfigSetting(config, "gui.filter.bg item", CompMaterial.BLACK_STAINED_GLASS_PANE.name());

	public static final ConfigSetting GUI_FILTER_ITEMS_ALL_ITEM = new ConfigSetting(config, "gui.filter.items.all.item", CompMaterial.HOPPER.name());
	public static final ConfigSetting GUI_FILTER_ITEMS_ALL_NAME = new ConfigSetting(config, "gui.filter.items.all.name", "&e&lAll");
	public static final ConfigSetting GUI_FILTER_ITEMS_ALL_LORE = new ConfigSetting(config, "gui.filter.items.all.lore", Collections.singletonList("&7Click to set the filter to&f: &eAll"));

	public static final ConfigSetting GUI_FILTER_ITEMS_OWN_NAME = new ConfigSetting(config, "gui.filter.items.own.name", "&e&lYour Listings");
	public static final ConfigSetting GUI_FILTER_ITEMS_OWN_LORE = new ConfigSetting(config, "gui.filter.items.own.lore", Collections.singletonList("&7Click to set the filter to&f: &eYour Listings"));

	public static final ConfigSetting GUI_FILTER_ITEMS_SEARCH_ITEM = new ConfigSetting(config, "gui.filter.items.search.item", CompMaterial.NAME_TAG.name());
	public static final ConfigSetting GUI_FILTER_ITEMS_SEARCH_NAME = new ConfigSetting(config, "gui.filter.items.search.name", "&e&lSearch");
	public static final ConfigSetting GUI_FILTER_ITEMS_SEARCH_LORE = new ConfigSetting(config, "gui.filter.items.search.lore", Arrays.asList(
			"&7Click to set the filter to&f: &eSearch",
			"&7Current search phrase&f: &e%filter_search_phrase%"
	));

	public static final ConfigSetting GUI_FILTER_ITEMS_MISC_ITEM = new ConfigSetting(config, "gui.filter.items.misc.item", CompMaterial.OAK_SIGN.name());
	public static final ConfigSetting GUI_FILTER_ITEMS_MISC_NAME = new ConfigSetting(config, "gui.filter.items.misc.name", "&e&lMiscellaneous");
	public static final ConfigSetting GUI_FILTER_ITEMS_MISC_LORE = new ConfigSetting(config, "gui.filter.items.misc.lore", Collections.singletonList("&7Click to set the filter to&f: &eMiscellaneous"));

	public static final ConfigSetting GUI_FILTER_ITEMS_POTIONS_ITEM = new ConfigSetting(config, "gui.filter.items.potions.item", CompMaterial.SPLASH_POTION.name());
	public static final ConfigSetting GUI_FILTER_ITEMS_POTIONS_NAME = new ConfigSetting(config, "gui.filter.items.potions.name", "&e&LPotions");
	public static final ConfigSetting GUI_FILTER_ITEMS_POTIONS_LORE = new ConfigSetting(config, "gui.filter.items.potions.lore", Collections.singletonList("&7Click to set the filter to&f: &ePotions"));


	public static final ConfigSetting GUI_FILTER_ITEMS_ENCHANTS_ITEM = new ConfigSetting(config, "gui.filter.items.enchants.item", CompMaterial.ENCHANTED_BOOK.name());
	public static final ConfigSetting GUI_FILTER_ITEMS_ENCHANTS_NAME = new ConfigSetting(config, "gui.filter.items.enchants.name", "&e&lEnchantments");
	public static final ConfigSetting GUI_FILTER_ITEMS_ENCHANTS_LORE = new ConfigSetting(config, "gui.filter.items.enchants.lore", Collections.singletonList("&7Click to set the filter to&f: &eEnchantments"));

	public static final ConfigSetting GUI_FILTER_ITEMS_ARMOR_ITEM = new ConfigSetting(config, "gui.filter.items.armor.item", CompMaterial.CHAINMAIL_CHESTPLATE.name());
	public static final ConfigSetting GUI_FILTER_ITEMS_ARMOR_NAME = new ConfigSetting(config, "gui.filter.items.armor.name", "&e&lArmor");
	public static final ConfigSetting GUI_FILTER_ITEMS_ARMOR_LORE = new ConfigSetting(config, "gui.filter.items.armor.lore", Collections.singletonList("&7Click to set the filter to&f: &eArmor"));

	public static final ConfigSetting GUI_FILTER_ITEMS_WEAPONS_ITEM = new ConfigSetting(config, "gui.filter.items.weapons.item", CompMaterial.DIAMOND_SWORD.name());
	public static final ConfigSetting GUI_FILTER_ITEMS_WEAPONS_NAME = new ConfigSetting(config, "gui.filter.items.weapons.name", "&e&lWeapons");
	public static final ConfigSetting GUI_FILTER_ITEMS_WEAPONS_LORE = new ConfigSetting(config, "gui.filter.items.weapons.lore", Collections.singletonList("&7Click to set the filter to&f: &eWeapons"));

	public static final ConfigSetting GUI_FILTER_ITEMS_TOOLS_ITEM = new ConfigSetting(config, "gui.filter.items.tools.item", CompMaterial.IRON_PICKAXE.name());
	public static final ConfigSetting GUI_FILTER_ITEMS_TOOLS_NAME = new ConfigSetting(config, "gui.filter.items.tools.name", "&e&lTools");
	public static final ConfigSetting GUI_FILTER_ITEMS_TOOLS_LORE = new ConfigSetting(config, "gui.filter.items.tools.lore", Collections.singletonList("&7Click to set the filter to&f: &eTools"));

	public static final ConfigSetting GUI_FILTER_ITEMS_SPAWNERS_ITEM = new ConfigSetting(config, "gui.filter.items.spawners.item", CompMaterial.CREEPER_SPAWN_EGG.name());
	public static final ConfigSetting GUI_FILTER_ITEMS_SPAWNERS_NAME = new ConfigSetting(config, "gui.filter.items.spawners.name", "&e&LSpawners");
	public static final ConfigSetting GUI_FILTER_ITEMS_SPAWNERS_LORE = new ConfigSetting(config, "gui.filter.items.spawners.lore", Collections.singletonList("&7Click to set the filter to&f: &eSpawners"));

	public static final ConfigSetting GUI_FILTER_ITEMS_BLOCKS_ITEM = new ConfigSetting(config, "gui.filter.items.blocks.item", CompMaterial.GOLD_BLOCK.name());
	public static final ConfigSetting GUI_FILTER_ITEMS_BLOCKS_NAME = new ConfigSetting(config, "gui.filter.items.blocks.name", "&e&lBlocks");
	public static final ConfigSetting GUI_FILTER_ITEMS_BLOCKS_LORE = new ConfigSetting(config, "gui.filter.items.blocks.lore", Collections.singletonList("&7Click to set the filter to&f: &eBlocks"));

	/*  ===============================
	 *      CUSTOM ITEM FILTER GUI
	 *  ===============================*/
	public static final ConfigSetting GUI_FILTER_WHITELIST_TITLE = new ConfigSetting(config, "gui.filter whitelist.title", "&7Auction Filter - &eWhitelist");
	public static final ConfigSetting GUI_FILTER_WHITELIST_BG_ITEM = new ConfigSetting(config, "gui.filter whitelist.bg item", CompMaterial.BLACK_STAINED_GLASS_PANE.name());

	public static final ConfigSetting GUI_FILTER_WHITELIST_ITEMS_BLOCKS_ITEM = new ConfigSetting(config, "gui.filter whitelist.items.blocks.item", CompMaterial.GRASS_BLOCK.name());
	public static final ConfigSetting GUI_FILTER_WHITELIST_ITEMS_BLOCKS_NAME = new ConfigSetting(config, "gui.filter whitelist.items.blocks.name", "&e&lBlock Filters");
	public static final ConfigSetting GUI_FILTER_WHITELIST_ITEMS_BLOCKS_LORE = new ConfigSetting(config, "gui.filter whitelist.items.blocks.lore", Collections.singletonList("&7Click to adjust the item whitelist for this filter"));

	public static final ConfigSetting GUI_FILTER_WHITELIST_ITEMS_FOOD_ITEM = new ConfigSetting(config, "gui.filter whitelist.items.food.item", CompMaterial.CAKE.name());
	public static final ConfigSetting GUI_FILTER_WHITELIST_ITEMS_FOOD_NAME = new ConfigSetting(config, "gui.filter whitelist.items.food.name", "&e&lFood Filters");
	public static final ConfigSetting GUI_FILTER_WHITELIST_ITEMS_FOOD_LORE = new ConfigSetting(config, "gui.filter whitelist.items.food.lore", Collections.singletonList("&7Click to adjust the item whitelist for this filter"));

	public static final ConfigSetting GUI_FILTER_WHITELIST_ITEMS_ARMOR_ITEM = new ConfigSetting(config, "gui.filter whitelist.items.armor.item", CompMaterial.DIAMOND_HELMET.name());
	public static final ConfigSetting GUI_FILTER_WHITELIST_ITEMS_ARMOR_NAME = new ConfigSetting(config, "gui.filter whitelist.items.armor.name", "&e&LArmor Filters");
	public static final ConfigSetting GUI_FILTER_WHITELIST_ITEMS_ARMOR_LORE = new ConfigSetting(config, "gui.filter whitelist.items.armor.lore", Collections.singletonList("&7Click to adjust the item whitelist for this filter"));

	public static final ConfigSetting GUI_FILTER_WHITELIST_ITEMS_TOOLS_ITEM = new ConfigSetting(config, "gui.filter whitelist.items.tools.item", CompMaterial.IRON_PICKAXE.name());
	public static final ConfigSetting GUI_FILTER_WHITELIST_ITEMS_TOOLS_NAME = new ConfigSetting(config, "gui.filter whitelist.items.tools.name", "&e&lTool Filters");
	public static final ConfigSetting GUI_FILTER_WHITELIST_ITEMS_TOOLS_LORE = new ConfigSetting(config, "gui.filter whitelist.items.tools.lore", Collections.singletonList("&7Click to adjust the item whitelist for this filter"));

	public static final ConfigSetting GUI_FILTER_WHITELIST_ITEMS_SPAWNERS_ITEM = new ConfigSetting(config, "gui.filter whitelist.items.spawners.item", CompMaterial.SPAWNER.name());
	public static final ConfigSetting GUI_FILTER_WHITELIST_ITEMS_SPAWNERS_NAME = new ConfigSetting(config, "gui.filter whitelist.items.spawners.name", "&e&lSpawner Filters");
	public static final ConfigSetting GUI_FILTER_WHITELIST_ITEMS_SPAWNERS_LORE = new ConfigSetting(config, "gui.filter whitelist.items.spawners.lore", Collections.singletonList("&7Click to adjust the item whitelist for this filter"));

	public static final ConfigSetting GUI_FILTER_WHITELIST_ITEMS_ENCHANTS_ITEM = new ConfigSetting(config, "gui.filter whitelist.items.enchants.item", CompMaterial.ENCHANTED_BOOK.name());
	public static final ConfigSetting GUI_FILTER_WHITELIST_ITEMS_ENCHANTS_NAME = new ConfigSetting(config, "gui.filter whitelist.items.enchants.name", "&e&lEnchantment Filters");
	public static final ConfigSetting GUI_FILTER_WHITELIST_ITEMS_ENCHANTS_LORE = new ConfigSetting(config, "gui.filter whitelist.items.enchants.lore", Collections.singletonList("&7Click to adjust the item whitelist for this filter"));

	public static final ConfigSetting GUI_FILTER_WHITELIST_ITEMS_WEAPONS_ITEM = new ConfigSetting(config, "gui.filter whitelist.items.weapons.item", CompMaterial.DIAMOND_SWORD.name());
	public static final ConfigSetting GUI_FILTER_WHITELIST_ITEMS_WEAPONS_NAME = new ConfigSetting(config, "gui.filter whitelist.items.weapons.name", "&e&lWeapon Filters");
	public static final ConfigSetting GUI_FILTER_WHITELIST_ITEMS_WEAPONS_LORE = new ConfigSetting(config, "gui.filter whitelist.items.weapons.lore", Collections.singletonList("&7Click to adjust the item whitelist for this filter"));

	public static final ConfigSetting GUI_FILTER_WHITELIST_ITEMS_POTIONS_ITEM = new ConfigSetting(config, "gui.filter whitelist.items.potions.item", CompMaterial.SPLASH_POTION.name());
	public static final ConfigSetting GUI_FILTER_WHITELIST_ITEMS_POTIONS_NAME = new ConfigSetting(config, "gui.filter whitelist.items.potions.name", "&e&LPotions Filters");
	public static final ConfigSetting GUI_FILTER_WHITELIST_ITEMS_POTIONS_LORE = new ConfigSetting(config, "gui.filter whitelist.items.potions.lore", Collections.singletonList("&7Click to adjust the item whitelist for this filter"));


	public static final ConfigSetting GUI_FILTER_WHITELIST_ITEMS_MISC_ITEM = new ConfigSetting(config, "gui.filter whitelist.items.misc.item", CompMaterial.BONE_MEAL.name());
	public static final ConfigSetting GUI_FILTER_WHITELIST_ITEMS_MISC_NAME = new ConfigSetting(config, "gui.filter whitelist.items.misc.name", "&e&lMiscellaneous Filters");
	public static final ConfigSetting GUI_FILTER_WHITELIST_ITEMS_MISC_LORE = new ConfigSetting(config, "gui.filter whitelist.items.misc.lore", Collections.singletonList("&7Click to adjust the item whitelist for this filter"));

	/*  ===============================
	 *      CUSTOM ITEM FILTER GUI
	 *  ===============================*/
	public static final ConfigSetting GUI_FILTER_WHITELIST_LIST_TITLE = new ConfigSetting(config, "gui.filter whitelist list.title", "&7Filter Whitelist - &e%filter_category%");
	public static final ConfigSetting GUI_FILTER_WHITELIST_LIST_BG_ITEM = new ConfigSetting(config, "gui.filter whitelist list.bg item", CompMaterial.BLACK_STAINED_GLASS_PANE.name());

	/*  ===============================
	 *    ITEM SELL LISTING TYPE GUI
	 *  ===============================*/
	public static final ConfigSetting GUI_SELL_LISTING_TYPE_TITLE = new ConfigSetting(config, "gui.sell listing type.title", "&7Auction House - &eSelect Listing Type");
	public static final ConfigSetting GUI_SELL_LISTING_TYPE_BG_ITEM = new ConfigSetting(config, "gui.sell listing type.bg item", CompMaterial.BLACK_STAINED_GLASS_PANE.name());

	public static final ConfigSetting GUI_SELL_LISTING_TYPE_ITEMS_BIN_ITEM = new ConfigSetting(config, "gui.sell listing type.items.bin.item", CompMaterial.SUNFLOWER.name());
	public static final ConfigSetting GUI_SELL_LISTING_TYPE_ITEMS_BIN_NAME = new ConfigSetting(config, "gui.sell listing type.items.bin.name", "&e&lBin Item");
	public static final ConfigSetting GUI_SELL_LISTING_TYPE_ITEMS_BIN_LORE = new ConfigSetting(config, "gui.sell listing type.items.bin.lore", Arrays.asList(
			"&7A Bin item is an item that does not accept any",
			"&7bids, it must be bought for the listed price.",
			"",
			"&7Click to list as &aBin Item"
	));

	public static final ConfigSetting GUI_SELL_LISTING_TYPE_ITEMS_AUCTION_ITEM = new ConfigSetting(config, "gui.sell listing type.items.auction.item", CompMaterial.DIAMOND.name());
	public static final ConfigSetting GUI_SELL_LISTING_TYPE_ITEMS_AUCTION_NAME = new ConfigSetting(config, "gui.sell listing type.items.auction.name", "&e&lAuction Item");
	public static final ConfigSetting GUI_SELL_LISTING_TYPE_ITEMS_AUCTION_LORE = new ConfigSetting(config, "gui.sell listing type.items.auction.lore", Arrays.asList(
			"&7An Auction item is an item that can be bid",
			"&7on by multiple people, the highest bid wins.",
			"",
			"&7Click to list as an &aAuction"
	));

	public static final ConfigSetting GUI_SELL_LISTING_TYPE_ITEMS_RETURN_ITEM = new ConfigSetting(config, "gui.sell listing type.items.return.item", CompMaterial.BARRIER.name());
	public static final ConfigSetting GUI_SELL_LISTING_TYPE_ITEMS_RETURN_NAME = new ConfigSetting(config, "gui.sell listing type.items.return.name", "&e&lAuction House");
	public static final ConfigSetting GUI_SELL_LISTING_TYPE_ITEMS_RETURN_LORE = new ConfigSetting(config, "gui.sell listing type.items.return.lore", Arrays.asList(
			"",
			"&7Click to go to the &aAuction House"
	));

	/*  ===============================
	 *    ITEM SELL PLACE ITEM GUI
	 *  ===============================*/
	public static final ConfigSetting GUI_SELL_PLACE_ITEM_TITLE = new ConfigSetting(config, "gui.sell place item.title", "&7Auction House - &ePlace Item");
	public static final ConfigSetting GUI_SELL_PLACE_ITEM_BUNDLE_TITLE = new ConfigSetting(config, "gui.sell place item.bundle title", "&7Auction House - &ePlace Item(s)");
	public static final ConfigSetting GUI_SELL_PLACE_ITEM_BG_ITEM = new ConfigSetting(config, "gui.sell place item.bg item", CompMaterial.BLACK_STAINED_GLASS_PANE.name());

	public static final ConfigSetting GUI_SELL_PLACE_ITEM_ITEMS_CONTINUE_ITEM = new ConfigSetting(config, "gui.sell place item.items.continue.item", CompMaterial.LIME_STAINED_GLASS_PANE.name());
	public static final ConfigSetting GUI_SELL_PLACE_ITEM_ITEMS_CONTINUE_NAME = new ConfigSetting(config, "gui.sell place item.items.continue.name", "&e&lContinue");
	public static final ConfigSetting GUI_SELL_PLACE_ITEM_ITEMS_CONTINUE_LORE = new ConfigSetting(config, "gui.sell place item.items.continue.lore", Arrays.asList(
			"",
			"&7Click to continue to pricing"
	));


	public static final ConfigSetting GUI_SELL_PLACE_ITEM_ITEMS_BUNDLE_ITEM = new ConfigSetting(config, "gui.sell place item.items.bundle.item", CompMaterial.GOLD_BLOCK.name());
	public static final ConfigSetting GUI_SELL_PLACE_ITEM_ITEMS_BUNDLE_NAME = new ConfigSetting(config, "gui.sell place item.items.bundle.name", "&e&lListing Bundle");
	public static final ConfigSetting GUI_SELL_PLACE_ITEM_ITEMS_BUNDLE_LORE = new ConfigSetting(config, "gui.sell place item.items.bundle.lore", Arrays.asList(
			"&7You are currently in the bundle",
			"&7listing view.",
			"",
			"&7Click to list a single item instead"
	));

	public static final ConfigSetting GUI_SELL_PLACE_ITEM_ITEMS_SINGLE_ITEM = new ConfigSetting(config, "gui.sell place item.items.single.item", CompMaterial.DIAMOND.name());

	public static final ConfigSetting GUI_SELL_PLACE_ITEM_ITEMS_SINGLE_NAME = new ConfigSetting(config, "gui.sell place item.items.single.name", "&e&lSingle Listing");
	public static final ConfigSetting GUI_SELL_PLACE_ITEM_ITEMS_SINGLE_LORE = new ConfigSetting(config, "gui.sell place item.items.single.lore", Arrays.asList(
			"&7You are currently in the single",
			"&7item listing view.",
			"",
			"&7Click to list a bundle instead"
	));

	/*  ===============================
	 *    ITEM SELL BIN GUI
	 *  ===============================*/

	public static final ConfigSetting GUI_SELL_ITEM_ITEM_CURRENCY_ITEM = new ConfigSetting(config, "gui.global items.currency.item", CompMaterial.GOLD_INGOT.name());
	public static final ConfigSetting GUI_SELL_ITEM_ITEM_CURRENCY_NAME = new ConfigSetting(config, "gui.global items.currency.name", "&e&lCurrency");
	public static final ConfigSetting GUI_SELL_ITEM_ITEM_CURRENCY_LORE = new ConfigSetting(config, "gui.global items.currency.lore", Arrays.asList(
			"",
			"&7Click to change currency"
	));

	public static final ConfigSetting GUI_SELL_BIN_TITLE = new ConfigSetting(config, "gui.sell bin item.title", "&7Auction House - &eBin Listing");
	public static final ConfigSetting GUI_SELL_BIN_BG_ITEM = new ConfigSetting(config, "gui.sell bin item.bg item", CompMaterial.BLACK_STAINED_GLASS_PANE.name());

	public static final ConfigSetting GUI_SELL_BIN_ITEM_ITEMS_CONTINUE_ITEM = new ConfigSetting(config, "gui.sell bin item.items.confirm.item", CompMaterial.LIME_STAINED_GLASS_PANE.name());
	public static final ConfigSetting GUI_SELL_BIN_ITEM_ITEMS_CONTINUE_NAME = new ConfigSetting(config, "gui.sell bin item.items.confirm.name", "&e&lList Item");
	public static final ConfigSetting GUI_SELL_BIN_ITEM_ITEMS_CONTINUE_LORE = new ConfigSetting(config, "gui.sell bin item.items.confirm.lore", Arrays.asList(
			"",
			"&7Click to list this item"
	));

	public static final ConfigSetting GUI_SELL_BIN_ITEM_ITEMS_FEE_ITEM = new ConfigSetting(config, "gui.sell bin item.items.fee.item", "https://textures.minecraft.net/texture/a4e1da882e434829b96ec8ef242a384a53d89018fa65fee5b37deb04eccbf10e");
	public static final ConfigSetting GUI_SELL_BIN_ITEM_ITEMS_FEE_NAME = new ConfigSetting(config, "gui.sell bin item.items.fee.name", "&e&lListing Fee");
	public static final ConfigSetting GUI_SELL_BIN_ITEM_ITEMS_FEE_LORE = new ConfigSetting(config, "gui.sell bin item.items.fee.lore", Arrays.asList(
			"&cThere is a listing fee to list this item.",
			"",
			"&7Rate&f: &a%listing_fee%",
			"",
			"&eCost to list&f: %listing_fee_total%"
	));

	public static final ConfigSetting GUI_SELL_BIN_ITEM_ITEMS_TIME_ITEM = new ConfigSetting(config, "gui.sell bin item.items.time.item", CompMaterial.CLOCK.name());
	public static final ConfigSetting GUI_SELL_BIN_ITEM_ITEMS_TIME_NAME = new ConfigSetting(config, "gui.sell bin item.items.time.name", "&e&lListing Time");
	public static final ConfigSetting GUI_SELL_BIN_ITEM_ITEMS_TIME_LORE = new ConfigSetting(config, "gui.sell bin item.items.time.lore", Arrays.asList(
			"",
			"&eCurrent Time: &b%remaining_days%&fd &b%remaining_hours%&fh &b%remaining_minutes%&fm &b%remaining_seconds%&Fs",
			"",
			"&7Click to edit the listing time"
	));

	public static final ConfigSetting GUI_SELL_BIN_ITEM_ITEMS_PRICE_ITEM = new ConfigSetting(config, "gui.sell bin item.items.price.item", CompMaterial.DIAMOND.name());
	public static final ConfigSetting GUI_SELL_BIN_ITEM_ITEMS_PRICE_NAME = new ConfigSetting(config, "gui.sell bin item.items.price.name", "&e&lPrice");
	public static final ConfigSetting GUI_SELL_BIN_ITEM_ITEMS_PRICE_LORE = new ConfigSetting(config, "gui.sell bin item.items.price.lore", Arrays.asList(
			"",
			"&7The current price if &F: &a%listing_bin_price%",
			"",
			"&7Click to edit the listing price"
	));

	public static final ConfigSetting GUI_SELL_BIN_ITEM_ITEMS_PARTIAL_ENABLED_ITEM = new ConfigSetting(config, "gui.sell bin item.items.partial enabled.item", CompMaterial.LIME_STAINED_GLASS_PANE.name());
	public static final ConfigSetting GUI_SELL_BIN_ITEM_ITEMS_PARTIAL_ENABLED_NAME = new ConfigSetting(config, "gui.sell bin item.items.partial enabled.name", "&e&lQuantity Purchase");
	public static final ConfigSetting GUI_SELL_BIN_ITEM_ITEMS_PARTIAL_ENABLED_LORE = new ConfigSetting(config, "gui.sell bin item.items.partial enabled.lore", Arrays.asList(
			"",
			"&7You have partial purchases &aenabled",
			"",
			"&7Click to &cdisable &7partial purchases"
	));

	public static final ConfigSetting GUI_SELL_BIN_ITEM_ITEMS_PARTIAL_DISABLED_ITEM = new ConfigSetting(config, "gui.sell bin item.items.partial disabled.item", CompMaterial.RED_STAINED_GLASS_PANE.name());
	public static final ConfigSetting GUI_SELL_BIN_ITEM_ITEMS_PARTIAL_DISABLED_NAME = new ConfigSetting(config, "gui.sell bin item.items.partial disabled.name", "&e&lQuantity Purchase");
	public static final ConfigSetting GUI_SELL_BIN_ITEM_ITEMS_PARTIAL_DISABLED_LORE = new ConfigSetting(config, "gui.sell bin item.items.partial disabled.lore", Arrays.asList(
			"",
			"&7You have partial purchases &cdisabled",
			"",
			"&7Click to &aenable &7partial purchases"
	));


	/*  ===============================
	 *    REQUEST ITEM GUI
	 *  ===============================*/

	public static final ConfigSetting GUI_REQUEST_TITLE = new ConfigSetting(config, "gui.request.title", "&7Auction House - &eRequest Item");
	public static final ConfigSetting GUI_REQUEST_ITEMS_AMT_ITEM = new ConfigSetting(config, "gui.request.items.amt.item", CompMaterial.REPEATER.name());
	public static final ConfigSetting GUI_REQUEST_ITEMS_AMT_NAME = new ConfigSetting(config, "gui.request.items.amt.name", "&e&LRequest Amount");
	public static final ConfigSetting GUI_REQUEST_ITEMS_AMT_LORE = new ConfigSetting(config, "gui.request.items.amt.lore", Arrays.asList(
			"&7How much of this item do you want?",
			"",
			"&eCurrent Amt: &e%request_amount%",
			"",
			"&7Click to edit request amount"
	));

	public static final ConfigSetting GUI_REQUEST_ITEMS_PRICE_ITEM = new ConfigSetting(config, "gui.request.items.price.item", CompMaterial.SUNFLOWER.name());
	public static final ConfigSetting GUI_REQUEST_ITEMS_PRICE_NAME = new ConfigSetting(config, "gui.request.items.price.name", "&e&LPrice");
	public static final ConfigSetting GUI_REQUEST_ITEMS_PRICE_LORE = new ConfigSetting(config, "gui.request.items.price.lore", Arrays.asList(
			"&7How much are you going to pay for this?",
			"",
			"&eCurrent: &e%request_price%",
			"",
			"&7Click to edit request price"
	));

	public static final ConfigSetting GUI_REQUEST_ITEMS_REQUEST_ITEM = new ConfigSetting(config, "gui.request.items.request.item", CompMaterial.LIME_STAINED_GLASS_PANE.name());
	public static final ConfigSetting GUI_REQUEST_ITEMS_REQUEST_NAME = new ConfigSetting(config, "gui.request.items.request.name", "&e&lRequest Item(s)");
	public static final ConfigSetting GUI_REQUEST_ITEMS_REQUEST_LORE = new ConfigSetting(config, "gui.request.items.request.lore", Arrays.asList(
			"",
			"&7Click to make this request"
	));



	/*  ===============================
	 *    ITEM SELL AUCTION GUI
	 *  ===============================*/

	public static final ConfigSetting GUI_SELL_AUCTION_TITLE = new ConfigSetting(config, "gui.sell auction item.title", "&7Auction House - &eAuction Listing");
	public static final ConfigSetting GUI_SELL_AUCTION_BG_ITEM = new ConfigSetting(config, "gui.sell auction item.bg item", CompMaterial.BLACK_STAINED_GLASS_PANE.name());

	public static final ConfigSetting GUI_SELL_AUCTION_ITEM_ITEMS_TIME_ITEM = new ConfigSetting(config, "gui.sell auction item.items.time.item", CompMaterial.CLOCK.name());
	public static final ConfigSetting GUI_SELL_AUCTION_ITEM_ITEMS_TIME_NAME = new ConfigSetting(config, "gui.sell auction item.items.time.name", "&e&lListing Time");
	public static final ConfigSetting GUI_SELL_AUCTION_ITEM_ITEMS_TIME_LORE = new ConfigSetting(config, "gui.sell auction item.items.time.lore", Arrays.asList(
			"",
			"&eCurrent Time: &b%remaining_days%&fd &b%remaining_hours%&fh &b%remaining_minutes%&fm &b%remaining_seconds%&Fs",
			"",
			"&7Click to edit the listing time"
	));

	public static final ConfigSetting GUI_SELL_AUCTION_ITEM_ITEMS_BUYOUT_PRICE_ITEM = new ConfigSetting(config, "gui.sell auction item.items.bin price.item", CompMaterial.DIAMOND.name());
	public static final ConfigSetting GUI_SELL_AUCTION_ITEM_ITEMS_BUYOUT_PRICE_NAME = new ConfigSetting(config, "gui.sell auction item.items.bin price.name", "&e&lBuyout Price");
	public static final ConfigSetting GUI_SELL_AUCTION_ITEM_ITEMS_BUYOUT_PRICE_LORE = new ConfigSetting(config, "gui.sell auction item.items.bin price.lore", Arrays.asList(
			"",
			"&7The current buyout price is&F: &a%listing_bin_price%",
			"",
			"&7Click to edit the buyout price"
	));

	public static final ConfigSetting GUI_SELL_AUCTION_ITEM_ITEMS_STARTING_PRICE_ITEM = new ConfigSetting(config, "gui.sell auction item.items.starting price.item", CompMaterial.DIAMOND.name());
	public static final ConfigSetting GUI_SELL_AUCTION_ITEM_ITEMS_STARTING_PRICE_NAME = new ConfigSetting(config, "gui.sell auction item.items.starting price.name", "&e&lStarting Price");
	public static final ConfigSetting GUI_SELL_AUCTION_ITEM_ITEMS_STARTING_PRICE_LORE = new ConfigSetting(config, "gui.sell auction item.items.starting price.lore", Arrays.asList(
			"",
			"&7The current starting price is&F: &a%listing_start_price%",
			"",
			"&7Click to edit the starting price"
	));

	public static final ConfigSetting GUI_SELL_AUCTION_ITEM_ITEMS_INCREMENT_PRICE_ITEM = new ConfigSetting(config, "gui.sell auction item.items.increment price.item", CompMaterial.DIAMOND.name());
	public static final ConfigSetting GUI_SELL_AUCTION_ITEM_ITEMS_INCREMENT_PRICE_NAME = new ConfigSetting(config, "gui.sell auction item.items.increment price.name", "&e&lIncrement Price");
	public static final ConfigSetting GUI_SELL_AUCTION_ITEM_ITEMS_INCREMENT_PRICE_LORE = new ConfigSetting(config, "gui.sell auction item.items.increment price.lore", Arrays.asList(
			"",
			"&7The current increment price is&F: &a%listing_increment_price%",
			"",
			"&7Click to edit the increment price"
	));

	public static final ConfigSetting GUI_SELL_AUCTION_ITEM_ITEMS_BUYOUT_ENABLED_ITEM = new ConfigSetting(config, "gui.sell auction item.items.buyout enabled.item", CompMaterial.LIME_STAINED_GLASS_PANE.name());
	public static final ConfigSetting GUI_SELL_AUCTION_ITEM_ITEMS_BUYOUT_ENABLED_NAME = new ConfigSetting(config, "gui.sell auction item.items.buyout enabled.name", "&e&lAuction Buyout");
	public static final ConfigSetting GUI_SELL_AUCTION_ITEM_ITEMS_BUYOUT_ENABLED_LORE = new ConfigSetting(config, "gui.sell auction item.items.buyout enabled.lore", Arrays.asList(
			"",
			"&7You have buyout &aenabled",
			"",
			"&7Click to &cdisable &7auction buyout"
	));

	public static final ConfigSetting GUI_SELL_AUCTION_ITEM_ITEMS_BUYOUT_DISABLED_ITEM = new ConfigSetting(config, "gui.sell auction item.items.buyout disabled.item", CompMaterial.RED_STAINED_GLASS_PANE.name());
	public static final ConfigSetting GUI_SELL_AUCTION_ITEM_ITEMS_BUYOUT_DISABLED_NAME = new ConfigSetting(config, "gui.sell auction item.items.buyout disabled.name", "&e&lAuction Buyout");
	public static final ConfigSetting GUI_SELL_AUCTION_ITEM_ITEMS_BUYOUT_DISABLED_LORE = new ConfigSetting(config, "gui.sell auction item.items.buyout disabled.lore", Arrays.asList(
			"",
			"&7You have buyout &cdisabled",
			"",
			"&7Click to &aenable &7auction buyout"
	));

	public static final ConfigSetting GUI_SELL_AUCTION_ITEM_ITEMS_CONTINUE_ITEM = new ConfigSetting(config, "gui.sell auction item.items.confirm.item", CompMaterial.LIME_STAINED_GLASS_PANE.name());
	public static final ConfigSetting GUI_SELL_AUCTION_ITEM_ITEMS_CONTINUE_NAME = new ConfigSetting(config, "gui.sell auction item.items.confirm.name", "&e&lList Item");
	public static final ConfigSetting GUI_SELL_AUCTION_ITEM_ITEMS_CONTINUE_LORE = new ConfigSetting(config, "gui.sell auction item.items.confirm.lore", Arrays.asList(
			"",
			"&7Click to list this item"
	));

	/*  ===============================
	 *         AH STATS GUI
	 *  ===============================*/
	public static final ConfigSetting GUI_STATS_VIEW_SELECT_TITLE = new ConfigSetting(config, "gui.stat view select.title", "&7Auction House - &eStatistics");
	public static final ConfigSetting GUI_STATS_VIEW_SELECT_BG_ITEM = new ConfigSetting(config, "gui.stat view select.bg item", CompMaterial.BLACK_STAINED_GLASS_PANE.name());

	public static final ConfigSetting GUI_STATS_VIEW_SELECT_ITEMS_PERSONAL_USE_HEAD = new ConfigSetting(config, "gui.stat view select.items.personal.use head", true);
	public static final ConfigSetting GUI_STATS_VIEW_SELECT_ITEMS_PERSONAL_ITEM = new ConfigSetting(config, "gui.stat view select.items.personal.item", CompMaterial.DIAMOND.name());
	public static final ConfigSetting GUI_STATS_VIEW_SELECT_ITEMS_PERSONAL_NAME = new ConfigSetting(config, "gui.stat view select.items.personal.name", "&e&lPersonal Statistics");
	public static final ConfigSetting GUI_STATS_VIEW_SELECT_ITEMS_PERSONAL_LORE = new ConfigSetting(config, "gui.stat view select.items.personal.lore", Collections.singletonList("&7Click to view your own stats"));

	public static final ConfigSetting GUI_STATS_VIEW_SELECT_ITEMS_LEADERBOARD_ITEM = new ConfigSetting(config, "gui.stat view select.items.leaderboard.item", CompMaterial.NETHER_STAR.name());
	public static final ConfigSetting GUI_STATS_VIEW_SELECT_ITEMS_LEADERBOARD_NAME = new ConfigSetting(config, "gui.stat view select.items.leaderboard.name", "&e&lLeaderboard");
	public static final ConfigSetting GUI_STATS_VIEW_SELECT_ITEMS_LEADERBOARD_LORE = new ConfigSetting(config, "gui.stat view select.items.leaderboard.lore", Collections.singletonList("&7Click to view server leaderboard"));

	public static final ConfigSetting GUI_STATS_SELF_TITLE = new ConfigSetting(config, "gui.stat view self.items.title", "&7Auction House - &eYour Stats");
	public static final ConfigSetting GUI_STATS_SELF_BG_ITEM = new ConfigSetting(config, "gui.stat view self.bg item", CompMaterial.BLACK_STAINED_GLASS_PANE.name());

	public static final ConfigSetting GUI_STATS_SELF_ITEMS_CREATED_AUCTION_ITEM = new ConfigSetting(config, "gui.stat view self.items.created auction.item", CompMaterial.DIAMOND.name());
	public static final ConfigSetting GUI_STATS_SELF_ITEMS_CREATED_AUCTION_NAME = new ConfigSetting(config, "gui.stat view self.items.created auction.name", "&e&lCreated Auctions");
	public static final ConfigSetting GUI_STATS_SELF_ITEMS_CREATED_AUCTION_LORE = new ConfigSetting(config, "gui.stat view self.items.created auction.lore", Collections.singletonList("&7You created &e%created_auctions% &7auctions"));

	public static final ConfigSetting GUI_STATS_SELF_ITEMS_CREATED_BIN_ITEM = new ConfigSetting(config, "gui.stat view self.items.created bin.item", CompMaterial.HOPPER_MINECART.name());
	public static final ConfigSetting GUI_STATS_SELF_ITEMS_CREATED_BIN_NAME = new ConfigSetting(config, "gui.stat view self.items.created bin.name", "&e&lCreated Bins");
	public static final ConfigSetting GUI_STATS_SELF_ITEMS_CREATED_BIN_LORE = new ConfigSetting(config, "gui.stat view self.items.created bin.lore", Collections.singletonList("&7You created &e%created_bins% &7bins"));

	public static final ConfigSetting GUI_STATS_SELF_ITEMS_SOLD_AUCTION_ITEM = new ConfigSetting(config, "gui.stat view self.items.sold auction.item", CompMaterial.LADDER.name());
	public static final ConfigSetting GUI_STATS_SELF_ITEMS_SOLD_AUCTION_NAME = new ConfigSetting(config, "gui.stat view self.items.sold auction.name", "&e&LSold Auctions");
	public static final ConfigSetting GUI_STATS_SELF_ITEMS_SOLD_AUCTION_LORE = new ConfigSetting(config, "gui.stat view self.items.sold auction.lore", Collections.singletonList("&7You sold &e%sold_auctions% &7auction(s)"));

	public static final ConfigSetting GUI_STATS_SELF_ITEMS_SOLD_BIN_ITEM = new ConfigSetting(config, "gui.stat view self.items.sold bin.item", CompMaterial.CHEST.name());
	public static final ConfigSetting GUI_STATS_SELF_ITEMS_SOLD_BIN_NAME = new ConfigSetting(config, "gui.stat view self.items.sold bin.name", "&e&LSold Bins");
	public static final ConfigSetting GUI_STATS_SELF_ITEMS_SOLD_BIN_LORE = new ConfigSetting(config, "gui.stat view self.items.sold bin.lore", Collections.singletonList("&7You sold &e%sold_bins% &7bin(s)"));

	public static final ConfigSetting GUI_STATS_SELF_ITEMS_MONEY_EARNED_ITEM = new ConfigSetting(config, "gui.stat view self.items.money earned.item", CompMaterial.LIME_STAINED_GLASS_PANE.name());
	public static final ConfigSetting GUI_STATS_SELF_ITEMS_MONEY_EARNED_NAME = new ConfigSetting(config, "gui.stat view self.items.money earned.name", "&e&LMoney Earned");
	public static final ConfigSetting GUI_STATS_SELF_ITEMS_MONEY_EARNED_LORE = new ConfigSetting(config, "gui.stat view self.items.money earned.lore", Collections.singletonList("&7You earned &a%money_earned%"));

	public static final ConfigSetting GUI_STATS_SELF_ITEMS_MONEY_SPENT_ITEM = new ConfigSetting(config, "gui.stat view self.items.money spent.item", CompMaterial.RED_STAINED_GLASS_PANE.name());
	public static final ConfigSetting GUI_STATS_SELF_ITEMS_MONEY_SPENT_NAME = new ConfigSetting(config, "gui.stat view self.items.money spent.name", "&e&LMoney Spent");
	public static final ConfigSetting GUI_STATS_SELF_ITEMS_MONEY_SPENT_LORE = new ConfigSetting(config, "gui.stat view self.items.money spent.lore", Collections.singletonList("&7You spent &c%money_spent%"));

	public static final ConfigSetting GUI_STATS_LEADERBOARD_TITLE = new ConfigSetting(config, "gui.stat view leaderboard.items.title", "&7Auction House - &eStat Leaderboard");
	public static final ConfigSetting GUI_STATS_LEADERBOARD_BG_ITEM = new ConfigSetting(config, "gui.stat view leaderboard.bg item", CompMaterial.BLACK_STAINED_GLASS_PANE.name());

	public static final ConfigSetting GUI_STATS_LEADERBOARD_ITEMS_PLAYER_NAME = new ConfigSetting(config, "gui.stat view leaderboard.items.player.name", "&e&l%player_name%");
	public static final ConfigSetting GUI_STATS_LEADERBOARD_ITEMS_PLAYER_LORE = new ConfigSetting(config, "gui.stat view leaderboard.items.player.lore", Arrays.asList(
			"",
			"&7Statistic&f: &e%auction_statistic_name%",
			"&7Value&f: &e%auction_statistic_value%",
			""
	));

	public static final ConfigSetting GUI_STATS_LEADERBOARD_ITEMS_STAT_ITEM = new ConfigSetting(config, "gui.stat view leaderboard.items.stat.item", CompMaterial.NETHER_STAR.name());
	public static final ConfigSetting GUI_STATS_LEADERBOARD_ITEMS_STAT_NAME = new ConfigSetting(config, "gui.stat view leaderboard.items.stat.name", "&e&lStatistic Type");
	public static final ConfigSetting GUI_STATS_LEADERBOARD_ITEMS_STAT_LORE = new ConfigSetting(config, "gui.stat view leaderboard.items.stat.lore", Arrays.asList(
			"",
			"&7Selected&f: &e%statistic_name%",
			"&7Click to &achange &7viewed statistic",
			""
	));

	// other player
	public static final ConfigSetting GUI_STATS_SEARCH_TITLE = new ConfigSetting(config, "gui.stat view other.items.title", "&7Auction House - &e%player_name% Stats");
	public static final ConfigSetting GUI_STATS_SEARCH_BG_ITEM = new ConfigSetting(config, "gui.stat view other.bg item", CompMaterial.BLACK_STAINED_GLASS_PANE.name());

	public static final ConfigSetting GUI_STATS_SEARCH_ITEMS_CREATED_AUCTION_ITEM = new ConfigSetting(config, "gui.stat view other.items.created auction.item", CompMaterial.DIAMOND.name());
	public static final ConfigSetting GUI_STATS_SEARCH_ITEMS_CREATED_AUCTION_NAME = new ConfigSetting(config, "gui.stat view other.items.created auction.name", "&e&lCreated Auctions");
	public static final ConfigSetting GUI_STATS_SEARCH_ITEMS_CREATED_AUCTION_LORE = new ConfigSetting(config, "gui.stat view other.items.created auction.lore", Collections.singletonList("&7They created &e%created_auctions% &7auctions"));

	public static final ConfigSetting GUI_STATS_SEARCH_ITEMS_CREATED_BIN_ITEM = new ConfigSetting(config, "gui.stat view other.items.created bin.item", CompMaterial.HOPPER_MINECART.name());
	public static final ConfigSetting GUI_STATS_SEARCH_ITEMS_CREATED_BIN_NAME = new ConfigSetting(config, "gui.stat view other.items.created bin.name", "&e&lCreated Bins");
	public static final ConfigSetting GUI_STATS_SEARCH_ITEMS_CREATED_BIN_LORE = new ConfigSetting(config, "gui.stat view other.items.created bin.lore", Collections.singletonList("&7They created &e%created_bins% &7bins"));

	public static final ConfigSetting GUI_STATS_SEARCH_ITEMS_SOLD_AUCTION_ITEM = new ConfigSetting(config, "gui.stat view other.items.sold auction.item", CompMaterial.LADDER.name());
	public static final ConfigSetting GUI_STATS_SEARCH_ITEMS_SOLD_AUCTION_NAME = new ConfigSetting(config, "gui.stat view other.items.sold auction.name", "&e&LSold Auctions");
	public static final ConfigSetting GUI_STATS_SEARCH_ITEMS_SOLD_AUCTION_LORE = new ConfigSetting(config, "gui.stat view other.items.sold auction.lore", Collections.singletonList("&7They sold &e%sold_auctions% &7auction(s)"));

	public static final ConfigSetting GUI_STATS_SEARCH_ITEMS_SOLD_BIN_ITEM = new ConfigSetting(config, "gui.stat view other.items.sold bin.item", CompMaterial.CHEST.name());
	public static final ConfigSetting GUI_STATS_SEARCH_ITEMS_SOLD_BIN_NAME = new ConfigSetting(config, "gui.stat view other.items.sold bin.name", "&e&LSold Bins");
	public static final ConfigSetting GUI_STATS_SEARCH_ITEMS_SOLD_BIN_LORE = new ConfigSetting(config, "gui.stat view other.items.sold bin.lore", Collections.singletonList("&7They &e%sold_bins% &7bin(s)"));

	public static final ConfigSetting GUI_STATS_SEARCH_ITEMS_MONEY_EARNED_ITEM = new ConfigSetting(config, "gui.stat view other.items.money earned.item", CompMaterial.LIME_STAINED_GLASS_PANE.name());
	public static final ConfigSetting GUI_STATS_SEARCH_ITEMS_MONEY_EARNED_NAME = new ConfigSetting(config, "gui.stat view other.items.money earned.name", "&e&LMoney Earned");
	public static final ConfigSetting GUI_STATS_SEARCH_ITEMS_MONEY_EARNED_LORE = new ConfigSetting(config, "gui.stat view other.items.money earned.lore", Collections.singletonList("&7They earned &a%money_earned%"));

	public static final ConfigSetting GUI_STATS_SEARCH_ITEMS_MONEY_SPENT_ITEM = new ConfigSetting(config, "gui.stat view other.items.money spent.item", CompMaterial.RED_STAINED_GLASS_PANE.name());
	public static final ConfigSetting GUI_STATS_SEARCH_ITEMS_MONEY_SPENT_NAME = new ConfigSetting(config, "gui.stat view other.items.money spent.name", "&e&LMoney Spent");
	public static final ConfigSetting GUI_STATS_SEARCH_ITEMS_MONEY_SPENT_LORE = new ConfigSetting(config, "gui.stat view other.items.money spent.lore", Collections.singletonList("&7They spent &c%money_spent%"));


	/*  ===============================
	 *       EXPIRED ITEMS ADMIN GUI
	 *  ===============================*/
	public static final ConfigSetting GUI_EXPIRED_ITEMS_ADMIN_TITLE = new ConfigSetting(config, "gui.expired items admin.title", "&7Auction House - &eAdmin Expired");
	public static final ConfigSetting GUI_EXPIRED_ITEMS_ADMIN_BG_ITEM = new ConfigSetting(config, "gui.expired items admin.bg item", CompMaterial.BLACK_STAINED_GLASS_PANE.name());
	public static final ConfigSetting GUI_EXPIRED_ITEMS_ADMIN_ITEMS_LORE = new ConfigSetting(config, "gui.expired items admin.item lore", Collections.singletonList("&7Click to delete this item"));

	/*  ===============================
	 *         ITEM ADMIN GUI
	 *  ===============================*/
	public static final ConfigSetting GUI_ITEM_ADMIN_TITLE = new ConfigSetting(config, "gui.item admin.title", "&7Auction House - &eAdmin Item");
	public static final ConfigSetting GUI_ITEM_ADMIN_BG_ITEM = new ConfigSetting(config, "gui.item admin.bg item", CompMaterial.BLACK_STAINED_GLASS_PANE.name());

	public static final ConfigSetting GUI_ITEM_ADMIN_ITEMS_RETURN_ITEM = new ConfigSetting(config, "gui.item admin.items.send to player.item", CompMaterial.ENDER_CHEST.name());
	public static final ConfigSetting GUI_ITEM_ADMIN_ITEMS_RETURN_NAME = new ConfigSetting(config, "gui.item admin.items.send to player.name", "&a&lReturn to player");
	public static final ConfigSetting GUI_ITEM_ADMIN_ITEMS_RETURN_LORE = new ConfigSetting(config, "gui.item admin.items.send to player.lore", Collections.singletonList("&7Click to return this item to the seller"));

	public static final ConfigSetting GUI_ITEM_ADMIN_ITEMS_CLAIM_ITEM = new ConfigSetting(config, "gui.item admin.items.claim item.item", CompMaterial.HOPPER.name());
	public static final ConfigSetting GUI_ITEM_ADMIN_ITEMS_CLAIM_NAME = new ConfigSetting(config, "gui.item admin.items.claim item.name", "&a&lClaim Item");
	public static final ConfigSetting GUI_ITEM_ADMIN_ITEMS_CLAIM_LORE = new ConfigSetting(config, "gui.item admin.items.claim item.lore", Collections.singletonList("&7Click to claim this item as yours"));

	public static final ConfigSetting GUI_ITEM_ADMIN_ITEMS_DELETE_ITEM = new ConfigSetting(config, "gui.item admin.items.delete item.item", CompMaterial.BARRIER.name());
	public static final ConfigSetting GUI_ITEM_ADMIN_ITEMS_DELETE_NAME = new ConfigSetting(config, "gui.item admin.items.delete item.name", "&a&lDelete Item");
	public static final ConfigSetting GUI_ITEM_ADMIN_ITEMS_DELETE_LORE = new ConfigSetting(config, "gui.item admin.items.delete item.lore", Collections.singletonList("&7Click to delete this item"));

	public static final ConfigSetting GUI_ITEM_ADMIN_ITEMS_COPY_ITEM = new ConfigSetting(config, "gui.item admin.items.copy item.item", CompMaterial.REPEATER.name());
	public static final ConfigSetting GUI_ITEM_ADMIN_ITEMS_COPY_NAME = new ConfigSetting(config, "gui.item admin.items.copy item.name", "&a&LCopy Item");
	public static final ConfigSetting GUI_ITEM_ADMIN_ITEMS_COPY_LORE = new ConfigSetting(config, "gui.item admin.items.copy item.lore", Collections.singletonList("&7Click to copy this item"));

	/*  ===============================
	 *         BIDDING GUI
	 *  ===============================*/
	public static final ConfigSetting GUI_BIDDING_TITLE = new ConfigSetting(config, "gui.bidding.title", "&7Auction House - &eBidding");
	public static final ConfigSetting GUI_BIDDING_BG_ITEM = new ConfigSetting(config, "gui.bidding.bg item", CompMaterial.BLACK_STAINED_GLASS_PANE.name());

	public static final ConfigSetting GUI_BIDDING_ITEMS_DEFAULT_ITEM = new ConfigSetting(config, "gui.bidding.items.default amount.item", CompMaterial.SUNFLOWER.name());
	public static final ConfigSetting GUI_BIDDING_ITEMS_DEFAULT_NAME = new ConfigSetting(config, "gui.bidding.items.default amount.name", "&a&LDefault Amount");
	public static final ConfigSetting GUI_BIDDING_ITEMS_DEFAULT_LORE = new ConfigSetting(config, "gui.bidding.items.default amount.lore", Collections.singletonList("&7Click to bid default amount"));

	public static final ConfigSetting GUI_BIDDING_ITEMS_CUSTOM_ITEM = new ConfigSetting(config, "gui.bidding.items.custom amount.item", CompMaterial.OAK_SIGN.name());
	public static final ConfigSetting GUI_BIDDING_ITEMS_CUSTOM_NAME = new ConfigSetting(config, "gui.bidding.items.custom amount.name", "&a&lCustom Amount");
	public static final ConfigSetting GUI_BIDDING_ITEMS_CUSTOM_LORE = new ConfigSetting(config, "gui.bidding.items.custom amount.lore", Collections.singletonList("&7Click to bid a custom amount"));

	/*  ===============================
	 *         BUNDLES GUI
	 *  ===============================*/
	public static final ConfigSetting GUI_CREATE_BUNDLE_TITLE = new ConfigSetting(config, "gui.create bundle.title", "&7Auction House - &eBundle Items");
	public static final ConfigSetting GUI_CREATE_BUNDLE_CONFIRM_ITEM = new ConfigSetting(config, "gui.create bundle.items.confirm.item", CompMaterial.LIME_STAINED_GLASS_PANE.name());
	public static final ConfigSetting GUI_CREATE_BUNDLE_CONFIRM_NAME = new ConfigSetting(config, "gui.create bundle.items.confirm.name", "&a&LConfirm");
	public static final ConfigSetting GUI_CREATE_BUNDLE_CONFIRM_LORE = new ConfigSetting(config, "gui.create bundle.items.confirm.lore", Collections.singletonList("&7Click to confirm listing"));

	/*  ===============================
	 *         AUCTION STACKS
	 *  ===============================*/
	public static final ConfigSetting AUCTION_STACK_DETAILS_HEADER = new ConfigSetting(config, "auction stack.header", Collections.singletonList("&7&m-------------------------"));
	public static final ConfigSetting AUCTION_STACK_DETAILS_SELLER = new ConfigSetting(config, "auction stack.seller lines", Arrays.asList(
			"&eSeller&f: &b%seller%"
	));

	public static final ConfigSetting AUCTION_STACK_DETAILS_REQUESTER = new ConfigSetting(config, "auction stack.requester lines", Arrays.asList(
			"&eRequester&f: &b%requester%",
			""
	));
	public static final ConfigSetting AUCTION_STACK_DETAILS_REQUEST_PRICE = new ConfigSetting(config, "auction stack.request price lines", Arrays.asList(
			"&eOffering: &a%request_price%"
	));

	public static final ConfigSetting AUCTION_STACK_DETAILS_REQUEST_COUNT = new ConfigSetting(config, "auction stack.request count lines", Arrays.asList(
			"&eRequest Amt: &a%request_amount%",
			""
	));

	public static final ConfigSetting AUCTION_STACK_DETAILS_BUY_NOW = new ConfigSetting(config, "auction stack.buy now lines", Arrays.asList(
			"&eBuy Now: &a%buynowprice%"
	));

	public static final ConfigSetting AUCTION_STACK_DETAILS_CURRENT_PRICE = new ConfigSetting(config, "auction stack.current price lines", Collections.singletonList(
			"&eCurrent Price: &a%currentprice%"
	));

	public static final ConfigSetting AUCTION_STACK_DETAILS_BID_INCREMENT = new ConfigSetting(config, "auction stack.bid increment lines", Collections.singletonList(
			"&eBid Increment: &a%bidincrement%"
	));

	public static final ConfigSetting AUCTION_STACK_DETAILS_HIGHEST_BIDDER = new ConfigSetting(config, "auction stack.highest bidder lines", Collections.singletonList(
			"&eHighest Bidder: &a%highestbidder%"
	));

	public static final ConfigSetting AUCTION_STACK_DETAILS_PRIORITY_LISTING = new ConfigSetting(config, "auction stack.priority listing lines", Collections.singletonList(
			"<GRADIENT:F55C7A>&LPriority Listing</GRADIENT:F6BC66>"
	));

	public static final ConfigSetting AUCTION_STACK_DETAILS_TIME_LEFT = new ConfigSetting(config, "auction stack.time left lines", Arrays.asList(
			"&eTime Left: &b%remaining_days%&fd &b%remaining_hours%&fh &b%remaining_minutes%&fm &b%remaining_seconds%s"
	));

	public static final ConfigSetting AUCTION_STACK_DETAILS_INFINITE = new ConfigSetting(config, "auction stack.infinite lines", Arrays.asList(
			"&eTime Left: &bNo Expiration"
	), "this will be used instead of the time left if the item is infinite");


	public static final ConfigSetting AUCTION_STACK_PURCHASE_CONTROL_HEADER = new ConfigSetting(config, "auction stack.controls.header", Collections.singletonList("&7&m-------------------------"));
	public static final ConfigSetting AUCTION_STACK_PURCHASE_CONTROL_FOOTER = new ConfigSetting(config, "auction stack.controls.footer", Collections.singletonList("&7&m-------------------------"));
	public static final ConfigSetting AUCTION_STACK_PURCHASE_CONTROLS_INSPECTION = new ConfigSetting(config, "auction stack.controls.inspection", Collections.singletonList("&eShift Right-Click to inspect"), "This will only be added to the control lore if the item can be inspected (skulker box/bundled item)");
	public static final ConfigSetting AUCTION_STACK_PURCHASE_CONTROLS_ACCEPT_BID = new ConfigSetting(config, "auction stack.controls.accept bid", Collections.singletonList("&eRight-Click to accept the current bid"), "This will only show on items within the active listings menu on biddable items.");
	public static final ConfigSetting AUCTION_STACK_PURCHASE_CONTROLS_PRIORITY_LISTING = new ConfigSetting(config, "auction stack.controls.priority listing", Collections.singletonList("&eShift + Left-Click to prioritize listing"), "This will only show on items within the active listings menu");
	public static final ConfigSetting AUCTION_STACK_PURCHASE_CONTROLS_CANCEL_ITEM = new ConfigSetting(config, "auction stack.controls.cancel item", Collections.singletonList("&eLeft-Click to cancel this listing"));
	public static final ConfigSetting AUCTION_STACK_PURCHASE_CONTROLS_CANCEL_REQUEST = new ConfigSetting(config, "auction stack.controls.cancel request", Collections.singletonList("&eLeft-Click to cancel this request"));
	public static final ConfigSetting AUCTION_STACK_LISTING_PREVIEW_ITEM = new ConfigSetting(config, "auction stack.controls.preview item", Collections.singletonList("&ePreviewing Listing"));
	public static final ConfigSetting AUCTION_STACK_LISTING_CART = new ConfigSetting(config, "auction stack.controls.cart", Collections.singletonList("&eClick to remove from cart"));
	public static final ConfigSetting AUCTION_STACK_HIGHEST_BIDDER_ITEM = new ConfigSetting(config, "auction stack.controls.highest bidder", Collections.singletonList("&eCurrently Winning!"));

	public static final ConfigSetting AUCTION_STACK_PURCHASE_CONTROLS_BID_ON = new ConfigSetting(config, "auction stack.controls.using bid", Arrays.asList(
			"&eLeft-Click&f: &bBid",
			"&eRight-Click&f: &bBuy Now"
	), "This will be appended at the end of the lore", "If the auction item is using a bid, this will show");

	public static final ConfigSetting AUCTION_STACK_PURCHASE_CONTROLS_BID_ON_NO_BUY_NOW = new ConfigSetting(config, "auction stack.controls.using bid without buy now", Collections.singletonList(
			"&eLeft-Click&f: &bBid"
	), "This will be appended at the end of the lore", "If the auction item is using a bid, this will show");

	public static final ConfigSetting AUCTION_STACK_PURCHASE_CONTROLS_FULFILL_REQUEST = new ConfigSetting(config, "auction stack.controls.fulfill request", Collections.singletonList(
			"&eLeft-Click&f: &bFulfill Request"
	), "This will be appended at the end of the lore", "If the listing is a request this will be shown to fulfill");

	public static final ConfigSetting AUCTION_STACK_PURCHASE_CONTROLS_BID_OFF = new ConfigSetting(config, "auction stack.controls.not using bid", Collections.singletonList(
			"&eLeft-Click&f: &bBuy Now"
	), "This will be appended at the end of the lore", "If the auction item is not using a bid, this will show");


	public static final ConfigSetting AUCTION_STACK_PURCHASE_CONTROLS_ADD_TO_CART = new ConfigSetting(config, "auction stack.controls.add to cart", Collections.singletonList(
			"&eRight-Click&f: &bAdd to cart"
	), "This will be appended at the end of the lore", "If the auction item is not using a bid and the cart system is enabled");

	public static final ConfigSetting AUCTION_STACK_PURCHASE_CONTROLS_PARTIAL_BUY = new ConfigSetting(config, "auction stack.controls.partial buy", Collections.singletonList(
			"&eShift Left-Click&f: &bBuy Quantity"
	), "This will be appended at the end of the lore", "If the auction item allows partial buys, this will be added");

	public static final ConfigSetting AUCTION_STACK_AUCTION_CLOSED = new ConfigSetting(config, "auction stack.controls.auction house closed", Arrays.asList(
			"&cAuction House is Closed",
			"&eReopens in: &b%hours%&fh &b%minutes%&fm &b%seconds%&fs"
	), "This will be appended at the end of the lore if access hours is enabled and not in range");


	public static final ConfigSetting AUCTION_STACK_INFO_LAYOUT = new ConfigSetting(config, "auction stack.info layout", Arrays.asList(
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
			"%controls_header%",
			"%controls%",
			"%controls_footer%"
	), "The info order for the stacks, if a listing doesnt require one of these, Auction House will just ignore it.", "This is mainly used to just change the ordering of listing stack information");


	/*  ===============================
	 *         AUCTION SOUNDS
	 *  ===============================*/
	public static final ConfigSetting SOUNDS_LISTED_ITEM_ON_AUCTION_HOUSE = new ConfigSetting(config, "sounds.listed item on the auction house", CompSound.ENTITY_EXPERIENCE_ORB_PICKUP.friendlyName());
	public static final ConfigSetting SOUNDS_NAVIGATE_GUI_PAGES = new ConfigSetting(config, "sounds.navigated between gui pages", CompSound.ENTITY_BAT_TAKEOFF.friendlyName());
	public static final ConfigSetting SOUNDS_NOT_ENOUGH_MONEY = new ConfigSetting(config, "sounds.not enough money", CompSound.ENTITY_ITEM_BREAK.friendlyName());

	public static void setup() {
		config.load();
		config.setAutoremove(false).setAutosave(true);
		config.saveChanges();
	}
}
