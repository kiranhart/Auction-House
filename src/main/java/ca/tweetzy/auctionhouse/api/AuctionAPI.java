package ca.tweetzy.auctionhouse.api;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.events.AuctionStartEvent;
import ca.tweetzy.auctionhouse.api.hook.MMOItemsHook;
import ca.tweetzy.auctionhouse.api.hook.McMMOHook;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.auction.AuctionedItem;
import ca.tweetzy.auctionhouse.auction.MinItemPrice;
import ca.tweetzy.auctionhouse.auction.enums.AuctionSaleType;
import ca.tweetzy.auctionhouse.helpers.ConfigurationItemHelper;
import ca.tweetzy.auctionhouse.helpers.MaterialCategorizer;
import ca.tweetzy.auctionhouse.managers.SoundManager;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.compatibility.CompatibleHand;
import ca.tweetzy.core.compatibility.ServerVersion;
import ca.tweetzy.core.compatibility.XMaterial;
import ca.tweetzy.core.hooks.EconomyManager;
import ca.tweetzy.core.utils.PlayerUtils;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.core.utils.items.ItemUtils;
import ca.tweetzy.core.utils.nms.NBTEditor;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	 * Used to send a discord message to a webhook link
	 *
	 * @param seller      The Seller of the auction item
	 * @param buyer       The Buyer of the auction item
	 * @param auctionItem The object of the auction item
	 * @param saleType    The sale type, was it a bid won or an immediate purchase?
	 * @param isNew       Is this the start of a new auction or the end of one?
	 * @param isBid       Is this auction a bid enabled auction, or a single sale auction?
	 */
	public void sendDiscordMessage(String webhook, OfflinePlayer seller, OfflinePlayer buyer, AuctionedItem auctionItem, AuctionSaleType saleType, boolean isNew, boolean isBid) {
		DiscordWebhook hook = new DiscordWebhook(webhook);
		hook.setUsername(Settings.DISCORD_MSG_USERNAME.getString());
		hook.setAvatarUrl(Settings.DISCORD_MSG_PFP.getString());

		String color = isBid ? Settings.DISCORD_MSG_DEFAULT_COLOUR_BID.getString() : isNew ? Settings.DISCORD_MSG_DEFAULT_COLOUR.getString() : Settings.DISCORD_MSG_DEFAULT_COLOUR_SALE.getString();

		String[] possibleColours = color.split("-");
		Color colour = Settings.DISCORD_MSG_USE_RANDOM_COLOUR.getBoolean()
				? Color.getHSBColor(ThreadLocalRandom.current().nextFloat() * 360F, ThreadLocalRandom.current().nextFloat() * 101F, ThreadLocalRandom.current().nextFloat() * 101F)
				: Color.getHSBColor(Float.parseFloat(possibleColours[0]) / 360, Float.parseFloat(possibleColours[1]) / 100, Float.parseFloat(possibleColours[2]) / 100);

		ItemStack itemStack = auctionItem.getItem();
		String itemName = MMOItemsHook.isEnabled() ? MMOItemsHook.getItemType(itemStack) : ChatColor.stripColor(getItemName(itemStack));
		DiscordWebhook.EmbedObject embedObject = new DiscordWebhook.EmbedObject();

		final String playerLost = AuctionHouse.getInstance().getLocale().getMessage("discord.player_lost").getMessage();
		final String notSold = AuctionHouse.getInstance().getLocale().getMessage("discord.not_sold").getMessage();
		final String noBuyer = AuctionHouse.getInstance().getLocale().getMessage("discord.no_buyer").getMessage();
		final String wasNotBought = AuctionHouse.getInstance().getLocale().getMessage("discord.not_bought").getMessage();
		final String isBidTrue = AuctionHouse.getInstance().getLocale().getMessage("discord.is_bid_true").getMessage();
		final String isBidFalse = AuctionHouse.getInstance().getLocale().getMessage("discord.is_bid_false").getMessage();
		final String isBidWin = AuctionHouse.getInstance().getLocale().getMessage("discord.sale_bid_win").getMessage();
		final String immediateBuy = AuctionHouse.getInstance().getLocale().getMessage("discord.sale_immediate_buy").getMessage();

		embedObject.setTitle(isNew ? Settings.DISCORD_MSG_START_TITLE.getString() : Settings.DISCORD_MSG_FINISH_TITLE.getString());
		embedObject.setColor(colour);
		embedObject.addField(Settings.DISCORD_MSG_FIELD_SELLER_NAME.getString(), Settings.DISCORD_MSG_FIELD_SELLER_VALUE.getString().replace("%seller%", seller.getName() != null ? seller.getName() : playerLost), Settings.DISCORD_MSG_FIELD_SELLER_INLINE.getBoolean());
		embedObject.addField(Settings.DISCORD_MSG_FIELD_BUYER_NAME.getString(), isNew ? noBuyer : Settings.DISCORD_MSG_FIELD_BUYER_VALUE.getString().replace("%buyer%", buyer.getName() != null ? buyer.getName() : playerLost), Settings.DISCORD_MSG_FIELD_BUYER_INLINE.getBoolean());
		embedObject.addField(Settings.DISCORD_MSG_FIELD_BUY_NOW_PRICE_NAME.getString(), Settings.DISCORD_MSG_FIELD_BUY_NOW_PRICE_VALUE.getString().replace("%buy_now_price%", this.getFriendlyNumber(auctionItem.getBasePrice())), Settings.DISCORD_MSG_FIELD_BUY_NOW_PRICE_INLINE.getBoolean());
		embedObject.addField(Settings.DISCORD_MSG_FIELD_FINAL_PRICE_NAME.getString(), isNew ? notSold : Settings.DISCORD_MSG_FIELD_FINAL_PRICE_VALUE.getString().replace("%final_price%", this.getFriendlyNumber(isBid ? auctionItem.getCurrentPrice() : auctionItem.getBasePrice())), Settings.DISCORD_MSG_FIELD_FINAL_PRICE_INLINE.getBoolean());
		embedObject.addField(Settings.DISCORD_MSG_FIELD_IS_BID_NAME.getString(), Settings.DISCORD_MSG_FIELD_IS_BID_VALUE.getString().replace("%is_bid%", isBid ? isBidTrue : isBidFalse), Settings.DISCORD_MSG_FIELD_IS_BID_INLINE.getBoolean());
		embedObject.addField(Settings.DISCORD_MSG_FIELD_PURCHASE_TYPE_NAME.getString(), isNew ? wasNotBought : Settings.DISCORD_MSG_FIELD_PURCHASE_TYPE_VALUE.getString().replace("%purchase_type%", saleType == AuctionSaleType.USED_BIDDING_SYSTEM ? isBidWin : immediateBuy), Settings.DISCORD_MSG_FIELD_PURCHASE_INLINE.getBoolean());
		embedObject.addField(Settings.DISCORD_MSG_FIELD_ITEM_NAME.getString(), Settings.DISCORD_MSG_FIELD_ITEM_VALUE.getString().replace("%item_name%", itemName), Settings.DISCORD_MSG_FIELD_ITEM_INLINE.getBoolean());
		embedObject.addField(Settings.DISCORD_MSG_FIELD_ITEM_AMOUNT_NAME.getString(), Settings.DISCORD_MSG_FIELD_ITEM_AMOUNT_VALUE.getString().replace("%item_amount%", String.valueOf(itemStack.getAmount())), Settings.DISCORD_MSG_FIELD_ITEM_AMOUNT_INLINE.getBoolean());

		hook.addEmbed(embedObject);

		try {
			hook.execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * A simplified version of {@link #sendDiscordMessage(String, OfflinePlayer, OfflinePlayer, AuctionedItem, AuctionSaleType, boolean, boolean)} used
	 * to just send bid updates
	 *
	 * @param webhook     the webhook url
	 * @param auctionItem The object of the auction item
	 */
	public void sendDiscordBidMessage(String webhook, AuctionedItem auctionItem, double newBid) {
		// oh boy the code repetition is high with this one
		DiscordWebhook hook = new DiscordWebhook(webhook);
		hook.setUsername(Settings.DISCORD_MSG_USERNAME.getString());
		hook.setAvatarUrl(Settings.DISCORD_MSG_PFP.getString());

		String[] possibleColours = Settings.DISCORD_MSG_DEFAULT_COLOUR_BID.getString().split("-");
		Color colour = Settings.DISCORD_MSG_USE_RANDOM_COLOUR.getBoolean()
				? Color.getHSBColor(ThreadLocalRandom.current().nextFloat() * 360F, ThreadLocalRandom.current().nextFloat() * 101F, ThreadLocalRandom.current().nextFloat() * 101F)
				: Color.getHSBColor(Float.parseFloat(possibleColours[0]) / 360, Float.parseFloat(possibleColours[1]) / 100, Float.parseFloat(possibleColours[2]) / 100);

		ItemStack itemStack = auctionItem.getItem();
		String itemName = MMOItemsHook.isEnabled() ? MMOItemsHook.getItemType(itemStack) : ChatColor.stripColor(getItemName(itemStack));

		DiscordWebhook.EmbedObject embedObject = new DiscordWebhook.EmbedObject();
		embedObject.setTitle(Settings.DISCORD_MSG_BID_TITLE.getString());
		embedObject.setColor(colour);
		embedObject.addField(Settings.DISCORD_MSG_FIELD_BIDDER_NAME.getString(), Settings.DISCORD_MSG_FIELD_BIDDER_VALUE.getString().replace("%bidder%", auctionItem.getHighestBidderName() != null ? auctionItem.getHighestBidderName() : AuctionHouse.getInstance().getLocale().getMessage("discord.player_lost").getMessage()), Settings.DISCORD_MSG_FIELD_BIDDER_INLINE.getBoolean());
		embedObject.addField(Settings.DISCORD_MSG_FIELD_BID_PRICE_NAME.getString(), Settings.DISCORD_MSG_FIELD_BID_PRICE_VALUE.getString().replace("%bid_price%", formatNumber(newBid)), Settings.DISCORD_MSG_FIELD_BID_PRICE_INLINE.getBoolean());

		embedObject.addField(Settings.DISCORD_MSG_FIELD_ITEM_NAME.getString(), Settings.DISCORD_MSG_FIELD_ITEM_VALUE.getString().replace("%item_name%", itemName), Settings.DISCORD_MSG_FIELD_ITEM_INLINE.getBoolean());
		embedObject.addField(Settings.DISCORD_MSG_FIELD_ITEM_AMOUNT_NAME.getString(), Settings.DISCORD_MSG_FIELD_ITEM_AMOUNT_VALUE.getString().replace("%item_amount%", String.valueOf(itemStack.getAmount())), Settings.DISCORD_MSG_FIELD_ITEM_AMOUNT_INLINE.getBoolean());
		hook.addEmbed(embedObject);

		try {
			hook.execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get the name of an item stack
	 *
	 * @param stack is the item you want to get name from
	 * @return the item name
	 */
	public String getItemName(ItemStack stack) {
		Objects.requireNonNull(stack, "Item stack cannot be null when getting name");
		return stack.getItemMeta().hasDisplayName() ? stack.getItemMeta().getDisplayName() : TextUtils.formatText("&f" + WordUtils.capitalize(stack.getType().name().toLowerCase().replace("_", " ")));
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
		List<String> enchantments = new ArrayList<>();
		Objects.requireNonNull(stack, "Item Stack cannot be null when getting enchantments");
		if (!stack.getEnchantments().isEmpty()) {
			stack.getEnchantments().forEach((k, i) -> {
				enchantments.add(k.getName() + i);
			});
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
		Pattern patt = Pattern.compile(ChatColor.stripColor(pattern), Pattern.CASE_INSENSITIVE);
		Matcher matcher = patt.matcher(sentence);
		return matcher.find();
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
		String formatted = String.format(Settings.CURRENCY_FORMAT.getString(), number);

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
				if (NBTEditor.getTexture(item).equals(NBTEditor.getTexture(stack))) total += item.getAmount();
			}
		} else {
			for (ItemStack item : player.getInventory().getContents()) {
				if (item == null || !item.isSimilar(stack)) continue;
				total += item.getAmount();
			}
		}
		return total;
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
				if (!NBTEditor.getTexture(item).equals(NBTEditor.getTexture(stack))) continue;
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
				if (!NBTEditor.getTexture(item).equals(NBTEditor.getTexture(stack))) continue;
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
		ItemStack item = ConfigurationItemHelper.createConfigurationItem(Settings.ITEM_BUNDLE_ITEM.getString(), Settings.ITEM_BUNDLE_NAME.getString(), Settings.ITEM_BUNDLE_LORE.getStringList(), new HashMap<String, Object>() {{
			put("%item_name%", getItemName(baseItem));
		}});

		ItemMeta meta = item.getItemMeta();
		meta.addItemFlags(ItemFlag.values());
		item.setItemMeta(meta);

		int total = items.length;
		item = NBTEditor.set(item, total, "AuctionBundleItem");
		item = NBTEditor.set(item, UUID.randomUUID().toString(), "AuctionBundleItemUUID-" + UUID.randomUUID().toString());

		for (int i = 0; i < total; i++) {
			item = NBTEditor.set(item, serializeItem(items[i]), "AuctionBundleItem-" + i);
		}

		ItemUtils.addGlow(item);
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
		return NBTEditor.contains(item, "AuctionHouseRepaired");
	}

	public double calculateListingFee(double basePrice) {
		return Settings.TAX_LISTING_FEE_PERCENTAGE.getBoolean() ? (Settings.TAX_LISTING_FEE.getDouble() / 100D) * basePrice : Settings.TAX_LISTING_FEE.getDouble();
	}

	public void listAuction(Player seller, ItemStack original, ItemStack item, int seconds, double basePrice, double bidStartPrice, double bidIncPrice, double currentPrice, boolean isBiddingItem, boolean isUsingBundle, boolean requiresHandRemove) {
		listAuction(seller, original, item, seconds, basePrice, bidStartPrice, bidIncPrice, currentPrice, isBiddingItem, isUsingBundle, requiresHandRemove, false, false);
	}

	/**
	 * Used to insert an auction into the database
	 *
	 * @param seller        Is the player who is listing the item
	 * @param item          Is the item stack being listed to the auction house
	 * @param original      Is the original item stack (only applies if using a bundle)
	 * @param seconds       Is the total amount of seconds the item will be active for
	 * @param basePrice     Is the buy now price
	 * @param bidStartPrice Is the price the bidding will start at if the item is an auction
	 * @param bidIncPrice   Is the default price increment for an auction
	 * @param currentPrice  Is the current/start price of an item
	 * @param isBiddingItem States whether the item is an auction or bin item
	 * @param isUsingBundle States whether the item is a bundled item
	 */
	public void listAuction(Player seller, ItemStack original, ItemStack item, int seconds, double basePrice, double bidStartPrice, double bidIncPrice, double currentPrice, boolean isBiddingItem, boolean isUsingBundle, boolean requiresHandRemove, boolean isInfinite, boolean allowPartialBuy) {
		if (McMMOHook.isUsingAbility(seller)) {
			AuctionHouse.getInstance().getLocale().getMessage("general.mcmmo_ability_active").sendPrefixedMessage(seller);
			return;
		}

		if (!Settings.ALLOW_SALE_OF_DAMAGED_ITEMS.getBoolean() && isDamaged(item)) {
			AuctionHouse.getInstance().getLocale().getMessage("general.cannot list damaged item").sendPrefixedMessage(seller);
			return;
		}

		if (Settings.PREVENT_SALE_OF_REPAIRED_ITEMS.getBoolean() && isRepaired(item)) {
			AuctionHouse.getInstance().getLocale().getMessage("general.cannot list repaired item").sendPrefixedMessage(seller);
			return;
		}

		if (!meetsMinItemPrice(isUsingBundle, isBiddingItem, original, basePrice, bidStartPrice)) {
			AuctionHouse.getInstance().getLocale().getMessage("pricing.minitemprice").processPlaceholder("price", AuctionAPI.getInstance().formatNumber(AuctionHouse.getInstance().getMinItemPriceManager().getMinPrice(original).getPrice())).sendPrefixedMessage(seller);
			return;
		}

		AuctionedItem auctionedItem = new AuctionedItem();
		auctionedItem.setId(UUID.randomUUID());
		auctionedItem.setOwner(seller.getUniqueId());
		auctionedItem.setHighestBidder(seller.getUniqueId());
		auctionedItem.setOwnerName(seller.getName());
		auctionedItem.setHighestBidderName(seller.getName());
		auctionedItem.setItem(item);
		auctionedItem.setCategory(MaterialCategorizer.getMaterialCategory(item));
		auctionedItem.setExpiresAt(System.currentTimeMillis() + 1000L * seconds);
		auctionedItem.setBidItem(isBiddingItem);
		auctionedItem.setExpired(false);

		auctionedItem.setBasePrice(Settings.ROUND_ALL_PRICES.getBoolean() ? Math.round(basePrice) : basePrice);
		auctionedItem.setBidStartingPrice(Settings.ROUND_ALL_PRICES.getBoolean() ? Math.round(bidStartPrice) : bidStartPrice);
		auctionedItem.setBidIncrementPrice(Settings.ROUND_ALL_PRICES.getBoolean() ? Math.round(bidIncPrice) : bidIncPrice);
		auctionedItem.setCurrentPrice(Settings.ROUND_ALL_PRICES.getBoolean() ? Math.round(currentPrice) : currentPrice);

		auctionedItem.setListedWorld(seller.getWorld().getName());
		auctionedItem.setInfinite(isInfinite);
		auctionedItem.setAllowPartialBuy(allowPartialBuy);

		if (Settings.TAX_ENABLED.getBoolean() && Settings.TAX_CHARGE_LISTING_FEE.getBoolean()) {
			if (!EconomyManager.hasBalance(seller, calculateListingFee(basePrice))) {
				AuctionHouse.getInstance().getLocale().getMessage("auction.tax.cannotpaylistingfee").processPlaceholder("price", AuctionAPI.getInstance().formatNumber(calculateListingFee(basePrice))).sendPrefixedMessage(seller);
				return;
			}
			EconomyManager.withdrawBalance(seller, calculateListingFee(basePrice));
			AuctionHouse.getInstance().getLocale().getMessage("auction.tax.paidlistingfee").processPlaceholder("price", AuctionAPI.getInstance().formatNumber(calculateListingFee(basePrice))).sendPrefixedMessage(seller);
			AuctionHouse.getInstance().getLocale().getMessage("pricing.moneyremove").processPlaceholder("player_balance", AuctionAPI.getInstance().formatNumber(EconomyManager.getBalance(seller))).processPlaceholder("price", AuctionAPI.getInstance().formatNumber(calculateListingFee(basePrice))).sendPrefixedMessage(seller);
		}

		AuctionStartEvent startEvent = new AuctionStartEvent(seller, auctionedItem);
		Bukkit.getServer().getPluginManager().callEvent(startEvent);
		if (startEvent.isCancelled()) return;

		ItemStack finalItemToSell = item.clone();
		int totalOriginal = isUsingBundle ? AuctionAPI.getInstance().getItemCountInPlayerInventory(seller, original) : finalItemToSell.getAmount();

		if (requiresHandRemove)
			PlayerUtils.takeActiveItem(seller, CompatibleHand.MAIN_HAND, totalOriginal);


		SoundManager.getInstance().playSound(seller, Settings.SOUNDS_LISTED_ITEM_ON_AUCTION_HOUSE.getString(), 1.0F, 1.0F);
		String NAX = AuctionHouse.getInstance().getLocale().getMessage("auction.biditemwithdisabledbuynow").getMessage();
		String msg = AuctionHouse.getInstance().getLocale().getMessage(auctionedItem.isBidItem() ? "auction.listed.withbid" : "auction.listed.nobid")
				.processPlaceholder("amount", finalItemToSell.getAmount())
				.processPlaceholder("item", AuctionAPI.getInstance().getItemName(finalItemToSell))
				.processPlaceholder("base_price", auctionedItem.getBasePrice() <= -1 ? NAX : AuctionAPI.getInstance().formatNumber(auctionedItem.getBasePrice()))
				.processPlaceholder("start_price", AuctionAPI.getInstance().formatNumber(auctionedItem.getBidStartingPrice()))
				.processPlaceholder("increment_price", AuctionAPI.getInstance().formatNumber(auctionedItem.getBidIncrementPrice())).getMessage();

		if (AuctionHouse.getInstance().getAuctionPlayerManager().getPlayer(seller.getUniqueId()) == null) {
			AuctionHouse.getInstance().getLocale().newMessage(TextUtils.formatText("&cCould not find auction player instance for&f: &e" + seller.getName() + "&c creating one now.")).sendPrefixedMessage(Bukkit.getConsoleSender());
			AuctionHouse.getInstance().getAuctionPlayerManager().addPlayer(new AuctionPlayer(seller));
		}

		if (AuctionHouse.getInstance().getAuctionPlayerManager().getPlayer(seller.getUniqueId()).isShowListingInfo()) {
			AuctionHouse.getInstance().getLocale().newMessage(msg).sendPrefixedMessage(seller);
		}


		// Actually attempt the insertion now
		AuctionHouse.getInstance().getDataManager().insertAuctionAsync(auctionedItem, (error, inserted) -> {
			if (error != null) {
				AuctionHouse.getInstance().getLocale().getMessage("general.something_went_wrong_while_listing").sendPrefixedMessage(seller);
				ItemStack originalCopy = original.clone();
				if (isUsingBundle) {
					originalCopy.setAmount(1);
					for (int i = 0; i < totalOriginal; i++) PlayerUtils.giveItem(seller, originalCopy);
				} else {
					originalCopy.setAmount(totalOriginal);
					PlayerUtils.giveItem(seller, originalCopy);
				}

				// If the item could not be added for whatever reason and the tax listing fee is enabled, refund them
				if (Settings.TAX_ENABLED.getBoolean() && Settings.TAX_CHARGE_LISTING_FEE.getBoolean()) {
					EconomyManager.deposit(seller, calculateListingFee(basePrice));
					AuctionHouse.getInstance().getLocale().getMessage("pricing.moneyadd").processPlaceholder("player_balance", AuctionAPI.getInstance().formatNumber(EconomyManager.getBalance(seller))).processPlaceholder("price", AuctionAPI.getInstance().formatNumber(calculateListingFee(basePrice))).sendPrefixedMessage(seller);
				}
				return;
			}

			AuctionHouse.getInstance().getAuctionItemManager().addAuctionItem(auctionedItem);
			if (Settings.BROADCAST_AUCTION_LIST.getBoolean()) {

				final String prefix = AuctionHouse.getInstance().getLocale().getMessage("general.prefix").getMessage();


				String msgToAll = AuctionHouse.getInstance().getLocale().getMessage(auctionedItem.isBidItem() ? "auction.broadcast.withbid" : "auction.broadcast.nobid")
						.processPlaceholder("amount", finalItemToSell.getAmount())
						.processPlaceholder("player", seller.getName())
						.processPlaceholder("player_displayname", AuctionAPI.getInstance().getDisplayName(seller))
						.processPlaceholder("item", AuctionAPI.getInstance().getItemName(finalItemToSell))
						.processPlaceholder("base_price", auctionedItem.getBasePrice() <= -1 ? NAX : AuctionAPI.getInstance().formatNumber(auctionedItem.getBasePrice()))
						.processPlaceholder("start_price", AuctionAPI.getInstance().formatNumber(auctionedItem.getBidStartingPrice()))
						.processPlaceholder("increment_price", AuctionAPI.getInstance().formatNumber(auctionedItem.getBidIncrementPrice())).getMessage();

				Bukkit.getOnlinePlayers().forEach(p -> {
					if (!p.getUniqueId().equals(seller.getUniqueId()))
						p.sendMessage(TextUtils.formatText((prefix.length() == 0 ? "" : prefix + " ") + msgToAll));
				});
			}
		});
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

	public void depositBalance(OfflinePlayer player, double amount) {
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
}
