package com.codepath.roadtrip_letsgo.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.codepath.roadtrip_letsgo.R;
import com.codepath.roadtrip_letsgo.adapters.SearchPagerAdapter;
import com.codepath.roadtrip_letsgo.adapters.SmartFragmentStatePagerAdapter;
import com.codepath.roadtrip_letsgo.fragments.ListViewFragment;
import com.codepath.roadtrip_letsgo.fragments.TravelModeFragment;
import com.codepath.roadtrip_letsgo.models.TripLocation;
import com.codepath.roadtrip_letsgo.models.TripStop;
import com.codepath.roadtrip_letsgo.network.GMapV2Direction;
import com.codepath.roadtrip_letsgo.network.YelpClient;
import com.codepath.roadtrip_letsgo.utils.StopType;
import com.codepath.roadtrip_letsgo.utils.Util;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.android.ui.IconGenerator;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;
import org.parceler.Parcels;
import org.w3c.dom.Document;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static com.codepath.roadtrip_letsgo.RoadTripApplication.getYelpClient;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

@RuntimePermissions
public class SearchActivity extends AppCompatActivity implements ListViewFragment.OnCompleteListener {
    private SupportMapFragment mapFragment;
    private ListViewFragment lvFragment;
    private GoogleMap map;
    private LocationRequest mLocationRequest;
    Location mCurrentLocation;
    private YelpClient yelpClient;
    private final static String KEY_LOCATION = "location";
    private long UPDATE_INTERVAL = 60000;  /* 60 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 secs */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    ArrayList<TripStop> stops;


    public static String POSITION = "POSITION";
    private final int REQUEST_CODE_ADD = 10;  //for add stop
    private final int REQUEST_CODE_SET = 20;  //for settings
    TripLocation origin;
    TripLocation dest;
    String stopType;

