package com.kiranhart.auctionhouse.auction;
/*
    The current file was created by Kiran Hart
    Date: August 03 2019
    Time: 8:11 PM
    
    Code within this class is not to be redistributed without proper permission.
*/

import com.kiranhart.auctionhouse.Core;
import com.kiranhart.auctionhouse.api.statics.AuctionPermissions;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

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
        for (AuctionItem item : Core.getInstance().getAuctionItems()) {
            if (item.getOwner().equals(player.getUniqueId())) {
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
        if (player.hasPermission(AuctionPermissions.MAX_AUCTIONS + ".*")) {
            return Integer.MAX_VALUE;
        }

        for (int i = 1001; i > 0; i--) {
            if (player.hasPermission(AuctionPermissions.MAX_AUCTIONS + "." + i)) {
                return i;
            }
        }
        return 0;
    }

    public Player getPlayer() {
        return player;
    }
}

