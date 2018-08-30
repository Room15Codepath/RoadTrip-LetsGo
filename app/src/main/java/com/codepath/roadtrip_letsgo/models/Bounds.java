package com.codepath.roadtrip_letsgo.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by yingbwan on 11/10/2017.
 */

public class Bounds {
    @SerializedName("southwest")
    @Expose
    private LatLng southwest;
    @SerializedName("northeast")
    @Expose
    private LatLng northeast;

    public LatLng getSouthwest() {
        return southwest;
    }

    public void setSouthwest(LatLng southwest) {
        this.southwest = southwest;
    }

    public LatLng getNortheast() {
        return northeast;
    }

    public void setNortheast(LatLng northeast) {
        this.northeast = northeast;
    }


}
