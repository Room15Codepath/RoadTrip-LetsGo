package com.codepath.roadtrip_letsgo.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.codepath.roadtrip_letsgo.Manifest;
import com.codepath.roadtrip_letsgo.R;
import com.codepath.roadtrip_letsgo.models.TripLocation;
import com.codepath.roadtrip_letsgo.utils.Util;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePredictionBufferResponse;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.codepath.roadtrip_letsgo.activities.LoginActivity.MY_PERMISSIONS_REQUEST_LOCATION;

public class HomeActivity extends AppCompatActivity {

    protected GeoDataClient mGeoDataClient;
    protected PlaceDetectionClient mPlaceDetectionClient;
    protected FusedLocationProviderClient mFusedLocationProviderClient;
    Context mContext;
    Place origin;
    Place destination;
    PlaceAutocompleteFragment originFragment;
    @BindView(R.id.btnFind)
    Button btnFind;
    @BindView(R.id.toolbar_home)
    Toolbar toolbarHome;
    @BindView(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    @BindView(R.id.container_home)
    FrameLayout containerFragments;
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
        mGeoDataClient = Places.getGeoDataClient(this, null);
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mContext = getApplicationContext();
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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the options menu from XML
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_home, menu);
//
//        return super.onCreateOptionsMenu(menu);
//    }

    private void setupOriginListener() {
        originFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.origin_autocomplete_fragment);
        originFragment.setHint("Enter Origin");
        if (ContextCompat.checkSelfPermission(HomeActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(HomeActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {

                ActivityCompat.requestPermissions(HomeActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        } else {
            Task locationResult = mFusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        setCurrentLocationAddress(task);
                    } else {
                        Log.d("gps", "location not returned");
                    }
                }
            });
        }

        originFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i("place", "Place: " + place.getName());
                origin = place;
                Util.saveOrigin(mContext, TripLocation.fromPlace(origin));
            }

            @Override
            public void onError(Status status) {
                Log.i("error", "An error occurred: " + status);
            }
        });
        originFragment.getView().findViewById(R.id.place_autocomplete_clear_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        originFragment.setText("");
                        view.setVisibility(View.GONE);
                        Util.saveOrigin(mContext, null);
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        Task locationResult = mFusedLocationProviderClient.getLastLocation();
                        setCurrentLocationAddress(locationResult);
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

        }
    }

    private void setCurrentLocationAddress(Task locationResult) {
        locationResult.addOnCompleteListener(this, new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Location mLastKnownLocation = (Location) task.getResult();
                    if (mLastKnownLocation != null) {

                        LatLng latlng = new LatLng(mLastKnownLocation.getLatitude(),
                                mLastKnownLocation.getLongitude());
                        Geocoder geocoder;
                        List<Address> addresses;
                        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                        try {
                            addresses = geocoder.getFromLocation(mLastKnownLocation.getLatitude(),
                                    mLastKnownLocation.getLongitude(), 1);
                            // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                            String address = addresses.get(0).getAddressLine(0);
                            LatLngBounds mBounds = new LatLngBounds(latlng, latlng);
                            setPlaceFromGivenAddress(address, mBounds);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.d("get last known location", "failed");
                    }
                }
            }
        });
    }

    private void setPlaceFromGivenAddress(String address, LatLngBounds mBounds) {
        mGeoDataClient.getAutocompletePredictions(address, mBounds, null).addOnCompleteListener(
                new OnCompleteListener<AutocompletePredictionBufferResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<AutocompletePredictionBufferResponse> task) {
                        if (task.isSuccessful()) {
                            AutocompletePredictionBufferResponse predictedPlaces = task.getResult();
                            if (predictedPlaces.getCount() > 0) {
                                String currentPlaceId = predictedPlaces.get(0).getPlaceId();
                                mGeoDataClient.getPlaceById(currentPlaceId).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
                                    @Override
                                    public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                                        if (task.isSuccessful()) {
                                            PlaceBufferResponse retrievedPlaces = task.getResult();
                                            Place currentPlace = retrievedPlaces.get(0);
                                            origin = currentPlace.freeze();
                                            originFragment.setText(address);
                                            Log.i("currentplace", "Place found: " + currentPlace.getName());
                                            retrievedPlaces.release();
                                        } else {
                                            Log.e("currentplace", "Place not found.");
                                        }
                                    }
                                });
                            }
                            predictedPlaces.release();
                        }
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
                Util.saveDestination(mContext, TripLocation.fromPlace(destination));
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("error", "An error occurred: " + status);
            }
        });
        destFragment.getView().findViewById(R.id.place_autocomplete_clear_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        destFragment.setText("");
                        view.setVisibility(View.GONE);
                        Util.saveDestination(mContext, null);
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
                Util.saveStops(getApplicationContext(),list);
                Intent i = new Intent(HomeActivity.this, SearchActivity.class);
//                i.putExtra("origin", Parcels.wrap(TripLocation.fromPlace(origin)));
//                i.putExtra("destination", Parcels.wrap(TripLocation.fromPlace(destination)));
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
}
