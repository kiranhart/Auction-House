package ca.tweetzy.auctionhouse.guis;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.helpers.ConfigurationItemHelper;
import ca.tweetzy.auctionhouse.managers.SoundManager;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.compatibility.XMaterial;
import ca.tweetzy.core.inventory.TInventory;
import ca.tweetzy.core.utils.PlayerUtils;
import ca.tweetzy.core.utils.nms.NBTEditor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * The current file has been created by Kiran Hart
 * Date Created: February 18 2021
 * Time Created: 10:07 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class ExpiredItemsGUI extends TInventory {

    private final Player player;
    private List<List<ItemStack>> items;

    public ExpiredItemsGUI(Player player) {
        this.player = player;
     //   this.items = Lists.partition(AuctionHouse.getInstance().getAuctionPlayerManager().locateAndSelectPlayer(player).getExpiredItems(player, true), 45);
        setTitle(Settings.GUI_EXPIRED_AUCTIONS_TITLE.getString());
        setPage(1);
        setRows(6);
        setDynamic(false);
    }

    @Override
    public void onClick(InventoryClickEvent e, int slot) {
        e.setCancelled(true);
        Player player = (Player) e.getWhoClicked();

        switch (slot) {
            case 45:
                // back button (send them to the main auction house page)
               // player.openInventory(new AuctionHouseGUI(AuctionHouse.getInstance().getAuctionPlayerManager().locateAndSelectPlayer(player)).getInventory());
                break;
            case 46:
                // claim all of the expired auctions
             //   PlayerUtils.giveItem(player, AuctionHouse.getInstance().getAuctionPlayerManager().locateAndSelectPlayer(player).getExpiredItems(player, false));
                AuctionHouse.getInstance().getData().set("expired." + this.player.getUniqueId().toString(), null);
                AuctionHouse.getInstance().getData().save();

                // reopen the gui
                player.openInventory(new ExpiredItemsGUI(this.player).getInventory());
                break;
            case 48:
                if (getPage() > 1) {
                    player.openInventory(setPage(getPage() - 1).getInventory());
                    SoundManager.getInstance().playSound(player, Settings.SOUNDS_NAVIGATE_GUI_PAGES.getString(), 1f, 1f);
                }
                break;
            case 49:
                player.openInventory(new ExpiredItemsGUI(this.player).getInventory());
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
                    if (!AuctionHouse.getInstance().getData().contains("expired." + player.getUniqueId().toString() + "." + key)) {
                        return;
                    }

                    PlayerUtils.giveItem(player, AuctionHouse.getInstance().getData().getItemStack("expired." + player.getUniqueId().toString() + "." + key + ".item"));
                    AuctionHouse.getInstance().getData().set("expired." + player.getUniqueId().toString() + "." + key, null);
                    AuctionHouse.getInstance().getData().save();

                }
                break;
        }
    }

    @Override
    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this, getSize(), getTitle());

        // set the buttons
        inventory.setItem(45, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_BACK_BTN_ITEM.getString(), Settings.GUI_BACK_BTN_NAME.getString(), Settings.GUI_BACK_BTN_LORE.getStringList(), null));
        inventory.setItem(46, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_EXPIRED_AUCTIONS_ITEM.getString(), Settings.GUI_EXPIRED_AUCTIONS_NAME.getString(), Settings.GUI_EXPIRED_AUCTIONS_LORE.getStringList(), null));
        inventory.setItem(48, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_BACK_BTN_ITEM.getString(), Settings.GUI_BACK_BTN_NAME.getString(), Settings.GUI_BACK_BTN_LORE.getStringList(), null));
        inventory.setItem(49, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_REFRESH_BTN_ITEM.getString(), Settings.GUI_REFRESH_BTN_NAME.getString(), Settings.GUI_REFRESH_BTN_LORE.getStringList(), null));
        inventory.setItem(50, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_NEXT_BTN_ITEM.getString(), Settings.GUI_NEXT_BTN_NAME.getString(), Settings.GUI_NEXT_BTN_LORE.getStringList(), null));

        // populate the gui with the active items
        if (items.size() != 0) {
            items.get(getPage() - 1).forEach(item -> inventory.setItem(inventory.firstEmpty(), item));
        }

        return inventory;
    }
}
