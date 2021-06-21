package ca.tweetzy.auctionhouse.guis;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.api.events.AuctionEndEvent;
import ca.tweetzy.auctionhouse.auction.AuctionItem;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.auction.AuctionSaleType;
import ca.tweetzy.auctionhouse.exception.ItemNotFoundException;
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

        setOnClose(close -> {
            AuctionHouse.getInstance().getTransactionManager().getPrePurchaseHolding().remove(close.player);
            close.manager.showGUI(close.player, new GUIAuctionHouse(this.auctionPlayer));
            AuctionHouse.getInstance().getLogger().info("Removed " + close.player.getName() + " from confirmation pre purchase");
        });

        draw();
    }

    private void draw() {
        setItems(this.buyingSpecificQuantity ? 9 : 0, this.buyingSpecificQuantity ? 12 : 3, new TItemBuilder(Objects.requireNonNull(Settings.GUI_CONFIRM_BUY_YES_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_CONFIRM_BUY_YES_NAME.getString()).setLore(Settings.GUI_CONFIRM_BUY_YES_LORE.getStringList()).toItemStack());
        setItem(this.buyingSpecificQuantity ? 1 : 0, 4, AuctionAPI.getInstance().deserializeItem(this.auctionItem.getRawItem()));
        setItems(this.buyingSpecificQuantity ? 14 : 5, this.buyingSpecificQuantity ? 17 : 8, new TItemBuilder(Objects.requireNonNull(Settings.GUI_CONFIRM_BUY_NO_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_CONFIRM_BUY_NO_NAME.getString()).setLore(Settings.GUI_CONFIRM_BUY_NO_LORE.getStringList()).toItemStack());

        setActionForRange(this.buyingSpecificQuantity ? 14 : 5, this.buyingSpecificQuantity ? 17 : 8, ClickType.LEFT, e -> {
            e.gui.close();
        });
        setActionForRange(this.buyingSpecificQuantity ? 9 : 0, this.buyingSpecificQuantity ? 12 : 3, ClickType.LEFT, e -> {
            // Re-select the item to ensure that it's available
            try {
                AuctionItem located = AuctionHouse.getInstance().getAuctionItemManager().getItem(this.auctionItem.getKey());
                preItemChecks(e, located);

                if (located == null) {
                    e.gui.close();
                    return;
                }

                // Check economy
                if (!AuctionHouse.getInstance().getEconomy().has(e.player, this.buyingSpecificQuantity ? this.purchaseQuantity * this.pricePerItem : located.getBasePrice())) {
                    AuctionHouse.getInstance().getLocale().getMessage("general.notenoughmoney").sendPrefixedMessage(e.player);
                    SoundManager.getInstance().playSound(e.player, Settings.SOUNDS_NOT_ENOUGH_MONEY.getString(), 1.0F, 1.0F);
                    e.gui.close();
                    return;
                }

                AuctionEndEvent auctionEndEvent = new AuctionEndEvent(Bukkit.getOfflinePlayer(this.auctionItem.getOwner()), e.player, this.auctionItem, AuctionSaleType.WITHOUT_BIDDING_SYSTEM, false);
                Bukkit.getServer().getPluginManager().callEvent(auctionEndEvent);
                if (auctionEndEvent.isCancelled()) return;

                if (!Settings.ALLOW_PURCHASE_IF_INVENTORY_FULL.getBoolean() && e.player.getInventory().firstEmpty() == -1) {
                    AuctionHouse.getInstance().getLocale().getMessage("general.noroom").sendPrefixedMessage(e.player);
                    return;
                }

                if (this.buyingSpecificQuantity) {
                    ItemStack item = AuctionAPI.getInstance().deserializeItem(located.getRawItem());

                    if (item.getAmount() - this.purchaseQuantity >= 1) {
                        item.setAmount(item.getAmount() - this.purchaseQuantity);
                        located.setRawItem(AuctionAPI.getInstance().serializeItem(item));
                        located.setBasePrice(located.getBasePrice() - this.purchaseQuantity * this.pricePerItem);
                        item.setAmount(this.purchaseQuantity);
                        transferFunds(e.player, this.purchaseQuantity * this.pricePerItem);
                    } else {
                        transferFunds(e.player, located.getBasePrice());
                        AuctionHouse.getInstance().getAuctionItemManager().sendToGarbage(located);
                    }

                    PlayerUtils.giveItem(e.player, item);
                    sendMessages(e, located, true, this.purchaseQuantity * this.pricePerItem);

                } else {
                    transferFunds(e.player, located.getBasePrice());
                    AuctionHouse.getInstance().getAuctionItemManager().sendToGarbage(located);
                    PlayerUtils.giveItem(e.player, AuctionAPI.getInstance().deserializeItem(located.getRawItem()));
                    sendMessages(e, located, false, 0);
                }

                AuctionHouse.getInstance().getTransactionManager().getPrePurchasePlayers(auctionItem.getKey()).forEach(player -> {
                    AuctionHouse.getInstance().getTransactionManager().removeAllRelatedPlayers(auctionItem.getKey());
                    player.closeInventory();
                });
            } catch (ItemNotFoundException exception) {
                AuctionHouse.getInstance().getLogger().info("Tried to purchase item that was bought, or does not exist");
            }
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
        AuctionHouse.getInstance().getLocale().getMessage("pricing.moneyremove").processPlaceholder("price", AuctionAPI.getInstance().formatNumber(overwritePrice ? price : located.getBasePrice())).sendPrefixedMessage(e.player);
        if (Bukkit.getOfflinePlayer(located.getOwner()).isOnline()) {
            AuctionHouse.getInstance().getLocale().getMessage("auction.itemsold")
                    .processPlaceholder("item", WordUtils.capitalizeFully(AuctionAPI.getInstance().deserializeItem(located.getRawItem()).getType().name().replace("_", " ")))
                    .processPlaceholder("price", AuctionAPI.getInstance().formatNumber(overwritePrice ? price : located.getBasePrice()))
                    .processPlaceholder("buyer_name", e.player.getName())
                    .sendPrefixedMessage(Bukkit.getOfflinePlayer(located.getOwner()).getPlayer());
            AuctionHouse.getInstance().getLocale().getMessage("pricing.moneyadd").processPlaceholder("price", AuctionAPI.getInstance().formatNumber(overwritePrice ? price : located.getBasePrice())).sendPrefixedMessage(Bukkit.getOfflinePlayer(located.getOwner()).getPlayer());
        }
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
            put("%original_stack_price%", AuctionAPI.getInstance().formatNumber(auctionItem.getBasePrice()));
            put("%price_per_item%", AuctionAPI.getInstance().formatNumber(pricePerItem));
            put("%purchase_quantity%", purchaseQuantity);
            put("%purchase_price%", AuctionAPI.getInstance().formatNumber(pricePerItem * purchaseQuantity));
        }});
        stack.setAmount(qty);
        return stack;
    }
}
