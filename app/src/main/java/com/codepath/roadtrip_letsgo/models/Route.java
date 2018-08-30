package com.codepath.roadtrip_letsgo.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by yingbwan on 11/10/2017.
 */

public class Route {
    @SerializedName("summary")
    @Expose
    private String summary;
    @SerializedName("legs")
    @Expose
    private List<Leg> legs = null;
    @SerializedName("copyrights")
    @Expose
    private String copyrights;
    @SerializedName("overview_polyline")
    @Expose
    private Polyline overviewPolyline;
    @SerializedName("warnings")
    @Expose
    private List<Object> warnings = null;
    @SerializedName("waypoint_order")
    @Expose
    private List<Integer> waypointOrder = null;
    @SerializedName("bounds")
    @Expose
    private Bounds bounds;

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<Leg> getLegs() {
        return legs;
    }

    public void setLegs(List<Leg> legs) {
        this.legs = legs;
    }

    public String getCopyrights() {
        return copyrights;
    }

    public void setCopyrights(String copyrights) {
        this.copyrights = copyrights;
    }

    public Polyline getOverviewPolyline() {
        return overviewPolyline;
    }

    public void setOverviewPolyline(Polyline overviewPolyline) {
        this.overviewPolyline = overviewPolyline;
    }

    public List<Object> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<Object> warnings) {
        this.warnings = warnings;
    }

    public List<Integer> getWaypointOrder() {
        return waypointOrder;
    }

    public void setWaypointOrder(List<Integer> waypointOrder) {
        this.waypointOrder = waypointOrder;
    }

    public Bounds getBounds() {
        return bounds;
    }

    public void setBounds(Bounds bounds) {
        this.bounds = bounds;
    }

}
