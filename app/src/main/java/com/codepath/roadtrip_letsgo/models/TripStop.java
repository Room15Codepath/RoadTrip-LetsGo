package com.codepath.roadtrip_letsgo.models;

import com.codepath.roadtrip_letsgo.utils.StopType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
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
    public ArrayList<String> categories;

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
            String phone = json.getString("phone");
            tripStop.phone = (phone.isEmpty()) ? "N/A" : phone;
            try {
                tripStop.price = json.getString("price");
            }catch (JSONException x){
                tripStop.price = "N/A";
            }
            tripStop.categories = new ArrayList<String>();
            JSONArray categoryList = json.getJSONArray("categories");
            for (int i=0; i<categoryList.length(); i++) {
                tripStop.categories.add(categoryList.getJSONObject(i).getString("title"));
            }
            tripStop.review_count = json.getInt("review_count");
            tripStop.url = json.getString("url");
            tripLocation.loc_name = json.getString("name");
            tripLocation.address = getAddressStr(
                    json.getJSONObject("location").getJSONArray("display_address"));
            tripLocation.lat  = json.getJSONObject("coordinates").getDouble("latitude");
            tripLocation.lng = json.getJSONObject("coordinates").getDouble("longitude");
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

    public String getCategoriesStr() {
        StringBuilder str = new StringBuilder();
        for (int i=0; i<categories.size(); i++) {
            str.append(categories.get(i));
            str.append((i == categories.size()-1) ? " " : ", ");
        }
        return str.toString();
    }


    public double getRating() {
        return rating;
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

    public static final Comparator<TripStop> COMPARE_BY_RATING = new Comparator<TripStop>() {
        @Override
        public int compare(TripStop lhs, TripStop rhs) {
            return Double.compare(rhs.getRating(), lhs.getRating());
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
