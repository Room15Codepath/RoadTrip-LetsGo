package com.codepath.roadtrip_letsgo.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.codepath.roadtrip_letsgo.R;
import com.codepath.roadtrip_letsgo.fragments.TravelModeFragment;
import com.codepath.roadtrip_letsgo.models.TripLocation;
import com.codepath.roadtrip_letsgo.utils.Util;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {

    protected GeoDataClient mGeoDataClient;
    protected PlaceDetectionClient mPlaceDetectionClient;
    Place origin;
    Place destination;
    @BindView(R.id.btnFind) Button btnFind;
    @BindView(R.id.toolbar_home)
    Toolbar toolbarHome;
    @BindView(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    @BindView(R.id.container_home)
    FrameLayout containerFragments;
    TravelModeFragment travelModeFragment;
    @BindView(R.id.search_container)
    RelativeLayout searchContainer;
    @BindView(R.id.btnStart)
    Button btnStart;

    //String mode;
    //Float rating, range;
    String userId;
    boolean permission;

    public static final String USER = "USER";
    public static final String PERMISSION = "PERMISSION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        if (toolbarHome != null) {
            setSupportActionBar(toolbarHome);
            setTitle("Road Trip");
        }
        travelModeFragment = new TravelModeFragment();

        mGeoDataClient = Places.getGeoDataClient(this, null);
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);
        setupOriginListener();
        setupDestListener();
        setupFindListener();
        parseIntent();
        setupStartListener();
    }

    public void parseIntent() {
        Bundle bundle = getIntent().getExtras();
        userId = bundle.getString(USER);
        permission = bundle.getBoolean(PERMISSION);
      //  rating = bundle.getFloat("rating");
      //  range = bundle.getFloat("range");

        //Log.d("DEBUG:", "bundle="+mode+rating+range);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);

        return super.onCreateOptionsMenu(menu);
    }

    private void setupOriginListener() {
        PlaceAutocompleteFragment originFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.origin_autocomplete_fragment);
        originFragment.setHint("Enter Origin");

        originFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.

                Log.i("place", "Place: " + place.getName());
                origin = place;
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("error", "An error occurred: " + status);
            }
        });
    }

    private void setupDestListener() {
        PlaceAutocompleteFragment destFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.destination_autocomplete_fragment);
        destFragment.setHint("Enter Destination");
        destFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                destination = place;
                Log.i("place", "Place: " + place.getName());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("error", "An error occurred: " + status);
            }
        });
    }

    private void setupFindListener() {
        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(origin ==null || destination == null) return;
                ArrayList<TripLocation> list = new ArrayList<>();
                list.add(TripLocation.fromPlace(origin));
                list.add(TripLocation.fromPlace(destination));
                Util.saveTrip(getApplicationContext(),list);
                Intent i = new Intent(HomeActivity.this, SearchActivity.class);
                i.putExtra("origin", Parcels.wrap(TripLocation.fromPlace(origin)));
                i.putExtra("destination", Parcels.wrap(TripLocation.fromPlace(destination)));
                //i.putExtra("stopType", sStopType.getSelectedItem().toString());
                startActivity(i);
            }
        });
    }

    private void setupStartListener() {
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(origin ==null || destination == null) return;
                StringBuilder sb = new StringBuilder();
                sb.append("https://www.google.com/maps/dir");
                sb.append("/" + origin.getLatLng().latitude +","+ origin.getLatLng().longitude );
                sb.append("/" + destination.getLatLng().latitude +","+ destination.getLatLng().longitude);

                Intent i = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(sb.toString()));
                i.setPackage("com.google.android.apps.maps");
                startActivity(i);
            }
        });
    }

    public void onTravelMode(MenuItem item) {

       // getSupportFragmentManager().beginTransaction().replace(R.id.container_home, travelModeFragment).addToBackStack(null).commit();
       // getSupportFragmentManager().executePendingTransactions();
       // searchContainer.setVisibility(View.GONE);
        TravelModeFragment travelModeFragment;
        FragmentManager fm = getSupportFragmentManager();
        travelModeFragment = TravelModeFragment.newInstance();
        travelModeFragment.show(fm, "fragment_travelmode");

    }
}
