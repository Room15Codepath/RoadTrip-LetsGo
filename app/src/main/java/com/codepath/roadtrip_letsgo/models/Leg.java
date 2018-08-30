package com.codepath.roadtrip_letsgo.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by yingbwan on 11/10/2017.
 */

public class Leg {
    @SerializedName("steps")
    @Expose
    private List<Step> steps = null;
    @SerializedName("duration")
    @Expose
    private Duration duration;
    @SerializedName("distance")
    @Expose
    private Distance distance;
    @SerializedName("start_location")
    @Expose
    private LatLng startLocation;
    @SerializedName("end_location")
    @Expose
    private LatLng endLocation;
    @SerializedName("start_address")
    @Expose
    private String startAddress;
    @SerializedName("end_address")
    @Expose
    private String endAddress;

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Distance getDistance() {
        return distance;
    }

    public void setDistance(Distance distance) {
        this.distance = distance;
    }

    public LatLng getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(LatLng startLocation) {
        this.startLocation = startLocation;
    }

    public LatLng getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(LatLng endLocation) {
        this.endLocation = endLocation;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }

}
