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

package ca.tweetzy.auctionhouse.settings.v3;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionUsageMode;
import ca.tweetzy.core.configuration.ConfigSetting;
import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.config.ConfigEntry;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class Settings extends SettingTemp {


	public static ConfigEntry PREFIX = create("prefix", "&8[&eAuctionHouse&8]", "The prefix for the plugin");
	public static ConfigEntry LANGUAGE = create("language", "en_us", "The primary language of the plugin");

	/*
	==============================================================
						 MySQL Options
	==============================================================
	 */
	public static ConfigEntry MYSQL_ENABLED = create("settings.mysql.enabled", false, "Should Auction House use MySQL to store data?");
	public static ConfigEntry MYSQL_HOST = create("settings.mysql.host", "database.com", "The host URL/IP of the MySQL Database");
	public static ConfigEntry MYSQL_PORT = create("settings.mysql.port", 3306, "The port of the MySQL Database");
	public static ConfigEntry MYSQL_NAME = create("settings.mysql.name", "dbname", "The name of the database");
	public static ConfigEntry MYSQL_USERNAME = create("settings.mysql.username", "username", "The username of the database");
	public static ConfigEntry MYSQL_PASSWORD = create("settings.mysql.password", "password123", "The password of the database");
	public static ConfigEntry MYSQL_PARAMS = create("settings.mysql.params", "?useUnicode=yes&characterEncoding=UTF-8&useServerPrepStmts=false&rewriteBatchedStatements=true&useSSL=true", "Do not touch this if you don't know what your doing");

	/*
	==============================================================
						  Economy Settings
	==============================================================
	 */
	public static final ConfigEntry CURRENCY_ALLOW_PICK = create("economy.currency.allow pick", true, "If true, players will be able to select which currency they want to use.");
	public static final ConfigEntry CURRENCY_DEFAULT_SELECTED = create("economy.currency.default selection", "Vault/Vault", "The default currency selection, PluginName/CurrencyName -> Ex. Vault/Vault or UltraEconomy/Gems etc");
	public static final ConfigEntry CURRENCY_VAULT_SYMBOL = create("economy.currency.vault symbol", "$", "When using default/vault currency, what symbol should be used.");
	public static final ConfigEntry CURRENCY_VAULT_SYMBOL_OVERRIDES = create("economy.currency.vault symbol overrides", false, "If true, the vault symbol will override the symbol provided by the country/language combination");
	public static final ConfigEntry CURRENCY_BLACKLISTED = create("economy.currency.black listed", Collections.singletonList("UltraEconomy:Test"), "A list of owning plugins & the currency to be blacklisted. Ex. UltraEconomy:Test");
	public static final ConfigEntry CURRENCY_FORMAT_LANGUAGE = create("economy.currency.format.language", "en", "An ISO 639 alpha-2 or alpha-3 language code.");
	public static final ConfigEntry CURRENCY_FORMAT_COUNTRY = create("economy.currency.format.country", "US", "An ISO 3166 alpha-2 country code or a UN M.49 numeric-3 area code.");
	public static final ConfigEntry CURRENCY_ABBREVIATE_NUMBERS = create("economy.currency.abbreviate numbers", false, "Should numbers be abbreviated?. Example: 123,000 will become 123k ");
	public static final ConfigEntry CURRENCY_HIDE_VAULT_SYMBOL = create("economy.currency.hide vault symbol", false, "Should the specified vault symbol be hidden?");
	public static final ConfigEntry CURRENCY_STRIP_ENDING_ZEROES = create("economy.currency.strip ending zeroes", false, "If the number ends with 00, should it be stripped. EX 123.00 becomes 123");
	public static final ConfigEntry CURRENCY_TIGHT_CURRENCY_SYMBOL = create("economy.currency.tight currency symbol", false, "If true, the space between the currency symbol and number will be removed");
	public static final ConfigEntry CURRENCY_USE_GROUPING = create("economy.currency.use grouping", true, "If false, number grouping will be disabled. Ex. 123,456.78 becomes 123456.78");

	/*
	==============================================================
						  COMMAND ALIASES
	==============================================================
	 */
	public static final ConfigEntry CMD_ALIAS_MAIN = create("command aliases.main", Arrays.asList("ah", "auctions", "auctionhouses", "ahgui", "auctiongui"), "Command aliases for the main command");
	public static final ConfigEntry CMD_ALIAS_SUB_ACTIVE = create("command aliases.subcommands.active", Collections.singletonList("active"), "Command aliases for the active command");
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

	/*
	==============================================================
						  	LISTINGS
	==============================================================
	 */

	// ================ LISTING PRIORITY ================
	public static ConfigEntry LISTING_PRIORITY_ENABLED = create(  "settings.listing.listing priority.enabled", true, "If true, players will be able to pay to prioritize listings");
	public static ConfigEntry LISTING_PRIORITY_TIME_PER_BOOST = create(  "settings.listing.listing priority.time per boost", 60 * 30, "How many seconds should the priority last for each time they pay", "By default users will be able to stack boosts");
	public static ConfigEntry LISTING_PRIORITY_TIME_ALLOW_MULTI_BOOST = create( "settings.listing.listing priority.allow multiple boost", false, "If true players can boost an item multiple times before it runs out. (ex. if they have a boost active they can extend by paying before it expires)");
	public static ConfigEntry LISTING_PRIORITY_TIME_COST_PER_BOOST = create( "settings.listing.listing priority.cost per boost", 1000, "How much should it cost the player to boost their item each time");

	/*
	==============================================================
						PAYMENTS & PRICING
	==============================================================
	 */
	public static ConfigEntry MANUAL_PAYMENTS_ONLY_FOR_OFFLINE_USERS = create( "settings.listing.use stored payments for offline only", false, "If true, the usage of the manual payment collection will only be done if the user is offline");
	public static ConfigEntry STORE_PAYMENTS_FOR_MANUAL_COLLECTION = create( "settings.listing.store payments for manual collection", false, "If true, auction house will store the payments to be manually collected rather than automatically given to the player");
	public static ConfigEntry PLAYER_NEEDS_TOTAL_PRICE_TO_BID = create(  "settings.listing.bidder must have funds in account", false, "Should the player who is placing a bid on an item have the money in their account to cover the cost?");

	/*
	==============================================================
							 TAX
	==============================================================
	 */
	public static ConfigEntry TAX_ENABLED = create( "settings.tax system.enabled", false, "Should auction house use it's tax system?");
	public static ConfigEntry TAX_CHARGE_LISTING_FEE = create(  "settings.tax system.charge listing fee", true, "Should auction house charge players to list an item?");
	public static ConfigEntry TAX_LISTING_FEE = create( "settings.tax system.listing fee", 5.0, "How much should it cost to list a new item?");
	public static ConfigEntry TAX_LISTING_FEE_PERCENTAGE = create(  "settings.tax system.listing fee is percentage", true, "Should the listing fee be based on a percentage instead?");
	public static ConfigEntry TAX_CHARGE_SALES_TAX_TO_BUYER = create(  "settings.tax system.charge sale tax to buyer", false, "Should auction house tax the buyer instead of the seller?");
	public static ConfigEntry TAX_SALES_TAX_BUY_NOW_PERCENTAGE = create(  "settings.tax system.buy now sales tax", 15.0, "Tax % that should be charged on items that are bought immediately");
	public static ConfigEntry TAX_SALES_TAX_AUCTION_WON_PERCENTAGE = create(  "settings.tax system.auction won sales tax", 10.0, "Tax % that should be charged on items that are won through the auction");


//	public static final ConfigSetting SHOW_LISTING_ERROR_IN_CONSOLE = new ConfigSetting(config, "auction setting.show listing error in console", false, "If true, an exception will be thrown and shown in the console if something goes wrong during item listing");
//	public static final ConfigSetting ALLOW_REPEAT_BIDS = new ConfigSetting(config, "auction setting.allow repeated bids", true, "If true, the highest bidder on an item can keep placing bids to raise their initial bid.");
//	public static final ConfigSetting COLLECTION_BIN_ITEM_LIMIT = new ConfigSetting(config, "auction setting.collection bin item limit", 45, "How many items can be stored in the collection bin. If this is reached the player cannot list anymore items, regardless of active listings");
//	public static final ConfigSetting SELL_MENU_SKIPS_TYPE_SELECTION = new ConfigSetting(config, "auction setting.skip type selection for sell menu", false, "If true the sell menu process will skip asking for the listing type depending on your auction settings (ie. bin only or auction only)");
//	public static final ConfigSetting BUNDLE_LIST_LIMIT = new ConfigSetting(config, "auction setting.bundle listing limit.listing limit", 45, "How many bundled listings can a player sell at any given time");
//	public static final ConfigSetting BUNDLE_LIST_LIMIT_INCLUDE_COLLECTION_BIN = new ConfigSetting(config, "auction setting.bundle listing limit.include collection bin", false, "If true, collection bin bundles will also count towards this limit");
//	public static final ConfigSetting DEFAULT_BIN_LISTING_TIME = new ConfigSetting(config, "auction setting.listings times.bin item", 86400, "The default listing time for bin items (buy only items) before they expire");
//	public static final ConfigSetting DEFAULT_AUCTION_LISTING_TIME = new ConfigSetting(config, "auction setting.listings times.auction item", 604800, "The default listing time for auction items before they expire");
//	public static final ConfigSetting DEFAULT_FILTER_CATEGORY = new ConfigSetting(config, "auction setting.default filters.auction category", "ALL", "Valid Options: ALL, FOOD, ARMOR, BLOCKS, TOOLS, WEAPONS, POTIONS, SPAWNERS, ENCHANTS, MISC, SEARCH, SELF");
//	public static final ConfigSetting DEFAULT_FILTER_SORT = new ConfigSetting(config, "auction setting.default filters.auction sort", "RECENT", "Valid Options: RECENT, OLDEST, PRICE");
//	public static final ConfigSetting DEFAULT_FILTER_SALE_TYPE = new ConfigSetting(config, "auction setting.default filters.sale type", "BOTH", "Valid Options: USED_BIDDING_SYSTEM, WITHOUT_BIDDING_SYSTEM, BOTH");
//	public static final ConfigSetting ENABLE_FILTER_SYSTEM = new ConfigSetting(config, "auction setting.use filter system", true, "If false, auction house will disable the filter button.");
//
//	public static final ConfigSetting OWNER_CAN_PURCHASE_OWN_ITEM = new ConfigSetting(config, "auction setting.purchase.owner can purchase own item", false, "Should the owner of an auction be able to purchase it?", "This probably should be set to false...");
//	public static final ConfigSetting OWNER_CAN_BID_OWN_ITEM = new ConfigSetting(config, "auction setting.purchase.owner can bid on own item", false, "Should the owner of an auction be able to bid on it?", "This probably should be set to false...");
//	public static final ConfigSetting OWNER_CAN_FULFILL_OWN_REQUEST = new ConfigSetting(config, "auction setting.purchase.owner can fulfill own request", false, "Should the owner of a request be able to fulfill it", "This probably should be set to false...");
//	public static final ConfigSetting MAX_REQUEST_AMOUNT = new ConfigSetting(config, "auction setting.max request amount", 64, "How much of an item should a player be able to ask for in a single request?");
//	public static final ConfigSetting BLOCK_REQUEST_USING_FILLED_SHULKER = new ConfigSetting(config, "auction setting.block requests using filled shulkers", true, "If false, players can request make a request using a shulker that contains items");
//
//
//	public static final ConfigSetting AUTO_REFRESH_AUCTION_PAGES = new ConfigSetting(config, "auction setting.auto refresh auction pages", true, "Should auction pages auto refresh?");
//	public static final ConfigSetting AUTO_REFRESH_ACTIVE_AUCTION_PAGES = new ConfigSetting(config, "auction setting.auto refresh active auction pages", false, "Should the /ah active pages be auto refreshed?");
//	public static final ConfigSetting INCREASE_TIME_ON_BID = new ConfigSetting(config, "auction setting.increase time on bid", true, "Should the remaining time be increased when a bid is placed?");
//	public static final ConfigSetting TIME_TO_INCREASE_BY_ON_BID = new ConfigSetting(config, "auction setting.time to increase by on the bid", 20, "How many seconds should be added to the remaining time?");
//	public static final ConfigSetting ALLOW_SALE_OF_DAMAGED_ITEMS = new ConfigSetting(config, "auction setting.allow sale of damaged items", true, "If true, player's can sell items that are damaged (not max durability)");
//	public static final ConfigSetting ALLOW_FLOODGATE_PLAYERS = new ConfigSetting(config, "auction setting.allow flood gate players", false, "If true, player's who connected using floodgate (bedrock players) won't be able to use the auction house");
//	public static final ConfigSetting RESTRICT_ALL_TRANSACTIONS_TO_PERM = new ConfigSetting(config, "auction setting.restrict viewing all transactions", false, "If true, player's will need the perm: auctionhouse.transactions.viewall to view all transactions");
//	public static final ConfigSetting BLOCKED_WORLDS = new ConfigSetting(config, "auction setting.blocked worlds", Collections.singletonList("creative"), "A list of worlds that Auction House will be disabled in");
//	public static final ConfigSetting PREVENT_SALE_OF_REPAIRED_ITEMS = new ConfigSetting(config, "auction setting.prevent sale of repaired items", false, "Items repaired before this setting is turned on will still be able to be listed.");
//	public static final ConfigSetting ITEM_COPY_REQUIRES_GMC = new ConfigSetting(config, "auction setting.admin copy requires creative", false, "If true when using the admin copy option the player must be in creative");
//	public static final ConfigSetting LOG_ADMIN_ACTIONS = new ConfigSetting(config, "auction setting.log admin actions", true, "If true, any admin actions made will be logged");
//	public static final ConfigSetting ROUND_ALL_PRICES = new ConfigSetting(config, "auction setting.round all prices", false, "If true, any decimal numbers will be rounded to the nearest whole number");
//	public static final ConfigSetting DISABLE_AUTO_SAVE_MSG = new ConfigSetting(config, "auction setting.disable auto save message", false, "If true, auction house will not log the auto save task to the console");
//	public static final ConfigSetting DISABLE_CLEANUP_MSG = new ConfigSetting(config, "auction setting.disable clean up message", false, "If true, auction house will not log the clean up process to the console");
//
//	public static final ConfigSetting DISABLE_PROFILE_UPDATE_MSG = new ConfigSetting(config, "auction setting.disable profile update message", false, "If true, auction house will not log the player profile updates to the console");
//
//
//	public static final ConfigSetting RECORD_TRANSACTIONS = new ConfigSetting(config, "auction setting.record transactions", true, "Should every transaction be recorded (everything an auction is won or an item is bought)");
//	public static final ConfigSetting BUNDLE_IS_OPENED_ON_RECLAIM = new ConfigSetting(config, "auction setting.open bundle on reclaim", true, "When the player claims an expired item, if its a bundle, should it be automatically opened. (items that cannot fit will drop to the ground)");
//
//	public static final ConfigSetting BROADCAST_AUCTION_LIST = new ConfigSetting(config, "auction setting.broadcast auction list", false, "Should the entire server be alerted when a player lists an item?");
//	public static final ConfigSetting BROADCAST_AUCTION_BID = new ConfigSetting(config, "auction setting.broadcast auction bid", false, "Should the entire server be alerted when a player bids on an item?");
//	public static final ConfigSetting BROADCAST_AUCTION_SALE = new ConfigSetting(config, "auction setting.broadcast auction sale", false, "Should the entire server be alerted when an auction is sold");
//	public static final ConfigSetting BROADCAST_AUCTION_ENDING = new ConfigSetting(config, "auction setting.broadcast auction ending", false, "Should the entire server be alerted when an auction is about to end?");
//	public static final ConfigSetting BROADCAST_AUCTION_ENDING_AT_TIME = new ConfigSetting(config, "auction setting.broadcast auction ending at time", 20, "When the time on the auction item reaches this amount of seconds left, the broadcast ending will take affect ");
//
//	public static final ConfigSetting USE_REALISTIC_BIDDING = new ConfigSetting(config, "auction setting.use realistic bidding", false, "If true auction house will use a more realistic bidding approach. Ex. the previous bid is 400, and if a player bids 500, rather than making the new bid 900, it will be set to 500.");
//	public static final ConfigSetting BID_MUST_BE_HIGHER_THAN_PREVIOUS = new ConfigSetting(config, "auction setting.bid must be higher than previous", true, "Only applies if use realistic bidding is true, this will make it so that they must bid higher than the current bid.");
//	public static final ConfigSetting USE_LIVE_BID_NUMBER_IN_CONFIRM_GUI = new ConfigSetting(config, "auction setting.live bid number in confirm gui.use", true, "If true, the bid confirmation menu will auto update every 1 second by default");
//	public static final ConfigSetting LIVE_BID_NUMBER_IN_CONFIRM_GUI_RATE = new ConfigSetting(config, "auction setting.live bid number in confirm gui.rate", 1, "How often the confirm gui for bids will update");
//
//
//	public static final ConfigSetting ALLOW_USAGE_OF_BID_SYSTEM = new ConfigSetting(config, "auction setting.allow bid system usage", true, "Should players be allowed to use the bid option cmd params?");
//	public static final ConfigSetting ALLOW_USAGE_OF_BUY_NOW_SYSTEM = new ConfigSetting(config, "auction setting.allow buy now system usage", true, "Should players be allowed to use the right-click buy now feature on biddable items?");
//	public static final ConfigSetting BUY_NOW_DISABLED_BY_DEFAULT_IN_SELL_MENU = new ConfigSetting(config, "auction setting.buy now disabled in sell menu by default", false, "If true, players will just need to toggle buy now on their items to allow buy now");
//	public static final ConfigSetting AUTO_SAVE_ENABLED = new ConfigSetting(config, "auction setting.auto save.enabled", true, "Should the auto save task be enabled?");
//	public static final ConfigSetting AUTO_SAVE_EVERY = new ConfigSetting(config, "auction setting.auto save.time", 900, "How often should the auto save active? (in seconds. Ex. 900 = 15min)");
//	public static final ConfigSetting ALLOW_PURCHASE_OF_SPECIFIC_QUANTITIES = new ConfigSetting(config, "auction setting.allow purchase of specific quantities", false, "When a buy now item is right-clicked should it open a", "special gui to specify the quantity of items to buy from the stack?");
//	public static final ConfigSetting USE_REFRESH_COOL_DOWN = new ConfigSetting(config, "auction setting.use refresh cool down", true, "Should the refresh cooldown be enabled?");
//	public static final ConfigSetting REFRESH_COOL_DOWN = new ConfigSetting(config, "auction setting.refresh cool down", 2, "How many seconds should pass before the player can refresh the auction house again?");
//	public static final ConfigSetting ALLOW_PURCHASE_IF_INVENTORY_FULL = new ConfigSetting(config, "auction setting.allow purchase with full inventory", true, "Should auction house allow players to buy items even if their", "inventory is full, if true, items will be dropped on the floor if there is no room.");
//	public static final ConfigSetting ASK_FOR_BID_CONFIRMATION = new ConfigSetting(config, "auction setting.ask for bid confirmation", true, "Should Auction House open the confirmation menu for the user to confirm", "whether they actually meant to place a bid or not?");
//	public static final ConfigSetting ASK_FOR_LISTING_CONFIRMATION = new ConfigSetting(config, "auction setting.ask for listing confirmation", false, "Should Auction House ask the user to confirm the listing?");
//	public static final ConfigSetting REPLACE_HOW_TO_SELL_WITH_LIST_BUTTON = new ConfigSetting(config, "auction setting.replace how to sell with list button", false, "This will replace the \"How to Sell\" button with a List Item button");
//	public static final ConfigSetting ALLOW_USAGE_OF_SELL_GUI = new ConfigSetting(config, "auction setting.allow usage of sell gui", true, "Should the sell menu be enabled?");
//	public static final ConfigSetting FORCE_AUCTION_USAGE = new ConfigSetting(config, "auction setting.force auction usage", false, "If enabled, all items sold on the auction house must be an auction (biddable) items");
//	public static final ConfigSetting ALLOW_INDIVIDUAL_ITEM_CLAIM = new ConfigSetting(config, "auction setting.allow individual item claim", true, "If enabled, you will be able to click individual items from the expiration menu to claim them back. Otherwise you will have to use the claim all button");
//	public static final ConfigSetting FORCE_CUSTOM_BID_AMOUNT = new ConfigSetting(config, "auction setting.force custom bid amount", false, "If enabled, the bid increment line on auction items will be hidden, bid increment values will be ignored, and when you go to bid on an item, it will ask you to enter a custom amount.");
//
//	public static final ConfigSetting BIDDING_TAKES_MONEY = new ConfigSetting(config, "auction setting.bidding takes money", false, "If enabled, players will be outright charged the current bid for the item", "If they are outbid or the item is cancelled, they will get their money back. Disables ability for owners to bid on their own items!");
//	public static final ConfigSetting FORCE_SYNC_MONEY_ACTIONS = new ConfigSetting(config, "auction setting.force sync money actions", false, "If true, auction house will forcefully run a sync task to withdraw/deposit cash, this does not apply when using the commands");
//	public static final ConfigSetting EXPIRATION_TIME_LIMIT_ENABLED = new ConfigSetting(config, "auction setting.expiration time limit.enabled", false, "If true, auction house will automatically delete un claimed expired items after 7 days (default)");
//	public static final ConfigSetting EXPIRATION_TIME_LIMIT = new ConfigSetting(config, "auction setting.expiration time limit.time", 24 * 7, "In hours, what should the minimum age of an unclaimed item be inorder for it to be deleted?");
//
//	public static final ConfigSetting ASK_FOR_CANCEL_CONFIRM_ON_BID_ITEMS = new ConfigSetting(config, "auction setting.ask for cancel confirm on bid items", true, "Should Auction House ask the user if they want to cancel the item?");
//	public static final ConfigSetting ASK_FOR_CANCEL_CONFIRM_ON_NON_BID_ITEMS = new ConfigSetting(config, "auction setting.ask for cancel confirm on non bid items", false, "Should Auction House ask the user if they want to cancel the item?");
//	public static final ConfigSetting ASK_FOR_CANCEL_CONFIRM_ON_ALL_ITEMS = new ConfigSetting(config, "auction setting.ask for cancel confirm on end all", true, "Should Auction House ask the user to confirm in chat when using end all in active listings?");
//
//	public static final ConfigSetting BASE_PRICE_MUST_BE_HIGHER_THAN_BID_START = new ConfigSetting(config, "auction setting.base price must be higher than bid start", true, "Should the base price (buy now price) be higher than the initial bid starting price?");
//	public static final ConfigSetting SYNC_BASE_PRICE_TO_HIGHEST_PRICE = new ConfigSetting(config, "auction setting.sync the base price to the current price", true, "Ex. If the buy now price was 100, and the current price exceeds 100 to say 200, the buy now price will become 200.");
//	public static final ConfigSetting ADMIN_OPTION_SHOW_RETURN_ITEM = new ConfigSetting(config, "auction setting.admin option.show return to player", true);
//	public static final ConfigSetting ADMIN_OPTION_SHOW_CLAIM_ITEM = new ConfigSetting(config, "auction setting.admin option.show claim item", true);
//	public static final ConfigSetting ADMIN_OPTION_SHOW_DELETE_ITEM = new ConfigSetting(config, "auction setting.admin option.show delete item", true);
//	public static final ConfigSetting ADMIN_OPTION_SHOW_COPY_ITEM = new ConfigSetting(config, "auction setting.admin option.show copy item", true);
//
//	public static final ConfigSetting ALLOW_PLAYERS_TO_ACCEPT_BID = new ConfigSetting(config, "auction setting.allow players to accept bid", true, "If true, players can right click a biddable item inside their active listings menu to accept the current bid");
//	public static final ConfigSetting SELLERS_MUST_WAIT_FOR_TIME_LIMIT_AFTER_BID = new ConfigSetting(config, "auction setting.prevent cancellation of bid on items", false, "If true, players must wait out the duration of the auction listing if there is already a bid on it (makes them commit to selling it)");
//	public static final ConfigSetting PER_WORLD_ITEMS = new ConfigSetting(config, "auction setting.per world items", false, "If true, items can only be seen in the world they were listed in, same goes for bidding/buying/collecting");
//	public static final ConfigSetting ALLOW_PLAYERS_TO_DEFINE_AUCTION_TIME = new ConfigSetting(config, "auction setting.allow players to set auction time", false, "If true players can use -t 1 day for example to set the listing time for their item");
//	public static final ConfigSetting MAX_CUSTOM_DEFINED_TIME = new ConfigSetting(config, "auction setting.max custom defined time", 604800, "What should the limit on custom defined listing times be in seconds?");
//	public static final ConfigSetting SMART_MIN_BUY_PRICE = new ConfigSetting(config, "auction setting.smart min and buy price", false, "Will calculate buy now/min prices on a per item basis. For example, if the user states $100 and the item is in a stack of", "32, the min / buy now price will be $3200. If they provide -s or -stack in the command", "this will be ignored and the entire stack will sell for $100");
//	public static final ConfigSetting TITLE_INPUT_CANCEL_WORD = new ConfigSetting(config, "auction setting.title input cancel word", "cancel", "The word to be used to cancel chat inputs (users can also just click any block)");
//
//	public static final ConfigSetting USE_SEPARATE_FILTER_MENU = new ConfigSetting(config, "auction setting.use separate filter menu", false, "If true, rather than using a single filter item inside the auction menu", "it will open an entirely new menu to select the filter");
//	public static final ConfigSetting FILTER_ONLY_USES_WHITELIST = new ConfigSetting(config, "auction setting.filter only uses whitelist", false, "If true, auction house will ignore default filters, and only filter by the items added to the category whitelists");
//	public static final ConfigSetting FILTER_WHITELIST_USES_DURABILITY = new ConfigSetting(config, "auction setting.filter whitelist uses durability", false, "If true, the filter will look at material names and durability values for comparisons only");
//	public static final ConfigSetting SELL_MENU_REQUIRES_USER_TO_HOLD_ITEM = new ConfigSetting(config, "auction setting.require user to hold item when using sell menu", false, "If enabled, when running just /ah sell, the user will need to hold the item in their hand, otherwise they just add it in the gui.");
//	public static final ConfigSetting OPEN_MAIN_AUCTION_HOUSE_AFTER_MENU_LIST = new ConfigSetting(config, "auction setting.open main auction house after listing using menu", true, "Should the main auction house be opened after the user lists an item using the sell menu?");
//	public static final ConfigSetting SELL_MENU_CLOSE_SENDS_TO_LISTING = new ConfigSetting(config, "auction setting.sell menu close sends to listings", true, "If true, when the player clicks the close button within the sell menu, it will send them to the main auction house");
//	public static final ConfigSetting PAYMENT_HANDLE_USE_CMD = new ConfigSetting(config, "auction setting.payment handle.use command", false, "In special cases, you will want to use this");
//	public static final ConfigSetting PAYMENT_HANDLE_WITHDRAW_CMD = new ConfigSetting(config, "auction setting.payment handle.withdraw command", "eco take %player% %price%", "Command that will be executed to withdraw a player's balance");
//	public static final ConfigSetting PAYMENT_HANDLE_DEPOSIT_CMD = new ConfigSetting(config, "auction setting.payment handle.deposit command", "eco give %player% %price%", "Command that will be executed to deposit a player's balance");

	/*
	==============================================================
					TBD
	==============================================================
	 */
	public static ConfigEntry AUCTION_HOUSE_USAGE_MODE = create("settings.usage mode", AuctionUsageMode.BIN_AND_AUCTION.name(), "How core listing mechanics should function for auction house.");
	public static ConfigEntry ALLOW_OWNER_TO_BUY_OWN_ITEM = create("settings.purchasing.allow owner to buy own item", false, "Should the lister be allowed to buy their own item (... but why)");
	public static ConfigEntry ALLOW_OWNER_TO_BID_ON_OWN_ITEM = create("settings.purchasing.allow owner to bid on own item", false, "Should the lister be allowed to bid on their own item (... but why)");


	/*
	==============================================================
			   Auction / Bin Listing Specific Features
	==============================================================
	 */
	public static ConfigEntry ALLOW_BUYOUT_ON_AUCTIONS = create("settings.allow buyout on auctions", true, "If true players can list auctions that use the buyout feature");


	/*
	==============================================================
						Min & Max Item Prices
	==============================================================
	 */
	public static ConfigEntry MIN_STARTING_PRICE = create("settings.pricing.minimum starting price", 1, "The minimum price a listing can be set at");
	public static ConfigEntry MIN_BIN_PRICE = create("settings.pricing.minimum bin price", 1, "The minimum price for a bin (non biddable) listing");
	public static ConfigEntry MAX_STARTING_PRICE = create("settings.pricing.maximum starting price", 1000000000, "The maximum price a listing can be set at");
	public static ConfigEntry MAX_BIN_PRICE = create("settings.pricing.maximum bin price", 1000000000, "The maximum price for a bin (non biddable) listing");

	/*
	==============================================================
					 		   Taxes
	==============================================================
	 */

	/*
	==============================================================
						Number & Date Formats
	==============================================================
	 */
	public static ConfigEntry DATE_FORMAT = create("settings.formatting.date format", "MMMM/dd/yyyy - hh:mm a", "The default date format to be used");
	public static ConfigEntry TIME_FORMAT = create("settings.formatting.time format", "hh:mm:ss a", "The default time format to be used");
	public static ConfigEntry DATETIME_FORMAT = create("settings.formatting.datetime format", "MMMM/dd/yyyy - hh:mm:ss a", "The default combined date/time format to be used");

	/*
	==============================================================
					 Internal Timers/Cooldowns
	==============================================================
	 */
	public static ConfigEntry DELAY_REFRESH_MAIN_AUCTION = create("settings.delays.main auction refresh", 3000, "How many milliseconds must pass before the user can click the refresh button again. (0 to disable)");
	public static ConfigEntry DELAY_EXPIRED_ITEM_CLAIM = create("settings.delays.expired item claim", 100, "How many milliseconds must pass between each expired item claim (0 to disable)");
	public static ConfigEntry DELAY_EXPIRED_ITEM_CLAIM_ALL = create("settings.delays.expired collect all", 1, "How many milliseconds must pass before the user can click the collect all button again (0 to disable)");
	public static ConfigEntry DELAY_INTERNAL_LISTING_CREATE = create("settings.delays.internal create delay", 2, "How many ticks should auction house wait before actually creating the item.");
	public static ConfigEntry DELAY_NEW_LISTING = create("settings.delays.new listing", -1, "If not set to -1 (disabled) how many seconds must a player wait to list another item after listing 1?");
	public static ConfigEntry TIMER_AUTO_REFRESH_MAIN_AUCTION = create("settings.timers.main auction auto refresh", 20, "How many ticks before the page updates all items (0 to disable)");
	public static ConfigEntry TIMER_GUI_AUTO_REFRESH = create("settings.timers.refresh guis every", 10, "How many seconds should pass before the auction gui auto refreshes?");
	public static ConfigEntry TIMER_SQL_PULL_RATE = create("settings.timers.sql pull rate", 20 * 60 * 10, "How many ticks before the page updates all items (0 to disable)");
	public static ConfigEntry TIMER_ITEM_UPDATE_TIME = create("settings.timers.update items every", 1, "How many seconds should pass before the plugin updates all the times on items?");

	// ========================== GARBAGE DELETION ==========================
	public static ConfigEntry GARBAGE_DELETION_TIMED_MODE = create("settings.clean up system.timed mode", true, "If true, auction house will only run the garbage deletion task, after set amount of seconds", "otherwise if false, it will wait until the total garbage bin count", "reaches/exceeds the specified value");
	public static ConfigEntry GARBAGE_DELETION_TIMED_DELAY = create("settings.clean up system.timed delay", 60, "If timed mode is true, this value will be ran after x specified seconds, the lower this number the more frequent a new async task will be ran!");
	public static ConfigEntry GARBAGE_DELETION_MAX_ITEMS = create("settings.clean up system.max items", 30, "If timed mode is false, whenever the garbage bin reaches this number, auction house will run the deletion task.", "You should adjust this number based on your server since some servers may have more or less items being claimed / marked for garbage clean up");


	/*
	==============================================================
					 	  Click Controls
	==============================================================
	 */

	/*
	==============================================================
				       Inventory Icons & Slots
	==============================================================
	 */

	// ==================== Main Auction Menu ==================== //
	public static ConfigEntry GUI_MAIN_ROWS = create("gui.main.rows", 6, "How many rows should the gui have");
	public static ConfigEntry GUI_MAIN_FILL_SLOTS = create("gui.main.fill slots", IntStream.rangeClosed(0, 44).boxed().collect(Collectors.toList()), "Slot numbers where auction items will be placed");
	public static ConfigEntry GUI_MAIN_DECORATION = create("gui.main.decoration", Collections.singletonList(""), "Additional Decoration Items, format is MATERIAL_NAME:slot so ex. DIAMOND:1");

	public static ConfigEntry GUI_MAIN_ITEMS_COLLECTION_BIN_SLOT = create("gui.main.items.collection bin.slot", 46, "What slot should this item be placed in?");
	public static ConfigEntry GUI_MAIN_ITEMS_COLLECTION_BIN_ITEM = create("gui.main.items.collection bin.item", CompMaterial.ENDER_CHEST.name(), "The material type/texture url for this item");

	public static ConfigEntry GUI_MAIN_ITEMS_ACTIVE_LISTINGS_SLOT = create("gui.main.items.active listings.slot", 45, "What slot should this item be placed in?");
	public static ConfigEntry GUI_MAIN_ITEMS_ACTIVE_LISTINGS_ITEM = create("gui.main.items.active listings.item", CompMaterial.DIAMOND.name(), "The material type/texture url for this item");

	public static ConfigEntry GUI_MAIN_ITEMS_PROFILE_SLOT = create("gui.main.items.profile.slot", 53, "What slot should this item be placed in?");

	public static ConfigEntry GUI_MAIN_ITEMS_REFRESH_SLOT = create("gui.main.items.refresh.slot", 49, "What slot should this item be placed in?");
	public static ConfigEntry GUI_MAIN_ITEMS_REFRESH_ITEM = create("gui.main.items.refresh.item", CompMaterial.END_CRYSTAL.name(), "The material type/texture url for this item");

	public static ConfigEntry GUI_MAIN_ITEMS_SEARCH_SLOT = create("gui.main.items.search.slot", 52, "What slot should this item be placed in?");
	public static ConfigEntry GUI_MAIN_ITEMS_SEARCH_ITEM = create("gui.main.items.search.item", CompMaterial.DARK_OAK_SIGN.name(), "The material type/texture url for this item");

	// ==================== Auction Collection Menu ==================== //
	public static ConfigEntry GUI_COLLECTION_BIN_ROWS = create("gui.collection bin.rows", 6, "How many rows should the gui have");
	public static ConfigEntry GUI_COLLECTION_BIN_FILL_SLOTS = create("gui.collection bin.fill slots", IntStream.rangeClosed(0, 44).boxed().collect(Collectors.toList()), "Slot numbers where collection items will be placed");
	public static ConfigEntry GUI_COLLECTION_BIN_DECORATION = create("gui.collection bin.decoration", Collections.singletonList(""), "Additional Decoration Items, format is MATERIAL_NAME:slot so ex. DIAMOND:1");


	// ==================== Player Preferences Menu ==================== //
	public static ConfigEntry GUI_PROFILE_SETTINGS_ROWS = create("gui.profile settings.rows", 6, "How many rows should the gui have");
	public static ConfigEntry GUI_PROFILE_SETTINGS_DECORATION = create("gui.profile settings.decoration", Collections.singletonList(""), "Additional Decoration Items, format is MATERIAL_NAME:slot so ex. DIAMOND:1");

	public static ConfigEntry GUI_PROFILE_SETTINGS_ITEMS_LANGUAGE_SLOT = create("gui.profile settings.items.language.slot", 10, "What slot should this item be placed in?");
	public static ConfigEntry GUI_PROFILE_SETTINGS_ITEMS_LANGUAGE_ITEM = create("gui.profile settings.items.language.item", "https://textures.minecraft.net/texture/fc1e73023352cbc77b896fe7ea242b43143e013bec5bf314d41e5f26548fb2d2", "The material type/texture url for this item");

	public static ConfigEntry GUI_PROFILE_SETTINGS_ITEMS_SHOW_LISTING_INFO_SLOT = create("gui.profile settings.items.show listing info.slot", 12, "What slot should this item be placed in?");
	public static ConfigEntry GUI_PROFILE_SETTINGS_ITEMS_SHOW_LISTING_INFO_ITEM_ON = create("gui.profile settings.items.show listing info.item on", CompMaterial.LIME_STAINED_GLASS_PANE.name(), "The material type/texture url for this item");
	public static ConfigEntry GUI_PROFILE_SETTINGS_ITEMS_SHOW_LISTING_INFO_ITEM_OFF = create("gui.profile settings.items.show listing info.item off", CompMaterial.RED_STAINED_GLASS_PANE.name(), "The material type/texture url for this item");

	public static ConfigEntry GUI_PROFILE_SETTINGS_ITEMS_SHOW_ALERT_ON_OUTBID_SLOT = create("gui.profile settings.items.show alert on outbid.slot", 14, "What slot should this item be placed in?");
	public static ConfigEntry GUI_PROFILE_SETTINGS_ITEMS_SHOW_ALERT_ON_OUTBID_ITEM_ON = create("gui.profile settings.items.show alert on outbid.item on", CompMaterial.LIME_STAINED_GLASS_PANE.name(), "The material type/texture url for this item");
	public static ConfigEntry GUI_PROFILE_SETTINGS_ITEMS_SHOW_ALERT_ON_OUTBID_ITEM_OFF = create("gui.profile settings.items.show alert on outbid.item off", CompMaterial.RED_STAINED_GLASS_PANE.name(), "The material type/texture url for this item");

	public static ConfigEntry GUI_PROFILE_SETTINGS_ITEMS_SHOW_ALERT_ON_SALE_SLOT = create("gui.profile settings.items.show alert on sale.slot", 16, "What slot should this item be placed in?");
	public static ConfigEntry GUI_PROFILE_SETTINGS_ITEMS_SHOW_ALERT_ON_SALE_ITEM_ON = create("gui.profile settings.items.show alert on sale.item on", CompMaterial.LIME_STAINED_GLASS_PANE.name(), "The material type/texture url for this item");
	public static ConfigEntry GUI_PROFILE_SETTINGS_ITEMS_SHOW_ALERT_ON_SALE_ITEM_OFF = create("gui.profile settings.items.show alert on sale.item off", CompMaterial.RED_STAINED_GLASS_PANE.name(), "The material type/texture url for this item");

	public static ConfigEntry GUI_PROFILE_SETTINGS_ITEMS_SHOW_ALERT_ON_AUCTION_WIN_SLOT = create("gui.profile settings.items.show alert on auction win.slot", 28, "What slot should this item be placed in?");
	public static ConfigEntry GUI_PROFILE_SETTINGS_ITEMS_SHOW_ALERT_ON_AUCTION_WIN_ITEM_ON = create("gui.profile settings.items.show alert on auction win.item on", CompMaterial.LIME_STAINED_GLASS_PANE.name(), "The material type/texture url for this item");
	public static ConfigEntry GUI_PROFILE_SETTINGS_ITEMS_SHOW_ALERT_ON_AUCTION_WIN_ITEM_OFF = create("gui.profile settings.items.show alert on auction win.item off", CompMaterial.RED_STAINED_GLASS_PANE.name(), "The material type/texture url for this item");

	// ==================== Auction Ban Menu ==================== //
	public static ConfigEntry GUI_BAN_BACKGROUND = create("gui.ban.background.item", CompMaterial.BLACK_STAINED_GLASS_PANE.name(), "The background item for this menu");

	// ==================== Player Selector Menu ==================== //
	public static ConfigEntry GUI_PLAYER_SELECTOR_BACKGROUND = create("gui.player selector.background.item", CompMaterial.BLACK_STAINED_GLASS_PANE.name(), "The background item for this menu");

	/*
	==============================================================
				      	   Confirmations
	==============================================================
	 */
	public static ConfigEntry CONFIRM_LISTING = create("settings.confirmation.listing", false, "Ask for confirmation before listing an item?");
	public static ConfigEntry CONFIRM_PLACE_BID = create("settings.confirmation.place bid", false, "Ask for confirmation before bidding on an item?");
	public static ConfigEntry CONFIRM_BUY_ITEM = create("settings.confirmation.buy item", false, "Ask for confirmation before buying an item?");
	public static ConfigEntry CONFIRM_ACCEPT_BID = create("settings.confirmation.accept bid", false, "Ask for confirmation before accepting current bid?");
	public static ConfigEntry CONFIRM_CANCEL_LISTING = create("settings.confirmation.cancel listing", false, "Ask for confirmation before canceling a listing?");

	public static void init() {
		AuctionHouse.getMigrationCoreConfig().init();
	}
}
