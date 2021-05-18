package ca.tweetzy.auctionhouse.managers;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.auctionhouse.transaction.Transaction;
import ca.tweetzy.core.utils.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: March 22 2021
 * Time Created: 3:34 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class TransactionManager {

    private final ArrayList<Transaction> transactions = new ArrayList<>();
    private final HashMap<Player, UUID> prePurchaseHolding = new HashMap<>();

    public void addTransaction(Transaction transaction) {
        if (transaction == null) return;
        this.transactions.add(transaction);
    }

    public void removeTransaction(UUID uuid) {
        this.transactions.removeIf(item -> item.getId().equals(uuid));
    }

    public Transaction getTransaction(UUID uuid) {
        return this.transactions.stream().filter(item -> item.getId().equals(uuid)).findFirst().orElse(null);
    }

    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(this.transactions);
    }

    public void addPrePurchase(Player player, UUID uuid) {
        this.prePurchaseHolding.put(player, uuid);
    }

    public void removeAllRelatedPlayers(UUID uuid) {
        this.prePurchaseHolding.keySet().removeIf(p -> this.prePurchaseHolding.get(p).equals(uuid));
    }

    public HashMap<Player, UUID> getPrePurchaseHolding() {
        return prePurchaseHolding;
    }

    public List<Player> getPrePurchasePlayers(UUID uuid) {
        return this.prePurchaseHolding.keySet().stream().filter(p -> this.prePurchaseHolding.get(p).equals(uuid)).collect(Collectors.toList());
    }

    public void loadTransactions(boolean useDatabase) {
        if (useDatabase) {
            AuctionHouse.getInstance().getDataManager().getTransactions(all -> all.forEach(this::addTransaction));
        } else {
            if (AuctionHouse.getInstance().getData().contains("transactions") && AuctionHouse.getInstance().getData().isList("transactions")) {
                List<Transaction> transactions = AuctionHouse.getInstance().getData().getStringList("transactions").stream().map(AuctionAPI.getInstance()::convertBase64ToObject).map(object -> (Transaction) object).collect(Collectors.toList());
                long start = System.currentTimeMillis();
                transactions.forEach(this::addTransaction);
                AuctionHouse.getInstance().getLocale().newMessage(TextUtils.formatText(String.format("&aLoaded &2%d &atransaction(s) in &e%d&fms", transactions.size(), System.currentTimeMillis() - start))).sendPrefixedMessage(Bukkit.getConsoleSender());
            }
        }
    }

    public void saveTransactions(boolean useDatabase, boolean async) {
        if (useDatabase) {
            AuctionHouse.getInstance().getDataManager().saveTransactions(getTransactions(), async);
        } else {
            AuctionHouse.getInstance().getData().set("transactions", this.transactions.stream().map(AuctionAPI.getInstance()::convertToBase64).collect(Collectors.toList()));
            AuctionHouse.getInstance().getData().save();
        }
    }
}
