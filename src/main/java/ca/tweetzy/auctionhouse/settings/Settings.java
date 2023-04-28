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
import ca.tweetzy.core.compatibility.XSound;
import ca.tweetzy.core.configuration.Config;
import ca.tweetzy.core.configuration.ConfigSetting;
import ca.tweetzy.core.hooks.EconomyManager;
import ca.tweetzy.flight.comp.enums.CompMaterial;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: January 12 2021
 * Time Created: 6:36 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class Settings {

	static final Config config = AuctionHouse.getInstance().getCoreConfig();

	public static final ConfigSetting LANG = new ConfigSetting(config, "lang", "en_US", "Default language file");
	public static final ConfigSetting ECONOMY_PLUGIN = new ConfigSetting(config, "economy provider", EconomyManager.getEconomy() == null ? "Vault" : EconomyManager.getEconomy().getName(),
			"Which economy should auction house use?",
			"You have the following supported economy plugins installed: \"" + EconomyManager.getManager().getPossiblePlugins().stream().collect(Collectors.joining("\", \"")) + "\"."
	);

	public static final ConfigSetting ALLOW_USAGE_OF_IN_GAME_EDITOR = new ConfigSetting(config, "Allow Usage Of This Menu In Game", true, "Once you set this to true, you will no longer be able to access it unless you enable it within the actual config.yml");
	public static final ConfigSetting UPDATE_CHECKER = new ConfigSetting(config, "update checker", true, "If true, auction house will check for updates periodically");


	/*  ===============================
	 *          BASIC SETTINGS
	 *  ===============================*/
	public static final ConfigSetting SHOW_LISTING_ERROR_IN_CONSOLE = new ConfigSetting(config, "auction setting.show listing error in console", false, "If true, an exception will be thrown and shown in the console if something goes wrong during item listing");
	public static final ConfigSetting STORE_PAYMENTS_FOR_MANUAL_COLLECTION = new ConfigSetting(config, "auction setting.store payments for manual collection", false, "If true, auction house will store the payments to be manually collected rather than automatically given to the player");
	public static final ConfigSetting ALLOW_REPEAT_BIDS = new ConfigSetting(config, "auction setting.allow repeated bids", true, "If true, the highest bidder on an item can keep placing bids to raise their initial bid.");
	public static final ConfigSetting COLLECTION_BIN_ITEM_LIMIT = new ConfigSetting(config, "auction setting.collection bin item limit", 45, "How many items can be stored in the collection bin. If this is reached the player cannot list anymore items, regardless of active listings");
	public static final ConfigSetting SELL_MENU_SKIPS_TYPE_SELECTION = new ConfigSetting(config, "auction setting.skip type selection for sell menu", false, "If true the sell menu process will skip asking for the listing type depending on your auction settings (ie. bin only or auction only)");

	public static final ConfigSetting BUNDLE_LIST_LIMIT = new ConfigSetting(config, "auction setting.bundle listing limit.listing limit", 45, "How many bundled listings can a player sell at any given time");
	public static final ConfigSetting BUNDLE_LIST_LIMIT_INCLUDE_COLLECTION_BIN = new ConfigSetting(config, "auction setting.bundle listing limit.include collection bin", false, "If true, collection bin bundles will also count towards this limit");

	public static final ConfigSetting DEFAULT_BIN_LISTING_TIME = new ConfigSetting(config, "auction setting.listings times.bin item", 86400, "The default listing time for bin items (buy only items) before they expire");
	public static final ConfigSetting DEFAULT_AUCTION_LISTING_TIME = new ConfigSetting(config, "auction setting.listings times.auction item", 604800, "The default listing time for auction items before they expire");
	public static final ConfigSetting INTERNAL_CREATE_DELAY = new ConfigSetting(config, "auction setting.internal create delay", 2, "How many ticks should auction house wait before actually creating the item.");
	public static final ConfigSetting MAX_AUCTION_PRICE = new ConfigSetting(config, "auction setting.pricing.max auction price", 1000000000, "The max price for buy only / buy now items");
	public static final ConfigSetting MAX_AUCTION_START_PRICE = new ConfigSetting(config, "auction setting.pricing.max auction start price", 1000000000, "The max price starting a bidding auction");
	public static final ConfigSetting MAX_AUCTION_INCREMENT_PRICE = new ConfigSetting(config, "auction setting.pricing.max auction increment price", 1000000000, "The max amount for incrementing a bid.");
	public static final ConfigSetting MIN_AUCTION_PRICE = new ConfigSetting(config, "auction setting.pricing.min auction price", 1, "The min price for buy only / buy now items");
	public static final ConfigSetting MIN_AUCTION_START_PRICE = new ConfigSetting(config, "auction setting.pricing.min auction start price", 1, "The min price starting a bidding auction");
	public static final ConfigSetting MIN_AUCTION_INCREMENT_PRICE = new ConfigSetting(config, "auction setting.pricing.min auction increment price", 1, "The min amount for incrementing a bid.");
	public static final ConfigSetting OWNER_CAN_PURCHASE_OWN_ITEM = new ConfigSetting(config, "auction setting.purchase.owner can purchase own item", false, "Should the owner of an auction be able to purchase it?", "This probably should be set to false...");
	public static final ConfigSetting OWNER_CAN_BID_OWN_ITEM = new ConfigSetting(config, "auction setting.purchase.owner can bid on own item", false, "Should the owner of an auction be able to bid on it?", "This probably should be set to false...");
	public static final ConfigSetting AUTO_REFRESH_AUCTION_PAGES = new ConfigSetting(config, "auction setting.auto refresh auction pages", true, "Should auction pages auto refresh?");
	public static final ConfigSetting AUTO_REFRESH_DOES_SLOT_CLEAR = new ConfigSetting(config, "auction setting.auto refresh does slot clear", true, "If true, on every refresh, the slots will be cleared (replaced by default item) then the actual listings will be placed.");
	public static final ConfigSetting USE_SHORT_NUMBERS_ON_ITEMS = new ConfigSetting(config, "auction setting.use short numbers", false, "Should numbers be shortened into a prefixed form?");
	public static final ConfigSetting USE_SHORT_NUMBERS_ON_PLAYER_BALANCE = new ConfigSetting(config, "auction setting.use short numbers on balance", false, "Should numbers be shortened into a prefixed form for the player balance?");
	public static final ConfigSetting INCREASE_TIME_ON_BID = new ConfigSetting(config, "auction setting.increase time on bid", true, "Should the remaining time be increased when a bid is placed?");
	public static final ConfigSetting TIME_TO_INCREASE_BY_ON_BID = new ConfigSetting(config, "auction setting.time to increase by on the bid", 20, "How many seconds should be added to the remaining time?");
	public static final ConfigSetting ALLOW_SALE_OF_DAMAGED_ITEMS = new ConfigSetting(config, "auction setting.allow sale of damaged items", true, "If true, player's can sell items that are damaged (not max durability)");
	public static final ConfigSetting ALLOW_FLOODGATE_PLAYERS = new ConfigSetting(config, "auction setting.allow flood gate players", false, "If true, player's who connected using floodgate (bedrock players) won't be able to use the auction house");
	public static final ConfigSetting RESTRICT_ALL_TRANSACTIONS_TO_PERM = new ConfigSetting(config, "auction setting.restrict viewing all transactions", false, "If true, player's will need the perm: auctionhouse.transactions.viewall to view all transactions");
	public static final ConfigSetting BLOCKED_WORLDS = new ConfigSetting(config, "auction setting.blocked worlds", Collections.singletonList("creative"), "A list of worlds that Auction House will be disabled in");
	public static final ConfigSetting PREVENT_SALE_OF_REPAIRED_ITEMS = new ConfigSetting(config, "auction setting.prevent sale of repaired items", false, "Items repaired before this setting is turned on will still be able to be listed.");
	public static final ConfigSetting SYNCHRONIZE_ITEM_ADD = new ConfigSetting(config, "auction setting.synchronize item add", false, "If an item is being added to a player's inventory, the process will be ran synchronously");
	public static final ConfigSetting ITEM_COPY_REQUIRES_GMC = new ConfigSetting(config, "auction setting.admin copy requires creative", false, "If true when using the admin copy option the player must be in creative");
	public static final ConfigSetting LOG_ADMIN_ACTIONS = new ConfigSetting(config, "auction setting.log admin actions", true, "If true, any admin actions made will be logged");
	public static final ConfigSetting ROUND_ALL_PRICES = new ConfigSetting(config, "auction setting.round all prices", false, "If true, any decimal numbers will be rounded to the nearest whole number");
	public static final ConfigSetting DISABLE_AUTO_SAVE_MSG = new ConfigSetting(config, "auction setting.disable auto save message", false, "If true, auction house will not log the auto save task to the console");
	public static final ConfigSetting DISABLE_CLEANUP_MSG = new ConfigSetting(config, "auction setting.disable clean up message", false, "If true, auction house will not log the clean up process to the console");

	public static final ConfigSetting DISABLE_PROFILE_UPDATE_MSG = new ConfigSetting(config, "auction setting.disable profile update message", false, "If true, auction house will not log the player profile updates to the console");

	public static final ConfigSetting TICK_UPDATE_TIME = new ConfigSetting(config, "auction setting.tick auctions every", 1, "How many seconds should pass before the plugin updates all the times on items?");

	public static final ConfigSetting GARBAGE_DELETION_TIMED_MODE = new ConfigSetting(config, "auction setting.garbage deletion.timed mode", true, "If true, auction house will only run the garbage deletion task, after set amount of seconds", "otherwise if false, it will wait until the total garbage bin count", "reaches/exceeds the specified value");
	public static final ConfigSetting GARBAGE_DELETION_TIMED_DELAY = new ConfigSetting(config, "auction setting.garbage deletion.timed delay", 60, "If timed mode is true, this value will be ran after x specified seconds, the lower this number the more frequent a new async task will be ran!");
	public static final ConfigSetting GARBAGE_DELETION_MAX_ITEMS = new ConfigSetting(config, "auction setting.garbage deletion.max items", 30, "If timed mode is false, whenever the garbage bin reaches this number, auction house will run the deletion task.", "You should adjust this number based on your server since some servers may have more or less items being claimed / marked for garbage clean up");

	public static final ConfigSetting CLAIM_MS_DELAY = new ConfigSetting(config, "auction setting.item claim delay", 100, "How many ms should a player wait before being allowed to claim an item?, Ideally you don't wanna change this. It's meant to prevent auto clicker dupe claims");

	public static final ConfigSetting TICK_UPDATE_GUI_TIME = new ConfigSetting(config, "auction setting.refresh gui every", 10, "How many seconds should pass before the auction gui auto refreshes?");
	public static final ConfigSetting RECORD_TRANSACTIONS = new ConfigSetting(config, "auction setting.record transactions", true, "Should every transaction be recorded (everything an auction is won or an item is bought)");
	public static final ConfigSetting BUNDLE_IS_OPENED_ON_RECLAIM = new ConfigSetting(config, "auction setting.open bundle on reclaim", true, "When the player claims an expired item, if its a bundle, should it be automatically opened. (items that cannot fit will drop to the ground)");

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
	public static final ConfigSetting ALLOW_PURCHASE_IF_INVENTORY_FULL = new ConfigSetting(config, "auction setting.allow purchase with full inventory", true, "Should auction house allow players to buy items even if their", "inventory is full, if true, items will be dropped on the floor if there is no room.");
	public static final ConfigSetting ASK_FOR_BID_CONFIRMATION = new ConfigSetting(config, "auction setting.ask for bid confirmation", true, "Should Auction House open the confirmation menu for the user to confirm", "whether they actually meant to place a bid or not?");
	public static final ConfigSetting ASK_FOR_LISTING_CONFIRMATION = new ConfigSetting(config, "auction setting.ask for listing confirmation", false, "Should Auction House ask the user to confirm the listing?");
	public static final ConfigSetting REPLACE_HOW_TO_SELL_WITH_LIST_BUTTON = new ConfigSetting(config, "auction setting.replace how to sell with list button", false, "This will replace the \"How to Sell\" button with a List Item button");
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

	public static final ConfigSetting BASE_PRICE_MUST_BE_HIGHER_THAN_BID_START = new ConfigSetting(config, "auction setting.base price must be higher than bid start", true, "Should the base price (buy now price) be higher than the initial bid starting price?");
	public static final ConfigSetting SYNC_BASE_PRICE_TO_HIGHEST_PRICE = new ConfigSetting(config, "auction setting.sync the base price to the current price", true, "Ex. If the buy now price was 100, and the current price exceeds 100 to say 200, the buy now price will become 200.");

	public static final ConfigSetting CURRENCY_FORMAT = new ConfigSetting(config, "auction setting.currency format", "%,.2f");
	public static final ConfigSetting STRIP_ZEROS_ON_WHOLE_NUMBERS = new ConfigSetting(config, "auction setting.strip zeros on whole numbers", false, "If the price / amount is a whole number (ex. 40.00) it will drop the .00");

	public static final ConfigSetting ADMIN_OPTION_SHOW_RETURN_ITEM = new ConfigSetting(config, "auction setting.admin option.show return to player", true);
	public static final ConfigSetting ADMIN_OPTION_SHOW_CLAIM_ITEM = new ConfigSetting(config, "auction setting.admin option.show claim item", true);
	public static final ConfigSetting ADMIN_OPTION_SHOW_DELETE_ITEM = new ConfigSetting(config, "auction setting.admin option.show delete item", true);
	public static final ConfigSetting ADMIN_OPTION_SHOW_COPY_ITEM = new ConfigSetting(config, "auction setting.admin option.show copy item", true);

	public static final ConfigSetting USE_ALTERNATE_CURRENCY_FORMAT = new ConfigSetting(config, "auction setting.use alternate currency format", false, "If true, $123,456.78 will become $123.456,78");
	public static final ConfigSetting USE_FLAT_NUMBER_FORMAT = new ConfigSetting(config, "auction setting.use flat number format", false, "If true, $123,456.78 will become $12345678");
	public static final ConfigSetting DATE_FORMAT = new ConfigSetting(config, "auction setting.date format", "MMM dd, yyyy hh:mm aa", "You can learn more about date formats by googling SimpleDateFormat patterns or visiting this link", "https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html");
	public static final ConfigSetting ALLOW_PLAYERS_TO_ACCEPT_BID = new ConfigSetting(config, "auction setting.allow players to accept bid", true, "If true, players can right click a biddable item inside their active listings menu to accept the current bid");
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

	public static final ConfigSetting ALL_FILTER_ENABLED = new ConfigSetting(config, "auction setting.enabled filters.all", true, "Should this filter be enabled?");
	public static final ConfigSetting FOOD_FILTER_ENABLED = new ConfigSetting(config, "auction setting.enabled filters.food", true, "Should this filter be enabled?");
	public static final ConfigSetting ARMOR_FILTER_ENABLED = new ConfigSetting(config, "auction setting.enabled filters.armor", true, "Should this filter be enabled?");
	public static final ConfigSetting BLOCKS_FILTER_ENABLED = new ConfigSetting(config, "auction setting.enabled filters.blocks", true, "Should this filter be enabled?");
	public static final ConfigSetting TOOLS_FILTER_ENABLED = new ConfigSetting(config, "auction setting.enabled filters.tools", true, "Should this filter be enabled?");
	public static final ConfigSetting WEAPONS_FILTER_ENABLED = new ConfigSetting(config, "auction setting.enabled filters.weapons", true, "Should this filter be enabled?");
	public static final ConfigSetting SPAWNERS_FILTER_ENABLED = new ConfigSetting(config, "auction setting.enabled filters.spawners", true, "Should this filter be enabled?");
	public static final ConfigSetting ENCHANTS_FILTER_ENABLED = new ConfigSetting(config, "auction setting.enabled filters.enchants", true, "Should this filter be enabled?");
	public static final ConfigSetting MISC_FILTER_ENABLED = new ConfigSetting(config, "auction setting.enabled filters.misc", true, "Should this filter be enabled?");
	public static final ConfigSetting SEARCH_FILTER_ENABLED = new ConfigSetting(config, "auction setting.enabled filters.search", true, "Should this filter be enabled?");
	public static final ConfigSetting SELF_FILTER_ENABLED = new ConfigSetting(config, "auction setting.enabled filters.self", true, "Should this filter be enabled?");
	public static final ConfigSetting USE_AUCTION_CHEST_MODE = new ConfigSetting(config, "auction setting.use auction chest mode", false, "Enabling this will make it so players can only access the auction through the auction chest");
	public static final ConfigSetting AUTO_BSTATS = new ConfigSetting(config, "auction setting.auto bstats", true, "Auto enable bStats");

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

	public static final ConfigSetting DISCORD_MSG_FIELD_BIN_LISTING_PRICE_NAME = new ConfigSetting(config, "discord.field.bin listing price.name", "Price");
	public static final ConfigSetting DISCORD_MSG_FIELD_BIN_LISTING_PRICE_VALUE = new ConfigSetting(config, "discord.field.bin listing price.value", "%item_price%");
	public static final ConfigSetting DISCORD_MSG_FIELD_BIN_LISTING_PRICE_INLINE = new ConfigSetting(config, "discord.field.bin listing price.inline", true);

	public static final ConfigSetting DISCORD_MSG_FIELD_ITEM_AMOUNT_NAME = new ConfigSetting(config, "discord.field.item amount.name", "Quantity");
	public static final ConfigSetting DISCORD_MSG_FIELD_ITEM_AMOUNT_VALUE = new ConfigSetting(config, "discord.field.item amount.value", "%item_amount%");
	public static final ConfigSetting DISCORD_MSG_FIELD_ITEM_AMOUNT_INLINE = new ConfigSetting(config, "discord.field.item amount.inline", true);

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

	public static final ConfigSetting GUI_BACK_BTN_ITEM = new ConfigSetting(config, "gui.global items.back button.item", "ARROW", "Settings for the previous page button");
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
	public static final ConfigSetting GUI_REFRESH_BTN_LORE = new ConfigSetting(config, "gui.global items.refresh button.lore", Arrays.asList("&7Click to refresh the currently", "&7available auction listings."));


	/*  ===============================
	 *         MAIN AUCTION GUI
	 *  ===============================*/
	public static final ConfigSetting GUI_AUCTION_HOUSE_TITLE = new ConfigSetting(config, "gui.auction house.title", "&7Auction House");

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
			"&e&lBalance &a$%player_balance%"
	));

	public static final ConfigSetting GUI_AUCTION_HOUSE_ITEMS_COLLECTION_BIN_ENABLED = new ConfigSetting(config, "gui.auction house.items.collection bin.enabled", true);
	public static final ConfigSetting GUI_AUCTION_HOUSE_ITEMS_COLLECTION_BIN_SLOT = new ConfigSetting(config, "gui.auction house.items.collection bind.slot", 46, "Valid Slots: 45 - 53");
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
			"",
			"&7Left-Click to change item category",
			"&7Right-Click to change change auction type",
			"&7Shift Right-Click to change sort order",
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
			"&7Price for entire stack&f: &a$%original_stack_price%",
			"&7Price per item&f: &a$%price_per_item%",
			"",
			"&7Purchase Qty&f: &e%purchase_quantity%",
			"&7Total&f: &a$%purchase_price%"
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
	public static final ConfigSetting GUI_PAYMENT_COLLECTION_PAYMENT_NAME = new ConfigSetting(config, "gui.payment collection.payment.name", "&a&l$%payment_amount%");
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
	public static final ConfigSetting GUI_MIN_ITEM_PRICES_TITLE = new ConfigSetting(config, "gui.min item prices.title", "&7&LMinimum Item Prices");
	public static final ConfigSetting GUI_MIN_ITEM_PRICES_LORE = new ConfigSetting(config, "gui.min item prices.lore", Arrays.asList(
			"&7&m-------------------------",
			"&7Minimum Price&f: &a%price%",
			"",
			"&7Click to delete"
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

	/*  ===============================
	 *      TRANSACTIONS VIEW GUI
	 *  ===============================*/
	public static final ConfigSetting GUI_TRANSACTION_VIEW_TITLE = new ConfigSetting(config, "gui.transaction view.title", "&7&LViewing Transaction");
	public static final ConfigSetting GUI_TRANSACTION_VIEW_BACKGROUND_FILL = new ConfigSetting(config, "gui.transaction view.background.fill", true);
	public static final ConfigSetting GUI_TRANSACTION_VIEW_BACKGROUND_ITEM = new ConfigSetting(config, "gui.transaction view.background.item", "BLACK_STAINED_GLASS_PANE");

	public static final ConfigSetting GUI_TRANSACTION_VIEW_ITEM_SELLER_NAME = new ConfigSetting(config, "gui.transaction view.items.seller.name", "&e%seller_name%");
	public static final ConfigSetting GUI_TRANSACTION_VIEW_ITEM_SELLER_LORE = new ConfigSetting(config, "gui.transaction view.items.seller.lore", Arrays.asList(
			"&7This is the player who sold the item.",
			"&7ID&F: &e%seller_id%"
	));

	public static final ConfigSetting GUI_TRANSACTION_VIEW_ITEM_BUYER_NAME = new ConfigSetting(config, "gui.transaction view.items.buyer.name", "&e%buyer_name%");
	public static final ConfigSetting GUI_TRANSACTION_VIEW_ITEM_BUYER_LORE = new ConfigSetting(config, "gui.transaction view.items.buyer.lore", Arrays.asList(
			"&7This is the player who bought the item.",
			"&7ID&F: &e%buyer_id%"
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
	public static final ConfigSetting GUI_BANS_TITLE = new ConfigSetting(config, "gui.bans.title", "&7&LAuction House &f- &eBans");
	public static final ConfigSetting GUI_BANS_BG_ITEM = new ConfigSetting(config, "gui.bans.bg item", CompMaterial.BLACK_STAINED_GLASS_PANE.name());
	public static final ConfigSetting GUI_BANS_BAN_NAME = new ConfigSetting(config, "gui.bans.ban name", "&e%player_name%");
	public static final ConfigSetting GUI_BANS_BAN_LORE = new ConfigSetting(config, "gui.bans.ban lore", Arrays.asList(
			"&7Time Remaining&f: &e%ban_amount%",
			"&7Ban Reason&f: &e%ban_reason%",
			"",
			"&7Right-Click to unban this user"
	));


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
	public static final ConfigSetting GUI_SELL_BIN_TITLE = new ConfigSetting(config, "gui.sell bin item.title", "&7Auction House - &eBin Listing");
	public static final ConfigSetting GUI_SELL_BIN_BG_ITEM = new ConfigSetting(config, "gui.sell bin item.bg item", CompMaterial.BLACK_STAINED_GLASS_PANE.name());

	public static final ConfigSetting GUI_SELL_BIN_ITEM_ITEMS_CONTINUE_ITEM = new ConfigSetting(config, "gui.sell bin item.items.confirm.item", CompMaterial.LIME_STAINED_GLASS_PANE.name());
	public static final ConfigSetting GUI_SELL_BIN_ITEM_ITEMS_CONTINUE_NAME = new ConfigSetting(config, "gui.sell bin item.items.confirm.name", "&e&lList Item");
	public static final ConfigSetting GUI_SELL_BIN_ITEM_ITEMS_CONTINUE_LORE = new ConfigSetting(config, "gui.sell bin item.items.confirm.lore", Arrays.asList(
			"",
			"&7Click to list this item"
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
			"&7The current price if &F: &a$%listing_bin_price%",
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
			"&7The current buyout price is&F: &a$%listing_bin_price%",
			"",
			"&7Click to edit the buyout price"
	));

	public static final ConfigSetting GUI_SELL_AUCTION_ITEM_ITEMS_STARTING_PRICE_ITEM = new ConfigSetting(config, "gui.sell auction item.items.starting price.item", CompMaterial.DIAMOND.name());
	public static final ConfigSetting GUI_SELL_AUCTION_ITEM_ITEMS_STARTING_PRICE_NAME = new ConfigSetting(config, "gui.sell auction item.items.starting price.name", "&e&lStarting Price");
	public static final ConfigSetting GUI_SELL_AUCTION_ITEM_ITEMS_STARTING_PRICE_LORE = new ConfigSetting(config, "gui.sell auction item.items.starting price.lore", Arrays.asList(
			"",
			"&7The current starting price is&F: &a$%listing_start_price%",
			"",
			"&7Click to edit the starting price"
	));

	public static final ConfigSetting GUI_SELL_AUCTION_ITEM_ITEMS_INCREMENT_PRICE_ITEM = new ConfigSetting(config, "gui.sell auction item.items.increment price.item", CompMaterial.DIAMOND.name());
	public static final ConfigSetting GUI_SELL_AUCTION_ITEM_ITEMS_INCREMENT_PRICE_NAME = new ConfigSetting(config, "gui.sell auction item.items.increment price.name", "&e&lIncrement Price");
	public static final ConfigSetting GUI_SELL_AUCTION_ITEM_ITEMS_INCREMENT_PRICE_LORE = new ConfigSetting(config, "gui.sell auction item.items.increment price.lore", Arrays.asList(
			"",
			"&7The current increment price is&F: &a$%listing_increment_price%",
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
	public static final ConfigSetting GUI_STATS_SELF_ITEMS_MONEY_EARNED_LORE = new ConfigSetting(config, "gui.stat view self.items.money earned.lore", Collections.singletonList("&7You earned &a$%money_earned%"));

	public static final ConfigSetting GUI_STATS_SELF_ITEMS_MONEY_SPENT_ITEM = new ConfigSetting(config, "gui.stat view self.items.money spent.item", CompMaterial.RED_STAINED_GLASS_PANE.name());
	public static final ConfigSetting GUI_STATS_SELF_ITEMS_MONEY_SPENT_NAME = new ConfigSetting(config, "gui.stat view self.items.money spent.name", "&e&LMoney Spent");
	public static final ConfigSetting GUI_STATS_SELF_ITEMS_MONEY_SPENT_LORE = new ConfigSetting(config, "gui.stat view self.items.money spent.lore", Collections.singletonList("&7You spent &c$%money_spent%"));

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
	public static final ConfigSetting GUI_STATS_SEARCH_ITEMS_MONEY_EARNED_LORE = new ConfigSetting(config, "gui.stat view other.items.money earned.lore", Collections.singletonList("&7They earned &a$%money_earned%"));

	public static final ConfigSetting GUI_STATS_SEARCH_ITEMS_MONEY_SPENT_ITEM = new ConfigSetting(config, "gui.stat view other.items.money spent.item", CompMaterial.RED_STAINED_GLASS_PANE.name());
	public static final ConfigSetting GUI_STATS_SEARCH_ITEMS_MONEY_SPENT_NAME = new ConfigSetting(config, "gui.stat view other.items.money spent.name", "&e&LMoney Spent");
	public static final ConfigSetting GUI_STATS_SEARCH_ITEMS_MONEY_SPENT_LORE = new ConfigSetting(config, "gui.stat view other.items.money spent.lore", Collections.singletonList("&7They spent &c$%money_spent%"));


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
			"&eSeller&f: &b%seller%",
			""
	));

	public static final ConfigSetting AUCTION_STACK_DETAILS_BUY_NOW = new ConfigSetting(config, "auction stack.buy now lines", Arrays.asList(
			"&eBuy Now: &a$%buynowprice%",
			""
	));

	public static final ConfigSetting AUCTION_STACK_DETAILS_CURRENT_PRICE = new ConfigSetting(config, "auction stack.current price lines", Collections.singletonList(
			"&eCurrent Price: &a$%currentprice%"
	));

	public static final ConfigSetting AUCTION_STACK_DETAILS_BID_INCREMENT = new ConfigSetting(config, "auction stack.bid increment lines", Collections.singletonList(
			"&eBid Increment: &a$%bidincrement%"
	));

	public static final ConfigSetting AUCTION_STACK_DETAILS_HIGHEST_BIDDER = new ConfigSetting(config, "auction stack.highest bidder lines", Collections.singletonList(
			"&eHighest Bidder: &a%highestbidder%"
	));

	public static final ConfigSetting AUCTION_STACK_DETAILS_TIME_LEFT = new ConfigSetting(config, "auction stack.time left lines", Arrays.asList(
			"",
			"&eTime Left: &b%remaining_days%&fd &b%remaining_hours%&fh &b%remaining_minutes%&fm &b%remaining_seconds%s"
	));

	public static final ConfigSetting AUCTION_STACK_DETAILS_INFINITE = new ConfigSetting(config, "auction stack.infinite lines", Arrays.asList(
			"",
			"&eTime Left: &bNo Expiration"
	), "this will be used instead of the time left if the item is infinite");


	public static final ConfigSetting AUCTION_STACK_PURCHASE_CONTROL_HEADER = new ConfigSetting(config, "auction stack.controls.header", Collections.singletonList("&7&m-------------------------"));
	public static final ConfigSetting AUCTION_STACK_PURCHASE_CONTROL_FOOTER = new ConfigSetting(config, "auction stack.controls.footer", Collections.singletonList("&7&m-------------------------"));
	public static final ConfigSetting AUCTION_STACK_PURCHASE_CONTROLS_INSPECTION = new ConfigSetting(config, "auction stack.controls.inspection", Collections.singletonList("&eShift Right-Click to inspect"), "This will only be added to the control lore if the item can be inspected (skulker box/bundled item)");
	public static final ConfigSetting AUCTION_STACK_PURCHASE_CONTROLS_ACCEPT_BID = new ConfigSetting(config, "auction stack.controls.accept bid", Collections.singletonList("&eRight-Click to accept the current bid"), "This will only show on items within the active listings menu on biddable items.");
	public static final ConfigSetting AUCTION_STACK_PURCHASE_CONTROLS_CANCEL_ITEM = new ConfigSetting(config, "auction stack.controls.cancel item", Collections.singletonList("&eLeft-Click to cancel this listing"));
	public static final ConfigSetting AUCTION_STACK_LISTING_PREVIEW_ITEM = new ConfigSetting(config, "auction stack.controls.preview item", Collections.singletonList("&ePreviewing Listing"));
	public static final ConfigSetting AUCTION_STACK_HIGHEST_BIDDER_ITEM = new ConfigSetting(config, "auction stack.controls.highest bidder", Collections.singletonList("&eCurrently Winning!"));

	public static final ConfigSetting AUCTION_STACK_PURCHASE_CONTROLS_BID_ON = new ConfigSetting(config, "auction stack.controls.using bid", Arrays.asList(
			"&eLeft-Click&f: &bBid",
			"&eRight-Click&f: &bBuy Now"
	), "This will be appended at the end of the lore", "If the auction item is using a bid, this will show");

	public static final ConfigSetting AUCTION_STACK_PURCHASE_CONTROLS_BID_ON_NO_BUY_NOW = new ConfigSetting(config, "auction stack.controls.using bid without buy now", Collections.singletonList(
			"&eLeft-Click&f: &bBid"
	), "This will be appended at the end of the lore", "If the auction item is using a bid, this will show");


	public static final ConfigSetting AUCTION_STACK_PURCHASE_CONTROLS_BID_OFF = new ConfigSetting(config, "auction stack.controls.not using bid", Collections.singletonList(
			"&eLeft-Click&f: &bBuy Now"
	), "This will be appended at the end of the lore", "If the auction item is not using a bid, this will show");

	public static final ConfigSetting AUCTION_STACK_PURCHASE_CONTROLS_PARTIAL_BUY = new ConfigSetting(config, "auction stack.controls.partial buy", Collections.singletonList(
			"&eShift Left-Click&f: &bBuy Quantity"
	), "This will be appended at the end of the lore", "If the auction item allows partial buys, this will be added");

	/*  ===============================
	 *         AUCTION SOUNDS
	 *  ===============================*/
	public static final ConfigSetting SOUNDS_LISTED_ITEM_ON_AUCTION_HOUSE = new ConfigSetting(config, "sounds.listed item on the auction house", XSound.ENTITY_EXPERIENCE_ORB_PICKUP.parseSound().name());
	public static final ConfigSetting SOUNDS_NAVIGATE_GUI_PAGES = new ConfigSetting(config, "sounds.navigated between gui pages", XSound.ENTITY_BAT_TAKEOFF.parseSound().name());
	public static final ConfigSetting SOUNDS_NOT_ENOUGH_MONEY = new ConfigSetting(config, "sounds.not enough money", XSound.ENTITY_ITEM_BREAK.parseSound().name());

	public static void setup() {
		config.load();
		config.setAutoremove(true).setAutosave(true);
		config.saveChanges();
	}
}
