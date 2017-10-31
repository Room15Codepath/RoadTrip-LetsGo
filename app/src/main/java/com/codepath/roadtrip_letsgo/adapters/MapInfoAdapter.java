package com.codepath.roadtrip_letsgo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.codepath.roadtrip_letsgo.R;
import com.codepath.roadtrip_letsgo.models.InfoWindow;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Created by tessavoon on 10/28/17.
 */

public class MapInfoAdapter  implements GoogleMap.InfoWindowAdapter {
    LayoutInflater mInflater;

    public MapInfoAdapter(LayoutInflater i) {
        mInflater = i;
    }

    // This defines the contents within the info window based on the marker
    @Override
    public View getInfoContents(Marker marker) {
        // Getting view from the layout file
        View v = mInflater.inflate(R.layout.custom_info_window, null);
        TextView title = (TextView) v.findViewById(R.id.tv_info_window_title);
        TextView address = (TextView) v.findViewById(R.id.tvAddress);
        TextView price = (TextView) v.findViewById(R.id.tvPrice);
        RatingBar rating = (RatingBar) v.findViewById(R.id.rbRating);
        TextView reviewCount = (TextView) v.findViewById(R.id.tvReviewCount);
        InfoWindow iw = new Gson().fromJson(marker.getTitle(), new TypeToken<InfoWindow>() {
        }.getType());
        title.setText(iw.name);
        address.setText(iw.address);
        price.setText(iw.price);
        rating.setRating((int) iw.rating);
        reviewCount.setText(iw.reviewCount + " Reviews");

        // Populate fields
//        TextView title = (TextView) v.findViewById(R.id.tv_info_window_title);
//        title.setText(marker.getTitle());
//
//        TextView description = (TextView) v.findViewById(R.id.tvAddress);
//        description.setText(marker.getSnippet());

        return v;
    }

    // This changes the frame of the info window; returning null uses the default frame.
    // This is just the border and arrow surrounding the contents specified above
    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }
}