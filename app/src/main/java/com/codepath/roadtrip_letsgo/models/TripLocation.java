package com.codepath.roadtrip_letsgo.models;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.yelp.fusion.client.models.Business;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tessavoon on 10/12/17.
 */


@Parcel
public class TripLocation {

    public LatLng point;
    public String address;
    public String loc_name;

    public TripLocation() {}

    public static TripLocation fromPlace(Place place) {
        TripLocation tripLocation = new TripLocation();
        tripLocation.point = place.getLatLng();
        tripLocation.address = (String) place.getAddress();
        tripLocation.loc_name = (String) place.getName();
        return tripLocation;
    }

    public static List<TripLocation> fromBusinesses(List<Business> list) {
        ArrayList<TripLocation> tList = new ArrayList<>();
        for (Business b: list) {
            TripLocation t = new TripLocation();
            t.loc_name = b.getName();
            t.address = b.getLocation().getDisplayAddress().toString();
            t.point = new LatLng(b.getCoordinates().getLatitude(), b.getCoordinates().getLongitude());
            tList.add(t);
        }
        return tList;
    }
}
