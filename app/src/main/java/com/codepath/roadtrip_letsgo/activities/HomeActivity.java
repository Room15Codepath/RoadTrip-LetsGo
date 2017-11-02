package com.codepath.roadtrip_letsgo.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.roadtrip_letsgo.Manifest;
import com.codepath.roadtrip_letsgo.R;
import com.codepath.roadtrip_letsgo.adapters.TripRecyclerAdapter;
import com.codepath.roadtrip_letsgo.fragments.TravelModeFragment;
import com.codepath.roadtrip_letsgo.helper.ItemClickSupport;
import com.codepath.roadtrip_letsgo.models.TripLocation;
import com.codepath.roadtrip_letsgo.network.GMapV2Direction;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.ui.IconGenerator;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.w3c.dom.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

import static com.codepath.roadtrip_letsgo.activities.LoginActivity.MY_PERMISSIONS_REQUEST_LOCATION;
import static com.codepath.roadtrip_letsgo.utils.Util.getStops;
import static com.codepath.roadtrip_letsgo.utils.Util.getTravelMode;

//import static com.codepath.roadtrip_letsgo.R.id.rvStops;

public class HomeActivity extends AppCompatActivity implements TripRecyclerAdapter.AdapterCallback {

    protected GeoDataClient mGeoDataClient;
    protected PlaceDetectionClient mPlaceDetectionClient;
    protected FusedLocationProviderClient mFusedLocationProviderClient;
    Context mContext;
    TripLocation origin;
    TripLocation destination;
    PlaceAutocompleteFragment originFragment;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    //    @BindView(R.id.container_home)
//    FrameLayout containerFragments;
    @BindView(R.id.search_container)
    RelativeLayout searchContainer;
    @BindView(R.id.btnStart)
    Button btnStart;

    @BindView(R.id.labelDestination)
    TextView lDestination;

    @BindView(R.id.tvHint)
    TextView tvHint;

    @BindView(R.id.llBottom)
    LinearLayout llBottom;

    @BindView(R.id.map_container)
    View mapContainer;
    @BindView(R.id.footer)
    View footer;
    //  PlacesAutocompleteTextView tvFrom;
    //@BindView(R.id.tvTo)
    // PlacesAutocompleteTextView tvTo;

    @BindView(R.id.rvResults)
    RecyclerView rvResults; //rvStops;

    private BottomSheetBehavior mBottomSheetBehavior;
    SupportMapFragment mapFragment;
    GoogleMap map;

    String userId;
    boolean permission;

    public static final String USER = "USER";
    public static final String PERMISSION = "PERMISSION";
    private ItemTouchHelper mItemTouchHelper;

    ArrayList<TripLocation> listFromShared;
    ArrayList<TripLocation> stops;
    TripRecyclerAdapter adapter;

