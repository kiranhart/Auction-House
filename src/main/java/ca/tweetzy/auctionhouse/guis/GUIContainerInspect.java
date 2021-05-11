package ca.tweetzy.auctionhouse.guis;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.helpers.ConfigurationItemHelper;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.gui.Gui;
import ca.tweetzy.core.utils.TextUtils;
import org.bukkit.block.ShulkerBox;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.util.Arrays;

/**
 * The current file has been created by Kiran Hart
 * Date Created: April 14 2021
 * Time Created: 12:28 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUIContainerInspect extends Gui {

    final int[] fillSlots = {0, 1, 2, 3, 4, 5, 6, 7, 8, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 46, 47, 48, 50, 51, 52, 53};
    final ItemStack container;

    /**
     * Used to inspect a shulker box from it's item stack.
     *
     * @param container is the shulker box
     */
    public GUIContainerInspect(ItemStack container) {
        this.container = container;
        setTitle(TextUtils.formatText(Settings.GUI_SHULKER_INSPECT_TITLE.getString()));
        setDefaultItem(Settings.GUI_SHULKER_INSPECT_BG_ITEM.getMaterial().parseItem());
        setUseLockedCells(false);
        setAcceptsItems(false);
        setAllowDrops(false);
        setRows(6);
        draw();

        setOnClose(close -> close.manager.showGUI(close.player, new GUIAuctionHouse(AuctionHouse.getInstance().getAuctionPlayerManager().getPlayer(close.player.getUniqueId()))));
    }

    private void draw() {
        reset();

        for (int i : fillSlots) setItem(i, getDefaultItem());
        setButton(5, 4, ConfigurationItemHelper.createConfigurationItem(Settings.GUI_CLOSE_BTN_ITEM.getString(), Settings.GUI_CLOSE_BTN_NAME.getString(), Settings.GUI_CLOSE_BTN_LORE.getStringList(), null), e -> e.manager.showGUI(e.player, new GUIAuctionHouse(AuctionHouse.getInstance().getAuctionPlayerManager().getPlayer(e.player.getUniqueId()))));
        BlockStateMeta meta = (BlockStateMeta) this.container.getItemMeta();
        ShulkerBox skulkerBox = (ShulkerBox) meta.getBlockState();

        int slot = 9;
        for (ItemStack item : skulkerBox.getInventory().getContents()) {
            setItem(slot++, item);
        }
    }
}
