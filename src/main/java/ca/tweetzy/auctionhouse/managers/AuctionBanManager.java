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

    public void loadBans(boolean useDatabase) {
        if (useDatabase) {
            AuctionHouse.getInstance().getDataManager().getBans(all -> all.forEach(this::addBan));
        } else {
            if (AuctionHouse.getInstance().getData().contains("auction bans") && AuctionHouse.getInstance().getData().isList("auction bans")) {
                List<AuctionBan> auctionBans = AuctionHouse.getInstance().getData().getStringList("auction bans").stream().map(AuctionAPI.getInstance()::convertBase64ToObject).map(object -> (AuctionBan) object).collect(Collectors.toList());
                long start = System.currentTimeMillis();
                auctionBans.forEach(this::addBan);
                AuctionHouse.getInstance().getLocale().newMessage(TextUtils.formatText(String.format("&aLoaded &2%d &abans(s) in &e%d&fms", auctionBans.size(), System.currentTimeMillis() - start))).sendPrefixedMessage(Bukkit.getConsoleSender());
            }
        }
    }

    public void saveBans(boolean useDatabase, boolean async) {
        if (useDatabase) {
            AuctionHouse.getInstance().getDataManager().saveBans(new ArrayList<>(getBans().values()), async);
        } else {
            AuctionHouse.getInstance().getData().set("auction bans", this.bans.values().stream().map(AuctionAPI.getInstance()::convertToBase64).collect(Collectors.toList()));
            AuctionHouse.getInstance().getData().save();
        }
    }
}