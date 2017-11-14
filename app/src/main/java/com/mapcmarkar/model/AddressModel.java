package com.mapcmarkar.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by kamal on 11/14/2017.
 */

public class AddressModel {
    @SerializedName("NetAutonumber")
    @Expose
    private String netAutonumber;
    @SerializedName("item1")
    @Expose
    private String item1;
    @SerializedName("item3")
    @Expose
    private String item3;
    @SerializedName("NetData")
    @Expose
    private String netData;
    @SerializedName("item5")
    @Expose
    private Object item5;
    @SerializedName("item2")
    @Expose
    private String item2;
    @SerializedName("Grup")
    @Expose
    private Integer grup;
    @SerializedName("Note")
    @Expose
    private String note;
    @SerializedName("SubGrup")
    @Expose
    private Integer subGrup;
    @SerializedName("item4")
    @Expose
    private String item4;

    public String getNetAutonumber() {
        return netAutonumber;
    }

    public void setNetAutonumber(String netAutonumber) {
        this.netAutonumber = netAutonumber;
    }

    public String getItem1() {
        return item1;
    }

    public void setItem1(String item1) {
        this.item1 = item1;
    }

    public String getItem3() {
        return item3;
    }

    public void setItem3(String item3) {
        this.item3 = item3;
    }

    public String getNetData() {
        return netData;
    }

    public void setNetData(String netData) {
        this.netData = netData;
    }

    public Object getItem5() {
        return item5;
    }

    public void setItem5(Object item5) {
        this.item5 = item5;
    }

    public String getItem2() {
        return item2;
    }

    public void setItem2(String item2) {
        this.item2 = item2;
    }

    public Integer getGrup() {
        return grup;
    }

    public void setGrup(Integer grup) {
        this.grup = grup;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getSubGrup() {
        return subGrup;
    }

    public void setSubGrup(Integer subGrup) {
        this.subGrup = subGrup;
    }

    public String getItem4() {
        return item4;
    }

    public void setItem4(String item4) {
        this.item4 = item4;
    }
}
