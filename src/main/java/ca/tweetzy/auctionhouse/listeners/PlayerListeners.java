package ca.tweetzy.auctionhouse.listeners;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.UpdateChecker;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.core.utils.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * The current file has been created by Kiran Hart
 * Date Created: February 10 2021
 * Time Created: 1:33 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class PlayerListeners implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(AuctionHouse.getInstance(), () -> {
            AuctionHouse.getInstance().getAuctionPlayerManager().addPlayer(new AuctionPlayer(player));
            if (AuctionHouse.getInstance().getStatus() == UpdateChecker.UpdateStatus.UNRELEASED_VERSION && player.isOp()) {
                AuctionHouse.getInstance().getLocale().getMessage(TextUtils.formatText(String.format("&dYou're running an unreleased version of Auction House &f(&c%s&f)", AuctionHouse.getInstance().getDescription().getVersion()))).sendPrefixedMessage(player);
            }
        }, 20);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        AuctionHouse.getInstance().getAuctionPlayerManager().removePlayer(player.getUniqueId());
        AuctionHouse.getInstance().getAuctionPlayerManager().getCooldowns().remove(player.getUniqueId());
    }
}
