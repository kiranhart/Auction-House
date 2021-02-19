package ca.tweetzy.auctionhouse.guis;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.events.AuctionEndEvent;
import ca.tweetzy.auctionhouse.auction.AuctionItem;
import ca.tweetzy.auctionhouse.auction.AuctionStackType;
import ca.tweetzy.auctionhouse.helpers.ConfigurationItemHelper;
import ca.tweetzy.auctionhouse.managers.SoundManager;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.compatibility.XMaterial;
import ca.tweetzy.core.inventory.TInventory;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.core.utils.nms.NBTEditor;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * The current file has been created by Kiran Hart
 * Date Created: February 18 2021
 * Time Created: 9:49 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class ActiveAuctionsGUI extends TInventory {

    private List<List<AuctionItem>> items;

    public ActiveAuctionsGUI(Player player)  {
        this.items = Lists.partition(AuctionHouse.getInstance().getAuctionPlayerManager().locateAndSelectPlayer(player).getActiveItems(), 45);
        setTitle(Settings.GUI_ACTIVE_AUCTIONS_TITLE.getString());
        setPage(1);
        setRows(6);
        setDynamic(true);
    }

    @Override
    public void onClick(InventoryClickEvent e, int slot) {
        e.setCancelled(true);
        Player player = (Player) e.getWhoClicked();

        switch (slot) {
            case 45:
                // back button (send them to the main auction house page)
                player.openInventory(new AuctionHouseGUI(AuctionHouse.getInstance().getAuctionPlayerManager().locateAndSelectPlayer(player)).getInventory());
                break;
            case 46:
                // end all the items belonging to that player
                AuctionHouse.getInstance().getAuctionItemManager().getAuctionItems().stream().filter(items -> items.getOwner().equals(player.getUniqueId())).forEach(item -> item.setRemainingTime(0));
                // reopen the gui
                player.openInventory(new ActiveAuctionsGUI(player).getInventory());
                break;
            case 48:
                if (getPage() > 1) {
                    player.openInventory(setPage(getPage() - 1).getInventory());
                    SoundManager.getInstance().playSound(player, Settings.SOUNDS_NAVIGATE_GUI_PAGES.getString(), 1f, 1f);
                }
                break;
            case 49:
                player.openInventory(new ActiveAuctionsGUI(player).getInventory());
                break;
            case 50:
                if (getPage() < this.items.size()) {
                    player.openInventory(setPage(getPage() + 1).getInventory());
                    SoundManager.getInstance().playSound(player, Settings.SOUNDS_NAVIGATE_GUI_PAGES.getString(), 1f, 1f);
                }
                break;
            default:
                if (e.getCurrentItem() != null && e.getCurrentItem().getType() != XMaterial.AIR.parseMaterial()) {
                    if (!NBTEditor.contains(e.getCurrentItem(), "AuctionItemKey")) {
                        return;
                    }

                    String key = NBTEditor.getString(e.getCurrentItem(), "AuctionItemKey");
                    if (AuctionHouse.getInstance().getAuctionItemManager().getAuctionItems().stream().noneMatch(item -> item.getKey().equals(UUID.fromString(key)))) {
                        return;
                    }

                    // set the remaining time to zero, then the tick task will handle the rest
                    AuctionHouse.getInstance().getAuctionItemManager().getAuctionItems().stream().filter(item -> item.getKey().equals(UUID.fromString(key))).findFirst().get().setRemainingTime(0);
                    player.openInventory(new ActiveAuctionsGUI(player).getInventory());
                }
                break;
        }
    }

    @Override
    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this, getSize(), getTitle());

        // set the buttons
        inventory.setItem(45, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_BACK_BTN_ITEM.getString(), Settings.GUI_BACK_BTN_NAME.getString(), Settings.GUI_BACK_BTN_LORE.getStringList(), null));
        inventory.setItem(46, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_ACTIVE_AUCTIONS_ITEM.getString(), Settings.GUI_ACTIVE_AUCTIONS_NAME.getString(), Settings.GUI_ACTIVE_AUCTIONS_LORE.getStringList(), null));
        inventory.setItem(48, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_BACK_BTN_ITEM.getString(), Settings.GUI_BACK_BTN_NAME.getString(), Settings.GUI_BACK_BTN_LORE.getStringList(), null));
        inventory.setItem(49, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_REFRESH_BTN_ITEM.getString(), Settings.GUI_REFRESH_BTN_NAME.getString(), Settings.GUI_REFRESH_BTN_LORE.getStringList(), null));
        inventory.setItem(50, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_NEXT_BTN_ITEM.getString(), Settings.GUI_NEXT_BTN_NAME.getString(), Settings.GUI_NEXT_BTN_LORE.getStringList(), null));

        // populate the gui with the active items
        if (items.size() != 0) {
            items.get(getPage() - 1).forEach(item -> inventory.setItem(inventory.firstEmpty(), item.getDisplayStack(AuctionStackType.ACTIVE_AUCTIONS_LIST)));
        }

        return inventory;
    }
}
