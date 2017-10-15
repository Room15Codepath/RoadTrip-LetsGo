package com.codepath.roadtrip_letsgo.activities;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
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

import com.codepath.roadtrip_letsgo.R;
import com.codepath.roadtrip_letsgo.adapters.SearchPagerAdapter;
import com.codepath.roadtrip_letsgo.adapters.SmartFragmentStatePagerAdapter;
import com.codepath.roadtrip_letsgo.fragments.ListViewFragment;
import com.codepath.roadtrip_letsgo.models.TripLocation;
import com.codepath.roadtrip_letsgo.models.TripStop;
import com.codepath.roadtrip_letsgo.utils.StopType;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.yelp.fusion.client.connection.YelpFusionApi;
import com.yelp.fusion.client.connection.YelpFusionApiFactory;
import com.yelp.fusion.client.models.Business;
import com.yelp.fusion.client.models.SearchResponse;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SearchActivity extends AppCompatActivity implements ListViewFragment.OnCompleteListener{
    private SupportMapFragment mapFragment;
    private ListViewFragment lvFragment;
    private GoogleMap map;
    private LocationRequest mLocationRequest;
    Location mCurrentLocation;
    YelpFusionApiFactory yelpFusionApiFactory;
    YelpFusionApi yelpFusionApi;
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

    //private SearchView searchView;
    private SmartFragmentStatePagerAdapter adapterViewPager;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.sliding_tabs) TabLayout tabLayout;
    @BindView(R.id.viewpager) ViewPager viewPager;

    Thread thread = new Thread(new Runnable(){
        @Override
        public void run() {
            try {
                yelpFusionApiFactory = new YelpFusionApiFactory();
                yelpFusionApi = yelpFusionApiFactory.createAPI(
                        getResources().getString(R.string.yelp_client_id),
                        getResources().getString(R.string.yelp_secret_key));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ButterKnife.bind(this);

       // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
 //       ViewPager viewPager = (ViewPager)findViewById(R.id.viewpager);
        adapterViewPager =new SearchPagerAdapter(getSupportFragmentManager(),this);
        viewPager.setAdapter(adapterViewPager);
//        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
        stops = new ArrayList<>();
        origin = Parcels.unwrap(getIntent().getParcelableExtra("origin"));
        dest = Parcels.unwrap(getIntent().getParcelableExtra("destination"));

        thread.start();
        while (yelpFusionApi ==null) {
            try {
                Thread.sleep(100);
            } catch (Exception e) {
                // skip;
            }
        }
        getBusinesses();


    }

    public void getBusinesses() {
        try {
            Map<String, String> params = new HashMap<>();

            params.put("term", "cafe");
            params.put("latitude", String.valueOf(origin.point.latitude));
            params.put("longitude", String.valueOf(origin.point.longitude));
            params.put("radius","2000");
            Call<SearchResponse> call = yelpFusionApi.getBusinessSearch(params);
            call.enqueue(callback);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void onComplete() {
        mapFragment = (SupportMapFragment) adapterViewPager.getRegisteredFragment(0);
        lvFragment = (ListViewFragment) adapterViewPager.getRegisteredFragment(1);
        if(lvFragment !=null)
        {
            lvFragment.addItems(stops);
        }

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
                //query
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

        if (id == R.id.action_add) {
            Intent i = new Intent(this, AddStopActivity.class);
            i.putExtra("start", Parcels.wrap(origin));
            i.putExtra("end", Parcels.wrap(dest));
            startActivityForResult(i, REQUEST_CODE_ADD);
        }

        if (id == R.id.action_settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            i.putExtra("home", "MY HOME");
            startActivityForResult(i, REQUEST_CODE_SET);
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

    Callback<SearchResponse> callback = new Callback<SearchResponse>() {
        @Override
        public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
            SearchResponse searchResponse = response.body();
            Log.d("response", searchResponse.getBusinesses().toString());
            ArrayList<Business> businesses= searchResponse.getBusinesses();
            for (Business b: businesses) {
                stops.add(TripStop.fromBusiness(b, StopType.CAFE));
            }
           if(lvFragment!=null) lvFragment.addItems(stops);
        }

        @Override
        public void onFailure(Call<SearchResponse> call, Throwable t) {
            // HTTP error happened, do something to handle it.
        }
    };

}
