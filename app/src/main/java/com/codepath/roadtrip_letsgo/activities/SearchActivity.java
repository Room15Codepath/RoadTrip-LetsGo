package com.codepath.roadtrip_letsgo.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;

import com.codepath.roadtrip_letsgo.R;
import com.codepath.roadtrip_letsgo.adapters.MapInfoAdapter;
import com.codepath.roadtrip_letsgo.adapters.SearchPagerAdapter;
import com.codepath.roadtrip_letsgo.adapters.SmartFragmentStatePagerAdapter;
import com.codepath.roadtrip_letsgo.fragments.FilterFragment;
import com.codepath.roadtrip_letsgo.fragments.ListViewFragment;
import com.codepath.roadtrip_letsgo.models.InfoWindow;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.maps.android.ui.IconGenerator;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;
import org.parceler.Parcels;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static com.codepath.roadtrip_letsgo.RoadTripApplication.getYelpClient;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

@RuntimePermissions
public class SearchActivity extends AppCompatActivity {
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

    Context mContext;
    public static String POSITION = "POSITION";
    private final int REQUEST_CODE_ADD = 10;  //for add stop
    private final int REQUEST_CODE_SET = 20;  //for settings
    TripLocation origin;
    TripLocation dest;
    String stopType=null;
    int pos =-1;

    private SmartFragmentStatePagerAdapter adapterViewPager;
//    @BindView(R.id.toolbar)
//    Toolbar toolbar;

    @BindView(R.id.llRoot)
    LinearLayout llRoot;

    @BindView(R.id.btn_filter)
    ImageView btnFilter;

    @BindView(R.id.sliding_tabs)
    TabLayout tabLayout;

    @BindView(R.id.viewpager)
    ViewPager viewPager;

    @BindView(R.id.btn_coffee_cup)
    ImageView btnCoffeCup;

    @BindView(R.id.btn_gas_station)
    ImageView btnGasStation;

    @BindView(R.id.btn_shopping_cart)
    ImageView btnShoppingCart;

    @BindView(R.id.btn_restaurant)
    ImageView btnRastaurant;

    @BindView(R.id.btn_atm)
    ImageView btnAtm;

    @BindView(R.id.search_stops)
    SearchView searchStops;

    @BindView(R.id.fabSort)
    com.getbase.floatingactionbutton.FloatingActionButton fabSort;

    @BindView(R.id.fabDistSort)
    com.getbase.floatingactionbutton.FloatingActionButton fabDistSort;

    @BindView(R.id.multiple_sort)
    com.getbase.floatingactionbutton.FloatingActionsMenu fabMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        mContext = getApplicationContext();
        yelpClient = getYelpClient();
        //setSupportActionBar(toolbar);
        adapterViewPager = new SearchPagerAdapter(getSupportFragmentManager(), this);
        fabMenu.setVisibility(View.GONE);
        viewPager.setAdapter(adapterViewPager);
        tabLayout.setupWithViewPager(viewPager);
        pos = getIntent().getIntExtra("position",-1);  //get stop position in list
        stops = new ArrayList<>();
        mapFragment = (SupportMapFragment) getCurrentPagerFragment(0);//adapterViewPager.getRegisteredFragment(0);
        lvFragment = (ListViewFragment) getCurrentPagerFragment(1);//adapterViewPager.getRegisteredFragment(1);
        lvFragment.setPosition(pos);    //pass stop position to listview
        // parseIntent();
        getOriginAndDestination();
        setupTabs();
        setFilterListener();
        searchStops.setQueryHint("search stop");
        searchStops.setFocusable(false);
        searchStops.setIconifiedByDefault(false);
        searchStops.clearFocus();
        searchStops.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                stopType = query;
                searchStops.setQuery(query, false);
                Log.d("STRING", stopType);
                cleanTabs();
                searchStops.clearFocus();
                onComplete();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                fabMenu.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        btnCoffeCup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("search activity", "cofee cup");
                searchStops.setQuery("cafe", false);
                stopType = "" + searchStops.getQuery();
                Log.d("STRING", stopType);
                cleanTabs();
                onComplete();

            }
        });

        btnGasStation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("search activity", "cofee cup");
                searchStops.setQuery("gas station", true);
                stopType = "" + searchStops.getQuery();
                Log.d("STRING", stopType);
                cleanTabs();
                onComplete();


            }
        });
        btnShoppingCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchStops.setQuery("mall", true);
                stopType = "" + searchStops.getQuery();
                Log.d("STRING", stopType);
                cleanTabs();
                onComplete();

            }
        });

        btnRastaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchStops.setQuery("restaurant", true);
                stopType = "" + searchStops.getQuery();
                Log.d("STRING", stopType);
                cleanTabs();
                onComplete();

            }
        });

        btnAtm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchStops.setQuery("atm", true);
                stopType = "" + searchStops.getQuery();
                Log.d("STRING", stopType);
                cleanTabs();
                onComplete();

            }
        });

        fabSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Collections.sort(stops, new Comparator<TripStop>() {
                    public int compare(TripStop o1, TripStop o2) {
                        return o1.getTripLocation_Name().compareTo(o2.getTripLocation_Name());
                    }
                });
                lvFragment.cleanList();
                lvFragment.addItems(stops);

            }
        });

        fabDistSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Collections.sort(stops, new Comparator<TripStop>() {
                    public int compare(TripStop o1, TripStop o2) {
                        float o1Distance = getDistanceFromOrigin(o1.getTrip_location());
                        float o2Distance = getDistanceFromOrigin(o2.getTrip_location());
                        return Float.compare(o1Distance, o2Distance);
                    }
                });
                lvFragment.cleanList();
                lvFragment.addItems(stops);

            }
        });


    }

    public void setupTabs() {
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap map) {
                    loadMap(map);
                }
            });
        } else {
            Log.d("DEBUG", "map fragment is null");
        }

    }


    public void cleanTabs(){
        if (lvFragment!=null && map != null) {
            lvFragment.cleanList();
            map.clear();
        }

    }

    private void setFilterListener() {
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FilterFragment filterFragment;
                FragmentManager fm = getSupportFragmentManager();
                filterFragment = FilterFragment.newInstance();
                filterFragment.show(fm, "fragment_travelmode");
            }
        });
    }

    public void getOriginAndDestination() {
        origin = Util.getOrigin(mContext);
        dest = Util.getDestination(mContext);
    }

    public float getDistanceFromOrigin(TripLocation loc) {
        float km = Util.getDistance(origin.lat, origin.lng, loc.lat, loc.lng);
        return Util.convertKmToMiles(km);
    }


