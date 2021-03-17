package ca.tweetzy.auctionhouse.settings;

import ca.tweetzy.auctionhouse.AuctionHouse;
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
    public static final ConfigSetting UPDATE_CHECKER = new ConfigSetting(config, "update checker", true, "Should auction house check for updates?");
    /*  ===============================
     *          BASIC SETTINGS
     *  ===============================*/
    public static final ConfigSetting DEFAULT_AUCTION_TIME = new ConfigSetting(config, "auction setting.default auction house", 900, "The default auction time before an item expires (in seconds)");
    public static final ConfigSetting MAX_AUCTION_PRICE = new ConfigSetting(config, "auction setting.pricing.max auction price", 1000000000, "The max price for buy only / buy now items");
    public static final ConfigSetting MAX_AUCTION_START_PRICE = new ConfigSetting(config, "auction setting.max auction start price", 1000000000, "The max price starting a bidding auction");
    public static final ConfigSetting MAX_AUCTION_INCREMENT_PRICE = new ConfigSetting(config, "auction setting.pricing.max auction increment price", 1000000000, "The max amount for incrementing a bid.");
    public static final ConfigSetting MIN_AUCTION_PRICE = new ConfigSetting(config, "auction setting.pricing.min auction price", 1, "The min price for buy only / buy now items");
    public static final ConfigSetting MIN_AUCTION_START_PRICE = new ConfigSetting(config, "auction setting.min auction start price", 1, "The min price starting a bidding auction");
    public static final ConfigSetting MIN_AUCTION_INCREMENT_PRICE = new ConfigSetting(config, "auction setting.pricing.min auction increment price", 1, "The min amount for incrementing a bid.");
    public static final ConfigSetting OWNER_CAN_PURCHASE_OWN_ITEM = new ConfigSetting(config, "auction setting.purchase.owner can purchase own item", false, "Should the owner of an auction be able to purchase it?", "This probably should be set to false...");
    public static final ConfigSetting OWNER_CAN_BID_OWN_ITEM = new ConfigSetting(config, "auction setting.purchase.owner can bid on own item", false, "Should the owner of an auction be able to bid on it?", "This probably should be set to false...");
    public static final ConfigSetting AUTO_REFRESH_AUCTION_PAGES = new ConfigSetting(config, "auction setting.auto refresh auction pages", true, "Should auction pages auto refresh?");
    public static final ConfigSetting USE_SHORT_NUMBERS_ON_ITEMS = new ConfigSetting(config, "auction setting.use short numbers", false, "Should numbers be shortened into a prefixed form?");
    public static final ConfigSetting INCREASE_TIME_ON_BID = new ConfigSetting(config, "auction setting.increase time on bid", true, "Should the remaining time be increased when a bid is placed?");
    public static final ConfigSetting TIME_TO_INCREASE_BY_ON_BID = new ConfigSetting(config, "auction setting.time to increase by on the bid", 20, "How many seconds should be added to the remaining time?");
    public static final ConfigSetting TICK_UPDATE_TIME = new ConfigSetting(config, "auction setting.tick auctions every", 1, "How many seconds should pass before the plugin updates all the times on items?");

    /*  ===============================
     *         DISCORD WEBHOOK
     *  ===============================*/
    public static final ConfigSetting DISCORD_ENABLED = new ConfigSetting(config, "discord.enabled", true, "Should the discord webhook feature be enabled?");
    public static final ConfigSetting DISCORD_ALERT_ON_AUCTION_START = new ConfigSetting(config, "discord.alert on auction start", true, "Should a message be sent to the discord server when someone lists a new auction item");
    public static final ConfigSetting DISCORD_ALERT_ON_AUCTION_FINISH = new ConfigSetting(config, "discord.alert on auction finish", true, "Should a message when an auction finishes?");
    public static final ConfigSetting DISCORD_WEBHOOKS = new ConfigSetting(config, "discord.webhooks", Collections.singletonList("https://discord.com/api/webhooks/821837927444119563/Yd3cWzVB56Tk_VuN1Lv2iGgvsbZt2YV5SDyCkVo6EjRAUqJk3nA2nSG9PH_Bl6rcFNnz"), "A list of webhook urls (channels) you want a message sent to");
    public static final ConfigSetting DISCORD_MSG_USERNAME = new ConfigSetting(config, "discord.user.username", "Auction House", "The name of the user who will send the message");
    public static final ConfigSetting DISCORD_MSG_PFP = new ConfigSetting(config, "discord.user.avatar picture", "https://cdn.kiranhart.com/spigot/auctionhouse/icon.png", "The name of the user who will send the message");
    public static final ConfigSetting DISCORD_MSG_USE_RANDOM_COLOUR = new ConfigSetting(config, "discord.msg.use random colour", true, "The name of the user who will send the message");
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
    public static final ConfigSetting DISCORD_MSG_FIELD_BUY_NOW_PRICE_VALUE = new ConfigSetting(config, "discord.msg.buy now price.value", "%buy_now_price%");
    public static final ConfigSetting DISCORD_MSG_FIELD_BUY_NOW_PRICE_INLINE = new ConfigSetting(config, "discord.msg.buy now price.inline", true);

    public static final ConfigSetting DISCORD_MSG_FIELD_FINAL_PRICE_NAME = new ConfigSetting(config, "discord.msg.final price.name", "Final Price");
    public static final ConfigSetting DISCORD_MSG_FIELD_FINAL_PRICE_VALUE = new ConfigSetting(config, "discord.msg.final price.value", "%final_price%");
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
     *         BLOCKED ITEMS
     *  ===============================*/
    public static final ConfigSetting BLOCKED_ITEMS = new ConfigSetting(config, "blocked items", Collections.singletonList("ENDER_CHEST"), "Materials that should be blocked (not allowed to sell)");

    /*  ===============================
     *         MAX AUCTION TIME
     *  ===============================*/
    public static final ConfigSetting AUCTION_TIME = new ConfigSetting(config, "auction time", Collections.singletonList("rankone:1200"), "Special time permissions for users.", "If they have the following permission in this format:", "auctionhouse.time.rankone", "rankone refers to the list item under auction time, they will get the time specified (in seconds)");

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
            "&7All sell listings last for a max of &e60 minutes",
            "&7unsold items are sent to your collection bin.",
            "",
            "&7Each item is listed as a auction, so",
            "&7players can out bid each other, or simply",
            "&7purchase it right away with a set price.",
            "",
            "&7For more help, use &e/ah help!"
    ));

    public static final ConfigSetting GUI_AUCTION_HOUSE_ITEMS_TRANSACTIONS_ITEM = new ConfigSetting(config, "gui.auction house.items.transactions.item", "PAPER");
    public static final ConfigSetting GUI_AUCTION_HOUSE_ITEMS_TRANSACTIONS_NAME = new ConfigSetting(config, "gui.auction house.items.transactions.name", "&e&lTransactions");
    public static final ConfigSetting GUI_AUCTION_HOUSE_ITEMS_TRANSACTIONS_LORE = new ConfigSetting(config, "gui.auction house.items.transactions.lore", Collections.singletonList(
            "&7Click to view transaction history"
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
            "&e&l%active_player_auctions% Item(s)"
    ));

    public static final ConfigSetting GUI_AUCTION_HOUSE_ITEMS_COLLECTION_BIN_ITEM = new ConfigSetting(config, "gui.auction house.items.collection bin.item", "ENDER_CHEST");
    public static final ConfigSetting GUI_AUCTION_HOUSE_ITEMS_COLLECTION_BIN_NAME = new ConfigSetting(config, "gui.auction house.items.collection bin.name", "&e&lCollection Bin");
    public static final ConfigSetting GUI_AUCTION_HOUSE_ITEMS_COLLECTION_BIN_LORE = new ConfigSetting(config, "gui.auction house.items.collection bin.lore", Arrays.asList(
            "&7Click here to view and collect all of the",
            "&7items you have cancelled or have expired.",
            "",
            "&e&l%expired_player_auctions% Item(s)"
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

    public static final ConfigSetting AUCTION_PURCHASE_CONTROLS_BID_ON = new ConfigSetting(config, "auction items.controls.using bid", Arrays.asList(
            "&7-------------------------",
            "&eLeft-Click&f: &bBid",
            "&eRight-Click&f: &bBuy Now",
            "&7-------------------------"
    ), "This will be appended at the end of the lore", "If the auction item is using a bid, this will show");

    public static final ConfigSetting AUCTION_PURCHASE_CONTROLS_BID_OFF = new ConfigSetting(config, "auction items.controls.not using bid", Arrays.asList(
            "&7-------------------------",
            "&eLeft-Click&f: &bBuy Now",
            "&7-------------------------"
    ), "This will be appended at the end of the lore", "If the auction item is not using a bid, this will show");

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
