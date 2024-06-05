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

package ca.tweetzy.auctionhouse.api;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.auction.AuctionPayment;
import ca.tweetzy.auctionhouse.auction.MinItemPrice;
import ca.tweetzy.auctionhouse.auction.enums.PaymentReason;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.compatibility.XMaterial;
import ca.tweetzy.core.hooks.EconomyManager;
import ca.tweetzy.flight.comp.enums.ServerVersion;
import ca.tweetzy.flight.nbtapi.NBT;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.flight.utils.Replacer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: January 17 2021
 * Time Created: 6:10 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class AuctionAPI {

	private static AuctionAPI instance;

	private AuctionAPI() {
	}

	public static AuctionAPI getInstance() {
		if (instance == null) {
			instance = new AuctionAPI();
		}
		return instance;
	}

	/**
	 * @param value a long number to be converted into a easily readable text
	 * @return a user friendly number to read
	 */
	public String getFriendlyNumber(double value) {
		if (value <= 0) return "0";

		int power;
		String suffix = " KMBTQPEZY";
		String formattedNumber = "";

		try {
			NumberFormat formatter = new DecimalFormat("#,###.#");
			power = (int) StrictMath.log10(value);
			value = value / (Math.pow(10, (power / 3) * 3));
			formattedNumber = formatter.format(value);

			formattedNumber = formattedNumber + suffix.charAt(power / 3);

		} catch (StringIndexOutOfBoundsException e) {
			return formatNumber(value);
		}

		return formattedNumber.length() > 4 ? formattedNumber.replaceAll("\\.[0-9]+", "") : formattedNumber;
	}

	/**
	 * Used to convert seconds to days, hours, minutes, and seconds
	 *
	 * @param seconds is the amount of seconds to convert
	 * @return an array containing the total number of days, hours, minutes, and seconds remaining
	 */
	public long[] getRemainingTimeValues(long seconds) {
		long[] vals = new long[4];
		int day = (int) TimeUnit.SECONDS.toDays(seconds);
		long hours = TimeUnit.SECONDS.toHours(seconds) - (day * 24L);
		long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds) * 60);
		long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) * 60);
		vals[0] = day;
		vals[1] = hours;
		vals[2] = minute;
		vals[3] = second;
		return vals;
	}

	/**
	 * Used to convert milliseconds (usually System.currentMillis) into a readable date format
	 *
	 * @param milliseconds is the total milliseconds
	 * @return a readable date format
	 */
	public String convertMillisToDate(long milliseconds) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Settings.DATE_FORMAT.getString());
		Date date = new Date(milliseconds);
		return simpleDateFormat.format(date);
	}

	/**
	 * Used to convert a serializable object into a base64 string
	 *
	 * @param object is the class that implements Serializable
	 * @return the base64 encoded string
	 */
	public String convertToBase64(Serializable object) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream;
		try {
			objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
			objectOutputStream.writeObject(object);
			objectOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
	}

	/**
	 * Used to convert a base64 string into an object
	 *
	 * @param string is the base64 string
	 * @return an object
	 */
	public Object convertBase64ToObject(String string) {
		byte[] data = Base64.getDecoder().decode(string);
		ObjectInputStream objectInputStream;
		Object object = null;
		try {
			objectInputStream = new ObjectInputStream(new ByteArrayInputStream(data));
			object = objectInputStream.readObject();
			objectInputStream.close();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return object;
	}

	/**
	 * Deserialize a byte array into an ItemStack.
	 *
	 * @param data Data to deserialize.
	 * @return Deserialized ItemStack.
	 */
	public ItemStack deserializeItem(byte[] data) {
		ItemStack item = null;
		try (BukkitObjectInputStream stream = new BukkitObjectInputStream(new ByteArrayInputStream(data))) {
			item = (ItemStack) stream.readObject();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}

		return item;
	}

	/**
	 * Serialize an ItemStack into a byte array.
	 *
	 * @param item Item to serialize.
	 * @return Serialized data.
	 */
	public byte[] serializeItem(ItemStack item) {
		try (ByteArrayOutputStream stream = new ByteArrayOutputStream(); BukkitObjectOutputStream bukkitStream = new BukkitObjectOutputStream(stream)) {
			bukkitStream.writeObject(item);
			return stream.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	// ========================================================================================

	public static String encodeItem(ItemStack itemStack) {
		YamlConfiguration config = new YamlConfiguration();
		config.set("i", itemStack);
		return config.saveToString();
//        return DatatypeConverter.printBase64Binary(config.saveToString().getBytes(StandardCharsets.UTF_8));
	}

	public static ItemStack decodeItem(String string) {
		YamlConfiguration config = new YamlConfiguration();
		try {
			config.loadFromString(string);
		} catch (IllegalArgumentException | InvalidConfigurationException e) {
			return null;
		}
		return config.getItemStack("i", null);
	}

	public static boolean tellMigrationStatus(CommandSender commandSender) {
		if (AuctionHouse.getInstance().isMigrating()) {
			AuctionHouse.getInstance().getLocale().newMessage("&cAuction House is currently migrating auction items, auction usage is disabled until it's finished").sendPrefixedMessage(commandSender);
			return true;
		}
		return false;
	}

	// ========================================================================================


	/**
	 * Used to create a player head
	 *
	 * @param name is the name of the player
	 * @return the player skull
	 */
	public ItemStack getPlayerHead(String name) {
		ItemStack stack = XMaterial.PLAYER_HEAD.parseItem();
		SkullMeta meta = (SkullMeta) stack.getItemMeta();
		meta.setOwner(name);
		stack.setItemMeta(meta);
		return stack;
	}

	/**
	 * Get the name of an item stack
	 *
	 * @param stack is the item you want to get name from
	 * @return the item name
	 */
	public String getItemName(ItemStack stack) {
		Objects.requireNonNull(stack, "Item stack cannot be null when getting name");
		final String name = stack.getItemMeta().hasDisplayName() ? stack.getItemMeta().getDisplayName() : WordUtils.capitalize(stack.getType().name().toLowerCase().replace("_", " "));
		return name;
	}

	/**
	 * Used to get the lore from an item stack
	 *
	 * @param stack is the item being checked
	 * @return the item lore if available
	 */
	public List<String> getItemLore(ItemStack stack) {
		List<String> lore = new ArrayList<>();
		Objects.requireNonNull(stack, "Item stack cannot be null when getting lore");
		if (stack.hasItemMeta()) {
			if (stack.getItemMeta().hasLore() && stack.getItemMeta().getLore() != null) {
				lore.addAll(stack.getItemMeta().getLore());
			}
		}
		return lore;
	}

	/**
	 * Used to get the names of all the enchantments on an item
	 *
	 * @param stack is the itemstack being checked
	 * @return a list of all the enchantment names
	 */
	public List<String> getItemEnchantments(ItemStack stack) {
		final List<String> enchantments = new ArrayList<>();
		Objects.requireNonNull(stack, "Item Stack cannot be null when getting enchantments");

		// actual enchantment books
		if (stack.getType() == XMaterial.ENCHANTED_BOOK.parseMaterial() && stack.getItemMeta() instanceof EnchantmentStorageMeta) {
			final EnchantmentStorageMeta meta = (EnchantmentStorageMeta) stack.getItemMeta();
			meta.getStoredEnchants().forEach((enchant, level) -> enchantments.add(enchantmentName(enchant.getName())));
			return enchantments;
		}

		// normal enchantments on item
		final ItemMeta meta = stack.getItemMeta();
		if (meta != null && meta.hasEnchants()) {
			meta.getEnchants().forEach((enchant, level) -> enchantments.add(enchantmentName(enchant.getName())));
		}

		return enchantments;
	}

	public String serializeLines(List<String> lines) {
		return StringUtils.join(lines, ";=;");
	}

	/**
	 * Used to match patterns
	 *
	 * @param pattern  is the keyword being searched for
	 * @param sentence is the sentence you're checking
	 * @return whether the keyword is found
	 */
	public boolean match(String pattern, String sentence) {
//		Pattern patt = Pattern.compile(ChatColor.stripColor(pattern), Pattern.CASE_INSENSITIVE);
		Pattern patt = Pattern.compile("\\b" + Pattern.quote(ChatColor.stripColor(pattern)) + "\\b", Pattern.CASE_INSENSITIVE);
		Matcher matcher = patt.matcher(sentence);
		return matcher.find();
	}

	public boolean matchSearch(String pattern, String sentence) {
		String escapedPattern = Pattern.quote(ChatColor.stripColor(pattern));
		Pattern patt = Pattern.compile(escapedPattern, Pattern.CASE_INSENSITIVE);
		Matcher matcher = patt.matcher(sentence);
		return matcher.find();
	}

	public boolean matchSearch(String pattern, List<String> lines) {
		for (String line : lines) {
			if (matchSearch(pattern, line)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param pattern is the keyword that you're currently searching for
	 * @param lines   is the lines being checked for the keyword
	 * @return whether the keyword was found in any of the lines provided
	 */
	public boolean match(String pattern, List<String> lines) {
		for (String line : lines) {
			if (match(pattern, line)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks whether a sentence matches the format to convert it into seconds
	 *
	 * @param sentence is the string being checked
	 * @return true if the string matches the time format
	 */
	public boolean isValidTimeString(String sentence) {
		Pattern pattern = Pattern.compile("([0-9]){1,10}(s|m|h|d|y){1}", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(sentence);
		return matcher.matches();
	}

	/**
	 * Used to format numbers with decimals and commas
	 *
	 * @param number is the number you want to format
	 * @return the formatted number string
	 */
	public String formatNumber(double number) {
		String formatted = String.format(Settings.CURRENCY_FORMAT.getString(), number);//%,.2f
		if (Settings.USE_SPACE_SEPARATOR_FOR_NUMBER.getBoolean())
			formatted = formatted.replace(",", " ");

		// do the zero drop here
		// this is a bit scuffed, I gotta improve this
		if (Settings.STRIP_ZEROS_ON_WHOLE_NUMBERS.getBoolean()) {
			if (Double.parseDouble(formatted.replace(",", "")) % 1 == 0) {

				formatted = formatted.replaceAll("0+$", "");
				if (formatted.endsWith("."))
					formatted = formatted.substring(0, formatted.length() - 1);
			}
		}

		String preDecimal = Settings.USE_ALTERNATE_CURRENCY_FORMAT.getBoolean() ? replaceLast(formatted.replace(",", "."), ".", ",") : formatted;
		return Settings.USE_FLAT_NUMBER_FORMAT.getBoolean() ? preDecimal.replace(".", "").replace(",", "") : preDecimal;
	}

	/**
	 * Used to replace the last portion of a string
	 *
	 * @param string      is the string being edited
	 * @param substring   is the to replace word/phrase
	 * @param replacement is the keyword(s) you're replacing the old substring with
	 * @return the updated string
	 */
	private String replaceLast(String string, String substring, String replacement) {
		int index = string.lastIndexOf(substring);
		if (index == -1) return string;
		return string.substring(0, index) + replacement + string.substring(index + substring.length());
	}

	public static String replaceAllExceptLast(String input, char target, char replacement) {
		int lastIndex = input.lastIndexOf(target);

		if (lastIndex == -1) {
			// If the target character is not found, return the original string
			return input;
		}

		StringBuilder result = new StringBuilder();
		boolean foundLast = false;

		for (int i = 0; i < input.length(); i++) {
			char currentChar = input.charAt(i);

			if (currentChar == target && i == lastIndex && !foundLast) {
				// Keep the last instance unchanged
				result.append(currentChar);
				foundLast = true;
			} else if (currentChar == target) {
				// Replace all instances except the last one
				result.append(replacement);
			} else {
				result.append(currentChar);
			}
		}

		return result.toString();
	}

	/**
	 * Used to get command flags (ex. -h, -f, -t, etc)
	 *
	 * @param args is the arguments passed when running a command
	 * @return any command flags if any
	 */
	public List<String> getCommandFlags(String... args) {
		List<String> flags = new ArrayList<>();
		for (String arg : args) {
			if (arg.startsWith("-") && arg.length() >= 2) {
				flags.add(arg.substring(0, 2));
			}
		}
		return flags;
	}

	/**
	 * Get the total amount of an item in the player's inventory
	 *
	 * @param player is the player being checked
	 * @param stack  is the item you want to find
	 * @return the total count of the item(s)
	 */
	public int getItemCountInPlayerInventory(Player player, ItemStack stack) {
		int total = 0;
		if (stack.getType() == XMaterial.PLAYER_HEAD.parseMaterial()) {
			for (ItemStack item : player.getInventory().getContents()) {
				if (item == null || item.getType() != XMaterial.PLAYER_HEAD.parseMaterial()) continue;
				if (getHeadTexture(item).equals(getHeadTexture(stack))) {
					final String invItemTexture = getHeadTexture(item);
					final String orgItemTexture = getHeadTexture(stack);

					if (invItemTexture == null) continue;
					if (orgItemTexture == null) continue;
					total += item.getAmount();
				}
			}
		} else {
			for (ItemStack item : player.getInventory().getContents()) {
				if (item == null || !item.isSimilar(stack)) continue;
				total += item.getAmount();
			}
		}
		return total;
	}

	public String getHeadTexture(final ItemStack item) {
		final String textureBase64 = NBT.get(item, nbt -> (String) nbt.getCompound("SkullOwner").getCompound("Properties").getCompoundList("textures").get(0).getString("Value"));
		final String textureJson = new String(Base64.getDecoder().decode(textureBase64));
		final JsonObject object = JsonParser.parseString(textureJson).getAsJsonObject();

		return object.get("textures").getAsJsonObject().get("SKIN").getAsJsonObject().get("url").getAsString();
	}

	/**
	 * Used to get any items that are similar to the provided stack in a player's inventory
	 *
	 * @param player is the player being checked
	 * @param stack  the item stack is being looked for
	 * @return all the items that are similar to the stack
	 */
	public List<ItemStack> getSimilarItemsFromInventory(Player player, ItemStack stack) {
		List<ItemStack> items = new ArrayList<>();
		for (int i = 0; i < player.getInventory().getSize(); i++) {
			ItemStack item = player.getInventory().getItem(i);
			if (item == null) continue;
			if (stack.getType() == XMaterial.PLAYER_HEAD.parseMaterial() && item.getType() == XMaterial.PLAYER_HEAD.parseMaterial()) {
//				NBT.get(item, nbt -> nbt.getCompoundList("textures").get(0).getString("Value"));

				if (!getHeadTexture(item).equals(getHeadTexture(stack))) continue;
			} else {
				if (!item.isSimilar(stack)) continue;
			}

			items.add(item);
		}

		return items;
	}

	/**
	 * Removes a set amount of a specific item from the player inventory
	 *
	 * @param player is the player you want to remove the item from
	 * @param stack  is the item that you want to remove
	 * @param amount is the amount of items you want to remove.
	 */
	public void removeSpecificItemQuantityFromPlayer(Player player, ItemStack stack, int amount) {
		int i = amount;
		for (int j = 0; j < player.getInventory().getSize(); j++) {
			ItemStack item = player.getInventory().getItem(j);
			if (item == null) continue;
			if (stack.getType() == XMaterial.PLAYER_HEAD.parseMaterial() && item.getType() == XMaterial.PLAYER_HEAD.parseMaterial()) {

				// is actual player head???
				SkullMeta invItemMeta = (SkullMeta) item.getItemMeta();
				SkullMeta orgItemMeta = (SkullMeta) stack.getItemMeta();

				if (invItemMeta != null && orgItemMeta != null && invItemMeta.hasOwner() && orgItemMeta.hasOwner()) {
					if (!invItemMeta.getOwner().equalsIgnoreCase(orgItemMeta.getOwner())) continue;
				} else {
					final String invItemTexture = getHeadTexture(item);
					final String orgItemTexture = getHeadTexture(stack);

					if (invItemTexture == null) continue;
					if (orgItemTexture == null) continue;

					if (!getHeadTexture(item).equals(getHeadTexture(stack))) continue;
				}

			} else {
				if (!item.isSimilar(stack)) continue;

			}

			if (i >= item.getAmount()) {
				player.getInventory().clear(j);
				i -= item.getAmount();
			} else if (i > 0) {
				item.setAmount(item.getAmount() - i);
				i = 0;
			} else {
				break;
			}
		}
	}

	/**
	 * Used to create an item bundle
	 *
	 * @param baseItem is the base item of the bundle (original)
	 * @param items    is the items that should be added to the bundle
	 * @return an item stack with all the items saved in NBT tags
	 */
	public ItemStack createBundledItem(ItemStack baseItem, ItemStack... items) {
		Objects.requireNonNull(items, "Cannot create a bundled item with no items");
		ItemStack item = QuickItem
				.of(Settings.ITEM_BUNDLE_ITEM.getString())
				.name(Settings.ITEM_BUNDLE_NAME.getString())
				.lore(Replacer.replaceVariables(Settings.ITEM_BUNDLE_LORE.getStringList(), "item_name", getItemName(baseItem)))
				.make();

		ItemMeta meta = item.getItemMeta();
		meta.addItemFlags(ItemFlag.values());
		item.setItemMeta(meta);

		int total = items.length;

		NBT.modify(item, nbt -> {
			nbt.setInteger("AuctionBundleItem", total);
			nbt.setString("AuctionBundleItemUUID-" + UUID.randomUUID().toString(), UUID.randomUUID().toString());
		});

		NBT.modify(item, nbt -> {
			nbt.setItemStackArray("AuctionBundleItems", items);
		});

		return item;
	}

	/**
	 * Take a string like 5d and convert it into seconds
	 * Valid suffixes:  m, d, w, mn, y
	 *
	 * @param time is the string time that will be converted
	 * @return the total amount of seconds
	 */
	public static long getSecondsFromString(String time) {
		time = time.toLowerCase();
		String[] tokens = time.split("(?<=\\d)(?=\\D)|(?=\\d)(?<=\\D)");
		char suffix = tokens[1].charAt(0);
		int amount = Integer.parseInt(tokens[0]);

		switch (suffix) {
			case 's':
				return amount;
			case 'm':
				return (long) amount * 60;
			case 'h':
				return (long) amount * 3600;
			case 'd':
				return (long) amount * 3600 * 24;
			case 'y':
				return (long) amount * 3600 * 24 * 365;
			default:
				return 0L;
		}
	}

	public boolean isDamaged(final ItemStack item) {
		if (item.hasItemMeta() && item.getItemMeta() instanceof Damageable) {
			final Damageable damageable = (Damageable) item.getItemMeta();

			if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13)) {
				return damageable.getDamage() > 0;
			}
			return damageable.hasDamage();
		}
		return false;
	}

	public boolean isRepaired(final ItemStack item) {
		return NBT.get(item, nbt -> (boolean) nbt.hasTag("AuctionHouseRepaired"));
	}

	public double calculateListingFee(double basePrice) {
		return Settings.TAX_LISTING_FEE_PERCENTAGE.getBoolean() ? (Settings.TAX_LISTING_FEE.getDouble() / 100D) * basePrice : Settings.TAX_LISTING_FEE.getDouble();
	}

	public boolean meetsMinItemPrice(boolean isUsingBundle, boolean isBiddingItem, ItemStack original, double basePrice, double bidStartPrice) {
		boolean valid = true;

		if (!AuctionHouse.getInstance().getMinItemPriceManager().getMinPrices().isEmpty() && !isUsingBundle) {
			final MinItemPrice foundMinPriceItem = AuctionHouse.getInstance().getMinItemPriceManager().getMinPrice(original);
			if (foundMinPriceItem != null) {

				if (isBiddingItem) {
					if (basePrice < foundMinPriceItem.getPrice() || bidStartPrice < foundMinPriceItem.getPrice()) valid = false;
				} else {
					if (basePrice < foundMinPriceItem.getPrice()) valid = false;
				}
			}
		}

		return valid;
	}

	public void logException(@Nullable Plugin plugin, @NotNull Throwable th) {
		logException(plugin, th, null);
	}

	public void logException(@Nullable Plugin plugin, @NotNull Throwable th, @Nullable String type) {
		Logger logger = plugin != null ? plugin.getLogger() : Logger.getGlobal();
		logger.log(Level.FINER, th, () -> "A " + (type == null ? "critical" : type) + " error occurred");
	}

	/**
	 * Converts the time from a human readable format like "10 minutes"
	 * to seconds.
	 *
	 * @param humanReadableTime the human readable time format: {time} {period}
	 *                          example: 5 seconds, 10 ticks, 7 minutes, 12 hours etc..
	 * @return the converted human time to secondsd
	 */
	public static long toTicks(final String humanReadableTime) {
		if (humanReadableTime == null) return 0;

		long seconds = 0L;

		final String[] split = humanReadableTime.split(" ");

		if (!(split.length > 1)) {
			return 0;
		}

		for (int i = 1; i < split.length; i++) {
			final String sub = split[i].toLowerCase();
			int multiplier = 0; // e.g 2 hours = 2
			long unit = 0; // e.g hours = 3600
			boolean isTicks = false;

			try {
				multiplier = Integer.parseInt(split[i - 1]);
			} catch (final NumberFormatException e) {
				continue;
			}

			// attempt to match the unit time
			if (sub.startsWith("tick"))
				isTicks = true;

			else if (sub.startsWith("second"))
				unit = 1;

			else if (sub.startsWith("minute"))
				unit = 60;

			else if (sub.startsWith("hour"))
				unit = 3600;

			else if (sub.startsWith("day"))
				unit = 86400;

			else if (sub.startsWith("week"))
				unit = 604800;

			else if (sub.startsWith("month"))
				unit = 2629743;

			else if (sub.startsWith("year"))
				unit = 31556926;

			else if (sub.startsWith("potato"))
				unit = 1337;

			else
				throw new IllegalArgumentException("Must define date type! Example: '1 second' (Got '" + sub + "')");

			seconds += multiplier * (isTicks ? 1 : unit);
		}

		return seconds;
	}

	public void withdrawBalance(OfflinePlayer player, double amount) {
		if (Settings.PAYMENT_HANDLE_USE_CMD.getBoolean()) {
			AuctionHouse.newChain().sync(() -> {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Settings.PAYMENT_HANDLE_WITHDRAW_CMD.getString().replace("%player%", player.getName()).replace("%price%", String.valueOf(amount)));
			}).execute();
		} else {
			if (Settings.FORCE_SYNC_MONEY_ACTIONS.getBoolean())
				AuctionHouse.newChain().sync(() -> EconomyManager.withdrawBalance(player, amount)).execute();
			else
				EconomyManager.withdrawBalance(player, amount);
		}
	}

	public void depositBalance(OfflinePlayer player, double amount, ItemStack item, OfflinePlayer paidFrom) {
		if (Settings.STORE_PAYMENTS_FOR_MANUAL_COLLECTION.getBoolean()) {
			if (Settings.MANUAL_PAYMENTS_ONLY_FOR_OFFLINE_USERS.getBoolean()) {
				if (!player.isOnline()) {
					AuctionHouse.getInstance().getDataManager().insertAuctionPayment(new AuctionPayment(player.getUniqueId(), amount, item, paidFrom.getName(), PaymentReason.ITEM_SOLD), null);
				} else {
					initiatePayment(player, amount);
				}
				return;
			}

			AuctionHouse.getInstance().getDataManager().insertAuctionPayment(new AuctionPayment(player.getUniqueId(), amount, item, paidFrom.getName(), PaymentReason.ITEM_SOLD), null);
			return;
		}

		initiatePayment(player, amount);
	}

	private void initiatePayment(OfflinePlayer player, double amount) {
		if (Settings.PAYMENT_HANDLE_USE_CMD.getBoolean()) {
			AuctionHouse.newChain().sync(() -> {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Settings.PAYMENT_HANDLE_DEPOSIT_CMD.getString().replace("%player%", player.getName()).replace("%price%", String.valueOf(amount)));
			}).execute();
		} else {
			if (Settings.FORCE_SYNC_MONEY_ACTIONS.getBoolean())
				AuctionHouse.newChain().sync(() -> EconomyManager.deposit(player, amount)).execute();
			else
				EconomyManager.deposit(player, amount);
		}
	}

	public String getDisplayName(OfflinePlayer player) {
		if (!player.isOnline())
			return player.getName();

		Player found = player.getPlayer();
		if (found != null)
			try {
				return found.getDisplayName();
			} catch (NullPointerException e) {
				return player.getName();
			}

		return player.getName();
	}

	public boolean meetsListingRequirements(Player player, ItemStack itemStack) {
		boolean meets = true;

		if (Settings.MAKE_BLOCKED_ITEMS_A_WHITELIST.getBoolean()) {
			if (!Settings.BLOCKED_ITEMS.getStringList().contains(itemStack.getType().name())) {
				AuctionHouse.getInstance().getLocale().getMessage("general.blockeditem").processPlaceholder("item", itemStack.getType().name()).sendPrefixedMessage(player);
				return false;
			}
		} else {
			if (Settings.BLOCKED_ITEMS.getStringList().contains(itemStack.getType().name())) {
				AuctionHouse.getInstance().getLocale().getMessage("general.blockeditem").processPlaceholder("item", itemStack.getType().name()).sendPrefixedMessage(player);
				return false;
			}
		}

		// Check NBT tags
		for (String nbtTag : Settings.BLOCKED_NBT_TAGS.getStringList()) {
			if (NBT.get(itemStack, nbt -> (boolean) nbt.hasTag(nbtTag))) {
				AuctionHouse.getInstance().getLocale().getMessage("general.blockednbttag").processPlaceholder("nbttag", nbtTag).sendPrefixedMessage(player);
				return false;
			}
		}

		String itemName = ChatColor.stripColor(getItemName(itemStack).toLowerCase());
		List<String> itemLore = getItemLore(itemStack).stream().map(line -> ChatColor.stripColor(line.toLowerCase())).collect(Collectors.toList());

		// Check for blocked names and lore
		for (String s : Settings.BLOCKED_ITEM_NAMES.getStringList()) {
			if (match(s, itemName)) {
				AuctionHouse.getInstance().getLocale().getMessage("general.blockedname").sendPrefixedMessage(player);
				meets = false;
			}
		}

		if (!itemLore.isEmpty() && meets) {
			for (String s : Settings.BLOCKED_ITEM_LORES.getStringList()) {
				for (String line : itemLore) {
					if (match(s, line)) {
						AuctionHouse.getInstance().getLocale().getMessage("general.blockedlore").sendPrefixedMessage(player);
						meets = false;
					}
				}
			}
		}

		return meets;
	}

	private String enchantmentName(String arg) {
		switch (Enchantment.getByName(arg).getName()) {
			case "ARROW_DAMAGE":
				return "Power";
			case "ARROW_FIRE":
				return "Flame";
			case "ARROW_INFINITE":
				return "Infinity";
			case "ARROW_KNOCKBACK":
				return "Punch";
			case "BINDING_CURSE":
				return "Curse of Binding";
			case "DAMAGE_ALL":
				return "Sharpness";
			case "DAMAGE_ARTHROPODS":
				return "Bane of Arthropods";
			case "DAMAGE_UNDEAD":
				return "Smite";
			case "DEPTH_STRIDER":
				return "Depth Strider";
			case "DIG_SPEED":
				return "Efficiency";
			case "DURABILITY":
				return "Unbreaking";
			case "FIRE_ASPECT":
				return "Fire Aspect";
			case "FROST_WALKER":
				return "Frost Walker";
			case "KNOCKBACK":
				return "Knockback";
			case "LOOT_BONUS_BLOCKS":
				return "Fortune";
			case "LOOT_BONUS_MOBS":
				return "Looting";
			case "LUCK":
				return "Luck of the Sea";
			case "LURE":
				return "Lure";
			case "MENDING":
				return "Mending";
			case "OXYGEN":
				return "Respiration";
			case "PROTECTION_ENVIRONMENTAL":
				return "Protection";
			case "PROTECTION_EXPLOSIONS":
				return "Blast Protection";
			case "PROTECTION_FALL":
				return "Feather Falling";
			case "PROTECTION_FIRE":
				return "Fire Protection";
			case "PROTECTION_PROJECTILE":
				return "Projectile Protection";
			case "SILK_TOUCH":
				return "Silk Touch";
			case "SWEEPING_EDGE":
				return "Sweeping Edge";
			case "THORNS":
				return "Thorns";
			case "VANISHING_CURSE":
				return "Cure of Vanishing";
			case "WATER_WORKER":
				return "Aqua Affinity";
			default:
				return "Unknown";
		}
	}
}