    private SmartFragmentStatePagerAdapter adapterViewPager;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.sliding_tabs)
    TabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        yelpClient = getYelpClient();
        setSupportActionBar(toolbar);
        adapterViewPager = new SearchPagerAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(adapterViewPager);
        tabLayout.setupWithViewPager(viewPager);
        stops = new ArrayList<>();
        parseIntent();
    }

    public void parseIntent() {
        origin = Parcels.unwrap(getIntent().getParcelableExtra("origin"));
        dest = Parcels.unwrap(getIntent().getParcelableExtra("destination"));
        stopType = getIntent().getStringExtra("stopType");
    }

    public void onComplete() {
        mapFragment = (SupportMapFragment) adapterViewPager.getRegisteredFragment(0);
        lvFragment = (ListViewFragment) adapterViewPager.getRegisteredFragment(1);
        lvFragment.addTrip(origin, dest);
        if(stops.size()>0)
        {
            lvFragment.addItems(stops);
        }
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap map) {
                    loadMap(map);
                    // map.setInfoWindowAdapter(new CustomWindowAdapter(getLayoutInflater()));
                }
            });
        } else {
            Toast.makeText(this, "Error - Map Fragment was null!!", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadMap(GoogleMap googleMap) {
        map = googleMap;
        if (map != null) {
            SearchActivityPermissionsDispatcher.getMyLocationWithCheck(this);
            SearchActivityPermissionsDispatcher.startLocationUpdatesWithCheck(this);
            map.getUiSettings().setZoomControlsEnabled(true);
            addLocationMarkers(origin, dest);
            addRoute(origin, dest);
            getBusinesses();
            // Zoom in the Google Map
            LatLng definedLoc = new LatLng(origin.point.latitude,origin.point.longitude);
            CameraPosition cameraPosition = new CameraPosition.Builder().target(definedLoc).zoom(13.0F).build();
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    Log.d("DEBUG:", "info window clicked");
                    Intent intent = new Intent(SearchActivity.this, LocationDetailActivity.class);
                    if(marker.getTitle().equals("origin") || marker.getTitle().equals("destination")) return;
                    TripStop loc = (TripStop) marker.getTag();
                    intent.putExtra("start", Parcels.wrap(origin));
                    intent.putExtra("end", Parcels.wrap(dest));
                    intent.putExtra("location", Parcels.wrap(loc));
                    startActivity(intent);

                }
            });
        } else {
            Toast.makeText(this, "Error - Map was null!!", Toast.LENGTH_SHORT).show();
        }
    }

    private void addRoute(TripLocation origin, TripLocation dest) {
        final GMapV2Direction md = new GMapV2Direction();

        md.getDocument(origin.point, dest.point, GMapV2Direction.MODE_DRIVING,
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

    private void getBusinesses() {
        SharedPreferences settings = getSharedPreferences("settings", 0);
        int radius = (int) settings.getFloat("range",1.0f) *1600;
        Log.d("DEBUG:", "Radius:" +radius);
        RequestParams params = new RequestParams();
        params.put("term", stopType);
        params.put("latitude", String.valueOf(origin.point.latitude));
        params.put("longitude", String.valueOf(origin.point.longitude));
        params.put("radius",String.valueOf(radius));
        yelpClient.getBusinesses(params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Log.d("yelpbusinesses", response.toString());
                    for (int i=0; i<response.getJSONArray("businesses").length(); i++) {
                        TripStop tripStop = TripStop.fromJSON(
                                response.getJSONArray("businesses").getJSONObject(i), StopType.CAFE);
                        BitmapDescriptor defaultMarker =
                                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE);
                        Marker marker = map.addMarker(new MarkerOptions()
                                .position(tripStop.trip_location.point)
                                .title(tripStop.trip_location.loc_name)
                                .snippet(tripStop.trip_location.address)
                                .icon(defaultMarker));
                        marker.setTag(tripStop);
                        stops.add(tripStop);
                    }
                    lvFragment.addItems(stops);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("yelpbusinesses", errorResponse.toString());
            }
        });
    }

    private void addLocationMarkers(TripLocation origin, TripLocation dest) {
        BitmapDescriptor icon_origin = Util.createBubble(this, IconGenerator.STYLE_WHITE, "origin");
        Marker marker_origin = Util.addMarker(map, origin.point, origin.loc_name, origin.address, icon_origin);
        BitmapDescriptor icon_dest = Util.createBubble(this, IconGenerator.STYLE_WHITE, "destination");
        Marker marker_dest = Util.addMarker(map, dest.point, dest.loc_name, dest.address, icon_dest);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

/*        if (id == R.id.action_add) {
            Intent i = new Intent(this, AddStopActivity.class);
            i.putExtra("start", Parcels.wrap(origin));
            i.putExtra("end", Parcels.wrap(dest));
            startActivityForResult(i, REQUEST_CODE_ADD);
        }
*/
        if (id == R.id.action_settings) {

            TravelModeFragment travelModeFragment;
            FragmentManager fm = getSupportFragmentManager();
            travelModeFragment = TravelModeFragment.newInstance();
            travelModeFragment.show(fm, "fragment_travelmode");
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(POSITION, tabLayout.getSelectedTabPosition());
        outState.putParcelable(KEY_LOCATION,mCurrentLocation);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        viewPager.setCurrentItem(savedInstanceState.getInt(POSITION));
        mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
    }

    private void drawPolyline(ArrayList<LatLng> directionPoint) {
        PolylineOptions rectLine = new PolylineOptions().width(7).color(
                ContextCompat.getColor(this, R.color.colorPrimary));

        for (int i = 0; i < directionPoint.size(); i++) {
            rectLine.add(directionPoint.get(i));
        }
        Polyline polyline = map.addPolyline(rectLine);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("DEBUG:", "permission granted.");
        SearchActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void getMyLocation() {
        //noinspection MissingPermission
        map.setMyLocationEnabled(true);

        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(this);
        //noinspection MissingPermission
        locationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            onLocationChanged(location);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("MapDemoActivity", "Error trying to get last GPS location");
                        e.printStackTrace();
                    }
                });
    }

    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    protected void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);
        //noinspection MissingPermission
        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
    }

    public void onLocationChanged(Location location) {
        // GPS may be turned off
        if (location == null) {
            return;
        }
        mCurrentLocation = location;
   //     String msg = "Updated Location: " +
     //           Double.toString(location.getLatitude()) + "," +
       //         Double.toString(location.getLongitude());
       // Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Display the connection status

        if (mCurrentLocation != null) {
            Toast.makeText(this, "GPS location was found!", Toast.LENGTH_SHORT).show();
            LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
            map.animateCamera(cameraUpdate);
        } else {
            Toast.makeText(this, "Current location was null, enable GPS on emulator!", Toast.LENGTH_SHORT).show();
        }
        SearchActivityPermissionsDispatcher.startLocationUpdatesWithCheck(this);
    }

}
