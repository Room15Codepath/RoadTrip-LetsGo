package com.codepath.roadtrip_letsgo.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;

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
import com.google.maps.android.ui.IconGenerator;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import cz.msebera.android.httpclient.Header;

/**
 * Created by tessavoon on 10/12/17.
 */

public class Util {

    public static Document byteToDocument(byte[] documentoXml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new ByteArrayInputStream(documentoXml));
    }

    public static void addRoute(TripLocation origin, TripLocation dest, Context context, GoogleMap map) {
        final GMapV2Direction md = new GMapV2Direction();

        md.getDocument(origin.point, dest.point, GMapV2Direction.MODE_DRIVING,
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
        BitmapDescriptor icon_origin = Util.createBubble(context, IconGenerator.STYLE_WHITE, "origin");
        Marker marker_origin = addMarker(map, origin.point, origin.loc_name, origin.address, icon_origin);
        BitmapDescriptor icon_dest = Util.createBubble(context, IconGenerator.STYLE_WHITE, "destination");
        Marker marker_dest = addMarker(map, dest.point, dest.loc_name, dest.address, icon_dest);
    }

    public static BitmapDescriptor createBubble(Context context, int style, String title) {
        IconGenerator iconGenerator = new IconGenerator(context);
        iconGenerator.setStyle(style);
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


}
