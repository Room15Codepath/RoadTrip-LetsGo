package com.codepath.roadtrip_letsgo.models;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;

import org.parceler.Parcel;

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
}
