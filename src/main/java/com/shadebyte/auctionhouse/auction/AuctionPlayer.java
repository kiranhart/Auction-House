package com.shadebyte.auctionhouse.auction;

import com.shadebyte.auctionhouse.Core;
import com.shadebyte.auctionhouse.api.enums.Permissions;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * The current file has been created by Kiran Hart
 * Date Created: 7/6/2018
 * Time Created: 11:49 AM
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise.
 */
public class AuctionPlayer {

    private Player player;

    public AuctionPlayer(Player player) {
        this.player = player;
    }

    public int getTotalActiveAuctions() {
        return getAuctionItems().size();
    }

    public List<AuctionItem> getAuctionItems() {
        List<AuctionItem> list = new ArrayList<>();
        for (AuctionItem item : Core.getInstance().auctionItems) {
            if (item.getOwner().equals(player.getUniqueId().toString())) {
                list.add(item);
            }
        }
        return list;
    }

    public int getTotalExpiredAuctions() {
        int total;
        ConfigurationSection section = Core.getInstance().getData().getConfig().getConfigurationSection("expired." + player.getUniqueId().toString());
        total = (section != null) ? section.getKeys(false).size() : 0;
        return total;
    }

    public int getLimit() {
        if (player.hasPermission(Permissions.MAX_AUCTIONS.getNode() + ".*")) {
            return Integer.MAX_VALUE;
        }

        for (int i = 1001; i > 0; i--) {
            if (player.hasPermission(Permissions.MAX_AUCTIONS.getNode() + "." + i)) {
                return i;
            }
        }
        return 0;
    }

    public Player getPlayer() {
        return player;
    }
}
