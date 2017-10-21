package com.codepath.roadtrip_letsgo.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LocationDetailActivity extends AppCompatActivity {
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvAddress)
    TextView tvAddress;
    @BindView(R.id.tvPhone)
    TextView tvPhone;
    @BindView(R.id.tvReviewCount)
    TextView tvReviewCount;
    @BindView(R.id.tvPrice)
    TextView tvPrice;
    @BindView(R.id.tvDistance)
    TextView tvDistance;
    @BindView(R.id.ratingBar)
    RatingBar ratingBar;
    @BindView(R.id.ivImage)
    ImageView ivImage;

    @BindView(R.id.toolbar_detail)
    Toolbar toolbarDetail;
    @BindView(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
   // @BindView(R.id.btnStart)
   // Button btnStart;

    TripLocation origin;
    TripLocation dest;
    TripStop stop;
    GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_detail);
        ButterKnife.bind(this);

        if (toolbarDetail != null) {
            setSupportActionBar(toolbarDetail);
        }

        origin = Parcels.unwrap(getIntent().getParcelableExtra("start"));
        dest = Parcels.unwrap(getIntent().getParcelableExtra("end"));

        stop = Parcels.unwrap(getIntent().getParcelableExtra("location"));
        ratingBar.setRating((float)(stop.rating));
        tvPhone.setText(stop.phone);
        tvPrice.setText("Price: " + stop.price);
        tvDistance.setText(String.format( "%.1f", stop.distance_away/1600) +" mile");
        tvReviewCount.setText(stop.review_count + " Reviews");
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_detail, menu);

        return super.onCreateOptionsMenu(menu);
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
            BitmapDescriptor defaultMarker =
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE);
            Marker marker = map.addMarker(new MarkerOptions()
                    .position(stop.trip_location.point)
                    .title(stop.trip_location.loc_name)
                    .snippet(stop.trip_location.address)
                    .icon(defaultMarker));
            Util.addRoute(origin, dest, this, map);
            map.moveCamera(CameraUpdateFactory.newLatLng(stop.trip_location.point));

            // Zoom in the Google Map
            map.animateCamera(CameraUpdateFactory.zoomTo(15));

        } else {
            Toast.makeText(this, "Error - Map was null!!", Toast.LENGTH_SHORT).show();
        }
    }

    public void onShare(MenuItem item) {

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String shareText = String.format("%s\n%s\n%s\nShared by Roadtrip LetsGo app", stop.trip_location.loc_name, stop.trip_location.address, stop.phone);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(shareIntent, "Share using"));
    }

   /* @OnClick(R.id.btnStart)
    public void onButtonClick() {

        StringBuilder sb = new StringBuilder();
        sb.append("https://www.google.com/maps/dir");
        sb.append("/" + origin.point.latitude +","+ origin.point.longitude );
        sb.append("/" + stop.trip_location.point.latitude +","+ stop.trip_location.point.longitude);
        sb.append("/" + dest.point.latitude +","+ dest.point.longitude);

        Intent i = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(sb.toString()));
        i.setPackage("com.google.android.apps.maps");
        startActivity(i);

    }*/

}
