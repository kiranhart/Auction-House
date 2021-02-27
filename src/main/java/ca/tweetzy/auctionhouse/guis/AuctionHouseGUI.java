package ca.tweetzy.auctionhouse.guis;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.auction.AuctionItem;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.auction.AuctionStackType;
import ca.tweetzy.auctionhouse.helpers.ConfigurationItemHelper;
import ca.tweetzy.auctionhouse.managers.SoundManager;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.inventory.TInventory;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.List;

/**
 * The current file has been created by Kiran Hart
 * Date Created: February 10 2021
 * Time Created: 12:56 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class AuctionHouseGUI extends TInventory {

    private final AuctionPlayer auctionPlayer;
    private List<List<AuctionItem>> items;

    public AuctionHouseGUI(AuctionPlayer auctionPlayer) {
        this.auctionPlayer = auctionPlayer;
        this.auctionPlayer.setViewingAuctionHouse(true);
        this.items = Lists.partition(AuctionHouse.getInstance().getAuctionItemManager().getFilteredItems(auctionPlayer.getPreferredCategory()), 45);
        setTitle(Settings.GUI_AUCTION_HOUSE_TITLE.getString());
        setPage(auctionPlayer.getCurrentAuctionPage());
        setRows(6);
        setDynamic(false);
    }

    @Override
    public void onClick(InventoryClickEvent e, int slot) {
        e.setCancelled(true);

        switch (slot) {
            case 45:
                auctionPlayer.getPlayer().openInventory(new ActiveAuctionsGUI(auctionPlayer.getPlayer()).getInventory());
                break;
            case 46:
                auctionPlayer.getPlayer().openInventory(new ExpiredItemsGUI(auctionPlayer.getPlayer()).getInventory());
                break;
            case 48:
                if (getPage() > 1) {
                    auctionPlayer.setCurrentAuctionPage(auctionPlayer.getCurrentAuctionPage() - 1);
                    auctionPlayer.getPlayer().openInventory(new AuctionHouseGUI(auctionPlayer).getInventory());
                    SoundManager.getInstance().playSound(auctionPlayer.getPlayer(), Settings.SOUNDS_NAVIGATE_GUI_PAGES.getString(), 1f, 1f);
                }
                break;
            case 49:
                auctionPlayer.setCurrentAuctionPage(1);
                auctionPlayer.getPlayer().openInventory(new AuctionHouseGUI(auctionPlayer).getInventory());
                break;
            case 50:
                if (getPage() < this.items.size()) {
                    auctionPlayer.setCurrentAuctionPage(auctionPlayer.getCurrentAuctionPage() + 1);
                    auctionPlayer.getPlayer().openInventory(new AuctionHouseGUI(auctionPlayer).getInventory());
                    SoundManager.getInstance().playSound(auctionPlayer.getPlayer(), Settings.SOUNDS_NAVIGATE_GUI_PAGES.getString(), 1f, 1f);
                }
                break;
            case 51:
                break;
            default:
                break;
        }
    }

    @Override
    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this, getSize(), getTitle());
        // fill the bottom bar first, then load items
        inventory.setItem(45, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_AUCTION_HOUSE_ITEMS_YOUR_AUCTIONS_ITEM.getString(), Settings.GUI_AUCTION_HOUSE_ITEMS_YOUR_AUCTIONS_NAME.getString(), Settings.GUI_AUCTION_HOUSE_ITEMS_YOUR_AUCTIONS_LORE.getStringList(), new HashMap<String, Object>(){{
            put("%active_player_auctions%", auctionPlayer.getActiveItems().size());
        }}));
        inventory.setItem(46, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_AUCTION_HOUSE_ITEMS_COLLECTION_BIN_ITEM.getString(), Settings.GUI_AUCTION_HOUSE_ITEMS_COLLECTION_BIN_NAME.getString(), Settings.GUI_AUCTION_HOUSE_ITEMS_COLLECTION_BIN_LORE.getStringList(), new HashMap<String, Object>(){{
            put("%expired_player_auctions%", auctionPlayer.getExpiredItems(auctionPlayer.getPlayer(), false).size());
        }}));
        inventory.setItem(48, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_BACK_BTN_ITEM.getString(), Settings.GUI_BACK_BTN_NAME.getString(), Settings.GUI_BACK_BTN_LORE.getStringList(), null));
        inventory.setItem(49, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_REFRESH_BTN_ITEM.getString(), Settings.GUI_REFRESH_BTN_NAME.getString(), Settings.GUI_REFRESH_BTN_LORE.getStringList(), null));
        inventory.setItem(50, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_NEXT_BTN_ITEM.getString(), Settings.GUI_NEXT_BTN_NAME.getString(), Settings.GUI_NEXT_BTN_LORE.getStringList(), null));
        inventory.setItem(51, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_AUCTION_HOUSE_ITEMS_TRANSACTIONS_ITEM.getString(), Settings.GUI_AUCTION_HOUSE_ITEMS_TRANSACTIONS_NAME.getString(), Settings.GUI_AUCTION_HOUSE_ITEMS_TRANSACTIONS_LORE.getStringList(), null));
        inventory.setItem(52, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_AUCTION_HOUSE_ITEMS_HOW_TO_SELL_ITEM.getString(), Settings.GUI_AUCTION_HOUSE_ITEMS_HOW_TO_SELL_NAME.getString(), Settings.GUI_AUCTION_HOUSE_ITEMS_HOW_TO_SELL_LORE.getStringList(), null));
        inventory.setItem(53, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_AUCTION_HOUSE_ITEMS_GUIDE_ITEM.getString(), Settings.GUI_AUCTION_HOUSE_ITEMS_GUIDE_NAME.getString(), Settings.GUI_AUCTION_HOUSE_ITEMS_GUIDE_LORE.getStringList(), null));

        // load in the auction items
        if (items.size() != 0) {
            int currPage = auctionPlayer.getCurrentAuctionPage() > items.size() ? 0 : auctionPlayer.getCurrentAuctionPage() - 1;
            items.get(currPage).forEach(item -> inventory.setItem(inventory.firstEmpty(), item.getDisplayStack(AuctionStackType.MAIN_AUCTION_HOUSE)));
        }

        return inventory;
    }
}
