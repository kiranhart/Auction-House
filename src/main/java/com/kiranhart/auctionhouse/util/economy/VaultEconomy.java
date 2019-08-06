package com.kiranhart.auctionhouse.util.economy;
/*
    The current file was created by Kiran Hart
    Date: August 05 2019
    Time: 9:13 PM
    
    Code within this class is not to be redistributed without proper permission.
*/

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class VaultEconomy implements Economy {

    private net.milkbowl.vault.economy.Economy vault;

    public VaultEconomy() {
        this.vault = Bukkit.getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class).getProvider();
    }

    @Override
    public boolean hasBalance(OfflinePlayer p, double cost) {
        return vault.has(p, cost);
    }

    @Override
    public boolean withdrawBalance(OfflinePlayer p, double cost) {
        return vault.withdrawPlayer(p, cost).transactionSuccess();
    }

    @Override
    public boolean deposit(OfflinePlayer p, double amount) {
        return vault.depositPlayer(p, amount).transactionSuccess();
    }
}
