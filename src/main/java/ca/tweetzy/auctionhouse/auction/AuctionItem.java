package ca.tweetzy.auctionhouse.auction;

import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.utils.PlayerUtils;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.core.utils.items.ItemUtils;
import ca.tweetzy.core.utils.nms.NBTEditor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
public class AuctionItem {

    private UUID owner;
    private UUID highestBidder;

    private ItemStack originalItem;
    private AuctionItemCategory category;
    private UUID key;

    private double basePrice;
    private double bidStartPrice;
    private double bidIncPrice;
    private double currentPrice;

    private int remainingTime;

    public AuctionItem(UUID owner, UUID highestBidder, ItemStack originalItem, AuctionItemCategory category, UUID key, double basePrice, double bidStartPrice, double bidIncPrice, double currentPrice, int remainingTime) {
        this.owner = owner;
        this.highestBidder = highestBidder;
        this.originalItem = originalItem;
        this.category = category;
        this.key = key;
        this.basePrice = basePrice;
        this.bidStartPrice = bidStartPrice;
        this.bidIncPrice = bidIncPrice;
        this.currentPrice = currentPrice;
        this.remainingTime = remainingTime;
    }

    public void updateRemainingTime(int removeAmount) {
        this.remainingTime = Math.max(this.remainingTime - removeAmount, 0);
    }

    public String getDisplayName() {
        String name;
        if (this.originalItem.hasItemMeta()) {
            name = (this.originalItem.getItemMeta().hasDisplayName()) ? this.originalItem.getItemMeta().getDisplayName() : StringUtils.capitalize(this.originalItem.getType().name().toLowerCase().replace("_", " "));
        } else {
            name = StringUtils.capitalize(this.originalItem.getType().name().toLowerCase().replace("_", " "));
        }
        return name;
    }

    public ItemStack getDisplayStack(AuctionStackType type) {
        ItemStack itemStack = this.originalItem.clone();
        itemStack.setAmount(Math.max(itemStack.getAmount(), 1));
        ItemMeta meta = itemStack.getItemMeta();
        List<String> lore = (meta.hasLore()) ? meta.getLore() : new ArrayList<>();

        String theSeller = (this.owner == null) ? "&eSeller Name???" : Bukkit.getOfflinePlayer(this.owner).getName();
        String highestBidder = (this.bidStartPrice <= 0) ? "" : (this.owner.equals(this.highestBidder)) ? Bukkit.getOfflinePlayer(this.owner).getName() : (Bukkit.getOfflinePlayer(this.highestBidder).isOnline()) ? Bukkit.getOfflinePlayer(this.highestBidder).getPlayer().getName() : "Offline";
        String basePrice = Settings.USE_SHORT_NUMBERS_ON_ITEMS.getBoolean() ? AuctionAPI.getInstance().getFriendlyNumber(this.basePrice) : String.format("%,.2f", this.basePrice);
      //  String bidStartPrice = Settings.USE_SHORT_NUMBERS_ON_ITEMS.getBoolean() ? AuctionAPI.getInstance().getFriendlyNumber(this.bidStartPrice) : String.format("%,.2f", this.bidStartPrice);
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
//        itemStack = NBTEditor.set(itemStack, getKey(), "AuctionItemKey");
        return itemStack;
    }
}
