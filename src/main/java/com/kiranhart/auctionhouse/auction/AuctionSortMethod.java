package com.kiranhart.auctionhouse.auction;
/*
    The current file was created by Kiran Hart
    Date: September 14 2019
    Time: 3:35 PM
    
    Code within this class is not to be redistributed without proper permission.
*/

public enum AuctionSortMethod {

    ARMOR("Armor"),
    TOOLS("Tools"),
    BLOCKS("Blocks"),
    FOOD("Food"),
    DEFAULT("Default");

    private String method;

    AuctionSortMethod(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }
}
