package ca.tweetzy.auctionhouse.listeners;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.api.UpdateChecker;
import ca.tweetzy.auctionhouse.guis.GUIAuctionHouse;
import ca.tweetzy.auctionhouse.helpers.PlayerHelper;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.compatibility.ServerVersion;
import ca.tweetzy.core.compatibility.XMaterial;
import ca.tweetzy.core.utils.PlayerUtils;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.core.utils.nms.NBTEditor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

/**
 * The current file has been created by Kiran Hart
 * Date Created: February 10 2021
 * Time Created: 1:33 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class PlayerListeners implements Listener {

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();

		AuctionHouse.getInstance().getAuctionPlayerManager().addPlayer(player);

		Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(AuctionHouse.getInstance(), () -> {

			if (Settings.UPDATE_CHECKER.getBoolean() && AuctionHouse.getInstance().getStatus() == UpdateChecker.UpdateStatus.UNRELEASED_VERSION && player.isOp()) {
				AuctionHouse.getInstance().getLocale().newMessage(TextUtils.formatText(String.format("&dYou're running an unreleased version of Auction House &f(&c%s&f)", AuctionHouse.getInstance().getDescription().getVersion()))).sendPrefixedMessage(player);
			}
		}, 20);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();


		//todo maybe add this back if it messes up stuff
//		AuctionHouse.getInstance().getAuctionPlayerManager().getCooldowns().remove(player.getUniqueId());
		AuctionHouse.getInstance().getAuctionPlayerManager().getSellHolding().remove(player.getUniqueId());
		AuctionHouse.getInstance().getLogger().info("Removing sell holding instance for user: " + player.getName());
	}

	@EventHandler
	public void onCraftWithBundle(PrepareItemCraftEvent event) {
		final ItemStack[] craftingItems = event.getInventory().getMatrix();

		for (ItemStack item : craftingItems) {
			if (item == null || item.getType() == XMaterial.AIR.parseMaterial()) continue;
			if (NBTEditor.contains(item, "AuctionBundleItem")) {
				event.getInventory().setResult(XMaterial.AIR.parseItem());
			}
		}
	}

	@EventHandler
	public void onAuctionChestClick(PlayerInteractEvent e) {
		if (ServerVersion.isServerVersionBelow(ServerVersion.V1_14)) return;

		final Player player = e.getPlayer();
		final Block block = e.getClickedBlock();

		if (block == null || block.getType() != XMaterial.CHEST.parseMaterial()) return;
		final Chest chest = (Chest) block.getState();

		final NamespacedKey key = new NamespacedKey(AuctionHouse.getInstance(), "AuctionHouseMarkedChest");
		if (chest.getPersistentDataContainer().has(key, PersistentDataType.BYTE)) {
			e.setUseInteractedBlock(Event.Result.DENY);
			e.setCancelled(true);

			if (AuctionHouse.getInstance().getAuctionBanManager().checkAndHandleBan(player)) {
				return;
			}

			AuctionHouse.getInstance().getGuiManager().showGUI(player, new GUIAuctionHouse(AuctionHouse.getInstance().getAuctionPlayerManager().getPlayer(player.getUniqueId())));
		}
	}

	@EventHandler
	public void onBundleClick(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		ItemStack heldItem = PlayerHelper.getHeldItem(player);

		if (heldItem == null || (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK))
			return;
		if (heldItem.getType() == XMaterial.AIR.parseMaterial()) return;
		if (!NBTEditor.contains(heldItem, "AuctionBundleItem")) return;
		e.setCancelled(true);

		List<ItemStack> items = new ArrayList<>();

		for (int i = 0; i < NBTEditor.getInt(heldItem, "AuctionBundleItem"); i++) {
			items.add(AuctionAPI.getInstance().deserializeItem(NBTEditor.getByteArray(heldItem, "AuctionBundleItem-" + i)));
		}

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

	@EventHandler
	public void onInventoryClick(PrepareAnvilEvent event) {
		ItemStack stack = event.getResult();
		if (stack == null) return;

		stack = NBTEditor.set(stack, "AUCTION_REPAIRED", "AuctionHouseRepaired");
		event.setResult(stack);
	}
}
