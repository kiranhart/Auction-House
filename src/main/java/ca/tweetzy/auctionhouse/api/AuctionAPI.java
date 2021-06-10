package ca.tweetzy.auctionhouse.api;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.events.AuctionEndEvent;
import ca.tweetzy.auctionhouse.auction.AuctionItem;
import ca.tweetzy.auctionhouse.auction.AuctionSaleType;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.compatibility.XMaterial;
import ca.tweetzy.core.utils.PlayerUtils;
import ca.tweetzy.core.utils.TextUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.awt.*;
import java.io.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
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
        int power;
        String suffix = " KMBTQ";
        String formattedNumber = "";

        NumberFormat formatter = new DecimalFormat("#,###.#");
        power = (int) StrictMath.log10(value);
        value = value / (Math.pow(10, (power / 3) * 3));
        formattedNumber = formatter.format(value);
        formattedNumber = formattedNumber + suffix.charAt(power / 3);
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
    public void sendDiscordMessage(String webhook, OfflinePlayer seller, OfflinePlayer buyer, AuctionItem auctionItem, AuctionSaleType saleType, boolean isNew, boolean isBid) {
        DiscordWebhook hook = new DiscordWebhook(webhook);
        hook.setUsername(Settings.DISCORD_MSG_USERNAME.getString());
        hook.setAvatarUrl(Settings.DISCORD_MSG_PFP.getString());

        String[] possibleColours = Settings.DISCORD_MSG_DEFAULT_COLOUR.getString().split("-");
        Color colour = Settings.DISCORD_MSG_USE_RANDOM_COLOUR.getBoolean()
                ? Color.getHSBColor(ThreadLocalRandom.current().nextFloat() * 360F, ThreadLocalRandom.current().nextFloat() * 101F, ThreadLocalRandom.current().nextFloat() * 101F)
                : Color.getHSBColor(Float.parseFloat(possibleColours[0]) / 360, Float.parseFloat(possibleColours[1]) / 100, Float.parseFloat(possibleColours[2]) / 100);

        hook.addEmbed(new DiscordWebhook.EmbedObject()
                .setTitle(isNew ? Settings.DISCORD_MSG_START_TITLE.getString() : Settings.DISCORD_MSG_FINISH_TITLE.getString())
                .setColor(colour)
                .addField(Settings.DISCORD_MSG_FIELD_SELLER_NAME.getString(), Settings.DISCORD_MSG_FIELD_SELLER_VALUE.getString().replace("%seller%", seller.getName() != null ? seller.getName() : AuctionHouse.getInstance().getLocale().getMessage("discord.player_lost").getMessage()), Settings.DISCORD_MSG_FIELD_SELLER_INLINE.getBoolean())
                .addField(Settings.DISCORD_MSG_FIELD_BUYER_NAME.getString(), isNew ? AuctionHouse.getInstance().getLocale().getMessage("discord.no_buyer").getMessage() : Settings.DISCORD_MSG_FIELD_BUYER_VALUE.getString().replace("%buyer%", buyer.getName() != null ? buyer.getName() : AuctionHouse.getInstance().getLocale().getMessage("discord.player_lost").getMessage()), Settings.DISCORD_MSG_FIELD_BUYER_INLINE.getBoolean())
                .addField(Settings.DISCORD_MSG_FIELD_BUY_NOW_PRICE_NAME.getString(), Settings.DISCORD_MSG_FIELD_BUY_NOW_PRICE_VALUE.getString().replace("%buy_now_price%", this.getFriendlyNumber(auctionItem.getBasePrice())), Settings.DISCORD_MSG_FIELD_BUY_NOW_PRICE_INLINE.getBoolean())
                .addField(Settings.DISCORD_MSG_FIELD_FINAL_PRICE_NAME.getString(), isNew ? AuctionHouse.getInstance().getLocale().getMessage("discord.not_sold").getMessage() : Settings.DISCORD_MSG_FIELD_FINAL_PRICE_VALUE.getString().replace("%final_price%", this.getFriendlyNumber(isBid ? auctionItem.getCurrentPrice() : auctionItem.getBasePrice())), Settings.DISCORD_MSG_FIELD_FINAL_PRICE_INLINE.getBoolean())
                .addField(Settings.DISCORD_MSG_FIELD_IS_BID_NAME.getString(), Settings.DISCORD_MSG_FIELD_IS_BID_VALUE.getString().replace("%is_bid%", isBid ? AuctionHouse.getInstance().getLocale().getMessage("discord.is_bid_true").getMessage() : AuctionHouse.getInstance().getLocale().getMessage("discord.is_bid_false").getMessage()), Settings.DISCORD_MSG_FIELD_IS_BID_INLINE.getBoolean())
                .addField(Settings.DISCORD_MSG_FIELD_PURCHASE_TYPE_NAME.getString(), isNew ? AuctionHouse.getInstance().getLocale().getMessage("discord.not_bought").getMessage() : Settings.DISCORD_MSG_FIELD_PURCHASE_TYPE_VALUE.getString().replace("%purchase_type%", saleType == AuctionSaleType.USED_BIDDING_SYSTEM ? AuctionHouse.getInstance().getLocale().getMessage("discord.sale_bid_win").getMessage() : AuctionHouse.getInstance().getLocale().getMessage("discord.sale_immediate_buy").getMessage()), Settings.DISCORD_MSG_FIELD_PURCHASE_INLINE.getBoolean())
                .addField(Settings.DISCORD_MSG_FIELD_ITEM_NAME.getString(), Settings.DISCORD_MSG_FIELD_ITEM_VALUE.getString().replace("%item_name%", auctionItem.getItemName()), Settings.DISCORD_MSG_FIELD_ITEM_INLINE.getBoolean())
                .addField(Settings.DISCORD_MSG_FIELD_ITEM_AMOUNT_NAME.getString(), Settings.DISCORD_MSG_FIELD_ITEM_AMOUNT_VALUE.getString().replace("%item_amount%", String.valueOf(this.deserializeItem(auctionItem.getRawItem()).getAmount())), Settings.DISCORD_MSG_FIELD_ITEM_AMOUNT_INLINE.getBoolean())
        );

        try {
            hook.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getItemName(ItemStack stack) {
        Objects.requireNonNull(stack, "Item stack cannot be null when getting name");
        return stack.getItemMeta().hasDisplayName() ? stack.getItemMeta().getDisplayName() : TextUtils.formatText("&f" + WordUtils.capitalize(stack.getType().name().toLowerCase().replace("_", " ")));
    }

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

    public boolean match(String pattern, String sentence) {
        Pattern patt = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        Matcher matcher = patt.matcher(sentence);
        return matcher.find();
    }

    public String formatNumber(double number) {
        String formatted = String.format("%,.2f", number);
        return Settings.USE_ALTERNATE_CURRENCY_FORMAT.getBoolean() ? replaceLast(formatted.replace(",", "."), ".", ",") : formatted;
    }

    private String replaceLast(String string, String substring, String replacement) {
        int index = string.lastIndexOf(substring);
        if (index == -1) return string;
        return string.substring(0, index) + replacement + string.substring(index + substring.length());
    }

    public List<String> getCommandFlags(String... args) {
        List<String> flags = new ArrayList<>();
        for (String arg : args) {
            if (arg.startsWith("-") && arg.length() >= 2) {
                flags.add(arg.substring(0, 2));
            }
        }
        return flags;
    }

    public void endAuction(AuctionItem item) {
        // check if the auction item owner is the same as the highest bidder
        if (item.getOwner().equals(item.getHighestBidder())) {
            // was not sold
            item.setExpired(true);
        } else {
            // the item was sold ?? then do the checks
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(item.getHighestBidder());
            AuctionEndEvent auctionEndEvent;
            if (offlinePlayer.isOnline()) {
                if (AuctionHouse.getInstance().getEconomy().has(offlinePlayer, item.getCurrentPrice())) {
                    auctionEndEvent = new AuctionEndEvent(Bukkit.getOfflinePlayer(item.getOwner()), offlinePlayer, item, AuctionSaleType.USED_BIDDING_SYSTEM);
                    AuctionHouse.getInstance().getServer().getPluginManager().callEvent(auctionEndEvent);

                    if (!auctionEndEvent.isCancelled()) {
                        // withdraw money and give to the owner
                        AuctionHouse.getInstance().getEconomy().withdrawPlayer(offlinePlayer, item.getCurrentPrice());
                        AuctionHouse.getInstance().getEconomy().depositPlayer(Bukkit.getOfflinePlayer(item.getOwner()), item.getCurrentPrice());
                        // send a message to each of them
                        AuctionHouse.getInstance().getLocale().getMessage("auction.bidwon")
                                .processPlaceholder("item", WordUtils.capitalizeFully(AuctionAPI.getInstance().deserializeItem(item.getRawItem()).getType().name().replace("_", " ")))
                                .processPlaceholder("amount", AuctionAPI.getInstance().deserializeItem(item.getRawItem()).getAmount())
                                .processPlaceholder("price", AuctionAPI.getInstance().formatNumber(item.getCurrentPrice()))
                                .sendPrefixedMessage(offlinePlayer.getPlayer());
                        AuctionHouse.getInstance().getLocale().getMessage("pricing.moneyremove").processPlaceholder("price", AuctionAPI.getInstance().formatNumber(item.getCurrentPrice())).sendPrefixedMessage(offlinePlayer.getPlayer());
                        // if the original owner is online, let them know they sold an item
                        if (Bukkit.getOfflinePlayer(item.getOwner()).isOnline()) {
                            AuctionHouse.getInstance().getLocale().getMessage("auction.itemsold")
                                    .processPlaceholder("item", WordUtils.capitalizeFully(AuctionAPI.getInstance().deserializeItem(item.getRawItem()).getType().name().replace("_", " ")))
                                    .processPlaceholder("price", AuctionAPI.getInstance().formatNumber(item.getCurrentPrice()))
                                    .processPlaceholder("buyer_name", Bukkit.getOfflinePlayer(item.getHighestBidder()).getName())
                                    .sendPrefixedMessage(Bukkit.getOfflinePlayer(item.getOwner()).getPlayer());
                            AuctionHouse.getInstance().getLocale().getMessage("pricing.moneyadd").processPlaceholder("price", AuctionAPI.getInstance().formatNumber(item.getCurrentPrice())).sendPrefixedMessage(Bukkit.getOfflinePlayer(item.getOwner()).getPlayer());
                        }

                        // since they're online, try to add the item to their inventory
                        // TODO CLEAN THIS UP A BIT
                        if (Settings.ALLOW_PURCHASE_IF_INVENTORY_FULL.getBoolean()) {
                            PlayerUtils.giveItem(offlinePlayer.getPlayer(), AuctionAPI.getInstance().deserializeItem(item.getRawItem()));
                            AuctionHouse.getInstance().getAuctionItemManager().removeItem(item.getKey());
                        } else {
                            if (offlinePlayer.getPlayer().getInventory().firstEmpty() == -1) {
                                item.setOwner(offlinePlayer.getUniqueId());
                                item.setExpired(true);
                            } else {
                                PlayerUtils.giveItem(offlinePlayer.getPlayer(), AuctionAPI.getInstance().deserializeItem(item.getRawItem()));
                                AuctionHouse.getInstance().getAuctionItemManager().removeItem(item.getKey());
                            }
                        }
                    }

                } else {
                    // they don't have enough money to buy it, so send it back to the original owner
                    item.setExpired(true);
                }
            } else {
                // offline, so save their purchase in the collection inventory
                if (AuctionHouse.getInstance().getEconomy().has(offlinePlayer, item.getCurrentPrice())) {
                    auctionEndEvent = new AuctionEndEvent(Bukkit.getOfflinePlayer(item.getOwner()), offlinePlayer, item, AuctionSaleType.USED_BIDDING_SYSTEM);
                    AuctionHouse.getInstance().getServer().getPluginManager().callEvent(auctionEndEvent);

                    if (!auctionEndEvent.isCancelled()) {
                        // withdraw money and give to the owner
                        AuctionHouse.getInstance().getEconomy().withdrawPlayer(offlinePlayer, item.getCurrentPrice());
                        AuctionHouse.getInstance().getEconomy().depositPlayer(Bukkit.getOfflinePlayer(item.getOwner()), item.getCurrentPrice());

                        if (Bukkit.getOfflinePlayer(item.getOwner()).isOnline()) {
                            AuctionHouse.getInstance().getLocale().getMessage("auction.itemsold")
                                    .processPlaceholder("item", WordUtils.capitalizeFully(AuctionAPI.getInstance().deserializeItem(item.getRawItem()).getType().name().replace("_", " ")))
                                    .processPlaceholder("price", AuctionAPI.getInstance().formatNumber(item.getCurrentPrice()))
                                    .processPlaceholder("buyer_name", Bukkit.getOfflinePlayer(item.getHighestBidder()).getName())
                                    .sendPrefixedMessage(Bukkit.getOfflinePlayer(item.getOwner()).getPlayer());
                            AuctionHouse.getInstance().getLocale().getMessage("pricing.moneyadd").processPlaceholder("price", AuctionAPI.getInstance().formatNumber(item.getCurrentPrice())).sendPrefixedMessage(Bukkit.getOfflinePlayer(item.getOwner()).getPlayer());
                        }

                        item.setOwner(offlinePlayer.getUniqueId());
                        item.setExpired(true);
                    }
                } else {
                    // they don't have enough money to buy it, so send it back to the original owner
                    item.setExpired(true);
                }
            }
        }
    }
}
