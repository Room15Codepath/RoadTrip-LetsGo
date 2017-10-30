package com.codepath.roadtrip_letsgo.models;

/**
 * Created by tessavoon on 10/30/17.
 */


public class InfoWindow {

    public String name;
    public String address;
    public double rating;
    public int reviewCount;
    public String price;

    public InfoWindow() {}

    public static InfoWindow fromTripStop(TripStop stop) {
        InfoWindow iw = new InfoWindow();
        iw.name = stop.getTripLocation_Name();
        iw.address = stop.trip_location.getAddress();
        iw.rating = stop.rating;
        iw.reviewCount = stop.review_count;
        iw.price = stop.price;
        return iw;
    }
}
