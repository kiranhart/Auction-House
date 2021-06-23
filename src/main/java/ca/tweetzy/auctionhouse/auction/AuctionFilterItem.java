package ca.tweetzy.auctionhouse.auction;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;

/**
 * The current file has been created by Kiran Hart
 * Date Created: June 22 2021
 * Time Created: 3:39 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */

@Getter
@Setter
public class AuctionFilterItem implements Serializable {

    private byte[] rawItem;
    private AuctionItemCategory category;

    public AuctionFilterItem(ItemStack item, AuctionItemCategory category) {
        this.rawItem = AuctionAPI.getInstance().serializeItem(item);
        this.category = category;
    }

    public ItemStack getItemStack() {
        return AuctionAPI.getInstance().deserializeItem(this.rawItem);
    }
}
