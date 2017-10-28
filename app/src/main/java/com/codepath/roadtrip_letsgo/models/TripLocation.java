package com.codepath.roadtrip_letsgo.models;

import com.google.android.gms.location.places.Place;

import org.parceler.Parcel;

/**
 * Created by tessavoon on 10/12/17.
 */


@Parcel
public class TripLocation {

    public String address;
    public String loc_name;
    public double lat;
    public double lng;

    public TripLocation() {}

    public static TripLocation fromPlace(Place place) {
        TripLocation tripLocation = new TripLocation();
        tripLocation.lat = place.getLatLng().latitude;
        tripLocation.lng = place.getLatLng().longitude;
        tripLocation.address = (String) place.getAddress();
        tripLocation.loc_name = (String) place.getName();
        return tripLocation;
    }


    public String getAddress() {
        return address;
    }

    public String getLoc_name() {
        return loc_name;
    }

    @Override
    public boolean equals(Object object) {
        if (object != null && object instanceof TripLocation) {
            TripLocation tripLocation = (TripLocation) object;
            return this.address.equals(tripLocation.getAddress());
        }
        return false;
    }


}
