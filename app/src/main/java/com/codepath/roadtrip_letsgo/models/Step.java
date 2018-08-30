package com.codepath.roadtrip_letsgo.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by yingbwan on 11/10/2017.
 */

public class Step {
    @SerializedName("travel_mode")
    @Expose
    private String travelMode;
    @SerializedName("start_location")
    @Expose
    private LatLng startLocation;
    @SerializedName("end_location")
    @Expose
    private LatLng endLocation;
    @SerializedName("polyline")
    @Expose
    private Polyline polyline;
    @SerializedName("duration")
    @Expose
    private Duration duration;
    @SerializedName("html_instructions")
    @Expose
    private String htmlInstructions;
    @SerializedName("distance")
    @Expose
    private Distance distance;

    public String getTravelMode() {
        return travelMode;
    }

    public void setTravelMode(String travelMode) {
        this.travelMode = travelMode;
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

    public Polyline getPolyline() {
        return polyline;
    }

    public void setPolyline(Polyline polyline) {
        this.polyline = polyline;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public String getHtmlInstructions() {
        return htmlInstructions;
    }

    public void setHtmlInstructions(String htmlInstructions) {
        this.htmlInstructions = htmlInstructions;
    }

    public Distance getDistance() {
        return distance;
    }

    public void setDistance(Distance distance) {
        this.distance = distance;
    }

}
