package ca.tweetzy.auctionhouse.managers;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.auction.AuctionBan;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.core.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: July 21 2021
 * Time Created: 2:27 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class AuctionBanManager {

    private final ConcurrentHashMap<UUID, AuctionBan> bans = new ConcurrentHashMap<>();

    public void addBan(AuctionBan ban) {
        if (ban == null) return;
        this.bans.put(ban.getBannedPlayer(), ban);
    }

    public void removeBan(UUID player) {
        if (player == null) return;
        this.bans.remove(player);
    }

    public ConcurrentHashMap<UUID, AuctionBan> getBans() {
        return this.bans;
    }

    public boolean checkAndHandleBan(Player player) {
        if (this.bans.containsKey(player.getUniqueId())) {
            long time = this.bans.get(player.getUniqueId()).getTime();
            if (System.currentTimeMillis() >= time) {
                removeBan(player.getUniqueId());
                return false;
            }
            AuctionHouse.getInstance().getLocale().getMessage("bans.remainingtime").processPlaceholder("ban_amount", TimeUtils.makeReadable(time - System.currentTimeMillis())).sendPrefixedMessage(player);
            return true;
        }
        return false;
    }

    public void loadBans() {
        AuctionHouse.getInstance().getDataManager().getBans(all -> all.forEach(this::addBan));
    }

    public void saveBans( boolean async) {
        AuctionHouse.getInstance().getDataManager().saveBans(new ArrayList<>(getBans().values()), async);
    }
}