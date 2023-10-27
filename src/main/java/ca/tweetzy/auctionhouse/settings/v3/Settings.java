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
import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.config.ConfigEntry;
import ca.tweetzy.flight.settings.FlightSettings;

import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class Settings extends FlightSettings {

	public static ConfigEntry PREFIX = create("prefix", "&8[&eAuctionHouse&8]").withComment("The prefix for the plugin");
	public static ConfigEntry LANGUAGE = create("language", "en_us").withComment("The primary language of the plugin");

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

	public static ConfigEntry CURRENCY_FORMAT = create("settings.formatting.number format", "%,.2f", "The default currency formatting (#,###.##)");

	/*
	==============================================================
					 Internal Timers/Cooldowns
	==============================================================
	 */
	public static ConfigEntry DELAY_REFRESH_MAIN_AUCTION = create("settings.delays.main auction refresh", 3000, "How many milliseconds must pass before the user can click the refresh button again. (0 to disable)");
	public static ConfigEntry DELAY_EXPIRED_ITEM_CLAIM = create("settings.delays.expired item claim", 100, "How many milliseconds must pass between each expired item claim (0 to disable)");
	public static ConfigEntry DELAY_EXPIRED_ITEM_CLAIM_ALL = create("settings.delays.expired collect all", 1, "How many milliseconds must pass before the user can click the collect all button again (0 to disable)");
	public static ConfigEntry TIMER_AUTO_REFRESH_MAIN_AUCTION = create("settings.timers.main auction auto refresh", 20, "How many ticks before the page updates all items (0 to disable)");
	public static ConfigEntry TIMER_SQL_PULL_RATE = create("settings.timers.sql pull rate", 20 * 60 * 10, "How many ticks before the page updates all items (0 to disable)");

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

	// ==================== Player Preferences Menu ==================== //
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

}
