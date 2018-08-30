package com.codepath.roadtrip_letsgo.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by yingbwan on 11/10/2017.
 */

public class Duration {
    @SerializedName("value")
    @Expose
    private int value;
    @SerializedName("text")
    @Expose
    private String text;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


}
