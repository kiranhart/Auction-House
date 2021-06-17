package ca.tweetzy.auctionhouse.listeners;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.api.UpdateChecker;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.helpers.PlayerHelper;
import ca.tweetzy.core.compatibility.CompatibleHand;
import ca.tweetzy.core.compatibility.ServerVersion;
import ca.tweetzy.core.compatibility.XMaterial;
import ca.tweetzy.core.utils.PlayerUtils;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.core.utils.nms.NBTEditor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

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
            AuctionHouse.getInstance().getLogger().info("Adding player: " + player.getName() + " to Auction Player list.");
            if (AuctionHouse.getInstance().getStatus() == UpdateChecker.UpdateStatus.UNRELEASED_VERSION && player.isOp()) {
                AuctionHouse.getInstance().getLocale().newMessage(TextUtils.formatText(String.format("&dYou're running an unreleased version of Auction House &f(&c%s&f)", AuctionHouse.getInstance().getDescription().getVersion()))).sendPrefixedMessage(player);
            }
        }, 20);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        AuctionHouse.getInstance().getAuctionPlayerManager().removePlayer(player.getUniqueId());
        AuctionHouse.getInstance().getAuctionPlayerManager().getCooldowns().remove(player.getUniqueId());
        AuctionHouse.getInstance().getLogger().info("Removing Auction Player and Cooldown instances for user: " + player.getName());
    }

    @EventHandler
    public void onBundleClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack heldItem = PlayerHelper.getHeldItem(player);

        if (heldItem == null || (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK)) return;
        if (heldItem.getType() == XMaterial.AIR.parseMaterial()) return;
        if (!NBTEditor.contains(heldItem, "AuctionBundleItem")) return;
        e.setCancelled(true);

        List<ItemStack> items = new ArrayList<>();

        for (int i = 0; i < NBTEditor.getInt(heldItem, "AuctionBundleItem"); i++) {
            items.add(AuctionAPI.getInstance().deserializeItem(NBTEditor.getByteArray(heldItem, "AuctionBundleItem-" + i)));
        }

        // TODO FIX THE TWEETY CORE TAKE ACTIVE ITEM METHOD, IN THE MEAN TIME DO IT LIKE THIS
        if (heldItem.getAmount() >= 2) {
            heldItem.setAmount(heldItem.getAmount() - 1);
        } else {
            if (ServerVersion.isServerVersionAbove(ServerVersion.V1_8)) {
                player.getInventory().setItemInMainHand(XMaterial.AIR.parseItem());
            } else {
                player.getInventory().setItemInHand(XMaterial.AIR.parseItem());
            }
        }

        PlayerUtils.giveItem(player, items);
    }
}
