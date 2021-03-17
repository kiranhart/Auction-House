package ca.tweetzy.auctionhouse.auction;

import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.core.utils.nms.NBTEditor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: January 17 2021
 * Time Created: 5:41 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
@Getter
@Setter
public class AuctionItem implements Serializable {

    private UUID owner;
    private UUID highestBidder;

    private byte[] rawItem;
    private AuctionItemCategory category;
    private UUID key;

    private double basePrice;
    private double bidStartPrice;
    private double bidIncPrice;
    private double currentPrice;

    private boolean expired;
    private int remainingTime;

    public AuctionItem(UUID owner, UUID highestBidder, ItemStack originalItem, AuctionItemCategory category, UUID key, double basePrice, double bidStartPrice, double bidIncPrice, double currentPrice, int remainingTime, boolean expired) {
        this.owner = owner;
        this.highestBidder = highestBidder;
        this.rawItem = AuctionAPI.getInstance().serializeItem(originalItem);
        this.category = category;
        this.key = key;
        this.basePrice = basePrice;
        this.bidStartPrice = bidStartPrice;
        this.bidIncPrice = bidIncPrice;
        this.currentPrice = currentPrice;
        this.remainingTime = remainingTime;
        this.expired = expired;
    }

    public void updateRemainingTime(int removeAmount) {
        this.remainingTime = Math.max(this.remainingTime - removeAmount, 0);
        if (this.remainingTime <= 0) this.expired = true;
    }

    public String getItemName() {
        ItemStack stack = AuctionAPI.getInstance().deserializeItem(this.rawItem);
        if (stack == null) return "";
        if (!stack.hasItemMeta()) return "";
        return stack.getItemMeta().hasDisplayName() ? ChatColor.stripColor(stack.getItemMeta().getDisplayName()) : WordUtils.capitalize(stack.getType().name().toLowerCase().replace("_", " "));
    }

    public ItemStack getDisplayStack(AuctionStackType type) {
        ItemStack itemStack = AuctionAPI.getInstance().deserializeItem(this.rawItem).clone();
        itemStack.setAmount(Math.max(itemStack.getAmount(), 1));
        ItemMeta meta = itemStack.getItemMeta();
        List<String> lore = (meta.hasLore()) ? meta.getLore() : new ArrayList<>();

        String theSeller = (this.owner == null) ? "&eSeller Name???" : Bukkit.getOfflinePlayer(this.owner).getName();
        String highestBidder = (this.bidStartPrice <= 0) ? "" : (this.owner.equals(this.highestBidder)) ? Bukkit.getOfflinePlayer(this.owner).getName() : (Bukkit.getOfflinePlayer(this.highestBidder).isOnline()) ? Bukkit.getOfflinePlayer(this.highestBidder).getPlayer().getName() : "Offline";
        String basePrice = Settings.USE_SHORT_NUMBERS_ON_ITEMS.getBoolean() ? AuctionAPI.getInstance().getFriendlyNumber(this.basePrice) : String.format("%,.2f", this.basePrice);
        String bidIncPrice = (this.bidStartPrice <= 0) ? "0" : Settings.USE_SHORT_NUMBERS_ON_ITEMS.getBoolean() ? AuctionAPI.getInstance().getFriendlyNumber(this.bidIncPrice) : String.format("%,.2f", this.bidIncPrice);
        String currentPrice = (this.bidStartPrice <= 0) ? "0" : Settings.USE_SHORT_NUMBERS_ON_ITEMS.getBoolean() ? AuctionAPI.getInstance().getFriendlyNumber(this.currentPrice) : String.format("%,.2f", this.currentPrice);

        long[] times = AuctionAPI.getInstance().getRemainingTimeValues(this.remainingTime);

        if (type == AuctionStackType.MAIN_AUCTION_HOUSE) {
            if (this.bidStartPrice <= 0) {
                Settings.AUCTION_ITEM_AUCTION_STACK.getStringList().forEach(line -> {
                    lore.add(TextUtils.formatText(line
                            .replace("%seller%", theSeller)
                            .replace("%buynowprice%", basePrice)
                            .replace("%remaining_days%", String.valueOf(times[0]))
                            .replace("%remaining_hours%", String.valueOf(times[1]))
                            .replace("%remaining_minutes%", String.valueOf(times[2]))
                            .replace("%remaining_seconds%", String.valueOf(times[3]))
                    ));
                });
            } else {
                Settings.AUCTION_ITEM_AUCTION_STACK_WITH_BID.getStringList().forEach(line -> {
                    lore.add(TextUtils.formatText(line
                            .replace("%seller%", theSeller)
                            .replace("%buynowprice%", basePrice)
                            .replace("%currentprice%", currentPrice)
                            .replace("%bidincrement%", bidIncPrice)
                            .replace("%highestbidder%", highestBidder)
                            .replace("%remaining_days%", String.valueOf(times[0]))
                            .replace("%remaining_hours%", String.valueOf(times[1]))
                            .replace("%remaining_minutes%", String.valueOf(times[2]))
                            .replace("%remaining_seconds%", String.valueOf(times[3]))
                    ));
                });
            }

            lore.addAll(this.bidStartPrice <= 0 ? Settings.AUCTION_PURCHASE_CONTROLS_BID_OFF.getStringList().stream().map(TextUtils::formatText).collect(Collectors.toList()) : Settings.AUCTION_PURCHASE_CONTROLS_BID_ON.getStringList().stream().map(TextUtils::formatText).collect(Collectors.toList()));
        } else {
            if (this.bidStartPrice <= 0) {
                Settings.AUCTION_ITEM_LISTING_STACK.getStringList().forEach(line -> {
                    lore.add(TextUtils.formatText(line
                            .replace("%seller%", theSeller)
                            .replace("%buynowprice%", basePrice)
                            .replace("%remaining_days%", String.valueOf(times[0]))
                            .replace("%remaining_hours%", String.valueOf(times[1]))
                            .replace("%remaining_minutes%", String.valueOf(times[2]))
                            .replace("%remaining_seconds%", String.valueOf(times[3]))
                    ));
                });
            } else {
                Settings.AUCTION_ITEM_LISTING_STACK_WITH_BID.getStringList().forEach(line -> {
                    lore.add(TextUtils.formatText(line
                            .replace("%seller%", theSeller)
                            .replace("%buynowprice%", basePrice)
                            .replace("%currentprice%", currentPrice)
                            .replace("%bidincrement%", bidIncPrice)
                            .replace("%highestbidder%", highestBidder)
                            .replace("%remaining_days%", String.valueOf(times[0]))
                            .replace("%remaining_hours%", String.valueOf(times[1]))
                            .replace("%remaining_minutes%", String.valueOf(times[2]))
                            .replace("%remaining_seconds%", String.valueOf(times[3]))
                    ));
                });
            }
        }

        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        itemStack = NBTEditor.set(itemStack, getKey().toString(), "AuctionItemKey");
        return itemStack;
    }
}
