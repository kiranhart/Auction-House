package ca.tweetzy.auctionhouse.settings;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.core.compatibility.XMaterial;
import ca.tweetzy.core.compatibility.XSound;
import ca.tweetzy.core.configuration.Config;
import ca.tweetzy.core.configuration.ConfigSetting;

import java.util.Arrays;
import java.util.Collections;

/**
 * The current file has been created by Kiran Hart
 * Date Created: January 12 2021
 * Time Created: 6:36 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class Settings {

    static final Config config = AuctionHouse.getInstance().getCoreConfig();

    public static final ConfigSetting LANG = new ConfigSetting(config, "lang", "en_US", "Default language file");
    /*  ===============================
     *          BASIC SETTINGS
     *  ===============================*/
    public static final ConfigSetting DEFAULT_AUCTION_TIME = new ConfigSetting(config, "auction setting.default auction house", 60, "The default auction time before an item expires (in seconds)");
    public static final ConfigSetting MAX_AUCTION_PRICE = new ConfigSetting(config, "auction setting.pricing.max auction price", 1000000000, "The max price for buy only / buy now items");
    public static final ConfigSetting MAX_AUCTION_START_PRICE = new ConfigSetting(config, "auction setting.pricing.max auction start price", 1000000000, "The max price starting a bidding auction");
    public static final ConfigSetting MAX_AUCTION_INCREMENT_PRICE = new ConfigSetting(config, "auction setting.pricing.max auction increment price", 1000000000, "The max amount for incrementing a bid.");
    public static final ConfigSetting MIN_AUCTION_PRICE = new ConfigSetting(config, "auction setting.pricing.min auction price", 1, "The min price for buy only / buy now items");
    public static final ConfigSetting MIN_AUCTION_START_PRICE = new ConfigSetting(config, "auction setting.pricing.min auction start price", 1, "The min price starting a bidding auction");
    public static final ConfigSetting MIN_AUCTION_INCREMENT_PRICE = new ConfigSetting(config, "auction setting.pricing.min auction increment price", 1, "The min amount for incrementing a bid.");
    public static final ConfigSetting OWNER_CAN_PURCHASE_OWN_ITEM = new ConfigSetting(config, "auction setting.purchase.owner can purchase own item", false, "Should the owner of an auction be able to purchase it?", "This probably should be set to false...");
    public static final ConfigSetting OWNER_CAN_BID_OWN_ITEM = new ConfigSetting(config, "auction setting.purchase.owner can bid on own item", false, "Should the owner of an auction be able to bid on it?", "This probably should be set to false...");
    public static final ConfigSetting AUTO_REFRESH_AUCTION_PAGES = new ConfigSetting(config, "auction setting.auto refresh auction pages", true, "Should auction pages auto refresh?");
    public static final ConfigSetting USE_SHORT_NUMBERS_ON_ITEMS = new ConfigSetting(config, "auction setting.use short numbers", false, "Should numbers be shortened into a prefixed form?");
    public static final ConfigSetting INCREASE_TIME_ON_BID = new ConfigSetting(config, "auction setting.increase time on bid", true, "Should the remaining time be increased when a bid is placed?");
    public static final ConfigSetting TIME_TO_INCREASE_BY_ON_BID = new ConfigSetting(config, "auction setting.time to increase by on the bid", 20, "How many seconds should be added to the remaining time?");
    public static final ConfigSetting TICK_UPDATE_TIME = new ConfigSetting(config, "auction setting.tick auctions every", 1, "How many seconds should pass before the plugin updates all the times on items?");
    public static final ConfigSetting TICK_UPDATE_GUI_TIME = new ConfigSetting(config, "auction setting.refresh gui every", 10, "How many seconds should pass before the auction gui auto refreshes?");
    public static final ConfigSetting REFRESH_GUI_WHEN_BID = new ConfigSetting(config, "auction setting.refresh gui when bid", true, "Should the auction gui be re-opened (not redrawn) when a user places a bid, so they get the latest items?");
    public static final ConfigSetting RECORD_TRANSACTIONS = new ConfigSetting(config, "auction setting.record transactions", true, "Should every transaction be recorded (everything an auction is won or an item is bought)");
    public static final ConfigSetting BROADCAST_AUCTION_LIST = new ConfigSetting(config, "auction setting.broadcast auction list", false, "Should the entire server be alerted when a player lists an item?");
    public static final ConfigSetting PLAYER_NEEDS_TOTAL_PRICE_TO_BID = new ConfigSetting(config, "auction setting.bidder must have funds in account", false, "Should the player who is placing a bid on an item have the money in their account to cover the cost?");
    public static final ConfigSetting USE_ASYNC_GUI_REFRESH = new ConfigSetting(config, "auction setting.use async gui refresh", true, "Should the gui refresh be done using asynchronous tasks?", "This may reduce lag that can be caused, but", "items may have a flickering effect inside the gui.");
    public static final ConfigSetting SEND_REMOVED_ITEM_BACK_TO_PLAYER = new ConfigSetting(config, "auction setting.send removed item back to player", true, "Should items removed by staff from the auction house be sent back to the player?");
    public static final ConfigSetting ALLOW_USAGE_OF_BID_SYSTEM = new ConfigSetting(config, "auction setting.allow bid system usage", true, "Should players be allowed to use the bid option cmd params?");
    public static final ConfigSetting ALLOW_USAGE_OF_BUY_NOW_SYSTEM = new ConfigSetting(config, "auction setting.allow buy now system usage", true, "Should players be allowed to use the right-click buy now feature on biddable items?");
    public static final ConfigSetting AUTO_SAVE_ENABLED = new ConfigSetting(config, "auction setting.auto save.enabled", true, "Should the auto save task be enabled?");
    public static final ConfigSetting AUTO_SAVE_EVERY = new ConfigSetting(config, "auction setting.auto save.time", 900, "How often should the auto save active? (in seconds. Ex. 900 = 15min)");
    public static final ConfigSetting ALLOW_PURCHASE_OF_SPECIFIC_QUANTITIES = new ConfigSetting(config, "auction setting.allow purchase of specific quantities", false, "When a buy now item is right-clicked should it open a", "special gui to specify the quantity of items to buy from the stack?");
    public static final ConfigSetting USE_REFRESH_COOL_DOWN = new ConfigSetting(config, "auction setting.use refresh cool down", true, "Should the refresh cooldown be enabled?");
    public static final ConfigSetting REFRESH_COOL_DOWN = new ConfigSetting(config, "auction setting.refresh cool down", 2, "How many seconds should pass before the player can refresh the auction house again?");
    public static final ConfigSetting ALLOW_PURCHASE_IF_INVENTORY_FULL = new ConfigSetting(config, "auction setting.allow purchase with full inventory", true, "Should auction house allow players to buy items even if their", "inventory is full, if true, items will be dropped on the floor if there is no room.");
    public static final ConfigSetting ASK_FOR_BID_CONFIRMATION = new ConfigSetting(config, "auction setting.ask for bid confirmation", true, "Should Auction House open the confirmation menu for the user to confirm", "whether they actually meant to place a bid or not?");

    public static final ConfigSetting ASK_FOR_CANCEL_CONFIRM_ON_BID_ITEMS = new ConfigSetting(config, "auction setting.ask for cancel confirm on bid items", true, "Should Auction House ask the user if they want to cancel the item?");
    public static final ConfigSetting ASK_FOR_CANCEL_CONFIRM_ON_NON_BID_ITEMS = new ConfigSetting(config, "auction setting.ask for cancel confirm on non bid items", false, "Should Auction House ask the user if they want to cancel the item?");

    public static final ConfigSetting BASE_PRICE_MUST_BE_HIGHER_THAN_BID_START = new ConfigSetting(config, "auction setting.base price must be higher than bid start", true, "Should the base price (buy now price) be higher than the initial bid starting price?");
    public static final ConfigSetting SYNC_BASE_PRICE_TO_HIGHEST_PRICE = new ConfigSetting(config, "auction setting.sync the base price to the current price", true, "Ex. If the buy now price was 100, and the current price exceeds 100 to say 200, the buy now price will become 200.");
    public static final ConfigSetting USE_ALTERNATE_CURRENCY_FORMAT = new ConfigSetting(config, "auction setting.use alternate currency format", false, "If true, $123,456.78 will become $123.456,78");
    public static final ConfigSetting DATE_FORMAT = new ConfigSetting(config, "auction setting.date format", "MMM dd, yyyy hh:mm aa", "You can learn more about date formats by googling SimpleDateFormat patterns or visiting this link", "https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html");
    public static final ConfigSetting ALLOW_PLAYERS_TO_ACCEPT_BID = new ConfigSetting(config, "auction setting.allow players to accept bid", true, "If true, players can right click a biddable item inside their active listings menu to accept the current bid");

    public static final ConfigSetting USE_SEPARATE_FILTER_MENU = new ConfigSetting(config, "auction setting.use separate filter menu", false, "If true, rather than using a single filter item inside the auction menu", "it will open an entirely new menu to select the filter");

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

    public static final ConfigSetting ALLOW_ITEM_BUNDLES = new ConfigSetting(config, "auction setting.bundles.enabled", true, "If true, players can use -b in the sell command to bundle all similar items into a single item.");
    public static final ConfigSetting ITEM_BUNDLE_ITEM = new ConfigSetting(config, "auction setting.bundles.item", XMaterial.GOLD_BLOCK.name());
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
    public static final ConfigSetting CLICKS_NON_BID_ITEM_QTY_PURCHASE = new ConfigSetting(config, "auction setting.clicks.non bid item qty purchase", "RIGHT",
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

    public static final ConfigSetting CLICKS_REMOVE_ITEM = new ConfigSetting(config, "auction setting.clicks.remove item", "MIDDLE",
            "Valid Click Types",
            "LEFT",
            "RIGHT",
            "SHIFT_LEFT",
            "SHIFT_RIGHT",
            "MIDDLE",
            "",
            "&cIf you overlap click types (ex. LEFT for both inspect and buy) things will go crazy."
    );

    /*  ===============================
     *         DATABASE OPTIONS
     *  ===============================*/
    public static final ConfigSetting DATABASE_USE = new ConfigSetting(config, "database.use database", false, "Should the plugin use a database to store shop data?");
    public static final ConfigSetting DATABASE_HOST = new ConfigSetting(config, "database.host", "localhost", "What is the connection url/host");
    public static final ConfigSetting DATABASE_PORT = new ConfigSetting(config, "database.port", 3306, "What is the port to database (default is 3306)");
    public static final ConfigSetting DATABASE_NAME = new ConfigSetting(config, "database.name", "plugin_dev", "What is the name of the database?");
    public static final ConfigSetting DATABASE_USERNAME = new ConfigSetting(config, "database.username", "root", "What is the name of the user connecting?");
    public static final ConfigSetting DATABASE_PASSWORD = new ConfigSetting(config, "database.password", "Password1.", "What is the password to the user connecting?");
    public static final ConfigSetting DATABASE_USE_SSL = new ConfigSetting(config, "database.use ssl", true, "Should the database connection use ssl?");

    public static final ConfigSetting DATABASE_UPDATE_ON_EVENT = new ConfigSetting(config, "database.update on event", true, "When an item is listed/bought/expired/cancelled should auction house update the database immediately?");


    /*  ===============================
     *         DISCORD WEBHOOK
     *  ===============================*/
    public static final ConfigSetting DISCORD_ENABLED = new ConfigSetting(config, "discord.enabled", true, "Should the discord webhook feature be enabled?");
    public static final ConfigSetting DISCORD_ALERT_ON_AUCTION_START = new ConfigSetting(config, "discord.alert on auction start", true, "Should a message be sent to the discord server when someone lists a new auction item");
    public static final ConfigSetting DISCORD_ALERT_ON_AUCTION_FINISH = new ConfigSetting(config, "discord.alert on auction finish", true, "Should a message when an auction finishes?");
    public static final ConfigSetting DISCORD_WEBHOOKS = new ConfigSetting(config, "discord.webhooks", Collections.singletonList("https://discord.com/api/webhooks/821837927444119563/Yd3cWzVB56Tk_VuN1Lv2iGgvsbZt2YV5SDyCkVo6EjRAUqJk3nA2nSG9PH_Bl6rcFNnz"), "A list of webhook urls (channels) you want a message sent to");
    public static final ConfigSetting DISCORD_MSG_USERNAME = new ConfigSetting(config, "discord.user.username", "Auction House", "The name of the user who will send the message");
    public static final ConfigSetting DISCORD_MSG_PFP = new ConfigSetting(config, "discord.user.avatar picture", "https://cdn.kiranhart.com/spigot/auctionhouse/icon.png", "The avatar image of the discord user");
    public static final ConfigSetting DISCORD_MSG_USE_RANDOM_COLOUR = new ConfigSetting(config, "discord.msg.use random colour", true, "colour of the message bar");
    public static final ConfigSetting DISCORD_MSG_DEFAULT_COLOUR = new ConfigSetting(config, "discord.msg.default colour", "137-100-100", "The color of the embed, it needs to be in hsb format.", "Separate the numbers with a -");
    public static final ConfigSetting DISCORD_MSG_START_TITLE = new ConfigSetting(config, "discord.msg.auction start title", "New Auction Available", "The title of the message when a new auction is made");
    public static final ConfigSetting DISCORD_MSG_FINISH_TITLE = new ConfigSetting(config, "discord.msg.auction finish title", "Auction Finished", "The title of th message when an auction finishes");

    public static final ConfigSetting DISCORD_MSG_FIELD_SELLER_NAME = new ConfigSetting(config, "discord.msg.seller.name", "Seller");
    public static final ConfigSetting DISCORD_MSG_FIELD_SELLER_VALUE = new ConfigSetting(config, "discord.msg.seller.value", "%seller%");
    public static final ConfigSetting DISCORD_MSG_FIELD_SELLER_INLINE = new ConfigSetting(config, "discord.msg.seller.inline", true);

    public static final ConfigSetting DISCORD_MSG_FIELD_BUYER_NAME = new ConfigSetting(config, "discord.msg.buyer.name", "Buyer");
    public static final ConfigSetting DISCORD_MSG_FIELD_BUYER_VALUE = new ConfigSetting(config, "discord.msg.buyer.value", "%buyer%");
    public static final ConfigSetting DISCORD_MSG_FIELD_BUYER_INLINE = new ConfigSetting(config, "discord.msg.buyer.inline", true);

    public static final ConfigSetting DISCORD_MSG_FIELD_BUY_NOW_PRICE_NAME = new ConfigSetting(config, "discord.msg.buy now price.name", "Buy Now Price");
    public static final ConfigSetting DISCORD_MSG_FIELD_BUY_NOW_PRICE_VALUE = new ConfigSetting(config, "discord.msg.buy now price.value", "$%buy_now_price%");
    public static final ConfigSetting DISCORD_MSG_FIELD_BUY_NOW_PRICE_INLINE = new ConfigSetting(config, "discord.msg.buy now price.inline", true);

    public static final ConfigSetting DISCORD_MSG_FIELD_FINAL_PRICE_NAME = new ConfigSetting(config, "discord.msg.final price.name", "Final Price");
    public static final ConfigSetting DISCORD_MSG_FIELD_FINAL_PRICE_VALUE = new ConfigSetting(config, "discord.msg.final price.value", "$%final_price%");
    public static final ConfigSetting DISCORD_MSG_FIELD_FINAL_PRICE_INLINE = new ConfigSetting(config, "discord.msg.final price.inline", true);

    public static final ConfigSetting DISCORD_MSG_FIELD_IS_BID_NAME = new ConfigSetting(config, "discord.msg.is bid.name", "Was Bid");
    public static final ConfigSetting DISCORD_MSG_FIELD_IS_BID_VALUE = new ConfigSetting(config, "discord.msg.is bid.value", "%is_bid%");
    public static final ConfigSetting DISCORD_MSG_FIELD_IS_BID_INLINE = new ConfigSetting(config, "discord.msg.is bid.inline", true);

    public static final ConfigSetting DISCORD_MSG_FIELD_PURCHASE_TYPE_NAME = new ConfigSetting(config, "discord.msg.purchase type.name", "Purchase Type");
    public static final ConfigSetting DISCORD_MSG_FIELD_PURCHASE_TYPE_VALUE = new ConfigSetting(config, "discord.msg.purchase type.value", "%purchase_type%");
    public static final ConfigSetting DISCORD_MSG_FIELD_PURCHASE_INLINE = new ConfigSetting(config, "discord.msg.purchase type.inline", true);

    public static final ConfigSetting DISCORD_MSG_FIELD_ITEM_NAME = new ConfigSetting(config, "discord.msg.item.name", "Item Name");
    public static final ConfigSetting DISCORD_MSG_FIELD_ITEM_VALUE = new ConfigSetting(config, "discord.msg.item.value", "%item_name%");
    public static final ConfigSetting DISCORD_MSG_FIELD_ITEM_INLINE = new ConfigSetting(config, "discord.msg.item.inline", true);

    public static final ConfigSetting DISCORD_MSG_FIELD_ITEM_AMOUNT_NAME = new ConfigSetting(config, "discord.msg.item amount.name", "Item Amount");
    public static final ConfigSetting DISCORD_MSG_FIELD_ITEM_AMOUNT_VALUE = new ConfigSetting(config, "discord.msg.item amount.value", "%item_amount%");
    public static final ConfigSetting DISCORD_MSG_FIELD_ITEM_AMOUNT_INLINE = new ConfigSetting(config, "discord.msg.item amount.inline", true);

    /*  ===============================
     *          BLACK LISTED
     *  ===============================*/
    public static final ConfigSetting BLOCKED_ITEMS = new ConfigSetting(config, "blocked items", Collections.singletonList("ENDER_CHEST"), "Materials that should be blocked (not allowed to sell)");
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
    public static final ConfigSetting GUI_BACK_BTN_ITEM = new ConfigSetting(config, "gui.global items.back button.item", "ARROW", "Settings for the back button");
    public static final ConfigSetting GUI_BACK_BTN_NAME = new ConfigSetting(config, "gui.global items.back button.name", "&e<< Back");
    public static final ConfigSetting GUI_BACK_BTN_LORE = new ConfigSetting(config, "gui.global items.back button.lore", Arrays.asList("&7Click the button to go", "&7back to the previous page."));

    public static final ConfigSetting GUI_CLOSE_BTN_ITEM = new ConfigSetting(config, "gui.global items.close button.item", "BARRIER", "Settings for the close button");
    public static final ConfigSetting GUI_CLOSE_BTN_NAME = new ConfigSetting(config, "gui.global items.close button.name", "&cClose");
    public static final ConfigSetting GUI_CLOSE_BTN_LORE = new ConfigSetting(config, "gui.global items.close button.lore", Collections.singletonList("&7Click to close this menu."));

    public static final ConfigSetting GUI_NEXT_BTN_ITEM = new ConfigSetting(config, "gui.global items.next button.item", "ARROW", "Settings for the next button");
    public static final ConfigSetting GUI_NEXT_BTN_NAME = new ConfigSetting(config, "gui.global items.next button.name", "&eNext >>");
    public static final ConfigSetting GUI_NEXT_BTN_LORE = new ConfigSetting(config, "gui.global items.next button.lore", Arrays.asList("&7Click the button to go", "&7to the next page."));

    public static final ConfigSetting GUI_REFRESH_BTN_ITEM = new ConfigSetting(config, "gui.global items.refresh button.item", "CHEST", "Settings for the refresh page");
    public static final ConfigSetting GUI_REFRESH_BTN_NAME = new ConfigSetting(config, "gui.global items.refresh button.name", "&6&LRefresh Page");
    public static final ConfigSetting GUI_REFRESH_BTN_LORE = new ConfigSetting(config, "gui.global items.refresh button.lore", Arrays.asList("&7Click to refresh the currently", "&7available auction listings."));


    /*  ===============================
     *         MAIN AUCTION GUI
     *  ===============================*/
    public static final ConfigSetting GUI_AUCTION_HOUSE_TITLE = new ConfigSetting(config, "gui.auction house.title", "&7Auction House");

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

    public static final ConfigSetting GUI_AUCTION_HOUSE_ITEMS_TRANSACTIONS_ITEM = new ConfigSetting(config, "gui.auction house.items.transactions.item", "PAPER");
    public static final ConfigSetting GUI_AUCTION_HOUSE_ITEMS_TRANSACTIONS_NAME = new ConfigSetting(config, "gui.auction house.items.transactions.name", "&e&lTransactions");
    public static final ConfigSetting GUI_AUCTION_HOUSE_ITEMS_TRANSACTIONS_LORE = new ConfigSetting(config, "gui.auction house.items.transactions.lore", Arrays.asList(
            "&7Click to view transaction history",
            "",
            "&eTotal Items Bought&f: &a%total_items_bought%",
            "&eTotal Items Sold&f: &a%total_items_sold%"
    ));

    public static final ConfigSetting GUI_AUCTION_HOUSE_ITEMS_HOW_TO_SELL_ITEM = new ConfigSetting(config, "gui.auction house.items.how to sell.item", "GOLD_INGOT");
    public static final ConfigSetting GUI_AUCTION_HOUSE_ITEMS_HOW_TO_SELL_NAME = new ConfigSetting(config, "gui.auction house.items.how to sell.name", "&e&lHow to Sell");
    public static final ConfigSetting GUI_AUCTION_HOUSE_ITEMS_HOW_TO_SELL_LORE = new ConfigSetting(config, "gui.auction house.items.how to sell.lore", Arrays.asList(
            "&7To list an item on the auction house, just hold",
            "&7the item in your hand and type the following command.",
            "&e/ah sell <buyNowPrice> [startPrice] [bidIncrement]"
    ));

    public static final ConfigSetting GUI_AUCTION_HOUSE_ITEMS_YOUR_AUCTIONS_ITEM = new ConfigSetting(config, "gui.auction house.items.your auctions.item", "DIAMOND");
    public static final ConfigSetting GUI_AUCTION_HOUSE_ITEMS_YOUR_AUCTIONS_NAME = new ConfigSetting(config, "gui.auction house.items.your auctions.name", "&e&lYour Auctions");
    public static final ConfigSetting GUI_AUCTION_HOUSE_ITEMS_YOUR_AUCTIONS_LORE = new ConfigSetting(config, "gui.auction house.items.your auctions.lore", Arrays.asList(
            "&7Click here to view all of the items you",
            "&7are currently selling on the auction.",
            "",
            "&e&l%active_player_auctions% Item(s)",
            "&e&lBalance &a$%player_balance%"
    ));

    public static final ConfigSetting GUI_AUCTION_HOUSE_ITEMS_COLLECTION_BIN_ITEM = new ConfigSetting(config, "gui.auction house.items.collection bin.item", "ENDER_CHEST");
    public static final ConfigSetting GUI_AUCTION_HOUSE_ITEMS_COLLECTION_BIN_NAME = new ConfigSetting(config, "gui.auction house.items.collection bin.name", "&e&lCollection Bin");
    public static final ConfigSetting GUI_AUCTION_HOUSE_ITEMS_COLLECTION_BIN_LORE = new ConfigSetting(config, "gui.auction house.items.collection bin.lore", Arrays.asList(
            "&7Click here to view and collect all of the",
            "&7items you have cancelled or have expired.",
            "",
            "&e&l%expired_player_auctions% Item(s)"
    ));

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
            "&7Middle-Click to reset filters"
    ));

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
            "&7Middle-Click to reset filters"
    ));

    /*  ===============================
     *         CONFIRM BUY GUI
     *  ===============================*/
    public static final ConfigSetting GUI_CONFIRM_BUY_TITLE = new ConfigSetting(config, "gui.confirm buy.title", "&7Are you sure?");
    public static final ConfigSetting GUI_CONFIRM_FILL_BG_ON_QUANTITY = new ConfigSetting(config, "gui.confirm buy.fill background when buying quantity", true, "Should the empty slots be filled with an item", "when the player decides to buy a specific quantity of items?");
    public static final ConfigSetting GUI_CONFIRM_BG_ITEM = new ConfigSetting(config, "gui.confirm buy.bg item", XMaterial.BLACK_STAINED_GLASS_PANE.name(), "This will only show when buying specific item quantities");

    public static final ConfigSetting GUI_CONFIRM_INCREASE_QTY_ITEM = new ConfigSetting(config, "gui.confirm buy.increase button.item", XMaterial.LIME_STAINED_GLASS_PANE.name());
    public static final ConfigSetting GUI_CONFIRM_INCREASE_QTY_NAME = new ConfigSetting(config, "gui.confirm buy.increase button.name", "&a&l+1");
    public static final ConfigSetting GUI_CONFIRM_INCREASE_QTY_LORE = new ConfigSetting(config, "gui.confirm buy.increase button.lore", Collections.singletonList("&7Click to add &a+1 &7to purchase quantity"));

    public static final ConfigSetting GUI_CONFIRM_DECREASE_QTY_ITEM = new ConfigSetting(config, "gui.confirm buy.decrease button.item", XMaterial.RED_STAINED_GLASS_PANE.name());
    public static final ConfigSetting GUI_CONFIRM_DECREASE_QTY_NAME = new ConfigSetting(config, "gui.confirm buy.decrease button.name", "&c&l-1");
    public static final ConfigSetting GUI_CONFIRM_DECREASE_QTY_LORE = new ConfigSetting(config, "gui.confirm buy.decrease button.lore", Collections.singletonList("&7Click to remove &c-1 &7from the purchase quantity"));

    public static final ConfigSetting GUI_CONFIRM_QTY_INFO_ITEM = new ConfigSetting(config, "gui.confirm buy.qty info.item", XMaterial.PAPER.name());
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
     *         EXPIRED AUCTION GUI
     *  ===============================*/

    public static final ConfigSetting GUI_EXPIRED_AUCTIONS_TITLE = new ConfigSetting(config, "gui.expired auctions.title", "&7Expired Listings");

    public static final ConfigSetting GUI_EXPIRED_AUCTIONS_ITEM = new ConfigSetting(config, "gui.expired auctions.cancel all.item", "ENDER_CHEST");
    public static final ConfigSetting GUI_EXPIRED_AUCTIONS_NAME = new ConfigSetting(config, "gui.expired auctions.cancel all.name", "&e&lClaim All");
    public static final ConfigSetting GUI_EXPIRED_AUCTIONS_LORE = new ConfigSetting(config, "gui.expired auctions.cancel all.lore", Collections.singletonList(
            "&7Click here to claim all of your expired auctions"
    ));

    /*  ===============================
     *      TRANSACTIONS LIST GUI
     *  ===============================*/
    public static final ConfigSetting GUI_TRANSACTIONS_TITLE = new ConfigSetting(config, "gui.transactions.title", "&7&LTransaction History");

    public static final ConfigSetting GUI_TRANSACTIONS_ITEM_TOGGLE_OWN_ITEM = new ConfigSetting(config, "gui.transactions.items.toggle own.item", "NETHER_STAR");
    public static final ConfigSetting GUI_TRANSACTIONS_ITEM_TOGGLE_OWN_NAME = new ConfigSetting(config, "gui.transactions.items.toggle own.name", "&e&LToggle Your Transactions");
    public static final ConfigSetting GUI_TRANSACTIONS_ITEM_TOGGLE_OWN_LORE = new ConfigSetting(config, "gui.transactions.items.toggle own.lore", Arrays.asList(
            "&7Click to toggle whether you see",
            "&7only your transactions or all",
            "&7the transactions ever made."
    ));

    public static final ConfigSetting GUI_TRANSACTIONS_ITEM_TRANSACTION_ITEM = new ConfigSetting(config, "gui.transactions.items.transaction.item", "PAPER");
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
            "&7Sale Type&f: &e%sale_type%",
            "&7Date&f: &e%transaction_date%",
            "&7Final Price&f: &e%final_price%"
    ));

    /*  ===============================
     *         INSPECTION GUI
     *  ===============================*/
    public static final ConfigSetting GUI_INSPECT_TITLE = new ConfigSetting(config, "gui.inspect.title", "&7&LInspecting Container");
    public static final ConfigSetting GUI_INSPECT_BG_ITEM = new ConfigSetting(config, "gui.inspect.bg item", XMaterial.BLACK_STAINED_GLASS_PANE.name());

    /*  ===============================
     *         FILTER GUI
     *  ===============================*/
    public static final ConfigSetting GUI_FILTER_TITLE = new ConfigSetting(config, "gui.filter.title", "&7Auction House - &eFilter Selection");
    public static final ConfigSetting GUI_FILTER_BG_ITEM = new ConfigSetting(config, "gui.filter.bg item", XMaterial.BLACK_STAINED_GLASS_PANE.name());

    public static final ConfigSetting GUI_FILTER_ITEMS_ALL_ITEM = new ConfigSetting(config, "gui.filter.items.all.item", XMaterial.HOPPER.name());
    public static final ConfigSetting GUI_FILTER_ITEMS_ALL_NAME = new ConfigSetting(config, "gui.filter.items.all.name", "&e&lAll");
    public static final ConfigSetting GUI_FILTER_ITEMS_ALL_LORE = new ConfigSetting(config, "gui.filter.items.all.lore", Collections.singletonList("&7Click to set the filter to&f: &eAll"));

    public static final ConfigSetting GUI_FILTER_ITEMS_OWN_NAME = new ConfigSetting(config, "gui.filter.items.own.name", "&e&lYour Listings");
    public static final ConfigSetting GUI_FILTER_ITEMS_OWN_LORE = new ConfigSetting(config, "gui.filter.items.own.lore", Collections.singletonList("&7Click to set the filter to&f: &eYour Listings"));

    public static final ConfigSetting GUI_FILTER_ITEMS_SEARCH_ITEM = new ConfigSetting(config, "gui.filter.items.search.item", XMaterial.NAME_TAG.name());
    public static final ConfigSetting GUI_FILTER_ITEMS_SEARCH_NAME = new ConfigSetting(config, "gui.filter.items.search.name", "&e&lSearch");
    public static final ConfigSetting GUI_FILTER_ITEMS_SEARCH_LORE = new ConfigSetting(config, "gui.filter.items.search.lore", Arrays.asList(
            "&7Click to set the filter to&f: &eSearch",
            "&7Current search phrase&f: &e%filter_search_phrase%"
    ));

    public static final ConfigSetting GUI_FILTER_ITEMS_MISC_ITEM = new ConfigSetting(config, "gui.filter.items.misc.item", XMaterial.OAK_SIGN.name());
    public static final ConfigSetting GUI_FILTER_ITEMS_MISC_NAME = new ConfigSetting(config, "gui.filter.items.misc.name", "&e&lMiscellaneous");
    public static final ConfigSetting GUI_FILTER_ITEMS_MISC_LORE = new ConfigSetting(config, "gui.filter.items.misc.lore", Collections.singletonList("&7Click to set the filter to&f: &eMiscellaneous"));

    public static final ConfigSetting GUI_FILTER_ITEMS_ENCHANTS_ITEM = new ConfigSetting(config, "gui.filter.items.enchants.item", XMaterial.ENCHANTED_BOOK.name());
    public static final ConfigSetting GUI_FILTER_ITEMS_ENCHANTS_NAME = new ConfigSetting(config, "gui.filter.items.enchants.name", "&e&lEnchantments");
    public static final ConfigSetting GUI_FILTER_ITEMS_ENCHANTS_LORE = new ConfigSetting(config, "gui.filter.items.enchants.lore", Collections.singletonList("&7Click to set the filter to&f: &eEnchantments"));

    public static final ConfigSetting GUI_FILTER_ITEMS_ARMOR_ITEM = new ConfigSetting(config, "gui.filter.items.armor.item", XMaterial.CHAINMAIL_CHESTPLATE.name());
    public static final ConfigSetting GUI_FILTER_ITEMS_ARMOR_NAME = new ConfigSetting(config, "gui.filter.items.armor.name", "&e&lArmor");
    public static final ConfigSetting GUI_FILTER_ITEMS_ARMOR_LORE = new ConfigSetting(config, "gui.filter.items.armor.lore", Collections.singletonList("&7Click to set the filter to&f: &eArmor"));

    public static final ConfigSetting GUI_FILTER_ITEMS_WEAPONS_ITEM = new ConfigSetting(config, "gui.filter.items.weapons.item", XMaterial.DIAMOND_SWORD.name());
    public static final ConfigSetting GUI_FILTER_ITEMS_WEAPONS_NAME = new ConfigSetting(config, "gui.filter.items.weapons.name", "&e&lWeapons");
    public static final ConfigSetting GUI_FILTER_ITEMS_WEAPONS_LORE = new ConfigSetting(config, "gui.filter.items.weapons.lore", Collections.singletonList("&7Click to set the filter to&f: &eWeapons"));

    public static final ConfigSetting GUI_FILTER_ITEMS_TOOLS_ITEM = new ConfigSetting(config, "gui.filter.items.tools.item", XMaterial.IRON_PICKAXE.name());
    public static final ConfigSetting GUI_FILTER_ITEMS_TOOLS_NAME = new ConfigSetting(config, "gui.filter.items.tools.name", "&e&lTools");
    public static final ConfigSetting GUI_FILTER_ITEMS_TOOLS_LORE = new ConfigSetting(config, "gui.filter.items.tools.lore", Collections.singletonList("&7Click to set the filter to&f: &eTools"));

    public static final ConfigSetting GUI_FILTER_ITEMS_SPAWNERS_ITEM = new ConfigSetting(config, "gui.filter.items.spawners.item", XMaterial.CREEPER_SPAWN_EGG.name());
    public static final ConfigSetting GUI_FILTER_ITEMS_SPAWNERS_NAME = new ConfigSetting(config, "gui.filter.items.spawners.name", "&e&LSpawners");
    public static final ConfigSetting GUI_FILTER_ITEMS_SPAWNERS_LORE = new ConfigSetting(config, "gui.filter.items.spawners.lore", Collections.singletonList("&7Click to set the filter to&f: &eSpawners"));

    public static final ConfigSetting GUI_FILTER_ITEMS_BLOCKS_ITEM = new ConfigSetting(config, "gui.filter.items.blocks.item", XMaterial.GOLD_BLOCK.name());
    public static final ConfigSetting GUI_FILTER_ITEMS_BLOCKS_NAME = new ConfigSetting(config, "gui.filter.items.blocks.name", "&e&lBlocks");
    public static final ConfigSetting GUI_FILTER_ITEMS_BLOCKS_LORE = new ConfigSetting(config, "gui.filter.items.blocks.lore", Collections.singletonList("&7Click to set the filter to&f: &eBlocks"));

    /*  ===============================
     *      CUSTOM ITEM FILTER GUI
     *  ===============================*/
    public static final ConfigSetting GUI_FILTER_WHITELIST_TITLE = new ConfigSetting(config, "gui.filter whitelist.title", "&7Auction Filter - &eWhitelist");
    public static final ConfigSetting GUI_FILTER_WHITELIST_BG_ITEM = new ConfigSetting(config, "gui.filter whitelist.bg item", XMaterial.BLACK_STAINED_GLASS_PANE.name());

    public static final ConfigSetting GUI_FILTER_WHITELIST_ITEMS_BLOCKS_ITEM = new ConfigSetting(config, "gui.filter whitelist.items.blocks.item", XMaterial.GRASS_BLOCK.name());
    public static final ConfigSetting GUI_FILTER_WHITELIST_ITEMS_BLOCKS_NAME = new ConfigSetting(config, "gui.filter whitelist.items.blocks.name", "&e&lBlock Filters");
    public static final ConfigSetting GUI_FILTER_WHITELIST_ITEMS_BLOCKS_LORE = new ConfigSetting(config, "gui.filter whitelist.items.blocks.lore", Collections.singletonList("&7Click to adjust the item whitelist for this filter"));

    public static final ConfigSetting GUI_FILTER_WHITELIST_ITEMS_FOOD_ITEM = new ConfigSetting(config, "gui.filter whitelist.items.food.item", XMaterial.CAKE.name());
    public static final ConfigSetting GUI_FILTER_WHITELIST_ITEMS_FOOD_NAME = new ConfigSetting(config, "gui.filter whitelist.items.food.name", "&e&lFood Filters");
    public static final ConfigSetting GUI_FILTER_WHITELIST_ITEMS_FOOD_LORE = new ConfigSetting(config, "gui.filter whitelist.items.food.lore", Collections.singletonList("&7Click to adjust the item whitelist for this filter"));

    public static final ConfigSetting GUI_FILTER_WHITELIST_ITEMS_ARMOR_ITEM = new ConfigSetting(config, "gui.filter whitelist.items.armor.item", XMaterial.DIAMOND_HELMET.name());
    public static final ConfigSetting GUI_FILTER_WHITELIST_ITEMS_ARMOR_NAME = new ConfigSetting(config, "gui.filter whitelist.items.armor.name", "&e&LArmor Filters");
    public static final ConfigSetting GUI_FILTER_WHITELIST_ITEMS_ARMOR_LORE = new ConfigSetting(config, "gui.filter whitelist.items.armor.lore", Collections.singletonList("&7Click to adjust the item whitelist for this filter"));

    public static final ConfigSetting GUI_FILTER_WHITELIST_ITEMS_TOOLS_ITEM = new ConfigSetting(config, "gui.filter whitelist.items.tools.item", XMaterial.IRON_PICKAXE.name());
    public static final ConfigSetting GUI_FILTER_WHITELIST_ITEMS_TOOLS_NAME = new ConfigSetting(config, "gui.filter whitelist.items.tools.name", "&e&lTool Filters");
    public static final ConfigSetting GUI_FILTER_WHITELIST_ITEMS_TOOLS_LORE = new ConfigSetting(config, "gui.filter whitelist.items.tools.lore", Collections.singletonList("&7Click to adjust the item whitelist for this filter"));

    public static final ConfigSetting GUI_FILTER_WHITELIST_ITEMS_SPAWNERS_ITEM = new ConfigSetting(config, "gui.filter whitelist.items.spawners.item", XMaterial.SPAWNER.name());
    public static final ConfigSetting GUI_FILTER_WHITELIST_ITEMS_SPAWNERS_NAME = new ConfigSetting(config, "gui.filter whitelist.items.spawners.name", "&e&lSpawner Filters");
    public static final ConfigSetting GUI_FILTER_WHITELIST_ITEMS_SPAWNERS_LORE = new ConfigSetting(config, "gui.filter whitelist.items.spawners.lore", Collections.singletonList("&7Click to adjust the item whitelist for this filter"));

    public static final ConfigSetting GUI_FILTER_WHITELIST_ITEMS_ENCHANTS_ITEM = new ConfigSetting(config, "gui.filter whitelist.items.enchants.item", XMaterial.ENCHANTED_BOOK.name());
    public static final ConfigSetting GUI_FILTER_WHITELIST_ITEMS_ENCHANTS_NAME = new ConfigSetting(config, "gui.filter whitelist.items.enchants.name", "&e&lEnchantment Filters");
    public static final ConfigSetting GUI_FILTER_WHITELIST_ITEMS_ENCHANTS_LORE = new ConfigSetting(config, "gui.filter whitelist.items.enchants.lore", Collections.singletonList("&7Click to adjust the item whitelist for this filter"));

    public static final ConfigSetting GUI_FILTER_WHITELIST_ITEMS_WEAPONS_ITEM = new ConfigSetting(config, "gui.filter whitelist.items.weapons.item", XMaterial.DIAMOND_SWORD.name());
    public static final ConfigSetting GUI_FILTER_WHITELIST_ITEMS_WEAPONS_NAME = new ConfigSetting(config, "gui.filter whitelist.items.weapons.name", "&e&lWeapon Filters");
    public static final ConfigSetting GUI_FILTER_WHITELIST_ITEMS_WEAPONS_LORE = new ConfigSetting(config, "gui.filter whitelist.items.weapons.lore", Collections.singletonList("&7Click to adjust the item whitelist for this filter"));

    public static final ConfigSetting GUI_FILTER_WHITELIST_ITEMS_MISC_ITEM = new ConfigSetting(config, "gui.filter whitelist.items.misc.item", XMaterial.BONE_MEAL.name());
    public static final ConfigSetting GUI_FILTER_WHITELIST_ITEMS_MISC_NAME = new ConfigSetting(config, "gui.filter whitelist.items.misc.name", "&e&lMiscellaneous Filters");
    public static final ConfigSetting GUI_FILTER_WHITELIST_ITEMS_MISC_LORE = new ConfigSetting(config, "gui.filter whitelist.items.misc.lore", Collections.singletonList("&7Click to adjust the item whitelist for this filter"));

    /*  ===============================
     *      CUSTOM ITEM FILTER GUI
     *  ===============================*/
    public static final ConfigSetting GUI_FILTER_WHITELIST_LIST_TITLE = new ConfigSetting(config, "gui.filter whitelist list.title", "&7Filter Whitelist - &e%filter_category%");
    public static final ConfigSetting GUI_FILTER_WHITELIST_LIST_BG_ITEM = new ConfigSetting(config, "gui.filter whitelist list.bg item", XMaterial.BLACK_STAINED_GLASS_PANE.name());


    /*  ===============================
     *         AUCTION STACKS
     *  ===============================*/
    public static final ConfigSetting AUCTION_ITEM_AUCTION_STACK = new ConfigSetting(config, "auction items.auction stack", Arrays.asList(
            "&7-------------------------",
            "&eSeller&f: &b%seller%",
            "",
            "&eBuy Now: &a$%buynowprice%",
            "",
            "&eTime Left: &b%remaining_days%&fd &b%remaining_hours%&fh &b%remaining_minutes%&fm &b%remaining_seconds%s"
    ), "This the item stack lore that will be appended to", "auction items in /ah (lore will be applied first, then these)");

    public static final ConfigSetting AUCTION_ITEM_LISTING_STACK = new ConfigSetting(config, "auction items.listing stack", Arrays.asList(
            "&7-------------------------",
            "&eBuy Now: &a$%buynowprice%",
            "&eTime Left: &b%remaining_days%&fd &b%remaining_hours%&fh &b%remaining_minutes%&fm &b%remaining_seconds%s"
    ), "This the item stack lore that will be appended to", "auction items in /ah listings (lore will be applied first, then these)");

    public static final ConfigSetting AUCTION_ITEM_AUCTION_STACK_WITH_BID = new ConfigSetting(config, "auction items.auction stack with bid", Arrays.asList(
            "&7-------------------------",
            "&eSeller&f: &b%seller%",
            "",
            "&eBuy Now: &a$%buynowprice%",
            "&eCurrent Price: &a$%currentprice%",
            "&eBid Increment: &a$%bidincrement%",
            "&eHighest Bidder: &a%highestbidder%",
            "",
            "&eTime Left: &b%remaining_days%&fd &b%remaining_hours%&fh &b%remaining_minutes%&fm &b%remaining_seconds%s"
    ), "This the item stack lore that will be appended to", "auction items in /ah (lore will be applied first, then these)");

    public static final ConfigSetting AUCTION_ITEM_LISTING_STACK_WITH_BID = new ConfigSetting(config, "auction items.listing stack with bid", Arrays.asList(
            "&7-------------------------",
            "&eBuy Now: &a$%buynowprice%",
            "&eCurrent Price: &a$%currentprice%",
            "&eBid Increment: &a$%bidincrement%",
            "&eHighest Bidder: &a%highestbidder%",
            "&eTime Left: &b%remaining_days%&fd &b%remaining_hours%&fh &b%remaining_minutes%&fm &b%remaining_seconds%s"
    ), "This the item stack lore that will be appended to", "auction items in /ah listings (lore will be applied first, then these)");

    public static final ConfigSetting AUCTION_PURCHASE_CONTROL_HEADER = new ConfigSetting(config, "auction items.controls.header", Collections.singletonList("&7-------------------------"));
    public static final ConfigSetting AUCTION_PURCHASE_CONTROL_FOOTER = new ConfigSetting(config, "auction items.controls.footer", Collections.singletonList("&7-------------------------"));

    public static final ConfigSetting AUCTION_PURCHASE_CONTROLS_BID_ON = new ConfigSetting(config, "auction items.controls.using bid", Arrays.asList(
            "&eLeft-Click&f: &bBid",
            "&eRight-Click&f: &bBuy Now"
    ), "This will be appended at the end of the lore", "If the auction item is using a bid, this will show");

    public static final ConfigSetting AUCTION_PURCHASE_CONTROLS_BID_OFF = new ConfigSetting(config, "auction items.controls.not using bid", Collections.singletonList(
            "&eLeft-Click&f: &bBuy Now"
    ), "This will be appended at the end of the lore", "If the auction item is not using a bid, this will show");

    public static final ConfigSetting AUCTION_PURCHASE_CONTROLS_INSPECTION = new ConfigSetting(config, "auction items.controls.inspection", Collections.singletonList("&eShift Right-Click to inspect"), "This will only be added to the control lore if the item can be inspected (skulker box/bundled item)");
    public static final ConfigSetting AUCTION_PURCHASE_CONTROLS_ACCEPT_BID = new ConfigSetting(config, "auction items.controls.accept bid", Collections.singletonList("&eRight-Click to accept the current bid"), "This will only show on items within the active listings menu on biddable items.");
    public static final ConfigSetting AUCTION_PURCHASE_CONTROLS_BUY_NOW_OFF_FOR_BID = new ConfigSetting(config, "auction items.controls.buy now is off for bid", "&cN/A", "If they player sets the buy now price to -1 on a bid item, it will mean make the item", "a bid item, but users will not be able to use the buy now option on the item.");

    /*  ===============================
     *         AUCTION STACKS
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
