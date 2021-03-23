package ca.tweetzy.auctionhouse.api;

import ca.tweetzy.auctionhouse.auction.AuctionItem;
import ca.tweetzy.auctionhouse.auction.AuctionSaleType;
import ca.tweetzy.auctionhouse.settings.Settings;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.awt.*;
import java.io.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm aa");
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
                : Color.getHSBColor(Float.parseFloat(possibleColours[0]), Float.parseFloat(possibleColours[1]), Float.parseFloat(possibleColours[2]));

        hook.addEmbed(new DiscordWebhook.EmbedObject()
                .setTitle(isNew ? Settings.DISCORD_MSG_START_TITLE.getString() : Settings.DISCORD_MSG_FINISH_TITLE.getString())
                .setColor(colour)
                .addField(Settings.DISCORD_MSG_FIELD_SELLER_NAME.getString(), Settings.DISCORD_MSG_FIELD_SELLER_VALUE.getString().replace("%seller%", seller.getName() != null ? seller.getName() : "Player Lost o.O"), Settings.DISCORD_MSG_FIELD_SELLER_INLINE.getBoolean())
                .addField(Settings.DISCORD_MSG_FIELD_BUYER_NAME.getString(), isNew ? "No Buyer" : Settings.DISCORD_MSG_FIELD_BUYER_VALUE.getString().replace("%buyer%", buyer.getName() != null ? buyer.getName() : "Player Lost o.O"), Settings.DISCORD_MSG_FIELD_BUYER_INLINE.getBoolean())
                .addField(Settings.DISCORD_MSG_FIELD_BUY_NOW_PRICE_NAME.getString(), Settings.DISCORD_MSG_FIELD_BUY_NOW_PRICE_VALUE.getString().replace("%buy_now_price%", this.getFriendlyNumber(auctionItem.getBasePrice())), Settings.DISCORD_MSG_FIELD_BUY_NOW_PRICE_INLINE.getBoolean())
                .addField(Settings.DISCORD_MSG_FIELD_FINAL_PRICE_NAME.getString(), isNew ? "Not Sold" : Settings.DISCORD_MSG_FIELD_FINAL_PRICE_VALUE.getString().replace("%final_price%", this.getFriendlyNumber(isBid ? auctionItem.getCurrentPrice() : auctionItem.getBasePrice())), Settings.DISCORD_MSG_FIELD_FINAL_PRICE_INLINE.getBoolean())
                .addField(Settings.DISCORD_MSG_FIELD_IS_BID_NAME.getString(), Settings.DISCORD_MSG_FIELD_IS_BID_VALUE.getString().replace("%is_bid%", String.valueOf(isBid)), Settings.DISCORD_MSG_FIELD_IS_BID_INLINE.getBoolean())
                .addField(Settings.DISCORD_MSG_FIELD_PURCHASE_TYPE_NAME.getString(), isNew ? "Was not bought" : Settings.DISCORD_MSG_FIELD_PURCHASE_TYPE_VALUE.getString().replace("%purchase_type%", saleType == AuctionSaleType.USED_BIDDING_SYSTEM ? "Won Bid" : "Bought Immediately"), Settings.DISCORD_MSG_FIELD_PURCHASE_INLINE.getBoolean())
                .addField(Settings.DISCORD_MSG_FIELD_ITEM_NAME.getString(), Settings.DISCORD_MSG_FIELD_ITEM_VALUE.getString().replace("%item_name%", auctionItem.getItemName()), Settings.DISCORD_MSG_FIELD_ITEM_INLINE.getBoolean())
                .addField(Settings.DISCORD_MSG_FIELD_ITEM_AMOUNT_NAME.getString(), Settings.DISCORD_MSG_FIELD_ITEM_AMOUNT_VALUE.getString().replace("%item_amount%", String.valueOf(this.deserializeItem(auctionItem.getRawItem()).getAmount())), Settings.DISCORD_MSG_FIELD_ITEM_AMOUNT_INLINE.getBoolean())
        );

        try {
            hook.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
