package com.codepath.roadtrip_letsgo.models;

import com.codepath.roadtrip_letsgo.utils.StopType;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.Comparator;

/**
 * Created by tessavoon on 10/12/17.
 */

@Parcel
public class TripStop {

    public TripLocation trip_location;
    public String yelp_id;
    public StopType stop_type;
    public String image_url;
    public double rating;
    public boolean is_closed;
    public double distance_away;
    public String phone;
    public String url;
    public int review_count;
    public String price;

    public TripStop() {}

    public static TripStop fromJSON(JSONObject json, StopType stopType) {
        TripStop tripStop = new TripStop();
        try {
            TripLocation tripLocation = new TripLocation();
            tripStop.distance_away = json.getDouble("distance");
            tripStop.image_url = json.getString("image_url");
            tripStop.stop_type = stopType;
            tripStop.is_closed = json.getBoolean("is_closed");
            tripStop.rating = json.getDouble("rating");
            tripStop.yelp_id = json.getString("id");
            tripStop.phone= json.getString("phone");
            try {
                tripStop.price = json.getString("price");
            }catch (JSONException x){
                //skip.
            }
            tripStop.review_count = json.getInt("review_count");
            tripStop.url = json.getString("url");
            tripLocation.loc_name = json.getString("name");
            tripLocation.address = getAddressStr(
                    json.getJSONObject("location").getJSONArray("display_address"));
            tripLocation.point = new LatLng(
                    json.getJSONObject("coordinates").getDouble("latitude"),
                    json.getJSONObject("coordinates").getDouble("longitude"));
            tripStop.trip_location = tripLocation;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tripStop;
    }

    private static String getAddressStr(JSONArray address) {
        StringBuilder str = new StringBuilder();
        try {
            for (int i=0; i<address.length(); i++) {
                str.append(address.getString(i));
                str.append(" ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str.toString();
    }

    public double getDistance_away() {
        return distance_away;
    }

    public TripLocation getTrip_location() {
        return trip_location;
    }

    public String getTripLocation_Name() {
        return trip_location.getLoc_name();
    }

    public String getYelp_id() {
        return yelp_id;
    }

    public static final Comparator<TripStop> COMPARE_BY_DISTANCE = new Comparator<TripStop>() {
        @Override
        public int compare(TripStop lhs, TripStop rhs) {
            return Double.compare(lhs.getDistance_away(), rhs.getDistance_away());
        }
    };

    @Override
    public boolean equals(Object object) {
        if (object != null && object instanceof TripStop) {
            TripStop tripStop = (TripStop) object;
            return this.getYelp_id().equals(tripStop.getYelp_id());
        }
        return false;
    }

}
