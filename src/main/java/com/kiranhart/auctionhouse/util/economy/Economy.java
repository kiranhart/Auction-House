package com.kiranhart.auctionhouse.util.economy;
/*
    The current file was created by Kiran Hart
    Date: August 05 2019
    Time: 9:06 PM
    
    Code within this class is not to be redistributed without proper permission.
*/

import org.bukkit.OfflinePlayer;

public interface Economy {

    boolean hasBalance(OfflinePlayer p, double cost);

    boolean withdrawBalance(OfflinePlayer p, double cost);

    boolean deposit(OfflinePlayer p, double amount);
}