//    public void parseIntent() {
//        origin = Parcels.unwrap(getIntent().getParcelableExtra("origin"));
//        dest = Parcels.unwrap(getIntent().getParcelableExtra("destination"));
//        pos = getIntent().getIntExtra("position",-1);
//    }

    public Fragment getCurrentPagerFragment(int position) {

        FragmentStatePagerAdapter a = (FragmentStatePagerAdapter) viewPager.getAdapter();

        return (Fragment) a.instantiateItem(viewPager, position);

    }
    public void onComplete() {

        //mapFragment = (SupportMapFragment) getCurrentPagerFragment(0);//adapterViewPager.getRegisteredFragment(0);
        //lvFragment = (ListViewFragment) getCurrentPagerFragment(1);//adapterViewPager.getRegisteredFragment(1);
    //    if(lvFragment !=null) {
            lvFragment.addTrip(origin, dest);

            /*if (stops.size() > 0) {
                lvFragment.addItems(stops);
            }*/
      //  }
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap map) {
                    loadMap(map);
                    // map.setInfoWindowAdapter(new CustomWindowAdapter(getLayoutInflater()));
                }
            });
        } else {
            Log.d("DEBUG", "map fragment is null");
            //Toast.makeText(this, "Error - Map Fragment was null!!", Toast.LENGTH_SHORT).show();
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
            //getBusinesses();
            // Zoom in the Google Map
           // LatLng definedLoc = new LatLng(origin.lat,origin.lng);

        //    CameraPosition cameraPosition = new CameraPosition.Builder().target(definedLoc).zoom(13.0F).build();
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(new LatLng(origin.lat, origin.lng));
            builder.include(new LatLng(dest.lat, dest.lng));
            LatLngBounds bounds = builder.build();

            int width = getResources().getDisplayMetrics().widthPixels;
            int height = getResources().getDisplayMetrics().heightPixels;
            int padding = (int) (width * 0.20); // offset from edges of the map 10% of screen
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

            map.animateCamera(cu);

            map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    Log.d("DEBUG:", "info window clicked");
                    Intent intent = new Intent(SearchActivity.this, PlaceDetailActivity.class);
                    if(marker.getTitle().equals("origin") || marker.getTitle().equals("destination")) return;
                    TripStop loc = (TripStop) marker.getTag();
                    intent.putExtra("location", Parcels.wrap(loc));
                    intent.putExtra("position", pos);
                    startActivity(intent);

                }
            });
        } else {
           Log.d("DEBUG", "map is null");// Toast.makeText(this, "Error - Map was null!!", Toast.LENGTH_SHORT).show();
        }
    }

    private void addRoute(TripLocation origin, TripLocation dest) {
        final GMapV2Direction md = new GMapV2Direction();
        LatLng pnt1 = new LatLng(origin.lat, origin.lng);
        LatLng pnt2 = new LatLng(dest.lat, dest.lng);
        md.getDocument( pnt1, pnt2, GMapV2Direction.MODE_DRIVING,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        try {
                            Log.d("DEBUG:", "draw route for origin/dest");
                            Document doc = Util.byteToDocument(responseBody);
                            ArrayList<LatLng> directionPoint = md.getDirection(doc);
                            drawPolyline(directionPoint);
                            if (stopType != null) {
                                 getBusinesses(directionPoint);
                            }
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
        Polyline polyline = map.addPolyline(rectLine);
    }

    private void getBusinesses(ArrayList<LatLng> directionPoint) {
        SharedPreferences settings = getSharedPreferences("settings", 0);
        Log.d("radius fl", String.valueOf(settings.getFloat("range", 1.0f)));
        float radius = settings.getFloat("range",1.0f) * 1609;
        Log.d("DEBUG:", "Radius:" +radius);
        if (directionPoint.size() <= 20) {
            getYelpBusinessesFromPoint(new LatLng(origin.lat, origin.lng), radius);
        }
        for (int i=0; i<directionPoint.size(); i+=20) {
            getYelpBusinessesFromPoint(directionPoint.get(i), radius);
        }
    }

    private void getYelpBusinessesFromPoint(LatLng point, float radius) {
        Log.d("search activity", "stopType"+stopType);
        RequestParams params = new RequestParams();
        params.put("term", stopType);
        params.put("latitude", String.valueOf(point.latitude));
        params.put("longitude", String.valueOf(point.longitude));
        params.put("radius",String.valueOf(Math.round(radius)));
        stops = new ArrayList<>();
        yelpClient.getBusinesses(params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Log.d("yelpbusinesses", response.toString());
                    for (int i=0; i<response.getJSONArray("businesses").length(); i++) {
                        TripStop tripStop = TripStop.fromJSON(
                                response.getJSONArray("businesses").getJSONObject(i), StopType.CAFE);
                        InfoWindow iw = InfoWindow.fromTripStop(tripStop);
                        String iwJSON = new Gson().toJson(iw);
                        BitmapDescriptor defaultMarker =
                                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
                        Marker marker = map.addMarker(new MarkerOptions()
                                .position(new LatLng(tripStop.trip_location.lat, tripStop.trip_location.lng))
                                .title(iwJSON)
                                .icon(defaultMarker));
                        map.setInfoWindowAdapter(new MapInfoAdapter(getLayoutInflater()));
                        marker.setTag(tripStop);

                        if (stops == null) {
                            stops = new ArrayList<>();
                        }
                        if (stops.contains(tripStop)) {
                            stops.set(stops.indexOf(tripStop), tripStop);
                        } else {
                            stops.add(tripStop);
                        }
                    }
                    Log.d("stop", stops.toString());
                    Log.d("response length", ""+response.getJSONArray("businesses").length());

                    Collections.sort(stops, TripStop.COMPARE_BY_DISTANCE);

                    lvFragment.cleanList();
                    lvFragment.addItems(stops);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

            }
        });
    }

    private void addLocationMarkers(TripLocation origin, TripLocation dest) {
        BitmapDescriptor icon_origin = Util.createBubble(this, IconGenerator.STYLE_WHITE, "origin");
        Marker marker_origin = Util.addMarker(map, new LatLng(origin.lat, origin.lng), origin.loc_name, origin.address, icon_origin);
        BitmapDescriptor icon_dest = Util.createBubble(this, IconGenerator.STYLE_WHITE, "destination");
        Marker marker_dest = Util.addMarker(map, new LatLng(dest.lat,dest.lng), dest.loc_name, dest.address, icon_dest);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        //MenuItem searchItem = menu.findItem(R.id.action_search);
        /*final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
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
        });*/

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

            FilterFragment filterFragment;
            FragmentManager fm = getSupportFragmentManager();
            filterFragment = FilterFragment.newInstance();
            filterFragment.show(fm, "fragment_travelmode");
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
    //    String msg = "Updated Location: " +
     //           Double.toString(location.getLatitude()) + "," +
     //           Double.toString(location.getLongitude());
      //  Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

/*    @Override
    protected void onResume() {
        super.onResume();

        // Display the connection status

        if (mCurrentLocation != null) {
            Toast.makeText(this, "GPS location was found!", Toast.LENGTH_SHORT).show();
            LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
 //           CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
//            map.animateCamera(cameraUpdate);
        } else {
            Log.d("DEBUG", " current location is null");
            //Toast.makeText(this, "Current location was null, enable GPS on emulator!", Toast.LENGTH_SHORT).show();
        }
        SearchActivityPermissionsDispatcher.startLocationUpdatesWithCheck(this);
    }
*/
}
