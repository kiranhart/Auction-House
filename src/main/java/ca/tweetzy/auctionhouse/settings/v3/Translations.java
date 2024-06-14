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
import ca.tweetzy.core.configuration.ConfigSetting;
import ca.tweetzy.flight.settings.TranslationEntry;
import ca.tweetzy.flight.settings.TranslationManager;
import lombok.NonNull;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public final class Translations extends TranslationManager {

	public Translations(@NonNull JavaPlugin plugin) {
		super(plugin);
		this.mainLanguage = "migration_prep_dont_touch";
	}

	/*
	==============================================================
				          Main Auction Menu
	==============================================================
	 */
	public static TranslationEntry GUI_MAIN_TITLE = create("gui.main.title", "<GRADIENT:FE8295>&lAuction House</GRADIENT:FFAD96>");
	public static TranslationEntry GUI_MAIN_ITEMS_COLLECTION_BIN_NAME = create("gui.main.items.collection bin.name", "<GRADIENT:FE8295>&lCollection Bin</GRADIENT:FFAD96>");
	public static TranslationEntry GUI_MAIN_ITEMS_COLLECTION_BIN_LORE = create("gui.main.items.collection bin.lore",
			"&7All of your expired or won items will be",
			"&7placed here for collection",
			"",
			" &f» #FE8295%expired_listings% &bExpired/Won Items &f«",
			"",
			"&e&lClick &8» &7To view collection bin"
	);

	public static TranslationEntry GUI_MAIN_ITEMS_ACTIVE_LISTINGS_NAME = create("gui.main.items.active listings.name", "<GRADIENT:FE8295>&LActive Listings</GRADIENT:FFAD96>");
	public static TranslationEntry GUI_MAIN_ITEMS_ACTIVE_LISTINGS_LORE = create("gui.main.items.active listings.lore",
			"&7All of your listings that you've",
			"&7placed will be found in here.",
			"",
			" &f» #FE8295%active_listings% &bActive Listings &f«",
			"",
			"&e&lClick &8» &7To view active listings"
	);

	public static TranslationEntry GUI_MAIN_ITEMS_PROFILE_NAME = create("gui.main.items.profile.name", "<GRADIENT:FE8295>&LYour Profile</GRADIENT:FFAD96>");
	public static TranslationEntry GUI_MAIN_ITEMS_PROFILE_LORE = create("gui.main.items.profile.lore",
			"&8Profile Settings",
			"&7You can view your auction profile, as",
			"&7well as edit preferences from here.",
			"",
			"&e&lClick &8» &7To view profile"
	);

	public static TranslationEntry GUI_MAIN_ITEMS_SEARCH_NAME = create("gui.main.items.search.name", "<GRADIENT:FE8295>&LSearch</GRADIENT:FFAD96>");
	public static TranslationEntry GUI_MAIN_ITEMS_SEARCH_LORE = create("gui.main.items.search.lore",
			"&8Search Listings",
			"&7Used to search the entire auction house",
			"&7by name, lore, enchants, owner, etc.",
			"",
			"&e&lClick &8» &7To search listings"
	);

	public static TranslationEntry GUI_MAIN_ITEMS_REFRESH_NAME = create("gui.main.items.refresh.name", "<GRADIENT:FE8295>&LRefresh</GRADIENT:FFAD96>");
	public static TranslationEntry GUI_MAIN_ITEMS_REFRESH_LORE = create("gui.main.items.refresh.lore",
			"&8Refresh Page",
			"&7Used to refresh the entire menu to",
			"&7view new and updated listings.",
			"",
			"&e&lClick &8» &7To refresh menu"
	);

	/*
	==============================================================
				         	Bans Menu
	==============================================================
	 */
	public static TranslationEntry GUI_BANS_TITLE = create("gui.bans.title", "<GRADIENT:FE8295>&lAuction House</GRADIENT:FFAD96> &7> &eBans");
	public static  TranslationEntry GUI_BANS_ITEMS_BAN_NAME = create("gui.bans.items.ban.name", "&e%player_name%");
	public static  TranslationEntry GUI_BANS_ITEMS_BAN_LORE = create("gui.bans.items.ban.lore",
			"&7Time Remaining&f: &e%ban_amount%",
			"&7Ban Reason&f: &e%ban_reason%",
			"",
			"&7Right-Click to unban this user"
	);

	public static void init() {
		new Translations(AuctionHouse.getInstance()).setup(AuctionHouse.getInstance());
	}
}
