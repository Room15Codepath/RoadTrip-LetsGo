package com.codepath.roadtrip_letsgo.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.format.DateUtils;
import android.util.Log;

import com.codepath.roadtrip_letsgo.R;
import com.codepath.roadtrip_letsgo.models.TripLocation;
import com.codepath.roadtrip_letsgo.network.GMapV2Direction;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.ui.IconGenerator;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import cz.msebera.android.httpclient.Header;

/**
 * Created by tessavoon on 10/12/17.
 */

public class Util {

    public static final String SHARED_PREF = "MyRoadTrip";
    public static final String ORIGIN_PARAM = "origin";
    public static final String DESTINATION_PARAM = "destination";
    public static final String STOP_LIST_PARAM = "stopList";
    public static final String TRAVEL_MODE_PARAM = "mode";

    public static Document byteToDocument(byte[] documentoXml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new ByteArrayInputStream(documentoXml));
    }

    public static float getDistance(double lat_a, double lng_a, double lat_b, double lng_b ) {
        double earthRadius = 3958.75;
        double latDiff = Math.toRadians(lat_b-lat_a);
        double lngDiff = Math.toRadians(lng_b-lng_a);
        double a = Math.sin(latDiff /2) * Math.sin(latDiff /2) +
                Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) *
                        Math.sin(lngDiff /2) * Math.sin(lngDiff /2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distance = earthRadius * c;

        int meterConversion = 1609;
        Log.d("conversion", String.valueOf(distance * meterConversion));

        return new Float(distance * meterConversion).floatValue();
    }

    public static float convertKmToMiles(float km) {
        return new Float(0.621 * km * 0.001).floatValue();
    }

    public static String getOpeningHrsStr(JSONObject jsonObject) {
        StringBuilder sb = new StringBuilder();
        try {
            sb.append(getReadableDay(jsonObject.getInt("day")));
            sb.append(" ");
            sb.append(getReadableHour(jsonObject.getString("start")));
            sb.append(" - ");
            sb.append(getReadableHour(jsonObject.getString("end")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static String getReadableDay(int day) {
        switch(day) {
            case 0: return "Mon ";
            case 1: return "Tue ";
            case 2: return "Wed ";
            case 3: return "Thu ";
            case 4: return "Fri ";
            case 5: return "Sat ";
            default: return "Sun ";
        }
    }

    public static String getReadableHour(String hr) {
        DateFormat f1 = new SimpleDateFormat("HHmm");
        try {
            Date d = f1.parse(hr);
            DateFormat f2 = new SimpleDateFormat("hh:mm a");
            return f2.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
            return hr;
        }
    }

    public static String getRelativeTimeAgo(String rawJsonDate) {
        String yelpFormat = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sf = new SimpleDateFormat(yelpFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.DAY_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    public static void addRoute(TripLocation origin, TripLocation dest, Context context, GoogleMap map) {
        final GMapV2Direction md = new GMapV2Direction();
        String mode = getTravelMode(context.getApplicationContext());
        LatLng pnt1 = new LatLng(origin.lat, origin.lng);
        LatLng pnt2 = new LatLng(dest.lat, dest.lng);
        md.getDocument(pnt1, pnt2, mode,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        try {
                            Document doc = Util.byteToDocument(responseBody);
                            ArrayList<LatLng> directionPoint = md.getDirection(doc);
                            drawPolyline(directionPoint, context, map);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Log.d("DBEUG", "failed with code " + statusCode);
                    }
                });
    }

    public static void drawPolyline(ArrayList<LatLng> directionPoint , Context context, GoogleMap map) {
        PolylineOptions rectLine = new PolylineOptions().width(7).color(
                ContextCompat.getColor(context, R.color.colorPrimary));

        for (int i = 0; i < directionPoint.size(); i++) {
            rectLine.add(directionPoint.get(i));
        }
        Polyline polyline = map.addPolyline(rectLine);
    }

    public static void addLocationMarkers(TripLocation origin, TripLocation dest, Context context, GoogleMap map) {
//        BitmapDescriptor originIcon = Util.createNewBubble(context, 0, R.style.iconGenText, context.getResources().getDrawable(R.drawable.ic_home), "");
//        Marker marker_origin = Util.addMarker(map, new LatLng(origin.lat, origin.lng), origin.loc_name, origin.address, originIcon);
//        BitmapDescriptor destIcon = Util.createNewBubble(context, 0, R.style.iconGenText, context.getResources().getDrawable(R.drawable.ic_flag), "");
//        Marker marker_dest = Util.addMarker(map, new LatLng(dest.lat,dest.lng), dest.loc_name, dest.address, destIcon);

        BitmapDescriptor icon_origin = Util.createBubble(context, IconGenerator.STYLE_WHITE, "origin");
        Marker marker_origin = addMarker(map, new LatLng(origin.lat,origin.lng), origin.loc_name, origin.address, icon_origin);
        BitmapDescriptor icon_dest = Util.createBubble(context, IconGenerator.STYLE_WHITE, "destination");
        Marker marker_dest = addMarker(map, new LatLng(dest.lat,dest.lng), dest.loc_name, dest.address, icon_dest);
    }

    public static BitmapDescriptor createBubble(Context context, int style, String title) {
        IconGenerator iconGenerator = new IconGenerator(context);
        iconGenerator.setStyle(style);
        Bitmap bitmap = iconGenerator.makeIcon(title);
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
        return bitmapDescriptor;
    }

    public static BitmapDescriptor createNewBubble(Context context, int style, int text_style, Drawable background, String title) {
        IconGenerator iconGenerator = new IconGenerator(context);
        iconGenerator.setStyle(style);
        iconGenerator.setBackground(background);
        iconGenerator.setTextAppearance(text_style);
        Bitmap bitmap = iconGenerator.makeIcon(title);
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
        return bitmapDescriptor;
    }

    public static Marker addMarker(GoogleMap map, LatLng point, String title, String snippet, BitmapDescriptor icon) {
        MarkerOptions options = new MarkerOptions()
                .position(point)
                .title(title)
                .snippet(snippet)
                .icon(icon);
        Marker marker = map.addMarker(options);
        return marker;
    }

    public static Marker addDefaultMarker(GoogleMap map, TripLocation loc) {
        BitmapDescriptor defaultMarker =
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE);
        Marker marker = map.addMarker(new MarkerOptions()
                .position(new LatLng(loc.lat, loc.lng))
                .title(loc.loc_name)
                .snippet(loc.address)
                .icon(defaultMarker));
        return marker;
    }

    public static void saveOrigin(Context context, TripLocation tripLocation) {
        String tripJSON = new Gson().toJson(tripLocation);
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(ORIGIN_PARAM, tripJSON);
        editor.apply();
    }

    public static void deleteOrigin(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(ORIGIN_PARAM);
        editor.commit();
    }


    public static TripLocation getOrigin(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        if(prefs.contains(ORIGIN_PARAM)) {
            String originJSON = prefs.getString(ORIGIN_PARAM, "");

            TripLocation origin =
                    new Gson().fromJson(originJSON, new TypeToken<TripLocation>() {
                    }.getType());
            return origin;
        }else{
            return null;
        }
    }

    public static void saveDestination(Context context, TripLocation tripLocation) {
        String tripJSON = new Gson().toJson(tripLocation);
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(DESTINATION_PARAM, tripJSON);
        editor.apply();
    }

    public static void deleteDestination(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(DESTINATION_PARAM);
        editor.commit();
    }

    public static TripLocation getDestination(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        if(prefs.contains(DESTINATION_PARAM)) {
            String originJSON = prefs.getString(DESTINATION_PARAM, "");

            TripLocation destination =
                    new Gson().fromJson(originJSON, new TypeToken<TripLocation>() {
                    }.getType());
            return destination;
        }else{
            return null;
        }
    }

    public static ArrayList<TripLocation> getStops(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        if(prefs.contains(STOP_LIST_PARAM)) {
            String JSONList = prefs.getString(STOP_LIST_PARAM, "");

            List<TripLocation> list =
                    new Gson().fromJson(JSONList, new TypeToken<List<TripLocation>>() {
                    }.getType());
            return new ArrayList<TripLocation>(list);
        } else {
            return new ArrayList<TripLocation>();
        }
    }

    public static void saveStops(Context context,List<TripLocation> list){
        String JSONList = new Gson().toJson(list);
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(STOP_LIST_PARAM, JSONList);
        editor.apply();
    }

    public static void saveStop(Context context, TripLocation stop) {
        ArrayList<TripLocation> trips = getStops(context);
        trips.add(stop);
        String stopJSON = new Gson().toJson(trips);
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(STOP_LIST_PARAM, stopJSON);
        editor.apply();
    }

    public static void saveStop(Context context, TripLocation stop, int position) {
        ArrayList<TripLocation> trips = getStops(context);
        trips.add(position,stop);
        String stopJSON = new Gson().toJson(trips);
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(STOP_LIST_PARAM, stopJSON);
        editor.apply();
    }


    public static void deleteStop(Context context, TripLocation stop) {
        Log.d ("DEBUG", "stop="+stop.getLoc_name());
        ArrayList<TripLocation> trips = getStops(context);
        trips.remove(stop);
        String stopJSON = new Gson().toJson(trips);
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(STOP_LIST_PARAM, stopJSON);
        editor.apply();
    }

    public static void deleteStop(Context context, int position) {
        ArrayList<TripLocation> trips = getStops(context);
        trips.remove(position);
        String stopJSON = new Gson().toJson(trips);
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(STOP_LIST_PARAM, stopJSON);
        editor.apply();
    }

    public static void deleteStops(Context context,List<TripLocation> list){
        list.clear();
        String JSONList = new Gson().toJson(list);
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(STOP_LIST_PARAM, JSONList);
        editor.apply();
    }

    public static void saveTravelMode(Context context, String mode) {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(TRAVEL_MODE_PARAM, mode);
        editor.apply();
    }

    public static String getTravelMode(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        if(prefs.contains(TRAVEL_MODE_PARAM)) {
            String mode = prefs.getString(TRAVEL_MODE_PARAM, "");
            return mode;
        }else{
            return null;
        }
    }

    public static void deleteTravelMode(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(TRAVEL_MODE_PARAM);
        editor.commit();
    }
}
