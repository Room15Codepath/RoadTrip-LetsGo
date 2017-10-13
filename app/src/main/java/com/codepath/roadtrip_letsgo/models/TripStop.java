package com.codepath.roadtrip_letsgo.models;

import com.codepath.roadtrip_letsgo.utils.StopType;
import com.google.android.gms.maps.model.LatLng;
import com.yelp.fusion.client.models.Business;

import org.parceler.Parcel;

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

    public TripStop() {}

    public static TripStop fromBusiness(Business biz, StopType stopType) {
        TripStop tripStop = new TripStop();
        TripLocation tripLocation = new TripLocation();
        tripStop.distance_away = biz.getDistance();
        tripStop.image_url = biz.getImageUrl();
        tripStop.stop_type = stopType;
        tripStop.is_closed = biz.getIsClosed();
        tripStop.rating = biz.getRating();
        tripStop.yelp_id = biz.getId();
        tripLocation.loc_name = biz.getName();
        tripLocation.address = biz.getLocation().getDisplayAddress().toString();
        tripLocation.point = new LatLng(biz.getCoordinates().getLatitude(),
            biz.getCoordinates().getLongitude());
        tripStop.trip_location = tripLocation;
        return tripStop;
    }

//    public static ArrayList<TripStop> fromJSONArray(JSONArray jsonArray, StopType stopType) {
//        ArrayList<TripStop> tripStops = new ArrayList<TripStop>();
//        for (int i=0; i<jsonArray.length(); i++) {
//            TripStop tripStop = new TripStop();
//            TripLocation tripLocation = new TripLocation();
//            try {
//                JSONObject placeObj = jsonArray.getJSONObject(i);
//                tripLocation.loc_name = placeObj.getString("name");
//                tripLocation.address = placeObj.getJSONObject("location").toString();
//                tripLocation.point = new LatLng(
//                        placeObj.getJSONObject("coordinates").getDouble("latitude"),
//                        placeObj.getJSONObject("coordinates").getDouble("longitude"));
//                tripStop.trip_location = tripLocation;
//                tripStop.yelp_id = placeObj.getString("id");
//                tripStop.rating = placeObj.getDouble("rating");
//                tripStop.is_closed = placeObj.getBoolean("isClosed");
//                tripStop.distance_away = placeObj.getDouble("distance");
//                tripStop.stop_type = stopType;
//                tripStop.image_url = placeObj.getString("imageUrl");
//                tripStops.add(tripStop);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        return tripStops;
//    }

}