    MenuItem mapMenu;
    TripLocation originFromShared;
    TripLocation destFromShared;
    private MenuItem car, bike, walk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        Log.d("home", "onCreate()");
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            setTitle("Road Trip");

        }

        mGeoDataClient = Places.getGeoDataClient(this, null);
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mContext = getApplicationContext();
        parseIntent();

        stops = new ArrayList<>();

        Util.deleteStops(getApplicationContext(), getStops(getApplicationContext()));
        Util.deleteOrigin(mContext);
        Util.deleteDestination(mContext);
        setupViews();  //disable destination fragment.

        adapter = new TripRecyclerAdapter(HomeActivity.this, stops);

        rvResults.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvResults.setLayoutManager(linearLayoutManager);
        ItemClickSupport.addTo(rvResults).setOnItemClickListener(
                (recyclerView, position, v) -> {
                    //create intent
                    if (position % 2 == 0) {
                        Intent i = new Intent(getApplicationContext(), SearchActivity.class);
                        i.putExtra("position", position / 2);
                        //launch activity
                        startActivity(i);
                    }
                }
        );

        setupOriginListener();
        setupDestListener();
        //setupFindListener();
        setupStartListener();

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                loadMap(googleMap);
            }
        });

    }

    public void setupViews() {
        tvHint.setVisibility(View.INVISIBLE);
        llBottom.setVisibility(View.INVISIBLE);
        btnStart.setEnabled(false);
    }

    public void enableDest() {
        tvHint.setVisibility(View.VISIBLE);
        llBottom.setVisibility(View.VISIBLE);
    }

    private void loadStops(List<TripLocation> list) {
        Log.d("DEBUG", "saved list size" + list.size());
        //   ArrayList<Parcelable> pList= getIntent().getParcelableArrayListExtra("stops");
        for (int i = 0; i < list.size(); i++) {
            TripLocation bt = new TripLocation();
            stops.add(bt);
            stops.add(list.get(i));
            Log.d("DEBUG", "saved stop:" + list.get(i).loc_name);
        }
        TripLocation bt = new TripLocation();
        stops.add(bt);
//        adapter.notifyDataSetChanged();
        Log.d("DEBUG", "after loading from pref:" + stops.size());
    }

    public void parseIntent() {
        Bundle bundle = getIntent().getExtras();
        userId = bundle.getString(USER);
        permission = bundle.getBoolean(PERMISSION);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        mapMenu = menu.findItem(R.id.action_show_map);
        mapMenu.setVisible(false);

        car = menu.findItem(R.id.action_car);
        DrawableCompat.setTint(car.getIcon(), ContextCompat.getColor(mContext, android.R.color.white));
        bike = menu.findItem(R.id.action_bike);
        DrawableCompat.setTint(bike.getIcon(), ContextCompat.getColor(mContext, android.R.color.white));
        walk = menu.findItem(R.id.action_walk);
        DrawableCompat.setTint(walk.getIcon(), ContextCompat.getColor(mContext, android.R.color.white));
        Util.saveTravelMode(mContext, "driving");
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String travelMode = Util.getTravelMode(mContext);
        Log.d("home", "travelMode=" + travelMode);
        TravelModeFragment travelModeFragment;
        FragmentManager fm = getSupportFragmentManager();
        int i = item.getItemId();
        if (travelMode.equals("driving")) {
            i = R.id.action_car;
        } else if (travelMode.equals("bicycling")) {
            i = R.id.action_bike;
        } else if (travelMode.equals("walking")) {
            i = R.id.action_walk;
        }

        switch (item.getItemId()) {
            case R.id.action_car:
                Log.d("home", "action_car=");
                travelModeFragment = TravelModeFragment.newInstance();
                travelModeFragment.show(fm, "fragment_travelmode");
                return true;
            case R.id.action_bike:
                Log.d("home", "action_bike=");
                travelModeFragment = TravelModeFragment.newInstance();
                travelModeFragment.show(fm, "fragment_travelmode");
                return true;
            case R.id.action_walk:
                Log.d("home", "action_walk=");
                travelModeFragment = TravelModeFragment.newInstance();
                travelModeFragment.show(fm, "fragment_travelmode");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setIconForTravelMode(boolean carv, boolean bikev, boolean walkv) {
        car.setVisible(carv);
        bike.setVisible(bikev);
        walk.setVisible(walkv);

    }

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
                origin = TripLocation.fromPlace(place);
                Util.saveOrigin(mContext, origin);
                //enable destination input
                if (destination == null) {
                    enableDest();
                }
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
                        //disable map
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
                                            origin = TripLocation.fromPlace(currentPlace.freeze());
                                            originFragment.setText(address);
                                            Util.saveOrigin(mContext, origin);
                                            //enable destination input
                                            if (destination == null) {
                                                enableDest();
                                            }
                                            Log.i("currentplace", "Place found: " + currentPlace.getName());
                                            retrievedPlaces.release();
                                            //enable map if start/end are ready.

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
                destination = TripLocation.fromPlace(place);
                Log.i("place", "Place: " + place.getName());
                Util.saveDestination(mContext, destination);

                tvHint.setVisibility(View.GONE);
                btnStart.setEnabled(true);
                mapMenu.setVisible(true);
                //add empty stop into list to indicate stops are enabled
                if (stops.size() == 0) {
                    TripLocation stop = new TripLocation();
                    stops.add(stop);
                    adapter.notifyDataSetChanged();
                }

                //enable map if start/end are ready.
                updateMap(map);

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
                        // disable map
                    }
                });
    }

    private void setupStartListener() {
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (origin == null || destination == null) return;
                StringBuilder sb = new StringBuilder();
                sb.append("https://www.google.com/maps/dir");
                sb.append("/" + origin.getAddress());
                ArrayList<TripLocation> trips = getStops(mContext);
                for (int i = 0; i < trips.size(); i++) {
                    sb.append("/" + trips.get(i).getAddress());
                }
                sb.append("/" + destination.getAddress());
                String travelMode = Util.getTravelMode(mContext);
                sb.append(Util.getEncodedTravelMode(travelMode));
                Intent i = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(sb.toString()));
                i.setPackage("com.google.android.apps.maps");
                startActivity(i);
            }
        });
    }


    private void loadMap(GoogleMap googleMap) {
        map = googleMap;
        if (map != null) {
            // Map is ready
            //   Toast.makeText(this, "Map Fragment was loaded properly!", Toast.LENGTH_SHORT).show();
            //      ResultsActivityPermissionsDispatcher.getMyLocationWithCheck(this);
            //     ResultsActivityPermissionsDispatcher.startLocationUpdatesWithCheck(this);
            map.getUiSettings().setZoomControlsEnabled(true);
            ArrayList<TripLocation> list = Util.getStops(getApplicationContext());
            if (list.isEmpty() && origin != null && destination != null) {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                //   builder.include(marker.getPosition());
                builder.include(new LatLng(origin.lat, origin.lng));
                builder.include(new LatLng(destination.lat, destination.lng));
                LatLngBounds bounds = builder.build();
                int width = getResources().getDisplayMetrics().widthPixels;
                int height = getResources().getDisplayMetrics().heightPixels;
                int padding = (int) (width * 0.20); // offset from edges of the map 10% of screen
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
                // Zoom in the Google Map
                map.moveCamera(cu);
                Util.addRoute(origin, destination, mContext, map);
                //Util.addLocationMarkers(origin, destination, mContext, map);
                BitmapDescriptor originIcon = Util.createBubble(this, IconGenerator.STYLE_WHITE, "origin");
                Marker marker_origin = Util.addMarker(map, new LatLng(origin.lat, origin.lng), origin.loc_name, origin.address, originIcon);
                BitmapDescriptor icon_dest = Util.createBubble(this, IconGenerator.STYLE_WHITE, "destination");
                Marker marker_dest = Util.addMarker(map, new LatLng(destination.lat, destination.lng), destination.loc_name, destination.address, icon_dest);
            }


            if (!list.isEmpty() && origin != null && destination != null) {
                Log.d("List", "list size=" + list.size());
                map.clear();
                putMarkers(map, list);
                drawRoute(map, list);
            }

        } else {
            Toast.makeText(this, "Error - Map was null!!", Toast.LENGTH_SHORT).show();
        }
    }

    public void putMarkers(GoogleMap map, List<TripLocation> list) {
        //IconGenerator icnGenerator = new IconGenerator(this);
//        BitmapDescriptor originIcon = Util.createNewBubble(this, 0, R.style.iconGenText, getResources().getDrawable(R.drawable.ic_home), "");
//        Marker marker_origin = Util.addMarker(map, new LatLng(origin.lat, origin.lng), origin.loc_name, origin.address, originIcon);
//        BitmapDescriptor destIcon = Util.createNewBubble(this, 0, R.style.iconGenText, getResources().getDrawable(R.drawable.ic_flag), "");
//        Marker marker_dest = Util.addMarker(map, new LatLng(destination.lat,destination.lng), destination.loc_name, destination.address, destIcon);
        BitmapDescriptor icon_origin = Util.createBubble(this, IconGenerator.STYLE_WHITE, "origin");
        Marker marker_origin = Util.addMarker(map, new LatLng(origin.lat, origin.lng), origin.loc_name, origin.address, icon_origin);
        BitmapDescriptor icon_dest = Util.createBubble(this, IconGenerator.STYLE_WHITE, "destination");
        Marker marker_dest = Util.addMarker(map, new LatLng(destination.lat, destination.lng), destination.loc_name, destination.address, icon_dest);
        for (int i = 0; i <= list.size() - 1; i++) {
            Log.d("List", "list=" + list.get(i).getLoc_name());
            //BitmapDescriptor defaultMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
            BitmapDescriptor stopMarker = Util.createNewBubble(this, IconGenerator.STYLE_GREEN, R.style.iconGenText,
                    getResources().getDrawable(R.drawable.ic_pin_map), " " + String.valueOf(i + 1) + ' ');
            Marker marker = map.addMarker(new MarkerOptions()
                    .position(new LatLng(list.get(i).lat, list.get(i).lng))
                    .title(list.get(i).loc_name)
                    .snippet(list.get(i).address)
                    .icon(stopMarker));
        }

    }

    public void drawRoute(GoogleMap map, List<TripLocation> list) {
        ArrayList<LatLng> directionPoint = new ArrayList<>();
        directionPoint.add(new LatLng(origin.lat, origin.lng));
        for (int i = 0; i <= list.size() - 1; i++) {
            directionPoint.add(new LatLng(list.get(i).lat, list.get(i).lng));
        }
        directionPoint.add(new LatLng(destination.lat, destination.lng));
        LatLngBounds.Builder latLngBuilder = new LatLngBounds.Builder();

        for (int j = 0; j <= directionPoint.size() - 2; j++) {
            latLngBuilder.include(directionPoint.get(j));
            addMultipleRoute(new LatLng(directionPoint.get(j).latitude, directionPoint.get(j).longitude),
                    new LatLng(directionPoint.get(j + 1).latitude, directionPoint.get(j + 1).longitude));

        }

        int size = getResources().getDisplayMetrics().widthPixels;
        LatLngBounds latLngBounds = latLngBuilder.build();
        CameraUpdate track = CameraUpdateFactory.newLatLngBounds(latLngBounds, size, size, 25);
        map.moveCamera(track);
    }

    // call back from recyclerView adapter for deleting stop.
    public void onAdapterCallback(int position) {
        TripLocation loc = stops.get(position);
        stops.remove(position);
        stops.remove(position);  //delete empty row which marked as "add a stop"
        adapter.notifyDataSetChanged();
        Util.deleteStop(getApplicationContext(), loc);
        //update map
        map.clear();
        updateMap(map);

    }

    private void addMultipleRoute(LatLng point1, LatLng point2) {
        final GMapV2Direction md = new GMapV2Direction();
        String mode = getTravelMode(mContext);
        Log.d("DEBUG", "in mode: " + mode);
        md.getDocument(point1, point2, mode,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        try {
                            Log.d("DEBUG:", "draw route for origin/dest");
                            Document doc = Util.byteToDocument(responseBody);
                            ArrayList<LatLng> directionPoint = md.getDirection(doc);
                            drawPolyline(directionPoint);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                    }
                });
    }

    private void drawPolyline(ArrayList<LatLng> directionPoint) {
        PolylineOptions rectLine = new PolylineOptions().width(7).color(
                ContextCompat.getColor(this, R.color.colorPrimary));

        for (int i = 0; i < directionPoint.size(); i++) {
            rectLine.add(directionPoint.get(i));
        }
        map.addPolyline(rectLine);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void onShowMap(MenuItem item) {
        //final View mapContainer = findViewById(R.id.map_container);
        Display mdisp = getWindowManager().getDefaultDisplay();
        int maxX = mdisp.getWidth();
        float radius = Math.max(mapContainer.getWidth(), mapContainer.getHeight()) * 2.0f;

        if (mapContainer.getVisibility() == View.INVISIBLE || mapContainer.getVisibility() == View.GONE) {
            mapContainer.setVisibility(View.VISIBLE);
            ViewAnimationUtils.createCircularReveal(mapContainer, maxX, 0, 0, radius).setDuration(1000).start();
            mapMenu.setIcon(getResources().getDrawable(R.drawable.ic_list));

        } else {
            Animator reveal = ViewAnimationUtils.createCircularReveal(
                    mapContainer, maxX, 0, radius, 0).setDuration(1000);
            reveal.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mapContainer.setVisibility(View.INVISIBLE);
                }
            });
            reveal.start();
            mapMenu.setIcon(getResources().getDrawable(R.drawable.ic_map));
        }
        footer.bringToFront();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        //will get called from detail page if we set this activity as singleTask mode
        boolean isAddStop = intent.getBooleanExtra("addstop", false);
        if (isAddStop) {
            listFromShared = getStops(getApplicationContext());
            stops.clear();
            loadStops(listFromShared);
            adapter.notifyDataSetChanged();

            Log.d("DEBUG", "stop added.");
            //update map
            map.clear();
            updateMap(map);

        } else {
            Log.d("DEBUG", "no stop.");
        }
        super.onNewIntent(intent);
    }

    public void updateMap(GoogleMap map) {
        // Util.addRoute(origin, destination, mContext, map);
        map.clear();
        //  Util.addLocationMarkers(origin, destination, mContext, map);

        ArrayList<TripLocation> list = Util.getStops(getApplicationContext());
        if (list == null) {
            list = new ArrayList<>();
        }
        if (origin != null && destination != null) {
            putMarkers(map, list);
            list.add(0, origin);
            list.add(destination);
            Log.d("List", "list size=" + list.size());
            //map.clear();
            drawRoute(map, list);
        }

    }
}
