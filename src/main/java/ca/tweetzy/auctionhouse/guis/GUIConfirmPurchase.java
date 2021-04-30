package ca.tweetzy.auctionhouse.guis;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.api.events.AuctionEndEvent;
import ca.tweetzy.auctionhouse.auction.AuctionItem;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.auction.AuctionSaleType;
import ca.tweetzy.auctionhouse.helpers.ConfigurationItemHelper;
import ca.tweetzy.auctionhouse.managers.SoundManager;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.gui.Gui;
import ca.tweetzy.core.gui.events.GuiClickEvent;
import ca.tweetzy.core.utils.PlayerUtils;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.core.utils.items.TItemBuilder;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Objects;

/**
 * The current file has been created by Kiran Hart
 * Date Created: March 17 2021
 * Time Created: 11:18 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUIConfirmPurchase extends Gui {

    final AuctionPlayer auctionPlayer;
    final AuctionItem auctionItem;

    boolean buyingSpecificQuantity;
    int purchaseQuantity = 0;
    int maxStackSize = 0;
    double pricePerItem = 0D;

    public GUIConfirmPurchase(AuctionPlayer auctionPlayer, AuctionItem auctionItem, boolean buyingSpecificQuantity) {
        this.auctionPlayer = auctionPlayer;
        this.auctionItem = auctionItem;
        this.buyingSpecificQuantity = buyingSpecificQuantity;
        setTitle(TextUtils.formatText(Settings.GUI_CONFIRM_BUY_TITLE.getString()));
        setAcceptsItems(false);

        int preAmount = AuctionAPI.getInstance().deserializeItem(auctionItem.getRawItem()).getAmount();
        if (preAmount == 1) {
            this.buyingSpecificQuantity = false;
        }

        setRows(!this.buyingSpecificQuantity ? 1 : 5);

        if (this.buyingSpecificQuantity) {
            setUseLockedCells(Settings.GUI_CONFIRM_FILL_BG_ON_QUANTITY.getBoolean());
            setDefaultItem(Settings.GUI_CONFIRM_BG_ITEM.getMaterial().parseItem());
            this.purchaseQuantity = preAmount;
            this.maxStackSize = preAmount;
            this.pricePerItem = this.auctionItem.getBasePrice() / this.maxStackSize;
        }

        draw();
    }

    private void draw() {
        setItems(this.buyingSpecificQuantity ? 9 : 0, this.buyingSpecificQuantity ? 12 : 3, new TItemBuilder(Objects.requireNonNull(Settings.GUI_CONFIRM_BUY_YES_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_CONFIRM_BUY_YES_NAME.getString()).setLore(Settings.GUI_CONFIRM_BUY_YES_LORE.getStringList()).toItemStack());
        setItem(this.buyingSpecificQuantity ? 1 : 0, 4, AuctionAPI.getInstance().deserializeItem(this.auctionItem.getRawItem()));
        setItems(this.buyingSpecificQuantity ? 14 : 5, this.buyingSpecificQuantity ? 17 : 8, new TItemBuilder(Objects.requireNonNull(Settings.GUI_CONFIRM_BUY_NO_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_CONFIRM_BUY_NO_NAME.getString()).setLore(Settings.GUI_CONFIRM_BUY_NO_LORE.getStringList()).toItemStack());

        setActionForRange(this.buyingSpecificQuantity ? 14 : 5, this.buyingSpecificQuantity ? 17 : 8, ClickType.LEFT, e -> e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer)));
        setActionForRange(this.buyingSpecificQuantity ? 9 : 0, this.buyingSpecificQuantity ? 12 : 3, ClickType.LEFT, e -> {
            // Re-select the item to ensure that it's available
            AuctionItem located = AuctionHouse.getInstance().getAuctionItemManager().getItem(this.auctionItem.getKey());
            preItemChecks(e, located);

            // Check economy
            if (!AuctionHouse.getInstance().getEconomy().has(e.player, this.buyingSpecificQuantity ? this.purchaseQuantity * this.pricePerItem : located.getBasePrice())) {
                AuctionHouse.getInstance().getLocale().getMessage("general.notenoughmoney").sendPrefixedMessage(e.player);
                SoundManager.getInstance().playSound(e.player, Settings.SOUNDS_NOT_ENOUGH_MONEY.getString(), 1.0F, 1.0F);
                e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
                return;
            }

            Bukkit.getServer().getScheduler().runTaskAsynchronously(AuctionHouse.getInstance(), () -> {
                AuctionEndEvent auctionEndEvent = new AuctionEndEvent(Bukkit.getOfflinePlayer(this.auctionItem.getOwner()), e.player, this.auctionItem, AuctionSaleType.WITHOUT_BIDDING_SYSTEM);
                Bukkit.getServer().getPluginManager().callEvent(auctionEndEvent);

                if (auctionEndEvent.isCancelled()) return;

                if (this.buyingSpecificQuantity) {
                    ItemStack item = AuctionAPI.getInstance().deserializeItem(located.getRawItem());
//                    Bukkit.broadcastMessage(String.format("Total Item Qty: %d\nTotal Purchase Qty: %d\nAmount of purchase: %d", item.getAmount(), this.purchaseQuantity, item.getAmount() - this.purchaseQuantity));

                    if (item.getAmount() - this.purchaseQuantity >= 1) {
                        item.setAmount(item.getAmount() - this.purchaseQuantity);
                        located.setRawItem(AuctionAPI.getInstance().serializeItem(item));
                        located.setBasePrice(located.getBasePrice() - this.purchaseQuantity * this.pricePerItem);
                        item.setAmount(this.purchaseQuantity);
                        transferFunds(e.player, this.purchaseQuantity * this.pricePerItem);
                    } else {
                        transferFunds(e.player, located.getBasePrice());
                        AuctionHouse.getInstance().getAuctionItemManager().removeItem(located.getKey());
                    }

                    givePlayerItem(e.player, item);
                    sendMessages(e, located, true, this.purchaseQuantity * this.pricePerItem);
                    e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
                    return;
                }

                transferFunds(e.player, located.getBasePrice());
                AuctionHouse.getInstance().getAuctionItemManager().removeItem(located.getKey());
                givePlayerItem(e.player, AuctionAPI.getInstance().deserializeItem(located.getRawItem()));
                sendMessages(e, located, false, 0);
                e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
            });
        });

        if (this.buyingSpecificQuantity) {
            drawPurchaseInfo(this.maxStackSize);

            // Decrease Button
            setButton(3, 3, new TItemBuilder(Settings.GUI_CONFIRM_DECREASE_QTY_ITEM.getMaterial().parseMaterial()).setName(Settings.GUI_CONFIRM_DECREASE_QTY_NAME.getString()).setLore(Settings.GUI_CONFIRM_DECREASE_QTY_LORE.getStringList()).toItemStack(), e -> {
                if ((this.purchaseQuantity - 1) <= 0) return;
                this.purchaseQuantity -= 1;
                drawPurchaseInfo(this.purchaseQuantity);
            });

            // Increase Button
            setButton(3, 5, new TItemBuilder(Settings.GUI_CONFIRM_INCREASE_QTY_ITEM.getMaterial().parseMaterial()).setName(Settings.GUI_CONFIRM_INCREASE_QTY_NAME.getString()).setLore(Settings.GUI_CONFIRM_INCREASE_QTY_LORE.getStringList()).toItemStack(), e -> {
                if ((this.purchaseQuantity + 1) > this.maxStackSize) return;
                this.purchaseQuantity += 1;
                drawPurchaseInfo(this.purchaseQuantity);
            });
        }
    }

    private void transferFunds(Player from, double amount) {
        AuctionHouse.getInstance().getEconomy().withdrawPlayer(from, amount);
        AuctionHouse.getInstance().getEconomy().depositPlayer(Bukkit.getOfflinePlayer(this.auctionItem.getOwner()), amount);
    }

    private void sendMessages(GuiClickEvent e, AuctionItem located, boolean overwritePrice, double price) {
        AuctionHouse.getInstance().getLocale().getMessage("pricing.moneyremove").processPlaceholder("price", String.format("%,.2f", overwritePrice ? price : located.getBasePrice())).sendPrefixedMessage(e.player);
        if (Bukkit.getOfflinePlayer(located.getOwner()).isOnline()) {
            AuctionHouse.getInstance().getLocale().getMessage("auction.itemsold")
                    .processPlaceholder("item", WordUtils.capitalizeFully(AuctionAPI.getInstance().deserializeItem(located.getRawItem()).getType().name().replace("_", " ")))
                    .processPlaceholder("price", String.format("%,.2f", overwritePrice ? price : located.getBasePrice()))
                    .sendPrefixedMessage(Bukkit.getOfflinePlayer(located.getOwner()).getPlayer());
            AuctionHouse.getInstance().getLocale().getMessage("pricing.moneyadd").processPlaceholder("price", String.format("%,.2f", overwritePrice ? price : located.getBasePrice())).sendPrefixedMessage(Bukkit.getOfflinePlayer(located.getOwner()).getPlayer());
        }
    }

    private void givePlayerItem(Player player, ItemStack stack) {
        Bukkit.getServer().getScheduler().runTask(AuctionHouse.getInstance(), () -> PlayerUtils.giveItem(player, stack));
    }

    private void drawPurchaseInfo(int amt) {
        setItem(3, 4, getPurchaseInfoItem(amt));
    }

    private void preItemChecks(GuiClickEvent e, AuctionItem located) {
        if (located == null || located.isExpired()) {
            AuctionHouse.getInstance().getLocale().getMessage("auction.itemnotavailable").sendPrefixedMessage(e.player);
            e.manager.showGUI(e.player, new GUIAuctionHouse(this.auctionPlayer));
            return;
        }
    }

    private ItemStack getPurchaseInfoItem(int qty) {
        ItemStack stack = ConfigurationItemHelper.createConfigurationItem(Settings.GUI_CONFIRM_QTY_INFO_ITEM.getString(), Settings.GUI_CONFIRM_QTY_INFO_NAME.getString(), Settings.GUI_CONFIRM_QTY_INFO_LORE.getStringList(), new HashMap<String, Object>() {{
            put("%original_stack_size%", maxStackSize);
            put("%original_stack_price%", String.format("%,.2f", auctionItem.getBasePrice()));
            put("%price_per_item%", String.format("%,.2f", pricePerItem));
            put("%purchase_quantity%", purchaseQuantity);
            put("%purchase_price%", String.format("%,.2f", pricePerItem * purchaseQuantity));
        }});
        stack.setAmount(qty);
        return stack;
    }
}
