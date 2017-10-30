package com.codepath.roadtrip_letsgo.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.roadtrip_letsgo.R;
import com.codepath.roadtrip_letsgo.models.TripLocation;
import com.codepath.roadtrip_letsgo.models.TripStop;
import com.codepath.roadtrip_letsgo.utils.Util;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
    @BindView(R.id.toolbar_detail)
    Toolbar toolbarDetail;
    @BindView(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    @BindView(R.id.btnRoute)
    Button btnRoute;

    TripLocation origin;
    TripLocation dest;
    TripStop stop;
    GoogleMap map;
    String shareText;

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
        tvAddress.setText(stop.trip_location.address);

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
                    .position(new LatLng(stop.trip_location.lat, stop.trip_location.lng))
                    .title(stop.trip_location.loc_name)
                    .snippet(stop.trip_location.address)
                    .icon(defaultMarker));
            Util.addRoute(origin, dest, this, map);
            map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(stop.trip_location.lat, stop.trip_location.lng)));

            // Zoom in the Google Map
            map.animateCamera(CameraUpdateFactory.zoomTo(15));

        } else {
            Toast.makeText(this, "Error - Map was null!!", Toast.LENGTH_SHORT).show();
        }
    }

    public void onShare(MenuItem item) {

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareText = String.format("%s\n%s\n%s\nShared by Roadtrip LetsGo app", stop.trip_location.loc_name, stop.trip_location.address, stop.phone);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

        final List<ResolveInfo> activities = getPackageManager().queryIntentActivities (shareIntent, 0);

        List<String> appNames = new ArrayList<String>();
        for (ResolveInfo info : activities) {
            appNames.add(info.loadLabel(getPackageManager()).toString());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Share using...");
        builder.setItems(appNames.toArray(new CharSequence[appNames.size()]), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                ResolveInfo info = activities.get(item);
                if (info.activityInfo.packageName.equals("com.facebook.katana")) {
                    setupFacebookShareIntent();
                }
                // start the selected activity
                shareIntent.setPackage(info.activityInfo.packageName);
                startActivity(shareIntent);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void setupFacebookShareIntent() {
        ShareDialog shareDialog;
        shareDialog = new ShareDialog(this);

        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                .setQuote(shareText)
                .setContentUrl(Uri.parse(stop.url))
                .build();
        shareDialog.show(linkContent);
    }

    @OnClick(R.id.btnRoute)
    public void onButtonClick() {

        Log.d("DEBUG:", "add route clicked");
        //StringBuilder sb = new StringBuilder();
      //  sb.append("https://www.google.com/maps/dir");
    //    sb.append("/" + origin.point.latitude +","+ origin.point.longitude );
  //      sb.append("/" + stop.trip_location.point.latitude +","+ stop.trip_location.point.longitude);
//        sb.append("/" + dest.point.latitude +","+ dest.point.longitude);

     //   Intent i = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(sb.toString()));
     //   i.setPackage("com.google.android.apps.maps");
        Intent i = new Intent(LocationDetailActivity.this, AddStopActivity.class);
        ArrayList<Parcelable> tList = new ArrayList<>();
        tList.add(Parcels.wrap(origin));
        tList.add(Parcels.wrap(stop.trip_location));
        tList.add(Parcels.wrap(dest));
        i.putParcelableArrayListExtra("stops", tList);
        startActivity(i);

    }

}
