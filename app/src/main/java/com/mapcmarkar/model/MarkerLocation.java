package com.mapcmarkar.model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by kamal on 11/14/2017.
 */

public class MarkerLocation {
    private LatLng latLng;
    private String address;
    private String sellername;
    private String clientName;

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSellername() {
        return sellername;
    }

    public void setSellername(String sellername) {
        this.sellername = sellername;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
}
