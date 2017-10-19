package com.codepath.roadtrip_letsgo.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.roadtrip_letsgo.R;
import com.codepath.roadtrip_letsgo.helper.GlideApp;
import com.codepath.roadtrip_letsgo.models.TripLocation;
import com.codepath.roadtrip_letsgo.models.TripStop;
import com.codepath.roadtrip_letsgo.utils.Util;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LocationDetailActivity extends AppCompatActivity {
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvAddress)
    TextView tvAddress;
    @BindView(R.id.ivImage)
    ImageView ivImage;
    @BindView(R.id.btnStart)
    Button btnStart;

    TripLocation origin;
    TripLocation dest;
    TripStop stop;
    GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_detail);
        ButterKnife.bind(this);

        origin = Parcels.unwrap(getIntent().getParcelableExtra("start"));
        dest = Parcels.unwrap(getIntent().getParcelableExtra("end"));

        stop = Parcels.unwrap(getIntent().getParcelableExtra("location"));
        tvName.setText(stop.trip_location.loc_name);
        tvAddress.setText(stop.trip_location.address );
        //Glide.with(this).from(stop.image_url).into(ivImage);
        GlideApp.with(this).load(stop.image_url).override(300,300).fitCenter().into(ivImage);

        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        fm.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                loadMap(googleMap);
            }
        });

        Log.d("DEBUG", "input location:" + stop.trip_location.loc_name);
    }

    public void onDirection(View view) {

        String gmmIntentUri = String.format("google.navigation:q=%s,+%s", stop.trip_location.loc_name, stop.trip_location.address);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(gmmIntentUri));
        // Make the Intent explicit by setting the Google Maps package
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }


    }

    private void loadMap(GoogleMap googleMap) {
        map = googleMap;
        if (map != null) {
            // Map is ready
            Toast.makeText(this, "Map Fragment was loaded properly!", Toast.LENGTH_SHORT).show();
      //      ResultsActivityPermissionsDispatcher.getMyLocationWithCheck(this);
       //     ResultsActivityPermissionsDispatcher.startLocationUpdatesWithCheck(this);
            TripLocation origin = Parcels.unwrap(getIntent().getParcelableExtra("start"));
            TripLocation dest = Parcels.unwrap(getIntent().getParcelableExtra("end"));
            map.getUiSettings().setZoomControlsEnabled(true);
            Util.addLocationMarkers(origin, dest, this, map);

            Util.addRoute(origin, dest, this, map);
            map.moveCamera(CameraUpdateFactory.newLatLng(origin.point));

            // Zoom in the Google Map
            map.animateCamera(CameraUpdateFactory.zoomTo(15));

        } else {
            Toast.makeText(this, "Error - Map was null!!", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.btnStart)
    public void onButtonClick() {

        StringBuilder sb = new StringBuilder();
        sb.append("https://www.google.com/maps/dir");
        sb.append("/" + origin.point.latitude +","+ origin.point.longitude );
        sb.append("/" + stop.trip_location.point.latitude +","+ stop.trip_location.point.longitude);
        sb.append("/" + dest.point.latitude +","+ dest.point.longitude);

        Intent i = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(sb.toString()));
        i.setPackage("com.google.android.apps.maps");
        startActivity(i);

    }

}
