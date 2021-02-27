package ca.tweetzy.auctionhouse.auction;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.core.utils.nms.NBTEditor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: February 02 2021
 * Time Created: 6:26 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */

@Getter
@Setter
public class AuctionPlayer {

    private final UUID id;
    private final Player player;

    private boolean viewingAuctionHouse;
    private int currentAuctionPage;
    private AuctionItemCategory preferredCategory;

    public AuctionPlayer(Player player) {
        this.player = player;
        this.id = player.getUniqueId();
        this.viewingAuctionHouse = false;
        this.currentAuctionPage = 1;
        this.preferredCategory = AuctionItemCategory.ALL;
    }

    public List<AuctionItem> getActiveItems() {
        return AuctionHouse.getInstance().getAuctionItemManager().getAuctionItems().stream().filter(item -> item.getOwner().equals(this.player.getUniqueId())).collect(Collectors.toList());
    }

    public int getSellLimit() {
        if (player.hasPermission("auctionhouse.maxsell.*")) return Integer.MAX_VALUE;
        for (int i = 1001; i > 0; i--) {
            if (player.hasPermission("auctionhouse.maxsell." + i)) return i;
        }
        return 0;
    }

    public ArrayList<ItemStack> getExpiredItems(Player player, boolean addNBT) {
        ConfigurationSection section = AuctionHouse.getInstance().getData().getConfigurationSection("expired." + player.getUniqueId().toString());
        ArrayList<ItemStack> expiredItems = new ArrayList<>();
        if (section == null || section.getKeys(false).size() == 0) return expiredItems;

        for (String nodes : section.getKeys(false)) {
            ItemStack stack = AuctionHouse.getInstance().getData().getItemStack("expired." + player.getUniqueId().toString() + "." + nodes + ".item");
            if (addNBT) {
                stack = NBTEditor.set(stack, AuctionHouse.getInstance().getData().getString("expired." + player.getUniqueId().toString() + "." + nodes + ".key"), "AuctionItemKey");
            }
            expiredItems.add(stack);
        }

        return expiredItems;
    }

    public boolean isAtSellLimit() {
        return getSellLimit() - 1 < getActiveItems().size();
    }
}
