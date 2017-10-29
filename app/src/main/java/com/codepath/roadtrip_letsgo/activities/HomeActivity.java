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
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.roadtrip_letsgo.Manifest;
import com.codepath.roadtrip_letsgo.R;
import com.codepath.roadtrip_letsgo.adapters.StopsRecyclerAdapter;
import com.codepath.roadtrip_letsgo.helper.OnStartDragListener;
import com.codepath.roadtrip_letsgo.helper.SimpleItemTouchHelperCallback;
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
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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
import static com.codepath.roadtrip_letsgo.utils.Util.getStops;

public class HomeActivity extends AppCompatActivity implements OnStartDragListener {

    protected GeoDataClient mGeoDataClient;
    protected PlaceDetectionClient mPlaceDetectionClient;
    protected FusedLocationProviderClient mFusedLocationProviderClient;
    Context mContext;
    Place origin;
    Place destination;
    PlaceAutocompleteFragment originFragment;
    @BindView(R.id.toolbar_home)
    Toolbar toolbarHome;
    @BindView(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
//    @BindView(R.id.container_home)
//    FrameLayout containerFragments;
    @BindView(R.id.search_container)
    RelativeLayout searchContainer;
    @BindView(R.id.btnStart)
    Button btnStart;

  //  @BindView(R.id.tvFrom)
  //  PlacesAutocompleteTextView tvFrom;
    //@BindView(R.id.tvTo)
   // PlacesAutocompleteTextView tvTo;

    @BindView(R.id.rvStops)
    RecyclerView rvStops;

    private BottomSheetBehavior mBottomSheetBehavior;
    SupportMapFragment mapFragment;
    GoogleMap map;
    //String mode;
    //Float rating, range;
    String userId;
    boolean permission;

    public static final String USER = "USER";
    public static final String PERMISSION = "PERMISSION";
    private ItemTouchHelper mItemTouchHelper;
    StopsRecyclerAdapter adapter;
    ArrayList<TripLocation> listFromShared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        Log.d("home", "onCreate()");
        if (toolbarHome != null) {
            setSupportActionBar(toolbarHome);
            setTitle("Road Trip");
        }
        listFromShared = getStops(getApplicationContext());
        if (!listFromShared.isEmpty()) {
            Util.deleteStops(getApplicationContext(), getStops(getApplicationContext()));
        }

        adapter = new StopsRecyclerAdapter(getApplicationContext(), this);
        rvStops.setHasFixedSize(true);
        rvStops.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvStops.setLayoutManager(linearLayoutManager);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(rvStops);
        mGeoDataClient = Places.getGeoDataClient(this, null);
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mContext = getApplicationContext();
        setupOriginListener();
        setupDestListener();
        //setupFindListener();
        parseIntent();
        setupStartListener();
        View bottomSheet = findViewById( R.id.bottom_sheet );
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        mBottomSheetBehavior.setPeekHeight(60);  // show top title
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // React to state change
                Log.d("DEBUG", "state change: " + newState);
                TextView tvTitle = bottomSheet.findViewById(R.id.bsTitle);
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    tvTitle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_arrow_downward, 0, 0, 0);
                }
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    tvTitle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_arrow_upward, 0, 0, 0);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // React to dragging events
                Log.d("DEBUG", "slide : " + slideOffset);
            }
        });
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                loadMap(googleMap);
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.d("home", "onStart()");
        listFromShared = getStops(getApplicationContext());
        Log.d("home", "listFromShared="+listFromShared.toString());
        adapter.customNotifyDataSetChanged(listFromShared);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        return super.onOptionsItemSelected(item);
    }

    private void setupOriginListener() {
        originFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.origin_autocomplete_fragment);
        originFragment.setHint("Enter Origin");
        ((EditText)originFragment.getView().findViewById(R.id.place_autocomplete_search_input)).setTextSize(14.0f);

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
                                            Util.saveOrigin(mContext, TripLocation.fromPlace(origin));
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
       ((EditText)destFragment.getView().findViewById(R.id.place_autocomplete_search_input)).setTextSize(14.0f);
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

    /*private void setupFindListener() {
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
    }*/

    private void setupStartListener() {
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(origin ==null || destination == null) return;
                StringBuilder sb = new StringBuilder();
                sb.append("https://www.google.com/maps/dir");
                sb.append("/" + origin.getAddress());
                ArrayList<TripLocation> trips = Util.getStops(mContext);
                for (int i=0; i<trips.size(); i++) {
                    sb.append("/" + trips.get(i).getAddress());
                }
                sb.append("/" + destination.getAddress());
                Intent i = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(sb.toString()));
                i.setPackage("com.google.android.apps.maps");
                startActivity(i);
            }
        });
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    public void onAddStop(MenuItem item) {
        if (origin == null || destination == null) {
            Snackbar.make(toolbarHome, R.string.snackbar_home, Snackbar.LENGTH_LONG)
                    .show();
            return;
        }
        ArrayList<TripLocation> list = new ArrayList<>();
        list.add(TripLocation.fromPlace(origin));
        list.add(TripLocation.fromPlace(destination));
        //Util.saveStops(getApplicationContext(),list);
        Intent i = new Intent(HomeActivity.this, SearchActivity.class);
        //i.putExtra("origin", Parcels.wrap(TripLocation.fromPlace(origin)));
        //i.putExtra("destination", Parcels.wrap(TripLocation.fromPlace(destination)));
        //i.putExtra("stopType", sStopType.getSelectedItem().toString());
        startActivity(i);
    }

    private void loadMap(GoogleMap googleMap) {
        map = googleMap;
        if (map != null) {
            // Map is ready
            Toast.makeText(this, "Map Fragment was loaded properly!", Toast.LENGTH_SHORT).show();
            //      ResultsActivityPermissionsDispatcher.getMyLocationWithCheck(this);
            //     ResultsActivityPermissionsDispatcher.startLocationUpdatesWithCheck(this);
            map.getUiSettings().setZoomControlsEnabled(true);
            ArrayList<TripLocation> list = Util.getStops(getApplicationContext());
            if ( list != null && list.size()>0) {
                TripLocation origin = list.get(0);
                TripLocation dest = list.get(list.size() - 1);
                Util.addLocationMarkers(origin, dest, this, map);
                Util.addRoute(origin, dest, this, map);

/*            BitmapDescriptor defaultMarker =
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE);
            Marker marker = map.addMarker(new MarkerOptions()
                    .position(new LatLng(stop.trip_location.lat, stop.trip_location.lng))
                    .title(stop.trip_location.loc_name)
                    .snippet(stop.trip_location.address)
                    .icon(defaultMarker));
*/
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                //   builder.include(marker.getPosition());
                builder.include(new LatLng(origin.lat, origin.lng));
                builder.include(new LatLng(dest.lat, dest.lng));
                LatLngBounds bounds = builder.build();

                int width = getResources().getDisplayMetrics().widthPixels;
                int height = getResources().getDisplayMetrics().heightPixels;
                int padding = (int) (width * 0.20); // offset from edges of the map 10% of screen
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
                // Zoom in the Google Map
                map.animateCamera(cu);
            }
        } else {
            Toast.makeText(this, "Error - Map was null!!", Toast.LENGTH_SHORT).show();
        }
    }

    public void drawRoute(GoogleMap map, List<TripLocation> list){


    }

}
