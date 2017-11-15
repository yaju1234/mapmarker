package com.mapcmarkar.model;

/**
 * Created by kamal on 11/15/2017.
 */

public class InvalidAddress {
    public  String address;
    public  String sellername;

    public InvalidAddress(String address, String sellername) {
        this.address = address;
        this.sellername = sellername;
    }
    public String getAddress() {
        return address;
    }

    public String getSellername() {
        return sellername;
    }


}
